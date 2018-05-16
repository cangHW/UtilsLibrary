package com.perfect.library.utils.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.perfect.library.utils.LogUtils;

/**
 * Created by Canghaixiao.
 * Time : 2017/6/9 16:46.
 * Function :音频播放管理类
 */
public class MediaPlayerManager {

    public interface MediaPlayerCallBack {
        void onStart(MediaPlayerManager mpu, int duration);

        void onProgress(MediaPlayerManager mu, int progress);

        void onCompletion(MediaPlayerManager mu);

        void onError(MediaPlayerManager mu);
    }

    private interface MediaPlayerBufferUpdateCallBack {
        void onUpdate(int percent);
    }

    private static final String TAG = "media_player";
    private static final long mDelayMillis = 1000;

    private MediaPlayer mPlayer;
    private MyHandle mHandle;
    private int mBufferProgress=-1;
    private boolean isPlaying = false;
    private boolean isPause = false;
    private boolean isPrepared=false;
    private MediaPlayerCallBack mCallBack;
    private MediaPlayerBufferUpdateCallBack mUpdateCallBack;

    public static MediaPlayerManager create() {
        return new MediaPlayerManager();
    }

    public void start(String url) {
        if (isPlaying|| TextUtils.isEmpty(url)) {
            return;
        }
        isPrepared=false;
        LogUtils.d(TAG, "start");
        mPlayer = new MediaPlayer();
        mHandle = new MyHandle(MediaPlayerManager.this);
        try {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(url);
            mPlayer.setOnPreparedListener(preparedListener);
            mPlayer.setOnSeekCompleteListener(seekCompleteListener);
            mPlayer.setOnCompletionListener(completionListener);
            mPlayer.setOnErrorListener(errorListener);
            mPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
            mPlayer.prepareAsync();
        } catch (Exception e) {
            LogUtils.d(TAG, e.toString());
            e.printStackTrace();
        }
    }

    private static class MyHandle extends Handler {

        private MediaPlayerManager mPlayerUtil;

        MyHandle(MediaPlayerManager playerUtil){
            this.mPlayerUtil=playerUtil;
        }

        void setClear(){
            mPlayerUtil=null;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogUtils.d(TAG, "media  handle");
            if (mPlayerUtil!=null&&mPlayerUtil.isPlaying()) {
                if (mPlayerUtil!=null&&mPlayerUtil.getCallBack() != null) {
                    int currentPosition = mPlayerUtil.getPlayer().getCurrentPosition();
                    mPlayerUtil.getCallBack().onProgress(mPlayerUtil, currentPosition);
                }
            }
            if (mPlayerUtil!=null&&mPlayerUtil.getHandle() != null) {
                mPlayerUtil.getHandle().sendEmptyMessageDelayed(0, mDelayMillis);
            }
        }
    }

    private MyHandle getHandle(){
        return mHandle;
    }

    private MediaPlayer getPlayer(){
        return mPlayer;
    }

    private MediaPlayerCallBack getCallBack(){
        return mCallBack;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * move
     */
    public void seekTo(final int msec) {
        if (mPlayer != null&&isPrepared) {
            try {
                isPlaying = false;
                mPlayer.pause();
                final int seekProgress = (int) ((msec * 1f / (mPlayer.getDuration() * 1f)) * 100) + 1;
                if (mBufferProgress == 100 || seekProgress < mBufferProgress) {
                    mPlayer.seekTo(msec);
                } else {
                    mUpdateCallBack = new MediaPlayerBufferUpdateCallBack() {
                        @Override
                        public void onUpdate(int percent) {
                            LogUtils.d(TAG, "onupdate");
                            if (mBufferProgress != 100 && seekProgress < mBufferProgress) {
                                LogUtils.d(TAG, "seek");
                                mPlayer.seekTo(msec);
                                mUpdateCallBack = null;
                            } else if (mBufferProgress == 100) {
                                mPlayer.seekTo(msec);
                                mUpdateCallBack = null;
                            }
                        }
                    };
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        if (mPlayer != null&&isPrepared) {
            try {
                isPlaying = false;
                isPause = true;
                mPlayer.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        if (mPlayer != null&&isPrepared) {
            try {
                isPlaying = true;
                isPause = false;
                mPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void finish() {
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mHandle!=null) {
            mHandle.setClear();
            mHandle = null;
        }
    }

    public void playFinish(){
        isPlaying=false;
        finish();
    }

    /**
     * Destruction of data
     */
    public void destory() {
        LogUtils.d(TAG,"destory");
        finish();
        this.isPlaying=false;
        this.mCallBack = null;
        this.isPause = false;
        this.mUpdateCallBack = null;
    }

    public void addMediaPlayerCallBack(MediaPlayerCallBack callBack) {
        mCallBack = callBack;
    }

    private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            LogUtils.d(TAG, "media  prepared");
            if (mp != null) {
                isPrepared=true;
                isPlaying = true;
                int duration = mp.getDuration();
                mp.start();
                if (mCallBack != null) {
                    mCallBack.onStart(MediaPlayerManager.this, duration);
                }
                if (mHandle != null) {
                    mHandle.sendEmptyMessageDelayed(0, mDelayMillis);
                }
            }
        }
    };

    private MediaPlayer.OnSeekCompleteListener seekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            LogUtils.d(TAG, "media  seek completion");
            if (mp != null) {
                if (!isPause) {
                    isPlaying = true;
                    mp.start();
                }
            }
        }
    };

    private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            LogUtils.d(TAG, "media  buffering update" + percent);
            mBufferProgress = percent;
            if (mUpdateCallBack != null) {
                mUpdateCallBack.onUpdate(percent);
            }
        }
    };

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            LogUtils.d(TAG, "media  completion");
            finish();
            mBufferProgress = 0;
            if (mCallBack != null && isPlaying) {
                isPlaying = false;
                mCallBack.onCompletion(MediaPlayerManager.this);
            }
        }
    };

    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            LogUtils.d(TAG, "media  error");
            finish();
            mBufferProgress = 0;
            if (mCallBack != null) {
                isPlaying = false;
                mCallBack.onError(MediaPlayerManager.this);
            }
            return true;
        }
    };
}
