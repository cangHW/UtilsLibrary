package com.perfect.library.utils.system;

import android.content.Context;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/12 16:45.
 * Function :dp、dip、px、sp等相互转化的工具类
 */

public class DisplayUtils {

    /**
     * px转dp
     *
     * @param context 上下文环境
     * @param pxValue 需要转化的px数值
     * @return 返回转化后的数值
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px
     *
     * @param context 上下文环境
     * @param dpValue 需要转化的dp数值
     * @return 返回转化后的数值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转sp
     *
     * @param context 上下文环境
     * @param pxValue 需要转化的px数值
     * @return 返回转化后的数值
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param context 上下文环境
     * @param spValue 需要转化的sp数值
     * @return 返回转化后的数值
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
