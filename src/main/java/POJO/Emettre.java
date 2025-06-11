package POJO;


import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "emettre")
@IdClass(EmettreId.class)
public class Emettre {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_message",
            referencedColumnName = "id_message",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_emettre_message")
    )
    private Message message;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_user",
            referencedColumnName = "login",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_emettre_user")
    )
    private User user;

    @Column(nullable = false, length = 50)
    private String reaction;  // Ex: "like", "love", "haha"...

    // Constructeurs
    public Emettre() {}

    public Emettre(Message message, User user, String reaction) {
        this.message = message;
        this.user = user;
        this.reaction = reaction;
    }

    // Getters/Setters
    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }
}