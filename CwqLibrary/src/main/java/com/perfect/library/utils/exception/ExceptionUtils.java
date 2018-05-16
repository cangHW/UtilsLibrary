package com.perfect.library.utils.exception;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.perfect.library.fragment.BasePerfectFragment;

/**
 * Created by Canghaixiao.
 * Time : 2017/1/3 14:28.
 * Function :异常工具类，用于检测内容的合法性
 */

public class ExceptionUtils {

    /**
     * 检测位置是否合法
     *
     * @param location 需要检测的位置
     */
    public static void checkLocationIsLegal(int location, int size) {
        if (location < 0) {
            throw new IndexOutOfBoundsException("Please check the location,Can be less than the zero?");
        }
        if (location >= size) {
            throw new IndexOutOfBoundsException("Please check the location,Can be greater than the Datas size?");
        }
    }

    /**
     * 检测数据集的成员数量是否是偶数
     *
     * @param objects 数据集合,格式:Object object, Object object或Object[] new Object[]{Object object, Object object}
     */
    public static void checkObjectsNumberIsEven(Object[] objects, String errMsg) {
        if (CheckUtils.checkIsEvent(objects)) {
            throw new IllegalAccessError(errMsg);
        }
    }

    /**
     * 检测fragment是否是继承于com.perfect.library.Fragment.BasePerfectFragment
     */
    public static void checkFragmentIsLegal(Fragment fragment) {
        if (fragment instanceof BasePerfectFragment) {
            return;
        }
        throw new ClassCastException("The fragment should be in basefragmtn inheritance");
    }

    /**
     * 检查object是否为空或null
     */
    public static void checkObjectIsEmpty(String s, String errMsg) {
        if (CheckUtils.checkIsEmpty(s)) {
            throw new IllegalAccessError(errMsg);
        }
    }

    /**
     * 检查object是否为null
     */
    public static void checkObjectIsNull(Object o, String errMsg) {
        if (CheckUtils.checkIsNull(o)) {
            throw new NullPointerException(errMsg);
        }
    }

    /**
     * 检查字符串是否是数字组成(只含整数)
     */
    public static void checkStringIsNum(String string, String errMsg) {
        if (!CheckUtils.checkIsNum(string)) {
            throw new IllegalAccessError(errMsg);
        }
    }

    /**
     * 检查字符串是否是数字(包含整数、小数、正负数)
     */
    public static void checkStringIsNum2(String string, String errMsg) {
        if (!CheckUtils.checkIsNum2(string)) {
            throw new IllegalAccessError(errMsg);
        }
    }
}
