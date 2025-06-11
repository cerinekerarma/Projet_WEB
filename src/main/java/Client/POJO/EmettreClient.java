package Client.POJO;

public class EmettreClient {

    private MessageClient message;
    private UserClient user;
    private String reaction;

    public EmettreClient() {}

    public EmettreClient(MessageClient message, UserClient user, String reaction) {
        this.message = message;
        this.user = user;
        this.reaction = reaction;
    }

    public MessageClient getMessage() {
        return message;
    }

    public void setMessage(MessageClient message) {
        this.message = message;
    }

    public UserClient getUser() {
        return user;
    }

    public void setUser(UserClient user) {
        this.user = user;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }
}
