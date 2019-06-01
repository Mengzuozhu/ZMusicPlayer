package com.mzz.zmusicplayer.edit;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 编辑信息
 * author : Mzz
 * date : 2019 2019/5/7 18:31
 * description :
 */
@Data
@AllArgsConstructor
public class EditItemInfo implements Parcelable {
    public static final Creator <EditItemInfo> CREATOR =
            new Creator <EditItemInfo>() {
                @Override
                public EditItemInfo createFromParcel(Parcel source) {
                    return new EditItemInfo(source);
                }

                @Override
                public EditItemInfo[] newArray(int size) {
                    return new EditItemInfo[size];
                }
            };

    private int id;
    private String info;

    private EditItemInfo(Parcel in) {
        this.id = in.readInt();
        this.info = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.info);
    }
}
