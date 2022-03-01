package com.alaan.roamudriver.fragement;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.session.SessionManager;
import com.fxn.stash.Stash;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;

import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link lang.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link lang#newInstance} factory method to
 * create an instance of this fragment.
 */
public class lang extends FragmentManagePermission {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String LANGUAGE = "ar";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button b1, b2, b3;
    View view;

    private OnFragmentInteractionListener mListener;

    public lang() {
        // Required empty public constructor
    }

    public static lang newInstance(String param1, String param2) {
        lang fragment = new lang();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void BindView() {

        b1 = view.findViewById(R.id.b1en);
        b2 = view.findViewById(R.id.b2ar);
        b3 = view.findViewById(R.id.b3ka);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en", getActivity());
                updateLanguage("2", "en");
                Stash.put("TAG_LANG", "en");
                getActivity().recreate();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ar", getActivity());
                updateLanguage("1", "ar");
                Stash.put("TAG_LANG", "ar");
                getActivity().recreate();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ku", getActivity());
                updateLanguage("3", "ku");
                Stash.put("TAG_LANG", "ku");
                getActivity().recreate();
            }
        });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lang.loadLocale(getActivity());
        Stash.init(getContext());
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lang, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.lang));

        BindView();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.update(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
            //                  + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void update(Uri uri);
    }

    public static void setLocale(String lang, Context context) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        if (lang.contains("ku")) {
            configuration.setLayoutDirection(new Locale("ar"));
        } else {
            configuration.setLayoutDirection(new Locale(lang));
        }

        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = context.getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("lang", lang);
        editor.apply();
    }

    public static void loadLocale(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Settings", MODE_PRIVATE);
        String lang = pref.getString("lang", "ar");
        LANGUAGE = lang;
        setLocale(lang, context);
    }

    private void updateLanguage(String lang_nu, String lang_text) {
        RequestParams params = new RequestParams();
        params.put("user_id", SessionManager.getUserId());
        params.put("lang_nu", lang_nu);
        params.put("lang_text", lang_text);
        Server.setHeader(SessionManager.getKEY());
        Server.setContentType();
        Server.post(Server.updateLanguage, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
//                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
