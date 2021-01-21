package com.alaan.roamudriver.fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.alaan.roamudriver.custom.CheckConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.adapter.AcceptedRequestAdapter;
import com.alaan.roamudriver.custom.Utils;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by android on 10/3/17.
 */

public class AcceptedRequestFragment extends Fragment implements BackFragment, AdapterView.OnItemSelectedListener {
    private View view;
    RecyclerView recyclerView;
    String userid = "";
    String key = "";
    TextView txt_error;
    String status;
    String status_id;

    SwipeRefreshLayout swipeRefreshLayout;

    String[] status_val_arr = {"All","PENDING", "ACCEPTED", "COMPLETED", "CANCELLED"};
    String[] status_arr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.accepted_request_fragment, container, false);
        bindView();
        return view;
    }

    public void bindView() {
        status_arr = new String[]{
                getString(R.string.All_travel),
                getString(R.string.pending_request), getString(R.string.accepted_request),
                getString(R.string.completed_request), getString(R.string.cancelled_request)};
//        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.accepted_request));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        Spinner droplist = (Spinner) view.findViewById(R.id.arf_simpleSpinner);
        droplist.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        ArrayAdapter data = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, status_arr);
        droplist.setAdapter(data);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        txt_error = (TextView) view.findViewById(R.id.txt_error);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        userid = SessionManager.getUserId();
        key = SessionManager.getKEY();

//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            status = bundle.getString("status");
//            status_id = bundle.getString("status_id");
//
//            ((HomeActivity) getActivity()).fontToTitleBar(setTitle(status));
//        }
//        if (Utils.haveNetworkConnection(getActivity())) {
//            if (status_id == "-1") {
//                getAcceptedRequest(status_id, status, key);
//            } else {
//                getAcceptedRequest(userid, status, key);
//            }
//        } else {
//            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
//        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (CheckConnection.haveNetworkConnection(getActivity())) {
            ((HomeActivity) getActivity()).fontToTitleBar(setTitle(status_val_arr[i]));
            getAcceptedRequest(userid, status_val_arr[i], key);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                            AcceptedRequestAdapter acceptedRequestAdapter = new AcceptedRequestAdapter(list);
                            recyclerView.setAdapter(acceptedRequestAdapter);
                            acceptedRequestAdapter.notifyDataSetChanged();

                        } else {
                            txt_error.setVisibility(View.GONE);
                            AcceptedRequestAdapter acceptedRequestAdapter = new AcceptedRequestAdapter(list);
                            recyclerView.setAdapter(acceptedRequestAdapter);
                            acceptedRequestAdapter.notifyDataSetChanged();
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
