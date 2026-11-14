package sutlocker.service;

import sutlocker.crypto.CryptoUtils;
import sutlocker.db.UserDao;
import sutlocker.exception.AppException;
import sutlocker.model.User;

// Регистрация и вход
public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Регистрирует нового пользователя
    public void register(String login, String password) {
        validate(login, password);
        String cleanLogin = login.trim();
        if (userDao.findByLogin(cleanLogin).isPresent()) {
            throw new AppException("Пользователь с таким логином уже существует");
        }
        String salt = CryptoUtils.generateSalt();
        String hash = CryptoUtils.hashPassword(password, salt);
        userDao.insert(new User(0, cleanLogin, hash, salt));
    }

    // Проверяет логин и пароль при регистрации
    private void validate(String login, String password) {
        if (login == null || login.trim().length() < 3) {
            throw new AppException("Логин должен содержать минимум 3 символа");
        }
        if (password == null || password.length() < 6) {
            throw new AppException("Пароль должен содержать минимум 6 символов");
        }
    }


    // Проверяет логин и пароль, возвращает данные сеанса
    public SessionContext login(String login, String password) {
        if (login == null || password == null) {
            throw new AppException("Неверный логин или пароль");
        }
        User user = userDao.findByLogin(login.trim())
                .orElseThrow(() -> new AppException("Неверный логин или пароль"));
        String hash = CryptoUtils.hashPassword(password, user.getSalt());
        if (!hash.equals(user.getPasswordHash())) {
            throw new AppException("Неверный логин или пароль");
        }
        return new SessionContext(user, CryptoUtils.deriveKey(password, user.getSalt()));
    }
}