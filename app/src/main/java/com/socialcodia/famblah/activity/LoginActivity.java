package com.socialcodia.famblah.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseLogin;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText inputEmail, inputPassword;
    private TextView tvRegister,tvForgotPassword;
    private Button btnLogin;
    private Intent intent;
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        setIntentEmailAndPassword();
        checkLoggedInStatus();

        btnLogin.setOnClickListener(v -> validateData());

        tvRegister.setOnClickListener(v -> sendToRegister());

        tvForgotPassword.setOnClickListener(v -> sendToForgotPassword());

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
        if (intent.getStringExtra("intentPassword")!=null)
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
            inputEmail.setError(getString(R.string.EVE));
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
            progressBar.setVisibility(View.VISIBLE);
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
                            progressBar.setVisibility(View.INVISIBLE);
                            ModelUser modelUser = responseLogin.getUser();
                            SharedPrefHandler.getInstance(getApplicationContext()).saveUser(modelUser);
                            TastyToast.makeText(getApplicationContext(),"Login Successful",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                            sendToMainActivity();
                        }
                        else
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            btnLogin.setEnabled(true);
                            if (responseLogin.getMessage().equals("Wrong Password"))
                            {
                                inputPassword.setError("Wrong Password");
                                inputPassword.requestFocus();
                                return;
                            }
                            if (responseLogin.getMessage().toLowerCase().equals("email is not registered"))
                            {
                                inputEmail.setError("Email is not Registered");
                                inputEmail.requestFocus();
                                return;
                            }
                            if (responseLogin.getMessage().toLowerCase().equals("username is not registered"))
                            {
                                inputEmail.setError("Username is not Registered");
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
                                TastyToast.makeText(getApplicationContext(),responseLogin.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            }
                        }
                    }
                    else
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        btnLogin.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseLogin> call, Throwable t) {
                    btnLogin.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
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
        inputEmail = (TextInputEditText) findViewById(R.id.inputEmail);
        inputPassword = (TextInputEditText) findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);
        constraintLayout = findViewById(R.id.constraintLayout);

        progressBar.setVisibility(View.INVISIBLE);
    }
}