package com.oobiliuoo.intelligentgarageapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.oobiliuoo.intelligentgarageapp.R;
import com.oobiliuoo.intelligentgarageapp.bean.ControlCard;
import com.oobiliuoo.intelligentgarageapp.bean.HostLocation;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;


/**
 * @author biliu
 */
public class MyUtils {

    /**服务器IP地址*/
    public static final String HOST_IP_ADDRESS = "192.168.43.239";
    /**服务器开放的端口号*/
    public static final int IP_PORT = 8081;

    /**广播对象*/
    public static final String MY_BROADCAST = "com.oobiliuoo.intelligentgarageapp.MY_BROADCAST";
    /**
     * 广播数据模式标志
     * CONNECT_SUCCESS ： 与服务器连接成功
     * CONNECT_FAIL    :  与服务器断开连接
     * RECEIVE_DATA    :  收到数据信息
     * RECEIVE_NOTIFY  :  收到通知信息
     * */
    public static final String BROADCAST_MSG = "broadcastMsg";
    /**连接成功*/
    public static final int CONNECT_SUCCESS = 0;
    /**连接失败*/
    public static final int CONNECT_FAIL = 1;


    /**收到信息的模式
     * DATA： 数据信息
     * NOTIFY： 通知信息
     * */
    public static final String[] RECEIVE_MODE = {"DATA","NOTIFY"};
    /**收到标志*/
    public static final String RECEIVE = "receive";
    /**收到数据*/
    public static final int RECEIVE_DATA = 3;
    /**收到通知*/
    public static final int RECEIVE_NOTIFY = 4;

    /** DIVISION: 分割符，根据这个拆分字符串 */
    public static final String DIVISION = "::";
    public static final String DIVISION2 = "-";


    /**保存控制卡片的图片*/
    public static HashMap<String,int[]> IMG_MAP = new HashMap<>();

    /**
     *  相当于 log.i("mLog1",text)
     *  text: 想要显示的文字
     * */
    public static void mLog1(String text){
        Log.i("mLog", text);
    }
    /**
     *  相当于 log.i(TAG,text)
     *  TAG: TAG
     *  text: 想要显示的文字
     * */
    public static void mLog1(String TAG ,String text){
        Log.i(TAG, text);
    }

    /**
     *  显示Toast
     * */
    public static void showToast(Context context, String text){

        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }

    /**
     *  通过Handler传递消息
     * */
    public static void sendMessage(Handler handler, int what, Object obj){
        Message message = Message.obtain();
        //Message message = new Message();
        message.what = what;
        message.obj = obj;
        handler.sendMessage(message);
    }

    /**
     *  判断是否为纯数字
     *  str:想判断的字符串
     *  return: boolean
     * */
    public static boolean isNumeric(String str) {
        for(int i =0;i<str.length();i++){
            if(!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    /**
     *  从SharePreferences中读取当前登录帐号
     *  context: 当前活动上下文
     *  return: 当前帐号
     * */
    public static String readCurrentUser(Context context){
        SharedPreferences pref = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        String tel = pref.getString("tel","");
        return tel;
    }

    /**
     * 初始化图片
     * 在这里设置每种控制卡片开/关对应的图片
     * */
    public static void initImgMap(){
        IMG_MAP.put("吊灯",new int[]{R.drawable.ceiling_lamp_off,R.drawable.ceiling_lamp_on});
        IMG_MAP.put("大灯",new int[]{R.drawable.light_off,R.drawable.light_on});
        IMG_MAP.put("门",new int[]{R.drawable.door_closed,R.drawable.door_open2});
        mLog1("img map init");
    }


    public static void resetControlCardTable(){
        LitePal.deleteAll(ControlCard.class);
        ControlCard card1 = new ControlCard("LED1","车库","吊灯","OFF","1");
        card1.save();
        ControlCard card2 = new ControlCard("LED2","车库外","大灯","OFF","1");
        card2.save();
        ControlCard card3 = new ControlCard("DOOR1","车库","门","OFF","1");
        card3.save();
        ControlCard card4 = new ControlCard("DOOR2","车库侧门","门","OFF","1");
        card4.save();

    }

    public static void showControlCardAll(){
        List<ControlCard> list = LitePal.findAll(ControlCard.class);
        if(list.size() > 0){
            for(ControlCard c : list){

                MyUtils.mLog1("card " +c.getName()+" " + c.getType()+ " " + c.getState());
            }
        }
    }


    public static void resetHostLocationTable(){
        LitePal.deleteAll(HostLocation.class);
        HostLocation temp1 = new HostLocation("家","192.168.43.131","8080");
        temp1.save();
        HostLocation temp2 = new HostLocation("公司","192.168.43.1","8080");
        temp2.save();
        HostLocation temp3 = new HostLocation("老家","192.168.43.131","8080");
        temp3.save();
        HostLocation temp4 = new HostLocation("北京","127.0.0.1","8080");
        temp4.save();
    }
}
