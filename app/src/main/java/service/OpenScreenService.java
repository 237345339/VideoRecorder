package service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OpenScreenService extends BroadcastReceiver {
    ScreenCallBack screenCallBack;

    @Override
    public void onReceive(Context context, Intent intent) {
        screenCallBack.stopRecordReceive();
    }

    public OpenScreenService(ScreenCallBack screenCallBack) {
        this.screenCallBack = screenCallBack;
    }

    public interface ScreenCallBack {
        void stopRecordReceive();
    }
}
