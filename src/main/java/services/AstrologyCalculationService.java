package services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import models.BirthChart;
import models.PlanetPosition;

public class AstrologyCalculationService {

    private static final String[] SIGNS = {
        "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
        "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    };

    private static final String[] PLANET_NAMES = {
        "Sun", "Moon", "Mercury", "Venus", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto"
    };

    private static final String[] PLANET_GLYPHS = {
        "☉", "☾", "☿", "♀", "♂", "♃", "♄", "♅", "♆", "♇"
    };

    private static final double[][] ORBITAL_ELEMENTS = {
        {0, 0, 282.9404, 1.0, 0.016709, 356.0470, 0, 0, 4.70935E-5, 0.9856002585},
        {48.3313, 7.0047, 29.1241, 0.387098, 0.205635, 168.6562, 3.24587E-5, 5.00E-8, 1.01444E-5, 4.0923344368},
        {76.6799, 3.3946, 54.8910, 0.723330, 0.006773, 48.0052, 2.46590E-5, 2.75E-8, 1.38374E-5, 1.6021302244},
        {49.5574, 1.8497, 286.5016, 1.523688, 0.093405, 18.6021, 2.11081E-5, -1.78E-8, 2.92961E-5, 0.5240207766},
        {100.4542, 1.3030, 273.8777, 5.20256, 0.048498, 19.8950, 2.76854E-5, -1.557E-7, 1.64505E-5, 0.0830853001},
        {113.6634, 2.4886, 339.3939, 9.55475, 0.055546, 316.9670, 2.38980E-5, -1.081E-7, 2.97661E-5, 0.0334442282},
        {74.0005, 0.7733, 96.6612, 19.18171, 0.047318, 142.5905, 1.3978E-5, 1.9E-8, 3.0565E-5, 0.011725806},
        {131.7806, 1.7700, 272.8461, 30.05826, 0.008606, 260.2471, 3.0173E-5, -2.55E-7, -2.0E-5, 0.005995147},
        {110.30347, 17.14175, 113.76329, 39.48168677, 0.24880766, 14.53, 2.551E-5, 1.844E-5, 2.38E-5, 0.0039757}
    };

    public BirthChart calculate(String fullName, LocalDateTime localBirthTime, String place,
                                double latitude, double longitude, String timezone) {
        ZoneId zone = ZoneId.of(timezone);
        ZonedDateTime zoned = localBirthTime.atZone(zone);
        double julianDay = toJulianDay(zoned.toLocalDateTime().toEpochSecond(zoned.getOffset()));

        List<PlanetPosition> planets = new ArrayList<>();
        double[] longitudes = new double[PLANET_NAMES.length];
        java.time.LocalDateTime utcLocal = localBirthTime;
        for (int i = 0; i < PLANET_NAMES.length; i++) {
            longitudes[i] = services.AstronomyEngineBridge.longitude(
                    PLANET_NAMES[i], utcLocal, timezone);
        }


        double sidereal = localSiderealDegrees(julianDay, longitude);
        double ascendant = calculateAscendant(sidereal, latitude);
        double midheaven = normalize(Math.toDegrees(Math.atan2(
                Math.sin(Math.toRadians(sidereal)),
                Math.cos(Math.toRadians(sidereal)) * Math.cos(Math.toRadians(23.4393)))));

        double[] houses = new double[12];
        for (int i = 0; i < 12; i++) {
            houses[i] = normalize(ascendant + i * 30.0);
        }

        for (int i = 0; i < longitudes.length; i++) {
            int house = (int) (normalize(longitudes[i] - ascendant) / 30.0) + 1;
            double before = services.AstronomyEngineBridge.longitude(PLANET_NAMES[i],
                    localBirthTime.minusHours(12), timezone);
            double after = services.AstronomyEngineBridge.longitude(PLANET_NAMES[i],
                    localBirthTime.plusHours(12), timezone);
            double movement = normalize(after - before + 180.0) - 180.0;
            planets.add(new PlanetPosition(PLANET_NAMES[i], PLANET_GLYPHS[i],
                    longitudes[i], Math.min(12, house), movement < 0));
        }

        List<String> aspects = calculateAspects(planets);
        String json = toJson(ascendant, midheaven, houses, planets, aspects);

        return new BirthChart(fullName, localBirthTime.toLocalDate(), localBirthTime.toLocalTime(),
                place, latitude, longitude, timezone, ascendant, midheaven,
                houses, planets, aspects, json);
    }

    private double[] heliocentric(double[] e, double d) {
        double n = normalize(e[0] + e[6] * d);
        double inc = e[1] + e[7] * d;
        double w = normalize(e[2] + e[8] * d);
        double a = e[3];
        double eccentricity = e[4] + (e[4] == 0.016709 ? -1.151E-9 * d : 0);
        double m = normalize(e[5] + e[9] * d);
        double eccentricAnomaly = Math.toRadians(m);

        for (int i = 0; i < 8; i++) {
            eccentricAnomaly -= (eccentricAnomaly - eccentricity * Math.sin(eccentricAnomaly)
                    - Math.toRadians(m)) / (1 - eccentricity * Math.cos(eccentricAnomaly));
        }

        double xv = a * (Math.cos(eccentricAnomaly) - eccentricity);
        double yv = a * Math.sqrt(1 - eccentricity * eccentricity) * Math.sin(eccentricAnomaly);
        double v = Math.atan2(yv, xv);
        double r = Math.sqrt(xv * xv + yv * yv);

        double nn = Math.toRadians(n);
        double ii = Math.toRadians(inc);
        double vw = v + Math.toRadians(w);

        double x = r * (Math.cos(nn) * Math.cos(vw) - Math.sin(nn) * Math.sin(vw) * Math.cos(ii));
        double y = r * (Math.sin(nn) * Math.cos(vw) + Math.cos(nn) * Math.sin(vw) * Math.cos(ii));
        double z = r * Math.sin(vw) * Math.sin(ii);

        return new double[] {x, y, z};
    }

    private double moonLongitude(double d) {
        double n = normalize(125.1228 - 0.0529538083 * d);
        double w = normalize(318.0634 + 0.1643573223 * d);
        double m = normalize(115.3654 + 13.0649929509 * d);
        double eccentricity = 0.0549;
        double eccentricAnomaly = Math.toRadians(m);

        for (int i = 0; i < 8; i++) {
            eccentricAnomaly -= (eccentricAnomaly - eccentricity * Math.sin(eccentricAnomaly)
                    - Math.toRadians(m)) / (1 - eccentricity * Math.cos(eccentricAnomaly));
        }

        double xv = 60.2666 * (Math.cos(eccentricAnomaly) - eccentricity);
        double yv = 60.2666 * Math.sqrt(1 - eccentricity * eccentricity) * Math.sin(eccentricAnomaly);
        double v = Math.atan2(yv, xv);
        double longitude = normalize(n + w + Math.toDegrees(v));

        double sunMean = normalize(356.0470 + 0.9856002585 * d);
        double elongation = normalize(longitude - normalize(280.460 + 0.9856474 * d));
        double argumentLatitude = normalize(longitude - n);

        longitude += -1.274 * Math.sin(Math.toRadians(m - 2 * elongation))
                + 0.658 * Math.sin(Math.toRadians(2 * elongation))
                - 0.186 * Math.sin(Math.toRadians(sunMean))
                - 0.059 * Math.sin(Math.toRadians(2 * m - 2 * elongation))
                - 0.057 * Math.sin(Math.toRadians(m - 2 * elongation + sunMean))
                + 0.053 * Math.sin(Math.toRadians(m + 2 * elongation))
                + 0.046 * Math.sin(Math.toRadians(2 * elongation - sunMean))
                - 0.041 * Math.sin(Math.toRadians(m - sunMean))
                - 0.035 * Math.sin(Math.toRadians(elongation))
                - 0.031 * Math.sin(Math.toRadians(m + sunMean))
                - 0.015 * Math.sin(Math.toRadians(2 * argumentLatitude - 2 * elongation))
                + 0.011 * Math.sin(Math.toRadians(m - 4 * elongation));

        return normalize(longitude);
    }

    private double calculateAscendant(double sidereal, double latitude) {
        double epsilon = Math.toRadians(23.4393);
        double lat = Math.toRadians(latitude);
        double previous = altitude(0, sidereal, lat, epsilon);
        double previousLambda = 0;

        for (int i = 1; i <= 720; i++) {
            double lambda = i * 0.5;
            double current = altitude(lambda, sidereal, lat, epsilon);

            if (previous == 0 || current == 0 || previous * current < 0) {
                double low = previousLambda;
                double high = lambda;

                for (int j = 0; j < 35; j++) {
                    double mid = (low + high) / 2.0;
                    double lowValue = altitude(low, sidereal, lat, epsilon);
                    double midValue = altitude(mid, sidereal, lat, epsilon);
                    if (lowValue * midValue <= 0) high = mid;
                    else low = mid;
                }

                double root = (low + high) / 2.0;
                double ra = rightAscension(root, epsilon);
                double hourAngle = signedAngle(ra - sidereal);

                if (hourAngle < 0) return normalize(root);
            }

            previous = current;
            previousLambda = lambda;
        }

        return normalize(sidereal + 90.0);
    }

    private double altitude(double lambda, double sidereal, double latitude, double epsilon) {
        double lambdaRad = Math.toRadians(lambda);
        double dec = Math.asin(Math.sin(epsilon) * Math.sin(lambdaRad));
        double hourAngle = Math.toRadians(signedAngle(rightAscension(lambda, epsilon) - sidereal));
        double sinAltitude = Math.sin(latitude) * Math.sin(dec)
                + Math.cos(latitude) * Math.cos(dec) * Math.cos(hourAngle);
        return Math.asin(Math.max(-1, Math.min(1, sinAltitude)));
    }

    private double rightAscension(double longitude, double epsilon) {
        double l = Math.toRadians(longitude);
        return normalize(Math.toDegrees(Math.atan2(Math.cos(epsilon) * Math.sin(l), Math.cos(l))));
    }

    private double localSiderealDegrees(double jd, double longitude) {
        double t = (jd - 2451545.0) / 36525.0;
        double gmst = 280.46061837 + 360.98564736629 * (jd - 2451545.0)
                + 0.000387933 * t * t - t * t * t / 38710000.0;
        return normalize(gmst + longitude);
    }

    private double toJulianDay(long epochSeconds) {
        return epochSeconds / 86400.0 + 2440587.5;
    }

    private List<String> calculateAspects(List<PlanetPosition> planets) {
        List<String> aspects = new ArrayList<>();
        double[] angles = {0, 60, 90, 120, 180};
        String[] names = {"Conjunction", "Sextile", "Square", "Trine", "Opposition"};

        for (int i = 0; i < planets.size(); i++) {
            for (int j = i + 1; j < planets.size(); j++) {
                double difference = Math.abs(planets.get(i).getLongitude() - planets.get(j).getLongitude());
                difference = Math.min(difference, 360 - difference);

                for (int k = 0; k < angles.length; k++) {
                    double orb = Math.abs(difference - angles[k]);
                    double allowed = (k == 0 || k == 4) ? 8 : 6;
                    if (orb <= allowed) {
                        aspects.add(planets.get(i).getName() + " " + names[k] + " "
                                + planets.get(j).getName() + " (" + String.format("%.1f°", orb) + " orb)");
                        break;
                    }
                }
            }
        }
        return aspects;
    }

    private String toJson(double asc, double mc, double[] houses,
                          List<PlanetPosition> planets, List<String> aspects) {
        StringBuilder json = new StringBuilder();
        json.append("{\"calculation\":\"low-precision orbital elements; equal houses\",");
        json.append("\"ascendant\":").append(asc).append(',');
        json.append("\"midheaven\":").append(mc).append(',');
        json.append("\"houses\":[");
        for (int i = 0; i < houses.length; i++) {
            if (i > 0) json.append(',');
            json.append(houses[i]);
        }
        json.append("],\"planets\":[");
        for (int i = 0; i < planets.size(); i++) {
            PlanetPosition p = planets.get(i);
            if (i > 0) json.append(',');
            json.append("{\"name\":\"").append(p.getName()).append("\",\"longitude\":")
                    .append(p.getLongitude()).append(",\"house\":").append(p.getHouse()).append('}');
        }
        json.append("],\"aspects\":[");
        for (int i = 0; i < aspects.size(); i++) {
            if (i > 0) json.append(',');
            json.append('\"').append(aspects.get(i).replace("\"", "\\\"")).append('\"');
        }
        json.append("]}");
        return json.toString();
    }

    private double signedAngle(double value) {
        value = normalize(value);
        return value > 180 ? value - 360 : value;
    }

    private double normalize(double value) {
        return PlanetPosition.normalize(value);
    }

    public static String signName(double longitude) {
        return SIGNS[(int) (PlanetPosition.normalize(longitude) / 30.0)];
    }

    public static String formatPosition(double longitude) {
        double degree = PlanetPosition.normalize(longitude) % 30.0;
        return signName(longitude) + " " + String.format("%.1f°", degree);
    }
}
