package cj.tzw.tag_uwb_m.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import cj.tzw.base_uwb_m.callback.FtSrCallback;
import cj.tzw.base_uwb_m.model.Device;
import cj.tzw.base_uwb_m.router.RouterPathMethod;
import cj.tzw.base_uwb_m.utils.ByteUtil;
import cj.tzw.base_uwb_m.utils.ConstantUtil;
import cj.tzw.base_uwb_m.utils.DialogUtil;
import cj.tzw.base_uwb_m.utils.FtAnalysisUtil;
import cj.tzw.base_uwb_m.utils.FtBaseUtil;
import cj.tzw.base_uwb_m.utils.FtHandleUtil;
import cj.tzw.base_uwb_m.utils.VerifyUtil;
import cj.tzw.tag_uwb_m.R;

@Route(path = RouterPathMethod.TAG_SET_PATH)
public class TagSetActivity extends AppCompatActivity implements FtSrCallback {

    private final static String TAG="TagSetActivity";
    private TagSetActivity tagSetActivity;
    private Toolbar toolbar;
    private Spinner spTagAlarmEn,spTagPowerOffEn;
    private TextView tvDeviceInfo;
    private EditText etID,etEnterLowPowerTime,etLowFreqDistance,etPanId;
    private Button btnReadID,btnReadEnterLowPowerTime,btnReadLowFreqDistance,btnReadTagAlarmEn,btnReadTagPowerOffEn,btnReadPanId;
    private Button btnSetID,btnSetEnterLowPowerTime,btnSetLowFreqDistance,btnSetTagAlarmEn,btnSetTagPowerOffEn,btnSetPanId;
    private Button btnSearch,btnResetPanID,btnReponseTagPowerOff;
    private TextView tvPromptStr;

    private ArrayAdapter<String> alarmEnAdapter;
    private ArrayAdapter<String> powerOffEnAdapter;

    private FtHandleUtil ftHandleUtil;
    private Device currentDevice;
    private FtBaseUtil ftBaseUtil;

    private int operateType = ConstantUtil.ERROR_OPERATE;
    private boolean cancelOperate = false;
    private boolean firstOutTime = true;

