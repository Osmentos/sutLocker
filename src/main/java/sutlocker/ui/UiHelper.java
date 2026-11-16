package sutlocker.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import sutlocker.db.PasswordEntryDao;
import sutlocker.exception.AppException;
import sutlocker.service.AuthService;

import java.io.IOException;

//переключение экранов, сообщения, доступ к сервисам
public class UiHelper {

    private static Stage stage;
    private static AuthService authService;
    private static PasswordEntryDao entryDao;

    // Запоминает главное окно и сервисы, вызывается один раз при запуске
    public static void init(Stage primaryStage, AuthService auth, PasswordEntryDao dao) {
        stage = primaryStage;
        authService = auth;
        entryDao = dao;
    }

    public static AuthService getAuthService() {
        return authService;
    }

    public static PasswordEntryDao getEntryDao() {
        return entryDao;
    }

    // Открывает экран из папки fxml в главном окне
    public static void showScene(String fxmlName, String title) {
        try {
            Parent root = FXMLLoader.load(UiHelper.class.getResource("/fxml/" + fxmlName));
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            throw new AppException("Не удалось открыть окно " + fxmlName, e);
        }
    }


    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, message);
    }


    public static void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, message);
    }

    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}