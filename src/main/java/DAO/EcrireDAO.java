package DAO;

import POJO.Ecrire;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class EcrireDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Ecrire ecrire) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(ecrire);
        em.getTransaction().commit();
        em.close();
    }

    public Ecrire findById(int id) {
        EntityManager em = emf.createEntityManager();
        Ecrire ecrire = em.find(Ecrire.class, id);
        em.close();
        return ecrire;
    }

    public List<Ecrire> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Ecrire> list = em.createQuery("SELECT e FROM Ecrire e", Ecrire.class).getResultList();
        em.close();
        return list;
    }

    public void update(Ecrire ecrire) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(ecrire);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Ecrire ecrire) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Ecrire managed = em.merge(ecrire);
        em.remove(managed);
        em.getTransaction().commit();
        em.close();
    }
}
