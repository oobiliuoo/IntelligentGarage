package com.oobiliuoo.intelligentgarageapp.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class NotifyInfo extends LitePalSupport {
    private String time;
    private String text;
    @Column(nullable = false)
    private String ipAddress;

    public NotifyInfo() {
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
}
