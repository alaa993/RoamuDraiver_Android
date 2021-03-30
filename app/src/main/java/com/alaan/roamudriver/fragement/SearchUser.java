package com.alaan.roamudriver.fragement;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.AddPostActivity;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.custom.CheckConnection;
import com.alaan.roamudriver.pojo.Pass;
import com.alaan.roamudriver.session.SessionManager;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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


import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.alaan.roamudriver.fragement.lang.setLocale;
import static com.loopj.android.http.AsyncHttpClient.log;
import static net.skoumal.fragmentback.BackFragment.NORMAL_BACK_PRIORITY;

public class SearchUser extends Fragment implements BackFragment {
    private static final String TAG = "user";
    View rootView;
    Place pickup;
    Place drop;
    Place point;

    Pass pass;
    Button search_for_users_btn, add_travel;
    TextView date_time_search;
    TextView pickup_location, drop_location;
    EditText mPickupPoint;
    private int PLACE_PICKER_REQUEST = 7896;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1234;
    private int POINT_PICKER_REQUEST = 12345;

    private String date_time_value;
    private CheckBox Checkbox;

    String date_time = "";
    String time_value = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    private String pickup_address;
    private String drop_address;
    String o = "";
    String d = "";



    private RelativeLayout footer;

    public SearchUser() {
    }

    public static SearchUser newInstance(String param1, String param2) {
        SearchUser fragment = new SearchUser();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.home));
        footer = (RelativeLayout) rootView.findViewById(R.id.search_box_rel);
        pickup_location = (TextView) rootView.findViewById(R.id.pickup_search_location);
        drop_location = (TextView) rootView.findViewById(R.id.search_drop_location);
        date_time_search = (TextView) rootView.findViewById(R.id.Time_date_search);


        search_for_users_btn = (Button) rootView.findViewById(R.id.search_for_users_btn);
        add_travel = (Button) rootView.findViewById(R.id.ride_add_btn);

        pickup_location.setOnClickListener(new View.OnClickListener() {
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
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            }
        });
        drop_location.setOnClickListener(new View.OnClickListener() {
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
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });
        search_for_users_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Fragment fragment = new fragment_searched_user();
                Intent intent = new Intent(getActivity(), Search_list_acticity.class);

                intent.putExtra("search_pich_location", (pickup_location.getText().toString()));
                intent.putExtra("search_drop_location", (drop_location.getText().toString()));

//                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.frame, fragment);
//                ft.commit();

                startActivity(intent);
            }
        });
        add_travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!CheckConnection.haveNetworkConnection(getActivity())) {
                    Toast.makeText(getActivity(), "network is not Available", Toast.LENGTH_LONG).show();

                } else {

                    if (pickup_location.getText().toString().matches("") || drop_location.getText().toString().matches("") || date_time_search.getText().toString().matches("")) {
                        Toast.makeText(SearchUser.this.getContext(), getString(R.string.Post_Empty), Toast.LENGTH_LONG).show();
                    } else {
                        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(SearchUser.this.getContext());
                        View mView = getLayoutInflater().inflate(R.layout.dialog_addtravel_layout, null);
                        final EditText mPassengers = (EditText) mView.findViewById(R.id.etPassengers);
                        final EditText mPrice = (EditText) mView.findViewById(R.id.etPrice);
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
                                if (!mPassengers.getText().toString().isEmpty()
                                        && !mPrice.getText().toString().isEmpty()
                                        && !mPickupPoint.getText().toString().isEmpty()) {
                                    dialog.dismiss();
                                    pickup_address = (String) pickup_location.getText();
                                    drop_address = (String) drop_location.getText();
                                    o = String.valueOf(pickup.getLatLng().latitude) + "," + String.valueOf(pickup.getLatLng().longitude);
                                    d = String.valueOf(drop.getLatLng().latitude) + "," + String.valueOf(drop.getLatLng().longitude);
                                    AddRide(SessionManager.getKEY(), pickup_address, drop_address, o, d,
                                            mPrice.getText().toString(), "0", mPassengers.getText().toString(),
                                            "", date_time_value, time_value, mPickupPoint.getText().toString());
                                } else {
                                    Toast.makeText(SearchUser.this.getContext(),
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
                }
            }
        });

        date_time_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                datePicker();
                //  Toast.makeText(getContext(), "date" + date_time_value, Toast.LENGTH_SHORT).show();


            }
        });

        return rootView;
    }

    public void SavePost(String pickup_address, String Drop_address, String date_time_value, String time_value, int travel_id) {
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
//                log.i("tag","success by ibrahim");
//                log.i("tag", UserName);
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
                databaseRef.setValue(userObject);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
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
//                log.i("tag","success by ibrahim");
//                log.i("tag", UserName);
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

    public void changeFragment(final Fragment fragment, final String fragmenttag) {
        try {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
            fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
            fragmentTransaction.commit();
        } catch (Exception e) {
        }
    }

    private void datePicker() {
        setLocale("en", getActivity());
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date_time = String.format("%04d-%02d-%02d", year, 1 + monthOfYear, dayOfMonth);
                        //date_time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        tiemPicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        date_time_value = date_time + " " + hourOfDay + ":" + minute;
                        time_value = hourOfDay + ":" + minute;
                        date_time_search.setText(date_time + " " + String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
            }
            if (resultCode == RESULT_CANCELED) {
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                pickup = Autocomplete.getPlaceFromIntent(data);
                pickup_location.setText(pickup.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.e(TAG, status.toString());
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
        } else if (requestCode == POINT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                point = Autocomplete.getPlaceFromIntent(data);
                mPickupPoint.setText(point.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void AddRide(String key, String pickup_address, String drop_address, String pickup_location, String drop_location, String amount,
                        String distance, String a_set, String u_set, String s_date, String s_time, String PickupPoint) {
        final RequestParams params = new RequestParams();
        params.put("driver_id", SessionManager.getUserId());
        params.put("pickup_address", pickup_address);
        params.put("drop_address", drop_address);
        params.put("pickup_location", pickup_location);
        params.put("drop_location", drop_location);
        params.put("pickup_point", PickupPoint);
        params.put("distance", distance);
        params.put("amount", amount);
        params.put("available_set", a_set);
        params.put("booked_set", u_set);
        params.put("travel_date", s_date);
        params.put("travel_time", s_time);
        params.put("smoked", "1");
        params.put("status", "0");

        Server.setHeader(key);
        Server.post("https://roamu.net/api/user/addTravel2/format/json", params, new JsonHttpResponseHandler() {
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
                        Toast.makeText(getActivity(), R.string.ride_has_been_requested, Toast.LENGTH_LONG).show();
                        ((HomeActivity) getActivity()).changeFragment(new SearchUser(), "fragment_search_user");
                        if (response.has("data")) {
                            JSONObject data = response.getJSONObject("data");
                            int travel_id = Integer.parseInt(data.getString("travel_id"));
                            addTravelToFireBase(travel_id);
                            if (Checkbox.isChecked()) {
                                SavePost(pickup_address, drop_address, date_time, time_value, travel_id);
                                ((HomeActivity) getActivity()).changeFragment(new SearchUser(), "fragment_search_user");
                            } else {
                            }
                        } else {
                        }
                    } else {
                        Toast.makeText(getActivity(), "tryAgain", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
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
//                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
    }

    public void addTravelToFireBase(int travel_id) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(String.valueOf(travel_id));
        Map<String, Object> travelObject = new HashMap<>();
        travelObject.put("driver_id", String.valueOf(SessionManager.getUserId()));
        databaseRef.setValue(travelObject);
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
