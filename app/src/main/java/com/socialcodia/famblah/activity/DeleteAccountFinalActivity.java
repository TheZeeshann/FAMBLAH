package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteAccountFinalActivity extends AppCompatActivity {

    private EditText inputOtp;
    private SharedPrefHandler sp;
    private ModelUser mUser;
    private String token,otp;
    private ActionBar actionBar;
    private Button btnDeleteAccount, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account_final);
        init();

        btnCancel.setOnClickListener(v->sendToHome());
        btnDeleteAccount.setOnClickListener(v->validateData());
    }

    private void sendToHome()
    {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
    }

    private void showDeleteAccountAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteAccountFinalActivity.this);
        builder.setTitle("Permanently Delete Account");
        builder.setMessage("Are you sure want to permanently delete your account");
        builder.setPositiveButton("Delete My Account", (dialogInterface, i) -> deleteAccount()).setNegativeButton("Cancel", (dialogInterface, i) -> TastyToast.makeText(getApplicationContext(), "Account Deletion Canceled", TastyToast.LENGTH_LONG, TastyToast.SUCCESS));
        builder.show();
    }

    private void deleteAccount()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            TastyToast.makeText(getApplicationContext(),"Deleting...",TastyToast.LENGTH_LONG,TastyToast.DEFAULT);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().deleteAccount(token,token);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault rd = response.body();
                        if (!rd.getError())
                        {
                            doLogout();
                            TastyToast.makeText(getApplicationContext(),rd.getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        }
                        else
                            TastyToast.makeText(getApplicationContext(),rd.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                    else
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void doLogout()
    {
        sp.doLogout();
        sendToLogin();
    }

    private void sendToLogin()
    {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
    }

    private void validateData()
    {
        otp = inputOtp.getText().toString().trim();
        if (otp.isEmpty())
        {
            inputOtp.setError("Enter Password");
            inputOtp.requestFocus();
            return;
        }
        if (otp.length() != 6)
        {
            inputOtp.setError("Enter Valid OTP");
            inputOtp.requestFocus();
        }
        else
        {
            showDeleteAccountAlert();
        }
    }

    private void init()
    {
        inputOtp = findViewById(R.id.inputOtp);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnCancel = findViewById(R.id.btnCancel);
        sp = SharedPrefHandler.getInstance(getApplicationContext());
        mUser = sp.getUser();
        token = mUser.getToken();
        actionBar = getSupportActionBar();
        actionBar.setTitle("Delete Account Final Step");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}