package com.heiman.mqttdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.gson.Gson;
import com.heiman.mqttdemo.Manage.DeviceManage;
import com.heiman.mqttdemo.back.Dialogback;
import com.heiman.mqttdemo.base.Device;
import com.heiman.mqttdemo.base.Login;
import com.heiman.mqttsdk.HmAgent;
import com.heiman.mqttsdk.http.HmHttpManage;
import com.heiman.mqttsdk.listtner.HmConnectDevice;
import com.heiman.mqttsdk.listtner.HmGatewayListener;
import com.heiman.mqttsdk.listtner.HmNetListener;
import com.heiman.mqttsdk.manage.HmDeviceManage;
import com.heiman.mqttsdk.modle.HmDevice;
import com.heiman.mqttsdk.modle.HmEPlug;
import com.heiman.mqttsdk.modle.HmFourLight;
import com.heiman.mqttsdk.modle.HmGatewayInfo;
import com.heiman.mqttsdk.modle.HmOnoff;
import com.heiman.mqttsdk.modle.HmPlug;
import com.heiman.mqttsdk.modle.HmRelay;
import com.heiman.mqttsdk.modle.HmRgb;
import com.heiman.mqttsdk.modle.HmSSBase;
import com.heiman.mqttsdk.modle.HmSoundLightAlarm;
import com.heiman.mqttsdk.modle.HmSubDevice;
import com.heiman.mqttsdk.modle.HmThp;
import com.heiman.mqttsdk.modle.HmTimer;
import com.heiman.utils.Convert;
import com.heiman.utils.HmUtils;
import com.hss01248.dialog.ActivityStackManager;
import com.hss01248.dialog.StyledDialog;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static com.heiman.mqttsdk.HmMQTTConnectionConstants.STATE_CONNECTED;
import static com.heiman.mqttsdk.HmMQTTConnectionConstants.STATE_CONNECTION_FAILED;
import static com.heiman.mqttsdk.HmMQTTConnectionConstants.STATE_NONE;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/11/17 下午5:51
 * @Description : 海曼应用程序
 * @Modify record :
 */
public class HmApplication extends Application implements HmNetListener, HmGatewayListener {
    public static final boolean ENCRYPTED = true;
    /**
     * 首选项设置
     * Preferences set
     */
    public static SharedPreferences sharedPreferences;

    private static Login login;
    public static HmApplication instances;

    @Override
    public void onCreate() {
        super.onCreate();
        HmAgent.getInstance().init(this);
        HmAgent.getInstance().addHmListener(this);
        instances = this;
        sharedPreferences = getSharedPreferences("HmOffciaDemo", Context.MODE_PRIVATE);
        init();
        initLiteSDK();
        initDevice();
        StyledDialog.init(this);
        registCallback();
        CrashReport.initCrashReport(getApplicationContext(), "f46a72b1b7", true);
    }

