package edu.neu.myapplication5_25.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

public class ThemeManager {
    private static final String PREFS_NAME = "weather_app_settings";
    private static final String THEME_KEY = "theme_index";
    
    public static final int THEME_DEFAULT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_COLORFUL = 2;
    
    private static ThemeManager instance;
    private Context context;
    private SharedPreferences prefs;
    private int currentTheme;
    
    private ThemeManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.currentTheme = prefs.getInt(THEME_KEY, THEME_DEFAULT);
    }
    
    public static ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context);
        }
        return instance;
    }
    
    public void setTheme(int theme) {
        currentTheme = theme;
        prefs.edit().putInt(THEME_KEY, theme).apply();
    }
    
    public int getCurrentTheme() {
        return currentTheme;
    }
    
    public String getThemeName() {
        switch (currentTheme) {
            case THEME_DARK:
                return "深色主题";
            case THEME_COLORFUL:
                return "彩色主题";
            default:
                return "默认主题";
        }
    }
    
    public int getPrimaryColor() {
        switch (currentTheme) {
            case THEME_DARK:
                return 0xFF2E2E2E; // 深灰色
            case THEME_COLORFUL:
                return 0xFFE91E63; // 粉红色
            default:
                return 0xFF4A90E2; // 蓝色
        }
    }
    
    public int getBackgroundColor() {
        switch (currentTheme) {
            case THEME_DARK:
                return 0xFF121212; // 深黑色
            case THEME_COLORFUL:
                return 0xFFF3E5F5; // 浅紫色
            default:
                return 0xFFF5F7FA; // 浅灰色
        }
    }
    
    public int getCardBackgroundColor() {
        switch (currentTheme) {
            case THEME_DARK:
                return 0xFF1E1E1E; // 深灰色
            case THEME_COLORFUL:
                return 0xFFFFFFFF; // 白色
            default:
                return 0xFFFFFFFF; // 白色
        }
    }
    
    public int getTextPrimaryColor() {
        switch (currentTheme) {
            case THEME_DARK:
                return 0xFFFFFFFF; // 白色
            case THEME_COLORFUL:
                return 0xFF2C3E50; // 深蓝灰
            default:
                return 0xFF2C3E50; // 深蓝灰
        }
    }
    
    public int getTextSecondaryColor() {
        switch (currentTheme) {
            case THEME_DARK:
                return 0xFFBDBDBD; // 浅灰色
            case THEME_COLORFUL:
                return 0xFF7F8C8D; // 中等灰色
            default:
                return 0xFF7F8C8D; // 中等灰色
        }
    }
    
    public boolean isDarkTheme() {
        return currentTheme == THEME_DARK;
    }
    
    public boolean isColorfulTheme() {
        return currentTheme == THEME_COLORFUL;
    }
    
    public void applyTheme(android.app.Activity activity) {
        // 根据主题设置状态栏颜色
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(getPrimaryColor());
        }
    }
    
    public static boolean isSystemDarkMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
} 