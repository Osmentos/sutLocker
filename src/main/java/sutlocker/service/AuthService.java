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

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /** Регистрирует нового пользователя. */
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

    /** Проверяет логин и пароль, возвращает данные сеанса. */
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

    /** Записывает неудачный вход в лог и создаёт ошибку для пользователя. */
    private AppException loginFailed(String login) {
        LOG.warn("Неудачная попытка входа: логин {}", login);
        return new AppException("Неверный логин или пароль");
    }

    /** Проверяет логин и пароль при регистрации. */
    private void validate(String login, String password) {
        if (login == null || login.trim().length() < 3) {
            throw new AppException("Логин должен содержать минимум 3 символа");
        }
        if (password == null || password.length() < 6) {
            throw new AppException("Пароль должен содержать минимум 6 символов");
        }
    }
}