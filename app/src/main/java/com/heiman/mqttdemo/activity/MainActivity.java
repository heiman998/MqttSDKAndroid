package com.heiman.mqttdemo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.heiman.mqttdemo.Constant;
import com.heiman.mqttdemo.Manage.DeviceManage;
import com.heiman.mqttdemo.R;
import com.heiman.mqttdemo.adpter.DeviceAdpter;
import com.heiman.mqttdemo.back.Dialogback;
import com.heiman.mqttdemo.base.Device;
import com.heiman.mqttdemo.base.DeviceBase;
import com.heiman.mqttdemo.base.DeviceList;
import com.heiman.mqttdemo.base.SubDeviceList;
import com.heiman.mqttdemo.util.SharedPreferencesUtil;
import com.heiman.mqttsdk.HmAgent;
import com.heiman.mqttsdk.HmCode;
import com.heiman.mqttsdk.HmConstant;
import com.heiman.mqttsdk.http.HmHttpManage;
import com.heiman.mqttsdk.listtner.HmPublishListener;
import com.heiman.mqttsdk.listtner.HmStart;
import com.heiman.mqttsdk.manage.HmDeviceManage;
import com.heiman.mqttsdk.manage.HmSubDeviceManage;
import com.heiman.mqttsdk.modle.HmDevice;
import com.heiman.mqttsdk.modle.HmDeviceType;
import com.heiman.mqttsdk.modle.HmSubDevice;
import com.heiman.utils.HmUtils;
import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.hss01248.dialog.interfaces.MyItemDialogListener;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int GET_SUB_DEVICE = 1000;
    private static final int SUB_DEVICE_LIST = 1002;
    private DeviceAdpter mAdpter;
    private ArrayList<DeviceBase> baseArrayList;
    private ListView mListViewDevice;
    public static boolean isForeground = false;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private Button btnAddDevice;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HmAgent.getInstance().start(new HmStart() {
            @Override
            public void onStart(int code) {
                if (code == HmCode.SERVER_START) {
                    HmAgent.getInstance().connect(HmConstant.host, HmConstant.port,
                            "AndroidMqttSDK", "AndroidMqttSDK", UUID.randomUUID().toString());
                }
            }
        });
        mListViewDevice = (ListView) findViewById(R.id.mListViewDevice);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        btnAddDevice = findViewById(R.id.btn_add_device);
        btnAddDevice.setOnClickListener(this);
        baseArrayList = new ArrayList<>();
        mAdpter = new DeviceAdpter(this, baseArrayList);
        mListViewDevice.setAdapter(mAdpter);
        mListViewDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceBase deviceBase = baseArrayList.get(i);
                Bundle paramBundle = new Bundle();
                paramBundle.putString(Constant.DEVICE_MAC, deviceBase.getDeviceMac());
                if (!HmUtils.isEmptyString(deviceBase.getSubMac())) {
                    paramBundle.putString(Constant.DEVICE_SUB_MAC, deviceBase.getSubMac());
                }
                if (deviceBase.getSubIndex() != 0) {
                    paramBundle.putInt(Constant.DEVICE_SUB_INDEX, deviceBase.getSubIndex());
                }
                openActivity(DeviceActivity.class, paramBundle);
            }
        });
        mListViewDevice.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceBase deviceBase = baseArrayList.get(i);
                showDeviceDialog(deviceBase, i);
                return true;
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                baseArrayList = new ArrayList<>();
                getDevice();
            }
        });
        getDevice();
        XGPushConfig.enableDebug(this, true);
        XGPushConfig.getToken(this);
        String email = SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID);
        /*
         注册信鸽服务的接口
         如果仅仅需要发推送消息调用这段代码即可
         */
        XGPushManager.registerPush(getApplicationContext(), email, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Logger.w(Constants.LogTag, "+++ register push sucess. token:" + data + "flag" + flag);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Logger.w(Constants.LogTag, "+++ register push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg);
            }
        });
    }

    /**
     * 显示弹出框
     *
     * @param deviceBase
     *         设备
     * @param i
     */
    private void showDeviceDialog(final DeviceBase deviceBase, final int i) {

        final List<String> strings = new ArrayList<>();
        strings.add("修改名字");
        strings.add("删除设备");
        strings.add("取消");
        StyledDialog.buildIosSingleChoose(strings, new MyItemDialogListener() {
            @Override
            public void onItemClick(CharSequence text, int position) {
                StyledDialog.dismissLoading();
                switch (position) {
                    case 0:
                        showChangeName(deviceBase, i);
                        break;
                    case 1:
                        showDeleteDevice(deviceBase, i);
                        break;
                }
            }

            @Override
            public void onBottomBtnClick() {

            }
        }).show();

    }

    private void showChangeName(final DeviceBase deviceBase, final int i) {
        String name = "";
        final HmDevice hmDevice = HmDeviceManage.getInstance().getDevice(deviceBase.getDeviceMac());
        name = hmDevice.getDeviceName();
        if (!HmUtils.isEmptyString(deviceBase.getSubMac())) {
            HmSubDevice hmSubDevice = HmSubDeviceManage.getInstance().getDevice(deviceBase.getDeviceMac(), deviceBase.getSubIndex());
            if (hmSubDevice != null) {
                if (!HmUtils.isEmptyString(hmSubDevice.getDeviceName())) {
                    name = hmSubDevice.getDeviceName();
                } else {
                    name = hmSubDevice.getDeviceMac();
                }
            } else {
                return;
            }
        }
        StyledDialog.buildNormalInput("修改名称" + name, "请输入设备名称", "",
                "确认", "取消", new MyDialogListener() {
                    @Override
                    public void onFirst() {
                    }

                    @Override
                    public void onSecond() {

                    }

                    @Override
                    public boolean onInputValid(final CharSequence input1, CharSequence input2, EditText editText1, EditText editText2) {
                        Logger.e("input1--input2:" + input1 + "--" + input2 + "is not accepted!");
                        StyledDialog.dismiss();
                        if (!HmUtils.isEmptyString(input1.toString())) {
                            if (!HmUtils.isEmptyString(deviceBase.getSubMac())) {
                                final HmSubDevice hmSubDevice = HmSubDeviceManage.getInstance().getDevice(deviceBase.getDeviceMac(), deviceBase.getSubIndex());
                                HmHttpManage.getInstance().onChangeSubDevice(input1.toString(), hmDevice.getDeviceID(), hmSubDevice, new Dialogback<Object>(MainActivity.this) {
                                    @Override
                                    public void onSuccess(Response<Object> response) {
                                        Logger.i(response.message());
                                        Logger.i(response.code() + "");
                                        Logger.i(response.getException() + "");
                                        hmSubDevice.setDeviceName(input1.toString());
                                        HmSubDeviceManage.getInstance().addDevice(hmSubDevice);
                                        deviceBase.setDeviceName(input1.toString());
                                        baseArrayList.remove(i);
                                        baseArrayList.add(i, deviceBase);
                                        mAdpter.notifyDataSetChanged();

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
                            } else {

                                HmHttpManage.getInstance().onChangeDevice(input1.toString(), hmDevice.getDeviceID(), new Dialogback<Object>(MainActivity.this) {
                                    @Override
                                    public void onSuccess(Response<Object> response) {
                                        Logger.i(response.message());
                                        Logger.i(response.code() + "");
                                        Logger.i(response.getException() + "");
                                        hmDevice.setDeviceName(input1.toString());
                                        HmDeviceManage.getInstance().addDevice(hmDevice);
                                        Device device = DeviceManage.getInstance().getDevice(hmDevice.getDeviceMac());
                                        device.setHmDevice(hmDevice);
                                        DeviceManage.getInstance().addDevice(device);
                                        deviceBase.setDeviceName(input1.toString());
                                        baseArrayList.remove(i);
                                        baseArrayList.add(i, deviceBase);
                                        mAdpter.notifyDataSetChanged();

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
                        }
                        return false;
                    }

                    @Override
                    public void onGetInput(CharSequence input1, CharSequence input2) {
                        super.onGetInput(input1, input2);
                        Logger.e("input1:" + input1 + "--input2:" + input2);
                    }
                })
                .setInput2HideAsPassword(false)
                .setCancelable(true, true)
                .show();
    }

    private void showDeleteDevice(final DeviceBase deviceBase, final int i) {
        String name = "";
        final HmDevice hmDevice = HmDeviceManage.getInstance().getDevice(deviceBase.getDeviceMac());
        name = hmDevice.getDeviceName();
        if (!HmUtils.isEmptyString(deviceBase.getSubMac())) {
            HmSubDevice hmSubDevice = HmSubDeviceManage.getInstance().getDevice(deviceBase.getDeviceMac(), deviceBase.getSubIndex());
            if (!HmUtils.isEmptyString(hmSubDevice.getDeviceName())) {
                name = hmSubDevice.getDeviceName();
            }
        }
        StyledDialog.buildIosAlert("", "删除设备" + name, new MyDialogListener() {
            @Override
            public void onFirst() {

            }

            @Override
            public void onSecond() {
                if (!HmUtils.isEmptyString(deviceBase.getSubMac())) {
                    final HmSubDevice hmSubDevice = HmSubDeviceManage.getInstance().getDevice(deviceBase.getDeviceMac(), deviceBase.getSubIndex());
                    HmAgent.getInstance().deleteSubDevice(hmDevice, hmSubDevice, new HmPublishListener() {
                        @Override
                        public void onPulishData(HmDevice hmDevice, int code) {
                            if (code != HmCode.SUCCEED) {
                                Logger.e("code:" + code);
                            } else {
                                HmSubDeviceManage.getInstance().removeDevice(hmSubDevice.getDeviceMac());
                                baseArrayList.remove(i);
                                mAdpter.notifyDataSetChanged();
                            }
                        }
                    });
                } else {
                    HmHttpManage.getInstance().onUnSubscribeDevice(hmDevice.getDeviceID(), new Dialogback<Object>(MainActivity.this) {
                        @Override
                        public void onSuccess(Response<Object> response) {
                            HmDeviceManage.getInstance().removeDevice(hmDevice.getDeviceMac());
                            DeviceManage.getInstance().removeDevice(hmDevice.getDeviceMac());
                            baseArrayList.remove(i);
                            mAdpter.notifyDataSetChanged();

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
            }

            @Override
            public void onThird() {

            }
        }).setBtnText("取消", "确认")
                .setBtnColor(R.color.dialogutil_text_black, R.color.colorPrimaryDark, 0)
                .show();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_SUB_DEVICE: {
                    final DeviceList deviceList = (DeviceList) msg.obj;
                    Observable<DeviceList.ListBean> sender = Observable.create(new Observable.OnSubscribe<DeviceList.ListBean>() {

                        @Override
                        public void call(Subscriber<? super DeviceList.ListBean> subscriber) {
                            for (DeviceList.ListBean listBean : deviceList.getList()) {
                                subscriber.onNext(listBean);
                            }
                            subscriber.onCompleted();
                        }
                    });
                    Observer<DeviceList.ListBean> receiver = new Observer<DeviceList.ListBean>() {

                        @Override
                        public void onCompleted() {
                            //数据接收完成时调用
                            mAdpter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            //发生错误调用
                        }

                        @Override
                        public void onNext(DeviceList.ListBean value) {
                            final Device device = value.listToDevice();
                            DeviceManage.getInstance().addDevice(device);
                            DeviceBase deviceBase = new DeviceBase();
                            deviceBase.setDeviceMac(value.getMac());
                            if (!HmUtils.isEmptyString(value.getName())) {
                                deviceBase.setDeviceName(value.getName());
                            } else {
                                deviceBase.setDeviceName(value.getMac());
                            }
                            deviceBase.setOnline(value.isOnline());
                            baseArrayList.add(deviceBase);
                            HmHttpManage.getInstance().getSubDevices(value.getId(), "", "", new Dialogback<SubDeviceList>(MainActivity.this) {
                                @Override
                                public void onSuccess(Response<SubDeviceList> response) {
                                    Logger.i(response.message());
                                    Logger.i(response.body().toString());
                                    Logger.i(response.code() + "");
                                    for (SubDeviceList.ListBean listBean : response.body().getList()) {
                                        HmSubDevice hmSubDevice = HmSubDeviceManage.getInstance().getDevice(device.getMac(), listBean.getSubIndex());
                                        if (hmSubDevice == null) {
                                            hmSubDevice = new HmSubDevice();
                                        }
                                        hmSubDevice.setIndex(listBean.getSubIndex());
                                        hmSubDevice.setDeviceMac(listBean.getMac());
                                        hmSubDevice.setDeviceType(listBean.getProductId());
                                        hmSubDevice.setOnline(listBean.isIsOnline());
                                        hmSubDevice.setHmDevice(device.getHmDeviceSDK());
                                        HmSubDeviceManage.getInstance().addDevice(hmSubDevice);
                                        DeviceBase deviceBase = new DeviceBase();
                                        deviceBase.setDeviceMac(device.getMac());
                                        deviceBase.setSubMac(listBean.getMac());
                                        deviceBase.setSubIndex(listBean.getSubIndex());
                                        deviceBase.setOnline(listBean.isIsOnline());
                                        if (!HmUtils.isEmptyString(listBean.getName())) {
                                            deviceBase.setDeviceName(listBean.getName());
                                        } else {
                                            deviceBase.setDeviceName(listBean.getMac());
                                        }
                                        baseArrayList.add(deviceBase);
                                    }
                                    mAdpter.notifyDataSetChanged();
                                }

                                @Override
                                public void onError(Response<SubDeviceList> response) {

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
                }
                break;
            }
        }
    };

    private void getDevice() {
        HmHttpManage.getInstance().getDevices("", "", new Dialogback<DeviceList>(this) {
            @Override
            public void onSuccess(Response<DeviceList> response) {
                Logger.i(response.message());
                Logger.i(response.body().toString());
                Logger.i(response.code() + "");
                Message message = new Message();
                message.obj = response.body();
                message.what = GET_SUB_DEVICE;
                mHandler.sendMessage(message);
            }

            @Override
            public void onError(Response<DeviceList> response) {

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
