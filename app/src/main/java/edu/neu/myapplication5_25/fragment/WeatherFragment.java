package edu.neu.myapplication5_25.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.neu.myapplication5_25.MainActivity;
import edu.neu.myapplication5_25.R;
import edu.neu.myapplication5_25.databinding.FragmentWeatherBinding;
import edu.neu.myapplication5_25.model.WeatherData;
import edu.neu.myapplication5_25.service.WeatherService;
import edu.neu.myapplication5_25.util.CityNameMapper;
import edu.neu.myapplication5_25.util.NetworkUtils;
import com.google.gson.Gson;

public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int LOCATION_TIMEOUT = 10000;
    private static final int LOCATION_UPDATE_INTERVAL = 5000;
    private static final int LOCATION_FASTEST_INTERVAL = 2000;
    private static final int WEATHER_CACHE_DURATION = 30 * 60 * 1000; // 30分钟缓存
    private static final double MAX_CITY_DISTANCE = 100.0; // 最大城市距离（公里）
    
    private FragmentWeatherBinding binding;
    private WeatherService weatherService;
    private FusedLocationProviderClient fusedLocationClient;
    private String currentCity = "沈阳";
    private Handler locationTimeoutHandler;
    private Runnable locationTimeoutRunnable;
    private long lastWeatherUpdateTime = 0;
    private boolean isLocationUpdating = false;
    private LocationCallback locationCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        Log.d(TAG, "WeatherFragment onViewCreated");
        
        // 获取天气服务
        if (getActivity() instanceof MainActivity) {
            weatherService = ((MainActivity) getActivity()).getWeatherService();
            Log.d(TAG, "WeatherService获取成功: " + (weatherService != null));
        } else {
            Log.e(TAG, "无法获取MainActivity实例");
        }
        
        // 初始化位置服务
        if (checkGooglePlayServices()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            locationTimeoutHandler = new Handler(Looper.getMainLooper());
            
            // 提前初始化位置回调，避免null pointer异常
            initLocationCallback();
        } else {
            Log.e(TAG, "Google Play Services不可用");
            Toast.makeText(getContext(), "定位服务不可用，请检查Google Play Services", Toast.LENGTH_LONG).show();
        }
        
        // 读取保存的城市选择
        loadSelectedCity();
        
        // 设置点击事件
        setupClickListeners();
        
        // 显示当前城市
        binding.tvCityName.setText(currentCity);
        
        // 加载天气数据
        loadWeatherData();
    }
    
    /**
     * 初始化位置回调
     */
    private void initLocationCallback() {
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    // 取消超时
                    if (locationTimeoutHandler != null && locationTimeoutRunnable != null) {
                        locationTimeoutHandler.removeCallbacks(locationTimeoutRunnable);
                    }
                    
                    // 处理位置信息
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "获取新位置成功: " + location.getLatitude() + ", " + location.getLongitude());
                        handleLocationUpdate(location);
                    } else {
                        Log.w(TAG, "新位置数据仍为null");
                        handleLocationError("无法获取位置信息，请检查GPS是否开启");
                    }
                    
                    // 清理资源
                    stopLocationUpdates();
                }
            };
        }
    }
    
    /**
     * 停止位置更新，释放资源
     */
    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        isLocationUpdating = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次页面恢复时检查是否有新选择的城市
        String previousCity = currentCity;
        loadSelectedCity();
        
        // 如果城市发生变化，重新加载天气数据
        if (!previousCity.equals(currentCity)) {
            Log.d(TAG, "城市已变更，从 " + previousCity + " 到 " + currentCity);
            binding.tvCityName.setText(currentCity);
            loadWeatherData();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // 确保暂停时停止位置更新
        stopLocationUpdates();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 清理位置相关资源
        stopLocationUpdates();
        if (locationTimeoutHandler != null) {
            locationTimeoutHandler.removeCallbacksAndMessages(null);
        }
        binding = null;
    }

    private void getCurrentLocation() {
        if (isLocationUpdating) {
            Log.d(TAG, "定位更新正在进行中");
            Toast.makeText(getContext(), "正在获取位置，请稍候...", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "开始获取当前位置");
        
        if (!checkGooglePlayServices()) {
            Toast.makeText(getContext(), "定位服务不可用，请检查Google Play Services", Toast.LENGTH_LONG).show();
            return;
        }
        
        // 确保GPS已开启
        if (!isLocationEnabled()) {
            Toast.makeText(getContext(), "请开启位置服务(GPS)", Toast.LENGTH_LONG).show();
            // 可以提供跳转到位置设置界面的选项
            showLocationSettingsDialog();
            return;
        }
        
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "没有位置权限");
            Toast.makeText(getContext(), "需要位置权限才能获取当前位置", Toast.LENGTH_SHORT).show();
            requestLocationPermission();
            return;
        }

        isLocationUpdating = true;
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutWeatherContent.setVisibility(View.GONE);
        binding.tvWeatherDescription.setText("正在获取位置信息...");
        
        // 确保回调已初始化
        initLocationCallback();

        // 设置超时处理
        locationTimeoutRunnable = () -> {
            Log.w(TAG, "定位超时");
            isLocationUpdating = false;
            binding.progressBar.setVisibility(View.GONE);
            binding.layoutWeatherContent.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "定位超时，请检查GPS是否开启", Toast.LENGTH_SHORT).show();
            
            // 超时后恢复显示当前城市的天气
            WeatherData cachedData = loadWeatherCache();
            if (cachedData != null) {
                updateWeatherUI(cachedData);
            } else {
                showDefaultWeatherData();
            }
        };
        locationTimeoutHandler.postDelayed(locationTimeoutRunnable, LOCATION_TIMEOUT);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null && location.getTime() > System.currentTimeMillis() - 60000) { // 只使用1分钟内的缓存位置
                        Log.d(TAG, "获取到最近的位置: " + location.getLatitude() + ", " + location.getLongitude() + ", 时间: " + new Date(location.getTime()));
                        locationTimeoutHandler.removeCallbacks(locationTimeoutRunnable);
                        handleLocationUpdate(location);
                        isLocationUpdating = false;
                    } else {
                        Log.w(TAG, "位置为null或过期，尝试请求新的位置");
                        requestNewLocationData();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "定位失败", e);
                    locationTimeoutHandler.removeCallbacks(locationTimeoutRunnable);
                    isLocationUpdating = false;
                    handleLocationError("定位失败: " + e.getMessage());
                });
    }
    
    /**
     * 判断位置服务是否开启
     */
    private boolean isLocationEnabled() {
        if (getContext() != null) {
            android.location.LocationManager locationManager = 
                (android.location.LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            return locationManager != null && 
                   (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) || 
                    locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER));
        }
        return false;
    }
    
    /**
     * 显示位置设置对话框
     */
    private void showLocationSettingsDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("位置服务未开启")
            .setMessage("请开启位置服务(GPS)以获取准确的天气信息")
            .setPositiveButton("去设置", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            })
            .setNegativeButton("取消", null)
            .create()
            .show();
    }

    private void handleLocationUpdate(Location location) {
        String nearestCity = findNearestCity(location.getLatitude(), location.getLongitude());
        
        if (nearestCity != null) {
            Log.d(TAG, "找到最近的城市: " + nearestCity);
            updateCityAndWeather(nearestCity);
        } else {
            Log.w(TAG, "未找到附近的城市，使用坐标获取天气");
            loadWeatherByLocation(location.getLatitude(), location.getLongitude());
        }
    }

    private void handleLocationError(String errorMessage) {
        binding.progressBar.setVisibility(View.GONE);
        binding.layoutWeatherContent.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        
        // 错误时尝试加载缓存的天气数据
        WeatherData cachedData = loadWeatherCache();
        if (cachedData != null) {
            updateWeatherUI(cachedData);
        }
    }

    private void requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("需要位置权限")
                    .setMessage("为了获取您所在城市的天气信息，我们需要访问您的位置。")
                    .setPositiveButton("确定", (dialog, which) -> {
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, LOCATION_PERMISSION_REQUEST_CODE);
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void requestNewLocationData() {
        try {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
            locationRequest.setFastestInterval(LOCATION_FASTEST_INTERVAL);
            locationRequest.setNumUpdates(1);
            locationRequest.setExpirationDuration(LOCATION_TIMEOUT);

            if (ActivityCompat.checkSelfPermission(requireContext(), 
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), 
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            // 确保回调已初始化
            initLocationCallback();
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            
        } catch (Exception e) {
            Log.e(TAG, "请求新位置数据失败: " + e.getMessage());
            locationTimeoutHandler.removeCallbacks(locationTimeoutRunnable);
            isLocationUpdating = false;
            handleLocationError("请求位置更新失败: " + e.getMessage());
        }
    }

    private void loadSelectedCity() {
        try {
            if (getContext() != null) {
                SharedPreferences prefs = getContext().getSharedPreferences("weather_prefs", 0);
                String selectedCity = prefs.getString("selected_city", "沈阳");
                if (selectedCity != null && !selectedCity.isEmpty()) {
                    currentCity = selectedCity;
                    Log.d(TAG, "加载选中的城市: " + currentCity);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "加载选中城市失败: " + e.getMessage());
        }
    }

    private void setupClickListeners() {
        // 城市选择点击事件
        binding.layoutCitySelector.setOnClickListener(v -> {
            try {
                // 导航到城市选择页面
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.citySelectionFragment);
                Log.d(TAG, "导航到城市选择页面");
            } catch (Exception e) {
                Log.e(TAG, "导航到城市选择页面失败: " + e.getMessage());
                // 如果导航失败，显示支持的城市列表
                Toast.makeText(getContext(), "城市选择功能，当前支持: " + String.join(", ", 
                    java.util.Arrays.copyOf(CityNameMapper.getSupportedChineseCities(), 10)), Toast.LENGTH_LONG).show();
            }
        });

        // 定位按钮点击事件
        binding.btnLocation.setOnClickListener(v -> {
            Log.d(TAG, "定位按钮被点击");
            getCurrentLocation();
        });

        // 刷新按钮点击事件
        binding.btnRefresh.setOnClickListener(v -> {
            Log.d(TAG, "刷新按钮被点击");
            loadWeatherData();
        });
    }

    private void loadWeatherData() {
        if (weatherService == null) {
            Log.e(TAG, "WeatherService为null");
            return;
        }

        // 检查是否需要更新天气数据
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastWeatherUpdateTime < WEATHER_CACHE_DURATION) {
            Log.d(TAG, "使用缓存的天气数据");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutWeatherContent.setVisibility(View.GONE);
        binding.tvWeatherDescription.setText("正在获取天气数据...");

        weatherService.getCurrentWeather(currentCity, new WeatherService.WeatherCallback() {
            @Override
            public void onSuccess(WeatherData weatherData) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        lastWeatherUpdateTime = System.currentTimeMillis();
                        updateWeatherUI(weatherData);
                        binding.progressBar.setVisibility(View.GONE);
                        binding.layoutWeatherContent.setVisibility(View.VISIBLE);
                        
                        // 保存天气数据到缓存
                        saveWeatherCache(weatherData);
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "获取天气数据失败: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 尝试从缓存加载数据
                        WeatherData cachedData = loadWeatherCache();
                        if (cachedData != null) {
                            updateWeatherUI(cachedData);
                            Toast.makeText(getContext(), "使用缓存的天气数据", Toast.LENGTH_SHORT).show();
                        } else {
                            showDefaultWeatherData();
                            Toast.makeText(getContext(), "获取天气失败: " + error, Toast.LENGTH_LONG).show();
                        }
                        binding.progressBar.setVisibility(View.GONE);
                        binding.layoutWeatherContent.setVisibility(View.VISIBLE);
                    });
                }
            }
        });
    }

    private void updateWeatherUI(WeatherData weatherData) {
        Log.d(TAG, "更新UI，天气数据: " + weatherData.getCityName() + ", " + weatherData.getTemperature() + "°C");
        
        // 更新城市名称 - 优先使用当前选择的中文城市名
        String displayCityName = currentCity; // 使用我们保存的中文城市名
        if (weatherData.getCityName() != null && !weatherData.getCityName().isEmpty()) {
            // 如果weatherData中有城市名，但可能是英文的，我们仍然使用中文名
            Log.d(TAG, "天气数据中的城市名: " + weatherData.getCityName());
        }
        binding.tvCityName.setText(displayCityName);
        
        // 更新温度
        binding.tvTemperature.setText(String.format(Locale.getDefault(), "%.0f°", weatherData.getTemperature()));
        
        // 更新天气描述
        binding.tvWeatherDescription.setText(weatherData.getDescription() != null ? weatherData.getDescription() : "天气晴朗");
        
        // 更新体感温度
        binding.tvFeelsLike.setText(String.format(Locale.getDefault(), "体感 %.0f°", weatherData.getFeelsLike()));
        
        // 更新湿度
        binding.tvHumidity.setText(String.format(Locale.getDefault(), "%d%%", weatherData.getHumidity()));
        
        // 更新风速
        binding.tvWindSpeed.setText(String.format(Locale.getDefault(), "%.1f m/s", weatherData.getWindSpeed()));
        
        // 更新气压
        binding.tvPressure.setText(String.format(Locale.getDefault(), "%.0f hPa", weatherData.getPressure()));
        
        // 更新能见度
        if (weatherData.getVisibility() > 0) {
            binding.tvVisibility.setText(String.format(Locale.getDefault(), "%.1f km", weatherData.getVisibility() / 1000.0));
        } else {
            binding.tvVisibility.setText("良好");
        }
        
        // 更新时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        binding.tvUpdateTime.setText("更新时间: " + sdf.format(new Date()));
        
        // 根据天气状况设置背景色
        updateWeatherBackground(weatherData.getWeatherMain() != null ? weatherData.getWeatherMain() : "Clear");
        
        Log.d(TAG, "UI更新完成，显示城市: " + displayCityName);
    }

    private void updateWeatherBackground(String weatherMain) {
        Log.d(TAG, "更新背景颜色，天气类型: " + weatherMain);
        
        int backgroundColor;
        switch (weatherMain.toLowerCase()) {
            case "clear":
                backgroundColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.sunny_color);
                break;
            case "clouds":
                backgroundColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.cloudy_color);
                break;
            case "rain":
            case "drizzle":
                backgroundColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.rainy_color);
                break;
            case "snow":
                backgroundColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.snowy_color);
                break;
            default:
                backgroundColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.primary_blue);
                break;
        }
        
        binding.layoutWeatherHeader.setBackgroundColor(backgroundColor);
    }

    private void showDefaultWeatherData() {
        Log.d(TAG, "显示默认天气数据");
        
        binding.tvCityName.setText(currentCity);
        binding.tvTemperature.setText("--°");
        binding.tvWeatherDescription.setText("无法获取天气数据");
        binding.tvFeelsLike.setText("体感 --°");
        binding.tvHumidity.setText("--%");
        binding.tvWindSpeed.setText("-- m/s");
        binding.tvPressure.setText("-- hPa");
        binding.tvVisibility.setText("-- km");
        binding.tvUpdateTime.setText("更新失败");
        binding.layoutWeatherContent.setVisibility(View.VISIBLE);
    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(requireContext());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(requireActivity(), result, 2404).show();
            }
            return false;
        }
        return true;
    }

    private String findNearestCity(double lat, double lon) {
        try {
            List<edu.neu.myapplication5_25.model.City> cities = edu.neu.myapplication5_25.model.City.getChinaCities();
            String nearestCity = null;
            double minDistance = Double.MAX_VALUE;
            
            for (edu.neu.myapplication5_25.model.City city : cities) {
                double distance = calculateDistance(lat, lon, city.getLatitude(), city.getLongitude());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestCity = city.getName();
                }
            }
            
            Log.d(TAG, "最近的城市: " + nearestCity + ", 距离: " + String.format("%.2f", minDistance) + " km");
            
            // 如果最近的城市距离超过阈值，返回null
            if (minDistance > MAX_CITY_DISTANCE) {
                Log.w(TAG, "最近的城市距离过远: " + minDistance + " km");
                return null;
            }
            
            return nearestCity;
        } catch (Exception e) {
            Log.e(TAG, "查找最近城市失败: " + e.getMessage());
            return null;
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 使用Haversine公式计算两点间距离
        final int R = 6371; // 地球半径（公里）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // 返回距离（公里）
    }

    private void updateCityAndWeather(String cityName) {
        if (getContext() != null) {
            // 保存城市信息到SharedPreferences
            SharedPreferences prefs = getContext().getSharedPreferences("weather_prefs", 0);
            prefs.edit()
                .putString("selected_city", cityName)
                .putLong("last_location_update", System.currentTimeMillis())
                .apply();
        }
        
        currentCity = cityName;
        binding.tvCityName.setText(currentCity);
        loadWeatherData();
        Toast.makeText(getContext(), "已定位到: " + cityName, Toast.LENGTH_SHORT).show();
    }

    private void loadWeatherByLocation(double lat, double lon) {
        Log.d(TAG, "根据位置加载天气: " + lat + ", " + lon);
        
        if (weatherService == null) {
            Log.e(TAG, "WeatherService为null");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutWeatherContent.setVisibility(View.GONE);
        binding.tvWeatherDescription.setText("正在获取位置天气数据...");

        weatherService.getCurrentWeatherByCoordinates(lat, lon, new WeatherService.WeatherCallback() {
            @Override
            public void onSuccess(WeatherData weatherData) {
                Log.d(TAG, "位置天气数据获取成功: " + weatherData.toString());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        currentCity = weatherData.getCityName() != null ? weatherData.getCityName() : "当前位置";
                        updateWeatherUI(weatherData);
                        binding.progressBar.setVisibility(View.GONE);
                        binding.layoutWeatherContent.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "位置天气数据更新成功", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "位置天气数据获取失败: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "获取位置天气失败: " + error, Toast.LENGTH_LONG).show();
                        binding.progressBar.setVisibility(View.GONE);
                        showDefaultWeatherData();
                    });
                }
            }
        });
    }

    private void saveWeatherCache(WeatherData weatherData) {
        try {
            if (getContext() != null) {
                SharedPreferences prefs = getContext().getSharedPreferences("weather_cache", 0);
                Gson gson = new Gson();
                String json = gson.toJson(weatherData);
                prefs.edit()
                    .putString("weather_data", json)
                    .putLong("cache_time", System.currentTimeMillis())
                    .apply();
            }
        } catch (Exception e) {
            Log.e(TAG, "保存天气缓存失败", e);
        }
    }

    private WeatherData loadWeatherCache() {
        try {
            if (getContext() != null) {
                SharedPreferences prefs = getContext().getSharedPreferences("weather_cache", 0);
                String json = prefs.getString("weather_data", null);
                if (json != null) {
                    Gson gson = new Gson();
                    return gson.fromJson(json, WeatherData.class);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "加载天气缓存失败", e);
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，获取位置
                getCurrentLocation();
            } else {
                // 权限被拒绝
                Toast.makeText(getContext(), "需要位置权限才能获取当前位置", Toast.LENGTH_LONG).show();
                binding.progressBar.setVisibility(View.GONE);
                binding.layoutWeatherContent.setVisibility(View.VISIBLE);
                
                // 尝试显示最近的天气数据
                WeatherData cachedData = loadWeatherCache();
                if (cachedData != null) {
                    updateWeatherUI(cachedData);
                } else {
                    showDefaultWeatherData();
                }
                
                // 如果是永久拒绝，提示用户手动开启权限
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(requireContext())
                        .setTitle("位置权限已被永久拒绝")
                        .setMessage("您需要在设置中手动开启位置权限以使用定位功能")
                        .setPositiveButton("去设置", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
                }
            }
        }
    }
} 