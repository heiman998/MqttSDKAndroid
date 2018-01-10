package com.heiman.mqttdemo.base;

/**
 * @Author : 肖力 by mac
 * @Time :  2018/1/4 下午2:08
 * @Description :
 * @Modify record :
 */
public class HttpOtaVersion {

    /**
     * size : 固件长度
     * md5 : md5校验值
     * version : 固件版本
     * productId : 产品id
     * url : 固件地址
     * otaId : 升级任务id
     * initVersion : 初始版本
     * otaDetail : 升级任务详情
     * sub_index : 子设备在父设备下的index
     */

    private String size;
    private String md5;
    private String version;
    private String productId;
    private String url;
    private String otaId;
    private String initVersion;
    private String otaDetail;
    private String sub_index;

    @Override
    public String toString() {
        return "HttpOtaVersion{" +
                "size='" + size + '\'' +
                ", md5='" + md5 + '\'' +
                ", version='" + version + '\'' +
                ", productId='" + productId + '\'' +
                ", url='" + url + '\'' +
                ", otaId='" + otaId + '\'' +
                ", initVersion='" + initVersion + '\'' +
                ", otaDetail='" + otaDetail + '\'' +
                ", sub_index='" + sub_index + '\'' +
                '}';
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOtaId() {
        return otaId;
    }

    public void setOtaId(String otaId) {
        this.otaId = otaId;
    }

    public String getInitVersion() {
        return initVersion;
    }

    public void setInitVersion(String initVersion) {
        this.initVersion = initVersion;
    }

    public String getOtaDetail() {
        return otaDetail;
    }

    public void setOtaDetail(String otaDetail) {
        this.otaDetail = otaDetail;
    }

    public String getSub_index() {
        return sub_index;
    }

    public void setSub_index(String sub_index) {
        this.sub_index = sub_index;
    }
}
