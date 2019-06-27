package cj.tzw.wireless_uwb_m.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import cj.tzw.base_uwb_m.callback.FtSrCallback;
import cj.tzw.base_uwb_m.model.Device;
import cj.tzw.base_uwb_m.router.RouterPathMethod;
import cj.tzw.base_uwb_m.utils.ByteUtil;
import cj.tzw.base_uwb_m.utils.DialogUtil;
import cj.tzw.base_uwb_m.utils.FtAnalysisUtil;
import cj.tzw.base_uwb_m.utils.FtBaseUtil;
import cj.tzw.base_uwb_m.utils.FtHandleUtil;
import cj.tzw.wireless_uwb_m.activity.utils.ConvertStatus;
import cj.tzw.wireless_uwb_m.activity.utils.HexToBin;
import cj.tzw.wireless_uwb_m.activity.utils.ImageMarkConf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private TextView tvFType,tvFVersion,tvFSize,tvDFVersion,tvAskToUpdateEn,tvWaitForUpdateEn,tvFirmwareType,tvFirmwareVersion,tvFirmwareSize,tvNeedUpdateVersion;
    private CheckBox cbAskToUpdate;

    private byte clickSelect=0;
    private byte transStatus=0;
    private byte[] sendRequestBuf;
    private Thread threadUpdate=null;

    private View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int vId = v.getId();

            if(vId==R.id.btnOpenFile){
                openAssignFolder("/");
                clickSelect=1;
            }else if(vId==R.id.btnStartUpdate){
                if("Start Update".equals(btnStartUpdate.getText())){
                    if("".equals(etUpdateVersion.getText().toString())){
                        needUpdateVersion=0;
                        Toast.makeText(WFActivity,"etUpdateVersion"+needUpdateVersion,Toast.LENGTH_LONG).show();
                    }else{
                        needUpdateVersion=Integer.parseInt(etUpdateVersion.getText().toString(),16);
                        Toast.makeText(WFActivity,"etUpdateVersiononxxxxxxx"+needUpdateVersion,Toast.LENGTH_LONG).show();
                        etUpdateVersion.setText(String.format("%08d",Integer.parseInt(etUpdateVersion.getText().toString())));
                    }
                    btnStartUpdate.setText("Stop Update");
                    sendRequestBuf=new byte[21];
                    int length=0;
                    Log.i(TAG, "onClick: binImageMark:"+binImageMark+"  binVersion:"+binVersion+"  binSize:"+binSize[0]+"  binCheckSum:"+binCheckSum+"  needUpdateVersion:"+needUpdateVersion);
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
                    if(cbAskToUpdate.isSelected()){
                        sendRequestBuf[length++] = 0x55;
                    }else{
                        sendRequestBuf[length++] = 0;
                    }
                    sendRequestBuf[length++] = 0x55;
                    if(threadUpdate!=null){
                        threadUpdate.stop();
                    }
                    threadUpdate=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ftHandleUtil.requestTransFirmWare(sendRequestBuf);
                            transStatus=1;
                        }
                    });
                    threadUpdate.start();
//                    ftHandleUtil.requestTransFirmWare(sendBuf);
                }else{
                    btnStartUpdate.setText("Start Update");
                }
                clickSelect=2;
            }else if(vId==R.id.btnReadStatus){
                ftHandleUtil.readDongleStatus();
                clickSelect=3;
            }else if(vId==R.id.btnStopUpdate){
                ftHandleUtil.stopDongleUpdate();
                clickSelect=4;
            }else{
                clickSelect=0;
            }



        }
    };


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
//            startActivity(Intent.createChooser(intent,"选择浏览工具"));
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
//                                        Log.i(TAG, "run: "+Environment.getExternalStorageDirectory());
                                        fileInputStream=new FileInputStream(upLoadFilePath);
                                        in=new BufferedInputStream(fileInputStream);
                                        //hex文件最大不能超过DeviceBinMaxSize*4K
