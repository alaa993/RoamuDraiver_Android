package com.alaan.roamudriver.acitivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.pojo.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.stash.Stash;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.custom.GPSTracker;
import com.alaan.roamudriver.custom.Utils;
import com.alaan.roamudriver.session.SessionManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;

import java.util.Calendar;

/**
 * Created by android on 7/3/17.
 */

public class RegisterActivity extends ActivityManagePermission implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    String permissionAsk[] = {
            PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,
            PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION
    };

    private FirebaseAuth mAuth;

    private static final String TAG = "Register";
    RelativeLayout relative_signin;
    TextInputEditText input_email, input_password, input_confirmPassword, input_mobile, input_name, input_last_name;
    AppCompatButton sign_up;
    String token;


    private CircleImageView imageProfile;
    private TextView changePhoto;
    private StorageReference storageRef;
    private FirebaseUser fUser;
    private Uri mImageUri;
    private StorageTask uploadTask;
    private final int PICK_IMAGE_REQUEST = 22;

    private File imageFile;
    String photoURL = "";
    String format = "";
    boolean is_image_selected = false;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Intent intent = getIntent();
        String email = "";

        email = intent.getStringExtra("phone");


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                SessionManager.setGcmToken(token);
            }
        });
        BindView();
        applyfonts();
        input_email.setText(email);

        relative_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();

            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = getCurrentFocus();
                if (view != null) {
                    Utils.hideKeyboard(getApplicationContext(), view);
                }
                if (Utils.haveNetworkConnection(getApplicationContext())) {
                    String latitude = "";
                    String longitude = "";
                    latitude = String.valueOf(currentLatitude);
                    longitude = String.valueOf(currentLongitude);
                    String city = null, state = null, country = null;
                    String email = input_email.getText().toString().trim();
                    String mobile = input_mobile.getText().toString().trim();
                    String password = input_password.getText().toString().trim();
                    String name = input_name.getText().toString().trim();
                    String lastname = input_last_name.getText().toString().trim();

                    Geocoder geocoder;

                    try {
                        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        if (latitude != null && longitude != null) {
                            if (!latitude.equals("0.0") && !longitude.equals("0.0")) {
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                                    if (addresses != null && addresses.size() > 0) {
                                        String merged = "";
                                        city = addresses.get(0).getLocality();
                                        country = addresses.get(0).getCountryName();
                                        state = addresses.get(0).getAdminArea();
                                        if (city != null) {
                                            merged = city;
                                        } else {
                                            city = "null";
                                        }
                                        if (state != null) {
                                            merged = city + "," + state;

                                        } else {
                                            state = "null";
                                        }
                                        if (country != null) {
                                            merged = city + "," + state + "," + country;

                                        } else {
                                            country = "null";
                                        }
                                    }
                                } catch (IOException | IllegalArgumentException e) {

                                    //  e.printStackTrace();
                                    //log.e("data", e.toString());
                                }
                            } else {
                                latitude = "0.0";
                                longitude = "0.0";
                                city = "null";
                                state = "null";
                                country = "null";
                            }
                        } else {
                            latitude = "0.0";
                            longitude = "0.0";
                            city = "null";
                            state = "null";
                            country = "null";
                        }
                    } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {
                        latitude = "0.0";
                        longitude = "0.0";
                        city = "null";
                        state = "null";
                        country = "null";
                    }
                    //original code by alaa, commented by ibrahim
                    //save user profile
