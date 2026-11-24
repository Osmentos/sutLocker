package sutlocker.crypto;

/** Пароль хранится как есть, без преобразований. */
public class PlainStrategy implements CryptoStrategy {

    /**
     * Возвращает пароль без изменений
     *
     * @param plain исходный пароль
     * @return тот же пароль
     */
    @Override
    public String encode(String plain) {
        return plain;
    }

    /**
     * Возвращает значение без изменений
     *
     * @param encoded значение из базы
     * @return тот же пароль
     */
    @Override
    public String decode(String encoded) {
        return encoded;
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