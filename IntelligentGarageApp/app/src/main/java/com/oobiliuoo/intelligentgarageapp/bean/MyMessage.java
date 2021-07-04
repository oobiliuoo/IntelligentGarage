package com.oobiliuoo.intelligentgarageapp.bean;


import com.oobiliuoo.intelligentgarageapp.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author biliu
 * 信息的操作类
 * 一条有效信息应该包含以下三个部分
 *  1 IP : 发送者的IP
 *  2 MODE : 数据信息模式
 *  3 CONTEXT : 数据内容
 *  格式为 IP：：MODE：：CONTEXT
 *  EX:
 *  192.168.43.131::DATA::LED1-ON::LED2-OFF::DOOR1-ON
 *  192.168.43.131::NOTIFY::WARING
 *  192.168.43.131::ATION::VIDEO-GET
 *  192.168.43.131::ATION::LED1-ON
 *
 */
public class MyMessage {


    /** DIVISION: 分割符，根据这个拆分字符串 */
    public static final String DIVISION = "::";
    public static final String DIVISION2 = "-";
    /**收到信息的模式
     * DATA： 数据信息
     * NOTIFY： 通知信息
     * */
    public static final String[] MESSAGE_MODE = {"DATA","NOTIFY","ACTION"};

    private String message;
    private String ip;
    private int mode;
    private String context;

    public MyMessage() {
        ip = MyUtils.HOST_IP_ADDRESS;
        mode = 0;
        context = "";
        message = ip + DIVISION + MESSAGE_MODE[mode] + DIVISION + context;
    }

    public MyMessage(String message) {
        this.message = message;
        String[] msg = message.split(DIVISION,3);
        this.ip = msg[0];
        for(int i=0;i<MESSAGE_MODE.length;i++){
            if(msg[1].equals(MESSAGE_MODE[i])){
                this.mode = i;
                break;
            }
        }
        this.context = msg[2];
    }

    public String getMessage() {
        message = ip + DIVISION + MESSAGE_MODE[mode] + DIVISION + context;
        return message;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMode(int mode){
        if(mode>0 && mode < MESSAGE_MODE.length){
            this.mode = mode;
        }
    }

    public String getMode() {
        return MESSAGE_MODE[this.mode];
    }
    public int getIntMode() {
        return this.mode;
    }


    public void setDataMode() {
        this.mode = 0;
    }
    public void setNotifyMode() {
        this.mode = 1;
    }
    public void setActionMode() {
        this.mode = 2;
    }

    public String getContext() {
        return context;
    }

    /**
     *  ex: context = "LED0-ON::LED1-OFF::DOOR-ON"
     *  第一步：data1 = {"LED0-ON", "LED1-OFF", "DOOR-ON"}
     *  第二步：temp1 = "LED0-ON"
     *         list.add( {"LED0", "ON"} )
     * */
    public ArrayList<String[]> getDataContext(){
        ArrayList<String[]> list = new ArrayList<>();
        if(this.mode == 0){
            String[] data1 = this.context.split(DIVISION);
            for(String temp : data1){
                list.add(temp.split(DIVISION2));
            }
        }else {
            MyUtils.mLog1("MyMessage: getDataContext fail. require mode 0 but mode is " + this.mode);
        }
        return list;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setContext(ControlCard card) {
        this.context += card.getName() + DIVISION + card.getState() + DIVISION;
    }

    public void setContext(List<ControlCard> list){
        for(ControlCard card : list){
            this.context += card.getName() + DIVISION + card.getState() + DIVISION;
        }
    }




}