    private void registCallback() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityStackManager.getInstance().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ActivityStackManager.getInstance().removeActivity(activity);
            }
        });
    }

    private void initLiteSDK() {
        LitePal.initialize(this);
        SQLiteDatabase db = Connector.getDatabase();
    }

    private void initDevice() {
        List<Device> devices = DataSupport.findAll(Device.class);
        for (Device device : devices) {
            DeviceManage.getInstance().addDeviceNoDb(device);
            HmAgent.getInstance().initDevice(device.getHmDeviceSDK());
        }
    }

    public static HmApplication getInstances() {
        return instances;
    }

    public static void setInstances(HmApplication instances) {
        HmApplication.instances = instances;
    }

    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void init() {
        JPushInterface.init(getApplicationContext());
    }

    @Override
    public void onStart(int code) {
        Logger.i("onStart:" + code);
    }

    @Override
    public void onLogin(int code) {
        Logger.i("onStart:" + code);
        switch (code) {
            case STATE_NONE:

                break;
            case STATE_CONNECTED:
                List<Device> deviceList = DeviceManage.getInstance().getDevices();
                for (Device device : deviceList) {
                    HmAgent.getInstance().initDevice(device.getHmDeviceSDK());
                    HmAgent.getInstance().connectDevice(device.getHmDeviceSDK(), new HmConnectDevice() {
                        @Override
                        public void onConnectDevice(HmDevice hmDevice, int code) {
                            Logger.e("hmDevice:" + code);
                        }
                    });
                }
                break;
            case STATE_CONNECTION_FAILED:
                Logger.e("HmApplication:服务器连接失败!");
                break;
        }
    }

    @Override
    public void onLocalDisconnect(int code) {
        Logger.i("onStart:" + code);
    }

    @Override
    public void onDisconnect(int code) {
        Logger.i("onStart:" + code);
    }

    @Override
    public void onRecvPipeData(HmDevice hmDevice, byte flags, byte[] data) {
        Logger.i("onRecvPipeData:" + hmDevice.getDeviceMac() + "\n" + HmUtils.getHexBinString(data));
    }

    @Override
    public void onDeviceStateChanged(HmDevice hmDevice, int state) {
        Logger.i("onDeviceStateChanged:" + hmDevice.getDeviceMac() + "\n" + state);
    }

    @Override
    public void onDataPointUpdate(HmDevice hmDevice, byte[] data) {
        Logger.i("onRecvPipeData:" + hmDevice.getDeviceMac() + "\n" + HmUtils.getHexBinString(data));

    }


    @Override
    public void onDeviceSubListChange(HmDevice hmDevice, byte[] data) {
        Logger.i("onDeviceStateChanged:" + hmDevice.getDeviceMac() + "\n" + HmUtils.getHexBinString(data));
    }

    @Override
    public void onDeviceInfo(HmDevice hmDevice) {
        Device device = DeviceManage.getInstance().getDevice(hmDevice.getDeviceMac());
        device.setHmDevice(hmDevice);
        DeviceManage.getInstance().addDevice(device);
        sendPipeBroad(Constant.DEVICE_INFO, hmDevice, hmDevice.toString());
    }

    @Override
    public void onSubDeviceInfo(HmDevice hmDevice, HmSubDevice hmSubDevice) {
        sendPipeBroad(Constant.DEVICE_INFO, hmDevice, hmSubDevice.toString());
    }

    @Override
    public void onGatewayInfo(HmDevice hmDevice, HmGatewayInfo hmGatewayInfo) {
        sendPipeBroad(Constant.GATEWAT_INFO, hmDevice,hmGatewayInfo.toString());
    }

    @Override
    public void onAddSubDevice(HmDevice hmDevice, boolean enbale) {
        sendPipeBroad(Constant.ADD_SUB_DEVICE, hmDevice, enbale + "");
    }


    @Override
    public void ondeleteSubDevice(HmDevice hmDevice, int index) {
        sendPipeBroad(Constant.DELETE_SUB, hmDevice, index + "");
    }

    @Override
    public void onUpDatairmware(HmDevice hmDevice, int fType, boolean enbale, int type) {
        sendPipeBroad(Constant.UPDATAIRMWARE, hmDevice, "fType:" + fType + "enbale:" + enbale + "type:" + type);
    }

    @Override
    public void onGatewaySubOnlie(HmDevice hmDevice, int index) {
        sendPipeBroad(Constant.GATEWAY_SUB_ONLIE, hmDevice, index + "");
    }

    @Override
    public void onSubSSBase(HmDevice hmDevice, HmSubDevice hmSubDevice, HmSSBase hmSSBase) {
        sendPipeBroad(Constant.SUB_SS_BASE, hmDevice, hmSSBase.getJsonString());
    }

    @Override
    public void onRgbSetting(HmDevice hmDevice, int index, HmRgb hmRgb) {
        sendPipeBroad(Constant.RGB_SETTING, hmDevice, hmRgb.toString());
    }

    @Override
    public void onThpSetting(HmDevice hmDevice, int index, HmThp hmThp) {
        sendPipeBroad(Constant.THP_SETTING, hmDevice, hmThp.toString());
    }

    @Override
    public void onPlugSetting(HmDevice hmDevice, int index, HmPlug hmPlug) {
        sendPipeBroad(Constant.PLUG_SETTING, hmDevice, hmPlug.getJsonString());
    }

    @Override
    public void onEPlugSetting(HmDevice hmDevice, int index, HmEPlug hmEPlug) {
        sendPipeBroad(Constant.EPLUG_SETTING, hmDevice, hmEPlug.toString());
    }

    @Override
    public void onOnoffSetting(HmDevice hmDevice, int index, HmOnoff hmOnoff) {
        sendPipeBroad(Constant.ONOFF_SETTING, hmDevice, hmOnoff.toString());
    }

    @Override
    public void onfourLightSetting(HmDevice hmDevice, int index, HmFourLight hmFourLight) {
        sendPipeBroad(Constant.FOUR_LIGH_SETTING, hmDevice, hmFourLight.toString());
    }

    @Override
    public void onSoundLightAlarmSetting(HmDevice hmDevice, int index, HmSoundLightAlarm hmSoundLightAlarm) {
        sendPipeBroad(Constant.LIGH_ALARM_SETTING, hmDevice, hmSoundLightAlarm.toString());
    }

    @Override
    public void onRelay(HmDevice hmDevice, int index, HmRelay hmRelay) {
        sendPipeBroad(Constant.LIGH_ALARM_SETTING, hmDevice, "设备index：" + index + "数据：" + hmRelay.toString());
    }

    @Override
    public void onWashSubList(HmDevice hmDevice, List<Integer> subIndexList) {
        sendPipeBroad(Constant.LIGH_ALARM_SETTING, hmDevice, Convert.toJson(subIndexList));
    }

    @Override
    public void onNightLight(HmDevice hmDevice, boolean isEN, List<Integer> subIndexList) {
        sendPipeBroad(Constant.LIGH_ALARM_SETTING, hmDevice, "使能：" + isEN + "数据：" + Convert.toJson(subIndexList));
    }

    @Override
    public void onAlarmTimer(HmDevice hmDevice, int type, HmTimer hmTimer) {
        sendPipeBroad(Constant.LIGH_ALARM_SETTING, hmDevice, "类型：" + type + "数据：" + hmTimer.toString());
    }

    @Override
    public void onAllLightOnoff(HmDevice hmDevice, int onoff) {
        sendPipeBroad(Constant.LIGH_ALARM_SETTING, hmDevice, onoff + "");
    }

    @Override
    public void onError(HmDevice hmDevice, byte[] data) {
        sendPipeBroad(Constant.ERROR, hmDevice, HmUtils.getHexBinString(data));
    }

    @Override
    public void onSLLinkAlarm(HmDevice hmDevice, HmSubDevice hmSubDevice, int en, List<Integer> indexList, List<Integer> alarmTypeList) {

    }

    /**
     * 发送广播
     *
     * @param action
     *         广播动作
     * @param device
     *         设备
     * @param data
     *         广播数据
     */
    public void sendPipeBroad(String action, HmDevice device, String data) {
        Intent intent = new Intent(action);
        intent.putExtra(Constant.DEVICE_MAC, device.getDeviceMac());
        if (data != null) {
            intent.putExtra(Constant.DATA, data);
        }
        HmApplication.this.sendBroadcast(intent);
    }

    public static Login getLogin() {
        return login;
    }

    public static void setLogin(Login login) {
        HmApplication.login = login;
    }
}
