package com.oobiliuoo.intelligentgarageapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.oobiliuoo.intelligentgarageapp.bean.MyMessage;
import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author biliu
 * Tcp客户端服务
 */
public class MyTcpService extends Service {

    private String TGA = "mLog";

    private MyBinder myBinder = new MyBinder();
    private Socket mSocket;
    private String ip;
    private int port;
    private OutputStream mOutStream;
    private InputStream mInStream;
    private String sendMsg;


    /**
     * 服务第一次启动时调用
     */
    @Override
    public void onCreate() {
        MyUtils.mLog1(TGA, "onCreate");
        initIpAndPort();
        connect();
        super.onCreate();
    }

    /**
     * 服务启动时调用
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyUtils.mLog1(TGA, "onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        MyUtils.mLog1(TGA, "onDestroy");
        super.onDestroy();
    }

    public MyTcpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    /**
     * 将服务与活动联系起来，活动通过获取服务对应的Binder进行操作
     */
    class MyBinder extends Binder {
        public void reConnect(String nIp, int nPort) {
            ip = nIp;
            port = nPort;

            if(mSocket.isConnected()){
                try {
                    mSocket.close();
                    MyUtils.mLog1(TGA,"close by man");
                    connect();
                    MyUtils.mLog1(TGA,"reconnect");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                connect();
            }
        }

        public void sendMyMessage(MyMessage myMessage){
            sendMsg = myMessage.getMessage();
            send();
        }


    }

    private void initIpAndPort() {
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);

        ip = pref.getString("ip","127.0.0.1");
        String mPort = pref.getString("port","8080");
        port = Integer.valueOf(mPort).intValue();
    }

    private void connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                tcpConnect();
            }
        }).start();
    }

    private void tcpConnect() {
        MyUtils.mLog1(TGA, "connect " + ip + " " + port);
        try {
            //指定ip地址和端口号
            // 1.创建一个客户端对象Socket,构造方法绑定服务器的IP地址和端口号
            mSocket = new Socket();
            mSocket.connect(new InetSocketAddress(ip, port), 1 * 1000);
            if (mSocket != null) {
                mSocket.setSoTimeout(300);
                //获取输出流、输入流
                mOutStream = mSocket.getOutputStream();
                mInStream = mSocket.getInputStream();
                MyUtils.mLog1(TGA, "连接成功");

                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putBoolean("connectSuccess",true);
                editor.apply();

                // 发送广播
                Intent intent = new Intent(MyUtils.MY_BROADCAST);
                intent.putExtra(MyUtils.BROADCAST_MSG,MyUtils.CONNECT_SUCCESS);
                sendBroadcast(intent);
                receive();
            } else {
                mSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyUtils.mLog1(TGA, "连接失败");
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putBoolean("connectSuccess",false);
            editor.apply();
            Intent intent = new Intent(MyUtils.MY_BROADCAST);
            intent.putExtra(MyUtils.BROADCAST_MSG,MyUtils.CONNECT_FAIL);
            sendBroadcast(intent);
        }
    }

    private void receive(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mSocket != null && mSocket.isConnected() && mInStream != null) {
                    String msg = receiveMsg();
                    if (!"".equals(msg)) {
                        MyUtils.mLog1(TGA, "recv:" + msg);
                        MyMessage message = new MyMessage(msg);
                        switch (message.getIntMode()){
                            case 0:
                                Intent intent = new Intent(MyUtils.MY_BROADCAST);
                                intent.putExtra(MyUtils.BROADCAST_MSG,MyUtils.RECEIVE_DATA);
                                intent.putExtra(MyUtils.RECEIVE,message.getMessage());
                                sendBroadcast(intent);
                                break;
                            case 1:
                                MyUtils.mLog1("server: notify");
                                sendNotify(message);
                                break;
                            default:
                        }

                    }
                }
            }
        }).start();
    }

    private String receiveMsg() {
        byte[] bytes = new byte[1024];
        int len = 0;
        try {
            len = mInStream.read(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String msg = new String(bytes, 0, len);
        return msg;
    }

    private void send(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket != null && mSocket.isConnected() ) {
                    writeMsg();

                } else {
                    MyUtils.mLog1( "server: 服务处于断开状态，请检查网络");
                    return;
                }
            }
        }).start();
    }

    private void writeMsg(){
        if (sendMsg.length() == 0 || mOutStream == null) {
            MyUtils.mLog1("server: send fail. check the message or internet");
            return;
        }
        try {   //发送
            mOutStream.write(sendMsg.getBytes());
            mOutStream.flush();
            MyUtils.mLog1("server: send success. the msg: " + sendMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void sendNotify(MyMessage myMessage){
        String text = myMessage.getContext();
        String id = "my_channel_01";
        String name="我是渠道名字";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setChannelId(id)
                    .setContentTitle("有车辆入库")
                    .setContentText(text)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo_16)
                    .build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("5 new messages")
                    .setContentText(text)
                    .setSmallIcon(R.drawable.logo_16)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo_48))
                    .setOngoing(true)
                    ;//无效
            notification = notificationBuilder.build();
        }
        notificationManager.notify(111123, notification);

    }

}