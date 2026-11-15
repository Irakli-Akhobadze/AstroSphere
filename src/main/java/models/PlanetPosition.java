package models;

public class PlanetPosition {
    private final String name;
    private final String glyph;
    private final double longitude;
    private final int house;
    private final boolean retrograde;

    public PlanetPosition(String name, String glyph, double longitude, int house, boolean retrograde) {
        this.name = name;
        this.glyph = glyph;
        this.longitude = longitude;
        this.house = house;
        this.retrograde = retrograde;
    }

    public String getName() { return name; }
    public String getGlyph() { return glyph; }
    public double getLongitude() { return longitude; }
    public int getHouse() { return house; }
    public boolean isRetrograde() { return retrograde; }

    public int getSignIndex() {
        return (int) (normalize(longitude) / 30.0);
    }

    public double getDegreeInSign() {
        return normalize(longitude) % 30.0;
    }

    public static double normalize(double value) {
        value %= 360.0;
        return value < 0 ? value + 360.0 : value;
    }
}
