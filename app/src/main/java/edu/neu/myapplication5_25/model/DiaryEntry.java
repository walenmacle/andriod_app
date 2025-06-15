package edu.neu.myapplication5_25.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryEntry {
    private int id;
    private String title;
    private String content;
    private long date;
    private String mood;
    private String weather;

    public DiaryEntry() {}

    public DiaryEntry(int id, String title, String content, long date, String mood, String weather) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.mood = mood;
        this.weather = weather;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        return sdf.format(new Date(date));
    }

    public String getShortFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.CHINA);
        return sdf.format(new Date(date));
    }
} 