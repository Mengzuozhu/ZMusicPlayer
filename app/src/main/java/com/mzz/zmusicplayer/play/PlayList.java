package com.mzz.zmusicplayer.play;

import android.os.Parcel;
import android.os.Parcelable;

import com.mzz.zmusicplayer.model.LocalSongModel;
import com.mzz.zmusicplayer.setting.AppSetting;
import com.mzz.zmusicplayer.setting.PlayedMode;
import com.mzz.zmusicplayer.song.LocalSong;
import com.mzz.zmusicplayer.song.SongInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

/**
 * 播放列表
 * @author : Mzz
 * date : 2019 2019/5/29 11:17
 * description :
 */
public class PlayList implements Parcelable {

    public static final Creator<PlayList> CREATOR = new Creator<PlayList>() {
        @Override
        public PlayList createFromParcel(Parcel source) {
            return new PlayList(source);
        }

        @Override
        public PlayList[] newArray(int size) {
            return new PlayList[size];
        }
    };
    @Getter
    private LocalSong localSongs;
    @Getter
    @Setter
    private List<SongInfo> playSongs;
    @Getter
    @Setter
    private int playingIndex = 0;
    @Getter
    @Setter
    private PlayedMode playMode;
    @Setter
    private PlayListObserver playListObserver;
    @Getter
    @Setter
    private SongListType songListType = SongListType.PLAYLIST;

    public PlayList() {
        init();
    }

    public PlayList(SongListType songListType) {
        this.songListType = songListType;
        init();
    }

    protected PlayList(Parcel in) {
        this.playSongs = in.createTypedArrayList(SongInfo.CREATOR);
        this.playingIndex = in.readInt();
        int tmpPlayMode = in.readInt();
        this.playMode = tmpPlayMode == -1 ? null : PlayedMode.values()[tmpPlayMode];
        int tmpSongListType = in.readInt();
        this.songListType = tmpSongListType == -1 ? null : SongListType.values()[tmpSongListType];
    }

    /**
     * Gets song index by id.根据ID获取歌曲在列表中的位置
     *
     * @param songInfos the song infos
     * @param songId    the song id
     * @return the song index by id
     */
    public static int getSongIndexById(List<SongInfo> songInfos, long songId) {
        int songIndex = -1;
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

    private void init() {
        this.localSongs = LocalSong.getInstance();
        this.playMode = AppSetting.getPlayMode();
        initPlayListSongs();
        updatePlayingIndexBySettingId();
    }

    /**
     * Update play list songs.
     *
     * @param checkedSongs the checked song
     */
    public void updatePlayListSongs(List<SongInfo> checkedSongs) {
        this.playSongs = localSongs.updatePlayListSongs(checkedSongs);
    }

    /**
     * Update playing index by setting id.
     */
    public void updatePlayingIndexBySettingId() {
        if (playSongs.isEmpty()) {
            playingIndex = 0;
            return;
        }
        long lastPlaySongId = AppSetting.getLastPlaySongId();
        playingIndex = getSongIndexById(playSongs, lastPlaySongId);
        if (playingIndex == -1) {
            playingIndex = 0;
        }
    }

    /**
     * Add songs.
     *
     * @param c the c
     */
    public void addSongs(Collection<SongInfo> c) {
        playSongs.addAll(c);
        notifySongCountOrModeChange();
    }

    private void initPlayListSongs() {
        this.playSongs = localSongs.getPlayListSongs();
    }

    /**
     * Add song.
     *
     * @param song the song
     */
    void addSong(SongInfo song) {
        playSongs.add(song);
        notifySongCountOrModeChange();
    }

    /**
     * Remove.
     *
     * @param keys the keys
     */
    public void remove(List<Long> keys) {
        List<SongInfo> deleteSongs = new ArrayList<>();
        for (int i = playSongs.size() - 1; i >= 0 && !keys.isEmpty(); i--) {
            SongInfo song = playSongs.get(i);
            Long id = song.getId();
            if (!keys.contains(id)) {
                continue;
            }
            song.setIsChecked(false);
            playSongs.remove(i);
            keys.remove(id);
            deleteSongs.add(song);
        }
        LocalSongModel.updateInTx(deleteSongs);
        notifySongCountOrModeChange();
    }

    /**
     * Remove.
     *
     * @param song the song
     */
    public void remove(SongInfo song) {
        if (song != null) {
            song.setIsChecked(false);
            LocalSongModel.update(song);
            notifySongCountOrModeChange();
        }
    }

    /**
     * Update recent playSongs.
     *
     * @param song the song
     */
    public void updateRecentSongs(SongInfo song) {
        updatePlaySongBackgroundColor(song);
        localSongs.updateRecentSong(song);
    }

    /**
     * Gets playing song.
     *
     * @return the playing song
     */
    public SongInfo getPlayingSong() {
        if (playSongs.isEmpty()) {
            return null;
        }

        //超出范围，则取第一个
        if (playingIndex < 0 || playingIndex >= playSongs.size()) {
            playingIndex = 0;
        }

        return playSongs.get(playingIndex);
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

    public void notifySongCountOrModeChange() {
        if (playListObserver != null) {
            playListObserver.onSongCountOrModeChange();
        }
    }

    private void updatePlaySongBackgroundColor(SongInfo song) {
        if (playListObserver != null) {
            playListObserver.updatePlaySongBackgroundColor(song);
        }
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
        dest.writeInt(this.songListType == null ? -1 : this.songListType.ordinal());
    }

    public interface PlayListObserver {
        void onSongCountOrModeChange();

        void updatePlaySongBackgroundColor(SongInfo song);
    }
}
