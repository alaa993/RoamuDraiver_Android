package com.alaan.roamudriver.pojo;

import java.io.Serializable;

public class Group_membar implements Serializable {



    public int driver_id ;
    public String  group_name ;
    public String driver_name ;
    public String driver_mobile ;

    public String driver_email ;
    public String driver_country ;
    public String driver_is_online;
    public String driver_vehicle_no;

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
}
