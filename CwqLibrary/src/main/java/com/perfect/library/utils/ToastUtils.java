package com.perfect.library.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by Canghaixiao.
 * Time : 2017/5/17 16:01.
 * Function :
 */
public class ToastUtils {

    private static boolean isDebug = false;
    private static Toast mToast;

    public static void init(boolean b) {
        isDebug = b;
    }

    @SuppressLint("ShowToast")
    public static void showMsg(Context context, CharSequence msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    @SuppressLint("ShowToast")
    public static void showMsg(Context context, @StringRes int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT);
        } else {
            mToast.setText(context.getResources().getString(resId));
        }
        mToast.show();
    }

    @SuppressLint("ShowToast")
    public static void showMsgOnDebug(Context context, CharSequence msg) {
        if (isDebug) {
            if (mToast == null) {
                mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            } else {
                mToast.setText(msg);
            }
            mToast.show();
        }
    }
}
