package sutlocker.service;

import sutlocker.crypto.CryptoStrategy;
import sutlocker.crypto.CryptoStrategyFactory;
import sutlocker.crypto.CryptoUtils;
import sutlocker.db.PasswordEntryDao;
import sutlocker.exception.AppException;
import sutlocker.model.EncryptionType;
import sutlocker.model.PasswordEntry;

// Добавление, удаление и просмотр записей текущего пользователя
public class PasswordEntryService {

    private final PasswordEntryDao dao;
    private final SessionContext session;
    private final CryptoStrategyFactory factory = new CryptoStrategyFactory();

    public PasswordEntryService(PasswordEntryDao dao, SessionContext session) {
        this.dao = dao;
        this.session = session;
    }

    // Кодирует пароль выбранным способом и сохраняет запись
    public void add(String service, String login, String password, EncryptionType type) {
        validate(service, login, password, type);
        String salt = type == EncryptionType.MD5_SALT ? CryptoUtils.generateSalt() : null;
        CryptoStrategy strategy = factory.create(type, salt, session.getKey());
        String encoded = strategy.encode(password);
        int userId = session.getUser().getId();
        dao.insert(new PasswordEntry(0, userId, service.trim(), login.trim(), encoded, type, salt));
    }

    // Удаляет запись текущего пользователя(номер записи понятен из окна)
    public void delete(int entryId) {
        dao.delete(entryId, session.getUser().getId());
    }

    private void validate(String service, String login, String password, EncryptionType type) {
        if (service == null || service.isBlank()) {
            throw new AppException("Укажите название сервиса");
        }
        if (login == null || login.isBlank()) {
            throw new AppException("Укажите логин");
        }
        if (password == null || password.isEmpty()) {
            throw new AppException("Укажите пароль");
        }
        if (type == null) {
            throw new AppException("Выберите способ хранения");
        }
    }
}