package sutlocker.crypto;

import sutlocker.exception.AppException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/**
 * Шифр Фейстеля: блок 2 байта, 4 раунда.
 * <p>
 * В каждом раунде блок делится на левую L и правую R половины:
 * {@code L(i+1) = R(i)}, {@code R(i+1) = L(i) xor F(R(i), K(i))}.
 * Расшифровка выполняется тем же циклом с ключами раундов в обратном порядке.
 */
public class FeistelStrategy implements CryptoStrategy {

    private static final int ROUNDS = 4;
    private final byte[] key;

    /**
     * Создаёт шифр по ключу.
     *
     * @param key ключ шифрования (любой непустой массив байт)
     * @throws AppException если ключ не задан
     */
    public FeistelStrategy(byte[] key) {
        if (key == null || key.length == 0) {
            throw new AppException("Ключ шифра не задан");
        }
        this.key = key;
    }

    /**
     * Шифрует пароль.
     *
     * @param plain исходный пароль
     * @return зашифрованные данные в виде строки Base64
     */
    @Override
    public String encode(String plain) {
        byte[] data = plain.getBytes(StandardCharsets.UTF_8);
        if (data.length % 2 != 0) {
            data = Arrays.copyOf(data, data.length + 1);
        }
        process(data, false);
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Расшифровывает пароль.
     *
     * @param encoded зашифрованные данные в виде строки Base64
     * @return исходный пароль
     * @throws AppException если данные повреждены
     */
    @Override
    public String decode(String encoded) {
        byte[] data = readBase64(encoded);
        process(data, true);
        int length = data.length;
        while (length > 0 && data[length - 1] == 0) {
            length--;
        }
        return new String(data, 0, length, StandardCharsets.UTF_8);
    }

    /**
     * Способ обратим.
     *
     * @return всегда true
     */
    @Override
    public boolean isReversible() {
        return true;
    }

    /**
     * Раундовая функция F.
     *
     * @param half     правая половина блока
     * @param roundKey ключ раунда
     * @return результат функции (одно значение байта)
     */
    private int roundFunction(int half, int roundKey) {
        return ((half ^ roundKey) * 31 + 7) & 0xFF;
    }

    /**
     * Проходит по всем блокам по 2 байта и шифрует или расшифровывает их на месте.
     *
     * @param data    данные, длина которых кратна двум
     * @param decrypt true для расшифровки, false для шифрования
     */
    private void process(byte[] data, boolean decrypt) {
        for (int i = 0; i < data.length; i += 2) {
            int left = data[i] & 0xFF;
            int right = data[i + 1] & 0xFF;
            for (int r = 0; r < ROUNDS; r++) {
                int round = decrypt ? ROUNDS - 1 - r : r;
                int newRight = left ^ roundFunction(right, key[round % key.length] & 0xFF);
                left = right;
                right = newRight;
            }
            data[i] = (byte) right;
            data[i + 1] = (byte) left;
        }
    }

    /**
     * Декодирует Base64 и проверяет, что длина данных чётная.
     *
     * @param encoded строка Base64
     * @return байты зашифрованных данных
     * @throws AppException если строка некорректна
     */
    private byte[] readBase64(String encoded) {
        try {
            byte[] data = Base64.getDecoder().decode(encoded);
            if (data.length % 2 != 0) {
                throw new AppException("Некорректная длина зашифрованных данных");
            }
            return data;
        } catch (IllegalArgumentException e) {
            throw new AppException("Некорректные зашифрованные данные", e);
        }
    }
}