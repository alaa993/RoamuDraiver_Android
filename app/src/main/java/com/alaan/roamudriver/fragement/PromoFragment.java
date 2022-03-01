package com.alaan.roamudriver.fragement;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.adapter.PromoAdapter;
import com.alaan.roamudriver.custom.CheckConnection;
import com.alaan.roamudriver.custom.SetCustomFont;
import com.alaan.roamudriver.pojo.promopojo;
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

public class PromoFragment extends Fragment {
    private View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    String userid = "";
    String key = "";

    TextView txt_error;
    String status;

    public PromoFragment() {
        // Required empty public constructor
    }

    public static PromoFragment newInstance(String param1, String param2) {
        PromoFragment fragment = new PromoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void bindView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.rvPromo);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_promo);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        SetCustomFont setCustomFont = new SetCustomFont();
        setCustomFont.overrideFonts(getActivity(), view);
        key = SessionManager.getKEY();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_promo, container, false);
        bindView();
        if (CheckConnection.haveNetworkConnection(getActivity())) {
            showPromotion(key);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }

        return view;

    }

    private void showPromotion(String key) {
        final RequestParams params = new RequestParams();
        params.put("user_id", SessionManager.getUserId());
        Server.setHeader(key);
        Server.get(Server.getPomoCodes, params, new JsonHttpResponseHandler() {
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
                        List<promopojo> list2 = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<promopojo>>() {
                        }.getType());
                        //log.e("data-customer", list2.size() + " "+ list2.get(1).getPromo_code());
                        if (response.has("data") && response.getJSONArray("data").length() == 0) {
                            txt_error.setVisibility(View.VISIBLE);
                        } else {
                            PromoAdapter promoAdapter = new PromoAdapter(list2);
                            recyclerView.setAdapter(promoAdapter);
                            promoAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getContext(), "" + errorResponse, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void onButtonPressed(Uri uri) {
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}