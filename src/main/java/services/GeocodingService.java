package services;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeocodingService {

    private static long lastRequestTime = 0;
    private static final java.util.Map<String, Location> LOCATION_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public Location findLocation(String place) throws Exception {
        String cacheKey = place.trim().toLowerCase(java.util.Locale.ROOT);
        Location cached = LOCATION_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        synchronized (GeocodingService.class) {
            long wait = 1100 - (System.currentTimeMillis() - lastRequestTime);
            if (wait > 0) {
                Thread.sleep(wait);
            }
            lastRequestTime = System.currentTimeMillis();
        }

        String query = URLEncoder.encode(place, StandardCharsets.UTF_8);
        String url = "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&q=" + query;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("User-Agent", "AstroSphere/1.0 (JavaFX birth chart application)")
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Birth place lookup failed. Please try a more specific city and country.");
        }

        String body = response.body();
        Matcher latMatcher = Pattern.compile("\"lat\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
        Matcher lonMatcher = Pattern.compile("\"lon\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
        Matcher nameMatcher = Pattern.compile("\"display_name\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"").matcher(body);

        if (!latMatcher.find() || !lonMatcher.find()) {
            throw new IllegalArgumentException("The birth place could not be found. Enter a city and country.");
        }

        String resolvedName = place;
        if (nameMatcher.find()) {
            resolvedName = nameMatcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
        }

        Location location = new Location(Double.parseDouble(latMatcher.group(1)),
                Double.parseDouble(lonMatcher.group(1)), resolvedName);
        LOCATION_CACHE.put(cacheKey, location);
        return location;
    }

    public static class Location {
        private final double latitude;
        private final double longitude;
        private final String displayName;

        public Location(double latitude, double longitude, String displayName) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.displayName = displayName;
        }

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getDisplayName() { return displayName; }
    }
}
