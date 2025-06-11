package Client.POJO;

import java.util.Date;

public class MessageClient {
    private int id;
    private String contenu;
    private Date sendDate;

    public MessageClient() {}

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public Date getSendDate() { return sendDate; }
    public void setSendDate(Date sendDate) { this.sendDate = sendDate; }
}
