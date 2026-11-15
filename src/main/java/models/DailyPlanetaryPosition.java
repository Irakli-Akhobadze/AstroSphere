package models;

import java.time.LocalDate;

public class DailyPlanetaryPosition {
    private final LocalDate date;
    private final String planet;
    private final String glyph;
    private final String zodiacSign;
    private final double degree;
    private final boolean retrograde;

    public DailyPlanetaryPosition(LocalDate date, String planet, String glyph, String zodiacSign,
                                  double degree, boolean retrograde) {
        this.date = date; this.planet = planet; this.glyph = glyph;
        this.zodiacSign = zodiacSign; this.degree = degree; this.retrograde = retrograde;
    }
    public LocalDate getDate() { return date; }
    public String getPlanet() { return planet; }
    public String getGlyph() { return glyph; }
    public String getZodiacSign() { return zodiacSign; }
    public double getDegree() { return degree; }
    public boolean isRetrograde() { return retrograde; }
}
