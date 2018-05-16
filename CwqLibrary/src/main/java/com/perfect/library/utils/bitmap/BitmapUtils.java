package com.perfect.library.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;

import com.perfect.library.utils.bitmap.messageBean.PrintWordTextMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/16 12:20.
 * Function :关于bitmap操作的工具类
 */

public class BitmapUtils {

    /**
     * 压缩图片,尺寸压缩
     *
     * @param bitmap   准备压缩的bitmap
     * @param width    想要的宽度
     * @param height   想要的高度
     * @param isAdjust 是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩
     * @return 压缩后的Bitmap
     */
    public Bitmap compressBitmapBySize(Bitmap bitmap, int width, int height, boolean isAdjust) {
        if (bitmap.getWidth() < width && bitmap.getHeight() < height) {
            return bitmap;
        }
        float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN).floatValue();
        float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN).floatValue();
        if (isAdjust) {
            sx = sx < sy ? sx : sy;
            sy = sx;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 压缩图片,质量压缩 适用于图片上传
     *
     * @param bitmap 准备压缩的bitmap
     * @param maxkb  压缩后的大小
     * @return 返回压缩后的bitmap
     */
    public static Bitmap compressBitmapByQuality(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > maxkb) {
            baos.reset();
            options -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * 旋转图片
     *
     * @param bitmap 准备旋转的bitmap
     * @param angle  旋转角度(90为顺时针旋转,-90为逆时针旋转)
     * @return 旋转后的Bitmap
     */
    public Bitmap rotate(Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 放大或缩小图片
     *
     * @param bitmap 准备放大或缩小的bitmap
     * @param ratio  放大或缩小的倍数，大于1表示放大，小于1表示缩小
     * @return 处理后的Bitmap
     */
    public Bitmap zoom(Bitmap bitmap, float ratio) {
        if (ratio < 0f) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 在图片上印字
     *
     * @param bitmap 需要印文字的bitmap
     * @param text   需要印上去的文字
     * @param message  字体信息
     * @return 修改后的Bitmap
     */
    public Bitmap printWord(Bitmap bitmap, String text, PrintWordTextMessage message) {
        if (TextUtils.isEmpty(text) || null == message) {
            return bitmap;
        }
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        Paint paint = new Paint();
        paint.setColor(-1 != message.text_color ? message.text_color : Color.BLACK);
        paint.setTextSize(-1 != message.text_size ? message.text_size : 20);
        paint.setFakeBoldText(message.IsShouldBold);
        canvas.drawText(text, -1 != message.left ? message.left : 0, -1 != message.top ? message.top : 0, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newBitmap;
    }

    /**
     * 创建logo(给图片加水印),
     *
     * @param bitmaps 原图片和水印图片
     * @param left    左边起点坐标
     * @param top     顶部起点坐标t
     * @return 合成后的Bitmap
     */
    public Bitmap createLogo(Bitmap[] bitmaps, int left, int top) {
        Bitmap newBitmap = Bitmap.createBitmap(bitmaps[0].getWidth(), bitmaps[0].getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        for (int i = 0; i < bitmaps.length; i++) {
            if (i == 0) {
                canvas.drawBitmap(bitmaps[0], 0, 0, null);
            } else {
                canvas.drawBitmap(bitmaps[i], left, top, null);
            }
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        }
        return newBitmap;
    }


    /**
     * 通过byte字节生成确定宽高的bitmap
     * */
    public static Bitmap decodeBitmap(byte[] data, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        int inSampleSize = 1;
        if (options.outHeight > height || options.outWidth > width) {

            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;

            while ((halfHeight / inSampleSize) >= height|| (halfWidth / inSampleSize) >= width) {
                inSampleSize *= 2;
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

}
