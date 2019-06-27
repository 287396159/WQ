package cj.tzw.wireless_uwb_m.activity.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HexToBin {

    private static int readFile(String path) {
        File mfile = new File(path);
        List<HexRec> hexRecs = new ArrayList<>();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        FileOutputStream fileOutputStream = null;
        int i = 0, j = 0;            //索引
        int l_addr;
        int len = 0;//数组索引
        long minAddr = 4294967295L;
        FileStruct hex = new FileStruct();
        try {
            if (mfile == null) {
                System.out.println("文件为空");
            }
            inputStream = new FileInputStream(mfile);
            //转成 reader 以 行 为单位读取文件
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //当前行字符串
            String hexLineStr;
            //当前行数
            int hexLineNum = 0;
            while ((hexLineStr = bufferedReader.readLine()) != null) {
                System.out.println(hexLineStr);
                hexLineNum++;
                if (!hexLineStr.startsWith(":", 0)) {
                    return -1;
                }
                if (hexLineStr.length() >= 11) {
                    hex.setStart(":");
                    byte[] data = hexString2ByteArray(hexLineStr.substring(1));//判断数据的正确是是不是0—F
                    if (data == null) return -1;
                    //解析数据
                    hex.setLength(Integer.parseInt(hexLineStr.substring(1, 3), 16));
                    hex.setOffset(Integer.parseInt(hexLineStr.substring(3, 7), 16));
                    hex.setType(Integer.parseInt(hexLineStr.substring(7, 9), 16));
                    ///////////////////////////////////////////////////////////////////
                    //判断数据类型是否合法, 未处理05的数据
                    if (0x00 != hex.getType() && 0x01 != hex.getType() && 0x02 != hex.getType() && 0x04 != hex.getType() && 0x05 != hex.getType()) {
                        return -1;
                    }
                    if (0x05 == hex.getType()) {//不处理05类型的数据
                        continue;
                    }
                    if (hex.getLength() > 0) {
                        hex.setData(hexLineStr.substring(9, 9 + hex.getLength() * 2));
                    }
                    if (!checkValue(hexLineStr)) return -1;
                    switch (hex.type) {
                        case 0x00:    //本行的数据类型为“数据记录”
                            //本行所从属的数据类型为“数据记录”
                            if (0x00 == hex.format) {
                                l_addr = hex.offset;
                            }
                            //本行所从属的数据类型为“扩展段地址记录”(HEX86)--20位地址
                            else if (0x02 == hex.format) {
                                l_addr = (hex.address << 4) + hex.offset;
                            }
                            //本行所从属的数据类型为“扩展线性地址记录”(HEX386)--32位地址
                            else if (0x04 == hex.format) {
                                l_addr = (hex.address << 16) + hex.offset;
                            }
                            //文件结束
                            else {
                                i = 1;
                                break;
                            }
                            //记录地址中的最大值
                            System.out.println("l_addr:" + l_addr);
                            if (minAddr > l_addr) minAddr = l_addr;
                            if (hex.length > 0) {
                                HexRec hexRec = new HexRec();
                                hexRec.setAddr(l_addr);
                                hexRec.setLen(hex.length);
                                hexRec.setBuf(hex.data);
                                hexRecs.add(hexRec);
                                len += hex.length;
                            }
                            break;

                        case 0x01:    //本行的数据类型为“文件结束记录”
                            //文件结束记录的数据个数一定是0x00
                            if (hex.length == 0x00) i = 1;
                            hex.format = 0x01;
                            break;

                        case 0x02:    //本行的数据类型为“扩展段地址记录”
                            //扩展段地址记录的数据个数一定是0x02
                            if (hex.length != 0x02) i = 3;
                            //扩展段地址记录的地址一定是0x0000
                            if (hex.offset != 0x0000) i = 3;
                            //更改hex从属的数据类型
                            hex.format = 0x02;
                            //获取段地址
                            String hexStr = hex.getData().substring(0, 4);
                            byte[] hexBytes = hexString2ByteArray(hexStr);
                            hex.address = (hexBytes[0] << 8 | hexBytes[1]);
                            break;

                        case 0x04://本行的数据类型为“扩展线性地址记录”
                            //扩展线性地址记录中的数据个数一定是0x02
                            if (hex.length != 0x02) i = 4;
                            //扩展线性地址记录的地址一定是0x0000
                            if (hex.offset != 0x0000) i = 4;
                            //更改hex从属的数据类型
                            hex.format = 0x04;
                            //获取高16位地址
                            hexStr = hex.getData().substring(0, 4);
                            hexBytes = hexString2ByteArray(hexStr);
                            hex.address = (hexBytes[0] << 8 | hexBytes[1]);
                            break;
                    }
                }
                //如果出现异常或文件结束退出循环
                if (i == 1) {
                    break;
                }
                if (i > 0) {
                    return -1;//文件解析出错
                }
            }
            len = 0;
            int minLen = 0;
            int offset = 0;
            StringBuffer buffer = new StringBuffer();
            for (int a = 0; a < hexRecs.size(); a++) {
                offset = (int) (hexRecs.get(a).getAddr() - minAddr);
                buffer.append(hexRecs.get(a).getBuf());
                if (minLen < offset + hexRecs.get(a).getLen()) {
                    minLen = offset + hexRecs.get(a).getLen();
                }
                len += hexRecs.get(a).getLen();
            }
            if (len < minLen) {
                len = minLen;
            }
            System.out.println(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
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

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 校验和必然是256的整数倍,如果有余数则认为校验和失败
     *
     * @return false 校验失败 反正成功
     */
    public static boolean checkValue(String hexLineStr) {
        byte[] buf = hexString2ByteArray(hexLineStr.substring(1));
        byte temp = 0;
        for (int i = 0; i < buf.length; i++) {
            temp += buf[i];
        }
        if (temp % 0xFF == 0) {
            return true;
        }
        return false;
    }

}
