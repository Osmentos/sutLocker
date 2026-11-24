package sutlocker.crypto;

import sutlocker.exception.AppException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;

/** Вспомогательные методы: соль, хэш мастер-пароля, ключ шифрования */
public class CryptoUtils {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Создаёт случайную соль
     *
     * @return соль в виде строки Base64
     */
    public static String generateSalt() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Считает хэш мастер-пароля: SHA-256 от строки «соль + пароль»
     *
     * @param password мастер-пароль
     * @param salt     соль пользователя
     * @return хэш в виде шестнадцатеричной строки
     */
    public static String hashPassword(String password, String salt) {
        return HexFormat.of().formatHex(sha256(salt + password));
    }

    /**
     * Получает ключ для шифра Фейстеля из мастер-пароля и соли
     * Слово «key» в начале строки делает ключ отличным от хэша, который хранится в базе
     *
     * @param password мастер-пароль
     * @param salt     соль пользователя
     * @return ключ длиной 16 байт
     */
    public static byte[] deriveKey(String password, String salt) {
        return Arrays.copyOf(sha256("key" + salt + password), 16);
    }

    /**
     * Считает SHA-256 от строки.
     *
     * @param text исходная строка
     * @return 32 байта хэша
     * @throws AppException если алгоритм SHA-256 недоступен
     */
    private static byte[] sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new AppException("Ошибка SHA-256", e);
        }
    }
}