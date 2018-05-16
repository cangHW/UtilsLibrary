package com.perfect.library.adapter.baseAdapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.perfect.library.utils.exception.ExceptionUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/12 18:18.
 * Function :自定义Viewpager的基类适配器
 */

public abstract class BaseViewPagerAdapter<T extends Object> extends PagerAdapter {

    /**
     * 上下文环境
     */
    protected Context mContext;
    /**
     * item数据集
     */
    protected List<T> mDataItems = new ArrayList<T>();
    /**
     * title数据集
     */
    protected List<String> mDataTitles = new ArrayList<String>();
    /**
     * viewpager缓存集
     */
    private Map<String, WeakReference<View>> mMap = new HashMap<>();
    /**
     * item第一个位置
     */
    public static final int ITEM_LEFT = 0;
    /**
     * item最后一个位置
     */
    public int ITEM_RIGHT = 0;

    /**
     * 构造函数
     *
     * @param context 上下文环境
     */
    public BaseViewPagerAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * 构造函数
     *
     * @param context    上下文环境
     * @param datas_item item数据集
     */
    public BaseViewPagerAdapter(Context context, List<T> datas_item) {
        this.mContext = context;
        if (datas_item!=null) {
            this.mDataItems.addAll(datas_item);
        }
    }

    /**
     * 初始化item数据集
     *
     * @param datas_item item数据集
     * @throws NullPointerException item数据集datas_item不能为null
     */
    public void setDatas_item(List<T> datas_item) {
        ExceptionUtils.checkObjectIsNull(datas_item,"The object cannot be NULL.");
        this.mDataItems.addAll(datas_item);
    }

    /**
     * 获得item数据集
     *
     * @return item数据集
     */
    public List<T> getDatas_item() {
        return mDataItems;
    }

    /**
     * 初始化title数据集
     *
     * @param datas_title title数据集
     * @throws NullPointerException title数据集datas_title不能为null
     */
    public void setDatas_title(List<String> datas_title) {
        ExceptionUtils.checkObjectIsNull(datas_title,"The object cannot be NULL.");
        this.mDataTitles.addAll(datas_title);
    }

    /**
     * 获得title数据集
     *
     * @return title数据集
     */
    public List<String> getDatas_title() {
        return mDataTitles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mDataTitles == null || mDataTitles.size() <= position ? super.getPageTitle(position) : setPageTitle(position);
    }

    @Override
    public int getCount() {
        ITEM_RIGHT = mDataItems == null ? 0 : mDataItems.size();
        return ITEM_RIGHT;
    }

    @Override
    public int getItemPosition(Object object) {
        ViewHolder holder = (ViewHolder) object;
        if (holder != null && holder.position < mDataItems.size()) {
            T t = mDataItems.get(holder.position);
            if (t.hashCode() == holder.t.hashCode()) {
                return POSITION_UNCHANGED;
            }
        }
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.getTag().hashCode() == object.hashCode();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        WeakReference<View> weak = mMap.get(position + "");
        if (weak != null) {
            View view = weak.get();
            if (view != null) {
                boolean flag = DestroyItem(container, view, position, object);
                if (!flag) {
                    container.removeView(view);
                }
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        ViewHolder holder = null;
        T t = this.mDataItems.get(position);
        String key = makeItemName(container.getId(), getItemId(position));
        if (mMap != null) {
            WeakReference<View> weak = mMap.get(key);
            if (weak != null) {
                view = weak.get();
                if (view!=null) {
                    Object o=view.getTag();
                    if (o!=null) {
                        holder = (ViewHolder) o;
                    }else {
                        holder=new ViewHolder();
                    }
                }
            }
        }
        if (view == null) {
            view = CreatItem(container, t, position);
            holder = new ViewHolder();
        }
        if (view == null || holder == null) {
            throw new NullPointerException("The view cannot be NULL.");
        }
        holder.t = t;
        holder.position = position;
        view.setTag(holder);
        container.addView(view);
        if (mMap == null) {
            mMap = new HashMap<>();
        }
        mMap.put(key, new WeakReference<View>(view));
        return holder;
    }

    /**
     * 设置标题title，需要传入tablayout
     *
     * @param position 标题在viewpager里面的位置
     * @return 返回标题内容
     */
    protected String setPageTitle(int position) {
        return mDataTitles.get(position);
    }

    /**
     * 创建item对象
     *
     * @param container viewpager内部的外层布局
     * @param t         数据
     * @param position  即将创建的item在viewpager里面的位置
     * @return 返回创建好的item
     */
    protected abstract View CreatItem(ViewGroup container, T t, int position);

    /**
     * 移除item,返回true表示用户自己处理移除这个活动，返回false表示用户把移除这个活动交给了BaseViewPagerAdapter
     *
     * @param container viewpager内部的外层布局
     * @param view      即将被移除的view
     * @param position  即将被移除的view在viewpager里面的位置
     * @param object    标识
     */
    protected abstract boolean DestroyItem(ViewGroup container, View view, int position, Object object);

    /**
     * 根据position，获得item的id
     *
     * @param position 位置
     */
    private long getItemId(int position) {
        T t = this.mDataItems.get(position);
        return t.hashCode();
    }

    /**
     * 通过id设置缓存名称
     *
     * @param viewId 父类view的id
     * @param id     即将设置缓存名称的id
     */
    private static String makeItemName(int viewId, long id) {
        return "CwqLibrary:" + viewId + ":" + id;
    }

    /**
     * 标识，用于判断item是否需要重建
     */
    private class ViewHolder {
        public int position;
        public T t;
    }
}
