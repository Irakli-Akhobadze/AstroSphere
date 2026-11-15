package controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import daos.DailyPositionsDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.DailyPlanetaryPosition;
import services.DailyPositionsService;

public class DailyPositionsController {
	@FXML
	private HBox hB1, hB2, hB3, hB4, hB5, hB6, hB7;
	
	@FXML
	private DatePicker datePicker;
	
	@FXML
	private VBox positionsList;
	
	@FXML private Label dateLabel, statusLabel;
    private final DailyPositionsService service = new DailyPositionsService();

    @FXML private void initialize() {
        datePicker.setValue(LocalDate.now());
        datePicker.valueProperty().addListener((o,oldValue,newValue) -> { if (newValue != null) loadDate(newValue); });
        hB1.setOnMouseClicked(e -> open("/fxml/Dashboard.fxml"));
        hB2.setOnMouseClicked(e -> open("/fxml/BirthChart.fxml"));
        hB3.setOnMouseClicked(e -> {});
        hB4.setOnMouseClicked(e -> open("/fxml/MyBlogs.fxml"));
        hB5.setOnMouseClicked(e -> open("/fxml/MyBlogs.fxml"));
        hB7.setOnMouseClicked(e -> { UserSession.clear(); open("/fxml/logIn.fxml"); });
        loadDate(LocalDate.now());
    }

    private void loadDate(LocalDate date) {
        positionsList.getChildren().clear();
        dateLabel.setText("პლანეტების მდებარეობა · " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        statusLabel.setText("მიმდინარეობს გამოთვლა...");
        try {
            List<DailyPlanetaryPosition> values = service.calculate(date);
            for (DailyPlanetaryPosition p : values) {
                HBox row = new HBox(14);
                row.getStyleClass().add("planet-row");
                Label glyph = new Label(p.getGlyph()); glyph.setStyle("-fx-font-size: 23px; -fx-text-fill: #7543E8;");
                VBox details = new VBox(3);
                Label name = new Label(p.getPlanet()); name.getStyleClass().add("planet-name");
                String retro = p.isRetrograde() ? " · რეტროგრადული" : "";
                Label value = new Label(String.format(java.util.Locale.ROOT, "%s · %.2f°%s", p.getZodiacSign(), p.getDegree(), retro));
                value.getStyleClass().add("planet-position");
                details.getChildren().addAll(name,value); row.getChildren().addAll(glyph,details);
                positionsList.getChildren().add(row);
            }
            try {
                DailyPositionsDao.saveForDate(values, date);
                statusLabel.setText("მონაცემები გამოთვლილია არჩეული დღის 12:00 UTC დროისთვის და შენახულია ბაზაში.");
            } catch (Exception dbError) {
                statusLabel.setText("პოზიციები გამოთვლილია, თუმცა ბაზაში შენახვა ვერ მოხერხდა: " + dbError.getMessage());
            }
        } catch (Exception ex) {
            statusLabel.setText("პოზიციების გამოთვლა ვერ მოხერხდა: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    private void open(String path) {
        try { Parent page = FXMLLoader.load(getClass().getResource(path)); hB1.getScene().setRoot(page); }
        catch (Exception e) { e.printStackTrace(); }
    }
}
