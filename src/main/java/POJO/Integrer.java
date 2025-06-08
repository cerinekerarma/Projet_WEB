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
            referencedColumnName = "id_user",
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

    @Column(name = "date_joined", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateJoined;  // Nouveau champ utile

    // Constructeurs
    public Integrer() {}

    public Integrer(User user, Server server) {
        this.user = user;
        this.server = server;
        this.dateJoined = new Date();
    }

    // Getters/Setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Server getServer() { return server; }
    public void setServer(Server server) { this.server = server; }

    public Date getDateJoined() { return dateJoined; }
    public void setDateJoined(Date dateJoined) { this.dateJoined = dateJoined; }
}