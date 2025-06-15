package edu.neu.myapplication5_25.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.neu.myapplication5_25.MainActivity;
import edu.neu.myapplication5_25.R;

public class NotificationService {
    private static final String TAG = "NotificationService";
    
    // 通知渠道ID
    private static final String CHANNEL_WEATHER_ALERT = "weather_alert";
    private static final String CHANNEL_DIARY_REMINDER = "diary_reminder";
    private static final String CHANNEL_SCHEDULE_REMINDER = "schedule_reminder";
    private static final String CHANNEL_GENERAL = "general";
    
    // 通知ID
    private static final int NOTIFICATION_WEATHER_ALERT = 1001;
    private static final int NOTIFICATION_DIARY_REMINDER = 1002;
    private static final int NOTIFICATION_SCHEDULE_REMINDER = 1003;
    private static final int NOTIFICATION_GENERAL = 1004;
    
    private Context context;
    private NotificationManagerCompat notificationManager;
    
    public NotificationService(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannels();
    }
    
    /**
     * 创建通知渠道（Android 8.0+）
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null) return;
            
            // 天气预警渠道
            NotificationChannel weatherChannel = new NotificationChannel(
                CHANNEL_WEATHER_ALERT,
                "天气预警",
                NotificationManager.IMPORTANCE_HIGH
            );
            weatherChannel.setDescription("重要天气预警通知");
            weatherChannel.enableVibration(true);
            weatherChannel.enableLights(true);
            manager.createNotificationChannel(weatherChannel);
            
            // 日记提醒渠道
            NotificationChannel diaryChannel = new NotificationChannel(
                CHANNEL_DIARY_REMINDER,
                "日记提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            diaryChannel.setDescription("日记写作提醒");
            manager.createNotificationChannel(diaryChannel);
            
            // 日程提醒渠道
            NotificationChannel scheduleChannel = new NotificationChannel(
                CHANNEL_SCHEDULE_REMINDER,
                "日程提醒",
                NotificationManager.IMPORTANCE_HIGH
            );
            scheduleChannel.setDescription("重要日程提醒");
            scheduleChannel.enableVibration(true);
            manager.createNotificationChannel(scheduleChannel);
            
            // 一般通知渠道
            NotificationChannel generalChannel = new NotificationChannel(
                CHANNEL_GENERAL,
                "一般通知",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            generalChannel.setDescription("应用一般通知");
            manager.createNotificationChannel(generalChannel);
            
            Log.d(TAG, "通知渠道创建完成");
        }
    }
    
    /**
     * 发送天气预警通知
     * @param title 标题
     * @param message 内容
     * @param weatherType 天气类型
     */
    public void sendWeatherAlert(String title, String message, String weatherType) {
        if (!hasNotificationPermission()) {
            Log.w(TAG, "没有通知权限");
            return;
        }
        
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            int iconRes = getWeatherIcon(weatherType);
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_WEATHER_ALERT)
                .setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setLights(0xFFFF0000, 3000, 3000);
            
