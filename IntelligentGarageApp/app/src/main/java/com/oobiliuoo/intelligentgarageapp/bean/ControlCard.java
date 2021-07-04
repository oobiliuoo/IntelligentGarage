package com.oobiliuoo.intelligentgarageapp.bean;

import android.media.Image;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;


/**
 * @author biliu
 * 控制卡片类
 * 同时作为表
 */
public class ControlCard extends LitePalSupport {
    private String name;
    private String location;
    private String type;
    @Column(defaultValue = "OFF")
    private String state;
    @Column(defaultValue = "1")
    private String enable;
    private int img;

    public ControlCard() {
    }

    public ControlCard(String name, String location, String type, String state, String enable) {
        this.name = name;
        this.location = location;
        this.type = type;
        this.state = state;
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }


    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }
}
