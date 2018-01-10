package com.heiman.mqttdemo.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.heiman.mqttdemo.Constant;
import com.heiman.mqttdemo.R;
import com.heiman.mqttdemo.back.Dialogback;
import com.heiman.mqttdemo.base.HttpOtaVersion;
import com.heiman.mqttsdk.HmAgent;
import com.heiman.mqttsdk.HmCode;
import com.heiman.mqttsdk.HmConstant;
import com.heiman.mqttsdk.http.HmHttpManage;
import com.heiman.mqttsdk.listtner.HmPublishListener;
import com.heiman.mqttsdk.manage.HmDeviceManage;
import com.heiman.mqttsdk.modle.HmComOID;
import com.heiman.mqttsdk.modle.HmCurtainController;
import com.heiman.mqttsdk.modle.HmDevice;
import com.heiman.mqttsdk.modle.HmEPlug;
import com.heiman.mqttsdk.modle.HmFourLight;
import com.heiman.mqttsdk.modle.HmGatewayInfo;
import com.heiman.mqttsdk.modle.HmIntelligentDoorLock;
import com.heiman.mqttsdk.modle.HmOnoff;
import com.heiman.mqttsdk.modle.HmPlug;
import com.heiman.mqttsdk.modle.HmRelay;
import com.heiman.mqttsdk.modle.HmRgb;
import com.heiman.mqttsdk.modle.HmSoundLightAlarm;
import com.heiman.mqttsdk.modle.HmSubDevice;
import com.heiman.mqttsdk.modle.HmThp;
import com.heiman.mqttsdk.modle.HmTimer;
import com.heiman.mqttsdk.modle.OTAVersion;
import com.heiman.utils.HmUtils;
import com.lzy.okgo.model.Response;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.orhanobut.logger.Logger;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

public class DeviceActivity extends BaseActivity implements View.OnClickListener {

    private static final int MESSAGE_UP_DATA = 1;
    private static final int SEND_OTA_DEVICE = 1002;
    private Button btnclear;
    private Button btnstopscroll;
    private Button btnstopread;
    private Button btnsendset;
    private Button btnsendget;
    private TextView logtext;
    private ScrollView scroll;
    private boolean isRegisterBroadcast = false;
    private boolean isRead = true;
    private boolean isScroll = true;
    private EditText etdata;
    private Button btnsend;

    private boolean onoff = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        this.btnsend = (Button) findViewById(R.id.btn_send);
        this.etdata = (EditText) findViewById(R.id.et_data);
        this.scroll = (ScrollView) findViewById(R.id.scroll);
        this.logtext = (TextView) findViewById(R.id.log_text);
        this.btnsendset = (Button) findViewById(R.id.btn_send_set);
        this.btnsendget = (Button) findViewById(R.id.btn_send_get);
        this.btnstopread = (Button) findViewById(R.id.btn_stop_read);
        this.btnstopscroll = (Button) findViewById(R.id.btn_stop_scroll);
        this.btnclear = (Button) findViewById(R.id.btn_clear);

        btnsendset.setOnClickListener(this);
        btnsendget.setOnClickListener(this);
        btnstopread.setOnClickListener(this);
        btnclear.setOnClickListener(this);
        btnstopscroll.setOnClickListener(this);
        btnsend.setOnClickListener(this);

