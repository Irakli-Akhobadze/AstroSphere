package services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import io.github.cosinekitty.astronomy.Aberration;
import io.github.cosinekitty.astronomy.Astronomy;
import io.github.cosinekitty.astronomy.Body;
import io.github.cosinekitty.astronomy.Ecliptic;
import io.github.cosinekitty.astronomy.Time;

public class AstronomyEngineBridge {

    public static double longitude(String planet, LocalDateTime dateTime, String zone) {

        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of(zone));
        ZonedDateTime utc = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        Time time = new Time(
                utc.getYear(),
                utc.getMonthValue(),
                utc.getDayOfMonth(),
                utc.getHour(),
                utc.getMinute(),
                utc.getSecond() + utc.getNano() / 1_000_000_000.0
        );

        Body body;

        switch (planet) {
            case "Sun":
                body = Body.Sun;
                break;
            case "Moon":
                body = Body.Moon;
                break;
            case "Mercury":
                body = Body.Mercury;
                break;
            case "Venus":
                body = Body.Venus;
                break;
            case "Mars":
                body = Body.Mars;
                break;
            case "Jupiter":
                body = Body.Jupiter;
                break;
            case "Saturn":
                body = Body.Saturn;
                break;
            case "Uranus":
                body = Body.Uranus;
                break;
            case "Neptune":
                body = Body.Neptune;
                break;
            case "Pluto":
                body = Body.Pluto;
                break;
            default:
                throw new IllegalArgumentException("Unknown planet: " + planet);
        }

        if (body == Body.Moon) {
            return Astronomy.eclipticGeoMoon(time).getLon();
        }

        Ecliptic ecliptic = Astronomy.equatorialToEcliptic(
                Astronomy.geoVector(body, time, Aberration.Corrected)
        );

        return ecliptic.getElon();
    }
}