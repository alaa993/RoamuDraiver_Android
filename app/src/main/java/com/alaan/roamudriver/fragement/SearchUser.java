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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import static com.loopj.android.http.AsyncHttpClient.log;
import static net.skoumal.fragmentback.BackFragment.NORMAL_BACK_PRIORITY;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchUser extends Fragment implements BackFragment {
    private static final String TAG = "user";
    View rootView;
    Place pickup;
    Place drop;
    Pass pass;
    Button search_for_users_btn, add_travel;
    TextView txt_vehicleinfo, rate, txt_info, txt_cost, txt_color, txt_address, request_ride, txt_date, txt_smoke, txt_fee, passanger_search, bag_search, smoke_search, date_time_search;

    TextView pickup_location, drop_location;
    private int PLACE_PICKER_REQUEST = 7896;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1234;
    private String driver_id, passanger_value, bag_value, smoke_value, date_time_value;
    Calendar date;
    String date_time = "";
    String time_value="";
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
        // Required empty public constructor
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
        smoke_search = (TextView) rootView.findViewById(R.id.somoke_search);
        date_time_search = (TextView) rootView.findViewById(R.id.Time_date_search);
        passanger_search = (TextView) rootView.findViewById(R.id.passenger_search);

        search_for_users_btn = (Button) rootView.findViewById(R.id.search_for_users_btn);
        add_travel = (Button) rootView.findViewById(R.id.ride_add_btn);

        pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Places.initialize(getActivity(), getString(R.string.google_android_map_api_key));


                List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Start the autocomplete intent.
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
        search_for_users_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new fragment_searched_user();
                Intent intent = new Intent(getActivity(), Search_list_acticity.class);

                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.frame, fragment);
                ft.commit();
                Bundle args = new Bundle();
                intent.putExtra("search_pich_location", (pickup_location.getText().toString()));
                intent.putExtra("search_drop_location", (drop_location.getText().toString()));
                if (smoke_value != null) {
                    intent.putExtra("smoke_value", String.valueOf(smoke_value));
                }
                if (smoke_value != null) {
                    intent.putExtra("date_time_value", String.valueOf(date_time_value));
                }
                if (passanger_value != null) {
                    intent.putExtra("passanger_value", String.valueOf(passanger_value));
                }
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
                        Button mSubmit = (Button) mView.findViewById(R.id.btnSubmitDialog);
                        Button mCancel = (Button) mView.findViewById(R.id.btnCancelDialog);
                        CheckBox Checkbox = (CheckBox)mView.findViewById(R.id.checkBox);
                        //checkBox
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
                                    AddRide(SessionManager.getKEY(), pickup_address, drop_address, o, d, mPrice.getText().toString(), "50", mPassengers.getText().toString(), "", date_time_value, date_time_value);
                                    if(Checkbox.isChecked())
                                        SavePost(pickup_address,drop_address,date_time,time_value);
//                                    Toast.makeText(getActivity(), "do tasked", Toast.LENGTH_SHORT).show();
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
//                o = String.valueOf(pickup.getLatLng().latitude) + "," + String.valueOf(pickup.getLatLng().longitude);
//                d = String.valueOf(drop.getLatLng().latitude) + "," + String.valueOf(drop.getLatLng().longitude);
//                pickup_address = (String) pickup_location.getText();
//                drop_address = (String) drop_location.getText();
//                Log.i("message", "ibrahim---------------------------------------");
//                Log.i("pickup_address", pickup_address);
//                Log.i("pickup_location", o);
//                Log.i("drop_address", drop_address);
//                Log.i("drop_location", d);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("data", pass);
//                RequestFragment fragobj = new RequestFragment();
//                fragobj.setArguments(bundle);
//                ((HomeActivity) getActivity()).changeFragment(fragobj, "Request Ride");
            }
        });
        smoke_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Somke");
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.box_input, (ViewGroup) getView(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                final TextInputLayout inputvalue = (TextInputLayout) viewInflated.findViewById(R.id.input_value);
                final RadioButton no = (RadioButton) viewInflated.findViewById(R.id.no);
                final RadioButton yes = (RadioButton) viewInflated.findViewById(R.id.yes);

                inputvalue.setVisibility(View.GONE);
                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (yes.isChecked()) {
                            smoke_value = "Yes";
                        } else {
                            smoke_value = "no";
                        }
                        smoke_search.setText(smoke_value);

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


            }
        });
        passanger_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Passanger Number");
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.box_input, (ViewGroup) getView(), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                final TextInputLayout inputvalue = (TextInputLayout) viewInflated.findViewById(R.id.input_value);
                final LinearLayout smoke_lyner = (LinearLayout) viewInflated.findViewById(R.id.smoke_lyner);

                smoke_lyner.setVisibility(View.GONE);
                builder.setView(viewInflated);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!input.getText().toString().equals("")) {

                            passanger_value = String.valueOf(input.getText());
                            passanger_search.setText(String.valueOf(input.getText()));

                        }
                        //log.e("grouop_id", String.valueOf(gruop_id));
                        //gruop_id = gruop_id;
                        //phone = input.getText().toString();
                        //Add_user_Group(phone,Driver_groups_model.getGroup_id());
                        passanger_search.setText(passanger_value);
                        //    Toast.makeText(getContext(), "passs" + passanger_value, Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
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
    public void SavePost(String pickup_address,String Drop_address,String date_time_value,String time_value) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());

        databaseRefID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UserName = dataSnapshot.child("username").getValue(String.class);
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                String text = getString(R.string.Travel_is_going_from)+pickup_address+" "+
                        getString(R.string.Travel_to)+Drop_address+" "+getString(R.string.Travel_on)+date_time_value+" "+getString(R.string.the_clock)+time_value+" ";
//                log.i("tag","success by ibrahim");
//                log.i("tag", UserName);
                // Firebase code here
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").push();
                Map<String,Object> author = new HashMap<>();
                author.put("uid" , user.getUid());
                author.put("username" , UserName);
                author.put("photoURL" , photoURL);

                Map<String,Object> userObject = new HashMap<>();
                userObject.put("author", author);
                userObject.put("text", text);
                userObject.put("type", "1");
                userObject.put("timestamp", ServerValue.TIMESTAMP);
                databaseRef.setValue(userObject);
                //inputEditPost.getText().clear();
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

                        date_time_value = date_time + " " + hourOfDay + ":" + minute + ":00";
//                        Toast.makeText(getContext(), "show time" + val_date, Toast.LENGTH_SHORT).show();
                        log.i("tag", "success by ibrahim");
                        log.i("tag", date_time_value);
                        time_value= hourOfDay + ":" + minute+ "0";
                        date_time_search.setText(date_time_value);

                    }
                }, mHour, 0, true);
        timePickerDialog.show();
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
        }

    }

    public void AddRide(String key, String pickup_adress, String drop_address, String pickup_location, String drop_locatoin, String amount, String distance, String a_set, String u_set, String s_time, String s_date) {
        final RequestParams params = new RequestParams();
//        params.put("driver_id", driver_id);
        params.put("driver_id", SessionManager.getUserId());
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
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(getActivity(), R.string.ride_has_been_requested, Toast.LENGTH_LONG).show();
                        ((HomeActivity) getActivity()).changeFragment(new SearchUser(), "fragment_search_user");
                    } else {
                        Toast.makeText(getActivity(), "tryAgain", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "tryAgain", Toast.LENGTH_LONG).show();
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
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public int getBackPriority() {
        return NORMAL_BACK_PRIORITY;
    }

}
