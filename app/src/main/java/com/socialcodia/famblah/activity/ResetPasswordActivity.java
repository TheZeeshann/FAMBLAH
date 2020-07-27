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
import com.socialcodia.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail, inputOtp, inputPassword;
    private Button btnResetPassword;
    private TextView tvLogin;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        init();
        setIntentEmail();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
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

    private void setIntentEmail()
    {
        intent = getIntent();
        if (intent.getStringExtra("intentEmail")!=null)
        {
            String email  = intent.getStringExtra("intentEmail");
            inputEmail.setText(email);
        }
    }

    private void validateData() 
    {
        String email, otp, password;
        
        email = inputEmail.getText().toString().trim();
        otp = inputOtp.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

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
            return;
        }
        if (otp.isEmpty())
        {
            inputOtp.setError("Enter OTP");
            inputOtp.requestFocus();
            return;
        }
        if (inputOtp.length()<6)
        {
            inputOtp.setError("Enter Valid OTP");
            inputOtp.requestFocus();
            return;
        }
        if (password.isEmpty())
        {
            inputPassword.setError("Enter Password");
            inputPassword.requestFocus();
            return;
        }
        if (password.length()<7 || password.length()>20)
        {
            inputPassword.setError("Password should be greater than 7 character");
            inputPassword.requestFocus();
        }
        else
        {
            doResetPassword(email,otp,password);
        }
    }

    private void doResetPassword(String email, String otp, String password)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnResetPassword.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().resetPassword(email,otp,password);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            btnResetPassword.setEnabled(true);
                            Toast.makeText(ResetPasswordActivity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                            sendToLoginWithEmailAndPassword(email,password);
                        }
                        else
                        {
                            btnResetPassword.setEnabled(true);
                            if (responseDefault.getMessage().toLowerCase().equals("invalid otp"))
                            {
                                inputOtp.setError("Wrong OTP");
                                inputOtp.requestFocus();
                            }
                            else
                            {
                                Toast.makeText(ResetPasswordActivity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(ResetPasswordActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnResetPassword.setEnabled(true);
                    Toast.makeText(ResetPasswordActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendToLoginWithEmailAndPassword(String email, String password)
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("intentEmail",email);
        intent.putExtra("intentPassword",password);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void sendToLogin()
    {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void init() {
        inputEmail = findViewById(R.id.inputEmail);
        inputOtp = findViewById(R.id.inputOtp);
        inputPassword = findViewById(R.id.inputPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvLogin = findViewById(R.id.tvLogin);
        inputEmail.setEnabled(false);
    }
}