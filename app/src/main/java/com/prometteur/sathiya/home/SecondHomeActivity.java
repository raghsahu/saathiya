package com.prometteur.sathiya.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.prometteur.sathiya.BaseActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.CardStackAdapter;
import com.prometteur.sathiya.adapters.UserHomeListAdapter;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.chat.ChatActivity;
import com.prometteur.sathiya.databinding.ActivitySecondHomeBinding;
import com.prometteur.sathiya.dialog.DialogWarningActivity;
import com.prometteur.sathiya.hobbies.HobbiesInterestActivity;
import com.prometteur.sathiya.notification.NotificationActivity;
import com.prometteur.sathiya.packages.PackageActivity;
import com.prometteur.sathiya.profile.EditProfileActivity;
import com.prometteur.sathiya.profile.ProfileActivity;
import com.prometteur.sathiya.profilestatus.ProfileBlockedActivity;
import com.prometteur.sathiya.profilestatus.ProfileCalledActivity;
import com.prometteur.sathiya.profilestatus.ProfileLikeReceivedActivity;
import com.prometteur.sathiya.profilestatus.ProfileLikedActivity;
import com.prometteur.sathiya.profilestatus.ProfileRejectedActivity;
import com.prometteur.sathiya.profilestatus.ProfileVisitedActivity;
import com.prometteur.sathiya.translateapi.Http;
import com.prometteur.sathiya.translateapi.MainViewModel;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import darren.googlecloudtts.GoogleCloudTTS;
import darren.googlecloudtts.GoogleCloudTTSFactory;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.SplashActivity.strLangCode;
import static com.prometteur.sathiya.chat.FriendsFragment.openChatOnce;
import static com.prometteur.sathiya.utills.AppConstants.isNotification;
import static com.prometteur.sathiya.utills.AppConstants.vibe;
import static com.prometteur.sathiya.utills.AppConstants.vibrateBig;
import static com.prometteur.sathiya.utills.AppConstants.vibrateSmall;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class SecondHomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, CardStackListener {

    ActivitySecondHomeBinding homeBinding;
    CardStackLayoutManager manager;
    CardStackAdapter adapter;
    BaseActivity nActivity=SecondHomeActivity.this;
    boolean isListener=false;
    public static ImageView ivNotification;
    public static boolean activityRunning=false;
    public static MainViewModel mMainViewModel;
    public static CountDownTimer countDownTimer;
    //replace yourActivity.this with your own activity or if you declared a context you can write context.getSystemService(Context.VIBRATOR_SERVICE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding=ActivitySecondHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());
        manager=new CardStackLayoutManager(this,this);
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        UserId = prefUpdate.getString("user_id", "");
        matri_id = prefUpdate.getString("matri_id", "");
        gender = prefUpdate.getString("gender", "");
        strUserImage = prefUpdate.getString("profile_image", "");
        username = prefUpdate.getString("username", "");
        Log.e("Save Data", "UserId=> " + UserId + "  MatriId=> " + matri_id + "  Gender=> " + gender);
        ivNotification=homeBinding.ivNotification;
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//replace yourActivity.this with your own activity or if you declared a context you can write context.getSystemService(Context.VIBRATOR_SERVICE);

        homeBinding.linLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, DialogWarningActivity.class));
                if (homeBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    homeBinding.drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        homeBinding.ivWedRing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        homeBinding.ivMenuDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    homeBinding.drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        homeBinding.navView.setNavigationItemSelectedListener(this);

        homeBinding.ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, FilterHomeActivity.class));
            }
        });
        homeBinding.ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, NotificationActivity.class));
            }
        });
        homeBinding.rivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, ProfileActivity.class));
            }
        });
        setSearchView();

        GoogleCloudTTS googleCloudTTS = GoogleCloudTTSFactory.create("AIzaSyCacicvhIfLWPSlmZxL-dG0nwbx5yo8lCI");
        mMainViewModel = new MainViewModel(nActivity.getApplication(), googleCloudTTS);

    }


    public void setSearchView()
    {

        homeBinding.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
                homeBinding.linSearch.setVisibility(View.VISIBLE);
                homeBinding.linToolbar.setVisibility(View.GONE);
                homeBinding.ivMenuDrawer.setVisibility(View.GONE);
                homeBinding.ivFilter.setVisibility(View.GONE);
                homeBinding.ivWedRing.setVisibility(View.GONE);
            }
        });
        homeBinding.imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!homeBinding.edtSearch.getText().toString().isEmpty()) {
                    ThirdHomeActivity.matri_id = homeBinding.edtSearch.getText().toString().replace("#","");
                    nActivity.startActivity(new Intent(nActivity, ThirdHomeActivity.class));
                    homeBinding.edtSearch.setText("");
                    homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
                    homeBinding.linSearch.setVisibility(View.GONE);
                    homeBinding.linToolbar.setVisibility(View.VISIBLE);
                    homeBinding.ivMenuDrawer.setVisibility(View.VISIBLE);
                    homeBinding.ivFilter.setVisibility(View.VISIBLE);
                    homeBinding.ivWedRing.setVisibility(View.VISIBLE);
                }else
                {
                    AppConstants.setToastStr(nActivity,getString(R.string.please_enter_valide_matri_id));
                }
            }
        });
        homeBinding.imgSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeBinding.edtSearch.setText("");
                homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
                homeBinding.linSearch.setVisibility(View.GONE);
                homeBinding.linToolbar.setVisibility(View.VISIBLE);
                homeBinding.ivMenuDrawer.setVisibility(View.VISIBLE);
                homeBinding.ivFilter.setVisibility(View.VISIBLE);
                homeBinding.ivWedRing.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getHeaderView() {
        View header = homeBinding.navView.getHeaderView(0);
        CircleImageView civProfileImg = header.findViewById(R.id.civProfileImg);
        ImageView ivClose = header.findViewById(R.id.ivClose);
        TextView tvProfileName = header.findViewById(R.id.tvProfileName);
        TextView tvId = header.findViewById(R.id.tvId);
        TextView tvMembership = header.findViewById(R.id.tvMembership);
        LinearLayout linHeaderClick = header.findViewById(R.id.linHeaderClick);
        tvProfileName.setText(""+username);
        tvId.setText("ID - #"+matri_id);
        Glide.with(this).load(strUserImage).placeholder(R.drawable.shape_corner_round_square).error(R.drawable.shape_corner_round_square).into(homeBinding.rivProfileImage);
        Glide.with(this).load(strUserImage).placeholder(R.drawable.shape_corner_round_square).error(R.drawable.shape_corner_round_square).into(civProfileImg);

        if(call_package_status.equalsIgnoreCase("active")) {
            tvMembership.setText(getString(R.string.membership_status)+" "+getString(R.string.paid));
            tvMembership.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(SecondHomeActivity.this, PackageActivity.class));
                }
            });
        }else
        {
            tvMembership.setText(getString(R.string.membership_status)+" "+getString(R.string.unpaid));
        }

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
                //  startActivity(new Intent(DashboardMainActivity.this, SettingsActivity.class));
            }
        });

        linHeaderClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(nActivity, ProfileActivity.class));
            }
        });
        civProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(nActivity, ProfileActivity.class));
            }
        });
     /*   tvLocation.setText(Preferences.getPreferenceValue(DashboardMainActivity.this, BRANCHADDRESS));
        salonName.setText(Preferences.getPreferenceValue(DashboardMainActivity.this, BRANCHNAME));
        tvTitle.setText(Preferences.getPreferenceValue(DashboardMainActivity.this, BRANCHNAME));
        Glide.with(this).load(Preferences.getPreferenceValue(DashboardMainActivity.this, BRANCHIMG)).error(R.drawable.img_chair).into(ivSalonImg)*/;
    }
    BaseActivity activity=SecondHomeActivity.this;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_edt_profile:
                startActivity(new Intent(activity, EditProfileActivity.class));
                break;
           /* case R.id.nav_liked_prof:
                startActivity(new Intent(activity, ProfileLikedActivity.class));
                break;*/
            case R.id.nav_liked_received_prof:
                startActivity(new Intent(activity, ProfileLikeReceivedActivity.class));
                break;
            case R.id.nav_rejected:
                startActivity(new Intent(activity, ProfileRejectedActivity.class));
                break;
            case R.id.nav_hobies_interest:
                startActivity(new Intent(activity, HobbiesInterestActivity.class));
                break;
            case R.id.nav_visited:
                startActivity(new Intent(activity, ProfileVisitedActivity.class));
                break;
            case R.id.nav_called_users:
                startActivity(new Intent(activity, ProfileCalledActivity.class));
                break;
            case R.id.nav_messaged_users:
                openChatOnce=true;
                startActivity(new Intent(activity, ChatActivity.class));
                break;
            case R.id.nav_blocked_users:
                startActivity(new Intent(activity, ProfileBlockedActivity.class));
                break;
            case R.id.nav_package:
                startActivity(new Intent(activity, PackageActivity.class));
                break;case R.id.nav_setting:
            startActivity(new Intent(activity, SettingActivity.class));
            break;
        }

        homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
