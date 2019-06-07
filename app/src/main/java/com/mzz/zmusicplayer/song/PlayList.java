package com.mzz.zmusicplayer.song;

import android.os.Parcel;
import android.os.Parcelable;

import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;

import java.util.ArrayList;
import java.util.Collection;
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

    private LocalSongs localSongs;
    @Setter
    @Getter
    private List <SongInfo> playSongs;
    @Getter
    @Setter
    private int playingIndex = 0;
    @Getter
    @Setter
    private PlayedMode playMode = PlayedMode.ORDER;
    private SongInfo playingSong;

    public PlayList() {
        localSongs = new LocalSongs(new ArrayList <>());
        initPlayListSongs();
    }

    public PlayList(List <SongInfo> localSongs, PlayedMode playMode) {
        this.localSongs = new LocalSongs(localSongs);
        this.playMode = playMode;
        initPlayListSongs();
        updatePlayingIndexBySettingId();
    }

    protected PlayList(Parcel in) {
        this.playSongs = in.createTypedArrayList(SongInfo.CREATOR);
        this.playingIndex = in.readInt();
        int tmpPlayMode = in.readInt();
        this.playMode = tmpPlayMode == -1 ? null : PlayedMode.values()[tmpPlayMode];
        this.playingSong = in.readParcelable(SongInfo.class.getClassLoader());
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

    private void initPlayListSongs() {
        this.playSongs = localSongs.getPlayListSongs();
    }

    /**
     * Gets local playSongs.
     *
     * @return the local playSongs
     */
    public List <SongInfo> getLocalSongs() {
        return localSongs.getAllSongs();
    }

    /**
     * Gets recent playSongs.
     *
     * @return the recent playSongs
     */
    public List <SongInfo> getRecentSongs() {
        return localSongs.getRecentSongs();
    }

    /**
     * Gets favorite playSongs.
     *
     * @return the favorite playSongs
     */
    public List <SongInfo> getFavoriteSongs() {
        return localSongs.getFavoriteSongs();
    }

    /**
     * Update play list songs.
     *
     * @param checkedSongs the checked song
     */
    public void updatePlayListSongs(List <SongInfo> checkedSongs) {
        this.playSongs = localSongs.updatePlayListSongs(checkedSongs);
    }

    /**
     * Update playing index by setting id.
     *
     * @return is Same with last song
     */
    public boolean updatePlayingIndexBySettingId() {
        long lastPlaySongId = AppSetting.getLastPlaySongId();
        this.playingIndex = getSongIndexById(playSongs, lastPlaySongId);
        SongInfo newSong = playSongs.get(playingIndex);
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
        playSongs.addAll(c);
        localSongs.addAll(c);
    }

    /**
     * Update recent playSongs.
     *
     * @param song the song
     */
    public void updateRecentSongs(SongInfo song) {
        localSongs.updateRecentSong(song);
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

        if (playingIndex < playSongs.size()) {
            playingSong = playSongs.get(playingIndex);
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
        return playSongs.isEmpty();
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

    private int getPreIndex() {
        int newIndex = playingIndex - 1;
        if (newIndex < 0) {
            newIndex = playSongs.size() - 1;
        }
        return newIndex;
    }

    private int getNextIndex() {
        int newIndex = playingIndex + 1;
        if (newIndex >= playSongs.size()) {
            newIndex = 0;
        }
        return newIndex;
    }

    private int getRandomPlayIndex() {
        int size = playSongs.size();
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
        dest.writeTypedList(this.playSongs);
        dest.writeInt(this.playingIndex);
        dest.writeInt(this.playMode == null ? -1 : this.playMode.ordinal());
        dest.writeParcelable(this.playingSong, flags);
    }
}
