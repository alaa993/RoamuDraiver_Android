package com.alaan.roamudriver.fragement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alaan.roamudriver.R;
import com.alaan.roamudriver.custom.Utils;
import com.alaan.roamudriver.pojo.PendingRequestPojo;

/**
 * Created by android on 15/4/17.
 */

public class PaymentDetail extends Fragment {
    View view;
    PendingRequestPojo pojo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.payment_detail, container, false);
        BindView();
        return view;
    }

    public void BindView() {
        TextView pickupaddress = (TextView) view.findViewById(R.id.txt_pickupaddress);
        TextView dropaddress = (TextView) view.findViewById(R.id.txt_dropaddress);
        TextView distance = (TextView) view.findViewById(R.id.txt_distance);
        TextView status = (TextView) view.findViewById(R.id.txt_status);
        TextView amount = (TextView) view.findViewById(R.id.txt_amount);
        TextView payment_status = (TextView) view.findViewById(R.id.txt_paymentstatus);
        TextView time = (TextView) view.findViewById(R.id.txt_datetime);
        Bundle bundle = getArguments();
        if (bundle != null) {
            pojo = (PendingRequestPojo) bundle.getSerializable("data");
            if (pojo != null) {
                pickupaddress.setText(pojo.getPickup_address());
                dropaddress.setText(pojo.getDrop_address());
                distance.setText(pojo.getDistance());
                status.setText(pojo.getStatus());
                amount.setText(pojo.getAmount());


                if (pojo.getPayment_status() != null && !pojo.getPayment_status().equals("")) {
                    payment_status.setText(pojo.getPayment_status());

                } else {
                    payment_status.setText(getString(R.string.unpaid));
                }
                Utils utils = new Utils();
                String binded = utils.getCurrentDateInSpecificFormat(pojo.getTime() + " , " + Utils.getformattedTime(pojo.getTime()));
                time.setText(binded);
            }
        }
    }


}