package com.perfect.library.utils;

import android.graphics.Bitmap;

import com.perfect.library.utils.exception.ExceptionUtils;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/12 17:02.
 * Function :缓存工具类，解决intent跳转无法携带大数据等问题，方便内存回收
 * (系统会非常容易回收该缓存里面的数据，因此此处缓存的数据并不是太安全)
 */

public class DataCatchManager {

    /**
     * 默认key值
     * */
    public static final String BASE_KEY = "catch_base_key";
    /**
     * 当前类对象
     * */
    private static DataCatchManager mUtils=new DataCatchManager();
    /**
     * 存储数据map
     * */
    private Map<String, SoftReference<Object>> mMap = new HashMap<>();

    /**
     * 构造函数
     * */
    private DataCatchManager(){

    }

    /**
     * 获取当前类对象
     * */
    public static DataCatchManager create() {
        if (mUtils == null) {
            mUtils = new DataCatchManager();
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
    public synchronized void put(String key, Object value) {
        ExceptionUtils.checkObjectIsEmpty(key,"Please check the key,Are you sure it's true?");
        ExceptionUtils.checkObjectIsNull(value,"You cannot catch the NULL.");
        SoftReference<Object> weak = new SoftReference<>(value);
        if (mMap == null) {
            mMap = new HashMap<>();
        }
        mMap.put(key, weak);
    }

    /**
     * 读取数据
     *
     * @param key 准备读取的数据的唯一标识
     * @throws NullPointerException key不能为空或为null
     * @return 返回读取到的数据，此数据可能为空或null
     */
    public synchronized Object get(String key) {
        ExceptionUtils.checkObjectIsEmpty(key,"Please check the key,Are you sure it's true?");
        if (mMap != null) {
            SoftReference<Object> weak = mMap.get(key);
            if (weak != null) {
                return weak.get();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 移除缓存
     *
     * @param key 唯一标识
     */
    public synchronized void removeObjectByKey(String key) {
        if (key != null) {
            if (mMap != null) {
                SoftReference<Object> weak = mMap.get(key);
                if (weak != null) {
                    mMap.remove(key);
                    Object object= weak.get();
                    weak.clear();
                    if (object!=null&&object instanceof Bitmap) {
                        Bitmap bm = (Bitmap) object;
                        if (!bm.isRecycled()) {
                            bm.recycle();
                        }
                    }
                }
            }
        }
    }

    /**
     * 清空缓存
     * */
    public void clear(){
        if (mMap!=null){
            mMap.clear();
            mMap=null;
        }
    }

}
