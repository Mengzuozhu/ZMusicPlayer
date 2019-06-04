package com.mzz.zmusicplayer.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mzz.zandroidcommon.common.DateHelper;
import com.mzz.zandroidcommon.common.EventBusHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.TimeHelper;
import com.mzz.zmusicplayer.contract.MusicPlayerContract;
import com.mzz.zmusicplayer.presenter.MusicPlayerPresenter;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;
import com.mzz.zmusicplayer.song.IPlayer;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.PlayObserver;
import com.mzz.zmusicplayer.song.Player;
import com.mzz.zmusicplayer.song.SongInfo;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The type Control fragment.
 */
public class MusicControlFragment extends Fragment implements MusicPlayerContract.View,
        PlayObserver {
    private static final String PLAY_LIST = "PLAY_LIST";
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    @BindView(R.id.tv_song_name)
    TextView tvSongName;
    @BindView(R.id.progress_song)
    SeekBar seekBarProgress;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.iv_play_pause)
    ImageView ivPlayOrPause;
    MusicControlListener controlListener;
    private int currentSongDuration;
    //与后台服务共用同一个播放器
    private IPlayer mPlayer;
    private Handler mHandler = new Handler();
    private MusicPlayerContract.Presenter musicPresenter;
    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) return;

            int currentPosition = mPlayer.getCurrentPosition();
            updateProgress(currentPosition, true);
        }

        private void updateProgress(int currentPosition, boolean isPostAgain) {
            if (currentSongDuration == 0) return;

            updateProgressTextWithDuration(currentPosition);
            float percent = (float) currentPosition / currentSongDuration;
            float progressMax = seekBarProgress.getMax();
            int progress = (int) (progressMax * percent);
            if (progress >= 0 && progress <= progressMax) {
                seekBarProgress.setProgress(progress);
                if (mPlayer.isPlaying() && isPostAgain) {
                    //在播放中，则每隔1s触发一次更新事件
                    mHandler.postDelayed(mProgressCallback, UPDATE_PROGRESS_INTERVAL);
                }
            }
        }

    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MusicControlFragment.
     */
    public static MusicControlFragment newInstance(PlayList playList) {
        MusicControlFragment fragment = new MusicControlFragment();
        Bundle args = new Bundle();
        args.putParcelable(PLAY_LIST, playList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();
        PlayList mPlayList = new PlayList();
        if (bundle != null) {
            mPlayList = bundle.getParcelable(PLAY_LIST);
        }
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        mPlayer.setPlayList(mPlayList);
        SongInfo playingSong = mPlayer.getPlayingSong();
        if (playingSong != null) {
            currentSongDuration = playingSong.getDuration();
        }
        musicPresenter = new MusicPlayerPresenter(getActivity(), this);
        musicPresenter.subscribe();
        setSeekBarListener();
        getListener();
        EventBusHelper.register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusHelper.unregister(this);
        mHandler.removeCallbacks(mProgressCallback);
        mPlayer.unregisterCallback(this);
        AppSetting.setLastPlaySongId(mPlayer.getPlayingSong().getId());
        mPlayer.releasePlayer();
        musicPresenter.unsubscribe();
    }

    private void getListener() {
        FragmentActivity activity = getActivity();
        if (activity instanceof MusicControlListener) {
            controlListener = (MusicControlListener) activity;
        }
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
                mHandler.removeCallbacks(mProgressCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!mPlayer.isPlaying()) {
                    mPlayer.play();
                }
                mPlayer.seekTo(getDuration(seekBar.getProgress()));
                if (mPlayer.isPlaying()) {
                    updateProgressBar();
                }
            }
        });
    }

    public void setPlayList(PlayList playList) {
        if (playList == null) {
            playList = new PlayList();
        }
        mPlayer.play(playList);
        onSongUpdated(mPlayer.getPlayingSong());
    }

    public void setPlayingIndex(int playingIndex) {
        mPlayer.play(playingIndex);
        onSongUpdated(mPlayer.getPlayingSong());
    }

    @Subscribe
    public void setPlayingSong(SongInfo songInfo) {
        mPlayer.play(songInfo);
        onSongUpdated(mPlayer.getPlayingSong());
    }

    public void setPlayMode(PlayedMode playMode) {
        mPlayer.setPlayMode(playMode);
    }

    @OnClick(R.id.iv_play_pause)
    public void onPlayStateChangeAction() {
        if (mPlayer == null) return;

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.play();
        }
        onPlayStatusChanged(mPlayer.isPlaying());
    }

    @OnClick(R.id.iv_play_pre)
    public void onPlayPreviousAction() {
        if (mPlayer == null) return;

        mPlayer.playPrevious();
    }

    @OnClick(R.id.iv_play_next)
    public void onPlayNextAction() {
        if (mPlayer == null) return;

        mPlayer.playNext();
    }

    private void updateProgressTextWithDuration(int playDuration) {
        tvProgress.setText(TimeHelper.formatDuration(playDuration));
    }

    private int getDuration(int seekBarProgress) {
        float percent = (float) seekBarProgress / this.seekBarProgress.getMax();
        return (int) (currentSongDuration * percent);
    }

    @Override
    public void resetAllState() {
        //重置所有状态
        String undefined = this.getString(R.string.undefined);
        tvSongName.setText(undefined);
        tvDuration.setText(undefined);
        tvProgress.setText(undefined);
        mHandler.removeCallbacks(mProgressCallback);
        updatePlayToggle(false);
        seekBarProgress.setProgress(0);
    }

    @Override
    public void onSongUpdated(@Nullable SongInfo song) {
        if (song == null) {
            return;
        }
        if (controlListener != null) {
            controlListener.updatePlaySongBackgroundColor(song);
        }
        Calendar nowTime = DateHelper.getNowTime();
        song.setLastPlayTime(nowTime.getTime());
        mPlayer.getPlayList().updateRecentSongs(song);
        //记录播放歌曲位置
        AppSetting.setLastPlaySongId(song.getId());
        tvSongName.setText(String.format("%s-%s", song.getName(), song.getArtist()));
        currentSongDuration = song.getDuration();
        tvDuration.setText(TimeHelper.formatDuration(currentSongDuration));
        updateProgressTextWithDuration(0);
    }

    @Override
    public void onSwitchPrevious(@Nullable SongInfo previous) {
        onSongUpdated(previous);
    }

    @Override
    public void onSwitchNext(@Nullable SongInfo next) {
        onSongUpdated(next);
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        if (isPlaying) {
            updateProgressBar();
        } else {
            mHandler.removeCallbacks(mProgressCallback);
        }
        updatePlayToggle(isPlaying);
    }

    private void updateProgressBar() {
        mHandler.removeCallbacks(mProgressCallback);
        mHandler.post(mProgressCallback);
    }

    @Override
    public void updatePlayToggle(boolean isPlaying) {
        ivPlayOrPause.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
    }

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    public interface MusicControlListener {
        void updatePlaySongBackgroundColor(SongInfo song);
    }
}
