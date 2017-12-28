package com.heiman.mqttdemo.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heiman.mqttdemo.R;
import com.heiman.mqttsdk.modle.HmDevice;
import com.heiman.utils.HmUtils;

import java.util.ArrayList;

/**
 * @Author : 肖力 by mac
 * @Time :  2017/11/27 下午2:39
 * @Description :
 * @Modify record :
 */
public class DeviceAdpter extends BaseAdapter {

    private ArrayList<HmDevice> hmDeviceList = new ArrayList<HmDevice>();

    private Context context;
    private LayoutInflater layoutInflater;

    public DeviceAdpter(Context context, ArrayList<HmDevice> hmDeviceList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.hmDeviceList = hmDeviceList;
    }

    @Override
    public int getCount() {
        return hmDeviceList.size();
    }

    @Override
    public HmDevice getItem(int position) {
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
        initializeViews((HmDevice) getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(HmDevice hmDevice, ViewHolder holder) {
        //TODO implement
        if (hmDevice != null) {
            if (!HmUtils.isEmptyString(hmDevice.getDeviceName())) {
                holder.tvName.setText(hmDevice.getDeviceName());
            }
            if (!HmUtils.isEmptyString(hmDevice.getDeviceMac())) {
                holder.tvMac.setText(hmDevice.getDeviceMac());
            }
        }
    }

    protected class ViewHolder {
        private TextView tvName;
        private TextView tvMac;

        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvMac = (TextView) view.findViewById(R.id.tv_mac);
        }
    }
}

