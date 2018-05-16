package com.perfect.library.utils.bitmap.messageBean;

import android.support.annotation.ColorInt;

/**
 * Created by Canghaixiao.
 * Time : 2017/1/17 15:40.
 * Function : 在图片上打印文字时所需要的文字信息
 */

public class PrintWordTextMessage {
    /**
     * 字体颜色 格式：0xFF000000
     */
    @ColorInt
    public int text_color = -1;
    /**
     * 字体尺寸
     */
    public int text_size = -1;
    /**
     * 是否加粗
     */
    public boolean IsShouldBold = false;
    /**
     * x轴开始位置
     */
    public int left = -1;
    /**
     * y轴开始位置
     */
    public int top = -1;
}
