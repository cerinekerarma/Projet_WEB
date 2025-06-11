package Client.POJO;

public class EcrireClient {
    private MessageClient message;
    private UserClient sender;
    private UserClient receiver;

    public EcrireClient() {}

    public EcrireClient(MessageClient message, UserClient sender, UserClient receiver) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
    }

    public MessageClient getMessage() {
        return message;
    }

    public void setMessage(MessageClient message) {
        this.message = message;
    }

    public UserClient getSender() {
        return sender;
    }

    public void setSender(UserClient sender) {
        this.sender = sender;
    }

    public UserClient getReceiver() {
        return receiver;
    }

    public void setReceiver(UserClient receiver) {
        this.receiver = receiver;
    }
}
