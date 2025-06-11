package POJO;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "\"User\"") // Nécessaire car "User" est un mot réservé SQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user", nullable = false, updatable = false)
    private Integer id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private byte[] password;

    @Column(name = "date_creation", nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    public User() {}

    public User(String email, byte[] password, Date creationDate) {
        this.email = email;
        this.password = password;
        this.creationDate = creationDate;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public byte[] getPassword() { return password; }
    public void setPassword(byte[] password) { this.password = password; }

    public Date getCreationDate() { return creationDate; }
    public void setDateCreation(Date creationDate) { this.creationDate = creationDate; }
}