package sutlocker.model;

/** Способы хранения пароля, из которых пользователь выбирает при добавлении записи. */
public enum EncryptionType {
    /** Пароль хранится в открытом виде */
    PLAIN("Открытый вид", true),
    /** Пароль кодируется в Base64 */
    BASE64("Base64", true),
    /** Хранится MD5-хэш пароля */
    MD5("MD5", false),
    /** Пароль шифруется шифром Фейстеля */
    FEISTEL("Шифр Фейстеля", true),
    /** Хранится MD5-хэш пароля с солью */
    MD5_SALT("MD5 + соль", false);

    private final String name;
    private final boolean reversible;

    /**
     * Создаёт способ хранения
     *
     * @param name       название для показа пользователю
     * @param reversible можно ли восстановить исходный пароль
     */
    EncryptionType(String name, boolean reversible) {
        this.name = name;
        this.reversible = reversible;
    }

    /**
     * Показывает, можно ли восстановить исходный пароль (у хэшей нельзя).
     *
     * @return true, если пароль можно расшифровать
     */
    public boolean isReversible() {
        return reversible;
    }

    /**
     * Возвращает название способа для показа в интерфейсе
     *
     * @return название на русском языке
     */
    @Override
    public String toString() {
        return name;
    }
}