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
}