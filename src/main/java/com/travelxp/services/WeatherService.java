package com.travelxp.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.travelxp.models.WeatherDTO;

/**
 * Service that retrieves current weather information from OpenWeather API.
 * Uses HttpClient (Java 11+) for REST calls and Gson for JSON parsing.
 *
 * Features:
 * - Fetches weather by latitude/longitude
 * - Parses JSON response from OpenWeather
 * - Handles errors gracefully
 * - Caches API key (load from environment variable or config file)
 */
public class WeatherService {

    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final String apiKey;
    private final HttpClient httpClient;

    /**
     * Initialize with API key from environment variable.
     * Set OPENWEATHER_API_KEY environment variable before running.
     */
    public WeatherService() {
        this.apiKey = System.getenv("OPENWEATHER_API_KEY");
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "OPENWEATHER_API_KEY environment variable is not set. " +
                "Please set it before using WeatherService. " +
                "Get a free key at https://openweathermap.org/api"
            );
        }
    }

    /**
     * Alternative constructor allowing injection of API key (for testing).
     */
    public WeatherService(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    /**
     * Fetch current weather for a given location.
     *
     * @param latitude  of the location
     * @param longitude of the location
     * @return WeatherDTO with current weather data
     * @throws IOException           if network request fails
     * @throws InterruptedException  if request is interrupted
     * @throws IllegalArgumentException if API response is invalid
     */
    public WeatherDTO getWeatherByCoordinates(double latitude, double longitude)
            throws IOException, InterruptedException {

        String url = String.format(
            "%s?lat=%.4f&lon=%.4f&appid=%s&units=metric",
            API_BASE_URL, latitude, longitude, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(java.time.Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException(
                    "OpenWeather API returned status " + response.statusCode() +
                    ": " + response.body()
                );
            }

            return parseWeatherResponse(response.body());
        } catch (IOException e) {
            throw new IOException("Failed to fetch weather data: " + e.getMessage(), e);
        }
    }

    /**
     * Parse JSON response from OpenWeather API.
     *
     * Expected JSON structure (simplified):
     * {
     *   "name": "London",
     *   "sys": { "country": "GB" },
     *   "main": {
     *     "temp": 10.5,
     *     "feels_like": 9.2,
     *     "pressure": 1013,
     *     "humidity": 72
     *   },
     *   "weather": [{
     *     "main": "Clouds",
     *     "description": "overcast clouds"
     *   }],
     *   "wind": { "speed": 5.1 },
     *   "visibility": 10000
     * }
     */
    private WeatherDTO parseWeatherResponse(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

            String city = obj.has("name") ? obj.get("name").getAsString() : "Unknown";
            String country = "Unknown";
            if (obj.has("sys")) {
                country = obj.getAsJsonObject("sys")
                        .get("country").getAsString();
            }

            String condition = "Unknown";
            String description = "";
            if (obj.has("weather")) {
                JsonObject weatherObj = obj.getAsJsonArray("weather")
                        .get(0).getAsJsonObject();
                condition = weatherObj.get("main").getAsString();
                description = weatherObj.get("description").getAsString();
            }

            Double temperature = null;
            Double feelsLike = null;
            Integer humidity = null;
            Double pressure = null;
            if (obj.has("main")) {
                JsonObject main = obj.getAsJsonObject("main");
                temperature = main.get("temp").getAsDouble();
                feelsLike = main.get("feels_like").getAsDouble();
                humidity = main.get("humidity").getAsInt();
                pressure = main.get("pressure").getAsDouble();
            }

            Double windSpeed = null;
            if (obj.has("wind")) {
                windSpeed = obj.getAsJsonObject("wind")
                        .get("speed").getAsDouble();
            }

            Double visibility = null;
            if (obj.has("visibility")) {
                visibility = obj.get("visibility").getAsDouble();
            }

            return new WeatherDTO(
                condition, description, temperature, feelsLike,
                humidity, windSpeed, pressure, visibility,
                city, country
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Failed to parse weather response: " + e.getMessage(), e
            );
        }
    }
}
