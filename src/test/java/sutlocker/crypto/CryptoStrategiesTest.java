package sutlocker.crypto;

import org.junit.jupiter.api.Test;
import sutlocker.exception.AppException;
import sutlocker.model.EncryptionType;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CryptoStrategiesTest {

    private final byte[] key = "0123456789abcdef".getBytes(StandardCharsets.UTF_8);

    @Test
    void plain_returnsSameValue() {
        CryptoStrategy s = new PlainStrategy();
        assertEquals("abc", s.encode("abc"));
    }

    @Test
    void base64_roundTrip() {
        CryptoStrategy s = new Base64Strategy();
        String encoded = s.encode("Пароль123!");
        assertNotEquals("Пароль123!", encoded);
        assertEquals("Пароль123!", s.decode(encoded));
    }

    @Test
    void md5_knownValue() {
        CryptoStrategy s = new Md5Strategy();
        assertEquals("5d41402abc4b2a76b9719d911017c592", s.encode("hello"));
    }

    @Test
    void md5_isNotReversible() {
        CryptoStrategy s = new Md5Strategy();
        assertFalse(s.isReversible());
        assertThrows(AppException.class, () -> s.decode("abc"));
    }

    @Test
    void md5Salt_differentSaltsGiveDifferentHashes() {
        String h1 = new Md5SaltStrategy("aaa").encode("pass");
        String h2 = new Md5SaltStrategy("bbb").encode("pass");
        assertNotEquals(h1, h2);
    }

    @Test
    void md5Salt_matches() {
        CryptoStrategy s = new Md5SaltStrategy("aaa");
        String stored = s.encode("pass");
        assertTrue(s.matches("pass", stored));
        assertFalse(s.matches("wrong", stored));
    }

    @Test
    void feistel_roundTrip() {
        CryptoStrategy s = new FeistelStrategy(key);
        String[] passwords = {"", "a", "12345678", "Пароль123!", "очень длинный пароль, больше двух блоков"};
        for (String p : passwords) {
            assertEquals(p, s.decode(s.encode(p)));
        }
    }

    @Test
    void feistel_encodedDiffersFromPlain() {
        CryptoStrategy s = new FeistelStrategy(key);
        assertNotEquals("secret", s.encode("secret"));
    }

    @Test
    void feistel_wrongKeyDoesNotRestore() {
        byte[] otherKey = "fedcba9876543210".getBytes(StandardCharsets.UTF_8);
        String encoded = new FeistelStrategy(key).encode("secret");
        try {
            assertNotEquals("secret", new FeistelStrategy(otherKey).decode(encoded));
        } catch (AppException e) {
            // тоже нормально: с чужим ключом расшифровка может завершиться ошибкой
        }
    }

    @Test
    void feistel_emptyKeyThrows() {
        assertThrows(AppException.class, () -> new FeistelStrategy(new byte[0]));
    }

    @Test
    void factory_returnsRightStrategies() {
        CryptoStrategyFactory f = new CryptoStrategyFactory();
        assertTrue(f.create(EncryptionType.FEISTEL, null, key).isReversible());
        assertFalse(f.create(EncryptionType.MD5_SALT, "s", null).isReversible());
        assertTrue(f.create(EncryptionType.PLAIN, null, null).isReversible());
    }

    @Test
    void factory_reversibilityMatchesEnum() {
        CryptoStrategyFactory f = new CryptoStrategyFactory();
        for (EncryptionType type : EncryptionType.values()) {
            assertEquals(type.isReversible(), f.create(type, "s", key).isReversible());
        }
    }
}