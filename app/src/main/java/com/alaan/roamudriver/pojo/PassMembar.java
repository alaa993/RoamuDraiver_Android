package com.alaan.roamudriver.pojo;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by android on 13/10/17.
 */

public class PassMembar implements Serializable,Parcelable {

    public int driver_id ;
    public String  group_name ;
    public String driver_name ;
    public String driver_mobile ;
    public String driver_email ;
    public String driver_country ;
    public String driver_is_online;
    public String driver_vehicle_no;

    protected PassMembar(Parcel in) {
        driver_id = in.readInt();
        group_name = in.readString();
        driver_name = in.readString();
        driver_mobile = in.readString();
        driver_email = in.readString();
        driver_country = in.readString();
        driver_is_online = in.readString();
        driver_vehicle_no = in.readString();
    }

    public PassMembar(){

    }
    public static final Creator<PassMembar> CREATOR = new Creator<PassMembar>() {
        @Override
        public PassMembar createFromParcel(Parcel in) {
            return new PassMembar(in);
        }

        @Override
        public PassMembar[] newArray(int size) {
            return new PassMembar[size];
        }
    };

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_mobile() {
        return driver_mobile;
    }

    public void setDriver_mobile(String driver_mobile) {
        this.driver_mobile = driver_mobile;
    }

    public String getDriver_email() {
        return driver_email;
    }

    public void setDriver_email(String driver_email) {
        this.driver_email = driver_email;
    }

    public String getDriver_country() {
        return driver_country;
    }

    public void setDriver_country(String driver_country) {
        this.driver_country = driver_country;
    }

    public String getDriver_is_online() {
        return driver_is_online;
    }

    public void setDriver_is_online(String driver_is_online) {
        this.driver_is_online = driver_is_online;
    }

    public String getDriver_vehicle_no() {
        return driver_vehicle_no;
    }

    public void setDriver_vehicle_no(String driver_vehicle_no) {
        this.driver_vehicle_no = driver_vehicle_no;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(String.valueOf(driver_id));
        parcel.writeString(driver_name);
        parcel.writeString(driver_mobile);

    }
}
