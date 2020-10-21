package com.socialcodia.famblah.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.Api;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.pojo.ResponseUpdate;
import com.socialcodia.famblah.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        checkUpdate();
    }

    private void checkUpdate()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            isUpdateAvailable();
        }
        else
        {
            Handler handler = new Handler();
            handler.postDelayed(() -> sendToLogin(),2000);
        }
    }

    private String getVersion()
    {
        String versionName = null;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void isUpdateAvailable()
    {
        Call<ResponseUpdate> call = ApiClient.getInstance().getApi().checkUpdate(getVersion());
        call.enqueue(new Callback<ResponseUpdate>() {
            @Override
            public void onResponse(Call<ResponseUpdate> call, Response<ResponseUpdate> response) {
                if (response.isSuccessful())
                {
                    ResponseUpdate responseUpdate = response.body();
                    if (!responseUpdate.getError())
                    {
                        String downloadUrl = responseUpdate.getUpdates().getUpdateUrl();
                        showUpdateDialog(downloadUrl);
                    }
                    else
                    {
                        sendToLogin();
                    }
                }
                else
                    sendToLogin();
            }
            @Override
            public void onFailure(Call<ResponseUpdate> call, Throwable t) {
                t.printStackTrace();
                sendToLogin();
            }
        });
    }

    private void showUpdateDialog(String downloadUrl)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("Update Available");
        builder.setMessage("A new version is available, Please update this application to continue");
        builder.setCancelable(false);
        builder.setPositiveButton("Update", (dialog, which) -> {
            sendToDownloadApp(downloadUrl);
            onBackPressed();
        });
        builder.setNegativeButton("Exit", (dialog, which) -> onBackPressed());
        builder.create().show();
    }

    private void sendToDownloadApp(String downloadUrl)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(downloadUrl));
        startActivity(intent);
    }

    private void sendToLogin()
    {
//        Intent intent = new Intent(getApplicationContext(),VideoPlayerActivity.class);
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}