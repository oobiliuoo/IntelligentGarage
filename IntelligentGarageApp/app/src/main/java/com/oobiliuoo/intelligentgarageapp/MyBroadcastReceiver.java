package com.oobiliuoo.intelligentgarageapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;


public class MyBroadcastReceiver extends BroadcastReceiver {
    private Handler handler;

    public MyBroadcastReceiver() {
    }

    public MyBroadcastReceiver(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int broadcastMsg = intent.getIntExtra(MyUtils.BROADCAST_MSG,-1);
        switch (broadcastMsg){
            case MyUtils.CONNECT_SUCCESS:
                MyUtils.sendMessage(handler,MyUtils.CONNECT_SUCCESS,null);
                break;
            case MyUtils.CONNECT_FAIL:
                MyUtils.sendMessage(handler,MyUtils.CONNECT_FAIL,null);
                break;
            case MyUtils.RECEIVE_DATA:
                MyUtils.sendMessage(handler,MyUtils.RECEIVE_DATA,intent.getStringExtra(MyUtils.RECEIVE));
                break;
            default:
        }


    }
}
