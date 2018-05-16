package com.perfect.library.utils;

import android.util.Log;

/**
 * Created by Canghaixiao.
 * Time : 2017/11/16 12:24.
 * Function :
 */
public class LogUtils {

    private static boolean isDebug=false;

    public static void init(boolean b){
        isDebug=b;
    }

    public static void i(String tag, String msg){
        if(isDebug){
            Log.i(tag,msg);
        }
    }

    public static void d(String tag, String msg){
        if(isDebug){
            Log.d(tag,msg);
        }
    }

    public static void e(String tag, String msg){
        if(isDebug){
            Log.e(tag,msg);
        }
    }

    public static void v(String tag, String msg){
        if(isDebug){
            Log.v(tag,msg);
        }
    }

    public static void w(String tag, String msg){
        if(isDebug){
            Log.w(tag,msg);
        }
    }
}
