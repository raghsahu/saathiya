package com.prometteur.sathiya.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.prometteur.sathiya.BaseActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.EditProfileTabAdapter;
import com.prometteur.sathiya.databinding.ActivityEditProfileBinding;
import com.prometteur.sathiya.fragments.EditBasicDetailFragment;
import com.prometteur.sathiya.fragments.EditDocumentFragment;
import com.prometteur.sathiya.fragments.EditEducationDetailFragment;
import com.prometteur.sathiya.fragments.EditOtherDetailsFragment;
import com.prometteur.sathiya.fragments.EditPhotosFragment;
import com.prometteur.sathiya.utills.ImageScaleView;

public class EditProfileActivity extends BaseActivity {

    ActivityEditProfileBinding profileBinding;
    public static ViewPager viewPager;
    public static TextView tvProfDet,tvPhoto;
    public static String pageType;
    public static ImageScaleView rivProfileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding=ActivityEditProfileBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(profileBinding.getRoot());
        viewPager=profileBinding.vpPager;
        rivProfileImage=profileBinding.rivProfileImage;
        profileBinding.toolBarTitle.setText(getString(R.string.edit_profile));
                profileBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        rivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
            }
        });
        tvProfDet=profileBinding.tvProfileDetails;
        tvPhoto=profileBinding.tvPhotos;
        EditProfileTabAdapter hotelDetailsTabAdapter = new EditProfileTabAdapter(getSupportFragmentManager());

        if(getIntent().getStringExtra("page")!=null && getIntent().getStringExtra("page").equalsIgnoreCase("photo")){
            hotelDetailsTabAdapter.addFragment(new EditPhotosFragment(), getString(R.string.photos_details));

            profileBinding.vpPager.setAdapter(hotelDetailsTabAdapter);

            profileBinding.vpPager.setOffscreenPageLimit(1);
            profileBinding.tvProfileDetails.setVisibility(View.GONE);
            profileBinding.tvPhotos.setVisibility(View.GONE);
            profileBinding.tvAdditionalDetails.setVisibility(View.GONE);
            profileBinding.tvOtherDetails.setVisibility(View.GONE);
        }else if(getIntent().getStringExtra("page")!=null && getIntent().getStringExtra("page").equalsIgnoreCase("basicDetails")){
            pageType=getIntent().getStringExtra("page");
            hotelDetailsTabAdapter.addFragment(new EditBasicDetailFragment(), "Basic Details");

            profileBinding.vpPager.setAdapter(hotelDetailsTabAdapter);

            profileBinding.vpPager.setOffscreenPageLimit(1);
            profileBinding.tvProfileDetails.setVisibility(View.GONE);
            profileBinding.tvPhotos.setVisibility(View.GONE);
            profileBinding.tvAdditionalDetails.setVisibility(View.GONE);
            profileBinding.tvOtherDetails.setVisibility(View.GONE);
        }else {
            pageType=null;
            profileBinding.tvProfileDetails.setVisibility(View.VISIBLE);
            profileBinding.tvPhotos.setVisibility(View.VISIBLE);
            profileBinding.tvAdditionalDetails.setVisibility(View.VISIBLE);
            profileBinding.tvOtherDetails.setVisibility(View.VISIBLE);
            /*setUpHotelDetailsFragment*/
            hotelDetailsTabAdapter.addFragment(new EditBasicDetailFragment(), "Basic Details");

            /*setUpHotelAmeninitesFragment*/
            hotelDetailsTabAdapter.addFragment(new EditEducationDetailFragment(), "Edutcation & Profession Details");

            /*setUpHotelCommentsFragment*/
            hotelDetailsTabAdapter.addFragment(new EditOtherDetailsFragment(), "Other Details");

            /*setUpHotelPhotosFragment*/
            //  hotelDetailsTabAdapter.addFragment(new EditDocumentFragment(),  "Document Details");
            /*setUpHotelVideosFragment*/
            hotelDetailsTabAdapter.addFragment(new EditPhotosFragment(), "Photos Details");

            profileBinding.vpPager.setAdapter(hotelDetailsTabAdapter);

            profileBinding.vpPager.setOffscreenPageLimit(4);
            profileBinding.tvProfileDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileBinding.vpPager.setCurrentItem(0);
                }
            });profileBinding.tvAdditionalDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileBinding.vpPager.setCurrentItem(1);
                }
            });profileBinding.tvOtherDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileBinding.vpPager.setCurrentItem(2);
                }
            });
            profileBinding.tvPhotos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    profileBinding.vpPager.setCurrentItem(3);
                }
            });

            profileBinding.vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if(position==0)
                    {
                        profileBinding.tvProfileDetails.setChecked(true);
                    } if(position==1)
                    {
                        profileBinding.tvAdditionalDetails.setChecked(true);
                    } if(position==2)
                    {
                        profileBinding.tvOtherDetails.setChecked(true);
                    } if(position==3)
                    {
                        profileBinding.tvPhotos.setChecked(true);
                    }
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }
}