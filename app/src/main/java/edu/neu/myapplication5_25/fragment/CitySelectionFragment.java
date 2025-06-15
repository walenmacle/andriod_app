package edu.neu.myapplication5_25.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.myapplication5_25.R;
import edu.neu.myapplication5_25.adapter.CityAdapter;
import edu.neu.myapplication5_25.databinding.FragmentCitySelectionBinding;
import edu.neu.myapplication5_25.model.City;

public class CitySelectionFragment extends Fragment implements CityAdapter.OnCityClickListener {
    private FragmentCitySelectionBinding binding;
    private CityAdapter cityAdapter;
    private List<City> allCities;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCitySelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews();
        loadCities();
    }

    private void setupViews() {
        // 设置RecyclerView
        binding.recyclerViewCities.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 初始化适配器
        allCities = City.getChinaCities();
        cityAdapter = new CityAdapter(allCities);
        cityAdapter.setOnCityClickListener(this);
        binding.recyclerViewCities.setAdapter(cityAdapter);

        // 设置搜索功能
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCities(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 设置返回按钮
        binding.buttonBack.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        // 设置清空搜索按钮
        binding.buttonClearSearch.setOnClickListener(v -> {
            binding.editTextSearch.setText("");
        });
    }

    private void loadCities() {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // 模拟加载延迟（实际项目中可能从网络或数据库加载）
        binding.getRoot().postDelayed(() -> {
            cityAdapter.updateData(allCities);
            binding.progressBar.setVisibility(View.GONE);
            
            // 显示城市数量
            binding.textCityCount.setText(String.format("共 %d 个城市", allCities.size()));
        }, 500);
    }

    private void filterCities(String keyword) {
        List<City> filteredCities = City.searchCities(keyword);
        cityAdapter.updateData(filteredCities);
        
        // 更新城市数量显示
        binding.textCityCount.setText(String.format("共 %d 个城市", filteredCities.size()));
        
        // 如果没有搜索结果，显示提示
        if (filteredCities.isEmpty() && !keyword.trim().isEmpty()) {
            binding.textNoResults.setVisibility(View.VISIBLE);
            binding.textNoResults.setText("未找到匹配的城市");
        } else {
            binding.textNoResults.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCityClick(City city) {
        try {
            // 保存选中的城市到SharedPreferences
            if (getContext() != null) {
                getContext().getSharedPreferences("weather_prefs", 0)
                        .edit()
                        .putString("selected_city", city.getName())
                        .putString("selected_province", city.getProvince())
                        .apply();
            }

            Toast.makeText(getContext(), "已选择: " + city.getName(), Toast.LENGTH_SHORT).show();

            // 返回上一个页面
            try {
                NavController navController = Navigation.findNavController(requireView());
                navController.navigateUp();
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "选择城市失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 