package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

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
	private PasswordField passwordField;
	
	@FXML
	private PasswordField confirmPassword;
	
	@FXML
	private Label passwordLabel;
	
	@FXML
	private Button createAccountButton;
	
	@FXML
	private VBox signInOpts;
	
	@FXML
	private Label optionForSignIn;
	
	@FXML
	private Hyperlink linkForSignIn;
	
	
	
}
