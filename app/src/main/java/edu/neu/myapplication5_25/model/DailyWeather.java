package edu.neu.myapplication5_25.model;

import java.io.Serializable;
import java.util.Objects;

public class DailyWeather implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String date;
    private double maxTemp;
    private double minTemp;
    private double feelsLike;
    private String description;
    private String icon;
    private int humidity;
    private double windSpeed;
    private double pressure;
    private String weatherMain;
    private long timestamp;

    public DailyWeather() {}

    public DailyWeather(String date, double maxTemp, double minTemp, 
                       String description, String icon, int humidity, 
                       double windSpeed, double pressure, String weatherMain, long timestamp) {
        this.date = date;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.feelsLike = (maxTemp + minTemp) / 2; // 默认体感温度为平均温度
        this.description = description;
        this.icon = icon;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.weatherMain = weatherMain;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getMaxTemp() { return maxTemp; }
    public void setMaxTemp(double maxTemp) { this.maxTemp = maxTemp; }

    public double getMinTemp() { return minTemp; }
    public void setMinTemp(double minTemp) { this.minTemp = minTemp; }

    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyWeather that = (DailyWeather) o;
        return Double.compare(that.maxTemp, maxTemp) == 0 &&
                Double.compare(that.minTemp, minTemp) == 0 &&
                Double.compare(that.feelsLike, feelsLike) == 0 &&
                humidity == that.humidity &&
                Double.compare(that.windSpeed, windSpeed) == 0 &&
                Double.compare(that.pressure, pressure) == 0 &&
                timestamp == that.timestamp &&
                Objects.equals(date, that.date) &&
                Objects.equals(description, that.description) &&
                Objects.equals(icon, that.icon) &&
                Objects.equals(weatherMain, that.weatherMain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, maxTemp, minTemp, feelsLike, description, icon, 
                           humidity, windSpeed, pressure, weatherMain, timestamp);
    }
} 