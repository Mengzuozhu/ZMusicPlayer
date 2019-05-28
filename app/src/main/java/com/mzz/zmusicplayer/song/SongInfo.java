package com.mzz.zmusicplayer.song;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import com.mzz.zandroidcommon.adapter.ICheckable;
import com.mzz.zandroidcommon.common.JsonConverter;
import com.mzz.zandroidcommon.view.QueryInfo;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 歌曲信息
 * author : Mzz
 * date : 2019 2019/5/7 19:22
 * description :
 */
@Data
@AllArgsConstructor
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

    private String name;
    private String path;
    private String artist;
    private int duration;
    private boolean isChecked;

    private SongInfo(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.isChecked = in.readByte() != 0;
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