        btnstopread.setText("停止接收");
        btnstopscroll.setText("停止滚动");
        initFilter();
    }

    /**
     * 注册广播
     */
    private void initFilter() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constant.ADD_SUB_DEVICE);
        myIntentFilter.addAction(Constant.DELETE_SUB);
        myIntentFilter.addAction(Constant.UPDATAIRMWARE);
        myIntentFilter.addAction(Constant.GATEWAY_SUB_ONLIE);
        myIntentFilter.addAction(Constant.SUB_SS_BASE);
        myIntentFilter.addAction(Constant.RGB_SETTING);
        myIntentFilter.addAction(Constant.THP_SETTING);
        myIntentFilter.addAction(Constant.PLUG_SETTING);
        myIntentFilter.addAction(Constant.EPLUG_SETTING);
        myIntentFilter.addAction(Constant.ONOFF_SETTING);
        myIntentFilter.addAction(Constant.FOUR_LIGH_SETTING);
        myIntentFilter.addAction(Constant.LIGH_ALARM_SETTING);
        myIntentFilter.addAction(Constant.GATEWAT_INFO);
        myIntentFilter.addAction(Constant.DEVICE_INFO);
        myIntentFilter.addAction(Constant.OTHER);
        myIntentFilter.addAction(Constant.ERROR);
        isRegisterBroadcast = true;
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisterBroadcast) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }


    /**
     * 监听的广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (HmUtils.isEmptyString(action)) {
                return;
            }
            String mac = intent.getStringExtra(Constant.DEVICE_MAC);
            if (HmUtils.isEmptyString(mac)) {
                return;
            }
            HmDevice hmDevice = HmDeviceManage.getInstance().getDevice(mac);
            if (hmDevice == null) {
                Logger.e("device == null");
                return;
            }

            if (!mac.equals(hmDevice.getDeviceMac())) {
                return;
            }
            String data = intent.getStringExtra(Constant.DATA);
            if (HmUtils.isEmptyString(data)) {
                return;
            }
            if (!isRead) {
                return;
            }
            Message message = new Message();
            message.what = MESSAGE_UP_DATA;
            message.obj = data;
            mHandler.sendMessage(message);

        }
    };


    private final Handler handler = new Handler();

    public void Log(String log) {
        if (logtext == null) {
            return;
        }
        if (logtext.length() > 6000) {
            String temptext = logtext.getText().toString();
            temptext = temptext.substring(temptext.length() - 2000,
                    temptext.length());
            logtext.setText(temptext);
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        log.replace("  ", " ");
        String dateString = formatter.format(new Date());
        logtext.append(dateString + ":");
        logtext.append("\n");
        logtext.append(log);
        logtext.append("\n");
        handler.post(new Runnable() {

            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        Logger.i(log);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clear:
                //TODO implement
                logtext.setText("");
                break;
            case R.id.btn_stop_scroll:
                //TODO implement
                isScroll = !isScroll;
                if (isScroll) {
                    btnstopscroll.setText("停止滚动");
                } else {
                    btnstopscroll.setText("启动滚动");
                }
                break;
            case R.id.btn_stop_read:
                //TODO implement
                isRead = !isRead;
                if (isRead) {
                    btnstopread.setText("停止接收");
                } else {
                    btnstopread.setText("启动接收");
                }
                break;
            case R.id.btn_send_get:
                //TODO implement
                if (isSub) {
                    HmAgent.getInstance().getData(hmDevice, hmSubDevice, HmComOID.GW_GET_SUB_SS, hmPublishListener);
                } else {
                    showSendGetList();
                }
                break;
            case R.id.btn_send_set:
                //TODO implement
                if (isSub) {
                    if (hmSubDevice == null) {
                        return;
                    }
                    switch (hmSubDevice.toDeviceType()) {
                        case DEVICE_ZIGBEE_RGB: {
                            HmRgb hmRgb = new HmRgb();
                            hmRgb.setBrightness(80);
                            hmRgb.setColorB(100);
                            hmRgb.setColorR(120);
                            hmRgb.setColorG(130);
                            int onoff1 = 0;
                            if (onoff) {
                                onoff1 = 1;
                            }
                            onoff = !onoff;
                            hmRgb.setOnoff(onoff1);
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmRgb, hmPublishListener);
                            Log("发送：" + hmRgb.toString());
                        }
                        break;
                        case DEVICE_ZIGBEE_ONE_ONOFF: {
                            HmOnoff hmOnoff = new HmOnoff();
                            int onoff1 = 0;
                            if (onoff) {
                                onoff1 = 1;
                            }
                            onoff = !onoff;
                            hmOnoff.setOnoff1(onoff1);
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmOnoff, hmPublishListener);
                            Log("发送：" + hmOnoff.toString());
                        }
                        break;
                        case DEVICE_ZIGBEE_TWO_ONOFF: {
                            HmOnoff hmOnoff = new HmOnoff();
                            int onoff1 = 0;
                            if (onoff) {
                                onoff1 = 1;
                            }
                            onoff = !onoff;
                            hmOnoff.setOnoff1(onoff1);
                            hmOnoff.setOnoff2(onoff1);
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmOnoff, hmPublishListener);
                            Log("发送：" + hmOnoff.toString());
                        }
                        break;
                        case DEVICE_ZIGBEE_THREE_ONOFF: {
                            HmOnoff hmOnoff = new HmOnoff();
                            int onoff1 = 0;
                            if (onoff) {
                                onoff1 = 1;
                            }
                            onoff = !onoff;
                            hmOnoff.setWf1(245);
                            hmOnoff.setsDay1(29);
                            hmOnoff.setsMonth1(12);
                            hmOnoff.setsHour1(10);
                            hmOnoff.setsMinutes1(10);
                            hmOnoff.seteDay1(12);
                            hmOnoff.seteMonth1(10);
                            hmOnoff.seteHour1(12);
                            hmOnoff.seteMinutes1(20);
                            hmOnoff.setOnoff1(onoff1);
                            hmOnoff.setOnoff2(onoff1);
                            hmOnoff.setOnoff3(onoff1);
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmOnoff, hmPublishListener);
                            Log("发送：" + hmOnoff.toString());
                        }
                        break;
                        case DEVICE_ZIGBEE_RC: {

                        }
                        break;
                        case DEVICE_ZIGBEE_RELAY: {
                            HmRelay hmRelay = new HmRelay();
                            int onoff1 = 0;
                            if (onoff) {
                                onoff1 = 1;
                            }
                            onoff = !onoff;
                            hmRelay.setOnoff(onoff1);
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmRelay, hmPublishListener);
                            Log("发送：" + hmRelay.toString());
                        }
                        break;
                        case DEVICE_ZIGBEE_DIMIMMER_SWITCH: {
                        }
                        break;
                        case DEVICE_ZIGBEE_LAMP_HOLDER: {
                        }
                        break;
                        case DEVICE_ZIGBEE_SITUATION_SWITCH: {
                        }
                        break;
                        case DEVICE_ZIGBEE_FOUR_LIGHT: {
                            HmFourLight hmFourLight = new HmFourLight();
                            hmFourLight.setBrightness1(100);
                            int onoff1 = 0;
                            if (onoff) {
                                onoff1 = 1;
                            }
                            onoff = !onoff;
                            hmFourLight.setOnoff2(onoff1);
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmFourLight, hmPublishListener);
                            Log("发送：" + hmFourLight.toString());
                        }
                        break;
                        case DEVICE_ZIGBEE_DOORS: {
                        }
                        break;
                        case DEVICE_ZIGBEE_WATER: {
                        }
                        break;
                        case DEVICE_ZIGBEE_PIR: {
                        }
                        break;
                        case DEVICE_ZIGBEE_SMOKE: {
                        }
                        break;
                        case DEVICE_ZIGBEE_THP: {
                            HmThp hmThp = new HmThp();
                            hmThp.setTempEnbale(false);
                            hmThp.setTempLow(20);
                            hmThp.setHumEnbale(true);
                            hmThp.setHumLow(80);
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmThp, hmPublishListener);
                            Log("发送：" + hmThp.toString());
                        }
                        break;
                        case DEVICE_ZIGBEE_GAS: {
                        }
                        break;
                        case DEVICE_ZIGBEE_SOUND_AND_LIGHT_ALARM: {
                            HmSoundLightAlarm hmSoundLightAlarm = new HmSoundLightAlarm();
                            hmSoundLightAlarm.setAlarmTimer(10);
                            int onoff1 = 0;
                            if (onoff) {
                                onoff1 = 1;
                            }
                            onoff = !onoff;
                            hmSoundLightAlarm.setOnoff(onoff1);
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmSoundLightAlarm, hmPublishListener);
                            Log("发送：" + hmSoundLightAlarm.toString());
                        }
                        break;
                        case DEVICE_ZIGBEE_CO: {
                        }
                        break;
                        case DEVICE_ZIGBEE_ILLUMINANCE: {
                        }
                        break;
                        case DEVICE_ZIGBEE_AIR: {
                        }
                        break;
                        case DEVICE_ZIGBEE_THERMOSTAT: {
                        }
                        break;
                        case DEVICE_ZIGBEE_SHOCK: {
                        }
                        break;
                        case DEVICE_ZIGBEE_SOS: {
                        }
                        break;
                        case DEVICE_ZIGBEE_SW: {
                        }
                        break;
                        case DEVICE_ZIGBEE_PLUGIN: {
                            HmPlug hmPlug = new HmPlug();
                            int onoff1 = 0;
                            if (onoff) {
                                onoff1 = 1;
                            }
                            onoff = !onoff;
                            hmPlug.setUsbonoff(onoff1);
                            hmPlug.setPoweronoff(onoff1);
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmPlug, hmPublishListener);
                            Log("发送：" + hmPlug.getJsonString());
                        }
                        break;
                        case DEVICE_ZIGBEE_METRTING_PLUGIN: {
                            HmEPlug hmEPlug = new HmEPlug();
                            int onoff1 = 0;
                            if (onoff) {
                                onoff1 = 1;
                            }
                            onoff = !onoff;
                            hmEPlug.setOnoff(onoff1);
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmEPlug, hmPublishListener);
                            Log("发送：" + hmEPlug.toString());
                        }
                        break;
                        case DEVICE_ZIGBEE_CURTAIN_CONTROLLER: {
                            HmCurtainController hmCurtainController = new HmCurtainController();
                            hmCurtainController.setMA(80);
                            int onoff1 = 0;
                            if (onoff) {
                                onoff1 = 1;
                            }
                            onoff = !onoff;
                            hmCurtainController.setOnoff(onoff1);
                            Log("发送：" + hmCurtainController.toString());
                            HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmCurtainController, hmPublishListener);
                        }
                        break;
                        case DEVICE_ZIGBEE_INTELLIGENT_DOOR_LOCK: {
                        }
                        break;
                    }
                } else {
                    showSendSetList();
                }
                break;
            case R.id.btn_send:
                sendData();
                break;
        }
    }

    /**
     * 发送透传自定义数据
     */
    private void sendData() {
        String data = etdata.getText().toString();
        HmAgent.getInstance().sendHexStringData(hmDevice, data, hmPublishListener);
    }

    /**
     * 设置列表
     */
    private void showSendSetList() {
        final String[] items = {"设置网关信息", "设置时区", "添加子设备", "设备升级", "设置网关ip", "设置在家布防定时", "设置一键开关灯"};
        new CircleDialog.Builder(this)
                .configDialog(new ConfigDialog() {
                    @Override
                    public void onConfig(DialogParams params) {
                        //增加弹出动画
//                        params.animStyle = R.style.dialogWindowAnim;
                    }
                })
                .setTitle("设置设备属性")
                .setTitleColor(Color.BLUE)
                .setItems(items, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                HmGatewayInfo hmGatewayInfo = new HmGatewayInfo();
                                hmGatewayInfo.setArmtype(2);
                                hmGatewayInfo.setLgtimer(10);
                                hmGatewayInfo.setGwlightonoff(1);
                                hmGatewayInfo.setGwlightlevel(88);
                                hmGatewayInfo.setGwlanguage(1);
                                hmGatewayInfo.setBetimer(10);
                                hmGatewayInfo.setSoundlevel(89);
                                Log("发送:" + hmGatewayInfo.toString());
                                HmAgent.getInstance().setGwInfo(hmDevice, hmGatewayInfo, hmPublishListener);
                                break;
                            case 1:
                                String TimerZone = "+03.30";
                                HmAgent.getInstance().setTimerZone(hmDevice, TimerZone, hmPublishListener);
                                Log("发送:" + TimerZone);
                                break;
                            case 2:
                                HmAgent.getInstance().addSubDevice(hmDevice, true, hmPublishListener);
                                break;
                            case 3:
                                OTAVersion otaVersion = new OTAVersion();
                                List<String> father = new ArrayList<>();
                                father.add(hmDevice.getDeviceID() + "");
                                otaVersion.setFather(father);
                                HmHttpManage.getInstance().getOtaVersion(otaVersion, new Dialogback<List<HttpOtaVersion>>(DeviceActivity.this) {
                                    @Override
                                    public void onSuccess(Response<List<HttpOtaVersion>> response) {
                                        Logger.i(response.message());
                                        Logger.i(response.body().toString());
                                        Logger.i(response.code() + "");
                                        if (HmUtils.isEmptyList(response.body())) {
                                            Log("未发现升级固件");
                                            return;
                                        }
                                        Message message = new Message();
                                        message.obj = response.body();
                                        message.what = SEND_OTA_DEVICE;
                                        mHandler.sendMessage(message);
                                    }

                                    @Override
                                    public void onError(Response<List<HttpOtaVersion>> response) {

                                        if (response != null) {
                                            Logger.i(response.message());
                                            Logger.i(response.code() + "");
                                            Logger.i(response.getException() + "");
                                        }
                                    }
                                });

                                break;
                            case 4:
                                HmAgent.getInstance().setDeviceIp(hmDevice, HmConstant.host, hmPublishListener);
                                break;
                            case 5:
                                HmTimer hmTimer = new HmTimer();
                                hmTimer.setWf(245);
                                hmTimer.setDay(29);
                                hmTimer.setMonth(12);
                                hmTimer.setHour(10);
                                hmTimer.setMinutes(10);
                                Log("发送:" + hmTimer.toString());
                                HmAgent.getInstance().setAlarmTimer(hmDevice, 1, hmTimer, hmPublishListener);
                                break;
                            case 6:
                                int onoff1 = 0;
                                if (onoff) {
                                    onoff1 = 1;
                                }
                                onoff = !onoff;
                                Log("发送:" + onoff);
                                HmAgent.getInstance().setLightOnoff(hmDevice, onoff1, hmPublishListener);
                                break;
                        }
                    }
                })
                .setNegative("取消", null)
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        //取消按钮字体颜色
                        params.textColor = Color.RED;
                    }
                })
                .show();
    }

    /**
     * 获取列表
     */
    private void showSendGetList() {
        final String[] items = {"获取网关信息", "获取秘钥", "获取基本信息"};
        new CircleDialog.Builder(this)
                .configDialog(new ConfigDialog() {
                    @Override
                    public void onConfig(DialogParams params) {
                        //增加弹出动画
//                        params.animStyle = R.style.dialogWindowAnim;
                    }
                })
                .setTitle("获取设备属性")
                .setTitleColor(Color.BLUE)
                .setItems(items, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                HmAgent.getInstance().getData(hmDevice, HmComOID.GW_GET_BASIC, hmPublishListener);
                                Log("获取:获取网关信息");
                                break;
                            case 1:
                                HmAgent.getInstance().getData(hmDevice, HmComOID.GW_GET_AES_KEY, hmPublishListener);
                                Log("获取:获取秘钥");
                                break;
                            case 2:
                                HmAgent.getInstance().getData(hmDevice, HmComOID.GW_GET_DEVICE_BASIC_INFORMATION, hmPublishListener);
                                Log("获取:获取基本信息");
                                break;
                        }
                    }
                })
                .setNegative("取消", null)
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        //取消按钮字体颜色
                        params.textColor = Color.RED;
                    }
                })
                .show();
    }


    HmPublishListener hmPublishListener = new HmPublishListener() {
        @Override
        public void onPulishData(HmDevice hmDevice, int code) {
            if (code != HmCode.SUCCEED) {
                Logger.e("code:" + code);
                Log("发送失败：" + code);
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_OTA_DEVICE:
                    final List<HttpOtaVersion> httpOtaVersionList = (List<HttpOtaVersion>) msg.obj;
                    Observable<HttpOtaVersion> sender = Observable.create(new Observable.OnSubscribe<HttpOtaVersion>() {

                        @Override
                        public void call(Subscriber<? super HttpOtaVersion> subscriber) {
                            for (HttpOtaVersion httpOtaVersion : httpOtaVersionList) {
                                subscriber.onNext(httpOtaVersion);
                            }
                            subscriber.onCompleted();
                        }
                    });
                    Observer<HttpOtaVersion> receiver = new Observer<HttpOtaVersion>() {

                        @Override
                        public void onCompleted() {
                            //数据接收完成时调用

                        }

                        @Override
                        public void onError(Throwable e) {
                            //发生错误调用
                        }

                        @Override
                        public void onNext(HttpOtaVersion value) {
                            HmHttpManage.getInstance().onOTAConfirm(value.getOtaId(), hmDevice.getDeviceID(), new Dialogback<Object>(DeviceActivity.this) {
                                @Override
                                public void onSuccess(Response<Object> response) {
                                    Logger.i(response.message());
                                    Logger.i(response.body().toString());
                                    Logger.i(response.code() + "");
                                    HmAgent.getInstance().upDatairmware(hmDevice, 1, true, 0, hmPublishListener);
                                }

                                @Override
                                public void onError(Response<Object> response) {

                                    if (response != null) {
                                        Logger.i(response.message());
                                        Logger.i(response.code() + "");
                                        Logger.i(response.getException() + "");
                                    }
                                }
                            });
                        }
                    };
                    sender.subscribe(receiver);
                    break;
                case MESSAGE_UP_DATA:
                    String data = (String) msg.obj;
                    Log("接收:" + data);
                    break;
            }
        }
    };
}
