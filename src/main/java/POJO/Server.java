package POJO;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Server\"")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_server")
    private Integer id;

    private String nom;

    @ManyToOne
    @JoinColumn(name = "id_admin", referencedColumnName = "id_user")
    private User admin;

    // Getters/Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }
}