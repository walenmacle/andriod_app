package edu.neu.myapplication5_25.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.neu.myapplication5_25.R;
import edu.neu.myapplication5_25.model.DailyWeather;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.ViewHolder> {
    private List<DailyWeather> dailyWeatherList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(DailyWeather dailyWeather);
    }

    public DailyWeatherAdapter(List<DailyWeather> dailyWeatherList) {
        this.dailyWeatherList = dailyWeatherList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void updateData(List<DailyWeather> newData) {
        if (dailyWeatherList == null || dailyWeatherList.isEmpty()) {
            this.dailyWeatherList = newData;
            notifyDataSetChanged();
            return;
        }
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return dailyWeatherList.size();
            }

            @Override
            public int getNewListSize() {
                return newData.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return dailyWeatherList.get(oldItemPosition).getDate()
                        .equals(newData.get(newItemPosition).getDate());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                DailyWeather oldItem = dailyWeatherList.get(oldItemPosition);
                DailyWeather newItem = newData.get(newItemPosition);
                return oldItem.equals(newItem);
            }
        });
        
        this.dailyWeatherList = newData;
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyWeather dailyWeather = dailyWeatherList.get(position);
        holder.bind(dailyWeather);
    }

    @Override
    public int getItemCount() {
        return dailyWeatherList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvWeatherDescription;
        private TextView tvMaxTemp;
        private TextView tvMinTemp;
        private ImageView ivWeatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvWeatherDescription = itemView.findViewById(R.id.tv_weather_description);
            tvMaxTemp = itemView.findViewById(R.id.tv_max_temp);
            tvMinTemp = itemView.findViewById(R.id.tv_min_temp);
            ivWeatherIcon = itemView.findViewById(R.id.iv_weather_icon);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(dailyWeatherList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(DailyWeather dailyWeather) {
            // 格式化日期显示
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
                Date date = inputFormat.parse(dailyWeather.getDate());
                if (date != null) {
                    tvDate.setText(outputFormat.format(date));
                } else {
                    tvDate.setText(dailyWeather.getDate());
                }
            } catch (Exception e) {
                tvDate.setText(dailyWeather.getDate());
            }

            tvWeatherDescription.setText(dailyWeather.getDescription());
            tvMaxTemp.setText(String.format(Locale.getDefault(), "%.0f°", dailyWeather.getMaxTemp()));
            tvMinTemp.setText(String.format(Locale.getDefault(), "%.0f°", dailyWeather.getMinTemp()));

            // 根据天气状况设置图标
            setWeatherIcon(dailyWeather.getWeatherMain());
        }

        private void setWeatherIcon(String weatherMain) {
            int iconRes;
            if (weatherMain == null) {
                iconRes = R.drawable.ic_weather;
                ivWeatherIcon.setImageResource(iconRes);
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
            ivWeatherIcon.setImageResource(iconRes);
        }
    }
} 