package sutlocker.db;

import sutlocker.exception.AppException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Подключение к файлу базы данных SQLite и создание таблицы
public class DatabaseManager {

    private final Connection connection;

    // Подключается к базе, которая лежит в файле по указанному пути
    public DatabaseManager(String filePath) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + filePath);
            createTables();
        } catch (SQLException e) {
            throw new AppException("Не удалось подключиться к базе данных", e);
        }
    }

    // Находит путь к архиву
    public static String defaultFilePath() {
        try {
            Path location = Path.of(DatabaseManager.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI());
            Path folder = Files.isDirectory(location) ? Path.of("").toAbsolutePath() : location.getParent();
            return folder.resolve("sutlocker.db").toString();
        } catch (Exception e) {
            throw new AppException("Не удалось определить папку для базы данных", e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    // Закрывает соединение
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new AppException("Не удалось закрыть базу данных", e);
        }
    }
    //users - данные для входа в приложение,  entries - информация о паролях сохраненных пользователем
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