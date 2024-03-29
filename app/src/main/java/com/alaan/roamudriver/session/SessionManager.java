package com.alaan.roamudriver.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.alaan.roamudriver.acitivities.LoginActivity;
import com.alaan.roamudriver.pojo.User;

import java.util.HashMap;


/**
 * Created by android on 9/3/17.
 */


public class SessionManager {
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Editor for Shared preferences
    //SharedPreferences.Editor editor;

    // Context
    //Context _context;

    // Shared pref mode
    //int PRIVATE_MODE = 0;
    public static final String AVATAR = "avatar";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_PAYPALID = "paypal_id";
    public static final String KEY_VEHICLE = "vehicle";
    public static final String GCM_TOKEN = "gcm_token";
    public static final String KEY_EMAIL = "email";
    public static final String FARE_UNIT = "unit";
    public static final String GROUP_NAME = "Group";
    public static final String GROUP_ID = "id";

    public static final String LOGIN_AS = "login_as";
    public static final String USER_ID = "user_id";
    public static final String car_type = "car_type";


    // Email address (make variable public to access from outside)
    public static final String IS_ONLINE = "false";
    public static final String USER = "user";
    public static final String BRAND = "brand";
    public static final String MODEL = "model";
    public static final String YEAR = "year";
    public static final String COLOR = "color";
    public static final String DRivingLicence = "licence";

    // vehicle info variables
    public static final String VehicleInsurance = "insurance";
    public static final String VehicleNo = "no";
    public static final String VehiclePermit = "permit";
    public static final String VehicleRegistartion = "registration";
    // Sharedpref file name
    private static final String PREF_NAME = "roamu";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // Shared Preferences
    static SharedPreferences pref;
    static SessionManager app;
    public String KEY = "key";
    private int name;
    private String avatar;
    private String email;


    public SessionManager() {
    }

    public static void initialize(Context context) {
        if (pref == null)
            pref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);


    }

    public static String getStatus() {

        return pref.getString(IS_ONLINE, null);
    }

    /*public static SessionManager getInstance(Context context) {
        if (app == null) {
            app = new SessionManager();

        }
        app.setrPref();
        return (app);
    }*/

    public static void setStatus(String staus) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(IS_ONLINE, staus);
        editor.commit();
    }

    public static void setIsLogin() {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(IS_LOGIN, true);
        prefsEditor.commit();

    }

    public static User getUser() {
        Gson gson = new Gson();
        return gson.fromJson(pref.getString(USER, null), User.class);

    }

    public static void setUser(String user) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(USER, user);
        prefsEditor.commit();
    }

    public static String getKEY() {

        return getUser() == null ? "" : getUser().getKey();
    }

 /*   public SharedPreferences getPref() {
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        return pref;
    }

    public SharedPreferences.Editor getDb() {
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        return editor;
    }*/

    public void setKEY(String k) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(KEY, k);
        prefsEditor.commit();
    }

    public static String getUnit() {

        return getUser() == null ? "" : getUser().getUnit();
    }

    public static void setUnit(String unit) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(FARE_UNIT, unit);
        prefsEditor.commit();
    }

    public static String getUserId() {
        return getUser() == null ? "" : getUser().getUser_id();
    }

    public static String getCarType() {
        return getUser() == null ? "" : getUser().getCarType();
    }

    /**
     * Clear session details
     */


    public static void logoutUser(Context _context) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(IS_LOGIN, false);
        prefsEditor.clear();
        prefsEditor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
    }

    /*public SharedPreferences setPref(Context context) {

        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        return pref;

    }*/

 /*   public SharedPreferences setrPref() {
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        return pref;
    }*/
    // Constructor

    /**
     * Quick checkky for login
     **/
    // Get Login State
    public static boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public static String getName() {
        return getUser() == null ? "" : getUser().getName();
    }

    /*  */

    /**
     * Create login session
     *//*
    public void createLoginSession(String name, String email, String user, String avatar, String mobile, String vehicle) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_VEHICLE, vehicle);
        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(USER_ID, user);
        editor.putString(AVATAR, avatar);


        // commit changes
        editor.commit();
    }

    public void setVehicleInfo(String brand, String model, String year, String color, String licence,
                               String insurance, String no, String permit, String registration) {

        editor.putString(BRAND, brand);
        editor.putString(MODEL, model);
        editor.putString(YEAR, year);
        editor.putString(COLOR, color);

        editor.putString(DRivingLicence, licence);
        editor.putString(VehicleInsurance, insurance);
        editor.putString(VehicleNo, no);
        editor.putString(VehiclePermit, permit);
        editor.putString(VehicleRegistartion, registration);
        editor.commit();
    }

    */
    public static String getAvatar() {
        return getUser() == null ? "" : getUser().getAvatar();
    }

    public static void setAvatar(String avatar) {


    }

    public String getGcmToken() {
        return pref.getString(GCM_TOKEN, null);
    }

    public static void setGcmToken(String gcmToken) {

        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(GCM_TOKEN, gcmToken);
        prefsEditor.commit();
    }

    public void setpaypalId(String k) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(KEY_PAYPALID, k);
        prefsEditor.commit();
    }

    public String getPaypalId() {

        return getUser().getPaypal_id();
    }

    /**
     * Check login method wil checkky user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin(Context _context) {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
        user.put(KEY_VEHICLE, pref.getString(KEY_VEHICLE, null));
        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(USER_ID, pref.getString(USER_ID, null));
        user.put(car_type, pref.getString(car_type, null));
        user.put(AVATAR, pref.getString(AVATAR, null));

        user.put(BRAND, pref.getString(BRAND, null));
        user.put(MODEL, pref.getString(MODEL, null));
        user.put(YEAR, pref.getString(YEAR, null));
        user.put(COLOR, pref.getString(COLOR, null));

        user.put(DRivingLicence, pref.getString(DRivingLicence, null));
        user.put(VehicleInsurance, pref.getString(VehicleInsurance, null));
        user.put(VehicleNo, pref.getString(VehicleNo, null));
        user.put(VehiclePermit, pref.getString(VehiclePermit, null));
        user.put(VehicleRegistartion, pref.getString(VehicleRegistartion, null));


        // return user
        return user;
    }

    public String getEmail() {
        return getUser() == null ? "" : getUser().getEmail();
    }

    public String getMobile() {
        return getUser().getMobile();
    }


}
