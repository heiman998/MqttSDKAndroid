package com.heiman.mqttdemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.heiman.mqttdemo.Constant;
import com.heiman.mqttdemo.HmApplication;
import com.heiman.mqttdemo.Manage.DeviceManage;
import com.heiman.mqttdemo.R;
import com.heiman.mqttdemo.adpter.DeviceAdpter;
import com.heiman.mqttdemo.back.Dialogback;
import com.heiman.mqttdemo.base.Device;
import com.heiman.mqttsdk.HmAgent;
import com.heiman.mqttsdk.HmCode;
import com.heiman.mqttsdk.HmConstant;
import com.heiman.mqttsdk.http.HmHttpManage;
import com.heiman.mqttsdk.listtner.HmStart;
import com.heiman.mqttsdk.manage.HmDeviceManage;
import com.heiman.mqttsdk.modle.HmDevice;
import com.heiman.mqttsdk.modle.HmDeviceType;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private DeviceAdpter mAdpter;
    private ArrayList<HmDevice> hmDevices;
    private ListView mListViewDevice;
    public static boolean isForeground = false;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private Button btnAddDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         HmAgent.getInstance().start(new HmStart() {
            @Override
            public void onStart(int code) {
                if (code == HmCode.SERVER_START) {
                    HmAgent.getInstance().connect(HmConstant.host, HmConstant.port,
                            "AndroidMqttSDK", "AndroidMqttSDK",
                            "U_" + HmApplication.getLogin().getAccess_token());
                }
            }
        });
        getDevice();
        mListViewDevice = (ListView) findViewById(R.id.mListViewDevice);
        btnAddDevice = findViewById(R.id.btn_add_device);
        btnAddDevice.setOnClickListener(this);
        hmDevices = new ArrayList<>();
        HmDevice hmDevice = new HmDevice();
        Device device = new Device();
//        hmDevice.setDeviceMac("845DD767D976");
        hmDevice.setDeviceMac("845DD76814C6");
        hmDevice.setPid(10000);
        hmDevice.setFactoryID(1000);
        hmDevice.setAcckey("bd17df6d548211e7");
        HmAgent.getInstance().initDevice(hmDevice);
        device.setHmDevice(hmDevice);
        device.setMac("845DD76814C6");
        DeviceManage.getInstance().addDevice(device);
        hmDevices = HmDeviceManage.getInstance().getDevices();
        mAdpter = new DeviceAdpter(this, hmDevices);
        mListViewDevice.setAdapter(mAdpter);
        mListViewDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HmDevice hmDevice = hmDevices.get(i);
                Bundle paramBundle = new Bundle();
                paramBundle.putString(Constant.DEVICE_MAC, hmDevice.getDeviceMac());
                openActivity(DeviceActivity.class, paramBundle);
            }
        });
    }

    private void getDevice() {
        HmHttpManage.getInstance().getDevices("", "", new Dialogback<Object>(this) {
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
    }

    @Override
    protected void onDestroy() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_device:

                Bundle paramBundle = new Bundle();
                paramBundle.putInt(Constant.TYPE, HmDeviceType.DEVICE_WIFI_GATEWAY_HS1GW_NEW.getValue());
                openActivity(SmarLinkActivity.class, paramBundle);
                break;
        }
    }
}
