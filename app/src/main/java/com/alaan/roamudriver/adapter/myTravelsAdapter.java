package com.alaan.roamudriver.adapter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.alaan.roamudriver.fragement.myTravelDetailFragment;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class myTravelsAdapter extends RecyclerView.Adapter<myTravelsAdapter.Holder> {

    private List<PendingRequestPojo> list;

    public myTravelsAdapter(List<PendingRequestPojo> list) {
        this.list = list;
    }

    @Override
    public myTravelsAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new myTravelsAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final myTravelsAdapter.Holder holder, int position) {
        final PendingRequestPojo pojo = list.get(position);
        Log.i("ibrahim_pojo", pojo.toString());
//        Log.i("ibrahim_pojo", pojo.getTravel_status());

        holder.from_add.setText(pojo.getPickup_address());
        holder.to_add.setText(pojo.getDrop_address());
        holder.customers_count.setText(pojo.getbooked_set());
        Log.i("ibrahim", "pojo.getbooked_set()");
        Log.i("ibrahim", pojo.getbooked_set());
        Utils utils = new Utils();
        holder.status.setText(pojo.getStatus());
//        holder.date.setText(utils.getCurrentDateInSpecificFormat(pojo.getTime()));
//        holder.date.setText(utils.getCurrentDateInSpecificFormat(pojo.getTime()));
        holder.date.setText(pojo.getDate());
        holder.time.setText(pojo.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", pojo);
                myTravelDetailFragment detailFragment = new myTravelDetailFragment();
                detailFragment.setArguments(bundle);
                ((HomeActivity) holder.itemView.getContext()).changeFragment(detailFragment, "Passenger Information");
            }
        });
        BookFont(holder, holder.f);
        BookFont(holder, holder.t);
        BookFont(holder, holder.dn);
        BookFont(holder, holder.dt);

        MediumFont(holder, holder.from_add);
        MediumFont(holder, holder.to_add);
        MediumFont(holder, holder.date);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView from, to, customers_count, from_add, to_add, date, time, status;
        TextView f, t, dn, dt;

        public Holder(View itemView) {
            super(itemView);

            f = (TextView) itemView.findViewById(R.id.from);
            t = (TextView) itemView.findViewById(R.id.to);

            dn = (TextView) itemView.findViewById(R.id.txt_customer_count);
            dt = (TextView) itemView.findViewById(R.id.datee);


            customers_count = (TextView) itemView.findViewById(R.id.txt_customer_count);
            from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            status = (TextView) itemView.findViewById(R.id.Statuss);
        }
    }

    public void BookFont(myTravelsAdapter.Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        view1.setTypeface(font1);
    }

    public void MediumFont(myTravelsAdapter.Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }
}
