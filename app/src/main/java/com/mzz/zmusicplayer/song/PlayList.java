package com.mzz.zmusicplayer.song;

import android.os.Parcel;
import android.os.Parcelable;

import com.mzz.zmusicplayer.setting.PlayedMode;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * author : Mzz
 * date : 2019 2019/5/29 11:17
 * description :
 */
@AllArgsConstructor
public class PlayList implements Parcelable {

    public static final Parcelable.Creator <PlayList> CREATOR =
            new Parcelable.Creator <PlayList>() {
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
    private List <SongInfo> songInfos;
    @Getter
    @Setter
    private int playingIndex = 0;
    @Getter
    @Setter
    private PlayedMode playMode = PlayedMode.ORDER;

    public PlayList() {
        this.songInfos = new ArrayList <>();
    }

    private PlayList(Parcel in) {
        this.songInfos = in.createTypedArrayList(SongInfo.CREATOR);
        this.playingIndex = in.readInt();
        int tmpPlayMode = in.readInt();
        this.playMode = tmpPlayMode == -1 ? null : PlayedMode.values()[tmpPlayMode];
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
            songInfos.sort((o1, o2) -> Collator.getInstance(Locale.CHINESE).compare(o1.getName(),
                    o2.getName()));
        } else {
            songInfos.sort((o1, o2) -> Collator.getInstance(Locale.CHINESE).compare(o2.getName(),
                    o1.getName()));
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

    boolean isEmpty() {
        return songInfos.isEmpty();
    }

    public SongInfo getPlayingSong() {
        if (playingIndex < 0) {
            playingIndex = 0;
        }

        if (playingIndex < songInfos.size()) {
            return songInfos.get(playingIndex);
        }
        return null;
    }

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
        }
        return getPlayingSong();
    }

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
            newIndex = songInfos.size() - 1;
        }
        return newIndex;
    }

    private int getNextIndex() {
        int newIndex = playingIndex + 1;
        if (newIndex >= songInfos.size()) {
            newIndex = 0;
        }
        return newIndex;
    }

    private int getRandomPlayIndex() {
        int size = songInfos.size();
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
        dest.writeTypedList(this.songInfos);
        dest.writeInt(this.playingIndex);
        dest.writeInt(this.playMode == null ? -1 : this.playMode.ordinal());
    }
}
