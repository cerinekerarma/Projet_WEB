package POJO;

import java.io.Serializable;
import java.util.Objects;

public class PublierId implements Serializable {
    private Integer message;

    public PublierId() {}

    public PublierId(Integer message) {
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
        if (this == o) return true;
        if (!(o instanceof PublierId)) return false;
        PublierId that = (PublierId) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}

