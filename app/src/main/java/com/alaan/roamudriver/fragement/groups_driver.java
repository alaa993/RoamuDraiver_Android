package com.alaan.roamudriver.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.PhoneAccount;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.acitivities.HomeActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.adapter.Group_membar_Adapter;
import com.alaan.roamudriver.pojo.Driver_groups_model;
import com.alaan.roamudriver.pojo.Group_membar;
import com.alaan.roamudriver.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class groups_driver extends Fragment implements BackFragment {
    Button button_add2, button_remove2, button_create_gruop, button_change_gruop, button_my_gruops;
    EditText phone_number, group_name_et;
    View rootView;
    String g_name;
    Integer gruop_id;
    RecyclerView recyclerView;

    public groups_driver() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_groups_driver, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.group_management));
        GetDirver();

        BindView(savedInstanceState);
//        getMemberList(Integer.parseInt(SessionManager.getUserId()));
//        Log.e("Get Data in id", SessionManager.getUserId());

        return rootView;

    }

    private void getMemberList(int gruop_id) {
        final RequestParams params = new RequestParams();
        params.put("admin_id", gruop_id);
        Log.i("ibrahim group id", String.valueOf(gruop_id));
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_MEBLIST, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();

                super.onSuccess(statusCode, headers, response);
                try {
                    Gson gson = new GsonBuilder().create();
                    List<Group_membar> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<Group_membar>>() {
                    }.getType());
                    Log.i("ibrahim list reply", response.getJSONArray("data").toString());
//                        RecyclerView recyclerView = (RecyclerView)  rootView.findViewById(R.id.member_list);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    Group_membar_Adapter group_membar_adapter = new Group_membar_Adapter(list);
                    recyclerView.setAdapter(group_membar_adapter);
                    group_membar_adapter.notifyDataSetChanged();
                } catch (JSONException e) {

                    //Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();

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
                    //   swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


    }

    private void GetDirver() {
        RequestParams params = new RequestParams();
        params.put("admin_id", SessionManager.getUserId());
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_GROUP, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("success", response.toString());
                try {
                    Gson gson = new Gson();
                    List<Group_membar> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<Group_membar>>() {
                    }.getType());
                    Log.e("Get Data in gruops", String.valueOf(list.toArray().length));
                    if (list.toArray().length > 0) {
                        button_create_gruop.setVisibility(View.GONE);
                    } else {
                        phone_number.setVisibility(View.GONE);
                        button_add2.setVisibility(View.GONE);
                        button_remove2.setVisibility(View.GONE);
                        button_change_gruop.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    Log.e("Get Data in gruops", e.getMessage());

                }
            }
        });


    }

    public static groups_driver newInstance(String param1, String param2) {
        groups_driver fragment = new groups_driver();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void Add_user_Group(String Phone, Integer gruop_id) {
        RequestParams params = new RequestParams();
        params.put("admin_id", gruop_id);
        params.put("mobile", Phone);
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.add_user_Gruop, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                // swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getContext(), "Successfully Added To Group", Toast.LENGTH_SHORT).show();
//                getMemberList(Integer.parseInt(SessionManager.getUserId()));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                // swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void Remove_user_Group(String Phone) {
        RequestParams params = new RequestParams();
        params.put("mobile", Phone);
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.remove_user_Gruop, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                // swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getContext(), "Successfully Removed From Group", Toast.LENGTH_SHORT).show();
//                getMemberList(Integer.parseInt(SessionManager.getUserId()));

            }

            @Override
            public void onFinish() {
                super.onFinish();
                // swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void BindView(Bundle savedInstanceState) {
        button_add2 = (Button) rootView.findViewById(R.id.button_add);
        button_remove2 = (Button) rootView.findViewById(R.id.button_remove);
        phone_number = (EditText) rootView.findViewById(R.id.phone_numbers);
        group_name_et = (EditText) rootView.findViewById(R.id.group_name_et);
//        recyclerView = (RecyclerView)rootView.findViewById(R.id.member_list);
        button_create_gruop = (Button) rootView.findViewById(R.id.button_create_gruop);
        button_change_gruop = (Button) rootView.findViewById(R.id.button_change_gruop);
        button_my_gruops = (Button) rootView.findViewById(R.id.button_my_gruops);

        button_add2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                gruop_id = gruop_id;
                if (phone_number.getText() != null) {
                    Add_user_Group(phone_number.getText().toString(), Integer.parseInt(SessionManager.getUserId()));
                }

            }


        });
        button_create_gruop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (group_name_et.getText() != null) {
                    createGroup(group_name_et.getText().toString());
                }
            }
        });

        button_change_gruop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (group_name_et.getText() != null) {
                    ChangeGroupName(group_name_et.getText().toString());
                }
            }
        });

        button_my_gruops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Group_detailsFragment group_detailsFragment = new Group_detailsFragment();
                changeFragment(group_detailsFragment, "Group Details Management");
            }
        });


        button_remove2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gruop_id = 0;
                if (phone_number.getText().toString() != null) {


                    Remove_user_Group(phone_number.getText().toString());
                } else {
                    Toast.makeText(getContext(), "Enter Phone Number !!! ", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    drawer_close();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack(null);
                }
            }, 50);
        } catch (Exception e) {

        }
    }

    private void createGroup(String s) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("group_name", s);
        requestParams.put("admin_id", SessionManager.getUserId());
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.addGruop, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                // swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getContext(), "Group Added Successfully", Toast.LENGTH_SHORT).show();
                group_name_et.setText("");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                // swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void ChangeGroupName(String s) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("group_name", s);
        requestParams.put("admin_id", SessionManager.getUserId());
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.ChangeGruopName, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                // swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getContext(), "Group Name Changed Successfully", Toast.LENGTH_SHORT).show();
                group_name_et.setText("");
//                Log.i("ibrahim was here","success");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                // swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        Toast.makeText(getContext(), "Pressed back in Gruop", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public int getBackPriority() {
        return 0;
    }
}
