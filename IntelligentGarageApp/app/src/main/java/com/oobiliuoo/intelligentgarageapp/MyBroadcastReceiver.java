package com.oobiliuoo.intelligentgarageapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;


public class MyBroadcastReceiver extends BroadcastReceiver {
    private Handler handler;

    public MyBroadcastReceiver(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int connectStates = intent.getIntExtra(MyUtils.CONNECT_STATES,-1);
        switch (connectStates){
            case MyUtils.CONNECT_SUCCESS:
                MyUtils.sendMessage(handler,MyUtils.CONNECT_SUCCESS,null);
                break;
            case MyUtils.CONNECT_FAIL:
                MyUtils.sendMessage(handler,MyUtils.CONNECT_FAIL,null);
                break;
            default:
        }
    }
}
