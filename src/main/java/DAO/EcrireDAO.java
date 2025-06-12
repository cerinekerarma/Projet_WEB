package DAO;

import POJO.Ecrire;
import POJO.Message;
import POJO.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public List<User> findDistinctUsersInConversationWith(User user) {
        EntityManager em = emf.createEntityManager();

        List<User> sentTo = em.createQuery("""
        SELECT DISTINCT e.receiver
        FROM Ecrire e
        WHERE e.sender = :user
        """, User.class)
                .setParameter("user", user)
                .getResultList();

        List<User> receivedFrom = em.createQuery("""
        SELECT DISTINCT e.sender
        FROM Ecrire e
        WHERE e.receiver = :user
        """, User.class)
                .setParameter("user", user)
                .getResultList();

        em.close();

        // Fusionner les deux listes en supprimant les doublons
        Set<User> allUsers = new HashSet<>(sentTo);
        allUsers.addAll(receivedFrom);

        return new ArrayList<>(allUsers);
    }

    public List<Ecrire> findConversationBetweenUsers(User user1, User user2) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT e FROM Ecrire e WHERE " +
                                    "(e.sender = :user1 AND e.receiver = :user2) OR " +
                                    "(e.sender = :user2 AND e.receiver = :user1) " +
                                    "ORDER BY e.message.sendDate", Ecrire.class)
                    .setParameter("user1", user1)
                    .setParameter("user2", user2)
                    .getResultList();
        } finally {
            em.close();
        }
    }



}
