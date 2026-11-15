package controllers;

import java.util.List;
import daos.BlogDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import models.Blog;

public class MyBlogsController {
    @FXML private HBox hB1,hB2,hB3,hB4,hB5,hB6,hB7;
    @FXML private ListView<Blog> blogsList;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private Label statusLabel;
    @FXML private Button saveButton,deleteButton,newButton;
    private Blog selected;

    @FXML private void initialize() {
        hB1.setOnMouseClicked(e -> open("/fxml/Dashboard.fxml"));
        hB2.setOnMouseClicked(e -> open("/fxml/BirthChart.fxml"));
        hB3.setOnMouseClicked(e -> open("/fxml/DailyPositions.fxml"));
        hB4.setOnMouseClicked(e -> {}); hB5.setOnMouseClicked(e -> {});
        hB6.setOnMouseClicked(e -> open("/fxml/Profile.fxml"));
        hB7.setOnMouseClicked(e -> { UserSession.clear(); open("/fxml/logIn.fxml"); });
        blogsList.getSelectionModel().selectedItemProperty().addListener((o,oldValue,value) -> {
            selected=value;
            if(value!=null){ titleField.setText(value.getTitle()); contentArea.setText(value.getContent()); deleteButton.setDisable(false); }
        });
        deleteButton.setDisable(true);
        loadBlogs();
    }

    @FXML private void newBlog() { selected=null; blogsList.getSelectionModel().clearSelection(); titleField.clear(); contentArea.clear(); deleteButton.setDisable(true); statusLabel.setText("ახალი ბლოგის შექმნა"); }

    @FXML private void saveBlog() {
        int userId=UserSession.getCurrentUserId();
        String title=titleField.getText()==null?"":titleField.getText().trim();
        String content=contentArea.getText()==null?"":contentArea.getText().trim();
        if(userId<=0){ statusLabel.setText("საჭიროა სისტემაში შესვლა."); return; }
        if(title.isEmpty()||content.isEmpty()){ statusLabel.setText("შეავსე სათაური და ტექსტი."); return; }
        try {
            if(selected==null) BlogDao.create(userId,title,content);
            else BlogDao.update(selected.getBlogId(),userId,title,content);
            statusLabel.setText("ბლოგი შენახულია შენს ანგარიშში."); loadBlogs(); newBlog();
        } catch(Exception ex){ statusLabel.setText("შენახვა ვერ მოხერხდა: "+ex.getMessage()); }
    }

    @FXML private void deleteBlog() {
        if(selected==null)return;
        try { BlogDao.delete(selected.getBlogId(),UserSession.getCurrentUserId()); statusLabel.setText("ბლოგი წაიშალა."); loadBlogs(); newBlog(); }
        catch(Exception ex){ statusLabel.setText("წაშლა ვერ მოხერხდა: "+ex.getMessage()); }
    }

    private void loadBlogs() {
        try { List<Blog> blogs=BlogDao.findByUser(UserSession.getCurrentUserId()); blogsList.getItems().setAll(blogs); }
        catch(Exception ex){ statusLabel.setText("ბლოგების ჩატვირთვა ვერ მოხერხდა: "+ex.getMessage()); }
    }
    private void open(String path){ try{ Parent page=FXMLLoader.load(getClass().getResource(path)); hB1.getScene().setRoot(page);}catch(Exception e){e.printStackTrace();} }
}
