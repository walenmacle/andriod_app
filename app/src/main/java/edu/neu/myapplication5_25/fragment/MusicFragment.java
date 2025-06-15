package edu.neu.myapplication5_25.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import edu.neu.myapplication5_25.R;
import edu.neu.myapplication5_25.adapter.MusicAdapter;
import edu.neu.myapplication5_25.databinding.FragmentMusicBinding;
import edu.neu.myapplication5_25.model.MusicItem;
import edu.neu.myapplication5_25.service.MusicService;

public class MusicFragment extends Fragment implements MusicService.MusicServiceListener {
    private FragmentMusicBinding binding;
    private MusicService musicService;
    private boolean isBound = false;
    private MusicAdapter musicAdapter;
    private List<MusicItem> musicList;
    private Handler handler = new Handler();
    private boolean isSeekBarDragging = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicService.setListener(MusicFragment.this);
            isBound = true;
            
            // 设置播放列表
            musicService.setPlaylist(musicList);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            musicService = null;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMusicBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupMusicPlayer();
        setupMusicList();
        bindMusicService();
    }

    private void setupMusicPlayer() {
        // 初始化播放列表
        musicList = createMusicList();
        
        // 设置播放控制按钮
        binding.btnPlayPause.setOnClickListener(v -> {
            if (isBound) {
                if (musicService.isPlaying()) {
                    musicService.pause();
                } else {
                    musicService.play();
                }
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (isBound) {
                musicService.playNext();
            }
        });

        binding.btnPrevious.setOnClickListener(v -> {
            if (isBound) {
                musicService.playPrevious();
            }
        });

        binding.btnLoop.setOnClickListener(v -> {
            if (isBound) {
                // 切换循环模式（这里简化为总是循环）
                Toast.makeText(getContext(), "循环播放模式", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置进度条
        binding.seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isBound) {
                    int duration = musicService.getDuration();
                    int newPosition = (progress * duration) / 100;
                    musicService.seekTo(newPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarDragging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekBarDragging = false;
            }
        });
    }

    private void setupMusicList() {
        binding.rvMusicList.setLayoutManager(new LinearLayoutManager(getContext()));
        musicAdapter = new MusicAdapter(musicList, position -> {
            if (isBound) {
                musicService.playAt(position);
                musicAdapter.setCurrentPlayingPosition(position);
            }
        });
        binding.rvMusicList.setAdapter(musicAdapter);
        
        // 更新播放列表数量
        binding.tvPlaylistCount.setText(musicList.size() + "首歌曲");
    }

    private List<MusicItem> createMusicList() {
        List<MusicItem> list = new ArrayList<>();
        // 添加res/raw文件夹中的三首歌
        list.add(new MusicItem(1, "轻快音乐1", "未知艺术家", "music1", 210000)); // 3:30
        list.add(new MusicItem(2, "优美音乐2", "未知艺术家", "music2", 240000)); // 4:00  
        list.add(new MusicItem(3, "动感音乐3", "未知艺术家", "music3", 195000)); // 3:15
        return list;
    }

    private void bindMusicService() {
        Intent intent = new Intent(getContext(), MusicService.class);
        getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void updateUI() {
        if (!isBound || musicService == null) return;

        // 更新当前播放歌曲信息
        MusicItem currentSong = musicService.getCurrentSong();
        if (currentSong != null) {
            binding.tvCurrentSongTitle.setText(currentSong.getTitle());
            binding.tvCurrentSongArtist.setText(currentSong.getArtist());
        }

        // 更新播放按钮
        binding.btnPlayPause.setImageResource(
            musicService.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play
        );

        // 更新播放列表中的当前播放项
        musicAdapter.setCurrentPlayingPosition(musicService.getCurrentPosition());
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void onPlaybackStateChanged(boolean isPlaying) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                binding.btnPlayPause.setImageResource(
                    isPlaying ? R.drawable.ic_pause : R.drawable.ic_play
                );
            });
        }
    }

    @Override
    public void onSongChanged(MusicItem currentSong, int position) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                binding.tvCurrentSongTitle.setText(currentSong.getTitle());
                binding.tvCurrentSongArtist.setText(currentSong.getArtist());
                musicAdapter.setCurrentPlayingPosition(position);
            });
        }
    }

    @Override
    public void onProgressChanged(int progress, int duration) {
        if (getActivity() != null && !isSeekBarDragging) {
            getActivity().runOnUiThread(() -> {
                binding.tvCurrentTime.setText(formatTime(progress));
                binding.tvTotalTime.setText(formatTime(duration));
                
                if (duration > 0) {
                    int progressPercent = (progress * 100) / duration;
                    binding.seekBarProgress.setProgress(progressPercent);
                }
            });
        }
    }

    @Override
    public void onError(String error) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isBound) {
            getContext().unbindService(serviceConnection);
            isBound = false;
        }
        binding = null;
    }
} 