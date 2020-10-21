package com.socialcodia.famblah.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.adapter.AdapterFeed;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelFeed;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.pojo.ResponseFeeds;
import com.socialcodia.famblah.pojo.ResponseUser;
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
    private ImageView userProfileImage,ivUserVerified;
    private Button btnAddFriend, btnUnFriend, btnCancelFriendRequest,btnAcceptFriendRequest,btnRejectFriendRequest,btnUnblock;

    private RecyclerView recyclerView;
    List<ModelFeed> modelFeedList;
    List<ModelUser> modelUserList;
    private Intent intent;
    private ActionBar actionBar;
    private LinearLayout layoutFriend;

    String token,username,name,id, image;
    int hisUserId,userStatus;
    private boolean isVisible = true;
    private Menu menu;


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

        btnUnblock.setOnClickListener(v->showUnblockAlert());

        userProfileImage.setOnClickListener(v -> {
            sendToZoomImage();
        });

        layoutFriend.setOnClickListener(v -> sendToFriends());

        getPostByUsername();
        getUser();
    }

    private void sendToFriends()
    {
        Intent intent = new Intent(getApplicationContext(),FriendsActivity.class);
        intent.putExtra("intentUsername",username);
        startActivity(intent);
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
        btnUnblock = findViewById(R.id.btnUnblock);
        recyclerView = findViewById(R.id.feedRecyclerView);
        tvFeedsCount = findViewById(R.id.tvFeedsCount);
        ivUserVerified = findViewById(R.id.ivUserVerified);
        layoutFriend = findViewById(R.id.layoutFriend);
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
                            TastyToast.makeText(getApplicationContext(),"Friend Request Rejected",TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                            btnRejectFriendRequest.setEnabled(true);
                            btnRejectFriendRequest.setVisibility(View.GONE);
                            btnAcceptFriendRequest.setVisibility(View.GONE);
                            btnAddFriend.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            btnRejectFriendRequest.setEnabled(true);
                            TastyToast.makeText(getApplicationContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        btnRejectFriendRequest.setEnabled(true);
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnRejectFriendRequest.setEnabled(true);
                    t.printStackTrace();
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
                            TastyToast.makeText(getApplicationContext(), responseDefault.getMessage(), TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                        }
                        else
                        {
                            btnAcceptFriendRequest.setEnabled(true);
                            TastyToast.makeText(getApplicationContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        btnAcceptFriendRequest.setEnabled(true);
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnAcceptFriendRequest.setEnabled(true);
                    t.printStackTrace();
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
                            TastyToast.makeText(getApplicationContext(),responseDefault.getMessage(),TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
                        }
                    }
                    else
                    {
                        btnCancelFriendRequest.setEnabled(true);
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnCancelFriendRequest.setEnabled(true);
                    t.printStackTrace();
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
                            TastyToast.makeText(getApplicationContext(), responseDefault.getMessage(), TastyToast.LENGTH_LONG, TastyToast.WARNING);
                        }
                        else
                        {
                            btnUnFriend.setEnabled(true);
                            TastyToast.makeText(getApplicationContext(), responseDefault.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        btnUnFriend.setEnabled(true);
                        TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnUnFriend.setEnabled(true);
                    t.printStackTrace();
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
                            TastyToast.makeText(getApplicationContext(), "Friend Request Sent", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                        }
                        else
                        {
                            TastyToast.makeText(getApplicationContext(), responseDefault.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        }
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(), String.valueOf(R.string.SNR),Toast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseDefault> call, Throwable t) {
                    btnAddFriend.setEnabled(true);
                    t.printStackTrace();
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
                        name = modelUser.getName();
                        hisUserId = modelUser.getId();
                        id = String.valueOf(modelUser.getId());
                        username = modelUser.getUsername();
                        String bio = modelUser.getBio();
                        image = modelUser.getImage();
                        userStatus = modelUser.getStatus();
                        String feedCount = String.valueOf(modelUser.getFeedsCount());
                        String friendsCount = String.valueOf(modelUser.getFriendsCount());
                        int friendShipStatus = modelUser.getFriendshipStatus();
                        int status = modelUser.getStatus();


                        tvUserName.setText(name);
                        tvUserUsername.setText("@"+username);
                        tvUserBio.setText(bio);
                        tvFeedsCount.setText(feedCount);
                        tvFriendsCount.setText(friendsCount);

                        if (status==0)
                        {
                            ivUserVerified.setVisibility(View.GONE);
                        }
                        else
                        {
                            ivUserVerified.setVisibility(View.VISIBLE);
                        }

                        if (userStatus!=2)
                        {
                            isVisible = true;
                            invalidateOptionsMenu();
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
                        }
                        else
                            btnUnblock.setVisibility(View.VISIBLE);

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
                    TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseUser> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        menu.findItem(R.id.miUserBlock).setVisible(isVisible);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.miUserBlock:
                showBlockAlert();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showBlockAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Are you sure want to block?");
        builder.setMessage(name+" will no longer be able to:\n\n\t○ See things you post on your timeline\n\t○ Add you as a friend\n\nIf you're friends, blocking "+name+" will also unfriend him");
        builder.setPositiveButton("Block", (dialog, which) -> doBlock() );
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
 
    private void showUnblockAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Are you sure want to Unblock?");
        builder.setMessage(name+" will be able to:\n\n\t○ See things you post on your timeline\n\t○ Add you as a friend");
        builder.setPositiveButton("UnBlock", (dialog, which) -> doUnblock());
        builder.setNegativeButton("Cancel", (dialog, which) ->doUnblock());
        builder.create().show();
    }

    private void doBlock()
    {
        Call<ResponseDefault> call = ApiClient.getInstance().getApi().doBlock(token,hisUserId);
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                if (response.isSuccessful())
                {
                    ResponseDefault responseDefault = response.body();
                    if (!responseDefault.getError())
                    {
                        isVisible = false;
                        invalidateOptionsMenu();
                        btnAddFriend.setVisibility(View.GONE);
                        btnAcceptFriendRequest.setVisibility(View.GONE);
                        btnCancelFriendRequest.setVisibility(View.GONE);
                        btnRejectFriendRequest.setVisibility(View.GONE);
                        btnUnFriend.setVisibility(View.GONE);
                        btnUnblock.setVisibility(View.VISIBLE);
                    }
                    else
                        btnUnblock.setVisibility(View.GONE);
                    TastyToast.makeText(getApplicationContext(), responseDefault.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void doUnblock()
    {
        Call<ResponseDefault> call = ApiClient.getInstance().getApi().doUnblock(token,hisUserId);
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                if (response.isSuccessful())
                {
                    ResponseDefault responseDefault = response.body();
                    if (!responseDefault.getError())
                    {
                        isVisible = true;
                        invalidateOptionsMenu();
                        btnUnblock.setVisibility(View.GONE);
                        btnAddFriend.setVisibility(View.VISIBLE);
                    }
                    TastyToast.makeText(getApplicationContext(), responseDefault.getMessage(), TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                }
            }
            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getPostByUsername()
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
                        AdapterFeed adapterFeed = new AdapterFeed(modelFeedList,ProfileActivity.this);
                        recyclerView.setAdapter(adapterFeed);
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(),responseFeeds.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }
                else
                {
                    TastyToast.makeText(getApplicationContext(),String.valueOf(R.string.SNR),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseFeeds> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}