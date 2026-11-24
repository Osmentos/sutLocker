package sutlocker.ui;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sutlocker.exception.AppException;

/** Окно регистрации. */
public class RegisterController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatField;

    /** Нажатие на «Зарегистрироваться»: создаёт пользователя и возвращает окно входа. */
    @FXML
    private void onRegister() {
        if (!passwordField.getText().equals(repeatField.getText())) {
            UiHelper.showError("Пароли не совпадают");
            return;
        }
        try {
            UiHelper.getAuthService().register(loginField.getText(), passwordField.getText());
            UiHelper.showInfo("Регистрация выполнена, теперь можно войти");
            onBack();
        } catch (AppException e) {
            UiHelper.showError(e.getMessage());
        }
    }

    /** Нажатие на «Назад ко входу»: возвращает окно входа. */
    @FXML
    private void onBack() {
        UiHelper.showScene("login.fxml", "SutLocker: вход");
    }
}