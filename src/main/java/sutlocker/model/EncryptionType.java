package sutlocker.model;

// Способы хранения пароля
public enum EncryptionType {
    PLAIN("Открытый вид", true),
    BASE64("Base64", true),
    MD5("MD5", false),
    FEISTEL("Шифр Фейстеля", true),
    MD5_SALT("MD5 + соль", false);

    private final String name;
    private final boolean reversible;

    EncryptionType(String name, boolean reversible) {
        this.name = name;
        this.reversible = reversible;
    }

    public boolean isReversible() {
        return reversible;
    }

    @Override
    public String toString() {
        return name;
    }
}