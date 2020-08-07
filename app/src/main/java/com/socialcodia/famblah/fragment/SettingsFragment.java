package com.socialcodia.famblah.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.model.response.ResponseDefault;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingsFragment extends Fragment {

    private EditText inputPassword, inputNewPassword,inputConfirmPassword;
    private Button btnUpdatePassword;
    SharedPrefHandler sharedPrefHandler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);

        //Init
        inputNewPassword = view.findViewById(R.id.inputNewPassword);
        inputPassword = view.findViewById(R.id.inputPassword);
        inputConfirmPassword = view.findViewById(R.id.inputConfirmPassword);
        btnUpdatePassword = view.findViewById(R.id.btnUpdatePassword);

        //Init SharePrefHandler
        sharedPrefHandler = SharedPrefHandler.getInstance(getContext());
        ModelUser modelUser = sharedPrefHandler.getUser();
        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        return  view;
    }

    private void validateData()
    {
        String password = inputPassword.getText().toString().trim();
        String newPassword = inputNewPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();
        if (password.isEmpty())
        {
            inputPassword.setError("Enter Password");
            inputPassword.requestFocus();
            return;
        }
        if (password.length()<7 || password.length()>30)
        {
            inputPassword.setError("Password should be greater than 7 character");
            inputPassword.requestFocus();
            return;
        }
        if (newPassword.isEmpty())
        {
            inputNewPassword.setError("Enter New Password");
            inputNewPassword.requestFocus();
            return;
        }
        if (newPassword.length()<7 || newPassword.length()>30)
        {
            inputNewPassword.setError("Password should be greater than 7 character");
            inputNewPassword.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty())
        {
            inputNewPassword.setError("Enter Confirm Password");
            inputNewPassword.requestFocus();
            return;
        }
        if (confirmPassword.length()<7 || confirmPassword.length()>30)
        {
            inputNewPassword.setError("Password should be greater than 7 character");
            inputNewPassword.requestFocus();
            return;
        }
        if (password==confirmPassword)
        {
            inputNewPassword.setError("Password Not Matched");
            inputNewPassword.requestFocus();
            inputConfirmPassword.setError("Password Not Matched");
            inputConfirmPassword.requestFocus();
            inputConfirmPassword.setText("");
            inputNewPassword.setError("");
        }
        else
        {
            doUpdatePassword(password,newPassword);
        }
    }

    private void doUpdatePassword(String password, String newPassword)
    {
         if (Utils.isNetworkAvailable(getContext()))
         {
             Toast.makeText(getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
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
                         Toast.makeText(getContext(), ResponseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                     else
                     {
                         btnUpdatePassword.setEnabled(true);
                         Toast.makeText(getContext(), "No Response From Server", Toast.LENGTH_SHORT).show();
                     }
                 }

                 @Override
                 public void onFailure(Call<ResponseDefault> call, Throwable t) {
                     btnUpdatePassword.setEnabled(true);
                     Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                 }
             });
         }
    }
}