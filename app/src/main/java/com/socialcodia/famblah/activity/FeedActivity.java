package com.socialcodia.famblah.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.socialcodia.famblah.model.ResponseDefault;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.adapter.AdapterComment;
import com.socialcodia.famblah.adapter.AdapterFeed;
import com.socialcodia.famblah.api.Api;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelComment;
import com.socialcodia.famblah.model.ResponseDefault;
import com.socialcodia.famblah.model.ModelFeed;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.model.ResponseComment;
import com.socialcodia.famblah.model.ResponseFeed;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity {

    private TextView tvCommentUserName, tvCommentTimestamp, tvCommentContent, tvCommentLikesCount, btnCommentReply, tvFeedTimestamp, tvFeedContent, tvUserName, tvFeedLike, tvFeedComment, tvLike, tvUnlike, tvComment, tvShare;
    private ImageView ivCommentUserProfileImage, ivCommentOption, btnCommentLike, btnAddComment, ivFeedOption, ivFeedImage, userProfileImage;
    private EditText inputComment;
    private ActionBar actionBar;
    private Intent intent;
    private List<ModelComment> modelCommentList;

    private RecyclerView commentRecyclerView;
    String token,feedId, comment,feedUserId,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        tvCommentUserName = findViewById(R.id.tvCommentUserName);
        tvCommentTimestamp = findViewById(R.id.tvCommentTimestamp);
        tvCommentContent = findViewById(R.id.tvCommentContent);
        tvCommentLikesCount = findViewById(R.id.tvCommentLikesCount);
        btnCommentReply = findViewById(R.id.btnCommentReply);
        ivCommentUserProfileImage = findViewById(R.id.ivCommentUserProfileImage);
        ivCommentOption = findViewById(R.id.ivCommentOption);
        btnCommentLike = findViewById(R.id.btnCommentLike);
        btnAddComment = findViewById(R.id.btnAddComment);
        userProfileImage = findViewById(R.id.userProfileImage);
        tvFeedTimestamp = findViewById(R.id.tvFeedTimestamp);
        tvFeedContent = findViewById(R.id.tvFeedContent);
        tvUserName = findViewById(R.id.tvUserName);
        tvFeedLike = findViewById(R.id.tvFeedLike);
        tvFeedComment = findViewById(R.id.tvFeedComment);
        tvLike = findViewById(R.id.tvLike);
        tvUnlike = findViewById(R.id.tvUnlike);
        tvComment = findViewById(R.id.tvComment);
        tvShare = findViewById(R.id.tvShare);
        userProfileImage = findViewById(R.id.userProfileImage);
        ivFeedOption = findViewById(R.id.ivFeedOption);
        ivFeedImage = findViewById(R.id.ivFeedImage);
        inputComment = findViewById(R.id.inputComment);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Feed");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        modelCommentList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        commentRecyclerView.setLayoutManager(layoutManager);

        ModelUser modelUser = SharedPrefHandler.getInstance(getApplicationContext()).getUser();
        token = modelUser.getToken();
        userId = String.valueOf(modelUser.getId());

        intent = getIntent();
        if (intent.getStringExtra("IntentFeedId")!=null)
        {
            feedId = intent.getStringExtra("IntentFeedId");
        }

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });


        ivFeedOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedActionOption(ivFeedOption,feedId,feedUserId);
            }
        });

        tvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLike(feedId);
            }
        });

        tvUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUnlike(feedId);
            }
        });



        getFeed();
        getComments();

    }

    private void getComments()
    {
        Call<ResponseComment> call = ApiClient.getInstance().getApi().getComments(token,feedId);
        call.enqueue(new Callback<ResponseComment>() {
            @Override
            public void onResponse(Call<ResponseComment> call, Response<ResponseComment> response) {
                if (response.isSuccessful())
                {
                    ResponseComment responseComment = response.body();
                    if (!responseComment.getError())
                    {
                        modelCommentList = responseComment.getComments();
                        AdapterComment adapterComment = new AdapterComment(getApplicationContext(),modelCommentList);
                        commentRecyclerView.setAdapter(adapterComment);
                    }
                    else
                    {
                        Toast.makeText(FeedActivity.this, responseComment.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(FeedActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseComment> call, Throwable t) {
                Toast.makeText(FeedActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void doLike(String feedId)
    {
        tvLike.setVisibility(View.INVISIBLE);
        tvUnlike.setVisibility(View.VISIBLE);
        Call<ResponseFeed> call = ApiClient.getInstance().getApi().doLike(token,Integer.parseInt(feedId));
        call.enqueue(new Callback<ResponseFeed>() {
            @Override
            public void onResponse(Call<ResponseFeed> call, Response<ResponseFeed> response) {
                ResponseFeed responseFeed = response.body();
                if (!responseFeed.getError())
                {
                    ModelFeed modelFeed = responseFeed.getFeed();
                    tvFeedLike.setText(modelFeed.getFeedLikes()+" Likes");
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseFeed> call, Throwable t) {
                tvUnlike.setVisibility(View.INVISIBLE);
                tvLike.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doUnlike(String feedId)
    {
        tvLike.setVisibility(View.VISIBLE);
        tvUnlike.setVisibility(View.INVISIBLE);
        Call<ResponseFeed> call = ApiClient.getInstance().getApi().doDislike(token,Integer.parseInt(feedId));
        call.enqueue(new Callback<ResponseFeed>() {
            @Override
            public void onResponse(Call<ResponseFeed> call, Response<ResponseFeed> response) {
                ResponseFeed responseFeed = response.body();
                if (!responseFeed.getError())
                {
                    ModelFeed modelFeed = responseFeed.getFeed();
                    tvFeedLike.setText(modelFeed.getFeedLikes()+" Likes");
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseFeed> call, Throwable t) {
                tvUnlike.setVisibility(View.VISIBLE);
                tvLike.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFeed()
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
                        ModelFeed modelFeed = responseFeed.getFeed();
                        feedId = modelFeed.getFeedId().toString();
                        String feedContent = modelFeed.getFeedContent();
                        String feedImage = modelFeed.getFeedImage();
                        String feedTimestamp = modelFeed.getFeedTimestamp();
                        feedUserId = modelFeed.getUserId().toString();
                        String userName = modelFeed.getUserName();
                        String userImage = modelFeed.getUserImage();
                        Boolean liked = modelFeed.getLiked();
                        String feedLikes = modelFeed.getFeedLikes().toString();
                        String feedComments = modelFeed.getFeedComments().toString();

                        tvUserName.setText(userName);
                        tvFeedContent.setText(feedContent);
                        tvFeedTimestamp.setText(feedTimestamp);
                        tvFeedLike.setText(feedLikes+" Likes");
                        tvFeedComment.setText(feedComments+" Comments");

                        try {
                            Picasso.get().load(userImage).into(userProfileImage);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(FeedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        if (!feedImage.isEmpty())
                        {
                            try {
                                Picasso.get().load(feedImage).into(ivFeedImage);
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(FeedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            ivFeedImage.setVisibility(View.GONE);
                        }

                        if (liked)
                        {
                            tvLike.setVisibility(View.INVISIBLE);
                            tvUnlike.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            tvLike.setVisibility(View.VISIBLE);
                            tvUnlike.setVisibility(View.INVISIBLE);
                        }

                    }
                    else
                    {
                        Toast.makeText(FeedActivity.this, responseFeed.getMessage(), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
                else
                {
                    Toast.makeText(FeedActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseFeed> call, Throwable t) {
                Toast.makeText(FeedActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showFeedActionOption(ImageView ivFeedOption, String feedId,String feedUserId )
    {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(),ivFeedOption);
        if (feedUserId.equals(userId))
        {
            popupMenu.getMenu().add(Menu.NONE,0,0,"Edit");
            popupMenu.getMenu().add(Menu.NONE,1,1,"Delete");
        }
        popupMenu.getMenu().add(Menu.NONE,2,2,"Share");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                if (id==0)
                {
                    Toast.makeText(FeedActivity.this, "Edit", Toast.LENGTH_SHORT).show();
                }
                else if (id==1)
                {
                    deleteFeed(feedId);
                }
                else if (id==2)
                {
                    Toast.makeText(FeedActivity.this, "Share", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });
        popupMenu.show();
    }

    private void deleteFeed(String feedId)
    {
        Call<ResponseDefault> call = ApiClient.getInstance().getApi().deleteFeed(token,feedId);
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                if (response.isSuccessful())
                {
                    ResponseDefault ResponseDefault = response.body();
                    if (!ResponseDefault.getError())
                    {
                        Toast.makeText(FeedActivity.this, ResponseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                    else
                    {
                        Toast.makeText(FeedActivity.this, ResponseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(FeedActivity.this,"Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                Toast.makeText(FeedActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateData()
    {
        comment = inputComment.getText().toString().trim();
        if (comment.isEmpty())
        {
            inputComment.setError("Can't Add Empty Comment");
            inputComment.requestFocus();
        }
        else
        {
            addComment(feedId,comment);
        }
    }

    private void addComment(String feedId, String comment)
    {
        Call<ResponseDefault> call = ApiClient.getInstance().getApi().postFeedComment(token,feedId,comment);
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                if (response.isSuccessful())
                {
                    ResponseDefault ResponseDefault = response.body();
                    if (!ResponseDefault.getError())
                    {
                        Toast.makeText(FeedActivity.this, ResponseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(FeedActivity.this, ResponseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(FeedActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                Toast.makeText(FeedActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}