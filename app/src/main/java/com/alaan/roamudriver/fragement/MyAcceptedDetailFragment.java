package com.alaan.roamudriver.fragement;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
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
import com.alaan.roamudriver.custom.GPSTracker;
import com.alaan.roamudriver.custom.LocationService;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.pojo.Post;
import com.alaan.roamudriver.pojo.Tracking;
import com.alaan.roamudriver.pojo.firebaseClients;
import com.alaan.roamudriver.pojo.firebaseRide;
import com.alaan.roamudriver.pojo.firebaseTravel;
import com.alaan.roamudriver.session.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
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
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MyAcceptedDetailFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback,
        Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, BackFragment, LocationListener {

    AppCompatButton trackRide, complete, cancel, approve, accept;
    TextView title, drivername, mobilenumber, pickup_location, drop_location, payment_status_TV, PickupPoint, MyADF_date, MyADF_TimeVal;


    String permissions[] = {PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};

    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    private View view;
    private String pickup = "";
    private String drop = "";
    private String driver = "";
    private String mobile = "";
    private String ride_id = "";
    private String user_id;

    //    private SwipeRefreshLayout swipeRefreshLayout;
    TableRow mobilenumber_row;
    GPSTracker gpsTracker;

    Bundle bundle;
    PendingRequestPojo rideJson;
    private String travel_status;
    private String ride_status;
    private String payment_status;
    private String payment_mode;

    DatabaseReference databaseRides;
    ValueEventListener listener;


    DatabaseReference databaseTravelRef;
    DatabaseReference databaseClientsLocation;

    firebaseTravel fbTravel;


    Button my_acc_d_f_home_button;

    com.google.android.gms.maps.MapView mMapView;
    GoogleMap myMap;

    private LatLng origin;
    private LatLng destination;
    private String pickup_address;
    private String drop_address;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private Marker my_marker;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    String distance;
    private Double fare = 50.00;
    Double finalfare;


    public MyAcceptedDetailFragment() {
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
            travel_status = rideJson.getTravel_status();
            ride_status = rideJson.getStatus();
            payment_status = rideJson.getPayment_status();
            payment_mode = rideJson.getPayment_mode();

            Log.i("ibrahim", rideJson.getRide_id());

            databaseRides = FirebaseDatabase.getInstance().getReference("rides").child(rideJson.getRide_id());
            databaseTravelRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());

        }
        view = inflater.inflate(R.layout.fragment_my_accepted_detail, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.passanger_info));
        BindView(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                    openSettingsApp(getActivity());
                }
            });

        } else {
            if (!GPSEnable()) {
                tunonGps();
            } else {
                getCurrentlOcation();
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        listener = databaseRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseRide fbRide = dataSnapshot.getValue(firebaseRide.class);
                Log.i("ibrahim ride", "----------");
                if (fbRide != null) {
                    travel_status = fbRide.travel_status;
                    ride_status = fbRide.ride_status;
                    payment_status = fbRide.payment_status;
                    payment_mode = fbRide.payment_mode;
                }
                setupData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
                        SendStatus(ride_id, status);
                    }
                })
                .setNegativeButton(getString(R.string.ccancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void BindView(Bundle savedInstanceState) {
        gpsTracker = new GPSTracker(getActivity());

        mMapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.MyADF_mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.MyADFswipe_refresh);
        my_acc_d_f_home_button = (Button) view.findViewById(R.id.my_acc_d_f_home_button);
        accept = (AppCompatButton) view.findViewById(R.id.MyADF_btn_accept);
        complete = (AppCompatButton) view.findViewById(R.id.MyADF_btn_complete);
        approve = (AppCompatButton) view.findViewById(R.id.MyADF_btn_approve);
        cancel = (AppCompatButton) view.findViewById(R.id.MyADF_btn_cancel);
        trackRide = (AppCompatButton) view.findViewById(R.id.MyADF_btn_trackride);

        drivername = (TextView) view.findViewById(R.id.MyADF_txt_drivername);
        mobilenumber = (TextView) view.findViewById(R.id.MyADF_txt_mobilenumber);
        pickup_location = (TextView) view.findViewById(R.id.MyADF_txt_pickuplocation);
        drop_location = (TextView) view.findViewById(R.id.MyADF_txt_droplocation);
        payment_status_TV = (TextView) view.findViewById(R.id.MyADF_payment_status_TV);

        PickupPoint = (TextView) view.findViewById(R.id.MyADF_txt_PickupPoint);
        MyADF_date = (TextView) view.findViewById(R.id.MyADF_dateTimeVal);
        MyADF_TimeVal = (TextView) view.findViewById(R.id.MyADF_TimeVal);

        mobilenumber_row = view.findViewById(R.id.MyADF_mobilenumber_row);

        pickup_location.setSelected(true);
        drop_location.setSelected(true);
        if (bundle != null) {
            ride_id = rideJson.getRide_id();
            user_id = rideJson.getUser_id();

            pickup = rideJson.getPickup_address();
            drop = rideJson.getDrop_address();
            driver = rideJson.getUser_name();
            mobile = rideJson.getUser_mobile();
            PickupPoint.setText(rideJson.getPickup_point());
            MyADF_date.setText(rideJson.getDate());
            MyADF_TimeVal.setText(rideJson.getTime());

            if (pickup != null) {
                pickup_location.setText(pickup);
            }
            if (drop != null) {
                drop_location.setText(drop);
            }
            if (driver != null) {
                drivername.setText(driver);
            }
            if (mobile != null) {
                mobilenumber.setText(mobile);
            }
            if (payment_mode == null) {
                payment_mode = "";
            }
            if (ride_id != null) {

            } else {
                ride_id = "";
            }
            Log.i("ibrahim", "before");
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
            Log.i("ibrahim", "after");
        }
        setupData();
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogCreate(getString(R.string.ride_completion), getString(R.string.ride_complete_msg), "COMPLETED");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogCreate(getString(R.string.ride_cancellation), getString(R.string.ride_cancel_msg), "CANCELLED");
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogCreate(getString(R.string.ride_acceptance), getString(R.string.ride_accept_msg), "ACCEPTED");

            }
        });
        my_acc_d_f_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
    }

    public void setupData() {
        if (bundle != null) {
            if (!ride_status.equals("") && ride_status.equalsIgnoreCase("PENDING")) {
                cancel.setVisibility(View.VISIBLE);
                accept.setVisibility(View.VISIBLE);
            }
            if (ride_status != null && !ride_status.equals("") && ride_status.equalsIgnoreCase("CANCELLED")) {
                trackRide.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);
                approve.setVisibility(View.GONE);
                complete.setVisibility(View.GONE);
                payment_status_TV.setText(payment_status);
            }
            if (ride_status != null && !ride_status.equals("") && ride_status.equalsIgnoreCase("COMPLETED")) {
                trackRide.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);
                approve.setVisibility(View.GONE);
                complete.setVisibility(View.GONE);
                payment_status_TV.setText(payment_status);
            }
        }
        if (!ride_status.equals("") && ride_status.equalsIgnoreCase("ACCEPTED")) {
            isStarted();
            if (travel_status.equalsIgnoreCase("PENDING")) {
                approve.setVisibility(View.VISIBLE);
                approve.setText(getString(R.string.Start_Travel));
                trackRide.setVisibility(View.GONE);
                complete.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);
                approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (travel_status.equalsIgnoreCase("PENDING")) {
                            SendTravelStatus(rideJson.getRide_id(), "ACCEPTED", rideJson.getTravel_id(), "STARTED");
                        } else {
                            approvePaymet();
                        }
                    }
                });
            } else {
                if (payment_mode.equals("OFFLINE") && !payment_status.equals("PAID")) {
                    payment_status_TV.setText(R.string.coh_driver);
                    approve.setVisibility(View.VISIBLE);
                    approve.setText(getString(R.string.approve_payment_offline));
                    complete.setVisibility(View.GONE);
                    approve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            approvePaymet();
                        }
                    });
                } else {
                    approve.setVisibility(View.GONE);
                    payment_status_TV.setText(payment_status);
                    complete.setVisibility(View.VISIBLE);
                }

                trackRide.setVisibility(View.GONE);

                if (payment_status.equals("") && payment_mode.equals("")) {
                    payment_status_TV.setText(R.string.unpaid);
                    complete.setVisibility(View.GONE);
                    cancel.setVisibility(View.VISIBLE);
                }
                if (payment_mode.equals("OFFLINE") && payment_status.equals("PAID")) {
                    payment_status_TV.setText(R.string.payment_receive_from_customer);
                }
            }
        }

        trackRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMap();
                askCompactPermissions(permissions, new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        launchNavigation();
                    }

                    @Override
                    public void permissionDenied() {

                    }

                    @Override
                    public void permissionForeverDenied() {

                    }
                });
            }
        });

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

        mobilenumber_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ibrahim", "mobile call function");
                askCompactPermission(PermissionUtils.Manifest_CALL_PHONE, new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        if (mobile != null && !mobile.equals("")) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + mobile));
                            startActivity(callIntent);
                        }
                    }

                    @Override
                    public void permissionDenied() {

                    }

                    @Override
                    public void permissionForeverDenied() {

                    }
                });
            }
        });
    }

    private void approvePaymet() {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("travel_id", rideJson.getTravel_id());
        params.put("payment_status", "PAID");
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.APPROVE_PAYMENT, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                updateRideFirebase(travel_status, ride_status, "PAID", payment_mode);
                updateNotificationFirebase(getString(R.string.notification_5));
                updateTravelFirebase();
                updateTravelCounterFirebase();
                approve.setVisibility(View.GONE);
                payment_status_TV.setText("PAID");
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void SendStatus(String ride_id, final String status) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("status", status);
        params.put("by", "driver");
        params.put("driver_id", SessionManager.getUserId());
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.STATUS_CHANGE, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
                    Bundle bundle;
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        updateRideFirebase(travel_status, status, payment_status, payment_mode);
                        updateNotificationFirebase(status);
                        updateTravelCounterFirebase(status);
                        if (status.equals("ACCEPTED")) {
                            updateTravelCounterFirebase();
                        }
                    } else if (response.has("status") && response.getString("status").equalsIgnoreCase("faild")) {
                        Log.i("ibrahim", "all sets booked");
                        Log.i("ibrahim", response.toString());
                        Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();


                    } else {
                        String data = response.getJSONObject("data").toString();
                        Toast.makeText(getActivity(), getString(R.string.full_travel), Toast.LENGTH_LONG).show();
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

    public void updateRideFirebase(String travel_status, String ride_status, String payment_status, String payment_mode) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(rideJson.getRide_id());
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_status", ride_status);
        if(ride_status.contains("ACCEPTED")){
            rideObject.put("travel_status", "STARTED");
        }
        else{
            rideObject.put("travel_status", travel_status);
        }
        rideObject.put("payment_status", payment_status);
        rideObject.put("payment_mode", payment_mode);
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        databaseRef.setValue(rideObject);
    }

    public void updateNotificationFirebase(String notificationText) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(rideJson.getUser_id()).push();
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_id", rideJson.getRide_id());
        rideObject.put("text", getString(R.string.RideUpdated) + " " + notificationText);
        rideObject.put("readStatus", "0");
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        rideObject.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseRef.setValue(rideObject);
    }



    public void updateTravelFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());
        Map<String, Object> travelObject = new HashMap<>();
        travelObject.put("driver_id", rideJson.getDriver_id());

        databaseRef.updateChildren(travelObject).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Clients").child(rideJson.getUser_id());
