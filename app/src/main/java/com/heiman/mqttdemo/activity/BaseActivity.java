package com.heiman.mqttdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.heiman.mqttdemo.Constant;
import com.heiman.mqttsdk.manage.HmDeviceManage;
import com.heiman.mqttsdk.modle.HmDevice;
import com.orhanobut.logger.Logger;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/11/27 下午2:51
 * @Description :
 * @Modify record :
 */
public class BaseActivity extends AppCompatActivity {
    public Context mContext;
    public HmDevice hmDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initDeviceData();
    }

    /**
     * 初始化设备信息
     */
    private void initDeviceData() {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            //接收name值
            String mac = bundle.getString(Constant.DEVICE_MAC);

            hmDevice = HmDeviceManage.getInstance().getDevice(mac);
        }
    }

    /**
     * 通过包名跳转
     *
     * @param activityName
     */
    public void startActivityForName(String activityName) {
        try {
            Class clazz = Class.forName(activityName);
            Intent intent = new Intent(this, clazz);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过包名跳转
     *
     * @param activityName
     */
    public void startActivityForName(String activityName, Bundle paramBundle) {
        try {
            Class clazz = Class.forName(activityName);
            Intent intent = new Intent(this, clazz);
            if (paramBundle != null)
                intent.putExtras(paramBundle);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 跳转界面
     *
     * @param paramClass
     */
    protected void openActivity(Class<?> paramClass) {
        Logger.e(getClass().getSimpleName(), "openActivity：：" + paramClass.getSimpleName());
        openActivity(paramClass, null);
    }

    protected void openActivity(Class<?> paramClass, Bundle paramBundle) {
        Intent localIntent = new Intent(this, paramClass);
        if (paramBundle != null)
            localIntent.putExtras(paramBundle);
        startActivity(localIntent);
    }
}
