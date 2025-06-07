package POJO;

import java.io.Serializable;
import java.util.Objects;

public class EcrireId implements Serializable {
    private Integer message;

    public EcrireId() {}

    public EcrireId(Integer message) {
        this.message = message;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EcrireId ecrireId = (EcrireId) o;
        return Objects.equals(message, ecrireId.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message);
    }
}
