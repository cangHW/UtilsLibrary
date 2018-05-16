package com.perfect.library.view.crop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.perfect.library.utils.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Canghaixiao.
 * Time : 2017/6/23 16:05.
 * Function :
 */
public class CropImageView extends android.support.v7.widget.AppCompatImageView {

    public interface onCropFinishedListener {
        void onFinished(String path);
    }

    private static final int DEFAULT_LINE_STROKEWIDTH = 2;
    private static final int DEFAULT_SHARP_STROKEWIDTH = 4;
    private static final int DEFAULT_SHARP_LENG = 44;
    private static final int DEFAULT_PADDING = DEFAULT_LINE_STROKEWIDTH + DEFAULT_SHARP_STROKEWIDTH;
    /**
     * 正比
     */
    private static final int TYPE_SCALE_W_H = DEFAULT_PADDING * 5;
    /**
     * 反比
     */
    private static final int TYPE_MOVE_W_H = DEFAULT_PADDING * 3;

    private static final String DEFAULT_BACKGROUNDCOLOR = "#4d000000";
    private static final int DEFAULT_LINECOLOR = Color.parseColor("#89e00d");

    private static final String TYPE_DEFAULT = "default";
    private static final String TYPE_SCALE = "scale";
    private static final String TYPE_MOVE = "move";

    private static final String PRESS_DEFAULT = "default";
    private static final String PRESS_LINE_LEFT = "left";
    private static final String PRESS_LINE_TOP = "top";
    private static final String PRESS_LINE_RIGHT = "right";
    private static final String PRESS_LINE_BOTTOM = "bottom";
    private static final String PRESS_ANGLE_L_T = "left_top";
    private static final String PRESS_ANGLE_R_T = "right_top";
    private static final String PRESS_ANGLE_L_B = "left_bottom";
    private static final String PRESS_ANGLE_R_B = "right_bottom";

    private int mWidth;
    private int mHeight;

    private boolean isShowCropBox = false;
    private Paint mCropPaint;
    private Paint mConverPaint;
    private PorterDuffXfermode mXfermode;
    private String mType = TYPE_DEFAULT;
    private String mPressSite = PRESS_DEFAULT;
    private int mEvent_X;
    private int mEvent_Y;

    private Rect mCropRect;
    private Rect mTypeScaleRect;
    private Rect mTypeMoveRect;
    private CropDrawable drawable;

    private String mPicturePath;

    public CropImageView(Context context) {
        super(context);
        init();
    }

