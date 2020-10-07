package com.alaan.roamudriver.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link platform#newInstance} factory method to
 * create an instance of this fragment.
 */
public class platform extends Fragment implements BackFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;
    ListView listViewPosts;


    //a list to store all the artist from firebase database
    List<Post> posts;
    DatabaseReference databasePosts;

    public platform() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment platform.
     */
    // TODO: Rename and change types and number of parameters
    public static platform newInstance(String param1, String param2) {
        platform fragment = new platform();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        //list to store artists
        posts = new ArrayList<>();
        databasePosts = FirebaseDatabase.getInstance().getReference("posts");
        view = inflater.inflate(R.layout.fragment_platform, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.platform));
        BindView();

        listViewPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the selected artist
                Post post = posts.get(i);

                //creating an intent
                Intent intent = new Intent(getActivity(), PostActivity.class);

                //putting artist name and id to intent
                intent.putExtra("Post_id", post.id);
                //intent.putExtra(ARTIST_NAME, artist.getArtistName());

                //starting the activity with intent
                startActivity(intent);
            }
        });

        return view;
    }

    public void BindView() {
        listViewPosts = (ListView) view.findViewById(R.id.listViewPosts);

    }

    @Override
    public void onStart() {
        super.onStart();
        //attaching value event listener
        databasePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //clearing the previous artist list
                posts.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Post Post = postSnapshot.getValue(Post.class);

                    Post.id = postSnapshot.getKey();
                    //adding artist to the list
                    posts.add(Post);



                }
                Collections.reverse(posts);
                //creating adapter
                if(!posts.isEmpty()) {
                    PostList postAdapter = new PostList(platform.this.getActivity(), posts);
                    //attaching adapter to the listview
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


}