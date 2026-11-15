package models;

import java.time.LocalDate;
import java.time.LocalTime;

public class BirthChartDetails {
    private final String fullName;
    private final LocalDate dateOfBirth;
    private final LocalTime timeOfBirth;
    private final String birthPlace;
    public BirthChartDetails(String fullName, LocalDate dateOfBirth, LocalTime timeOfBirth, String birthPlace) {
        this.fullName=fullName; this.dateOfBirth=dateOfBirth; this.timeOfBirth=timeOfBirth; this.birthPlace=birthPlace;
    }
    public String getFullName(){return fullName;}
    public LocalDate getDateOfBirth(){return dateOfBirth;}
    public LocalTime getTimeOfBirth(){return timeOfBirth;}
    public String getBirthPlace(){return birthPlace;}
}
