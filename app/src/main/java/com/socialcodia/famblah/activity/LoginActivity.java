package com.socialcodia.famblah.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.model.ResponseLogin;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private TextView tvRegister,tvForgotPassword;
    private Button btnLogin;
    private Switch btnRememberMe;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        setIntentEmailAndPassword();
        checkLoggedInStatus();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegister();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToForgotPassword();
            }
        });

    }

    private void sendToForgotPassword()
    {
        Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void setIntentEmailAndPassword()
    {
        intent = getIntent();
        if (intent.getStringExtra("intentEmail")!=null)
        {
            inputEmail.setText(intent.getStringExtra("intentEmail"));
        }
        else if (intent.getStringExtra("intentPassword")!=null)
        {
            inputPassword.setText(intent.getStringExtra("intentPassword"));
        }
    }

    private void checkLoggedInStatus()
    {
        if (SharedPrefHandler.getInstance(getApplicationContext()).isLoggedIn())
        {
            sendToMainActivity();
        }
    }

    private void sendToRegister()
    {
        Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);
    }

    private void validateData()
    {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty())
        {
            inputEmail.setError("Enter Email");
            inputEmail.requestFocus();
            return;
        }
        if (email.length()>1 && email.length() <4)
        {
            inputEmail.setError("Enter Valid Username");
            inputEmail.requestFocus();
            return;
        }
        if (email.length()>30)
        {
            inputEmail.setError("Enter Valid Email");
            inputEmail.requestFocus();
            return;
        }
        if (password.isEmpty())
        {
            inputPassword.setError("Enter Password");
            inputPassword.requestFocus();
            return;
        }
        if (inputPassword.length()<8 || inputPassword.length()>30)
        {
            inputPassword.setError("Password should be greater than 7 character");
            inputPassword.requestFocus();
        }
        else
        {
            doLogin(email,password);
        }
    }

    private void doLogin(String email, String password)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnLogin.setEnabled(false);
            Call<ResponseLogin> call = ApiClient.getInstance().getApi().login(email,password);
            call.enqueue(new Callback<ResponseLogin>() {
                @Override
                public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                    if (response.isSuccessful())
                    {
                        ResponseLogin responseLogin = response.body();
                        if (!responseLogin.getError())
                        {
                            ModelUser modelUser = responseLogin.getUser();
                            SharedPrefHandler.getInstance(getApplicationContext()).saveUser(modelUser);
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            sendToMainActivity();
                        }
                        else
                        {
                            btnLogin.setEnabled(true);
                            if (responseLogin.getMessage().equals("Wrong Password"))
                            {
                                inputPassword.setError("Wrong Password");
                                inputPassword.requestFocus();
                                return;
                            }
                            if (responseLogin.getMessage().equals("Email or Username is Wrong"))
                            {
                                inputEmail.setError("Wrong Email or Username");
                                inputEmail.requestFocus();
                                return;
                            }
                            if (responseLogin.getMessage().toLowerCase().equals("email is not verified"))
                            {
                                inputEmail.setError("Email Not Verified");
                                inputEmail.requestFocus();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseLogin> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                }
            });
        }
    }

    private void sendToMainActivity()
    {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void init()
    {
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnRememberMe = findViewById(R.id.btnRememberMe);
    }
}