package com.perfect.library.utils.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.perfect.library.utils.audio.messageBean.SoundMessage;

import java.util.List;

/**
 * Created by Canghaixiao.
 * Time : 2017/8/17 11:47.
 * Function :短音频池管理类
 */
public class SoundManager {

    private static int MAX_MUSIC_NUM = 10;
    private static int NOW_MUSIC_NUM = 2;

    private static SoundPool mSoundPool;
    private static List<SoundMessage> mData;
    private static int mLoadNum = 0;
    private static boolean isLoadComplete = false;
    private static boolean isCanPlay = false;

    private static void init() {
        mSoundPool = new SoundPool(MAX_MUSIC_NUM, AudioManager.STREAM_SYSTEM, 0);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mLoadNum++;
                if (mLoadNum == NOW_MUSIC_NUM) {
                    isLoadComplete = true;
                }
            }
        });
    }

    public static void setMaxMusicNum(int maxMusicNum) {
        MAX_MUSIC_NUM = maxMusicNum;
    }

    public static void initialize(Context context, List<SoundMessage> list) {
        mData = list;
        NOW_MUSIC_NUM = list.size();
        init();
        for (SoundMessage message : list) {
            message.soundID = mSoundPool.load(context, message.resId, 1);
        }
    }

    public static void setCanPlay(boolean b) {
        isCanPlay = b;
    }

    public static void play(String tag) {
        if (mSoundPool != null && isLoadComplete && isCanPlay) {
            for (SoundMessage message : mData) {
                if (message.tag.equals(tag)) {
                    mSoundPool.play(message.soundID, 1, 1, 0, 0, 1);
                }
            }
        }
    }
}
