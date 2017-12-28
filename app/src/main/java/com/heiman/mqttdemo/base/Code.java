package com.heiman.mqttdemo.base;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/12/12 上午9:03
 * @Description :
 * @Modify record :
 */
public class Code<T> {
    @Expose
    @SerializedName("code")
    public int code;
    @Expose
    @SerializedName("msg")
    public String msg;
    @Expose
    @SerializedName("data")
    public T data;

    @Override
    public String toString() {
        return "Remoteble{\n" +//
                "\tcode=" + code + "\n" +//
                "\tmsg='" + msg + "\'\n" +//
                "\tdata=" + data + "\n" +//
                '}';
    }
}
