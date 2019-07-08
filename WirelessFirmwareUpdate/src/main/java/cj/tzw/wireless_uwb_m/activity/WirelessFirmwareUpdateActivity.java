package cj.tzw.wireless_uwb_m.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.AppCompatCheckBox;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import cj.tzw.base_uwb_m.adapter.PageAdapter;
import cj.tzw.base_uwb_m.callback.FtSrCallback;
import cj.tzw.base_uwb_m.model.Device;
import cj.tzw.base_uwb_m.router.RouterPathMethod;
import cj.tzw.base_uwb_m.utils.ByteUtil;
import cj.tzw.base_uwb_m.utils.DialogUtil;
import cj.tzw.base_uwb_m.utils.FtAnalysisUtil;
import cj.tzw.base_uwb_m.utils.FtBaseUtil;
import cj.tzw.base_uwb_m.utils.FtHandleUtil;
import cj.tzw.wireless_uwb_m.activity.utils.CJTableRow;
import cj.tzw.wireless_uwb_m.activity.utils.ConvertStatus;
import cj.tzw.wireless_uwb_m.activity.utils.DevicesUpdateConf;
import cj.tzw.wireless_uwb_m.activity.utils.HexToBin;
import cj.tzw.wireless_uwb_m.activity.utils.ImageMarkConf;
import cj.tzw.wireless_uwb_m.activity.utils.UpdateStatus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Route(path = RouterPathMethod.WIRELESS_FIRMWARE_UPDATE_PATH)
public class WirelessFirmwareUpdateActivity  extends AppCompatActivity implements FtSrCallback {
    private String TAG="WirelessFirmwareUpdateActivity";
    private WirelessFirmwareUpdateActivity WFActivity=this;
    private Button btnOpenFile,btnStartUpdate,btnReadStatus,btnStopUpdate;
    private EditText etFilePath,etUpdateVersion;
    private TextView tvFType,tvFVersion,tvFSize,tvDFVersion,tvAskToUpdateEn,tvWaitForUpdateEn,tvFirmwareType,tvFirmwareVersion,tvFirmwareSize,tvNeedUpdateVersion,tvTotal,tvSuccess,tvFailure;
    private TextView tvPromptStr;
    private AppCompatCheckBox cbAskToUpdate;
    private byte clickSelect=0;
    private byte transStatus=0;
    private byte[] sendRequestBuf;
    private Thread threadUpdate=null;
    private ProgressBar updateProgressBar;
    private int progressBarLength;
    private LayoutInflater inflater;
    private TabLayout tab;
    private ViewPager vp;
    private ArrayList<View> viewList;
    private View usbDongleView,resultView,tableRow;

