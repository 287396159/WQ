package cj.tzw.wireless_uwb_m.activity.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cj.tzw.wireless_uwb_m.activity.R;

public class CJTableRow extends LinearLayout {

    private TextView textViewId,textViewType,textViewVersion,textViewStatus;

    public void setCJTableRow(String[] args){
        textViewId.setText(args[0]);
        textViewType.setText(args[1]);
        textViewVersion.setText(args[2]);
        textViewStatus.setText(args[3]);
    }

    public CJTableRow(Context context){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.tablerow_layout,this,true);
        textViewId=findViewById(R.id.tvCjDeviceID);
        textViewType=findViewById(R.id.tvCjType);
        textViewStatus=findViewById(R.id.tvCjUpdateStatus);
        textViewVersion=findViewById(R.id.tvCjFirmwareVersion);
    }

    public CJTableRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CJTableRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CJTableRow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
