package com.alaan.roamudriver.Server;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.alaan.roamudriver.adapter.Group_membar_Adapter;
import com.alaan.roamudriver.pojo.Group_membar;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.session.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by android on 18/4/17.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static int NOTIFICATION_ID = 1;
    private String TAG = "FireBaseService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            if (remoteMessage.getData().get("msg") != null) {
                String statusCode = remoteMessage.getData().get("msg");
                String resourceAppStatusString = "notification_".concat(statusCode);
                int messageId = getResourceId(resourceAppStatusString, "string", getPackageName());
                String message = getString(messageId);

                if (remoteMessage.getData().get("msg").equals("0") && remoteMessage.getData().get("name") != null) {
                    message = remoteMessage.getData().get("name") + " " + message;
                } else if (remoteMessage.getData().get("msg").equals("2") && remoteMessage.getData().get("name") != null) {
                    message = remoteMessage.getData().get("name") + " " + message;
                } else if (remoteMessage.getData().get("msg").equals("3") && remoteMessage.getData().get("name") != null) {
                    message = remoteMessage.getData().get("name") + " " + message;
                } else if (remoteMessage.getData().get("msg").equals("5") && remoteMessage.getData().get("name") != null) {
                    message = remoteMessage.getData().get("name") + " " + message;
                } else if (remoteMessage.getData().get("msg").equals("6") && remoteMessage.getData().get("name") != null) {
                    message = remoteMessage.getData().get("name") + " " + message;
                } else if (remoteMessage.getData().get("msg").equals("7") && remoteMessage.getData().get("name") != null) {
                    message = remoteMessage.getData().get("name") + " " + message;
                }
                sendNotification(remoteMessage.getData(), message);
            }
        } catch (Resources.NotFoundException e) {
            System.err.println("Resources NotFoundException exception");
            try {
                sendNotification(remoteMessage.getData(), remoteMessage.getData().get("msg"));
            } catch (Resources.NotFoundException f) {
                System.err.println("Resources NotFoundException exception");
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);
    }

    private void sendNotification(Map<String, String> data, String message) {

        int num = ++NOTIFICATION_ID;
        Bundle msg = new Bundle();
        for (String key : data.keySet()) {
            //log.e(key, data.get(key));
            msg.putString(key, data.get(key));
        }

        //.setContentText(msg.getString("msg"))

        Intent intent = new Intent(this, HomeActivity.class);
        if (msg.containsKey("action")) {
            intent.putExtra("action", msg.getString("action"));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, num /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, String.valueOf(NOTIFICATION_ID))
                .setSmallIcon(R.drawable.taxi)
                .setContentTitle(msg.getString("title"))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(String.valueOf(NOTIFICATION_ID), "MyNotification", importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(num, notificationBuilder.build());
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        SessionManager.initialize(getApplicationContext());
        RequestParams params = new RequestParams();
        SessionManager.setGcmToken(token);
        Server.setHeader(SessionManager.getKEY());
        params.put("user_id", SessionManager.getUserId());
        params.put("gcm_token", token);
        Server.postSync("api/user/update/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, responseString);
            }
        });

    }

    private int getResourceId(String pVariableName, String pResourceName, String pPackageName) {
        try {
            return getResources().getIdentifier(pVariableName, pResourceName, pPackageName);
        } catch (NullPointerException e) {
            System.err.println("Null pointer exception");
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
