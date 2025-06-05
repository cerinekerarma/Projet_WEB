package POJO;

import jakarta.persistence.*;

@Entity
@Table(name = "\"publier\"")
public class Publier {
    @Id
    @OneToOne
    @JoinColumn(name = "id_message", referencedColumnName = "id_message")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "id_server", referencedColumnName = "id_server")
    private Server server;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;

    // Getters/Setters

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}