            notificationManager.notify(NOTIFICATION_WEATHER_ALERT, builder.build());
            Log.d(TAG, "天气预警通知已发送: " + title);
            
        } catch (Exception e) {
            Log.e(TAG, "发送天气预警通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送日记提醒通知
     * @param title 标题
     * @param message 内容
     */
    public void sendDiaryReminder(String title, String message) {
        if (!hasNotificationPermission()) {
            Log.w(TAG, "没有通知权限");
            return;
        }
        
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("fragment_target", "diary");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_DIARY_REMINDER)
                .setSmallIcon(R.drawable.ic_weather) // 可以换成日记图标
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
            
            notificationManager.notify(NOTIFICATION_DIARY_REMINDER, builder.build());
            Log.d(TAG, "日记提醒通知已发送: " + title);
            
        } catch (Exception e) {
            Log.e(TAG, "发送日记提醒通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送日程提醒通知
     * @param title 标题
     * @param message 内容
     * @param scheduleTime 日程时间
     */
    public void sendScheduleReminder(String title, String message, String scheduleTime) {
        if (!hasNotificationPermission()) {
            Log.w(TAG, "没有通知权限");
            return;
        }
        
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("fragment_target", "diary");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_SCHEDULE_REMINDER)
                .setSmallIcon(R.drawable.ic_weather) // 可以换成日程图标
                .setContentTitle(title)
                .setContentText(message)
                .setSubText("时间: " + scheduleTime)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 200, 500});
            
            notificationManager.notify(NOTIFICATION_SCHEDULE_REMINDER, builder.build());
            Log.d(TAG, "日程提醒通知已发送: " + title);
            
        } catch (Exception e) {
            Log.e(TAG, "发送日程提醒通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送一般通知
     * @param title 标题
     * @param message 内容
     */
    public void sendGeneralNotification(String title, String message) {
        if (!hasNotificationPermission()) {
            Log.w(TAG, "没有通知权限");
            return;
        }
        
        try {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_GENERAL)
                .setSmallIcon(R.drawable.ic_weather)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
            
            notificationManager.notify(NOTIFICATION_GENERAL, builder.build());
            Log.d(TAG, "一般通知已发送: " + title);
            
        } catch (Exception e) {
            Log.e(TAG, "发送一般通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据天气类型获取图标
     * @param weatherType 天气类型
     * @return 图标资源ID
     */
    private int getWeatherIcon(String weatherType) {
        if (weatherType == null) {
            return R.drawable.ic_weather;
        }
        
        switch (weatherType.toLowerCase()) {
            case "晴":
            case "clear":
                return R.drawable.ic_weather_sunny;
            case "多云":
            case "阴":
            case "clouds":
                return R.drawable.ic_weather_cloudy;
            case "雨":
            case "rain":
            case "drizzle":
                return R.drawable.ic_weather_rainy;
            case "雪":
            case "snow":
                return R.drawable.ic_weather_snowy;
            default:
                return R.drawable.ic_weather;
        }
    }
    
    /**
     * 检查是否有通知权限
     * @return 是否有权限
     */
    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) 
                == PackageManager.PERMISSION_GRANTED;
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }
    
    /**
     * 取消所有通知
     */
    public void cancelAllNotifications() {
        try {
            notificationManager.cancelAll();
            Log.d(TAG, "所有通知已取消");
        } catch (Exception e) {
            Log.e(TAG, "取消通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消特定类型的通知
     * @param notificationType 通知类型
     */
    public void cancelNotification(String notificationType) {
        try {
            int notificationId;
            switch (notificationType) {
                case "weather":
                    notificationId = NOTIFICATION_WEATHER_ALERT;
                    break;
                case "diary":
                    notificationId = NOTIFICATION_DIARY_REMINDER;
                    break;
                case "schedule":
                    notificationId = NOTIFICATION_SCHEDULE_REMINDER;
                    break;
                default:
                    notificationId = NOTIFICATION_GENERAL;
                    break;
            }
            
            notificationManager.cancel(notificationId);
            Log.d(TAG, "通知已取消: " + notificationType);
            
        } catch (Exception e) {
            Log.e(TAG, "取消通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送天气变化提醒
     * @param oldWeather 旧天气
     * @param newWeather 新天气
     * @param cityName 城市名
     */
    public void sendWeatherChangeAlert(String oldWeather, String newWeather, String cityName) {
        if (oldWeather == null || newWeather == null || oldWeather.equals(newWeather)) {
            return;
        }
        
        String title = cityName + " 天气变化提醒";
        String message = String.format("天气已从 %s 变为 %s，请注意添减衣物", oldWeather, newWeather);
        
        sendWeatherAlert(title, message, newWeather);
    }
    
    /**
     * 发送每日日记提醒
     */
    public void sendDailyDiaryReminder() {
        String title = "日记提醒";
        String message = "今天还没有写日记哦，记录一下今天的美好时光吧！";
        
        sendDiaryReminder(title, message);
    }
} 