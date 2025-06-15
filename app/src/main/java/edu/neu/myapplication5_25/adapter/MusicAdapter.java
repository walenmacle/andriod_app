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
import edu.neu.myapplication5_25.model.MusicItem;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private List<MusicItem> musicList;
    private OnMusicClickListener listener;
    private int currentPlayingPosition = -1;

    public interface OnMusicClickListener {
        void onMusicClick(int position);
    }

    public MusicAdapter(List<MusicItem> musicList, OnMusicClickListener listener) {
        this.musicList = musicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicItem music = musicList.get(position);
        holder.bind(music, position == currentPlayingPosition);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public void setCurrentPlayingPosition(int position) {
        int oldPosition = currentPlayingPosition;
        currentPlayingPosition = position;
        
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
        if (currentPlayingPosition != -1) {
            notifyItemChanged(currentPlayingPosition);
        }
    }

    public void updateMusicList(List<MusicItem> newMusicList) {
        if (musicList == null || musicList.isEmpty()) {
            this.musicList = newMusicList;
            notifyDataSetChanged();
        } else {
            int oldSize = this.musicList.size();
            this.musicList = newMusicList;
            int newSize = newMusicList.size();
            
            if (newSize > oldSize) {
                notifyItemRangeInserted(oldSize, newSize - oldSize);
            } else if (newSize < oldSize) {
                notifyItemRangeRemoved(newSize, oldSize - newSize);
            } else {
                notifyItemRangeChanged(0, newSize);
            }
        }
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvArtist, tvDuration;
        private ImageView ivPlayIcon, ivMusicIcon;
        private View itemView;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTitle = itemView.findViewById(R.id.tv_music_title);
            tvArtist = itemView.findViewById(R.id.tv_music_artist);
            tvDuration = itemView.findViewById(R.id.tv_music_duration);
            ivPlayIcon = itemView.findViewById(R.id.iv_play_icon);
            ivMusicIcon = itemView.findViewById(R.id.iv_music_icon);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMusicClick(getAdapterPosition());
                }
            });
        }

        public void bind(MusicItem music, boolean isCurrentlyPlaying) {
            tvTitle.setText(music.getTitle());
            tvArtist.setText(music.getArtist());
            tvDuration.setText(music.getFormattedDuration());

            if (isCurrentlyPlaying) {
                ivPlayIcon.setVisibility(View.VISIBLE);
                ivPlayIcon.setImageResource(music.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
                itemView.setBackgroundResource(R.drawable.music_item_selected_background);
            } else {
                ivPlayIcon.setVisibility(View.GONE);
                itemView.setBackgroundResource(R.drawable.music_item_background);
            }
        }
    }
} 