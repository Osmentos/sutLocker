package sutlocker.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sutlocker.db.DatabaseManager;
import sutlocker.db.UserDao;
import sutlocker.exception.AppException;
import sutlocker.model.User;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private DatabaseManager db;
    private UserDao userDao;
    private AuthService auth;

    @BeforeEach
    void setUp() {
        db = new DatabaseManager(":memory:");
        userDao = new UserDao(db);
        auth = new AuthService(userDao);
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    void register_savesUserWithHashedPassword() {
        auth.register("osmen", "secret123");
        User user = userDao.findByLogin("osmen").orElseThrow();
        assertNotEquals("secret123", user.getPasswordHash());
        assertNotNull(user.getSalt());
    }

    @Test
    void register_duplicateLoginThrows() {
        auth.register("osmen", "secret123");
        assertThrows(AppException.class, () -> auth.register("osmen", "other456"));
    }

    @Test
    void register_shortPasswordThrows() {
        assertThrows(AppException.class, () -> auth.register("osmen", "123"));
    }

    @Test
    void register_shortLoginThrows() {
        assertThrows(AppException.class, () -> auth.register("ab", "secret123"));
    }

    @Test
    void login_successReturnsSessionWithKey() {
        auth.register("osmen", "secret123");
        SessionContext session = auth.login("osmen", "secret123");
        assertEquals("osmen", session.getUser().getLogin());
        assertEquals(16, session.getKey().length);
    }

    @Test
    void login_wrongPasswordThrows() {
        auth.register("osmen", "secret123");
        assertThrows(AppException.class, () -> auth.login("osmen", "wrong"));
    }

    @Test
    void login_unknownUserThrows() {
        assertThrows(AppException.class, () -> auth.login("nobody", "secret123"));
    }
}