package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.socialcodia.famblah.R;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.storage.SharedPrefHandler;

public class FeedLikedUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConstraintLayout userRowLayout;
    private ImageView userProfileImage,ivUserVerified;
    private TextView tvUserName,tvLikeTime;
    private ActionBar actionBar;
    private SharedPrefHandler sp;
    private String token, username;
    private int userId;
    private ModelUser modelUser;

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
        userRowLayout.setOnClickListener(v->sendToProfileActivity());
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
    }

    private void sendToProfileActivity()
    {
        Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
        startActivity(intent);
    }

}