package sutlocker.model;

//класс для записи информации в бд
public class PasswordEntry {

    private int id;
    private int userId;
    private String service;
    private String login;
    private String password;
    private EncryptionType type;
    private String salt;

    public PasswordEntry(int id, int userId, String service, String login,
                         String password, EncryptionType type, String salt) {
        this.id = id;
        this.userId = userId;
        this.service = service;
        this.login = login;
        this.password = password;
        this.type = type;
        this.salt = salt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public String getService() { return service; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public EncryptionType getType() { return type; }
    public String getSalt() { return salt; }
}