package com.perfect.library.utils.time;

import android.os.CountDownTimer;

import java.lang.ref.WeakReference;

/**
 * Created by Canghaixiao.
 * Time : 2017/5/11 17:02.
 * Function :倒计时管理类
 */

@SuppressWarnings("unused")
public class CountDownTimeManager {

    public interface ScheduleListener {
        void onProgress(long progress);

        void onFinish();
    }

    private static final long DEFAULT_TOTALTIME = 45000;
    private static final long DEFAULT_INTERVATIME = 1000;

    /**
     * the countdown total time
     */
    private long totalTime = DEFAULT_TOTALTIME;
    /**
     * the countdown interval time
     */
    private long intervalTime = DEFAULT_INTERVATIME;
    private ScheduleListener mListener;
    private Timers mTimers;
    private static CountDownTimeManager mManager;

    public static CountDownTimeManager create() {
        if (mManager == null) {
            mManager = new CountDownTimeManager();
        }
        return mManager;
    }

    /**
     * finished callback
     */
    public void setFinished() {
        this.mListener = null;
        if (mTimers != null) {
            this.mTimers.cancel();
        }
    }

    /**
     * the totalTime in milliseconds
     */
    public CountDownTimeManager setTotalTime(long totalTime) {
        this.totalTime = totalTime;
        return this;
    }

    /**
     * the intervalTime in milliseconds
     */
    public CountDownTimeManager setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
        return this;
    }

    public CountDownTimeManager setScheduleListener(ScheduleListener listener) {
        this.mListener = listener;
        return this;
    }

    public void start() {
        mTimers = new Timers(totalTime, intervalTime, mListener);
        mTimers.start();
    }

    public void cancel() {
        try {
            mTimers.clear();
            mTimers.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Timers extends CountDownTimer {

        private WeakReference<ScheduleListener> mReference;

        Timers(long millisInFuture, long countDownInterval, ScheduleListener listener) {
            super(millisInFuture, countDownInterval);
            if (listener != null) {
                mReference = new WeakReference<>(listener);
            }
        }

        private void clear() {
            mReference = null;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (mReference != null) {
                ScheduleListener listener = mReference.get();
                if (listener != null) {
                    listener.onProgress(millisUntilFinished);
                }
            }
        }

        @Override
        public void onFinish() {
            if (mReference != null) {
                ScheduleListener listener = mReference.get();
                if (listener != null) {
                    listener.onFinish();
                }
            }
        }
    }
}

