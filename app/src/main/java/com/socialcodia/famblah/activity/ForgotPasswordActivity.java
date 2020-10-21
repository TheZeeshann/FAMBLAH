package com.socialcodia.famblah.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText inputEmail;
    private Button btnForgotPassword;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();

        btnForgotPassword.setOnClickListener(v -> validateData());

        tvLogin.setOnClickListener(v -> sendToLogin());

    }

    private void sendToLogin()
    {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void sendToResetPasswordWithEmail(String email)
    {
        Intent intent = new Intent(getApplicationContext(),ResetPasswordActivity.class);
        intent.putExtra("intentEmail",email);
        startActivity(intent);
        finish();
    }

    private void validateData()
    {
        String email = inputEmail.getText().toString().trim();
        if (email.isEmpty())
        {
            inputEmail.setError(getString(R.string.EE));
            inputEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            inputEmail.setError(getString(R.string.EVE));
            inputEmail.requestFocus();
        }
        else
        {
            sendForgotPasswordEmail(email);
        }
    }

    private void sendForgotPasswordEmail(String email)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnForgotPassword.setEnabled(false);
            Toast.makeText(this, "Please wait...", Toast.LENGTH_LONG).show();
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().forgotPassword(email);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            TastyToast.makeText(getApplicationContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                            btnForgotPassword.setEnabled(true);
                            sendToResetPasswordWithEmail(email);
                        }
                        else
                        {
                            btnForgotPassword.setEnabled(true);
                            if (responseDefault.getMessage().toLowerCase().equals("email is not registered"))
                            {
                                inputEmail.setError("Email Not Registered");
                                inputEmail.requestFocus();
                                return;
                            }
                            if (responseDefault.getMessage().toLowerCase().equals("email is not verified"))
                            {
                                inputEmail.setError("Email Not Verified");
                                inputEmail.requestFocus();
                            }
                            else
                            {
                                TastyToast.makeText(getApplicationContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            }
                        }
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        btnForgotPassword.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    Toast.makeText(ForgotPasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    btnForgotPassword.setEnabled(true);
                }
            });
        }
    }

    private void init()
    {
        inputEmail = findViewById(R.id.inputEmail);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        tvLogin = findViewById(R.id.tvLogin);
    }
}