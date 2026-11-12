
package controllers;

import java.io.IOException;

import Validations.EmailValidation;
import Validations.PasswordValidation;
import daos.UserDao;
import databaseConnection.databaseConnector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class fxmlController {

    @FXML
    private AnchorPane background;

    @FXML
    private HBox mainLayout;

    @FXML
    private VBox leftLayout;

    @FXML
    private ImageView imageForLeft;

    @FXML
    private Label mainText;

    @FXML
    private Label subText;

    @FXML
    private ImageView decoration;

    @FXML
    private HBox threeItemsBox;

    @FXML
    private VBox rightLayout;

    @FXML
    private ImageView rightLayoutImage;

    @FXML
    private Label rightLayoutLabel1;

    @FXML
    private Label rightLayoutLabel2;

    @FXML
    private TextField rightLayoutTextField;

    @FXML
    private TextField name;

    @FXML
    private TextField lastN;

    @FXML
    private TextField userName;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label nameError;

    @FXML
    private Label lastNameError;

    @FXML
    private Label usernameError;

    @FXML
    private Label emailError;

    @FXML
    private Label passwordError;

    @FXML
    private Label confirmPasswordError;

    @FXML
    private Button createAccountButton;

    @FXML
    private VBox signInOpts;

    @FXML
    private Label optionForSignIn;

    @FXML
    private Hyperlink linkForSignIn;

    @FXML
    private void initialize() {

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                clearError(name, nameError);
            }
        });

        lastN.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                clearError(lastN, lastNameError);
            }
        });

        userName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                clearError(userName, usernameError);
            }
        });

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty() && EmailValidation.isValid(newValue.trim())) {
                clearError(emailField, emailError);
            }
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && PasswordValidation.isValidPassword(newValue)) {
                clearError(passwordField, passwordError);
            }

            if (!confirmPasswordField.getText().isEmpty()
                    && newValue.equals(confirmPasswordField.getText())) {
                clearError(confirmPasswordField, confirmPasswordError);
            }
        });

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && newValue.equals(passwordField.getText())) {
                clearError(confirmPasswordField, confirmPasswordError);
            }
        });
    }

    private void showError(TextField field, Label errorLabel, String message) {
        if (!field.getStyleClass().contains("error")) {
            field.getStyleClass().add("error");
        }

        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearError(TextField field, Label errorLabel) {
        field.getStyleClass().remove("error");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    @FXML
    private void clickRegistrationBtn(ActionEvent event) throws IOException {

        String firstName = name.getText().trim();
        String lastName = lastN.getText().trim();
        String username = userName.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        boolean isValid = true;

        if (firstName.isEmpty()) {
            showError(name, nameError, "სახელი სავალდებულოა");
            isValid = false;
        } else {
            clearError(name, nameError);
        }

        if (lastName.isEmpty()) {
            showError(lastN, lastNameError, "გვარი სავალდებულოა");
            isValid = false;
        } else {
            clearError(lastN, lastNameError);
        }

        if (username.isEmpty()) {
            showError(userName, usernameError, "ზედმეტსახელი სავალდებულოა");
            isValid = false;
        } else {
            clearError(userName, usernameError);
        }

        if (email.isEmpty()) {
            showError(emailField, emailError, "ელ-ფოსტა სავალდებულოა");
            isValid = false;
        } else if (!EmailValidation.isValid(email)) {
            showError(emailField, emailError, "ელ-ფოსტის ფორმატი არასწორია");
            isValid = false;
        } else {
            clearError(emailField, emailError);
        }

        if (password.isEmpty()) {
            showError(passwordField, passwordError, "პაროლი სავალდებულოა");
            isValid = false;
        } else if (!PasswordValidation.isValidPassword(password)) {
            showError(passwordField, passwordError, "პაროლი არ აკმაყოფილებს მოთხოვნებს");
            isValid = false;
        } else {
            clearError(passwordField, passwordError);
        }

        if (confirmPassword.isEmpty()) {
            showError(confirmPasswordField, confirmPasswordError, "გთხოვთ დაადასტუროთ პაროლი");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            showError(confirmPasswordField, confirmPasswordError, "პაროლები არ ემთხვევა ერთმანეთს");
            isValid = false;
        } else {
            clearError(confirmPasswordField, confirmPasswordError);
        }

        if (!isValid) {
            return;
        }
        
        UserDao.insertInto(username, firstName, lastName, confirmPassword, email);
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/logIn.fxml"));
        Scene currentScene = ((Node) event.getSource()).getScene();
        currentScene.setRoot(root);

    }

    @FXML
    private void toSignIn(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/logIn.fxml"));
        Scene currentScene = ((Node) event.getSource()).getScene();
        currentScene.setRoot(root);
    }
}
