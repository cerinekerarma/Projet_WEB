package DAO;

import POJO.Integrer;
import POJO.IntegrerId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class IntegrerDAO {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Integrer integrer) {
        executeInTransaction(em -> em.persist(integrer));
    }

    public Integrer findById(int id) {
        return execute(em -> em.find(Integrer.class, id));
    }

    public Integrer findById(String userId, int serverId) {
        IntegrerId id = new IntegrerId(userId, serverId);
        return execute(em -> em.find(Integrer.class, id));
    }

    public List<Integrer> findAll() {
        return execute(em ->
                em.createQuery("SELECT i FROM Integrer i", Integrer.class).getResultList()
        );
    }

    public void update(Integrer integrer) {
        executeInTransaction(em -> em.merge(integrer));
    }

    public void delete(Integrer integrer) {
        executeInTransaction(em -> {
            Integrer managed = em.merge(integrer);
            em.remove(managed);
        });
    }

    // Trouver tous les serveurs rejoints par un utilisateur
    public List<Integrer> findByUserId(int userId) {
        return execute(em ->
                em.createQuery("SELECT i FROM Integrer i WHERE i.id_user = :userId", Integrer.class)
                        .setParameter("userId", userId)
                        .getResultList()
        );
    }

    // Trouver tous les utilisateurs ayant rejoint un serveur
    public List<Integrer> findByServerId(int serverId) {
        return execute(em ->
                em.createQuery("SELECT i FROM Integrer i WHERE i.id_server = :serverId", Integrer.class)
                        .setParameter("serverId", serverId)
                        .getResultList()
        );
    }

    // ===== Méthodes utilitaires =====
    private <T> T execute(DAOOperation<T> operation) {
        EntityManager em = emf.createEntityManager();
        try {
            return operation.execute(em);
        } finally {
            em.close();
        }
    }

    private void executeInTransaction(DAOOperationVoid operation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            operation.execute(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("DAO operation failed", e);
        } finally {
            em.close();
        }
    }

    @FunctionalInterface
    private interface DAOOperation<T> {
        T execute(EntityManager em);
    }

    @FunctionalInterface
    private interface DAOOperationVoid {
        void execute(EntityManager em);
    }
}
