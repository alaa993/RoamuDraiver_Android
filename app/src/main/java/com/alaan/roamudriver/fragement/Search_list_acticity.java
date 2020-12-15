package com.alaan.roamudriver.fragement;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.adapter.SearchUserAdapter;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.session.SessionManager;
import com.fxn.stash.Stash;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Search_list_acticity extends AppCompatActivity {
    RecyclerView recyclerView;
    private String unit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list_acticity);
        Stash.init(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String search_pich_location = bundle.getString("search_pich_location");
            String search_drop_location = bundle.getString("search_drop_location");
            String smoke_value = bundle.getString("smoke_value");
            String date_time_value = bundle.getString("date_time_value");
            String passanger_value = bundle.getString("passanger_value");
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview_searchUser);
            GetRides(search_pich_location, search_drop_location, smoke_value, passanger_value, date_time_value);
        } else {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview_searchUser);
            // send null value if all params is null
            GetRides("", "", "", "", "");
        }
    }
    private void GetRides(String pick, String drop, String smoke, String passn, String Date) {
        RequestParams params = new RequestParams();
        params.put("travel_id", "-1");
        params.put("ride_id", "-1");
        params.put("pickup_address", pick);
        params.put("drop_address", drop);
        params.put("date", Date);
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

                    Log.i("ibrahim_list","list");
                    Log.i("ibrahim_list",list.toString());

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(list);
                    recyclerView.setAdapter(searchUserAdapter);
                    searchUserAdapter.notifyDataSetChanged();
                    unit = response.getJSONObject("fair").getString("unit");
                    Stash.put("UNIT_TAG", unit);
                    Log.e("Get Data", unit);
                } catch (JSONException e) {
                    Log.e("Get Data", e.getMessage());
                }
            }
        });
    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_list, fragment, fragmenttag);
                    fragmentTransaction.commit();
                }
            }, 50);
        } catch (Exception e) {
        }
    }

    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            // finish();
            startActivity(new Intent((getApplicationContext()), Search_list_acticity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //  super.onBackPressed();
        }
    }

    public void showframe(int stauts) {

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (stauts == 0) {
                        recyclerView.setVisibility(View.GONE);
                    } else if (stauts == 1) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }, 50);
        } catch (Exception e) {

        }

    }


}
