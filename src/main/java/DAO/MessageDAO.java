package DAO;

import POJO.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MessageDAO {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Message message) {
        executeInTransaction(em -> em.persist(message));
    }

    public Message findById(int id) {
        return execute(em -> em.find(Message.class, id));
    }

    public List<Message> findAll() {
        return execute(em ->
                em.createQuery("SELECT m FROM Message m", Message.class).getResultList()
        );
    }

    public void update(Message message) {
        executeInTransaction(em -> em.merge(message));
    }

    public void delete(Message message) {
        executeInTransaction(em -> {
            Message managedMessage = em.merge(message); // En cas d'entité détachée
            em.remove(managedMessage);
        });
    }

    public List<Message> findByChannelId(int channelId) {
        return execute(em ->
                em.createQuery("SELECT m FROM Message m WHERE m.channel.id = :channelId", Message.class)
                        .setParameter("channelId", channelId)
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
