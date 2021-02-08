package com.mzz.zmusicplayer.song;

import com.mzz.zmusicplayer.common.util.FileUtil;
import com.mzz.zmusicplayer.model.LocalSongModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * 本地所有歌曲
 *
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
        removeInvalidSong();
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
            songInfos.sort(Comparator.comparing(SongInfo::getNameSpell));
        } else {
            songInfos.sort(Comparator.comparing(SongInfo::getNameSpell, Comparator.reverseOrder()));
        }
    }

    /**
     * Sort by id.
     *
     * @param songInfos the song infos
     */
    public static void sortById(List<SongInfo> songInfos) {
        songInfos.sort(Comparator.comparing(SongInfo::getId));
    }

    /**
     * Sort by play count.
     *
     * @param songInfos the song infos
     */
    public static void sortByPlayCount(List<SongInfo> songInfos) {
        songInfos.sort(Comparator.comparing(SongInfo::getPlayCount).reversed());
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
     * @param songInfos the songInfos
     */
    public List<SongInfo> addToLocalSongs(Collection<SongInfo> songInfos) {
        allLocalSongs.addAll(songInfos);
        LocalSongModel.insertOrReplaceInTx(songInfos);
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
        removeInvalidSong();
        LocalSongModel.updateInTx(this.allLocalSongs);
        return playListSongs;
    }

    /**
     * Gets play list playSongs.
     *
     * @return the play list playSongs
     */
    public List<SongInfo> getPlayListSongs() {
        return allLocalSongs.stream()
                .filter(SongInfo::getIsChecked)
                .collect(Collectors.toList());
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
        return allLocalSongs.stream()
                .map(SongInfo::getSongIdInFile)
                .collect(Collectors.toSet());
    }

    private void removeInvalidSong() {
        allLocalSongs.removeIf(songInfo -> FileUtil.isFileNotExists(songInfo.getPath()));
    }
}
