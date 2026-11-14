package sutlocker.db;

import sutlocker.exception.AppException;
import sutlocker.model.EncryptionType;
import sutlocker.model.PasswordEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Работа с таблицей entries
public class PasswordEntryDao {

    private final Connection connection;

    public PasswordEntryDao(DatabaseManager db) {
        this.connection = db.getConnection();
    }

    // Сохраняет новую запись
    public void insert(PasswordEntry entry) {
        String sql = "INSERT INTO entries (user_id, service, login, password, type, salt) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entry.getUserId());
            ps.setString(2, entry.getService());
            ps.setString(3, entry.getLogin());
            ps.setString(4, entry.getPassword());
            ps.setString(5, entry.getType().name());
            ps.setString(6, entry.getSalt());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Не удалось сохранить запись", e);
        }
    }

    // Возвращает все записи пользователя
    public List<PasswordEntry> findByUser(int userId) {
        String sql = "SELECT * FROM entries WHERE user_id = ? ORDER BY service";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<PasswordEntry> entries = new ArrayList<>();
            while (rs.next()) {
                entries.add(readEntry(rs));
            }
            return entries;
        } catch (SQLException e) {
            throw new AppException("Не удалось загрузить записи", e);
        }
    }

    // Удаляет запись пользователя по номеру
    public void delete(int id, int userId) {
        String sql = "DELETE FROM entries WHERE id = ? AND user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Не удалось удалить запись", e);
        }
    }

    private PasswordEntry readEntry(ResultSet rs) throws SQLException {
        return new PasswordEntry(rs.getInt("id"), rs.getInt("user_id"), rs.getString("service"),
                rs.getString("login"), rs.getString("password"),
                EncryptionType.valueOf(rs.getString("type")), rs.getString("salt"));
    }
}