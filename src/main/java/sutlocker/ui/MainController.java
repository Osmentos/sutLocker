package sutlocker.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import sutlocker.exception.AppException;
import sutlocker.model.EntryView;
import sutlocker.service.PasswordEntryService;

import java.util.Optional;

// Главный экран: таблица записей, поиск, удаление, копирование, проверка
public class MainController {

    @FXML private TableView<EntryView> table;
    @FXML private TableColumn<EntryView, String> serviceColumn;
    @FXML private TableColumn<EntryView, String> loginColumn;
    @FXML private TableColumn<EntryView, String> passwordColumn;
    @FXML private TableColumn<EntryView, String> typeColumn;
    @FXML private TextField searchField;

    private PasswordEntryService service;

    // Вызывается автоматически после загрузки fxml
    @FXML
    private void initialize() {
        service = UiHelper.newEntryService();
        serviceColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getService()));
        loginColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLogin()));
        passwordColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPassword()));
        typeColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType().toString()));
        searchField.textProperty().addListener((observable, oldText, newText) -> refresh());
        refresh();
    }

    // Нажатие на «Добавить»
    @FXML
    private void onAdd() {
        UiHelper.showDialog("add-entry.fxml", "Новая запись");
        refresh();
    }

    // Нажатие на «Удалить»
    @FXML
    private void onDelete() {
        EntryView entry = selectedOrNull();
        if (entry == null || !UiHelper.confirm("Удалить запись «" + entry.getService() + "»?")) {
            return;
        }
        try {
            service.delete(entry.getId());
            refresh();
        } catch (AppException e) {
            UiHelper.showError(e.getMessage());
        }
    }

    // Нажатие на «Копировать пароль»: для хэшей копируется сам хэш
    @FXML
    private void onCopy() {
        EntryView entry = selectedOrNull();
        if (entry != null) {
            UiHelper.copyToClipboard(entry.getPassword());
        }
    }

    // Нажатие на «Проверить пароль»
    @FXML
    private void onCheck() {
        EntryView entry = selectedOrNull();
        if (entry == null) {
            return;
        }
        try {
            Optional<String> text = UiHelper.askText("Введите пароль для проверки");
            if (text.isPresent()) {
                boolean ok = service.check(entry.getId(), text.get());
                UiHelper.showInfo(ok ? "Пароль верный" : "Пароль неверный");
            }
        } catch (AppException e) {
            UiHelper.showError(e.getMessage());
        }
    }

    // Нажатие на «Выйти»
    @FXML
    private void onLogout() {
        UiHelper.logout();
    }

    // Заполняет таблицу записями, подходящими под строку поиска
    private void refresh() {
        try {
            table.getItems().setAll(service.search(searchField.getText()));
        } catch (AppException e) {
            UiHelper.showError(e.getMessage());
        }
    }

    private EntryView selectedOrNull() {
        EntryView entry = table.getSelectionModel().getSelectedItem();
        if (entry == null) {
            UiHelper.showError("Сначала выберите запись в таблице");
        }
        return entry;
    }
}