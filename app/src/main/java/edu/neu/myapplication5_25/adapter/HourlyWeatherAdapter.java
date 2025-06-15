package edu.neu.myapplication5_25.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import edu.neu.myapplication5_25.R;
import edu.neu.myapplication5_25.model.HourlyWeather;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {
    private List<HourlyWeather> hourlyWeatherList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(HourlyWeather hourlyWeather);
    }

    public HourlyWeatherAdapter(List<HourlyWeather> hourlyWeatherList) {
        this.hourlyWeatherList = hourlyWeatherList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void updateData(List<HourlyWeather> newData) {
        if (hourlyWeatherList == null || hourlyWeatherList.isEmpty()) {
            this.hourlyWeatherList = newData;
            notifyDataSetChanged();
        } else {
            int oldSize = this.hourlyWeatherList.size();
            this.hourlyWeatherList = newData;
            int newSize = newData.size();
            
            if (newSize > oldSize) {
                notifyItemRangeInserted(oldSize, newSize - oldSize);
            } else if (newSize < oldSize) {
                notifyItemRangeRemoved(newSize, oldSize - newSize);
            } else {
                notifyItemRangeChanged(0, newSize);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);
        holder.bind(hourlyWeather);
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime;
        private TextView tvTemperature;
        private TextView tvDescription;
        private ImageView ivWeatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTemperature = itemView.findViewById(R.id.tv_temperature);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ivWeatherIcon = itemView.findViewById(R.id.iv_weather_icon);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(hourlyWeatherList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(HourlyWeather hourlyWeather) {
            tvTime.setText(hourlyWeather.getTime());
            tvTemperature.setText(String.format(Locale.getDefault(), "%.0f°", hourlyWeather.getTemperature()));
            tvDescription.setText(hourlyWeather.getDescription());

            // 根据天气状况设置图标
            setWeatherIcon(hourlyWeather.getWeatherMain());
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