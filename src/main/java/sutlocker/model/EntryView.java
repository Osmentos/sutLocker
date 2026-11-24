package sutlocker.model;

/**
 * Запись менеджера паролей в виде, пригодном для показа в таблице на главном экране
 * <p>
 * В отличие от {@link PasswordEntry}, которая хранит данные так, как они лежат в базе
 * (пароль закодирован), здесь в поле {@code password} находится значение для показа:
 * расшифрованный пароль для обратимых способов хранения или хэш для необратимых.
 * Объект создаётся сервисом {@code PasswordEntryService} и в базу не сохраняется.
 */
public class EntryView {

    private int id;
    private String service;
    private String login;
    private String password;
    private EncryptionType type;

    /**
     * Создаёт запись для показа.
     *
     * @param id       номер записи в базе
     * @param service  название сервиса, например Gmail
     * @param login    логин на этом сервисе
     * @param password пароль для показа: расшифрованный или хэш
     * @param type     способ, которым пароль хранится в базе
     */
    public EntryView(int id, String service, String login, String password, EncryptionType type) {
        this.id = id;
        this.service = service;
        this.login = login;
        this.password = password;
        this.type = type;
    }

    /** @return номер записи в базе */
    public int getId() { return id; }

    /** @return название сервиса */
    public String getService() { return service; }

    /** @return логин на сервисе */
    public String getLogin() { return login; }

    /** @return пароль для показа (расшифрованный или хэш) */
    public String getPassword() { return password; }

    /** @return способ хранения пароля в базе */
    public EncryptionType getType() { return type; }
}