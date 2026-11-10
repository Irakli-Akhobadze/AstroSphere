package controllers;

import java.io.IOException;

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
	private void toRegistration(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("/fxml/Registration.fxml"));
		Scene currentScene = ((Node) event.getSource()).getScene();
		currentScene.setRoot(root);
	}
}
