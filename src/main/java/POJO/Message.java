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

    @Column(nullable = false, length = 2000)
    @Lob
    private String contenu;

    @Column(name = "date_envoie", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User auteur;

    public Message() {}

    public Message(String contenu, Date sendDate, User auteur) {
        this.contenu = contenu;
        this.sendDate = sendDate;
        this.auteur = auteur;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Date getSendDate() { return sendDate; }
    public void setSendDate(Date sendDate) { this.sendDate = sendDate; }

    public User getAuteur() { return auteur; }
    public void setAuteur(User auteur) { this.auteur = auteur; }
}