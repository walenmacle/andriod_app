package edu.neu.myapplication5_25.fragment;

import android.app.AlertDialog;
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

import edu.neu.myapplication5_25.MainActivity;
import edu.neu.myapplication5_25.databinding.FragmentSettingsBinding;
import edu.neu.myapplication5_25.util.ThemeManager;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";
    private FragmentSettingsBinding binding;
    private SharedPreferences sharedPreferences;
    private String[] themes = {"默认主题", "深色主题", "彩色主题"};
    private ThemeManager themeManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        Log.d(TAG, "SettingsFragment onViewCreated");
        
        // 获取主题管理器
        if (getActivity() instanceof MainActivity) {
            themeManager = ((MainActivity) getActivity()).getThemeManager();
        } else {
            themeManager = ThemeManager.getInstance(getContext());
        }
        
        setupSettings();
        setupClickListeners();
        loadSettings();
        applyCurrentTheme();
    }

    @Override
    public void onResume() {
        super.onResume();
        applyCurrentTheme();
    }

    private void applyCurrentTheme() {
        try {
            if (binding == null || themeManager == null) return;
            
            // 更新背景色
            binding.getRoot().setBackgroundColor(themeManager.getBackgroundColor());
            
            // 更新当前主题文本颜色
            binding.tvCurrentTheme.setTextColor(themeManager.getTextSecondaryColor());
            
            Log.d(TAG, "主题应用完成: " + themeManager.getThemeName());
        } catch (Exception e) {
            Log.e(TAG, "应用主题失败: " + e.getMessage());
        }
    }

    private void setupSettings() {
        sharedPreferences = getContext().getSharedPreferences("weather_app_settings", 
            getContext().MODE_PRIVATE);
        
        // 设置通知开关监听
        binding.switchNotifications.setOnCheckedChangeListener(
            (buttonView, isChecked) -> saveNotificationSetting(isChecked)
        );
    }

    private void setupClickListeners() {
        // 个人信息
        binding.layoutProfile.setOnClickListener(v -> showProfileDialog());
        
        // 切换账号
        binding.layoutSwitchAccount.setOnClickListener(v -> showSwitchAccountDialog());
        
        // 切换主题
        binding.layoutTheme.setOnClickListener(v -> showThemeDialog());
        
        // 关于应用
        binding.layoutAbout.setOnClickListener(v -> showAboutDialog());
        
        // 帮助与反馈
        binding.layoutHelp.setOnClickListener(v -> showHelpDialog());
        
        // 退出登录
        binding.layoutLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadSettings() {
        // 加载通知设置
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        binding.switchNotifications.setChecked(notificationsEnabled);
        
        // 加载主题设置
        if (themeManager != null) {
            binding.tvCurrentTheme.setText(themeManager.getThemeName());
        }
    }

    private void saveNotificationSetting(boolean enabled) {
        sharedPreferences.edit()
            .putBoolean("notifications_enabled", enabled)
            .apply();
        
        String message = enabled ? "通知已开启" : "通知已关闭";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showProfileDialog() {
        String profileInfo = "用户名：天气用户\n" +
                           "邮箱：user@weather.app\n" +
                           "注册时间：2024年5月25日\n" +
                           "使用天数：30天";
        
        new AlertDialog.Builder(getContext())
            .setTitle("个人信息")
            .setMessage(profileInfo)
            .setPositiveButton("编辑", (dialog, which) -> {
                Toast.makeText(getContext(), "编辑功能正在开发中", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("关闭", null)
            .show();
    }

    private void showSwitchAccountDialog() {
        String[] accounts = {"当前账号 (user@weather.app)", "添加新账号"};
        
        new AlertDialog.Builder(getContext())
            .setTitle("切换账号")
            .setItems(accounts, (dialog, which) -> {
                if (which == 0) {
                    Toast.makeText(getContext(), "当前账号已选中", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "添加账号功能正在开发中", Toast.LENGTH_SHORT).show();
                }
            })
            .show();
    }

    private void showThemeDialog() {
        int currentThemeIndex = themeManager != null ? themeManager.getCurrentTheme() : 0;
        
        new AlertDialog.Builder(getContext())
            .setTitle("选择主题")
            .setSingleChoiceItems(themes, currentThemeIndex, (dialog, which) -> {
                try {
                    if (themeManager != null) {
                        // 设置新主题
                        themeManager.setTheme(which);
                        
                        // 更新显示
                        binding.tvCurrentTheme.setText(themeManager.getThemeName());
                        
                        // 立即应用主题
                        applyCurrentTheme();
                        
                        // 通知MainActivity刷新所有页面
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).refreshAllFragments();
                        }
                        
                        Toast.makeText(getContext(), "已切换到" + themes[which], Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "主题切换成功: " + themes[which]);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "主题切换失败: " + e.getMessage());
                    Toast.makeText(getContext(), "主题切换失败", Toast.LENGTH_SHORT).show();
                }
                
                dialog.dismiss();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void showAboutDialog() {
        String aboutInfo = "天气预报应用 v1.0.0\n\n" +
                          "功能特色：\n" +
                          "• 实时天气查询\n" +
                          "• 多日天气预报\n" +
                          "• 音乐播放功能\n" +
                          "• 日记记录功能\n" +
                          "• 个性化设置\n\n" +
                          "开发团队：Weather Team\n" +
                          "技术支持：Android SDK";
        
        new AlertDialog.Builder(getContext())
            .setTitle("关于应用")
            .setMessage(aboutInfo)
            .setPositiveButton("确定", null)
            .show();
    }

    private void showHelpDialog() {
        String helpInfo = "常见问题：\n\n" +
                         "Q: 如何查看其他城市天气？\n" +
                         "A: 在天气页面点击城市名称即可切换\n\n" +
                         "Q: 如何添加音乐？\n" +
                         "A: 音乐功能支持内置音乐播放\n\n" +
                         "Q: 日记数据会丢失吗？\n" +
                         "A: 所有日记数据都保存在本地数据库中\n\n" +
                         "联系我们：\n" +
                         "邮箱：support@weather.app\n" +
                         "QQ群：123456789";
        
        new AlertDialog.Builder(getContext())
            .setTitle("帮助与反馈")
            .setMessage(helpInfo)
            .setPositiveButton("确定", null)
            .setNeutralButton("联系客服", (dialog, which) -> {
                Toast.makeText(getContext(), "正在跳转到客服页面...", Toast.LENGTH_SHORT).show();
            })
            .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
            .setTitle("退出登录")
            .setMessage("确定要退出当前账号吗？")
            .setPositiveButton("退出", (dialog, which) -> {
                // 清除用户数据
                sharedPreferences.edit().clear().apply();
                
                Toast.makeText(getContext(), "已退出登录", Toast.LENGTH_SHORT).show();
                
                // 这里可以跳转到登录页面
                // 暂时只显示提示
                showLoginRequiredDialog();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void showLoginRequiredDialog() {
        new AlertDialog.Builder(getContext())
            .setTitle("需要重新登录")
            .setMessage("您已退出登录，需要重新登录才能使用应用功能")
            .setPositiveButton("重新登录", (dialog, which) -> {
                Toast.makeText(getContext(), "登录功能正在开发中", Toast.LENGTH_SHORT).show();
                // 这里可以实现重新登录逻辑
                restoreDefaultSettings();
            })
            .setCancelable(false)
            .show();
    }

    private void restoreDefaultSettings() {
        try {
            // 恢复默认设置
            if (themeManager != null) {
                themeManager.setTheme(ThemeManager.THEME_DEFAULT);
                binding.tvCurrentTheme.setText(themeManager.getThemeName());
                applyCurrentTheme();
            }
            
            binding.switchNotifications.setChecked(true);
            
            // 保存默认设置
            sharedPreferences.edit()
                .putBoolean("notifications_enabled", true)
                .apply();
            
            Toast.makeText(getContext(), "已恢复默认设置", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "恢复默认设置失败: " + e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "SettingsFragment onDestroyView");
        binding = null;
    }
} 