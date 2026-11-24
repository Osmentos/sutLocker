package sutlocker.crypto;

import sutlocker.exception.AppException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Кодирование пароля в Base64 */
public class Base64Strategy implements CryptoStrategy {

    /**
     * Кодирует пароль в Base64
     *
     * @param plain исходный пароль
     * @return строка Base64
     */
    @Override
    public String encode(String plain) {
        return Base64.getEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Декодирует строку Base64 в пароль
     *
     * @param encoded строка Base64
     * @return исходный пароль
     * @throws AppException если строка не является корректной Base64
     */
    @Override
    public String decode(String encoded) {
        try {
            return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new AppException("Некорректная строка Base64", e);
        }
    }

    /**
     * Способ обратим
     *
     * @return всегда true
     */
    @Override
    public boolean isReversible() {
        return true;
    }
}