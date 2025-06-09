package DAO;

import POJO.Publier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class PublierDAO {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Publier publier) {
        executeInTransaction(em -> em.persist(publier));
    }

    public Publier findById(int id) {
        return execute(em -> em.find(Publier.class, id));
    }

    public List<Publier> findAll() {
        return execute(em ->
                em.createQuery("SELECT p FROM Publier p", Publier.class).getResultList()
        );
    }

    public void update(Publier publier) {
        executeInTransaction(em -> em.merge(publier));
    }

    public void delete(Publier publier) {
        executeInTransaction(em -> {
            Publier managedPublier = em.merge(publier);
            em.remove(managedPublier);
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
