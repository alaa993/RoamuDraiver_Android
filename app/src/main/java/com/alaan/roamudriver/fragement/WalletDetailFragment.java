package com.alaan.roamudriver.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.adapter.WalletAdapter;
import com.alaan.roamudriver.custom.GPSTracker;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.pojo.Wallet;
import com.alaan.roamudriver.session.SessionManager;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class WalletDetailFragment extends Fragment {

    TextView txt_sender, txt_reciver, txt_amount, txt_transaction_type, txt_status, txt_datetime;
    private View view;
    Wallet wallet;
    Bundle bundle;

    public WalletDetailFragment() {
        // Required empty public constructor
    }

    public static WalletDetailFragment newInstance(String param1, String param2) {
        WalletDetailFragment fragment = new WalletDetailFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wallet_detail, container, false);
        bundle = getArguments();
        if (bundle != null) {
            wallet = (Wallet) bundle.getSerializable("data");
            BindView();
        }
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.WalletItem3));
        return view;
    }

    public void BindView() {
        txt_sender = (TextView) view.findViewById(R.id.txt_sender);
        txt_reciver = (TextView) view.findViewById(R.id.txt_reciver);
        txt_amount = (TextView) view.findViewById(R.id.txt_amount);
        txt_transaction_type = (TextView) view.findViewById(R.id.txt_transaction_type);
        txt_status = (TextView) view.findViewById(R.id.txt_status);
        txt_datetime = (TextView) view.findViewById(R.id.txt_datetime);
        if (bundle != null && wallet.id_transaction != null) {
            getInfoTransactionsRequest();
        }
    }

    public void getInfoTransactionsRequest() {
        final RequestParams params = new RequestParams();
        params.put("transaction_id", wallet.id_transaction);
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.getInfoTransactions, params, new JsonHttpResponseHandler() {
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
                        if (response.has("data") && response.getJSONArray("data").length() > 0) {
                            txt_sender.setText(list.get(0).name_send);
                            txt_reciver.setText(list.get(0).name_recive);
                            txt_amount.setText(list.get(0).amount);
                            txt_transaction_type.setText(list.get(0).typeTName);
                            txt_status.setText(list.get(0).statusName);
                            txt_datetime.setText(list.get(0).transaction_date);
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