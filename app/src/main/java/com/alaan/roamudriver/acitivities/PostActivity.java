package com.alaan.roamudriver.acitivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.alaan.roamudriver.pojo.Comment;
import com.alaan.roamudriver.adapter.CommentAdapter;
import com.alaan.roamudriver.pojo.Post;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.loopj.android.http.AsyncHttpClient.log;

public class PostActivity extends AppCompatActivity {

    //View view;
    ListView listViewPosts;
    EditText inputEditComment;
    TextView AddCommentBTN;

    //a list to store all the artist from firebase database
    List<Comment> comments;
    DatabaseReference databasePost;
    DatabaseReference databaseComments;
    ValueEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Intent intent = getIntent();
        comments = new ArrayList<>();
        databasePost = FirebaseDatabase.getInstance().getReference("posts").child(intent.getStringExtra("Post_id"));
        databaseComments = FirebaseDatabase.getInstance().getReference("posts").child(intent.getStringExtra("Post_id")).child("Comments");
        listViewPosts = (ListView) findViewById(R.id.Post_listViewComments);
        //log.i("tag", "success by ibrahim");
        //log.i("tag", intent.getStringExtra("Post_id"));
        ////log.i("tag",databaseComments.child("Comment1").child("username"));

        inputEditComment = findViewById(R.id.input_Comment);
        AddCommentBTN = findViewById(R.id.AddCommentBTN);
        AddCommentBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (inputEditComment.getText().toString().matches("")) {
                    Toast.makeText(PostActivity.this, getString(R.string.Post_Empty), Toast.LENGTH_LONG).show();
                } else {
                    SavePost(intent);
                }
            }
        });

    }

    public void SavePost(Intent intent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());

        databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UserName = dataSnapshot.child("username").getValue(String.class);
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                //log.i("tag", "success by ibrahim");
                //log.i("tag", UserName);
                // Firebase code here
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").child(intent.getStringExtra("Post_id")).child("Comments").push();
                Map<String, Object> author = new HashMap<>();
                author.put("uid", user.getUid());
                author.put("username", UserName);
                author.put("photoURL", photoURL);

                Map<String, Object> userObject = new HashMap<>();
                userObject.put("author", author);
                userObject.put("text", inputEditComment.getText().toString());
                userObject.put("timestamp", ServerValue.TIMESTAMP);
                //type = 0 => driver
                //type = 1 => user
                userObject.put("type", "0");
                userObject.put("privacy", "1");
                userObject.put("travel_id", 0);
                databaseRef.setValue(userObject);
                inputEditComment.getText().clear();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        attaching value event listener
        databasePost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                TextView textViewName = (TextView) findViewById(R.id.Post_textViewName);
                TextView textViewText = (TextView) findViewById(R.id.Post_textViewText);
                TextView textViewDate = (TextView) findViewById(R.id.Post_textViewDate);
                ImageView PostAvatar = (ImageView) findViewById(R.id.Post_image);
                textViewName.setText(post.author.username);
                textViewText.setText(post.text);

                if (post.timestamp != null) {
                    Date date = new Date(post.timestamp);
                    SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String stringDate = DateFor.format(date);
                    textViewDate.setText(stringDate.toString());
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                String uid = user.getUid();
                DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(post.author.uid);
                databaseRefID.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
                        if (photoURL != null) {
//                    Glide.with(PostAdapter.this.getContext()).load(post.author.photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                            Glide.with(PostActivity.this).load(photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });

        listener = databaseComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                comments.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Comment Comment = postSnapshot.getValue(Comment.class);
                    //adding artist to the list
                    comments.add(Comment);
                }

                //creating adapter
                CommentAdapter commentAdapter = new CommentAdapter(PostActivity.this, comments);
                //attaching adapter to the listview
                listViewPosts.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseComments.removeEventListener(listener);
    }
}