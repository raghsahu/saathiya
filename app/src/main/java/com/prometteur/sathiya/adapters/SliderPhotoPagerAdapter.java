package com.prometteur.sathiya.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanHobbyImage;
import com.prometteur.sathiya.dialog.DialogPlayVideoActivity;
import com.prometteur.sathiya.home.FullScreenImageActivity;
import com.prometteur.sathiya.utills.AppConstants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class SliderPhotoPagerAdapter extends PagerAdapter {
private LayoutInflater layoutInflater;
    Activity activity;
    List<beanHobbyImage> image_arraylist;
    SharedPreferences prefUpdate;
    String matri_id="";
    public SliderPhotoPagerAdapter(Activity activity, List<beanHobbyImage> image_arraylist) {
        this.activity = activity;
        this.image_arraylist = image_arraylist;

        prefUpdate= PreferenceManager.getDefaultSharedPreferences(activity);
        matri_id=prefUpdate.getString("matri_id","");
    }
 
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        View view = layoutInflater.inflate(R.layout.item_photos_slider_for_details, container, false);
        ImageView im_slider = (ImageView) view.findViewById(R.id.imgSlide);
        ImageView ivPlayPause = (ImageView) view.findViewById(R.id.ivPlayPause);
        Glide.with(activity.getApplicationContext())
                .load(image_arraylist.get(position).getHobby_name())
                .placeholder(R.drawable.bg_darkgray_rectengle_deselected) // optional
                .error(R.drawable.bg_darkgray_rectengle_deselected)         // optional
                .into(im_slider);
if(position==image_arraylist.size()-1){
    ivPlayPause.setVisibility(View.VISIBLE);
    ivPlayPause.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            activity.startActivity(new Intent(activity, DialogPlayVideoActivity.class));
        }
    });
}else{
    ivPlayPause.setVisibility(View.GONE);
}
im_slider.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        showFullScreen(image_arraylist.get(position).getHobby_name());
    }
});

        container.addView(view);
 
        return view;
    }
 
    @Override
    public int getCount() {
        return image_arraylist.size();
    }
 

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }
 
 
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }


    public void showFullScreen(String imagePath)
    {
        activity.startActivity(new Intent(activity, FullScreenImageActivity.class).putExtra("fullImage",imagePath));
    }
}