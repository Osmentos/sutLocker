package sutlocker.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sutlocker.AppPaths;
import sutlocker.exception.AppException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Подключение к файлу базы данных SQLite и создание таблиц. */
public class DatabaseManager {

    private static final Logger LOG = LogManager.getLogger(DatabaseManager.class);

    private final Connection connection;

    /** Подключается к базе, которая лежит в файле по указанному пути (":memory:" для тестов). */
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

    /** Путь к файлу базы по умолчанию: рядом с jar-файлом (при запуске из IDE: в папке проекта). */
    public static String defaultFilePath() {
        return AppPaths.folder().resolve("sutlocker.db").toString();
    }

    public Connection getConnection() {
        return connection;
    }

    /** Закрывает соединение. */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            LOG.error("Ошибка при закрытии базы данных", e);
            throw new AppException("Не удалось закрыть базу данных", e);
        }
    }

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