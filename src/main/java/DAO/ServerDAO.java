package DAO;

import POJO.Server;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ServerDAO {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Server server) {
        executeInTransaction(em -> em.persist(server));
    }

    public Server findById(int id) {
        return execute(em -> em.find(Server.class, id));
    }

    public List<Server> findAll() {
        return execute(em ->
                em.createQuery("SELECT s FROM Server s", Server.class).getResultList()
        );
    }

    public List<Server> findByAdminId(int adminId) {
        return execute(em ->
                em.createQuery("SELECT s FROM Server s WHERE s.admin.id = :adminId", Server.class)
                        .setParameter("adminId", adminId)
                        .getResultList()
        );
    }

    public void update(Server server) {
        executeInTransaction(em -> em.merge(server));
    }

    public void delete(Server server) {
        executeInTransaction(em -> {
            Server managedServer = em.merge(server);
            em.remove(managedServer);
        });
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