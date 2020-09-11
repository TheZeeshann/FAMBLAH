package com.socialcodia.famblah.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.socialcodia.famblah.adapter.AdapterFeed;
import com.socialcodia.famblah.fragment.AddFeedFragment;
import com.socialcodia.famblah.pojo.ResponseComment;
import com.socialcodia.famblah.pojo.ResponseDefault;
import com.socialcodia.famblah.R;
import com.socialcodia.famblah.adapter.AdapterComment;
import com.socialcodia.famblah.api.ApiClient;
import com.socialcodia.famblah.model.ModelComment;
import com.socialcodia.famblah.model.ModelFeed;
import com.socialcodia.famblah.model.ModelUser;
import com.socialcodia.famblah.pojo.ResponseComments;
import com.socialcodia.famblah.pojo.ResponseFeed;
import com.socialcodia.famblah.storage.SharedPrefHandler;
import com.socialcodia.famblah.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity {

    private TextView tvCommentUserName, tvCommentTimestamp, tvCommentContent, tvCommentLikesCount, btnCommentReply, tvFeedTimestamp, tvFeedContent, tvUserName, tvFeedLike, tvFeedComment, tvLike, tvUnlike, tvComment, tvShare;
    private ImageView ivCommentUserProfileImage, ivCommentOption, btnCommentLike, btnAddComment, ivFeedOption, ivFeedImage, userProfileImage,feedUserImage,ivUserVerified;
    private EditText inputComment;
    private VideoView feedVideo;
    private ActionBar actionBar;
    private Intent intent;
    private List<ModelComment> modelCommentList;
    AdapterComment adapterComment;
    ModelUser modelUser;

    private RecyclerView commentRecyclerView;
    String token,feedId,feedContent,comment,feedUserId,userId,image;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        init();

        tvComment.setTextColor(this.getResources().getColor(R.color.colorRed));

        actionBar = getSupportActionBar();
        actionBar.setTitle("Feed");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        modelCommentList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        commentRecyclerView.setLayoutManager(layoutManager);

        modelUser = SharedPrefHandler.getInstance(getApplicationContext()).getUser();
        token = modelUser.getToken();
        userId = String.valueOf(modelUser.getId());

        intent = getIntent();
        if (intent.getStringExtra("IntentFeedId")!=null)
        {
            feedId = intent.getStringExtra("IntentFeedId");
        }

        btnAddComment.setOnClickListener(v -> validateData());

        ivFeedOption.setOnClickListener(v -> showFeedActionOption(ivFeedOption,feedId,feedUserId));

        tvLike.setOnClickListener(v -> doLike(feedId));

        tvUnlike.setOnClickListener(v -> doUnlike(feedId));

        tvShare.setOnClickListener(v -> shareFeedWithImage(feedContent));

        ivFeedImage.setOnClickListener(v->sendToZoomImage());

        getFeed();
        getComments();

    }

    public void shareFeedWithImage(String feedContent)
    {
        // Get access to bitmap image from view
        ImageView ivImage = (ImageView) ivFeedImage;
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(ivImage);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT,feedContent);
            shareIntent.setType("image/*");
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Feed"));

        } else {

        }
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "famblah_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init()
    {
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
        feedUserImage = findViewById(R.id.feedUserImage);
        feedVideo = findViewById(R.id.feedVideo);
        tvFeedTimestamp = findViewById(R.id.tvFeedTimestamp);
        tvFeedContent = findViewById(R.id.tvFeedContent);
        tvUserName = findViewById(R.id.tvUserName);
        tvFeedLike = findViewById(R.id.tvFeedLike);
        tvFeedComment = findViewById(R.id.tvFeedComment);
        tvLike = findViewById(R.id.tvLike);
        tvUnlike = findViewById(R.id.tvUnlike);
        tvComment = findViewById(R.id.tvComment);
        tvShare = findViewById(R.id.tvShare);
        ivFeedOption = findViewById(R.id.ivFeedOption);
        ivFeedImage = findViewById(R.id.ivFeedImage);
        inputComment = findViewById(R.id.inputComment);
        commentRecyclerView = findViewById(R.id.commentRecyclerView);
        ivUserVerified = findViewById(R.id.ivUserVerified);
        setTextViewDrawableColor(tvComment, R.color.colorRed);
    }

    private void getComments()
    {
        Call<ResponseComments> call = ApiClient.getInstance().getApi().getComments(token,feedId);
        call.enqueue(new Callback<ResponseComments>() {
            @Override
            public void onResponse(Call<ResponseComments> call, Response<ResponseComments> response) {
                if (response.isSuccessful())
                {
                    ResponseComments responseComment = response.body();
                    if (!responseComment.getError())
                    {
                        modelCommentList = responseComment.getComments();
                        adapterComment = new AdapterComment(getApplicationContext(),modelCommentList);
                        commentRecyclerView.setAdapter(adapterComment);
                    }
                }
                else
                {
                    Toast.makeText(FeedActivity.this, "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseComments> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void sendToZoomImage()
    {
        Intent intent = new Intent(getApplicationContext(),ZoomImageActivity.class);
        intent.putExtra("intentImage",image);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void doLike(String feedId)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
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
                    t.printStackTrace();
                }
            });
        }
    }

    private void doUnlike(String feedId)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
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
                    t.printStackTrace();
                }
            });
        }
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
                        feedContent = modelFeed.getFeedContent();
                        String feedImage = modelFeed.getFeedImage();
                        image = modelFeed.getFeedImage();
                        String feedTimestamp = modelFeed.getFeedTimestamp();
                        feedUserId = modelFeed.getUserId().toString();
                        String userName = modelFeed.getUserName();
                        String userImage = modelFeed.getUserImage();
                        Boolean liked = modelFeed.getLiked();
                        String feedLikes = modelFeed.getFeedLikes().toString();
                        String feedComments = modelFeed.getFeedComments().toString();
                        int verified = modelFeed.getUserVerified();
                        String feedType = modelFeed.getFeedType();
                        String feedVideoUrl = modelFeed.getFeedVideo();

                        if (feedType.equals("video"))
                        {
                            Toast.makeText(FeedActivity.this, "Feed Video Url Is "+feedVideoUrl, Toast.LENGTH_SHORT).show();
                            feedVideo.setVisibility(View.VISIBLE);
                            MediaController mediaController = new MediaController(FeedActivity.this);
                            mediaController.setAnchorView(feedVideo);
                            feedVideo.setMediaController(mediaController);
                            feedVideo.seekTo(100);
                            feedVideo.requestFocus();
                            feedVideo.setVideoURI(Uri.parse(feedVideoUrl));
//                            feedVideo.setVideoPath(feedVideoUrl);
                            feedVideo.setOnPreparedListener(MediaPlayer::start);
                        }
                        else if (feedType.equals("image"))
                        {
                            ivFeedImage.setVisibility(View.VISIBLE);
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
                        }

                        if (verified==0)
                        {
                            ivUserVerified.setVisibility(View.GONE);
                        }
                        else
                        {
                            ivUserVerified.setVisibility(View.VISIBLE);
                        }

                        tvUserName.setText(userName);
                        tvFeedContent.setText(feedContent);
                        tvFeedTimestamp.setText(feedTimestamp);
                        tvFeedLike.setText(feedLikes+" Likes");
                        tvFeedComment.setText(feedComments+" Comments");

                        if (!feedContent.isEmpty())
                        {
                            tvFeedContent.setText(feedContent);
                        }
                        else
                        {
                            tvFeedContent.setVisibility(View.GONE);
                        }

                        try {
                            Picasso.get().load(userImage).into(feedUserImage);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(FeedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                t.printStackTrace();
            }
        });
    }

    private void showFeedActionOption(ImageView ivFeedOption, String feedId,String feedUserId )
    {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(),ivFeedOption);
        if (feedUserId.equals(userId))
        {
            popupMenu.getMenu().add(Menu.NONE,1,1,"Delete");
        }
        popupMenu.getMenu().add(Menu.NONE,2,2,"Report");
        popupMenu.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();
            if (id==1)
            {
                deleteFeedAlert(feedId);
            }
            else if (id==2)
            {
//                    reportFeed(feedId);
                reportFeedAlert(feedId);
            }

            return false;
        });
        popupMenu.show();
    }

    private void reportFeedAlert(String feedId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedActivity.this);
        builder.setTitle("Report");
        builder.setMessage("Are you sure want to report this feed?");
        builder.setPositiveButton("Yes",((dialog, which) -> {
            reportFeed(feedId);
        }));
        builder.setNegativeButton("No",((dialog, which) -> {
            dialog.dismiss();
        }));
        builder.create().show();
    }

    private void deleteFeedAlert(String feedId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure want to delete?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteFeed(feedId);
        });
        builder.setNegativeButton("No", ((dialog, which) -> {
            dialog.dismiss();
        }));
        builder.create().show();
    }

    private void reportFeed(String feedId)
    {
        Call<ResponseDefault> call = ApiClient.getInstance().getApi().reportFeed(SharedPrefHandler.getInstance(getApplicationContext()).getUser().getToken(),Integer.valueOf(feedId));
        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                if (response.isSuccessful())
                {
                    ResponseDefault ResponseDefault = response.body();
                    Toast.makeText(getApplicationContext(), ResponseDefault.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Server Not Responding", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void deleteFeed(String feedId)
    {
        if (Utils.isNetworkAvailable(getApplicationContext()))
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
                    t.printStackTrace();
                }
            });
        }
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
        if (Utils.isNetworkAvailable(getApplicationContext()))
        {
            Call<ResponseComment> call = ApiClient.getInstance().getApi().postFeedComment(token,feedId,comment);
            call.enqueue(new Callback<ResponseComment>() {
                @Override
                public void onResponse(Call<ResponseComment> call, Response<ResponseComment> response) {
                    if (response.isSuccessful())
                    {
                        ResponseComment responseComment = response.body();
                        if (!responseComment.getError())
                        {
                            inputComment.setText("");
                            if (modelCommentList.size()>0)
                            {
                                ModelComment modelComment = responseComment.getComments();
                                modelCommentList.add(modelComment);
                                adapterComment.notifyDataSetChanged();
                            }
                            else
                            {
                                getComments();
                            }
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
                    t.printStackTrace();
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(getColor(color), PorterDuff.Mode.SRC_IN));
            }
        }
    }
}