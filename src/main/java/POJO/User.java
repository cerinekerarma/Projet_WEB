package POJO;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "\"User\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer id;

    private String email;

    @Lob
    private byte[] password;

    @Column(name = "date_creation")
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    // Getters/Setters
}