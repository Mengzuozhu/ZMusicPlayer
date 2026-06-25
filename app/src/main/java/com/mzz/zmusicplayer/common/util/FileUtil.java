package com.mzz.zmusicplayer.common.util;

import android.net.Uri;
import android.util.Log;

import com.mzz.zmusicplayer.MusicApplication;

import java.io.File;
import java.io.InputStream;

/**
 * The type File util.
 *
 * @author zuozhu.meng
 * @date 2020 /5/1
 */
public class FileUtil {

    public static boolean isFileExists(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        try {
            if (path.startsWith("content://")) {
                try (InputStream inputStream = MusicApplication.getContext()
                        .getContentResolver().openInputStream(Uri.parse(path))) {
                    return inputStream != null;
                }
            }
            return new File(path).exists();
        } catch (Exception e) {
            Log.e("FileUtil", "isFileExists: ", e);
            return false;
        }
    }

    public static boolean isFileNotExists(String path) {
        return !isFileExists(path);
    }
}