    private View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cancelOperate = false;
            int vId = v.getId();
            DialogUtil.showWait(tagSetActivity,DialogUtil.WAIT_DIALOG,"正在操作，请稍候...");
            if(vId==R.id.btnReadID){
                ftHandleUtil.readCardId();
                operateType = ConstantUtil.READ_ONE_OPERATE;

            }else if(vId==R.id.btnReadEnterLowPowerTime){
                ftHandleUtil.readCardEnterLowPowerTime();
                operateType = ConstantUtil.READ_ONE_OPERATE;

            }else if(vId==R.id.btnReadLowFreqDistance){
                ftHandleUtil.readCardLowFreqDistance();
                operateType = ConstantUtil.READ_ONE_OPERATE;

            }else if(vId==R.id.btnReadTagAlarmEn){
                ftHandleUtil.readCardAlarmEn();
                operateType = ConstantUtil.READ_ONE_OPERATE;
            }else if(vId==R.id.btnReadTagPowerOffEn){
                ftHandleUtil.readCardPowerOffEn();
                operateType = ConstantUtil.READ_ONE_OPERATE;

            }else if(vId==R.id.btnReadPanId){
                ftHandleUtil.readCardPanId();
                operateType = ConstantUtil.READ_ONE_OPERATE;

            }else if(vId==R.id.btnSetID){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String setId=etID.getText().toString();
                if(VerifyUtil.verifyExcludeId(setId)){
                    ftHandleUtil.setCardId(ByteUtil.toBytes(setId));
                }else{
                    DialogUtil.showWait(tagSetActivity,DialogUtil.ERROR_DIALOG,"卡片ID不能为空或格式错误！");
                }

            }else if(vId==R.id.btnSetEnterLowPowerTime){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String setLowPowerTime=etEnterLowPowerTime.getText().toString();
                if(VerifyUtil.verifyRangeIncludeZero(setLowPowerTime)){
                    ftHandleUtil.setCardEnterLowPowerTime(ByteUtil.getBytes(Short.valueOf(setLowPowerTime),true));
                }else{
                    DialogUtil.showWait(tagSetActivity,DialogUtil.ERROR_DIALOG,"卡片警报使能位不能为空或格式错误！");
                }

            }else if(vId==R.id.btnSetLowFreqDistance){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String setLowFreqDistance=etLowFreqDistance.getText().toString();
                if(VerifyUtil.verifyRangeIncludeZero(setLowFreqDistance)){
                    ftHandleUtil.setCardLowFreqDistance(ByteUtil.getBytes(Short.valueOf(setLowFreqDistance),true));
                }else{
                    DialogUtil.showWait(tagSetActivity,DialogUtil.ERROR_DIALOG,"卡片静止进入低功耗的时间不能为空或格式错误！");
                }

            }else if(vId==R.id.btnSetTagAlarmEn){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                ftHandleUtil.setCardAlarmEn((byte)spTagAlarmEn.getSelectedItemPosition());

            }else if(vId==R.id.btnSetTagPowerOffEn){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                ftHandleUtil.setCardPowerOffEn((byte)spTagPowerOffEn.getSelectedItemPosition());

            }else if(vId==R.id.btnSetPanId){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String setPanId=etPanId.getText().toString();
                Log.i(TAG, "onClick: PanId"+setPanId);
                if(VerifyUtil.verifyExcludeId(setPanId)){
                    ftHandleUtil.setCardPanId(ByteUtil.toBytes(setPanId));
                }else{
                    DialogUtil.showWait(tagSetActivity,DialogUtil.ERROR_DIALOG,"卡片PanId不能为空或格式错误！");
                }
            }else if(vId==R.id.btnSearch){
                tvDeviceInfo.setText("Firmware Version：1bSVersion\t\t\t\tHardware Version：1hSVersion");
                readDeviceAllData();
            }else if(vId==R.id.btnResetPanID){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                ftHandleUtil.resetCardPanID();
            }else if(vId==R.id.btnReponseTagPowerOff){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                if("Start Reponse Tag Power Off".equals(btnReponseTagPowerOff.getText().toString())){
                    ftHandleUtil.setReponseTagPowerOff((byte) 1);
                }else if("Stop Reponse Tag Power Off".equals(btnReponseTagPowerOff.getText().toString())){
                    ftHandleUtil.setReponseTagPowerOff((byte) 0);
                }
            }
        }
    };

    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int id = v.getId();
            if(id==R.id.etEnterLowPowerTime){
                tvPromptStr.setText("range：（0-65535,unit:second）");
            }else if(id==R.id.etLowFreqDistance){
                tvPromptStr.setText("range：（0-65535,unit:cm）");
            }else if(id==R.id.etPanId){
                tvPromptStr.setText("range：（0001-FFFE）");
            }else{
                tvPromptStr.setText("");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_set);
        initViewAndLoadDeviceInfo();
        initListener();
        readDeviceAllData();

    }

    public void initViewAndLoadDeviceInfo(){
        tagSetActivity=this;

        tvDeviceInfo=findViewById(R.id.tvDeviceInfo);
        spTagAlarmEn=findViewById(R.id.spTagAlarmEn);
        spTagPowerOffEn=findViewById(R.id.spTagPowerOffEn);
        etID=findViewById(R.id.etID);
        etEnterLowPowerTime=findViewById(R.id.etEnterLowPowerTime);
        etLowFreqDistance=findViewById(R.id.etLowFreqDistance);
        etPanId=findViewById(R.id.etPanId);

        btnReadID=findViewById(R.id.btnReadID);
        btnReadEnterLowPowerTime=findViewById(R.id.btnReadEnterLowPowerTime);
        btnReadLowFreqDistance=findViewById(R.id.btnReadLowFreqDistance);
        btnReadTagAlarmEn=findViewById(R.id.btnReadTagAlarmEn);
        btnReadTagPowerOffEn=findViewById(R.id.btnReadTagPowerOffEn);
        btnReadPanId=findViewById(R.id.btnReadPanId);

        btnSetID=findViewById(R.id.btnSetID);
        btnSetEnterLowPowerTime=findViewById(R.id.btnSetEnterLowPowerTime);
        btnSetLowFreqDistance=findViewById(R.id.btnSetLowFreqDistance);
        btnSetTagAlarmEn=findViewById(R.id.btnSetTagAlarmEn);
        btnSetTagPowerOffEn=findViewById(R.id.btnSetTagPowerOffEn);
        btnSetPanId=findViewById(R.id.btnSetPanId);

        btnSearch=findViewById(R.id.btnSearch);
        btnResetPanID=findViewById(R.id.btnResetPanID);
        btnReponseTagPowerOff=findViewById(R.id.btnReponseTagPowerOff);


        tvPromptStr = findViewById(R.id.tvPromptStr);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);






        currentDevice = getIntent().getParcelableExtra("device");
        ftBaseUtil = FtBaseUtil.getInstance(getApplicationContext()).setFtSrCallback(this);
        ftHandleUtil = new FtHandleUtil(ftBaseUtil,currentDevice);

        spTagAlarmEn.setDropDownVerticalOffset((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));
        spTagPowerOffEn.setDropDownVerticalOffset((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));


    }
    public void initListener(){
        alarmEnAdapter = new ArrayAdapter<String>(this,R.layout.item_spinner,ConstantUtil.EN_OPTIONS);
        alarmEnAdapter.setDropDownViewResource(R.layout.layout_spinner);

        powerOffEnAdapter = new ArrayAdapter<String>(this,R.layout.item_spinner,ConstantUtil.EN_OPTIONS);
        powerOffEnAdapter.setDropDownViewResource(R.layout.layout_spinner);

        btnReadID.setOnClickListener(clickListener);
        btnReadEnterLowPowerTime.setOnClickListener(clickListener);
        btnReadLowFreqDistance.setOnClickListener(clickListener);
        btnReadTagAlarmEn.setOnClickListener(clickListener);
        btnReadTagPowerOffEn.setOnClickListener(clickListener);
        btnReadPanId.setOnClickListener(clickListener);

        btnSetID.setOnClickListener(clickListener);
        btnSetEnterLowPowerTime.setOnClickListener(clickListener);
        btnSetLowFreqDistance.setOnClickListener(clickListener);
        btnSetTagAlarmEn.setOnClickListener(clickListener);
        btnSetTagPowerOffEn.setOnClickListener(clickListener);
        btnSetPanId.setOnClickListener(clickListener);
        btnSearch.setOnClickListener(clickListener);
        btnResetPanID.setOnClickListener(clickListener);
        btnReponseTagPowerOff.setOnClickListener(clickListener);

        etEnterLowPowerTime.setOnFocusChangeListener(focusChangeListener);
        etLowFreqDistance.setOnFocusChangeListener(focusChangeListener);
        etPanId.setOnFocusChangeListener(focusChangeListener);

        spTagPowerOffEn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemSelected: "+id+spTagPowerOffEn.isSelected());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "onNothingSelected: ");
            }

        });
    }

    public void readDeviceAllData(){
        cancelOperate = false;
        firstOutTime = true;
        operateType = ConstantUtil.READ_ALL_OPERATE;
        ftHandleUtil.readCardDeviceAllInfo();
    }

    @Override
    public void ftSended() {

    }
    @Override
    public void ftRecevied(byte[] recevieMsg) {
        Log.i(TAG, "ftRecevied: "+ByteUtil.bytesToHexFun3(recevieMsg));
        HashMap<String,byte[]> retMap=FtAnalysisUtil.analysisCardData(recevieMsg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtil.showWait(tagSetActivity,DialogUtil.OK_DIALOG,"操作成功！");
            }
        });
        showReceviedContent(retMap);

    }

    public void showReceviedContent(final HashMap<String,byte[]> retMap){
        Log.i(TAG, "showReceviedContent: "+retMap);
        if(retMap==null){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Set<String> set=retMap.keySet();
                String[] keyArray=set.toArray(new String[0]);
                for(String key:keyArray){
                    byte[] retBytes=retMap.get(key);
                    switch (key){
                        case "firmware_version":
                            String firmwareVersionString=ByteUtil.bytesToHexFun3(retBytes);
                            tvDeviceInfo.setText("Firmware Version："+ "20"+firmwareVersionString.substring(0,2)+"-"
                                    +firmwareVersionString.substring(2,4)+"-"+firmwareVersionString.substring(4,6)+" V"+firmwareVersionString.substring(6,7)
                                    +"."+firmwareVersionString.substring(7,8));
                            break;
                        case "hardware_version":
                            tvDeviceInfo.setText(tvDeviceInfo.getText()+"\t\t\t\tHardware Version："+ ByteUtil.bytesToHexFun3(retBytes));
                            break;
                        case "card_id":
                            etID.setText(ByteUtil.bytesToHexFun3(retBytes));
                            break;
                        case "alarm_en":
                            spTagAlarmEn.setAdapter(alarmEnAdapter);
                            spTagAlarmEn.setSelection(retBytes[0]);
                            Log.i(TAG, "run: alarm_en"+ByteUtil.bytesToHexFun3(retBytes));
                            break;
                        case "enter_low_power_time":
                            etEnterLowPowerTime.setText(ByteUtil.getShort(retBytes)+"");
                            break;
                        case "low_power_distance":
                            etLowFreqDistance.setText(ByteUtil.getShort(retBytes)+"");
                            break;
                        case "pan_id":
                            etPanId.setText(ByteUtil.bytesToHexFun3(retBytes));
                            break;
                        case "power_off_en":
                            Log.i(TAG, "run: alarm_en"+ByteUtil.bytesToHexFun3(retBytes));
                            spTagPowerOffEn.setAdapter(powerOffEnAdapter);
                            spTagPowerOffEn.setSelection(retBytes[0]);
                            break;
                        case "reponse_tag_power_off":

                            if(1==retBytes[0]){
                                btnReponseTagPowerOff.setText("Stop Reponse Tag Power Off");
                            }else if(0==retBytes[0]){
                                btnReponseTagPowerOff.setText("Start Reponse Tag Power Off");
                            }

                            break;

                    }
                }
            }
        });
    }

    @Override
    public void ftRecevieOutTime() {

        if(cancelOperate){
            return;
        }
        if(operateType==ConstantUtil.READ_ALL_OPERATE){
            if(firstOutTime){
                firstOutTime = false;
            }else{
                return;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtil.showWait(tagSetActivity,DialogUtil.ERROR_DIALOG,"操作超时！");
            }
        });
    }

    @Override
    public void ftSendFail(String failMsg) {

    }
}
