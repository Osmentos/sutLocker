package sutlocker.model;

//основные данные пользователя
public class User {

    private int id;
    private String login;
    private String passwordHash;
    private String salt;
    private String encryptedKey;

    public User(int id, String login, String passwordHash, String salt, String encryptedKey) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.encryptedKey = encryptedKey;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLogin() { return login; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
    public String getEncryptedKey() { return encryptedKey; }
}