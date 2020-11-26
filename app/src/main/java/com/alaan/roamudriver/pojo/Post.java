package com.alaan.roamudriver.pojo;

import java.io.Serializable;

public class Post implements Serializable{
    public String id;
    public UserProfile author;
    public String text;
    public String type;
    public String privacy;
    public int travel_id;

    public Long timestamp;

    public Post()
    {
    }

    public Post(String id, UserProfile author, String Text, Long timestamp,String type, String privacy, int travel_id)
    {
        this.id = id;
        this.author = author;
        this.text = text;

        this.timestamp = timestamp;
        this.type = type;
        this.privacy = privacy;
        this.travel_id = travel_id;
    }
}
