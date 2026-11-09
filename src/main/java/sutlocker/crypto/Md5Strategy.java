package sutlocker.crypto;

import sutlocker.exception.AppException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

// MD5
public class Md5Strategy implements CryptoStrategy {

    @Override
    public String encode(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new AppException("Ошибка MD5", e);
        }
    }

    @Override
    public String decode(String encoded) {
        throw new AppException("MD5 необратим, пароль восстановить нельзя");
    }

    @Override
    public boolean isReversible() {
        return false;
    }
}