//                                        char[] buf=new char[deviceBinMaxSize*4];
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
                                            Log.i(TAG, "run: hex*****************************************");
                                            ConvertStatus res=hexToBin.HexFileToBinFile(buf,buffer,binSize);
                                            /*BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream("/mnt/sdcard/app.bin"));
                                            byte[] bytes=new byte[binSize[0]];
                                            for(int i=0;i<binSize[0];i++){
                                                bytes[i]=buffer[i];
                                            }
                                            out.write(bytes);
                                            out.flush();*/
                                            Log.i(TAG, "run: hex*****************************************"+res.getStatus()+"binSize:"+binSize);
                                            if(res.getStatus()==0){
                                                Log.i(TAG, "run: hex##################################################");
                                                index=true;
                                            }
                                        }else{
                                            index=true;
                                            binSize[0]=byteSize;
                                        }
                                        if(index){
//                                            Log.i(TAG, "run: index"+index);
                                            int imageMarkNum=-1;
                                            int imageMark=(buffer[8195] << 24) + (buffer[8194] << 16) + (buffer[8193] << 8) + (buffer[8192]);
//                                            Log.e(TAG, "run: imageMark"+imageMark,new NullPointerException() );
                                            Log.i(TAG, "run: deviceImageMarkConfA"+deviceImageMarkConf.length);
                                            for(int j=0;j<deviceImageMarkConf.length;j++){
                                                Log.i(TAG, "run: deviceImageMarkConfB"+deviceImageMarkConf[j].getImageMark());
                                                if(imageMark==deviceImageMarkConf[j].getImageMark()){
                                                    imageMarkNum=j;
                                                    Log.i(TAG, "run: deviceImageMarkConfC"+deviceImageMarkConf[j].getImageMark());
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
                                                if(isDongleImage==true){
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
                                                        Log.i(TAG, "run: "+(binVersion));
                                                        tvFVersion.setText("20" + Integer.toString((byte)(binVersion >> 24),16) + "-" + Integer.toString(((byte)(binVersion >> 16)),16) + "-" + Integer.toString((byte)(binVersion >> 8),16) +
                                                                " V" + Integer.toString((byte)binVersion,16).substring(0, 1) + "." + Integer.toString((byte)binVersion,16).substring(1, 2));
                                                        tvFSize.setText(binSize[0]+"KB");
                                                        btnStartUpdate.setEnabled(true);
                                                    }
                                                });

                                            }
                                            for(int i=0;i<binSize[0];i++){
                                                Log.i(TAG, "run: buffer["+i+"]"+buffer[i]);
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_firmware_update);
        initView();
        initEvent();

    }

    private void initView(){

        btnOpenFile=findViewById(R.id.btnOpenFile);
        btnStartUpdate=findViewById(R.id.btnStartUpdate);
        btnReadStatus=findViewById(R.id.btnReadStatus);
        btnStopUpdate=findViewById(R.id.btnStopUpdate);
        etFilePath=findViewById(R.id.etFilePath);
        etUpdateVersion=findViewById(R.id.etUpdateVersion);

        cbAskToUpdate=findViewById(R.id.cbAskToUpdate);

        tvFType=findViewById(R.id.tvFType);
        tvFVersion=findViewById(R.id.tvFVersion);
        tvFSize=findViewById(R.id.tvFSize);

        tvDFVersion=findViewById(R.id.tvDFVersion);
        tvAskToUpdateEn=findViewById(R.id.tvAskToUpdateEn);
        tvWaitForUpdateEn=findViewById(R.id.tvWaitForUpdateEn);
        tvFirmwareType=findViewById(R.id.tvFirmwareType);
        tvFirmwareVersion=findViewById(R.id.tvFirmwareVersion);
        tvFirmwareSize=findViewById(R.id.tvFirmwareSize);
        tvNeedUpdateVersion=findViewById(R.id.tvNeedUpdateVersion);


        currentDevice = new Device();
        ftBaseUtil=FtBaseUtil.getInstance(getApplicationContext()).setFtSrCallback(this);
        ftHandleUtil = new FtHandleUtil(ftBaseUtil,currentDevice);

    }

    private void initEvent(){

        btnOpenFile.setOnClickListener(clickListener);
        btnStartUpdate.setOnClickListener(clickListener);
        btnReadStatus.setOnClickListener(clickListener);
        btnStopUpdate.setOnClickListener(clickListener);

    }

    @Override
    public void ftSended() {

    }

    @Override
    public void ftSendFail(String failMsg) {

    }

    private  static HashMap<String,byte[]> map;
    @Override
    public void ftRecevied(byte[] recevieMsg) {
        Log.i(TAG, "ftRecevied: "+ ByteUtil.bytesToHexFun3(recevieMsg)+recevieMsg.length);
        if(clickSelect==2){
            if(transStatus==1){

            }
        }else if(clickSelect==3){
            this.map=FtAnalysisUtil.analysisDongleData(recevieMsg);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvDFVersion.setText(ByteUtil.bytesToHexFun3(map.get("dongle_firmware_version")));
                    int askToUpdateEn=ByteUtil.getShort(map.get("askToUpdate_en"));
                    if(askToUpdateEn==0){
                        tvAskToUpdateEn.setText("Disable");
                    }else if(askToUpdateEn==1){
                        tvAskToUpdateEn.setText("Enable");
                    }
                    int waitForUpdateEn=ByteUtil.getShort(map.get("waitForUpdate_en"));
                    if(waitForUpdateEn==0){
                        tvWaitForUpdateEn.setText("Disable");
                    }else if(waitForUpdateEn==1){
                        tvWaitForUpdateEn.setText("Enable");
                    }

                    String firmwareTypeStr=ByteUtil.bytesToHexFun3(map.get("firmware_type"));
                    Log.i(TAG, "run: firmwareTypeStr"+firmwareTypeStr);
//                    int imageMarkInt=Integer.valueOf(firmwareTypeStr.substring(0,2),16)*65536+Integer.valueOf(firmwareTypeStr.substring(2,4),16)*4096+Integer.valueOf(firmwareTypeStr.substring(4,6),16)*256+Integer.valueOf(firmwareTypeStr.substring(6,8),16);
                    int imageMarkInt=Integer.parseInt(firmwareTypeStr,16);
                    for(int i=0;i<deviceImageMarkConf.length;i++){
                        Log.i(TAG, "run: imageMarkInt"+imageMarkInt+"deviceImageMarkConf["+i+"]"+deviceImageMarkConf[i].getImageMark());
                        if(imageMarkInt==deviceImageMarkConf[i].getImageMark()){
                            tvFirmwareType.setText(deviceImageMarkConf[i].getType());
                        }
                    }
                    for(int i=0;i<dongleImageMarkConf.length;i++){
                        Log.i(TAG, "run: imageMarkInt"+imageMarkInt+"deviceImageMarkConf["+i+"]"+dongleImageMarkConf[i].getImageMark());
                        if(imageMarkInt==dongleImageMarkConf[i].getImageMark()){
                            tvFirmwareType.setText(dongleImageMarkConf[i].getType());
                        }
                    }
//                    tvFirmwareType.setText(ByteUtil.bytesToHexFun3(map.get("firmware_type")));
                    tvFirmwareVersion.setText(ByteUtil.bytesToHexFun3(map.get("firmware_version")));
                    String firmwareSizeStr=ByteUtil.bytesToHexFun3(map.get("firmware_size"));
                    Log.i(TAG, "run: firmwareSizeStr"+firmwareSizeStr);
                    Log.i(TAG, "run: firmwareSizeStra"+Integer.valueOf(firmwareSizeStr.substring(0,2),16));
                    tvFirmwareSize.setText(Integer.valueOf(firmwareSizeStr.substring(0,2),16)*4096+Integer.valueOf(firmwareSizeStr.substring(2,4),16)*256+Integer.valueOf(firmwareSizeStr.substring(4,6),16)+"KB");
                    if(ByteUtil.bytesToHexFun3(map.get("need_update_version")).equals("00000000")){
                        tvNeedUpdateVersion.setText("none");
                    }else{
                        tvNeedUpdateVersion.setText(ByteUtil.bytesToHexFun3(map.get("need_update_version")));
                    }
                }
            });

        }else if(clickSelect==4){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.showWait(WFActivity,DialogUtil.OK_DIALOG,"Update has stopped...");
                }
            });
        }

    }

    @Override
    public void ftRecevieOutTime() {

    }
}
