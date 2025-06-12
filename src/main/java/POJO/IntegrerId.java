package POJO;

import java.io.Serializable;
import java.util.Objects;

public class IntegrerId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String user; // login de User
    private Integer server; // id de Server

    public IntegrerId() {}

    public IntegrerId(String userId, Integer serverId) {
        this.user = userId;
        this.server = serverId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
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
        if (this == o) return true;
        if (!(o instanceof IntegrerId)) return false;
        IntegrerId that = (IntegrerId) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(server, that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, server);
    }
}
