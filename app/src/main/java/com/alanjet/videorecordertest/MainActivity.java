package com.alanjet.videorecordertest;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import service.OpenScreenService;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = "MainActivity";
    private MyMediaSurfaceView mSurfaceView;
    private ImageButton mBtnStartStop;
    private ImageButton mBtnSet;
    private ImageButton mBtnShowFile;
    private Chronometer mTimer;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    AppLockService as;
    MyServiceConn mc;
    OpenScreenService openScreenService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowFeature();
        setContentView(R.layout.activity_main);
        initView();
       /* if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }*/


        initService();
        openScreenService = new OpenScreenService(new OpenScreenService.ScreenCallBack() {
            @Override
            public void stopRecordReceive() {
                stopRecord();
            }
        });
        registerReceiver(openScreenService, new IntentFilter());


    }

    private void initService() {
        mc = new MyServiceConn();

        //服务不是new出来的的。而是通过ServiceConnection的IBinder返回来的===一定要在fest中注册服务===
        boolean isStartService = bindService(new Intent(MainActivity.this, AppLockService.class), mc
                , Service.BIND_AUTO_CREATE);
        Log.i("ting", "" + isStartService);
    }


    private void initListeners() {
        mBtnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "录像");

                startOrStop();
            }
        });
        mBtnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "设置");
                Toast.makeText(MainActivity.this, "设置待开发...", Toast.LENGTH_SHORT).show();
            }
        });
        mBtnShowFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowVideoActivity.class);
                startActivity(intent);
            }
        });

    }

    private void startOrStop() {
        if (mSurfaceView.isRecording()) {
            Log.d(TAG, "停止录像");
            releaseMedia();
            mTimer.stop();
            mBtnStartStop.setBackgroundResource(R.drawable.rec_start);
        } else {
            if (startRecording()) {
                Log.d(TAG, "开始录像");
                mTimer.setBase(SystemClock.elapsedRealtime());
                mTimer.start();
                mBtnStartStop.setBackgroundResource(R.drawable.rec_stop);
            }
        }
    }

    private void startRecord() {
        if (mSurfaceView.isRecording()) {
            Log.d(TAG, "停止录像");
            releaseMedia();
            mTimer.stop();
            mBtnStartStop.setBackgroundResource(R.drawable.rec_start);
        }
    }

    private void stopRecord() {
        if (startRecording()) {
            Log.d(TAG, "开始录像");
            mTimer.setBase(SystemClock.elapsedRealtime());
            mTimer.start();
            mBtnStartStop.setBackgroundResource(R.drawable.rec_stop);
        }
    }


    public boolean startRecording() {

        if (mSurfaceView.startRecord()) {
            return true;
        } else {
            return false;
        }
    }

    private void releaseMedia() {
        mSurfaceView.releaseMediaRecorder();
    }

    private void initView() {
        mSurfaceView = (MyMediaSurfaceView) findViewById(R.id.capture_surfaceview);
        mBtnStartStop = (ImageButton) findViewById(R.id.ib_stop);
        mBtnSet = (ImageButton) findViewById(R.id.capture_imagebutton_setting);
        mBtnShowFile = (ImageButton) findViewById(R.id.capture_imagebutton_showfiles);
        mTimer = (Chronometer) findViewById(R.id.crm_count_time);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        initView();
        initListeners();
    }

    private void initWindowFeature() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        // 设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // 选择支持半透明模式,在有SurfaceView的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i("ting", "");
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.i("ting", "");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i("ting", "");
    }


    //    -------------service---------------------
    class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AppLockService.LocalBinder ib = (AppLockService.LocalBinder) iBinder;//容易记不住！！！！
            as = ib.getService();
            as.setMcb(new AppLockService.MyServiceCallBack() {

                @Override
                public void callBackForActPowerOFF() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopRecord();
                        }
                    });
                }

                @Override
                public void callBackForActPowerOn() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startRecord();
                        }
                    });
                }

                @Override
                public void callBackForActVoice() {

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            as = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mc);
        //        unregisterReceiver(openScreenService);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }

        return super.onKeyDown(keyCode, event);

    }
}

