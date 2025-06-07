package DAO;

import POJO.Publier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class PublierDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Publier publier) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(publier);
        em.getTransaction().commit();
        em.close();
    }

    public Publier findById(int id) {
        EntityManager em = emf.createEntityManager();
        Publier publier = em.find(Publier.class, id);
        em.close();
        return publier;
    }

    public List<Publier> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Publier> list = em.createQuery("SELECT p FROM Publier p", Publier.class).getResultList();
        em.close();
        return list;
    }

    public void update(Publier publier) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(publier);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Publier publier) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Publier managedPublier = em.merge(publier);
        em.remove(managedPublier);
        em.getTransaction().commit();
        em.close();
    }
}
