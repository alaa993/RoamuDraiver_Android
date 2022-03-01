package com.alaan.roamudriver.fragement;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.pojo.Comment;
import com.alaan.roamudriver.adapter.CommentAdapter;
import com.alaan.roamudriver.pojo.Post;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import afu.org.checkerframework.checker.nullness.qual.NonNull;

import static com.loopj.android.http.AsyncHttpClient.log;

public class PostFragment extends Fragment {
    View view;
    ListView listViewPosts;
    EditText inputEditComment;
    TextView AddCommentBTN;
    ValueEventListener listener;
    public ImageView more;


    //a list to store all the artist from firebase database
    List<Comment> comments;
    DatabaseReference databasePost;
    DatabaseReference databaseComments;
    RelativeLayout PostRL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //log.i("ibrahim from post1", "-----------------");

        view = inflater.inflate(R.layout.fragment_post, container, false);
        //log.i("ibrahim from post2", "-----------------");

        view.setBackgroundColor(Color.WHITE);
        PostRL = (RelativeLayout) view.findViewById(R.id.activity_post_post);

        Bundle bundle = getArguments();
        String Post_id_bundle = bundle.getString("Post_id");
        if (bundle.getString("request_type").contains("private")) {
            PostRL.setVisibility(View.GONE);
            databasePost = FirebaseDatabase.getInstance().getReference("private_posts").child(Post_id_bundle);
            databaseComments = FirebaseDatabase.getInstance().getReference("private_posts").child(Post_id_bundle).child("Comments");

        } else if (bundle.getString("request_type").contains("public")) {
            PostRL.setVisibility(View.VISIBLE);
            databasePost = FirebaseDatabase.getInstance().getReference("posts").child(Post_id_bundle);
            databaseComments = FirebaseDatabase.getInstance().getReference("posts").child(Post_id_bundle).child("Comments");

        }

        comments = new ArrayList<>();
        listViewPosts = (ListView) view.findViewById(R.id.Post_listViewComments);
        //log.i("tag", "success by ibrahim");
        //log.i("tag", Post_id_bundle);
        ////log.i("tag",databaseComments.child("Comment1").child("username"));

        inputEditComment = view.findViewById(R.id.input_Comment);
        AddCommentBTN = view.findViewById(R.id.AddCommentBTN);
        AddCommentBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (inputEditComment.getText().toString().matches("")) {
                    Toast.makeText(getActivity(), getString(R.string.Post_Empty), Toast.LENGTH_LONG).show();
                } else {
                    SavePost(bundle);
                }
            }
        });

        more = view.findViewById(R.id.more);

        return view;
    }

    public void SavePost(Bundle bundle) {
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
                if (bundle.getString("request_type").contains("private")) {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("private_posts").child(bundle.getString("Post_id")).child("Comments").push();
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
                    databaseRef.setValue(userObject);
                    inputEditComment.getText().clear();
                } else if (bundle.getString("request_type").contains("public")) {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").child(bundle.getString("Post_id")).child("Comments").push();
                    Map<String, Object> author = new HashMap<>();
                    author.put("uid", user.getUid());
                    author.put("username", UserName);
                    author.put("photoURL", photoURL);

                    Map<String, Object> userObject = new HashMap<>();
                    userObject.put("author", author);
                    userObject.put("text", inputEditComment.getText().toString());
                    userObject.put("timestamp", ServerValue.TIMESTAMP);
                    userObject.put("type", "1");
                    databaseRef.setValue(userObject);
                    inputEditComment.getText().clear();
                }
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
        databasePost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                post.id = dataSnapshot.getKey();
                TextView textViewName = (TextView) view.findViewById(R.id.Post_textViewName);
                TextView textViewText = (TextView) view.findViewById(R.id.Post_textViewText);
                TextView textViewDate = (TextView) view.findViewById(R.id.Post_textViewDate);
                ImageView PostAvatar = (ImageView) view.findViewById(R.id.Post_image);
//                textViewName.setText(post.author.username);
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
                        String UserName = dataSnapshot.child("username").getValue(String.class);
                        textViewName.setText(UserName);
                        if (photoURL != null) {
//                    Glide.with(PostAdapter.this.getContext()).load(post.author.photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                            Glide.with(PostFragment.this).load(photoURL).apply(new RequestOptions().error(R.drawable.images)).into(PostAvatar);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                });

                if (post.author.uid.endsWith(user.getUid()) && post.travel_id == 0) {
                    more.setVisibility(View.VISIBLE);
                    more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (post.author.uid.endsWith(user.getUid()) && post.travel_id == 0) {
                                more.setVisibility(View.VISIBLE);
                                PopupMenu popup = new PopupMenu(getContext(), more);
                                popup.inflate(R.menu.post_menu);
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        switch (item.getItemId()) {
                                            case R.id.nav_edit:
                                                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(getContext());
                                                LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                                View mView = li.inflate(R.layout.dialog_update_post_layout, null);
                                                final EditText ET_DUOL = (EditText) mView.findViewById(R.id.ET_DUOL);
                                                ET_DUOL.setText(post.text);
                                                Button btnSubmit_DUOL = (Button) mView.findViewById(R.id.btnSubmit_DUOL);
                                                Button btnCancel_DUOL = (Button) mView.findViewById(R.id.btnCancel_DUOL);
                                                mBuilder.setView(mView);
                                                final android.app.AlertDialog dialog = mBuilder.create();
                                                dialog.show();

                                                btnSubmit_DUOL.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        //log.i("ibrahim", ET_DUOL.getText().toString());
                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put("text", ET_DUOL.getText().toString());
                                                        //log.i("ibrahim_post_id", post.id);
                                                        FirebaseDatabase.getInstance().getReference("posts").child(post.id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getContext(), getContext().getString(R.string.Post_updated_successfully), Toast.LENGTH_SHORT).show();
                                                                    dialog.dismiss();
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                    }
                                                });
                                                btnCancel_DUOL.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                return true;

                                            case R.id.nav_delete:
                                                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                                alertDialog.setTitle(getContext().getString(R.string.do_you_want_to_delete));
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(final DialogInterface dialog, int which) {
                                                        //log.i("ibrahim_post_id", post.id);
                                                        FirebaseDatabase.getInstance().getReference("posts").child(post.id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getContext(), getContext().getString(R.string.Post_deleted_successfully), Toast.LENGTH_SHORT).show();
                                                                    dialog.dismiss();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

                                                alertDialog.show();
                                                return true;
                                            default:
                                                return false;
                                        }
                                    }
                                });
                                popup.show();
                            }
                        }
                    });
                }
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
                CommentAdapter commentAdapter = new CommentAdapter(getActivity(), comments);
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
