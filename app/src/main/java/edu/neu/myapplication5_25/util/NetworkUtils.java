package edu.neu.myapplication5_25.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    
    /**
     * 检查网络连接是否可用
     * @param context 上下文
     * @return 是否有网络连接
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager == null) {
                return false;
            }
            
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            
        } catch (Exception e) {
            Log.e(TAG, "检查网络连接失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查是否连接到WiFi
     * @param context 上下文
     * @return 是否连接WiFi
     */
    public static boolean isWifiConnected(Context context) {
        if (context == null) {
            return false;
        }
        
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager == null) {
                return false;
            }
            
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiInfo != null && wifiInfo.isConnected();
            
        } catch (Exception e) {
            Log.e(TAG, "检查WiFi连接失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查是否连接到移动网络
     * @param context 上下文
     * @return 是否连接移动网络
     */
    public static boolean isMobileConnected(Context context) {
        if (context == null) {
            return false;
        }
        
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager == null) {
                return false;
            }
            
            NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return mobileInfo != null && mobileInfo.isConnected();
            
        } catch (Exception e) {
            Log.e(TAG, "检查移动网络连接失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取网络类型描述
     * @param context 上下文
     * @return 网络类型字符串
     */
    public static String getNetworkType(Context context) {
        if (context == null) {
            return "未知";
        }
        
        try {
            ConnectivityManager connectivityManager = 
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager == null) {
                return "未知";
            }
            
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null) {
                return "无网络";
            }
            
            switch (activeNetworkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return "WiFi";
                case ConnectivityManager.TYPE_MOBILE:
                    return "移动网络";
                case ConnectivityManager.TYPE_ETHERNET:
                    return "以太网";
                default:
                    return activeNetworkInfo.getTypeName();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "获取网络类型失败: " + e.getMessage());
            return "未知";
        }
    }
    
    /**
     * 测试网络连通性
     * @param testUrl 测试URL，默认使用谷歌DNS
     * @param timeoutMs 超时时间（毫秒）
     * @return 是否能够连通
     */
    public static boolean isInternetReachable(String testUrl, int timeoutMs) {
        if (testUrl == null || testUrl.isEmpty()) {
            testUrl = "https://8.8.8.8"; // 使用Google DNS作为默认测试地址
        }
        
        try {
            URL url = new URL(testUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "WeatherApp/1.0");
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            connection.connect();
            
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            
            return responseCode >= 200 && responseCode < 400;
            
        } catch (IOException e) {
            Log.w(TAG, "网络连通性测试失败: " + e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e(TAG, "网络连通性测试异常: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 测试网络连通性（使用默认超时时间）
     * @return 是否能够连通
     */
    public static boolean isInternetReachable() {
        return isInternetReachable("https://www.baidu.com", 5000); // 使用百度作为测试地址，5秒超时
    }
    
    /**
     * 获取网络状态描述信息
     * @param context 上下文
     * @return 网络状态描述
     */
    public static String getNetworkStatusDescription(Context context) {
        if (context == null) {
            return "无法获取网络状态";
        }
        
        if (!isNetworkAvailable(context)) {
            return "无网络连接";
        }
        
        String networkType = getNetworkType(context);
        if (isWifiConnected(context)) {
            return "WiFi连接正常";
        } else if (isMobileConnected(context)) {
            return "移动网络连接正常";
        } else {
            return networkType + "连接正常";
        }
    }
    
    /**
     * 获取网络连接建议
     * @param context 上下文
     * @return 网络连接建议
     */
    public static String getNetworkAdvice(Context context) {
        if (context == null) {
            return "无法分析网络状态";
        }
        
        if (!isNetworkAvailable(context)) {
            return "请检查网络连接，确保WiFi或移动数据已开启";
        }
        
        if (isWifiConnected(context)) {
            return "WiFi连接良好，适合获取天气数据";
        } else if (isMobileConnected(context)) {
            return "正在使用移动网络，建议在WiFi环境下使用以节省流量";
        } else {
            return "网络连接异常，请检查网络设置";
        }
    }
} 