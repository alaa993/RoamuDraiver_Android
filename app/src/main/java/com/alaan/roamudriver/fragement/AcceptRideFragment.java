package com.alaan.roamudriver.fragement;

import android.app.Activity;
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
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.alaan.roamudriver.pojo.Pass;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.pojo.SearchForUser;
import com.alaan.roamudriver.pojo.Tracking;
import com.alaan.roamudriver.session.SessionManager;
import com.fxn.stash.Stash;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import org.json.JSONException;
import org.json.JSONObject;

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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.loopj.android.http.AsyncHttpClient.log;

public class AcceptRideFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback {
    AppCompatButton trackRide, complete, cancel, approve, accept;
    TextView title, drivername, txt_city, mobilenumber, pickup_location, drop_location, fare, payment_status, bag, txt_dateandtime;
    String request = "";
    String permissions[] = {PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    GoogleMap myMap;
    Pass pass;
    ImageView img_call;
    RatingBar ratingBar;
    PendingRequestPojo pass1;
    private String pickup_address;
    private String drop_address;
    private LatLng origin;
    private LatLng destination;
    Place s_drop, s_pic;
    Place point;
    com.google.android.gms.maps.MapView mapView;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    private View view;
    private String pickup = "";
    private String drop = "";
    private String driver = "";
    private String city_st = "";
    private String basefare = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private String mobile = "";
    private String ride_id = "";
    private String paymnt_status = "";
    private String paymnt_mode = "";
    LinearLayout linearChat;
    TableRow mobilenumber_row;
    private String user_id;
    PendingRequestPojo pojo;
    SearchForUser pojo1;
    GPSTracker gpsTracker;
    private CheckBox Checkbox;
    private EditText mPassengers;
    private EditText mPrice;
    private EditText mPickupPoint;
    boolean Checkbox_bool = false;
    private int POINT_PICKER_REQUEST = 12345;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.accepted_ride_fragmnet, container, false);
        //((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.passanger_info));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BindView(savedInstanceState);
        Stash.init(getContext());

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
                AlertDialogAddTravel();
            }
        });
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
                        SendStatusAccept(ride_id, status);
                    }
                })
                .setNegativeButton(getString(R.string.ccancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void AlertDialogAddTravel() {
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(AcceptRideFragment.this.getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialog_addtravel_layout, null);
        mPassengers = (EditText) mView.findViewById(R.id.etPassengers);
        mPrice = (EditText) mView.findViewById(R.id.etPrice);
        mPickupPoint = (EditText) mView.findViewById(R.id.etPickupPoint);
        Button mSubmit = (Button) mView.findViewById(R.id.btnSubmitDialog);
        Button mCancel = (Button) mView.findViewById(R.id.btnCancelDialog);
        Checkbox = (CheckBox) mView.findViewById(R.id.checkBox);
        //checkBox
        mBuilder.setView(mView);
        final android.app.AlertDialog dialog = mBuilder.create();
        dialog.show();
        mPickupPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Places.initialize(getActivity(), getString(R.string.google_android_map_api_key));
                List<com.google.android.libraries.places.api.model.Place.Field> fields =
                        Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID,
                                com.google.android.libraries.places.api.model.Place.Field.NAME,
                                com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                                com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getActivity());
                startActivityForResult(intent, POINT_PICKER_REQUEST);
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPassengers.getText().toString().isEmpty() && !mPrice.getText().toString().isEmpty() && !mPickupPoint.getText().toString().isEmpty()) {
                    if (Integer.valueOf(mPassengers.getText().toString()) < Integer.valueOf(pojo.getbooked_set())) {
                        Toast.makeText(AcceptRideFragment.this.getContext(),
                                getString(R.string.ConfirmRideVC_AlertDetail),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        SendStatusAccept(ride_id, "WAITED");
                    }
                } else {
                    Toast.makeText(AcceptRideFragment.this.getContext(),
                            getString(R.string.Post_Empty),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == POINT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                point = Autocomplete.getPlaceFromIntent(data);
                mPickupPoint.setText(point.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void BindView(Bundle savedInstanceState) {
        gpsTracker = new GPSTracker(getActivity());
        setData();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.mapview);
        linearChat = (LinearLayout) view.findViewById(R.id.linear_chat);
        accept = (AppCompatButton) view.findViewById(R.id.btn_accept);
        complete = (AppCompatButton) view.findViewById(R.id.btn_complete);
        approve = (AppCompatButton) view.findViewById(R.id.btn_approve);
        cancel = (AppCompatButton) view.findViewById(R.id.btn_cancel);
        img_call = (ImageView) view.findViewById(R.id.img_call);
        trackRide = (AppCompatButton) view.findViewById(R.id.btn_trackride);
        title = (TextView) view.findViewById(R.id.title);
        ratingBar = (RatingBar) view.findViewById(R.id.rating_val);
        drivername = (TextView) view.findViewById(R.id.txt_drivername);
        txt_city = (TextView) view.findViewById(R.id.txt_city);
        mobilenumber = (TextView) view.findViewById(R.id.txt_mobilenumber);
        payment_status = (TextView) view.findViewById(R.id.txt_paymentstatus);
        pickup_location = (TextView) view.findViewById(R.id.txt_pickuplocation);
        txt_dateandtime = (TextView) view.findViewById(R.id.txt_dateandtime);
        drop_location = (TextView) view.findViewById(R.id.txt_droplocation);
        fare = (TextView) view.findViewById(R.id.txt_basefare);
        bag = (TextView) view.findViewById(R.id.bagnumber);
        mobilenumber_row = view.findViewById(R.id.mobilenumber_row);
        pickup_location.setSelected(true);
        drop_location.setSelected(true);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            pojo = (PendingRequestPojo) bundle.getSerializable("data");
            title.setText(getString(R.string.taxi));
            ratingBar.setRating(3);
            pickup = pojo.getPickup_address();
            drop = pojo.getDrop_address();
            driver = pojo.getUser_name();
            city_st = pojo.getCity();
            basefare = pojo.getAmount();
            ride_id = pojo.getRide_id();
            user_id = pojo.getUser_id();
            mobile = pojo.getUser_mobile();
            log.e("booked", pojo.getUser_mobile());
            bag.setText(pojo.getbooked_set());
            paymnt_mode = pojo.getRide_smoked();
            txt_dateandtime.setText(pojo.getTime() + " " + pojo.getDate());
            payment_status.setText(pojo.getRide_smoked());

            if (pickup != null) {
                pickup_location.setText(pickup);
            }
            if (drop != null) {
                drop_location.setText(drop);
            }
            if (driver != null) {
                drivername.setText(driver);
            }
            if (city_st != null) {
                txt_city.setText(city_st);
            }
            if (fare != null) {
                // commented by ibrahim 8-2-2021 because the user can't decide the base fare before the driver
                fare.setText(basefare);
            }
            if (mobile != null) {
                mobilenumber.setText(mobile);
            }
            if (paymnt_mode == null) {
                paymnt_mode = pojo.getbooked_set();
            }
            if (ride_id != null) {
            } else {
                ride_id = "";
            }
            request = pojo.getStatus();
            if (!request.equals("") && request.equalsIgnoreCase("PENDING")) {
                cancel.setVisibility(View.VISIBLE);
                accept.setVisibility(View.VISIBLE);
            }
            if (!request.equals("") && request.equalsIgnoreCase("REQUESTED")) {
                cancel.setVisibility(View.VISIBLE);
                accept.setVisibility(View.VISIBLE);
            }
            if (request != null && !request.equals("") && request.equalsIgnoreCase("CANCELLED")) {
                trackRide.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);
                approve.setVisibility(View.GONE);
                complete.setVisibility(View.GONE);
            }
            if (request != null && !request.equals("") && request.equalsIgnoreCase("COMPLETED")) {
                trackRide.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);
                approve.setVisibility(View.GONE);
                complete.setVisibility(View.GONE);
            }
        }

        if (!request.equals("") && request.equalsIgnoreCase("ACCEPTED")) {
            isStarted();
            if (paymnt_mode.equals("OFFLINE") && !paymnt_status.equals("PAID")) {
                approve.setVisibility(View.VISIBLE);
                complete.setVisibility(View.GONE);
                approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        approvePaymet();
                    }
                });
            } else {
                approve.setVisibility(View.GONE);
                complete.setVisibility(View.VISIBLE);
            }
            trackRide.setVisibility(View.VISIBLE);
            if (pojo.getPayment_status().equals("") && pojo.getPayment_mode().equals("")) {
                complete.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
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
                approve.setVisibility(View.GONE);
                payment_status.setText("PAID");
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
                        if (status.equalsIgnoreCase("COMPLETED")) {
                            bundle = new Bundle();
                            bundle.putString("status", "COMPLETED");
                            acceptedRequestFragment.setArguments(bundle);
                            ((HomeActivity) getActivity()).changeFragment(acceptedRequestFragment, getString(R.string.requests));
                            Toast.makeText(getActivity(), getString(R.string.ride_reuest_completed), Toast.LENGTH_LONG).show();
                        } else if (status.equalsIgnoreCase("ACCEPTED")) {
                            startService();
                            bundle = new Bundle();
                            bundle.putString("status", "ACCEPTED");
                            acceptedRequestFragment.setArguments(bundle);
                            ((HomeActivity) getActivity()).setPojo(pojo);
                            ((HomeActivity) getActivity()).setStatus(pojo, "accepted", true);
                            ((HomeActivity) getActivity()).changeFragment(acceptedRequestFragment, getString(R.string.requests));
                            Toast.makeText(getActivity(), getString(R.string.ride_reuest_accepted), Toast.LENGTH_LONG).show();
                        } else {
                            bundle = new Bundle();
                            bundle.putString("status", "CANCELLED");
                            acceptedRequestFragment.setArguments(bundle);
                            ((HomeActivity) getActivity()).changeFragment(acceptedRequestFragment, getString(R.string.requests));
                            Toast.makeText(getActivity(), getString(R.string.ride_reuest_cancelled), Toast.LENGTH_LONG).show();
                        }
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

    public void SendStatusAccept(String ride_id, final String status) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("status", status);
        params.put("driver_id", SessionManager.getUserId());
        params.put("pickup_address", pickup_address);
        params.put("drop_address", drop_address);
        params.put("pickup_location", s_pic.getLatLng().latitude + "," + s_pic.getLatLng().longitude);
        params.put("drop_location", s_drop.getLatLng().latitude + "," + s_drop.getLatLng().latitude);
        params.put("distance", "0");

        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        String Url = "";
        if (status == "WAITED") {
            Log.i("ibrahim_waited", "1");
            Log.i("ibrahim_waited", mPrice.getText().toString());
            Log.i("ibrahim_waited", mPrice.getText().toString());
            Log.i("ibrahim_waited", mPrice.getText().toString());
            Log.i("ibrahim_waited", pojo.getbooked_set());
            Log.i("ibrahim_waited", pojo.getDate());
            Log.i("ibrahim_waited", pojo.getTime());
            params.put("amount", mPrice.getText().toString());
            params.put("available_set", mPassengers.getText().toString());
            params.put("pickup_point", mPickupPoint.getText().toString());
            params.put("booked_set", pojo.getbooked_set());
            params.put("travel_date", pojo.getDate());
            params.put("travel_time", pojo.getTime());
            params.put("ride_status", status);
            params.put("status", "0");
            params.put("smoked", "0");
            Url = Server.CONFIRM_REQUST;
        } else
            Url = Server.STATUS_CHANGE;
        Server.post(Url, params, new JsonHttpResponseHandler() {
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
                        if (status.equalsIgnoreCase("COMPLETED")) {
//                            bundle = new Bundle();
//                            bundle.putString("status", "COMPLETED");
//                            acceptedRequestFragment.setArguments(bundle);
//                            ((HomeActivity) getActivity()).changeFragment(acceptedRequestFragment, getString(R.string.requests));
                            Toast.makeText(getActivity(), getString(R.string.ride_reuest_completed), Toast.LENGTH_LONG).show();
                        } else if (status.equalsIgnoreCase("ACCEPTED")) {
                            startService();
//                            bundle = new Bundle();
//                            bundle.putString("status", "ACCEPTED");
//                            acceptedRequestFragment.setArguments(bundle);
//                            ((HomeActivity) getActivity()).setPojo(pojo);
//                            ((HomeActivity) getActivity()).setStatus(pojo, "accepted", true);
//                            ((HomeActivity) getActivity()).changeFragment(acceptedRequestFragment, getString(R.string.requests));
//                            ((HomeActivity) getActivity()).changeFragment(new SearchUser(), "fragment_search_user");
                            Toast.makeText(getActivity(), getString(R.string.ride_reuest_accepted), Toast.LENGTH_LONG).show();
                        } else if (status.equalsIgnoreCase("WAITED")) {
                            startService();
                            if (response.has("data")) {
                                JSONObject data = response.getJSONObject("data");
                                int travel_id = Integer.parseInt(data.getString("travel_id"));
                                Log.i("ibrahim travel_id", String.valueOf(travel_id));
                                if (Checkbox.isChecked()) {
                                    Log.i("ibrahim check box", "is checked");
                                    SavePost(pickup_address, drop_address, pojo.getDate(), pojo.getTime(), travel_id, status);
                                } else {
                                    Log.i("ibrahim check box", "is not checked");
                                }
                            } else {
                                Log.i("ibrahim_response", "no travel id");
                            }
//                            ((HomeActivity) getActivity()).changeFragment(new SearchUser(), "fragment_search_user");
//                            bundle = new Bundle();
//                            bundle.putString("status", "ACCEPTED");
//                            acceptedRequestFragment.setArguments(bundle);
//                            ((HomeActivity) getActivity()).setPojo(pojo);
//                            ((HomeActivity) getActivity()).setStatus(pojo, "accepted", true);
//                            ((HomeActivity) getActivity()).changeFragment(acceptedRequestFragment, getString(R.string.requests));
//                            Toast.makeText(getActivity(), getString(R.string.ride_reuest_accepted), Toast.LENGTH_LONG).show();
                        } else {
//                            bundle = new Bundle();
//                            bundle.putString("status", "CANCELLED");
//                            acceptedRequestFragment.setArguments(bundle);
//                            ((HomeActivity) getActivity()).changeFragment(acceptedRequestFragment, getString(R.string.requests));
//                            Toast.makeText(getActivity(), getString(R.string.ride_reuest_cancelled), Toast.LENGTH_LONG).show();
                        }
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

    public void SavePost(String pickup_address, String Drop_address, String date_time_value, String time_value, int travel_id, String status) {
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
                // Firebase code here
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").push();
                Map<String, Object> author = new HashMap<>();
                author.put("uid", user.getUid());
                author.put("username", UserName);
                author.put("photoURL", photoURL);
                Map<String, Object> userObject = new HashMap<>();
                userObject.put("author", author);
                userObject.put("text", text);
                //type = 0 => driver
                //type = 1 => user
                userObject.put("type", "0");
                userObject.put("privacy", "1");
                userObject.put("travel_id", travel_id);
                userObject.put("timestamp", ServerValue.TIMESTAMP);
//                databaseRef.setValue(userObject);
                databaseRef.setValue(userObject, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.i("ibrahim", "Data could not be saved. " + databaseError.getMessage());
                        } else {
//                            Log.i("ibrahim","Data saved successfully." + databaseError.getMessage());
                            updateRideFirebase("", status, pojo.getPayment_status(), pojo.getPayment_mode());
                            updateNotificationFirebase();
                        }
                    }
                });
//                getFragmentManager().popBackStack();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void updateRideFirebase(String travel_status, String ride_status, String payment_status, String payment_mode) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(pojo.getRide_id());
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_status", ride_status);
        rideObject.put("travel_status", travel_status);
        rideObject.put("payment_status", payment_status);
        rideObject.put("payment_mode", payment_mode);
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
//        databaseRef.setValue(rideObject);
        databaseRef.setValue(rideObject, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.i("ibrahim", "Data could not be saved. " + databaseError.getMessage());
                } else {
//                    Log.i("ibrahim","Data saved successfully." + databaseError.getMessage());
                    updateNotificationFirebase();
                }
            }
        });
    }

    public void updateNotificationFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(pojo.getUser_id()).push();
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_id", pojo.getRide_id());
        rideObject.put("text", getString(R.string.Notification_Request));
        rideObject.put("readStatus", "0");
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        rideObject.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//        databaseRef.setValue(rideObject);
        databaseRef.setValue(rideObject, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.i("ibrahim", "Data could not be saved. " + databaseError.getMessage());
                } else {
//                    changeFragment(new SearchUser(), "fragment_search_user");
//                    Log.i("ibrahim","Data saved successfully." + databaseError.getMessage());
                    startActivity(new Intent(getContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            }
        });
    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {
        try {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
            fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
            fragmentTransaction.commit();
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

    void isStarted() {
        Log.i("ibrahim", "isStarted");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tracking/" + pojo.getRide_id());
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
                String[] latlong = pojo.getpickup_location().split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                String[] latlong1 = pojo.getdrop_location().split(",");
                double latitude1 = Double.parseDouble(latlong1[0]);
                double longitude1 = Double.parseDouble(latlong1[1]);
// Create a NavigationViewOptions object to package everything together
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

    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (getActivity() != null) {
            if (direction.isOK()) {
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
                myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("Pickup Location").snippet(pickup_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Drop Location").snippet(drop_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
            } else {
                // do nothing
            }
        }
    }

    public void onDirectionFailure(Throwable t) {
        // show error message when its failure
    }

    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        setData();
        //loop for request dricestion
        new Handler().postDelayed(this::requestDirection, 2000);
    }

    public void requestDirection() {
        setData();
        Context context = getContext();
        if (context != null) {
            // key api for google
            GoogleDirection.withServerKey(getString(R.string.google_android_map_api_key))
                    .from(origin)
                    .to(destination)
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);
        }
    }

    private void gotoMap() {
        if (GPSEnable()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", pojo);
            MapView mapView = new MapView();
            mapView.setArguments(bundle);
            ((HomeActivity) getActivity()).changeFragment(mapView, "MapView");
        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void setData() {
        Bundle bundle = getArguments();
        pass1 = new PendingRequestPojo();
        if (bundle != null) {
            pass1 = (PendingRequestPojo) bundle.getSerializable("data");
            if (pass1 != null) {
                s_pic = new Place() {
                    @Nullable
                    @Override
                    public String getAddress() {
                        return pass1.getPickup_address();
                    }
                    @Nullable
                    @Override
                    public List<String> getAttributions() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public String getId() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public LatLng getLatLng() {
                        String[] parts = pass1.getpickup_location().split(",");
                        LatLng location = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                        return location;
                    }
                    @Nullable
                    @Override
                    public String getName() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public OpeningHours getOpeningHours() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public String getPhoneNumber() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public List<PhotoMetadata> getPhotoMetadatas() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public PlusCode getPlusCode() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public Integer getPriceLevel() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public Double getRating() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public List<Type> getTypes() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public Integer getUserRatingsTotal() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public LatLngBounds getViewport() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public Uri getWebsiteUri() {
                        return null;
                    }
                    @Override
                    public int describeContents() {
                        return 0;
                    }
                    @Override
                    public void writeToParcel(Parcel dest, int flags) {
                    }
                };
                s_drop = new Place() {
                    @Nullable
                    @Override
                    public String getAddress() {
                        return pass1.getDrop_address();
                    }
                    @Nullable
                    @Override
                    public List<String> getAttributions() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public String getId() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public LatLng getLatLng() {
                        String[] parts = pass1.getdrop_location().split(",");
                        LatLng location = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                        return location;
                    }
                    @Nullable
                    @Override
                    public String getName() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public OpeningHours getOpeningHours() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public String getPhoneNumber() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public List<PhotoMetadata> getPhotoMetadatas() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public PlusCode getPlusCode() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public Integer getPriceLevel() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public Double getRating() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public List<Type> getTypes() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public Integer getUserRatingsTotal() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public LatLngBounds getViewport() {
                        return null;
                    }
                    @Nullable
                    @Override
                    public Uri getWebsiteUri() {
                        return null;
                    }
                    @Override
                    public int describeContents() {
                        return 0;
                    }
                    @Override
                    public void writeToParcel(Parcel dest, int flags) {
                    }
                };
                origin = s_pic.getLatLng();
                destination = s_drop.getLatLng();
                pickup_address = s_pic.getAddress().toString();
                drop_address = s_drop.getAddress().toString();
            }
        }
    }
}