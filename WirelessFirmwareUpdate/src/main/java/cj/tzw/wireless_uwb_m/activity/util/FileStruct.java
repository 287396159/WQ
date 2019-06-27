package cj.tzw.wireless_uwb_m.activity.util;

public class FileStruct {
    public String start = ":";    //每一条Hex记录的起始字符“:”
    public int length = 0x00;    //数据的字节数量
    public int address = 0x0000;    //数据存放的地址
    public int type = 0xFF;    //HEX记录的类型
    public String data;//一行最多有16个字节的数据
    public int check = 0xAA;    //校验和
    public int offset = 0x0000;    //偏移量
    public int format = 0x00;    //数据行所从属的记录类型

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }
}
