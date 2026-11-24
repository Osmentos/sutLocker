package sutlocker.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sutlocker.AppPaths;
import sutlocker.exception.AppException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Подключение к файлу базы данных SQLite и создание таблиц */
public class DatabaseManager {

    private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

    private final Connection connection;

    /**
     * Подключается к базе данных и создаёт таблицы, если их ещё нет
     *
     * @param filePath путь к файлу базы данных (":memory:" для базы в памяти, используется в тестах)
     * @throws AppException если подключиться не удалось
     */
    public DatabaseManager(String filePath) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + filePath);
            createTables();
            LOG.info("База данных: {}", filePath);
        } catch (SQLException e) {
            LOG.error("Ошибка подключения к базе данных", e);
            throw new AppException("Не удалось подключиться к базе данных", e);
        }
    }

    /**
     * Возвращает путь к файлу базы по умолчанию: рядом с jar-файлом
     * (при запуске из IDE в папке проекта)
     *
     * @return путь к файлу sutlocker.db
     */
    public static String defaultFilePath() {
        return AppPaths.folder().resolve("sutlocker.db").toString();
    }

    /**
     * Возвращает соединение с базой данных
     *
     * @return открытое соединение
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Закрывает соединение с базой данных
     *
     * @throws AppException если закрыть соединение не удалось
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            LOG.error("Ошибка при закрытии базы данных", e);
            throw new AppException("Не удалось закрыть базу данных", e);
        }
    }

    /**
     * Создаёт таблицы users и entries, если они не существуют
     *
     * @throws SQLException при ошибке выполнения запроса
     */
    private void createTables() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "login TEXT NOT NULL UNIQUE, "
                    + "password_hash TEXT NOT NULL, "
                    + "salt TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS entries ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "user_id INTEGER NOT NULL, "
                    + "service TEXT NOT NULL, "
                    + "login TEXT NOT NULL, "
                    + "password TEXT NOT NULL, "
                    + "type TEXT NOT NULL, "
                    + "salt TEXT)");
        }
    }
}