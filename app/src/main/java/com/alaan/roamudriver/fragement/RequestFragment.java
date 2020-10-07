package com.alaan.roamudriver.fragement;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.custom.CheckConnection;
import com.alaan.roamudriver.fragement.HomeFragment;
import com.alaan.roamudriver.pojo.Pass;
import com.alaan.roamudriver.session.SessionManager;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

;import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by android on 14/3/17.
 */

public class RequestFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback {
    View view;
    AppCompatButton confirm, cancel, setTime, setDate;
    ElegantNumberButton btn_book, btn_ava;
    TextView pickup_location, drop_location;
    Double finalfare;
    MapView mapView;
    private Double fare;
    GoogleMap myMap;
    AlertDialog alert;
    private LatLng origin;
    private LatLng destination;
    private String networkAvailable;
    private String tryAgain;
    private String directionRequest;
    TextView textView1, textView2, textView3, textView4, textView5, txt_name, txt_number, title, txt_vehiclename;
    EditText txt_fare;
    String driver_id;
    private String user_id;
    private String pickup_address;
    private String drop_address;
    String distance;
    private String drivername = "";
    SwipeRefreshLayout swipeRefreshLayout;
    Pass pass;
    TextView calculateFare;
    Snackbar snackbar;
    EditText av_set, un_set;
    String val_timel, val_date;
    Calendar date;
    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;

    int mHour;
    int mMinute;

    String o = "";
    String d = "";
    Place pickup;
    Place drop;

    private int PLACE_PICKER_REQUEST = 7896;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1234;



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        networkAvailable = "network";
        tryAgain = "try_again";
        directionRequest = "direction_request";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ride, container, false);

        if (!CheckConnection.haveNetworkConnection(getActivity())) {
            Toast.makeText(getActivity(), networkAvailable, Toast.LENGTH_LONG).show();
        }
        bindView(savedInstanceState);

     /*   setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                val_timel =hourOfDay + ":" + minute;
                            }
                        },hour, minute, false);
                timePickerDialog.show();
            }
        });

      */
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckConnection.haveNetworkConnection(getActivity())) {
                    Toast.makeText(getActivity(), networkAvailable, Toast.LENGTH_LONG).show();

                } else {
                    android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(RequestFragment.this.getContext());
                    View mView = getLayoutInflater().inflate(R.layout.dialog_addtravel_layout, null);
                    final EditText mPassengers = (EditText) mView.findViewById(R.id.etPassengers);
                    final EditText mPrice = (EditText) mView.findViewById(R.id.etPrice);
                    Button mSubmit = (Button) mView.findViewById(R.id.btnSubmitDialog);
                    Button mCancel = (Button) mView.findViewById(R.id.btnCancelDialog);
                    mBuilder.setView(mView);
                    final android.app.AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    mSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!mPassengers.getText().toString().isEmpty() && !mPrice.getText().toString().isEmpty()) {

                                dialog.dismiss();
                                log.i("tag", "success by ibrahim");
                                log.i("tag", mPassengers.getText().toString());
                                log.i("tag", mPrice.getText().toString());
                                //    origin.latitude + "," + origin.longitude;
                                // destination.latitude + "," + destination.longitude;

                                pickup_address = (String) pickup_location.getText();
                                drop_address = (String) drop_location.getText();

                                o = String.valueOf(pickup.getLatLng().latitude) + "," + String.valueOf(pickup.getLatLng().longitude);
                                d = String.valueOf(drop.getLatLng().latitude) + "," + String.valueOf(drop.getLatLng().longitude);

//                                AddRide(SessionManager.getKEY(), pickup_address, drop_address, o, d,txt_fare.getText().toString(), distance,btn_ava.getNumber(),btn_book.getNumber(),val_timel,val_date);
//                                AddRide(String key, String pickup_adress, String drop_address, String pickup_location, String drop_locatoin, String amount, String distance, String a_set, String u_set, String s_time, String s_date)
                                AddRide(SessionManager.getKEY(), pickup_address, drop_address, o, d, mPrice.getText().toString(), distance, mPassengers.getText().toString(), btn_book.getNumber(), val_date, val_date);
                                Toast.makeText(getActivity(), "do tasked", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(RequestFragment.this.getContext(),
                                        "Failed",
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
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));

            }
        });
        return view;
    }

    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

//                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        date_time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        //*************Call Time Picker Here ********************
                        tiemPicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        val_date = date_time + " " + hourOfDay + ":" + minute + ":00";
