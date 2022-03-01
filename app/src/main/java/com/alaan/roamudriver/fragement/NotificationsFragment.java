package com.alaan.roamudriver.fragement;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.adapter.NotificationAdapter;
import com.alaan.roamudriver.pojo.Notification;
import com.alaan.roamudriver.session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    View view;
    ListView listViewNotificatoins;
    private FirebaseUser fUser;


    //a list to store all the artist from firebase database
    List<Notification> notifications;
    DatabaseReference databasePosts;
    ValueEventListener listener;

    public NotificationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        notifications = new ArrayList<>();
        //log.i("ibrahim_uid", String.valueOf(SessionManager.getUser()));
        //log.i("ibrahim_uid", String.valueOf(SessionManager.getUser().getUser_id()));
        try {
            databasePosts = FirebaseDatabase.getInstance().getReference("Notifications").child(SessionManager.getUser().getUser_id());
        }catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e){
            //log.i("ibrahim_e",e.getMessage());
        }
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.notifications));
        view.setBackgroundColor(Color.WHITE);
        BindView();

        return view;
    }

    public void BindView() {
        listViewNotificatoins = (ListView) view.findViewById(R.id.listViewNotifications);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        listener = databasePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                notifications.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    //log.i("ibrahim_notificatoni",postSnapshot.toString());
                    Notification notification = postSnapshot.getValue(Notification.class);
                    notification.id = postSnapshot.getKey();
                    notifications.add(notification);
                }
                Collections.reverse(notifications);
                if (!notifications.isEmpty()) {
                    //creating adapter
                    NotificationAdapter notificationAdapter = new NotificationAdapter(getActivity(), notifications);
                    //attaching adapter to the listview
                    notificationAdapter.notifyDataSetChanged();
                    listViewNotificatoins.setAdapter(notificationAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        databasePosts.removeEventListener(listener);
    }
}