package POJO;

import jakarta.persistence.*;

@Entity
@Table(name = "\"Server\"")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_server", nullable = false, updatable = false)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String nom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "id_admin",
            referencedColumnName = "login",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_server_admin")
    )
    private User admin;

    public Server() {}

    public Server(String nom, User admin) {
        this.nom = nom;
        this.admin = admin;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }
}