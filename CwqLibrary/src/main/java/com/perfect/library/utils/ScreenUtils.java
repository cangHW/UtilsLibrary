package com.perfect.library.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/12 15:13.
 * Function :获取有关屏幕信息的工具类
 */

public class ScreenUtils {

    /**
     * 获取屏幕宽度
     *
     * @param context 上下文环境
     */
    public static int getWindowWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context 上下文环境
     */
    public static int getWindowHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕状态栏高度
     * <p>
     * 需要在屏幕绘制完成后获取
     *
     * @param context 上下文环境
     */
    public static int getWindowStateHeight(Context context) {
        Rect rect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * 获取屏幕标题栏高度
     * <p>
     * 需要在屏幕绘制完成后获取
     *
     * @param context 上下文环境
     */
    public static int getWindowTitleHeight(Context context) {
        View view = ((Activity) context).getWindow().getDecorView();
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        return rect.height() - view.getHeight();
    }

    /**
     * 获取当前屏幕截图
     *
     * @param activity 正在运行的activity对象
     */
    public static Bitmap getWindowScreenShots(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = getWindowWidth(activity);
        int height = getWindowHeight(activity);
        Bitmap bitmap = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bitmap;
    }

}
