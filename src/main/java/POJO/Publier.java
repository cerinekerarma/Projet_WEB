package POJO;

import jakarta.persistence.*;

@Entity
@Table(name = "publier")
public class Publier {
    @Id
    @OneToOne
    @JoinColumn(
            name = "id_message",
            referencedColumnName = "id_message",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_publier_message")
    )
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_server",
            referencedColumnName = "id_server",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_publier_server")
    )
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_user",
            referencedColumnName = "id_user",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_publier_user")
    )
    private User user;

    public Publier() {}

    public Publier(Message message, Server server, User user) {
        this.message = message;
        this.server = server;
        this.user = user;
    }

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
