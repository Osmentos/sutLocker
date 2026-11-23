package sutlocker.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sutlocker.db.DatabaseManager;
import sutlocker.db.PasswordEntryDao;
import sutlocker.db.UserDao;
import sutlocker.exception.AppException;
import sutlocker.model.EncryptionType;
import sutlocker.model.EntryView;
import sutlocker.model.PasswordEntry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEntryServiceTest {

    private DatabaseManager db;
    private PasswordEntryDao dao;
    private PasswordEntryService service;
    private SessionContext session;

    @BeforeEach
    void setUp() {
        db = new DatabaseManager(":memory:");
        AuthService auth = new AuthService(new UserDao(db));
        auth.register("osmen", "secret123");
        session = auth.login("osmen", "secret123");
        dao = new PasswordEntryDao(db);
        service = new PasswordEntryService(dao, session);
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    private List<PasswordEntry> storedEntries() {
        return dao.findByUser(session.getUser().getId());
    }

    @Test
    void add_plainStoresPasswordAsIs() {
        service.add("Gmail", "me@mail.ru", "pass123", EncryptionType.PLAIN);
        assertEquals("pass123", storedEntries().get(0).getPassword());
    }

    @Test
    void add_base64StoresEncodedPassword() {
        service.add("Gmail", "me@mail.ru", "pass123", EncryptionType.BASE64);
        assertEquals("cGFzczEyMw==", storedEntries().get(0).getPassword());
    }

    @Test
    void add_feistelStoresEncryptedPassword() {
        service.add("Gmail", "me@mail.ru", "pass123", EncryptionType.FEISTEL);
        assertNotEquals("pass123", storedEntries().get(0).getPassword());
    }

    @Test
    void add_md5SaltSavesSalt() {
        service.add("Gmail", "me@mail.ru", "pass123", EncryptionType.MD5_SALT);
        assertNotNull(storedEntries().get(0).getSalt());
    }

    @Test
    void add_emptyServiceThrows() {
        assertThrows(AppException.class,
                () -> service.add("", "me@mail.ru", "pass123", EncryptionType.PLAIN));
    }

    @Test
    void delete_removesEntry() {
        service.add("Gmail", "me@mail.ru", "pass123", EncryptionType.PLAIN);
        service.delete(storedEntries().get(0).getId());
        assertTrue(storedEntries().isEmpty());
    }

    @Test
    void list_showsDecodedPasswordForReversibleTypes() {
        service.add("A", "a", "pass1", EncryptionType.PLAIN);
        service.add("B", "b", "pass2", EncryptionType.BASE64);
        service.add("C", "c", "pass3", EncryptionType.FEISTEL);
        List<EntryView> views = service.list();
        assertEquals("pass1", views.get(0).getPassword());
        assertEquals("pass2", views.get(1).getPassword());
        assertEquals("pass3", views.get(2).getPassword());
    }

    @Test
    void list_showsHashForMd5() {
        service.add("A", "a", "hello", EncryptionType.MD5);
        EntryView view = service.list().get(0);
        assertEquals("5d41402abc4b2a76b9719d911017c592", view.getPassword());
        assertFalse(view.getType().isReversible());
    }

    @Test
    void check_md5SaltRightAndWrongPassword() {
        service.add("A", "a", "pass123", EncryptionType.MD5_SALT);
        int id = service.list().get(0).getId();
        assertTrue(service.check(id, "pass123"));
        assertFalse(service.check(id, "wrong"));
    }

    @Test
    void check_differentPasswordsMatchOnlyOwnEntry() {
        service.add("A", "a", "pass1", EncryptionType.MD5);
        service.add("B", "b", "pass2", EncryptionType.MD5);
        List<EntryView> views = service.list();
        assertTrue(service.check(views.get(0).getId(), "pass1"));
        assertFalse(service.check(views.get(0).getId(), "pass2"));
    }

    @Test
    void search_filtersByServiceAndLogin() {
        service.add("Gmail", "me@mail.ru", "p1", EncryptionType.PLAIN);
        service.add("GitHub", "osmen", "p2", EncryptionType.PLAIN);
        assertEquals(2, service.search("").size());
        assertEquals(1, service.search("git").size());
        assertEquals(1, service.search("OSMEN").size());
        assertEquals(0, service.search("xyz").size());
    }

    @Test
    void list_showsOnlyOwnEntries() {
        service.add("Gmail", "me@mail.ru", "p1", EncryptionType.PLAIN);
        AuthService auth = new AuthService(new UserDao(db));
        auth.register("other", "secret456");
        PasswordEntryService otherService =
                new PasswordEntryService(dao, auth.login("other", "secret456"));
        assertTrue(otherService.list().isEmpty());
    }
}