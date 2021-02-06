package com.mzz.zmusicplayer.view.ui;

import androidx.fragment.app.Fragment;

import com.mzz.zmusicplayer.song.ISongChangeListener;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.List;

/**
 * The type Song fragment.
 *
 * @author zuozhu.meng
 */
public abstract class SongFragment extends Fragment implements ISongChangeListener {
    /**
     * Remove.
     *
     * @param keys the keys
     */
    public abstract void remove(List<Long> keys);

    /**
     * Remove song.
     *
     * @param song the song
     */
    public abstract void removeSong(SongInfo song);
}
