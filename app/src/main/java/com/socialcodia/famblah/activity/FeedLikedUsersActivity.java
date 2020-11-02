package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.tastytoast.TastyToast;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.adapter.AdapterUser;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseUser;
import com.socialcodia.famblah.pojo.ResponseUsers;
import com.socialcodia.famblah.storage.SharedPrefHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedLikedUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConstraintLayout userRowLayout;
    private ImageView userProfileImage,ivUserVerified;
    private TextView tvUserName,tvLikeTime;
    private ActionBar actionBar;
    private SharedPrefHandler sp;
    private String token, username;
    private int userId,feedId;
    private Intent intent;
    private ModelUser modelUser;
    private List<ModelUser> modelUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_liked_users);
        init();


        addListener();
    }

    private void addListener()
    {
        actionBar.setTitle("Likes User");
//        userRowLayout.setOnClickListener(v->sendToProfileActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (intent.hasExtra("intentFeedId"))
            feedId = Integer.parseInt(intent.getStringExtra("intentFeedId"));
        else
        {
            TastyToast.makeText(getApplicationContext(),"Failed To Fetch Feed Id",TastyToast.LENGTH_LONG,TastyToast.ERROR);
            onBackPressed();
        }

        getUsers();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void getUsers() {
        Call<ResponseUsers> call = ApiClient.getInstance().getApi().getLikedUsers(token,feedId);
        call.enqueue(new Callback<ResponseUsers>() {
            @Override
            public void onResponse(Call<ResponseUsers> call, Response<ResponseUsers> response) {
                if (response.isSuccessful())
                {
                    ResponseUsers responseUsers = response.body();
                    if (!responseUsers.getError())
                    {
                        modelUserList = responseUsers.getUsers();
                        AdapterUser adapterUser = new AdapterUser(modelUserList,getApplicationContext());
                        recyclerView.setAdapter(adapterUser);
                    }
                    else
                    {
                        TastyToast.makeText(getApplicationContext(),responseUsers.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseUsers> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private void init()
    {
        recyclerView = findViewById(R.id.likesRecyclerView);
        userRowLayout = findViewById(R.id.userRowLayout);
        userProfileImage = findViewById(R.id.userProfileImage);
        ivUserVerified = findViewById(R.id.ivUserVerified);
        tvUserName = findViewById(R.id.tvUserName);
        tvLikeTime = findViewById(R.id.tvLikeTime);
        actionBar = getSupportActionBar();
        sp = SharedPrefHandler.getInstance(getApplicationContext());
        modelUser = sp.getUser();
        token = modelUser.getToken();
        username = modelUser.getUsername();
        userId = modelUser.getId();
        modelUserList = new ArrayList<>();
        intent = getIntent();
    }

    private void sendToProfileActivity()
    {
        Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
        startActivity(intent);
    }

}