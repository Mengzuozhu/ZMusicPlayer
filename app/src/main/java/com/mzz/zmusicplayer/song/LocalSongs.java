package com.mzz.zmusicplayer.song;

import com.mzz.zmusicplayer.model.LocalSongModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

/**
 * author : Mzz
 * date : 2019 2019/6/7 20:01
 * description :
 */
public class LocalSongs {
    private static final int RECENT_MAX_COUNT = 50;
    @Getter
    private List <SongInfo> allSongs;
    private LinkedList <SongInfo> recentSongs;

    LocalSongs(List <SongInfo> allSongs) {
        this.allSongs = allSongs;
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

    /**
     * Sort by last play time.
     *
     * @param songInfos the song infos
     */
    private static void sortByLastPlayTime(List <SongInfo> songInfos) {
        songInfos.sort((o1, o2) -> {
            Date lastPlayTime = o2.getLastPlayTime();
            if (lastPlayTime == null) {
                return -1;
            }
            Date lastPlayTime2 = o1.getLastPlayTime();
            if (lastPlayTime2 == null) {
                return 1;
            }
            return lastPlayTime.compareTo(lastPlayTime2);
        });
    }

    /**
     * Update play list playSongs list .
     *
     * @param checkedSong the checked song
     * @return the PlayListSongs
     */
    List <SongInfo> updatePlayListSongs(List <SongInfo> checkedSong) {
        List <SongInfo> songs = new ArrayList <>();
        for (SongInfo localSong : this.allSongs) {
            boolean isChecked = checkedSong.contains(localSong);
            if (isChecked) {
                songs.add(localSong);
            }
            localSong.setIsChecked(isChecked);
        }
        LocalSongModel.updateInTx(this.allSongs);
        return songs;
    }

    /**
     * Add all.
     *
     * @param c the c
     */
    void addAll(Collection <SongInfo> c) {
        allSongs.addAll(c);
        LocalSongModel.insertOrReplaceInTx(c);
    }

    /**
     * Gets play list playSongs.
     *
     * @return the play list playSongs
     */
    List <SongInfo> getPlayListSongs() {
        List <SongInfo> songs = new ArrayList <>();
        for (SongInfo localSong : this.allSongs) {
            if (localSong.getIsChecked()) {
                songs.add(localSong);
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
            recentSongs = new LinkedList <>();
            for (SongInfo localSong : allSongs) {
                if (recentSongs.size() >= RECENT_MAX_COUNT) {
                    break;
                }
                //播放时间非空，且保证唯一
                if (localSong.getLastPlayTime() != null && !recentSongs.contains(localSong)) {
                    recentSongs.add(localSong);
                }
            }
            sortByLastPlayTime(recentSongs);

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
        for (SongInfo localSong : allSongs) {
            if (localSong.getIsFavorite()) {
                favoriteSongs.add(localSong);
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
    }

    private void removeRecentSong() {
        while (recentSongs.size() > RECENT_MAX_COUNT) {
            recentSongs.removeLast();
        }
    }

}
