package edu.neu.myapplication5_25.model;

public class MusicItem {
    private int id;
    private String title;
    private String artist;
    private String filePath;
    private long duration;
    private boolean isPlaying;

    public MusicItem() {}

    public MusicItem(int id, String title, String artist, String filePath, long duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.filePath = filePath;
        this.duration = duration;
        this.isPlaying = false;
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getFormattedDuration() {
        long minutes = duration / 60000;
        long seconds = (duration % 60000) / 1000;
        return String.format("%d:%02d", minutes, seconds);
    }
} 