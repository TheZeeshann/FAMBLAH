package com.socialcodia.famblah.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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
import com.socialcodia.famblah.activity.MainActivity;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseDefault;
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

import static android.app.Activity.RESULT_OK;

public class AddFeedFragment extends Fragment {

    private EditText inputContent;
    private ImageView selectFeedImage,selectFeedVideo,ivFeedImage,inputFeedImage,userProfileImage,feedUserImage,ivUserVerified,ivUserVerifiedSocialCodia,ivPublicIcon,ivFriendIcon,ivPrivateIcon;
    private TextView tvFeedContent, tvUserName,tvUserNameSocialCodia, tvFeedTimestamp;
    private Button btnPostFeed;
    private CardView cardView;
    private Bitmap bitmap;
    private Spinner privacySpinner;
    String[] privacyTitle = {"Public","Friends","Only me"};
    String[] privacyDescription = {"Anyone on Famblah","Your friends on Famblah","Only me"};
    String token;
    Uri filePath, filePathVideo;
    int feedPrivacy = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_feed, container, false);
        init(view);
        setHasOptionsMenu(true);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,privacyTitle);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        privacySpinner.setAdapter(arrayAdapter);
        ModelUser modelUser = SharedPrefHandler.getInstance(getContext()).getUser();
        token = modelUser.getToken();
        tvUserName.setText(modelUser.getName());
        tvUserNameSocialCodia.setText(modelUser.getName());
        Picasso.get().load(modelUser.getImage()).into(userProfileImage);
        Picasso.get().load(modelUser.getImage()).into(feedUserImage);

        if (modelUser.getStatus()==1) {
            ivUserVerified.setVisibility(View.VISIBLE);
            ivUserVerifiedSocialCodia.setVisibility(View.VISIBLE);
        }
        else {
            ivUserVerified.setVisibility(View.GONE);
            ivUserVerifiedSocialCodia.setVisibility(View.GONE);
        }


        ivPrivateIcon.setVisibility(View.GONE);
        ivFriendIcon.setVisibility(View.GONE);
        ivPublicIcon.setVisibility(View.VISIBLE);

        privacySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position)
                {
                    case 0:
                        feedPrivacy = 1;
                        ivPrivateIcon.setVisibility(View.GONE);
                        ivFriendIcon.setVisibility(View.GONE);
                        ivPublicIcon.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        feedPrivacy = 2;
                        ivPrivateIcon.setVisibility(View.GONE);
                        ivPublicIcon.setVisibility(View.GONE);
                        ivFriendIcon.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        feedPrivacy = 3;
                        ivPublicIcon.setVisibility(View.GONE);
                        ivFriendIcon.setVisibility(View.GONE);
                        ivPrivateIcon.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectFeedImage.setOnClickListener(v->chooseImage());

        selectFeedVideo.setOnClickListener(v-> chooseVideo());

        inputContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()!=0)
                {
                    cardView.setVisibility(View.VISIBLE);
                    tvFeedContent.setVisibility(View.VISIBLE);
                    tvUserName.setVisibility(View.VISIBLE);
                    tvFeedContent.setText(s);
                }
                else
                {
                    tvFeedContent.setText(null);
                    if (filePath==null)
                        cardView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cardView.setVisibility(View.GONE);
        tvFeedContent.setVisibility(View.GONE);
        ivFeedImage.setVisibility(View.GONE);
        tvFeedTimestamp.setText(getTime(System.currentTimeMillis()));

        btnPostFeed.setOnClickListener(v -> validateData());

        return view;
    }

    private void init(View view)
    {
        inputContent = view.findViewById(R.id.inputFeedContent);
        selectFeedImage = view.findViewById(R.id.selectFeedImage);
        selectFeedVideo = view.findViewById(R.id.selectFeedVideo);
        tvFeedContent = view.findViewById(R.id.tvFeedContent);
        ivFeedImage = view.findViewById(R.id.ivFeedImage);
        inputFeedImage = view.findViewById(R.id.inputFeedImage);
        btnPostFeed = view.findViewById(R.id.btnPostFeed);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserNameSocialCodia = view.findViewById(R.id.tvUserNameSocialCodia);
        ivUserVerifiedSocialCodia = view.findViewById(R.id.ivUserVerifiedSocialCodia);
        tvFeedTimestamp = view.findViewById(R.id.tvFeedTimestamp);
        tvFeedContent = view.findViewById(R.id.tvFeedContent);
        userProfileImage = view.findViewById(R.id.userProfileImage);
        feedUserImage = view.findViewById(R.id.feedUserImage);
        ivUserVerified = view.findViewById(R.id.ivUserVerified);
        ivPublicIcon = view.findViewById(R.id.ivPublicIcon);
        ivFriendIcon = view.findViewById(R.id.ivFriendIcon);
        ivPrivateIcon = view.findViewById(R.id.ivPrivateIcon);
        cardView = view.findViewById(R.id.cardView);
        privacySpinner = view.findViewById(R.id.privacySpinner);
    }

    private void validateData()
    {
        String content = inputContent.getText().toString().trim();
        if (content.length()<=0 && filePath==null)
        {
            TastyToast.makeText(getContext(),"Can't post empty feed",TastyToast.LENGTH_LONG,TastyToast.WARNING);
        }
        else if (filePath!=null || filePathVideo!=null)
        {
            File file;
            if (filePath!=null)
                file = new File(getRealPathFromURI(filePath));
            else
                file = new File(getVideoRealPathFromURI(filePathVideo));

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            postFeedWithImage(content,body);
        }
        else
        {
            postFeed(content);
        }

    }

    public void setFragment(Fragment fragment)
    {
        ((MainActivity)getActivity()).setFragment(fragment);
    }

    private void postFeed(String content)
    {
        if (Utils.isNetworkAvailable(getContext()))
        {
            btnPostFeed.setEnabled(false);
            String image = "";
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().postFeed(token,feedPrivacy,content);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        TastyToast.makeText(getContext(),response.body().getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        inputContent.setText("");
                        inputFeedImage.setImageBitmap(null);
                        btnPostFeed.setEnabled(true);
                        Fragment fragment = new HomeFragment();
                        setFragment(fragment);

                    }
                    else
                    {
                        TastyToast.makeText(getContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        btnPostFeed.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    t.printStackTrace();
                    btnPostFeed.setEnabled(true);
                }
            });
        }
    }

    private RequestBody toRequestBody(String value)
    {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),value);
        return requestBody;
    }

    private void postFeedWithImage(String content, MultipartBody.Part body)
    {
        if (Utils.isNetworkAvailable(getContext()))
        {
            Map<String,RequestBody> map = new HashMap<>();
            TastyToast.makeText(getContext(),"Posting...",TastyToast.LENGTH_LONG,TastyToast.DEFAULT);
            btnPostFeed.setEnabled(false);
            String image = "";
            map.put("content",toRequestBody(content));
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().postFeedWithImage(token,feedPrivacy,map,body);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        TastyToast.makeText(getContext(),response.body().getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        inputContent.setText("");
                        inputFeedImage.setImageBitmap(null);
                        btnPostFeed.setEnabled(true);
                        Fragment fragment = new HomeFragment();
                        setFragment(fragment);
                    }
                    else
                    {
                        TastyToast.makeText(getContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        btnPostFeed.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    t.printStackTrace();
                    btnPostFeed.setEnabled(true);
                }
            });
        }
    }

    private void chooseVideo()
    {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,300);
    }

    private void chooseImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,200);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(), contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private String getVideoRealPathFromURI(Uri contentUri)
    {
        String[] projection = {MediaStore.Video.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(),contentUri,projection,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
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
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filePath);
                selectFeedImage.setImageBitmap(bitmap);
                ivFeedImage.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.VISIBLE);
                ivFeedImage.setVisibility(View.VISIBLE);
                ivFeedImage.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode==300 && resultCode==RESULT_OK && data!=null)
        {
            Toast.makeText(getContext(), "Video Selected", Toast.LENGTH_SHORT).show();
            filePathVideo = data.getData();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.miSettings).setVisible(false);
        menu.findItem(R.id.miPostFeed).setVisible(false);
        menu.findItem(R.id.miSearch).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}