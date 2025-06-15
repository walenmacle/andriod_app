package edu.neu.myapplication5_25;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.neu.myapplication5_25.databinding.ActivityMainBinding;
import edu.neu.myapplication5_25.service.WeatherService;
import edu.neu.myapplication5_25.util.ThemeManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    private ActivityMainBinding binding;
    private NavController navController;
    private WeatherService weatherService;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.d(TAG, "MainActivity onCreate 开始");
            
            // 初始化主题管理器
            themeManager = ThemeManager.getInstance(this);
            
            // 应用主题
            applyCurrentTheme();
            
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // 初始化天气服务
            weatherService = new WeatherService();
            Log.d(TAG, "WeatherService 初始化完成");

            // 设置导航
            setupNavigation();

            // 设置浮动操作按钮
            setupFab();

            // 请求权限
            requestPermissions();
            
            Log.d(TAG, "MainActivity onCreate 完成");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "MainActivity onCreate 失败: " + e.getMessage());
            Toast.makeText(this, "应用启动失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次恢复时检查主题是否有变化
        applyCurrentTheme();
        updateUIColors();
    }

    private void applyCurrentTheme() {
        try {
            // 应用主题到Activity
            themeManager.applyTheme(this);
            Log.d(TAG, "应用主题: " + themeManager.getThemeName());
        } catch (Exception e) {
            Log.e(TAG, "应用主题失败: " + e.getMessage());
        }
    }

    private void updateUIColors() {
        try {
            if (binding == null) return;
            
            // 更新底部导航栏颜色
            binding.bottomNavigation.setBackgroundColor(themeManager.getCardBackgroundColor());
            
            // 更新浮动按钮颜色
            binding.fabRefresh.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(themeManager.getPrimaryColor())
            );
            
            // 更新根布局背景色
            binding.getRoot().setBackgroundColor(themeManager.getBackgroundColor());
            
            Log.d(TAG, "UI颜色更新完成");
        } catch (Exception e) {
            Log.e(TAG, "更新UI颜色失败: " + e.getMessage());
        }
    }

    private void setupNavigation() {
        try {
            Log.d(TAG, "设置导航开始");
            
            // 添加延迟以确保Fragment容器已准备就绪
            binding.getRoot().post(() -> {
                try {
                    // 使用多种方式尝试获取NavController
                    navController = null;
                    
                    // 方式1：通过FragmentContainerView获取
                    try {
                        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
                        Log.d(TAG, "通过FragmentContainerView获取NavController成功");
                    } catch (Exception e) {
                        Log.w(TAG, "通过FragmentContainerView获取NavController失败: " + e.getMessage());
                    }
                    
                    // 方式2：如果方式1失败，通过Fragment获取
                    if (navController == null) {
                        try {
                            androidx.fragment.app.Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                            if (navHostFragment != null) {
                                navController = androidx.navigation.fragment.NavHostFragment.findNavController(navHostFragment);
                                Log.d(TAG, "通过Fragment获取NavController成功");
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "通过Fragment获取NavController失败: " + e.getMessage());
                        }
                    }
                    
                    if (navController == null) {
                        Log.e(TAG, "无法获取NavController，使用基础导航");
                        setupBasicNavigation();
                        return;
                    }
                    
                    BottomNavigationView bottomNav = binding.bottomNavigation;
                    Log.d(TAG, "NavController 和 BottomNavigationView 获取成功");
                    
                    // 首先尝试使用NavigationUI自动设置
                    try {
                        NavigationUI.setupWithNavController(bottomNav, navController);
                        Log.d(TAG, "NavigationUI 设置成功");
                    } catch (Exception e) {
                        Log.e(TAG, "NavigationUI 设置失败，使用手动设置: " + e.getMessage());
                        setupManualNavigation(bottomNav);
                    }
                    
                    // 添加导航目标变化监听器
                    navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                        Log.d(TAG, "导航目标变化: " + destination.getLabel());
                    });
                    
                    Log.d(TAG, "导航设置完成");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "导航设置过程中出现异常: " + e.getMessage());
                    setupBasicNavigation();
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "导航设置初始化失败: " + e.getMessage());
            setupBasicNavigation();
        }
    }
    
    private void setupManualNavigation(BottomNavigationView bottomNav) {
        // 手动处理导航项点击
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                try {
                    int itemId = item.getItemId();
                    Log.d(TAG, "底部导航项被点击: " + itemId);
                    
                    if (navController == null) {
                        Log.e(TAG, "NavController为null，无法导航");
                        Toast.makeText(MainActivity.this, "导航功能未初始化", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    
                    if (itemId == R.id.weatherFragment) {
                        Log.d(TAG, "导航到天气页面");
                        navController.navigate(R.id.weatherFragment);
                        return true;
                    } else if (itemId == R.id.forecastFragment) {
                        Log.d(TAG, "导航到预报页面");
                        navController.navigate(R.id.forecastFragment);
                        return true;
                    } else if (itemId == R.id.musicFragment) {
                        Log.d(TAG, "导航到音乐页面");
                        navController.navigate(R.id.musicFragment);
                        return true;
                    } else if (itemId == R.id.diaryFragment) {
                        Log.d(TAG, "导航到日记页面");
                        navController.navigate(R.id.diaryFragment);
                        return true;
                    } else if (itemId == R.id.settingsFragment) {
                        Log.d(TAG, "导航到设置页面");
                        navController.navigate(R.id.settingsFragment);
                        return true;
                    }
                    
                    Log.w(TAG, "未知的导航项ID: " + itemId);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "导航失败: " + ex.getMessage());
                    Toast.makeText(MainActivity.this, "导航失败: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                
                return false;
            }
        });
        
        Log.d(TAG, "手动导航设置完成");
    }
    
    private void setupBasicNavigation() {
        Log.d(TAG, "设置基础导航（无NavController）");
        
        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                try {
                    int itemId = item.getItemId();
                    Log.d(TAG, "基础导航项被点击: " + itemId);
                    
                    androidx.fragment.app.Fragment fragment = null;
                    String fragmentTag = "";
                    
                    if (itemId == R.id.weatherFragment) {
                        fragment = new edu.neu.myapplication5_25.fragment.WeatherFragment();
                        fragmentTag = "WeatherFragment";
                    } else if (itemId == R.id.forecastFragment) {
                        fragment = new edu.neu.myapplication5_25.fragment.ForecastFragment();
                        fragmentTag = "ForecastFragment";
                    } else if (itemId == R.id.musicFragment) {
                        fragment = new edu.neu.myapplication5_25.fragment.MusicFragment();
                        fragmentTag = "MusicFragment";
                    } else if (itemId == R.id.diaryFragment) {
                        fragment = new edu.neu.myapplication5_25.fragment.DiaryFragment();
                        fragmentTag = "DiaryFragment";
                    } else if (itemId == R.id.settingsFragment) {
                        fragment = new edu.neu.myapplication5_25.fragment.SettingsFragment();
                        fragmentTag = "SettingsFragment";
                    }
                    
                    if (fragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.nav_host_fragment, fragment, fragmentTag)
                                .addToBackStack(fragmentTag)
                                .commit();
                        Log.d(TAG, "基础导航到: " + fragmentTag);
                        return true;
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "基础导航失败: " + ex.getMessage());
                    Toast.makeText(MainActivity.this, "页面切换失败: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
                
                return false;
            }
        });
        
        // 默认显示天气页面
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new edu.neu.myapplication5_25.fragment.WeatherFragment(), "WeatherFragment")
                    .commit();
            Log.d(TAG, "默认显示天气页面");
        } catch (Exception e) {
            Log.e(TAG, "默认页面设置失败: " + e.getMessage());
        }
    }

    private void setupFab() {
        try {
            FloatingActionButton fab = binding.fabRefresh;
            fab.setOnClickListener(view -> {
                try {
                    Log.d(TAG, "刷新按钮被点击");
                    
                    // 添加空值检查，避免空指针异常
                    NavDestination currentDestination = navController.getCurrentDestination();
                    if (currentDestination == null) {
                        Log.w(TAG, "当前导航目标为null");
                        Toast.makeText(this, "刷新功能", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    int currentDestinationId = currentDestination.getId();
                    Log.d(TAG, "当前页面ID: " + currentDestinationId);
                    
                    if (currentDestinationId == R.id.weatherFragment) {
                        // 刷新天气数据
                        refreshWeatherData();
                    } else if (currentDestinationId == R.id.forecastFragment) {
                        // 刷新预报数据
                        refreshForecastData();
                    } else {
                        Toast.makeText(this, "刷新功能", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "刷新按钮处理失败: " + e.getMessage());
                    Toast.makeText(this, "刷新失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "浮动按钮设置失败: " + e.getMessage());
            Toast.makeText(this, "浮动按钮设置失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshWeatherData() {
        Log.d(TAG, "刷新天气数据");
        Toast.makeText(this, "正在刷新天气数据...", Toast.LENGTH_SHORT).show();
        // 这里会在WeatherFragment中实现具体的刷新逻辑
    }

    private void refreshForecastData() {
        Log.d(TAG, "刷新预报数据");
        Toast.makeText(this, "正在刷新预报数据...", Toast.LENGTH_SHORT).show();
        
        // 尝试获取当前显示的ForecastFragment并刷新
        try {
            androidx.fragment.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (currentFragment instanceof androidx.navigation.fragment.NavHostFragment) {
                androidx.navigation.fragment.NavHostFragment navHostFragment = (androidx.navigation.fragment.NavHostFragment) currentFragment;
                androidx.fragment.app.Fragment activeFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                
                if (activeFragment instanceof edu.neu.myapplication5_25.fragment.ForecastFragment) {
                    edu.neu.myapplication5_25.fragment.ForecastFragment forecastFragment = 
                        (edu.neu.myapplication5_25.fragment.ForecastFragment) activeFragment;
                    forecastFragment.refreshForecastData();
                    Log.d(TAG, "已调用ForecastFragment刷新");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "刷新ForecastFragment失败: " + e.getMessage());
        }
    }

    private void requestPermissions() {
        try {
            String[] permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.POST_NOTIFICATIONS
            };

            boolean needRequest = false;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    needRequest = true;
                    break;
                }
            }

            if (needRequest) {
                Log.d(TAG, "请求权限");
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            } else {
                Log.d(TAG, "所有权限已授予");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "权限请求失败: " + e.getMessage());
            Toast.makeText(this, "权限请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        try {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                boolean allGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }
                
                if (allGranted) {
                    Log.d(TAG, "所有权限已授予");
                    Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "部分权限未授予");
                    Toast.makeText(this, "部分权限未授予，可能影响应用功能", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "权限结果处理失败: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (weatherService != null) {
                weatherService.shutdown();
                Log.d(TAG, "WeatherService 已关闭");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "资源清理失败: " + e.getMessage());
        }
    }

    public WeatherService getWeatherService() {
        return weatherService;
    }
    
    public ThemeManager getThemeManager() {
        return themeManager;
    }
    
    public void refreshAllFragments() {
        // 通知所有Fragment刷新主题
        updateUIColors();
        
        // 可以通过广播或其他方式通知Fragment更新主题
        Log.d(TAG, "刷新所有Fragment主题");
    }
}