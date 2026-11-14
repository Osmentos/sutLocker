package sutlocker.service;

import sutlocker.model.User;

import java.util.Arrays;

// Данные текущего сеанса: вошедший пользователь и его ключ шифрования
public class SessionContext {

    private final User user;
    private final byte[] key;

    public SessionContext(User user, byte[] key) {
        this.user = user;
        this.key = key;
    }

    public User getUser() { return user; }
    public byte[] getKey() { return key; }

    // Стирает ключ из памяти при выходе
    public void clear() {
        Arrays.fill(key, (byte) 0);
    }
}