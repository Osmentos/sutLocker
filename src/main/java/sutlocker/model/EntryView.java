package sutlocker.model;

/** Запись для таблицы на главном экране */
public class EntryView {

    private int id;
    private String service;
    private String login;
    private String password;
    private EncryptionType type;

    public EntryView(int id, String service, String login, String password, EncryptionType type) {
        this.id = id;
        this.service = service;
        this.login = login;
        this.password = password;
        this.type = type;
    }

    public int getId() { return id; }
    public String getService() { return service; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public EncryptionType getType() { return type; }
}