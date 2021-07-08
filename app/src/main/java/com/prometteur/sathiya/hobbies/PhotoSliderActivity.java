package com.prometteur.sathiya.hobbies;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.prometteur.sathiya.BaseActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.HobbiesListAdapter;
import com.prometteur.sathiya.adapters.SliderPagerAdapter;
import com.prometteur.sathiya.beans.beanHobbyImage;
import com.prometteur.sathiya.databinding.ActivityPhotoSliderBinding;
import com.prometteur.sathiya.home.ThirdHomeActivity;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class PhotoSliderActivity extends BaseActivity {

    BaseActivity nActivity= PhotoSliderActivity.this;
ActivityPhotoSliderBinding hobbiesInterestBinding;
    SharedPreferences prefUpdate;
    String matri_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hobbiesInterestBinding=ActivityPhotoSliderBinding.inflate(getLayoutInflater());

        setContentView(hobbiesInterestBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        strLang = prefUpdate.getString("selLang", "");
        matri_id=prefUpdate.getString("matri_id","");
        hobbiesInterestBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        if (NetworkConnection.hasConnection(nActivity)) {
            getHobbyImageList(matri_id,getIntent().getStringExtra("matriId"),"","");
        } else {
            AppConstants.CheckConnection(nActivity);
        }


        hobbiesInterestBinding.linProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThirdHomeActivity.matri_id=getIntent().getStringExtra("matriId");
                nActivity.startActivity(new Intent(nActivity, ThirdHomeActivity.class));
            }
        });

    }
    SliderPagerAdapter sliderPagerAdapter;
    int page_position = 0;
    private void init() {

        hobbiesInterestBinding.tvProfileName.setText(arrHobby.get(0).getName());
        hobbiesInterestBinding.tvLocation.setText(arrHobby.get(0).getLocation());
        Glide.with(getApplicationContext())
                .load(arrHobby.get(0).getUserPhoto())
                .placeholder(R.mipmap.ic_launcher) // optional
                .error(R.mipmap.ic_launcher)         // optional
                .into(hobbiesInterestBinding.civProfileImg);

        sliderPagerAdapter = new SliderPagerAdapter(nActivity, arrHobby);
        hobbiesInterestBinding.vpSlider.setAdapter(sliderPagerAdapter);

        hobbiesInterestBinding.vpSlider.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
    TextView[] dots;
    private void addBottomDots(int currentPage) {
        dots = new TextView[arrHobby.size()];

        hobbiesInterestBinding.llDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#000000"));
            hobbiesInterestBinding.llDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
    }



    List<beanHobbyImage> arrHobby =new ArrayList<>();
    private void getHobbyImageList(String loginMatriId,String matri_id,String hoby_id,String km) {
        /*final ProgressDialog progressDialogSendReq = new ProgressDialog(nActivity);
        progressDialogSendReq.setCancelable(false);
        progressDialogSendReq.setMessage(getString(R.string.Please_Wait));
        progressDialogSendReq.setIndeterminate(true);*/
        Dialog progressDialogSendReq = showProgress(nActivity);
        progressDialogSendReq.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "get_my_interest.php";
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("login_matri_id", loginMatriId);
                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", matri_id);
                BasicNameValuePair HobbyIdPair = new BasicNameValuePair("hoby_id", hoby_id);
                BasicNameValuePair KmPair = new BasicNameValuePair("km", km);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(HobbyIdPair);
                nameValuePairList.add(KmPair);
                nameValuePairList.add(languagePAir);
                try {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    try {
                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        InputStream inputStream = httpResponse.getEntity().getContent();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String bufferedStrChunk = null;
                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("Firstption caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out.println("Secondption caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }

                } catch (Exception uee) {
                    System.out.println("Anption given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String Ressponce) {
                super.onPostExecute(Ressponce);

                Log.e("--City --", "==" + Ressponce);

                try {
                    arrHobby = new ArrayList<beanHobbyImage>();
                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");


                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String hobyId = resItem.getString("hoby_id");
                            String hobyName = resItem.getString("hoby_title"); //hobby_name
                            String intrestId = resItem.getString("intrest_id");
                            String matriId = resItem.getString("matri_id");
                            String photo = resItem.getString("photo");
                            String name = resItem.getString("name");
                            String city = resItem.getString("city");
                            String userPhoto = resItem.getString("profile_photo");
                            String isLiked = resItem.getString("like_status");
                            arrHobby.add(new beanHobbyImage(hobyId, hobyName,matriId,intrestId,photo,name,city,userPhoto,isLiked));

                        }
                        if (arrHobby.size() > 0) {
                            /*hobbiesInterestBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                            hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.GONE);*/
// method for initialisation
                            init();

// method for adding indicators
                            addBottomDots(0);

                            /*final Handler handler = new Handler();

                            final Runnable update = new Runnable() {
                                public void run() {
                                    if (page_position == arrHobby.size()) {
                                        page_position = 0;
                                    } else {
                                        page_position = page_position + 1;
                                    }
                                    hobbiesInterestBinding.vpSlider.setCurrentItem(page_position, true);
                                }
                            };

                            new Timer().schedule(new TimerTask() {

                                @Override
                                public void run() {
                                    handler.post(update);
                                }
                            }, 100, 5000);*/
                        }else
                        {
                            /*hobbiesInterestBinding.rvLikedUsers.setVisibility(View.GONE);
                            hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.VISIBLE);*/
                        }

                        resIter = null;

                    }else{

                            /*hobbiesInterestBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                            hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.GONE);*/

                    }

                    responseData = null;
                    responseObj = null;

                    progressDialogSendReq.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialogSendReq.dismiss();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
