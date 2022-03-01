package com.alaan.roamudriver.adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.acitivities.LoginActivity;
import com.alaan.roamudriver.custom.Utils;
import com.alaan.roamudriver.fragement.AcceptedDetailFragment;
import com.alaan.roamudriver.fragement.myTravelDetailFragment;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.pojo.firebaseTravelCounters;
import com.alaan.roamudriver.session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import afu.org.checkerframework.checker.nullness.qual.NonNull;
import cz.msebera.android.httpclient.Header;

import static com.alaan.roamudriver.Server.Server.travel_type_change;

public class MyScheduledTravelAdapter extends RecyclerView.Adapter<MyScheduledTravelAdapter.Holder> {

    private List<PendingRequestPojo> list;

    TextView date_time_search;
    private String date_time_value;
    String date_time = "";
    String time_value = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;

    public MyScheduledTravelAdapter(List<PendingRequestPojo> list) {
        this.list = list;
    }

    @Override
    public MyScheduledTravelAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyScheduledTravelAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.scheduled_travel_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyScheduledTravelAdapter.Holder holder, int position) {
        final PendingRequestPojo pojo = list.get(position);
        //log.i("ibrahim_pojo", pojo.toString());
//        //log.i("ibrahim_pojo", pojo.getTravel_status());

        holder.from_add.setText(pojo.getPickup_address());
        holder.to_add.setText(pojo.getDrop_address());
        holder.customers_count.setText(pojo.getbooked_set());
        //log.i("ibrahim", "pojo.getbooked_set()");
        //log.i("ibrahim", pojo.getbooked_set());
        Utils utils = new Utils();
//        holder.date.setText(utils.getCurrentDateInSpecificFormat(pojo.getTime()));
//        holder.date.setText(utils.getCurrentDateInSpecificFormat(pojo.getTime()));
        holder.date.setText(pojo.getDate());
        holder.time.setText(pojo.getTime());

        if (pojo.travel_type.equalsIgnoreCase("1")) {
            holder.switchCompat.setChecked(true);
            DrawableCompat.setTintList(DrawableCompat.wrap(holder.switchCompat.getThumbDrawable()), new ColorStateList(holder.states, holder.thumbColors));
        }

        holder.Post_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), holder.itemView);
                popup.inflate(R.menu.post_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_edit:
                                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(view.getContext());
                                LayoutInflater li = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View mView = li.inflate(R.layout.dialog_update_scheduled_travel_layout, null);

                                TextView pickup_location = (TextView) mView.findViewById(R.id.pickup_search_location);
                                TextView drop_location = (TextView) mView.findViewById(R.id.search_drop_location);
                                date_time_search = (TextView) mView.findViewById(R.id.Time_date_search);
                                final EditText mPassengers = (EditText) mView.findViewById(R.id.etPassengers);
                                final EditText mPrice = (EditText) mView.findViewById(R.id.etPrice);
                                final EditText etNotes = (EditText) mView.findViewById(R.id.etNotes);
                                final EditText mPickupPoint = (EditText) mView.findViewById(R.id.etPickupPoint);

                                pickup_location.setText(pojo.getPickup_address());
                                drop_location.setText(pojo.getDrop_address());
                                date_time_search.setText(pojo.getDate() + " " + pojo.getTime());
                                mPassengers.setText(pojo.available_set);
                                mPrice.setText(pojo.getAmount());
                                etNotes.setText(pojo.tr_notes);
                                mPickupPoint.setText(pojo.getPickup_point());

                                Button btnSubmit_DUOL = (Button) mView.findViewById(R.id.btnSubmit_DUOL);
                                Button btnCancel_DUOL = (Button) mView.findViewById(R.id.btnCancel_DUOL);

                                date_time_search.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        datePicker(view);
                                    }
                                });

                                mBuilder.setView(mView);
                                final android.app.AlertDialog dialog = mBuilder.create();
                                dialog.show();

                                btnSubmit_DUOL.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        update_scheduled_travels(pojo.getTravel_id(), pojo.getPickup_address(), pojo.getDrop_address(), pojo.getpickup_location(), pojo.getdrop_location(), mPrice.getText().toString(), mPassengers.getText().toString(),
                                                date_time_value, time_value, mPickupPoint.getText().toString(), "", String.valueOf(etNotes.getText()));
                                        dialog.dismiss();
                                    }
                                });
                                btnCancel_DUOL.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                return true;

                            case R.id.nav_delete:
                                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
                                alertDialog.setTitle(view.getContext().getString(R.string.do_you_want_to_delete));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, view.getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, view.getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        delete_scheduled_travels(pojo.getTravel_id());
                                        dialog.dismiss();
                                    }
                                });

                                alertDialog.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), holder.itemView);
                popup.inflate(R.menu.post_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_edit:
                                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(view.getContext());
                                LayoutInflater li = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View mView = li.inflate(R.layout.dialog_update_scheduled_travel_layout, null);

                                TextView pickup_location = (TextView) mView.findViewById(R.id.pickup_search_location);
                                TextView drop_location = (TextView) mView.findViewById(R.id.search_drop_location);
                                date_time_search = (TextView) mView.findViewById(R.id.Time_date_search);
                                final EditText mPassengers = (EditText) mView.findViewById(R.id.etPassengers);
                                final EditText mPrice = (EditText) mView.findViewById(R.id.etPrice);
                                final EditText etNotes = (EditText) mView.findViewById(R.id.etNotes);
                                final EditText mPickupPoint = (EditText) mView.findViewById(R.id.etPickupPoint);

                                pickup_location.setText(pojo.getPickup_address());
                                drop_location.setText(pojo.getDrop_address());
                                date_time_search.setText(pojo.getDate() + " " + pojo.getTime());
                                mPassengers.setText(pojo.available_set);
                                mPrice.setText(pojo.getAmount());
                                etNotes.setText(pojo.tr_notes);
                                mPickupPoint.setText(pojo.getPickup_point());

                                Button btnSubmit_DUOL = (Button) mView.findViewById(R.id.btnSubmit_DUOL);
                                Button btnCancel_DUOL = (Button) mView.findViewById(R.id.btnCancel_DUOL);

                                date_time_search.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        datePicker(view);
                                    }
                                });

                                mBuilder.setView(mView);
                                final android.app.AlertDialog dialog = mBuilder.create();
                                dialog.show();

                                btnSubmit_DUOL.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        update_scheduled_travels(pojo.getTravel_id(), pojo.getPickup_address(), pojo.getDrop_address(), pojo.getpickup_location(), pojo.getdrop_location(), mPrice.getText().toString(), mPassengers.getText().toString(),
                                                date_time_value, time_value, mPickupPoint.getText().toString(), "", String.valueOf(etNotes.getText()));
                                        dialog.dismiss();
                                    }
                                });
                                btnCancel_DUOL.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                return true;

                            case R.id.nav_delete:
                                AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
                                alertDialog.setTitle(view.getContext().getString(R.string.do_you_want_to_delete));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, view.getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, view.getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        delete_scheduled_travels(pojo.getTravel_id());
                                        dialog.dismiss();
                                    }
                                });

                                alertDialog.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Utils.haveNetworkConnection(buttonView.getContext())) {
                    DrawableCompat.setTintList(DrawableCompat.wrap(holder.switchCompat.getThumbDrawable()), new ColorStateList(holder.states, holder.thumbColors));

                    if (isChecked) {
                        //log.i("ibrhim", "switchCompat");
                        //log.i("ibrhim", "1");
                        travel_type_change(holder, pojo.getTravel_id(), "1", false);
                    } else {
                        //log.i("ibrhim", "switchCompat");
                        //log.i("ibrhim", "0");
                        travel_type_change(holder, pojo.getTravel_id(), "0", false);
                    }

                } else {
                    Toast.makeText(buttonView.getContext(), buttonView.getContext().getString(R.string.network_not_available), Toast.LENGTH_LONG).show();

                }
            }
        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("data", pojo);
