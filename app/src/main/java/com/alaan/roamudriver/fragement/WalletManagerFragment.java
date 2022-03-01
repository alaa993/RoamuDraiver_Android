package com.alaan.roamudriver.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
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

public class WalletManagerFragment extends Fragment {

    View rootView;
    TextView accountsChargeCell, sendMoneyCell, sendMoneyRequestCell, transactionCell;
    TextView txt_wallet_id, txt_balance;

    public WalletManagerFragment() {
        // Required empty public constructor
    }

    public static WalletManagerFragment newInstance(String param1, String param2) {
        WalletManagerFragment fragment = new WalletManagerFragment();
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
        rootView = inflater.inflate(R.layout.fragment_wallet_manager, container, false);
        accountsChargeCell = (TextView) rootView.findViewById(R.id.accountsChargeCell);
        sendMoneyCell = (TextView) rootView.findViewById(R.id.sendMoneyCell);
        sendMoneyRequestCell = (TextView) rootView.findViewById(R.id.sendMoneyRequestCell);
        transactionCell = (TextView) rootView.findViewById(R.id.transactionCell);
        txt_wallet_id = (TextView) rootView.findViewById(R.id.txt_wallet_id);
        txt_balance = (TextView) rootView.findViewById(R.id.txt_balance);

        getPersonalWalletRequest();

        accountsChargeCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountsChargeFragment accountsChargeFragment = new AccountsChargeFragment();
                ((HomeActivity) getContext()).changeFragment(accountsChargeFragment, "accountsChargeFragment");
            }
        });
        sendMoneyCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMoneyFragment sendMoneyFragment = new SendMoneyFragment();
                ((HomeActivity) getContext()).changeFragment(sendMoneyFragment, "sendMoneyFragment");
            }
        });
        sendMoneyRequestCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestMoneyFragment requestMoneyFragment = new RequestMoneyFragment();
                ((HomeActivity) getContext()).changeFragment(requestMoneyFragment, "sendMoneyRequestCell");
            }
        });
        transactionCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionListFragment transactionListFragment = new TransactionListFragment();
                ((HomeActivity) getContext()).changeFragment(transactionListFragment, "transactionListFragment");
            }
        });

        return rootView;
    }

    public void getPersonalWalletRequest() {
        //log.i("ibrahim", "getAcceptedRequest");
        final RequestParams params = new RequestParams();
        params.put("user_id", SessionManager.getUserId());
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.getPersonalWallet, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //log.i("response",response.toString());
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<Wallet> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<Wallet>>() {
                        }.getType());
                        if (response.has("data") && response.getJSONArray("data").length() > 0) {
                            txt_wallet_id.setText(list.get(0).id);
                            txt_balance.setText(list.get(0).balance);
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