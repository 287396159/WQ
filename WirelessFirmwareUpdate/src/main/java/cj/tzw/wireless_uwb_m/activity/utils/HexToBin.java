package cj.tzw.wireless_uwb_m.activity.utils;

import android.util.Log;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HexToBin {

    private int lineHexDataMaxLength=256;
    private int lineHexDataMinLength=11;

    private byte lineLength;
    private byte lineType;
    private int firstAddr;
    private int lineStartAddr;
    private int baseAddr;
    private byte[] lineBinData;


    public byte getLineLength() {
        return lineLength;
    }

    public void setLineLength(byte lineLength) {
        this.lineLength = lineLength;
    }

    public byte getLineType() {
        return lineType;
    }

    public void setLineType(byte lineType) {
        this.lineType = lineType;
    }

    public int getFirstAddr() {
        return firstAddr;
    }

    public void setFirstAddr(int firstAddr) {
        this.firstAddr = firstAddr;
    }

    public int getLineStartAddr() {
        return lineStartAddr;
    }

    public void setLineStartAddr(int lineStartAddr) {
        this.lineStartAddr = lineStartAddr;
    }

    public int getBaseAddr() {
        return baseAddr;
    }

    public void setBaseAddr(int baseAddr) {
        this.baseAddr = baseAddr;
    }

    public byte[] getLineBinData() {
        return lineBinData;
    }

    public void setLineBinData(byte[] lineBinData) {
        this.lineBinData = lineBinData;
    }

    public HexToBin(){
        this.lineBinData=new byte[256];
        this.lineLength = 0;
        this.lineStartAddr = 0;
        this.lineType = 0;
        this.baseAddr = 0;
        this.firstAddr = 0xFFFF;
    }


    /// <summary>
    /// 每行字符形式的hex数据转换为hex
    /// </summary>
    /// <param name="strLineHexData"></param>
    /// <returns></returns>
    public ConvertStatus covertLineHexToBin(String  strLineHexData)
    {
        //行数据过长
        if (strLineHexData.length() > lineHexDataMaxLength)
            return new ConvertStatus(ConvertStatus.DATANOCOLON);
        //行数据过短
        if (strLineHexData.length() < lineHexDataMinLength)
            return new ConvertStatus(ConvertStatus.DATATOSHORT);
        //第一个字符不是冒号
        if (strLineHexData.toCharArray()[0]!= ':')
            return new ConvertStatus(ConvertStatus.DATANOCOLON);
        //行数据的长度应该为奇数
        if (strLineHexData.length()% 2 == 0)
            return new ConvertStatus(ConvertStatus.DATALENGTHERROR);

        //bin行数据长度
//        Log.i("lineLength", "covertLineHexToBin: "+hexString2ByteArray(strLineHexData.substring(1,3))[0]);

        this.lineLength=Byte.valueOf(strLineHexData.substring(1,3),16);
//    hexString2ByteArray(strLineHexData.substring(1,3))[0];

//        Log.i("lineStartAddr", "covertLineHexToBin: "+hexString2ByteArray(strLineHexData.substring(3,5))[0]*256+hexString2ByteArray(strLineHexData.substring(5,7))[0]);
        //bin行数据储存起始地址
        this.lineStartAddr=Integer.valueOf(strLineHexData.substring(3,5),16)*256+Integer.valueOf(strLineHexData.substring(5,7),16);
//                hexString2ByteArray(strLineHexData.substring(3,5))[0]*256+Math.abs(hexString2ByteArray(strLineHexData.substring(5,7))[0]);
//        Log.i("lineType", "covertLineHexToBin: "+hexString2ByteArray(strLineHexData.substring(7,9))[0]);

        //bin行数据类型
        this.lineType = hexString2ByteArray(strLineHexData.substring(7,9))[0];
//        Log.i("",""+strLineHexData.substring(1,3)+strLineHexData.substring(3,5));
        //将hex数据转换为bin数据

       /* byte[] z=hexString2ByteArray(strLineHexData);
        for(byte s:z){
            Log.i("hexString2ByteArray", "covertLineHexToBin: "+s);
        }*/
        int j=0;
        for(int i=0;i<strLineHexData.length()-9;){
//            Log.i("", "covertLineHexToBin: "+strLineHexData+"+++++++++++++"+j+"^^"+strLineHexData.substring(9+i,11+i));
            this.lineBinData[j]=hexString2ByteArray(strLineHexData.substring(9+i,11+i))[0];
            i+=2;
            j++;
        }
        //标识的长度和实际的不一样 还有最后一个校验字节
        if (j != this.lineLength + 1)
            return new ConvertStatus(ConvertStatus.DATALENGTHERROR);


        //数据校验检查 0x01 + 取反（其他数据）= 最后一个字节的校验位
        byte cs=0;int k=0;
        for(k=0;k<(strLineHexData.length()-3)/2;k++){//除去校验位和开始的冒号
//            Log.i("strLineHexData", "covertLineHexToBin: "+hexString2ByteArray(strLineHexData.substring(1+2*k,3+2*k))[0]);
            cs+=hexString2ByteArray(strLineHexData.substring(1+2*k,3+2*k))[0];
//            cs+=Byte.valueOf(strLineHexData.substring(1+2*k,3+2*k));
        }
//        Log.i("lineType", "covertLineHexToBin: "+hexString2ByteArray(strLineHexData.substring(7,9))[0]);
        byte dataCs=hexString2ByteArray(strLineHexData.substring(1+2*k,3+2*k))[0];
//        byte dataCs=Byte.valueOf(strLineHexData.substring(1+2*k,3+2*k));
        cs=(byte)~cs;
        cs+=0x01;
        if(cs!=dataCs){
            return new ConvertStatus(ConvertStatus.DATACHECKERROR);
        }
        return new ConvertStatus(ConvertStatus.CONVERTOK);
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param hexString
     * @return
     */
    public static byte[] hexString2ByteArray(String hexString) {
        try {
            if (hexString == null || hexString.equals("")) {
                return null;
            }
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /// <summary>
    /// HEX转换为bin
    /// </summary>
    /// <param name="strHexFile"></hex所有字符串>
    /// <param name="binFile"></存放的bin数组>
    /// <param name="binSize"></存放的bin文件的大小>
    /// <returns></returns>

    public ConvertStatus HexFileToBinFile(int[] hexFileToInts, byte[] binFile, int[] binSize){
        int addrStart=0;
        int addrBase=0;

        for(int i=0;i<binFile.length;i++){
            binFile[i]=(byte) 0xFF;
        }

        int newLine=13;
        int newL=10;
        boolean isLine=false;
        int lineCount=0;
        List<String> list=new ArrayList<>();
        String tempStr="";
        //将其分割为一行一行的数据。
        for(int i=0;i<hexFileToInts.length;i++){
            if(isLine&&newL==hexFileToInts[i]){
                list.add(tempStr);
                tempStr="";
                lineCount++;
                isLine=false;
                continue;
            }
            if(13==hexFileToInts[i]){
                isLine=true;
                continue;
            }else{
                isLine=false;
            }
            tempStr+=(char)hexFileToInts[i];
        }
        String[] strLineData=new String[lineCount];
        list.toArray(strLineData);
//        Log.i("HexFileToBinFile", "HexFileToBinFile: "+strLineData.length);
        for(int i=0;i<strLineData.length;i++){
//            Log.i("HexFileToBinFile", "HexFileToBinFile: "+strLineData[i]+"length"+strLineData.length);
            ConvertStatus res=covertLineHexToBin(strLineData[i]);
            if(res.getStatus()!=ConvertStatus.CONVERTOK){
                return res;
            }else{
                switch (this.lineType){
                    //数据记录
                    case 0:
                        if(this.firstAddr==0xFFFF){
                            this.firstAddr=this.lineStartAddr;
                        }
                        addrStart=addrBase+this.lineStartAddr-this.firstAddr;//每行的起始地址 需要加上基地址

                        /*if(addrStart<0){
                            addrStart=Math.abs(addrStart);
                        }*/
//                        Log.i("11111", "HexFileToBinFile:this.lineStartAddr "+this.lineStartAddr+"*******this.firstAddr"+this.firstAddr);
//                        Log.i("Ip", "HexFileToBinFile: "+addrStart);

                        for (int j=0;j<this.lineLength;j++){
                            binFile[j+addrStart]=this.lineBinData[j];
                        }
                        binSize[0]=this.lineLength+addrStart;
                        break;
                    //数据结束
                    case 1:
//                        Log.i("binSizeOKOK", "HexFileToBinFile: "+binSize);
                        return new ConvertStatus(ConvertStatus.CONVERTOK);
                    //扩展段地址记录
                    case 2:
                        if(this.lineLength!=2){
                            return new ConvertStatus(ConvertStatus.DATATYPEERROR);//扩展地址只有两个字节
                        }
                        if(i==0){//首次仅仅记录基地址
                            this.baseAddr=(this.lineBinData[0]<<8)+(this.lineBinData[1]<<4);
                        }else{
                            if ((((this.lineBinData[0] << 8) + this.lineBinData[1]) << 4) < this.baseAddr)
                                return new ConvertStatus(ConvertStatus.DATATYPEERROR);
                            addrBase = ((((this.lineBinData[0] << 8) + this.lineBinData[1]) << 4) - this.baseAddr);
                        }
                        break;
                    //开始段地址记录
                    case 3:
                        break;
                    //扩展线性地址记录
                    case 4:
                        if(this.lineLength!=2){
                            return new ConvertStatus(ConvertStatus.DATATYPEERROR);//扩展地址只有两个字节
                        }
                        if(i==0){//首次仅仅记录基地址
                            this.baseAddr=(this.lineBinData[0]<<8)+(this.lineBinData[1]<<16);
                        }else{
                            if ((((this.lineBinData[0] << 8) + this.lineBinData[1]) << 16) < this.baseAddr)
                                return new ConvertStatus(ConvertStatus.DATATYPEERROR);
                            addrBase = ((((this.lineBinData[0] << 8) + this.lineBinData[1]) << 16) - this.baseAddr);
                        }
                        break;
                    //开始线性地址记录
                    case 5:
                        break;
                        default:
                            return new ConvertStatus(ConvertStatus.DATATYPEERROR);
                }
            }
        }
        return new ConvertStatus(ConvertStatus.HEXNOEND);
    }
}
