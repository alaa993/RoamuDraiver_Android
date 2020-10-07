package com.alaan.roamudriver.custom;

import android.os.Build;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.pojo.Driver_groups_model;
import com.alaan.roamudriver.session.SessionManager;
import com.mapbox.mapboxsdk.Mapbox;

import io.fabric.sdk.android.Fabric;

/**
 * Created by android on 15/3/17.
 */

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapboxkey));

        SessionManager.initialize(getApplicationContext());
        Driver_groups_model.initialize(getApplicationContext());
    }

   public static boolean isM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return true;
        return false;
    }

    public static boolean isO() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            return true;
        return false;
    }
}
