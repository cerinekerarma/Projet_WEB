package DAO;

import POJO.Integrer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class IntegrerDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Integrer integrer) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(integrer);
        em.getTransaction().commit();
        em.close();
    }

    public Integrer findById(int id) {
        EntityManager em = emf.createEntityManager();
        Integrer integrer = em.find(Integrer.class, id);
        em.close();
        return integrer;
    }

    public List<Integrer> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Integrer> list = em.createQuery("SELECT i FROM Integrer i", Integrer.class).getResultList();
        em.close();
        return list;
    }

    public void update(Integrer integrer) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(integrer);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Integrer integrer) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Integrer managed = em.merge(integrer);
        em.remove(managed);
        em.getTransaction().commit();
        em.close();
    }
}
