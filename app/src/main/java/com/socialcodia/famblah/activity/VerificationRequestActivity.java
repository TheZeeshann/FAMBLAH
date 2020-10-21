package com.socialcodia.famblah.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationRequestActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private EditText inputName,inputUsername;
    private ImageView inputImage;
    private Button btnSubmit;
    private String name,username,token;
    private SharedPrefHandler sharedPrefHandler;
    private ModelUser mUser;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_request);
        init();

        sharedPrefHandler = SharedPrefHandler.getInstance(getApplicationContext());
        mUser = sharedPrefHandler.getUser();
        token = mUser.getToken();

        actionBar = getSupportActionBar();
        actionBar.setTitle("Verification Request");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        inputImage.setOnClickListener(v -> chooseImage());

        btnSubmit.setOnClickListener(v -> {
            validateData();
        });

    }

    private RequestBody toRequestBody(String value)
    {
        return RequestBody.create(MediaType.parse("text/plane"),value);
    }

    private void chooseImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==200 && resultCode==RESULT_OK && data!=null)
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                inputImage.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void validateData()
    {
        name = inputName.getText().toString().trim();
        username = inputUsername.getText().toString();
        if (name.isEmpty())
        {
            inputName.setError("Enter Name");
            inputName.requestFocus();
            return;
        }
        if (name.length()<3 || name.length()>40)
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
        if (username.length()<3 || username.length()>20)
        {
            inputUsername.setError("Enter Valid Username");
            inputUsername.requestFocus();
            return;
        }
        if (!username.toLowerCase().equals(mUser.getUsername().toLowerCase()))
        {
            inputUsername.setError("Enter Your Username");
            inputUsername.requestFocus();
            return;
        }
        if (filePath == null)
        {
            TastyToast.makeText(getApplicationContext(),"Select A Image",TastyToast.LENGTH_LONG, TastyToast.WARNING);
        }
        else
        {
            File file = new File(getRealPathFromURI(filePath));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("image",file.getName(),requestFile);
            postVerificationRequest(part);
        }

    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    private void postVerificationRequest(MultipartBody.Part part)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(false);
            Map<String,RequestBody> map = new HashMap<>();
            map.put("name",toRequestBody(name));
            map.put("username",toRequestBody(username));
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().postVerificationRequest(token,map,part);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        btnSubmit.setEnabled(true);
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            TastyToast.makeText(getApplicationContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                            onBackPressed();
                        }
                        else
                        {
                            TastyToast.makeText(getApplicationContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        btnSubmit.setEnabled(true);
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t)
                {
                    btnSubmit.setEnabled(true);
                    t.printStackTrace();
                }
            });
        }
    }

    private void init()
    {
        inputName = findViewById(R.id.inputName);
        inputUsername = findViewById(R.id.inputUsername);
        inputImage = findViewById(R.id.inputImage);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}