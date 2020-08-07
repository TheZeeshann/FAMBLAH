package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.adapter.AdapterFeed;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelFeed;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.model.response.ResponseDefault;
import com.socialcodia.famblah.model.response.ResponseFeeds;
import com.socialcodia.famblah.model.response.ResponseUser;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName,tvUserUsername,tvUserBio,tvFeedsCount,tvFriendsCount;
    private ImageView userProfileImage;
    private Button btnAddFriend, btnUnFriend, btnCancelFriendRequest,btnAcceptFriendRequest,btnRejectFriendRequest;

    private RecyclerView recyclerView;
    List<ModelFeed> modelFeedList;
    List<ModelUser> modelUserList;
    private Intent intent;
    private ActionBar actionBar;

    String token,username,id, image;
    int hisUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        intent = getIntent();
        if (intent.getStringExtra("IntentUsername")!=null)
        {
            username = intent.getStringExtra("IntentUsername");
        }

        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        modelFeedList = new ArrayList<>();
        modelUserList = new ArrayList<>();

        ModelUser modelUser = SharedPrefHandler.getInstance(getApplicationContext()).getUser();
        token = modelUser.getToken();

        btnAddFriend.setOnClickListener(v -> {
            sendFriendRequest();
        });

        btnUnFriend.setOnClickListener(v->{
            unFriend();
        });

        btnCancelFriendRequest.setOnClickListener(v -> {
            cancelFriendRequest();
        });

        btnAcceptFriendRequest.setOnClickListener(v -> {
            acceptFriendRequest();
        });

        btnRejectFriendRequest.setOnClickListener(v -> {
            rejectFriendRequest();
        });

        userProfileImage.setOnClickListener(v -> {
//            sendToZoomImage();
        });

        getPostByUsername();
        getUser();
    }

    private void sendToZoomImage()
    {
        Intent intent = new Intent(getApplicationContext(),ZoomImageActivity.class);
        intent.putExtra("intentImage",image);
        startActivity(intent);
    }

    private void init()
    {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserUsername = findViewById(R.id.tvUserUsername);
        userProfileImage = findViewById(R.id.userProfileImage);
        tvUserBio = findViewById(R.id.tvUserBio);
        tvFriendsCount = findViewById(R.id.tvFriendsCount);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnUnFriend = findViewById(R.id.btnUnFriend);
        btnCancelFriendRequest = findViewById(R.id.btnCancelFriendRequest);
        btnAcceptFriendRequest = findViewById(R.id.btnAcceptFriendRequest);
        btnRejectFriendRequest = findViewById(R.id.btnRejectFriendRequest);
        recyclerView = findViewById(R.id.feedRecyclerView);
        tvFeedsCount = findViewById(R.id.tvFeedsCount);
    }

    private void rejectFriendRequest()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnRejectFriendRequest.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().cancelFriendRequest(token,hisUserId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            btnRejectFriendRequest.setEnabled(true);
                            btnRejectFriendRequest.setVisibility(View.GONE);
                            btnAcceptFriendRequest.setVisibility(View.GONE);
                            btnAddFriend.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            btnRejectFriendRequest.setEnabled(true);
                            Toast.makeText(ProfileActivity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        btnRejectFriendRequest.setEnabled(true);
                        Toast.makeText(ProfileActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnRejectFriendRequest.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void acceptFriendRequest()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnAcceptFriendRequest.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().acceptFriendRequest(token,hisUserId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            btnAcceptFriendRequest.setEnabled(true);
                            btnRejectFriendRequest.setVisibility(View.GONE);
                            btnAcceptFriendRequest.setVisibility(View.GONE);
                            btnUnFriend.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            btnAcceptFriendRequest.setEnabled(true);
                            Toast.makeText(ProfileActivity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        btnAcceptFriendRequest.setEnabled(true);
                        Toast.makeText(ProfileActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnAcceptFriendRequest.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cancelFriendRequest()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnCancelFriendRequest.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().cancelFriendRequest(token,hisUserId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            btnCancelFriendRequest.setEnabled(true);
                            btnCancelFriendRequest.setVisibility(View.GONE);
                            btnAddFriend.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            btnCancelFriendRequest.setEnabled(true);
                            Toast.makeText(ProfileActivity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        btnCancelFriendRequest.setEnabled(true);
                        Toast.makeText(ProfileActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnCancelFriendRequest.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void unFriend()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnUnFriend.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().deleteFriend(token,hisUserId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            btnUnFriend.setEnabled(true);
                            btnUnFriend.setVisibility(View.GONE);
                            btnAddFriend.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            btnUnFriend.setEnabled(true);
                            Toast.makeText(ProfileActivity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        btnUnFriend.setEnabled(true);
                        Toast.makeText(ProfileActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnUnFriend.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendFriendRequest()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            btnAddFriend.setEnabled(false);
            Call<ResponseDefault> call = ApiClient.getInstance().getApi().sendFriendRequest(token,hisUserId);
            call.enqueue(new Callback<ResponseDefault>() {
                @Override
                public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                    if (response.isSuccessful())
                    {
                        ResponseDefault responseDefault = response.body();
                        if (!responseDefault.getError())
                        {
                            btnAddFriend.setEnabled(true);
                            btnAddFriend.setVisibility(View.GONE);
                            btnCancelFriendRequest.setVisibility(View.VISIBLE);
                            Toast.makeText(ProfileActivity.this, "Friend Request Sent", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ProfileActivity.this, responseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(ProfileActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnAddFriend.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void getUser()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            Call<ResponseUser> call = ApiClient.getInstance().getApi().getUserByUsername(token,username);
            call.enqueue(new Callback<ResponseUser>() {
                @Override
                public void onResponse(Call<ResponseUser> call, Response<ResponseUser> response) {
                    if (response.isSuccessful())
                    {
                        ResponseUser responseUser = response.body();
                        if (!responseUser.getError())
                        {
                            ModelUser modelUser = responseUser.getUser();
                            String name = modelUser.getName();
                            hisUserId = modelUser.getId();
                            id = String.valueOf(modelUser.getId());
                            String username = modelUser.getUsername();
                            String bio = modelUser.getBio();
                            image = modelUser.getImage();
                            String feedCount = String.valueOf(modelUser.getFeedsCount());
                            String friendsCount = String.valueOf(modelUser.getFriendsCount());
                            int friendShipStatus = modelUser.getFriendshipStatus();

                            tvUserName.setText(name);
                            tvUserUsername.setText("@"+username);
                            tvUserBio.setText(bio);
                            tvFeedsCount.setText(feedCount);
                            tvFriendsCount.setText(friendsCount);
                            if (friendShipStatus==0)
                            {
                                btnAddFriend.setVisibility(View.VISIBLE);
                            }
                            else if (friendShipStatus==1)
                            {
                                btnAddFriend.setVisibility(View.GONE);
                                btnUnFriend.setVisibility(View.VISIBLE);
                            }
                            else if (friendShipStatus==2)
                            {
                                btnAddFriend.setVisibility(View.GONE);
                                btnCancelFriendRequest.setVisibility(View.VISIBLE);
                            }
                            else if (friendShipStatus==3)
                            {
                                btnAddFriend.setVisibility(View.GONE);
                                btnUnFriend.setVisibility(View.GONE);
                                btnAcceptFriendRequest.setVisibility(View.VISIBLE);
                                btnRejectFriendRequest.setVisibility(View.VISIBLE);
                            }
                            try {
                                Picasso.get().load(image).placeholder(R.drawable.user).into(userProfileImage);
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            onBackPressed();
                            Toast.makeText(ProfileActivity.this, responseUser.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(ProfileActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseUser> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getPostByUsername()
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            Call<ResponseFeeds> call = ApiClient.getInstance().getApi().getUserFeeds(username,token);
            call.enqueue(new Callback<ResponseFeeds>() {
                @Override
                public void onResponse(Call<ResponseFeeds> call, Response<ResponseFeeds> response) {
                    if (response.isSuccessful())
                    {
                        ResponseFeeds responseFeeds = response.body();
                        if (!responseFeeds.getError())
                        {
                            modelFeedList = responseFeeds.getFeeds();
                            AdapterFeed adapterFeed = new AdapterFeed(modelFeedList,getApplicationContext());
                            recyclerView.setAdapter(adapterFeed);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), responseFeeds.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseFeeds> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}