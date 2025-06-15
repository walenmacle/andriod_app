package edu.neu.myapplication5_25.model;

public class WeatherData {
    private String cityName;
    private String country;
    private double temperature;
    private String description;
    private String icon;
    private int humidity;
    private double windSpeed;
    private double pressure;
    private long timestamp;
    private double feelsLike;
    private int visibility;
    private String weatherMain;

    public WeatherData() {}

    public WeatherData(String cityName, String country, double temperature, 
                      String description, String icon, int humidity, 
                      double windSpeed, double pressure, long timestamp) {
        this.cityName = cityName;
        this.country = country;
        this.temperature = temperature;
        this.description = description;
        this.icon = icon;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public double getPressure() { return pressure; }
    public void setPressure(double pressure) { this.pressure = pressure; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

    public int getVisibility() { return visibility; }
    public void setVisibility(int visibility) { this.visibility = visibility; }

    public String getWeatherMain() { return weatherMain; }
    public void setWeatherMain(String weatherMain) { this.weatherMain = weatherMain; }

    @Override
    public String toString() {
        return "WeatherData{" +
                "cityName='" + cityName + '\'' +
                ", country='" + country + '\'' +
                ", temperature=" + temperature +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", humidity=" + humidity +
                ", windSpeed=" + windSpeed +
                ", pressure=" + pressure +
                ", timestamp=" + timestamp +
                ", feelsLike=" + feelsLike +
                ", visibility=" + visibility +
                ", weatherMain='" + weatherMain + '\'' +
                '}';
    }
} 