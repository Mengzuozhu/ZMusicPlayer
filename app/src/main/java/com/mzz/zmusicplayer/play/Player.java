package com.mzz.zmusicplayer.play;

import android.media.MediaPlayer;
import android.util.Log;

import com.mzz.zandroidcommon.view.ViewerHelper;
import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.config.AppSetting;
import com.mzz.zmusicplayer.config.PlayedMode;
import com.mzz.zmusicplayer.song.FavoriteSong;
import com.mzz.zmusicplayer.song.SongInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * @author : Mzz
 * date : 2019 2019/5/28 18:52
 * description :
 */
public class Player implements IPlayer, MediaPlayer.OnCompletionListener {
    private static final String TAG = "Player";
    private static Player sInstance = new Player();
    private MediaPlayer mPlayer;
    private boolean isPaused;
    @Getter
    private PlayList playList;
    private List<PlayObserver> playObservers = new ArrayList<>(2);

    private Player() {
        playList = new PlayList();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
    }

    /**
     * Gets single instance.单例
     *
     * @return the single instance
     */
    public static Player getInstance() {
        return sInstance;
    }

    @Override
    public void setPlayList(PlayList list) {
        if (list == null) {
            list = new PlayList();
        }
        playList = list;
        //保证更新播放列表后，可以重新开始播放新歌
        isPaused = false;
    }

    @Override
    public boolean play() {
        if (isPaused) {
            mPlayer.start();
            notifyPlayStatusChanged(true);
            isPaused = false;
            return true;
        }
        SongInfo playingSong = getPlayingSong();
        if (playingSong != null) {
            return startNewSong(playingSong);
        } else {
            mPlayer.reset();
            notifyResetAllState();
        }
        return false;
    }

    private boolean startNewSong(SongInfo playingSong) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(playingSong.getPath());
            mPlayer.prepare();
            mPlayer.start();
            notifyPlayStatusChanged(true);
            recordPlayingSong(playingSong);
        } catch (IOException e) {
            Log.e(TAG, "play: ", e);
            ViewerHelper.showToast(MusicApplication.getContext(), String.format("歌曲(%s)播放失败！"
                    , playingSong.getName()));
            playNext();
            return false;
        }
        return true;
    }

    @Override
    public boolean play(PlayList playList) {
        if (playList == null) return false;

        isPaused = false;
        setPlayList(playList);
        return play();
    }

    @Override
    public boolean play(int playingIndex) {
        playList.setPlayingIndex(playingIndex);
        isPaused = false;
        return play();
    }

    @Override
    public boolean play(SongInfo songInfo) {
        List<SongInfo> playSongs = playList.getPlaySongs();
        int songIndexById = PlayList.getSongIndexById(playSongs, songInfo.getId());
        if (songIndexById == -1) {
            songInfo.setIsChecked(true);
            playList.addSong(songInfo);
            songIndexById = playSongs.size() - 1;
        }
        return play(songIndexById);
    }

    @Override
    public SongInfo getPlayingSong() {
        return playList.getPlayingSong();
    }

    @Override
    public void changePlayMode() {
        PlayedMode playMode = playList.getPlayMode();
        playMode = playMode.getNextMode();
        playList.setPlayMode(playMode);
        notifyPlayModeChanged(playMode);
    }

    @Override
    public void switchFavorite() {
        SongInfo playingSong = getPlayingSong();
        if (playingSong == null) {
            return;
        }
        boolean isFavorite = FavoriteSong.getInstance().switchFavorite(playingSong);
        notifyFavoriteChanged(isFavorite);
    }

    @Override
    public void playPrevious() {
        isPaused = false;
        SongInfo previous = playList.previous();
        notifyPlayPrevious(previous);
        play();
    }

    @Override
    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            isPaused = true;
            notifyPlayStatusChanged(false);
        }
    }

    @Override
    public void playNext() {
        isPaused = false;
        SongInfo next = playList.next();
        notifyPlayNext(next);
        play();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    private void recordPlayingSong(SongInfo song) {
        //记录播放歌曲信息
        song.setLastPlayTime(System.currentTimeMillis());
        song.addPlayCount();
        playList.updateRecentSongs(song);
        //记录播放歌曲ID
        AppSetting.setLastPlaySongId(song.getId());
    }

    @Override
    public int getCurrentPosition() {
        int position = mPlayer.getCurrentPosition();
        if (position == 0) {
            position = 1;
        }
        return position;
    }

    @Override
    public void seekTo(int progressMilli) {
        if (playList.isEmpty()) return;

        SongInfo currentSong = playList.getPlayingSong();
        if (currentSong != null) {
            if (progressMilli >= currentSong.getDuration()) {
                onCompletion(mPlayer);
            } else {
                mPlayer.seekTo(progressMilli);
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
    }

    @Override
    public void releasePlayer() {
        if (mPlayer == null) {
            return;
        }
        playObservers.clear();
        mPlayer.reset();
        mPlayer.release();
    }

    private void notifyPlayStatusChanged(boolean isPlaying) {
        for (PlayObserver playObserver : playObservers) {
            if (playObserver != null) {
                playObserver.onPlayStatusChanged(isPlaying);
            }
        }
    }

    private void notifyFavoriteChanged(boolean isFavorite) {
        for (PlayObserver playObserver : playObservers) {
            if (playObserver != null) {
                playObserver.onSwitchFavorite(isFavorite);
            }
        }
    }

    private void notifyPlayPrevious(SongInfo song) {
        for (PlayObserver playObserver : playObservers) {
            if (playObserver != null) {
                playObserver.onSwitchPrevious(song);
            }
        }
    }

    private void notifyPlayNext(SongInfo song) {
        for (PlayObserver playObserver : playObservers) {
            if (playObserver != null) {
                playObserver.onSwitchNext(song);
            }
        }
    }

    private void notifyPlayModeChanged(PlayedMode playedMode) {
        for (PlayObserver playObserver : playObservers) {
            if (playObserver != null) {
                playObserver.onSwitchPlayMode(playedMode);
            }
        }
    }

    private void notifyResetAllState() {
        for (PlayObserver playObserver : playObservers) {
            if (playObserver != null) {
                playObserver.resetAllState();
            }
        }
    }

    @Override
    public void registerCallback(PlayObserver playObserver) {
        playObservers.add(playObserver);
    }

    @Override
    public void unregisterCallback(PlayObserver playObserver) {
        playObservers.remove(playObserver);
    }

}
