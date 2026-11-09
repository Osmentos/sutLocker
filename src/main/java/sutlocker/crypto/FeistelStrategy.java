package sutlocker.crypto;

import sutlocker.exception.AppException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

// Шифр Фейстеля
public class FeistelStrategy implements CryptoStrategy {

    private static final int ROUNDS = 4;
    private final byte[] key;

    // Создаёт шифр по ключу (любой непустой массив байт)
    public FeistelStrategy(byte[] key) {
        if (key == null || key.length == 0) {
            throw new AppException("Ключ шифра не задан");
        }
        this.key = key;
    }

    @Override
    public String encode(String plain) {
        byte[] data = plain.getBytes(StandardCharsets.UTF_8);
        if (data.length % 2 != 0) {
            data = Arrays.copyOf(data, data.length + 1);
        }
        process(data, false);
        return Base64.getEncoder().encodeToString(data);
    }

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

    @Override
    public boolean isReversible() {
        return true;
    }

    // Раундовая функция F
    private int roundFunction(int half, int roundKey) {
        return ((half ^ roundKey) * 31 + 7) & 0xFF;
    }

    // Проходит по всем блокам по 2 байта и шифрует или расшифровывает их.
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

    // Декодирует Base64 и проверяет, что длина чётная
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