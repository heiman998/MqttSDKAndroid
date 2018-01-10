package com.heiman.mqttdemo.base;

/**
 * @Author : 肖力 by mac
 * @Time :  2018/1/3 下午2:20
 * @Description :
 * @Modify record :
 */
public class DeviceBase {

    private String deviceName;
    private String deviceMac;
    private String subMac;
    private int subIndex;
    private boolean isOnline;

    @Override
    public String toString() {
        return "DeviceBase{" +
                "deviceName='" + deviceName + '\'' +
                ", deviceMac='" + deviceMac + '\'' +
                ", subMac='" + subMac + '\'' +
                ", subIndex=" + subIndex +
                ", isOnline=" + isOnline +
                '}';
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getSubIndex() {
        return subIndex;
    }

    public void setSubIndex(int subIndex) {
        this.subIndex = subIndex;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getSubMac() {
        return subMac;
    }

    public void setSubMac(String subMac) {
        this.subMac = subMac;
    }
}
