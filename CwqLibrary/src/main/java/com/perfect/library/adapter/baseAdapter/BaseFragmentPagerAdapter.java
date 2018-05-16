package com.perfect.library.adapter.baseAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.perfect.library.utils.exception.ExceptionUtils;
import com.perfect.library.fragment.BasePerfectFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/30 15:55.
 * Function :自定义继承于FragmentPagerAdapter的基类，主要用于少量的静态展示页面
 */

public abstract class BaseFragmentPagerAdapter<T> extends FragmentPagerAdapter {

    /**
     * 数据集
     */
    private List<T> mDatas = new ArrayList<>();
    /**
     * item第一个位置
     */
    public static final int ITEM_LEFT = 0;
    /**
     * item最后一个位置
     */
    public int ITEM_RIGHT = 0;

    public BaseFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public BaseFragmentPagerAdapter(FragmentManager fm,List<T> datas){
        super(fm);
        if (datas!=null) {
            this.mDatas.addAll(datas);
        }
    }

    /**
     * 初始化数据集
     *
     * @param datas 数据集合
     * @throws NullPointerException list不能为null
     */
    public void setData(List<T> datas) {
        ExceptionUtils.checkObjectIsNull(datas,"The object cannot be NULL.");
        this.mDatas.addAll(datas);
    }

    /**
     * 获得数据集合
     *
     * @return 返回当前数据集合
     */
    public List<T> getDatas() {
        return mDatas;
    }

    @Override
    public int getCount() {
        ITEM_RIGHT = mDatas == null ? 0 : mDatas.size();
        return ITEM_RIGHT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        return fragment;
    }

    @Override
    public long getItemId(int position) {
        T t=mDatas.get(position);
        if (t!=null) {
            return t.hashCode();
        }else {
            return position;
        }
    }

    @Override
    public Fragment getItem(int position) {
        T t = mDatas.get(position);
        Fragment fragment = CreatItemFragment(t, position);
        ExceptionUtils.checkFragmentIsLegal(fragment);
        BasePerfectFragment basePerfectFragment = (BasePerfectFragment) fragment;
        ViewHolder holder = new ViewHolder();
        holder.position = position;
        holder.t = t;
        basePerfectFragment.setFragmentTag(holder);
        return basePerfectFragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((Fragment) object).getView();
    }

    @Override
    public int getItemPosition(Object object) {
        ViewHolder holder = (ViewHolder) ((BasePerfectFragment) object).getFragmentTag();
        if (holder != null && holder.position < mDatas.size()) {
            T t = mDatas.get(holder.position);
            if (t!=null) {
                if (t.hashCode() == holder.t.hashCode()) {
                    return POSITION_UNCHANGED;
                }
            }
        }
        return POSITION_NONE;
    }

    /**
     * 创建fragment
     *
     * @param t        数据
     * @param position 即将创建的item在viewpager里面的位置
     */
    protected abstract Fragment CreatItemFragment(T t, int position);

    /**
     * 标识，用于判断item是否需要重建
     */
    private class ViewHolder {
        int position;
        T t;
    }
}
