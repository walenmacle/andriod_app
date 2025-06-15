package edu.neu.myapplication5_25.model;

import java.io.Serializable;
import java.util.Objects;

public class HourlyWeather implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String time;
    private double temperature;
    private String description;
    private String icon;
    private int humidity;
    private double windSpeed;
    private double pressure;
    private String weatherMain;
    private long timestamp;
    private double feelsLike;

    public HourlyWeather() {}

    public HourlyWeather(String time, double temperature, String description, 
                        String icon, int humidity, double windSpeed, 
                        double pressure, String weatherMain, long timestamp, double feelsLike) {
        this.time = time;
        this.temperature = temperature;
        this.description = description;
        this.icon = icon;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.weatherMain = weatherMain;
        this.timestamp = timestamp;
        this.feelsLike = feelsLike;
    }

    // Getters and Setters
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDateTime() { return time; } // 别名方法，与WeatherDetailFragment兼容

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

    public String getWeatherMain() { return weatherMain; }
    public void setWeatherMain(String weatherMain) { this.weatherMain = weatherMain; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HourlyWeather that = (HourlyWeather) o;
        return Double.compare(that.temperature, temperature) == 0 &&
                humidity == that.humidity &&
                Double.compare(that.windSpeed, windSpeed) == 0 &&
                Double.compare(that.pressure, pressure) == 0 &&
                timestamp == that.timestamp &&
                Double.compare(that.feelsLike, feelsLike) == 0 &&
                Objects.equals(time, that.time) &&
                Objects.equals(description, that.description) &&
                Objects.equals(icon, that.icon) &&
                Objects.equals(weatherMain, that.weatherMain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, temperature, description, icon, humidity, 
                           windSpeed, pressure, weatherMain, timestamp, feelsLike);
    }
} 