package com.socialcodia.famblah.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelFeed;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.pojo.ResponseFeed;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditFeedActivity extends AppCompatActivity {

    private EditText inputContent;
    private ImageView selectFeedImage,ivFeedImage,inputFeedImage,userProfileImage,feedUserImage;
    private TextView tvFeedContent,tvUserName,tvFeedTimestamp,tvFeedLike,tvFeedComment;
    private Button btnUpdateFeed;
    private CardView cardView;
    private Intent intent;
    private Bitmap bitmap;
    String token,feedId,feedUserId;
    private int feedPrivacy;
    private SharedPrefHandler sharedPrefHandler;
    private Uri filePath;
    private ActionBar actionBar;
    private Spinner privacySpinner;

    String[] privacyType = {"Public","Friends","Only me"};
    String[] privacyDesc = {"Anyone on FAMBLAH","Your friends on FAMBLAH","Only me"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_feed);
        init();

        sharedPrefHandler = SharedPrefHandler.getInstance(getApplicationContext());
        ModelUser modelUser = sharedPrefHandler.getUser();

        token = modelUser.getToken();
        tvUserName.setText(modelUser.getName());
        Picasso.get().load(modelUser.getImage()).into(userProfileImage);
        Picasso.get().load(modelUser.getImage()).into(feedUserImage);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,privacyType);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        privacySpinner.setAdapter(adapter);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Update Feed");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        feedPrivacy = 1;
                        break;
                    case 1:
                        feedPrivacy = 2;
                        break;
                    case 2:
                        feedPrivacy = 3;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        intent = getIntent();
        if (intent.getStringExtra("intentFeedId")!=null)
        {
            feedId = intent.getStringExtra("intentFeedId");
        }
        else
        {
            Toast.makeText(this, R.string.SWW, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }


        selectFeedImage.setOnClickListener(v -> chooseImage());

        inputContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cardView.setVisibility(View.VISIBLE);
                    tvFeedContent.setVisibility(View.VISIBLE);
                    tvFeedContent.setText(s);
                if (s.length()==0)
                {
                    tvFeedContent.setText(null);
                    if (filePath==null)
                    {
                        cardView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvFeedContent.setVisibility(View.GONE);
//        ivFeedImage.setVisibility(View.GONE);

        btnUpdateFeed.setOnClickListener(v -> validateData());

        getFeed();
    }

    private void init() {
        inputContent = findViewById(R.id.inputFeedContent);
        selectFeedImage = findViewById(R.id.selectFeedImage);
        tvFeedContent = findViewById(R.id.tvFeedContent);
        ivFeedImage = findViewById(R.id.ivFeedImage);
        inputFeedImage = findViewById(R.id.inputFeedImage);
        btnUpdateFeed = findViewById(R.id.btnUpdateFeed);
        tvUserName = findViewById(R.id.tvUserName);
        tvFeedTimestamp = findViewById(R.id.tvFeedTimestamp);
        tvFeedContent = findViewById(R.id.tvFeedContent);
        userProfileImage = findViewById(R.id.userProfileImage);
        feedUserImage = findViewById(R.id.feedUserImage);
        tvFeedLike = findViewById(R.id.tvFeedLike);
        tvFeedComment = findViewById(R.id.tvFeedComment);
        cardView = findViewById(R.id.cardView);
        privacySpinner = findViewById(R.id.privacySpinner);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void validateData()
    {
        String content = inputContent.getText().toString().trim();
        if (content.length()<=0 && filePath==null)
        {
            TastyToast.makeText(getApplicationContext(),"Please write something, or select an image",TastyToast.LENGTH_LONG,TastyToast.WARNING);
        }
        else if (filePath!=null)
        {
            File file = new File(getRealPathFromURI(filePath));
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            updateFeedWithImage(content,body);
        }
        else
        {
            updateFeed(content);
        }

    }

    private void getFeed()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            Call<ResponseFeed> call = ApiClient.getInstance().getApi().getFeedById(token,feedId);
            call.enqueue(new Callback<ResponseFeed>() {
                @Override
                public void onResponse(Call<ResponseFeed> call, Response<ResponseFeed> response) {
                    if (response.isSuccessful())
                    {
                        ResponseFeed responseFeed = response.body();
                        if (!responseFeed.getError())
                        {
                            String feedImage = null;
                            ModelFeed modelFeed = responseFeed.getFeed();
                            String feedContent = modelFeed.getFeedContent();
                            String feedType = modelFeed.getFeedType();
                            if (feedType.equals("image")) {
                                feedImage = modelFeed.getFeedImage();
                            }
                            String feedTimestamp = modelFeed.getFeedTimestamp();
                            String feedLikes = modelFeed.getFeedLikes().toString();
                            String feedComments = modelFeed.getFeedComments().toString();

                            inputContent.setText(feedContent);
                            tvFeedContent.setText(feedContent);
                            tvFeedTimestamp.setText(feedTimestamp);
                            tvFeedLike.setText(feedLikes+" Likes");
                            tvFeedComment.setText(feedComments+" Comments");

//                            Picasso.get().load(feedImage).into(ivFeedImage);
                            
                            if (feedType.equals("image"))
                            {
                                if (!feedImage.isEmpty())
                                {
                                    try {
                                        cardView.setVisibility(View.VISIBLE);
                                        ivFeedImage.setVisibility(View.VISIBLE);
                                        Picasso.get().load(feedImage).into(ivFeedImage);
                                    }
                                    catch (Exception e)
                                    {
                                        Toast.makeText(EditFeedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    ivFeedImage.setVisibility(View.GONE);
                                }
                            }

//                            if (liked)
//                            {
//                                tvLike.setVisibility(View.INVISIBLE);
//                                tvUnlike.setVisibility(View.VISIBLE);
//                            }
//                            else
//                            {
//                                tvLike.setVisibility(View.VISIBLE);
//                                tvUnlike.setVisibility(View.INVISIBLE);
//                            }

                        }
                        else
                        {
                            Toast.makeText(EditFeedActivity.this, responseFeed.getMessage(), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseFeed> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void updateFeed(String content)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnUpdateFeed.setEnabled(false);
            String image = "";
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().updateFeed(token,feedId,content);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        TastyToast.makeText(getApplicationContext(),response.body().getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        inputContent.setText("");
                        inputFeedImage.setImageBitmap(null);
                        btnUpdateFeed.setEnabled(true);
                        onBackPressed();
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        btnUpdateFeed.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    t.printStackTrace();
                    btnUpdateFeed.setEnabled(true);
                }
            });
        }
    }

    private RequestBody toRequestBody(String value)
    {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),value);
        return requestBody;
    }

    private void updateFeedWithImage(String content, MultipartBody.Part body)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            Map<String,RequestBody> map = new HashMap<>();
            TastyToast.makeText(getApplicationContext(),"Updating...",TastyToast.LENGTH_LONG,TastyToast.DEFAULT);
            btnUpdateFeed.setEnabled(false);
            String image = "";
            map.put("content",toRequestBody(content));
            map.put("feedId",toRequestBody(feedId));
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().updateFeedWithImage(token,map,body);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        TastyToast.makeText(getApplicationContext(),response.body().getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        inputContent.setText("");
                        inputFeedImage.setImageBitmap(null);
                        btnUpdateFeed.setEnabled(true);
                        onBackPressed();
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        btnUpdateFeed.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnUpdateFeed.setEnabled(true);
                    t.printStackTrace();
                }
            });
        }
    }

    private void chooseImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,200);
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

    private String getTime(Long timestamp)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:a");
        String time = sdf.format(new Date(timestamp));
        return String.valueOf(time);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==200 && resultCode== RESULT_OK && data!=null)
        {
            filePath = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                selectFeedImage.setImageBitmap(bitmap);
                ivFeedImage.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.VISIBLE);
                ivFeedImage.setVisibility(View.VISIBLE);
                ivFeedImage.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                TastyToast.makeText(getApplicationContext(),e.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
            }
        }
    }
    
}