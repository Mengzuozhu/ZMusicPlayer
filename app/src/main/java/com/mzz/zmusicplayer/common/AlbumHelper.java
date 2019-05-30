package com.mzz.zmusicplayer.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.mzz.zmusicplayer.song.SongInfo;

import java.io.File;

/**
 * author : Mzz
 * date : 2019 2019/5/30 21:14
 * description :
 */
public class AlbumHelper {
    public static Bitmap parseAlbum(SongInfo song) {
        return parseAlbum(new File(song.getPath()));
    }

    public static Bitmap parseAlbum(File file) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        try {
            metadataRetriever.setDataSource(file.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            Log.e("AlbumHelper", "parseAlbum: ", e);
            return null;
        }
        byte[] albumData = metadataRetriever.getEmbeddedPicture();
        if (albumData != null) {
            return BitmapFactory.decodeByteArray(albumData, 0, albumData.length);
        }
        return null;
    }

}
