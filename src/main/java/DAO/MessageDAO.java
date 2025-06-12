package DAO;

import POJO.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MessageDAO {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Message message) {
        executeInTransaction(em -> em.persist(message));
    }

    public Message findById(int id) {
        return execute(em -> em.find(Message.class, id));
    }

    public List<Message> findAll() {
        return execute(em ->
                em.createQuery("SELECT m FROM Message m", Message.class).getResultList()
        );
    }

    public void update(Message message) {
        executeInTransaction(em -> em.merge(message));
    }

    public void delete(Message message) {
        executeInTransaction(em -> {
            Message managedMessage = em.merge(message); // En cas d'entité détachée
            em.remove(managedMessage);
        });
    }

    public List<Message> findByServerId(int serverId) {
        return execute(em ->
                em.createNativeQuery("""
                    SELECT m.id_message, m.contenu, m.date_envoie
                    FROM "Message" m
                    JOIN publier p ON m.id_message = p.id_message
                    WHERE p.id_server = ?
                """, Message.class)
                        .setParameter(1, serverId)
                        .getResultList()
        );
    }

    public void insertIntoPublier(int messageId, int serverId, String userId) {
        executeInTransaction(em -> {
            Message message = em.find(Message.class, messageId);
            Server server = em.find(Server.class, serverId);
            User user = em.find(User.class, userId);

            if (message == null || server == null || user == null) {
                System.err.println("❌ Données manquantes :");
                if (message == null) System.err.println("  -> Message introuvable : " + messageId);
                if (server == null) System.err.println("  -> Serveur introuvable : " + serverId);
                if (user == null) System.err.println("  -> Auteur introuvable : " + userId);
                return;
            }

            Publier publier = new Publier();
            publier.setMessage(message);
            publier.setServer(server);
            publier.setUser(user);

            em.persist(publier);
            em.flush(); // Force l’insertion tout de suite (utile pour debug)
            System.out.println("✅ Entrée 'publier' insérée avec succès.");
        });
    }


    public void insertIntoEcrire(int messageId, String senderId, String receiverId) {
        executeInTransaction(em -> {
            Message message = em.find(Message.class, messageId);
            User sender = em.find(User.class, senderId);
            User receiver = em.find(User.class, receiverId);

            if (message == null || sender == null || receiver == null) {
                System.err.println("❌ Données manquantes :");
                if (message == null) System.err.println("  -> Message introuvable : " + messageId);
                if (sender == null) System.err.println("  -> Expéditeur introuvable : " + senderId);
                if (receiver == null) System.err.println("  -> Destinataire introuvable : " + receiverId);
                return;
            }

            Ecrire ecrire = new Ecrire(message, sender, receiver);
            em.persist(ecrire);
            em.flush();

            System.out.println("✅ Ecrire persisté : " + messageId + " de " + senderId + " à " + receiverId);
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
