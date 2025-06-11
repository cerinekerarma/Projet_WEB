package DAO;

import POJO.Emettre;
import POJO.EmettreId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class EmettreDAO {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Emettre emettre) {
        executeInTransaction(em -> em.persist(emettre));
    }

    public Emettre findById(int id) {
        return execute(em -> em.find(Emettre.class, id));
    }

    public Emettre findById(int messageId, String userId) {
        return execute(em -> {
            EmettreId id = new EmettreId(messageId, userId);
            return em.find(Emettre.class, id);
        });
    }


    public List<Emettre> findAll() {
        return execute(em ->
                em.createQuery("SELECT e FROM Emettre e", Emettre.class).getResultList()
        );
    }

    public List<Emettre> findByUserId(String userId) {
        return execute(em ->
                em.createQuery("SELECT e FROM Emettre e WHERE e.user.id = :userId", Emettre.class)
                        .setParameter("userId", userId)
                        .getResultList()
        );
    }

    public List<Emettre> findByMessageId(int messageId) {
        return execute(em ->
                em.createQuery("SELECT e FROM Emettre e WHERE e.message.id = :messageId", Emettre.class)
                        .setParameter("messageId", messageId)
                        .getResultList()
        );
    }

    public void update(Emettre emettre) {
        executeInTransaction(em -> em.merge(emettre));
    }

    public void delete(Emettre emettre) {
        executeInTransaction(em -> {
            Emettre managed = em.merge(emettre);
            em.remove(managed);
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
