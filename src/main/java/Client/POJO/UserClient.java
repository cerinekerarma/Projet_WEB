package Client.POJO;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserClient {
    private String id;           // <-- nouveau champ id
    private String email;
    private byte[] password;
    private Date creationDate;

    public UserClient() {}

    public UserClient(String id, String email, byte[] password, Date creationDate) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.creationDate = creationDate;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
