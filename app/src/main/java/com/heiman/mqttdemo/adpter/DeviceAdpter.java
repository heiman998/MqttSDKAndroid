package com.heiman.mqttdemo.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heiman.mqttdemo.R;
import com.heiman.mqttdemo.base.DeviceBase;
import com.heiman.utils.HmUtils;

import java.util.ArrayList;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/11/27 下午2:39
 * @Description :
 * @Modify record :
 */
public class DeviceAdpter extends BaseAdapter {

    private ArrayList<DeviceBase> hmDeviceList = new ArrayList<DeviceBase>();

    private Context context;
    private LayoutInflater layoutInflater;

    public DeviceAdpter(Context context, ArrayList<DeviceBase> hmDeviceList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.hmDeviceList = hmDeviceList;
    }

    @Override
    public int getCount() {
        return hmDeviceList.size();
    }

    @Override
    public DeviceBase getItem(int position) {
        return hmDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_device, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews((DeviceBase) getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    @SuppressLint("SetTextI18n")
    private void initializeViews(DeviceBase hmDevice, ViewHolder holder) {
        //TODO implement
        if (hmDevice != null) {
            if (!HmUtils.isEmptyString(hmDevice.getDeviceName())) {
                holder.tvName.setText("设备名称：" + hmDevice.getDeviceName());
            }
            if (!HmUtils.isEmptyString(hmDevice.getDeviceMac())) {
                holder.tvMac.setText("设备mac：" + hmDevice.getDeviceMac());
            }
            if (!HmUtils.isEmptyString(hmDevice.getSubMac())) {
                holder.tvMac.setText("设备mac：" + hmDevice.getSubMac());
            }
            if (hmDevice.isOnline()) {
                holder.tvOnline.setText("设备在线");
                holder.tvOnline.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                holder.tvOnline.setText("设备离线");
                holder.tvOnline.setTextColor(context.getResources().getColor(R.color.viewfinder_laser));
            }
        }
    }

    protected class ViewHolder {
        private TextView tvName;
        private TextView tvMac;
        private TextView tvOnline;

        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvMac = (TextView) view.findViewById(R.id.tv_mac);
            tvOnline = (TextView) view.findViewById(R.id.tv_online);
        }
    }
}

