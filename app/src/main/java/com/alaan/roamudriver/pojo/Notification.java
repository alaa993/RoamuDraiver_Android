package com.alaan.roamudriver.pojo;

import java.io.Serializable;

public class Notification implements Serializable {
    public String id;
    public String text;
    public String uid;
    public String ride_id;


    public Notification()
    {
    }

    public Notification(String id, String text, String uid, String ride_id)
    {
        this.id = id;
        this.text = text;
        this.uid = uid;
        this.ride_id = ride_id;
    }
}
