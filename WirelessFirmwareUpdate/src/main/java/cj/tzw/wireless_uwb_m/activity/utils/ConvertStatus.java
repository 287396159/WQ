package cj.tzw.wireless_uwb_m.activity.utils;

import java.util.Objects;

public class ConvertStatus {

    private int status;

    public static final int CONVERTOK=0;  //转换成功
    public static final int DATATOLONG=1;  //行数据长度过长
    public static final int DATATOSHORT=2;  //行数据长度过短
    public static final int DATANOCOLON=3;  //行数据没有冒号
    public static final int DATATYPEERROR=4;  //行数据类型错误
    public static final int DATALENGTHERROR=5;  //行数据长度与标记的不匹配
    public static final int DATACHECKERROR=6;  //CRC校验和错误
    public static final int HEXNOEND=7;   //文件没有结束符

    public ConvertStatus(){

    }
    public ConvertStatus(int status){
        this.status=status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConvertStatus that = (ConvertStatus) o;
        return status == that.status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }
}
