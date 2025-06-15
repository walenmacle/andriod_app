package edu.neu.myapplication5_25.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.neu.myapplication5_25.R;
import edu.neu.myapplication5_25.model.MusicItem;

public class MusicService extends Service {
    private static final String TAG = "MusicService";

    private MediaPlayer mediaPlayer;
    private List<MusicItem> playlist;
    private int currentPosition = 0;
    private boolean isLooping = true;
    private boolean isPrepared = false;

    private MusicServiceListener listener;

    public interface MusicServiceListener {
        void onPlaybackStateChanged(boolean isPlaying);
        void onSongChanged(MusicItem currentSong, int position);
        void onProgressChanged(int progress, int duration);
        void onError(String error);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    private final IBinder binder = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        playlist = new ArrayList<>();
        setupMediaPlayer();
    }

    private void setupMediaPlayer() {
        mediaPlayer.setOnCompletionListener(mp -> {
            if (isLooping && playlist.size() > 0) {
                playNext();
            }
        });

        mediaPlayer.setOnPreparedListener(mp -> {
            isPrepared = true;
            mediaPlayer.start();
            if (listener != null) {
                listener.onPlaybackStateChanged(true);
                if (currentPosition < playlist.size()) {
                    listener.onSongChanged(playlist.get(currentPosition), currentPosition);
                }
            }
            startProgressUpdater();
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
            if (listener != null) {
                listener.onError("播放出错，正在尝试下一首");
            }
            playNext();
            return true;
        });
    }

    public void setPlaylist(List<MusicItem> newPlaylist) {
        this.playlist = new ArrayList<>(newPlaylist);
        currentPosition = 0;
    }

    public void play() {
        if (playlist.isEmpty()) return;

        if (isPrepared && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            if (listener != null) {
                listener.onPlaybackStateChanged(true);
            }
            startProgressUpdater();
        } else if (!isPrepared) {
            playCurrentSong();
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if (listener != null) {
                listener.onPlaybackStateChanged(false);
            }
        }
    }

    public void playNext() {
        if (playlist.isEmpty()) return;

        currentPosition = (currentPosition + 1) % playlist.size();
        playCurrentSong();
    }

    public void playPrevious() {
        if (playlist.isEmpty()) return;

        currentPosition = currentPosition > 0 ? currentPosition - 1 : playlist.size() - 1;
        playCurrentSong();
    }

    public void playAt(int position) {
        if (position >= 0 && position < playlist.size()) {
            currentPosition = position;
            playCurrentSong();
        }
    }

    private void playCurrentSong() {
        if (playlist.isEmpty() || currentPosition >= playlist.size()) return;

        try {
            mediaPlayer.reset();
            isPrepared = false;

            MusicItem currentSong = playlist.get(currentPosition);
            
            // 根据歌曲ID选择对应的资源文件
            int resourceId = getMusicResourceId(currentSong.getId());
            if (resourceId == -1) {
                if (listener != null) {
                    listener.onError("找不到音乐文件");
                }
                return;
            }
            
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            Log.e(TAG, "Error playing song", e);
            if (listener != null) {
                listener.onError("无法播放当前歌曲");
            }
        }
    }

    private int getMusicResourceId(int musicId) {
        switch (musicId) {
            case 1:
                return R.raw.music1; // 需要将music1.mp3移动到res/raw/文件夹
            case 2:
                return R.raw.music2; // 需要将music2.mp3移动到res/raw/文件夹
            case 3:
                return R.raw.music3; // 需要将music3.mp3移动到res/raw/文件夹
            default:
                return -1;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public MusicItem getCurrentSong() {
        if (!playlist.isEmpty() && currentPosition < playlist.size()) {
            return playlist.get(currentPosition);
        }
        return null;
    }

    public int getProgress() {
        if (isPrepared) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (isPrepared) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public void seekTo(int position) {
        if (isPrepared) {
            mediaPlayer.seekTo(position);
        }
    }

    public void setLooping(boolean looping) {
        this.isLooping = looping;
    }

    public void setListener(MusicServiceListener listener) {
        this.listener = listener;
    }

    private void startProgressUpdater() {
        new Thread(() -> {
            while (mediaPlayer.isPlaying()) {
                try {
                    if (listener != null && isPrepared) {
                        listener.onProgressChanged(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
} 