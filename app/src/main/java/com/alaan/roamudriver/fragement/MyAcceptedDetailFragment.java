package com.alaan.roamudriver.fragement;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.custom.GPSTracker;
import com.alaan.roamudriver.custom.LocationService;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.pojo.Tracking;
import com.alaan.roamudriver.pojo.firebaseRide;
import com.alaan.roamudriver.session.SessionManager;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAcceptedDetailFragment extends FragmentManagePermission implements BackFragment {

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

    private SwipeRefreshLayout swipeRefreshLayout;
    TableRow mobilenumber_row;
    GPSTracker gpsTracker;

    DatabaseReference databaseRides;
    Bundle bundle;
    PendingRequestPojo rideJson;
    private String travel_status;
    private String ride_status;
    private String payment_status;
    private String payment_mode;

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
        }
        view = inflater.inflate(R.layout.fragment_my_accepted_detail, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.passanger_info));
        BindView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        databaseRides.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot rideSnapshot : dataSnapshot.getChildren()) {
                    firebaseRide fbRide = dataSnapshot.getValue(firebaseRide.class);
                    Log.i("ibrahim ride", "----------");
                    travel_status = fbRide.travel_status;
                    ride_status = fbRide.ride_status;
                    payment_status = fbRide.payment_status;
                    payment_mode = fbRide.payment_mode;
                    setupData();
                }
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

    public void BindView() {
        gpsTracker = new GPSTracker(getActivity());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.MyADFswipe_refresh);
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

                trackRide.setVisibility(View.VISIBLE);

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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mobilenumber_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        params.put("payment_status", "PAID");
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.APPROVE_PAYMENT, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                updateRideFirebase(travel_status, ride_status, "PAID", payment_mode);
                updateNotificationFirebase();
                approve.setVisibility(View.GONE);
                payment_status_TV.setText("PAID");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void SendStatus(String ride_id, final String status) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("status", status);
        params.put("driver_id", SessionManager.getUserId());
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.STATUS_CHANGE, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
                    Bundle bundle;
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        updateRideFirebase(travel_status, status, payment_status, payment_mode);
                        updateNotificationFirebase();
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
                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    public void updateRideFirebase(String travel_status, String ride_status, String payment_status, String payment_mode) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(rideJson.getRide_id());
        Map<String, Object> rideObject = new HashMap<>();

        rideObject.put("ride_status", ride_status);
        rideObject.put("travel_status", travel_status);
        rideObject.put("payment_status", payment_status);
        rideObject.put("payment_mode", payment_mode);
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        databaseRef.setValue(rideObject);
    }

    public void updateNotificationFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(rideJson.getUser_id()).push();
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_id", rideJson.getRide_id());
        rideObject.put("text", "Ride Updated");
        rideObject.put("readStatus", "0");
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        rideObject.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseRef.setValue(rideObject);
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
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
                    Bundle bundle;
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        updateRideFirebase(travel_statusParam, status, payment_status, payment_mode);
                        updateNotificationFirebase();
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
                swipeRefreshLayout.setRefreshing(false);
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
        Log.i("ibrahim","isStarted");
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
        if (GPSEnable()) {
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
}