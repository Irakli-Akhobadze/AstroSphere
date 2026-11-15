package models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class BirthChart {
    private final String fullName;
    private final LocalDate dateOfBirth;
    private final LocalTime timeOfBirth;
    private final String birthPlace;
    private final double latitude;
    private final double longitude;
    private final String timezone;
    private final double ascendant;
    private final double midheaven;
    private final double[] houseCusps;
    private final List<PlanetPosition> planets;
    private final List<String> aspects;
    private final String chartJson;

    public BirthChart(String fullName, LocalDate dateOfBirth, LocalTime timeOfBirth,
                      String birthPlace, double latitude, double longitude, String timezone,
                      double ascendant, double midheaven, double[] houseCusps,
                      List<PlanetPosition> planets, List<String> aspects, String chartJson) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.timeOfBirth = timeOfBirth;
        this.birthPlace = birthPlace;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.ascendant = ascendant;
        this.midheaven = midheaven;
        this.houseCusps = houseCusps;
        this.planets = planets;
        this.aspects = aspects;
        this.chartJson = chartJson;
    }

    public String getFullName() { return fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public LocalTime getTimeOfBirth() { return timeOfBirth; }
    public String getBirthPlace() { return birthPlace; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getTimezone() { return timezone; }
    public double getAscendant() { return ascendant; }
    public double getMidheaven() { return midheaven; }
    public double[] getHouseCusps() { return houseCusps; }
    public List<PlanetPosition> getPlanets() { return planets; }
    public List<String> getAspects() { return aspects; }
    public String getChartJson() { return chartJson; }
}
