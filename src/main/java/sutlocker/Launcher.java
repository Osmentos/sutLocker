package sutlocker;

/** Точка входа: нужна, чтобы jar с JavaFX запускался из консоли */
public class Launcher {

    /**
     * Задаёт папку для лога и запускает приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        System.setProperty("sutlocker.dir", AppPaths.folder().toString());
        MainApp.main(args);
    }
}