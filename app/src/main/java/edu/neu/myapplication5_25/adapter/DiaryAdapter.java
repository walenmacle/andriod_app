package edu.neu.myapplication5_25.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.myapplication5_25.R;
import edu.neu.myapplication5_25.model.DiaryEntry;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {
    private List<DiaryEntry> diaryList;
    private OnDiaryClickListener listener;

    public interface OnDiaryClickListener {
        void onDiaryClick(DiaryEntry diary);
        void onDiaryLongClick(DiaryEntry diary);
    }

    public DiaryAdapter(List<DiaryEntry> diaryList, OnDiaryClickListener listener) {
        this.diaryList = diaryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diary, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryEntry diary = diaryList.get(position);
        holder.bind(diary);
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    public void updateDiaryList(List<DiaryEntry> newDiaryList) {
        if (diaryList == null || diaryList.isEmpty()) {
            this.diaryList = newDiaryList;
            notifyDataSetChanged();
        } else {
            int oldSize = this.diaryList.size();
            this.diaryList = newDiaryList;
            int newSize = newDiaryList.size();
            
            if (newSize > oldSize) {
                notifyItemRangeInserted(oldSize, newSize - oldSize);
            } else if (newSize < oldSize) {
                notifyItemRangeRemoved(newSize, oldSize - newSize);
            } else {
                notifyItemRangeChanged(0, newSize);
            }
        }
    }

    class DiaryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvDate, tvContent, tvMood, tvWeather;
        private ImageView ivMoodIcon, ivWeatherIcon;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_diary_title);
            tvDate = itemView.findViewById(R.id.tv_diary_date);
            tvContent = itemView.findViewById(R.id.tv_diary_content);
            tvMood = itemView.findViewById(R.id.tv_diary_mood);
            tvWeather = itemView.findViewById(R.id.tv_diary_weather);
            ivMoodIcon = itemView.findViewById(R.id.iv_mood_icon);
            ivWeatherIcon = itemView.findViewById(R.id.iv_weather_icon);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDiaryClick(diaryList.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onDiaryLongClick(diaryList.get(getAdapterPosition()));
                }
                return true;
            });
        }

        public void bind(DiaryEntry diary) {
            tvTitle.setText(diary.getTitle());
            tvDate.setText(diary.getFormattedDate());
            tvMood.setText(diary.getMood());
            tvWeather.setText(diary.getWeather());

            // 显示内容预览（最多两行）
            String content = diary.getContent();
            if (content.length() > 50) {
                content = content.substring(0, 50) + "...";
            }
            tvContent.setText(content);

            // 设置心情图标
            setMoodIcon(diary.getMood());
            
            // 设置天气图标
            setWeatherIcon(diary.getWeather());
        }

        private void setMoodIcon(String mood) {
            int iconRes = R.drawable.ic_mood_default;
            switch (mood) {
                case "开心":
                    iconRes = R.drawable.ic_mood_happy;
                    break;
                case "难过":
                    iconRes = R.drawable.ic_mood_sad;
                    break;
                case "平静":
                    iconRes = R.drawable.ic_mood_calm;
                    break;
                case "兴奋":
                    iconRes = R.drawable.ic_mood_excited;
                    break;
                case "生气":
                    iconRes = R.drawable.ic_mood_angry;
                    break;
                default:
                    iconRes = R.drawable.ic_mood_default;
                    break;
            }
            ivMoodIcon.setImageResource(iconRes);
        }

        private void setWeatherIcon(String weather) {
            int iconRes = R.drawable.ic_weather_sunny;
            switch (weather) {
                case "晴":
                    iconRes = R.drawable.ic_weather_sunny;
                    break;
                case "多云":
                    iconRes = R.drawable.ic_weather_cloudy;
                    break;
                case "雨":
                    iconRes = R.drawable.ic_weather_rainy;
                    break;
                case "雪":
                    iconRes = R.drawable.ic_weather_snowy;
                    break;
                default:
                    iconRes = R.drawable.ic_weather_sunny;
                    break;
            }
            ivWeatherIcon.setImageResource(iconRes);
        }
    }
} 