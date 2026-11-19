package sutlocker;

import sutlocker.exception.AppException;

import java.nio.file.Files;
import java.nio.file.Path;

/** Папка приложения: рядом с jar-файлом (при запуске из IDE: папка проекта). */
public class AppPaths {

    public static Path folder() {
        try {
            Path location = Path.of(AppPaths.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI());
            return Files.isDirectory(location) ? Path.of("").toAbsolutePath() : location.getParent();
        } catch (Exception e) {
            throw new AppException("Не удалось определить папку приложения", e);
        }
    }
}