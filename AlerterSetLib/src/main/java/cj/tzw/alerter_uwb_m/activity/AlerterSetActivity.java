package cj.tzw.alerter_uwb_m.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import cj.tzw.alerter_uwb_m.R;
import cj.tzw.base_uwb_m.adapter.PageAdapter;
import cj.tzw.base_uwb_m.callback.FtSrCallback;
import cj.tzw.base_uwb_m.model.Device;
import cj.tzw.base_uwb_m.router.RouterPathMethod;
import cj.tzw.base_uwb_m.utils.ByteUtil;
import cj.tzw.base_uwb_m.utils.ConstantUtil;
import cj.tzw.base_uwb_m.utils.DialogUtil;
import cj.tzw.base_uwb_m.utils.FtAnalysisUtil;
import cj.tzw.base_uwb_m.utils.FtBaseUtil;
import cj.tzw.base_uwb_m.utils.FtComUtil;
import cj.tzw.base_uwb_m.utils.FtHandleUtil;
import cj.tzw.base_uwb_m.utils.ToastUtil;
import cj.tzw.base_uwb_m.utils.VerifyUtil;

@Route(path = RouterPathMethod.ALERTER_SET_PATH)
public class AlerterSetActivity extends AppCompatActivity implements FtSrCallback{
    private final String TAG = "AlerterSetActivity";
    private AlerterSetActivity asActivity;
    private Toolbar toolbar;
    private TabLayout tab;
    private ViewPager vp;
    private SeekBar sbSoundVolume;
    private Spinner spTagAlarmEn,spForkLiftAlarmEn_fo,spFixAlarmEn,spForkLiftAlarmEn_fi,spLedAlarmEn,spSoundAlarmEn
            ,spSoundAlarmMode;
    private EditText etId_fo,etTagAlarmRange,etTagSafeRange,etTagSafeRangeExtend,etForkLiftAlarmRange_fo
            ,etLowFreqDistance,etPanId_fo,etId_fi,etForkLiftAlarmRange_fi,etPanId_fi,etFixedModeOnTime
            ,etFixedModeOffTime;
    private Button btnSetId_fo,btnSetTagAlarmEn,btnReadTagAlarmEn,btnSetTagAlarmRange
            ,btnReadTagAlarmRange,btnSetTagSafeRange,btnReadTagSafeRange
            ,btnSetTagSafeRangeExtend,btnReadTagSafeRangeExtend,btnSetForkLiftAlarmEn_fo
            ,btnReadForkLiftAlarmEn_fo,btnSetForkLiftAlarmRange_fo,btnReadForkLiftAlarmRange_fo
            ,btnSetFixAlarmEn,btnReadFixAlarmEn,btnSetLowFreqDistance,btnReadLowFreqDistance
            ,btnSetPanId_fo,btnReadPanId_fo,btnResetPanId_fo,btnSetForkLiftAlarmEn_fi
            ,btnReadForkLiftAlarmEn_fi,btnSetForkLiftAlarmRange_fi,btnReadForkLiftAlarmRange_fi
            ,btnSetPanId_fi,btnReadPanId_fi,btnResetPanId_fi,btnSetLedAlarmEn,btnReadLedAlarmEn
            ,btnSetSoundAlarmEn,btnReadSoundAlarmEn,btnSetSoundVolume,btnReadSoundVolume
            ,btnSetFixedModeOnTime,btnReadFixedModeOnTime,btnSetFixedModeOffTime,btnReadFixedModeOffTime
            ,btnSetId_fi,btnSetSoundAlarmMode,btnReadSoundAlarmMode,btnResetDevice;
    private TextView tvDeviceInfo,tvPromptStr,tvSoundVolume,tvSoundVolumeHint,tvFixedModeOnTimeHint,tvFixedModeOffTimeHint;
    private LinearLayout linearPrompt;
    private  ImageView ivPutWay;
    private View foaSetView,fiaSetiView,aSetView;
    private ArrayList<View> viewList;
    private LayoutInflater inflater;
    private Device currentDevice;
    private int deviceType;
    private String deviceIdStr;
    private String deviceTypeStr;
    private FtHandleUtil ftHandleUtil;
    private FtBaseUtil ftBaseUtil;
    private FtComUtil ftComUtil;
    private ArrayAdapter<String> alarmEnAdapter;
    private ArrayAdapter<String> soundModeAdapter;
    private int operateType = ConstantUtil.ERROR_OPERATE;
    private boolean firstOutTime = true;
    private boolean cancelOperate = false;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cancelOperate = false;
            int vId = v.getId();
            DialogUtil.showWait(asActivity,DialogUtil.WAIT_DIALOG,"正在操作，请稍候...");
            if(vId == R.id.btnResetDevice){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                if(deviceType==1){
                    ftHandleUtil.resetForkLiftAlerter();
                }else if(deviceType==2){
                    ftHandleUtil.resetFixAlerter();
                }
            }else if(vId == R.id.btnSetId_fork){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String forkLiftId = etId_fo.getText().toString();
                if(VerifyUtil.verifyDeviceId(forkLiftId)){
                    ftHandleUtil.setForkLiftAlerterId(forkLiftId);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"叉车报警器ID不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"叉车报警器ID不能为空！");
                }
            }else if(vId == R.id.btnSetTagAlarmEn){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                ftHandleUtil.setTagAlarmEn(spTagAlarmEn.getSelectedItemPosition());
            }else if(vId == R.id.btnReadTagAlarmEn){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readTagAlarmEn();
            }else if(vId == R.id.btnSetTagAlarmRange){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String rangeStr = etTagAlarmRange.getText().toString();
                if(VerifyUtil.verifyRangeExcludeZero(rangeStr)){
                    short range = Short.valueOf(rangeStr);
                    ftHandleUtil.setTagAlarmRange(range);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"卡片报警器报警距离不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"卡片报警器报警距离不能为空！");
                }
            }else if(vId == R.id.btnReadTagAlarmRange){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readTagAlarmRange();
            }else if(vId == R.id.btnSetTagSafeRange){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String rangeStr = etTagSafeRange.getText().toString();
                if(VerifyUtil.verifyRangeIncludeZero(rangeStr)){
                    short range = Short.valueOf(rangeStr);
                    ftHandleUtil.setTagSafeRange(range);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"卡片报警器安全距离不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"卡片报警器安全距离不能为空！");
                }
            }else if(vId == R.id.btnReadTagSafeRange){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readTagSafeRange();
            }else if(vId == R.id.btnSetTagSafeRangeExtend){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String rangeStr = etTagSafeRangeExtend.getText().toString();
                if(VerifyUtil.verifyRangeIncludeZero(rangeStr)){
                    short range = Short.valueOf(rangeStr);
                    ftHandleUtil.setTagSafeRangeExtend(range);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"卡片报警器安全距离扩展不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"卡片报警器安全距离扩展不能为空！");
                }
            }else if(vId == R.id.btnReadTagSafeRangeExtend){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readTagSafeRangeExtend();
            }else if(vId == R.id.btnSetForkLiftAlarmEn_fork){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                ftHandleUtil.setForkLiftAlarmEn_fork(spForkLiftAlarmEn_fo.getSelectedItemPosition());
            }else if(vId == R.id.btnReadForkLiftAlarmEn_fork){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readForkLiftAlarmEn_fork();
            }else if(vId == R.id.btnSetForkLiftAlarmRange_fork){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String rangeStr = etForkLiftAlarmRange_fo.getText().toString();
                if(VerifyUtil.verifyRangeExcludeZero(rangeStr)){
                    short range = Short.valueOf(rangeStr);
                    ftHandleUtil.setForkLiftAlarmRange_fork(range);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"叉车报警器报警距离不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"叉车报警器报警距离不能为空！");
                }
            }else if(vId == R.id.btnReadForkLiftAlarmRange_fork){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readForkLiftAlarmRange_fork();
            }else if(vId == R.id.btnSetFixAlarmEn){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                ftHandleUtil.setFixAlarmEn(spFixAlarmEn.getSelectedItemPosition());
            }else if(vId == R.id.btnReadFixAlarmEn){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readFixAlarmEn();
            }else if(vId == R.id.btnSetLowFreqDistance){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String distanceStr = etLowFreqDistance.getText().toString();
                if(VerifyUtil.verifyRangeIncludeZero(distanceStr)){
                    short distance = Short.valueOf(distanceStr);
                    ftHandleUtil.setLowFreqDistance(distance);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"降频距离不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"降频距离不能为空！");
                }
            }else if(vId == R.id.btnReadLowFreqDistance){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readLowFreqDistance();
            }else if(vId == R.id.btnSetPanId_fork){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String panId = etPanId_fo.getText().toString();
                if(VerifyUtil.verifyDeviceId(panId)){
                    ftHandleUtil.setForkPanId(panId);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"叉车报警器PanID不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"叉车报警器PanID不能为空！");
                }
            }else if(vId == R.id.btnReadPanId_fork){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readForkPanId();
            }else if(vId == R.id.btnResetPanId_fork){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.resetForkPanId();
            }else if(vId == R.id.btnSetLedAlarmEn){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                ftHandleUtil.setLedAlarmEn(spLedAlarmEn.getSelectedItemPosition());
            }else if(vId == R.id.btnReadLedAlarmEn){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readLedAlarmEn();
            }else if(vId == R.id.btnSetSoundAlarmEn){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                ftHandleUtil.setSoundAlarmEn(spSoundAlarmEn.getSelectedItemPosition());
            }else if(vId == R.id.btnReadSoundAlarmEn){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readSoundAlarmEn();
            }else if(vId == R.id.btnSetSoundVolume){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                short volume = (short) sbSoundVolume.getProgress();
                ftHandleUtil.setSoundVolume(volume);
            }else if(vId == R.id.btnReadSoundVolume){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readSoundVolume();
            }else if(vId == R.id.btnSetSoundAlarmMode){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                ftHandleUtil.setSoundAlarmMode(spSoundAlarmMode.getSelectedItemPosition());
            }else if(vId == R.id.btnReadSoundAlarmMode){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readSoundAlarmMode();
            }else if(vId == R.id.btnSetFixedModeOnTime){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String onTimeStr = etFixedModeOnTime.getText().toString();
                if(VerifyUtil.verifyOnOrOffTime(onTimeStr)){
                    short onTime = Short.valueOf(onTimeStr);
                    ftHandleUtil.setLowFreqDistance(onTime);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"声音报警开启时间不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"声音报警开启时间不能为空！");
                }
            }else if(vId == R.id.btnReadFixedModeOnTime){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readFixedModeOnTime();
            }else if(vId == R.id.btnSetFixedModeOffTime){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String offTimeStr = etLowFreqDistance.getText().toString();
                if(VerifyUtil.verifyOnOrOffTime(offTimeStr)){
                    short offTime = Short.valueOf(offTimeStr);
                    ftHandleUtil.setLowFreqDistance(offTime);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"声音报警关闭时间不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"声音报警关闭时间不能为空！");
                }
            }else if(vId == R.id.btnReadFixedModeOffTime){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readFixedModeOffTime();
            }else if(vId == R.id.btnSetId_fix){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String id = etId_fi.getText().toString();
                if(VerifyUtil.verifyDeviceId(id)){
                    ftHandleUtil.setFixAlerterId(id);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"固定报警器ID不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"固定报警器ID不能为空！");
                }
            }else if(vId == R.id.btnSetForkLiftAlarmEn_fix){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                ftHandleUtil.setForkLiftAlarmEn_fix(spForkLiftAlarmEn_fi.getSelectedItemPosition());
            }else if(vId == R.id.btnReadForkLiftAlarmEn_fix){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readForkLiftAlarmEn_fix();
            }else if(vId == R.id.btnSetForkLiftAlarmRange_fix){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String rangeStr = etForkLiftAlarmRange_fi.getText().toString();
                if(VerifyUtil.verifyRangeExcludeZero(rangeStr)){
                    short range = Short.valueOf(rangeStr);
                    ftHandleUtil.setForkLiftAlarmRange_fix(range);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"固定报警器报警距离不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"固定报警器报警距离不能为空！");
                }
            }else if(vId == R.id.btnReadForkLiftAlarmRange_fix){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readForkLiftAlarmRange_fix();
            }else if(vId == R.id.btnSetPanId_fix){
                operateType = ConstantUtil.SET_ONE_OPERATE;
                String panId = etId_fi.getText().toString();
                if(VerifyUtil.verifyDeviceId(panId)){
                    ftHandleUtil.setFixPanId(panId);
                }else{
                    DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"固定报警器PanID不能为空或格式错误！");
//                    ToastUtil.toastShortMsg(asActivity,"固定报警器PanID不能为空！");
                }
            }else if(vId == R.id.btnReadPanId_fix){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.readFixPanId();
            }else if(vId == R.id.btnResetPanId_fix){
                operateType = ConstantUtil.READ_ONE_OPERATE;
                ftHandleUtil.resetFixPanId();
            }
        }
    };
    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            int id = v.getId();
            if(id==R.id.etId_fork){
                tvPromptStr.setText("range：（0001-FFFE）");
            }else if(id==R.id.etTagAlarmRange){
                tvPromptStr.setText("range：（1-65535,unit:cm）");
            }else if(id==R.id.etTagSafeRange){
                tvPromptStr.setText("range：（0-65535,unit:cm）");
            }else if(id==R.id.etTagSafeRangeExtend){
                tvPromptStr.setText("range：（0-65535,unit:cm）");
            }else if(id==R.id.etForkLiftAlarmRange_fork){
                tvPromptStr.setText("range：（1-65535,unit:cm）");
            }else if(id==R.id.etLowFreqDistance){
                tvPromptStr.setText("range：（0-65535,unit:cm）");
            }else if(id==R.id.etPanId_fork){
                tvPromptStr.setText("range：（0001-FFFE）");
            }else if(id==R.id.etFixedModeOnTime){
                tvPromptStr.setText("range：（0-255,unit:0.05s）");
            }else if(id==R.id.etFixedModeOffTime){
                tvPromptStr.setText("range：（0-255,unit:0.05s）");
            }else if(id==R.id.etId_fix){
                tvPromptStr.setText("range：（0001-FFFE）");
            }else if(id==R.id.etForkLiftAlarmRange_fix){
                tvPromptStr.setText("range：（1-65535,unit:cm）");
            }else if(id==R.id.etPanId_fix){
                tvPromptStr.setText("range：（0001-FFFE）");
            }else{
                tvPromptStr.setText("");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerter_set);
        initViewAndLoadDeviceInfo();
        initPager();
        initListener();
        readDeviceAllData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        cancelOperate = true;
        super.onBackPressed();
    }

    /**
     * 初始化控件并读取设备信息
     */
    public void initViewAndLoadDeviceInfo(){
        asActivity = this;
        inflater = LayoutInflater.from(this);
        viewList = new ArrayList<>();
        tab = findViewById(R.id.tab);
        vp = findViewById(R.id.vp);
        btnResetDevice = findViewById(R.id.btnResetDevice);
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo);
        linearPrompt = findViewById(R.id.linearPrompt);
        ivPutWay = findViewById(R.id.ivPutWay);
        tvPromptStr = findViewById(R.id.tvPromptStr);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        //获取设备对象
        currentDevice = getIntent().getParcelableExtra("device");
        ftBaseUtil = FtBaseUtil.getInstance(getApplicationContext()).setFtSrCallback(this);
        ftHandleUtil = new FtHandleUtil(ftBaseUtil,currentDevice);
        if(currentDevice!=null){
            ftComUtil = new FtComUtil(currentDevice);
            byte[] idBytes = currentDevice.getAlerterID();
            deviceType = currentDevice.getType();
            deviceIdStr = ByteUtil.bytesToHexFun3(idBytes);
            if(deviceType==1){//叉车报警器
                deviceTypeStr = "ForkLift Alerter";
                initFoaSetView();
                initASetView();
                etId_fo.setText(deviceIdStr);
            }else if(deviceType==2){//固定报警器
                deviceTypeStr = "Fix Alerter";
                initFiaSetView();
                initASetView();
                etId_fi.setText(deviceIdStr);
            }else{//卡片
                deviceTypeStr = "Card";
            }
            tvDeviceInfo.setText("Alerter Id："+deviceIdStr.toLowerCase()+"\t\tType："+deviceTypeStr+"");
        }
    }

    public void initPager(){
        vp.setAdapter(new PageAdapter(viewList,deviceType));
        tab.setupWithViewPager(vp);
    }

    public void initFoaSetView(){
        foaSetView = inflater.inflate(R.layout.layout_forklift_alerter_set,null);
        viewList.add(foaSetView);
        etId_fo = foaSetView.findViewById(R.id.etId_fork);
        btnSetId_fo = foaSetView.findViewById(R.id.btnSetId_fork);
        spTagAlarmEn = foaSetView.findViewById(R.id.spTagAlarmEn);
        btnSetTagAlarmEn = foaSetView.findViewById(R.id.btnSetTagAlarmEn);
        btnReadTagAlarmEn = foaSetView.findViewById(R.id.btnReadTagAlarmEn);
        etTagAlarmRange = foaSetView.findViewById(R.id.etTagAlarmRange);
        btnSetTagAlarmRange = foaSetView.findViewById(R.id.btnSetTagAlarmRange);
        btnReadTagAlarmRange = foaSetView.findViewById(R.id.btnReadTagAlarmRange);
        etTagSafeRange = foaSetView.findViewById(R.id.etTagSafeRange);
        btnSetTagSafeRange = foaSetView.findViewById(R.id.btnSetTagSafeRange);
        btnReadTagSafeRange = foaSetView.findViewById(R.id.btnReadTagSafeRange);
        etTagSafeRangeExtend = foaSetView.findViewById(R.id.etTagSafeRangeExtend);
        btnSetTagSafeRangeExtend = foaSetView.findViewById(R.id.btnSetTagSafeRangeExtend);
        btnReadTagSafeRangeExtend = foaSetView.findViewById(R.id.btnReadTagSafeRangeExtend);
        spForkLiftAlarmEn_fo = foaSetView.findViewById(R.id.spForkLiftAlarmEn_fork);
        btnSetForkLiftAlarmEn_fo = foaSetView.findViewById(R.id.btnSetForkLiftAlarmEn_fork);
        btnReadForkLiftAlarmEn_fo = foaSetView.findViewById(R.id.btnReadForkLiftAlarmEn_fork);
        etForkLiftAlarmRange_fo = foaSetView.findViewById(R.id.etForkLiftAlarmRange_fork);
        btnSetForkLiftAlarmRange_fo = foaSetView.findViewById(R.id.btnSetForkLiftAlarmRange_fork);
        btnReadForkLiftAlarmRange_fo = foaSetView.findViewById(R.id.btnReadForkLiftAlarmRange_fork);
        spFixAlarmEn = foaSetView.findViewById(R.id.spFixAlarmEn);
        btnSetFixAlarmEn = foaSetView.findViewById(R.id.btnSetFixAlarmEn);
        btnReadFixAlarmEn = foaSetView.findViewById(R.id.btnReadFixAlarmEn);
        etLowFreqDistance = foaSetView.findViewById(R.id.etLowFreqDistance);
        btnSetLowFreqDistance = foaSetView.findViewById(R.id.btnSetLowFreqDistance);
        btnReadLowFreqDistance = foaSetView.findViewById(R.id.btnReadLowFreqDistance);
        etPanId_fo = foaSetView.findViewById(R.id.etPanId_fork);
        btnSetPanId_fo = foaSetView.findViewById(R.id.btnSetPanId_fork);
        btnReadPanId_fo = foaSetView.findViewById(R.id.btnReadPanId_fork);
        btnResetPanId_fo = foaSetView.findViewById(R.id.btnResetPanId_fork);

        spTagAlarmEn.setDropDownVerticalOffset((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));
        spForkLiftAlarmEn_fo.setDropDownVerticalOffset((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));
        spFixAlarmEn.setDropDownVerticalOffset((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));
    }


    public void initFiaSetView(){
        fiaSetiView = inflater.inflate(R.layout.layout_fix_alerter_set,null);
        viewList.add(fiaSetiView);
        etId_fi = fiaSetiView.findViewById(R.id.etId_fix);
        btnSetId_fi = fiaSetiView.findViewById(R.id.btnSetId_fix);
        spForkLiftAlarmEn_fi = fiaSetiView.findViewById(R.id.spForkLiftAlarmEn_fix);
        btnSetForkLiftAlarmEn_fi = fiaSetiView.findViewById(R.id.btnSetForkLiftAlarmEn_fix);
        btnReadForkLiftAlarmEn_fi = fiaSetiView.findViewById(R.id.btnReadForkLiftAlarmEn_fix);
        etForkLiftAlarmRange_fi = fiaSetiView.findViewById(R.id.etForkLiftAlarmRange_fix);
        btnSetForkLiftAlarmRange_fi = fiaSetiView.findViewById(R.id.btnSetForkLiftAlarmRange_fix);
        btnReadForkLiftAlarmRange_fi = fiaSetiView.findViewById(R.id.btnReadForkLiftAlarmRange_fix);
        etPanId_fi = fiaSetiView.findViewById(R.id.etPanId_fix);
        btnSetPanId_fi = fiaSetiView.findViewById(R.id.btnSetPanId_fix);
        btnReadPanId_fi = fiaSetiView.findViewById(R.id.btnReadPanId_fix);
        btnResetPanId_fi = fiaSetiView.findViewById(R.id.btnResetPanId_fix);

        spForkLiftAlarmEn_fi.setDropDownVerticalOffset((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));
    }

    public void initASetView(){
        aSetView = inflater.inflate(R.layout.layout_alarm_set,null);
        viewList.add(aSetView);
        spLedAlarmEn = aSetView.findViewById(R.id.spLedAlarmEn);
        btnSetLedAlarmEn = aSetView.findViewById(R.id.btnSetLedAlarmEn);
        btnReadLedAlarmEn = aSetView.findViewById(R.id.btnReadLedAlarmEn);
        spSoundAlarmEn = aSetView.findViewById(R.id.spSoundAlarmEn);
        btnSetSoundAlarmEn = aSetView.findViewById(R.id.btnSetSoundAlarmEn);
        btnReadSoundAlarmEn = aSetView.findViewById(R.id.btnReadSoundAlarmEn);
        sbSoundVolume = aSetView.findViewById(R.id.sbSoundVolume);
        tvSoundVolume = aSetView.findViewById(R.id.tvSoundVolume);
        btnSetSoundVolume = aSetView.findViewById(R.id.btnSetSoundVolume);
        btnReadSoundVolume = aSetView.findViewById(R.id.btnReadSoundVolume);
        spSoundAlarmMode = aSetView.findViewById(R.id.spSoundAlarmMode);
        btnSetSoundAlarmMode = aSetView.findViewById(R.id.btnSetSoundAlarmMode);
        btnReadSoundAlarmMode = aSetView.findViewById(R.id.btnReadSoundAlarmMode);
        etFixedModeOnTime = aSetView.findViewById(R.id.etFixedModeOnTime);
        btnSetFixedModeOnTime = aSetView.findViewById(R.id.btnSetFixedModeOnTime);
        btnReadFixedModeOnTime = aSetView.findViewById(R.id.btnReadFixedModeOnTime);
        etFixedModeOffTime = aSetView.findViewById(R.id.etFixedModeOffTime);
        btnSetFixedModeOffTime = aSetView.findViewById(R.id.btnSetFixedModeOffTime);
        btnReadFixedModeOffTime = aSetView.findViewById(R.id.btnReadFixedModeOffTime);

        tvSoundVolumeHint = aSetView.findViewById(R.id.tvSoundVolumeHint);
        tvFixedModeOnTimeHint = aSetView.findViewById(R.id.tvFixedModeOnTimeHint);
        tvFixedModeOffTimeHint = aSetView.findViewById(R.id.tvFixedModeOffTimeHint);

        spLedAlarmEn.setDropDownVerticalOffset((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));
        spSoundAlarmEn.setDropDownVerticalOffset((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));
        spSoundAlarmMode.setDropDownVerticalOffset((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,40,getResources().getDisplayMetrics()));

        sbSoundVolume.setMax(10);
    }


    /**
     * 改变声音报警器模式
     * @param mode
     */
    public void soundAlarmModeChange(int mode){
        Log.i(TAG,"模式："+mode);
        if(mode==0){
            sbSoundVolume.setEnabled(true);
            btnSetSoundVolume.setEnabled(true);
            btnReadSoundVolume.setEnabled(true);
            btnSetFixedModeOnTime.setEnabled(false);
            btnReadFixedModeOnTime.setEnabled(false);
            btnSetFixedModeOffTime.setEnabled(false);
            btnReadFixedModeOffTime.setEnabled(false);
            etFixedModeOnTime.setEnabled(false);
            etFixedModeOffTime.setEnabled(false);
            tvSoundVolumeHint.setEnabled(true);
            tvFixedModeOnTimeHint.setEnabled(false);
            tvFixedModeOffTimeHint.setEnabled(false);
            etFixedModeOnTime.setFocusable(false);
            etFixedModeOffTime.setFocusable(false);

            btnSetSoundVolume.setBackgroundResource(R.drawable.button_bg);
            btnReadSoundVolume.setBackgroundResource(R.drawable.button_bg);
            btnSetFixedModeOnTime.setBackgroundResource(R.drawable.button_disable_bg);
            btnReadFixedModeOnTime.setBackgroundResource(R.drawable.button_disable_bg);
            btnSetFixedModeOffTime.setBackgroundResource(R.drawable.button_disable_bg);
            btnReadFixedModeOffTime.setBackgroundResource(R.drawable.button_disable_bg);
            etFixedModeOnTime.setBackgroundResource(R.drawable.edit_disable_bg);
            etFixedModeOffTime.setBackgroundResource(R.drawable.edit_disable_bg);
            tvSoundVolume.setTextColor(Color.BLACK);
            tvSoundVolumeHint.setTextColor(Color.BLACK);
            tvFixedModeOnTimeHint.setTextColor(getResources().getColor(android.R.color.darker_gray));
            tvFixedModeOffTimeHint.setTextColor(getResources().getColor(android.R.color.darker_gray));
            etFixedModeOnTime.setTextColor(getResources().getColor(android.R.color.darker_gray));
            etFixedModeOffTime.setTextColor(getResources().getColor(android.R.color.darker_gray));


        }else if(mode==1){
            tvSoundVolumeHint.setEnabled(true);
            sbSoundVolume.setEnabled(true);
            btnSetSoundVolume.setEnabled(true);
            btnReadSoundVolume.setEnabled(true);
            tvFixedModeOnTimeHint.setEnabled(true);
            etFixedModeOnTime.setEnabled(true);
            etFixedModeOffTime.setEnabled(true);
            btnSetFixedModeOnTime.setEnabled(true);
            btnReadFixedModeOnTime.setEnabled(true);
            tvFixedModeOffTimeHint.setEnabled(true);
            btnSetFixedModeOffTime.setEnabled(true);
            btnReadFixedModeOffTime.setEnabled(true);
            etFixedModeOnTime.setFocusable(true);
            etFixedModeOffTime.setFocusable(true);

            btnSetSoundVolume.setBackgroundResource(R.drawable.button_bg);
            btnReadSoundVolume.setBackgroundResource(R.drawable.button_bg);
            btnSetFixedModeOnTime.setBackgroundResource(R.drawable.button_bg);
            btnReadFixedModeOnTime.setBackgroundResource(R.drawable.button_bg);
            btnSetFixedModeOffTime.setBackgroundResource(R.drawable.button_bg);
            btnReadFixedModeOffTime.setBackgroundResource(R.drawable.button_bg);
            etFixedModeOnTime.setBackgroundResource(R.drawable.edit_bg);
            etFixedModeOffTime.setBackgroundResource(R.drawable.edit_bg);
            tvSoundVolume.setTextColor(Color.BLACK);
            tvSoundVolumeHint.setTextColor(Color.BLACK);
            tvFixedModeOnTimeHint.setTextColor(Color.BLACK);
            tvFixedModeOffTimeHint.setTextColor(Color.BLACK);
            etFixedModeOnTime.setTextColor(Color.BLACK);
            etFixedModeOffTime.setTextColor(Color.BLACK);
        }else if(mode==2){
            tvSoundVolumeHint.setEnabled(false);
            sbSoundVolume.setEnabled(false);
            btnSetSoundVolume.setEnabled(false);
            btnReadSoundVolume.setEnabled(false);
            tvFixedModeOnTimeHint.setEnabled(false);
            etFixedModeOnTime.setEnabled(false);
            etFixedModeOffTime.setEnabled(false);
            btnSetFixedModeOnTime.setEnabled(false);
            btnReadFixedModeOnTime.setEnabled(false);
            tvFixedModeOffTimeHint.setEnabled(false);
            btnSetFixedModeOffTime.setEnabled(false);
            btnReadFixedModeOffTime.setEnabled(false);
            etFixedModeOnTime.setFocusable(false);
            etFixedModeOffTime.setFocusable(false);

            btnSetSoundVolume.setBackgroundResource(R.drawable.button_disable_bg);
            btnReadSoundVolume.setBackgroundResource(R.drawable.button_disable_bg);
            btnSetFixedModeOnTime.setBackgroundResource(R.drawable.button_disable_bg);
            btnReadFixedModeOnTime.setBackgroundResource(R.drawable.button_disable_bg);
            btnSetFixedModeOffTime.setBackgroundResource(R.drawable.button_disable_bg);
            btnReadFixedModeOffTime.setBackgroundResource(R.drawable.button_disable_bg);
            etFixedModeOnTime.setBackgroundResource(R.drawable.edit_disable_bg);
            etFixedModeOffTime.setBackgroundResource(R.drawable.edit_disable_bg);
            tvSoundVolume.setTextColor(getResources().getColor(android.R.color.darker_gray));
            tvSoundVolumeHint.setTextColor(getResources().getColor(android.R.color.darker_gray));
            tvFixedModeOnTimeHint.setTextColor(getResources().getColor(android.R.color.darker_gray));
            tvFixedModeOffTimeHint.setTextColor(getResources().getColor(android.R.color.darker_gray));
            etFixedModeOnTime.setTextColor(getResources().getColor(android.R.color.darker_gray));
            etFixedModeOffTime.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    public void initListener(){
        alarmEnAdapter = new ArrayAdapter<String>(asActivity,R.layout.item_spinner,ConstantUtil.EN_OPTIONS);
        alarmEnAdapter.setDropDownViewResource(R.layout.layout_spinner);

        soundModeAdapter = new ArrayAdapter<String>(asActivity,R.layout.item_spinner,ConstantUtil.SOUND_ALARM_MODE);
        soundModeAdapter.setDropDownViewResource(R.layout.layout_spinner);

        if(deviceType==1){
            btnSetId_fo.setOnClickListener(clickListener);
            btnSetTagAlarmEn.setOnClickListener(clickListener);
            btnReadTagAlarmEn.setOnClickListener(clickListener);
            btnSetTagAlarmRange.setOnClickListener(clickListener);
            btnReadTagAlarmRange.setOnClickListener(clickListener);
            btnSetTagSafeRange.setOnClickListener(clickListener);
            btnReadTagSafeRange.setOnClickListener(clickListener);
            btnSetTagSafeRangeExtend.setOnClickListener(clickListener);
            btnReadTagSafeRangeExtend.setOnClickListener(clickListener);
            btnSetForkLiftAlarmEn_fo.setOnClickListener(clickListener);
            btnReadForkLiftAlarmEn_fo.setOnClickListener(clickListener);
            btnSetForkLiftAlarmRange_fo.setOnClickListener(clickListener);
            btnReadForkLiftAlarmRange_fo.setOnClickListener(clickListener);
            btnSetFixAlarmEn.setOnClickListener(clickListener);
            btnReadFixAlarmEn.setOnClickListener(clickListener);
            btnSetLowFreqDistance.setOnClickListener(clickListener);
            btnReadLowFreqDistance.setOnClickListener(clickListener);
            btnSetPanId_fo.setOnClickListener(clickListener);
            btnReadPanId_fo.setOnClickListener(clickListener);
            btnResetPanId_fo.setOnClickListener(clickListener);

            etId_fo.setOnFocusChangeListener(focusChangeListener);
            etTagAlarmRange.setOnFocusChangeListener(focusChangeListener);
            etTagSafeRange.setOnFocusChangeListener(focusChangeListener);
            etTagSafeRangeExtend.setOnFocusChangeListener(focusChangeListener);
            etForkLiftAlarmRange_fo.setOnFocusChangeListener(focusChangeListener);
            etLowFreqDistance.setOnFocusChangeListener(focusChangeListener);
            etPanId_fo.setOnFocusChangeListener(focusChangeListener);
        }else if(deviceType==2){
            btnSetId_fi.setOnClickListener(clickListener);
            btnSetForkLiftAlarmEn_fi.setOnClickListener(clickListener);
            btnReadForkLiftAlarmEn_fi.setOnClickListener(clickListener);
            btnSetForkLiftAlarmRange_fi.setOnClickListener(clickListener);
            btnReadForkLiftAlarmRange_fi.setOnClickListener(clickListener);
            btnSetPanId_fi.setOnClickListener(clickListener);
            btnReadPanId_fi.setOnClickListener(clickListener);
            btnResetPanId_fi.setOnClickListener(clickListener);

            etId_fi.setOnFocusChangeListener(focusChangeListener);
            etForkLiftAlarmRange_fi.setOnFocusChangeListener(focusChangeListener);
            etPanId_fi.setOnFocusChangeListener(focusChangeListener);
        }
        etFixedModeOnTime.setOnFocusChangeListener(focusChangeListener);
        etFixedModeOffTime.setOnFocusChangeListener(focusChangeListener);
        btnResetDevice.setOnClickListener(clickListener);

        spSoundAlarmMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG,"Mode位置："+position);
                soundAlarmModeChange(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSetLedAlarmEn.setOnClickListener(clickListener);
        btnReadLedAlarmEn.setOnClickListener(clickListener);
        btnSetSoundAlarmEn.setOnClickListener(clickListener);
        btnReadSoundAlarmEn.setOnClickListener(clickListener);
        btnSetSoundVolume.setOnClickListener(clickListener);
        btnReadSoundVolume.setOnClickListener(clickListener);
        btnSetSoundAlarmMode.setOnClickListener(clickListener);
        btnReadSoundAlarmMode.setOnClickListener(clickListener);
        btnSetFixedModeOnTime.setOnClickListener(clickListener);
        btnReadFixedModeOnTime.setOnClickListener(clickListener);
        btnSetFixedModeOffTime.setOnClickListener(clickListener);
        btnReadFixedModeOffTime.setOnClickListener(clickListener);


        sbSoundVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                soundVolume = progress;
                tvSoundVolume.setText(String.valueOf(soundVolume));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        btnSetId_fo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ftHandleUtil.readForkPanId();
//            }
//        });
    }




    public void readDeviceAllData(){
        cancelOperate = false;
        firstOutTime = true;
        operateType = ConstantUtil.READ_ALL_OPERATE;
        DialogUtil.releaseWait();
        DialogUtil.closeWait();
        DialogUtil.showWait(asActivity,DialogUtil.WAIT_DIALOG,"正在获取，请稍候...");
        ftHandleUtil.readDeviceAllInfo();
    }


    int tagAlarmEn,forkLiftAlarmEn_fork,fixAlarmEn,ledAlarmEn,soundAlarmEn
        ,soundAlarmMode,forkLiftAlarmEn_fix,forkLiftAlarmRange_fix,soundVolume;

    /**
     * 将解析完的数据显示
     * @param retMap
     */
    public void showReceviedContent(final HashMap<String,byte[]> retMap){
        if(retMap==null){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Set<String> set = retMap.keySet();
                String[] keyArray = set.toArray(new String[0]);
                for(String key : keyArray){
                    byte[] retBytes = retMap.get(key);
                    switch (key){
                        //********************************Set***********************************
                        //forklift alerter set
                        case "fork_id_set":
                            String setId = etId_fo.getText().toString();
                            String retId = ByteUtil.bytesToHexFun3(retBytes);
                            etId_fo.setText(retId);
                            tvDeviceInfo.setText("AlerterId："+retId+"\t\tType："+deviceTypeStr);
                            if(retId.equalsIgnoreCase(setId)){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "tag_alarm_en_set":
                            int tag_alarm_en =  spTagAlarmEn.getSelectedItemPosition();
                            tagAlarmEn = retBytes[0];
                            Log.i(TAG,"使能位对比："+tag_alarm_en+"-"+tagAlarmEn);
                            spTagAlarmEn.setAdapter(alarmEnAdapter);
                            spTagAlarmEn.setSelection(tagAlarmEn);
                            if(tag_alarm_en==tagAlarmEn){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "tag_alarm_range_set":
                            int tag_alarm_range = Integer.valueOf(etTagAlarmRange.getText().toString());
                            int tagAlarmRange = ByteUtil.getShort(retBytes);
                            if(tag_alarm_range==tagAlarmRange){
                                etTagAlarmRange.setText(String.valueOf(tagAlarmRange));
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "tag_safe_range_set":
                            int tag_safe_range = Integer.valueOf(etTagSafeRange.getText().toString());
                            int tagSafeRange = ByteUtil.getShort(retBytes);
                            if(tag_safe_range==tagSafeRange){
                                etTagSafeRange.setText(String.valueOf(tagSafeRange));
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "tag_safe_range_extend_set":
                            int tag_safe_range_extend = Integer.valueOf(etTagSafeRangeExtend.getText().toString());
                            int tagSafeRangeExtend = ByteUtil.getShort(retBytes);
                            if(tag_safe_range_extend==tagSafeRangeExtend){
                                etTagSafeRangeExtend.setText(String.valueOf(tagSafeRangeExtend));
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "forklift_alarm_en_fork_set":
                            int forklift_alarm_fork =  spForkLiftAlarmEn_fo.getSelectedItemPosition();
                            forkLiftAlarmEn_fork = retBytes[0];
                            spForkLiftAlarmEn_fo.setAdapter(alarmEnAdapter);
                            spForkLiftAlarmEn_fo.setSelection(forkLiftAlarmEn_fork);
                            if(forklift_alarm_fork == forkLiftAlarmEn_fork){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "forklift_alarm_range_fork_set":
                            int forklift_alarm_range_fork = Integer.valueOf(etForkLiftAlarmRange_fo.getText().toString());
                            int forkLiftAlarmRange_fork = ByteUtil.getShort(retBytes);
                            if(forklift_alarm_range_fork==forkLiftAlarmRange_fork){
                                etForkLiftAlarmRange_fo.setText(String.valueOf(forkLiftAlarmRange_fork));
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "fix_alarm_en_set":
                            int fix_alarm_en =  spFixAlarmEn.getSelectedItemPosition();
                            fixAlarmEn = retBytes[0];
                            spFixAlarmEn.setAdapter(alarmEnAdapter);
                            spFixAlarmEn.setSelection(fixAlarmEn);
                            if(fix_alarm_en==fixAlarmEn){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "low_freq_distance_set":
                            int low_freq_distance = Integer.valueOf(etLowFreqDistance.getText().toString());
                            int lowFreqDistance = ByteUtil.getShort(retBytes);
                            if(low_freq_distance==lowFreqDistance){
                                etLowFreqDistance.setText(String.valueOf(lowFreqDistance));
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "pan_id_fork_set":
                            String pan_id_fork = etPanId_fo.getText().toString();
                            String panId_fork = ByteUtil.bytesToHexFun3(retBytes);
                            etPanId_fo.setText(panId_fork);
                            if(pan_id_fork.equalsIgnoreCase(panId_fork)){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                            //alarm set
                        case "led_alarm_en_set":
                            int led_alarm_en =  spLedAlarmEn.getSelectedItemPosition();
                            ledAlarmEn = retBytes[0];
                            spLedAlarmEn.setAdapter(alarmEnAdapter);
                            spLedAlarmEn.setSelection(ledAlarmEn);
                            if(led_alarm_en==ledAlarmEn){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "sound_alarm_en_set":
                            int sound_alarm_en =  spSoundAlarmEn.getSelectedItemPosition();
                            soundAlarmEn = retBytes[0];
                            spSoundAlarmEn.setAdapter(alarmEnAdapter);
                            spSoundAlarmEn.setSelection(soundAlarmEn);
                            if(sound_alarm_en==soundAlarmEn){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "sound_volume_set":
                            int sound_volume =  sbSoundVolume.getProgress();
                            soundVolume = ByteUtil.getShort(retBytes);
                            tvSoundVolume.setText(String.valueOf(soundVolume));
                            if(sound_volume==soundVolume){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "sound_alarm_mode_set":
                            int sound_alarm_mode = spSoundAlarmMode.getSelectedItemPosition();
                            soundAlarmMode = retBytes[0];
                            spSoundAlarmMode.setAdapter(soundModeAdapter);
                            spSoundAlarmMode.setSelection(soundAlarmMode);
                            if(sound_alarm_mode==soundAlarmMode){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "fixed_mode_ontime_set":
                            int fixed_mode_ontime = Integer.valueOf(etFixedModeOnTime.getText().toString());
                            int fixedModeOntime = retBytes[0];
                            Log.i(TAG,"输入框中的和结果："+fixed_mode_ontime+"-"+fixedModeOntime);
                            etFixedModeOnTime.setText(String.valueOf(fixedModeOntime));
                            if(fixed_mode_ontime==fixedModeOntime){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "fixed_mode_offtime_set":
                            int fixed_mode_offtime = Integer.valueOf(etFixedModeOffTime.getText().toString());
                            int fixedModeOfftime = retBytes[0];
                            etFixedModeOffTime.setText(String.valueOf(fixedModeOfftime));
                            if(fixed_mode_offtime==fixedModeOfftime){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                            //fix alerter set
                        case "fix_id_set":
                            setId = etId_fi.getText().toString();
                            retId = ByteUtil.bytesToHexFun3(retBytes);
                            etId_fi.setText(retId);
                            tvDeviceInfo.setText("AlerterId："+retId+"\t\tType："+deviceTypeStr);
                            if(retId.equalsIgnoreCase(setId)){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "forklift_alarm_en_fix_set":
                            int forklift_alarm_en_fix = spForkLiftAlarmEn_fi.getSelectedItemPosition();
                            forkLiftAlarmEn_fix = retBytes[0];
                            spForkLiftAlarmEn_fi.setAdapter(alarmEnAdapter);
                            spForkLiftAlarmEn_fi.setSelection(forkLiftAlarmEn_fix);
                            if(forklift_alarm_en_fix==forkLiftAlarmEn_fix){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "forklift_alarm_range_fix_set":
                            int forklift_alarm_range_fix = Integer.valueOf(etForkLiftAlarmRange_fi.getText().toString());
                            forkLiftAlarmRange_fix = ByteUtil.getShort(retBytes);
                            etForkLiftAlarmRange_fi.setText(String.valueOf(forkLiftAlarmRange_fix));
                            if(forklift_alarm_range_fix==forkLiftAlarmRange_fix){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        case "pan_id_fix_set":
                            String pan_id_fix = etPanId_fi.getText().toString();
                            String panId_fix = ByteUtil.bytesToHexFun3(retBytes);
                            etPanId_fi.setText(panId_fix);
                            if(pan_id_fix.equalsIgnoreCase(panId_fix)){
                                DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"设置成功！");
                            }else{
                                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"设置失败！");
                            }
                            break;
                        //********************************READ*********************************
                        //forklift alerter read
                        case "reset_forklift_alerter":
//                            DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"复位成功！");
                            break;
                        case "tag_alarm_en_read":
                            tagAlarmEn = retBytes[0];
                            spTagAlarmEn.setAdapter(alarmEnAdapter);
                            spTagAlarmEn.setSelection(tagAlarmEn);
                            break;
                        case "tag_alarm_range_read":
                            tagAlarmRange = ByteUtil.getShort(retBytes);
                            etTagAlarmRange.setText(String.valueOf(tagAlarmRange));
                            break;
                        case "tag_safe_range_read":
                            tagSafeRange = ByteUtil.getShort(retBytes);
                            etTagSafeRange.setText(String.valueOf(tagSafeRange));
                            break;
                        case "tag_safe_range_extend_read":
                            tagSafeRangeExtend = ByteUtil.getShort(retBytes);
                            etTagSafeRangeExtend.setText(String.valueOf(tagSafeRangeExtend));
                            break;
                        case "forklift_alarm_en_fork_read":
                            forkLiftAlarmEn_fork = retBytes[0];
                            spForkLiftAlarmEn_fo.setAdapter(alarmEnAdapter);
                            spForkLiftAlarmEn_fo.setSelection(forkLiftAlarmEn_fork);
                            break;
                        case "forklift_alarm_range_fork_read":
                            forkLiftAlarmRange_fork = ByteUtil.getShort(retBytes);
                            etForkLiftAlarmRange_fo.setText(String.valueOf(forkLiftAlarmRange_fork));

                            break;
                        case "fix_alarm_en_read":
                            fixAlarmEn = retBytes[0];
                            spFixAlarmEn.setAdapter(alarmEnAdapter);
                            spFixAlarmEn.setSelection(fixAlarmEn);
                            break;
                        case "low_freq_distance_read":
                            lowFreqDistance = ByteUtil.getShort(retBytes);
                            etLowFreqDistance.setText(String.valueOf(lowFreqDistance));

                            break;
                        case "pan_id_fork_read":
                            panId_fork = ByteUtil.bytesToHexFun3(retBytes);
                            etPanId_fo.setText(panId_fork);

                            break;
                        case "pan_id_fork_reset":
                            panId_fork = ByteUtil.bytesToHexFun3(retBytes);
                            etPanId_fo.setText(panId_fork);

                            break;
                        //alarm read
                        case "led_alarm_en_read":
                            ledAlarmEn = retBytes[0];
                            spLedAlarmEn.setAdapter(alarmEnAdapter);
                            spLedAlarmEn.setSelection(ledAlarmEn);
                            break;
                        case "sound_alarm_en_read":
                            soundAlarmEn = retBytes[0];
                            spSoundAlarmEn.setAdapter(alarmEnAdapter);
                            spSoundAlarmEn.setSelection(soundAlarmEn);
                            break;
                        case "sound_volume_read":
                            soundVolume = ByteUtil.getShort(retBytes);
                            sbSoundVolume.setProgress(soundVolume);
                            tvSoundVolume.setText(String.valueOf(soundVolume));
                            break;
                        case "sound_alarm_mode_read":
                            soundAlarmMode = retBytes[0];
                            spSoundAlarmMode.setAdapter(soundModeAdapter);
                            spSoundAlarmMode.setSelection(soundAlarmMode);
                            break;
                        case "fixed_mode_ontime_read":
                            fixedModeOntime = retBytes[0];
                            etFixedModeOnTime.setText(String.valueOf(fixedModeOntime));

                            break;
                        case "fixed_mode_offtime_read":
                            fixedModeOfftime = retBytes[0];
                            etFixedModeOffTime.setText(String.valueOf(fixedModeOfftime));

                            break;
                        //fix alerter read
                        case "reset_fix_alerter":
//                            DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"复位成功！");
                            break;
                        case "forklift_alarm_en_fix_read":
                            forkLiftAlarmEn_fix = retBytes[0];
                            spForkLiftAlarmEn_fi.setAdapter(alarmEnAdapter);
                            spForkLiftAlarmEn_fi.setSelection(forkLiftAlarmEn_fix);

                            break;
                        case "forklift_alarm_range_fix_read":
                            forkLiftAlarmRange_fix = ByteUtil.getShort(retBytes);
                            etForkLiftAlarmRange_fi.setText(String.valueOf(forkLiftAlarmRange_fix));

                            break;
                        case "pan_id_fix_read":
                            panId_fix = ByteUtil.bytesToHexFun3(retBytes);
                            etPanId_fi.setText(panId_fix);

                            break;
                        case "pan_id_fix_reset":
                            panId_fix = ByteUtil.bytesToHexFun3(retBytes);
                            etPanId_fi.setText(panId_fix);
                            break;

                    }
                }
            }
        });
    }


    @Override
    public void ftSended() {
        Log.i(TAG,"AlerterSetActivity：：ftSended！");
    }

    @Override
    public void ftSendFail(String failMsg) {
        Log.i(TAG,"AlerterSetActivity：：ftSendFail！");
    }

    int receiveCount = 0;
    @Override
    public void ftRecevied(byte[] recevieMsg) {
        Log.i(TAG,"接受数据："+ByteUtil.bytesToHexFun3(recevieMsg));
        if(operateType==ConstantUtil.READ_ALL_OPERATE){
            if(deviceType==1){
                receiveCount++;
                if(receiveCount==ftComUtil.FORKLIFT_READ_ORDER.length){
                    receiveCount = 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"操作成功！");
                        }
                    });
                }
            }else if(deviceType==2){
                receiveCount++;
                if(receiveCount==ftComUtil.FIX_READ_ORDER.length){
                    receiveCount = 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"操作成功！");
                        }
                    });
                }
            }
        }else if(operateType==ConstantUtil.READ_ONE_OPERATE){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.showWait(asActivity,DialogUtil.OK_DIALOG,"操作成功！");
                }
            });
        }else if(operateType==ConstantUtil.SET_ONE_OPERATE){
            //DialogUtil.showWait(this,DialogUtil.OK_DIALOG,"设置完毕！");
        }
        HashMap<String,byte[]> retMap = FtAnalysisUtil.analysisData(recevieMsg);
        showReceviedContent(retMap);
    }

    @Override
    public void ftRecevieOutTime() {
        Log.i(TAG,"是否取消了："+cancelOperate);
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
                DialogUtil.showWait(asActivity,DialogUtil.ERROR_DIALOG,"操作超时！");
            }
        });
    }
}
