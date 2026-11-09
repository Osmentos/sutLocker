package sutlocker.crypto;

import sutlocker.exception.AppException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Кодирование в Base64 */
public class Base64Strategy implements CryptoStrategy {

    @Override
    public String encode(String plain) {
        return Base64.getEncoder().encodeToString(plain.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decode(String encoded) {
        try {
            return new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new AppException("Некорректная строка Base64", e);
        }
    }

    @Override
    public boolean isReversible() {
        return true;
    }
}