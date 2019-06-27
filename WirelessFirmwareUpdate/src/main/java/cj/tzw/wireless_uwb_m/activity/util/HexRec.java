package cj.tzw.wireless_uwb_m.activity.util;

public class HexRec {

    private int addr;
    private int len;
    private String buf;

    public HexRec() {

    }

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public String getBuf() {
        return buf;
    }

    public void setBuf(String buf) {
        this.buf = buf;
    }
}
