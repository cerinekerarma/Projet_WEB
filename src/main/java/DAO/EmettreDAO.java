package DAO;

import POJO.Emettre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class EmettreDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Emettre emettre) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(emettre);
        em.getTransaction().commit();
        em.close();
    }

    public Emettre findById(int id) {
        EntityManager em = emf.createEntityManager();
        Emettre emettre = em.find(Emettre.class, id);
        em.close();
        return emettre;
    }

    public List<Emettre> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Emettre> list = em.createQuery("SELECT e FROM Emettre e", Emettre.class).getResultList();
        em.close();
        return list;
    }

    public void update(Emettre emettre) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(emettre);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Emettre emettre) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Emettre managed = em.merge(emettre);
        em.remove(managed);
        em.getTransaction().commit();
        em.close();
    }
}
