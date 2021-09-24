package com.project.network.ssugaeting.activity;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.project.network.ssugaeting.R;
import com.project.network.ssugaeting.databinding.ActivityImageBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    ActivityImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image);
        final String imageURI = getIntent().getStringExtra("SELECTED_IMAGE");
        Picasso.with(this).load(imageURI).resize(1080, 1080).into(binding.ivFullScreenImage);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
