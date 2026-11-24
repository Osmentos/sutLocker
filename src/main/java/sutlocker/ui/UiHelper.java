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

/** Общие действия окон: переключение экранов, сообщения, сеанс, буфер обмена. */
public class UiHelper {

    private static Stage stage;
    private static AuthService authService;
    private static PasswordEntryDao entryDao;
    private static SessionContext session;

    /**
     * Запоминает главное окно и сервисы, вызывается один раз при запуске.
     *
     * @param primaryStage главное окно
     * @param auth         сервис входа и регистрации
     * @param dao          доступ к записям
     */
    public static void init(Stage primaryStage, AuthService auth, PasswordEntryDao dao) {
        stage = primaryStage;
        authService = auth;
        entryDao = dao;
    }

    /** @return сервис входа и регистрации */
    public static AuthService getAuthService() {
        return authService;
    }

    /** @return данные текущего сеанса или null, если пользователь не вошёл */
    public static SessionContext getSession() {
        return session;
    }

    /**
     * Запоминает сеанс после успешного входа.
     *
     * @param newSession данные сеанса
     */
    public static void setSession(SessionContext newSession) {
        session = newSession;
    }

    /**
     * Создаёт сервис записей для текущего пользователя.
     *
     * @return сервис записей
     */
    public static PasswordEntryService newEntryService() {
        return new PasswordEntryService(entryDao, session);
    }

    /** Выход: стирает ключ из памяти и возвращает окно входа. */
    public static void logout() {
        if (session != null) {
            session.clear();
        }
        session = null;
        showScene("login.fxml", "SutLocker: вход");
    }

    /**
     * Открывает экран из папки fxml в главном окне.
     *
     * @param fxmlName имя файла fxml
     * @param title    заголовок окна
     */
    public static void showScene(String fxmlName, String title) {
        stage.setScene(new Scene(loadFxml(fxmlName)));
        stage.setTitle(title);
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Открывает маленькое окно поверх главного и ждёт, пока его закроют.
     *
     * @param fxmlName имя файла fxml
     * @param title    заголовок окна
     */
    public static void showDialog(String fxmlName, String title) {
        Stage dialog = new Stage();
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);
        dialog.setScene(new Scene(loadFxml(fxmlName)));
        dialog.showAndWait();
    }

    /**
     * Показывает сообщение об ошибке.
     *
     * @param message текст сообщения
     */
    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, message);
    }

    /**
     * Показывает обычное сообщение.
     *
     * @param message текст сообщения
     */
    public static void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, message);
    }

    /**
     * Спрашивает подтверждение действия.
     *
     * @param message текст вопроса
     * @return true, если пользователь нажал OK
     */
    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        alert.setHeaderText(null);
        return alert.showAndWait().filter(button -> button == ButtonType.OK).isPresent();
    }

    /**
     * Просит пользователя ввести строку.
     *
     * @param message подсказка в окне ввода
     * @return введённый текст или пустое значение, если окно закрыли
     */
    public static Optional<String> askText(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait();
    }

    /**
     * Копирует текст в буфер обмена.
     *
     * @param text копируемый текст
     */
    public static void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    /**
     * Загружает разметку окна из папки fxml.
     *
     * @param fxmlName имя файла fxml
     * @return корневой элемент окна
     * @throws AppException если файл не удалось загрузить
     */
    private static Parent loadFxml(String fxmlName) {
        try {
            return FXMLLoader.load(UiHelper.class.getResource("/fxml/" + fxmlName));
        } catch (IOException e) {
            throw new AppException("Не удалось открыть окно " + fxmlName, e);
        }
    }

    /**
     * Показывает окно сообщения заданного типа.
     *
     * @param type    тип сообщения
     * @param message текст сообщения
     */
    private static void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}