package com.alaan.roamudriver.pojo;

import android.app.Activity;
import android.graphics.Color;
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

public class PostList extends ArrayAdapter<Post> {
    private Activity context;
    List<Post> posts;

    public PostList(Activity context, List<Post> posts) {
        super(context, R.layout.layout_artist_list, posts);
        this.context = context;
        this.posts = posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_artist_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewText = (TextView) listViewItem.findViewById(R.id.textViewText);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.textViewDate);
        ImageView PostAvatar  = (ImageView) listViewItem.findViewById(R.id.image);
        Post post = posts.get(position);
        String type = post.type;

        //   Log.i("mmmmmmmmtttttt", type);
        // Log.println(1,"tttttttt", type);

        if(type.equals("0")){
            listViewItem.setBackgroundColor(Color.parseColor("#f5fafe"));
        }else if(type.equals("1")){
            listViewItem.setBackgroundColor(Color.WHITE);
            //listViewItem.setBackgroundResource(R.drawable.listview_item_border);
        }
        textViewName.setText(post.author.username);
        textViewText.setText(post.text);
        if (post.timestamp != null)
        {
            Date date = new Date(post.timestamp);
            SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String stringDate= DateFor.format(date);
            textViewDate.setText(stringDate.toString());
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(post.author.uid);
        databaseRefID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                if (photoURL != null) {
//                    Glide.with(PostList.this.getContext()).load(post.author.photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                    Glide.with(PostList.this.getContext()).load(photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
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