//                    String photoURL = "";
//                    User user = new User(name, photoURL);
//                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//                    mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                    // utype = "0" means user and "1" = driver
                    // mtype = "0" means iOS  and "1" = Android

                    if (input_name.getText().toString().trim().equals("")) {
                        input_name.setError(getString(R.string.fiels_is_required));
                    } else if (input_last_name.getText().toString().trim().equals("")) {
                        input_last_name.setError(getString(R.string.fiels_is_required));
                    } else if (is_image_selected == false) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.upload_images), Toast.LENGTH_LONG).show();
                    } else {
                        register(email, mobile, password, name + " " + lastname, latitude, longitude, country, state, city, "1", token, "1", "");
                        saveProfile(name);
                    }
                    //uploadImage2();

                } else {
                    Toast.makeText(RegisterActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
    }

    // Select Image method
    private void SelectImage() {
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            } else {
                TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(RegisterActivity.this)
                        .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {
                                // here is selected uri
                                imageFile = new File(uri.getPath());
                                // profile_pic.setImageURI(uri);
                                format = getMimeType(RegisterActivity.this, uri);
                                Glide.with(RegisterActivity.this).load(uri.getPath()).apply(new RequestOptions().error(R.drawable.user_default)).into(imageProfile);
                                is_image_selected = true;
//                                upload_pic(format);
                                       /* if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("png") || format.equalsIgnoreCase("gif") || format.equalsIgnoreCase("jpeg")) {

                                        } else {
                                            Toast.makeText(getActivity(), "jpg,png or gif is only accepted", Toast.LENGTH_LONG).show();
                                        }*/
                            }
                        }).setOnErrorListener(new TedBottomPicker.OnErrorListener() {
                            @Override
                            public void onError(String message) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
//                                Log.d(getTag(), message);
                            }
                        })
                        .create();

                tedBottomPicker.show(RegisterActivity.this.getSupportFragmentManager());
            }


        } else {
            TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(RegisterActivity.this)
                    .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                        @Override
                        public void onImageSelected(Uri uri) {
                            // here is selected uri
                            imageFile = new File(uri.getPath());
                            //  profile_pic.setImageURI(uri);
                            format = getMimeType(RegisterActivity.this, uri);
                            is_image_selected = true;
//                            upload_pic(format);
                        }
                    }).setOnErrorListener(new TedBottomPicker.OnErrorListener() {
                        @Override
                        public void onError(String message) {
                            Toast.makeText(RegisterActivity.this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
//                            Log.d(getTag(), message);
                        }
                    })
                    .create();

            tedBottomPicker.show(RegisterActivity.this.getSupportFragmentManager());
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private boolean checkIfAlreadyhavePermission() {
        int fine = ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.CAMERA);
        int read = ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (fine == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (read == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (write == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //log.e("permisson", "granted");
                    TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(RegisterActivity.this)
                            .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                                @Override
                                public void onImageSelected(Uri uri) {
                                    // here is selected uri
//                                    imageProfile.setImageURI(uri);
//                                    is_image_selected = true;
                                    imageFile = new File(uri.getPath());
                                    //  profile_pic.setImageURI(uri);
                                    format = getMimeType(RegisterActivity.this, uri);
                                    is_image_selected = true;
                                }
                            }).setOnErrorListener(new TedBottomPicker.OnErrorListener() {
                                @Override
                                public void onError(String message) {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
//                                    Log.d(getTag(), message);
                                }
                            })
                            .create();

                    tedBottomPicker.show(RegisterActivity.this.getSupportFragmentManager());

                } else {

                }
            }
        }
    }

    public void upload_pic(String type) {
//        progressBar.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        if (imageFile != null) {
            try {

                if (type.equals("jpg")) {
                    params.put("avatar", imageFile, "image/jpeg");
                } else if (type.equals("jpeg")) {
                    params.put("avatar", imageFile, "image/jpeg");
                } else if (type.equals("png")) {
                    params.put("avatar", imageFile, "image/png");
                } else {
                    params.put("avatar", imageFile, "image/gif");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("catch", e.toString());
            }
        }
        Server.setHeader(SessionManager.getKEY());
        params.put("user_id", SessionManager.getUserId());

        Server.post(Server.UPDATE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //log.e("success", response.toString());
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        String url = response.getJSONObject("data").getString("avatar");

                        try {
                            //log.i("ibrahim", "url");
                            //log.i("ibrahim", url);
                            Glide.with(RegisterActivity.this).load(photoURL).apply(new RequestOptions().error(R.drawable.user_default)).into(imageProfile);
                        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {
                        }
                        User user = SessionManager.getUser();
                        user.setAvatar(url);
                        Gson gson = new Gson();
                        SessionManager.setUser(gson.toJson(user));
//                        profileUpdateListener.update(url);
//                        input_name.setText(user.getName());
//                        input_email.setText(user.getEmail());
//                        input_mobile.setText(user.getMobile());
//                        input_vehicle.setText(user.getVehicle_info());

                        try {
                            FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = fuser.getUid();
                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users/profile").child(uid);
                            Map<String, Object> userObject = new HashMap<>();
                            userObject.put("photoURL", url);
                            databaseRef.updateChildren(userObject);
                        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {
                        }

                    } else {
//                        progressBar.setVisibility(View.GONE);
                        if (response.has("data")) {
                            Toast.makeText(RegisterActivity.this, response.getString("data"), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

            }
        });
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public void saveProfile(String username) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users/profile").child(uid);
        Map<String, Object> userObject = new HashMap<>();
        userObject.put("username", input_name.getText().toString().trim() + " " + input_last_name.getText().toString().trim());
        userObject.put("photoURL", "https://firebasestorage.googleapis.com/v0/b/roamu-f58c1.appspot.com/o/man-avatar-profile-vector-21372076.jpg?alt=media&token=d8d704ce-9ead-457e-af9e-fd9a263604b8");
        databaseRef.setValue(userObject);
    }

    private void onAuthSuccess(FirebaseUser firebaseUser) {
        String email = firebaseUser.getEmail();
        String username = email;


        User user = new User(username, email);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(firebaseUser.getUid()).setValue(user);


    }

    public Boolean validate() {
        Boolean value = true;

        if (input_name.getText().toString().trim().equals("")) {
            input_name.setError(getString(R.string.fiels_is_required));
            value = false;
        } else {
            input_name.setError(null);
        }
        if (input_email.getText().toString().trim().equals("")) {
            input_email.setError(getString(R.string.email_invalid));
            value = false;
        } else {
            input_email.setError(null);
        }
        if (input_mobile.getText().toString().trim().equals("")) {
            input_mobile.setError(getString(R.string.mobile_invalid));
            value = false;
        } else {
            input_mobile.setError(null);
        }
        if (!(input_password.length() >= 6)) {
            value = false;
            input_password.setError(getString(R.string.password_length));
        } else {
            input_password.setError(null);
        }
        if (!input_password.getText().toString().trim().equals("") && (!input_confirmPassword.getText().toString().trim().equals(input_password.getText().toString().trim()))) {
            value = false;
            input_confirmPassword.setError(getString(R.string.password_nomatch));
        } else {
            input_confirmPassword.setError(null);
        }
        return value;
    }

    public void BindView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        relative_signin = (RelativeLayout) findViewById(R.id.relative_signin);
        input_email = (TextInputEditText) findViewById(R.id.input_email);
        input_name = (TextInputEditText) findViewById(R.id.input_name);
        input_last_name = (TextInputEditText) findViewById(R.id.input_last_name);
        input_password = (TextInputEditText) findViewById(R.id.input_password);
        input_confirmPassword = (TextInputEditText) findViewById(R.id.input_confirmPassword);
        input_mobile = (TextInputEditText) findViewById(R.id.input_mobile);
        input_mobile.setEnabled(false);
        sign_up = (AppCompatButton) findViewById(R.id.sign_up);

        imageProfile = findViewById(R.id.Reg_image_profile);
        changePhoto = findViewById(R.id.Reg_change_photo);
        storageRef = FirebaseStorage.getInstance().getReference().child("Profile");

        AskPermission();
        swipeRefreshLayout.setOnRefreshListener(() -> swipeRefreshLayout.setRefreshing(false));
    }

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            return false;
        }

    }

    public void AskPermission() {
        askCompactPermissions(permissionAsk, new PermissionResult() {
            @Override
            public void permissionGranted() {
                if (!GPSEnable()) {
                    tunonGps();
                } else {
                    getCurrentlOcation();
                }

            }

            @Override
            public void permissionDenied() {

            }

            @Override
            public void permissionForeverDenied() {
                openSettingsApp(getApplicationContext());
            }
        });
    }

    public void applyfonts() {
        TextView textView = (TextView) findViewById(R.id.txt_register);
        Typeface font = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Medium.otf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/AvenirLTStd_Book.otf");
        textView.setTypeface(font);
        input_email.setTypeface(font1);
        input_password.setTypeface(font1);
        input_confirmPassword.setTypeface(font1);
        input_mobile.setTypeface(font1);
        sign_up.setTypeface(font);

    }

    public void register(String email, String mobile, String password, String name, String latitude, String longitude,
                         String country, String state, String city, String mtype, String gcm_token, String utype, String vehicle_info) {
        RequestParams params = new RequestParams();
        //params.put("email", email);
        params.put("mobile", email);
        params.put("password", password);
        params.put("name", name);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("country", country);
        params.put("state", state);
        params.put("city", city);
        params.put("mtype", mtype);
        params.put("utype", utype);
        params.put("vehicle_info", vehicle_info);
        params.put("avatar", "");
        params.put("gcm_token", gcm_token);

        Server.post(Server.REGISTER, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                        //log.i("ibrahim was here", response.toString());

                        if (response.has("data")) {
                            JSONObject data = response.getJSONObject("data");
                            int user_id = Integer.parseInt(data.getString("user_id"));
//                            //log.i("ibrahim travel_id", String.valueOf(travel_id));
                            Date currentDate = Calendar.getInstance().getTime();
                            Date currentTime = Calendar.getInstance().getTime();
                            SavePrivatePost("pickup_address", "drop_address", currentDate.toString(), currentTime.toString(), user_id);
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            if (auth.getCurrentUser() != null) {
                                if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                        @Override
                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            token = instanceIdResult.getToken();
                                            SessionManager.setGcmToken(token);
                                        }
                                    });
                                    login(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), input_password.getText().toString().trim());

                                }
                            }
                        } else {
//                            //log.i("ibrahim_response", "no travel id");
                        }
                    } else {

                        Toast.makeText(RegisterActivity.this, response.getString("data"), Toast.LENGTH_LONG).show();


                    }
                } catch (JSONException e) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    public void login(String email, String password) {

        RequestParams params = new RequestParams();
        params.put("mobile", email);
        // params.put("password", password);
        params.put("utype", "1");
        params.put("gcm_token", token);
//        //log.e("TOKEN",token);
        Server.post(Server.LOGIN, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                        Utils utils = new Utils(RegisterActivity.this);
                        utils.isAnonymouslyLoggedIn();

                        Gson gson = new Gson();
                        User user = gson.fromJson(response.getJSONObject("data").toString(), User.class);
                        //log.i("ibrahim", "response.getJSONObject(\"data\").toString()");
                        //log.i("ibrahim", response.getJSONObject("data").toString());
                        SessionManager.setUser(gson.toJson(user));
                        SessionManager.setIsLogin();
                        upload_pic(format);
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else {
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void SavePrivatePost(String pickup_address, String Drop_address, String date_time_value, String time_value, int travel_id) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());

        databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UserName = dataSnapshot.child("username").getValue(String.class);
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                String text = getString(R.string.Travel_is_going_from) + " " + System.getProperty("line.separator")
                        + getString(R.string.Travel_from) + " " + pickup_address + System.getProperty("line.separator")
                        + getString(R.string.Travel_to) + " " + Drop_address + System.getProperty("line.separator")
                        + getString(R.string.Travel_on) + " " + date_time_value + System.getProperty("line.separator")
                        + getString(R.string.the_clock) + " " + time_value;
//                //log.i("tag","success by ibrahim");
//                //log.i("tag", UserName);
                // Firebase code here
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("private_posts").child(String.valueOf(travel_id));
                Map<String, Object> author = new HashMap<>();
                author.put("uid", user.getUid());
                author.put("username", UserName);
                author.put("photoURL", photoURL);

                Map<String, Object> userObject = new HashMap<>();
                userObject.put("author", author);
                userObject.put("text", text);
                userObject.put("type", "1");
                userObject.put("privacy", "0");
                userObject.put("travel_id", travel_id);
                userObject.put("timestamp", ServerValue.TIMESTAMP);
                databaseRef.setValue(userObject);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void getCurrentlOcation() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mGoogleApiClient.connect();
    }

    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            getCurrentlOcation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and checkky the result in onActivityResult().
                                status.startResolutionForResult(RegisterActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        // by ibrahim
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // end by ibrahim
        android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            //Toast.makeText(getActivity(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentlOcation();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            mImageUri = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                mImageUri);
                imageProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}