package sutlocker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import sutlocker.db.DatabaseManager;
import sutlocker.db.PasswordEntryDao;
import sutlocker.db.UserDao;
import sutlocker.exception.AppException;
import sutlocker.service.AuthService;
import sutlocker.ui.UiHelper;

// Запуск приложения: подключение к базе и открытие окна входа
public class MainApp extends Application {

    private DatabaseManager db;

    @Override
    public void start(Stage stage) {
        try {
            db = new DatabaseManager(DatabaseManager.defaultFilePath());
            UiHelper.init(stage, new AuthService(new UserDao(db)), new PasswordEntryDao(db));
            UiHelper.showScene("login.fxml", "SutLocker: вход");
        } catch (AppException e) {
            UiHelper.showError(e.getMessage());
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        if (db != null) {
            db.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}