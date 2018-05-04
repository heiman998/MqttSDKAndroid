package com.heiman.mqttdemo.base;

import com.google.gson.annotations.Expose;
import com.heiman.mqttdemo.Manage.DeviceManage;
import com.heiman.mqttsdk.modle.HmDevice;
import com.heiman.mqttsdk.modle.HmDeviceType;
import com.heiman.utils.HmUtils;

import java.util.List;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/12/29 下午2:10
 * @Description :
 * @Modify record :
 */
public class DeviceList extends Code {

    /**
     * list : [{"id":18611,"mac":"845DD7681AD3","productId":10000,"mcuVersion":1,"role":"deviceadmin","source":1,"authority":true}]
     * count : 1
     * limit : 100
     * offset : 0
     */
    @Expose
    private int count;
    @Expose
    private int limit;
    @Expose
    private int offset;
    @Expose
    private List<ListBean> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 18611
         * mac : 845DD7681AD3
         * productId : 10000
         * mcuVersion : 1
         * role : deviceadmin
         * source : 1
         * authority : true
         */
        @Expose
        private int id;
        @Expose
        private String mac;
        @Expose
        private int productId;
        @Expose
        private int mcuVersion;
        @Expose
        private String role;
        @Expose
        private int source;
        @Expose
        private boolean authority;
        @Expose
        private String name;
        @Expose
        private boolean isOnline;

        public boolean isOnline() {
            return isOnline;
        }

        public void setOnline(boolean online) {
            isOnline = online;
        }

        public Device listToDevice() {
            Device device = DeviceManage.getInstance().getDevice(mac);
            HmDevice hmDevice;
            if (device == null) {
                device = new Device();
                hmDevice = new HmDevice();
            } else {
                hmDevice = device.getHmDeviceSDK();
            }
            device.setMac(mac);
            hmDevice.setDeviceID(id);
            hmDevice.setDeviceMac(mac);
            hmDevice.setPid(productId);
            if (isOnline) {
                hmDevice.setDeviceStates(1);
            } else {
                hmDevice.setDeviceStates(0);
            }
            if (productId == 10000) {
                hmDevice.setType(HmDeviceType.DEVICE_WIFI_GATEWAY_HS1GW_NEW.getValue());
                hmDevice.setAcckey("bd17df6d548211e7");
            }
            if (!HmUtils.isEmptyString(name)) {
                hmDevice.setDeviceName(name);
            }
            if (isOnline) {
                hmDevice.setDeviceStates(1);
            } else {
                hmDevice.setDeviceStates(0);
            }
            device.setHmDevice(hmDevice);
            return device;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getMcuVersion() {
            return mcuVersion;
        }

        public void setMcuVersion(int mcuVersion) {
            this.mcuVersion = mcuVersion;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public boolean isAuthority() {
            return authority;
        }

        public void setAuthority(boolean authority) {
            this.authority = authority;
        }
    }

    @Override
    public String toString() {
        return "DeviceList{" +
                "count=" + count +
                ", limit=" + limit +
                ", offset=" + offset +
                ", list=" + list +
                '}';
    }
}
