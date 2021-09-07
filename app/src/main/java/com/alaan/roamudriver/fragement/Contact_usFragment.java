package com.alaan.roamudriver.fragement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.custom.CheckConnection;


public class Contact_usFragment extends Fragment {

    View view;
    EditText Name, description;
    AppCompatButton btn_send;
    TextView whatsAppNumber;

    public Contact_usFragment() {
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
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.contact_us));
        BindView();
        return view;
    }

    public void BindView() {
        Name = (EditText) view.findViewById(R.id.FN_Etxt1);
//        email = (EditText) view.findViewById(R.id.FN_Etxt2);
        description = (EditText) view.findViewById(R.id.FN_Etxt3);
        whatsAppNumber = (TextView) view.findViewById(R.id.FN_txt5);
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

        whatsAppNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(getActivity())) {
                    String text = "Name: " + Name.getText().toString() + "\n"
//                + "Email: " + email.getText().toString() + "\n"
                            + "Description: " + description.getText().toString();
                    openWhatsAppConversationUsingUri(getContext(), "+447732830221", text);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void openWhatsAppConversationUsingUri(Context context, String numberWithCountryCode, String message) {

        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + numberWithCountryCode + "&text=" + message);

        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);

        context.startActivity(sendIntent);
    }

    public void sendEmail(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        String text = "Name: " + Name.getText().toString() + "\n"
//                + "Email: " + email.getText().toString() + "\n"
                + "Description: " + description.getText().toString();

        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "info@roamu.net");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact us");
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(Intent.createChooser(emailIntent, "Select Email Client"));
            Name.setText("");
            description.setText("");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "No Email client found!!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}