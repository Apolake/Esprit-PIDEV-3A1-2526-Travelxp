package com.travelxp.models;

/**
 * DTO for weather information fetched from OpenWeather API.
 * Stores current weather conditions for a given location.
 */
public class WeatherDTO {
    private String condition;            // e.g., "Clear", "Rain", "Clouds"
    private String description;          // more detailed description
    private Double temperature;          // in Celsius
    private Double temperatureFahrenheit; // in Fahrenheit
    private Double feelsLike;           // feels-like temperature
    private Integer humidity;           // percentage 0-100
    private Double windSpeed;           // in m/s
    private Double pressure;            // in hPa
    private Double visibility;          // in meters
    private String city;                // city name
    private String country;             // country code
    private Long fetchTime;             // timestamp when fetched

    public WeatherDTO() {}

    public WeatherDTO(
        String condition, String description,
        Double temperature, Double feelsLike,
        Integer humidity, Double windSpeed,
        Double pressure, Double visibility,
        String city, String country) {
        this.condition        = condition;
        this.description      = description;
        this.temperature      = temperature;
        this.feelsLike        = feelsLike;
        this.humidity         = humidity;
        this.windSpeed        = windSpeed;
        this.pressure         = pressure;
        this.visibility       = visibility;
        this.city             = city;
        this.country          = country;
        this.fetchTime        = System.currentTimeMillis();
        this.temperatureFahrenheit = temperature != null
                                     ? (temperature * 9.0 / 5.0) + 32
                                     : null;
    }

    // getters and setters
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
        if (temperature != null) {
            this.temperatureFahrenheit = (temperature * 9.0 / 5.0) + 32;
        }
    }

    public Double getTemperatureFahrenheit() { return temperatureFahrenheit; }

    public Double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(Double feelsLike) { this.feelsLike = feelsLike; }

    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }

    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }

    public Double getPressure() { return pressure; }
    public void setPressure(Double pressure) { this.pressure = pressure; }

    public Double getVisibility() { return visibility; }
    public void setVisibility(Double visibility) { this.visibility = visibility; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Long getFetchTime() { return fetchTime; }
    public void setFetchTime(Long fetchTime) { this.fetchTime = fetchTime; }

    @Override
    public String toString() {
        return "WeatherDTO{" +
                "condition='" + condition + '\'' +
                ", temperature=" + temperature + "°C" +
                ", humidity=" + humidity + "%" +
                ", windSpeed=" + windSpeed + " m/s" +
                ", city='" + city + '\'' +
                '}';
    }
}
