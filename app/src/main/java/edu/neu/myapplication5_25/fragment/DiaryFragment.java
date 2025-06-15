package edu.neu.myapplication5_25.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.neu.myapplication5_25.MainActivity;
import edu.neu.myapplication5_25.R;
import edu.neu.myapplication5_25.adapter.DiaryAdapter;
import edu.neu.myapplication5_25.database.WeatherDatabaseHelper;
import edu.neu.myapplication5_25.databinding.FragmentDiaryBinding;
import edu.neu.myapplication5_25.model.DiaryEntry;
import edu.neu.myapplication5_25.model.ScheduleEntry;
import edu.neu.myapplication5_25.service.NotificationService;
import edu.neu.myapplication5_25.util.ThemeManager;

public class DiaryFragment extends Fragment implements DiaryAdapter.OnDiaryClickListener {
    private static final String TAG = "DiaryFragment";
    private FragmentDiaryBinding binding;
    private DiaryAdapter diaryAdapter;
    private List<DiaryEntry> diaryList;
    private List<DiaryEntry> allDiaryList; // 原始完整列表
    private List<ScheduleEntry> scheduleList;
    private WeatherDatabaseHelper databaseHelper;
    private NotificationService notificationService;
    private ThemeManager themeManager;
    
    // 当前查看模式
    private enum ViewMode {
        ALL, TODAY, WEEK, MONTH, SCHEDULE
    }
    private ViewMode currentViewMode = ViewMode.ALL;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        Log.d(TAG, "DiaryFragment onViewCreated");
        
        // 初始化服务
        databaseHelper = new WeatherDatabaseHelper(getContext());
        notificationService = new NotificationService(getContext());
        if (getActivity() instanceof MainActivity) {
            themeManager = ((MainActivity) getActivity()).getThemeManager();
        } else {
            themeManager = ThemeManager.getInstance(getContext());
        }
        
