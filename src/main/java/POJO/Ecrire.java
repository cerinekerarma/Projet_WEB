package POJO;

import jakarta.persistence.*;

@Entity
@Table(name = "\"ecrire\"")
public class Ecrire {
    @Id
    @OneToOne
    @JoinColumn(name = "id_message", referencedColumnName = "id_message")
    private Message message;

    @ManyToOne
    @JoinColumn(name = "id_user1", referencedColumnName = "id_user")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "id_user2", referencedColumnName = "id_user")
    private User receiver;

    // Getters/Setters
}