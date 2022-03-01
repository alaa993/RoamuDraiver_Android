package com.alaan.roamudriver.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.fragement.ShareAppFragment;
import com.alaan.roamudriver.pojo.promopojo;

import java.util.List;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.Holder> {
    List<promopojo> list;

    public PromoAdapter(List<promopojo> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.promo_list, viewGroup, false));
    }

    public class Holder extends RecyclerView.ViewHolder {


        TextView PromoDec, PromoTitle, PromoPre;

        public Holder(View itemView) {
            super(itemView);


            PromoDec = (TextView) itemView.findViewById(R.id.tvPromoDesc);
            PromoTitle = (TextView) itemView.findViewById(R.id.tvPromoTitle);
            PromoPre = (TextView) itemView.findViewById(R.id.tvPromoTerm);


        }
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        final promopojo pojo = list.get(i);

        holder.PromoTitle.setText(pojo.promo_title);
        holder.PromoDec.setText(pojo.promo_desc);
        holder.PromoPre.setText(pojo.promo_code);
        //log.e("ss",""+list.size());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", pojo);
                ShareAppFragment shareAppFragment = new ShareAppFragment();
                shareAppFragment.setArguments(bundle);
                ((HomeActivity) holder.itemView.getContext()).changeFragment(shareAppFragment, "Share Application");
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();

    }
}
