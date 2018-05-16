package com.perfect.library.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/16 17:24.
 * Function :随机数字的图片验证码管理类
 */

public class ImageAutCodeManager {

    private static ImageAutCodeManager mUtils = new ImageAutCodeManager();
    /**
     * 图片宽度
     */
    private int mWidth = 140;
    /**
     * 图片高度
     */
    private int mHeight = 40;
    /**
     * 数字位数
     */
    private int mCodeLen = 4;
    /**
     * 验证码内容
     */
    private String mCheckCode = "";
    /**
     * 随机数生成器
     */
    private Random mRandom = new Random();

    /**
     * 获取当前类对象
     *
     * @return 返回当前类对象
     */
    public static ImageAutCodeManager getInstence() {
        if (mUtils != null) {
            mUtils = new ImageAutCodeManager();
        }
        return mUtils;
    }

    /**
     * 产生一个随机数字的图片验证码
     *
     * @param width   设置图片宽度，设为0或小于0则使用默认值
     * @param height  设置图片高度，设为0或小于0则使用默认值
     * @param codeLen 设置验证码长度，设为0或小于0则使用默认值
     * @return 生成好的Bitmap
     */
    public Bitmap createCode(int width, int height, int codeLen) {

        if (width > 0) {
            this.mWidth = width;
        }
        if (height > 0) {
            this.mHeight = height;
        }
        if (codeLen > 0) {
            this.mCodeLen = codeLen;
        }

        mCheckCode = "";
        String[] chars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (int i = 0; i < codeLen; i++) {
            mCheckCode += chars[mRandom.nextInt(chars.length)];
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setTextSize(30);
        paint.setColor(Color.BLUE);
        for (int i = 0; i < mCheckCode.length(); i++) {
            paint.setColor(randomColor());
            paint.setFakeBoldText(mRandom.nextBoolean());
            float skewX = mRandom.nextInt(11) / 10;
            paint.setTextSkewX(mRandom.nextBoolean() ? skewX : -skewX);
            int x = width / codeLen * i + mRandom.nextInt(10);
            canvas.drawText(String.valueOf(mCheckCode.charAt(i)), x, 28, paint);
        }
        for (int i = 0; i < 3; i++) {
            drawLine(canvas, paint);
        }
        for (int i = 0; i < 255; i++) {
            drawPoints(canvas, paint);
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bitmap;
    }

    /**
     * 获得一个随机的颜色
     *
     * @return 返回一个颜色数值
     */
    private int randomColor() {
        int red = mRandom.nextInt(256), green = mRandom.nextInt(256), blue = mRandom.nextInt(256);
        return Color.rgb(red, green, blue);
    }

    /**
     * 画随机线条
     *
     * @param canvas 画布
     * @param paint  画笔
     */
    private void drawLine(Canvas canvas, Paint paint) {
        int startX = mRandom.nextInt(mWidth), startY = mRandom.nextInt(mHeight);
        int stopX = mRandom.nextInt(mWidth), stopY = mRandom.nextInt(mHeight);
        paint.setStrokeWidth(1);
        paint.setColor(randomColor());
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    /**
     * 画随机干扰点
     *
     * @param canvas 画布
     * @param paint  画笔
     */
    private void drawPoints(Canvas canvas, Paint paint) {
        int stopX = mRandom.nextInt(mWidth), stopY = mRandom.nextInt(mHeight);
        paint.setStrokeWidth(1);
        paint.setColor(randomColor());
        canvas.drawPoint(stopX, stopY, paint);
    }

    /**
     * 返回真实验证码字符串
     *
     * @return 验证码内容
     */
    public String getCheckCode() {
        return mCheckCode;
    }
}
