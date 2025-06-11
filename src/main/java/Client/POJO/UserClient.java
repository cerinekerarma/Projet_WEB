package Client.POJO;

import java.util.Date;

public class UserClient {
    private String login;
    private String email;
    private byte[] password;
    private Date creationDate;

    public UserClient() {}

    public UserClient(String login, String email, byte[] password, Date creationDate) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.creationDate = creationDate;
    }

    // Getters and setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
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

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
