package com.heiman.mqttdemo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.heiman.mqttdemo.Constant;
import com.heiman.mqttdemo.Manage.DeviceManage;
import com.heiman.mqttdemo.R;
import com.heiman.mqttdemo.back.Dialogback;
import com.heiman.mqttdemo.base.Device;
import com.heiman.mqttsdk.http.HmHttpManage;
import com.heiman.mqttsdk.modle.DeviceBean;
import com.heiman.mqttsdk.modle.HmDevice;
import com.heiman.mqttsdk.modle.SmartLink;
import com.heiman.mqttsdk.smartlinkutils.ConfigCallback;
import com.heiman.mqttsdk.smartlinkutils.Smartlink;
import com.heiman.mqttsdk.smartlinkutils.SmartlinkFactory;
import com.heiman.utils.HmUtils;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/12/2 上午10:48
 * @Description :
 * @Modify record :
 */
public class SmarLinkActivity extends BaseActivity implements View.OnClickListener {


    private TextView tvwifiname;
    private EditText etwifipass;
    private Button btnstart;
    private Smartlink smartlink;
    private boolean isStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartlink);
        this.btnstart = (Button) findViewById(R.id.btn_start);
        this.etwifipass = (EditText) findViewById(R.id.et_wifi_pass);
        this.tvwifiname = (TextView) findViewById(R.id.tv_wifi_name);
        btnstart.setOnClickListener(this);
        Bundle bundle = this.getIntent().getExtras();
        int deviceType = bundle.getInt(Constant.TYPE);
        tvwifiname.setText("Wifi名称:" + HmUtils.getSSid(this));
        smartlink = SmartlinkFactory.getSmartlink(this, deviceType, configCallback);
    }

    private final static int MSG_SMARTLINK_FAIL = 10004;
    private final static int MSG_SMARTLINK_SUCCEED = 10000;
    private final static int UPDATE_FIRMWARE_FAIL_TIME = 150 * 1000;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SMARTLINK_SUCCEED: {
                    if (msg.obj == null || !(msg.obj instanceof SmartLink)) {
                        return;
                    }
                    SmartLink link = (SmartLink) msg.obj;
                    smartlink.stopConfig();
                    smartlink.setConfigCallback(null);
                    smartlink.release();
                    Logger.e("TEST", "IP:" + link.getDevice().getDeviceIP() + "MAC:" + link.getDevice().getMacAddress());
                    Toast.makeText(SmarLinkActivity.this, "配置成功！" + link.getDevice().getMacAddress(), Toast.LENGTH_SHORT).show();
                    HmDevice hmDevice = new HmDevice();
                    Device device = new Device();
                    device.setMac(link.getDevice().getMacAddress());
                    hmDevice.setDeviceMac(link.getDevice().getMacAddress());
                    if (!HmUtils.isEmptyString(link.getDevice().getDeviceIP())) {
                        hmDevice.setDeviceIP(link.getDevice().getDeviceIP());
                    }
                    hmDevice.setPid(10000);
                    hmDevice.setFactoryID(1000);
                    device.setHmDevice(hmDevice);
                    DeviceManage.getInstance().addDevice(device);
                    HmHttpManage.getInstance().onRegisterDevice(hmDevice.getPid()+"", hmDevice.getDeviceMac(), "1", "1", hmDevice.getDeviceMac(), new Dialogback<Object>(SmarLinkActivity.this) {
                        @Override
                        public void onSuccess(Response<Object> response) {
                            Logger.i(response.message());
                            Logger.i(response.body().toString());
                            Logger.i(response.code() + "");
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
                    finish();
                    break;
                }
                case MSG_SMARTLINK_FAIL:
                    smartlink.stopConfig();
                    smartlink.setConfigCallback(null);
                    isStart = true;
                    btnstart.setText("开始配网");
                    etwifipass.setFocusable(true);
                    etwifipass.setFocusableInTouchMode(true);
                    etwifipass.requestFocus();
                    Toast.makeText(SmarLinkActivity.this, "配置失败！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (isStart) {
                    mHandler.sendEmptyMessageDelayed(MSG_SMARTLINK_FAIL, UPDATE_FIRMWARE_FAIL_TIME);
                    try {
                        smartlink.setConfigCallback(configCallback);
                        smartlink.startConfig(HmUtils.getSSid(this), getBssId(), etwifipass.getText().toString(), UPDATE_FIRMWARE_FAIL_TIME);
                        isStart = false;
                        btnstart.setText("停止配置");
                        etwifipass.setFocusable(false);
                        etwifipass.clearFocus();
                        etwifipass.setFocusableInTouchMode(false);

                        final View focusView = etwifipass;

                        if (focusView != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            assert imm != null;
                            boolean b = imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                            focusView.clearFocus();
                        }

                    } catch (Exception e) {
                        mHandler.removeMessages(MSG_SMARTLINK_FAIL);
                        mHandler.sendEmptyMessage(MSG_SMARTLINK_FAIL);
                    }
                } else {
                    smartlink.stopConfig();
                    smartlink.setConfigCallback(null);
                    isStart = true;
                    btnstart.setText("开始配网");
                    etwifipass.setFocusable(true);
                    etwifipass.setFocusableInTouchMode(true);
                    etwifipass.requestFocus();
                }
                break;
        }
    }

    private ConfigCallback configCallback = new ConfigCallback() {
        @Override
        public void onConfigSucceed(String ip, String mac) {
            Logger.e("ip:" + ip + "\tmac:" + mac);
            Message msg = new Message();
            msg.what = MSG_SMARTLINK_SUCCEED;
            SmartLink link = new SmartLink();
            link.setDevice(new DeviceBean());
            link.getDevice().setMacAddress(mac);
            link.getDevice().setDeviceIP(ip);
            link.getDevice().setDeviceName("");
            msg.obj = link;
            mHandler.sendMessage(msg);
            mHandler.removeMessages(MSG_SMARTLINK_FAIL);
        }

        @Override
        public void onErr() {
            mHandler.removeMessages(MSG_SMARTLINK_FAIL);
            mHandler.sendEmptyMessage(MSG_SMARTLINK_FAIL);
        }
    };

    /**
     * 获得bssId
     *
     * @return bssId
     */
    public String getBssId() {
        String bssId = "";
        try {
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
            if (null != info) {
                if (!TextUtils.isEmpty(info.getBSSID())) {
                    bssId = info.getBSSID();
                }
            }
        } catch (Exception e) {
            Logger.e(e == null ? "" : e.getMessage());
            return bssId;
        }
        Logger.e("bssId:" + bssId);
        return bssId;
    }
}
