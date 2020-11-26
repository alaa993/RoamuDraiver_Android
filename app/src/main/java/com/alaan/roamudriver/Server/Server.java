package com.alaan.roamudriver.Server;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by android on 17/3/17.
 */

public class Server {
    OkHttpClient okHttpClient = new OkHttpClient();
    public static final String BASE_URL = "https://roamu.net/";

    private static final String TAG = "server";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static final String FORMAT = "/format/json";
    public static String addGruop = BASE_URL +"api/driver/addgroup"+FORMAT;
    public static String ChangeGruopName = BASE_URL +"api/driver/editgroup"+FORMAT;
    public static String add_user_Gruop =  BASE_URL +"api/driver/addUserToGroup"+ FORMAT;
    public static String remove_user_Gruop =  BASE_URL +"api/driver/delUserFromGroup"+ FORMAT;

    public static String GET_RIDES =  BASE_URL +"api/user/driverTravelMange"+ FORMAT;
    public static String GET_SEARCHUSER = BASE_URL +"api/user/drivers_search"+ FORMAT;
    public static String GET_SEARCHUSER1 = BASE_URL +"api/user/requested_rides"+ FORMAT;
    public static String ManageRide = BASE_URL +"api/user/manage_ride"+ FORMAT;

    public static final String REGISTER = BASE_URL + "user/register" + FORMAT;
    public static final String LOGIN = BASE_URL + "user/loginByMobile" + FORMAT;
    public static final String APPROVE_PAYMENT = BASE_URL + "api/user/rides" + FORMAT;
    public static final String STATUS_CHANGE = BASE_URL + "api/user/rides" + FORMAT;
    public static final String CONFIRM_REQUST = BASE_URL + "api/user/confirm_requested_rides" + FORMAT;
    public static final String UPDATE = BASE_URL + "api/user/update" + FORMAT;
    public static final String FORGOT_PASSWORD = BASE_URL + "user/forgot_password" + FORMAT;

    public static final String EARN = BASE_URL + "api/driver/earn" + FORMAT;
    public static final String GET_PROFILE = BASE_URL + "api/user/profile" + FORMAT;
    public static final String GET_GROUP = BASE_URL + "api/driver/getAdminGroupInfo" +FORMAT;
    public static final String GET_MEBLIST =  BASE_URL + "api/driver/getGroupList" +FORMAT;
    public static final String GET_MyGroupLIST =  BASE_URL + "api/driver/getMyGroupList" +FORMAT;

    public static final String GET_REQUEST = BASE_URL + "api/user/rides" + FORMAT;
    public static final String GET_REQUEST1 = BASE_URL + "api/user/rides2" + FORMAT;

    public static final String PASSWORD_RESET = BASE_URL + "api/user/change_password" + FORMAT;
    public static final String PAYMENT_HISTORY = BASE_URL + "api/driver/rides" + FORMAT;

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(3000);
        client.get(getAbsoluteUrl(url), params, responseHandler);

        Log.d(TAG, getAbsoluteUrl(url));
    }

    public static void postSync(String url, RequestParams params, JsonHttpResponseHandler jsonHttpResponseHandler) {
        try {
            SyncHttpClient client = new SyncHttpClient();
            client.post(getAbsoluteUrl(url), params, jsonHttpResponseHandler);
            Log.d(TAG, getAbsoluteUrl(url));
        } catch (Exception e) {


        }
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(AsyncHttpClient.DEFAULT_MAX_CONNECTIONS);
        client.post(getAbsoluteUrl(url), params, responseHandler);
        Log.e(TAG, getAbsoluteUrl(url) + params.toString());
    }

    public static String getAbsoluteUrl(String relativeUrl) {
        return relativeUrl;

    }

    public static void setHeader(String header) {
        client.addHeader("X-API-KEY", header);
    }

    public static void setContentType() {
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
    }

    String doGetRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

  /*  String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }*/


}
