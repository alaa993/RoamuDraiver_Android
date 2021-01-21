package com.alaan.roamudriver.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.adapter.MyAcceptedRequestAdapter;
import com.alaan.roamudriver.custom.Utils;
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

public class MyAcceptedRequestFragment extends Fragment implements BackFragment {
    private View view;
    RecyclerView recyclerView;
    String userid = "";
    String key = "";
    TextView txt_error;
    String status;
    String status_id;

    SwipeRefreshLayout swipeRefreshLayout;

    public MyAcceptedRequestFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_accepted_request, container, false);
        bindView();
        return view;
    }

    public void bindView() {
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.accepted_request));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.MyAR_swipe_refresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.MyAR_recyclerview);
        txt_error = (TextView) view.findViewById(R.id.MyAR_txt_error);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        userid = SessionManager.getUserId();
        key = SessionManager.getKEY();
        Bundle bundle = getArguments();
        if (bundle != null) {
            status = bundle.getString("status");
            status_id = bundle.getString("status_id");
        }
        if (Utils.haveNetworkConnection(getActivity())) {
            if (status_id == "-1") {
                getAcceptedRequest(status_id, status, key);
            } else {
                getAcceptedRequest(userid, status, key);
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void getAcceptedRequest(String id, String status, String key) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("status", status);
        params.put("utype", "1");
        Server.setHeader(key);
        Server.get(Server.GET_REQUEST1, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();

                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());
                        Log.e("success", response.toString());
                        if (response.has("data") && response.getJSONArray("data").length() == 0) {
                            txt_error.setVisibility(View.VISIBLE);
                            MyAcceptedRequestAdapter myAceptedRequestAdapter = new MyAcceptedRequestAdapter(list);
                            recyclerView.setAdapter(myAceptedRequestAdapter);
                            myAceptedRequestAdapter.notifyDataSetChanged();

                        } else {
                            txt_error.setVisibility(View.GONE);
                            MyAcceptedRequestAdapter myAceptedRequestAdapter = new MyAcceptedRequestAdapter(list);
                            recyclerView.setAdapter(myAceptedRequestAdapter);
                            myAceptedRequestAdapter.notifyDataSetChanged();
                        }

                    } else {

                        Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {

                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private String setTitle(String s) {
        String title = null;
        switch (s) {
            case "ACCEPTED":
                title = getString(R.string.accepted_request);
                break;
            case "PENDING":
                title = getString(R.string.pending_request);
                break;
            case "Search":
                title = getString(R.string.search);
                break;
            case "CANCELLED":
                title = getString(R.string.cancelled_request);
                break;
            case "COMPLETED":
                title = getString(R.string.completed_request);
                break;
            case "All":
                title = getString(R.string.All_travel);
                break;
        }
        return title;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public int getBackPriority() {
        return 0;
    }
}