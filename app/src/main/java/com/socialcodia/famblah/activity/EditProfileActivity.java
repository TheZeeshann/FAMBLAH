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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.model.response.ResponseUser;
import com.socialcodia.famblah.storage.Constants;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText inputName, inputUsername, inputBio;
    private Button btnUpdateProfile;
    private ImageView userProfileImage;
    private ActionBar actionBar;
    //    private Bitmap bitmap;
    private Uri filePath;
    String token,image,email,name,username,bio;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();

        actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        ModelUser user = SharedPrefHandler.getInstance(getApplicationContext()).getUser();
        inputName.setText(user.getName());
        inputUsername.setText(user.getUsername());
        inputBio.setText(user.getBio());
        token = user.getToken();
        id = user.getId();
        image = user.getImage();
        email = user.getEmail();

        try {
            Picasso.get().load(user.getImage()).placeholder(R.drawable.user).into(userProfileImage);
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    private void init()
    {
        inputName = findViewById(R.id.inputName);
        inputUsername = findViewById(R.id.inputUsername);
        inputBio = findViewById(R.id.inputBio);
        userProfileImage = findViewById(R.id.userProfileImage);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void chooseImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data!=null)
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                userProfileImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void validateData()
    {
        name = inputName.getText().toString().trim();
        username = inputUsername.getText().toString().trim();
        bio = inputBio.getText().toString().trim();
        if (name.isEmpty())
        {
            inputName.setError("Enter Name");
            inputName.requestFocus();
            return;
        }
        if (name.length()<4 || name.length()>20)
        {
            inputName.setError("Name should be in between 4 to 20 character");
            inputName.requestFocus();
            return;
        }
        if (username.isEmpty())
        {
            inputUsername.setError("Enter Username");
            inputUsername.requestFocus();
            return;
        }
        if (username.length()<4 || username.length()>15)
        {
            inputUsername.setError("Username should be in between 4 to 15 character");
            inputUsername.requestFocus();
            return;
        }
        if (bio.isEmpty())
        {
            inputBio.setText("This user is lazy. So they didn't written any bio.");
            return;
        }
        if (filePath==null)
        {
            updateProfile(name,username,bio);
        }
        else
        {
            File file = new File(getRealPathFromURI(filePath));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
            MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("image",file.getName(),requestFile);
            updateProfileWithImage(name,username,bio,multipartBody);
        }

    }

    private RequestBody toRequestBody(String value)
    {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plane"),value);
        return requestBody;
    }

    private void updateProfileWithImage(String name, String username, String bio, MultipartBody.Part multipartBody)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnUpdateProfile.setEnabled(false);
            Map<String,RequestBody> map = new HashMap<>();
            map.put(Constants.USER_USERNAME,toRequestBody(username));
            map.put(Constants.USER_NAME,toRequestBody(name));
            map.put(Constants.USER_BIO,toRequestBody(bio));

            Call<ResponseUser> call = ApiClient.getInstance().getApi().updateUserWithImage(token,map,multipartBody);
            call.enqueue(new Callback<ResponseUser>() {
                @Override
                public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                    if (response.isSuccessful())
                    {
                        ResponseUser responseUser = response.body();
                        if (!responseUser.getError())
                        {
                            btnUpdateProfile.setEnabled(true);
                            ModelUser user = new ModelUser();
                            user.setBio(bio);
                            user.setUsername(username);
                            user.setName(name);
                            user.setToken(token);
                            SharedPrefHandler.getInstance(getApplicationContext()).saveUser(user);
                            onBackPressed();
                        }
                        else
                        {
                            btnUpdateProfile.setEnabled(true);
                            Toast.makeText(EditProfileActivity.this, responseUser.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        btnUpdateProfile.setEnabled(true);
                        Toast.makeText(EditProfileActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseUser> call, Throwable t) {

                }
            });
        }
    }

    private void updateProfile(String name, String username, String bio)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnUpdateProfile.setEnabled(false);
            Call<ResponseUser> call = ApiClient.getInstance().getApi().updateUser(token,name,username,bio);
            call.enqueue(new Callback<ResponseUser>() {
                @Override
                public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                    if (response.isSuccessful())
                    {
                        ResponseUser responseUser = response.body();
                        if (!responseUser.getError())
                        {
                            btnUpdateProfile.setEnabled(true);
                            Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            ModelUser modelUser = new ModelUser();
                            modelUser.setName(name);
                            modelUser.setUsername(username);
                            modelUser.setBio(bio);
                            modelUser.setToken(token);
                            SharedPrefHandler.getInstance(getApplicationContext()).saveUser(modelUser);
                            onBackPressed();
                        }
                        else
                        {
                            btnUpdateProfile.setEnabled(true);
                            Toast.makeText(EditProfileActivity.this, responseUser.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        btnUpdateProfile.setEnabled(true);
                        Toast.makeText(EditProfileActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseUser> call, Throwable t) {
                    btnUpdateProfile.setEnabled(true);
                    Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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

}