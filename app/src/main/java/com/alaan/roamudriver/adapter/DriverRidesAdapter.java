package com.alaan.roamudriver.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.fragement.AcceptRideFragment;
import com.alaan.roamudriver.fragement.MangeDriverDetail;
import com.alaan.roamudriver.pojo.DriverRides;
import com.alaan.roamudriver.pojo.Pass;

import java.util.List;

import static com.loopj.android.http.AsyncHttpClient.log;

public class DriverRidesAdapter extends RecyclerView.Adapter<DriverRidesAdapter.Holder> {

    List<DriverRides> list;
    Pass pass;

    public DriverRidesAdapter(List<DriverRides> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.acceptedrequest_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DriverRidesAdapter.Holder holder, int i) {
        final DriverRides pojo = list.get(i);

        holder.txt_to_add.setText(pojo.getDrop_address());
        holder.txt_from_add.setText(pojo.getPickup_address());
        holder.time.setText(pojo.getTravel_time());
        holder.name.setText(pojo.getPickup_address());
        holder.date.setText(pojo.getTravel_date());

        log.e("ss",""+list.size());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data",pojo);
                pass = new Pass();
                Toast.makeText(v.getContext(), ""+i, Toast.LENGTH_SHORT).show();
                pass.setPickPlace(pojo.getPickup_address());
                pass.setDropPlace(pojo.getDrop_address());
                pass.setFromAddress(pojo.getPickup_location());
                pass.setToAddress(pojo.getDrop_location());
               // pass.setDriverId(nearbyData.getUser_id());
               // pass.setFare(nearbyData.getAmount());
                //pass.setDriverName(nearbyData.getName());
                AcceptRideFragment fragobj = new AcceptRideFragment();
                fragobj.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                bundle.putSerializable("data", pass);
                fragobj.setArguments(bundle);

                MangeDriverDetail mangeDriverDetail = new MangeDriverDetail();
                mangeDriverDetail.setArguments(bundle);

                ((HomeActivity) holder.itemView.getContext()).changeFragment(mangeDriverDetail, "Passenger Information");
            }
        });

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView txt_from_add, txt_to_add, time , date , name;


        public Holder(@NonNull View itemView) {
            super(itemView);

            time = (TextView) itemView.findViewById(R.id.time);
            name = (TextView) itemView.findViewById(R.id.txt_drivername);
            date = (TextView) itemView.findViewById(R.id.date);
            txt_from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            txt_to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
        }
    }
}
