package com.oobiliuoo.intelligentgarageapp;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

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

    private String TGA = "server";

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


    }

    private void initIpAndPort() {
        ip = MyUtils.HOST_IP_ADDRESS;
        port = MyUtils.IP_PORT;
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
            mSocket.connect(new InetSocketAddress(ip, port), 5 * 1000);
            if (mSocket != null) {
                mSocket.setSoTimeout(300);
                //获取输出流、输入流
                mOutStream = mSocket.getOutputStream();
                mInStream = mSocket.getInputStream();
                MyUtils.mLog1(TGA, "连接成功");
                Intent intent = new Intent(MyUtils.CONNECT_SUCCESS_BROADCAST);
                intent.putExtra(MyUtils.CONNECT_STATES,MyUtils.CONNECT_SUCCESS);
                sendBroadcast(intent);
                receive();
            } else {
                mSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyUtils.mLog1(TGA, "连接失败");
            Intent intent = new Intent(MyUtils.CONNECT_SUCCESS_BROADCAST);
            intent.putExtra(MyUtils.CONNECT_STATES,MyUtils.CONNECT_FAIL);
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


}