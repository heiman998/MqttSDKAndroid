package com.heiman.mqttdemo.base;

import com.google.gson.annotations.Expose;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/12/12 上午11:07
 * @Description :
 * @Modify record :
 */
public class Login extends Code{

    /**
     * userId : 37
     * access_token : eyJhbGciOiJIUzI1NiJ9.eyJlbnRlcnByaXNlSWQiOjEsImV4cCI6MTUxMzA1NTIyMSwidXNlcklkIjozN30.7xjICypnHsUW7PRGaEoDJDF68PEc3DhWMDlUDKnsgbE
     */
    @Expose
    private int userId;
    @Expose
    private String access_token;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
