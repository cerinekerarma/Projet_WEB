package POJO;

import java.io.Serializable;
import java.util.Objects;

public class EmettreId implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer message;
    private Integer user;

    // Constructeurs
    public EmettreId() {}

    public EmettreId(Integer messageId, Integer userId) {
        this.message = messageId;
        this.user = userId;
    }

    // Getters/Setters
    public Integer getMessage() { return message; }
    public void setMessage(Integer message) { this.message = message; }

    public Integer getUser() { return user; }
    public void setUser(Integer user) { this.user = user; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EmettreId emettreId = (EmettreId) o;
        return Objects.equals(message, emettreId.message) && Objects.equals(user, emettreId.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, user);
    }
}