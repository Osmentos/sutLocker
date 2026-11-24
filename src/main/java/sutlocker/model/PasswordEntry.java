package sutlocker.model;

/** Запись менеджера паролей в том виде, в котором она хранится в базе (пароль закодирован) */
public class PasswordEntry {

    private int id;
    private int userId;
    private String service;
    private String login;
    private String password;
    private EncryptionType type;
    private String salt;

    /**
     * Создаёт запись
     *
     * @param id       номер записи в базе (0, если ещё не сохранена)
     * @param userId   номер владельца записи
     * @param service  название сервиса
     * @param login    логин на сервисе
     * @param password закодированный пароль
     * @param type     способ хранения пароля
     * @param salt     соль (только для способа MD5 + соль, иначе null)
     */
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

    /** @return номер записи в базе */
    public int getId() { return id; }

    /**
     * Задаёт номер записи после сохранения в базе
     *
     * @param id новый номер
     */
    public void setId(int id) { this.id = id; }

    /** @return номер владельца записи */
    public int getUserId() { return userId; }

    /** @return название сервиса */
    public String getService() { return service; }

    /** @return логин на сервисе */
    public String getLogin() { return login; }

    /** @return закодированный пароль */
    public String getPassword() { return password; }

    /** @return способ хранения пароля */
    public EncryptionType getType() { return type; }

    /** @return соль или null, если способ хранения её не использует */
    public String getSalt() { return salt; }
}