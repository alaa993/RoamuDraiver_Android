package com.alaan.roamudriver.pojo;

import java.io.Serializable;

public class DriverRides implements Serializable {
    public String travel_id ;
    public String driver_id ;
    public String pickup_address ;
    public String drop_address ;
    public String pickup_location ;
    public String drop_location ;
    public String distance ;
    public String amount ;
    public String avalable_set ;
    public String booked_set ;
    public String travel_time ;
    public String travel_date ;
    public String somked ;

    public String getTravel_id() {
        return travel_id;
    }

    public void setTravel_id(String travel_id) {
        this.travel_id = travel_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getDrop_location() {
        return drop_location;
    }

    public void setDrop_location(String drop_location) {
        this.drop_location = drop_location;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAvalable_set() {
        return avalable_set;
    }

    public void setAvalable_set(String avalable_set) {
        this.avalable_set = avalable_set;
    }

    public String getBooked_set() {
        return booked_set;
    }

    public void setBooked_set(String booked_set) {
        this.booked_set = booked_set;
    }

    public String getTravel_time() {
        return travel_time;
    }

    public void setTravel_time(String travel_time) {
        this.travel_time = travel_time;
    }

    public String getTravel_date() {
        return travel_date;
    }

    public void setTravel_date(String travel_date) {
        this.travel_date = travel_date;
    }

    public String getSomked() {
        return somked;
    }

    public void setSomked(String somked) {
        this.somked = somked;
    }
}
