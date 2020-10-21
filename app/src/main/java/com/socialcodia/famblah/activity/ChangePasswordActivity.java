package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText inputPassword, inputNewPassword,inputConfirmPassword;
    private Button btnUpdatePassword;
    SharedPrefHandler sharedPrefHandler;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //Init
        inputNewPassword = findViewById(R.id.inputNewPassword);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Change Password");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Init SharePrefHandler
        sharedPrefHandler = SharedPrefHandler.getInstance(getApplicationContext());
        ModelUser modelUser = sharedPrefHandler.getUser();
        btnUpdatePassword.setOnClickListener(v -> validateData());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void validateData()
    {
        String password = inputPassword.getText().toString().trim();
        String newPassword = inputNewPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();
        if (password.isEmpty())
        {
            inputPassword.setError(getString(R.string.EP));
            inputPassword.requestFocus();
            return;
        }
        if (password.length()<7 || password.length()>30)
        {
            inputPassword.setError(getString(R.string.PG7));
            inputPassword.requestFocus();
            return;
        }
        if (newPassword.isEmpty())
        {
            inputNewPassword.setError(getString(R.string.ENP));
            inputNewPassword.requestFocus();
            return;
        }
        if (newPassword.length()<7 || newPassword.length()>30)
        {
            inputNewPassword.setError(getString(R.string.PG7));
            inputNewPassword.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty())
        {
            inputConfirmPassword.setError(getString(R.string.ECP));
            inputConfirmPassword.requestFocus();
            return;
        }
        if (confirmPassword.length()<7 || confirmPassword.length()>30)
        {
            inputConfirmPassword.setError(getString(R.string.PG7));
            inputConfirmPassword.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmPassword))
        {
            inputNewPassword.setError(getString(R.string.PNM));
            inputNewPassword.requestFocus();
            inputConfirmPassword.setError(getString(R.string.PNM));
            inputConfirmPassword.requestFocus();
            inputConfirmPassword.setText("");
            inputNewPassword.setError("");
            return;
        }
        if (password.equals(newPassword))
        {
            inputNewPassword.setError("You can't use your old password");
            inputNewPassword.requestFocus();
            inputNewPassword.setText("");
        }
        else
        {
            doUpdatePassword(password,newPassword);
        }
    }

    private void doUpdatePassword(String password, String newPassword)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            TastyToast.makeText(getApplicationContext(),"Please wait...",TastyToast.LENGTH_LONG,TastyToast.DEFAULT);
            btnUpdatePassword.setEnabled(false);
            ModelUser modelUser = sharedPrefHandler.getUser();
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().updatePassword(modelUser.getToken(),password,newPassword);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    ResponseDefault ResponseDefault = response.body();
                    if (ResponseDefault!=null)
                    {
                        btnUpdatePassword.setEnabled(true);
                        Toast.makeText(getApplicationContext(), ResponseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        btnUpdatePassword.setEnabled(true);
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnUpdatePassword.setEnabled(true);
                    t.printStackTrace();
                }
            });
        }
    }
}