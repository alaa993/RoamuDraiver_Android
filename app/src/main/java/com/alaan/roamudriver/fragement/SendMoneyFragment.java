package com.alaan.roamudriver.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import com.google.firebase.messaging.FirebaseMessagingService;

public class SendMoneyFragment extends Fragment {
    View rootView;
    EditText edtPhoneNumber;
    EditText edtAmount;
    Button apply_button;

    public SendMoneyFragment() {
        // Required empty public constructor
    }

    public static SendMoneyFragment newInstance(String param1, String param2) {
        SendMoneyFragment fragment = new SendMoneyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_send_money, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.WalletItem1));
        edtPhoneNumber = rootView.findViewById(R.id.edtPhoneNumber);
        edtAmount = rootView.findViewById(R.id.edtAmount);
        apply_button = (Button) rootView.findViewById(R.id.apply_button);

        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMoneyToWallet(SessionManager.getUserId(), edtPhoneNumber.getText().toString(), edtAmount.getText().toString());
            }
        });
        return rootView;
    }

    public void SendMoneyToWallet(String wallet_id, String wallet_id2, String amount) {
        RequestParams params = new RequestParams();
        params.put("wallet_id", wallet_id);
        params.put("wallet_id2", wallet_id2);
        params.put("amount", amount);
        params.put("transaction_type_id", "2");
        params.put("transaction_type_id2", "1");
        params.put("status_type_id", "2");

        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        String Url = "";
        Url = Server.addBalanceUserToWallets;
        Server.get(Url, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //log.i("response", response.toString());
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        String data = response.getString("message");
                        if (data.contains("transactionSuccess")) {
                            String resourceAppStatusString = "message_".concat(data);
                            //log.i("resourceAppStatusString", resourceAppStatusString);
                            int messageId = getResourceId(resourceAppStatusString, "string", "com.alaan.roamudriver");
                            String message = getString(messageId);

                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
                        }
                        edtPhoneNumber.setText("");
                        edtAmount.setText("");
                    } else {
                        Toast.makeText(getActivity(), R.string.sonething_went_wrong, Toast.LENGTH_LONG).show();
                        edtPhoneNumber.setText("");
                        edtAmount.setText("");
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private int getResourceId(String pVariableName, String pResourceName, String pPackageName) {
        try {
            return getResources().getIdentifier(pVariableName, pResourceName, pPackageName);
        } catch (NullPointerException e) {
            System.err.println("Null pointer exception");
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}