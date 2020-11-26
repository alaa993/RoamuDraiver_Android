package com.alaan.roamudriver.adapter;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.pojo.Notification;
import com.alaan.roamudriver.pojo.Pass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    private FragmentActivity context;
    List<Notification> notifications;
    Pass pass;
    DatabaseReference databaseComments;

    public NotificationAdapter(FragmentActivity context, List<Notification> notifications) {

        super(context, R.layout.notification_item, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(R.layout.notification_item, null, true);
        //listViewItem.setBackgroundResource(R.drawable.listview_item_border);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.Notificatoin_textViewName);
        TextView textViewText = (TextView) listViewItem.findViewById(R.id.Notificatoin_textViewText);
        ImageView PostAvatar = (ImageView) listViewItem.findViewById(R.id.Notificatoin_image);
        Notification notification = notifications.get(position);

        Log.i("ibrahim_1",notification.toString());
        textViewText.setText(notification.text);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(notification.uid);
        databaseRefID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                String UserName = dataSnapshot.child("username").getValue(String.class);

                if (photoURL != null) {
                    Glide.with(NotificationAdapter.this.getContext()).load(photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                }
                textViewName.setText(UserName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });


        return listViewItem;
    }
}
