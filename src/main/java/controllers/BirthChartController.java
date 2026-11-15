package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import daos.BirthChartDao;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import models.BirthChart;
import models.PlanetPosition;
import services.AstrologyCalculationService;
import services.BirthChartService;

public class BirthChartController {
	 @FXML private HBox hB1, hB2, hB3, hB4, hB5, hB6, hB7;
	    @FXML private TextField fullNameField, timeOfBirthField, birthPlaceField;
	    @FXML private DatePicker dateOfBirthPicker;
	    @FXML private Label fullNameError, dateError, timeError, placeError;
	    @FXML private Label statusLabel, chartPersonLabel, sunSummary, moonSummary, ascSummary, aspectSummary;
	    @FXML private Button generateButton, saveButton;
	    @FXML private Canvas chartCanvas;
	    @FXML private VBox planetList;

	    private final BirthChartService birthChartService = new BirthChartService();
	    private BirthChart currentChart;

	    @FXML
	    private void initialize() {
	        javafx.scene.text.Font.loadFont(
	                getClass().getResourceAsStream("/fonts/ss_geo_hakuna.otf"), 14);
	        setActiveMenu(hB2);

	        hB1.setOnMouseClicked(event -> openPage("/fxml/Dashboard.fxml"));
	        hB2.setOnMouseClicked(event -> setActiveMenu(hB2));
	        hB3.setOnMouseClicked(event -> openPage("/fxml/DailyPositions.fxml"));
	        hB4.setOnMouseClicked(event -> openPage("/fxml/MyBlogs.fxml"));
	        hB5.setOnMouseClicked(event -> openPage("/fxml/MyBlogs.fxml"));
	        hB6.setOnMouseClicked(event -> setActiveMenu(hB6));
	        hB7.setOnMouseClicked(event -> {
	            UserSession.clear();
	            openPage("/fxml/logIn.fxml");
	        });

	        fullNameField.textProperty().addListener((obs, oldValue, newValue) -> hideError(fullNameError));
	        birthPlaceField.textProperty().addListener((obs, oldValue, newValue) -> hideError(placeError));
	        timeOfBirthField.textProperty().addListener((obs, oldValue, newValue) -> hideError(timeError));
	        dateOfBirthPicker.valueProperty().addListener((obs, oldValue, newValue) -> hideError(dateError));

	        drawEmptyChart();
	        int userId = UserSession.getCurrentUserId();
	        if (userId > 0) {
	            try {
	                models.BirthChartDetails saved = BirthChartDao.loadLatest(userId);
	                if (saved != null) {
	                    fullNameField.setText(saved.getFullName());
	                    dateOfBirthPicker.setValue(saved.getDateOfBirth());
	                    timeOfBirthField.setText(saved.getTimeOfBirth().toString().substring(0, 5));
	                    birthPlaceField.setText(saved.getBirthPlace());
	                    generateChart();
	                }
	            } catch (SQLException ex) {
	                statusLabel.setText("შენახული დაბადების მონაცემების წაკითხვა ვერ მოხერხდა: " + ex.getMessage());
	            }
	        }
	    }

	    @FXML
	    private void generateChart() {
	        String fullName = fullNameField.getText() == null ? "" : fullNameField.getText().trim();
	        String place = birthPlaceField.getText() == null ? "" : birthPlaceField.getText().trim();
	        LocalDate date = dateOfBirthPicker.getValue();
	        LocalTime time = null;

	        boolean valid = true;

	        if (fullName.isEmpty()) {
	            showError(fullNameError, "შეიყვანე სრული სახელი");
	            valid = false;
	        } else {
	            hideError(fullNameError);
	        }

	        if (date == null) {
	            showError(dateError, "აირჩიე დაბადების თარიღი");
	            valid = false;
	        } else if (date.isAfter(LocalDate.now())) {
	            showError(dateError, "დაბადების თარიღი მომავალში ვერ იქნება");
	            valid = false;
	        } else if (date.getYear() < 1900 || date.getYear() > 2100) {
	            showError(dateError, "ამ ვერსიაში მხარდაჭერილია 1900-2100 წლების თარიღები");
	            valid = false;
	        } else {
	            hideError(dateError);
	        }

	        try {
	            time = LocalTime.parse(timeOfBirthField.getText().trim(),
	                    DateTimeFormatter.ofPattern("H:mm", Locale.ROOT));
	            hideError(timeError);
	        } catch (DateTimeParseException | NullPointerException ex) {
	            showError(timeError, "დრო შეიყვანე HH:mm ფორმატით, მაგალითად 14:35");
	            valid = false;
	        }

	        if (place.isEmpty()) {
	            showError(placeError, "შეიყვანე ქალაქი და ქვეყანა");
	            valid = false;
	        } else {
	            hideError(placeError);
	        }

	        if (!valid) {
	            return;
	        }

	        LocalTime birthTime = time;
	        generateButton.setDisable(true);
	        saveButton.setDisable(true);
	        statusLabel.setText("მიმდინარეობს ადგილისა და ცის პოზიციების გამოთვლა...");

	        Thread worker = new Thread(() -> {
	            try {
	                BirthChart chart = birthChartService.generate(fullName, date, birthTime, place);
	                Platform.runLater(() -> {
	                    currentChart = chart;
	                    showChart(chart);
	                    generateButton.setDisable(false);
	                    saveButton.setDisable(UserSession.getCurrentUserId() <= 0);
	                    statusLabel.setText(UserSession.getCurrentUserId() > 0
	                            ? "ნათალური რუკა წარმატებით შეიქმნა. შეგიძლია შეინახო."
	                            : "ნათალური რუკა შეიქმნა. შენახვისთვის საჭიროა აქტიური მომხმარებლის ID.");
	                });
	            } catch (Exception ex) {
	                Platform.runLater(() -> {
	                    generateButton.setDisable(false);
	                    statusLabel.setText(ex.getMessage() == null
	                            ? "რუკის შექმნა ვერ მოხერხდა. სცადე ხელახლა."
	                            : ex.getMessage());
	                });
	            }
	        }, "birth-chart-calculation");

	        worker.setDaemon(true);
	        worker.start();
	    }

