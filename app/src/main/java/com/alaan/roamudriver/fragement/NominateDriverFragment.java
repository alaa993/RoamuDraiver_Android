package com.alaan.roamudriver.fragement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.custom.CheckConnection;

public class NominateDriverFragment extends Fragment {

    private View view;
    EditText driverName, driverCity, driverCountry, driverPhone, driverVehicle, description;
    AppCompatButton btn_send;

    public NominateDriverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.nominate_a_driver));
        view = inflater.inflate(R.layout.fragment_nominate_driver, container, false);
        BindView();
        return view;
    }

    public void BindView() {
        driverName = (EditText) view.findViewById(R.id.FN_Etxt1);
//        email = (EditText) view.findViewById(R.id.FN_Etxt2);
        driverCity = (EditText) view.findViewById(R.id.FN_Etxt3);
        driverCountry = (EditText) view.findViewById(R.id.FN_Etxt4);
        driverPhone = (EditText) view.findViewById(R.id.FN_Etxt5);
        driverVehicle = (EditText) view.findViewById(R.id.FN_Etxt6);
        description = (EditText) view.findViewById(R.id.FN_Etxt7);
        btn_send = (AppCompatButton) view.findViewById(R.id.FN_BTN);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(getActivity())) {
                    sendEmail(view);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sendEmail(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        String text = "Driver Name: " + driverName.getText().toString() + "\n"
//                + "Email: " + email.getText().toString() + "\n"
                + "Driver City: " + driverCity.getText().toString() + "\n"
                + "Driver Country: " + driverCountry.getText().toString() + "\n"
                + "Driver Phone: " + driverPhone.getText().toString() + "\n"
                + "Driver Vehicle: " + driverVehicle.getText().toString() + "\n"
                + "Description: " + description.getText().toString();

        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"info@roamu.net"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Nominate a Driver");
        emailIntent.putExtra(Intent.EXTRA_TEXT   , text);
        try {
            startActivity(Intent.createChooser(emailIntent, "Select Email Client"));
            driverName.setText("");
            driverCity.setText("");
            driverCountry.setText("");
            driverPhone.setText("");
            driverVehicle.setText("");
            description.setText("");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "No Email client found!!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}