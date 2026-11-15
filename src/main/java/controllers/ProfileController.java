package controllers;

import java.io.IOException;
import java.util.List;

import daos.BlogDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import models.Blog;

public class ProfileController {
    @FXML private HBox hB1, hB2, hB3, hB4, hB5, hB6, hB7;
    @FXML private Label profileName, blogCount, profileStatus;
    @FXML private ListView<Blog> profileBlogs;

    @FXML
    private void initialize() {
        profileName.setText(UserSession.getCurrentUserName().isBlank() ? "მომხმარებელი" : UserSession.getCurrentUserName());
        hB1.setOnMouseClicked(e -> open("/fxml/Dashboard.fxml"));
        hB2.setOnMouseClicked(e -> open("/fxml/BirthChart.fxml"));
        hB3.setOnMouseClicked(e -> open("/fxml/DailyPositions.fxml"));
        hB4.setOnMouseClicked(e -> open("/fxml/MyBlogs.fxml"));
        hB5.setOnMouseClicked(e -> open("/fxml/MyBlogs.fxml"));
        hB6.setOnMouseClicked(e -> {});
        hB7.setOnMouseClicked(e -> { UserSession.clear(); open("/fxml/logIn.fxml"); });
        loadBlogs();
    }

    private void loadBlogs() {
        try {
            List<Blog> blogs = BlogDao.findByUser(UserSession.getCurrentUserId());
            profileBlogs.getItems().setAll(blogs);
            blogCount.setText(String.valueOf(blogs.size()));
            profileStatus.setText(blogs.isEmpty() ? "ჯერ ბლოგი არ შეგიქმნია." : "შენი შექმნილი ბლოგები");
        } catch (Exception ex) {
            profileStatus.setText("ბლოგების ჩატვირთვა ვერ მოხერხდა.");
        }
    }

    private void open(String path) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(path));
            hB6.getScene().setRoot(page);
        } catch (IOException ex) {
            profileStatus.setText("გვერდის გახსნა ვერ მოხერხდა.");
        }
    }
}
