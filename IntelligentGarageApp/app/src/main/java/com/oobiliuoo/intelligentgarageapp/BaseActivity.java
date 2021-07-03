package com.oobiliuoo.intelligentgarageapp;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

import org.litepal.LitePal;

public abstract class BaseActivity extends AppCompatActivity {

    private String TGA = "BaseActivity";
    /**
     * 点击控件的ID
     */
    public int CLICK_ID;
    /**
     * 消息对象
     */
    public Message MESSAGE;

    /**
     * 联系和操作服务的类
     */
    public MyTcpService.MyBinder myBinder;
    /**
     * 广播监听对象
     */
    public MyBroadcastReceiver myBroadcastReceiver;
    public IntentFilter intentFilter;

    /**
     * 将活动与服务联系起来
     */
    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyTcpService.MyBinder) service;
            MyUtils.mLog1(TGA, "bind success");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 用于消息传递，进行UI更新
     */
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MESSAGE = msg;
            initHandleMessage();

        }
    };

    /**
     * 消息处理事件
     * MESSAGE 收到的消息
     */
    protected abstract void initHandleMessage();


    /**
     * 加final 是为了不让子类覆盖,预防一些类还没初始化就被子类调用
     */
    @Override
    protected final void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        initServer();
        LitePal.initialize(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        // 添加动作对象
        intentFilter.addAction(MyUtils.CONNECT_SUCCESS_BROADCAST);
        myBroadcastReceiver = new MyBroadcastReceiver(handler);
        // 注册广播
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myBroadcastReceiver != null) {
            myBroadcastReceiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 绑定服务
     */
    private void initServer() {
        Intent bindIntent = new Intent(BaseActivity.this, MyTcpService.class);
        startService(bindIntent);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
    }

    /**
     * 检查应用权限
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{Manifest.permission.INTERNET}, 1);
        }
    }

    /**
     * 权限查询的回调函数
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    MyUtils.showToast(BaseActivity.this, "拒绝服务将无法使用程序");
                    finish();
                }
                break;
            default:
        }
    }

    /**
     * 提供给子类的初始化函数
     */
    public abstract void init();

    /**
     * 点击事件的监听类
     */
    class MyLister implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CLICK_ID = v.getId();
            initClick();
        }
    }

    /**
     * 提供给子类的点击事件函数
     * CLICK_ID 点击控件的ID
     */
    public abstract void initClick();


}