Direction direction;
    int position;
    public static boolean isSwiped=false;
    long mLastClickTimeLeft=0;
    long mLastClickTimeRight=0;
    @Override
    public void onCardDragging(Direction direction, float ratio) {
        //Toast.makeText(nActivity,"onCardDragging "+direction.toString(),Toast.LENGTH_SHORT).show();
        Log.e("ratio",""+ratio);
        try {
            if (direction != null && direction.toString().equalsIgnoreCase("Left") && ratio>=1) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeLeft < 2000) {
                    return;
                }
                mLastClickTimeLeft = SystemClock.elapsedRealtime();
               // AppConstants.setToastStrTop(nActivity,getString(R.string.you_are_about_to_reject));
            } else if (direction != null && direction.toString().equalsIgnoreCase("Right") && ratio>=1) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeRight < 2000) {
                    return;
                }
                mLastClickTimeRight = SystemClock.elapsedRealtime();
               // AppConstants.setToastStrTop(nActivity,getString(R.string.you_are_about_to_like));
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onCardSwiped(Direction direction) {
       this.direction=direction;
        if(countDownTimer!=null) {
            countDownTimer.cancel();
        }
       if(isListener){
        //Toast.makeText(nActivity,"onCardSwiped "+direction.toString(),Toast.LENGTH_SHORT).show();
        try {
            if (direction != null && direction.toString().equalsIgnoreCase("Left")) {
                if(!isSwiped) {
                    vibe.vibrate(vibrateSmall);//80 represents the milliseconds
                    adapter.sendInterestRequestRemind(matri_id, arrUserDataList.get(position).getMatri_id(), arrUserDataList.get(position).getIs_favourite(), position, "activity");
                }
            } else if (direction != null && direction.toString().equalsIgnoreCase("Right")) {
                if(!isSwiped) {
                    vibe.vibrate(vibrateBig);//80 represents the milliseconds
                    adapter.sendInterestRequest(matri_id, arrUserDataList.get(position).getMatri_id(), arrUserDataList.get(position).getIs_favourite(), position, "activity");
                }
            }else if (direction != null && (direction.toString().equalsIgnoreCase("Top")|| direction.toString().equalsIgnoreCase("Bottom"))) {
                if (arrUserDataList.size() > 0) {
                        homeBinding.cardStackView.setVisibility(View.VISIBLE);
                        homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);

                }else{
                    homeBinding.cardStackView.setVisibility(View.GONE);
                    homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                }
            }
            isSwiped=false;
            if (MusicService.mPlayer == null) {
                MusicService.mPlayer = new MediaPlayer();
            }
            MusicService.mPlayer.stop();
            stopService(new Intent(nActivity, MusicService.class));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {
       // Toast.makeText(nActivity,"onCardAppeared "+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardDisappeared(View view, int position) {
        this.position=position;

     //   Toast.makeText(nActivity,"onCardDisappeared "+position,Toast.LENGTH_SHORT).show();
    }


    private void initialize() {
        manager.setStackFrom(StackFrom.Top);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(12.0f);
        manager.setScaleInterval(0.90f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
//        manager.setDirections(Direction.HORIZONTAL);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(true);
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        homeBinding.cardStackView.setLayoutManager(manager);
        homeBinding.cardStackView.setAdapter(adapter);
        homeBinding.cardStackView.setItemAnimator(new RecyclerView.ItemAnimator() {
            @Override
            public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public void runPendingAnimations() {

            }

            @Override
            public void endAnimation(@NonNull RecyclerView.ViewHolder item) {

            }

            @Override
            public void endAnimations() {

            }

            @Override
            public boolean isRunning() {
                return false;
            }
        });
       // homeBinding.cardStackView.smoothScrollToPosition(getIntent().getIntExtra("pos",0));
        isListener=true;
        }



    private ArrayList<beanUserData> arrUserDataList;
    private ArrayList<beanUserData> tempArrUserDataList;
    SharedPreferences prefUpdate;
    String UserId = "", matri_id = "", gender = "",username="",strUserImage="",call_package_status="";
    private void getUserDataRequest(String MatriId, String Gender) {
        /*final ProgressDialog progresDialog11 = new ProgressDialog(nActivity);
        progresDialog11.setCancelable(false);
        progresDialog11.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog11.setIndeterminate(true);*/
        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramMatriId = params[0];
                String paramGender = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "resent_join.php";
                Log.e("URL_Recent", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", paramMatriId);
                BasicNameValuePair GenderPair = new BasicNameValuePair("gender", paramGender);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(GenderPair);

                try {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    Log.e("Parametters Array=", "== " + (nameValuePairList.toString().trim().replaceAll(",", "&")));
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
                } catch (Exception uee) //UnsupportedEncodingException
                {
                    System.out.println("Anption given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                //  refresh.setRefreshing(false);
                //	progressBar1.setVisibility(View.GONE);

                Log.e("Recent_Listing", "==" + result);
                try {
                    if (result!=null) {


                        JSONObject obj = new JSONObject(result);
                        ArrayList<String> tokans = new ArrayList<>();
                        tokans.clear();
                        String status = obj.getString("status");
                        try {
                            strUserImage = obj.getString("my_photo");
                            prefUpdate.edit().putString("profile_image", strUserImage).apply();
                            getHeaderView();
                        }catch (Exception e)
                        {e.printStackTrace();}
                        if (status.equalsIgnoreCase("1")) {
                            arrUserDataList = new ArrayList<beanUserData>();
                            tempArrUserDataList = new ArrayList<beanUserData>();
                            JSONArray responseData = obj.getJSONArray("responseData");


                            if (responseData.length()>0) {
                                for(int i=0;i<responseData.length();i++) {

                                    JSONObject resItem = responseData.getJSONObject(i);


                                    String user_id = resItem.getString("user_id");
                                    String matri_id1 = resItem.getString("matri_id");
                                    String username = resItem.getString("username");
                                    String birthdate = resItem.getString("birthdate");
                                    String ocp_name = resItem.getString("ocp_name");
                                    String height = getString(R.string.age)+" "+resItem.getString("age")+" "+getString(R.string.years)+", "+resItem.getString("height");
                                    String Address = resItem.getString("profile_text");
                                    String city_name = resItem.getString("city_name");
                                    String country_name = resItem.getString("country_name");
                                    String photo1_approve = resItem.getString("photo1_approve");
                                    String photo_view_status = resItem.getString("photo_view_status");
                                    String photo_protect = resItem.getString("photo_protect");
                                    String photo_pswd = resItem.getString("photo_pswd");
                                    String gender1 = resItem.getString("gender");
                                    String is_shortlisted = resItem.getString("is_shortlisted");
                                    String is_blocked = resItem.getString("is_blocked");
                                    String is_favourite = resItem.getString("is_favourite");
                                    String user_profile_picture = resItem.getString("user_profile_picture");
                                    String eiId = resItem.getString("ei_id");
                                    String userMobile = resItem.getString("mobile");
                                    String userEmail = resItem.getString("email");
                                    String firebaseEmail = resItem.getString("firebase_email");
                                    tokans.add(resItem.getString("tokan"));
                                    if(i<getIntent().getIntExtra("pos",0)){
                                      beanUserData beanUserData=  new beanUserData(user_id, matri_id1, username, birthdate, ocp_name, height, Address,
                                                city_name, country_name, photo1_approve, photo_view_status, photo_protect,
                                                photo_pswd, gender1, is_shortlisted, is_blocked, is_favourite, user_profile_picture,eiId,userMobile,firebaseEmail,"");
                                        beanUserData.setStateName(resItem.getString("state_name"));
                                        beanUserData.setAge(resItem.getString("age"));
                                        beanUserData.setManglik(resItem.getString("manglik"));
                                        beanUserData.setDiet(resItem.getString("diet"));
                                        beanUserData.setIncome(resItem.getString("income"));
                                        beanUserData.setM_tongue(resItem.getString("m_tongue"));
                                        ArrayList<Uri> imgUris=new ArrayList<>();
                                        JSONArray profilePhotoArr=new JSONArray(resItem.getString("profile_photos"));
                                        for(int i1=0;i1<profilePhotoArr.length();i1++) {
                                            JSONObject profilePhotoObj=profilePhotoArr.getJSONObject(i1);
                                            Uri uri = Uri.parse(profilePhotoObj.getString("photo"));
                                            imgUris.add(uri);
                                        }
                                        JSONArray hobbiesPhotoArr=new JSONArray(resItem.getString("hobby_data"));
                                        for(int i1=0;i1<hobbiesPhotoArr.length();i1++) {
                                            JSONObject hobbiesPhotoObj=hobbiesPhotoArr.getJSONObject(i1);
                                            Uri uri = Uri.parse(hobbiesPhotoObj.getString("photo"));
                                            imgUris.add(uri);
                                        }
                                        beanUserData.setImageUri(imgUris);

                                        ArrayList<String> fillerTextList=new ArrayList<>();
                                        JSONArray fillerTextArr=new JSONArray(resItem.getString("filler"));
                                        for(int i1=0;i1<fillerTextArr.length();i1++) {
                                            JSONObject hobbiesPhotoObj=fillerTextArr.getJSONObject(i1);
                                            String textFiller = hobbiesPhotoObj.getString("text");
                                            fillerTextList.add(textFiller);
                                        }
                                        beanUserData.setPhotoPath(fillerTextList);
                                        tempArrUserDataList.add(beanUserData);
                                    }else {
                                        beanUserData beanUserData=  new beanUserData(user_id, matri_id1, username, birthdate, ocp_name, height, Address,
                                                city_name, country_name, photo1_approve, photo_view_status, photo_protect,
                                                photo_pswd, gender1, is_shortlisted, is_blocked, is_favourite, user_profile_picture,eiId,userMobile,firebaseEmail,"");
                                        beanUserData.setStateName(resItem.getString("state_name"));
                                        beanUserData.setAge(resItem.getString("age"));
                                        beanUserData.setManglik(resItem.getString("manglik"));
                                        beanUserData.setDiet(resItem.getString("diet"));
                                        beanUserData.setIncome(resItem.getString("income"));
                                        beanUserData.setM_tongue(resItem.getString("m_tongue"));
                                        ArrayList<Uri> imgUris=new ArrayList<>();
                                        JSONArray profilePhotoArr=new JSONArray(resItem.getString("profile_photos"));
                                        for(int i1=0;i1<profilePhotoArr.length();i1++) {
                                            JSONObject profilePhotoObj=profilePhotoArr.getJSONObject(i1);
                                            Uri uri = Uri.parse(profilePhotoObj.getString("photo"));
                                            imgUris.add(uri);
                                        }
                                        JSONArray hobbiesPhotoArr=new JSONArray(resItem.getString("hobby_data"));
                                        for(int i1=0;i1<hobbiesPhotoArr.length();i1++) {
                                            JSONObject hobbiesPhotoObj=hobbiesPhotoArr.getJSONObject(i1);
                                            Uri uri = Uri.parse(hobbiesPhotoObj.getString("photo"));
                                            imgUris.add(uri);
                                        }
                                        beanUserData.setImageUri(imgUris);

                                        ArrayList<String> fillerTextList=new ArrayList<>();
                                        JSONArray fillerTextArr=new JSONArray(resItem.getString("filler"));
                                        for(int i1=0;i1<fillerTextArr.length();i1++) {
                                            JSONObject hobbiesPhotoObj=fillerTextArr.getJSONObject(i1);
                                            String textFiller = hobbiesPhotoObj.getString("text");
                                            fillerTextList.add(textFiller);
                                        }
                                        beanUserData.setPhotoPath(fillerTextList);
                                        arrUserDataList.add(beanUserData);
                                    }
                                }
                                arrUserDataList.addAll(tempArrUserDataList);
                                tempArrUserDataList.clear();
                                if (arrUserDataList.size() > 0) {
                                    homeBinding.cardStackView.setVisibility(View.VISIBLE);
                                    homeBinding.tvEmptyMsg.setVisibility(View.GONE);
                                    adapter=new CardStackAdapter(nActivity, arrUserDataList, homeBinding.cardStackView, tokans, manager, new CardStackAdapter.OnNotifyDataSetChanged() {
                                        @Override
                                        public void OnNotifyDataSetChangedFired(int dataSize) {
                                            if(dataSize==0) {
                                                homeBinding.cardStackView.setVisibility(View.GONE);
                                                homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                    initialize();
                                } else {
                                    homeBinding.cardStackView.setVisibility(View.GONE);
                                    homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                                }
                            }
                            progresDialog11.dismiss();
                        } else {
                            String msgError = obj.getString("message");
                            progresDialog11.dismiss();
                            homeBinding.cardStackView.setVisibility(View.GONE);
                            homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                        }
                        //refresh.setRefreshing(false);
                    }else {
                        progresDialog11.dismiss();
                        getUserDataRequest(matri_id, gender);
                    }

                } catch (Exception t) {
                    t.printStackTrace();
                    progresDialog11.dismiss();
                }

                progresDialog11.dismiss();
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(MatriId, Gender);
    }

    @Override
    public void onResume() {
        try {
            activityRunning = true;
            mMainViewModel.resume();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onResume();
        try{
            if(isNotification)
            {
                Glide.with(nActivity).asGif().load(R.drawable.notification_gif).into(ivNotification);
                ivNotification.setPadding(0,15,0,15);
            }else
            {
                Glide.with(nActivity).load(R.drawable.ic_notification).into(ivNotification);
            }}catch (Exception e)
        {
            e.printStackTrace();
        }
        call_package_status = prefUpdate.getString("call_package_status", "");
        getHeaderView();
        getIntetData();


    }

    @Override
    protected void onPause() {
        try {
            activityRunning = false;
            mMainViewModel.pause();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onPause();
    }


    String AgeM="", AgeF="", HeightM="", HeightF="", ReligionId="", CasteId="", CountryId="",
            StateId="", CityId="", HighestEducationId="", AnnualIncome="", MotherToungueID="", Diet="",Manglik="";

    public void getIntetData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");


        if (bundle != null) {
            String SearchType = bundle.getString("SearchType");
            /*if (SearchType.equalsIgnoreCase("byId")) {
                MatriId = bundle.getString("MatriId");
                isType = bundle.getString("istype");
                textviewHeaderText.setText("SEARCH BY MATRI ID");
                if (NetworkConnection.hasConnection(SearchResultActivity.this)) {
                    getSearchByMatriIDRequest(MatriId, matri_id, Gender);

                } else {
                    AppConstants.CheckConnection(SearchResultActivity.this);
                }


            } else if (SearchType.equalsIgnoreCase("bydata")) {*/
            //textviewHeaderText.setText("SEARCH RESULT");
            //Gender = bundle.getString("Gender");
            // Log.e("genderr", Gender);
            if(bundle.getString("AgeM")!=null) {
                AgeM = bundle.getString("AgeM");
                AgeF = bundle.getString("AgeF");
                HeightM = bundle.getString("HeightM");
                HeightF = bundle.getString("HeightF");
                ReligionId = bundle.getString("ReligionId");
                CasteId = bundle.getString("CasteId");
                CountryId = bundle.getString("CountryId");
                StateId = bundle.getString("StateId");
                CityId = bundle.getString("CityId");
                HighestEducationId = bundle.getString("HighestEducationId");
                AnnualIncome = bundle.getString("AnnualIncome");
                MotherToungueID = bundle.getString("MotherToungueID");
                Diet = bundle.getString("Diet");
                Manglik = bundle.getString("Manglik");

                if (NetworkConnection.hasConnection(nActivity)) {

                    getSearchResultRequest(gender, AgeM, AgeF, HeightM, HeightF, ReligionId,
                            CasteId, CountryId, StateId, CityId, HighestEducationId, AnnualIncome,
                            matri_id, MotherToungueID, Diet,Manglik);

                } else {
                    AppConstants.CheckConnection(nActivity);
                }

                //}

            }else
            {
                if (NetworkConnection.hasConnection(nActivity)) {
                    getUserDataRequest(matri_id, gender);
                } else {
                    AppConstants.CheckConnection(nActivity);
                }
            }
        }else
        {
            if (NetworkConnection.hasConnection(nActivity)) {
                getUserDataRequest(matri_id, gender);
            } else {
                AppConstants.CheckConnection(nActivity);
            }
        }
    }

    private void getSearchResultRequest(final String Gender, String AgeM, String AgeF, String HeightM, String HeightF,
                                        String ReligionId,
                                        String CasteId, String CountryId, String StateId, String CityId, String HighestEducationId,
                                        String AnnualIncome,
                                        String login_matriId, String MotherToungueID, String Diet,String Manglik) {
        //refresh.setRefreshing(true);

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramGender = params[0];
                String paramAgeM = params[1];
                String paramAgeF = params[2];
                String paramHeightM = params[3];
                String paramHeightF = params[4];
                String paramReligionId = params[5];
                String paramCasteId = params[6];
                String paramCountryId = params[7];
                String paramStateId = params[8];
                String paramCityId = params[9];
                String paramEducationId = params[10];
                String paramAnnualIncome = params[11];

                String paramsLoginMatriId = params[12];
                String paramsMotherTounguId = params[13];
                String paramsDiet = params[14];
                String paramsManglik = params[15];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "search.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair PairGender = new BasicNameValuePair("gender", paramGender);
                BasicNameValuePair PairAgeM = new BasicNameValuePair("fromage", paramAgeM);
                BasicNameValuePair PairAgeF = new BasicNameValuePair("toage", paramAgeF);
                BasicNameValuePair PairHeightM = new BasicNameValuePair("fromheight", paramHeightM);
                BasicNameValuePair PairHeightF = new BasicNameValuePair("toheight", paramHeightF);

                BasicNameValuePair PairReligionId = new BasicNameValuePair("religion", paramReligionId);
                BasicNameValuePair PairCasteId = new BasicNameValuePair("caste", paramCasteId);
                BasicNameValuePair PairCountryId = new BasicNameValuePair("country", paramCountryId);
                BasicNameValuePair PairStateId = new BasicNameValuePair("state", paramStateId);
                BasicNameValuePair PairCityId = new BasicNameValuePair("city", paramCityId);
                BasicNameValuePair PairEducationId = new BasicNameValuePair("education", paramEducationId);
                BasicNameValuePair PairAnnualIncome = new BasicNameValuePair("annual_income", paramAnnualIncome);
                BasicNameValuePair MatriSeachIdPair = new BasicNameValuePair("login_matri_id", paramsLoginMatriId);
                BasicNameValuePair PairMotherToungue = new BasicNameValuePair("mother_tongue_id", paramsMotherTounguId);
                BasicNameValuePair DietPair = new BasicNameValuePair("diet", paramsDiet);
                BasicNameValuePair ManglikPair = new BasicNameValuePair("manglik", paramsManglik);


                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(PairGender);
                nameValuePairList.add(PairAgeM);
                nameValuePairList.add(PairAgeF);
                nameValuePairList.add(PairHeightM);
                nameValuePairList.add(PairHeightF);
                nameValuePairList.add(PairReligionId);
                nameValuePairList.add(PairCasteId);
                nameValuePairList.add(PairCountryId);
                nameValuePairList.add(PairStateId);
                nameValuePairList.add(PairCityId);
                nameValuePairList.add(PairEducationId);
                nameValuePairList.add(PairAnnualIncome);
                nameValuePairList.add(MatriSeachIdPair);
                nameValuePairList.add(PairMotherToungue);
                nameValuePairList.add(DietPair);
                nameValuePairList.add(ManglikPair);


                try {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    Log.e("Parametters Array=", "== " + (nameValuePairList.toString().trim().replaceAll(",", "&")));

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

                Log.e("--Search by Result --", "==" + Ressponce);
                ArrayList<String> tokans = new ArrayList<>();
                tokans.clear();
                try {
                    JSONObject obj = new JSONObject(Ressponce);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        arrUserDataList = new ArrayList<beanUserData>();
                        tempArrUserDataList = new ArrayList<beanUserData>();
                        JSONObject responseData = obj.getJSONObject("responseData");

                        if (responseData.has("1")) {
                            Iterator<String> resIter = responseData.keys();

                            while (resIter.hasNext()) {

                                String key = resIter.next();
                                JSONObject resItem = responseData.getJSONObject(key);

                                String user_id = resItem.getString("user_id");
                                String matri_id1 = resItem.getString("matri_id");
                                String username = resItem.getString("username");
                                String birthdate = resItem.getString("birthdate");
                                String ocp_name = resItem.getString("ocp_name");
                                String height = resItem.getString("height");
                                String Address = resItem.getString("profile_text");
                                String city_name = resItem.getString("city_name");
                                String country_name = resItem.getString("country_name");
                                String photo1_approve = resItem.getString("photo1_approve");
                                String photo_view_status = resItem.getString("photo_view_status");
                                String photo_protect = resItem.getString("photo_protect");
                                String photo_pswd = resItem.getString("photo_pswd");
                                String gender1 = resItem.getString("gender");
                                AppConstants.is_shortlisted = resItem.getString("is_shortlisted");
                                AppConstants.is_blocked = resItem.getString("is_blocked");

                                String is_favourite = resItem.getString("is_favourite");
                                String user_profile_picture = resItem.getString("user_profile_picture");
                                String eiId = resItem.getString("ei_id");
                                tokans.add(resItem.getString("tokan"));
                                if((Integer.parseInt(key)-1)<getIntent().getIntExtra("pos",0)){
                                    beanUserData beanUserData=new beanUserData(user_id, matri_id1, username, birthdate, ocp_name, height, Address, city_name, country_name, photo1_approve, photo_view_status, photo_protect,
                                            photo_pswd, gender1, AppConstants.is_shortlisted, AppConstants.is_blocked, is_favourite, user_profile_picture,eiId,"");
                                    beanUserData.setIncome(resItem.getString("income"));
                                    tempArrUserDataList.add(beanUserData);

                                }else{
                                    beanUserData beanUserData=new beanUserData(user_id, matri_id1, username, birthdate, ocp_name, height, Address, city_name, country_name, photo1_approve, photo_view_status, photo_protect,
                                            photo_pswd, gender1, AppConstants.is_shortlisted, AppConstants.is_blocked, is_favourite, user_profile_picture,eiId,"");
                                    beanUserData.setIncome(resItem.getString("income"));
                                    arrUserDataList.add(beanUserData);
                                }

                            }
                            arrUserDataList.addAll(tempArrUserDataList);
                            tempArrUserDataList.clear();
                            if (arrUserDataList.size() > 0) {
                                homeBinding.cardStackView.setVisibility(View.VISIBLE);
                                homeBinding.tvEmptyMsg.setText(getString(R.string.your_viewed_all_the_profiles));
                                homeBinding.tvEmptyMsg.setVisibility(View.GONE);

                                adapter=new CardStackAdapter(nActivity, arrUserDataList, homeBinding.cardStackView, tokans, manager, new CardStackAdapter.OnNotifyDataSetChanged() {
                                    @Override
                                    public void OnNotifyDataSetChangedFired(int dataSize) {
                                        if(dataSize==0) {
                                            homeBinding.cardStackView.setVisibility(View.GONE);
                                            homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                                initialize();

                            } else {
                                String msgError = obj.getString("message");
                                AppConstants.setToastStr(nActivity, "" + msgError);
                                homeBinding.cardStackView.setVisibility(View.GONE);
                                homeBinding.tvEmptyMsg.setText(getString(R.string.you_are_all_caught_for_now));
                                homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                            }
                        }


                    } else {
                        String msgError = obj.getString("message");
                        AppConstants.setToastStr(nActivity, "" + msgError);
                        homeBinding.cardStackView.setVisibility(View.GONE);
                        homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                    }


                   // homeBinding.refresh.setRefreshing(false);
                } catch (Throwable t) {
                   // homeBinding.refresh.setRefreshing(false);
                }
              //  homeBinding.refresh.setRefreshing(false);

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(Gender, AgeM, AgeF, HeightM, HeightF, ReligionId,
                CasteId, CountryId, StateId, CityId, HighestEducationId, AnnualIncome,
                login_matriId, MotherToungueID, Diet,Manglik);

    }


    @Override
    protected void onDestroy() {
        activityRunning = false;
        mMainViewModel.dispose();
        if(countDownTimer!=null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }


    @Override
    protected void onStop() {
        activityRunning = false;
        mMainViewModel.dispose();
        if(countDownTimer!=null) {
            countDownTimer.cancel();
        }
        super.onStop();
    }

}