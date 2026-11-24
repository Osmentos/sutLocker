package sutlocker.crypto;

/** Способ преобразования пароля перед хранением в базе */
public interface CryptoStrategy {

    /**
     * Превращает пароль в значение для хранения в базе
     *
     * @param plain исходный пароль
     * @return закодированное значение
     */
    String encode(String plain);

    /**
     * Восстанавливает исходный пароль из значения базы
     *
     * @param encoded значение из базы
     * @return исходный пароль
     * @throws sutlocker.exception.AppException если способ необратим или данные повреждены
     */
    String decode(String encoded);

    /**
     * Показывает, можно ли восстановить исходный пароль.
     *
     * @return true, если способ обратим
     */
    boolean isReversible();

    /**
     * Проверяет, совпадает ли введённый пароль с сохранённым значением
     * Работает для любого способа, в том числе для необратимых хэшей
     *
     * @param plain  введённый пароль
     * @param stored значение из базы
     * @return true, если пароль совпал
     */
    default boolean matches(String plain, String stored) {
        return encode(plain).equals(stored);
    }
}