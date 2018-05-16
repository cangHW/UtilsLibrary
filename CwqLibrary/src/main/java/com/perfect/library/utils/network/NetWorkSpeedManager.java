package com.perfect.library.utils.network;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Canghaixiao.
 * Time : 2017/11/17 18:07.
 * Function :网速监测管理类
 */
public class NetWorkSpeedManager {

    public interface onNetWorkSpeedChangedListener {
        void onNetWorkSpeedChanged(String netWorkSpeed);
        void onCancel();
    }

    private static final String TAG = "NetWorkSpeedManager";

    private static NetWorkSpeedManager mManager;
    private long mPreRxBytes = 0;
    private Timer mTimer = null;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private onNetWorkSpeedChangedListener mNetWorkSpeedChangedListener;

    private NetWorkSpeedManager() {
    }

    public static NetWorkSpeedManager create() {
        if (mManager == null) {
            mManager = new NetWorkSpeedManager();
        }
        return mManager;
    }

    public NetWorkSpeedManager setOnNetWorkSpeedChangedListener(onNetWorkSpeedChangedListener netWorkSpeedChangedListener) {
        this.mNetWorkSpeedChangedListener = netWorkSpeedChangedListener;
        return this;
    }

    public void start() {
        if (mTimer == null) {
            mPreRxBytes = getNetworkRxBytes();
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String netSpeed = getNetSpeed();
                            if (mNetWorkSpeedChangedListener != null) {
                                mNetWorkSpeedChangedListener.onNetWorkSpeedChanged(netSpeed);
                            }
                        }
                    });
                }
            }, 1000, 1000);
        }
    }

    public void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mNetWorkSpeedChangedListener!=null){
            mNetWorkSpeedChangedListener.onCancel();
        }
        mNetWorkSpeedChangedListener=null;
    }

    /**
     * 获取当前下载流量总和
     */
    private static long getNetworkRxBytes() {
        return TrafficStats.getTotalRxBytes();
    }

    /**
     * 获取当前上传流量总和
     */
    private static long getNetworkTxBytes() {
        return TrafficStats.getTotalTxBytes();
    }

    /**
     * 获取当前网速
     */
    private String getNetSpeed() {
        long curRxBytes = getNetworkRxBytes();
        if (mPreRxBytes == 0)
            mPreRxBytes = curRxBytes;
        long bytes = curRxBytes - mPreRxBytes;
        mPreRxBytes = curRxBytes;
        double kb = (double) bytes / (double) 1024;
        BigDecimal bd = new BigDecimal(kb);
        return bd.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
    }
}
