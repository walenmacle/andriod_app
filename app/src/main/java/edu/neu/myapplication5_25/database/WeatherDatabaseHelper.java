package edu.neu.myapplication5_25.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.neu.myapplication5_25.model.WeatherData;

public class WeatherDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weather_app.db";
    private static final int DATABASE_VERSION = 1;

    // 天气数据表
    private static final String TABLE_WEATHER = "weather";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CITY_NAME = "city_name";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_TEMPERATURE = "temperature";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_ICON = "icon";
    private static final String COLUMN_HUMIDITY = "humidity";
    private static final String COLUMN_WIND_SPEED = "wind_speed";
    private static final String COLUMN_PRESSURE = "pressure";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_FEELS_LIKE = "feels_like";
    private static final String COLUMN_VISIBILITY = "visibility";
    private static final String COLUMN_WEATHER_MAIN = "weather_main";

    // 城市表
    private static final String TABLE_CITIES = "cities";
    private static final String COLUMN_CITY_ID = "city_id";
    private static final String COLUMN_CITY_NAME_CITIES = "city_name";
    private static final String COLUMN_COUNTRY_CITIES = "country";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_IS_FAVORITE = "is_favorite";

    // 日记表
    private static final String TABLE_DIARY = "diary";
    private static final String COLUMN_DIARY_ID = "diary_id";
    private static final String COLUMN_DIARY_TITLE = "title";
    private static final String COLUMN_DIARY_CONTENT = "content";
    private static final String COLUMN_DIARY_DATE = "date";
    private static final String COLUMN_DIARY_CREATED_AT = "created_at";
    private static final String COLUMN_DIARY_UPDATED_AT = "updated_at";

    // 日程表
    private static final String TABLE_SCHEDULE = "schedule";
    private static final String COLUMN_SCHEDULE_ID = "schedule_id";
    private static final String COLUMN_SCHEDULE_TITLE = "title";
    private static final String COLUMN_SCHEDULE_CONTENT = "content";
    private static final String COLUMN_SCHEDULE_DATE = "date";
    private static final String COLUMN_SCHEDULE_TIME = "time";
    private static final String COLUMN_SCHEDULE_IS_NOTIFIED = "is_notified";

    // 音乐播放列表表
    private static final String TABLE_PLAYLIST = "playlist";
    private static final String COLUMN_PLAYLIST_ID = "playlist_id";
    private static final String COLUMN_SONG_TITLE = "song_title";
    private static final String COLUMN_ARTIST = "artist";
    private static final String COLUMN_ALBUM = "album";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_IS_FAVORITE_SONG = "is_favorite";

    public WeatherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建天气数据表
        String createWeatherTable = "CREATE TABLE " + TABLE_WEATHER + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CITY_NAME + " TEXT,"
                + COLUMN_COUNTRY + " TEXT,"
                + COLUMN_TEMPERATURE + " REAL,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_ICON + " TEXT,"
                + COLUMN_HUMIDITY + " INTEGER,"
                + COLUMN_WIND_SPEED + " REAL,"
                + COLUMN_PRESSURE + " REAL,"
                + COLUMN_TIMESTAMP + " INTEGER,"
                + COLUMN_FEELS_LIKE + " REAL,"
                + COLUMN_VISIBILITY + " INTEGER,"
                + COLUMN_WEATHER_MAIN + " TEXT"
                + ")";
        db.execSQL(createWeatherTable);

        // 创建城市表
        String createCitiesTable = "CREATE TABLE " + TABLE_CITIES + "("
                + COLUMN_CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CITY_NAME_CITIES + " TEXT,"
                + COLUMN_COUNTRY_CITIES + " TEXT,"
                + COLUMN_LATITUDE + " REAL,"
                + COLUMN_LONGITUDE + " REAL,"
                + COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(createCitiesTable);

        // 创建日记表
        String createDiaryTable = "CREATE TABLE " + TABLE_DIARY + "("
                + COLUMN_DIARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DIARY_TITLE + " TEXT,"
                + COLUMN_DIARY_CONTENT + " TEXT,"
                + COLUMN_DIARY_DATE + " TEXT,"
                + COLUMN_DIARY_CREATED_AT + " INTEGER,"
                + COLUMN_DIARY_UPDATED_AT + " INTEGER"
                + ")";
        db.execSQL(createDiaryTable);

        // 创建日程表
        String createScheduleTable = "CREATE TABLE " + TABLE_SCHEDULE + "("
                + COLUMN_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SCHEDULE_TITLE + " TEXT,"
                + COLUMN_SCHEDULE_CONTENT + " TEXT,"
                + COLUMN_SCHEDULE_DATE + " TEXT,"
                + COLUMN_SCHEDULE_TIME + " TEXT,"
                + COLUMN_SCHEDULE_IS_NOTIFIED + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(createScheduleTable);

        // 创建音乐播放列表表
        String createPlaylistTable = "CREATE TABLE " + TABLE_PLAYLIST + "("
                + COLUMN_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SONG_TITLE + " TEXT,"
                + COLUMN_ARTIST + " TEXT,"
                + COLUMN_ALBUM + " TEXT,"
                + COLUMN_DURATION + " INTEGER,"
                + COLUMN_FILE_PATH + " TEXT,"
                + COLUMN_IS_FAVORITE_SONG + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(createPlaylistTable);

        // 插入一些默认城市
        insertDefaultCities(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        onCreate(db);
    }

    private void insertDefaultCities(SQLiteDatabase db) {
        String[] cities = {
                "北京,CN,39.9042,116.4074",
                "上海,CN,31.2304,121.4737",
                "广州,CN,23.1291,113.2644",
                "深圳,CN,22.5431,114.0579",
                "杭州,CN,30.2741,120.1551",
                "成都,CN,30.5728,104.0668",
                "西安,CN,34.3416,108.9398",
                "南京,CN,32.0603,118.7969",
                "武汉,CN,30.5928,114.3055",
                "天津,CN,39.3434,117.3616"
        };

        for (String cityInfo : cities) {
            String[] parts = cityInfo.split(",");
            ContentValues values = new ContentValues();
            values.put(COLUMN_CITY_NAME_CITIES, parts[0]);
            values.put(COLUMN_COUNTRY_CITIES, parts[1]);
            values.put(COLUMN_LATITUDE, Double.parseDouble(parts[2]));
            values.put(COLUMN_LONGITUDE, Double.parseDouble(parts[3]));
            values.put(COLUMN_IS_FAVORITE, 0);
            db.insert(TABLE_CITIES, null, values);
        }
    }

    // 天气数据操作方法
    public long insertWeatherData(WeatherData weatherData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_NAME, weatherData.getCityName());
        values.put(COLUMN_COUNTRY, weatherData.getCountry());
        values.put(COLUMN_TEMPERATURE, weatherData.getTemperature());
        values.put(COLUMN_DESCRIPTION, weatherData.getDescription());
        values.put(COLUMN_ICON, weatherData.getIcon());
        values.put(COLUMN_HUMIDITY, weatherData.getHumidity());
        values.put(COLUMN_WIND_SPEED, weatherData.getWindSpeed());
        values.put(COLUMN_PRESSURE, weatherData.getPressure());
        values.put(COLUMN_TIMESTAMP, weatherData.getTimestamp());
        values.put(COLUMN_FEELS_LIKE, weatherData.getFeelsLike());
        values.put(COLUMN_VISIBILITY, weatherData.getVisibility());
        values.put(COLUMN_WEATHER_MAIN, weatherData.getWeatherMain());

        long id = db.insert(TABLE_WEATHER, null, values);
        db.close();
        return id;
    }

    public WeatherData getLatestWeatherData(String cityName) {
        SQLiteDatabase db = this.getReadableDatabase();
        WeatherData weatherData = null;

        String selection = COLUMN_CITY_NAME + " = ?";
        String[] selectionArgs = {cityName};
        String orderBy = COLUMN_TIMESTAMP + " DESC";

        Cursor cursor = db.query(TABLE_WEATHER, null, selection, selectionArgs, null, null, orderBy, "1");

        if (cursor.moveToFirst()) {
            weatherData = new WeatherData();
            weatherData.setCityName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_NAME)));
            weatherData.setCountry(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY)));
            weatherData.setTemperature(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TEMPERATURE)));
            weatherData.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            weatherData.setIcon(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ICON)));
            weatherData.setHumidity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HUMIDITY)));
            weatherData.setWindSpeed(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WIND_SPEED)));
            weatherData.setPressure(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRESSURE)));
            weatherData.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
            weatherData.setFeelsLike(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FEELS_LIKE)));
            weatherData.setVisibility(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VISIBILITY)));
            weatherData.setWeatherMain(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEATHER_MAIN)));
        }

        cursor.close();
        db.close();
        return weatherData;
    }

    // 城市操作方法
    public List<String> getAllCities() {
        List<String> cities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CITIES, new String[]{COLUMN_CITY_NAME_CITIES}, 
                null, null, null, null, COLUMN_CITY_NAME_CITIES + " ASC");

        if (cursor.moveToFirst()) {
            do {
                cities.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_NAME_CITIES)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cities;
    }

    public void addFavoriteCity(String cityName, String country, double lat, double lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_NAME_CITIES, cityName);
        values.put(COLUMN_COUNTRY_CITIES, country);
        values.put(COLUMN_LATITUDE, lat);
        values.put(COLUMN_LONGITUDE, lon);
        values.put(COLUMN_IS_FAVORITE, 1);

        db.insert(TABLE_CITIES, null, values);
        db.close();
    }

    // 日记操作方法
    public long insertDiary(String title, String content, String date, String mood, String weather) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DIARY_TITLE, title);
        values.put(COLUMN_DIARY_CONTENT, content);
        values.put(COLUMN_DIARY_DATE, date);
        values.put(COLUMN_DIARY_CREATED_AT, System.currentTimeMillis());
        values.put(COLUMN_DIARY_UPDATED_AT, System.currentTimeMillis());
        // 添加心情和天气字段，如果数据库需要扩展
        
        long id = db.insert(TABLE_DIARY, null, values);
        db.close();
        return id;
    }

    public void updateDiary(long id, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DIARY_TITLE, title);
        values.put(COLUMN_DIARY_CONTENT, content);
        values.put(COLUMN_DIARY_UPDATED_AT, System.currentTimeMillis());

        db.update(TABLE_DIARY, values, COLUMN_DIARY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteDiary(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DIARY, COLUMN_DIARY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    
    public List<edu.neu.myapplication5_25.model.DiaryEntry> getAllDiaries() {
        List<edu.neu.myapplication5_25.model.DiaryEntry> diaries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String orderBy = COLUMN_DIARY_CREATED_AT + " DESC";
        Cursor cursor = db.query(TABLE_DIARY, null, null, null, null, null, orderBy);
        
        if (cursor.moveToFirst()) {
            do {
                edu.neu.myapplication5_25.model.DiaryEntry diary = new edu.neu.myapplication5_25.model.DiaryEntry();
                diary.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIARY_ID)));
                diary.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIARY_TITLE)));
                diary.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIARY_CONTENT)));
                diary.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DIARY_CREATED_AT)));
                
                // 设置默认值，因为旧的数据库可能没有这些字段
                diary.setMood("开心");
                diary.setWeather("晴");
                
                diaries.add(diary);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return diaries;
    }
    
    public List<edu.neu.myapplication5_25.model.DiaryEntry> searchDiaries(String keyword) {
        List<edu.neu.myapplication5_25.model.DiaryEntry> diaries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_DIARY_TITLE + " LIKE ? OR " + COLUMN_DIARY_CONTENT + " LIKE ?";
        String[] selectionArgs = {"%" + keyword + "%", "%" + keyword + "%"};
        String orderBy = COLUMN_DIARY_CREATED_AT + " DESC";
        
        Cursor cursor = db.query(TABLE_DIARY, null, selection, selectionArgs, null, null, orderBy);
        
        if (cursor.moveToFirst()) {
            do {
                edu.neu.myapplication5_25.model.DiaryEntry diary = new edu.neu.myapplication5_25.model.DiaryEntry();
                diary.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIARY_ID)));
                diary.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIARY_TITLE)));
                diary.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIARY_CONTENT)));
                diary.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DIARY_CREATED_AT)));
                
                // 设置默认值
                diary.setMood("开心");
                diary.setWeather("晴");
                
                diaries.add(diary);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return diaries;
    }
    
    public edu.neu.myapplication5_25.model.DiaryEntry getDiaryById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        edu.neu.myapplication5_25.model.DiaryEntry diary = null;
        
        String selection = COLUMN_DIARY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        
        Cursor cursor = db.query(TABLE_DIARY, null, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            diary = new edu.neu.myapplication5_25.model.DiaryEntry();
            diary.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIARY_ID)));
            diary.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIARY_TITLE)));
            diary.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIARY_CONTENT)));
            diary.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DIARY_CREATED_AT)));
            
            // 设置默认值
            diary.setMood("开心");
            diary.setWeather("晴");
        }
        
        cursor.close();
        db.close();
        return diary;
    }
    
    public int getDiaryCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_DIARY;
        Cursor cursor = db.rawQuery(countQuery, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        return count;
    }
    
    public int getThisMonthDiaryCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        
        // 获取本月开始时间戳
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        long monthStart = calendar.getTimeInMillis();
        
        String selection = COLUMN_DIARY_CREATED_AT + " >= ?";
        String[] selectionArgs = {String.valueOf(monthStart)};
        
        Cursor cursor = db.query(TABLE_DIARY, new String[]{"COUNT(*)"}, selection, selectionArgs, null, null, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        return count;
    }

    // 日程操作方法
    public long insertSchedule(String title, String content, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULE_TITLE, title);
        values.put(COLUMN_SCHEDULE_CONTENT, content);
        values.put(COLUMN_SCHEDULE_DATE, date);
        values.put(COLUMN_SCHEDULE_TIME, time);
        values.put(COLUMN_SCHEDULE_IS_NOTIFIED, 0);

        long id = db.insert(TABLE_SCHEDULE, null, values);
        db.close();
        return id;
    }
    
    public List<edu.neu.myapplication5_25.model.ScheduleEntry> getAllSchedules() {
        List<edu.neu.myapplication5_25.model.ScheduleEntry> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String orderBy = COLUMN_SCHEDULE_DATE + " ASC, " + COLUMN_SCHEDULE_TIME + " ASC";
        Cursor cursor = db.query(TABLE_SCHEDULE, null, null, null, null, null, orderBy);
        
        if (cursor.moveToFirst()) {
            do {
                edu.neu.myapplication5_25.model.ScheduleEntry schedule = new edu.neu.myapplication5_25.model.ScheduleEntry();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_ID)));
                schedule.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_TITLE)));
                schedule.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_CONTENT)));
                schedule.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_DATE)));
                schedule.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_TIME)));
                schedule.setNotified(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_IS_NOTIFIED)) == 1);
                
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return schedules;
    }
    
    public List<edu.neu.myapplication5_25.model.ScheduleEntry> getSchedulesByDate(String date) {
        List<edu.neu.myapplication5_25.model.ScheduleEntry> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_SCHEDULE_DATE + " = ?";
        String[] selectionArgs = {date};
        String orderBy = COLUMN_SCHEDULE_TIME + " ASC";
        
        Cursor cursor = db.query(TABLE_SCHEDULE, null, selection, selectionArgs, null, null, orderBy);
        
        if (cursor.moveToFirst()) {
            do {
                edu.neu.myapplication5_25.model.ScheduleEntry schedule = new edu.neu.myapplication5_25.model.ScheduleEntry();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_ID)));
                schedule.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_TITLE)));
                schedule.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_CONTENT)));
                schedule.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_DATE)));
                schedule.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_TIME)));
                schedule.setNotified(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_IS_NOTIFIED)) == 1);
                
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return schedules;
    }
    
    public List<edu.neu.myapplication5_25.model.ScheduleEntry> getUpcomingSchedules() {
        List<edu.neu.myapplication5_25.model.ScheduleEntry> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // 获取今天及以后的日程
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String today = sdf.format(new java.util.Date());
        
        String selection = COLUMN_SCHEDULE_DATE + " >= ?";
        String[] selectionArgs = {today};
        String orderBy = COLUMN_SCHEDULE_DATE + " ASC, " + COLUMN_SCHEDULE_TIME + " ASC";
        
        Cursor cursor = db.query(TABLE_SCHEDULE, null, selection, selectionArgs, null, null, orderBy);
        
        if (cursor.moveToFirst()) {
            do {
                edu.neu.myapplication5_25.model.ScheduleEntry schedule = new edu.neu.myapplication5_25.model.ScheduleEntry();
                schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_ID)));
                schedule.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_TITLE)));
                schedule.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_CONTENT)));
                schedule.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_DATE)));
                schedule.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_TIME)));
                schedule.setNotified(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULE_IS_NOTIFIED)) == 1);
                
                schedules.add(schedule);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return schedules;
    }
    
    public void updateSchedule(long id, String title, String content, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULE_TITLE, title);
        values.put(COLUMN_SCHEDULE_CONTENT, content);
        values.put(COLUMN_SCHEDULE_DATE, date);
        values.put(COLUMN_SCHEDULE_TIME, time);

        db.update(TABLE_SCHEDULE, values, COLUMN_SCHEDULE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    
    public void deleteSchedule(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCHEDULE, COLUMN_SCHEDULE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    
    public void markScheduleAsNotified(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULE_IS_NOTIFIED, 1);

        db.update(TABLE_SCHEDULE, values, COLUMN_SCHEDULE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    
    // 增强的日记查询方法
    public List<edu.neu.myapplication5_25.model.DiaryEntry> getDiariesByDateRange(long startTime, long endTime) {
        List<edu.neu.myapplication5_25.model.DiaryEntry> diaries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_DIARY_CREATED_AT + " >= ? AND " + COLUMN_DIARY_CREATED_AT + " <= ?";
        String[] selectionArgs = {String.valueOf(startTime), String.valueOf(endTime)};
        String orderBy = COLUMN_DIARY_CREATED_AT + " DESC";
        
        Cursor cursor = db.query(TABLE_DIARY, null, selection, selectionArgs, null, null, orderBy);
        
        if (cursor.moveToFirst()) {
            do {
                edu.neu.myapplication5_25.model.DiaryEntry diary = new edu.neu.myapplication5_25.model.DiaryEntry();
                diary.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIARY_ID)));
                diary.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIARY_TITLE)));
                diary.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIARY_CONTENT)));
                diary.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DIARY_CREATED_AT)));
                
                // 设置默认值
                diary.setMood("开心");
                diary.setWeather("晴");
                
                diaries.add(diary);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return diaries;
    }
    
    public List<edu.neu.myapplication5_25.model.DiaryEntry> getTodayDiaries() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        long startOfDay = calendar.getTimeInMillis();
        
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        long endOfDay = calendar.getTimeInMillis();
        
        return getDiariesByDateRange(startOfDay, endOfDay);
    }
    
    public List<edu.neu.myapplication5_25.model.DiaryEntry> getThisWeekDiaries() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        long startOfWeek = calendar.getTimeInMillis();
        
        calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        long endOfWeek = calendar.getTimeInMillis();
        
        return getDiariesByDateRange(startOfWeek, endOfWeek);
    }
    
    public List<edu.neu.myapplication5_25.model.DiaryEntry> getThisMonthDiariesList() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();
        
        calendar.add(java.util.Calendar.MONTH, 1);
        long endOfMonth = calendar.getTimeInMillis();
        
        return getDiariesByDateRange(startOfMonth, endOfMonth);
    }

    // 音乐播放列表操作方法
    public long insertSong(String title, String artist, String album, int duration, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SONG_TITLE, title);
        values.put(COLUMN_ARTIST, artist);
        values.put(COLUMN_ALBUM, album);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_FILE_PATH, filePath);
        values.put(COLUMN_IS_FAVORITE_SONG, 0);

        long id = db.insert(TABLE_PLAYLIST, null, values);
        db.close();
        return id;
    }

    public void clearOldWeatherData(long olderThan) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEATHER, COLUMN_TIMESTAMP + " < ?", new String[]{String.valueOf(olderThan)});
        db.close();
    }
} 