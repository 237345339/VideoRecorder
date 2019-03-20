package com.alanjet.videorecordertest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Administrator on 2019/3/4.
 */

public class MyMediaSurfaceView extends SurfaceView {
    private SurfaceHolder mHolder;
    private static final String TAG = "MainActivity";
    private Camera.Parameters mParameters;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private Context mContext;


    public MyMediaSurfaceView(Context context) {
        super(context);

    }

    public MyMediaSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        startCallback();


    }

    public MyMediaSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startCallback() {
        mHolder = getHolder();
        mHolder.addCallback(new CustomCallBack());
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("TEST", "Granted");
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    return true;
                } else {
                    ActivityCompat.requestPermissions((Activity)mContext,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);//1 can be another integer
                }

            } else {
                ActivityCompat.requestPermissions((Activity)mContext,
                        new String[]{Manifest.permission.RECORD_AUDIO}, 101);//1 can be another integer
            }


        } else {
            ActivityCompat.requestPermissions((Activity)mContext,
                    new String[]{Manifest.permission.CAMERA}, 1);//1 can be another integer
        }
        return false;
    }

    /**
     * 开始录制
     */
    public boolean startRecord() {
       /* if (mCamera == null)
            openCamera(null);*/

        mCamera.autoFocus(null);
        prepareMediaRecorder();
        if (null != mMediaRecorder) {
            mMediaRecorder.start();//开始录制
            return true;
        }
        return false;
    }

    private class CustomCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mHolder = holder;
            if (mCamera == null)
                try {
                    openCamera(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (mMediaRecorder == null)
                return;
            mediaPrepare(mMediaRecorder);

            mParameters = mCamera.getParameters();
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCamera.setParameters(mParameters);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        Log.d(TAG, "自动对焦成功");
                    }
                }
            });
            try {
                mCamera.setPreviewDisplay(holder);//一定要在surfaceCreated中使用holder,而不能用全局的getHolder
                mCamera.startPreview();

                //下面这个方法能帮我们获取到相机预览帧，我们可以在这里实时地处理每一帧----------后期！！！！---------
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        Log.i(TAG, "获取预览帧...");
                        new ProcessFrameAsyncTask().execute(data);
                        Log.d(TAG, "预览帧大小：" + String.valueOf(data.length));
                    }
                });
            } catch (IOException e) {
                Log.d(TAG, "设置相机预览失败", e);
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e(TAG, ">>>>>>>>>>surfaceChanged width:" + width + " height:" + height);
          /*  mWidthPixel = width;
            mHeightPixel = height;
            setCameraParameters();
            updateCameraOrientation();*/
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e(TAG, ">>>>>>>>>>surfaceDestroyed");
            mHolder.removeCallback(this);
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mHolder = null;
        }
    }

    /**
     * 相机绑定surfaceview
     *
     * @param holder
     */
    private void openCamera(SurfaceHolder holder) throws IOException {
        mCamera = Camera.open();
        /*WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);//得到窗口管理器
        Display display = wm.getDefaultDisplay();//得到当前屏幕
        Camera.Parameters parameters =mCamera.getParameters();//得到摄像头的参数

        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();

        int PreviewWidth = 0;
        int PreviewHeight = 0;
        // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
        if (sizeList.size() > 1) {
            Iterator<Camera.Size> itor = sizeList.iterator();
            while (itor.hasNext()) {
                Camera.Size cur = itor.next();
                if (cur.width >= PreviewWidth
                        && cur.height >= PreviewHeight) {
                    PreviewWidth = cur.width;
                    PreviewHeight = cur.height;
                    break;
                }
            }
        }
        parameters.setPreviewSize(PreviewWidth, PreviewHeight); //获得摄像区域的大小
        parameters.setPreviewFrameRate(3);//设置每秒3帧
        parameters.setPictureFormat(PixelFormat.JPEG);//设置照片的格式
        parameters.setJpegQuality(85);//设置照片的质量
        parameters.setPictureSize(PreviewWidth, PreviewHeight);//设置拍出来的屏幕大小*/
        //        mCamera.setParameters(parameters);
        mCamera.setPreviewDisplay(holder);//通过SurfaceView显示取景画面
        mCamera.startPreview();//开始预览
    }

    private boolean prepareMediaRecorder() {
        mMediaRecorder = new MediaRecorder();
        //        mCamera.setDisplayOrientation(90);//不要再旋转了，因为一开始已经让相机90度了，再转就出问题了————————————-！！！！
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);


        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);


        mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);

        mMediaRecorder.setOrientationHint(90);

        mMediaRecorder.setMaxDuration(30 * 1000);
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());//-----------千万别写--------

        mediaPrepare(mMediaRecorder);

        return true;
    }

    private void mediaPrepare(MediaRecorder mMediaRecorder) {

        String path = getSDPath();
        if (path != null) {

            File dir = null;
            File movieFile = null;
            try {
                dir = new File(Environment.getExternalStorageDirectory()//内部存储/Test
                        .getCanonicalFile() + "/Test");
                if(!dir.exists())
                    dir.mkdir();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //            dir=new File(  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath()+"/" + getDate() +".mp4");
            movieFile = new File(dir.getPath() + "/" + getDate() + ".mp4");


            mMediaRecorder.setOutputFile(movieFile.getAbsolutePath());
            try {
                mMediaRecorder.prepare();
            } catch (IOException e) {
                releaseMediaRecorder();
                e.printStackTrace();
            }
        }
    }


    public void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    public boolean isRecording() {
        return mMediaRecorder != null;
    }


    private class ProcessFrameAsyncTask extends AsyncTask<byte[], Void, String> {

        @Override
        protected String doInBackground(byte[]... params) {
            processFrame(params[0]);
            return null;
        }

        private void processFrame(byte[] frameData) {

            Log.i(TAG, "正在处理预览帧...");
            Log.i(TAG, "预览帧大小" + String.valueOf(frameData.length));
            Log.i(TAG, "预览帧处理完毕...");
            //下面这段注释掉的代码是把预览帧数据输出到sd卡中，以.yuv格式保存
            //            String path = getSDPath();
            //            File dir = new File(path + "/FrameTest");
            //            if (!dir.exists()) {
            //                dir.mkdir();
            //            }
            //            path = dir + "/" + "testFrame"+".yuv";
            //            File file =new File(path);
            //            try {
            //                FileOutputStream fileOutputStream=new FileOutputStream(file);
            //                BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
            //                bufferedOutputStream.write(frameData);
            //                Log.i(TAG, "预览帧处理完毕...");
            //
            //            } catch (FileNotFoundException e) {
            //                e.printStackTrace();
            //            } catch (IOException e) {
            //                e.printStackTrace();
            //            }
        }
    }

    /**
     * 获取SD path
     */
    public String getSDPath() {
        File sdDir;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取外部存储的根目录
            return sdDir.toString();
        }

        return null;
    }

    /**
     * 获取系统时间
     */
    public static String getDate() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);           // 获取年份
        int month = ca.get(Calendar.MONTH);         // 获取月份
        int day = ca.get(Calendar.DATE);            // 获取日
        int minute = ca.get(Calendar.MINUTE);       // 分
        int hour = ca.get(Calendar.HOUR);           // 小时
        int second = ca.get(Calendar.SECOND);       // 秒

        String date = "" + year + (month + 1) + day + hour + minute + second;
        Log.d(TAG, "date:" + date);

        return date;
    }
}
