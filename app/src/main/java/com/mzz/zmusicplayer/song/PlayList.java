package com.mzz.zmusicplayer.song;

import android.os.Parcel;
import android.os.Parcelable;

import com.mzz.zmusicplayer.model.PlayListModel;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

/**
 * author : Mzz
 * date : 2019 2019/5/29 11:17
 * description :
 */
public class PlayList implements Parcelable {

    public static final Creator <PlayList> CREATOR = new Creator <PlayList>() {
        @Override
        public PlayList createFromParcel(Parcel source) {
            return new PlayList(source);
        }

        @Override
        public PlayList[] newArray(int size) {
            return new PlayList[size];
        }
    };
    private static final int RECENT_MAX_COUNT = 50;
    @Setter
    @Getter
    private List <SongInfo> songs;
    private LinkedList <SongInfo> recentSongs;
    @Getter
    @Setter
    private int playingIndex = 0;
    @Getter
    @Setter
    private PlayedMode playMode = PlayedMode.ORDER;
    private SongInfo playingSong;

    public PlayList() {
        this.songs = new ArrayList <>();
    }

    public PlayList(List <SongInfo> songs, int playingIndex, PlayedMode playMode) {
        this.songs = songs;
        this.playingIndex = playingIndex;
        this.playMode = playMode;
    }

    protected PlayList(Parcel in) {
        this.songs = in.createTypedArrayList(SongInfo.CREATOR);
        this.playingIndex = in.readInt();
        int tmpPlayMode = in.readInt();
        this.playMode = tmpPlayMode == -1 ? null : PlayedMode.values()[tmpPlayMode];
        this.playingSong = in.readParcelable(SongInfo.class.getClassLoader());
    }

    /**
     * Gets favorite songs.
     *
     * @param songInfos the song infos
     * @return the favorite songs
     */
    public static List <SongInfo> getFavoriteSongs(Collection <SongInfo> songInfos) {
        List <SongInfo> favoriteSongs = new ArrayList <>();
        for (SongInfo songInfo : songInfos) {
            if (songInfo.getIsFavorite()) {
                favoriteSongs.add(songInfo);
            }
        }
        return favoriteSongs;
    }

    /**
     * Gets recent songs.
     *
     * @param songInfos the song infos
     * @return the recent songs
     */
    public static List <SongInfo> getRecentSongs(Collection <SongInfo> songInfos) {
        List <SongInfo> recentSongs = new LinkedList <>();
        for (SongInfo songInfo : songInfos) {
            if (recentSongs.size() >= RECENT_MAX_COUNT) {
                break;
            }
            //播放时间非空，且保证唯一
            if (songInfo.getLastPlayTime() != null && !recentSongs.contains(songInfo)) {
                recentSongs.add(songInfo);
            }
        }
        sortByLastPlayTime(recentSongs);
        return recentSongs;
    }

    /**
     * Gets song index by id.
     *
     * @param songInfos the song infos
     * @param songId    the song id
     * @return the song index by id
     */
    public static int getSongIndexById(List <SongInfo> songInfos, long songId) {
        int songIndex = 0;
        //根据ID获取歌曲在列表中的位置
        for (int i = 0; i < songInfos.size(); i++) {
            SongInfo songInfo = songInfos.get(i);
            if (songInfo.getId().equals(songId)) {
                songIndex = i;
                break;
            }
        }
        return songIndex;
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

    public List <SongInfo> getRecentSongs() {
        if (recentSongs == null) {
            recentSongs = (LinkedList <SongInfo>) getRecentSongs(songs);
        }
        return recentSongs;
    }

    /**
     * Update playing index by setting id.
     *
     * @return is Same with last song
     */
    public boolean updatePlayingIndexBySettingId() {
        long lastPlaySongId = AppSetting.getLastPlaySongId();
        playingIndex = PlayList.getSongIndexById(songs, lastPlaySongId);
        SongInfo newSong = songs.get(playingIndex);
        boolean isSameLastSong = false;
        if (playingSong != null && newSong != null) {
            isSameLastSong = playingSong.getId().equals(newSong.getId());
        }
        //新的播放歌曲是否和上一次一样
        return isSameLastSong;
    }

    /**
     * Add all.
     *
     * @param c the c
     */
    public void addAll(Collection <SongInfo> c) {
        songs.addAll(c);
        PlayListModel.insertOrReplaceInTx(c);
    }

    /**
     * Update recent songs.
     *
     * @param song the song
     */
    public void updateRecentSongs(SongInfo song) {
        if (recentSongs == null) {
            getRecentSongs();
        }
        recentSongs.remove(song);
        recentSongs.addFirst(song);
        removeRecentSong();
    }

    /**
     * Gets playing song.
     *
     * @return the playing song
     */
    public SongInfo getPlayingSong() {
        if (playingIndex < 0) {
            playingIndex = 0;
        }

        if (playingIndex < songs.size()) {
            playingSong = songs.get(playingIndex);
            return playingSong;
        }
        return null;
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    boolean isEmpty() {
        return songs.isEmpty();
    }

    /**
     * Previous song info.
     *
     * @return the song info
     */
    SongInfo previous() {
        switch (playMode) {
            //循环当前歌曲
            case SINGLE:
                break;
            case ORDER:
                playingIndex = getPreIndex();
                break;
            case RANDOM:
                playingIndex = getRandomPlayIndex();
                break;
            default:
                break;
        }
        return getPlayingSong();
    }

    /**
     * Next song info.
     *
     * @return the song info
     */
    SongInfo next() {
        switch (playMode) {
            //循环当前歌曲
            case SINGLE:
                break;
            case ORDER:
                playingIndex = getNextIndex();
                break;
            case RANDOM:
                playingIndex = getRandomPlayIndex();
                break;
        }
        return getPlayingSong();
    }

    private void removeRecentSong() {
        while (recentSongs.size() > RECENT_MAX_COUNT) {
            recentSongs.removeLast();
        }
    }

    private int getPreIndex() {
        int newIndex = playingIndex - 1;
        if (newIndex < 0) {
            newIndex = songs.size() - 1;
        }
        return newIndex;
    }

    private int getNextIndex() {
        int newIndex = playingIndex + 1;
        if (newIndex >= songs.size()) {
            newIndex = 0;
        }
        return newIndex;
    }

    private int getRandomPlayIndex() {
        int size = songs.size();
        if (size == 0) {
            return -1;
        }
        int randomIndex = new Random().nextInt(size);
        // 非单曲循环模式下，确保不会连续播放同一首歌曲
        if (playMode != PlayedMode.SINGLE && size > 1 && randomIndex == playingIndex) {
            playingIndex = getNextIndex();
        }
        return randomIndex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.songs);
        dest.writeInt(this.playingIndex);
        dest.writeInt(this.playMode == null ? -1 : this.playMode.ordinal());
        dest.writeParcelable(this.playingSong, flags);
    }
}
