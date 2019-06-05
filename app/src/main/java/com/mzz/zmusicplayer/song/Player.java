package com.mzz.zmusicplayer.song;

import android.media.MediaPlayer;
import android.util.Log;

import com.mzz.zmusicplayer.model.SongModel;
import com.mzz.zmusicplayer.setting.PlayedMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * author : Mzz
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
    private List <PlayObserver> mPlayObservers = new ArrayList <>(2);

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
    }

    @Override
    public boolean play() {
        if (isPaused) {
            mPlayer.start();
            notifyPlayStatusChanged(true);
            return true;
        }
        SongInfo currentSong = getPlayingSong();
        if (currentSong != null) {
            try {
                mPlayer.reset();
                mPlayer.setDataSource(currentSong.getPath());
                mPlayer.prepare();
                mPlayer.start();
                notifyPlayStatusChanged(true);
            } catch (IOException e) {
                Log.e(TAG, "play: ", e);
                notifyPlayStatusChanged(false);
                return false;
            }
            return true;
        } else {
            mPlayer.reset();
            notifyResetAllState();
        }
        return false;
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
        int songIndexById = PlayList.getSongIndexById(playList.getSongInfos(), songInfo.getId());
        return play(songIndexById);
    }

    @Override
    public void switchFavorite() {
        SongInfo playingSong = getPlayingSong();
        if (playingSong == null) {
            return;
        }
        boolean isFavorite = !playingSong.getIsFavorite();
        playingSong.setIsFavorite(isFavorite);
        SongModel.update(playingSong);
        notifyFavoriteChanged(isFavorite);
    }

    @Override
    public boolean playPrevious() {
        isPaused = false;
        SongInfo previous = playList.previous();
        notifyPlayPrevious(previous);
        return play();
    }

    @Override
    public boolean pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            isPaused = true;
            notifyPlayStatusChanged(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean playNext() {
        isPaused = false;
        SongInfo next = playList.next();
        notifyPlayNext(next);
        return play();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
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
    public boolean seekTo(int progressMilli) {
        if (playList.isEmpty()) return false;

        SongInfo currentSong = playList.getPlayingSong();
        if (currentSong != null) {
            if (progressMilli >= currentSong.getDuration()) {
                onCompletion(mPlayer);
            } else {
                mPlayer.seekTo(progressMilli);
            }
            return true;
        }
        return false;
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
        mPlayer.reset();
//        mPlayer.release();
//        mPlayer = null;
    }

    private void notifyPlayStatusChanged(boolean isPlaying) {
        for (PlayObserver playObserver : mPlayObservers) {
            if (playObserver != null) {
                playObserver.onPlayStatusChanged(isPlaying);
            }
        }
    }

    private void notifyFavoriteChanged(boolean isFavorite) {
        for (PlayObserver playObserver : mPlayObservers) {
            if (playObserver != null) {
                playObserver.onSwitchFavorite(isFavorite);
            }
        }
    }

    private void notifyPlayPrevious(SongInfo song) {
        for (PlayObserver playObserver : mPlayObservers) {
            if (playObserver != null) {
                playObserver.onSwitchPrevious(song);
            }
        }
    }

    private void notifyPlayNext(SongInfo song) {
        for (PlayObserver playObserver : mPlayObservers) {
            if (playObserver != null) {
                playObserver.onSwitchNext(song);
            }
        }
    }

    private void notifyPlayModeChanged(PlayedMode playedMode) {
        for (PlayObserver playObserver : mPlayObservers) {
            if (playObserver != null) {
                playObserver.onSwitchPlayMode(playedMode);
            }
        }
    }

    private void notifyResetAllState() {
        for (PlayObserver playObserver : mPlayObservers) {
            if (playObserver != null) {
                playObserver.resetAllState();
            }
        }
    }

    @Override
    public void registerCallback(PlayObserver playObserver) {
        mPlayObservers.add(playObserver);
    }

    @Override
    public void unregisterCallback(PlayObserver playObserver) {
        mPlayObservers.remove(playObserver);
    }

    @Override
    public void clearCallbacks() {
        mPlayObservers.clear();
    }

}
