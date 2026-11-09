package sutlocker.exception;

public class AppException extends RuntimeException{
    /**
     * @param message описание ошибки
     */
    public AppException(String message) {
        super(message);
    }

    /**
     * @param message описание ошибки
     * @param cause   исходное исключение
     */
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
