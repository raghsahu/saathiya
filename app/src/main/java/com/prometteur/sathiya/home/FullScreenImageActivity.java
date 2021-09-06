package com.prometteur.sathiya.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

        Glide.with(this).load(getIntent().getStringExtra("fullImage")).error(R.drawable.ic_person_avatar).placeholder(R.drawable.ic_person_avatar).into(imageView);

        ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
       /* imageView.setImageResource(R.drawable.jump);

// Change scale type to matrix
        imageView.setScaleType(ImageView.ScaleType.MATRIX);

// Calculate bottom crop matrix
        Matrix matrix = getBottomCropMatrix(FullScreenImageActivity.this, imageView.getDrawable().getIntrinsicWidth(), imageView.getDrawable().getIntrinsicHeight());

// Set matrixx
        imageView.setImageMatrix(matrix);
// Redraw image
        imageView.invalidate();*/
    }


    public Matrix getBottomCropMatrix(Context context,float imageWidth,float imageHeight)
    {
        Matrix matrix = new Matrix();

// Get screen size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displaymetrics);
        int screenWidth =imageView.getWidth(); //displaymetrics.widthPixels;
        int screenHeight =imageView.getHeight(); //displaymetrics.heightPixels;
// Get scale to match parent
        float scaleWidthRatio = screenWidth / imageWidth;
        float scaleHeightRatio = screenHeight / imageHeight;

// screenHeight multi by width scale to get scaled image height
        float scaledImageHeight = imageHeight * scaleWidthRatio;

// If scaledImageHeight < screenHeight, set scale to scaleHeightRatio to fit screen
// If scaledImageHeight >= screenHeight, use width scale as height scale
        if (scaledImageHeight >= screenHeight) {
            scaleHeightRatio = scaleWidthRatio;
        }

        matrix.setScale(scaleWidthRatio, scaleHeightRatio);
        return matrix;

    }
}