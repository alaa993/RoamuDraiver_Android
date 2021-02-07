package com.alaan.roamudriver.pojo;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.acitivities.PostActivity;
import com.alaan.roamudriver.adapter.SearchUserAdapter;
import com.alaan.roamudriver.fragement.AcceptRideFragment;
import com.alaan.roamudriver.fragement.Search_list_acticity;
import com.alaan.roamudriver.session.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.stash.Stash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import cz.msebera.android.httpclient.Header;

public class PostList extends ArrayAdapter<Post> {
    private Activity context;
    List<Post> posts;
    DatabaseReference databaseComments;

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
        TextView textViewCommentsNo = (TextView) listViewItem.findViewById(R.id.textViewCommentsNo);
        ImageView PostAvatar = (ImageView) listViewItem.findViewById(R.id.image);
        TextView TripDetail = (TextView) listViewItem.findViewById(R.id.TripDetail);
        Post post = posts.get(position);
        String type = post.type;


        if (type.equals("0")) {
            listViewItem.setBackgroundColor(Color.parseColor("#e4e4e4"));
            textViewText.setTypeface(Typeface.create("Quranic fonts.ttf", Typeface.BOLD_ITALIC));
        } else if (type.equals("1")) {
            listViewItem.setBackgroundColor(Color.WHITE);
            //listViewItem.setBackgroundResource(R.drawable.listview_item_border);
        }
        textViewName.setText(post.author.username);
        textViewText.setText(post.text);

        //type = 0 => driver
        //type = 1 => user
        if (post.type.contains("1") && post.travel_id > 0) {
            TripDetail.setVisibility(View.VISIBLE);
        } else {
            TripDetail.setVisibility(View.GONE);
        }

        databaseComments = FirebaseDatabase.getInstance().getReference("posts").child(post.id).child("Comments");
        databaseComments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                textViewCommentsNo.setText(context.getResources().getString(R.string.CommentsNoSt) + " (" + dataSnapshot.getChildrenCount() + ")");
                Log.i("ibrahim was here", String.valueOf(context.getResources().getString(R.string.CommentsNoSt)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (post.timestamp != null) {
            Date date = new Date(post.timestamp);
            SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String stringDate = DateFor.format(date);
            textViewDate.setText(stringDate.toString());
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(post.author.uid);
        databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
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

        TripDetail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GetRides(String.valueOf(post.travel_id));
            }
        });
        return listViewItem;
    }

    private void GetRides(String ride_id) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_SEARCHUSER1, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("success", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                    }.getType());

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", list.get(0));
                    AcceptRideFragment detailFragment = new AcceptRideFragment();
                    detailFragment.setArguments(bundle);

                    ((HomeActivity) getContext()).changeFragment(detailFragment, "Passenger Information");

                } catch (JSONException e) {
                    Log.e("Get Data", e.getMessage());
                }
            }
        });
    }
}