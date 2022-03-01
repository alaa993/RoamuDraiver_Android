package com.alaan.roamudriver.acitivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.custom.Utils;
import com.alaan.roamudriver.pojo.User;
import com.alaan.roamudriver.session.SessionManager;
import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.alaan.roamudriver.fragement.lang.setLocale;

/**
 * Created by android on 7/3/17.
 */

public class SplashActivity extends ActivityManagePermission {
    private final static int SPLASH_TIME_OUT = 500;
    String token;
    public static final int OPEN_NEW_ACTIVITY = 123;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stash.init(this);
        if (Stash.getString("TAG_LANG") != null) {
            setLocale(Stash.getString("TAG_LANG"), this);
        } else {
            setLocale("en", this);
        }
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.splash_activity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Askpermission();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void Askpermission() {
        askCompactPermissions(permissionAsk, new PermissionResult() {
            @Override
            public void permissionGranted() {
                redirect();
            }

            @Override
            public void permissionDenied() {
                redirect();
            }

            @Override
            public void permissionForeverDenied() {
                redirect();
            }
        });
    }

    private void redirect() {
        // SessionManager.getInstance().setPref(getApplicationContext());
        if (SessionManager.isLoggedIn()) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            token = instanceIdResult.getToken();
                            SessionManager.setGcmToken(token);
                            login(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), "");
                        }
                    });
                } else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivityForResult(i, OPEN_NEW_ACTIVITY);
                }
            } else {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivityForResult(i, OPEN_NEW_ACTIVITY);
            }
        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivityForResult(i, OPEN_NEW_ACTIVITY);
        }
//        finish();
    }

    public void login(String email, String password) {

        RequestParams params = new RequestParams();
        params.put("mobile", email);
        params.put("utype", "1");
        params.put("gcm_token", token);
        Server.post(Server.LOGIN, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                        Utils utils = new Utils(SplashActivity.this);
                        utils.isAnonymouslyLoggedIn();
                        if (response.has("data")) {
                            Gson gson = new Gson();
                            User user = gson.fromJson(response.getJSONObject("data").toString(), User.class);
                            //log.i("ibrahim", "response.getJSONObject(\"data\").toString()");
                            //log.i("ibrahim", response.getJSONObject("data").toString());
                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                            if (user.getBrand().length() == 0 || user.getModel().length() == 0 || user.getYear().length() == 0 || user.getVehicle_no().length() == 0 || user.getColor().length() == 0) {
                                intent.putExtra("go", "vehicle");
                                startActivityForResult(intent, OPEN_NEW_ACTIVITY);
                            } else {
                                startActivityForResult(new Intent(SplashActivity.this, HomeActivity.class), OPEN_NEW_ACTIVITY);
                            }
                        }
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        startActivityForResult(new Intent(SplashActivity.this, LoginActivity.class), OPEN_NEW_ACTIVITY);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_NEW_ACTIVITY) {
            finish();
        }
    }
}