    private LinearLayout tableLayout;
    private List<CJTableRow> cJTableRows=new ArrayList<>();
    private LinearLayout titleLinearLayout;
    private TextView textView1,textView2,textView3,textView4;
    private void openAssignFolder(String path){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("file/*");
        try {
            startActivityForResult(intent,1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
    public ImageMarkConf[] deviceImageMarkConf=new ImageMarkConf[]{new ImageMarkConf(0x12121212, "Locate_Anchor-V1"), new ImageMarkConf(0x34343434, "Locate_TD-V1"),
            new ImageMarkConf(0x80808080, "Locate_Tag_UTAG-9056-V1.3"),new ImageMarkConf(0x81818181, "Locate_Tag_UTAG-8141-V1.3/V1.4"), new ImageMarkConf(0x82828282, "Locate_Tag_UTAG-7045-V1.4/V2.0"),
            new ImageMarkConf(0x83838383, "Locate_Tag_UTAG-H02-V1.2"),new ImageMarkConf(0x84848484, "Locate_Tag_UTAG-9056-V1.01"),  new ImageMarkConf(0x85858585, "Locate_Tag_UTAG-5136-V1.1/V1.3"),
            new ImageMarkConf(0x86868686, "Locate_Tag_UTAG-9060WPC-V1.2"),new ImageMarkConf(0x87878787, "Locate_Tag_UTAG-H03-V1.0"), new ImageMarkConf(0x88888888, "Locate_Tag_UTAG-M02-V1.0/Locate_Tag_UTAG-4937-V1.0"),
            new ImageMarkConf(0x89898989, "Locate_Tag_UTAG-7045-V2.2/V2.3/V3.0"),new ImageMarkConf(0x8A8A8A8A, "Locate_Tag_UTAG-9056-V2.1/V2.2"),

            new ImageMarkConf(0x19011220, "India_NormalAlarmCar_UTAG-7045-V2.2/V2.3/V3.0"),
            new ImageMarkConf(0x19011221, "India_NormalAlarmCar_UTAG-SL90-V1.0/V1.2"),
            new ImageMarkConf(0x19011240, "India_RelayAlarmCar_UTAG-7045-V2.2/V2.3/V3.0"),
            new ImageMarkConf(0x19011260, "India_PersonCounter_Anchor-V1"),
            new ImageMarkConf(0x19011280, "India_Tag_UTAG-5136-V1.1/V1.3"),

            new ImageMarkConf(0x19012520, "Alarm_FixedAlarm_Anchor-V1"),
            new ImageMarkConf(0x19012521, "Alarm_FixedAlarm_UTAG-SL90-V1.0/V1.2"),
            new ImageMarkConf(0x19012540, "Alarm_ForkliftAlarm_Anchor-V1"),
            new ImageMarkConf(0x19012541, "Alarm_ForkliftAlarm_UTAG-7045-V1.4/V2.0"),
            new ImageMarkConf(0x19012542, "Alarm_ForkliftAlarm_UTAG-7045-V2.2/V2.3/V3.0"),
            new ImageMarkConf(0x19012543, "Alarm_ForkliftAlarm_UTAG-SL90-V1.0/V1.2"),
            new ImageMarkConf(0x19012560, "Alarm_Tag_UTAG-5136-V1.0"),
            new ImageMarkConf(0x19012561, "Alarm_Tag_UTAG-5136-V1.1/V1.3"),
            new ImageMarkConf(0x19012562, "Alarm_Tag_UTAG-9056-V1.3"),
            new ImageMarkConf(0x19012563, "Alarm_Tag_UTAG-9056-V2.1/V2.2"),
            new ImageMarkConf(0x19012564, "Alarm_Tag_UTAG-H02-V1.2")};

    public ImageMarkConf[] dongleImageMarkConf = new ImageMarkConf[]{
            new ImageMarkConf(0x70707070, "Locate_Dongle_UWB-USB-01-V01.00"),
            new ImageMarkConf(0x19011200, "India_Dongle_UWB-USB-01-V01.00"),
            new ImageMarkConf(0x19012500, "Alarm_Dongle_UWB-USB-01-V01.00")};


    private static byte[] buffer=new byte[100*1024];
    private static String upLoadFilePath;
    private static int binImageMark = 0;
    private static int binVersion = 0;
    private static String binType = "";
    private static int[] binSize = new int[]{0};
    private static int binCheckSum = 0;
    private int needUpdateVersion=0;
    private int deviceBinMaxSize=80*1024;
    private int dongleBinMaxSize=40*1024;
    private static boolean isDongleImage = false;                     //固件是否为dongle固件

    private FtHandleUtil ftHandleUtil;
    private Device currentDevice;
    private FtBaseUtil ftBaseUtil;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("myapplication", "onActivityResult: requestCode:"+requestCode+"resultCode"+resultCode+"data"+data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Log.i(TAG, "onActivityResult: "+uri);
                if (uri != null) {
                    File file = new File(uri.toString().substring(7));
                    if (file.exists()) {
                        upLoadFilePath = file.toString();
                        String fileType=upLoadFilePath.substring(upLoadFilePath.lastIndexOf(".")+1,upLoadFilePath.length());
                        if("hex".equalsIgnoreCase(fileType)||"bin".equalsIgnoreCase(fileType)){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String fileTypeRun=upLoadFilePath.substring(upLoadFilePath.lastIndexOf(".")+1,upLoadFilePath.length());
                                    FileInputStream fileInputStream=null;
                                    BufferedInputStream in=null;
                                    boolean index=false;
                                    try {
                                        fileInputStream=new FileInputStream(upLoadFilePath);
                                        in=new BufferedInputStream(fileInputStream);
                                        List<Integer> integers=new ArrayList<Integer>();
                                        int read=0;
                                        int byteSize=0;
                                        while ((read=in.read())!=-1){
                                            integers.add(read);
                                            byteSize++;
                                        }
                                        int[] buf=new int[byteSize];
                                        for(int i=0;i<byteSize;i++){
                                            buf[i]=integers.get(i);
                                        }
                                        if(byteSize==deviceBinMaxSize*4||byteSize==0){
                                            Looper.prepare();
                                            Toast.makeText(WFActivity, "The hex file is too large or the hex file is empty!", Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                            WFActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    etFilePath.setText("");
                                                    tvFType.setText("");
                                                    tvFVersion.setText("");
                                                    tvFSize.setText("");
                                                    btnStartUpdate.setEnabled(false);
                                                }
                                            });
                                            return;
                                        }

                                        if("hex".equalsIgnoreCase(fileTypeRun)){
                                            HexToBin hexToBin=new HexToBin();
                                            ConvertStatus res=hexToBin.HexFileToBinFile(buf,buffer,binSize);
                                            if(res.getStatus()==0){
                                                index=true;
                                            }
                                        }else if("bin".equalsIgnoreCase(fileTypeRun)){
                                            binSize[0]=0;
                                            for(int i=0;i<buf.length;i++){
                                                buffer[i]=(byte)buf[i];
                                                binSize[0]++;
                                            }
                                            index = true;
                                        }else{
                                            index=true;
                                            binSize[0]=byteSize;
                                        }
                                        if(index){
                                            int imageMarkNum=-1;
                                            int imageMark=(buffer[8195] << 24) + (buffer[8194] << 16) + (buffer[8193] << 8) + (buffer[8192]);
                                            for(int j=0;j<deviceImageMarkConf.length;j++){
                                                if(imageMark==deviceImageMarkConf[j].getImageMark()){
                                                    imageMarkNum=j;
                                                    isDongleImage=false;
                                                    break;
                                                }
                                            }
                                            if(imageMark==-1){
                                                for(int j=0;j<dongleImageMarkConf.length;j++){
                                                    if(imageMark==dongleImageMarkConf[j].getImageMark()){
                                                        imageMarkNum=j;
                                                        isDongleImage=true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if(imageMark!=-1){
                                                int maxBinSize=0;
                                                if(isDongleImage){
                                                    maxBinSize=dongleBinMaxSize;
                                                }else{
                                                    maxBinSize=deviceBinMaxSize;
                                                }

                                                if(binSize[0]>maxBinSize){
                                                    Looper.prepare();
                                                    Toast.makeText(WFActivity, "The file is too large!", Toast.LENGTH_SHORT).show();
                                                    Looper.loop();

                                                    WFActivity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            etFilePath.setText("");
                                                            tvFType.setText("");
                                                            tvFVersion.setText("");
                                                            tvFSize.setText("");
                                                            btnStartUpdate.setEnabled(false);
                                                        }
                                                    });

                                                    return;
                                                }
                                                binImageMark=imageMark;
                                                binVersion=(buffer[8199] << 24) + (buffer[8198] << 16) + (buffer[8197] << 8) + (buffer[8196]);
                                                if (isDongleImage){
                                                    binType = dongleImageMarkConf[imageMarkNum].getType();
                                                }
                                                else{
                                                    binType = deviceImageMarkConf[imageMarkNum].getType();
                                                }
                                                WFActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        etFilePath.setText(upLoadFilePath);
                                                        tvFType.setText(binType);
                                                        tvFVersion.setText("20" + Integer.toString((byte)(binVersion >> 24),16) + "-" + Integer.toString(((byte)(binVersion >> 16)),16) + "-" + Integer.toString((byte)(binVersion >> 8),16) +
                                                                " V" + Integer.toString((byte)binVersion,16).substring(0, 1) + "." + Integer.toString((byte)binVersion,16).substring(1, 2));
                                                        tvFSize.setText(binSize[0]/1000.000+"KB");
                                                        btnStartUpdate.setEnabled(true);
                                                    }
                                                });

                                            }
                                            binCheckSum=0;
                                            for(int i=0;i<binSize[0];i++){
                                                if(buffer[i]<0){
                                                    binCheckSum+=256+buffer[i];
                                                }else{
                                                    binCheckSum+=buffer[i];
                                                }

                                            }
                                        }else{
                                            Looper.prepare();
                                            Toast.makeText(WFActivity, "Illegally firmware!", Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                            WFActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    etFilePath.setText("");
                                                    tvFType.setText("");
                                                    tvFVersion.setText("");
                                                    tvFSize.setText("");
                                                    btnStartUpdate.setEnabled(false);
                                                }
                                            });
                                            return;
                                        }
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }finally {
                                        try {
                                            fileInputStream.close();
                                            in.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                        }else{
                            DialogUtil.showWait(WFActivity,DialogUtil.ERROR_DIALOG,"文件格式不正确，请选择hex或bin文件！");
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_firmware_update);
        initView();
        initEvent();
        }


