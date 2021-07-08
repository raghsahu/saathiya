package com.prometteur.sathiya.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.prometteur.sathiya.R;

public class FullScreenImageActivity extends AppCompatActivity {

    private ImageView imageView,ivBackArrowimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_image);


        imageView = findViewById(R.id.imageView);
        ivBackArrowimg = findViewById(R.id.ivBackArrowimg);
       // imageView.setImageResource(R.drawable.nature);
        Glide.with(this).load(getIntent().getStringExtra("fullImage")).error(R.drawable.ic_person_avatar).placeholder(R.drawable.ic_person_avatar).into(imageView);

        ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}