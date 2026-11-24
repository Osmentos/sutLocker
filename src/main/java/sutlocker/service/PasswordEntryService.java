package sutlocker.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOG = LogManager.getLogger(PasswordEntryService.class);

    private final PasswordEntryDao dao;
    private final SessionContext session;
    private final CryptoStrategyFactory factory = new CryptoStrategyFactory();

    /**
     * Создаёт сервис для вошедшего пользователя.
     *
     * @param dao     доступ к таблице записей
     * @param session данные текущего сеанса
     */
    public PasswordEntryService(PasswordEntryDao dao, SessionContext session) {
        this.dao = dao;
        this.session = session;
    }

    /**
     * Кодирует пароль выбранным способом и сохраняет запись.
     *
     * @param service  название сервиса
     * @param login    логин на сервисе
     * @param password пароль
     * @param type     способ хранения
     * @throws AppException если какое-то поле не заполнено
     */
    public void add(String service, String login, String password, EncryptionType type) {
        validate(service, login, password, type);
        String salt = type == EncryptionType.MD5_SALT ? CryptoUtils.generateSalt() : null;
        CryptoStrategy strategy = factory.create(type, salt, session.getKey());
        String encoded = strategy.encode(password);
        dao.insert(new PasswordEntry(0, userId(), service.trim(), login.trim(), encoded, type, salt));
        LOG.info("Добавлена запись: сервис {}, способ {}", service.trim(), type);
    }

    /**
     * Удаляет запись текущего пользователя.
     *
     * @param entryId номер записи
     */
    public void delete(int entryId) {
        dao.delete(entryId, userId());
        LOG.info("Удалена запись {} пользователя {}", entryId, session.getUser().getLogin());
    }

    /**
     * Возвращает все записи пользователя для показа в таблице.
     *
     * @return список записей
     */
    public List<EntryView> list() {
        return search("");
    }

    /**
     * Ищет записи, у которых сервис или логин содержат текст запроса (без учёта регистра).
     *
     * @param query текст запроса (пустой или null означает все записи)
     * @return найденные записи
     */
    public List<EntryView> search(String query) {
        String text = query == null ? "" : query.trim().toLowerCase();
        return dao.findByUser(userId()).stream()
                .filter(e -> e.getService().toLowerCase().contains(text)
                        || e.getLogin().toLowerCase().contains(text))
                .map(this::toView)
                .toList();
    }

    /**
     * Проверяет, совпадает ли введённый пароль с сохранённым в записи.
     *
     * @param entryId  номер записи
     * @param password введённый пароль
     * @return true, если пароль верный
     * @throws AppException если записи нет у текущего пользователя
     */
    public boolean check(int entryId, String password) {
        PasswordEntry entry = findEntry(entryId);
        boolean ok = strategyFor(entry).matches(password, entry.getPassword());
        LOG.info("Проверка пароля записи {}: {}", entryId, ok ? "верно" : "неверно");
        return ok;
    }

    /**
     * Возвращает номер текущего пользователя.
     *
     * @return номер пользователя
     */
    private int userId() {
        return session.getUser().getId();
    }

    /**
     * Находит запись текущего пользователя по номеру.
     *
     * @param entryId номер записи
     * @return найденная запись
     * @throws AppException если записи нет
     */
    private PasswordEntry findEntry(int entryId) {
        return dao.findByUser(userId()).stream()
                .filter(e -> e.getId() == entryId)
                .findFirst()
                .orElseThrow(() -> new AppException("Запись не найдена"));
    }

    /**
     * Создаёт стратегию шифрования для записи.
     *
     * @param entry запись
     * @return стратегия, подходящая способу хранения записи
     */
    private CryptoStrategy strategyFor(PasswordEntry entry) {
        return factory.create(entry.getType(), entry.getSalt(), session.getKey());
    }

    /**
     * Превращает запись из базы в запись для показа в таблице.
     *
     * @param entry запись из базы
     * @return запись с паролем для показа
     */
    private EntryView toView(PasswordEntry entry) {
        return new EntryView(entry.getId(), entry.getService(), entry.getLogin(),
                shownPassword(entry), entry.getType());
    }

    /**
     * Возвращает пароль для показа: расшифрованный для обратимых способов, хэш для необратимых.
     *
     * @param entry запись из базы
     * @return пароль, хэш или сообщение об ошибке расшифровки
     */
    private String shownPassword(PasswordEntry entry) {
        if (!entry.getType().isReversible()) {
            return entry.getPassword();
        }
        try {
            return strategyFor(entry).decode(entry.getPassword());
        } catch (AppException e) {
            LOG.warn("Не удалось расшифровать запись {}", entry.getId(), e);
            return "[ошибка расшифровки]";
        }
    }

    /**
     * Проверяет, что все поля новой записи заполнены.
     *
     * @param service  название сервиса
     * @param login    логин
     * @param password пароль
     * @param type     способ хранения
     * @throws AppException если какое-то поле пустое
     */
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