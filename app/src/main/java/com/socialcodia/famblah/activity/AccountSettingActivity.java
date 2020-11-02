package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.storage.SharedPrefHandler;

public class AccountSettingActivity extends AppCompatActivity {

    String[] settingList = {"Update Account","Change Password","Blocked Users","Feed Privacy","Delete my account"};
    String[] settingDesList = {"Update account information","Change your account password","Blocked users list","Feed privacy, public, friends","Permanently delete your account"};
    int[] imageList = {R.drawable.person,R.drawable.password,R.drawable.ic_baseline_block,R.drawable.home,R.drawable.ic_baseline_delete};

    private SharedPrefHandler sp;
    private ActionBar actionBar;
    private ListView listView;

    private ImageView ivSetting;
    private TextView tvSetting, tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        init();

    }

    private void init()
    {
        sp = SharedPrefHandler.
                getInstance(getApplicationContext());
        actionBar = getSupportActionBar();
        actionBar.setTitle("Account Settings");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.settingListView);

        AccountSettingAdapter settingAdapter = new AccountSettingAdapter();
        listView.setAdapter(settingAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    class AccountSettingAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return settingList.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.row_setting,null);
            tvSetting = view.findViewById(R.id.tvSetting);
            tvDesc = view.findViewById(R.id.tvDesc);
            ivSetting = view.findViewById(R.id.ivSetting);
            tvSetting.setText(settingList[i]);
            tvDesc.setText(settingDesList[i]);
            ivSetting.setImageResource(imageList[i]);

            view.setOnClickListener(view1 -> {
                switch (settingList[i])
                {
                    case "Update Account":
                        sendToEditProfile();
                        break;
                    case "Change Password":
                        sendToChangePassword();
                        break;
                    case "Blocked Users":
                        sendToBlockedUsers();
                        break;
                    case "Feed Privacy":
                        sendToFeedPrivacy();
                        break;
                    case "Delete my account":
                        sendToDeleteAccount();
                        break;
                }
            });

            return view;
        }
    }

    private void sendToFeedPrivacy() {
        startActivity(new Intent(getApplicationContext(),FeedPrivacyActivity.class));
    }

    private void sendToDeleteAccount() {
        startActivity(new Intent(getApplicationContext(),DeleteAccountActivity.class));
    }

    private void sendToBlockedUsers() {
        startActivity(new Intent(getApplicationContext(),BlockedUsersActivity.class));
    }

    private void sendToEditProfile()
    {
        Intent intent = new Intent(getApplicationContext(),EditProfileActivity.class);
        startActivity(intent);
    }

    private void sendToChangePassword()
    {
        Intent intent = new Intent(getApplicationContext(),ChangePasswordActivity.class);
        startActivity(intent);
    }
}