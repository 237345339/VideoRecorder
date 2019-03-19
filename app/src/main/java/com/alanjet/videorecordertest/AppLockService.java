package com.alanjet.videorecordertest;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2019/3/8.
 */

public class AppLockService extends Service {
    private ActivityManager am;
    private KeyguardManager keyguardManager;
    private AudioManager mAudioManager;
    private LockScreenReceiver receiver;
    private IBinder iBinder = new LocalBinder();//容易记不住！！！！，思路：服务内创建一个Binder,通过onBind返回
    public boolean isLockScreen = false;
    public boolean isVoiceChange = false;

    @Override
    public void onCreate() {
        super.onCreate();

        beginListenBroadcast();


    }

    private void beginListenBroadcast() {
        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);//注册锁屏广播接收者---------IntentFilter---------
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);//注册锁屏广播接收者---------IntentFilter---------
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");//注册锁屏广播接收者---------IntentFilter---------
        //        intentFilter.addAction("android.i ntent.action.VOICE_ASSIST");//注册锁屏广播接收者---------IntentFilter---------
        //        intentFilter.addAction("android.intent.action.VOICE_COMMAND");//注册锁屏广播接收者---------IntentFilter---------
        //        intentFilter.addAction("android.intent.action.MEDIA_BUTTON");
        //        intentFilter.addAction("android.media.AUDIO.BECOMING_NOISY");
        //        intentFilter.addAction("android.media.VIBRATE_SETTING_CHANGED");
        //        intentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
        //        intentFilter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");


        receiver = new LockScreenReceiver();
        registerReceiver(receiver, intentFilter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                /*while (true) {
                    isLockScreen = keyguardManager.inKeyguardRestrictedInputMode();
                    isVoiceChange = mAudioManager.isMusicActive();
                    if (isLockScreen)
                        if (mcb != null)
                            mcb.callBackForActPower();
                    if (isVoiceChange)
                        mcb.callBackForActVoice();

                }*/
            }
        }).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }//容易记不住！！！！，思路：服务内创建一个Binder,通过onBind返回

    private final class LockScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (mcb != null)
                    mcb.callBackForActPowerOFF();

            }
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                if (mcb != null)
                    mcb.callBackForActPowerOn();

            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消锁屏的广播监听
        unregisterReceiver(receiver);
    }


    public class LocalBinder extends Binder {
        public AppLockService getService() {//容易记不住！！！！，思路：binder可返回服务
            return AppLockService.this;
        }

    }

    MyServiceCallBack mcb;

    public interface MyServiceCallBack {
        public void callBackForActPowerOFF();
        public void callBackForActPowerOn();

        public void callBackForActVoice();
    }

    public void setMcb(MyServiceCallBack mcb) {
        this.mcb = mcb;
    }
}