	    @FXML
	    private void saveChart() {
	        if (currentChart == null) {
	            statusLabel.setText("ჯერ შექმენი ნათალური რუკა.");
	            return;
	        }

	        int userId = UserSession.getCurrentUserId();
	        if (userId <= 0) {
	            statusLabel.setText("აქტიური მომხმარებელი ვერ მოიძებნა. ავტორიზაციის შემდეგ უნდა ჩაიწეროს მომხმარებლის ID.");
	            return;
	        }

	        saveButton.setDisable(true);
	        try {
	            BirthChartDao.save(currentChart, userId);
	            statusLabel.setText("ნათალური რუკა წარმატებით შეინახა შენს ანგარიშში.");
	        } catch (SQLException | IllegalArgumentException ex) {
	            statusLabel.setText("შენახვა ვერ მოხერხდა: " + ex.getMessage());
	            saveButton.setDisable(false);
	        }
	    }

	    private void showChart(BirthChart chart) {
	        chartPersonLabel.setText(chart.getFullName() + " · " + chart.getDateOfBirth()
	                + " · " + chart.getTimeOfBirth() + " · " + chart.getBirthPlace());

	        sunSummary.setText(summaryFor(chart, "Sun"));
	        moonSummary.setText(summaryFor(chart, "Moon"));
	        ascSummary.setText(AstrologyCalculationService.formatPosition(chart.getAscendant()));

	        planetList.getChildren().clear();
	        for (PlanetPosition planet : chart.getPlanets()) {
	            HBox row = new HBox(10);
	            row.getStyleClass().add("planet-row");

	            Label glyph = new Label(planet.getGlyph());
	            glyph.setMinWidth(24);
	            glyph.setStyle("-fx-text-fill: #7543E8; -fx-font-size: 19px;");

	            VBox details = new VBox(3);
	            Label name = new Label(planet.getName());
	            name.getStyleClass().add("planet-name");

	            Label position = new Label(AstrologyCalculationService.formatPosition(planet.getLongitude())
	                    + " · House " + planet.getHouse() + (planet.isRetrograde() ? " · Retrograde" : ""));
	            position.getStyleClass().add("planet-position");

	            details.getChildren().addAll(name, position);
	            row.getChildren().addAll(glyph, details);
	            planetList.getChildren().add(row);
	        }

	        aspectSummary.setText(chart.getAspects().isEmpty()
	                ? "ასპექტები: ძირითადი ასპექტები არ მოიძებნა."
	                : "ასპექტები: " + String.join(" · ", chart.getAspects()));
	        drawChart(chart);
	    }

	    private String summaryFor(BirthChart chart, String name) {
	        for (PlanetPosition planet : chart.getPlanets()) {
	            if (planet.getName().equals(name)) {
	                return AstrologyCalculationService.formatPosition(planet.getLongitude());
	            }
	        }
	        return "—";
	    }

	    private void drawEmptyChart() {
	        GraphicsContext gc = chartCanvas.getGraphicsContext2D();
	        gc.clearRect(0, 0, chartCanvas.getWidth(), chartCanvas.getHeight());
	        double center = chartCanvas.getWidth() / 2.0;
	        gc.setStroke(Color.web("#E3DCF4"));
	        gc.setLineWidth(1.5);
	        gc.strokeOval(center - 140, center - 140, 280, 280);
	        gc.strokeOval(center - 112, center - 112, 224, 224);
	        gc.strokeOval(center - 75, center - 75, 150, 150);
	        gc.setFill(Color.web("#A69ABF"));
	        gc.setFont(javafx.scene.text.Font.font(14));
	        gc.fillText("შენი რუკა აქ გამოჩნდება", center - 78, center + 5);
	    }

