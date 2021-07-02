package com.oobiliuoo.intelligentgarageapp.bean;

import android.media.Image;

public class ControlCard {
    private String location;
    private String type;
    private String state;
    private int img;

    public ControlCard() {
    }

    public ControlCard(String location, String type, String state, int img) {
        this.location = location;
        this.type = type;
        this.state = state;
        this.img = img;
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
}
