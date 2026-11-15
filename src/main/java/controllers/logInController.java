
package controllers;

import java.io.IOException;

import Validations.EmailValidation;
import Validations.PasswordValidation;
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
import javafx.scene.control.TextInputControl;

public class logInController {
	@FXML
	private TextField emailForLogIn;
	
	@FXML
	private PasswordField passwordFieldForLogIn;
	
	@FXML
	private Button createAccountButtonForLogIn;
	
	@FXML
	private Hyperlink linkForRegistration;
	
	@FXML
    private Label emailError;
	
	@FXML
    private Label passwordError;
	
	
	@FXML
	private void initialize() {
		  emailForLogIn.textProperty().addListener((observable, oldValue, newValue) -> {
	            if (!newValue.trim().isEmpty() && EmailValidation.isValid(newValue.trim())) {
	                clearError(emailForLogIn, emailError);
	            }
	        });

	        passwordFieldForLogIn.textProperty().addListener((observable, oldValue, newValue) -> {
	            if (!newValue.isEmpty() && PasswordValidation.isValidPassword(newValue)) {
	                clearError(passwordFieldForLogIn, passwordError);
	            }

	        });
	}
	
	private void showError(TextInputControl field, Label errorLabel, String message) {
	    if (!field.getStyleClass().contains("error")) {
	        field.getStyleClass().add("error");
	    }

	    errorLabel.setText(message);
	    errorLabel.setVisible(true);
	    errorLabel.setManaged(true);
	}

	private void clearError(TextInputControl field, Label errorLabel) {
	    field.getStyleClass().remove("error");
	    errorLabel.setVisible(false);
	    errorLabel.setManaged(false);
	}
	
    @FXML
    private void ClickOnLogIn(ActionEvent event) {
    	
    	boolean isValid = true;   
    	
    	 String email = emailForLogIn.getText().trim();
         String password = passwordFieldForLogIn.getText();
    	if (email.isEmpty()) {
            showError(emailForLogIn, emailError, "ელ-ფოსტა სავალდებულოა");
            isValid = false;
        } else if (!EmailValidation.isValid(email)) {
            showError(emailForLogIn, emailError, "ელ-ფოსტის ფორმატი არასწორია");
            isValid = false;
        } else {
            clearError(emailForLogIn, emailError);
        }

        if (password.isEmpty()) {
            showError(passwordFieldForLogIn, passwordError, "პაროლი სავალდებულოა");
            isValid = false;
        } else if (!PasswordValidation.isValidPassword(password)) {
            showError(passwordFieldForLogIn, passwordError, "პაროლი არ აკმაყოფილებს მოთხოვნებს");
            isValid = false;
        } else {
            clearError(passwordFieldForLogIn, passwordError);
        }
    }
	
	@FXML
	private void toRegistration(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Registration.fxml"));
		Scene currentScene = ((Node) event.getSource()).getScene();
		currentScene.setRoot(root);
	}
	
}
