package com.example.administrator.mylibrary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.perfect.library.adapter.functionAdapter.FunctionViewPagerAdapter;

/**
 * Created by cangHaiXiao.
 * Time : 2016/12/13 10:05.
 * Function :
 */

public class ViewPagerFragmentAdapter extends FunctionViewPagerAdapter<String> {

    public ViewPagerFragmentAdapter(Context context) {
        super(context);
    }

    @Override
    protected View CreatItem(ViewGroup container, String string, int position) {
        View imageView = null;

//        if (position%3==0){
//            imageView = new ImageView(mContext);
//            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            imageView.setLayoutParams(params);
//            ((ImageView)imageView).setImageResource(R.drawable.qwe);
//        }else if (position%3==1){
            imageView = new TextView(mContext);
            ((TextView)imageView).setTextSize(30);
            ((TextView)imageView).setTextColor(Color.parseColor("#00ff00"));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);
            ((TextView)imageView).setText(string);
//        }else if (position%3==2){
//            imageView=new Button(mContext);
//            ((Button)imageView).setText("啦啦啦"+position);
//            ((Button)imageView).setTextSize(30);
//            ((Button)imageView).setTextColor(Color.parseColor("#00ff00"));
//            ((Button)imageView).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(mContext,"123",Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
        return imageView;
    }

    @Override
    protected boolean DestroyItem(ViewGroup container, View view, int position, Object object) {
        return false;
    }

}