        setupDiary();
        setupDiaryList();
        setupViewModeSpinner();
        setupFilterButtons();
        loadDiariesFromDatabase();
        updateStatistics();
        applyTheme();
    }

    @Override
    public void onResume() {
        super.onResume();
        applyTheme();
        checkScheduleReminders();
    }

    private void applyTheme() {
        if (themeManager == null || binding == null) return;
        
        try {
            // 应用主题颜色
            binding.getRoot().setBackgroundColor(themeManager.getBackgroundColor());
            
            Log.d(TAG, "主题应用完成");
        } catch (Exception e) {
            Log.e(TAG, "应用主题失败: " + e.getMessage());
        }
    }

    private void setupDiary() {
        diaryList = new ArrayList<>();
        allDiaryList = new ArrayList<>();
        scheduleList = new ArrayList<>();
        
        // 添加日记按钮
        binding.btnAddDiary.setOnClickListener(v -> showAddDiaryDialog());
        
        // 搜索按钮
        binding.btnSearch.setOnClickListener(v -> showAdvancedSearchDialog());
        
        // 添加日程按钮
        if (binding.btnAddSchedule != null) {
            binding.btnAddSchedule.setOnClickListener(v -> showAddScheduleDialog());
        }
    }

    private void setupDiaryList() {
        binding.rvDiaryList.setLayoutManager(new LinearLayoutManager(getContext()));
        diaryAdapter = new DiaryAdapter(diaryList, this);
        binding.rvDiaryList.setAdapter(diaryAdapter);
        
        updateEmptyState();
    }

    private void setupViewModeSpinner() {
        String[] viewOptions = {"全部", "今日", "本周", "本月", "日程"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, viewOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSort.setAdapter(adapter);
        
        binding.spinnerSort.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 全部
                        setViewMode(ViewMode.ALL);
                        break;
                    case 1: // 今日
                        setViewMode(ViewMode.TODAY);
                        break;
                    case 2: // 本周
                        setViewMode(ViewMode.WEEK);
                        break;
                    case 3: // 本月
                        setViewMode(ViewMode.MONTH);
                        break;
                    case 4: // 日程
                        setViewMode(ViewMode.SCHEDULE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void setupFilterButtons() {
        // 可以在这里添加额外的筛选按钮
    }

    private void setViewMode(ViewMode mode) {
        currentViewMode = mode;
        loadDiariesByViewMode();
    }

    private void loadDiariesByViewMode() {
        Log.d(TAG, "按模式加载数据: " + currentViewMode);
        
        try {
            switch (currentViewMode) {
                case ALL:
                    loadAllDiaries();
                    break;
                case TODAY:
                    loadTodayDiaries();
                    break;
                case WEEK:
                    loadWeekDiaries();
                    break;
                case MONTH:
                    loadMonthDiaries();
                    break;
                case SCHEDULE:
                    loadScheduleEntries();
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "加载数据失败: " + e.getMessage());
            Toast.makeText(getContext(), "加载数据失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAllDiaries() {
        allDiaryList.clear();
        allDiaryList.addAll(databaseHelper.getAllDiaries());
        
        diaryList.clear();
        diaryList.addAll(allDiaryList);
        
        diaryAdapter.updateDiaryList(diaryList);
        updateStatistics();
        updateEmptyState();
    }

    private void loadTodayDiaries() {
        List<DiaryEntry> todayDiaries = databaseHelper.getTodayDiaries();
        
        diaryList.clear();
        diaryList.addAll(todayDiaries);
        
        diaryAdapter.updateDiaryList(diaryList);
        updateEmptyState();
        
        Toast.makeText(getContext(), "今日日记: " + todayDiaries.size() + " 篇", Toast.LENGTH_SHORT).show();
    }

    private void loadWeekDiaries() {
        List<DiaryEntry> weekDiaries = databaseHelper.getThisWeekDiaries();
        
        diaryList.clear();
        diaryList.addAll(weekDiaries);
        
        diaryAdapter.updateDiaryList(diaryList);
        updateEmptyState();
        
        Toast.makeText(getContext(), "本周日记: " + weekDiaries.size() + " 篇", Toast.LENGTH_SHORT).show();
    }

    private void loadMonthDiaries() {
        List<DiaryEntry> monthDiaries = databaseHelper.getThisMonthDiariesList();
        
        diaryList.clear();
        diaryList.addAll(monthDiaries);
        
        diaryAdapter.updateDiaryList(diaryList);
        updateEmptyState();
        
        Toast.makeText(getContext(), "本月日记: " + monthDiaries.size() + " 篇", Toast.LENGTH_SHORT).show();
    }

    private void loadScheduleEntries() {
        scheduleList.clear();
        scheduleList.addAll(databaseHelper.getUpcomingSchedules());
        
        // 转换为DiaryEntry格式显示（临时解决方案）
        diaryList.clear();
        for (ScheduleEntry schedule : scheduleList) {
            DiaryEntry diaryEntry = new DiaryEntry();
            diaryEntry.setId(schedule.getId());
            diaryEntry.setTitle("📅 " + schedule.getTitle());
            diaryEntry.setContent(schedule.getContent() + "\n⏰ " + schedule.getFormattedDateTime());
            diaryEntry.setDate(System.currentTimeMillis());
            diaryEntry.setMood("计划");
            diaryEntry.setWeather(schedule.getStatusDescription());
            diaryList.add(diaryEntry);
        }
        
        diaryAdapter.updateDiaryList(diaryList);
        updateEmptyState();
        
        Toast.makeText(getContext(), "即将到来的日程: " + scheduleList.size() + " 项", Toast.LENGTH_SHORT).show();
    }

    private void loadDiariesFromDatabase() {
        loadAllDiaries();
        
        // 如果数据库为空，添加一些示例数据
        if (allDiaryList.isEmpty()) {
            insertSampleDiaries();
        }
    }

    private void insertSampleDiaries() {
        Log.d(TAG, "插入示例日记和日程到数据库");
        try {
            // 添加示例日记
            databaseHelper.insertDiary("美好的一天", 
                "今天天气很好，和朋友一起去公园散步，心情非常愉快。看到了很多美丽的花朵，还喂了鸽子。", 
                "2024-01-01", "开心", "晴");
            
            databaseHelper.insertDiary("学习新技能", 
                "今天开始学习Android开发，虽然有些困难，但是很有成就感。希望能够坚持下去。", 
                "2024-01-02", "兴奋", "多云");
            
            // 添加示例日程
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            String tomorrow = dateFormat.format(calendar.getTime());
            
            databaseHelper.insertSchedule("重要会议", 
                "与客户讨论项目进展", tomorrow, "14:00");
            
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            String dayAfterTomorrow = dateFormat.format(calendar.getTime());
            
            databaseHelper.insertSchedule("体检预约", 
                "年度健康体检", dayAfterTomorrow, "09:30");

            // 重新加载数据
            loadDiariesFromDatabase();
            
            Log.d(TAG, "示例数据插入完成");
        } catch (Exception e) {
            Log.e(TAG, "插入示例数据失败: " + e.getMessage());
        }
    }

    private void showAddDiaryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        
        // 创建输入布局
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        
        EditText etTitle = new EditText(getContext());
        etTitle.setHint("请输入日记标题");
        
        EditText etContent = new EditText(getContext());
        etContent.setHint("请输入日记内容");
        etContent.setMinLines(3);
        etContent.setMaxLines(6);
        
        // 心情选择
        Spinner spinnerMood = new Spinner(getContext());
        String[] moods = {"开心", "平静", "兴奋", "难过", "生气", "感激", "期待"};
        ArrayAdapter<String> moodAdapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, moods);
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMood.setAdapter(moodAdapter);
        
        // 天气选择
        Spinner spinnerWeather = new Spinner(getContext());
        String[] weathers = {"晴", "多云", "阴", "雨", "雪", "雾", "风"};
        ArrayAdapter<String> weatherAdapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, weathers);
        weatherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeather.setAdapter(weatherAdapter);
        
        layout.addView(etTitle);
        layout.addView(etContent);
        layout.addView(spinnerMood);
        layout.addView(spinnerWeather);
        
        builder.setView(layout)
            .setTitle("新建日记")
            .setPositiveButton("保存", (dialog, which) -> {
                try {
                    String title = etTitle.getText().toString().trim();
                    String content = etContent.getText().toString().trim();
                    
                    if (title.isEmpty() || content.isEmpty()) {
                        Toast.makeText(getContext(), "请填写标题和内容", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    String mood = spinnerMood.getSelectedItem().toString();
                    String weather = spinnerWeather.getSelectedItem().toString();
                    
                    // 保存到数据库
                    long id = databaseHelper.insertDiary(title, content, 
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()), mood, weather);
                    
                    if (id > 0) {
                        Log.d(TAG, "日记保存成功，ID: " + id);
                        Toast.makeText(getContext(), "日记保存成功", Toast.LENGTH_SHORT).show();
                        // 重新加载数据
                        loadDiariesByViewMode();
                    } else {
                        Log.e(TAG, "日记保存失败");
                        Toast.makeText(getContext(), "日记保存失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "保存日记时出错: " + e.getMessage());
                    Toast.makeText(getContext(), "保存日记时出错", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        
        // 创建输入布局
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        
        EditText etTitle = new EditText(getContext());
        etTitle.setHint("请输入日程标题");
        
        EditText etContent = new EditText(getContext());
        etContent.setHint("请输入日程详情");
        etContent.setMinLines(2);
        etContent.setMaxLines(4);
        
        // 日期选择
        EditText etDate = new EditText(getContext());
        etDate.setHint("选择日期");
        etDate.setFocusable(false);
        etDate.setClickable(true);
        
        // 时间选择
        EditText etTime = new EditText(getContext());
        etTime.setHint("选择时间");
        etTime.setFocusable(false);
        etTime.setClickable(true);
        
        // 设置默认日期为明天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(dateFormat.format(calendar.getTime()));
        
        // 设置默认时间
        etTime.setText("09:00");
        
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                etDate.setText(dateFormat.format(selectedDate.getTime()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
        
        etTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        });
        
        layout.addView(etTitle);
        layout.addView(etContent);
        layout.addView(etDate);
        layout.addView(etTime);
        
        builder.setView(layout)
            .setTitle("新建日程")
            .setPositiveButton("保存", (dialog, which) -> {
                try {
                    String title = etTitle.getText().toString().trim();
                    String content = etContent.getText().toString().trim();
                    String date = etDate.getText().toString().trim();
                    String time = etTime.getText().toString().trim();
                    
                    if (title.isEmpty()) {
                        Toast.makeText(getContext(), "请填写日程标题", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (date.isEmpty() || time.isEmpty()) {
                        Toast.makeText(getContext(), "请选择日期和时间", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // 保存到数据库
                    long id = databaseHelper.insertSchedule(title, content, date, time);
                    
                    if (id > 0) {
                        Log.d(TAG, "日程保存成功，ID: " + id);
                        Toast.makeText(getContext(), "日程保存成功", Toast.LENGTH_SHORT).show();
                        // 重新加载数据
                        loadDiariesByViewMode();
                        
                        // 设置提醒通知
                        scheduleNotificationReminder(title, content, date, time);
                    } else {
                        Log.e(TAG, "日程保存失败");
                        Toast.makeText(getContext(), "日程保存失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "保存日程时出错: " + e.getMessage());
                    Toast.makeText(getContext(), "保存日程时出错", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void scheduleNotificationReminder(String title, String content, String date, String time) {
        try {
            // 这里可以实现更复杂的提醒逻辑
            // 暂时发送一个测试通知
            notificationService.sendScheduleReminder(
                "日程提醒: " + title, 
                content + "\n时间: " + date + " " + time, 
                date + " " + time
            );
            
            Log.d(TAG, "日程提醒通知已设置");
        } catch (Exception e) {
            Log.e(TAG, "设置日程提醒失败: " + e.getMessage());
        }
    }

    private void showAdvancedSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        
        // 创建搜索布局
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        
        EditText etKeyword = new EditText(getContext());
        etKeyword.setHint("输入搜索关键词");
        
        // 搜索类型选择
        Spinner spinnerSearchType = new Spinner(getContext());
        String[] searchTypes = {"全部内容", "仅标题", "仅内容", "心情", "天气"};
        ArrayAdapter<String> searchAdapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_spinner_item, searchTypes);
        searchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSearchType.setAdapter(searchAdapter);
        
        layout.addView(etKeyword);
        layout.addView(spinnerSearchType);
        
        builder.setView(layout)
            .setTitle("高级搜索")
            .setPositiveButton("搜索", (dialog, which) -> {
                String keyword = etKeyword.getText().toString().trim();
                int searchType = spinnerSearchType.getSelectedItemPosition();
                performAdvancedSearch(keyword, searchType);
            })
            .setNeutralButton("按日期搜索", (dialog, which) -> {
                showDateSearchDialog();
            })
            .setNegativeButton("显示全部", (dialog, which) -> {
                setViewMode(ViewMode.ALL);
            })
            .show();
    }

    private void showDateSearchDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            
            // 设置一天的开始和结束
            selectedDate.set(Calendar.HOUR_OF_DAY, 0);
            selectedDate.set(Calendar.MINUTE, 0);
            selectedDate.set(Calendar.SECOND, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);
            long startTime = selectedDate.getTimeInMillis();
            
            selectedDate.add(Calendar.DAY_OF_MONTH, 1);
            long endTime = selectedDate.getTimeInMillis();
            
            // 搜索指定日期的日记
            List<DiaryEntry> dateResults = databaseHelper.getDiariesByDateRange(startTime, endTime);
            
            diaryList.clear();
            diaryList.addAll(dateResults);
            diaryAdapter.updateDiaryList(diaryList);
            updateEmptyState();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
            Toast.makeText(getContext(), 
                dateFormat.format(new Date(startTime)) + " 的日记: " + dateResults.size() + " 篇", 
                Toast.LENGTH_SHORT).show();
                
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void performAdvancedSearch(String keyword, int searchType) {
        Log.d(TAG, "高级搜索：" + keyword + ", 类型: " + searchType);
        try {
            if (keyword.isEmpty()) {
                Toast.makeText(getContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show();
                return;
            }
            
            List<DiaryEntry> searchResults;
            
            switch (searchType) {
                case 0: // 全部内容
                default:
                    searchResults = databaseHelper.searchDiaries(keyword);
                    break;
                case 1: // 仅标题
                case 2: // 仅内容
                case 3: // 心情
                case 4: // 天气
                    // 这些可以进一步细化搜索逻辑
                    searchResults = databaseHelper.searchDiaries(keyword);
                    break;
            }
            
            diaryList.clear();
            diaryList.addAll(searchResults);
            diaryAdapter.updateDiaryList(diaryList);
            updateEmptyState();
            
            if (diaryList.isEmpty()) {
                Toast.makeText(getContext(), "没有找到相关日记", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "找到 " + diaryList.size() + " 篇相关日记", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "搜索失败: " + e.getMessage());
            Toast.makeText(getContext(), "搜索失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStatistics() {
        try {
            int totalDiaries = databaseHelper.getDiaryCount();
            int thisMonthCount = databaseHelper.getThisMonthDiaryCount();
            
            binding.tvTotalDiaries.setText(String.valueOf(totalDiaries));
            binding.tvThisMonthDiaries.setText(String.valueOf(thisMonthCount));
            
            // 显示今日心情（如果有今日日记的话）
            String todayMood = "😊";
            List<DiaryEntry> todayDiaries = databaseHelper.getTodayDiaries();
            if (!todayDiaries.isEmpty()) {
                DiaryEntry latestToday = todayDiaries.get(0);
                switch (latestToday.getMood()) {
                    case "开心": todayMood = "😊"; break;
                    case "难过": todayMood = "😢"; break;
                    case "平静": todayMood = "😌"; break;
                    case "兴奋": todayMood = "😃"; break;
                    case "生气": todayMood = "😠"; break;
                    case "感激": todayMood = "🙏"; break;
                    case "期待": todayMood = "🤗"; break;
                }
            }
            binding.tvMoodToday.setText(todayMood);
            
            Log.d(TAG, "统计更新：总计 " + totalDiaries + " 篇，本月 " + thisMonthCount + " 篇");
        } catch (Exception e) {
            Log.e(TAG, "更新统计失败: " + e.getMessage());
        }
    }

    private void updateEmptyState() {
        if (diaryList.isEmpty()) {
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
            binding.rvDiaryList.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyState.setVisibility(View.GONE);
            binding.rvDiaryList.setVisibility(View.VISIBLE);
        }
    }

    private void checkScheduleReminders() {
        try {
            List<ScheduleEntry> upcomingSchedules = databaseHelper.getUpcomingSchedules();
            
            for (ScheduleEntry schedule : upcomingSchedules) {
                long minutesUntil = schedule.getTimeUntilSchedule();
                
                // 如果距离日程开始时间在30分钟内且未通知过
                if (minutesUntil > 0 && minutesUntil <= 30 && !schedule.isNotified()) {
                    notificationService.sendScheduleReminder(
                        "即将开始: " + schedule.getTitle(),
                        schedule.getContent() + "\n将在 " + minutesUntil + " 分钟后开始",
                        schedule.getFormattedDateTime()
                    );
                    
                    // 标记为已通知
                    databaseHelper.markScheduleAsNotified(schedule.getId());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "检查日程提醒失败: " + e.getMessage());
        }
    }

    @Override
    public void onDiaryClick(DiaryEntry diary) {
        // 显示日记详情
        showDiaryDetailDialog(diary);
    }

    @Override
    public void onDiaryLongClick(DiaryEntry diary) {
        // 显示操作菜单
        showDiaryActionDialog(diary);
    }

    private void showDiaryDetailDialog(DiaryEntry diary) {
        String detail = "标题：" + diary.getTitle() + "\n\n" +
                       "日期：" + diary.getFormattedDate() + "\n" +
                       "心情：" + diary.getMood() + "\n" +
                       "天气：" + diary.getWeather() + "\n\n" +
                       "内容：\n" + diary.getContent();
        
        new AlertDialog.Builder(getContext())
            .setTitle("日记详情")
            .setMessage(detail)
            .setPositiveButton("确定", null)
            .setNeutralButton("编辑", (dialog, which) -> showEditDiaryDialog(diary))
            .show();
    }

    private void showDiaryActionDialog(DiaryEntry diary) {
        String[] actions = {"编辑", "删除", "分享"};
        
        new AlertDialog.Builder(getContext())
            .setTitle("选择操作")
            .setItems(actions, (dialog, which) -> {
                switch (which) {
                    case 0: // 编辑
                        showEditDiaryDialog(diary);
                        break;
                    case 1: // 删除
                        showDeleteDiaryDialog(diary);
                        break;
                    case 2: // 分享
                        shareDiary(diary);
                        break;
                }
            })
            .show();
    }

    private void showEditDiaryDialog(DiaryEntry diary) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        
        // 创建编辑布局
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        
        EditText etTitle = new EditText(getContext());
        etTitle.setText(diary.getTitle());
        etTitle.setHint("请输入日记标题");
        
        EditText etContent = new EditText(getContext());
        etContent.setText(diary.getContent());
        etContent.setHint("请输入日记内容");
        etContent.setMinLines(3);
        etContent.setMaxLines(6);
        
        layout.addView(etTitle);
        layout.addView(etContent);
        
        builder.setView(layout)
            .setTitle("编辑日记")
            .setPositiveButton("保存", (dialog, which) -> {
                try {
                    String title = etTitle.getText().toString().trim();
                    String content = etContent.getText().toString().trim();
                    
                    if (title.isEmpty() || content.isEmpty()) {
                        Toast.makeText(getContext(), "请填写标题和内容", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // 更新数据库
                    databaseHelper.updateDiary(diary.getId(), title, content);
                    
                    Log.d(TAG, "日记更新成功，ID: " + diary.getId());
                    Toast.makeText(getContext(), "日记更新成功", Toast.LENGTH_SHORT).show();
                    
                    // 重新加载数据
                    loadDiariesByViewMode();
                } catch (Exception e) {
                    Log.e(TAG, "更新日记时出错: " + e.getMessage());
                    Toast.makeText(getContext(), "更新日记时出错", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void showDeleteDiaryDialog(DiaryEntry diary) {
        new AlertDialog.Builder(getContext())
            .setTitle("删除日记")
            .setMessage("确定要删除这篇日记吗？\n\n\"" + diary.getTitle() + "\"")
            .setPositiveButton("删除", (dialog, which) -> {
                try {
                    databaseHelper.deleteDiary(diary.getId());
                    Log.d(TAG, "日记删除成功，ID: " + diary.getId());
                    Toast.makeText(getContext(), "日记已删除", Toast.LENGTH_SHORT).show();
                    
                    // 重新加载数据
                    loadDiariesByViewMode();
                } catch (Exception e) {
                    Log.e(TAG, "删除日记失败: " + e.getMessage());
                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void shareDiary(DiaryEntry diary) {
        try {
            String shareText = "分享我的日记\n\n" +
                             "标题：" + diary.getTitle() + "\n" +
                             "日期：" + diary.getFormattedDate() + "\n" +
                             "心情：" + diary.getMood() + "\n\n" +
                             diary.getContent();
            
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            startActivity(android.content.Intent.createChooser(shareIntent, "分享日记"));
            
        } catch (Exception e) {
            Log.e(TAG, "分享日记失败: " + e.getMessage());
            Toast.makeText(getContext(), "分享失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "DiaryFragment onDestroyView");
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        binding = null;
    }
} 