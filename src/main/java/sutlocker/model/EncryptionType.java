package sutlocker.model;

//Способы хранения пароля
public enum EncryptionType {
    PLAIN("Открытый вид"),
    BASE64("Base64"),
    MD5("MD5"),
    FEISTEL("Шифр Фейстеля"),
    MD5_SALT("MD5 + соль");
    private final String name;

    EncryptionType(String name) {
        this.name = name;
    }
    /**
     * @return название на русском
     */
    @Override
    public String toString() {
        return name;
    }
}