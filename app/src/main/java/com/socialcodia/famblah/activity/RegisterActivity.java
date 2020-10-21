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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText inputName, inputUsername, inputEmail, inputPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        btnRegister.setOnClickListener(v -> validateData());
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

    private void validateData()
    {
        String name, username, email, password;
        name = inputName.getText().toString().trim();
        username = inputUsername.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        if (name.isEmpty())
        {
            inputName.setError("Enter Name");
            inputName.requestFocus();
            return;
        }
        if (name.length()<4 || name.length()>20)
        {
            inputName.setError("Enter Valid Name");
            inputName.requestFocus();
            return;
        }
        if (username.isEmpty())
        {
            inputUsername.setError("Enter Username");
            inputUsername.requestFocus();
            return;
        }
        if (username.length()<3 || username.length()>15)
        {
            inputUsername.setError("Enter Valid Username");
            inputUsername.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            inputEmail.setError("Enter Email");
            inputEmail.requestFocus();
            return;
        }
        if (email.length()<10 || email.length()>50)
        {
            inputEmail.setError("Enter Valid Email");
            inputEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
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
        if (password.length()<7 || password.length()>20)
        {
            inputPassword.setError("Password should be greater than 7 character");
            inputPassword.requestFocus();
        }
        else
        {
            doRegister(name,username,email,password);
        }
    }

    private void doRegister(String name, String username, String email, String password)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnRegister.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().register(name,username,email,password);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {

                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            sendToLoginWithEmailAndPassword(email,password);
                            TastyToast.makeText(getApplicationContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        }
                        else
                        {
                            btnRegister.setEnabled(true);
                            if (responseDefault.getMessage().toLowerCase().equals("email already registered"))
                            {
                                inputEmail.setError("Email Already Registered");
                                inputEmail.requestFocus();
                                return;
                            }
                            if (responseDefault.getMessage().toLowerCase().equals("username not available"))
                            {
                                inputUsername.setError("Username Not Available");
                                inputUsername.requestFocus();
                            }
                            else
                            {
                                btnRegister.setEnabled(true);
                                TastyToast.makeText(getApplicationContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                            }
                        }
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        btnRegister.setEnabled(true);
                    }

                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    t.printStackTrace();
                    btnRegister.setEnabled(true);
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

    private void init()
    {
        inputName = findViewById(R.id.inputName);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }
}