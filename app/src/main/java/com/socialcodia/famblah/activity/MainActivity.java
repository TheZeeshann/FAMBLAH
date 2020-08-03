package com.socialcodia.famblah.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.fragment.AboutUsFragment;
import com.socialcodia.famblah.fragment.AddFeedFragment;
import com.socialcodia.famblah.fragment.NotificationFragment;
import com.socialcodia.famblah.fragment.UsersFragment;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.fragment.HomeFragment;
import com.socialcodia.famblah.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private SharedPrefHandler sharedPrefHandler;
    private BottomNavigationView navigationView;
    private ActionBar actionBar;
    private String storagePermission[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkStoragePermission();
        Fragment fragment = new HomeFragment();
        setFragment(fragment);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                Fragment fragment = null;
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.miHome:
                        fragment = new HomeFragment();
                        setFragment(fragment);
                        actionBar.setTitle("Home");
                        break;
                    case R.id.miUsers:
                        fragment = new UsersFragment();
                        setFragment(fragment);
                        actionBar.setTitle("Users");
                        break;
                    case R.id.miNotification:
                        fragment = new NotificationFragment();
                        setFragment(fragment);
                        actionBar.setTitle("Notifications");
                        break;
                    case R.id.miProfile:
                        fragment = new ProfileFragment();
                        setFragment(fragment);
                        actionBar.setTitle("Profile");
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Fragment fragment = null;
        switch (id) {
            case R.id.miAbout:
                fragment = new AboutUsFragment();
                setFragment(fragment);
                actionBar.setTitle("About Us");
                break;
            case R.id.miLogout:
                doLogout();
                break;
            case R.id.miPostFeed:
                fragment = new AddFeedFragment();
                setFragment(fragment);
                actionBar.setTitle("Post Feed");
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
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else
            {
                checkStoragePermission();
            }
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
        Toast.makeText(this, "Successfully Logout", Toast.LENGTH_SHORT).show();
        sharedPrefHandler.doLogout();
        sendToLogin();
    }

    private void sendToLogin()
    {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}