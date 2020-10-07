package com.alaan.roamudriver.pojo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;

public class Driver_groups_model implements Serializable {
    private static final String GROUP_NAME = "Group";
    private static final String GROUP_ID = "ID";
    String GroupName;
    static String USER = "GROUP";
    int Group_id;
    int Driver_id;
    static SharedPreferences pref;
    public static void initialize(Context context) {
        if (pref == null)
            pref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);


    }

    public static String getGroupName() {
        return pref.getString(GROUP_NAME,null);
    }



    public void setGroupName(String groupName) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(GROUP_NAME, groupName);
        prefsEditor.commit();
    }

    public static int getGroup_id() {
        return pref.getInt(GROUP_ID,0);
    }

    public void setGroup_id(int group_id) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putInt(GROUP_ID, group_id);
        prefsEditor.commit();
    }

    public int getDriver_id() {
        return Driver_id;
    }

    public void setDriver_id(int driver_id) {
        Driver_id = driver_id;
    }
}
