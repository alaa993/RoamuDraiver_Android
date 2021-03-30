package com.alaan.roamudriver.adapter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.custom.Utils;
import com.alaan.roamudriver.fragement.AcceptedDetailFragment;
import com.alaan.roamudriver.fragement.MyAcceptedDetailFragment;
import com.alaan.roamudriver.pojo.PendingRequestPojo;

import java.util.List;

public class MyAcceptedRequestAdapter extends RecyclerView.Adapter<MyAcceptedRequestAdapter.Holder> {
    private List<PendingRequestPojo> list;

    public MyAcceptedRequestAdapter(List<PendingRequestPojo> list) {
        this.list = list;
    }

    @Override
    public MyAcceptedRequestAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyAcceptedRequestAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_acceptedrequest_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final PendingRequestPojo pojo = list.get(position);
        Log.i("ibrahim_pojo", pojo.toString());
        Log.i("ibrahim_pojo", pojo.getTravel_status());

        holder.from_add.setText(pojo.getPickup_address());
        holder.to_add.setText(pojo.getDrop_address());
        holder.drivername.setText(pojo.getUser_name());
        holder.status.setText(pojo.getStatus());

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
        BookFont(holder, holder.f);
        BookFont(holder, holder.t);
        BookFont(holder, holder.dn);
//        BookFont(holder, holder.dt);

        MediumFont(holder, holder.from_add);
        MediumFont(holder, holder.to_add);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {


        TextView drivername, from_add, to_add, status;
        TextView f, t, dn;

        public Holder(View itemView) {
            super(itemView);

            f = (TextView) itemView.findViewById(R.id.from);
            t = (TextView) itemView.findViewById(R.id.to);

            dn = (TextView) itemView.findViewById(R.id.drivername);
//            dt = (TextView) itemView.findViewById(R.id.datee);


            drivername = (TextView) itemView.findViewById(R.id.txt_drivername);
            from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
            status = (TextView) itemView.findViewById(R.id.Statuss);
        }
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
