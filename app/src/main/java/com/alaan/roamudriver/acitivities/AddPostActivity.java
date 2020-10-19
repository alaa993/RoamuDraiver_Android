package com.alaan.roamudriver.acitivities;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.loopj.android.http.AsyncHttpClient.log;

public class AddPostActivity extends AppCompatActivity {

    TextInputEditText inputEditPost;
    Button AddPostBTN;
//    String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        inputEditPost = findViewById(R.id.input_Post);
        AddPostBTN = findViewById(R.id.AddPostBTN);
        AddPostBTN.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (inputEditPost.getText().toString().matches(""))
                {
                    Toast.makeText(AddPostActivity.this, getString(R.string.Post_Empty), Toast.LENGTH_LONG).show();
                }
                else
                {
                    SavePost();
                }
            }
        });
    }



    public void SavePost() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseRefID = FirebaseDatabase.getInstance().getReference("users/profile").child(uid.toString());

        databaseRefID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String UserName = dataSnapshot.child("username").getValue(String.class);
                String photoURL = dataSnapshot.child("photoURL").getValue(String.class);
//                log.i("tag","success by ibrahim");
//                log.i("tag", UserName);
                // Firebase code here
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").push();
                Map<String,Object> author = new HashMap<>();
                author.put("uid" , user.getUid());
                author.put("username" , UserName);
                author.put("photoURL" , photoURL);

                Map<String,Object> userObject = new HashMap<>();
                userObject.put("author", author);
                userObject.put("text", inputEditPost.getText().toString());
                userObject.put("type", "1");
                userObject.put("timestamp", ServerValue.TIMESTAMP);
                userObject.put("privacy" , 1);

                databaseRef.setValue(userObject);
                inputEditPost.getText().clear();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }
}