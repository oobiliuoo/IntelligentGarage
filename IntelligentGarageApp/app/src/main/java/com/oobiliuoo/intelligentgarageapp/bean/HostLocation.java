package com.oobiliuoo.intelligentgarageapp.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class HostLocation extends LitePalSupport {

    @Column(defaultValue = "unKnow")
    private String locationName;
    @Column(nullable = false)
    private String ipAddress;
    @Column(nullable = false)
    private String port;

    public HostLocation() {
    }

    public HostLocation(String locationName, String ipAddress, String port) {
        this.locationName = locationName;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
