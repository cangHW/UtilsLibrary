package com.perfect.library.view.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import com.perfect.library.utils.FileUtils;
import com.perfect.library.utils.ScreenUtils;
import com.perfect.library.utils.ToastUtils;
import com.perfect.library.utils.bitmap.BitmapUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Canghaixiao.
 * Time : 2017/6/21 14:18.
 * Function :
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private static final String CAMERA_FACING_BACK = "0";
    private static final String CAMERA_FACING_FRONT = "1";

    private String mCameraId = CAMERA_FACING_BACK;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    private Context mContext;
    private int mWidth;
    private int mHeight;
    private ImageReader mImageReader;
    private CameraDevice cameraDevice;
    private CameraCaptureSession mPreviewSession;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CaptureRequest mCaptureRequest;
    private Size mPreviewSize;
    private String mPicturePath;
    private CameraView.onTakePictureFinishedListener mTakePictureFinishedListener;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public CameraTextureView(Context context) {
        super(context);
        initView(context);
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        this.setSurfaceTextureListener(this);
    }

    public void changeDirection() {
        onStop();
        if (CAMERA_FACING_BACK.equals(mCameraId)) {
            mCameraId = CAMERA_FACING_FRONT;
        } else if (CAMERA_FACING_FRONT.equals(mCameraId)) {
            mCameraId = CAMERA_FACING_BACK;
        }
        onResume();
    }

    public void onStop() {
        if (cameraDevice != null) {
            release();
        }
    }

    public void onResume() {
        startCamera();
    }

    public void takePicture(String path,CameraView.onTakePictureFinishedListener mTakePictureFinishedListener) {
        try {
            if (cameraDevice == null) {
                return;
            }
            this.mPicturePath=path;
            this.mTakePictureFinishedListener = mTakePictureFinishedListener;
            mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
            int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
            mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            mPreviewSession.stopRepeating();
            CaptureRequest captureRequest = mCaptureRequestBuilder.build();
            mPreviewSession.capture(captureRequest, captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            try {
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                mPreviewSession.setRepeatingRequest(mCaptureRequest, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    };

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mWidth = width;
        mHeight = height;
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void startCamera() {
        try {
            if (isAvailable()) {
                if (cameraDevice == null) {
                    openCamera();
                }
            } else {
                setSurfaceTextureListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        setCameraCharacteristics(manager);
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (manager!=null) {
                manager.openCamera(mCameraId, stateCallback, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void release() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void setCameraCharacteristics(CameraManager manager) {
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map != null) {
//                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                mPreviewSize = getCloselyPreSize(mWidth, mHeight, map.getOutputSizes(SurfaceTexture.class));
                mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.JPEG, 2);
//                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, 2);
                mImageReader.setOnImageAvailableListener(imageAvailableListener, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Size getCloselyPreSize(int surfaceWidth, int surfaceHeight, Size[] preSizeList) {

        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Size size : preSizeList) {
            if ((size.getWidth() == surfaceHeight) && (size.getHeight() == surfaceWidth)) {
                return size;
            }
        }
        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) surfaceHeight) / surfaceWidth;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Size retSize = null;
        for (Size size : preSizeList) {
            if (size.getWidth() >= 720 && size.getHeight() < surfaceHeight) {
                curRatio = ((float) size.getWidth()) / size.getHeight();
                deltaRatio = Math.abs(reqRatio - curRatio);
                if (deltaRatio < deltaRatioMin) {
                    deltaRatioMin = deltaRatio;
                    retSize = size;
                }
            }
        }

        if (retSize == null) {
            retSize = preSizeList[preSizeList.length / 2];
        }
        return retSize;
    }

    private void takePreview() {
        SurfaceTexture mSurfaceTexture = getSurfaceTexture();
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface mSurface = new Surface(mSurfaceTexture);
        try {
            final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureRequestBuilder.addTarget(mSurface);
            cameraDevice.createCaptureSession(Arrays.asList(mSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        mCaptureRequest = captureRequestBuilder.build();
                        mPreviewSession = session;
                        mPreviewSession.setRepeatingRequest(mCaptureRequest, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            CameraTextureView.this.cameraDevice = cameraDevice;
            takePreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            if (CameraTextureView.this.cameraDevice != null) {
                CameraTextureView.this.cameraDevice.close();
                CameraTextureView.this.cameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            cameraDevice.close();
        }
    };

    private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                SavePictureTask task = new SavePictureTask(mContext, mTakePictureFinishedListener,mCameraId);
                task.execute(reader);
            } else {
                ToastUtils.showMsg(mContext,"no sd_card");
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    private class SavePictureTask extends AsyncTask<ImageReader, Integer, String> {

        private Context mContext;
        private String mCameraId;
        private CameraView.onTakePictureFinishedListener mTakePictureFinishedListener;

        SavePictureTask(Context context, CameraView.onTakePictureFinishedListener mTakePictureFinishedListener,String cameraId) {
            this.mContext = context;
            this.mCameraId=cameraId;
            this.mTakePictureFinishedListener = mTakePictureFinishedListener;
        }

        @Override
        protected String doInBackground(ImageReader... params) {
            ImageReader reader = params[0];
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            String filePath=save(data);

            if (CAMERA_FACING_FRONT.equals(mCameraId)){
                int degree=readPictureDegree(filePath);
//                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                Bitmap bitmap = BitmapUtils.decodeBitmap(data, ScreenUtils.getWindowWidth(mContext), ScreenUtils.getWindowHeight(mContext));
                Matrix matrix = new Matrix();
                matrix.postRotate(degree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                data=baos.toByteArray();
                filePath=save(data);
                bitmap.recycle();
            }

            image.close();
            return filePath;
        }

        private String save(byte[] data){
            String filePath;
            if (TextUtils.isEmpty(mPicturePath)){
                filePath = FileUtils.getSavePicturePath(System.currentTimeMillis() + ".jpg");
            }else {
                filePath=mPicturePath;
            }
            File file = new File(filePath);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(data);
                fileOutputStream.close();
            } catch (Exception e) {
                filePath = null;
                e.printStackTrace();
            }
            return filePath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Uri localUri = Uri.fromFile(new File(s));
                Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                mContext.sendBroadcast(localIntent);
            }
            if (mTakePictureFinishedListener != null) {
                mTakePictureFinishedListener.onFinish(s);
            }
        }

        private int readPictureDegree(String path) {
            int degree = 0;
            try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 270;
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_UNDEFINED:
                        degree = 180;
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return degree;
        }
    }
}
