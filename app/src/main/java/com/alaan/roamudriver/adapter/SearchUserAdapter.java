package com.alaan.roamudriver.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.fragement.Search_list_acticity;
import com.alaan.roamudriver.fragement.AcceptRideFragment;
import com.alaan.roamudriver.pojo.PendingRequestPojo;

import java.util.List;

import static com.loopj.android.http.AsyncHttpClient.log;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.Holder>  {
    List<PendingRequestPojo> list;
    PendingRequestPojo pass;
    public SearchUserAdapter(List<PendingRequestPojo> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_user_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserAdapter.Holder holder, int i) {
        final PendingRequestPojo pojo = list.get(i);

        holder.txt_to_add.setText(pojo.getDrop_address());
        holder.txt_from_add.setText(pojo.getPickup_address());
        holder.time.setText(pojo.getTime());
        holder.name.setText(pojo.getUser_name());
        holder.date.setText(pojo.getAmount());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
//                pass = new PendingRequestPojo();
//                bundle.putSerializable("data", pass);
//                Toast.makeText(v.getContext(), ""+i, Toast.LENGTH_SHORT).show();
//                pass.setPickup_address(pojo.getPickup_address());
//                pass.setDrop_address(pojo.getDrop_address());
//                pass.setpickup_location(pojo.getpickup_location());
//                pass.setdrop_location(pojo.getdrop_location());
//                pass.setRide_smoked(pojo.getRide_smoked());
//                pass.setbooked_set(pojo.getbooked_set());
//                pass.setUser_mobile(pojo.getUser_mobile());
//                pass.setCity(pojo.getCity());
                bundle.putSerializable("data",pojo);
                AcceptRideFragment detailFragment = new AcceptRideFragment();
                detailFragment.setArguments(bundle);

                ((Search_list_acticity) holder.itemView.getContext()).changeFragment(detailFragment, "Passenger Information");
                ((Search_list_acticity) holder.itemView.getContext()).showframe(0);

            }
        });
        log.e("ss",""+list.size());
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
