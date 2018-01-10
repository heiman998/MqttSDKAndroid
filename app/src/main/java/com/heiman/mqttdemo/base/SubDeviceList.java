package com.heiman.mqttdemo.base;

import java.util.List;

/**
 * @Author : 肖力 by mac
 * @Time :  2018/1/4 下午3:20
 * @Description :
 * @Modify record :
 */
public class SubDeviceList {

    /**
     * list : [{"id":18576,"mac":"98301201008d1500","isOnline":true,"subIndex":11572,"productId":28},{"id":18577,"mac":"f4fe1201008d1500","isOnline":true,"subIndex":12601,"productId":20,"lastData":"2017-12-27 11:14:47"},{"id":18579,"mac":"d0d0e70b006f0d00","isOnline":true,"subIndex":7802,"productId":49,"lastData":"2017-12-15 17:16:53"},{"id":18580,"mac":"8fb7e70b006f0d00","isOnline":true,"subIndex":28102,"productId":68,"lastData":"2017-12-16 15:43:11"},{"id":18581,"mac":"6ab3e70b006f0d00","isOnline":true,"subIndex":24073,"productId":19,"lastData":"2018-01-03 08:27:25"},{"id":18582,"mac":"59732b01008d1500","isOnline":true,"subIndex":8305,"productId":0},{"id":18583,"mac":"642d1201008d1500","isOnline":true,"subIndex":28600,"productId":50,"lastData":"2017-12-18 10:24:25"},{"id":18584,"mac":"7e943803c9435000","isOnline":true,"subIndex":7974,"productId":17,"lastData":"2017-12-20 14:21:39"},{"id":18585,"mac":"082f1201008d1500","isOnline":true,"subIndex":12801,"productId":21,"lastData":"2017-12-15 16:35:37"},{"id":18586,"mac":"d078d30e006f0d00","isOnline":true,"subIndex":16604,"productId":19,"lastData":"2017-12-15 14:12:12"},{"id":18587,"mac":"15943803c9435000","isOnline":true,"subIndex":6772,"productId":18,"lastData":"2017-12-21 14:48:59"},{"id":18588,"mac":"b32e1201008d1500","isOnline":true,"subIndex":5404,"productId":22},{"id":18589,"mac":"4e45db01008d1500","isOnline":true,"subIndex":8400,"productId":0,"lastData":"2017-12-15 16:17:32"},{"id":18590,"mac":"26e3d20e006f0d00","isOnline":true,"subIndex":22904,"productId":10,"lastData":"2017-12-15 15:18:12"},{"id":18591,"mac":"36001301008d1500","isOnline":true,"subIndex":13600,"productId":68,"lastData":"2017-12-15 18:31:27"},{"id":18593,"mac":"842e1201008d1500","isOnline":true,"subIndex":26705,"productId":17,"lastData":"2017-12-16 15:45:12"},{"id":18594,"mac":"19913f0d006f0d00","isOnline":true,"subIndex":5104,"productId":68,"lastData":"2017-12-16 15:44:42"},{"id":18595,"mac":"135a1201008d1500","isOnline":true,"subIndex":4903,"productId":4,"lastData":"2017-12-16 15:44:18"},{"id":18596,"mac":"ec591201008d1500","isOnline":true,"subIndex":4673,"productId":50,"lastData":"2017-12-27 11:24:22"},{"id":18597,"mac":"571231a3c9435000","isOnline":true,"subIndex":9772,"productId":17,"lastData":"2017-12-27 15:00:50"},{"id":18598,"mac":"965e2e1fc9435000","isOnline":true,"subIndex":16672,"productId":67},{"id":18599,"mac":"1b10e80b006f0d00","isOnline":true,"subIndex":24872,"productId":20},{"id":18600,"mac":"1b10e80b006f0d00","isOnline":true,"subIndex":10772,"productId":18,"lastData":"2017-12-20 11:25:11"},{"id":18601,"mac":"1350e60b006f0d00","isOnline":true,"subIndex":23400,"productId":23,"lastData":"2017-12-26 17:19:25"},{"id":18614,"mac":"73bde70b006f0d00","isOnline":true,"subIndex":3873,"productId":24}]
     * count : 25
     * limit : 100
     * offset : 0
     */

    private int count;
    private int limit;
    private int offset;
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
         * id : 18576
         * mac : 98301201008d1500
         * isOnline : true
         * subIndex : 11572
         * productId : 28
         * lastData : 2017-12-27 11:14:47
         */

        private int id;
        private String mac;
        private boolean isOnline;
        private int subIndex;
        private int productId;
        private String lastData;
        private String name;

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

        public boolean isIsOnline() {
            return isOnline;
        }

        public void setIsOnline(boolean isOnline) {
            this.isOnline = isOnline;
        }

        public int getSubIndex() {
            return subIndex;
        }

        public void setSubIndex(int subIndex) {
            this.subIndex = subIndex;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getLastData() {
            return lastData;
        }

        public void setLastData(String lastData) {
            this.lastData = lastData;
        }
    }

    @Override
    public String toString() {
        return "SubDeviceList{" +
                "count=" + count +
                ", limit=" + limit +
                ", offset=" + offset +
                ", list=" + list +
                '}';
    }
}
