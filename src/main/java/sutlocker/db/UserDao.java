package sutlocker.db;

import sutlocker.exception.AppException;
import sutlocker.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

// Работа с таблицей users
public class UserDao {

    private final Connection connection;

    public UserDao(DatabaseManager db) {
        this.connection = db.getConnection();
    }

    // Сохраняет нового пользователя
    public void insert(User user) {
        String sql = "INSERT INTO users (login, password_hash, salt) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getSalt());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Не удалось сохранить пользователя", e);
        }
    }

    // Ищет пользователя по логину
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? Optional.of(readUser(rs)) : Optional.empty();
        } catch (SQLException e) {
            throw new AppException("Не удалось найти пользователя", e);
        }
    }

    private User readUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("login"),
                rs.getString("password_hash"), rs.getString("salt"));
    }
}