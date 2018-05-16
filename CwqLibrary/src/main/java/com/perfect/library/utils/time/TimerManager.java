package com.perfect.library.utils.time;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Canghaixiao.
 * Time : 2017/11/30 17:24.
 * Function : 计时管理类
 */
public class TimerManager {

    public interface onTimeProgressListener {
        /**
         * 单位毫秒级
         */
        void onProgress(TimerManager manager, long progress);

        void onCancel();
    }

    private Timer mTimer;
    private long mTime;
    private onTimeProgressListener mProgressListener;

    private TimerManager() {
    }

    public static TimerManager create() {
        return new TimerManager();
    }

    public void setProgressListener(onTimeProgressListener progressListener) {
        this.mProgressListener = progressListener;
    }

    public void start() {
        if (mTimer == null) {
            mTime = System.currentTimeMillis();
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    long time = System.currentTimeMillis() - mTime;
                    if (mProgressListener != null) {
                        mProgressListener.onProgress(TimerManager.this, time);
                    }
                }
            }, 0, 1000);
        }
    }

    public void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mProgressListener != null) {
            mProgressListener.onCancel();
            mProgressListener = null;
        }
    }

}
