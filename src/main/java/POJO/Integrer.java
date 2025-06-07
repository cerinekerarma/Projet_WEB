package POJO;

import jakarta.persistence.*;

@Entity
@Table(name = "integrer")
@IdClass(IntegrerId.class)
public class Integrer {
    @Id
    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_server", referencedColumnName = "id_server")
    private Server server;

    // Getters/Setters

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}