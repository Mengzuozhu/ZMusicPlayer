package com.mzz.zmusicplayer.song;

import com.mzz.zmusicplayer.model.LocalSongModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/6/9 20:01
 * description :
 */
public class FavoriteSong {
    private static final FavoriteSong singleton = new FavoriteSong();
    private List <SongInfo> favoriteSongs;

    private FavoriteSong() {
    }

    public static FavoriteSong getInstance() {
        return singleton;
    }

    /**
     * Gets favorite playSongs.
     *
     * @return the favorite playSongs
     */
    public List <SongInfo> getFavoriteSongs() {
        List <SongInfo> allLocalSongs = LocalSong.getInstance().getAllLocalSongs();
        favoriteSongs = new ArrayList <>();
        for (SongInfo song : allLocalSongs) {
            if (song.getIsFavorite()) {
                favoriteSongs.add(song);
            }
        }
        return favoriteSongs;
    }

    /**
     * Switch favorite boolean.
     *
     * @param song the song
     * @return isFavorite
     */
    boolean switchFavorite(SongInfo song) {
        if (song == null) {
            return false;
        }
        boolean isFavorite = !song.getIsFavorite();
        song.setIsFavorite(isFavorite);
        LocalSongModel.update(song);
        return isFavorite;
    }

    /**
     * Remove list .
     *
     * @param keys the keys
     * @return the list
     */
    public List <SongInfo> remove(Collection <Long> keys) {
        List <SongInfo> removeSongs = new ArrayList <>();
        Player player = Player.getInstance();
        for (int i = favoriteSongs.size() - 1; i >= 0 && !keys.isEmpty(); i--) {
            SongInfo song = favoriteSongs.get(i);
            Long id = song.getId();
            if (keys.contains(id)) {
                //是选中播放的歌曲，直接更新状态
                if (song.isPlayListSelected()) {
                    player.switchFavorite();
                } else {
                    song.setIsFavorite(false);
                    removeSongs.add(song);
                }
                favoriteSongs.remove(i);
                keys.remove(id);
            }
        }
        LocalSongModel.updateInTx(removeSongs);
        return favoriteSongs;
    }

    /**
     * Remove.
     *
     * @param song the song
     */
    public void remove(SongInfo song) {
        if (song == null) {
            return;
        }
        if (song.isPlayListSelected()) {
            Player.getInstance().switchFavorite();
        } else {
            song.setIsFavorite(false);
            LocalSongModel.update(song);
        }
    }

}
