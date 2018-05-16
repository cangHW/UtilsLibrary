package com.perfect.library.utils.anim;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.perfect.library.utils.LogUtils;

/**
 * Created by Canghaixiao.
 * Time : 2017/8/29 12:16.
 * Function :弹出弹入背景颜色变化动画管理类
 */
public class TranslationYAnimManager {

    private static final String TAG="TranslationYAnimManager";

    private static final int DEFAULT_DURATION = 200;

    private static TranslationYAnimManager mTranslationYAnimUtil;
    private TranslationAnim mTranslationAnim = new TranslationAnim();
    private TranslationAnimUpdata mTranslationAnimUpdata = new TranslationAnimUpdata();
    private int mTranslationY = 0;
    private int mDuration = 0;

    private TranslationYAnimManager() {
        mTranslationAnim.clear();
        mTranslationAnimUpdata.clear();
    }

    public static TranslationYAnimManager create() {
        mTranslationYAnimUtil = new TranslationYAnimManager();
        return mTranslationYAnimUtil;
    }

    public TranslationYAnimManager setAnimViewHeight(Context context, @DimenRes int resId) {
        mTranslationY = context.getResources().getDimensionPixelSize(resId);
        mDuration = DEFAULT_DURATION;
        return mTranslationYAnimUtil;
    }

    public TranslationYAnimManager setDuration(Context context, int duration) {
        this.mDuration = duration;
        return mTranslationYAnimUtil;
    }

    public TranslationYAnimManager setBgGradation(View view, float start, float end) {
        mTranslationAnimUpdata.setParame(view, start, end, mTranslationY);
        mTranslationAnim.setParame(view, start, end, mTranslationAnimUpdata);
        return mTranslationYAnimUtil;
    }

    public void setStartAnim(View view) {
        ViewCompat.setTranslationY(view, mTranslationY);

        ViewCompat.animate(view)
                .setInterpolator(new LinearInterpolator())
                .translationY(0)
                .setDuration(mDuration)
                .setUpdateListener(mTranslationAnimUpdata)
                .setListener(mTranslationAnim);
    }

    public void setCloseAnim(View view, onCloseFinishedListener listener) {
        ViewCompat.setTranslationY(view, 0);
        mTranslationAnim.setListener(listener);
        mTranslationAnimUpdata.setListener(listener);
        ViewCompat.animate(view)
                .setInterpolator(new LinearInterpolator())
                .translationY(mTranslationY)
                .setDuration(mDuration)
                .setUpdateListener(mTranslationAnimUpdata)
                .setListener(mTranslationAnim);
    }

    private static class TranslationAnim implements ViewPropertyAnimatorListener {

        private View mWindowView;
        private TranslationAnimUpdata mAnimUpdata;
        private onCloseFinishedListener mListener;
        private float mStart;
        private float mEnd;

        private TranslationAnim() {

        }

        private void setParame(View view, float start, float end, TranslationAnimUpdata animUpdata) {
            this.mWindowView = view;
            this.mAnimUpdata = animUpdata;
            this.mStart = start;
            this.mEnd = end;
        }

        private void clear() {
            this.mListener = null;
            this.mWindowView = null;
            this.mStart = 0f;
            this.mEnd = 0f;
        }

        private void setListener(onCloseFinishedListener listener) {
            this.mListener = listener;
        }

        @Override
        public void onAnimationStart(View view) {
            if (mWindowView != null) {
                mWindowView.setAlpha(mStart);
            }
        }

        @Override
        public void onAnimationEnd(View view) {
            if (mWindowView != null) {
                mWindowView.setAlpha(mEnd);
            }
            if (mListener != null) {
                mListener.onFinished();
            }
            clear();
            mAnimUpdata.clear();
        }

        @Override
        public void onAnimationCancel(View view) {
            clear();
            mAnimUpdata.clear();
        }
    }

    private static class TranslationAnimUpdata implements ViewPropertyAnimatorUpdateListener {

        private View mWindowView;
        private onCloseFinishedListener mListener;
        private float mStart;
        private float mEnd;
        private int mTranslationY;

        private TranslationAnimUpdata() {

        }

        private void setParame(View view, float start, float end, int translationY) {
            this.mWindowView = view;
            this.mStart = start;
            this.mEnd = end;
            this.mTranslationY = translationY;
        }

        private void clear() {
            this.mListener = null;
            this.mWindowView = null;
            this.mStart = 0f;
            this.mEnd = 0f;
            this.mTranslationY = 0;
        }

        private void setListener(onCloseFinishedListener listener) {
            this.mListener = listener;
        }

        @Override
        public void onAnimationUpdate(final View view) {
            if (mWindowView != null) {
                int translationY = (int) view.getTranslationY();
                LogUtils.d(TAG, translationY + "");
                if (mListener != null) {
                    float ratio = ((float) Math.abs(mTranslationY) - (float) Math.abs(translationY)) / (float) Math.abs(mTranslationY);
                    mWindowView.setAlpha((mStart - mEnd) * ratio + mEnd);
                    LogUtils.d(TAG, (mEnd - mStart) * ratio + mStart + "");
                } else {
                    float ratio = ((float) Math.abs(mTranslationY) - (float) Math.abs(translationY)) / (float) Math.abs(mTranslationY);
                    mWindowView.setAlpha((mEnd - mStart) * ratio + mStart);
                }
            }
        }
    }

    public interface onCloseFinishedListener {
        void onFinished();
    }
}
