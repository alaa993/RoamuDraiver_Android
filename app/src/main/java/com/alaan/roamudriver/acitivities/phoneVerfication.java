package com.alaan.roamudriver.acitivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.alaan.roamudriver.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;


public class phoneVerfication extends AppCompatActivity {
    private final int REQUESR_LOG = 1000;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0 ;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            if(!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()){
                if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null)
                startActivity(new Intent(this, RegisterActivity.class)
                        .putExtra("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())

                );
                finish();

            }
        }
        else{
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder().setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build())).setLogo(R.drawable.direction_arrive).setTheme(R.style.AppTheme).build(),REQUESR_LOG);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESR_LOG)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {
                if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty())
                {
                    startActivity(new Intent(this, RegisterActivity.class).putExtra("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
                    finish();
                    return;
                }else{
                    if (response == null ) {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(this, "NO internet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(this, "Unkonw erorrs", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
    }
}
