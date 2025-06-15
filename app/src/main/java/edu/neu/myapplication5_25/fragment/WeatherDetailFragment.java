package edu.neu.myapplication5_25.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.neu.myapplication5_25.MainActivity;
import edu.neu.myapplication5_25.R;
import edu.neu.myapplication5_25.databinding.FragmentWeatherDetailBinding;
import edu.neu.myapplication5_25.model.DailyWeather;
import edu.neu.myapplication5_25.model.HourlyWeather;
import edu.neu.myapplication5_25.service.WeatherService;
import edu.neu.myapplication5_25.util.ThemeManager;

public class WeatherDetailFragment extends Fragment {
    private static final String TAG = "WeatherDetailFragment";
    private FragmentWeatherDetailBinding binding;
    private DailyWeather dailyWeather;
    private HourlyWeather hourlyWeather;
    private WeatherService weatherService;
    private ThemeManager themeManager;

    public static WeatherDetailFragment newInstance(DailyWeather dailyWeather) {
        WeatherDetailFragment fragment = new WeatherDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("daily_weather", dailyWeather);
        fragment.setArguments(args);
        return fragment;
    }

    public static WeatherDetailFragment newInstance(HourlyWeather hourlyWeather) {
        WeatherDetailFragment fragment = new WeatherDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("hourly_weather", hourlyWeather);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWeatherDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        Log.d(TAG, "WeatherDetailFragment onViewCreated");
        
        // 获取服务
        if (getActivity() instanceof MainActivity) {
            weatherService = ((MainActivity) getActivity()).getWeatherService();
            themeManager = ((MainActivity) getActivity()).getThemeManager();
        } else {
            themeManager = ThemeManager.getInstance(getContext());
        }
        
        // 获取传递的数据
        if (getArguments() != null) {
            dailyWeather = (DailyWeather) getArguments().getSerializable("daily_weather");
            hourlyWeather = (HourlyWeather) getArguments().getSerializable("hourly_weather");
        }
        
        setupViews();
        setupClickListeners();
        displayWeatherDetail();
        applyTheme();
    }

    @Override
    public void onResume() {
        super.onResume();
        applyTheme();
    }

    private void applyTheme() {
        if (themeManager == null || binding == null) return;
        
        try {
            // 应用主题颜色
            binding.getRoot().setBackgroundColor(themeManager.getBackgroundColor());
            binding.cardWeatherOverview.setCardBackgroundColor(themeManager.getCardBackgroundColor());
            binding.cardWeatherDetails.setCardBackgroundColor(themeManager.getCardBackgroundColor());
            binding.cardWeatherStats.setCardBackgroundColor(themeManager.getCardBackgroundColor());
            
            // 更新文本颜色
            binding.tvDetailTitle.setTextColor(themeManager.getTextPrimaryColor());
            binding.tvDetailDescription.setTextColor(themeManager.getTextSecondaryColor());
            binding.tvDetailTemp.setTextColor(themeManager.getTextPrimaryColor());
            binding.tvDetailDate.setTextColor(themeManager.getTextSecondaryColor());
            
        } catch (Exception e) {
            Log.e(TAG, "应用主题失败: " + e.getMessage());
        }
    }

