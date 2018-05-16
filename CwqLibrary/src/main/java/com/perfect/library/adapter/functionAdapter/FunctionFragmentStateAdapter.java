package com.perfect.library.adapter.functionAdapter;

import android.support.v4.app.FragmentManager;

import com.perfect.library.utils.exception.ExceptionUtils;
import com.perfect.library.adapter.baseAdapter.BaseFragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Canghaixiao.
 * Time : 2017/1/3 17:51.
 * Function :对BaseFragmentStatePagerAdapter的功能方面扩充
 */

public abstract class FunctionFragmentStateAdapter<T> extends BaseFragmentStatePagerAdapter<T> {

    public FunctionFragmentStateAdapter(FragmentManager fm) {
        super(fm);
    }

    public FunctionFragmentStateAdapter(FragmentManager fm, List<T> datas) {
        super(fm, datas);
    }

    /**
     * 通过位置删除item
     *
     * @param location 需要删除的位置
     * @throws IndexOutOfBoundsException location不能超出数据集的范围
     */
    public void deleteItemByLocation(int location) {
        ExceptionUtils.checkLocationIsLegal(location, getCount());
        getDatas().remove(location);
        this.notifyDataSetChanged();
    }

}
