package com.alaan.roamudriver.pojo;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;

/**
 * Created by android on 15/3/17.
 */

public class PendingRequestPojo implements Serializable {
    private String ride_id;
    private String user_id;
    private String driver_id;
    private String travel_id;
    private String pickup_address;
    private String drop_address;
    private String pickup_location;
    private String drop_location;
    private String pickup_point;
    private String city;
    private String distance;
    private String status;
    private String travel_status;
    private String payment_status;
    private String amount;
    private String time;
    private String date;
    private String user_mobile;
    private String user_avatar;
    private String driver_avatar;
    private String user_name;
    private String driver_mobile;
    private String driver_name;
    private String booked_set;
    private String empty_set;
    private String Ride_smoked;
    public String ride_notes;
    public String tr_notes;

//    private Place fromPlace;
//    private Place toPlace;

    public String getbooked_set() {
        return booked_set;
    }

    public void setbooked_set(String booked_set) {
        this.booked_set = booked_set;
    }

    public String getempty_set() {
        return empty_set;
    }

    public void setempty_set(String empty_set) {
        this.empty_set = empty_set;
    }

    public String getRide_smoked() {
        return Ride_smoked;
    }

    public void setRide_smoked(String ride_smoked) {
        Ride_smoked = ride_smoked;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    private String payment_mode;


    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getDriver_avatar() {
        return driver_avatar;
    }

    public void setDriver_avatar(String driver_avatar) {
        this.driver_avatar = driver_avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDriver_mobile() {
        return driver_mobile;
    }

    public void setDriver_mobile(String driver_mobile) {
        this.driver_mobile = driver_mobile;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }


    public PendingRequestPojo() {
    }

    public String getRide_id() {
        return ride_id;
    }

    public void setRide_id(String ride_id) {
        this.ride_id = ride_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }
    public String getTravel_id() {
        return travel_id;
    }

    public void setTravel_id(String travel_id) {
        this.travel_id = travel_id;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public String getPickup_point() {
        return pickup_point;
    }

    public void setPickup_point(String pickup_point) {
        this.pickup_point = pickup_point;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getpickup_location() {
        return pickup_location;
    }

    public void setpickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getdrop_location() {
        return drop_location;
    }

    public void setdrop_location(String drop_location) {
        this.drop_location = drop_location;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTravel_status() {
        return travel_status;
    }

    public void setTravel_status(String travel_status) {
        this.travel_status = travel_status;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

//    public Place getFromPlace() {
//        return fromPlace;
//    }
//
//    public void setFromPlace(Place fromPlace) {
//        this.fromPlace = fromPlace;
//    }
//
//    public Place getToPlace() {
//        return toPlace;
//    }
//
//    public void setToPlace(Place toPlace) {
//        this.toPlace = toPlace;
//    }
}
