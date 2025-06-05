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
}