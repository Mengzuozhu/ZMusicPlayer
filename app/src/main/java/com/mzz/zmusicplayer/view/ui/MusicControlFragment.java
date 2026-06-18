package com.mzz.zmusicplayer.view.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mzz.zandroidcommon.common.TimeHelper;
import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.util.SongUtil;
import com.mzz.zmusicplayer.config.AppSetting;
import com.mzz.zmusicplayer.databinding.FragmentControlBinding;
import com.mzz.zmusicplayer.enums.PlayedMode;
import com.mzz.zmusicplayer.manage.ListenerManager;
import com.mzz.zmusicplayer.manage.ListenerManager.CallStateRegistration;
import com.mzz.zmusicplayer.play.IPlayer;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.play.PlayObserver;
import com.mzz.zmusicplayer.play.Player;
import com.mzz.zmusicplayer.service.SeekBarService;
import com.mzz.zmusicplayer.song.SongInfo;
import com.mzz.zmusicplayer.view.contract.MusicControlContract;
import com.mzz.zmusicplayer.view.presenter.MusicControlPresenter;

import java.util.Optional;

public class MusicControlFragment extends Fragment implements MusicControlContract.View, PlayObserver {
    private static final String ARGUMENT_PLAY_LIST = "ARGUMENT_PLAY_LIST";
    private FragmentControlBinding binding;
    private IPlayer mPlayer;
    private MusicControlContract.Presenter musicPresenter;
    private TelephonyManager mTelephonyManager;
    private final CallStateRegistration callStateRegistration = new CallStateRegistration();
    private OnAudioFocusChangeListener onAudioFocusChangeListener;
    private AudioManager audioManager;
    private SeekBarService seekBarService;

    public static MusicControlFragment newInstance(PlayList playList) {
        MusicControlFragment fragment = new MusicControlFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_PLAY_LIST, playList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentControlBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupClickListeners();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        listenPhoneState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        seekBarService.removeProgressCallback();
        ListenerManager.unregisterCallStateListener(mTelephonyManager, callStateRegistration);
        mPlayer.unregisterCallback(this);
        Optional.ofNullable(mPlayer.getPlayingSong())
                .map(SongInfo::getId)
                .ifPresent(AppSetting::setLastPlaySongId);
        musicPresenter.unsubscribe();
        binding = null;
    }

    public void updateControlPlayList(PlayList playList) {
        if (playList == null) {
            playList = new PlayList();
        }
        PlayList currentPlayList = mPlayer.getPlayList();
        if (currentPlayList != null && currentPlayList != playList) {
            playList.setPlayMode(currentPlayList.getPlayMode());
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
        binding.controlSong.ivFavorite.setImageResource(isFavorite ? R.drawable.favorite : R.drawable.favorite_white);
    }

    @Override
    public void onSwitchNext(@Nullable SongInfo next) {
        onSongUpdated(next);
    }

    @Override
    public void onSwitchPlayMode(PlayedMode playedMode) {
        ViewerHelper.showToast(getContext(), playedMode.getDesc());
        binding.controlSong.ivPlayMode.setImageResource(playedMode.getIcon());
        AppSetting.setPlayMode(playedMode);
        mPlayer.getPlayList().notifySongCountOrModeChange();
    }

    @Override
    public void onSongNameChanged(@Nullable SongInfo songInfo) {
        updateSongName(songInfo);
    }

    @Override
    public void resetAllState() {
        String undefined = this.getString(R.string.undefined);
        binding.controlSong.tvSongName.setText(undefined);
        binding.controlProgress.tvDuration.setText(undefined);
        binding.controlProgress.tvProgress.setText(undefined);
        seekBarService.removeProgressCallback();
        seekBarService.resetProgress();
        updatePlayToggle(false);
    }

    @Override
    public void onSongUpdated(@Nullable SongInfo song) {
        if (song == null) {
            return;
        }
        updateSongName(song);
        int duration = song.getDuration();
        binding.controlProgress.tvDuration.setText(TimeHelper.formatDurationToTime(duration));
        seekBarService.onSongUpdated(duration);
        onSwitchFavorite(song.getIsFavorite());
    }

    @Override
    public void updatePlayToggle(boolean isPlaying) {
        binding.controlSong.ivPlayPause.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
    }

    private void setupClickListeners() {
        binding.controlSong.ivFavorite.setOnClickListener(v -> {
            if (mPlayer != null) {
                mPlayer.switchFavorite();
            }
        });
        binding.controlSong.ivPlayPre.setOnClickListener(v -> {
            if (mPlayer != null) {
                mPlayer.playPrevious();
            }
        });
        binding.controlSong.ivPlayPause.setOnClickListener(v -> {
            if (mPlayer == null) {
                return;
            }
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            } else {
                mPlayer.play();
            }
            onPlayStatusChanged(mPlayer.isPlaying());
        });
        binding.controlSong.ivPlayNext.setOnClickListener(v -> {
            if (mPlayer != null) {
                mPlayer.playNext();
            }
        });
        binding.controlSong.ivPlayMode.setOnClickListener(v -> {
            if (mPlayer != null) {
                mPlayer.changePlayMode();
            }
        });
    }

    private void updateSongName(SongInfo song) {
        binding.controlSong.tvSongName.setText(SongUtil.joinSongShowedName(song));
    }

    private void init() {
        mPlayer = Player.getInstance();
        mPlayer.registerCallback(this);
        mPlayer.setPlayList(mPlayer.getPlayList());
        SeekBar seekBarProgress = binding.controlProgress.progressSong;
        TextView tvProgress = binding.controlProgress.tvProgress;
        seekBarService = new SeekBarService(seekBarProgress, tvProgress, mPlayer, this);
        onSongUpdated(mPlayer.getPlayingSong());
        musicPresenter = new MusicControlPresenter(getActivity(), this);
        musicPresenter.subscribe();
        PlayedMode playMode = mPlayer.getPlayList().getPlayMode();
        binding.controlSong.ivPlayMode.setImageResource(playMode.getIcon());
        initOnAudioFocusChangeListener();
    }

    private void initOnAudioFocusChangeListener() {
        audioManager = (AudioManager) ListenerManager.getSystemService(Context.AUDIO_SERVICE);
        onAudioFocusChangeListener = ListenerManager.getOnAudioFocusChangeListener();
    }

    private void listenPhoneState() {
        if (callStateRegistration.isRegistered()) {
            return;
        }
        Context context = getContext();
        if (context == null) {
            return;
        }
        mTelephonyManager = (TelephonyManager) ListenerManager.getSystemService(Context.TELEPHONY_SERVICE);
        ListenerManager.registerCallStateListener(context, mTelephonyManager, callStateRegistration);
    }

}
