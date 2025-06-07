package POJO;

import java.io.Serializable;
import java.util.Objects;

public class EmettreId implements Serializable {
    private Integer message;
    private Integer user;

    public EmettreId() {}

    public EmettreId(Integer message, Integer user) {
        this.message = message;
        this.user = user;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

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
