package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.storage.SharedPrefHandler;

import java.io.ByteArrayOutputStream;
import java.util.Set;

public class SettingActivity extends AppCompatActivity {

    String[] settingList = {"Account","Change Password","Request Verification","Invite a friend","Logout","Contact Us","About Us"};
    String[] settingDesList = {"Account setting, Update profile","Change your account Password","Request a Verification badge for your account","Invite your friend to FAMBLAH","Logout your account","Contact Us for any query","About our application"};
    int[] imageItem = {R.drawable.person,R.drawable.password,R.drawable.correct,R.drawable.group,R.drawable.logout,R.drawable.contact_us,R.drawable.info};
    ListView listView;
    ActionBar actionBar;

    ImageView ivSetting;
    TextView tvSetting,tvDesc;
    private SharedPrefHandler sharedPrefHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPrefHandler = SharedPrefHandler.getInstance(getApplicationContext());
        listView = findViewById(R.id.settingListView);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Setting");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        SettingAdapter settingAdapter = new SettingAdapter();
        listView.setAdapter(settingAdapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    class  SettingAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return settingList.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.row_setting,null);
            tvSetting = convertView.findViewById(R.id.tvSetting);
            ivSetting = convertView.findViewById(R.id.ivSetting);
            tvDesc = convertView.findViewById(R.id.tvDesc);
            tvSetting.setText(settingList[position]);
            tvDesc.setText(settingDesList[position]);
            ivSetting.setImageResource(imageItem[position]);

            convertView.setOnClickListener(v -> {
                switch (settingList[position])
                {
                    case "Account":
                        sendToEditProfile();
                        break;
                    case "Change Password":
                        sendToChangePassword();
                        break;
                    case "Request Verification":
                        sendToVerification();
                        break;
                    case "Invite a friend":
                        inviteFriend();
                        break;
                    case "Logout":
                        doLogout();
                        break;
                    case "Contact Us":
                        sendToContactUs();
                        break;
                    case "About Us":
                        sendToAboutUs();
                        break;
                }
            });

            return convertView;
        }
    }

    private void sendToContactUs()
    {
        Intent intent = new Intent(getApplicationContext(),ContactUsActivity.class);
        startActivity(intent);
    }

    private void inviteFriend()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.APP_NAME_CAP));
        intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.INVITE_TEXT));
        intent.setType("image/png");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.famblah_sharing_poster);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Fambalh",null);
        Uri imageUri = Uri.parse(path);
        intent.putExtra(Intent.EXTRA_STREAM,imageUri);
        startActivity(Intent.createChooser(intent,"choose one"));
    }



    private void sendToChangePassword()
    {
        Intent intent = new Intent(getApplicationContext(),ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void sendToAboutUs()
    {
        Intent intent = new Intent(getApplicationContext(),AboutUsActivity.class);
        startActivity(intent);
    }

    private void sendToVerification()
    {
        Intent intent = new Intent(getApplicationContext(),VerificationRequestActivity.class);
        startActivity(intent);
    }

    private void sendToEditProfile()
    {
        Intent intent = new Intent(getApplicationContext(),EditProfileActivity.class);
        startActivity(intent);
    }

    private void doLogout()
    {
        TastyToast.makeText(getApplicationContext(),"Logout Successfully", TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
        sharedPrefHandler.doLogout();
        sendToLogin();
    }

    private void sendToLogin()
    {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finishAffinity();
    }
}