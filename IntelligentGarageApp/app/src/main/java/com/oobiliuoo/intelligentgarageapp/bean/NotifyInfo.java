package com.oobiliuoo.intelligentgarageapp.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class NotifyInfo extends LitePalSupport {
    private String time;
    private String text;
    @Column(nullable = false)
    private String ipAddress;
    private String mode;

    public NotifyInfo() {
    }

    public NotifyInfo(String time, String mode,String text, String ipAddress) {
        this.time = time;
        this.mode = mode;
        this.text = text;
        this.ipAddress = ipAddress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
