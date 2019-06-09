package com.mzz.zmusicplayer.song;

import com.mzz.zmusicplayer.model.LocalSongModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

/**
 * author : Mzz
 * date : 2019 2019/6/7 20:01
 * description :
 */
public class LocalSongClass {
    private static final int RECENT_MAX_COUNT = 50;
    private static LocalSongClass localSong = new LocalSongClass();
    @Getter
    private List <SongInfo> allLocalSongs;
    private LinkedList <SongInfo> recentSongs;

    private LocalSongClass() {
        this.allLocalSongs = LocalSongModel.getOrderLocalSongs();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static LocalSongClass getInstance() {
        return localSong;
    }

    /**
     * Sort by chinese name.
     *
     * @param songInfos the song infos
     * @param isAscend  the is ascend
     */
    public static void sortByChineseName(List <SongInfo> songInfos, boolean isAscend) {
        if (isAscend) {
            songInfos.sort((o1, o2) -> {
                String spell = o1.getNameSpell();
                if (spell == null) {
                    return -1;
                }
                return spell.compareTo(o2.getNameSpell());
            });
        } else {
            songInfos.sort((o1, o2) -> {
                String spell = o2.getNameSpell();
                if (spell == null) {
                    return -1;
                }
                return spell.compareTo(o1.getNameSpell());
            });
        }
    }

    /**
     * Sort by id.
     *
     * @param songInfos the song infos
     */
    public static void sortById(List <SongInfo> songInfos) {
        songInfos.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
    }

    public List <SongInfo> remove(List <Long> keys) {
        LocalSongModel.deleteByKeyInTx(keys);
        for (int i = allLocalSongs.size() - 1; i >= 0 && !keys.isEmpty(); i--) {
            SongInfo song = allLocalSongs.get(i);
            Long id = song.getId();
            if (keys.contains(id)) {
                allLocalSongs.remove(i);
                keys.remove(id);
            }
        }
        return allLocalSongs;
    }

    /**
     * Update play list songs list .
     *
     * @param newPlaySongs the new play songs
     * @return the list
     */
    List <SongInfo> updatePlayListSongs(List <SongInfo> newPlaySongs) {
        //新建播放列表，避免受其他列表的影响
        List <SongInfo> playListSongs = new ArrayList <>();
        for (SongInfo song : this.allLocalSongs) {
            boolean isPlay = newPlaySongs.contains(song);
            //更新播放列表歌曲
            if (isPlay) {
                playListSongs.add(song);
            }
            //更新是否播放
            song.setIsChecked(isPlay);
        }
        LocalSongModel.updateInTx(this.allLocalSongs);
        return playListSongs;
    }

    /**
     * Add all.
     *
     * @param c the c
     */
    public List <SongInfo> addToLocalSongs(Collection <SongInfo> c) {
        allLocalSongs.addAll(c);
        LocalSongModel.insertOrReplaceInTx(c);
        return allLocalSongs;
    }

    /**
     * Gets play list playSongs.
     *
     * @return the play list playSongs
     */
    List <SongInfo> getPlayListSongs() {
        List <SongInfo> songs = new ArrayList <>();
        for (SongInfo song : this.allLocalSongs) {
            if (song.getIsChecked()) {
                songs.add(song);
            }
        }
        return songs;
    }

    /**
     * Gets recent playSongs.
     *
     * @return the recent playSongs
     */
    List <SongInfo> getRecentSongs() {
        if (recentSongs == null) {
            recentSongs = new RecentSong(allLocalSongs).sortRecentSongs();
        }
        return recentSongs;
    }

    /**
     * Gets favorite playSongs.
     *
     * @return the favorite playSongs
     */
    List <SongInfo> getFavoriteSongs() {
        List <SongInfo> favoriteSongs = new ArrayList <>();
        for (SongInfo song : allLocalSongs) {
            if (song.getIsFavorite()) {
                favoriteSongs.add(song);
            }
        }
        return favoriteSongs;
    }

    /**
     * Update recent song.
     *
     * @param song the song
     */
    void updateRecentSong(SongInfo song) {
        if (recentSongs == null) {
            getRecentSongs();
        }
        recentSongs.remove(song);
        recentSongs.addFirst(song);
        removeRecentSong();
        LocalSongModel.update(song);
    }

    private void removeRecentSong() {
        while (recentSongs.size() > RECENT_MAX_COUNT) {
            recentSongs.removeLast();
        }
    }

}
