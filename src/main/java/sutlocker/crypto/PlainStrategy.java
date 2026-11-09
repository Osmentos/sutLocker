package sutlocker.crypto;

/** Пароль хранится как есть */
public class PlainStrategy implements CryptoStrategy {

    @Override
    public String encode(String plain) {
        return plain;
    }

    @Override
    public String decode(String encoded) {
        return encoded;
    }

    @Override
    public boolean isReversible() {
        return true;
    }
}