package com.perfect.library.utils.exception;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Canghaixiao.
 * Time : 2017/11/24 17:08.
 * Function :
 */
class CheckUtils {

    /**
     * 检查是否是数字(只含整数)
     * */
    static boolean checkIsNum(String s){
        Pattern pattern=Pattern.compile("^[0-9]*$");
        Matcher matcher=pattern.matcher(s);
        return matcher.find();
    }

    /**
     * 检查是否是数字(包含整数、小数、正负数)
     * */
    static boolean checkIsNum2(String s){
        Pattern pattern=Pattern.compile("-?[0-9]*.?[0-9]*");
        Matcher matcher=pattern.matcher(s);
        return matcher.find();
    }

    /**
     * 检查是否是null
     * */
    static boolean checkIsNull(Object o){
        return o==null;
    }

    /**
     * 检查是否是空或null
     * */
    static boolean checkIsEmpty(String s){
        return TextUtils.isEmpty(s);
    }

    /**
     * 检查是否是偶数
     * */
    static boolean checkIsEvent(Object[] objects){
        int i = objects.length % 2;
        return i==0;
    }
}
