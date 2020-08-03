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

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ResponseDefault;
import com.socialcodia.famblah.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnForgotPassword;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });

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
            inputEmail.setError("Enter Email");
            inputEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            inputEmail.setError("Enter Valid Email");
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
                            Toast.makeText(ForgotPasswordActivity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ForgotPasswordActivity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(ForgotPasswordActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
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