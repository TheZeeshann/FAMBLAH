package com.socialcodia.famblah.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.fragment.AddFeedFragment;
import com.socialcodia.famblah.fragment.NotificationFragment;
import com.socialcodia.famblah.fragment.UsersFragment;
import com.socialcodia.famblah.pojo.ResponseNotificationsCount;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.fragment.HomeFragment;
import com.socialcodia.famblah.fragment.ProfileFragment;
import com.socialcodia.famblah.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SharedPrefHandler sharedPrefHandler;
    private BottomNavigationView navigationView;
    private ActionBar actionBar;
    private String[] storagePermission;
    private String token;
    private boolean doublePressToExitPressedOne = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkStoragePermission();
        Fragment fragment = new HomeFragment();
        setFragment(fragment);
        sharedPrefHandler = SharedPrefHandler.getInstance(getApplicationContext());
        token = sharedPrefHandler.getUser().getToken();

        navigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment1 = null;
            int id = item.getItemId();
            switch (id)
            {
                case R.id.miHome:
                    fragment1 = new HomeFragment();
                    setFragment(fragment1);
                    actionBar.setTitle("Home");
                    break;
                case R.id.miUsers:
                    fragment1 = new UsersFragment();
                    setFragment(fragment1);
                    actionBar.setTitle("Users");
                    break;
                case R.id.miNotification:
                    fragment1 = new NotificationFragment();
                    setFragment(fragment1);
                    actionBar.setTitle("Notifications");
                    break;
                case R.id.miProfile:
                    fragment1 = new ProfileFragment();
                    setFragment(fragment1);
                    actionBar.setTitle("Profile");
                    break;
                case R.id.miAddFeed:
                    fragment1 = new AddFeedFragment();
                    setFragment(fragment1);
                    actionBar.setTitle("Post Feed");
                    break;
            }
            return true;
        });

        isLoggedIn();
        getNotificationsCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (doublePressToExitPressedOne)
        {
            super.onBackPressed();
            return;
        }
        this.doublePressToExitPressedOne = true;
        TastyToast.makeText(getApplicationContext(),"Tab again to Exit",TastyToast.LENGTH_LONG,TastyToast.DEFAULT);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doublePressToExitPressedOne = false;
            }
        },2000);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Fragment fragment = null;
        switch (id) {
            case R.id.miPostFeed:
                fragment = new AddFeedFragment();
                setFragment(fragment);
                actionBar.setTitle("Post Feed");
                break;
            case R.id.miSettings:
                sendToSetting();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
    }

    private void init()
    {
        navigationView = findViewById(R.id.navigationView);
        sharedPrefHandler = SharedPrefHandler.getInstance(getApplicationContext());
        actionBar = getSupportActionBar();
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void checkStoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        if (!result)
        {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this,storagePermission,100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0)
        {
            boolean storagePermissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (storagePermissionAccepted)
            {
                TastyToast.makeText(getApplicationContext(),"Permission Granted",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
            }
            else
            {
                checkStoragePermission();
            }
        }
    }

    public void getNotificationsCount()
    {
        Call<ResponseNotificationsCount> call = ApiClient.getInstance().getApi().getNotificationsCount(token);
        call.enqueue(new Callback<ResponseNotificationsCount>() {
            @Override
            public void onResponse(Call<ResponseNotificationsCount> call, Response<ResponseNotificationsCount> response) {
                if (response.isSuccessful())
                {
                    ResponseNotificationsCount responseNotificationsCount = response.body();
                    if (!responseNotificationsCount.getError())
                    {
                        int notificationsCount = responseNotificationsCount.getNotificationsCount();
                        sharedPrefHandler.saveNotificationsCount(notificationsCount);
                        setNotificationsBadge();
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(),responseNotificationsCount.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }
                else
                {
                    TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseNotificationsCount> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public  void setNotificationsBadge()
    {
        int notificationsCount = sharedPrefHandler.getNotificationsCount();
        if (notificationsCount>0)
        {
            navigationView.getOrCreateBadge(R.id.miNotification).setNumber(notificationsCount);
            navigationView.getOrCreateBadge(R.id.miNotification).setVisible(true);
        }
        else
        {
            navigationView.getOrCreateBadge(R.id.miNotification).setVisible(false);
        }
    }

    private void isLoggedIn()
    {
        if (!sharedPrefHandler.isLoggedIn())
        {
            doLogout();
        }
    }

    private void doLogout()
    {
        TastyToast.makeText(getApplicationContext(),"Logout Successfully",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
        sharedPrefHandler.doLogout();
        sendToLogin();
    }

    private void sendToLogin()
    {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void sendToSetting()
    {
        Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}