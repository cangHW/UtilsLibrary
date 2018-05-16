package com.example.administrator.mylibrary.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.example.administrator.mylibrary.fragment.TestOneFragment;
import com.perfect.library.adapter.functionAdapter.FunctionFragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Canghaixiao.
 * Time : 2017/1/4 15:00.
 * Function :
 */

public class TeseAdapter extends FunctionFragmentStateAdapter<String> {

    private List<String> title=new ArrayList<>();

    public TeseAdapter(FragmentManager fm) {
        super(fm);
    }

    public TeseAdapter(FragmentManager fm, List<String> datas) {
        super(fm, datas);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }

    public void setTitle(List<String> list){
        this.title.addAll(list);
    }

    @Override
    protected Fragment CreatItemFragment(String s, int position) {
        Log.d("asd",position+"");
        TestOneFragment fragment=new TestOneFragment();
        Bundle bundle=new Bundle();
        bundle.putString("message",position+"");
        fragment.setArguments(bundle);
        return fragment;
    }
}
