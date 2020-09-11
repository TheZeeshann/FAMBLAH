package com.socialcodia.famblah.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.VideoView;

import com.socialcodia.famblah.R;

public class VideoPlayerActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);

        videoView.setVideoPath("http://10.0.2.2/SocialApiFriendsSystemVideo/public/uploads/videos/5f47a94a1be62.mp4");
        videoView.setOnPreparedListener(mp->{
            mp.start();
        });

    }
}