//                        Toast.makeText(getContext(), "show time" + val_date, Toast.LENGTH_SHORT).show();
                        log.i("tag", "success by ibrahim");
                        log.i("tag", val_date);
                        setDate.setText(val_date);

                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
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

    public void bindView(Bundle savedInstanceState) {
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.home));
        //((HomeActivity) getActivity()).toolbar.setTitle(getString(R.string.request_ride));
        mapView = (MapView) view.findViewById(R.id.mapview);
        calculateFare = (TextView) view.findViewById(R.id.txt_calfare);
        confirm = (AppCompatButton) view.findViewById(R.id.btn_confirm);
        cancel = (AppCompatButton) view.findViewById(R.id.btn_cancel);
        pickup_location = (TextView) view.findViewById(R.id.pickup_search_location);
        drop_location = (TextView) view.findViewById(R.id.search_drop_location);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        textView3 = (TextView) view.findViewById(R.id.textView3);
        textView4 = (TextView) view.findViewById(R.id.textView4);
        textView5 = (TextView) view.findViewById(R.id.textView5);
        btn_book = (ElegantNumberButton) view.findViewById(R.id.elegend_num_booked);
        btn_ava = (ElegantNumberButton) view.findViewById(R.id.elegend_num_avaliable);
        txt_name = (TextView) view.findViewById(R.id.txt_name);
        txt_number = (TextView) view.findViewById(R.id.txt_number);
        txt_fare = (EditText) view.findViewById(R.id.txt_fare);
        txt_vehiclename = (TextView) view.findViewById(R.id.txt_vehiclename);
        title = (TextView) view.findViewById(R.id.title);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        un_set = (EditText) view.findViewById(R.id.unv_set);
        av_set = (EditText) view.findViewById(R.id.ava_set);
        setTime = (AppCompatButton) view.findViewById(R.id.btnPickTime);
        setDate = (AppCompatButton) view.findViewById(R.id.btnPickDate);

        Typeface book = Typeface.createFromAsset(getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        title.setTypeface(book);
        cancel.setTypeface(book);
        confirm.setTypeface(book);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        setData();
        overrideFonts(getActivity(), view);
        user_id = SessionManager.getUserId();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Places.initialize(getActivity(), getString(R.string.google_android_map_api_key));
                List<Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getActivity());
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            }
        });
        drop_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Places.initialize(getActivity(), getString(R.string.google_android_map_api_key));
                List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getActivity());
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

    }

    private void setData() {
        Bundle bundle = getArguments();
        pass = new Pass();
        if (bundle != null) {
            pass = (Pass) bundle.getSerializable("data");
            if (pass != null) {
                if (pass.getFromPlace().getLatLng() != null && pass.getToPlace().getLatLng() != null) {
                    // origin = pass.getFromPlace().getLatLng();
                    //destination = pass.getToPlace().getLatLng();
                } else {
                    Toast.makeText(getContext(), "Enter Location First", Toast.LENGTH_SHORT).show();
                }
                driver_id = pass.getDriverId();
                fare = Double.valueOf("2");
                drivername = pass.getDriverName();

                if (drivername != null) {
                    txt_name.setText(drivername);
                }
                pickup_location.setText(pickup_address);
                drop_location.setText(drop_address);
                txt_vehiclename.setText(pass.getVehicleName() + "");
            }
        }
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (getActivity() != null) {
            if (direction.isOK()) {
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
                myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("Pickup Location").snippet(pickup_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Drop Location").snippet(drop_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));

                calculateDistance(Double.valueOf(direction.getRouteList().get(0).getLegList().get(0).getDistance().getValue()) / 1000);

            } else {
                distanceAlert(direction.getErrorMessage());
                //calculateFare.setVisibility(View.GONE);
                dismiss();
            }


        }


    }

    @Override
    public void onDirectionFailure(Throwable t) {
        distanceAlert(t.getMessage() + "\n" + t.getLocalizedMessage() + "\n");
        //  calculateFare.setVisibility(View.GONE);
        dismiss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        setData();
        //your code here
        new Handler().postDelayed(this::requestDirection, 2000);


    }

    public void requestDirection() {


        //snackbar = Snackbar.make(view, getString(R.string.fare_calculating), Snackbar.LENGTH_INDEFINITE);
        //snackbar.show();
       /* Context context = getContext();
        if (context != null) {
            GoogleDirection.withServerKey(getString(R.string.google_android_map_api_key))
                    .from(origin)
                    .to(destination)
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);

            confirm.setEnabled(false);
        }*/
    }

    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Medium.otf"));
            }
        } catch (Exception e) {
        }
    }

    public void AddRide(String key, String pickup_adress, String drop_address, String pickup_location, String drop_locatoin, String amount, String distance, String a_set, String u_set, String s_time, String s_date) {
        final RequestParams params = new RequestParams();
        params.put("driver_id", driver_id);
        params.put("driver_id", user_id);
        params.put("pickup_address", pickup_adress);
        params.put("drop_address", drop_address);
        params.put("pickup_location", pickup_location);
        params.put("drop_location", drop_locatoin);
        params.put("distance", distance);
        params.put("amount", amount);
        params.put("avalable_set", a_set);
        params.put("booked_set", u_set);
        //commited by ibrahim
        //params.put("travel_time",s_time);
//        params.put("travel_date", "2023-10-18 15:18:00");
        params.put("travel_date", s_time);
        params.put("somked", "1");
        params.put("status", "0");

        Server.setHeader(key);
        Server.post("https://roamu.net/api/user/addTravel2/format/json", params, new JsonHttpResponseHandler() {
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
                        Toast.makeText(getActivity(), "ride_has_been_requested", Toast.LENGTH_LONG).show();
                        ((HomeActivity) getActivity()).changeFragment(new SearchUser(), "fragment_search_user");
                    } else {
                        Toast.makeText(getActivity(), tryAgain, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), tryAgain, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                    swipeRefreshLayout.setRefreshing(false);

                }

            }
        });
    }

    public void calculateDistance(Double aDouble) {

        distance = String.valueOf(aDouble);
        confirm.setEnabled(true);

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
                    dismiss();

                } catch (Exception e) {

                }

                // txt_fare.setText(finalfare + " " + SessionManager.getUnit());
                //  txt_fare.setText(finalfare + " $ ");

            } else {
                //txt_fare.setText(SessionManager.getUnit());
            }
        }


    }


    public void distanceAlert(String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("INVALID_DISTANCE");
        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        alertDialog.setIcon(drawable);


        alertDialog.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.cancel();
            }
        });
        alert = alertDialog.create();
        alert.show();
    }

    private void dismiss() {
        if (snackbar != null) {
            snackbar.dismiss();

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                pickup = Autocomplete.getPlaceFromIntent(data);

                pickup_location.setText(pickup.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);

                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                drop = Autocomplete.getPlaceFromIntent(data);
                drop_location.setText(drop.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();

            }
        }

    }


}
