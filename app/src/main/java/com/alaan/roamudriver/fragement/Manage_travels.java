package com.alaan.roamudriver.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.adapter.DriverRidesAdapter;
import com.alaan.roamudriver.adapter.Group_membar_Adapter;
import com.alaan.roamudriver.pojo.DriverRides;
import com.alaan.roamudriver.pojo.Driver_groups_model;
import com.alaan.roamudriver.pojo.Group_membar;
import com.alaan.roamudriver.session.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Driver;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Manage_travels#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Manage_travels extends Fragment {
    View rootView;



    public Manage_travels() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Manage_travels.
     */
    // TODO: Rename and change types and number of parameters
    public static Manage_travels newInstance(String param1, String param2) {
        Manage_travels fragment = new Manage_travels();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetRides();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView =  inflater.inflate(R.layout.fragment_manage_travels, container, false);

        return rootView;

        // Inflate the layout for this fragment
    }
    private void GetRides() {
        // get driver from db by id
        RequestParams params = new RequestParams();
        params.put("driver_id", SessionManager.getUserId());
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_RIDES, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //log.e("success", response.toString());
                try {

                       Gson gson = new GsonBuilder().create();
                    List<DriverRides> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<DriverRides>>() {
                    }.getType());
                    RecyclerView recyclerView = (RecyclerView)  rootView.findViewById(R.id.recyclerview_driver_rides);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    DriverRidesAdapter driverRidesAdapter = new DriverRidesAdapter(list);
                    recyclerView.setAdapter(driverRidesAdapter);
                    driverRidesAdapter.notifyDataSetChanged();





                } catch (JSONException e) {
                    //log.e("Get Data", e.getMessage());

                }
            }
        });

    }

}
