package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // 1. ჩავტვირთოთ FXML ფაილი რესურსების საქაღალდიდან
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));

            // (სურვილისამებრ) შრიფტის ჩატვირთვა, თუ CSS-ით არ გაქვთ გაწერილი
            Font.loadFont(getClass().getResourceAsStream("/fonts/SS GEO HAKUNA - Regular.otf"), 20);

            // 2. შევქმნათ სცენა და მივამაგროთ FXML-ის ძირითადი ელემენტი (root)
            Scene scene = new Scene(root, 600, 400);

            Image icon = new Image(getClass().getResourceAsStream("../images/logo.png"));
            
            primaryStage.setTitle("ასტროსფერო");
            primaryStage.getIcons().add(icon);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            primaryStage.setMinWidth(1100.0);  // მინიმალური სიგანე, რომელზე ქვემოთაც ვეღარ დააპატარავებს
            primaryStage.setMinHeight(750.0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}