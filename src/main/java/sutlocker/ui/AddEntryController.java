package sutlocker.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sutlocker.exception.AppException;
import sutlocker.model.EncryptionType;

/** Форма добавления записи */
public class AddEntryController {

    @FXML private TextField serviceField;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<EncryptionType> typeBox;

    @FXML
    private void initialize() {
        typeBox.getItems().setAll(EncryptionType.values());
        typeBox.getSelectionModel().selectFirst();
    }

    // Нажатие на «Сохранить»
    @FXML
    private void onSave() {
        try {
            UiHelper.newEntryService().add(serviceField.getText(), loginField.getText(),
                    passwordField.getText(), typeBox.getValue());
            serviceField.getScene().getWindow().hide();
        } catch (AppException e) {
            UiHelper.showError(e.getMessage());
        }
    }
}