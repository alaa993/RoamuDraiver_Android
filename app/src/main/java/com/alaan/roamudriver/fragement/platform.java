package com.alaan.roamudriver.fragement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.acitivities.PostActivity;
import com.alaan.roamudriver.pojo.Post;

import com.alaan.roamudriver.pojo.PostList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.skoumal.fragmentback.BackFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class platform extends Fragment implements BackFragment {

    View view;
    ListView listViewPosts;
    List<Post> posts;
    DatabaseReference databasePosts;
    ValueEventListener listener;

    public platform() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        posts = new ArrayList<>();
        databasePosts = FirebaseDatabase.getInstance().getReference("posts");
        view = inflater.inflate(R.layout.fragment_platform, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.platform));
        BindView();

        listViewPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Post post = posts.get(i);
                Bundle bundle = new Bundle();
                bundle.putString("Post_id", post.id);
                bundle.putString("request_type", "public");
                PostFragment postfragment = new PostFragment();
                postfragment.setArguments(bundle);
                changeFragment(postfragment, "Requests");
            }
        });
        return view;
    }
    public void BindView() {
        listViewPosts = (ListView) view.findViewById(R.id.listViewPosts);
        listViewPosts.setBackgroundColor(Color.WHITE);
    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack(null);
                }
            }, 50);
        } catch (Exception e) {
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        listener = databasePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post Post = postSnapshot.getValue(Post.class);
                    Post.id = postSnapshot.getKey();
                    if (Post.privacy.contains("1")) {
                        posts.add(Post);
                    }
                }
                Collections.reverse(posts);
                if(!posts.isEmpty()) {
                    PostList postAdapter = new PostList(platform.this.getActivity(), posts);
                    postAdapter.notifyDataSetChanged();
                    listViewPosts.setAdapter(postAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onBackPressed() {
        getActivity().getSupportFragmentManager().popBackStack();
        return false;
    }
    @Override
    public int getBackPriority() {
        return 0;
    }
    @Override
    public void onPause() {
        super.onPause();
        databasePosts.removeEventListener(listener);
    }
}