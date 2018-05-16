package com.example.administrator.mylibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.perfect.library.utils.system.DisplayUtils;
import com.perfect.library.utils.ScreenUtils;

/**
 * Created by Canghaixiao.
 * Time : 2017/1/13 10:40.
 * Function :
 */

public class MyTablayout extends TabLayout {
    private Context mContext;
    private int windowWidth;

    public MyTablayout(Context context) {
        this(context, null);

    }

    public MyTablayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTablayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext=context;
        windowWidth = DisplayUtils.dp2px(context, ScreenUtils.getWindowWidth(context));
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        super.setupWithViewPager(viewPager);
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh) {
        super.setupWithViewPager(viewPager, autoRefresh);
        for (int i = 0; i < this.getTabCount(); i++) {
//            TabLayout.Tab tab = this.getTabAt(i);
//            LinearLayout layout= (LinearLayout) this.getChildAt(0);
            LinearLayout linearLayout = (LinearLayout) this.getChildAt(0);
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//            linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,R.drawable.layout_divider_vertical));
            linearLayout.setDividerDrawable(new itemline(mContext));
        }
    }

    public class itemline extends Drawable {
        private Paint mPaint;
        private RectF rectF;
        Context context;

        public itemline(Context context) {
            mPaint = new Paint();
            this.context = context;
        }

        @Override
        public void draw(Canvas canvas) {

            Rect rects=getBounds();

            RectF rect = new RectF(rects.left, 0, rects.left+10, 17);
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawRect(rect, mPaint);
            RectF rectF1 = new RectF(rects.left, 17, rects.left+10, 26);
            mPaint.setColor(Color.parseColor("#ff0000"));
            canvas.drawRect(rectF1, mPaint);
            RectF rectF2 = new RectF(rects.left, 26, rects.left+10, 44);
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawRect(rectF2, mPaint);
            setBounds(0, 0, 10, 44);
        }

        @Override
        public void setAlpha(int i) {
            mPaint.setAlpha(i);
        }


        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

//    private class MyDrawable extends Drawable{
//
//        @Override
//        public void draw(Canvas canvas) {
//
//        }
//
//        @Override
//        public void setAlpha(int i) {
//
//        }
//
//        @Override
//        public void setBounds(Rect bounds) {
//            super.setBounds(bounds);
//        }
//
//        @Override
//        public void setBounds(int left, int top, int right, int bottom) {
//            super.setBounds(left, top, right, bottom);
//        }
//
//        @Override
//        public void setColorFilter(ColorFilter colorFilter) {
//
//        }
//
//        @Override
//        public int getOpacity() {
//            return 0;
//        }
//    }

}
