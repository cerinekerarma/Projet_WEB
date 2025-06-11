package POJO;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "\"Message\"")  // Guillemets pour compatibilité avec certains SGBD
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_message", nullable = false, updatable = false)
    private Integer id;

    @Column(length=2000)
    private String contenu;

    @Column(name = "date_envoie", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date sendDate;

    public Message() {}

    public Message(String contenu, Date sendDate, User auteur) {
        this.contenu = contenu;
        this.sendDate = sendDate;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Date getSendDate() { return sendDate; }
    public void setSendDate(Date sendDate) { this.sendDate = sendDate; }
}