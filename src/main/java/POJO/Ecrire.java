package POJO;

import jakarta.persistence.*;

@Entity
@Table(name = "ecrire")
@IdClass(EcrireId.class)
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

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}