package com.example.administrator.mylibrary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.mylibrary.R;

/**
 * Created by Canghaixiao.
 * Time : 2017/1/4 15:02.
 * Function :
 */

public class TestOneFragment extends BaseFragment {

    private TextView text_view;
    private String message="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            message=getArguments().getString("message","0");
        }
//        if (savedInstanceState!=null){
//            Log.d("asd","有");
//        }else {
//            Log.d("asd","无");
//        }
    }

    @Override
    protected int getResourceID() {
        return R.layout.fragment_one;
    }

    @Override
    protected void initView(View rootView) {
        text_view= (TextView) rootView.findViewById(R.id.text_view);
    }

    @Override
    protected void initData() {
        text_view.setText(message);
    }
}
