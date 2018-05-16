package com.perfect.library.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Canghaixiao.
 * Time : 2017/11/16 15:42.
 * Function :
 */
public class FileUtils {

    public static String getSavePicturePath(String name){
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        File file1 = new File(dir);
        if (!file1.exists())
            file1.mkdirs();
        return dir+name;
    }


}
