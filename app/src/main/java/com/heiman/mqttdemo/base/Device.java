package com.heiman.mqttdemo.base;

import com.heiman.mqttsdk.HmAgent;
import com.heiman.mqttsdk.modle.HmDevice;

import org.litepal.annotation.Encrypt;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/12/4 上午9:48
 * @Description :
 * @Modify record :
 */
public class Device extends DataSupport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Encrypt(algorithm = AES)
    private String hmDevice;
    private Long id;
    private String mac;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getHmDevice() {
        return hmDevice;
    }

    public HmDevice getHmDeviceSDK() {
        return HmAgent.getInstance().jsonToHmDevice(hmDevice);
    }

    public void setHmDevice(HmDevice hmDevice) {
        this.hmDevice = HmAgent.getInstance().hmDeviceToJson(hmDevice);
    }

    public void setHmDevice(String hmDevice) {
        this.hmDevice = hmDevice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
