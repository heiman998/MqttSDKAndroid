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
import com.heiman.mqttsdk.HmAgent;
import com.heiman.mqttsdk.HmCode;
import com.heiman.mqttsdk.HmConstant;
import com.heiman.mqttsdk.listtner.HmPublishListener;
import com.heiman.mqttsdk.manage.HmDeviceManage;
import com.heiman.mqttsdk.modle.HmComOID;
import com.heiman.mqttsdk.modle.HmDevice;
import com.heiman.mqttsdk.modle.HmGatewayInfo;
import com.heiman.mqttsdk.modle.HmRgb;
import com.heiman.mqttsdk.modle.HmSubDevice;
import com.heiman.utils.HmUtils;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceActivity extends BaseActivity implements View.OnClickListener {

    private static final int MESSAGE_UP_DATA = 1;
    private Button btnclear;
    private Button btnstopscroll;
    private Button btnstopread;
    private Button btnsendset;
    private Button btnsendget;
    private LinearLayout lin;
    private TextView logtext;
    private ScrollView scroll;
    private RelativeLayout logview;
    private boolean isRegisterBroadcast = false;
    private boolean isRead = true;
    private boolean isScroll = true;
    private EditText etdata;
    private Button btnsend;
    private LinearLayout sendlin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        this.sendlin = (LinearLayout) findViewById(R.id.send_lin);
        this.btnsend = (Button) findViewById(R.id.btn_send);
        this.etdata = (EditText) findViewById(R.id.et_data);
        this.logview = (RelativeLayout) findViewById(R.id.log_view);
        this.scroll = (ScrollView) findViewById(R.id.scroll);
        this.logtext = (TextView) findViewById(R.id.log_text);
        this.lin = (LinearLayout) findViewById(R.id.lin);
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

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UP_DATA:
                    String data = (String) msg.obj;
                    Log("接收:" + data);
                    break;
            }
            super.handleMessage(msg);
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
                showSendGetList();
                break;
            case R.id.btn_send_set:
                //TODO implement
                showSendSetList();
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
        final String[] items = {"设置网关信息", "设置时区", "设置子设备信息", "添加子设备", "设备升级", "设置网关ip"};
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
                        byte[] data = null;
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
                                Logger.i(hmGatewayInfo.toString());
                                HmAgent.getInstance().setGwInfo(hmDevice, hmGatewayInfo, hmPublishListener);
                                break;
                            case 1:
                                HmAgent.getInstance().setTimerZone(hmDevice, "+03.30", hmPublishListener);
                                break;
                            case 2:
                                HmRgb hmRgb = new HmRgb();
                                hmRgb.setBrightness(100);
                                hmRgb.setColorB(255);
                                hmRgb.setColorR(255);
                                hmRgb.setColorG(255);
                                hmRgb.setOnoff(1);
                                Logger.i(hmRgb.toString());
                                HmSubDevice hmSubDevice = new HmSubDevice();
                                hmSubDevice.setDeviceType(1);
                                hmSubDevice.setIndex(1);
                                HmAgent.getInstance().setSubsetting(hmDevice, hmSubDevice, hmRgb, hmPublishListener);
                                break;
                            case 3:
                                HmAgent.getInstance().addSubDevice(hmDevice, true, hmPublishListener);
                                break;
                            case 4:
                                HmAgent.getInstance().upDatairmware(hmDevice, 1, true, 0, hmPublishListener);
                                break;
                            case 5:
                                HmAgent.getInstance().setDeviceIp(hmDevice, HmConstant.host, hmPublishListener);
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
                                break;
                            case 1:
                                HmAgent.getInstance().getData(hmDevice, HmComOID.GW_GET_AES_KEY,hmPublishListener);
                                break;
                            case 2:
                                HmAgent.getInstance().getData(hmDevice, HmComOID.GW_GET_DEVICE_BASIC_INFORMATION, hmPublishListener);
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


    HmPublishListener hmPublishListener =new HmPublishListener() {
        @Override
        public void onPulishData(HmDevice hmDevice, int code) {
            if (code != HmCode.SUCCEED) {
                Logger.e("code:" + code);
                Log("发送失败：" + code);
            }
        }
    };

}
