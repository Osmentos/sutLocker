package sutlocker.model;

/**
 * Пользователь приложения: учётная запись, с которой выполняется вход.
 * <p>
 * Пароль пользователя в открытом виде не хранится: в базе лежат случайная соль
 * и хэш, посчитанный от строки «соль + пароль». По ним при входе проверяется
 * введённый пароль.
 */
public class User {

    private int id;
    private String login;
    private String passwordHash;
    private String salt;

    /**
     * Создаёт пользователя.
     *
     * @param id           номер пользователя в базе (0, если ещё не сохранён)
     * @param login        логин
     * @param passwordHash хэш мастер-пароля
     * @param salt         случайная соль, с которой считался хэш
     */
    public User(int id, String login, String passwordHash, String salt) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    /** @return номер пользователя в базе */
    public int getId() { return id; }

    /**
     * Задаёт номер пользователя после сохранения в базе.
     *
     * @param id новый номер
     */
    public void setId(int id) { this.id = id; }

    /** @return логин пользователя */
    public String getLogin() { return login; }

    /** @return хэш мастер-пароля */
    public String getPasswordHash() { return passwordHash; }

    /** @return соль, использованная при вычислении хэша */
    public String getSalt() { return salt; }
}