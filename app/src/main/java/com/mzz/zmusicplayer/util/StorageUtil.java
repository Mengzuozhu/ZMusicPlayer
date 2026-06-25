package com.mzz.zmusicplayer.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StorageUtil {
    public static String writeToFileExternalStorage(String fileName, String fileContent) {
        if (isExternalStorageWritable()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);
            try(FileOutputStream fos = new FileOutputStream(file);) {
                fos.write(fileContent.getBytes());
                return file.getPath();
            } catch (IOException e) {
                log.error("writeToFileExternalStorage_failed::fileName ={},", fileName, e);
            }
        }
        return "";
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
