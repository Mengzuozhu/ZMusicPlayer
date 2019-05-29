package com.mzz.zmusicplayer.song;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private PlayList mPlayList;
    private List <PlayObserver> mPlayObservers = new ArrayList <>(2);

    private Player() {
        mPlayList = new PlayList();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
    }

    public static Player getInstance() {
        return sInstance;
    }

    @Override
    public void setPlayList(PlayList list) {
        if (list == null) {
            list = new PlayList();
        }
        mPlayList = list;
    }

    @Override
    public boolean play() {
        if (isPaused) {
            mPlayer.start();
            notifyPlayStatusChanged(true);
            return true;
        }
        SongInfo currentSong = getCurrentSong();
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
        }
        return false;
    }

    @Override
    public SongInfo getCurrentSong() {
        return mPlayList.getCurrentSong();
    }

    @Override
    public boolean play(PlayList list) {
        if (list == null) return false;

        isPaused = false;
        setPlayList(list);
        return play();
    }

    @Override
    public boolean pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            isPaused = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean playPrevious() {
        isPaused = false;
        SongInfo previous = mPlayList.previous();
        notifyPlayPrevious(previous);
        return play();
    }

    @Override
    public boolean playNext() {
        isPaused = false;
        SongInfo next = mPlayList.next();
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
    public boolean seekTo(int progress) {
        if (mPlayList.isEmpty()) return false;

        SongInfo currentSong = mPlayList.getCurrentSong();
        if (currentSong != null) {
            if (progress >= currentSong.getDuration()) {
                onCompletion(mPlayer);
            } else {
                mPlayer.seekTo(progress);
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
    public int getCurrentSongDuration() {
        int duration;
        SongInfo currentSong = getCurrentSong();
        if (currentSong != null) {
            duration = currentSong.getDuration();
        } else {
            duration = getCurrentPosition();
        }
        return duration;
    }

    @Override
    public void releasePlayer() {
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
//        sInstance = null;
    }

    private void notifyPlayStatusChanged(boolean isPlaying) {
        for (PlayObserver playObserver : mPlayObservers) {
            if (playObserver != null) {
                playObserver.onPlayStatusChanged(isPlaying);
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
