package sutlocker.ui;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sutlocker.exception.AppException;
import sutlocker.service.SessionContext;

/** Окно входа. */
public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;

    /** Нажатие на «Войти». */
    @FXML
    private void onLogin() {
        try {
            SessionContext session = UiHelper.getAuthService()
                    .login(loginField.getText(), passwordField.getText());
            UiHelper.showInfo("Вход выполнен: " + session.getUser().getLogin());
        } catch (AppException e) {
            UiHelper.showError(e.getMessage());
        }
    }

    /** Нажатие на «Регистрация». */
    @FXML
    private void onRegister() {
        UiHelper.showScene("register.fxml", "SutLocker: регистрация");
    }
}