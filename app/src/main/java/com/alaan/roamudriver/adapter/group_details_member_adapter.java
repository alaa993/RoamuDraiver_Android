package com.alaan.roamudriver.adapter;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.fragement.Group_detailsFragment;
import com.alaan.roamudriver.fragement.Group_details_MembersFragment;
import com.alaan.roamudriver.fragement.Gruop_managment;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.fragement.AcceptRideFragment;
import com.alaan.roamudriver.pojo.Group_List_membar;
import com.alaan.roamudriver.pojo.Group_membar;
import com.alaan.roamudriver.pojo.PassMembar;

import java.util.List;

import static com.loopj.android.http.AsyncHttpClient.log;

public class group_details_member_adapter extends RecyclerView.Adapter<group_details_member_adapter.Holder>{

    List<Group_List_membar> list;
    PassMembar pass;

    public group_details_member_adapter(List<Group_List_membar> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public group_details_member_adapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new group_details_member_adapter.Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_details_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull group_details_member_adapter.Holder holder, int i) {
        final Group_List_membar pojo = list.get(i);

        holder.group_name_item.setText(pojo.group_name);
        holder.admin_name_item.setText(pojo.name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("data", pojo);
                Group_details_MembersFragment group_details_MembersFragment = new Group_details_MembersFragment();
                group_details_MembersFragment.setArguments(bundle);
                ((HomeActivity) holder.itemView.getContext()).changeFragment(group_details_MembersFragment, "Group Details Management");

            }
        });
    }

    public class Holder extends RecyclerView.ViewHolder {


        TextView admin_name_item, group_name_item, PromoPre;

        public Holder(View itemView) {
            super(itemView);
            admin_name_item = (TextView) itemView.findViewById(R.id.admin_name_item);
            group_name_item = (TextView) itemView.findViewById(R.id.group_name_item);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();

    }
}
