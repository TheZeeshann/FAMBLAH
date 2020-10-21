package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private TextInputEditText inputName, inputEmail, inputMessage;
    private Button btnSubmit;
    private ImageView btnWhatsapp, btnFacebook, btnInstagram,btnTwitter;
    private SharedPrefHandler sharedPrefHandler;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        init();


        actionBar = getSupportActionBar();
        actionBar.setTitle("Contact Us");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        btnSubmit.setOnClickListener(v->{
            validateData();
        });

        btnWhatsapp.setOnClickListener(v -> {
            whatsapp();
        });

        btnFacebook.setOnClickListener(v -> {
            facebook();
        });

        btnInstagram.setOnClickListener(v -> {
            instagram();
        });

        btnTwitter.setOnClickListener(v -> {
            twitter();
        });

    }

    private void twitter()
    {
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/SocialCodia")));
    }

    private void instagram()
    {
        String url = "http://instagram.com/_u/SocialCodia";
        try {
            PackageManager packageManager = getPackageManager();
            packageManager.getPackageInfo("com.instagram.android",PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

        } catch (PackageManager.NameNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://instagram.com/SocialCodia")));
            e.printStackTrace();
        }
    }

    private void facebook()
    {
        String url = "fb://page/SocialCodia";
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch(Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/SocialCodia")));
        }
    }

    private void whatsapp()
    {
        String contact = getString(R.string.DEV_MOBILE);
        String url = "https://api.whatsapp.com/send?phone="+contact;
        try {
            PackageManager packageManager = getPackageManager();
            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            TastyToast.makeText(getApplicationContext(),"Whatsapp not installed in your phone",TastyToast.LENGTH_LONG,TastyToast.ERROR);
        }
    }


    private void validateData()
    {
        String name, email, message;
        name = inputName.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        message = inputMessage.getText().toString().trim();

        if (name.isEmpty())
        {
            inputName.setError(getString(R.string.EN));
            inputName.requestFocus();
            return;
        }
        if (name.length()<3 || name.length()>40)
        {
            inputName.setError(getString(R.string.EVN));
            inputName.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            inputEmail.setError(getString(R.string.EE));
            inputEmail.requestFocus();
            return;
        }
        if (email.length()<10 || email.length()>60 || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            inputEmail.setError(getString(R.string.EVE));
            inputEmail.requestFocus();
        }
        else
        {
            postContactUs(name,email,message);
        }
    }

    private void postContactUs(String name, String email, String message)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnSubmit.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().postContactUs(token,name,email,message);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    btnSubmit.setEnabled(true);
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            onBackPressed();
                            TastyToast.makeText(getApplicationContext(),"Your information has been submitted",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);

                        }
                        else
                        {
                            TastyToast.makeText(getApplicationContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnSubmit.setEnabled(true);
                    t.printStackTrace();
                }
            });
        }
    }

    private void init()
    {
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputMessage = findViewById(R.id.inputMessage);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnWhatsapp = findViewById(R.id.btnWhatsapp);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnInstagram = findViewById(R.id.btnInstagram);
        btnTwitter = findViewById(R.id.btnTwitter);

        sharedPrefHandler = SharedPrefHandler.getInstance(getApplicationContext());
        ModelUser mUser = sharedPrefHandler.getUser();
        token = mUser.getToken();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}