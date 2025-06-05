package POJO;


import jakarta.persistence.*;

@Entity
@Table(name = "\"emettre\"")
@IdClass(Emettre.class)
public class Emettre {
    @Id
    @ManyToOne
    @JoinColumn(name = "id_message", referencedColumnName = "id_message")
    private Message message;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private User user;

    private String reaction;

    // Getters/Setters
}
