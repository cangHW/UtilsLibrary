package com.perfect.library.utils.system.messageBean;

import android.graphics.drawable.Drawable;

/**
 * Created by Canghaixiao.
 * Time : 2017/11/17 12:17.
 * Function :
 */
public class AppInfoMessage {
    public String name;
    public Drawable icon;
    public String packageName;
    public String versionName;
    public int versionCode;
    /**
     * 是否安装在sd卡
     * */
    public boolean isSD;
    /**
     * 是否是普通应用(非系统应用)
     * */
    public boolean isUser;
}
