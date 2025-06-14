package POJO;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "ecrire")
public class Ecrire {
    @Id
    @OneToOne
    @JoinColumn(
            name = "id_message",
            referencedColumnName = "id_message",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ecrire_message")
    )
    private Message message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "id_user1",
            referencedColumnName = "login",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ecrire_sender")
    )
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "id_user2",
            referencedColumnName = "login",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ecrire_receiver")
    )
    private User receiver;

    public Ecrire() {}

    public Ecrire(Message message, User sender, User receiver) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }
}