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
import com.alaan.roamudriver.adapter.SearchUserAdapter;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.session.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;


public class fragment_searched_user extends Fragment {
    View rootView;
    RecyclerView recyclerView;

    public fragment_searched_user() {
        // Required empty public constructor
    }

    public static fragment_searched_user newInstance(String param1, String param2) {
        fragment_searched_user fragment = new fragment_searched_user();
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
        rootView = inflater.inflate(R.layout.fragment_searched_user, container, false);


        final Bundle bundle = this.getArguments();
        if (bundle != null) {

            String search_pich_location = bundle.getString("search_pich_location");
            String search_drop_location = bundle.getString("search_drop_location");

            String date_time_value = bundle.getString("date_time_value");
//            String passanger_value = bundle.getString("passanger_value");
            //  Toast.makeText(getContext(), ""+smoke_value+date_time_value+passanger_value, Toast.LENGTH_SHORT).show();
            GetRides(search_pich_location, search_drop_location, "0", "", date_time_value, "");

        }


        return rootView;

    }

    private void GetRides(String pick, String drop, String smoke, String passn, String Date, String car_type) {
        RequestParams params = new RequestParams();
        params.put("id", SessionManager.getUserId());
        params.put("pick_add", pick);
        params.put("drop_add", drop);
        params.put("smoke", smoke);
        params.put("passn", passn);
        params.put("date", Date);
        //log.i("ibrahim","car_type");
        //log.i("ibrahim",SessionManager.getCarType());
        params.put("car_type", SessionManager.getCarType());
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_SEARCHUSER, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //log.e("success", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                    }.getType());
                    RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_searchUser);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(list);
                    recyclerView.setAdapter(searchUserAdapter);
                    searchUserAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    //log.e("Get Data", e.getMessage());

                }
            }
        });

    }

}
