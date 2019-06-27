package cj.tzw.wireless_uwb_m.activity.utils;

public class ImageMarkConf {

    private int imageMark;
    private String type;

    public int getImageMark() {
        return imageMark;
    }

    public void setImageMark(int imageMark) {
        this.imageMark = imageMark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ImageMarkConf(int imageMark, String type)
    {
        this.imageMark = imageMark;
        this.type = type;
    }
}
