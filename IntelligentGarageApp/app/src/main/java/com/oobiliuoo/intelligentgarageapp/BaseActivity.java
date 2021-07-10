package com.oobiliuoo.intelligentgarageapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.oobiliuoo.intelligentgarageapp.adapter.HostLocationListViewAdapter;
import com.oobiliuoo.intelligentgarageapp.adapter.MyGridViewAdapter;
import com.oobiliuoo.intelligentgarageapp.bean.ControlCard;
import com.oobiliuoo.intelligentgarageapp.bean.HostLocation;
import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

import org.litepal.LitePal;

public abstract class BaseActivity extends AppCompatActivity {

    private String TAG = "BaseActivity";

    /**
     * 读取本地信息
     */
    public SharedPreferences pref;
    public SharedPreferences.Editor prefEditor;
    public HostLocation host;


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
            MyUtils.mLog1(TAG, "bind success");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 用于消息传递，进行UI更新
     */
    public Handler handler;

    /**
     * 消息处理事件
     * MESSAGE 收到的消息
     */
    protected abstract void initHandleMessage(Message msg);


    /**
     * 加final 是为了不让子类覆盖,预防一些类还没初始化就被子类调用
     */
    @Override
    protected final void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.Snow2));

        initData();
        checkPermission();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                initHandleMessage(msg);

            }
        };

        initServer();
        LitePal.initialize(this);
        init();
    }

    public void initData() {
        pref = getSharedPreferences("data",MODE_PRIVATE);
        prefEditor = getSharedPreferences("data",MODE_PRIVATE).edit();
        String hostName = pref.getString("hostname","home");
        String ip = pref.getString("ip","127.0.0.1");
        String port = pref.getString("port","8080");
        host = new HostLocation(hostName,ip,port);
    }

    @Override
    protected void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        // 添加动作对象
        intentFilter.addAction(MyUtils.MY_BROADCAST);
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
        MyUtils.mLog1("BaseActivity onDestroy");
        Intent bindIntent = new Intent(BaseActivity.this, MyTcpService.class);
        stopService(bindIntent);
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

    private void readCurrentHostLocation(){

    }

    /**
     * 点击事件的监听类
     */
    class MyLister implements View.OnClickListener,MyGridViewAdapter.MyOnClickListener {

        @Override
        public void onClick(View v) {
            initClick(v);
        }

        @Override
        public boolean MyCheckChanged(ControlCard card, boolean isChecked) {
            return checkChanged(card,isChecked);
        }


    }


    /**
     * 提供给子类的点击事件函数
     * v 点击控件
     */
    public abstract void initClick(View v);

    /**
     * 提供给子类的GridView it 点击事件
     * IS_CHECK: 表示被选中
     * card: 哪个控制卡片
     * */
    protected abstract boolean checkChanged(ControlCard card, boolean isChecked);






}


