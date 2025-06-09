package DAO;

import POJO.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class UserDAO {
    private final EntityManagerFactory emf;

    public UserDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public UserDAO() {
        this(Persistence.createEntityManagerFactory("PU_JPA"));
    }

    public void create(User user) {
        executeInTransaction(em -> em.persist(user));
    }

    public User findById(int id) {
        return execute(em -> em.find(User.class, id));
    }

    public List<User> findAll() {
        return execute(em ->
                em.createQuery("SELECT u FROM User u", User.class).getResultList()
        );
    }

    public void update(User user) {
        executeInTransaction(em -> em.merge(user));
    }

    public void delete(User user) {
        executeInTransaction(em -> {
            User managedUser = em.merge(user);
            em.remove(managedUser);
        });
    }

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