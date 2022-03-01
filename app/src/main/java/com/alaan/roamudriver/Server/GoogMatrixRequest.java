package com.alaan.roamudriver.Server;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogMatrixRequest {

    private static final String API_KEY = "AIzaSyCa3yhDGMZM2xHCc5ieYeyz87SuHYDzozU";

    OkHttpClient client = new OkHttpClient();

    public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String getGoogMatrixRequest(String origins, String destinations) throws IOException {
        //37.7680296,-122.4375126
        //37.7663444,-122.4412006
        String response = "";
        try {
            GoogMatrixRequest request = new GoogMatrixRequest();
            String url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origins + "&destinations=" + destinations + "&mode=driving&language=en-EN&key=" + API_KEY;

            response = request.run(url_request);
            //log.i("ibrahim", "getGoogMatrixRequest");
            //log.i("ibrahim", response);
            System.out.println(response);
            return response;
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
