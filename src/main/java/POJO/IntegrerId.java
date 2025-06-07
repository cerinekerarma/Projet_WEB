package POJO;

import java.io.Serializable;
import java.util.Objects;

public class IntegrerId implements Serializable {
    private Integer user;
    private Integer server;

    public IntegrerId() {
    }

    public IntegrerId(Integer userId, Integer serverId) {
        this.user = userId;
        this.server = serverId;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Integer getServer() {
        return server;
    }

    public void setServer(Integer server) {
        this.server = server;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IntegrerId that = (IntegrerId) o;
        return Objects.equals(user, that.user) && Objects.equals(server, that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, server);
    }
}