//                Map<String, String> Client = new HashMap<>();
//                Client.put(rideJson.getUser_id(),rideJson.getUser_id());Map<String, String> Client = new HashMap<>();
////                Client.put(rideJson.getUser_id(),rideJson.getUser_id());
                databaseRef.setValue(rideJson.getUser_id());
            }
        });
    }


    public void updateTravelCounterFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseTravel fbTravel = dataSnapshot.getValue(firebaseTravel.class);
                if (fbTravel != null) {
                    Log.i("ibrahim", "fbTravel");
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("PAID");
                    databaseRef.setValue(fbTravel.Counters.PAID + 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void updateTravelCounterFirebase(String status) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseTravel fbTravel = dataSnapshot.getValue(firebaseTravel.class);
                if (fbTravel != null) {
                    if(status.contains("ACCEPTED")){
                        Log.i("ibrahim", "fbTravel");
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("ACCEPTED");
                        databaseRef.setValue(fbTravel.Counters.ACCEPTED + 1);
                    }
                    else if(status.contains("COMPLETED")){
                        Log.i("ibrahim", "fbTravel");
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("COMPLETED");
                        databaseRef.setValue(fbTravel.Counters.COMPLETED + 1);

                        DatabaseReference databaseRef1 = FirebaseDatabase.getInstance().getReference("Travels").child(rideJson.getTravel_id()).child("Counters").child("ACCEPTED");
                        databaseRef1.setValue(fbTravel.Counters.ACCEPTED -1 );
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void SendTravelStatus(String ride_id, final String status, final String travelId, final String travel_statusParam) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("status", status);
        params.put("travel_id", travelId);
        params.put("travel_status", travel_statusParam);
        params.put("driver_id", SessionManager.getUserId());
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.STATUS_CHANGE, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
                    Bundle bundle;
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        updateRideFirebase(travel_statusParam, status, payment_status, payment_mode);
                        updateNotificationFirebase(status);
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

    public void startService() {
        Intent myIntent = new Intent(getActivity(), LocationService.class);
        pendingIntent = PendingIntent.getService(getActivity(), 0, myIntent, 0);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 60); // first time
        long frequency = 60 * 1000; // in ms
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
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

    void isStarted() {
        Log.i("ibrahim", "isStarted");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tracking/" + rideJson.getRide_id());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    Tracking tracking = dataSnapshot.getValue(Tracking.class);
                    if (tracking.getStatus() != null) {
                        if (tracking.getStatus().equalsIgnoreCase("accepted")) {
                            trackRide.setText(getString(R.string.Pick_Customer));

                            trackRide.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        askCompactPermissions(permissions, new PermissionResult() {
                                            @Override
                                            public void permissionGranted() {
                                                gotoMap();
                                            }

                                            @Override
                                            public void permissionDenied() {

                                            }

                                            @Override
                                            public void permissionForeverDenied() {

                                            }
                                        });
                                    } else {
                                        gotoMap();

                                    }
                                }
                            });

                        } else if (tracking.getStatus().equalsIgnoreCase("started")) {
                            trackRide.setText(getString(R.string.track_ride));

                            trackRide.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gotoMap();
                                    askCompactPermissions(permissions, new PermissionResult() {
                                        @Override
                                        public void permissionGranted() {
                                            launchNavigation();

                                        }

                                        @Override
                                        public void permissionDenied() {

                                        }

                                        @Override
                                        public void permissionForeverDenied() {

                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    private void gotoMap() {
        Log.i("ibrahim", "inside gotoMap1");
        if (GPSEnable()) {
            Log.i("ibrahim", "inside gotoMap2");
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", rideJson);
            MapView mapView = new MapView();
            mapView.setArguments(bundle);
            ((HomeActivity) getActivity()).changeFragment(mapView, "MapView");
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public int getBackPriority() {
        return NORMAL_BACK_PRIORITY;
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseRides.removeEventListener(listener);
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
    public void onDirectionFailure(Throwable t) {

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
//                Log.i("ibrahim", fbTravel.toString());
//                Log.i("ibrahim_travel", fbTravel.driver_id);
//                Log.i("ibrahim_travel", fbTravel.Clients.toString());
                if (fbTravel != null) {
                    drawMap(fbTravel);
                }
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
//        GoogleDirection.withServerKey(getString(R.string.google_android_map_api_key))
//                .from(origin)
//                .to(destination)
//                .transportMode(TransportMode.DRIVING)
//                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (getActivity() != null) {
            if (direction.isOK()) {
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 2, Color.BLUE));
                myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title(getString(R.string.pick_up_location)).snippet(rideJson.getPickup_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title(getString(R.string.drop_up_location)).snippet(rideJson.getDrop_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
                calculateDistance(Double.valueOf(direction.getRouteList().get(0).getLegList().get(0).getDistance().getValue()) / 1000);
            } else {
//                distanceAlert(direction.getErrorMessage());
                //calculateFare.setVisibility(View.GONE);
//                dismiss();
            }
        }
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
                    myMap.clear();
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
                            if (fbTravel != null) {
                                drawMap(fbTravel);
                            }
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
//                    myMap.clear();
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

//    private void setCurrentLocation() {
//        if (!GPSEnable()) {
//            tunonGps();
//        } else {
//            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//                try {
//                    /*@SuppressLint("MissingPermission") Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
//                    placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
//                        @Override
//                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
//                            try {
//                                if (task.isSuccessful()) {
//                                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
//                                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                                        pickup = placeLikelihood.getPlace().freeze();
//                                        pickup_location.setText(placeLikelihood.getPlace().getAddress());
//                                        current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));
//
//                                    }
//                                    likelyPlaces.release();
//                                }
//                            } catch (Exception e) {
//
//                            }
//
//                        }
//                    });*/
//
//                    // Use fields to define the data types to return.
//                    List<Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);
//
//// Use the builder to create a FindCurrentPlaceRequest.
//                    FindCurrentPlaceRequest request =
//                            FindCurrentPlaceRequest.builder(placeFields).build();
//
//// Call findCurrentPlace and handle the response (first check that the user has granted permission).
//                    if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
//                        placeResponse.addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                FindCurrentPlaceResponse response = task.getResult();
//                                /*for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
//                                    Log.i(TAG, String.format("Place '%s' has likelihood: %f",
//                                            placeLikelihood.getPlace().getName(),
//                                            placeLikelihood.getLikelihood()));
//                                    pickup = placeLikelihood.getPlace();
//                                    pickup_location.setText(placeLikelihood.getPlace().getAddress());
//                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));
//
//                                }
//*/
//                                if (response != null && response.getPlaceLikelihoods() != null) {
//                                    PlaceLikelihood placeLikelihood = response.getPlaceLikelihoods().get(0);
//                                    pickup = placeLikelihood.getPlace();
//                                    Log.i("ibrahim", "gps");
//                                    Log.i("ibrahim", placeLikelihood.getPlace().getLatLng().toString());
//
//                                    pickup_location.setText(placeLikelihood.getPlace().getAddress());
//                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));
//
//                                }
//                            } else {
//                                Exception exception = task.getException();
//                                if (exception instanceof ApiException) {
//                                    ApiException apiException = (ApiException) exception;
//                                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
//                                }
//                            }
//                        });
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//        }
//    }
}