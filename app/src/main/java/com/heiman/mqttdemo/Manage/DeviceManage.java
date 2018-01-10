package com.heiman.mqttdemo.Manage;
/**
 * Copyright ©深圳市海曼科技有限公司
 */

import android.text.TextUtils;

import com.heiman.mqttdemo.base.Device;
import com.heiman.mqttsdk.HmAgent;
import com.orhanobut.logger.Logger;

import org.litepal.crud.DataSupport;
import org.litepal.crud.callback.SaveCallback;
import org.litepal.crud.callback.UpdateOrDeleteCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author :    肖力
 * @Email : 554674787@qq.com
 * @Phone : 18312377810
 * @Time :  2017/3/22 14:23
 * @Description :
 */
public class DeviceManage {

    private static DeviceManage instance;
    public static ConcurrentHashMap<String, Device> deviceMap = new ConcurrentHashMap<String, Device>();

    public static DeviceManage getInstance() {
        if (instance == null) {
            instance = new DeviceManage();
        }
        return instance;
    }

    public static List<Device> listDev = new ArrayList<Device>();

    /**
     * 获取所有设备
     *
     * @return
     */
    public synchronized List<Device> getDevices() {
        listDev.clear();
        Iterator<Map.Entry<String, Device>> iter = deviceMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Device> entry = iter.next();
            listDev.add(entry.getValue());
        }
        return listDev;
    }

    /**
     * 获取单个设备
     *
     * @param mac
     * @return
     */
    public Device getDevice(String mac) {
        if (TextUtils.isEmpty(mac)) {
            Logger.e("mac is empty!");
            return null;
        }
        return deviceMap.get(mac);
    }

    /**
     * 添加设备
     *
     * @param dev
     */
    public void addDevice(final Device dev) {
        if (dev == null) {
            return;
        }
        deviceMap.put(dev.getMac(), dev);
        dev.saveOrUpdateAsync("mac = ?", dev.getMac()).listen(new SaveCallback() {
            @Override
            public void onFinish(boolean rowsAffected) {
                Logger.i("更新设备：" + rowsAffected + "\n" + dev.getMac());
            }
        });
    }

    /**
     * 添加设备
     *
     * @param dev
     */
    public void addDeviceNoDb(Device dev) {

        if (dev == null) {
            return;
        }

        deviceMap.put(dev.getMac(), dev);
    }

    /**
     * 更新数据
     *
     * @param device
     */
    public void updateDevice(final Device device) {
        deviceMap.remove(device.getMac());
        deviceMap.put(device.getMac(), device);
        device.updateAllAsync("mac = ?", device.getMac()).listen(new UpdateOrDeleteCallback() {
            @Override
            public void onFinish(int rowsAffected) {
                Logger.i("更新设备：" + device.getMac());
            }
        });
    }

    /**
     * 删除设备
     *
     * @param mac
     */
    public void removeDevice(String mac) {
        Device xlinkDevice = getDevice(mac);
        if (xlinkDevice == null) {
            return;
        }
        deviceMap.remove(mac);
        DataSupport.deleteAllAsync(Device.class, "mac = ?", mac).listen(new UpdateOrDeleteCallback() {
            @Override
            public void onFinish(int rowsAffected) {
                Logger.i("删除设备：" + rowsAffected);
            }
        });
        HmAgent.getInstance().removeDevice(mac);
    }

    /**
     * 清空设备
     */
    public synchronized void clearAllDevice() {
        DataSupport.deleteAllAsync(Device.class).listen(new UpdateOrDeleteCallback() {
            @Override
            public void onFinish(int rowsAffected) {
                Logger.i("删除设备：" + rowsAffected);
            }
        });
        deviceMap.clear();
        listDev.clear();
        HmAgent.getInstance().removeAllDevice();
    }
}
