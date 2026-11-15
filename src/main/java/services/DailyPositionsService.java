package services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import models.DailyPlanetaryPosition;

public class DailyPositionsService {
    private static final String[] NAMES = {"Sun", "Moon", "Mercury", "Venus", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto"};
    private static final String[] GLYPHS = {"☉", "☾", "☿", "♀", "♂", "♃", "♄", "♅", "♆", "♇"};
    private static final String[] SIGNS = {"ვერძი", "კურო", "ტყუპები", "კირჩხიბი", "ლომი", "ქალწული", "სასწორი", "მორიელი", "მშვილდოსანი", "თხის რქა", "მერწყული", "თევზები"};

    public List<DailyPlanetaryPosition> calculate(LocalDate date) {
        List<DailyPlanetaryPosition> positions = new ArrayList<>();
        LocalDateTime noon = date.atTime(12, 0);
        for (int i = 0; i < NAMES.length; i++) {
            double longitude = AstronomyEngineBridge.longitude(NAMES[i], noon, "UTC");
            double before = AstronomyEngineBridge.longitude(NAMES[i], noon.minusHours(12), "UTC");
            double after = AstronomyEngineBridge.longitude(NAMES[i], noon.plusHours(12), "UTC");
            double movement = normalize(after - before + 180.0) - 180.0;
            int signIndex = (int)(normalize(longitude) / 30.0);
            double degree = normalize(longitude) - signIndex * 30.0;
            positions.add(new DailyPlanetaryPosition(date, NAMES[i], GLYPHS[i], SIGNS[signIndex], degree, movement < 0));
        }
        return positions;
    }

    private double normalize(double value) {
        value %= 360.0;
        return value < 0 ? value + 360.0 : value;
    }
}
