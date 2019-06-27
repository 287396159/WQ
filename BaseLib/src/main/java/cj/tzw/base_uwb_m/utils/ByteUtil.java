package cj.tzw.base_uwb_m.utils;

import android.util.Log;

public class ByteUtil{


    /**
     * short 转 byte[]
     */
    public final static byte[] getBytes(short s, boolean asc) {
        byte[] buf = new byte[2];
        if (asc)
            for (int i = buf.length - 1; i >= 0; i--) {
                buf[i] = (byte) (s & 0x00ff);
                s >>= 8;
            }
        else
            for (int i = 0; i < buf.length; i++) {
                buf[i] = (byte) (s & 0x00ff);
                s >>= 8;
            }
        return buf;
    }



    /**
     * byte[] 转 short
     * @param
     * @return
     */
    public final static int getShort(byte[] buf)
    {
        if (buf == null)
        {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (buf.length > 2)
        {
            throw new IllegalArgumentException("byte array size > 2 !");
        }
        short r = 0;
        int lastVal = 0;
        for (int i = 0; i < buf.length; i++)
        {
            r <<= 8;
            r |= (buf[i] & 0x00ff);
        }
        lastVal = r;
        String binaryString = Integer.toBinaryString(r);
        if(binaryString.length()>=16){
            String binary = binaryString.substring(16);
            int intVal =  binaryToInt(binary);
            lastVal = intVal;
            Log.e("SettingActivity","二进制："+binary+"|"+r);
        }
        return lastVal;
    }


    /**
     * 二进制字符转数字
     * @param binary
     * @return
     */
    public static int binaryToInt(String binary) {
        if (binary == null) {
            System. out.println("can't input null ！" );
        }
        if (binary.isEmpty()) {
            System. out.println("you input is Empty !" );
        }
        int max = binary.length();
        String new_binary = "";
        if (max >= 2 && binary.startsWith("0")) {
            int position = 0;
            for (int i = 0; i < binary.length(); i++) {
                char a = binary.charAt(i);
                if (a != '0' ) {
                    position = i;
                    break;
                }
            }
            if (position == 0) {
                new_binary = binary.substring(max - 1, max);
            } else {
                new_binary = binary.substring(position, max);
            }
        } else {
            new_binary = binary;
        }
        int new_width = new_binary.length();

        long result = 0;
        if (new_width < 32) {
            for (int i = new_width; i > 0; i--) {
                char c = new_binary.charAt(i - 1);
                int algorism = c - '0' ;
                result += Math. pow(2, new_width - i) * algorism;
            }
        } else if (new_width == 32) {
            for (int i = new_width; i > 1; i--) {
                char c = new_binary.charAt(i - 1);
                int algorism = c - '0' ;
                result += Math. pow(2, new_width - i) * algorism;
            }
            result += -2147483648;
        }
        int a = new Long(result).intValue();
        return a;
    }


    /**
     * 将byte数组转换为int数据
     * @param b 字节数组
     * @return 生成的int数据
     */
    public static int byteToInt2(byte[] b){
        return (((int)b[0]) << 24) + (((int)b[1]) << 16) + (((int)b[2]) << 8) + b[3];
    }


    public static String CharToString(char[] data, int len)
    {
        String str = "";
        for(int i=0; i<len; i++)
        {
            if(((data[i]>>4)&0xF) < 10)
                str = str + (char)('0'+((data[i]>>4)&0xF));
            else
                str = str + (char)('A'+(((data[i]>>4)&0xF)-10));
            if((data[i]&0xF) < 10)
                str = str + (char)('0'+(data[i]&0xF));
            else
                str = str + (char)('A'+((data[i]&0xF)-10));
            str = str + " ";
        }
        return str;
    }

    public static byte[] StringToBytes(String str){
        String strHex = Integer.toHexString(Integer.valueOf(str));
        byte[] strHexBytes = ByteUtil.toBytes(strHex);
        byte[] strFinalBytes = new byte[2];
        if(strHexBytes.length==1){
            strFinalBytes[0] = 0;
            strFinalBytes[1] = strHexBytes[0];
        }else if(strHexBytes.length==2){
            strFinalBytes[0] = strHexBytes[0];
            strFinalBytes[1] = strHexBytes[1];
        }
        return strFinalBytes;
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }


    /**
     * 十六进制转String
     * @param bytes
     * @return
     */
    public static String bytesToHexFun3(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for(byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }

        return buf.toString();
    }


    /**
     * 计算校验和
     * @param buf
     * @param length
     * @return
     */
    public static byte getCheckBit(byte[] buf,int length){
        int index = 0;
        int check = 0;
        if(index >= length) return (byte) check;
        for (int i = index; i < length; i++){
            Log.e("ByteUtil","check发送的命令包："+(buf[i]&0xff));
            check += (buf[i]&0xff);
        }
        return (byte)check;
    }




}
