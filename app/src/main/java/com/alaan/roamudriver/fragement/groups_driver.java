package com.alaan.roamudriver.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.PhoneAccount;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.acitivities.RegisterActivity;
import com.alaan.roamudriver.adapter.group_details_member_adapter;
import com.alaan.roamudriver.pojo.Group_List_membar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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

import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class groups_driver extends Fragment implements BackFragment {
    Button button_add2, button_remove2, button_create_gruop, button_change_gruop, button_my_gruops;
    //    EditText phone_number;//, group_name_et;
    TextView group_name_1;
    View rootView;
    ImageView profile_pic;
    String g_name;
    Integer group_id;
    RecyclerView recyclerView;

    public groups_driver() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_groups_driver, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.group_management));
        BindView(savedInstanceState);
        GetDirver();
//        getMemberList(Integer.parseInt(SessionManager.getUserId()));
//        Log.e("Get Data in id", SessionManager.getUserId());
        getGroupList(Integer.parseInt(SessionManager.getUserId()));
        return rootView;

    }

    private void BindView(Bundle savedInstanceState) {
        profile_pic = (ImageView) rootView.findViewById(R.id.profile_pic);
        button_add2 = (Button) rootView.findViewById(R.id.button_add);
        button_remove2 = (Button) rootView.findViewById(R.id.button_remove);
//        phone_number = (EditText) rootView.findViewById(R.id.phone_numbers);
//        group_name_et = (EditText) rootView.findViewById(R.id.group_name_et);
        group_name_1 = (TextView) rootView.findViewById(R.id.group_name_1);
//        recyclerView = (RecyclerView)rootView.findViewById(R.id.member_list);
        button_create_gruop = (Button) rootView.findViewById(R.id.button_create_gruop);
        button_change_gruop = (Button) rootView.findViewById(R.id.button_change_gruop);
        button_my_gruops = (Button) rootView.findViewById(R.id.button_my_gruops);

        button_create_gruop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog("createGroup");
//                if (group_name_et.getText() != null) {
//                    createGroup(group_name_et.getText().toString());
//                }
            }
        });

        button_change_gruop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog("changeGroup");
//                if (group_name_et.getText() != null) {
//                    ChangeGroupName(group_name_et.getText().toString());
//                }
            }
        });

        button_my_gruops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Group_detailsFragment group_detailsFragment = new Group_detailsFragment();
                changeFragment(group_detailsFragment, "Group Details Management");
            }
        });

        button_add2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                openDialog("addDriver");
//                group_id = group_id;
//                if (phone_number.getText() != null) {
//                    Add_user_Group(phone_number.getText().toString(), Integer.parseInt(SessionManager.getUserId()));
//                }
            }
        });

        button_remove2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group_id = 0;
                openDialog("delDriver");
