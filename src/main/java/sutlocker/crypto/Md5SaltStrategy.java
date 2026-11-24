package sutlocker.crypto;

/** MD5-хэш от строки «соль + пароль» */
public class Md5SaltStrategy extends Md5Strategy {

    private final String salt;

    /**
     * Создаёт стратегию с заданной солью
     *
     * @param salt случайная соль записи
     */
    public Md5SaltStrategy(String salt) {
        this.salt = salt;
    }

    /**
     * Считает MD5-хэш от соли и пароля
     *
     * @param plain исходный пароль
     * @return хэш в виде шестнадцатеричной строки
     */
    @Override
    public String encode(String plain) {
        return super.encode(salt + plain);
    }
}