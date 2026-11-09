package sutlocker.crypto;

/** MD5-хэш от строки*/
public class Md5SaltStrategy extends Md5Strategy {

    private final String salt;

    public Md5SaltStrategy(String salt) {
        this.salt = salt;
    }

    @Override
    public String encode(String plain) {
        return super.encode(salt + plain);
    }
}