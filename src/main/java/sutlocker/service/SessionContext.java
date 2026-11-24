package sutlocker.service;

import sutlocker.model.User;

import java.util.Arrays;

/** Данные текущего сеанса: вошедший пользователь и его ключ шифрования */
public class SessionContext {

    private final User user;
    private final byte[] key;

    /**
     * Создаёт сеанс.
     *
     * @param user вошедший пользователь
     * @param key  ключ шифрования пользователя
     */
    public SessionContext(User user, byte[] key) {
        this.user = user;
        this.key = key;
    }

    /** @return вошедший пользователь */
    public User getUser() { return user; }

    /** @return ключ шифрования пользователя */
    public byte[] getKey() { return key; }

    /** Стирает ключ из памяти при выходе из приложения. */
    public void clear() {
        Arrays.fill(key, (byte) 0);
    }
}