    private void setupViews() {
        // 设置返回按钮
        binding.btnBack.setOnClickListener(v -> {
            try {
                Navigation.findNavController(v).navigateUp();
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
        
        // 设置分享按钮
        binding.btnShare.setOnClickListener(v -> shareWeatherInfo());
    }

    private void setupClickListeners() {
        // 可以添加更多交互功能
        binding.cardWeatherOverview.setOnClickListener(v -> {
            Toast.makeText(getContext(), "天气概览", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayWeatherDetail() {
        if (dailyWeather != null) {
            displayDailyWeatherDetail();
        } else if (hourlyWeather != null) {
            displayHourlyWeatherDetail();
        } else {
            showNoDataMessage();
        }
    }

    private void displayDailyWeatherDetail() {
        Log.d(TAG, "显示多日天气详情");
        
        try {
            // 设置标题
            binding.tvDetailTitle.setText("多日天气详情");
            
            // 设置日期
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.getDefault());
            try {
                Date date = inputFormat.parse(dailyWeather.getDate());
                if (date != null) {
                    binding.tvDetailDate.setText(outputFormat.format(date));
                } else {
                    binding.tvDetailDate.setText(dailyWeather.getDate());
                }
            } catch (Exception e) {
                binding.tvDetailDate.setText(dailyWeather.getDate());
            }
            
            // 设置温度
            binding.tvDetailTemp.setText(String.format(Locale.getDefault(), 
                "%.0f° / %.0f°", dailyWeather.getMaxTemp(), dailyWeather.getMinTemp()));
            
            // 设置天气描述
            binding.tvDetailDescription.setText(dailyWeather.getDescription());
            
            // 设置天气图标
            setWeatherIcon(binding.ivDetailWeatherIcon, dailyWeather.getWeatherMain());
            
            // 设置详细信息
            binding.tvHumidityValue.setText(String.format(Locale.getDefault(), "%d%%", dailyWeather.getHumidity()));
            binding.tvWindSpeedValue.setText(String.format(Locale.getDefault(), "%.1f m/s", dailyWeather.getWindSpeed()));
            binding.tvPressureValue.setText(String.format(Locale.getDefault(), "%.0f hPa", dailyWeather.getPressure()));
            binding.tvVisibilityValue.setText("良好");
            
            // 设置统计信息
            binding.tvMaxTempValue.setText(String.format(Locale.getDefault(), "%.0f°", dailyWeather.getMaxTemp()));
            binding.tvMinTempValue.setText(String.format(Locale.getDefault(), "%.0f°", dailyWeather.getMinTemp()));
            binding.tvFeelsLikeValue.setText(String.format(Locale.getDefault(), "%.0f°", dailyWeather.getFeelsLike()));
            
            // 设置天气建议
            binding.tvWeatherAdvice.setText(getWeatherAdvice(dailyWeather.getWeatherMain(), dailyWeather.getMaxTemp()));
            
        } catch (Exception e) {
            Log.e(TAG, "显示多日天气详情失败: " + e.getMessage());
            showErrorMessage();
        }
    }

    private void displayHourlyWeatherDetail() {
        Log.d(TAG, "显示逐时天气详情");
        
        try {
            // 设置标题
            binding.tvDetailTitle.setText("逐时天气详情");
            
            // 设置时间
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
            try {
                Date date = inputFormat.parse(hourlyWeather.getDateTime());
                if (date != null) {
                    binding.tvDetailDate.setText(outputFormat.format(date));
                } else {
                    binding.tvDetailDate.setText(hourlyWeather.getDateTime());
                }
            } catch (Exception e) {
                binding.tvDetailDate.setText(hourlyWeather.getDateTime());
            }
            
            // 设置温度
            binding.tvDetailTemp.setText(String.format(Locale.getDefault(), "%.0f°", hourlyWeather.getTemperature()));
            
            // 设置天气描述
            binding.tvDetailDescription.setText(hourlyWeather.getDescription());
            
            // 设置天气图标
            setWeatherIcon(binding.ivDetailWeatherIcon, hourlyWeather.getWeatherMain());
            
            // 设置详细信息
            binding.tvHumidityValue.setText(String.format(Locale.getDefault(), "%d%%", hourlyWeather.getHumidity()));
            binding.tvWindSpeedValue.setText(String.format(Locale.getDefault(), "%.1f m/s", hourlyWeather.getWindSpeed()));
            binding.tvPressureValue.setText(String.format(Locale.getDefault(), "%.0f hPa", hourlyWeather.getPressure()));
            binding.tvVisibilityValue.setText("良好");
            
            // 设置统计信息
            binding.tvMaxTempValue.setText(String.format(Locale.getDefault(), "%.0f°", hourlyWeather.getTemperature()));
            binding.tvMinTempValue.setText(String.format(Locale.getDefault(), "%.0f°", hourlyWeather.getTemperature()));
            binding.tvFeelsLikeValue.setText(String.format(Locale.getDefault(), "%.0f°", hourlyWeather.getFeelsLike()));
            
            // 设置天气建议
            binding.tvWeatherAdvice.setText(getWeatherAdvice(hourlyWeather.getWeatherMain(), hourlyWeather.getTemperature()));
            
        } catch (Exception e) {
            Log.e(TAG, "显示逐时天气详情失败: " + e.getMessage());
            showErrorMessage();
        }
    }

    private void setWeatherIcon(ImageView imageView, String weatherMain) {
        int iconRes;
        if (weatherMain == null) {
            iconRes = R.drawable.ic_weather;
            imageView.setImageResource(iconRes);
            return;
        }
        
        switch (weatherMain.toLowerCase()) {
            case "晴":
            case "clear":
                iconRes = R.drawable.ic_weather_sunny;
                break;
            case "多云":
            case "阴":
            case "clouds":
            case "cloudy":
                iconRes = R.drawable.ic_weather_cloudy;
                break;
            case "雨":
            case "小雨":
            case "中雨":
            case "大雨":
            case "rain":
            case "drizzle":
                iconRes = R.drawable.ic_weather_rainy;
                break;
            case "雪":
            case "小雪":
            case "中雪":
            case "大雪":
            case "snow":
                iconRes = R.drawable.ic_weather_snowy;
                break;
            default:
                iconRes = R.drawable.ic_weather;
                break;
        }
        imageView.setImageResource(iconRes);
    }

    private String getWeatherAdvice(String weatherMain, double temperature) {
        if (weatherMain == null) {
            return "注意关注天气变化，合理安排出行。";
        }
        
        StringBuilder advice = new StringBuilder();
        
        // 根据天气类型给建议
        switch (weatherMain.toLowerCase()) {
            case "晴":
            case "clear":
                advice.append("天气晴朗，适合外出活动。");
                if (temperature > 30) {
                    advice.append("温度较高，注意防晒和补水。");
                } else if (temperature < 10) {
                    advice.append("温度较低，注意保暖。");
                }
                break;
            case "多云":
            case "阴":
            case "clouds":
                advice.append("天气阴沉，光线较弱。");
                break;
            case "雨":
            case "rain":
            case "drizzle":
                advice.append("有降雨，出行请携带雨具。注意道路湿滑。");
                break;
            case "雪":
            case "snow":
                advice.append("有降雪，注意保暖和防滑。路面可能结冰，小心驾驶。");
                break;
            default:
                advice.append("注意关注天气变化。");
                break;
        }
        
        // 根据温度给建议
        if (temperature > 35) {
            advice.append("高温天气，避免长时间户外活动。");
        } else if (temperature < -10) {
            advice.append("严寒天气，注意防冻保暖。");
        }
        
        return advice.toString();
    }

    private void shareWeatherInfo() {
        try {
            String shareText;
            if (dailyWeather != null) {
                shareText = String.format("今日天气：%s，温度 %.0f°/%.0f°，%s", 
                    dailyWeather.getDate(), dailyWeather.getMinTemp(), dailyWeather.getMaxTemp(), 
                    dailyWeather.getDescription());
            } else if (hourlyWeather != null) {
                shareText = String.format("当前天气：%s，温度 %.0f°，%s", 
                    hourlyWeather.getDateTime(), hourlyWeather.getTemperature(), 
                    hourlyWeather.getDescription());
            } else {
                shareText = "分享天气信息";
            }
            
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            startActivity(android.content.Intent.createChooser(shareIntent, "分享天气信息"));
            
        } catch (Exception e) {
            Log.e(TAG, "分享天气信息失败: " + e.getMessage());
            Toast.makeText(getContext(), "分享失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNoDataMessage() {
        binding.tvDetailTitle.setText("天气详情");
        binding.tvDetailDescription.setText("暂无天气数据");
        binding.tvDetailTemp.setText("--°");
        binding.tvDetailDate.setText("--");
        binding.ivDetailWeatherIcon.setImageResource(R.drawable.ic_weather);
    }

    private void showErrorMessage() {
        Toast.makeText(getContext(), "显示天气详情时发生错误", Toast.LENGTH_SHORT).show();
        showNoDataMessage();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "WeatherDetailFragment onDestroyView");
        binding = null;
    }
} 