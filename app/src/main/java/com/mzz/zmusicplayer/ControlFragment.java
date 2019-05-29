package com.mzz.zmusicplayer;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mzz.zmusicplayer.common.TimeHelper;
import com.mzz.zmusicplayer.song.Player;
import com.mzz.zmusicplayer.song.SongInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * The type Control fragment.
 */
public class ControlFragment extends Fragment {
    private static final String SONG_INFO = "SONG_INFO";
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    SongInfo songInfo;
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
    Unbinder unbinder;
    Player mPlayer;
    private Handler mHandler = new Handler();

    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDetached()) return;

            if (mPlayer.isPlaying()) {
                float progressMax = seekBarProgress.getProgressMax();
                float percent = (float) mPlayer.getProgress() / mPlayer.getCurrentSongDuration();
                float progress = progressMax * percent;
                updateProgressTextWithDuration(mPlayer.getProgress());
                if (progress >= 0 && progress <= progressMax) {
                    seekBarProgress.setProgress(progress);
                    //每隔1s触发一次更新事件
                    mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
                }
            }
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ControlFragment.
     */
    public static ControlFragment newInstance(SongInfo songInfo) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putParcelable(SONG_INFO, songInfo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            songInfo = bundle.getParcelable(SONG_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        mPlayer = Player.getInstance();
//        mPlayer.play(songInfo);
//        updatePlayState();
//        tvDuration.setText(TimeHelper.formatDuration(mPlayer.getCurrentSongDuration()));
        return view;
    }

    private void initView() {
        if (songInfo == null) {
            return;
        }
        tvSongName.setText(songInfo.getName());
        tvArtist.setText(songInfo.getArtist());
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.iv_play_pause)
    public void onPlayStateChangeAction() {
        if (mPlayer == null) return;

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.play();
        }
        updatePlayState();
    }

    private void updateProgressTextWithDuration(int duration) {
        tvProgress.setText(TimeHelper.formatDuration(duration));
    }

    public void onSongUpdated(@Nullable SongInfo song) {
    }

    public void updatePlayState() {
        boolean isPlay = mPlayer.isPlaying();
        if (isPlay) {
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        } else {
            mHandler.removeCallbacks(mProgressCallback);
        }
        ivPlayOrPause.setImageResource(isPlay ? R.drawable.pause : R.drawable.play);
    }

}
