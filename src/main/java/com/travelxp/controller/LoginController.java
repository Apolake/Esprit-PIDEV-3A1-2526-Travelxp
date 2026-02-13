package com.travelxp.controller;

import com.travelxp.model.User;
import com.travelxp.service.AuthService;
import com.travelxp.util.FXMLView;
import com.travelxp.util.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Controller
public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private Label messageLabel;

    @Autowired
    private AuthService authService;

    @Autowired
    private StageManager stageManager;

    private static User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization hook for UI
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter username and password", true);
            return;
        }

        Optional<User> userOpt = authService.login(username, password);
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            showMessage("Login successful!", false);
            stageManager.switchScene(FXMLView.DASHBOARD);
        } else {
            showMessage("Invalid username or password", true);
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Register New Account");
        dialog.setHeaderText("Create your TravelXP account");

        TextField regUsername = new TextField();
        regUsername.setPromptText("Username");
        TextField regEmail = new TextField();
        regEmail.setPromptText("Email");
        PasswordField regPassword = new PasswordField();
        regPassword.setPromptText("Password");
        TextField regFullName = new TextField();
        regFullName.setPromptText("Full Name");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Username:"), 0, 0);
        grid.add(regUsername, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(regEmail, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(regPassword, 1, 2);
        grid.add(new Label("Full Name:"), 0, 3);
        grid.add(regFullName, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                User newUser = authService.register(
                    regUsername.getText(),
                    regEmail.getText(),
                    regPassword.getText(),
                    regFullName.getText()
                );
                showMessage("Registration successful! Please login.", false);
            } catch (Exception e) {
                showMessage("Registration failed: " + e.getMessage(), true);
            }
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
