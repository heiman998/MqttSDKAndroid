package com.heiman.mqttdemo.base;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/12/12 上午9:04
 * @Description :
 * @Modify record :
 */
public class SimpleResponse {
    public int code;
    public String msg;

    public Code toLzyResponse() {
        Code httpsCode = new Code();
        httpsCode.code = code;
        httpsCode.msg = msg;
        return httpsCode;
    }
}
