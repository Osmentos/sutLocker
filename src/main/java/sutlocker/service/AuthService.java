package sutlocker.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sutlocker.crypto.CryptoUtils;
import sutlocker.db.UserDao;
import sutlocker.exception.AppException;
import sutlocker.model.User;

/** Регистрация и вход пользователей. */
public class AuthService {

    private static final Logger LOG = LogManager.getLogger(AuthService.class);

    private final UserDao userDao;

    /**
     * Создаёт сервис.
     *
     * @param userDao доступ к таблице пользователей
     */
    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Регистрирует нового пользователя: создаёт соль и сохраняет хэш пароля.
     *
     * @param login    логин (минимум 3 символа)
     * @param password пароль (минимум 6 символов)
     * @throws AppException если данные некорректны или логин уже занят
     */
    public void register(String login, String password) {
        validate(login, password);
        String cleanLogin = login.trim();
        if (userDao.findByLogin(cleanLogin).isPresent()) {
            LOG.warn("Регистрация отклонена: логин {} уже занят", cleanLogin);
            throw new AppException("Пользователь с таким логином уже существует");
        }
        String salt = CryptoUtils.generateSalt();
        String hash = CryptoUtils.hashPassword(password, salt);
        userDao.insert(new User(0, cleanLogin, hash, salt));
        LOG.info("Зарегистрирован пользователь {}", cleanLogin);
    }

    /**
     * Проверяет логин и пароль и создаёт сеанс с ключом шифрования.
     *
     * @param login    логин
     * @param password пароль
     * @return данные сеанса
     * @throws AppException если логин или пароль неверны
     */
    public SessionContext login(String login, String password) {
        if (login == null || password == null) {
            throw loginFailed(login);
        }
        User user = userDao.findByLogin(login.trim())
                .orElseThrow(() -> loginFailed(login));
        String hash = CryptoUtils.hashPassword(password, user.getSalt());
        if (!hash.equals(user.getPasswordHash())) {
            throw loginFailed(login);
        }
        LOG.info("Вход выполнен: {}", user.getLogin());
        return new SessionContext(user, CryptoUtils.deriveKey(password, user.getSalt()));
    }

    /**
     * Записывает неудачный вход в лог и создаёт ошибку для пользователя.
     *
     * @param login введённый логин
     * @return исключение с общим сообщением (не говорит, что именно неверно)
     */
    private AppException loginFailed(String login) {
        LOG.warn("Неудачная попытка входа: логин {}", login);
        return new AppException("Неверный логин или пароль");
    }

    /**
     * Проверяет логин и пароль при регистрации.
     *
     * @param login    логин
     * @param password пароль
     * @throws AppException если логин или пароль слишком короткие
     */
    private void validate(String login, String password) {
        if (login == null || login.trim().length() < 3) {
            throw new AppException("Логин должен содержать минимум 3 символа");
        }
        if (password == null || password.length() < 6) {
            throw new AppException("Пароль должен содержать минимум 6 символов");
        }
    }
}