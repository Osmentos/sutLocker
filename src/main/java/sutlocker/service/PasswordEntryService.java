package sutlocker.service;

import sutlocker.crypto.CryptoStrategy;
import sutlocker.crypto.CryptoStrategyFactory;
import sutlocker.crypto.CryptoUtils;
import sutlocker.db.PasswordEntryDao;
import sutlocker.exception.AppException;
import sutlocker.model.EncryptionType;
import sutlocker.model.EntryView;
import sutlocker.model.PasswordEntry;

import java.util.List;

/** Добавление, удаление, просмотр и поиск записей текущего пользователя. */
public class PasswordEntryService {

    private final PasswordEntryDao dao;
    private final SessionContext session;
    private final CryptoStrategyFactory factory = new CryptoStrategyFactory();

    public PasswordEntryService(PasswordEntryDao dao, SessionContext session) {
        this.dao = dao;
        this.session = session;
    }

    /** Кодирует пароль выбранным способом и сохраняет запись. */
    public void add(String service, String login, String password, EncryptionType type) {
        validate(service, login, password, type);
        String salt = type == EncryptionType.MD5_SALT ? CryptoUtils.generateSalt() : null;
        CryptoStrategy strategy = factory.create(type, salt, session.getKey());
        String encoded = strategy.encode(password);
        dao.insert(new PasswordEntry(0, userId(), service.trim(), login.trim(), encoded, type, salt));
    }

    /** Удаляет запись текущего пользователя. */
    public void delete(int entryId) {
        dao.delete(entryId, userId());
    }

    /** Возвращает все записи пользователя для показа в таблице. */
    public List<EntryView> list() {
        return search("");
    }

    /** Ищет записи, у которых сервис или логин содержат текст запроса. */
    public List<EntryView> search(String query) {
        String text = query == null ? "" : query.trim().toLowerCase();
        return dao.findByUser(userId()).stream()
                .filter(e -> e.getService().toLowerCase().contains(text)
                        || e.getLogin().toLowerCase().contains(text))
                .map(this::toView)
                .toList();
    }

    /** Проверяет, совпадает ли введённый пароль с сохранённым в записи. */
    public boolean check(int entryId, String password) {
        PasswordEntry entry = findEntry(entryId);
        return strategyFor(entry).matches(password, entry.getPassword());
    }

    private int userId() {
        return session.getUser().getId();
    }

    private PasswordEntry findEntry(int entryId) {
        return dao.findByUser(userId()).stream()
                .filter(e -> e.getId() == entryId)
                .findFirst()
                .orElseThrow(() -> new AppException("Запись не найдена"));
    }

    private CryptoStrategy strategyFor(PasswordEntry entry) {
        return factory.create(entry.getType(), entry.getSalt(), session.getKey());
    }

    private EntryView toView(PasswordEntry entry) {
        return new EntryView(entry.getId(), entry.getService(), entry.getLogin(),
                shownPassword(entry), entry.getType());
    }

    /** Расшифрованный пароль для обратимых способов, хэш для необратимых. */
    private String shownPassword(PasswordEntry entry) {
        if (!entry.getType().isReversible()) {
            return entry.getPassword();
        }
        try {
            return strategyFor(entry).decode(entry.getPassword());
        } catch (AppException e) {
            return "[ошибка расшифровки]";
        }
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