package com.alaan.roamudriver.pojo;

public class firebaseClients {
    public Double latitude;
    public Double longitude;

    public firebaseClients()
    {
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public firebaseClients(Double latitude, Double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
