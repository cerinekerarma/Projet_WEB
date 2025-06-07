package DAO;

import POJO.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class UserDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("monPU");

    public void create(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        em.close();
    }

    public User findById(int id) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, id);
        em.close();
        return user;
    }

    public List<User> findAll() {
        EntityManager em = emf.createEntityManager();
        List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
        em.close();
        return users;
    }

    public void update(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        User managedUser = em.merge(user); // Nécessaire s’il est détaché
        em.remove(managedUser);
        em.getTransaction().commit();
        em.close();
    }
}
