package com.mzz.zmusicplayer.service;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mzz.zandroidcommon.common.TimeHelper;
import com.mzz.zmusicplayer.play.IPlayer;

import lombok.Setter;

/**
 * 进度条服务
 *
 * @author zuozhu.meng
 * @date 2020/2/23
 **/
public class SeekBarService {
    /**
     * 更新进度条的间隔，单位：ms
     */
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    private SeekBar seekBarProgress;
    private TextView tvProgress;
    private IPlayer player;
    private Handler mHandler = new Handler();
    @Setter
    private int currentSongDuration;
    private Fragment musicControlFragment;
    private Runnable progressCallback;

    public SeekBarService(SeekBar seekBarProgress, TextView tvProgress, IPlayer player, Fragment musicControlFragment) {
        this.seekBarProgress = seekBarProgress;
        this.tvProgress = tvProgress;
        this.player = player;
        this.musicControlFragment = musicControlFragment;
        progressCallback = updateProgressInTime();
        setSeekBarListener();
    }

    public void removeProgressCallback() {
        mHandler.removeCallbacks(progressCallback);
    }

    public void onSongUpdated(int duration) {
        setCurrentSongDuration(duration);
        updateProgressTextWithDuration(0);
        resetProgress();
    }

    public void updateProgressBar() {
        removeProgressCallback();
        mHandler.post(progressCallback);
    }

    public void resetProgress() {
        seekBarProgress.setProgress(0);
    }

    private void updateProgressTextWithDuration(int playDuration) {
        tvProgress.setText(TimeHelper.formatDurationToTime(playDuration));
    }

    private Runnable updateProgressInTime() {
        return new Runnable() {
            @Override
            public void run() {
                if (musicControlFragment.isDetached()) {
                    return;
                }

                int currentPosition = player.getCurrentPosition();
                updateProgress(currentPosition);
            }

            private void updateProgress(int currentPosition) {
                if (currentSongDuration == 0) {
                    return;
                }

                updateProgressTextWithDuration(currentPosition);
                float percent = (float) currentPosition / currentSongDuration;
                float progressMax = seekBarProgress.getMax();
                int progress = (int) (progressMax * percent);
                if (progress >= 0 && progress <= progressMax) {
                    seekBarProgress.setProgress(progress);
                    if (player != null && player.isPlaying()) {
                        //在播放中，则每隔1s触发一次更新事件
                        mHandler.postDelayed(progressCallback, UPDATE_PROGRESS_INTERVAL);
                    }
                }
            }

        };
    }

    private void setSeekBarListener() {
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateProgressTextWithDuration(getDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                removeProgressCallback();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!player.isPlaying()) {
                    player.play();
                }
                player.seekTo(getDuration(seekBar.getProgress()));
                if (player.isPlaying()) {
                    updateProgressBar();
                }
            }
        });
    }

    private int getDuration(int seekBarProgress) {
        float percent = (float) seekBarProgress / this.seekBarProgress.getMax();
        return (int) (currentSongDuration * percent);
    }

}
