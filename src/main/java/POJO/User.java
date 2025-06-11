package POJO;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "\"User\"") // Nécessaire car "User" est un mot réservé SQL
public class User {

    @Id
    @Column(name = "login", nullable = false, length = 255, updatable = false)
    private String login;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private byte[] password;

    @Column(name = "date_creation", nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    public User() {}

    public User(String login, String email, byte[] password, Date creationDate) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.creationDate = creationDate;
    }

    // Getters and Setters

    public String getId() {
        return login;
    }

    public void setId(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setDateCreation(Date creationDate) {
        this.creationDate = creationDate;
    }
}
