package cj.tzw.base_uwb_m.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable {
    byte[] alerterID;
    int type;
    String firmware_version;
    String hVersion;

    public Device() {
    }

    public byte[] getAlerterID() {
        return alerterID;
    }

    public void setAlerterID(byte[] alerterID) {
        this.alerterID = alerterID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFirmware_version() {
        return firmware_version;
    }

    public void setFirmware_version(String firmware_version) {
        this.firmware_version = firmware_version;
    }

    public String gethVersion() {
        return hVersion;
    }

    public void sethVersion(String hVersion) {
        this.hVersion = hVersion;
    }

    protected Device(Parcel in) {
        alerterID = in.createByteArray();
        type = in.readInt();
        firmware_version = in.readString();
        hVersion = in.readString();
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(alerterID);
        dest.writeInt(type);
        dest.writeString(firmware_version);
        dest.writeString(hVersion);
    }
}
