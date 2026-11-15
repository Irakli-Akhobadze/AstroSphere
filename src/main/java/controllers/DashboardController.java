package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DashboardController {
    @FXML private HBox hB1, hB2, hB3, hB4, hB5, hB6, hB7;
    @FXML private VBox birthChartCard, dailyPositionsCard, blogCard, myBlogsCard;
    @FXML private Label welcomeText;

    @FXML
    private void initialize() {
        if (welcomeText != null && !UserSession.getCurrentUserName().isBlank()) {
            welcomeText.setText("მოგესალმები, " + UserSession.getCurrentUserName() + "!");
        }
        setActiveMenu(hB1);
        hB1.setOnMouseClicked(event -> setActiveMenu(hB1));
        hB2.setOnMouseClicked(event -> open("/fxml/BirthChart.fxml"));
        hB3.setOnMouseClicked(event -> open("/fxml/DailyPositions.fxml"));
        hB4.setOnMouseClicked(event -> open("/fxml/MyBlogs.fxml"));
        hB5.setOnMouseClicked(event -> open("/fxml/MyBlogs.fxml"));
        hB6.setOnMouseClicked(event -> setActiveMenu(hB6));
        hB7.setOnMouseClicked(event -> logout());
        birthChartCard.setOnMouseClicked(event -> open("/fxml/BirthChart.fxml"));
        dailyPositionsCard.setOnMouseClicked(event -> open("/fxml/DailyPositions.fxml"));
        blogCard.setOnMouseClicked(event -> open("/fxml/MyBlogs.fxml"));
        myBlogsCard.setOnMouseClicked(event -> open("/fxml/MyBlogs.fxml"));
    }

    private void setActiveMenu(HBox selected) {
        HBox[] menus = {hB1,hB2,hB3,hB4,hB5,hB6,hB7};
        for (HBox menu : menus) menu.getStyleClass().remove("active");
        if (!selected.getStyleClass().contains("active")) selected.getStyleClass().add("active");
    }

    private void open(String path) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(path));
            hB1.getScene().setRoot(page);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void logout() {
        try {
            UserSession.clear();
            Parent page = FXMLLoader.load(getClass().getResource("/fxml/logIn.fxml"));
            hB7.getScene().setRoot(page);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
