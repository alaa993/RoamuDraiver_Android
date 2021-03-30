package com.alaan.roamudriver.fragement;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.adapter.AcceptedRequestAdapter;
import com.alaan.roamudriver.pojo.PassMembar;
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

import static com.loopj.android.http.AsyncHttpClient.log;

public class Gruop_managment extends Fragment implements AdapterView.OnItemSelectedListener {
    View rootView;
    PassMembar pojo;
    RecyclerView recyclerView;
    TextView txt_name,txt_mobile_number,txt_email,txt_country,txt_status,txt_vehicle;
    ImageView image;
    Spinner droplist;
    String[] status_arr={"PENDING","ACCEPTED","COMPLETED","CANCELLED","REQUESTED"};
    public Gruop_managment() {
        // Required empty public constructor
    }

    public static Gruop_managment newInstance(String param1, String param2) {
        Gruop_managment fragment = new Gruop_managment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        rootView = inflater.inflate(R.layout.fragment_gruop_managment, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.group_members));
        droplist = (Spinner) rootView.findViewById(R.id.simpleSpinner);
        droplist.setOnItemSelectedListener(this);
        ArrayAdapter data = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,status_arr);
        droplist.setAdapter(data);
        txt_name = (TextView) rootView.findViewById(R.id.txt_name);
        txt_mobile_number = (TextView) rootView.findViewById(R.id.txt_mobile_number);
        txt_email = (TextView) rootView.findViewById(R.id.txt_email);
        txt_country = (TextView) rootView.findViewById(R.id.txt_country);
        txt_status = (TextView) rootView.findViewById(R.id.txt_status);
        txt_vehicle = (TextView) rootView.findViewById(R.id.txt_vehicle);
        image = (ImageView) rootView.findViewById(R.id.fgm_img_call);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_status);

        Bundle bundle = getArguments();


        if (bundle != null) {
            pojo = (PassMembar) bundle.getSerializable("data");
            if (pojo != null) {
                txt_name.setText(pojo.getDriver_name());
                txt_email.setText(pojo.getDriver_email());
                txt_mobile_number.setText(pojo.getDriver_mobile());
                txt_country.setText(pojo.getDriver_country());
                if(pojo.getDriver_is_online().contains("1")){
                    txt_status.setText("Online");
                    txt_status.setTextColor(getResources().getColor(R.color.green));
                }
                else{
                    txt_status.setText("Offline");
                    txt_status.setTextColor(getResources().getColor(R.color.red));
                }
                txt_vehicle.setText(pojo.getDriver_vehicle_no());
                log.e("ss",""+pojo.getDriver_mobile());

            }
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pojo.getDriver_mobile() != null)
                {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + pojo.getDriver_mobile()));
                    getContext().startActivity(intent);
                }
            }
        });

        return rootView;


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        Toast.makeText(getContext(), status_arr[i], Toast.LENGTH_LONG).show();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        getAcceptedRequest(String.valueOf(pojo.getDriver_id()),status_arr[i], SessionManager.getKEY());

    }

    public void getAcceptedRequest(String id, String status, String key) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        params.put("status", status);
        params.put("utype", "1");
        Server.setHeader(key);
        Server.get(Server.GET_REQUEST, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
               }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();

                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<PendingRequestPojo> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                        }.getType());
                        if (list.size() == 0) {
                           // txt_error.setVisibility(View.VISIBLE);

                        } else {
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
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

}
