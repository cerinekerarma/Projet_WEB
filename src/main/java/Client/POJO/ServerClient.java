package Client.POJO;

public class ServerClient {

    private Integer id;
    private String nom;
    private UserClient admin;  // objet UserClient au lieu de juste login

    public ServerClient() {}

    public ServerClient(Integer id, String nom, UserClient admin) {
        this.id = id;
        this.nom = nom;
        this.admin = admin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public UserClient getAdmin() {
        return admin;
    }

    public void setAdmin(UserClient admin) {
        this.admin = admin;
    }
}
