
package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardController {

    @FXML
    private HBox hB1, hB2, hB3, hB4, hB5, hB6, hB7;

    @FXML
    private VBox birthChartCard, dailyPositionsCard, blogCard, myBlogsCard;

    @FXML
    private VBox LeftVBox, RightVBox;

    @FXML
    private HBox logoHbox;

    @FXML
    private void initialize() {

        setActiveMenu(hB1);

        hB1.setOnMouseClicked(event -> setActiveMenu(hB1));
        hB2.setOnMouseClicked(event -> setActiveMenu(hB2));
        hB3.setOnMouseClicked(event -> setActiveMenu(hB3));
        hB4.setOnMouseClicked(event -> setActiveMenu(hB4));
        hB5.setOnMouseClicked(event -> setActiveMenu(hB5));
        hB6.setOnMouseClicked(event -> setActiveMenu(hB6));
        hB7.setOnMouseClicked(event -> setActiveMenu(hB7));

        birthChartCard.setOnMouseClicked(event -> setActiveMenu(hB2));
        dailyPositionsCard.setOnMouseClicked(event -> setActiveMenu(hB3));
        blogCard.setOnMouseClicked(event -> setActiveMenu(hB4));
        myBlogsCard.setOnMouseClicked(event -> setActiveMenu(hB5));
    }

    private void setActiveMenu(HBox selectedMenu) {

        HBox[] menus = {
            hB1, hB2, hB3, hB4, hB5, hB6, hB7
        };

        for (HBox menu : menus) {
            menu.getStyleClass().remove("active");
        }

        if (!selectedMenu.getStyleClass().contains("active")) {
            selectedMenu.getStyleClass().add("active");
        }
    }
}
