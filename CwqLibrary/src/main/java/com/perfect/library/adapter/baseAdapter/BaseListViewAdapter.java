package com.perfect.library.adapter.baseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.perfect.library.utils.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Canghaixiao.
 * Time : 2016/12/7 14:20.
 * Function :自定义ListView等的基类适配器
 */

public abstract class BaseListViewAdapter<T, K extends BaseListViewAdapter.BaseViewHolder> extends BaseAdapter {

    /**
     * 上下文环境
     */
    protected Context mContext;
    /**
     * 数据集
     */
    private List<T> mDatas = new ArrayList<T>();
    /**
     * 布局加载器
     */
    private LayoutInflater mInflater;
    /**
     * item第一个位置
     */
    protected static final int ITEM_LEFT = 0;
    /**
     * item最后一个位置
     */
    protected int ITEM_RIGHT = 0;

    public BaseListViewAdapter(Context context) {
        initInflater(context);
    }

    public BaseListViewAdapter(Context context, List<T> datas) {
        initInflater(context);
        if (datas!=null) {
            this.setDatas(datas);
        }
    }

    /**
     * 初始化Inflater,context
     *
     * @param context 上下文环境
     */
    private void initInflater(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    /**
     * 初始化数据集
     *
     * @param datas 数据集合
     * @throws NullPointerException datas不能为null
     */
    public void setDatas(List<T> datas) {
        ExceptionUtils.checkObjectIsNull(datas,"The object cannot be NULL.");
        this.mDatas.clear();
        this.mDatas.addAll(datas);
    }

    /**
     * 获得数据集
     *
     * @return 返回当前的数据集
     */
    public List<T> getDatas() {
        return mDatas;
    }

    @Override
    public int getCount() {
        ITEM_RIGHT=mDatas == null ? 0 : mDatas.size();
        return ITEM_RIGHT;
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = CreateViewHolder(mInflater, parent, getItemViewType(position));
            convertView = viewHolder.itemView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (BaseViewHolder) convertView.getTag();
        }
        viewHolder.setPosition(position);
        T t = getItem(position);
        onBindView((K) viewHolder, convertView, t);
        return convertView;
    }

    /**
     * 创建viewHolder,初始化控件
     *
     * @param inflater     布局加载器
     * @param parent       父布局对象
     * @param itemViewType item类型
     */
    protected abstract BaseViewHolder CreateViewHolder(LayoutInflater inflater, ViewGroup parent, int itemViewType);

    /**
     * 数据绑定，事件绑定
     *
     * @param k        当前viewholder对象
     * @param itemView item自身
     * @param t        当前数据对象
     */
    protected abstract void onBindView(K k, View itemView, T t);

    /**
     * 控件缓存基类，解耦和
     */
    public static abstract class BaseViewHolder {
        private final View itemView;
        private int mPosition;

        /**
         * 构造函数
         *
         * @param itemView item的view对象
         */
        public BaseViewHolder(View itemView) {
            ExceptionUtils.checkObjectIsNull(itemView,"The object cannot be NULL.");
            this.itemView = itemView;
        }

        /**
         * 设置当前item的position
         *
         * @param position 当前的position
         */
        private void setPosition(int position) {
            this.mPosition = position;
        }

        /**
         * 获得当前item的position
         *
         * @return 返回当前的position
         */
        public int getPosition() {
            return mPosition;
        }
    }
}
