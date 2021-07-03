package com.oobiliuoo.intelligentgarageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

public class SettingActivity extends AppCompatActivity {

    private String TGA = "SettingActivity";
    private MyTcpService.MyBinder myBinder;
    private MyBroadcastReceiver myBroadcastReceiver;
    private IntentFilter intentFilter;

    private Toolbar toolbar;
    private Button btnConnect;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            myBinder = (MyTcpService.MyBinder) service;
            MyUtils.mLog1(TGA,"bind success");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MyUtils.CONNECT_SUCCESS:
                    MyUtils.showToast(SettingActivity.this,"成功连接");
                    btnConnect.setBackground(getDrawable(R.drawable.button_drawable2));

                    break;
                case MyUtils.CONNECT_FAIL:
                    MyUtils.showToast(SettingActivity.this,"连接失败");
                    btnConnect.setBackground(getDrawable(R.drawable.button_drawable1));

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initServer();
        checkPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction(MyUtils.CONNECT_SUCCESS_BROADCAST);
        myBroadcastReceiver = new MyBroadcastReceiver(handler);
        registerReceiver(myBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(myBroadcastReceiver != null){
            myBroadcastReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    MyUtils.showToast(SettingActivity.this,"拒绝服务将无法使用程序");
                    finish();
                }
                break;
            default:
        }
    }

    private void initServer(){
        Intent bindIntent = new Intent(SettingActivity.this,MyTcpService.class);
        startService(bindIntent);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
    }

    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.set_toorbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnConnect = (Button) findViewById(R.id.set_btn_connect);
        btnConnect.setOnClickListener(new MyLister());

    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SettingActivity.this,
                    new String[]{Manifest.permission.INTERNET},1);
        }
    }


    class MyLister implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.set_btn_connect:
                    MyUtils.mLog1(TGA,"click reconnect");
                    myBinder.reConnect(MyUtils.HOST_IP_ADDRESS,8080);
                    break;

                default:
                    break;
            }
        }
    }





}