package sutlocker;

/** Точка входа: нужна, чтобы jar с JavaFX запускался из консоли */
public class Launcher {

    public static void main(String[] args) {
        // папку нужно задать до создания первого логгера
        System.setProperty("sutlocker.dir", AppPaths.folder().toString());
        MainApp.main(args);
    }
}