package com.alaan.roamudriver.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.adapter.NoteAdapter;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.session.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class NoteFragment extends Fragment implements BackFragment {

    View view;
    RecyclerView recyclerView;
    ListView listViewPosts;
    String travel_id;

    public NoteFragment() {
        // Required empty public constructor
    }

    public static NoteFragment newInstance(String param1, String param2) {
        NoteFragment fragment = new NoteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_note, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        Bundle bundle = getArguments();
        if (bundle != null) {
            travel_id = bundle.getString("travel_id");
            //log.i("ibraim", "NoteFragment");
            //log.i("ibraim", travel_id);
            //log.i("ibraim", SessionManager.getKEY());
            getNoteRequest(travel_id);
        }
        return view;
    }

    public void getNoteRequest(String travel_id) {
        //log.i("ibrahim", "getAcceptedRequest");
        final RequestParams params = new RequestParams();
        params.put("travel_id", travel_id);
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_Rides_Notes, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());
                        //log.i("ibrahim_list", list.toString());
                        //log.i("ibrahim_list", response.getString("data"));
                        //log.i("ibrahim_list", "-----------");
                        if (response.has("data") && response.getJSONArray("data").length() == 0) {
//                            txt_error.setVisibility(View.VISIBLE);
                            NoteAdapter acceptedRequestAdapter = new NoteAdapter(list);
                            recyclerView.setAdapter(acceptedRequestAdapter);
                            acceptedRequestAdapter.notifyDataSetChanged();
                        } else {
//                            txt_error.setVisibility(View.GONE);
                            NoteAdapter acceptedRequestAdapter = new NoteAdapter(list);
                            recyclerView.setAdapter(acceptedRequestAdapter);
                            acceptedRequestAdapter.notifyDataSetChanged();
                        }
                    } else {
//                        Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
//                    Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
//                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        this.startActivity(new Intent(getContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

        return false;
    }

    @Override
    public int getBackPriority() {
        return 0;
    }
}