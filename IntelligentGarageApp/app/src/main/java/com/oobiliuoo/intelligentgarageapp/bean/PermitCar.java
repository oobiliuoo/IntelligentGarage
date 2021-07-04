package com.oobiliuoo.intelligentgarageapp.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class PermitCar extends LitePalSupport {
    private String carName;
    @Column(nullable = false,unique = true)
    private String licensePlate;
    @Column(nullable = false)
    private String ipAddress;

    public PermitCar() {
    }

    public PermitCar(String carName, String licensePlate, String ipAddress) {
        this.carName = carName;
        this.licensePlate = licensePlate;
        this.ipAddress = ipAddress;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
