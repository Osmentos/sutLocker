package sutlocker.crypto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {

    @Test
    void hashPassword_sameInputSameHash() {
        assertEquals(CryptoUtils.hashPassword("pass", "salt"), CryptoUtils.hashPassword("pass", "salt"));
    }

    @Test
    void hashPassword_differentSaltDifferentHash() {
        assertNotEquals(CryptoUtils.hashPassword("pass", "s1"), CryptoUtils.hashPassword("pass", "s2"));
    }

    @Test
    void generateSalt_isRandom() {
        assertNotEquals(CryptoUtils.generateSalt(), CryptoUtils.generateSalt());
    }

    @Test
    void deriveKey_stableAndDependsOnPassword() {
        assertArrayEquals(CryptoUtils.deriveKey("pass", "salt"), CryptoUtils.deriveKey("pass", "salt"));
        assertFalse(Arrays.equals(CryptoUtils.deriveKey("pass", "salt"), CryptoUtils.deriveKey("other", "salt")));
    }
}