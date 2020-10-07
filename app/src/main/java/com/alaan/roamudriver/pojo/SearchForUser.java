package com.alaan.roamudriver.pojo;

import java.io.Serializable;

public class SearchForUser implements Serializable {

    public SearchForUser() {
    }

    private String  ride_id;
    private String  user_id;
    private String  driver_id;
    private String  pickup_adress;
    private String  drop_address;
    private String  pikup_location;
    private String  drop_locatoin;
    private String  distance;
    private String  status;
    private String  payment_status;
    private String  pay_driver;
    private String  payment_mode;
    private  String  amount;
    private  String  time;
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

    public String getPickup_adress() {
        return pickup_adress;
    }

    public void setPickup_adress(String pickup_adress) {
        this.pickup_adress = pickup_adress;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }

    public String getPikup_location() {
        return pikup_location;
    }

    public void setPikup_location(String pikup_location) {
        this.pikup_location = pikup_location;
    }

    public String getDrop_locatoin() {
        return drop_locatoin;
    }

    public void setDrop_locatoin(String drop_locatoin) {
        this.drop_locatoin = drop_locatoin;
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

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getPay_driver() {
        return pay_driver;
    }

    public void setPay_driver(String pay_driver) {
        this.pay_driver = pay_driver;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
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



}
