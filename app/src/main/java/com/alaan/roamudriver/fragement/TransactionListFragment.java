package com.alaan.roamudriver.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.adapter.NoteAdapter;
import com.alaan.roamudriver.adapter.WalletAdapter;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.pojo.Wallet;
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

public class TransactionListFragment extends Fragment {

    View rootView;
    RecyclerView recyclerView;
    ListView listViewTransactions;


    public TransactionListFragment() {
        // Required empty public constructor
    }

    public static TransactionListFragment newInstance(String param1, String param2) {
        TransactionListFragment fragment = new TransactionListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_transaction_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        getTransactionsRequest();
        return rootView;
    }

    public void getTransactionsRequest() {
        //log.i("ibrahim", "getAcceptedRequest");
        final RequestParams params = new RequestParams();
        params.put("wallet_id", SessionManager.getUserId());
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.getTransactionsList, params, new JsonHttpResponseHandler() {
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
                        List<Wallet> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<Wallet>>() {
                        }.getType());
                        if (response.has("data") && response.getJSONArray("data").length() == 0) {
                            WalletAdapter walletAdapter = new WalletAdapter(list);
                            recyclerView.setAdapter(walletAdapter);
                            walletAdapter.notifyDataSetChanged();
                        } else {
                            WalletAdapter walletAdapter = new WalletAdapter(list);
                            recyclerView.setAdapter(walletAdapter);
                            walletAdapter.notifyDataSetChanged();
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
}