package sutlocker.crypto;

/** Способ преобразования пароля перед хранением. */
public interface CryptoStrategy {

    /** Превращает пароль в значение для базы. */
    String encode(String plain);

    /** Восстанавливает пароль из значения базы, если способ обратим. */
    String decode(String encoded);

    /** Можно ли восстановить исходный пароль. */
    boolean isReversible();

    /** Проверяет, совпадает ли введённый пароль с сохранённым значением. Для необратимых */
    default boolean matches(String plain, String stored) {
        return encode(plain).equals(stored);
    }
}