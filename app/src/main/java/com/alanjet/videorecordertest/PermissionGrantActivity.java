package com.alanjet.videorecordertest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * Created by yz on 2019/3/20.
 */

public class PermissionGrantActivity extends Activity implements View.OnClickListener {

    private final int My_CAMERA_RequestCode = 1;
    private final int My_RECORD_RequestCode = 2;
    private final int My_STORAGE_RequestCode = 3;


    TextView tv_1;
    TextView tv_2;
    TextView tv_3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        initView();
        obtainPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        obtainPermission();


    }

    private void initView() {
        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_3 = findViewById(R.id.tv_3);

        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        tv_3.setOnClickListener(this);
    }

    private void obtainPermission() {

        obtainCameraPermission();


    }


    private void obtainCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            tv_1.setText("相机已经打开权限");
            obtainRecordAudioPermission();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, My_CAMERA_RequestCode);//1 can be another integer
            tv_1.setText("相机没打开权限");
        }

    }

    private void obtainRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            tv_2.setText("录像机已经打开权限");
            obtainStoragePermission();
        } else {
            tv_2.setText("录像机没打开权限");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, My_RECORD_RequestCode);//1 can be another integer
        }

    }

    private void obtainStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            tv_3.setText("存储已经打开权限");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            tv_3.setText("存储没打开权限");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, My_STORAGE_RequestCode);//1 can be another integer
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_1:
                obtainCameraPermission();
                break;
            case R.id.tv_2:
                obtainRecordAudioPermission();
                break;
            case R.id.tv_3:
                obtainStoragePermission();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case My_CAMERA_RequestCode:
                toastShow("请打开相机权限");
                break;
            case My_RECORD_RequestCode:
                toastShow("请打开录像权限");
                break;
            case My_STORAGE_RequestCode:
                toastShow("请打开存储权限");
                break;
        }

        reloadRequest(permissions,grantResults);
    }

    private void toastShow(String s) {

        Toast.makeText(this,s,Toast.LENGTH_LONG).show();

    }

    /**
     * 不断循环，到这个app的设置页面，直到用户将权限打开
     * @param permissions
     * @param grantResults
     */
    private void reloadRequest(String[] permissions, int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 0);
            }
        }
    }
}
