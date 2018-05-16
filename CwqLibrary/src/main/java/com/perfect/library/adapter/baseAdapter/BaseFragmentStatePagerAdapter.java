package com.perfect.library.adapter.baseAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.perfect.library.utils.exception.ExceptionUtils;
import com.perfect.library.fragment.BasePerfectFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/30 16:00.
 * Function :自定义继承于FragmentStatePagerAdapter的基类，主要用于大量item或者可能频繁操作等消耗内存较多的情况
 */

public abstract class BaseFragmentStatePagerAdapter<T> extends FragmentStatePagerAdapter {

    /**
     * fragment缓存集
     */
    private Map<String, WeakReference<BasePerfectFragment>> mMap = new HashMap<>();
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

    public BaseFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public BaseFragmentStatePagerAdapter(FragmentManager fm,List<T> datas){
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
        return super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        BasePerfectFragment fragment = null;
        ViewHolder holder = null;
        T t = mDatas.get(position);
        String key = makeItemName(getItemId(position));
        if (mMap != null) {
            WeakReference<BasePerfectFragment> weak = mMap.get(key);
            if (weak != null) {
                fragment = weak.get();
                if (fragment!=null) {
                    Object o = fragment.getFragmentTag();
                    if (o != null) {
                        holder = (ViewHolder) o;
                    } else {
                        holder = new ViewHolder();
                    }
                }
            }
        }
        if (fragment == null) {
            fragment = (BasePerfectFragment) CreatItemFragment(t, position);
            holder = new ViewHolder();
        }
        if (fragment == null) {
            throw new NullPointerException("The fragment cannot be NULL.");
        }
        holder.position = position;
        holder.t = t;
        fragment.setFragmentTag(holder);
        if (mMap == null) {
            mMap = new HashMap<>();
        }
        mMap.put(key, new WeakReference<>(fragment));
        return fragment;
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
     * 根据position，获得item的id
     *
     * @param position 位置
     */
    private long getItemId(int position) {
        T t = this.mDatas.get(position);
        if (t!=null) {
            return t.hashCode();
        }else {
            return position;
        }
    }

    /**
     * 通过id设置缓存名称
     *
     * @param id 即将设置缓存名称的id
     */
    private static String makeItemName(long id) {
        return "CwqLibrary:" + "BaseFragmentStatePagerAdapter" + ":" + id;
    }

    /**
     * 标识，用于判断item是否需要重建
     */
    private class ViewHolder {
        int position;
        T t;
    }

}


