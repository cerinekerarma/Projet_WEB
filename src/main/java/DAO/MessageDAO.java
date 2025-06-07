package DAO;

import POJO.Message;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class MessageDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Message message) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(message);
        em.getTransaction().commit();
        em.close();
    }

    public Message findById(int id) {
        EntityManager em = emf.createEntityManager();
        Message message = em.find(Message.class, id);
        em.close();
        return message;
    }

    public List<Message> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Message> messages = em.createQuery("SELECT m FROM Message m", Message.class).getResultList();
        em.close();
        return messages;
    }

    public void update(Message message) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(message);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Message message) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Message managedMessage = em.merge(message); // Si détaché
        em.remove(managedMessage);
        em.getTransaction().commit();
        em.close();
    }

    public List<Message> findByServerId(int channelId) {
        EntityManager em = emf.createEntityManager();
        List<Message> messages = em.createQuery(
                        "SELECT m FROM Message m WHERE m.id = :channelId", Message.class)
                .setParameter("channelId", channelId)
                .getResultList();
        em.close();
        return messages;
    }
}
