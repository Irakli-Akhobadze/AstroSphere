package services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import models.BirthChart;

public class BirthChartService {

    private final GeocodingService geocodingService = new GeocodingService();
    private final TimeZoneService timeZoneService = new TimeZoneService();
    private final AstrologyCalculationService calculationService = new AstrologyCalculationService();

    public BirthChart generate(String fullName, LocalDate date, LocalTime time, String place) throws Exception {
        GeocodingService.Location location = geocodingService.findLocation(place);
        ZoneId zone = timeZoneService.findTimeZone(location.getLatitude(), location.getLongitude());

        LocalDateTime localBirthDateTime = LocalDateTime.of(date, time);
        List<ZoneOffset> offsets = zone.getRules().getValidOffsets(localBirthDateTime);

        if (offsets.isEmpty()) {
            throw new IllegalArgumentException("The entered birth time is invalid because of a historical clock change.");
        }

        return calculationService.calculate(fullName, localBirthDateTime, place,
                location.getLatitude(), location.getLongitude(), zone.getId());
    }
}
