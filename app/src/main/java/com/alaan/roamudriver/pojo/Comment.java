package com.alaan.roamudriver.pojo;

public class Comment {
    //public String uid;
    //public String username;
    public UserProfile author;
    public String text;
    public Long timestamp;
    public String type;

    public Comment() {
        this.author = new UserProfile();
        this.text = "";
        this.timestamp = Long.valueOf(0);
        this.type = "";
    }

    public Comment(UserProfile author, String text, Long timestamp, String type) {
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
        this.type = type;
    }
}
