package com.mzz.zmusicplayer.song;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import com.mzz.zandroidcommon.adapter.ICheckable;
import com.mzz.zandroidcommon.common.JsonConverter;
import com.mzz.zandroidcommon.view.QueryInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;

/**
 * 歌曲信息
 * author : Mzz
 * date : 2019 2019/5/7 19:22
 * description :
 */
@Entity
public class SongInfo implements Parcelable, ICheckable, QueryInfo {
    public static final Creator <SongInfo> CREATOR = new Creator <SongInfo>() {
        @Override
        public SongInfo createFromParcel(Parcel source) {
            return new SongInfo(source);
        }

        @Override
        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String path;
    private String artist;
    private int duration;
    private boolean isChecked = true;

    private SongInfo(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.isChecked = in.readByte() != 0;
    }

    @Generated(hash = 218269514)
    public SongInfo(Long id, String name, String path, String artist, int duration,
                    boolean isChecked) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.artist = artist;
        this.duration = duration;
        this.isChecked = isChecked;
    }

    @Generated(hash = 1061935912)
    public SongInfo() {
    }

    public Bitmap getPicture() {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] picture = mediaMetadataRetriever.getEmbeddedPicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        return bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    @Override
    public String getInfo() {
        return name;
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

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static class SongInfoListConverter implements PropertyConverter <ArrayList <SongInfo>,
            String> {

        @Override
        public ArrayList <SongInfo> convertToEntityProperty(String databaseValue) {
            return JsonConverter.jsonToList(databaseValue, SongInfo.class);
        }

        @Override
        public String convertToDatabaseValue(ArrayList <SongInfo> entityProperty) {
            return JsonConverter.convertToStringValue(entityProperty);
        }
    }
}
