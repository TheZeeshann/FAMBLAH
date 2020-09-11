package com.socialcodia.famblah.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.socialcodia.famblah.R;
import com.squareup.picasso.Picasso;

public class ZoomImageActivity extends AppCompatActivity {

    private Intent intent;
    String zoomImage;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        PhotoView photoView = (PhotoView) findViewById(R.id.ivZoomImage);
        btnBack = findViewById(R.id.btnBack);

        intent  = getIntent();

        if (intent.getStringExtra("intentImage")!=null)
        {
            zoomImage = intent.getStringExtra("intentImage");
            try {
                Picasso.get().load(zoomImage).into(photoView);
            }
            catch (Exception e)
            {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            onBackPressed();
        }

        btnBack.setOnClickListener(v->{
            onBackPressed();
        });

    }


}