package com.alaan.roamudriver.Server;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

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



        if(remoteMessage.getData().get("msg") != null){
            String statusCode = remoteMessage.getData().get("msg");
            String resourceAppStatusString = "notification_".concat(statusCode);
            int messageId = getResourceId(resourceAppStatusString, "string", getPackageName());
            String message = getString(messageId);

            Log.i("notifcode by ibrahim",remoteMessage.getData().get("msg").toString());
            Log.i("notification by ibrahim",message);

            if (remoteMessage.getData().get("msg").equals("0") && remoteMessage.getData().get("name") != null)
            {
                message = remoteMessage.getData().get("name") + " " + message;
            }else if (remoteMessage.getData().get("msg").equals("2") && remoteMessage.getData().get("name") != null)
            {
                message = remoteMessage.getData().get("name") + " " + message;
            }else if (remoteMessage.getData().get("msg").equals("3") && remoteMessage.getData().get("name") != null)
            {
                message = remoteMessage.getData().get("name") + " " + message;
            }else if (remoteMessage.getData().get("msg").equals("5") && remoteMessage.getData().get("name") != null)
            {
                message = remoteMessage.getData().get("name") + " " + message;
            }else if (remoteMessage.getData().get("msg").equals("6") && remoteMessage.getData().get("name") != null)
            {
                message = remoteMessage.getData().get("name") + " " + message;
            }else if (remoteMessage.getData().get("msg").equals("7") && remoteMessage.getData().get("name") != null)
            {
                message = remoteMessage.getData().get("name") + " " + message;
            }
            sendNotification(remoteMessage.getData(), message);
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
            Log.e(key, data.get(key));
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
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,String.valueOf(NOTIFICATION_ID))
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
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
