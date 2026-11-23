package sutlocker.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sutlocker.db.PasswordEntryDao;
import sutlocker.exception.AppException;
import sutlocker.service.AuthService;
import sutlocker.service.PasswordEntryService;
import sutlocker.service.SessionContext;

import java.io.IOException;
import java.util.Optional;

/** переключение экранов, сообщения, сеанс, буфер обмена */
public class UiHelper {

    private static Stage stage;
    private static AuthService authService;
    private static PasswordEntryDao entryDao;
    private static SessionContext session;

    /** Запоминает главное окно и сервисы, вызывается один раз при запуске */
    public static void init(Stage primaryStage, AuthService auth, PasswordEntryDao dao) {
        stage = primaryStage;
        authService = auth;
        entryDao = dao;
    }

    public static AuthService getAuthService() {
        return authService;
    }

    public static SessionContext getSession() {
        return session;
    }

    public static void setSession(SessionContext newSession) {
        session = newSession;
    }

    /** Создаёт сервис записей для текущего пользователя */
    public static PasswordEntryService newEntryService() {
        return new PasswordEntryService(entryDao, session);
    }

    /** Выход: стирает ключ из памяти и возвращает окно входа */
    public static void logout() {
        if (session != null) {
            session.clear();
        }
        session = null;
        showScene("login.fxml", "SutLocker: вход");
    }

    /** Открывает экран из папки fxml в главном окне */
    public static void showScene(String fxmlName, String title) {
        stage.setScene(new Scene(loadFxml(fxmlName)));
        stage.setTitle(title);
        stage.sizeToScene();
        stage.show();
    }

    /** Открывает маленькое окно поверх главного и ждёт, пока его закроют */
    public static void showDialog(String fxmlName, String title) {
        Stage dialog = new Stage();
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        dialog.setScene(new Scene(loadFxml(fxmlName)));
        dialog.showAndWait();
    }

    /** Показывает сообщение об ошибке */
    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, message);
    }

    /** Показывает обычное сообщение */
    public static void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, message);
    }

    /** Спрашивает подтверждение, возвращает true, если нажали ок */
    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        alert.setHeaderText(null);
        return alert.showAndWait().filter(button -> button == ButtonType.OK).isPresent();
    }

    /** Просит ввести строку */
    public static Optional<String> askText(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait();
    }

    /** Копирует текст в буфер обмена */
    public static void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    private static Parent loadFxml(String fxmlName) {
        try {
            return FXMLLoader.load(UiHelper.class.getResource("/fxml/" + fxmlName));
        } catch (IOException e) {
            throw new AppException("Не удалось открыть окно " + fxmlName, e);
        }
    }

    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}