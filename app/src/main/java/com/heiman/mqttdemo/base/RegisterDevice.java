package com.heiman.mqttdemo.base;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/12/29 上午11:30
 * @Description :
 * @Modify record :
 */
public class RegisterDevice {

    /**
     * id : 18605
     * mac : 845DD7681AD3
     * name : HMDevice
     * productId : 10000
     * mcuVersion : 1
     * wifiVersion : 1
     * enterpriseId : 1
     */

    private int id;
    private String mac;
    private String name;
    private int productId;
    private int mcuVersion;
    private int wifiVersion;
    private int enterpriseId;

    @Override
    public String toString() {
        return "RegisterDevice{" +
                "id=" + id +
                ", mac='" + mac + '\'' +
                ", name='" + name + '\'' +
                ", productId=" + productId +
                ", mcuVersion=" + mcuVersion +
                ", wifiVersion=" + wifiVersion +
                ", enterpriseId=" + enterpriseId +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getMcuVersion() {
        return mcuVersion;
    }

    public void setMcuVersion(int mcuVersion) {
        this.mcuVersion = mcuVersion;
    }

    public int getWifiVersion() {
        return wifiVersion;
    }

    public void setWifiVersion(int wifiVersion) {
        this.wifiVersion = wifiVersion;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
