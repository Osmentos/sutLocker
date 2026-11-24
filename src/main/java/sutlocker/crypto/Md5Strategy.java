package sutlocker.crypto;

import sutlocker.exception.AppException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/** MD5-хэш пароля без соли */
public class Md5Strategy implements CryptoStrategy {

    /**
     * Считает MD5-хэш пароля
     *
     * @param plain исходный пароль
     * @return хэш в виде шестнадцатеричной строки
     * @throws AppException если алгоритм MD5 недоступен
     */
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

    /**
     * Восстановить пароль из хэша нельзя
     *
     * @param encoded хэш
     * @return не возвращает значение
     * @throws AppException всегда, потому что MD5 необратим
     */
    @Override
    public String decode(String encoded) {
        throw new AppException("MD5 необратим, пароль восстановить нельзя");
    }

    /**
     * Способ необратим
     *
     * @return всегда false
     */
    @Override
    public boolean isReversible() {
        return false;
    }
}