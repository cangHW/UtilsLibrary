package com.example.administrator.mylibrary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.perfect.library.fragment.BasePerfectFragment;

/**
 * Created by cangHaiXiao.
 * Time : 2016/12/13 10:00.
 * Function :fragment基类
 */

public abstract class BaseFragment extends BasePerfectFragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(getResourceID(),container,false);
        initView(view);
        initData();
        return view;
    }

    protected abstract int getResourceID();
    protected abstract void initView(View rootView);
    protected abstract void initData();
}
