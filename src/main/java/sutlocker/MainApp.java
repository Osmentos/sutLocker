package sutlocker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sutlocker.db.DatabaseManager;
import sutlocker.db.PasswordEntryDao;
import sutlocker.db.UserDao;
import sutlocker.exception.AppException;
import sutlocker.service.AuthService;
import sutlocker.ui.UiHelper;

/** Запуск приложения: подключение к базе и открытие окна входа */
public class MainApp extends Application {

    private static final Logger LOG = LogManager.getLogger(MainApp.class);

    private DatabaseManager db;

    /**
     * Подключается к базе данных и открывает окно входа
     *
     * @param stage главное окно приложения
     */
    @Override
    public void start(Stage stage) {
        try {
            db = new DatabaseManager(DatabaseManager.defaultFilePath());
            UiHelper.init(stage, new AuthService(new UserDao(db)), new PasswordEntryDao(db));
            UiHelper.showScene("login.fxml", "SutLocker: вход");
            LOG.info("Приложение запущено");
        } catch (AppException e) {
            LOG.error("Не удалось запустить приложение", e);
            UiHelper.showError(e.getMessage());
            Platform.exit();
        }
    }

    /** Закрывает соединение с базой данных при выходе из приложения */
    @Override
    public void stop() {
        if (db != null) {
            db.close();
        }
        LOG.info("Приложение закрыто");
    }

    /** запускает JavaFX-приложение */
    public static void main(String[] args) {
        launch(args);
    }
}