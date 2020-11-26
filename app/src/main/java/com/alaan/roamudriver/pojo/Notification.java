package com.alaan.roamudriver.pojo;

import java.io.Serializable;

public class Notification implements Serializable {
    public String id;
    public String text;
    public String uid;

    public Notification()
    {
    }

    public Notification(String id, String Text, String uid)
    {
        this.id = id;
        this.text = text;
        this.uid = uid;
    }
}
