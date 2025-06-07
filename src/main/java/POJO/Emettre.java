package POJO;


import jakarta.persistence.*;

@Entity
@Table(name = "\"emettre\"")
@IdClass(EmettreId.class)
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

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}
