package cj.tzw.base_uwb_m.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cj.tzw.base_uwb_m.R;
import cj.tzw.base_uwb_m.model.Device;
import cj.tzw.base_uwb_m.utils.ByteUtil;

public class DeviceAdapter extends BaseAdapter {
    private final String TAG = "DeviceAdapter";
    private ArrayList<Device> deviceList;
    private LayoutInflater inflater;

    public DeviceAdapter(Context context, ArrayList<Device> deviceList) {
        this.deviceList = deviceList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        Log.e(TAG,"deviceList："+deviceList);
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(TAG,"getView()");
        ViewHolder vh;
        if(convertView==null){
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_device,null);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        Device device = deviceList.get(position);
        vh.tvId = convertView.findViewById(R.id.tvId);
        vh.tvType = convertView.findViewById(R.id.tvType);
        vh.tvFVersion = convertView.findViewById(R.id.tvFVersion);
        vh.tvHVersion = convertView.findViewById(R.id.tvHVersion);
        byte[] routerIDBytes = device.getAlerterID();
        String routerID = ByteUtil.bytesToHexFun3(routerIDBytes);
        String firmVersion = device.getFirmware_version();
        String year = firmVersion.substring(0,2);
        String month = firmVersion.substring(2,4);
        String day = firmVersion.substring(4,6);
        String version = firmVersion.substring(6,8);
        String versionHead = version.substring(0,1);
        String versionTail = version.substring(1);
        int type = device.getType();
        String typeName = null;
        if(type==1){
            typeName = "ForkLift Alerter";
        }else if(type==2){
            typeName = "Fix Alerter";
        }else{
            typeName = "CARD";
        }
        vh.tvId.setText(routerID.toUpperCase());
        vh.tvType.setText(typeName);
        vh.tvFVersion.setText("20"+year+"-"+month+"-"+day+"\t\tV"+versionHead+"."+versionTail);
        vh.tvHVersion.setText(device.gethVersion());


        return convertView;
    }

    class ViewHolder{
        TextView tvId;
        TextView tvType;
        TextView tvFVersion;
        TextView tvHVersion;
    }

}
