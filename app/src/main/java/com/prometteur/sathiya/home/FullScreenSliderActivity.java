package com.prometteur.sathiya.home;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.SliderPhotoFullPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FullScreenSliderActivity extends AppCompatActivity {

    ViewPager vpSlider;
    LinearLayout llDots;
    SliderPhotoFullPagerAdapter sliderPagerAdapter;
    List<String> arrPhotos = new ArrayList<>();
    TextView[] dots;
    private ImageView ivBackArrowimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_slider);

        ivBackArrowimg = findViewById(R.id.ivBackArrowimg);
        vpSlider = findViewById(R.id.vp_slider);
        llDots = findViewById(R.id.ll_dots);

        init();

        ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void init() {
        arrPhotos=getIntent().getStringArrayListExtra("fullImage");
        sliderPagerAdapter = new SliderPhotoFullPagerAdapter(FullScreenSliderActivity.this, arrPhotos);
        vpSlider.setAdapter(sliderPagerAdapter);

        vpSlider.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[arrPhotos.size()];

        llDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#000000"));
            llDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
    }
}