    public CropImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaint();
        setPadding(0, 0, 0, 0);
    }

    private void initPaint() {
        isShowCropBox = false;

        mCropPaint = new Paint();
        mCropPaint.setAntiAlias(true);
        mCropPaint.setDither(true);
        mCropPaint.setStrokeWidth(DEFAULT_LINE_STROKEWIDTH);
        mCropPaint.setStyle(Paint.Style.FILL);
        mCropPaint.setColor(DEFAULT_LINECOLOR);

        mConverPaint = new Paint();
        mConverPaint.setStyle(Paint.Style.FILL);
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        isShowCropBox = true;
        drawable = new CropDrawable(bm, TYPE_SCALE_W_H, new CropDrawable.onBoundsFinishedListener() {
            @Override
            public void onBoundsFisished(int width, int height) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                if (params.width != width || params.height != height) {
                    params.width = width;
                    params.height = height;
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    setLayoutParams(params);
                }
            }
        });
        setBackgroundDrawable(drawable);
    }

    public void startCrop(String path, onCropFinishedListener cropFinishedListener) {
        try {
            mPicturePath = path;
            SaveBitmapTask saveBitmapTask = new SaveBitmapTask(mCropRect, cropFinishedListener);
            saveBitmapTask.execute(drawable.getBitmap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReset() {
        mCropRect = new Rect(TYPE_SCALE_W_H, TYPE_SCALE_W_H, mWidth - TYPE_SCALE_W_H, mHeight - TYPE_SCALE_W_H);
        mTypeScaleRect = new Rect(mCropRect.left - TYPE_SCALE_W_H, mCropRect.top - TYPE_SCALE_W_H, mCropRect.right + TYPE_SCALE_W_H, mCropRect.bottom + TYPE_SCALE_W_H);
        mTypeMoveRect = new Rect(mCropRect.left + TYPE_MOVE_W_H, mCropRect.top + TYPE_MOVE_W_H, mCropRect.right - TYPE_MOVE_W_H, mCropRect.bottom - TYPE_MOVE_W_H);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        mCropRect = new Rect(TYPE_SCALE_W_H, TYPE_SCALE_W_H, mWidth - TYPE_SCALE_W_H, mHeight - TYPE_SCALE_W_H);
        mTypeScaleRect = new Rect(mCropRect.left - TYPE_SCALE_W_H, mCropRect.top - TYPE_SCALE_W_H, mCropRect.right + TYPE_SCALE_W_H, mCropRect.bottom + TYPE_SCALE_W_H);
        mTypeMoveRect = new Rect(mCropRect.left + TYPE_MOVE_W_H, mCropRect.top + TYPE_MOVE_W_H, mCropRect.right - TYPE_MOVE_W_H, mCropRect.bottom - TYPE_MOVE_W_H);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isShowCropBox) {
            int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
            mConverPaint.setColor(Color.parseColor(DEFAULT_BACKGROUNDCOLOR));
            canvas.drawRect(TYPE_SCALE_W_H, TYPE_SCALE_W_H, mWidth - TYPE_SCALE_W_H, mHeight - TYPE_SCALE_W_H, mConverPaint);
            mConverPaint.setXfermode(mXfermode);
            mConverPaint.setColor(Color.TRANSPARENT);
            canvas.drawRect(mCropRect, mConverPaint);
            mConverPaint.setXfermode(null);
            canvas.restoreToCount(layerId);
            drawLines(canvas, mCropRect);
            drawSharp(canvas, mCropRect);
        }
    }

    private void drawLines(Canvas canvas, final Rect rect) {
        if (rect == null) {
            return;
        }
        int centerToBitmap = DEFAULT_LINE_STROKEWIDTH / 2;
        int sideToBitmap = DEFAULT_LINE_STROKEWIDTH;

        mCropPaint.setStrokeWidth(DEFAULT_LINE_STROKEWIDTH);
        canvas.drawLine(rect.left - sideToBitmap, rect.top - centerToBitmap, rect.right + sideToBitmap, rect.top - centerToBitmap, mCropPaint);
        canvas.drawLine(rect.left, rect.height() / 3 + rect.top, rect.right, rect.height() / 3 + rect.top, mCropPaint);
        canvas.drawLine(rect.left, rect.height() * 2 / 3 + rect.top, rect.right, rect.height() * 2 / 3 + rect.top, mCropPaint);
        canvas.drawLine(rect.left - sideToBitmap, rect.bottom + centerToBitmap, rect.right + sideToBitmap, rect.bottom + centerToBitmap, mCropPaint);

        canvas.drawLine(rect.left - centerToBitmap, rect.top - sideToBitmap, rect.left - centerToBitmap, rect.bottom + sideToBitmap, mCropPaint);
        canvas.drawLine(rect.width() / 3 + rect.left, rect.top, rect.width() / 3 + rect.left, rect.bottom, mCropPaint);
        canvas.drawLine(rect.width() * 2 / 3 + rect.left, rect.top, rect.width() * 2 / 3 + rect.left, rect.bottom, mCropPaint);
        canvas.drawLine(rect.right + centerToBitmap, rect.top - sideToBitmap, rect.right + centerToBitmap, rect.bottom + sideToBitmap, mCropPaint);
    }

    private void drawSharp(Canvas canvas, final Rect rect) {
        if (rect == null) {
            return;
        }
        int centerToBitmap = DEFAULT_LINE_STROKEWIDTH + DEFAULT_SHARP_STROKEWIDTH / 2;
        int sideToBitmap = DEFAULT_LINE_STROKEWIDTH + DEFAULT_SHARP_STROKEWIDTH;

        mCropPaint.setStrokeWidth(DEFAULT_SHARP_STROKEWIDTH);
        canvas.drawLine(rect.left - sideToBitmap, rect.top - centerToBitmap, rect.left - sideToBitmap + DEFAULT_SHARP_LENG, rect.top - centerToBitmap, mCropPaint);
        canvas.drawLine(rect.right + sideToBitmap - DEFAULT_SHARP_LENG, rect.top - centerToBitmap, rect.right + sideToBitmap, rect.top - centerToBitmap, mCropPaint);
        canvas.drawLine(rect.left - sideToBitmap, rect.bottom + centerToBitmap, rect.left - sideToBitmap + DEFAULT_SHARP_LENG, rect.bottom + centerToBitmap, mCropPaint);
        canvas.drawLine(rect.right + sideToBitmap - DEFAULT_SHARP_LENG, rect.bottom + centerToBitmap, rect.right + sideToBitmap, rect.bottom + centerToBitmap, mCropPaint);

        canvas.drawLine(rect.left - centerToBitmap, rect.top - sideToBitmap, rect.left - centerToBitmap, rect.top - sideToBitmap + DEFAULT_SHARP_LENG, mCropPaint);
        canvas.drawLine(rect.left - centerToBitmap, rect.bottom + sideToBitmap - DEFAULT_SHARP_LENG, rect.left - centerToBitmap, rect.bottom + sideToBitmap, mCropPaint);
        canvas.drawLine(rect.right + centerToBitmap, rect.top - sideToBitmap, rect.right + centerToBitmap, rect.top - sideToBitmap + DEFAULT_SHARP_LENG, mCropPaint);
        canvas.drawLine(rect.right + centerToBitmap, rect.bottom + sideToBitmap - DEFAULT_SHARP_LENG, rect.right + centerToBitmap, rect.bottom + sideToBitmap, mCropPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mEvent_X = (int) event.getX();
                mEvent_Y = (int) event.getY();
                if (mTypeScaleRect != null && mTypeMoveRect != null && mTypeScaleRect.contains(mEvent_X, mEvent_Y)) {
                    if (mTypeMoveRect.contains(mEvent_X, mEvent_Y)) {
                        mType = TYPE_MOVE;
                    } else {
                        mType = TYPE_SCALE;
                        if (mEvent_X < mTypeMoveRect.left && (mEvent_Y < mTypeScaleRect.bottom - DEFAULT_SHARP_LENG && mEvent_Y > mTypeScaleRect.top + DEFAULT_SHARP_LENG)) {
                            mPressSite = PRESS_LINE_LEFT;
                        } else if (mEvent_X > mTypeMoveRect.right && (mEvent_Y < mTypeScaleRect.bottom - DEFAULT_SHARP_LENG && mEvent_Y > mTypeScaleRect.top + DEFAULT_SHARP_LENG)) {
                            mPressSite = PRESS_LINE_RIGHT;
                        } else if (mEvent_Y < mTypeMoveRect.top && (mEvent_X < mTypeScaleRect.right - DEFAULT_SHARP_LENG && mEvent_X > mTypeScaleRect.left + DEFAULT_SHARP_LENG)) {
                            mPressSite = PRESS_LINE_TOP;
                        } else if (mEvent_Y > mTypeMoveRect.bottom && (mEvent_X < mTypeScaleRect.right - DEFAULT_SHARP_LENG && mEvent_X > mTypeScaleRect.left + DEFAULT_SHARP_LENG)) {
                            mPressSite = PRESS_LINE_BOTTOM;
                        } else if (mEvent_X < mTypeMoveRect.left && mEvent_Y >= mTypeScaleRect.bottom - DEFAULT_SHARP_LENG) {
                            mPressSite = PRESS_ANGLE_L_B;
                        } else if (mEvent_X < mTypeMoveRect.left && mEvent_Y <= mTypeScaleRect.top + DEFAULT_SHARP_LENG) {
                            mPressSite = PRESS_ANGLE_L_T;
                        } else if (mEvent_X > mTypeMoveRect.right && mEvent_Y >= mTypeScaleRect.bottom - DEFAULT_SHARP_LENG) {
                            mPressSite = PRESS_ANGLE_R_B;
                        } else if (mEvent_X > mTypeMoveRect.right && (mEvent_Y <= mTypeScaleRect.top + DEFAULT_SHARP_LENG)) {
                            mPressSite = PRESS_ANGLE_R_T;
                        } else if (mEvent_Y < mTypeMoveRect.top && (mEvent_X > mTypeScaleRect.left + DEFAULT_PADDING && mEvent_X <= mTypeScaleRect.left + DEFAULT_SHARP_LENG)) {
                            mPressSite = PRESS_ANGLE_L_T;
                        } else if (mEvent_Y < mTypeMoveRect.top && (mEvent_X >= mTypeScaleRect.right - DEFAULT_SHARP_LENG && mEvent_X < mTypeScaleRect.right - DEFAULT_PADDING)) {
                            mPressSite = PRESS_ANGLE_R_T;
                        } else if (mEvent_Y > mTypeMoveRect.bottom && (mEvent_X > mTypeScaleRect.left + DEFAULT_PADDING && mEvent_X <= mTypeScaleRect.left + DEFAULT_SHARP_LENG)) {
                            mPressSite = PRESS_ANGLE_L_B;
                        } else if (mEvent_Y > mTypeMoveRect.bottom && (mEvent_X >= mTypeScaleRect.right - DEFAULT_SHARP_LENG && mEvent_X < mTypeScaleRect.right - DEFAULT_PADDING)) {
                            mPressSite = PRESS_ANGLE_R_B;
                        } else {
                            mPressSite = PRESS_DEFAULT;
                        }
                    }
                } else {
                    mType = TYPE_DEFAULT;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int move_X = (int) event.getX();
                int move_Y = (int) event.getY();

                int change_X = move_X - mEvent_X;
                int change_Y = move_Y - mEvent_Y;

                mEvent_X = move_X;
                mEvent_Y = move_Y;
                if (mType.equals(TYPE_SCALE)) {
                    if (!mPressSite.equals(PRESS_DEFAULT)) {
                        setScaleRect(change_X, change_Y, mPressSite);
                    }
                } else if (mType.equals(TYPE_MOVE)) {
                    setMoveRect(change_X, change_Y);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mEvent_X = 0;
                mEvent_Y = 0;
                mType = TYPE_DEFAULT;
                mPressSite = PRESS_DEFAULT;
                break;
        }
        return true;
    }

    private void setScaleRect(final int changeX, final int changeY, final String pressSite) {
        int start_left = mCropRect.left;
        int start_top = mCropRect.top;
        int start_right = mCropRect.right;
        int start_bottom = mCropRect.bottom;

        int end_left = 0;
        int end_top = 0;
        int end_right = 0;
        int end_bottom = 0;

        switch (pressSite) {
            case PRESS_DEFAULT:
                end_top = start_top;
                end_bottom = start_bottom;
                end_left = start_left;
                end_right = start_right;
                break;
            case PRESS_LINE_LEFT:
                end_top = start_top;
                end_bottom = start_bottom;
                end_right = start_right;
                end_left = start_left + changeX;
                if (end_left < TYPE_SCALE_W_H) {
                    end_left = TYPE_SCALE_W_H;
                }
                if (end_right - end_left < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_right = end_left + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                if (end_right > mWidth - TYPE_SCALE_W_H) {
                    end_right = mWidth - TYPE_SCALE_W_H;
                    end_left = end_right - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                break;
            case PRESS_LINE_TOP:
                end_left = start_left;
                end_right = start_right;
                end_bottom = start_bottom;
                end_top = start_top + changeY;
                if (end_top < TYPE_SCALE_W_H) {
                    end_top = TYPE_SCALE_W_H;
                }
                if (end_bottom - end_top < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_bottom = end_top + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                if (end_bottom > mHeight - TYPE_SCALE_W_H) {
                    end_bottom = mHeight - TYPE_SCALE_W_H;
                    end_top = end_bottom - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                break;
            case PRESS_LINE_RIGHT:
                end_top = start_top;
                end_bottom = start_bottom;
                end_left = start_left;
                end_right = start_right + changeX;
                if (end_right > mWidth - TYPE_SCALE_W_H) {
                    end_right = mWidth - TYPE_SCALE_W_H;
                }
                if (end_right - end_left < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_left = end_right - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                if (end_left < TYPE_SCALE_W_H) {
                    end_left = TYPE_SCALE_W_H;
                    end_right = end_left + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                break;
            case PRESS_LINE_BOTTOM:
                end_left = start_left;
                end_right = start_right;
                end_top = start_top;
                end_bottom = start_bottom + changeY;
                if (end_bottom > mHeight - TYPE_SCALE_W_H) {
                    end_bottom = mHeight - TYPE_SCALE_W_H;
                }
                if (end_bottom - end_top < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_top = end_bottom - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                if (end_top < TYPE_SCALE_W_H) {
                    end_top = TYPE_SCALE_W_H;
                    end_bottom = end_top + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                break;
            case PRESS_ANGLE_L_T:
                end_right = start_right;
                end_bottom = start_bottom;
                end_left = start_left + changeX;
                end_top = start_top + changeY;
                if (end_left < TYPE_SCALE_W_H) {
                    end_left = TYPE_SCALE_W_H;
                }
                if (end_right - end_left < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_right = end_left + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                if (end_right > mWidth - TYPE_SCALE_W_H) {
                    end_right = mWidth - TYPE_SCALE_W_H;
                    end_left = end_right - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                if (end_top < TYPE_SCALE_W_H) {
                    end_top = TYPE_SCALE_W_H;
                }
                if (end_bottom - end_top < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_bottom = end_top + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                if (end_bottom > mHeight - TYPE_SCALE_W_H) {
                    end_bottom = mHeight - TYPE_SCALE_W_H;
                    end_top = end_bottom - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                break;
            case PRESS_ANGLE_R_T:
                end_left = start_left;
                end_bottom = start_bottom;
                end_right = start_right + changeX;
                end_top = start_top + changeY;
                if (end_right > mWidth - TYPE_SCALE_W_H) {
                    end_right = mWidth - TYPE_SCALE_W_H;
                }
                if (end_right - end_left < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_left = end_right - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                if (end_left < TYPE_SCALE_W_H) {
                    end_left = TYPE_SCALE_W_H;
                    end_right = end_left + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                if (end_top < TYPE_SCALE_W_H) {
                    end_top = TYPE_SCALE_W_H;
                }
                if (end_bottom - end_top < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_bottom = end_top + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                if (end_bottom > mHeight - TYPE_SCALE_W_H) {
                    end_bottom = mHeight - TYPE_SCALE_W_H;
                    end_top = end_bottom - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                break;
            case PRESS_ANGLE_L_B:
                end_right = start_right;
                end_top = start_top;
                end_left = start_left + changeX;
                end_bottom = start_bottom + changeY;
                if (end_left < TYPE_SCALE_W_H) {
                    end_left = TYPE_SCALE_W_H;
                }
                if (end_right - end_left < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_right = end_left + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                if (end_right > mWidth - TYPE_SCALE_W_H) {
                    end_right = mWidth - TYPE_SCALE_W_H;
                    end_left = end_right - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                if (end_bottom > mHeight - TYPE_SCALE_W_H) {
                    end_bottom = mHeight - TYPE_SCALE_W_H;
                }
                if (end_bottom - end_top < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_top = end_bottom - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                if (end_top < TYPE_SCALE_W_H) {
                    end_top = TYPE_SCALE_W_H;
                    end_bottom = end_top + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                break;
            case PRESS_ANGLE_R_B:
                end_left = start_left;
                end_top = start_top;
                end_right = start_right + changeX;
                end_bottom = start_bottom + changeY;
                if (end_right > mWidth - TYPE_SCALE_W_H) {
                    end_right = mWidth - TYPE_SCALE_W_H;
                }
                if (end_right - end_left < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_left = end_right - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                if (end_left < TYPE_SCALE_W_H) {
                    end_left = TYPE_SCALE_W_H;
                    end_right = end_left + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                if (end_bottom > mHeight - TYPE_SCALE_W_H) {
                    end_bottom = mHeight - TYPE_SCALE_W_H;
                }
                if (end_bottom - end_top < DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2) {
                    end_top = end_bottom - (DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2);
                }
                if (end_top < TYPE_SCALE_W_H) {
                    end_top = TYPE_SCALE_W_H;
                    end_bottom = end_top + DEFAULT_SHARP_LENG * 2 - DEFAULT_PADDING * 2;
                }
                break;
        }
        mCropRect = new Rect(end_left, end_top, end_right, end_bottom);
        mTypeScaleRect = new Rect(mCropRect.left - TYPE_SCALE_W_H, mCropRect.top - TYPE_SCALE_W_H, mCropRect.right + TYPE_SCALE_W_H, mCropRect.bottom + TYPE_SCALE_W_H);
        mTypeMoveRect = new Rect(mCropRect.left + TYPE_MOVE_W_H, mCropRect.top + TYPE_MOVE_W_H, mCropRect.right - TYPE_MOVE_W_H, mCropRect.bottom - TYPE_MOVE_W_H);
    }

    private void setMoveRect(final int changeX, final int changeY) {
        int start_left = mCropRect.left;
        int start_top = mCropRect.top;
        int start_right = mCropRect.right;
        int start_bottom = mCropRect.bottom;

        int width = mCropRect.width();
        int height = mCropRect.height();

        int end_left = start_left + changeX;
        int end_top = start_top + changeY;
        int end_right = start_right + changeX;
        int end_bottom = start_bottom + changeY;

        if (end_left < TYPE_SCALE_W_H) {
            end_left = TYPE_SCALE_W_H;
            end_right = end_left + width;
        }
        if (end_right > mWidth - TYPE_SCALE_W_H) {
            end_right = mWidth - TYPE_SCALE_W_H;
            end_left = end_right - width;
        }
        if (end_top < TYPE_SCALE_W_H) {
            end_top = TYPE_SCALE_W_H;
            end_bottom = end_top + height;
        }
        if (end_bottom > mHeight - TYPE_SCALE_W_H) {
            end_bottom = mHeight - TYPE_SCALE_W_H;
            end_top = end_bottom - height;
        }
        mCropRect = new Rect(end_left, end_top, end_right, end_bottom);
        mTypeScaleRect = new Rect(mCropRect.left - TYPE_SCALE_W_H, mCropRect.top - TYPE_SCALE_W_H, mCropRect.right + TYPE_SCALE_W_H, mCropRect.bottom + TYPE_SCALE_W_H);
        mTypeMoveRect = new Rect(mCropRect.left + TYPE_MOVE_W_H, mCropRect.top + TYPE_MOVE_W_H, mCropRect.right - TYPE_MOVE_W_H, mCropRect.bottom - TYPE_MOVE_W_H);
    }

    @SuppressLint("StaticFieldLeak")
    private class SaveBitmapTask extends AsyncTask<Bitmap, Integer, String> {

        private Rect mRect;
        private onCropFinishedListener mCropFinishedListener;

        SaveBitmapTask(Rect rect, onCropFinishedListener cropFinishedListener) {
            this.mRect = rect;
            this.mCropFinishedListener = cropFinishedListener;
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            Bitmap bitmap = Bitmap.createBitmap(params[0], mRect.left - TYPE_SCALE_W_H, mRect.top - TYPE_SCALE_W_H, mRect.width(), mRect.height());
            String filePath;
            if (TextUtils.isEmpty(mPicturePath)) {
                filePath = FileUtils.getSavePicturePath(System.currentTimeMillis() + ".jpg");
            } else {
                filePath = mPicturePath;
            }
            File file = new File(filePath);
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                filePath = null;
            } finally {
                try {
                    if (bos != null) {
                        bos.flush();
                        bos.close();
                    }
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return filePath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mCropFinishedListener != null) {
                mCropFinishedListener.onFinished(s);
            }
        }
    }

}