//                if (phone_number.getText().toString() != null) {
//                    Remove_user_Group(phone_number.getText().toString());
//                } else {
//                    Toast.makeText(getContext(), "Enter Phone Number !!! ", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    private void getMemberList(int group_id) {
        final RequestParams params = new RequestParams();
        params.put("admin_id", group_id);
        Log.i("ibrahim group id", String.valueOf(group_id));
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
        Log.i("ibrahim", "GetDirver()");
        Log.i("ibrahim", SessionManager.getUserId());
        Log.i("ibrahim", SessionManager.getKEY());
        Log.i("ibrahim", Server.GET_GROUP);
        Server.get(Server.GET_GROUP, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i("ibrahim", "GetDirver()");
                Log.i("ibrahim", response.toString());
                try {
                    Gson gson = new Gson();
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        List<Group_membar> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<Group_membar>>() {
                        }.getType());
                        Log.e("Get Data in gruops", String.valueOf(list.toArray().length));
                        if (list.toArray().length > 0) {
                            Log.i("ibrahim", "list.toArray().length > 0");
                            button_create_gruop.setVisibility(View.GONE);
                            button_add2.setVisibility(View.VISIBLE);
                            button_remove2.setVisibility(View.VISIBLE);
                            button_change_gruop.setVisibility(View.VISIBLE);
                            button_my_gruops.setVisibility(View.VISIBLE);
                            group_name_1.setVisibility(View.VISIBLE);
                            profile_pic.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.i("ibrahim", "else list.toArray().length > 0");
                        button_create_gruop.setVisibility(View.VISIBLE);
                        button_add2.setVisibility(View.GONE);
                        button_remove2.setVisibility(View.GONE);
                        button_change_gruop.setVisibility(View.GONE);
                        group_name_1.setVisibility(View.GONE);
                        profile_pic.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    Log.e("Get Data in gruops", e.getMessage());
                }
            }
        });
    }

    private void getGroupList(int driver_id) {
        Log.i("ibrahim", "inside getGroupList");
        Log.i("ibrahim", String.valueOf(driver_id));

        final RequestParams params = new RequestParams();
        params.put("user_id", driver_id);
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_MyGroupLIST, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    Toast.makeText(getActivity(), getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                super.onSuccess(statusCode, headers, response);
                Log.i("ibrahim", "response.toString()");
                Log.i("ibrahim", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    List<Group_List_membar> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<Group_List_membar>>() {
                    }.getType());
                    if (list.size() > 0) {
                        Log.i("ibrahim list reply", response.getJSONArray("data").toString());
                        Log.i("ibrahim list reply", list.get(0).group_name);
                        Log.i("ibrahim list reply", list.get(0).admin_name);
                        Log.i("ibrahim list reply", String.valueOf(list.get(0).group_id));
                        group_name_1.setText(list.get(0).group_name);
                        Glide.with(getActivity()).load(list.get(0).admin_avatar).apply(new RequestOptions().error(R.drawable.user_default)).into(profile_pic);
                    }
                } catch (JSONException e) {
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

    private void Add_user_Group(String Phone, Integer group_id) {
        RequestParams params = new RequestParams();
        params.put("admin_id", group_id);
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
//                phone_number.setText("");
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
//                phone_number.setText("");
//                getMemberList(Integer.parseInt(SessionManager.getUserId()));

            }

            @Override
            public void onFinish() {
                super.onFinish();
                // swipeRefreshLayout.setRefreshing(false);
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
//                group_name_et.setText("");
                GetDirver();
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
//                group_name_et.setText("");
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

    public void openDialog(String actionStatus){
        android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(groups_driver.this.getContext());
        View mView = getLayoutInflater().inflate(R.layout.group_dialog_layout, null);
        final TextView textView = (TextView) mView.findViewById(R.id.textView);
        final EditText etGroupText = (EditText) mView.findViewById(R.id.etGroupText);

        Button mSubmit = (Button) mView.findViewById(R.id.btnSubmitDialog);
        Button mCancel = (Button) mView.findViewById(R.id.btnCancelDialog);

        switch (actionStatus) {
            case "createGroup":
                textView.setText(getString(R.string.addChangeGroup));
                etGroupText.setHint("");
                etGroupText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "changeGroup":
                textView.setText(getString(R.string.addChangeGroup));
                etGroupText.setHint("");
                etGroupText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "addDriver":
                textView.setText(getString(R.string.addDeleteDriver));
                etGroupText.setHint("+964");
                etGroupText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case "delDriver":
                textView.setText(getString(R.string.addDeleteDriver));
                etGroupText.setHint("+964");
                etGroupText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
        }

        mBuilder.setView(mView);
        final android.app.AlertDialog dialog = mBuilder.create();
        dialog.show();

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etGroupText.getText().toString().isEmpty()) {
                    dialog.dismiss();
                    switch (actionStatus) {
                        case "createGroup":
                            createGroup(etGroupText.getText().toString());
                            break;
                        case "changeGroup":
                            ChangeGroupName(etGroupText.getText().toString());
                            break;
                        case "addDriver":
                            Add_user_Group(etGroupText.getText().toString(), Integer.parseInt(SessionManager.getUserId()));
                            break;
                        case "delDriver":
                            Remove_user_Group(etGroupText.getText().toString());
                            break;
                    }
                    GetDirver();
                    getGroupList(Integer.parseInt(SessionManager.getUserId()));
                } else {
                    Toast.makeText(groups_driver.this.getContext(),
                            getString(R.string.Post_Empty),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
