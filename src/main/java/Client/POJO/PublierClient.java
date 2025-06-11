package Client.POJO;

public class PublierClient {

    private MessageClient message;
    private ServerClient server;
    private UserClient user;

    public PublierClient() {}

    public PublierClient(MessageClient message, ServerClient server, UserClient user) {
        this.message = message;
        this.server = server;
        this.user = user;
    }

    public MessageClient getMessage() {
        return message;
    }

    public void setMessage(MessageClient message) {
        this.message = message;
    }

    public ServerClient getServer() {
        return server;
    }

    public void setServer(ServerClient server) {
        this.server = server;
    }

    public UserClient getUser() {
        return user;
    }

    public void setUser(UserClient user) {
        this.user = user;
    }
}
