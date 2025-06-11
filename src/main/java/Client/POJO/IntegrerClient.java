package Client.POJO;

public class IntegrerClient {
    private UserClient user;
    private ServerClient server;

    public IntegrerClient() {}

    public IntegrerClient(UserClient user, ServerClient server) {
        this.user = user;
        this.server = server;
    }

    // Getters / Setters
    public UserClient getUser() {
        return user;
    }

    public void setUser(UserClient user) {
        this.user = user;
    }

    public ServerClient getServer() {
        return server;
    }

    public void setServer(ServerClient server) {
        this.server = server;
    }
}
