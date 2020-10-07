package com.alaan.roamudriver.pojo;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;

/**
 * Created by android on 13/10/17.
 */

public class PassReques implements Serializable,Parcelable {
    private String  fromPlace;

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    private String toPlace;
    private String  fromAddress;
    private String toAddress;

    private String driverId;
    private String driverName;
    private String fare;
    private String vehicleName;

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public PassReques() {
    }

    protected PassReques(Parcel in) {
        driverId = in.readString();
        driverName = in.readString();
        fare = in.readString();
    }

    public static final Creator<Pass> CREATOR = new Creator<Pass>() {
        @Override
        public Pass createFromParcel(Parcel in) {
            return new Pass(in);
        }

        @Override
        public Pass[] newArray(int size) {
            return new Pass[size];
        }
    };

    public String getFromPlace() {

        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(driverId);
        parcel.writeString(driverName);
        parcel.writeString(fare);
    }
}
