package sutlocker.crypto;

import sutlocker.exception.AppException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;

/** Вспомогательные методы: соль, хэш мастер-пароля, ключ шифрования. */
public class CryptoUtils {

    private static final SecureRandom RANDOM = new SecureRandom();

    /** Случайная соль в виде строки. */
    public static String generateSalt() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /** Хэш мастер-пароля: SHA-256 от «соль + пароль». */
    public static String hashPassword(String password, String salt) {
        return HexFormat.of().formatHex(sha256(salt + password));
    }

    /** Ключ для шифра Фейстеля, получается из мастер-пароля и соли. */
    public static byte[] deriveKey(String password, String salt) {
        return Arrays.copyOf(sha256("key" + salt + password), 16);
    }

    private static byte[] sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new AppException("Ошибка SHA-256", e);
        }
    }
}