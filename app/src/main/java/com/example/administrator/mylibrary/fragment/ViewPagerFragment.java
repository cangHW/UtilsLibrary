package com.example.administrator.mylibrary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.administrator.mylibrary.R;
import com.example.administrator.mylibrary.adapter.TeseAdapter;
import com.example.administrator.mylibrary.adapter.ViewPagerFragmentAdapter;
import com.example.administrator.mylibrary.view.MyTablayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cangHaiXiao.
 * Time : 2016/12/13 9:57.
 * Function :
 */

public class ViewPagerFragment extends BaseFragment {

    private ViewPager viewPager;
    private Button button;
    private MyTablayout tablayout;

    @Override
    protected int getResourceID() {
        return R.layout.fragment_viewpager;
    }

    @Override
    protected void initView(View rootView) {
        viewPager= (ViewPager) rootView.findViewById(R.id.view_pager);
        tablayout= (MyTablayout) rootView.findViewById(R.id.tablayout);
        button= (Button) rootView.findViewById(R.id.button);
    }

    @Override
    protected void initData() {
//        final ViewPagerFragmentAdapter adapter=new ViewPagerFragmentAdapter(getActivity());
//        adapter.setDatas_item(getList());
//        viewPager.setAdapter(adapter);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                adapter.deleteDatasByLocation(3);
////                adapter.deleteItemsFromLocation(3);
////                adapter.notifyDataSetChanged();
//            }
//        });

        TeseAdapter adapter=new TeseAdapter(getChildFragmentManager(),getList());
        adapter.setTitle(getList());
        viewPager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewPager);
        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

//    private List<ImageView> getList(){
//        List<ImageView> list=new ArrayList<ImageView>();
//        for (int i=0;i<3;i++) {
//            ImageView imageView = new ImageView(getActivity());
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            imageView.setLayoutParams(params);
//            list.add(imageView);
//        }
//        return list;
//    }

    private List<String> getList(){
        List<String> list=new ArrayList<String>();
        for (int i=0;i<10;i++){
            list.add(i+"");
        }
//        list.add("1");
//        list.add("2");
//        list.add("3");
//        list.add("4");
//        list.add("5");
//        list.add("6");
//        list.add("7");
//        list.add("8");
        return list;
    }
}
