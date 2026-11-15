package services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.zone.ZoneRules;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeZoneService {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public ZoneId findTimeZone(double latitude, double longitude) throws Exception {
        String url = "https://timeapi.io/api/timezone/coordinate?latitude="
                + latitude + "&longitude=" + longitude;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Could not determine the time zone for this birth place.");
        }

        Matcher matcher = Pattern.compile("\"timeZone\"\\s*:\\s*\"([^\"]+)\"").matcher(response.body());
        if (!matcher.find()) {
            throw new IllegalStateException("The time-zone service returned an unexpected response.");
        }

        return ZoneId.of(matcher.group(1));
    }

    public static java.time.ZoneOffset getBirthOffset(ZoneId zone, LocalDateTime localDateTime) {
        ZoneRules rules = zone.getRules();
        java.util.List<java.time.ZoneOffset> offsets = rules.getValidOffsets(localDateTime);

        if (offsets.isEmpty()) {
            throw new IllegalArgumentException("This local birth time did not exist because clocks changed in that location. Check the time.");
        }

        return offsets.get(0);
    }
}
