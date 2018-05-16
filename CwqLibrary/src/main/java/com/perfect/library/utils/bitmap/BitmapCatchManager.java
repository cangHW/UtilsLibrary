package com.perfect.library.utils.bitmap;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.perfect.library.utils.exception.ExceptionUtils;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/16 18:22.
 * Function :图片缓存管理类，采用最少最近原则
 * 有效控制内存消耗，保证数据的安全性
 */

public class BitmapCatchManager {

    /**
     * 默认key值
     */
    public static final String BASE_KEY = "catch_base_key";
    /**
     * 当前类对象
     */
    private static BitmapCatchManager mUtils;
    /**
     * 缓存数据catch
     */
    private BitmapLruCatch mCache;

    /**
     * 构造函数 初始化缓存类
     *
     * @param context 上下文环境
     * @throws NullPointerException context不能为null
     */
    private BitmapCatchManager(Context context) {
        if (context == null) {
            throw new NullPointerException("The context cannot be NULL.");
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memory = 0;
        if (activityManager != null) {
            memory = activityManager.getMemoryClass();
        }
//        int largememory = activityManager.getLargeMemoryClass();
        mCache = new BitmapLruCatch(memory / 8);
    }

    /**
     * 获取当前类对象
     *
     * @param context 上下文环境
     * @return 返回当前类对象
     */
    public static BitmapCatchManager create(Context context) {
        if (mUtils == null) {
            mUtils = new BitmapCatchManager(context);
        }
        return mUtils;
    }

    /**
     * 存储数据
     *
     * @param key   要储存数据的key值
     * @param value 要储存的数据
     * @throws NullPointerException key不能为空或为null
     * @throws IllegalAccessError   不能存储null
     */
    public synchronized void put(String key, Bitmap value) {
        ExceptionUtils.checkObjectIsEmpty(key,"Please check the key,Are you sure it's true?");
        ExceptionUtils.checkObjectIsNull(value,"You cannot catch the NULL.");
        if (mCache != null) {
            mCache.put(key, value);
        }
    }

    /**
     * 读取数据
     *
     * @param key 准备读取的数据的唯一标识
     * @return 返回读取到的数据，此数据可能为空或null
     * @throws NullPointerException key不能为空或为null
     */
    public synchronized Bitmap get(String key) {
        ExceptionUtils.checkObjectIsEmpty(key,"Please check the key,Are you sure it's true?");
        if (mCache != null) {
            return mCache.get(key);
        } else {
            return null;
        }
    }

    /**
     * 移除缓存
     *
     * @param key 唯一标识
     */
    public synchronized void removeBitmapByKey(String key) {
        if (key != null) {
            if (mCache != null) {
                Bitmap bm = mCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }

    /**
     * 清空缓存
     */
    public void clear() {
        if (mCache != null) {
            mCache.evictAll();
            mCache=null;
        }
    }

    private class BitmapLruCatch extends LruCache<String, Bitmap> {

        private BitmapLruCatch(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
            if (evicted && oldValue != null) {
                oldValue.recycle();
            }
        }

        /**
         * 根据项目情况进行处理
         */
        @Override
        protected Bitmap create(String key) {
            return super.create(key);
        }
    }

}