	    private void drawChart(BirthChart chart) {
	        GraphicsContext gc = chartCanvas.getGraphicsContext2D();
	        gc.clearRect(0, 0, chartCanvas.getWidth(), chartCanvas.getHeight());

	        double center = chartCanvas.getWidth() / 2.0;
	        double outer = 140;
	        double zodiacInner = 112;
	        double houseRadius = 106;
	        double planetRadius = 88;

	        gc.setStroke(Color.web("#DCD3F1"));
	        gc.setLineWidth(1.4);
	        gc.strokeOval(center - outer, center - outer, outer * 2, outer * 2);
	        gc.strokeOval(center - zodiacInner, center - zodiacInner, zodiacInner * 2, zodiacInner * 2);
	        gc.strokeOval(center - houseRadius, center - houseRadius, houseRadius * 2, houseRadius * 2);
	        gc.strokeOval(center - 75, center - 75, 150, 150);

	        for (int i = 0; i < 12; i++) {
	            double longitude = chart.getAscendant() + i * 30.0;
	            double angle = Math.toRadians(longitude - chart.getAscendant() + 180.0);
	            double x1 = center + zodiacInner * Math.cos(angle);
	            double y1 = center + zodiacInner * Math.sin(angle);
	            double x2 = center + outer * Math.cos(angle);
	            double y2 = center + outer * Math.sin(angle);

	            gc.setStroke(Color.web("#E7E1F3"));
	            gc.setLineWidth(1);
	            gc.strokeLine(x1, y1, x2, y2);

	            String[] signs = {"♈","♉","♊","♋","♌","♍","♎","♏","♐","♑","♒","♓"};
	            double signAngle = Math.toRadians(longitude - chart.getAscendant() + 15.0 + 180.0);
	            gc.setFill(Color.web("#7543E8"));
	            gc.setFont(javafx.scene.text.Font.font("Serif", 17));
	            int signIndex = (int) (PlanetPosition.normalize(longitude) / 30.0);
	            gc.fillText(signs[signIndex],
	                    center + 125 * Math.cos(signAngle) - 6,
	                    center + 125 * Math.sin(signAngle) + 6);

	            double houseAngle = Math.toRadians(longitude - chart.getAscendant() + 180.0);
	            gc.setStroke(Color.web("#CFC3E9"));
	            gc.strokeLine(center, center,
	                    center + houseRadius * Math.cos(houseAngle),
	                    center + houseRadius * Math.sin(houseAngle));
	        }

	        for (PlanetPosition planet : chart.getPlanets()) {
	            double angle = Math.toRadians(planet.getLongitude() - chart.getAscendant() + 180.0);
	            double x = center + planetRadius * Math.cos(angle);
	            double y = center + planetRadius * Math.sin(angle);

	            gc.setFill(Color.web("#30234F"));
	            gc.setFont(javafx.scene.text.Font.font("Serif", 15));
	            gc.fillText(planet.getGlyph(), x - 6, y + 5);
	        }

	        gc.setFill(Color.web("#7543E8"));
	        gc.setFont(javafx.scene.text.Font.font("Serif", 29));
	        gc.fillText("✦", center - 10, center + 10);

	        gc.setFill(Color.web("#6F6684"));
	        gc.setFont(javafx.scene.text.Font.font(10));
	        gc.fillText("ASC", center - outer + 3, center + 4);
	        gc.fillText("MC " + String.format("%.1f°", chart.getMidheaven()),
	                center - 34, center - outer + 2);
	    }

	    private void showError(Label label, String message) {
	        label.setText(message);
	        label.setVisible(true);
	        label.setManaged(true);
	    }

	    private void hideError(Label label) {
	        label.setVisible(false);
	        label.setManaged(false);
	    }

	    private void setActiveMenu(HBox selected) {
	        HBox[] menus = {hB1, hB2, hB3, hB4, hB5, hB6, hB7};
	        for (HBox menu : menus) {
	            menu.getStyleClass().remove("active");
	        }
	        if (!selected.getStyleClass().contains("active")) {
	            selected.getStyleClass().add("active");
	        }
	    }

	    private void openPage(String path) {
	        try {
	            Parent page = FXMLLoader.load(getClass().getResource(path));
	            Node source = hB1;
	            source.getScene().setRoot(page);
	        } catch (IOException ex) {
	            statusLabel.setText("გვერდის გახსნა ვერ მოხერხდა: " + ex.getMessage());
	        }
	    }
}
