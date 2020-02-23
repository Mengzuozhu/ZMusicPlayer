package com.mzz.zmusicplayer.song;

import com.mzz.zmusicplayer.model.LocalSongModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

/**
 * 本地所有歌曲
 * @author : Mzz
 * date : 2019 2019/6/7 20:01
 * description :
 */
public class LocalSong {
    private static LocalSong localSong = new LocalSong();
    @Getter
    private List<SongInfo> allLocalSongs;

    private LocalSong() {
        this.allLocalSongs = LocalSongModel.getOrderLocalSongs();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static LocalSong getInstance() {
        return localSong;
    }

    /**
     * Sort by chinese name.
     *
     * @param songInfos the song infos
     * @param isAscend  the is ascend
     */
    public static void sortByChineseName(List<SongInfo> songInfos, boolean isAscend) {
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
    public static void sortById(List<SongInfo> songInfos) {
        songInfos.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
    }

    /**
     * Sort by play count.
     *
     * @param songInfos the song infos
     */
    public static void sortByPlayCount(List<SongInfo> songInfos) {
        songInfos.sort((o1, o2) -> o2.getPlayCount().compareTo(o1.getPlayCount()));
    }

    /**
     * Remove list .
     *
     * @param keys the keys
     * @return the list
     */
    public List<SongInfo> remove(Collection<Long> keys) {
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
     * Remove.
     *
     * @param song the song
     */
    public void remove(SongInfo song) {
        LocalSongModel.delete(song);
    }

    /**
     * Add all.
     *
     * @param c the c
     */
    public List<SongInfo> addToLocalSongs(Collection<SongInfo> c) {
        allLocalSongs.addAll(c);
        LocalSongModel.insertOrReplaceInTx(c);
        return allLocalSongs;
    }

    /**
     * Update play list songs list .
     *
     * @param newPlaySongs the new play songs
     * @return the list
     */
    public List<SongInfo> updatePlayListSongs(List<SongInfo> newPlaySongs) {
        //新建播放列表，避免受其他列表的影响
        List<SongInfo> playListSongs = new ArrayList<>();
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
     * Gets play list playSongs.
     *
     * @return the play list playSongs
     */
    public List<SongInfo> getPlayListSongs() {
        List<SongInfo> playListSongs = new ArrayList<>();
        for (SongInfo song : this.allLocalSongs) {
            if (song.getIsChecked()) {
                playListSongs.add(song);
            }
        }
        return playListSongs;
    }

    /**
     * Update recent song.
     *
     * @param song the song
     */
    public void updateRecentSong(SongInfo song) {
        RecentSong.getInstance().updateRecentSong(song);
    }

    /**
     * Gets all song id in file.
     *
     * @return the all song id in file
     */
    public Set<Integer> getAllSongIdInFile() {
        Set<Integer> songIdInFile = new HashSet<>(allLocalSongs.size());
        for (SongInfo allLocalSong : allLocalSongs) {
            songIdInFile.add(allLocalSong.getSongIdInFile());
        }
        return songIdInFile;
    }
}
