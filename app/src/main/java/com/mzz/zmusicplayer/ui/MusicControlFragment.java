package com.mzz.zmusicplayer.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.TimeHelper;
import com.mzz.zmusicplayer.contract.MusicControlContract;
import com.mzz.zmusicplayer.presenter.MusicControlPresenter;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;
import com.mzz.zmusicplayer.play.IPlayer;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.play.PlayObserver;
import com.mzz.zmusicplayer.play.Player;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The type Control fragment.
 */
public class MusicControlFragment extends Fragment implements MusicControlContract.View,
        PlayObserver {
    private static final String ARGUMENT_PLAY_LIST = "ARGUMENT_PLAY_LIST";
    //更新进度条的间隔，单位：ms
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    @BindView(R.id.progress_song)
    SeekBar seekBarProgress;
    @BindView(R.id.tv_song_name)
    TextView tvSongName;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.iv_play_pause)
    ImageView ivPlayOrPause;
    @BindView(R.id.iv_favorite)
    ImageView ivFavorite;
    @BindView(R.id.iv_play_mode)
    ImageView ivPlayMode;
    private int currentSongDuration;
    //与后台服务共用同一个播放器
    private IPlayer mPlayer;
    private Handler mHandler = new Handler();
    private MusicControlContract.Presenter musicPresenter;
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
        args.putParcelable(ARGUMENT_PLAY_LIST, playList);
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

        init();
    }

    private void init() {
        Bundle bundle = getArguments();
        PlayList mPlayList = new PlayList();
        if (bundle != null) {
            mPlayList = bundle.getParcelable(ARGUMENT_PLAY_LIST);
        }
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        mPlayer.setPlayList(mPlayList);
        SongInfo playingSong = mPlayer.getPlayingSong();
        if (playingSong != null) {
            currentSongDuration = playingSong.getDuration();
            onSongUpdated(playingSong);
        }
        musicPresenter = new MusicControlPresenter(getActivity(), this);
        musicPresenter.subscribe();
        ivPlayMode.setImageResource(AppSetting.getPlayMode().getIcon());
        setSeekBarListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeProgressCallback();
        mPlayer.unregisterCallback(this);
        AppSetting.setLastPlaySongId(mPlayer.getPlayingSong().getId());
        mPlayer.releasePlayer();
        musicPresenter.unsubscribe();
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

    public void updateControlPlayList(PlayList playList) {
        if (playList == null) {
            playList = new PlayList();
        }

        playList.updatePlayingIndexBySettingId();
        if (mPlayer.isPlaying()) {
            mPlayer.play(playList);
        } else {
            mPlayer.setPlayList(playList);
        }
        onSongUpdated(playList.getPlayingSong());
    }

    public void updatePlayingIndex(int playingIndex) {
        mPlayer.play(playingIndex);
        onSongUpdated(mPlayer.getPlayingSong());
    }

    public void updatePlayingSong(SongInfo song) {
        mPlayer.play(song);
        onSongUpdated(mPlayer.getPlayingSong());
    }

    @OnClick(R.id.iv_favorite)
    public void onFavoriteChangeAction() {
        if (mPlayer == null) return;

        mPlayer.switchFavorite();
    }

    @OnClick(R.id.iv_play_pre)
    public void onPlayPreviousAction() {
        if (mPlayer == null) return;

        mPlayer.playPrevious();
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

    @OnClick(R.id.iv_play_next)
    public void onPlayNextAction() {
        if (mPlayer == null) return;

        mPlayer.playNext();
    }

    @OnClick(R.id.iv_play_mode)
    public void onPlayModeAction() {
        if (mPlayer == null) return;

        mPlayer.changePlayMode();
    }

    @Override
    public void resetAllState() {
        //重置所有状态
        String undefined = this.getString(R.string.undefined);
        tvSongName.setText(undefined);
        tvDuration.setText(undefined);
        tvProgress.setText(undefined);
        removeProgressCallback();
        updatePlayToggle(false);
        seekBarProgress.setProgress(0);
    }

    @Override
    public void onSongUpdated(@Nullable SongInfo song) {
        if (song == null) {
            return;
        }
        song.setLastPlayTime(new Date());
        mPlayer.getPlayList().updateRecentSongs(song);
        //记录播放歌曲ID
        AppSetting.setLastPlaySongId(song.getId());
        tvSongName.setText(String.format("%s-%s", song.getName(), song.getArtist()));
        currentSongDuration = song.getDuration();
        tvDuration.setText(TimeHelper.formatDuration(currentSongDuration));
        updateProgressTextWithDuration(0);
        onSwitchFavorite(song.getIsFavorite());
        seekBarProgress.setProgress(0);
    }

    @Override
    public void onSwitchPrevious(@Nullable SongInfo previous) {
        onSongUpdated(previous);
    }

    @Override
    public void onSwitchFavorite(boolean isFavorite) {
        ivFavorite.setImageResource(isFavorite ? R.drawable.favorite : R.drawable.favorite_white);
    }

    @Override
    public void onSwitchNext(@Nullable SongInfo next) {
        onSongUpdated(next);
    }

    @Override
    public void onSwitchPlayMode(PlayedMode playedMode) {
        ViewerHelper.showToast(getContext(), playedMode.getDesc());
        ivPlayMode.setImageResource(playedMode.getIcon());
        AppSetting.setPlayMode(playedMode);
        mPlayer.getPlayList().notifySongCountOrModeChange();
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        if (isPlaying) {
            updateProgressBar();
        } else {
            removeProgressCallback();
        }
        updatePlayToggle(isPlaying);
    }

    @Override
    public void updatePlayToggle(boolean isPlaying) {
        ivPlayOrPause.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
    }

    private void updateProgressTextWithDuration(int playDuration) {
        tvProgress.setText(TimeHelper.formatDuration(playDuration));
    }

    private int getDuration(int seekBarProgress) {
        float percent = (float) seekBarProgress / this.seekBarProgress.getMax();
        return (int) (currentSongDuration * percent);
    }

    private void updateProgressBar() {
        removeProgressCallback();
        mHandler.post(mProgressCallback);
    }

    private void removeProgressCallback() {
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void handleError(Throwable error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
