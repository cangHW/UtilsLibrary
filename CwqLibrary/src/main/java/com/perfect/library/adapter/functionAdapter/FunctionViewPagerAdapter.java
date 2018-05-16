package com.perfect.library.adapter.functionAdapter;

import android.content.Context;

import com.perfect.library.utils.exception.ExceptionUtils;
import com.perfect.library.adapter.baseAdapter.BaseViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/13 14:40.
 * Function :对BaseViewPagerAdapter的功能方面扩充
 */

public abstract class FunctionViewPagerAdapter<T extends Object> extends BaseViewPagerAdapter<T> {
    public FunctionViewPagerAdapter(Context context) {
        super(context);
    }

    public FunctionViewPagerAdapter(Context context, List<T> datas) {
        super(context, datas);
    }

    /**
     * 把数据集添加在顶部
     *
     * @param datas 数据集合
     * @throws NullPointerException datas不能为null
     */
    public void addDatasAtTop(List<T> datas) {
        ExceptionUtils.checkObjectIsNull(datas,"The object cannot be NULL.");
        this.addDatas(ITEM_LEFT, datas);
    }

    /**
     * 把数据集添加到某个位置
     *
     * @param location 需要放置数据集合的位置
     * @param datas    数据集合
     * @throws IndexOutOfBoundsException location不能超出数据集的范围
     * @throws NullPointerException      datas不能为null
     */
    public void addDatasAtLocation(int location, List<T> datas) {
        ExceptionUtils.checkLocationIsLegal(location,getCount());
        ExceptionUtils.checkObjectIsNull(datas,"The object cannot be NULL.");
        this.addDatas(location, datas);
    }

    /**
     * 把数据集添加在底部
     *
     * @param datas 数据集合
     * @throws NullPointerException datas不能为null
     */
    public void addDatasAtBottom(List<T> datas) {
        ExceptionUtils.checkObjectIsNull(datas,"The object cannot be NULL.");
        this.addDatas(ITEM_RIGHT, datas);
    }

    /**
     * 添加数据集
     *
     * @param location 需要放置数据集合的位置
     * @param datas    数据集合
     */
    private void addDatas(int location, List<T> datas) {
        this.getDatas_item().addAll(location, datas);
        this.setNotify();
    }

    /**
     * 把单个数据添加在顶部
     *
     * @param t 数据对象
     * @throws NullPointerException t不能为null
     */
    public void addDataAtTop(T t) {
        ExceptionUtils.checkObjectIsNull(t,"The object cannot be NULL.");
        this.addData(ITEM_LEFT, t);
    }

    /**
     * 把单个数据添加到某个位置
     *
     * @param location 添加位置
     * @throws IndexOutOfBoundsException location不能超出数据集的范围
     * @throws NullPointerException      t不能为null
     */
    public void addDataAtLocation(int location, T t) {
        ExceptionUtils.checkLocationIsLegal(location,getCount());
        ExceptionUtils.checkObjectIsNull(t,"The object cannot be NULL.");
        this.addData(location, t);
    }

    /**
     * 把单个数据添加在底部
     *
     * @param t 数据对象
     * @throws NullPointerException t不能为null
     */
    public void addDataAtBottom(T t) {
        ExceptionUtils.checkObjectIsNull(t,"The object cannot be NULL.");
        this.addData(ITEM_RIGHT, t);
    }

    /**
     * 添加单个数据
     *
     * @param location 添加位置
     * @param t        数据对象
     */
    private void addData(int location, T t) {
        this.getDatas_item().add(location, t);
        this.setNotify();
    }

    /**
     * 通过数据对象删除item
     *
     * @param ts 数据集合,格式:T t, T t或T[] new T[]{T t, T t}
     * @throws NullPointerException t不能为null
     */
    public void deleteItemsFromData(T... ts) {
        for (T t : ts) {
            ExceptionUtils.checkObjectIsNull(t,"The object cannot be NULL.");
            this.getDatas_item().remove(t);
        }
        this.setNotify();
    }

