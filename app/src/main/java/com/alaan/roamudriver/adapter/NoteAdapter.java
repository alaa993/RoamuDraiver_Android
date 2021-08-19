package com.alaan.roamudriver.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Holder> {
    ValueEventListener listener;
    DatabaseReference databaseRides;
    List<PendingRequestPojo> list;

    public NoteAdapter(List<PendingRequestPojo> list) {
        this.list = list;
    }

    @Override
    public NoteAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoteAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final NoteAdapter.Holder holder, int position) {
        final PendingRequestPojo pojo = list.get(position);

        holder.txt_name.setText(pojo.getUser_name());
        holder.txt_note.setText(pojo.ride_notes);


        BookFont(holder, holder.name);
        BookFont(holder, holder.note);
        BookFont(holder, holder.txt_name);
        BookFont(holder, holder.txt_note);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView name, note;
        TextView txt_name, txt_note;

        public Holder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            note = (TextView) itemView.findViewById(R.id.note);

            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_note = (TextView) itemView.findViewById(R.id.txt_note);

        }
    }

    public void BookFont(NoteAdapter.Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        view1.setTypeface(font1);
    }

    public void MediumFont(NoteAdapter.Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }

}
