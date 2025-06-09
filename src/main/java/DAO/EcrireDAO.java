package DAO;

import POJO.Ecrire;
import POJO.Message;
import POJO.User;
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

    public Ecrire findByMessageId(int messageId) {
        EntityManager em = emf.createEntityManager();
        Ecrire ecrire = em.find(Ecrire.class, messageId);
        em.close();
        return ecrire;
    }

    public Ecrire findByMessage(Message message) {
        EntityManager em = emf.createEntityManager();
        Ecrire ecrire = em.find(Ecrire.class, message.getId());
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

    // Rechercher les messages envoyés par un utilisateur
    public List<Ecrire> findBySender(User sender) {
        EntityManager em = emf.createEntityManager();
        List<Ecrire> list = em.createQuery("SELECT e FROM Ecrire e WHERE e.sender = :sender", Ecrire.class)
                .setParameter("sender", sender)
                .getResultList();
        em.close();
        return list;
    }

    // Rechercher les messages reçus par un utilisateur
    public List<Ecrire> findByReceiver(User receiver) {
        EntityManager em = emf.createEntityManager();
        List<Ecrire> list = em.createQuery("SELECT e FROM Ecrire e WHERE e.receiver = :receiver", Ecrire.class)
                .setParameter("receiver", receiver)
                .getResultList();
        em.close();
        return list;
    }
}