    /**
     * 通过数据对象集删除item
     *
     * @param datas 数据集合
     * @throws NullPointerException datas不能为null
     * @throws NullPointerException t不能为null
     */
    public void deleteItemsFromData(List<T> datas) {
        ExceptionUtils.checkObjectIsNull(datas,"The object cannot be NULL.");
        for (T t : datas) {
            ExceptionUtils.checkObjectIsNull(t,"The object cannot be NULL.");
            this.getDatas_item().remove(t);
        }
        this.setNotify();
    }

    /**
     * 通过位置删除item
     *
     * @param locations 数据位置集合,格式:int location, int location或int[] new int[]{int location, int location}
     * @throws IndexOutOfBoundsException location不能超出数据集的范围
     */
    public void deleteItemsFromLocation(int... locations) {
        int[] ints = this.sortingFromIntArray(locations);
        for (int location : ints) {
            ExceptionUtils.checkLocationIsLegal(location,getCount());
            this.getDatas_item().remove(location);
        }
        this.setNotify();
    }

    /**
     * 根据location更新数据
     *
     * @param location 需要更新的数据在数据集里面的位置
     * @param t        新的数据
     * @throws IndexOutOfBoundsException location不能超出数据集的范围
     * @throws NullPointerException      t不能为null
     */
    public void upData(int location, T t) {
        ExceptionUtils.checkLocationIsLegal(location,getCount());
        ExceptionUtils.checkObjectIsNull(t,"The object cannot be NULL.");
        this.getDatas_item().remove(location);
        this.getDatas_item().add(location, t);
        this.setNotify();
    }

    /**
     * 更新多组数据
     *
     * @param objects 格式:int location, T data, int location, T data
     * @throws IllegalAccessError        object数量不是偶数
     * @throws IndexOutOfBoundsException location不能超出数据集的范围
     * @throws NullPointerException      t不能为null
     * @throws ClassCastException        类型转换异常
     */
    public void upDatas(Object... objects) {
        ExceptionUtils.checkObjectsNumberIsEven(objects,"Please check the objects's size,The objects's size cannot be odd number.");
        int number = objects.length;
        for (int i = 0; i < number; i++) {
            try {
                int location = (int) objects[i * 2];
                T data = (T) objects[i * 2 + 1];
                this.upData(location, data);
            } catch (Exception e) {
                throw new ClassCastException("Please check the objects,some objects are not legal.");
            }
        }
    }

    /**
     * 查找数据
     *
     * @param location 要查找的数据在数据集里面的位置
     * @return 此方法获得的数据对象与集合内的数据对象指针指向同一位置
     * @throws IndexOutOfBoundsException location不能超出数据集的范围
     */
    public T findDataByLocation(int location) {
        ExceptionUtils.checkLocationIsLegal(location,getCount());
        return this.getDatas_item().get(location);
    }

    /**
     * 查找多组数据
     *
     * @param ints 要查找数据的位置集合,格式:int location, int location或int[] new int[]{int location, int location}
     * @return 此方法获得的数据对象与集合内的数据对象指针指向同一位置
     * @throws IndexOutOfBoundsException location不能超出数据集的范围
     */
    public List<T> findDatasByLocations(int... ints) {
        List<T> list = new ArrayList<T>();
        for (int location : ints) {
            ExceptionUtils.checkLocationIsLegal(location,getCount());
            list.add(this.getDatas_item().get(location));
        }
        return list;
    }

    /**
     * 对数组进行从大到小排序
     *
     * @param arrays 需要进行排序的int数组
     * @return 返回按照从大到小规则排序好的数组
     */
    private int[] sortingFromIntArray(int[] arrays) {
        int length = arrays.length;
        int[] ints = new int[length];
        for (int k = 0; k < length; k++) {
            int i = -1;
            int m = -1;
            for (int l = 0; l < length; l++) {
                if (i == -1) {
                    i = arrays[l];
                    m = 0;
                } else {
                    if (i < arrays[l]) {
                        i = arrays[l];
                        m = l;
                    }
                }
            }
            ints[k] = i;
            arrays[m] = -1;
        }
        return ints;
    }

    /**
     * 刷新当前显示区域
     */
    public void setNotify() {
        this.notifyDataSetChanged();
    }

}
