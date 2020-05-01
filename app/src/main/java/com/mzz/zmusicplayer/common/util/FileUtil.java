package com.mzz.zmusicplayer.common.util;

import android.util.Log;

import java.io.File;

/**
 * The type File util.
 *
 * @author zuozhu.meng
 * @date 2020 /5/1
 */
public class FileUtil {

    public static boolean isFileExists(String path) {
        try {
            return new File(path).exists();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FileUtil", "isFileExists: ", e);
            return false;
        }
    }

    public static boolean isFileNotExists(String path) {
        return !isFileExists(path);
    }
}
