package com.alaan.roamudriver.fragement;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.adapter.AcceptedRequestAdapter;
import com.alaan.roamudriver.adapter.MyAcceptedRequestAdapter;
import com.alaan.roamudriver.custom.GPSTracker;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.pojo.firebaseClients;
import com.alaan.roamudriver.pojo.firebaseRide;
import com.alaan.roamudriver.pojo.firebaseTravel;
import com.alaan.roamudriver.session.SessionManager;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class myTravelDetailFragment extends Fragment implements OnMapReadyCallback, DirectionCallback,
        Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private View view;
    RecyclerView recyclerView;
    Button my_acc_d_f_home_button;
    AppCompatButton approve, complete, cancel, start, show_customers;
    TextView PickupPoint, pickup_address_tv, drop_address_tv, MyADF_date, MyADF_TimeVal, txt_Available_Seats, txt_Empty_Seats;

    String permissions[] = {PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};

    Bundle bundle;
    PendingRequestPojo rideJson;
    private String travel_status;

    GPSTracker gpsTracker;
    com.google.android.gms.maps.MapView mMapView;
    GoogleMap myMap;

    private LatLng origin;
    private LatLng destination;
    private boolean checkPayments = false;


//    private String pickup_address;
//    private String drop_address;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private Marker my_marker;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    String distance;
    private Double fare = 50.00;
    Double finalfare;
    private String travel_id = "";
    private String user_id = "";


    DatabaseReference databaseTravelRef;
    DatabaseReference databaseClientsLocation;

    firebaseTravel fbTravel;


    public myTravelDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bundle = getArguments();
        if (bundle != null) {
            rideJson = (PendingRequestPojo) bundle.getSerializable("data");
            travel_status = rideJson.getStatus();

//            databaseRides = FirebaseDatabase.getInstance().getReference("rides").child(rideJson.getRide_id());
//            databaseTravelRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());

        }
        view = inflater.inflate(R.layout.fragment_my_travel_detail, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.passanger_info));
        BindView(savedInstanceState);
        databaseTravelRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener

    }

    public void BindView(Bundle savedInstanceState) {
        gpsTracker = new GPSTracker(getActivity());
        mMapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.MyADF_mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        my_acc_d_f_home_button = (Button) view.findViewById(R.id.my_acc_d_f_home_button);

        show_customers = (AppCompatButton) view.findViewById(R.id.MyADF_btn_show_customers);
        start = (AppCompatButton) view.findViewById(R.id.MyADF_btn_start);
        complete = (AppCompatButton) view.findViewById(R.id.MyADF_btn_complete);
        cancel = (AppCompatButton) view.findViewById(R.id.MyADF_btn_cancel);
        approve = (AppCompatButton) view.findViewById(R.id.MyADF_btn_approve);

        pickup_address_tv = (TextView) view.findViewById(R.id.MyADF_txt_pickuplocation);
        drop_address_tv = (TextView) view.findViewById(R.id.MyADF_txt_droplocation);
        PickupPoint = (TextView) view.findViewById(R.id.MyADF_txt_PickupPoint);
        MyADF_date = (TextView) view.findViewById(R.id.MyADF_dateTimeVal);
        MyADF_TimeVal = (TextView) view.findViewById(R.id.MyADF_TimeVal);

        txt_Available_Seats = (TextView) view.findViewById(R.id.txt_Available_Seats);
        txt_Empty_Seats = (TextView) view.findViewById(R.id.txt_Empty_Seats);

        if (bundle != null) {

            travel_id = rideJson.getTravel_id();
            user_id = rideJson.getUser_id();

            if (rideJson.getPickup_address() != null) {
                pickup_address_tv.setText(rideJson.getPickup_address());
            }
            if (rideJson.getDrop_address() != null) {
                drop_address_tv.setText(rideJson.getDrop_address());
            }
            if (rideJson.getPickup_point() != null) {
                PickupPoint.setText(rideJson.getPickup_point());
            }
            if (rideJson.getDate() != null) {
                MyADF_date.setText(rideJson.getDate());
            }
            if (rideJson.getTime() != null) {
                MyADF_TimeVal.setText(rideJson.getTime());
            }
            if (rideJson.getbooked_set() != null) {
                txt_Available_Seats.setText(rideJson.getbooked_set());
            }
            if (rideJson.getempty_set() != null) {
                txt_Empty_Seats.setText(rideJson.getempty_set());
            }

        }
        setupData();
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogCreate(getString(R.string.ride_completion), getString(R.string.ride_completion), "COMPLETED");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogCreate(getString(R.string.ride_cancellation), getString(R.string.ride_cancellation), "CANCELLED");
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogCreate(getString(R.string.Start_Travel), getString(R.string.Start_Travel), "STARTED");
            }
        });
        show_customers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMySpecificTravel(rideJson.getDriver_id(), rideJson.getTravel_id(), "All", SessionManager.getKEY());
            }
        });
        my_acc_d_f_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvePayment();
            }
        });
    }

    public void setupData() {
        if (bundle != null) {
            if (rideJson.getpickup_location() != null && rideJson.getdrop_location() != null) {
                Log.i("ibrahim", "inside");
                Log.i("ibrahim", rideJson.getpickup_location());
                Log.i("ibrahim", rideJson.getdrop_location());

                String[] pickuplatlong = rideJson.getpickup_location().split(",");
                double pickuplatitude = Double.parseDouble(pickuplatlong[0]);
                double pickuplongitude = Double.parseDouble(pickuplatlong[1]);
                origin = new LatLng(pickuplatitude, pickuplongitude);

                Log.i("ibrahim", origin.toString());

                String[] droplatlong = rideJson.getdrop_location().split(",");
                double droplatitude = Double.parseDouble(droplatlong[0]);
                double droplongitude = Double.parseDouble(droplatlong[1]);
                destination = new LatLng(droplatitude, droplongitude);
                Log.i("ibrahim", destination.toString());
            }
            Log.i("ibrahim", "status");
            Log.i("ibrahim", travel_status);
            if (!travel_status.equals("") && travel_status.equalsIgnoreCase("PENDING")) {
                approve.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
                complete.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
            }
            if (travel_status != null && !travel_status.equals("") && travel_status.equalsIgnoreCase("CANCELLED")) {
                approve.setVisibility(View.GONE);
                start.setVisibility(View.GONE);
                complete.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }
            if (travel_status != null && !travel_status.equals("") && travel_status.equalsIgnoreCase("COMPLETED")) {
                approve.setVisibility(View.GONE);
                start.setVisibility(View.GONE);
                complete.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }
            if (travel_status != null && !travel_status.equals("") && travel_status.equalsIgnoreCase("STARTED")) {
                if (checkPayments()) {
                    Log.i("ibrahim", "checkPayments");
                    Log.i("ibrahim", String.valueOf(checkPayments()));
                    approve.setVisibility(View.VISIBLE);
                    complete.setVisibility(View.GONE);
                    start.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                } else {
                    approve.setVisibility(View.GONE);
                    start.setVisibility(View.GONE);
                    complete.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                }
            }
            if (travel_status != null && !travel_status.equals("") && travel_status.equalsIgnoreCase("PAID")) {
                approve.setVisibility(View.GONE);
                start.setVisibility(View.GONE);
                complete.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
            }
            getMySpecificTravel(rideJson.getDriver_id(), rideJson.getTravel_id(), "All", SessionManager.getKEY());
        }
    }

    public void AlertDialogCreate(String title, String message, final String status) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new AlertDialog.Builder(getActivity())
                .setIcon(drawable)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.ccancel), null)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        SendStatus(travel_id, status);
                    }
                })
                .setNegativeButton(getString(R.string.ccancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void SendStatus(String travel_id, final String status) {
        RequestParams params = new RequestParams();
        params.put("travel_id", travel_id);
        params.put("travel_status", status);
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.RIDES_STATUS_CHANGE, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("ibrahim", "response.toString()");
                Log.i("ibrahim", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());

                        if (response.has("data") && response.getJSONArray("data").length() > 0) {
                            if (list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    if (status.equalsIgnoreCase("COMPLETED")) {
                                        updateNotificationFirebase(list.get(i).getRide_id(), list.get(i).getUser_id(), getString(R.string.notification_5));
                                    }
                                    updateRideFirebase(list.get(i).getRide_id(), status, list.get(i).getStatus(), list.get(i).getPayment_status(), list.get(i).getPayment_mode());
                                    updateNotificationFirebase(list.get(i).getRide_id(), list.get(i).getUser_id(), list.get(i).getStatus());
                                }
                            }
                        }
                        Log.i("ibrahim", "status");
                        Log.i("ibrahim", status);
                        if (status.equalsIgnoreCase("PENDING")) {
                            approve.setVisibility(View.GONE);
                            start.setVisibility(View.VISIBLE);
                            complete.setVisibility(View.GONE);
                            cancel.setVisibility(View.VISIBLE);
                        }
                        if (status.equalsIgnoreCase("CANCELLED")) {
                            approve.setVisibility(View.GONE);
                            start.setVisibility(View.GONE);
                            complete.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                        }
                        if (status.equalsIgnoreCase("COMPLETED")) {
                            approve.setVisibility(View.GONE);
                            start.setVisibility(View.GONE);
                            complete.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                        }
                        if (status.equalsIgnoreCase("STARTED")) {
                            checkPayments();
//                            if (checkPayments()) {
//                                Log.i("ibrahim", "checkPayments_true");
//                                Log.i("ibrahim", String.valueOf(checkPayments()));
//                                approve.setVisibility(View.VISIBLE);
//                                complete.setVisibility(View.GONE);
//                                start.setVisibility(View.GONE);
//                                cancel.setVisibility(View.GONE);
//                            } else {
//                                Log.i("ibrahim", "checkPayments_false");
//                                Log.i("ibrahim", String.valueOf(checkPayments()));
//                                approve.setVisibility(View.GONE);
//                                start.setVisibility(View.GONE);
//                                complete.setVisibility(View.GONE);
//                                cancel.setVisibility(View.GONE);
//                            }
                        }

                        getMySpecificTravel(rideJson.getDriver_id(), rideJson.getTravel_id(), "All", SessionManager.getKEY());
                    } else {
                        String data = response.getJSONObject("data").toString();
                        Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
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

    public boolean checkPayments() {
        Log.i("ibrahim", "checkPayments_inside");
        RequestParams params = new RequestParams();
        params.put("travel_id", travel_id);
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.get(Server.checkAllPayments, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("ibrahim", "response.toString()");
                Log.i("ibrahim", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status")
                            && response.getString("status").equalsIgnoreCase("success")
                            && response.getString("data").equalsIgnoreCase("true")) {
//                        checkPayments = true;
//                        Log.i("ibrahim_checkPayments1", String.valueOf(checkPayments));
                        approve.setVisibility(View.VISIBLE);
                        complete.setVisibility(View.GONE);
                        start.setVisibility(View.GONE);
                        cancel.setVisibility(View.GONE);

                    } else {
//                        checkPayments = false;
//                        Log.i("ibrahim_checkPayments2", String.valueOf(checkPayments));
                        approve.setVisibility(View.GONE);
                        start.setVisibility(View.GONE);
                        complete.setVisibility(View.GONE);
                        cancel.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    checkPayments = false;
                    Log.i("ibrahim_checkPayments3", String.valueOf(checkPayments));
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
        Log.i("ibrahim_checkPayments4", String.valueOf(checkPayments));
        return checkPayments;
    }

    private void approvePayment() {
        Log.i("ibrahim", "approvePayment");
        Log.i("ibrahim", "approvePayment");

        RequestParams params = new RequestParams();
        params.put("travel_id", travel_id);
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.get(Server.APPROVE_PAYMENT_ALL, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("ibrahim", "response.toString()");
                Log.i("ibrahim", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());

                        if (response.has("data") && response.getJSONArray("data").length() > 0) {
                            if (list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    updateRideFirebase(list.get(i).getRide_id(), "PAID", list.get(i).getStatus(), list.get(i).getPayment_status(), list.get(i).getPayment_mode());
                                    updateNotificationFirebase(list.get(i).getRide_id(), list.get(i).getUser_id(), getString(R.string.notification_5));
                                }
                            }
                        }
                        Toast.makeText(getActivity(), getString(R.string.approve_payment_offline), Toast.LENGTH_LONG).show();
                        approve.setVisibility(View.GONE);
                        start.setVisibility(View.GONE);
                        complete.setVisibility(View.VISIBLE);
                        cancel.setVisibility(View.GONE);
                    } else {
//                        String data = response.getJSONObject("data").toString();
//                        Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
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

    public void updateRideFirebase(String ride_id, String travel_status, String ride_status, String payment_status, String payment_mode) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(ride_id);
        Map<String, Object> rideObject = new HashMap<>();

        rideObject.put("ride_status", ride_status);
        rideObject.put("travel_status", travel_status);
        rideObject.put("payment_status", payment_status);
        rideObject.put("payment_mode", payment_mode);
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        databaseRef.setValue(rideObject);
    }

    public void updateNotificationFirebase(String ride_id, String user_id, String notificationText) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(user_id).push();
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_id", ride_id);
        rideObject.put("text", getString(R.string.RideUpdated) + " " + notificationText);
        rideObject.put("readStatus", "0");
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        rideObject.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseRef.setValue(rideObject);
    }


    public void getMySpecificTravel(String driver_id, String travel_id, String status, String key) {
        Log.i("ibrahim", "getMyTravels");

        RequestParams params = new RequestParams();
        params.put("driver_id", driver_id);
        params.put("travel_id", travel_id);
        params.put("status", status);
//        params.put("utype", "1");
        Server.setHeader(key);
        Server.get(Server.driver_specific_travel, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();

                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());

                        if (response.has("data") && response.getJSONArray("data").length() == 0) {

                            MyAcceptedRequestAdapter acceptedRequestAdapter = new MyAcceptedRequestAdapter(list);
                            recyclerView.removeAllViews();
                            recyclerView.setAdapter(acceptedRequestAdapter);
                            acceptedRequestAdapter.notifyDataSetChanged();

                        } else {

                            MyAcceptedRequestAdapter acceptedRequestAdapter = new MyAcceptedRequestAdapter(list);
                            recyclerView.setAdapter(acceptedRequestAdapter);
                            acceptedRequestAdapter.notifyDataSetChanged();
                        }

                    } else {

                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (getActivity() != null) {
            if (direction.isOK()) {
                Log.i("ibrahim", "onDirectionSuccess");
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 2, Color.BLUE));

                myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title(getString(R.string.pick_up_location)).snippet(rideJson.getPickup_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title(getString(R.string.drop_up_location)).snippet(rideJson.getDrop_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
//                calculateDistance(Double.valueOf(direction.getRouteList().get(0).getLegList().get(0).getDistance().getValue()) / 1000);
            } else {
//                distanceAlert(direction.getErrorMessage());
                //calculateFare.setVisibility(View.GONE);
//                dismiss();
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
    }

    @SuppressLint("MissingPermission")
    public void onConnected(@Nullable Bundle bundle) {
        try {
            @SuppressLint("MissingPermission") android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                if (myMap != null) {
//                    myMap.clear();
                    my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("You are here.").icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi)));
                    my_marker.showInfoWindow();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 12);
                    myMap.animateCamera(cameraUpdate);
                    myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                        }
                    });
                    databaseTravelRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            fbTravel = dataSnapshot.getValue(firebaseTravel.class);
//                            Log.i("ibrahim", fbTravel.toString());
//                            Log.i("ibrahim_travel", fbTravel.driver_id);
//                            Log.i("ibrahim_travel", fbTravel.clients.toString());
                            drawMap(fbTravel);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                        }
                    });
                }
                setCurrentLocation(currentLatitude, currentLongitude);
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        databaseTravelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbTravel = dataSnapshot.getValue(firebaseTravel.class);
                Log.i("ibrahim", fbTravel.toString());
                Log.i("ibrahim_travel", fbTravel.driver_id);
                Log.i("ibrahim_travel", fbTravel.Clients.toString());
                drawMap(fbTravel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (mMapView != null) {
                mMapView.onSaveInstanceState(outState);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            if (mMapView != null) {
                mMapView.onLowMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (mMapView != null) {
                mMapView.onStop();
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (mMapView != null) {
                mMapView.onResume();
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setMaxZoomPreference(80);
        requestDirection();
        //by ibrahim
        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                View v = null;
                if (getActivity() != null) {
                    v = getActivity().getLayoutInflater().inflate(R.layout.view_custom_marker, null);
                    TextView title = (TextView) v.findViewById(R.id.t);
                    TextView t1 = (TextView) v.findViewById(R.id.t1);
                    TextView t2 = (TextView) v.findViewById(R.id.t2);
                    Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
                    t1.setTypeface(font);
                    t2.setTypeface(font);
                    String name = marker.getTitle();
                    title.setText(name);
                    String info = marker.getSnippet();
                    t1.setText(info);
//                    driver_id = (String) marker.getTag();
//                    drivername = marker.getTitle();
                }
                return v;
            }
        });
        if (myMap != null) {
            tunonGps();
        }
    }

    @SuppressLint("MissingPermission")
    public void drawMap(firebaseTravel fbTravel) {
        try {
            @SuppressLint("MissingPermission") android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                if (myMap != null) {
                    myMap.clear();
                    my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("You are here.").icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi)));
                    my_marker.showInfoWindow();
                    Log.i("ibrahim", "drawRoute");
                    for (Map.Entry<String, String> entry : fbTravel.Clients.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        databaseClientsLocation = FirebaseDatabase.getInstance().getReference("Location").child(value);
                        databaseClientsLocation.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    firebaseClients clients = dataSnapshot.getValue(firebaseClients.class);
//                                    firebaseUsers.put(dataSnapshot.getKey(),clients);
                                    if (clients != null) {
                                        my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(clients.latitude, clients.longitude)).title("User").icon(BitmapDescriptorFactory.fromResource(R.drawable.user_default)));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Log.i("ibrahim", value.toString());
                    }


                }
                setCurrentLocation(currentLatitude, currentLongitude);
            }
        } catch (Exception e) {

        }
    }

    public void setCurrentLocation(final Double lat, final Double log) {
        try {
            my_marker.setPosition(new LatLng(lat, log));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 12);
            myMap.animateCamera(cameraUpdate);
            RequestParams par = new RequestParams();
            Server.setHeader(SessionManager.getKEY());
            par.put("user_id", SessionManager.getUserId());
            par.add("latitude", String.valueOf(currentLatitude));
            par.add("longitude", String.valueOf(currentLongitude));
            Server.post(Server.UPDATE, par, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                }

            });
        } catch (Exception e) {

        }
    }

    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
                                status.startResolutionForResult(getActivity(), 1000);
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

    public void getCurrentlOcation() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }

    public void calculateDistance(Double aDouble) {

        distance = String.valueOf(aDouble);
//        confirm.setEnabled(true);

        if (aDouble != null) {
            if (fare != null && fare != 0.0) {
                DecimalFormat dtime = new DecimalFormat("##.##");
                Double ff = aDouble * fare;

                try {

                    if (dtime.format(ff).contains(",")) {
                        String value = dtime.format(ff).replaceAll(",", ".");
                        finalfare = Double.valueOf(value);
                    } else {

                        finalfare = Double.valueOf(dtime.format(ff));
                    }
//                    dismiss();

                } catch (Exception e) {

                }

                // txt_fare.setText(finalfare + " " + SessionManager.getUnit());
                //  txt_fare.setText(finalfare + " $ ");

            } else {
                //txt_fare.setText(SessionManager.getUnit());
            }
        }
    }

    public void requestDirection() {

        try {
//            Snackbar.make(view, getString(R.string.direct_requesting), Snackbar.LENGTH_SHORT).show();
            GPSTracker tracker = new GPSTracker(((HomeActivity) getActivity()));
            LatLng myLocation = new LatLng(tracker.getLatitude(), tracker.getLongitude());

            GoogleDirection.withServerKey(getString(R.string.google_android_map_api_key))
                    .from(origin)
                    .to(destination)
                    .transportMode(TransportMode.DRIVING)//.waypoints(Collections.singletonList(origin))
                    .execute(this);

            GoogleDirection.withServerKey(getString(R.string.google_android_map_api_key))
                    .from(myLocation)
                    .to(origin)
                    .transportMode(TransportMode.DRIVING)//.waypoints(Collections.singletonList())
                    .execute(this);
        } catch (Exception e) {
        }
    }

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            gpsTracker.showSettingsAlert();
            return false;
        }
    }

    private void launchNavigation() {
        if (GPSEnable()) {

            try {
                String[] latlong = rideJson.getpickup_location().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                String[] latlong1 = rideJson.getdrop_location().split(",");
                double latitude1 = Double.parseDouble(latlong1[0]);
                double longitude1 = Double.parseDouble(latlong1[1]);

                Point origin = Point.fromLngLat(longitude, latitude);
                Point destination = Point.fromLngLat(longitude1, latitude1);

                fetchRoute(origin, destination);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.toString() + " ", Toast.LENGTH_SHORT).show();
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute.builder(getActivity())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        DirectionsRoute directionsRoute = response.body().routes().get(0);
                        startNavigation(directionsRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationLauncherOptions.Builder navigationLauncherOptions = NavigationLauncherOptions.builder();
        navigationLauncherOptions.shouldSimulateRoute(false);
        navigationLauncherOptions.enableOffRouteDetection(true);
        navigationLauncherOptions.snapToRoute(true);
        navigationLauncherOptions.directionsRoute(directionsRoute);
        NavigationLauncher.startNavigation(getActivity(), navigationLauncherOptions.build());


    }

}