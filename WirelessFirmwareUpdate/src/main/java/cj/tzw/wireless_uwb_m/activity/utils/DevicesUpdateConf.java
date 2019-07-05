package cj.tzw.wireless_uwb_m.activity.utils;

public class DevicesUpdateConf {

    private byte[] id=new byte[2];
    private byte[] version=new byte[4];
    private UpdateStatus updateStatus;
    private String type = "Unknown";

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getVersion() {
        return version;
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }

    public UpdateStatus getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(UpdateStatus updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
