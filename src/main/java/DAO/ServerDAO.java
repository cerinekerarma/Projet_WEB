package DAO;

import POJO.Server;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class ServerDAO {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU_JPA");

    public void create(Server server) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(server);
        em.getTransaction().commit();
        em.close();
    }

    public Server findById(int id) {
        EntityManager em = emf.createEntityManager();
        Server server = em.find(Server.class, id);
        em.close();
        return server;
    }

    public List<Server> findAll() {
        EntityManager em = emf.createEntityManager();
        List<Server> servers = em.createQuery("SELECT s FROM Server s", Server.class).getResultList();
        em.close();
        return servers;
    }

    public void update(Server server) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(server);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Server server) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Server managedServer = em.merge(server);  // au cas où l'objet est détaché
        em.remove(managedServer);
        em.getTransaction().commit();
        em.close();
    }
}

