package com.alaan.roamudriver.pojo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alaan.roamudriver.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentList extends ArrayAdapter<Comment>{
    private Activity context;
    List<Comment> comments;

    public CommentList(Activity context, List<Comment> comments) {
        super(context, R.layout.layout_artist_list, comments);
        this.context = context;
        this.comments = comments;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_artist_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewText = (TextView) listViewItem.findViewById(R.id.textViewText);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDate);
        ImageView CommentAvatar  = (ImageView) listViewItem.findViewById(R.id.image);

        Comment comment = comments.get(position);
        textViewName.setText(comment.author.username);
        textViewText.setText(comment.text);

        if (comment.timestamp != null)
        {
            Date date = new Date(comment.timestamp);
            SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String stringDate= DateFor.format(date);
            textViewDate.setText(stringDate.toString());
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(comment.author.uid);
        databaseRefID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);

                if (photoURL != null) {
                    Glide.with(CommentList.this.getContext()).load(photoURL).apply(new RequestOptions().error(R.drawable.images)).into(CommentAvatar);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });


        return listViewItem;
    }
}
