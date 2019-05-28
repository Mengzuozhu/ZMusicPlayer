package com.mzz.zmusicplayer.song;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * author : Mzz
 * date : 2019 2019/5/28 18:52
 * description :
 */
public class Player {
    private static final String TAG = "Player";
    private static Player sInstance = new Player();
    private MediaPlayer mPlayer;
    private SongInfo currentSong;
    private boolean isPaused;

    private Player() {
        mPlayer = new MediaPlayer();
    }

    public static Player getInstance() {
        return sInstance;
    }

    public boolean play() {
        if (isPaused) {
            mPlayer.start();
            return true;
        }
        if (currentSong != null) {
            try {
                mPlayer.reset();
                mPlayer.setDataSource(currentSong.getPath());
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e(TAG, "play: ", e);
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean play(SongInfo songInfo) {
        if (songInfo == null) return false;

        currentSong = songInfo;
        isPaused = false;
        return play();
    }

    public boolean pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            isPaused = true;
            return true;
        }
        return false;
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public int getProgress() {
        return mPlayer.getCurrentPosition();
    }

    public int getCurrentSongDuration() {
        int duration;
        if (currentSong != null) {
            duration = currentSong.getDuration();
        } else {
            duration = getProgress();
        }
        return duration;
    }

    public void releasePlayer() {
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
//        sInstance = null;
    }

}
