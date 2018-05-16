package com.perfect.library.view.camera;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Canghaixiao.
 * Time : 2017/6/20 16:33.
 * Function :
 */
public class CameraView extends FrameLayout {

    private static final String TYPE_DEFAULT="api_2";
    private static final String TYPE_M="api_23";

    private CameraSurfaceView mSurfaceView;
    private CameraTextureView mTextureView;
    private onTakePictureListener mTakePictureListener;

    private String TYPE=TYPE_DEFAULT;

    public interface onTakePictureListener {
        void onComplete(boolean isSuccess, String path);
    }

    interface onTakePictureFinishedListener{
        void onFinish(String path);
    }

    public CameraView(@NonNull Context context) {
        this(context, null);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TYPE=TYPE_M;
            mTextureView=new CameraTextureView(context);
            this.addView(mTextureView);
        }else {
            TYPE=TYPE_DEFAULT;
            mSurfaceView = new CameraSurfaceView(context);
            this.addView(mSurfaceView);
        }
    }

    public void onResume() {
        if (TYPE.equals(TYPE_DEFAULT)) {
            mSurfaceView.onResume();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTextureView.onResume();
            }
        }
    }

    public void onStop() {
        if (TYPE.equals(TYPE_DEFAULT)) {
            mSurfaceView.onStop();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTextureView.onStop();
            }
        }
    }

    public void changeDirection(){
        if (TYPE.equals(TYPE_DEFAULT)) {
            mSurfaceView.changeDirection();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTextureView.changeDirection();
            }
        }
    }

    public void takePicture(@Nullable String path,@Nullable onTakePictureListener onTakePictureListener) {
        this.mTakePictureListener=onTakePictureListener;
        if (TYPE.equals(TYPE_DEFAULT)) {
            mSurfaceView.takePicture(path,takePictureFinishedListener);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTextureView.takePicture(path,takePictureFinishedListener);
            }
        }
    }

    private onTakePictureFinishedListener takePictureFinishedListener=new onTakePictureFinishedListener() {
        @Override
        public void onFinish(String path) {
            if (mTakePictureListener!=null) {
                if (path == null) {
                    mTakePictureListener.onComplete(false, null);
                } else {
                    mTakePictureListener.onComplete(true, path);
                }
            }
        }
    };

}


