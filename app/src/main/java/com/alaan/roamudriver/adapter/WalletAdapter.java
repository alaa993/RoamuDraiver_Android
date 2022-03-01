package com.alaan.roamudriver.adapter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.fragement.AcceptedDetailFragment;
import com.alaan.roamudriver.fragement.WalletDetailFragment;
import com.alaan.roamudriver.pojo.Wallet;
import com.alaan.roamudriver.pojo.Wallet;

import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.Holder>{
    List<Wallet> list;

    public WalletAdapter(List<Wallet> list) {
        this.list = list;
    }

    @Override
    public WalletAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WalletAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final WalletAdapter.Holder holder, int position) {
        final Wallet pojo = list.get(position);

        holder.txt_transaction_type.setText(pojo.typeTName);
        holder.txt_amount.setText(pojo.amount);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", pojo);
                WalletDetailFragment walletDetailFragment = new WalletDetailFragment();
                walletDetailFragment.setArguments(bundle);
                ((HomeActivity) holder.itemView.getContext()).changeFragment(walletDetailFragment, "Wallet Information");
            }
        });

        BookFont(holder, holder.transaction_type);
        BookFont(holder, holder.amount);
        BookFont(holder, holder.txt_transaction_type);
        BookFont(holder, holder.txt_amount);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView transaction_type, amount;
        TextView txt_transaction_type, txt_amount;

        public Holder(View itemView) {
            super(itemView);
            transaction_type = (TextView) itemView.findViewById(R.id.transaction_type);
            amount = (TextView) itemView.findViewById(R.id.amount);

            txt_transaction_type = (TextView) itemView.findViewById(R.id.txt_transaction_type);
            txt_amount = (TextView) itemView.findViewById(R.id.txt_amount);

        }
    }

    public void BookFont(WalletAdapter.Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        view1.setTypeface(font1);
    }

    public void MediumFont(WalletAdapter.Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }
}
