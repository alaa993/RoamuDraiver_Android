package com.alaan.roamudriver.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.custom.Utils;
import com.alaan.roamudriver.fragement.AcceptedDetailFragment;
import com.alaan.roamudriver.fragement.AcceptedRequestFragment;
import com.alaan.roamudriver.fragement.MyAcceptedDetailFragment;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.pojo.firebaseRide;
import com.alaan.roamudriver.pojo.firebaseTravel;
import com.alaan.roamudriver.session.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class MyAcceptedRequestAdapter extends RecyclerView.Adapter<MyAcceptedRequestAdapter.Holder> {
    private List<PendingRequestPojo> list;
    ValueEventListener listener;
    DatabaseReference databaseRides;
    String Status;
    private String ride_id = "";

    private String travel_status;
    private String ride_status;
    private String payment_status;
    private String payment_mode;

    public MyAcceptedRequestAdapter(List<PendingRequestPojo> list) {
        this.list = list;
    }

    @Override
    public MyAcceptedRequestAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyAcceptedRequestAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_acceptedrequest_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        PendingRequestPojo pojo = list.get(position);
        Log.i("ibrahim_pojo", pojo.toString());
        Log.i("ibrahim_pojo", pojo.getTravel_status());
        Log.i("ibrahim_POSITION", String.valueOf(position));
        ride_id = pojo.getRide_id();

        travel_status = pojo.getTravel_status();
        ride_status = pojo.getStatus();
        payment_status = pojo.getPayment_status();
        payment_mode = pojo.getPayment_mode();


        holder.from_add.setText(pojo.getPickup_address());
        holder.to_add.setText(pojo.getDrop_address());
        holder.drivername.setText(pojo.getUser_name());
        Status = "";
        if (pojo.getStatus().contains("PENDING")) {
            Status = holder.itemView.getContext().getString(R.string.pending);
        } else if (pojo.getStatus().contains("ACCEPTED")) {
            Status = holder.itemView.getContext().getString(R.string.accepted);
        } else if (pojo.getStatus().contains("COMPLETED")) {
            Status = holder.itemView.getContext().getString(R.string.completed);
        } else if (pojo.getStatus().contains("CANCELLED")) {
            Status = holder.itemView.getContext().getString(R.string.cancelled);
        }

        if (pojo.getPayment_status().length() > 0 && pojo.getPayment_mode().length() > 0) {
            Status += ", " + holder.itemView.getContext().getString(R.string.approve_payment_offline);
        } else if (pojo.getPayment_status().length() == 0 && pojo.getPayment_mode().length() > 0) {
            Status += ", " + holder.itemView.getContext().getString(R.string.paid_but_not_approved);
        } else {
            Status += ", " + holder.itemView.getContext().getString(R.string.not_paid);
        }
        holder.status.setText(Status);

        setupData(holder, pojo);

        databaseRides = FirebaseDatabase.getInstance().getReference("rides").child(pojo.getRide_id());
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
                    Status = "";
                    if (fbRide.ride_status.contains("PENDING")) {
                        Status = holder.itemView.getContext().getString(R.string.pending);
                    } else if (fbRide.ride_status.contains("ACCEPTED")) {
                        Status = holder.itemView.getContext().getString(R.string.accepted);
                    } else if (fbRide.ride_status.contains("COMPLETED")) {
                        Status = holder.itemView.getContext().getString(R.string.completed);
                    } else if (fbRide.ride_status.contains("CANCELLED")) {
                        Status = holder.itemView.getContext().getString(R.string.cancelled);
                    }

                    if (fbRide.payment_status.length() > 0 && fbRide.payment_mode.length() > 0) {
                        Status += ", " + holder.itemView.getContext().getString(R.string.approve_payment_offline);
                    } else if (fbRide.payment_status.length() == 0 && fbRide.payment_mode.length() > 0) {
                        Status += ", " + holder.itemView.getContext().getString(R.string.paid_but_not_approved);
                    } else {
                        Status += ", " + holder.itemView.getContext().getString(R.string.not_paid);
                    }
                    holder.status.setText(Status);
//                    setupData(holder, pojo);

                    Log.i("ibrahim_status", String.valueOf(ride_status));

                    if (!ride_status.equals("") && ride_status.equalsIgnoreCase("PENDING")) {
                        holder.item_Button.setVisibility(View.VISIBLE);
                        holder.item_Button1.setVisibility(View.GONE);
//                        holder.item_Button.setText(holder.itemView.getContext().getString(R.string.Pick_Customer));
                    }
                    if (ride_status != null && !ride_status.equals("") && ride_status.equalsIgnoreCase("CANCELLED")) {
                        holder.item_Button.setVisibility(View.GONE);
                        holder.item_Button1.setVisibility(View.GONE);
                    }
                    if (ride_status != null && !ride_status.equals("") && ride_status.equalsIgnoreCase("COMPLETED")) {
                        holder.item_Button.setVisibility(View.GONE);
                        holder.item_Button1.setVisibility(View.GONE);
                    }
                    if (!ride_status.equals("") && ride_status.equalsIgnoreCase("ACCEPTED")) {
                        {
                            if (payment_mode.equals("OFFLINE") && !payment_status.equals("PAID")) {
                                holder.item_Button.setVisibility(View.GONE);
                                holder.item_Button1.setVisibility(View.VISIBLE);
//                                holder.item_Button.setText(holder.itemView.getContext().getString(R.string.approve_payment_offline));
                            } else {
                                holder.item_Button.setVisibility(View.GONE);
                                holder.item_Button1.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", pojo);
                MyAcceptedDetailFragment myDetailFragment = new MyAcceptedDetailFragment();
                myDetailFragment.setArguments(bundle);
                ((HomeActivity) holder.itemView.getContext()).changeFragment(myDetailFragment, "Passenger Information");
            }
        });

//        holder.item_Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (pojo != null) {
//                    Log.i("ibrahim_status", "item_Button");
//                    Log.i("ibrahim_status", String.valueOf(ride_status));
//                    Log.i("ibrahim_status", String.valueOf(payment_mode));
//                    Log.i("ibrahim_status", String.valueOf(payment_status));
//                    if (!ride_status.equals("") && ride_status.equalsIgnoreCase("PENDING")) {
//                        AlertDialogCreate(pojo, holder.itemView.getContext(), holder.itemView.getContext().getString(R.string.ride_acceptance), holder.itemView.getContext().getString(R.string.ride_accept_msg), "ACCEPTED");
//                    }
//                    if (ride_status != null && !ride_status.equals("") && ride_status.equalsIgnoreCase("CANCELLED")) {
//
//                    }
//                    if (ride_status != null && !ride_status.equals("") && ride_status.equalsIgnoreCase("COMPLETED")) {
//
//                    }
//                    if (!ride_status.equals("") && ride_status.equalsIgnoreCase("ACCEPTED")) {
//                        {
//                            if (payment_mode.equals("OFFLINE") && !payment_status.equals("PAID")) {
//                                approvePaymet(pojo, holder.itemView.getContext());
//                            }
//                        }
//                    }
//                }
//            }
//        });

        holder.item_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pojo != null) {
                    Log.i("ibrahim_status", "item_Button");
                    Log.i("ibrahim_status", String.valueOf(ride_status));
                    Log.i("ibrahim_status", String.valueOf(payment_mode));
                    Log.i("ibrahim_status", String.valueOf(payment_status));
                    AlertDialogCreate(pojo, holder.itemView.getContext(), holder.itemView.getContext().getString(R.string.ride_acceptance), holder.itemView.getContext().getString(R.string.ride_accept_msg), "ACCEPTED");
                }
            }
        });

        holder.item_Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pojo != null) {
                    Log.i("ibrahim_status", "item_Button");
                    Log.i("ibrahim_status", String.valueOf(ride_status));
                    Log.i("ibrahim_status", String.valueOf(payment_mode));
                    Log.i("ibrahim_status", String.valueOf(payment_status));

                    approvePaymet(pojo, holder.itemView.getContext());
                }
            }
        });

        BookFont(holder, holder.f);
        BookFont(holder, holder.t);
        BookFont(holder, holder.dn);

        MediumFont(holder, holder.from_add);
        MediumFont(holder, holder.to_add);
    }

    public void setupData(Holder holder, PendingRequestPojo pojo1) {
//        if (pojo1 != null) {
        if (!pojo1.getStatus().equals("") && pojo1.getStatus().equalsIgnoreCase("PENDING")) {
            holder.item_Button.setVisibility(View.VISIBLE);
            holder.item_Button1.setVisibility(View.GONE);
//            holder.item_Button.setText(holder.itemView.getContext().getString(R.string.Pick_Customer));
        }
        if (pojo1.getStatus() != null && !pojo1.getStatus().equals("") && pojo1.getStatus().equalsIgnoreCase("CANCELLED")) {
            holder.item_Button.setVisibility(View.GONE);
            holder.item_Button1.setVisibility(View.GONE);
        }
        if (pojo1.getStatus() != null && !pojo1.getStatus().equals("") && pojo1.getStatus().equalsIgnoreCase("COMPLETED")) {
            holder.item_Button.setVisibility(View.GONE);
            holder.item_Button1.setVisibility(View.GONE);
        }
        if (!pojo1.getStatus().equals("") && pojo1.getStatus().equalsIgnoreCase("ACCEPTED")) {
            {
                if (pojo1.getPayment_mode().equals("OFFLINE") && !pojo1.getPayment_status().equals("PAID")) {
                    holder.item_Button.setVisibility(View.GONE);
                    holder.item_Button1.setVisibility(View.VISIBLE);
//                    holder.item_Button.setText(holder.itemView.getContext().getString(R.string.approve_payment_offline));
                } else {
                    holder.item_Button.setVisibility(View.GONE);
                    holder.item_Button1.setVisibility(View.GONE);
                }
            }
        }
//        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {


        TextView drivername, from_add, to_add, status;
        TextView f, t, dn;
        AppCompatButton item_Button;
        AppCompatButton item_Button1;

        public Holder(View itemView) {
            super(itemView);

            f = (TextView) itemView.findViewById(R.id.from);
            t = (TextView) itemView.findViewById(R.id.to);
            dn = (TextView) itemView.findViewById(R.id.drivername);


            drivername = (TextView) itemView.findViewById(R.id.txt_drivername);
            from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
            status = (TextView) itemView.findViewById(R.id.Statuss);
            item_Button = (AppCompatButton) itemView.findViewById(R.id.MyAR_I_btn);
            item_Button1 = (AppCompatButton) itemView.findViewById(R.id.MyAR_I_btn1);
        }
    }

    public void AlertDialogCreate(PendingRequestPojo pojo, Context context, String title, String message, final String status) {
        Log.i("ibrahim_Alert", pojo.getRide_id());
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new AlertDialog.Builder(context)
                .setIcon(drawable)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(context.getString(R.string.ccancel), null)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        SendStatus(pojo, context, ride_id, status);
                    }
                })
                .setNegativeButton(context.getString(R.string.ccancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void SendStatus(PendingRequestPojo pojo, Context context, String ride_id, final String status) {
        Log.i("ibrahim_SendStatus", pojo.getRide_id());
        Log.i("ibrahim_SendStatus", ride_id);

        RequestParams params = new RequestParams();
        params.put("ride_id", pojo.getRide_id());
        params.put("status", status);
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
                        updateRideFirebase(travel_status, status, payment_status, payment_mode, pojo);
                        updateNotificationFirebase(context, status, pojo);
                        updateTravelCounterFirebase(status, pojo);
                        if (status.equals("ACCEPTED")) {
                            updateTravelFirebase(pojo);
                        }
                    } else {
                        String data = response.getJSONObject("data").toString();
                        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
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

    public void updateTravelCounterFirebase(String status, PendingRequestPojo pojo) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(pojo.getTravel_id());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseTravel fbTravel = dataSnapshot.getValue(firebaseTravel.class);
                if (fbTravel != null) {
                    if (status.contains("ACCEPTED")) {
                        Log.i("ibrahim", "fbTravel");
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(pojo.getTravel_id()).child("Counters").child("ACCEPTED");
                        databaseRef.setValue(fbTravel.Counters.ACCEPTED + 1);
                    } else if (status.contains("COMPLETED")) {
                        Log.i("ibrahim", "fbTravel");
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(pojo.getTravel_id()).child("Counters").child("COMPLETED");
                        databaseRef.setValue(fbTravel.Counters.COMPLETED + 1);

                        DatabaseReference databaseRef1 = FirebaseDatabase.getInstance().getReference("Travels").child(pojo.getTravel_id()).child("Counters").child("ACCEPTED");
                        databaseRef.setValue(fbTravel.Counters.ACCEPTED - 1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    private void approvePaymet(PendingRequestPojo pojo, Context context) {
        RequestParams params = new RequestParams();
        params.put("ride_id", pojo.getRide_id());
        params.put("travel_id", pojo.getTravel_id());
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
                updateRideFirebase(travel_status, ride_status, "PAID", payment_mode, pojo);
                updateNotificationFirebase(context, context.getString(R.string.notification_5), pojo);
                updateTravelCounterFirebase(pojo);
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void updateTravelCounterFirebase(PendingRequestPojo pojo) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(pojo.getTravel_id());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseTravel fbTravel = dataSnapshot.getValue(firebaseTravel.class);
                if (fbTravel != null) {
                    Log.i("ibrahim", "fbTravel");
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(pojo.getTravel_id()).child("Counters").child("PAID");
                    databaseRef.setValue(fbTravel.Counters.PAID + 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void updateRideFirebase(String travel_status, String ride_status, String payment_status, String payment_mode, PendingRequestPojo pojo1) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("rides").child(pojo1.getRide_id());
        Map<String, Object> rideObject = new HashMap<>();

        rideObject.put("ride_status", ride_status);
//        if (ride_status.contains("ACCEPTED")) {
//            rideObject.put("travel_status", "STARTED");
//        } else {
//            rideObject.put("travel_status", travel_status);
//        }
        rideObject.put("travel_status", travel_status);

        rideObject.put("payment_status", payment_status);
        rideObject.put("payment_mode", payment_mode);
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        databaseRef.setValue(rideObject);
    }

    public void updateNotificationFirebase(Context context, String notificationText, PendingRequestPojo pojo1) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Notifications").child(pojo1.getUser_id()).push();
        Map<String, Object> rideObject = new HashMap<>();
        rideObject.put("ride_id", pojo1.getRide_id());
        rideObject.put("text", context.getString(R.string.RideUpdated) + " " + notificationText);
        rideObject.put("readStatus", "0");
        rideObject.put("timestamp", ServerValue.TIMESTAMP);
        rideObject.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseRef.setValue(rideObject);
    }

    public void updateTravelFirebase(PendingRequestPojo pojo1) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(pojo1.getTravel_id());
        Map<String, Object> travelObject = new HashMap<>();
        travelObject.put("driver_id", pojo1.getDriver_id());

        databaseRef.updateChildren(travelObject).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Travels").child(pojo1.getTravel_id()).child("Clients").child(pojo1.getUser_id());
                databaseRef.setValue(pojo1.getUser_id());
            }
        });

    }

    public void BookFont(MyAcceptedRequestAdapter.Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        view1.setTypeface(font1);
    }

    public void MediumFont(MyAcceptedRequestAdapter.Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }
}
