package sutlocker.crypto;

import sutlocker.model.EncryptionType;

/** Выбирает стратегию по способу хранения. */
public class CryptoStrategyFactory {

    /**
     *
     * @param type способ хранения
     * @param salt соль (нужна только для MD5_SALT)
     * @param key  ключ (нужен только для FEISTEL)
     */
    public CryptoStrategy create(EncryptionType type, String salt, byte[] key) {
        return switch (type) {
            case PLAIN -> new PlainStrategy();
            case BASE64 -> new Base64Strategy();
            case MD5 -> new Md5Strategy();
            case MD5_SALT -> new Md5SaltStrategy(salt);
            case FEISTEL -> new FeistelStrategy(key);
        };
    }
}