    private SpannableStringBuilder getChar(String host,int start,int end,int color){
        SpannableStringBuilder ssb=new SpannableStringBuilder(host);
        ssb.setSpan(new ForegroundColorSpan(color),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new AbsoluteSizeSpan(color,false),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;

    }

    private void initView(){
        tab=findViewById(R.id.tab);
        vp=findViewById(R.id.vp);
        inflater = LayoutInflater.from(this);
        usbDongleView=inflater.inflate(R.layout.usb_dongle_layout,null);
        resultView=inflater.inflate(R.layout.resoult_layout,null);
        tableRow=inflater.inflate(R.layout.tablerow_layout,null);


        tableLayout=resultView.findViewById(R.id.tableLayout);
        titleLinearLayout=resultView.findViewById(R.id.titleLinearLayout);
        textView1=resultView.findViewById(R.id.tvDeviceID);
        textView2=resultView.findViewById(R.id.tvType);
        textView3=resultView.findViewById(R.id.tvFirmwareVersion);
        textView4=resultView.findViewById(R.id.tvUpdateStatus);
        updateProgressBar=findViewById(R.id.updateProgressBar);
        viewList=new ArrayList();
        viewList.add(usbDongleView);
        viewList.add(resultView);


        btnOpenFile=findViewById(R.id.btnOpenFile);
        btnStartUpdate=findViewById(R.id.btnStartUpdate);
        btnReadStatus=usbDongleView.findViewById(R.id.btnReadStatus);
        btnStopUpdate=usbDongleView.findViewById(R.id.btnStopUpdate);
        etFilePath=findViewById(R.id.etFilePath);
        etUpdateVersion=findViewById(R.id.etUpdateVersion);

        cbAskToUpdate=findViewById(R.id.cbAskToUpdate);

        tvFType=findViewById(R.id.tvFType);
        tvFVersion=findViewById(R.id.tvFVersion);
        tvFSize=findViewById(R.id.tvFSize);

        tvDFVersion=usbDongleView.findViewById(R.id.tvDFVersion);
        tvAskToUpdateEn=usbDongleView.findViewById(R.id.tvAskToUpdateEn);
        tvWaitForUpdateEn=usbDongleView.findViewById(R.id.tvWaitForUpdateEn);
        tvFirmwareType=usbDongleView.findViewById(R.id.tvFirmwareType);
        tvFirmwareVersion=usbDongleView.findViewById(R.id.tvFirmwareVersion);
        tvFirmwareSize=usbDongleView.findViewById(R.id.tvFirmwareSize);
        tvNeedUpdateVersion=usbDongleView.findViewById(R.id.tvNeedUpdateVersion);


        ViewGroup.LayoutParams params=vp.getLayoutParams();
        params.height=800;
        vp.setLayoutParams(params);
        vp.setAdapter(new PageAdapter(viewList,3));
        tab.setupWithViewPager(vp);

        currentDevice = getIntent().getParcelableExtra("device");
        ftBaseUtil=FtBaseUtil.getInstance(getApplicationContext()).setFtSrCallback(this);
        ftHandleUtil = new FtHandleUtil(ftBaseUtil,currentDevice);
        tvTotal=findViewById(R.id.tvTotal);
        tvSuccess=findViewById(R.id.tvSuccess);
        tvFailure=findViewById(R.id.tvFailure);

        tvPromptStr = findViewById(R.id.tvPromptStr);


    }

    private void initEvent(){

        btnOpenFile.setOnClickListener(clickListener);
        btnStartUpdate.setOnClickListener(clickListener);
        Log.i(TAG, "initEvent: "+btnReadStatus);
        btnReadStatus.setOnClickListener(clickListener);
        btnStopUpdate.setOnClickListener(clickListener);

    }

    @Override
    public void ftSended() {

    }

    @Override
    public void ftSendFail(String failMsg) {

    }

    private View.OnFocusChangeListener focusChangeListener=new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            int id = v.getId();
            if(id==R.id.etUpdateVersion){
                tvPromptStr.setText("range：（0-65535,unit:second）");
            }

        }
    };


    private View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int vId = v.getId();

            if(vId==R.id.btnOpenFile){
                clickSelect=1;
                openAssignFolder("/");
            }else if(vId==R.id.btnStartUpdate){

                if("Start Update".equals(btnStartUpdate.getText())){
                    tvTotal.setText("Total:");
                    tvSuccess.setText("Success:");
                    tvFailure.setText("Failure:");
//                    progressBarLength=0;
//                    updateProgressBar.setProgress(0);
                    isStopRun=false;
                    for(CJTableRow row:cJTableRows){
                        tableLayout.removeView(row);
                    }
                    if("".equals(etUpdateVersion.getText().toString())){
                        needUpdateVersion=0;
                    }else{
                        try{
                            needUpdateVersion=Integer.parseInt(etUpdateVersion.getText().toString(),16);
                            etUpdateVersion.setText(formatStringVersion(etUpdateVersion.getText().toString()));
                        }catch (NumberFormatException e){
                            DialogUtil.showWait(WFActivity,DialogUtil.ERROR_DIALOG,"Need Update Version格式错误！");

                            e.printStackTrace();
                            return;
                        }
//                        etUpdateVersion.setText(String.format("%08d",Integer.parseInt(etUpdateVersion.getText().toString())));


                    }
                    btnStartUpdate.setText("Stop Update");
                    sendRequestBuf=new byte[21];
                    int length=0;
//                    Log.i(TAG, "onClick: binImageMark:"+binImageMark+"  binVersion:"+binVersion+"  binSize:"+binSize[0]+"  binCheckSum:"+binCheckSum+"  needUpdateVersion:"+needUpdateVersion);
                    sendRequestBuf[length++]=(byte)(binImageMark >> 24);
                    sendRequestBuf[length++]=(byte)(binImageMark >> 16);
                    sendRequestBuf[length++]=(byte)(binImageMark >> 8);
                    sendRequestBuf[length++]=(byte)(binImageMark);
                    sendRequestBuf[length++]=(byte)(binVersion >> 24);
                    sendRequestBuf[length++]=(byte)(binVersion >> 16);
                    sendRequestBuf[length++]=(byte)(binVersion >> 8);
                    sendRequestBuf[length++]=(byte)(binVersion);
                    sendRequestBuf[length++]=(byte)(binSize[0] >> 16);
                    sendRequestBuf[length++]=(byte)(binSize[0] >> 8);
                    sendRequestBuf[length++]=(byte)(binSize[0]);
                    sendRequestBuf[length++]=(byte)(binCheckSum >> 24);
                    sendRequestBuf[length++]=(byte)(binCheckSum >> 16);
                    sendRequestBuf[length++]=(byte)(binCheckSum >> 8);
                    sendRequestBuf[length++]=(byte)(binCheckSum);
                    sendRequestBuf[length++]=(byte)(needUpdateVersion >> 24);
                    sendRequestBuf[length++]=(byte)(needUpdateVersion >> 16);
                    sendRequestBuf[length++]=(byte)(needUpdateVersion >> 8);
                    sendRequestBuf[length++]=(byte)(needUpdateVersion);
                    if(cbAskToUpdate.isChecked()){
                        sendRequestBuf[length++] = 0x55;
                    }else{
                        sendRequestBuf[length++] = 0;
                    }
                    sendRequestBuf[length++] = 0x55;
                    if(threadUpdate!=null){
                        threadUpdate.interrupt();
//                        threadUpdate.stop();
                    }
                    threadUpdate=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ftHandleUtil.requestTransFirmWare(sendRequestBuf);
                            transStatus=1;
                        }
                    });
                    threadUpdate.start();
                }else{
                    isStopRun=true;
                    btnStartUpdate.setText("Start Update");
                }
                clickSelect=2;
            }else if(vId==R.id.btnReadStatus){
                dongleStatusRecevieMsgLength=0;
                clickSelect=3;
                ftHandleUtil.readDongleStatus();
            }else if(vId==R.id.btnStopUpdate){
                clickSelect=4;
                ftHandleUtil.stopDongleUpdate();
            }else{
                clickSelect=0;
            }



        }
    };
    private  static HashMap<String,byte[]> map;

    private String formatStringVersion(String str){
        if(str.length()<8){
            char[] strChars=new char[8];
            for(int i=0;i<8-str.length();i++){
                strChars[i]='0';
            }
            boolean isOk=false;
            for(int i=8-str.length();i<8;i++){
                char strChar= str.charAt(i-8+str.length());
                if('a'<=strChar&&strChar<='f'||'A'<=strChar&&strChar<='F'||'0'<=strChar&&strChar<='9'){
                    strChars[i]=strChar;
                }else{
                    isOk=true;

                }

            }
            Log.i(TAG, "formatStringVersion: "+String.valueOf(strChars));
            if(!isOk){
                return String.valueOf(strChars);
            }
        }
        return null;
    }

    private static boolean isStopRun=false;

    //开始传输
    private void startTransFirmWare(){
        if(transStatus==2){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnReadStatus.setEnabled(false);
                    btnStopUpdate.setEnabled(false);
                    btnOpenFile.setEnabled(false);
                }
            });
            byte[] sendTransFirmWareBuf=new byte[131];
            int addr=0;
            int txLen=0;
            int dataNum=0;
            //分包传输
            while (addr<binSize[0]){
                if(isStopRun){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnReadStatus.setEnabled(true);
                            btnStopUpdate.setEnabled(true);
                            btnOpenFile.setEnabled(true);
                        }
                    });
                    return;
                }
                if(binSize[0]-addr>128){
                    dataNum=128;
                }else{
                    dataNum=(int)(binSize[0]-addr);
                }
                txLen=0;
                sendTransFirmWareBuf[txLen++]=(byte)(addr>>16);
                sendTransFirmWareBuf[txLen++]=(byte)(addr>>8);
                sendTransFirmWareBuf[txLen++]=(byte)(addr);
                for (int i=0;i<dataNum;i++){
                    sendTransFirmWareBuf[txLen++]=buffer[addr+i];
                }
                if(dataNum!=128){
                    for (int i = 0; i < 128 - dataNum; i++)
                    {
                        sendTransFirmWareBuf[txLen++] = (byte)0xFF;
                    }
                }
                ftHandleUtil.startTransFirmWare(sendTransFirmWareBuf);
                addr += 128;
                progressBarLength++;
                try {
                    Thread.sleep(100);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateProgressBar.setProgress(100*128*progressBarLength/binSize[0]);
                        }
                    });
                }catch (InterruptedException e){
                    Log.e(TAG, "startTransFirmWare: ",e );
                }
            }
        }
    }


    private void transFirmWareComplete(){
        byte[] sendCompleteBuf = new byte[21];
        int txLen=0;

        sendCompleteBuf[txLen++]=(byte)(binImageMark >> 24);
        sendCompleteBuf[txLen++]=(byte)(binImageMark >> 16);
        sendCompleteBuf[txLen++]=(byte)(binImageMark >> 8);
        sendCompleteBuf[txLen++]=(byte)(binImageMark);
        sendCompleteBuf[txLen++]=(byte)(binVersion >> 24);
        sendCompleteBuf[txLen++]=(byte)(binVersion >> 16);
        sendCompleteBuf[txLen++]=(byte)(binVersion >> 8);
        sendCompleteBuf[txLen++]=(byte)(binVersion);
        sendCompleteBuf[txLen++]=(byte)(binSize[0] >> 16);
        sendCompleteBuf[txLen++]=(byte)(binSize[0] >> 8);
        sendCompleteBuf[txLen++]=(byte)(binSize[0]);
        sendCompleteBuf[txLen++]=(byte)(binCheckSum >> 24);
        sendCompleteBuf[txLen++]=(byte)(binCheckSum >> 16);
        sendCompleteBuf[txLen++]=(byte)(binCheckSum >> 8);
        sendCompleteBuf[txLen++]=(byte)(binCheckSum);
        sendCompleteBuf[txLen++]=(byte)(needUpdateVersion >> 24);
        sendCompleteBuf[txLen++]=(byte)(needUpdateVersion >> 16);
        sendCompleteBuf[txLen++]=(byte)(needUpdateVersion >> 8);
        sendCompleteBuf[txLen++]=(byte)(needUpdateVersion);
        if(cbAskToUpdate.isChecked()){
            sendCompleteBuf[txLen++] = 0x55;
        }else{
            sendCompleteBuf[txLen++] = 0;
        }
        sendCompleteBuf[txLen++] = 0x55;
        ftHandleUtil.transFirmWareComplete(sendCompleteBuf);
    }

    private String transString="";
    List<DevicesUpdateConf> deviceUpdateConfList = new ArrayList<DevicesUpdateConf>();

    private byte[] dongleStatusRecevieMsg=new byte[26];
    private int dongleStatusRecevieMsgLength;
    private byte[] dongleUpdateStatusRecevieMsg=new byte[15];
    private int dongleUpdateStatusRecevieMsgLength;

    //接收传输后的消息
    @Override
    public void ftRecevied(byte[] recevieMsg) {
        if(recevieMsg.length>1&&(recevieMsg[1]==(byte)0x21||recevieMsg[1]==(byte)0x22)){
            dongleUpdateStatusRecevieMsgLength=recevieMsg.length;
            for(int i=0;i<recevieMsg.length;i++){
                dongleUpdateStatusRecevieMsg[i]=recevieMsg[i];
            }
        }else if(recevieMsg[recevieMsg.length-1]==(byte)0x9E){
            if((recevieMsg.length+dongleUpdateStatusRecevieMsgLength)==dongleUpdateStatusRecevieMsg.length){
                for(int i=0;i<recevieMsg.length;i++){
                    dongleUpdateStatusRecevieMsg[i+dongleUpdateStatusRecevieMsgLength]=recevieMsg[i];
                }
                dongleUpdateStatusRecevieMsgLength++;
            }
        }else{
            for(int i=0;i<recevieMsg.length;i++){
                if((i+dongleUpdateStatusRecevieMsgLength)<dongleUpdateStatusRecevieMsg.length){
                    dongleUpdateStatusRecevieMsg[i+dongleUpdateStatusRecevieMsgLength]=recevieMsg[i];
                    dongleUpdateStatusRecevieMsgLength++;
                }
            }
        }
        if(dongleUpdateStatusRecevieMsg.length>1&&(dongleUpdateStatusRecevieMsg[1]==(byte)0x21||dongleUpdateStatusRecevieMsg[1]==(byte)0x22)){
            if(dongleUpdateStatusRecevieMsg.length>=4&&dongleUpdateStatusRecevieMsg[0]==(byte)0xE9&&(dongleUpdateStatusRecevieMsg[1]==(byte)0x21||dongleUpdateStatusRecevieMsg[1]==(byte)0x22)&&dongleUpdateStatusRecevieMsg[dongleUpdateStatusRecevieMsg.length-1]==(byte)0x9E){
                byte sum=0;
                for(int i=0;i<dongleUpdateStatusRecevieMsg.length-2;i++){
                    sum+=dongleUpdateStatusRecevieMsg[i];
                }
                if(sum==dongleUpdateStatusRecevieMsg[dongleUpdateStatusRecevieMsg.length-2]){

                    int binImageMarka=dongleUpdateStatusRecevieMsg[5];
                    int binImageMarkb=dongleUpdateStatusRecevieMsg[6];
                    int binImageMarkc=dongleUpdateStatusRecevieMsg[7];
                    int binImageMarkd=dongleUpdateStatusRecevieMsg[8];
                    if(dongleUpdateStatusRecevieMsg[5]<0){
                        binImageMarka=256+binImageMarka;
                    }
                    if(dongleUpdateStatusRecevieMsg[6]<0){
                        binImageMarkb=256+binImageMarkb;
                    }
                    if(dongleUpdateStatusRecevieMsg[7]<0){
                        binImageMarkc=256+binImageMarkc;
                    }
                    if(dongleUpdateStatusRecevieMsg[8]<0){
                        binImageMarkd=256+binImageMarkd;
                    }

                    if(((binImageMarka << 24)+(binImageMarkb << 16)+(binImageMarkc << 8)+binImageMarkd)==binImageMark&&dongleUpdateStatusRecevieMsg.length==15){
                        //dongle上传设备上报消息
                        if(dongleUpdateStatusRecevieMsg[1]==(byte)0x21){
                            boolean isExist=false;
                            for(DevicesUpdateConf item:deviceUpdateConfList){
                                if(item.getId()[0]==dongleUpdateStatusRecevieMsg[2]&&item.getId()[1]==dongleUpdateStatusRecevieMsg[3]){
                                    isExist=true;
                                    if(item.getVersion()[0]!=dongleUpdateStatusRecevieMsg[9]||item.getVersion()[1]!=dongleUpdateStatusRecevieMsg[10]||item.getVersion()[2]!=dongleUpdateStatusRecevieMsg[11]||item.getVersion()[3]!=dongleUpdateStatusRecevieMsg[12]||item.getUpdateStatus().getUpdateStatus()!=dongleUpdateStatusRecevieMsg[4]){
                                        item.setVersion(new byte[]{dongleUpdateStatusRecevieMsg[9],dongleUpdateStatusRecevieMsg[10],dongleUpdateStatusRecevieMsg[11],dongleUpdateStatusRecevieMsg[12]});
                                        item.setUpdateStatus(new UpdateStatus(dongleUpdateStatusRecevieMsg[4]));
                                    }
                                    evLvDeviceFlush();
                                    break;
                                }
                            }
                            if(!isExist){
                                DevicesUpdateConf devicesUpdateConf=new DevicesUpdateConf();
                                devicesUpdateConf.setId(new byte[]{dongleUpdateStatusRecevieMsg[2],dongleUpdateStatusRecevieMsg[3]});
                                devicesUpdateConf.setUpdateStatus(new UpdateStatus(dongleUpdateStatusRecevieMsg[4]));
                                devicesUpdateConf.setVersion(new byte[]{dongleUpdateStatusRecevieMsg[9],dongleUpdateStatusRecevieMsg[10],dongleUpdateStatusRecevieMsg[11],dongleUpdateStatusRecevieMsg[12]});
                                devicesUpdateConf.setType(tvFType.getText().toString());
                                deviceUpdateConfList.add(devicesUpdateConf);
                                evLvDeviceFlush();
                            }
                        }else if(dongleUpdateStatusRecevieMsg[1]==(byte)0x22){
                            boolean isExist=false;
                            for(DevicesUpdateConf item:deviceUpdateConfList){
                                if(item.getId()[0]==dongleUpdateStatusRecevieMsg[2]&&item.getId()[1]==dongleUpdateStatusRecevieMsg[3]){
                                    isExist=true;
                                    item.setVersion(new byte[]{dongleUpdateStatusRecevieMsg[9],dongleUpdateStatusRecevieMsg[10],dongleUpdateStatusRecevieMsg[11],dongleUpdateStatusRecevieMsg[12]});
                                    if(dongleUpdateStatusRecevieMsg[4]==0){
                                        item.setUpdateStatus(new UpdateStatus(UpdateStatus.updateSuccessful));
                                    }else {
                                        item.setUpdateStatus(new UpdateStatus(UpdateStatus.updateFailed));
                                    }
//                                    evLvDeviceFlush();
                                    break;
                                }
                                if(!isExist){
                                    DevicesUpdateConf devicesUpdateConf=new DevicesUpdateConf();
                                    devicesUpdateConf.setId(new byte[]{dongleUpdateStatusRecevieMsg[2],dongleUpdateStatusRecevieMsg[3]});
                                    if(dongleUpdateStatusRecevieMsg[4]==0){
                                        item.setUpdateStatus(new UpdateStatus(UpdateStatus.updateSuccessful));
                                    }else {
                                        item.setUpdateStatus(new UpdateStatus(UpdateStatus.updateFailed));
                                    }
                                    devicesUpdateConf.setVersion(new byte[]{dongleUpdateStatusRecevieMsg[9],dongleUpdateStatusRecevieMsg[10],dongleUpdateStatusRecevieMsg[11],dongleUpdateStatusRecevieMsg[12]});
                                    devicesUpdateConf.setType(tvFType.getText().toString());
//                                    deviceUpdateConfList.add(devicesUpdateConf);
//                                    evLvDeviceFlush();
                                }
                            }
                        }
                        dongleUpdateStatusRecevieMsg=new byte[15];
                    }

                }
            }
        }
        Log.i(TAG, "ftRecevied: "+ ByteUtil.bytesToHexFun3(recevieMsg)+recevieMsg.length+"ThreadName"+Thread.currentThread().getName()+"addr:"+"transStatus"+transStatus+"clickSelect"+clickSelect );
        if(transStatus==3&&recevieMsg.length==5&&recevieMsg[0]==(byte)0xE9&&recevieMsg[1]==(byte)0x03&&recevieMsg[recevieMsg.length-1]==(byte)0x9E){
            byte sum=0;
            for(int i=0;i<recevieMsg.length-2;i++){
                sum+=(recevieMsg[i]&0xff);
            }
            if(sum==recevieMsg[recevieMsg.length-2]){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBarLength=0;
                        updateProgressBar.setProgress(0);
                        btnReadStatus.setEnabled(true);
                        btnStopUpdate.setEnabled(true);
                        btnOpenFile.setEnabled(true);
                        DialogUtil.showWait(WFActivity,DialogUtil.OK_DIALOG,"传输完成！");
                        btnStartUpdate.setText("Start Update");
                        transStatus=4;

                    }
                });
            }
        }
        if(transStatus==2){
            transString+=ByteUtil.bytesToHexFun3(recevieMsg);
            boolean isComplete=true;
            if(transString.length()==(14*(binSize[0]/128+1))){
                transFirmWareComplete();
                transStatus=3;
            }
        }
        if(clickSelect==2) {
            if (transStatus == 1) {
                transStatus = 2;
                if(recevieMsg.length == 5 && recevieMsg[0] == (byte)0xE9 && recevieMsg[1] == (byte)0x01&&recevieMsg[2]==(byte)0x00&&recevieMsg[3]==(byte)0xEA && recevieMsg[recevieMsg.length - 1] == (byte)0x9E) {
                    transString="";
                    startTransFirmWare();
                }else if(recevieMsg.length == 5 && recevieMsg[0] == (byte)0xE9 && recevieMsg[1] == (byte)0x01&& recevieMsg[recevieMsg.length - 1] == (byte)0x9E){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnReadStatus.setEnabled(true);
                            btnStopUpdate.setEnabled(true);
                            btnOpenFile.setEnabled(true);
                            btnStartUpdate.setText("Start Update");
                        }
                    });

                    switch (recevieMsg[2]){
                        case 1:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtil.showWait(WFActivity,DialogUtil.ERROR_DIALOG,"Version same");

                                }
                            });
                            break;
                        case 2:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtil.showWait(WFActivity,DialogUtil.ERROR_DIALOG,"Firmware too large");
                                }
                            });
                            break;
                        case 3:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtil.showWait(WFActivity,DialogUtil.ERROR_DIALOG,"No enable updates");
                                }
                            });
                            break;
                        case 4:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtil.showWait(WFActivity,DialogUtil.ERROR_DIALOG,"NeedUpdateVersion can not be the same as Firmware Version");
                                }
                            });
                            break;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogUtil.showWait(WFActivity,DialogUtil.ERROR_DIALOG,"Unknown error");
                                    }
                                });
                                break;
                    }
                }
            }
            }else if (clickSelect == 3) {

                if(recevieMsg.length!=26){
                if(recevieMsg[0]==(byte)0xE9&&recevieMsg[1]==(byte)0x41){
                    dongleStatusRecevieMsgLength=recevieMsg.length;
                    for(int j=0;j<recevieMsg.length;j++){
                        dongleStatusRecevieMsg[j]=recevieMsg[j];
                    }
                }else if(recevieMsg[recevieMsg.length-1]==(byte)0x9E&&((dongleStatusRecevieMsgLength+recevieMsg.length)==dongleStatusRecevieMsg.length)){
                    for(int j=0;j<recevieMsg.length;j++){
                        dongleStatusRecevieMsg[dongleStatusRecevieMsgLength+j]=recevieMsg[j];
                    }
                }else {
                    if((dongleStatusRecevieMsgLength+recevieMsg.length)<dongleStatusRecevieMsg.length){
                        for(int j=0;j<recevieMsg.length;j++){
                            dongleStatusRecevieMsgLength++;
                            dongleStatusRecevieMsg[dongleStatusRecevieMsgLength+j]=recevieMsg[j];
                        }
                    }
                }

            }else{
                    dongleStatusRecevieMsgLength=recevieMsg.length;
                    for(int j=0;j<recevieMsg.length;j++){
                        dongleStatusRecevieMsg[j]=recevieMsg[j];
                    }
                }
                if(dongleStatusRecevieMsg[0]==(byte)0xE9&&dongleStatusRecevieMsg[dongleStatusRecevieMsg.length-1]==(byte)0x9E&&dongleStatusRecevieMsg[1]==(byte)0x41){
                    this.map = FtAnalysisUtil.analysisDongleData(dongleStatusRecevieMsg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: map"+map);
                            Log.i(TAG, "run: "+map.get("dongle_firmware_version").toString());
                            tvDFVersion.setText(ByteUtil.bytesToHexFun3(map.get("dongle_firmware_version")));
                            int askToUpdateEn = ByteUtil.getShort(map.get("askToUpdate_en"));
                            if (askToUpdateEn == 0) {
                                tvAskToUpdateEn.setText("Disable");
                            } else if (askToUpdateEn == 85) {
                                tvAskToUpdateEn.setText("Enable");
                            }
                            int waitForUpdateEn = ByteUtil.getShort(map.get("waitForUpdate_en"));
                            if (waitForUpdateEn == 0) {
                                tvWaitForUpdateEn.setText("Disable");
                            } else if (waitForUpdateEn == 85) {
                                tvWaitForUpdateEn.setText("Enable");
                            }
                            String firmwareTypeStr = ByteUtil.bytesToHexFun3(map.get("firmware_type"));
                            int imageMarkInt = Integer.parseInt(firmwareTypeStr, 16);
                            for (int i = 0; i < deviceImageMarkConf.length; i++) {
                                if (imageMarkInt == deviceImageMarkConf[i].getImageMark()) {
                                    tvFirmwareType.setText(deviceImageMarkConf[i].getType());
                                }
                            }
                            for (int i = 0; i < dongleImageMarkConf.length; i++) {
                                if (imageMarkInt == dongleImageMarkConf[i].getImageMark()) {
                                    tvFirmwareType.setText(dongleImageMarkConf[i].getType());
                                }
                            }
                            tvFirmwareVersion.setText(ByteUtil.bytesToHexFun3(map.get("firmware_version")));
                            String firmwareSizeStr = ByteUtil.bytesToHexFun3(map.get("firmware_size"));
                            tvFirmwareSize.setText((Integer.valueOf(firmwareSizeStr.substring(0, 2), 16) * 4096 + Integer.valueOf(firmwareSizeStr.substring(2, 4), 16) * 256 + Integer.valueOf(firmwareSizeStr.substring(4, 6), 16))/1000.000 + "KB");
                            if (ByteUtil.bytesToHexFun3(map.get("need_update_version")).equals("00000000")) {
                                tvNeedUpdateVersion.setText("none");
                            } else {
                                tvNeedUpdateVersion.setText(ByteUtil.bytesToHexFun3(map.get("need_update_version")));
                            }
                        }
                    });
                }
        } else if (clickSelect == 4) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtil.showWait(WFActivity, DialogUtil.OK_DIALOG, "Update has stopped...");
                    }
                });
            }
    }


    private void  evLvDeviceFlush(){
        if(deviceUpdateConfList.size()>0){
            for(int i=deviceUpdateConfList.size()-1;i>0;i--){
                for(int j=0;j<i;j++){
                    DevicesUpdateConf devicesUpdateConf=deviceUpdateConfList.get(j);
                    DevicesUpdateConf devicesUpdateConf1=deviceUpdateConfList.get(j+1);
                    byte[] devicesUpdateConfId=devicesUpdateConf.getId();
                    byte[] devicesUpdateConfId1=devicesUpdateConf1.getId();
                    int devicesUpdateConfIda=devicesUpdateConfId[0];
                    int devicesUpdateConfIdb=devicesUpdateConfId[1];
                    int devicesUpdateConfIda1=devicesUpdateConfId1[0];
                    int devicesUpdateConfIdb1=devicesUpdateConfId1[1];
                    if(devicesUpdateConfIda<0){
                        devicesUpdateConfIda=256+devicesUpdateConfIda;
                    }
                    if(devicesUpdateConfIdb<0){
                        devicesUpdateConfIdb=256+devicesUpdateConfIdb;
                    }
                    if(devicesUpdateConfIda1<0){
                        devicesUpdateConfIda1=256+devicesUpdateConfIda1;
                    }
                    if(devicesUpdateConfIdb1<0){
                        devicesUpdateConfIdb1=256+devicesUpdateConfIdb1;
                    }

                    if(((devicesUpdateConfIda << 8)+devicesUpdateConfIdb)>((devicesUpdateConfIda1 << 8)+devicesUpdateConfIdb1)){
                        DevicesUpdateConf devicesUpdateConfTemp=deviceUpdateConfList.get(j);
                        deviceUpdateConfList.set(j,deviceUpdateConfList.get(j+1));
                        deviceUpdateConfList.set(j+1,devicesUpdateConfTemp);
                    }
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int deviceSuccessNum=0;
                int deviceFailNum=0;
                for (DevicesUpdateConf item:deviceUpdateConfList){
                    CJTableRow cjTableRow=new CJTableRow(WFActivity);
                    byte[] id=item.getId();
                    String type=item.getType();
                    byte[] version=item.getVersion();
                    String versionString=ByteUtil.bytesToHexFun3(version);
                    String versionStr="20"+versionString.substring(0,2)+"-"
                            +versionString.substring(2,4)+"-"+versionString.substring(4,6)+" V"+versionString.substring(6,7)
                            +"."+versionString.substring(7,8);
                    String updateStatusString="";

                    switch (item.getUpdateStatus().getUpdateStatus()){
                        case 0:
                            updateStatusString="No Update";
                            break;
                        case 1:
                            updateStatusString="Update Successful";
                            break;
                        case 2:
                            updateStatusString="Update Failed";
                            break;
                        case 3:
                            updateStatusString="Update Disable Or No Firmware";
                            break;
                        case 4:
                            updateStatusString="Version Same ";
                            break;
                        case 5:
                            updateStatusString="Illegally Firmware";
                            break;
                        case 6:
                            updateStatusString="Not Same With NeedUpdateVersion";
                            break;
                        case 7:
                            updateStatusString="Firmware Too Large";
                            break;
                    }
                    if(item.getUpdateStatus().getUpdateStatus()==UpdateStatus.updateSuccessful||item.getUpdateStatus().getUpdateStatus()==UpdateStatus.versionSame){
                        deviceSuccessNum ++;
                    }else{
                        deviceFailNum ++;
                    }
                    cjTableRow.setCJTableRow(new String[]{ByteUtil.bytesToHexFun3(id),type,versionStr,updateStatusString});
                    cJTableRows.add(cjTableRow);
                    tableLayout.addView(cjTableRow);

                }
                tvTotal.setText("Total:"+deviceUpdateConfList.size());
                tvSuccess.setText("Success:"+deviceSuccessNum);
                tvFailure.setText("Failure:"+deviceFailNum);
            }
        });
    }

    @Override
    public void ftRecevieOutTime() {
        if(transStatus!=2&&transStatus!=3&&transStatus!=4){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.showWait(WFActivity,DialogUtil.ERROR_DIALOG,"操作超时！");
                }
            });
        }
    }

}
