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
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.TimeHelper;
import com.mzz.zmusicplayer.contract.MainContract;
import com.mzz.zmusicplayer.contract.MusicPlayerContract;
import com.mzz.zmusicplayer.presenter.MusicPlayerPresenter;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;
import com.mzz.zmusicplayer.song.IPlayer;
import com.mzz.zmusicplayer.song.PlayList;
import com.mzz.zmusicplayer.song.PlayObserver;
import com.mzz.zmusicplayer.song.Player;
import com.mzz.zmusicplayer.song.SongInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The type Control fragment.
 */
public class MusicControlFragment extends Fragment implements MusicPlayerContract.View,
        PlayObserver {
    private static final String SONG_INFO = "SONG_INFO";
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    @BindView(R.id.tv_song_name)
    TextView tvSongName;
    @BindView(R.id.tv_artist)
    TextView tvArtist;
    @BindView(R.id.progress_song)
    CircularProgressBar seekBarProgress;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.iv_play_pause)
    ImageView ivPlayOrPause;
    //与后台服务共用同一个播放器
    private IPlayer mPlayer;
    private Handler mHandler = new Handler();
    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) return;

            updateProgressTextWithDuration(mPlayer.getCurrentPosition());
            float progressMax = seekBarProgress.getProgressMax();
            float percent =
                    (float) mPlayer.getCurrentPosition() / mPlayer.getCurrentSongDuration();
            int progress = (int) (progressMax * percent);
            if (progress >= 0 && progress <= progressMax) {
                seekBarProgress.setProgress(progress);
                if (mPlayer.isPlaying()) {
                    //在播放中，则每隔1s触发一次更新事件
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
                }
            }
        }
    };
    private MainContract.Presenter mainPresenter;
    private MusicPlayerContract.Presenter mPresenter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MusicControlFragment.
     */
    public static MusicControlFragment newInstance(PlayList playList) {
        MusicControlFragment fragment = new MusicControlFragment();
        Bundle args = new Bundle();
        args.putParcelable(SONG_INFO, playList);
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
            mPlayList = bundle.getParcelable(SONG_INFO);
        }
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        mPlayer.setPlayList(mPlayList);
        mPresenter = new MusicPlayerPresenter(getActivity(), this);
        mPresenter.subscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mProgressCallback);
        mPlayer.unregisterCallback(this);
        AppSetting.setLastPlaySongIndex(mPlayer.getPlayingIndex());
        mPresenter.unsubscribe();
    }

    public void setMainPresenter(MainContract.Presenter mainPresenter) {
        this.mainPresenter = mainPresenter;
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

    private void updateProgressTextWithDuration(int duration) {
        tvProgress.setText(TimeHelper.formatDuration(duration));
    }

    @Override
    public void onSongUpdated(@Nullable SongInfo song) {
        if (song == null) {
            return;
        }
        if (mainPresenter != null) {
            mainPresenter.updatePlaySongBackgroundColor(song);
        }
        //记录播放歌曲位置
        AppSetting.setLastPlaySongIndex(mPlayer.getPlayingIndex());
        tvSongName.setText(song.getName());
        tvArtist.setText(song.getArtist());
        tvDuration.setText(TimeHelper.formatDuration(mPlayer.getCurrentSongDuration()));
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

}
