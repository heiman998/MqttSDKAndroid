package com.heiman.mqttdemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.heiman.mqttdemo.Manage.DeviceManage;
import com.heiman.mqttdemo.base.Device;
import com.heiman.mqttdemo.base.Login;
import com.heiman.mqttsdk.HmAgent;
import com.heiman.mqttsdk.listtner.HmConnectDevice;
import com.heiman.mqttsdk.listtner.HmGatewayListener;
import com.heiman.mqttsdk.listtner.HmNetListener;
import com.heiman.mqttsdk.modle.HmDevice;
import com.heiman.mqttsdk.modle.HmEPlug;
import com.heiman.mqttsdk.modle.HmFourLight;
import com.heiman.mqttsdk.modle.HmGatewayInfo;
import com.heiman.mqttsdk.modle.HmOnoff;
import com.heiman.mqttsdk.modle.HmPlug;
import com.heiman.mqttsdk.modle.HmRelay;
import com.heiman.mqttsdk.modle.HmRgb;
import com.heiman.mqttsdk.modle.HmSoundLightAlarm;
import com.heiman.mqttsdk.modle.HmThp;
import com.heiman.mqttsdk.modle.HmTimer;
import com.heiman.utils.Convert;
import com.heiman.utils.HmUtils;
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


    //    private DaoMaster.DevOpenHelper mHelper;
//    private SQLiteDatabase db;
//    private DaoMaster mDaoMaster;
//    private DaoSession mDaoSession;

    public static HmApplication instances;

    @Override
    public void onCreate() {
        super.onCreate();
        HmAgent.getInstance().init(this);
        HmAgent.getInstance().addHmListener(this);
        instances = this;
        sharedPreferences = getSharedPreferences("HmOffciaDemo", Context.MODE_PRIVATE);
        init();
        setDatabase();
        initLiteSDK();
        initDevice();
        CrashReport.initCrashReport(getApplicationContext(), "f46a72b1b7", true);

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

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
//        mHelper = new DaoMaster.DevOpenHelper(this, DeviceDao.TABLENAME+".db", null);
//        db = mHelper.getWritableDatabase();
//        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
//        mDaoMaster = new DaoMaster(db);
//        mDaoSession = mDaoMaster.newSession();
    }

//    public DaoSession getDaoSession() {
//        return mDaoSession;
//    }

//    public SQLiteDatabase getDb() {
//        return db;
//    }

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
        if (code == STATE_NONE) {

        } else if (code == STATE_CONNECTED) {
            HmDevice hmDevice = new HmDevice();
            hmDevice.setDeviceMac("845DD76814C6");
            hmDevice.setPid(10000);
            hmDevice.setFactoryID(1000);
            hmDevice.setAcckey("bd17df6d548211e7");
            HmAgent.getInstance().initDevice(hmDevice);
            HmAgent.getInstance().connectDevice(hmDevice, new HmConnectDevice() {
                @Override
                public void onConnectDevice(HmDevice hmDevice, int code) {
                    Logger.e("hmDevice:" + code);
                }
            });
        } else if (code == STATE_CONNECTION_FAILED) {
            Logger.e("HmApplication:服务器连接失败!");
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
        sendPipeBroad(Constant.DEVICE_INFO, hmDevice, Convert.toJson(hmDevice));
    }

    @Override
    public void onGatewayInfo(HmDevice hmDevice, HmGatewayInfo hmGatewayInfo) {
        sendPipeBroad(Constant.GATEWAT_INFO, hmDevice, Convert.toJson(hmGatewayInfo));
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
        sendPipeBroad(Constant.UPDATAIRMWARE, hmDevice, "");
    }

    @Override
    public void onGatewaySubOnlie(HmDevice hmDevice, int index) {
        sendPipeBroad(Constant.GATEWAY_SUB_ONLIE, hmDevice, index + "");
    }

    @Override
    public void onRgbSetting(HmDevice hmDevice, int index, HmRgb hmRgb) {
        sendPipeBroad(Constant.RGB_SETTING, hmDevice, Convert.toJson(hmRgb));
    }

    @Override
    public void onThpSetting(HmDevice hmDevice, int index, HmThp hmThp) {
        sendPipeBroad(Constant.THP_SETTING, hmDevice, Convert.toJson(hmThp));
    }

    @Override
    public void onPlugSetting(HmDevice hmDevice, int index, HmPlug hmPlug) {
        sendPipeBroad(Constant.PLUG_SETTING, hmDevice, Convert.toJson(hmPlug));
    }

    @Override
    public void onEPlugSetting(HmDevice hmDevice, int index, HmEPlug hmEPlug) {
        sendPipeBroad(Constant.EPLUG_SETTING, hmDevice, Convert.toJson(hmEPlug));
    }

    @Override
    public void onOnoffSetting(HmDevice hmDevice, int index, HmOnoff hmOnoff) {
        sendPipeBroad(Constant.ONOFF_SETTING, hmDevice, Convert.toJson(hmOnoff));
    }

    @Override
    public void onfourLightSetting(HmDevice hmDevice, int index, HmFourLight hmFourLight) {
        sendPipeBroad(Constant.FOUR_LIGH_SETTING, hmDevice, Convert.toJson(hmFourLight));
    }

    @Override
    public void onSoundLightAlarmSetting(HmDevice hmDevice, int index, HmSoundLightAlarm hmSoundLightAlarm) {
        sendPipeBroad(Constant.LIGH_ALARM_SETTING, hmDevice, Convert.toJson(hmSoundLightAlarm));
    }

    @Override
    public void onRelay(HmDevice hmDevice, int index, HmRelay hmRelay) {
        sendPipeBroad(Constant.LIGH_ALARM_SETTING, hmDevice, "设备index：" + index + "数据：" + Convert.toJson(hmRelay));
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
        sendPipeBroad(Constant.LIGH_ALARM_SETTING, hmDevice, "类型：" + type + "数据：" + Convert.toJson(hmTimer));
    }

    @Override
    public void onAllLightOnoff(HmDevice hmDevice, int onoff) {
        sendPipeBroad(Constant.LIGH_ALARM_SETTING, hmDevice, onoff + "");
    }

    @Override
    public void onError(HmDevice hmDevice, byte[] data) {
        sendPipeBroad(Constant.ERROR, hmDevice, HmUtils.getHexBinString(data));
    }

    /**
     * 发送广播
     *
     * @param action 广播动作
     * @param device 设备
     * @param data   广播数据
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
