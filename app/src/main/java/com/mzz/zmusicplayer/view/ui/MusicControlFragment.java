package com.mzz.zmusicplayer.view.ui;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mzz.zandroidcommon.common.TimeHelper;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.config.AppSetting;
import com.mzz.zmusicplayer.config.PlayedMode;
import com.mzz.zmusicplayer.manage.ListenerManager;
import com.mzz.zmusicplayer.play.IPlayer;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.play.PlayObserver;
import com.mzz.zmusicplayer.play.Player;
import com.mzz.zmusicplayer.service.SeekBarService;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.contract.MusicControlContract;
import com.mzz.zmusicplayer.view.presenter.MusicControlPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The type Control fragment.
 *
 * @author Mengzz
 */
public class MusicControlFragment extends Fragment implements MusicControlContract.View, PlayObserver {
    private static final String ARGUMENT_PLAY_LIST = "ARGUMENT_PLAY_LIST";
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
    /**
     * 与后台服务共用同一个播放器
     */
    private IPlayer mPlayer;
    private MusicControlContract.Presenter musicPresenter;
    private TelephonyManager mTelephonyManager;
    private PhoneStateListener phoneStateListener;
    private OnAudioFocusChangeListener onAudioFocusChangeListener;
    private AudioManager audioManager;
    private SeekBarService seekBarService;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        seekBarService.removeProgressCallback();
        if (mTelephonyManager != null && phoneStateListener != null) {
            mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        mPlayer.unregisterCallback(this);
        AppSetting.setLastPlaySongId(mPlayer.getPlayingSong().getId());
        mPlayer.releasePlayer();
        musicPresenter.unsubscribe();
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
        if (mPlayer != null) {
            mPlayer.switchFavorite();
        }
    }

    @OnClick(R.id.iv_play_pre)
    public void onPlayPreviousAction() {
        if (mPlayer != null) {
            mPlayer.playPrevious();
        }
    }

    @OnClick(R.id.iv_play_pause)
    public void onPlayStateChangeAction() {
        if (mPlayer == null) {
            return;
        }

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.play();
        }
        onPlayStatusChanged(mPlayer.isPlaying());
    }

    @OnClick(R.id.iv_play_next)
    public void onPlayNextAction() {
        if (mPlayer == null) {
            return;
        }

        mPlayer.playNext();
    }

    @OnClick(R.id.iv_play_mode)
    public void onPlayModeAction() {
        if (mPlayer == null) {
            return;
        }

        mPlayer.changePlayMode();
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        if (isPlaying) {
            seekBarService.updateProgressBar();
            audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        } else {
            seekBarService.removeProgressCallback();
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
        updatePlayToggle(isPlaying);
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
    public void resetAllState() {
        //重置所有状态
        String undefined = this.getString(R.string.undefined);
        tvSongName.setText(undefined);
        tvDuration.setText(undefined);
        tvProgress.setText(undefined);
        seekBarService.removeProgressCallback();
        seekBarService.resetProgress();
        updatePlayToggle(false);
    }

    @Override
    public void handleError(Throwable error) {
        Log.e("MusicControlFragment", error.getMessage(), error);
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSongUpdated(@Nullable SongInfo song) {
        if (song == null) {
            return;
        }
        tvSongName.setText(String.format("%s-%s", song.getName(), song.getArtist()));
        int duration = song.getDuration();
        tvDuration.setText(TimeHelper.formatDurationToTime(duration));
        seekBarService.onSongUpdated(duration);
        onSwitchFavorite(song.getIsFavorite());
    }

    @Override
    public void updatePlayToggle(boolean isPlaying) {
        ivPlayOrPause.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
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
        seekBarService = new SeekBarService(seekBarProgress, tvProgress, mPlayer, this);
        SongInfo playingSong = mPlayer.getPlayingSong();
        if (playingSong != null) {
            seekBarService.setCurrentSongDuration(playingSong.getDuration());
            onSongUpdated(playingSong);
        }
        musicPresenter = new MusicControlPresenter(getActivity(), this);
        musicPresenter.subscribe();
        ivPlayMode.setImageResource(AppSetting.getPlayMode().getIcon());
        listenPhoneState();
        initOnAudioFocusChangeListener();
    }

    private void initOnAudioFocusChangeListener() {
        audioManager = (AudioManager) ListenerManager.getSystemService(Context.AUDIO_SERVICE);
        onAudioFocusChangeListener = ListenerManager.getOnAudioFocusChangeListener();
    }

    private void listenPhoneState() {
        mTelephonyManager = (TelephonyManager) ListenerManager.getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = ListenerManager.getMusicPhoneStateListener();
        mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

}
