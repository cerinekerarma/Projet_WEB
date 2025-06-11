package POJO;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "integrer")
@IdClass(IntegrerId.class)
public class Integrer {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_user",
            referencedColumnName = "login",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_integrer_user")
    )
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_server",
            referencedColumnName = "id_server",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_integrer_server")
    )
    private Server server;

    // Constructeurs
    public Integrer() {}

    public Integrer(User user, Server server) {
        this.user = user;
        this.server = server;
    }

    // Getters/Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Server getServer() { return server; }
    public void setServer(Server server) { this.server = server; }
}