package POJO;

import jakarta.persistence.*;

@Entity
@Table(name = "\"integrer\"")
@IdClass(Integrer.class)
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
}