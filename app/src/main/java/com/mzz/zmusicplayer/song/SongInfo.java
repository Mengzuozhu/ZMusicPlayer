package com.mzz.zmusicplayer.song;

import android.os.Parcel;
import android.os.Parcelable;

import com.mzz.zandroidcommon.adapter.ICheckable;
import com.mzz.zandroidcommon.common.StringHelper;
import com.mzz.zandroidcommon.view.QueryInfo;
import com.mzz.zmusicplayer.view.edit.IEditItem;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import lombok.Getter;
import lombok.Setter;

/**
 * 歌曲信息
 *
 * @author : Mzz
 * date : 2019 2019/5/7 19:22
 * description :
 */
@Entity
public class SongInfo implements Parcelable, ICheckable, QueryInfo, IEditItem {

    public static final Creator<SongInfo> CREATOR = new Creator<SongInfo>() {
        @Override
        public SongInfo createFromParcel(Parcel source) {
            return new SongInfo(source);
        }

        @Override
        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };
    /**
     * 当前歌曲是否被选中
     */
    @Transient
    @Setter
    @Getter
    private boolean isPlayListSelected;
    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String path;
    private String artist;
    /**
     * 全大写的中文歌名拼写，英文保存原样
     */
    private String nameSpell;
    private String title;
    private String fileArtist;
    private int songIdInFile;
    private int duration;
    private Integer playCount = 0;
    private Long lastPlayTime = 0L;
    private boolean isChecked = true;
    private boolean isFavorite = false;

    @Generated(hash = 1061935912)
    public SongInfo() {
    }

    @Generated(hash = 628585481)
    public SongInfo(Long id, String name, String path, String artist, String nameSpell,
                    String title, String fileArtist, int songIdInFile, int duration,
                    Integer playCount, Long lastPlayTime, boolean isChecked, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.artist = artist;
        this.nameSpell = nameSpell;
        this.title = title;
        this.fileArtist = fileArtist;
        this.songIdInFile = songIdInFile;
        this.duration = duration;
        this.playCount = playCount;
        this.lastPlayTime = lastPlayTime;
        this.isChecked = isChecked;
        this.isFavorite = isFavorite;
    }

    protected SongInfo(Parcel in) {
        this.isPlayListSelected = in.readByte() != 0;
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.path = in.readString();
        this.artist = in.readString();
        this.nameSpell = in.readString();
        this.title = in.readString();
        this.fileArtist = in.readString();
        this.songIdInFile = in.readInt();
        this.duration = in.readInt();
        this.playCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lastPlayTime = (Long) in.readValue(Long.class.getClassLoader());
        this.isChecked = in.readByte() != 0;
        this.isFavorite = in.readByte() != 0;
    }

    @Override
    public String getInfo() {
        return name;
    }

    /**
     * Add play count.
     */
    public void addPlayCount() {
        if (playCount == null) {
            playCount = 0;
        }
        playCount++;
    }

    /**
     * Gets song detail.
     *
     * @return the song detail
     */
    public String getSongDetail() {
        return StringHelper.getLocalFormat("歌名: %s\n", getName()) +
                StringHelper.getLocalFormat("歌手: %s\n", getArtist()) +
                StringHelper.getLocalFormat("歌手（默认）: %s\n", getFileArtist()) +
                StringHelper.getLocalFormat("标题: %s\n", getTitle()) +
                StringHelper.getLocalFormat("播放量: %s\n", getPlayCount()) +
                StringHelper.getLocalFormat("文件路径: %s\n", getPath());
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean getIsChecked() {
        return this.isChecked;
    }

    @Override
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameSpell() {
        return this.nameSpell;
    }

    public void setNameSpell(String nameSpell) {
        this.nameSpell = nameSpell;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileArtist() {
        return this.fileArtist;
    }

    public void setFileArtist(String fileArtist) {
        this.fileArtist = fileArtist;
    }

    public int getSongIdInFile() {
        return this.songIdInFile;
    }

    public void setSongIdInFile(int songIdInFile) {
        this.songIdInFile = songIdInFile;
    }

    public Long getLastPlayTime() {
        return this.lastPlayTime;
    }

    public void setLastPlayTime(Long lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }

    public Integer getPlayCount() {
        if (playCount == null) {
            playCount = 0;
        }
        return this.playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isPlayListSelected ? (byte) 1 : (byte) 0);
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.artist);
        dest.writeString(this.nameSpell);
        dest.writeString(this.title);
        dest.writeString(this.fileArtist);
        dest.writeInt(this.songIdInFile);
        dest.writeInt(this.duration);
        dest.writeValue(this.playCount);
        dest.writeValue(this.lastPlayTime);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
    }
}
