package edu.neu.myapplication5_25.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.neu.myapplication5_25.model.DailyWeather;
import edu.neu.myapplication5_25.model.HourlyWeather;
import edu.neu.myapplication5_25.model.WeatherData;
import edu.neu.myapplication5_25.util.CityNameMapper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherService {
    private static final String TAG = "WeatherService";
    // 用户提供的OpenWeatherMap API密钥
    private static final String API_KEY = "4d654fc4d2ee4a219bc5ca3b018810ec";
    private static final String API_HOST = "https://api.openweathermap.org/data/2.5";
    private static final String GEO_API_HOST = "http://api.openweathermap.org/geo/1.0";
    
    private OkHttpClient client;
    private Gson gson;
    private ExecutorService executor;
    private Handler mainHandler;

    public WeatherService() {
        client = new OkHttpClient();
        gson = new Gson();
        executor = Executors.newFixedThreadPool(3);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface WeatherCallback {
        void onSuccess(WeatherData weatherData);
        void onError(String error);
    }

    public interface DailyWeatherCallback {
        void onSuccess(List<DailyWeather> dailyWeatherList);
        void onError(String error);
    }

    public interface HourlyWeatherCallback {
        void onSuccess(List<HourlyWeather> hourlyWeatherList);
        void onError(String error);
    }

    public interface LocationCallback {
        void onSuccess(String locationId);
        void onError(String error);
    }

    // 根据城市名称获取坐标
    public void getLocationCoordinates(String cityName, LocationCallback callback) {
        // 将中文城市名转换为英文城市名
        String englishCityName = CityNameMapper.toEnglish(cityName);
        String url = GEO_API_HOST + "/direct?q=" + englishCityName + "&limit=1&appid=" + API_KEY;
        
        Log.d(TAG, "请求城市坐标: " + url + " (原始城市名: " + cityName + ", 英文城市名: " + englishCityName + ")");
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "获取城市坐标失败", e);
                mainHandler.post(() -> callback.onError("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "城市坐标响应: " + responseBody);
                    
                    executor.execute(() -> {
                        try {
                            JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();
                            
                            if (jsonArray.size() > 0) {
                                JsonObject location = jsonArray.get(0).getAsJsonObject();
                                double lat = location.get("lat").getAsDouble();
                                double lon = location.get("lon").getAsDouble();
                                String coordinates = lat + "," + lon;
                                Log.d(TAG, "获取到坐标: " + coordinates);
                                mainHandler.post(() -> callback.onSuccess(coordinates));
                            } else {
                                Log.w(TAG, "未找到城市信息: " + englishCityName);
                                mainHandler.post(() -> callback.onError("未找到城市信息"));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "解析城市坐标失败", e);
                            mainHandler.post(() -> callback.onError("数据解析失败: " + e.getMessage()));
                        }
                    });
                } else {
                    Log.e(TAG, "API请求失败: " + response.code() + " " + response.message());
                    final String errorMsg;
                    if (response.code() == 401) {
                        errorMsg = "API密钥无效，请检查密钥是否正确";
                    } else if (response.code() == 429) {
                        errorMsg = "API调用次数超限，请稍后再试";
                    } else {
                        errorMsg = "API请求失败: " + response.code();
                    }
                    mainHandler.post(() -> callback.onError(errorMsg));
                }
            }
        });
    }

    // 获取当前天气
    public void getCurrentWeather(String cityName, WeatherCallback callback) {
        Log.d(TAG, "开始获取天气数据，城市: " + cityName);
        
        // 如果是坐标格式（包含逗号），直接使用
        if (cityName.contains(",")) {
            getCurrentWeatherByCoordinates(cityName, callback);
        } else {
            // 否则先获取坐标
            getLocationCoordinates(cityName, new LocationCallback() {
                @Override
                public void onSuccess(String coordinates) {
                    Log.d(TAG, "获取坐标成功，开始获取天气数据");
                    getCurrentWeatherByCoordinates(coordinates, callback);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "获取坐标失败: " + error);
                    callback.onError(error);
                }
            });
        }
    }

    // 根据坐标获取当前天气
    private void getCurrentWeatherByCoordinates(String coordinates, WeatherCallback callback) {
        String[] parts = coordinates.split(",");
        if (parts.length != 2) {
            callback.onError("坐标格式错误");
            return;
        }
        
        String lat = parts[0].trim();
        String lon = parts[1].trim();
        String url = API_HOST + "/weather?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric&lang=zh_cn";
        
        Log.d(TAG, "请求当前天气: " + url);
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "获取天气失败", e);
                mainHandler.post(() -> callback.onError("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "天气响应: " + responseBody);
                    
                    executor.execute(() -> {
                        try {
                            WeatherData weatherData = parseCurrentWeather(responseBody);
                            mainHandler.post(() -> callback.onSuccess(weatherData));
                        } catch (Exception e) {
                            Log.e(TAG, "解析天气数据失败", e);
                            mainHandler.post(() -> callback.onError("数据解析失败: " + e.getMessage()));
                        }
                    });
                } else {
                    Log.e(TAG, "API请求失败: " + response.code());
                    final String errorMsg;
                    if (response.code() == 401) {
                        errorMsg = "API密钥无效";
                    } else {
                        errorMsg = "API请求失败: " + response.code();
                    }
                    mainHandler.post(() -> callback.onError(errorMsg));
                }
            }
        });
    }

    // 根据经纬度获取当前天气
    public void getCurrentWeatherByCoordinates(double lat, double lon, WeatherCallback callback) {
        String coordinates = lat + "," + lon;
        getCurrentWeatherByCoordinates(coordinates, callback);
    }

    // 获取7天天气预报
    public void getFiveDayForecast(String cityName, DailyWeatherCallback callback) {
        if (cityName.contains(",")) {
            getDailyForecastByCoordinates(cityName, callback);
        } else {
            getLocationCoordinates(cityName, new LocationCallback() {
                @Override
                public void onSuccess(String coordinates) {
                    getDailyForecastByCoordinates(coordinates, callback);
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        }
    }

    private void getDailyForecastByCoordinates(String coordinates, DailyWeatherCallback callback) {
        String[] parts = coordinates.split(",");
        if (parts.length != 2) {
            callback.onError("坐标格式错误");
            return;
        }
        
        String lat = parts[0].trim();
        String lon = parts[1].trim();
        String url = API_HOST + "/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric&lang=zh_cn";
        
        Log.d(TAG, "请求天气预报: " + url);
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "获取预报失败", e);
                mainHandler.post(() -> callback.onError("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "预报响应: " + responseBody);
                    
                    executor.execute(() -> {
                        try {
                            List<DailyWeather> dailyWeatherList = parseDailyWeather(responseBody);
                            mainHandler.post(() -> callback.onSuccess(dailyWeatherList));
                        } catch (Exception e) {
                            Log.e(TAG, "解析预报数据失败", e);
                            mainHandler.post(() -> callback.onError("数据解析失败: " + e.getMessage()));
                        }
                    });
                } else {
                    Log.e(TAG, "API请求失败: " + response.code());
                    final String errorMsg;
                    if (response.code() == 401) {
                        errorMsg = "API密钥无效";
                    } else {
                        errorMsg = "API请求失败: " + response.code();
                    }
                    mainHandler.post(() -> callback.onError(errorMsg));
                }
            }
        });
    }

    // 获取24小时逐时天气预报
    public void getHourlyForecast(String cityName, HourlyWeatherCallback callback) {
        if (cityName.contains(",")) {
            getHourlyForecastByCoordinates(cityName, callback);
        } else {
            getLocationCoordinates(cityName, new LocationCallback() {
                @Override
                public void onSuccess(String coordinates) {
                    getHourlyForecastByCoordinates(coordinates, callback);
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        }
    }

    private void getHourlyForecastByCoordinates(String coordinates, HourlyWeatherCallback callback) {
        String[] parts = coordinates.split(",");
        if (parts.length != 2) {
            callback.onError("坐标格式错误");
            return;
        }
        
        String lat = parts[0].trim();
        String lon = parts[1].trim();
        String url = API_HOST + "/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric&lang=zh_cn";
        
        Log.d(TAG, "请求逐时预报: " + url);
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "获取逐时预报失败", e);
                mainHandler.post(() -> callback.onError("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "逐时预报响应: " + responseBody);
                    
                    executor.execute(() -> {
                        try {
                            List<HourlyWeather> hourlyWeatherList = parseHourlyWeather(responseBody);
                            mainHandler.post(() -> callback.onSuccess(hourlyWeatherList));
                        } catch (Exception e) {
                            Log.e(TAG, "解析逐时预报数据失败", e);
                            mainHandler.post(() -> callback.onError("数据解析失败: " + e.getMessage()));
                        }
                    });
                } else {
                    Log.e(TAG, "API请求失败: " + response.code());
                    final String errorMsg;
                    if (response.code() == 401) {
                        errorMsg = "API密钥无效";
                    } else {
                        errorMsg = "API请求失败: " + response.code();
                    }
                    mainHandler.post(() -> callback.onError(errorMsg));
                }
            }
        });
    }

    private WeatherData parseCurrentWeather(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        
        // 检查是否有错误代码
        if (jsonObject.has("cod")) {
            int cod = jsonObject.get("cod").getAsInt();
            if (cod != 200) {
                String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "未知错误";
                throw new RuntimeException("API返回错误: " + cod + " - " + message);
            }
        }
        
        JsonObject main = jsonObject.getAsJsonObject("main");
        JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
        JsonObject weather = weatherArray.get(0).getAsJsonObject();
        JsonObject wind = jsonObject.has("wind") ? jsonObject.getAsJsonObject("wind") : null;
        
        WeatherData weatherData = new WeatherData();
        weatherData.setTemperature(main.get("temp").getAsDouble());
        weatherData.setFeelsLike(main.get("feels_like").getAsDouble());
        weatherData.setDescription(weather.get("description").getAsString());
        weatherData.setWeatherMain(weather.get("main").getAsString());
        weatherData.setIcon(weather.get("icon").getAsString());
        weatherData.setHumidity(main.get("humidity").getAsInt());
        weatherData.setPressure(main.get("pressure").getAsDouble());
        
        if (wind != null && wind.has("speed")) {
            weatherData.setWindSpeed(wind.get("speed").getAsDouble());
        }
        
        if (jsonObject.has("visibility")) {
            weatherData.setVisibility(jsonObject.get("visibility").getAsInt());
        }
        
        if (jsonObject.has("name")) {
            weatherData.setCityName(jsonObject.get("name").getAsString());
        }
        
        if (jsonObject.has("sys")) {
            JsonObject sys = jsonObject.getAsJsonObject("sys");
            if (sys.has("country")) {
                weatherData.setCountry(sys.get("country").getAsString());
            }
        }
        
        weatherData.setTimestamp(System.currentTimeMillis());
        
        return weatherData;
    }

    private List<DailyWeather> parseDailyWeather(String jsonResponse) {
        List<DailyWeather> dailyWeatherList = new ArrayList<>();
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        
        if (jsonObject.has("cod")) {
            int cod = jsonObject.get("cod").getAsInt();
            if (cod != 200) {
                String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "未知错误";
                throw new RuntimeException("API返回错误: " + cod + " - " + message);
            }
        }
        
        JsonArray list = jsonObject.getAsJsonArray("list");
        
        // 按天分组数据
        String currentDate = "";
        DailyWeather currentDayWeather = null;
        
        for (int i = 0; i < list.size(); i++) {
            JsonObject item = list.get(i).getAsJsonObject();
            
            String dateTime = item.get("dt_txt").getAsString();
            String date = dateTime.split(" ")[0];
            
            if (!date.equals(currentDate)) {
                if (currentDayWeather != null) {
                    dailyWeatherList.add(currentDayWeather);
                }
                
                currentDate = date;
                currentDayWeather = new DailyWeather();
                
                JsonObject main = item.getAsJsonObject("main");
                JsonArray weatherArray = item.getAsJsonArray("weather");
                JsonObject weather = weatherArray.get(0).getAsJsonObject();
                JsonObject wind = item.has("wind") ? item.getAsJsonObject("wind") : null;
                
                currentDayWeather.setDate(date);
                currentDayWeather.setMaxTemp(main.get("temp_max").getAsDouble());
                currentDayWeather.setMinTemp(main.get("temp_min").getAsDouble());
                currentDayWeather.setDescription(weather.get("description").getAsString());
                currentDayWeather.setWeatherMain(weather.get("main").getAsString());
                currentDayWeather.setIcon(weather.get("icon").getAsString());
                currentDayWeather.setHumidity(main.get("humidity").getAsInt());
                currentDayWeather.setPressure(main.get("pressure").getAsDouble());
                
                if (wind != null && wind.has("speed")) {
                    currentDayWeather.setWindSpeed(wind.get("speed").getAsDouble());
                }
                
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date parsedDate = sdf.parse(date);
                    if (parsedDate != null) {
                        currentDayWeather.setTimestamp(parsedDate.getTime());
                    }
                } catch (Exception e) {
                    currentDayWeather.setTimestamp(System.currentTimeMillis());
                }
            } else if (currentDayWeather != null) {
                // 更新当天的最高最低温度
                JsonObject main = item.getAsJsonObject("main");
                double tempMax = main.get("temp_max").getAsDouble();
                double tempMin = main.get("temp_min").getAsDouble();
                
                if (tempMax > currentDayWeather.getMaxTemp()) {
                    currentDayWeather.setMaxTemp(tempMax);
                }
                if (tempMin < currentDayWeather.getMinTemp()) {
                    currentDayWeather.setMinTemp(tempMin);
                }
            }
        }
        
        if (currentDayWeather != null) {
            dailyWeatherList.add(currentDayWeather);
        }
        
        return dailyWeatherList;
    }

    private List<HourlyWeather> parseHourlyWeather(String jsonResponse) {
        List<HourlyWeather> hourlyWeatherList = new ArrayList<>();
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        
        if (jsonObject.has("cod")) {
            int cod = jsonObject.get("cod").getAsInt();
            if (cod != 200) {
                String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "未知错误";
                throw new RuntimeException("API返回错误: " + cod + " - " + message);
            }
        }
        
        JsonArray list = jsonObject.getAsJsonArray("list");
        
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        for (int i = 0; i < Math.min(list.size(), 24); i++) {
            JsonObject item = list.get(i).getAsJsonObject();
            
            HourlyWeather hourlyWeather = new HourlyWeather();
            
            try {
                String dateTime = item.get("dt_txt").getAsString();
                Date date = inputFormat.parse(dateTime);
                if (date != null) {
                    hourlyWeather.setTime(outputFormat.format(date));
                    hourlyWeather.setTimestamp(date.getTime());
                } else {
                    hourlyWeather.setTime("--:--");
                    hourlyWeather.setTimestamp(System.currentTimeMillis());
                }
            } catch (Exception e) {
                hourlyWeather.setTime("--:--");
                hourlyWeather.setTimestamp(System.currentTimeMillis());
            }
            
            JsonObject main = item.getAsJsonObject("main");
            JsonArray weatherArray = item.getAsJsonArray("weather");
            JsonObject weather = weatherArray.get(0).getAsJsonObject();
            JsonObject wind = item.has("wind") ? item.getAsJsonObject("wind") : null;
            
            hourlyWeather.setTemperature(main.get("temp").getAsDouble());
            hourlyWeather.setFeelsLike(main.get("feels_like").getAsDouble());
            hourlyWeather.setDescription(weather.get("description").getAsString());
            hourlyWeather.setWeatherMain(weather.get("main").getAsString());
            hourlyWeather.setIcon(weather.get("icon").getAsString());
            hourlyWeather.setHumidity(main.get("humidity").getAsInt());
            hourlyWeather.setPressure(main.get("pressure").getAsDouble());
            
            if (wind != null && wind.has("speed")) {
                hourlyWeather.setWindSpeed(wind.get("speed").getAsDouble());
            }
            
            hourlyWeatherList.add(hourlyWeather);
        }
        
        return hourlyWeatherList;
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }
} 