package edu.neu.myapplication5_25.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import edu.neu.myapplication5_25.MainActivity;
import edu.neu.myapplication5_25.adapter.DailyWeatherAdapter;
import edu.neu.myapplication5_25.adapter.HourlyWeatherAdapter;
import edu.neu.myapplication5_25.databinding.FragmentForecastBinding;
import edu.neu.myapplication5_25.model.DailyWeather;
import edu.neu.myapplication5_25.model.HourlyWeather;
import edu.neu.myapplication5_25.service.WeatherService;

public class ForecastFragment extends Fragment {
    private static final String TAG = "ForecastFragment";
    private FragmentForecastBinding binding;
    private WeatherService weatherService;
    private DailyWeatherAdapter dailyAdapter;
    private HourlyWeatherAdapter hourlyAdapter;
    private String currentCity = "沈阳";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        Log.d(TAG, "ForecastFragment onViewCreated");
        
        // 获取天气服务
        if (getActivity() instanceof MainActivity) {
            weatherService = ((MainActivity) getActivity()).getWeatherService();
        }
        
        // 读取保存的城市选择
        loadSelectedCity();
        
        setupRecyclerViews();
        setupTabLayout();
        updateCityDisplay();
        loadForecastData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ForecastFragment onResume");
        
        // 每次页面恢复时检查是否有新选择的城市
        String previousCity = currentCity;
        loadSelectedCity();
        
        // 如果城市发生变化，重新加载预报数据
        if (!previousCity.equals(currentCity)) {
            Log.d(TAG, "预报页面城市已变更，从 " + previousCity + " 到 " + currentCity);
            updateCityDisplay();
            loadForecastData();
        }
    }

    private void loadSelectedCity() {
        try {
            if (getContext() != null) {
                SharedPreferences prefs = getContext().getSharedPreferences("weather_prefs", 0);
                String selectedCity = prefs.getString("selected_city", "沈阳");
                if (selectedCity != null && !selectedCity.isEmpty()) {
                    currentCity = selectedCity;
                    Log.d(TAG, "预报页面加载选中的城市: " + currentCity);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "加载选中城市失败: " + e.getMessage());
        }
    }

    private void updateCityDisplay() {
        // 更新预报页面的城市显示
        if (binding != null && binding.textCurrentCity != null) {
            binding.textCurrentCity.setText(currentCity + " 天气预报");
        }
    }

    private void setupRecyclerViews() {
        // 设置多日天气RecyclerView
        dailyAdapter = new DailyWeatherAdapter(new ArrayList<>());
        dailyAdapter.setOnItemClickListener(dailyWeather -> {
            try {
                Log.d(TAG, "点击多日天气项: " + dailyWeather.getDate());
                // 跳转到详情页面
                androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireView());
                
                // 创建Bundle传递数据
                Bundle args = new Bundle();
                args.putSerializable("daily_weather", dailyWeather);
                
                navController.navigate(edu.neu.myapplication5_25.R.id.weatherDetailFragment, args);
                Toast.makeText(getContext(), "查看 " + dailyWeather.getDate() + " 详情", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "跳转到天气详情失败: " + e.getMessage());
                Toast.makeText(getContext(), "无法打开详情页面", Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerViewDaily.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewDaily.setAdapter(dailyAdapter);

        // 设置逐时天气RecyclerView
        hourlyAdapter = new HourlyWeatherAdapter(new ArrayList<>());
        hourlyAdapter.setOnItemClickListener(hourlyWeather -> {
            try {
                Log.d(TAG, "点击逐时天气项: " + hourlyWeather.getTime());
                // 跳转到详情页面
                androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(requireView());
                
                // 创建Bundle传递数据
                Bundle args = new Bundle();
                args.putSerializable("hourly_weather", hourlyWeather);
                
                navController.navigate(edu.neu.myapplication5_25.R.id.weatherDetailFragment, args);
                Toast.makeText(getContext(), "查看 " + hourlyWeather.getTime() + " 详情", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "跳转到天气详情失败: " + e.getMessage());
                Toast.makeText(getContext(), "无法打开详情页面", Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerViewHourly.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewHourly.setAdapter(hourlyAdapter);
    }

    private void setupTabLayout() {
        // 设置Tab切换
        binding.tabDaily.setOnClickListener(v -> {
            showDailyForecast();
            updateTabSelection(true);
        });

        binding.tabHourly.setOnClickListener(v -> {
            showHourlyForecast();
            updateTabSelection(false);
        });

        // 默认显示多日预报
        showDailyForecast();
        updateTabSelection(true);
    }

    private void updateTabSelection(boolean isDailySelected) {
        if (isDailySelected) {
            binding.tabDaily.setBackgroundResource(edu.neu.myapplication5_25.R.drawable.bg_tab_selected);
            binding.tabDaily.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.white));
            binding.tabHourly.setBackgroundResource(edu.neu.myapplication5_25.R.drawable.bg_tab_normal);
            binding.tabHourly.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), edu.neu.myapplication5_25.R.color.text_secondary));
        } else {
            binding.tabDaily.setBackgroundResource(edu.neu.myapplication5_25.R.drawable.bg_tab_normal);
            binding.tabDaily.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), edu.neu.myapplication5_25.R.color.text_secondary));
            binding.tabHourly.setBackgroundResource(edu.neu.myapplication5_25.R.drawable.bg_tab_selected);
            binding.tabHourly.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.white));
        }
    }

    private void showDailyForecast() {
        binding.recyclerViewDaily.setVisibility(View.VISIBLE);
        binding.recyclerViewHourly.setVisibility(View.GONE);
    }

    private void showHourlyForecast() {
        binding.recyclerViewDaily.setVisibility(View.GONE);
        binding.recyclerViewHourly.setVisibility(View.VISIBLE);
    }

    private void loadForecastData() {
        Log.d(TAG, "开始加载预报数据，当前城市: " + currentCity);
        
        if (weatherService == null) {
            Log.e(TAG, "WeatherService为null");
            Toast.makeText(getContext(), "天气服务未初始化", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        // 加载多日预报
        weatherService.getFiveDayForecast(currentCity, new WeatherService.DailyWeatherCallback() {
            @Override
            public void onSuccess(List<DailyWeather> dailyWeatherList) {
                Log.d(TAG, "多日预报数据获取成功，数据量: " + dailyWeatherList.size());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        dailyAdapter.updateData(dailyWeatherList);
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), currentCity + " 预报数据更新成功", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "多日预报数据获取失败: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "获取 " + currentCity + " 多日预报失败: " + error, Toast.LENGTH_LONG).show();
                        binding.progressBar.setVisibility(View.GONE);
                    });
                }
            }
        });

        // 加载逐时预报
        weatherService.getHourlyForecast(currentCity, new WeatherService.HourlyWeatherCallback() {
            @Override
            public void onSuccess(List<HourlyWeather> hourlyWeatherList) {
                Log.d(TAG, "逐时预报数据获取成功，数据量: " + hourlyWeatherList.size());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        hourlyAdapter.updateData(hourlyWeatherList);
                    });
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "逐时预报数据获取失败: " + error);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "获取 " + currentCity + " 逐时预报失败: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    // 公开方法，供外部调用刷新数据
    public void refreshForecastData() {
        Log.d(TAG, "外部调用刷新预报数据");
        loadSelectedCity();
        updateCityDisplay();
        loadForecastData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "ForecastFragment onDestroyView");
        binding = null;
    }
} 