//                myTravelDetailFragment detailFragment = new myTravelDetailFragment();
//                detailFragment.setArguments(bundle);
//                ((HomeActivity) holder.itemView.getContext()).changeFragment(detailFragment, "Passenger Information");
//            }
//        });
        BookFont(holder, holder.f);
        BookFont(holder, holder.t);
        BookFont(holder, holder.dn);
        BookFont(holder, holder.dt);

        MediumFont(holder, holder.from_add);
        MediumFont(holder, holder.to_add);
        MediumFont(holder, holder.date);
    }

    public void travel_type_change(final MyScheduledTravelAdapter.Holder holder, String user_id, String status, Boolean what) {
        RequestParams params = new RequestParams();
        params.put("travel_id", user_id);
        params.put("travel_type", status);
        Server.setHeader(SessionManager.getKEY());
        Server.post(Server.scheduled_travel_type_change, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        if (response.has("travel_id")) {
                            String travel_id = response.getString("travel_id");
                            if(travel_id != null && !travel_id.contains("null")){
                                addTravelToFireBase(travel_id);
                            }
                        }
                    } else {
//                        Toast.makeText(getContext(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
//                    Toast.makeText(getContext(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                //log.e("FAIl", throwable.toString() + ".." + errorResponse);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //log.e("FAIl", throwable.toString() + ".." + errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //log.e("FAIl", throwable.toString() + ".." + responseString);
            }

            @Override
            public void onFinish() {
                super.onFinish();

            }
        });
    }

    public void update_scheduled_travels(String travel_id, String pickup_address, String drop_address, String pickup_location, String drop_location, String amount, String a_set, String s_date, String s_time, String PickupPoint, String pickup_point_location, String tr_notes) {
        RequestParams params = new RequestParams();
        params.put("travel_id", travel_id);
//        params.put("pickup_address", pickup_address);
//        params.put("drop_address", drop_address);
//        params.put("pickup_location", pickup_location);
//        params.put("drop_location", drop_location);
//        params.put("pickup_point", PickupPoint);
//        params.put("pickup_point_location", pickup_point_location);
        params.put("amount", amount);
        params.put("available_set", a_set);
        params.put("travel_date", s_date);
        params.put("travel_time", s_time);
        params.put("tr_notes", tr_notes);
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.Update_schduled_Travels, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //log.i("ibrahim", "response.toString()");
                //log.i("ibrahim", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());

                        if (response.has("data") && response.getJSONArray("data").length() > 0) {

                        }
                    } else {
                        String data = response.getJSONObject("data").toString();
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

    public void delete_scheduled_travels(String travel_id) {
        RequestParams params = new RequestParams();
        params.put("travel_id", travel_id);
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.Delete_schduled_Travels, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                    } else {
                        String data = response.getJSONObject("data").toString();
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

    private void datePicker(View view) {

//        setLocale("en", getActivity());
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

//                        date_time = String.format("%04d-%02d-%02d", year, 1 + monthOfYear, dayOfMonth);
                        date_time = formatDateWithPattern1(String.format("%04d-%02d-%02d", year, 1 + monthOfYear, dayOfMonth));
                        //log.i("ibrahim", date_time);
                        //date_time = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        tiemPicker(view);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker(View view) {
//        setLocale("en", getActivity());
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
//                        date_time_value = date_time + " " + hourOfDay + ":" + minute;
                        date_time_value = date_time + " " + formatDateWithPattern2(String.format("%02d:%02d", hourOfDay, minute));
                        time_value = hourOfDay + ":" + minute;
                        date_time_search.setText(date_time + " " + formatDateWithPattern2(String.format("%02d:%02d", hourOfDay, minute)));
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    public void addTravelToFireBase(String travel_id) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(travel_id);
        firebaseTravelCounters counters = new firebaseTravelCounters();
        Map<String, Object> travelObject = new HashMap<>();
        travelObject.put("driver_id", String.valueOf(SessionManager.getUserId()));
        travelObject.put("Counters", counters);
        databaseRef.setValue(travelObject);
    }

    static String formatDateWithPattern1(String strDate) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = fmt.parse(strDate);
            return fmt.format(date);
        } catch (ParseException pe) {
            return "Date";
        }
    }

    static String formatDateWithPattern2(String strDate) {
        SimpleDateFormat fmt = new SimpleDateFormat("kk:mm", Locale.ENGLISH);
        try {
            Date date = fmt.parse(strDate);
            return fmt.format(date);
        } catch (ParseException pe) {
            return "Date";
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView from, to, customers_count, from_add, to_add, date, time;
        TextView f, t, dn, dt;
        ImageView Post_more;
        Switch switchCompat;
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
        };

        int[] thumbColors = new int[]{
                Color.RED,
                Color.GREEN,
        };

        public Holder(View itemView) {
            super(itemView);

            f = (TextView) itemView.findViewById(R.id.from);
            t = (TextView) itemView.findViewById(R.id.to);

            dn = (TextView) itemView.findViewById(R.id.txt_customer_count);
            dt = (TextView) itemView.findViewById(R.id.datee);
            Post_more = (ImageView) itemView.findViewById(R.id.img);


            customers_count = (TextView) itemView.findViewById(R.id.txt_customer_count);
            from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);

            switchCompat = (Switch) itemView.findViewById(R.id.travel_schedule);
            switchCompat.setChecked(false);
            DrawableCompat.setTintList(DrawableCompat.wrap(switchCompat.getThumbDrawable()), new ColorStateList(states, thumbColors));
        }
    }

    public void BookFont(MyScheduledTravelAdapter.Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        view1.setTypeface(font1);
    }

    public void MediumFont(MyScheduledTravelAdapter.Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }
}