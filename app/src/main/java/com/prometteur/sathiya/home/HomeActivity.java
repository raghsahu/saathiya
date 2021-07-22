package com.prometteur.sathiya.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;

import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.prometteur.sathiya.BaseActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.prometteur.sathiya.BaseActivity;
import com.prometteur.sathiya.MyApp;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.UserHomeListAdapter;
import com.prometteur.sathiya.adapters.UserLikedListAdapter;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.chat.ChatActivity;
import com.prometteur.sathiya.databinding.ActivityHomeBinding;
import com.prometteur.sathiya.dialog.DialogUdateFieldsActivity;
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
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.LocaleUtils;
import com.prometteur.sathiya.utills.NetworkConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.chat.FriendsFragment.openChatOnce;
import static com.prometteur.sathiya.utills.AppConstants.isNotification;
import static com.prometteur.sathiya.utills.AppConstants.vibe;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

//import android.support.v7.app.BaseActivity;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayoutManager linearLayoutManager;
    UserHomeListAdapter userLikedListAdapter;

    BaseActivity nActivity = HomeActivity.this;
    ActivityHomeBinding homeBinding;
public static ImageView ivNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(homeBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        UserId = prefUpdate.getString("user_id", "");
        matri_id = prefUpdate.getString("matri_id", "");
        gender = prefUpdate.getString("gender", "");
        strUserImage = prefUpdate.getString("profile_image", "");
        username = prefUpdate.getString("username", "");
        ivNotification=homeBinding.ivNotification;
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //set language
        strLang=prefUpdate.getString("selLang","English");
        LocaleUtils.setLocale(new Locale(prefUpdate.getString("selLangCode","en")));
        LocaleUtils.updateConfig(MyApp.getInstance(), getResources().getConfiguration());


        Log.e("Save Data", "UserId=> " + UserId + "  MatriId=> " + matri_id + "  Gender=> " + gender);

        linearLayoutManager = new LinearLayoutManager(nActivity);

        homeBinding.rvLikedUsers.setLayoutManager(linearLayoutManager);
        homeBinding.rvLikedUsers.setHasFixedSize(true);


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                getHeaderView();
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        homeBinding.linLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, DialogWarningActivity.class));
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        homeBinding.ivMenuDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
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
homeBinding.rivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, ProfileActivity.class));
            }
        });
homeBinding.ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, NotificationActivity.class));
            }
        });
        try {
            Field f = homeBinding.refresh.getClass().getDeclaredField("mCircleView");
            f.setAccessible(true);
            ImageView img = (ImageView)f.get(homeBinding.refresh);
            img.setAlpha(0.0f);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
homeBinding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
        if (NetworkConnection.hasConnection(nActivity)){
            getUserDataRequest(matri_id, gender);

        }else
        {
            AppConstants.CheckConnection(nActivity);
        }

    }
});
        setSearchView();
        /*try {
            ChatUI.getInstance().setContext(this);
        }catch (Exception e)
        {
            e.printStackTrace();
        }*/


        Bundle bundle=new Bundle();
        bundle=getIntent().getBundleExtra("notificat");
if(bundle!=null) {
    Log.e("notifi message home",""+bundle.getString("name"));
    openActivityOnNotification(bundle);
}
    }

    private void openActivityOnNotification(Bundle bundle) {
        if(bundle.getString("name").contains("New Message"))
        {
            startActivity(new Intent(nActivity,ChatActivity.class));
        }else //if(bundle.getString("name").contains("Interest Rejected") ||bundle.getString("name").contains("Interest Received")||bundle.getString("name").contains("Interest accepted") )
        {
            ThirdHomeActivity.matri_id=bundle.getString("senderId");
            startActivity(new Intent(nActivity, ThirdHomeActivity.class));
        }
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
                AppConstants.setToastStr(nActivity,getString(R.string.please_enter_valid_matri_id));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    private void getHeaderView() {
        View header = homeBinding.navView.getHeaderView(0);
        PorterShapeImageView civProfileImg = header.findViewById(R.id.civProfileImg);
        ImageView ivClose = header.findViewById(R.id.ivClose);
        TextView tvProfileName = header.findViewById(R.id.tvProfileName);
        TextView tvId = header.findViewById(R.id.tvId);
        TextView tvMembership = header.findViewById(R.id.tvMembership);
        LinearLayout linHeaderClick = header.findViewById(R.id.linHeaderClick);
        tvProfileName.setText(""+username);
        tvId.setText("ID - #"+matri_id);
        if(call_package_status.equalsIgnoreCase("active")) {
            tvMembership.setText(getString(R.string.membership_status)+" "+getString(R.string.paid));
        }else
        {
            tvMembership.setText(getString(R.string.membership_status)+" "+getString(R.string.unpaid));
        }
        tvMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, PackageActivity.class));
            }
        });
        Glide.with(this).load(strUserImage).error(R.drawable.img_item).into(homeBinding.rivProfileImage);
        Glide.with(this).load(strUserImage).error(R.drawable.img_item).into(civProfileImg);
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
BaseActivity activity=HomeActivity.this;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
                case R.id.nav_edt_profile:
startActivity(new Intent(activity, EditProfileActivity.class).putExtra("page","Other"));
                break;
                /*case R.id.nav_liked_prof:
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
                    // Login
                  /*  ChatSDK.auth().authenticate(AccountDetails.username("trimbake10@gmail.com", "admin@123")).subscribe(() -> {
                        ChatSDK.ui().startSplashScreenActivity(this);
                        }, throwable -> {
Log.i("chat login","Chat login");
                    });*/
                   // ChatSDK.ui().startSplashScreenActivity(this);
//
                    openChatOnce=true;
                    startActivity(new Intent(activity, ChatActivity.class));
                break;
                case R.id.nav_blocked_users:
                    startActivity(new Intent(activity, ProfileBlockedActivity.class));
                break;
                case R.id.nav_package:
                    startActivity(new Intent(activity, PackageActivity.class));
                break;
                case R.id.nav_setting:
                    startActivity(new Intent(activity, SettingActivity.class));
                break;
        }

        homeBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private ArrayList<beanUserData> arrUserDataList;
    SharedPreferences prefUpdate;
    String UserId = "", matri_id = "", gender = "",username="",strUserImage="",call_package_status="";
    String updateStatus;
    String fieldToUpdate;
    private void getUserDataRequest(String MatriId, String Gender) {
        final Dialog progresDialog11 = showProgress(nActivity);
        if(!progresDialog11.isShowing())
        {
            progresDialog11.show();
        }
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
                try{
                if(progresDialog11.isShowing()) {
                    progresDialog11.dismiss();
                }}catch (Exception e)
                {
                    e.printStackTrace();
                }

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
                            try {
                                JSONArray responseData = obj.getJSONArray("responseData");

                                updateStatus=obj.getString("update_status");
                                fieldToUpdate=obj.getString("field_to_update");

                                if(updateStatus.equalsIgnoreCase("1")){
                                    startActivity(new Intent(HomeActivity.this,DialogUdateFieldsActivity.class).putExtra("fieldToUpdate",fieldToUpdate));
                                }

                                if (responseData.length() > 0) {
                                    for (int i = 0; i < responseData.length(); i++) {

                                        JSONObject resItem = responseData.getJSONObject(i);


                                        String user_id = resItem.getString("user_id");
                                        String matri_id1 = resItem.getString("matri_id");
                                        String username = resItem.getString("username");
                                        String birthdate = resItem.getString("birthdate");
                                        String ocp_name = resItem.getString("ocp_name");
                                        String height = getString(R.string.age)+" " + resItem.getString("age") +" "+ getString(R.string.years) +", "+ resItem.getString("height");
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
                                        String eiId = resItem.getString("ei_id");
                                        String userMobile = resItem.getString("mobile");
                                        String userEmail = resItem.getString("email");
                                        String income = resItem.getString("email");
                                        String user_profile_picture = resItem.getString("user_profile_picture");
                                        tokans.add(resItem.getString("tokan"));
                                        beanUserData beanUserData=new beanUserData(user_id, matri_id1, username, birthdate, ocp_name, height, Address,
                                                city_name, country_name, photo1_approve, photo_view_status, photo_protect,
                                                photo_pswd, gender1, is_shortlisted, is_blocked, is_favourite, user_profile_picture, eiId,userMobile,userEmail,"");
                                        beanUserData.setIncome(resItem.getString("income"));
                                        arrUserDataList.add(beanUserData);

                                    }
                                    if (arrUserDataList.size() > 0) {
                                        homeBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                                        homeBinding.tvEmptyMsg.setVisibility(View.GONE);

                                        userLikedListAdapter = new UserHomeListAdapter(nActivity, false, arrUserDataList, tokans, new UserHomeListAdapter.OnNotifyDataSetChanged() {
                                            @Override
                                            public void OnNotifyDataSetChangedFired(int dataSize) {
                                                if(dataSize==0){
                                                    homeBinding.rvLikedUsers.setVisibility(View.GONE);
                                                    homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        },null);
                                        homeBinding.rvLikedUsers.setAdapter(userLikedListAdapter);
                                        homeBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                                    } else {
                                        homeBinding.rvLikedUsers.setVisibility(View.GONE);
                                        homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                                    }
                                }
                            }catch (Exception e)
                            {
                                homeBinding.rvLikedUsers.setVisibility(View.GONE);
                                homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                                e.printStackTrace();
                                progresDialog11.dismiss();
                            }
                        } else {
                            String msgError = obj.getString("message");
                            try{
                            if(progresDialog11.isShowing()) {
                                progresDialog11.dismiss();
                            }}catch (Exception e)
                        {
                            e.printStackTrace();
                        }

						/*AlertDialog.Builder builder = new AlertDialog.Builder(activity);
						builder.setMessage(""+msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								dialog.dismiss();
							}
						});
						AlertDialog alert = builder.create();
						alert.show();*/
                        }
                        homeBinding.refresh.setRefreshing(false);
                    }else {
                        if (NetworkConnection.hasConnection(nActivity)){
                            getUserDataRequest(matri_id, gender);

                        }else
                        {
                            AppConstants.CheckConnection(nActivity);
                        }
                    }
                    homeBinding.refresh.setRefreshing(false);
                    progresDialog11.dismiss();
                } catch (Exception t) {
                    t.printStackTrace();
                    homeBinding.refresh.setRefreshing(false);
                    try{
                        if(progresDialog11.isShowing()) {
                            progresDialog11.dismiss();
                        }}catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                try{
                    if(progresDialog11.isShowing()) {
                        progresDialog11.dismiss();
                    }}catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(MatriId, Gender);
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
        if(isNotification)
        {
            Glide.with(nActivity).asGif().load(R.drawable.notification_gif).into(ivNotification);
            ivNotification.setPadding(0,15,0,15);
        }else
        {
            Glide.with(nActivity).load(R.drawable.ic_notification).into(ivNotification);
        }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        call_package_status = prefUpdate.getString("call_package_status", "");
        getHeaderView();
        getIntetData();


    }

    String AgeM="", AgeF="", HeightM="", HeightF="", ReligionId="", CasteId="", CountryId="",
            StateId="", CityId="", HighestEducationId="", AnnualIncome="", MotherToungueID="", Diet="",Manglik="";
Bundle bundle1=null;
    public void getIntetData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        bundle1=bundle;

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
                                beanUserData beanUserData=new beanUserData(user_id, matri_id1, username, birthdate, ocp_name, height, Address, city_name, country_name, photo1_approve, photo_view_status, photo_protect,
                                        photo_pswd, gender1, AppConstants.is_shortlisted, AppConstants.is_blocked, is_favourite, user_profile_picture,eiId,"");
                                beanUserData.setIncome(resItem.getString("income"));
                                arrUserDataList.add(beanUserData);

                            }

                            if (arrUserDataList.size() > 0) {
                                homeBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                                homeBinding.tvEmptyMsg.setText(getString(R.string.your_viewed_all_the_profiles));
                                homeBinding.tvEmptyMsg.setVisibility(View.GONE);

                                userLikedListAdapter = new UserHomeListAdapter(nActivity, false, arrUserDataList, tokans, new UserHomeListAdapter.OnNotifyDataSetChanged() {
                                    @Override
                                    public void OnNotifyDataSetChangedFired(int dataSize) {
                                        if(dataSize==0){
                                            homeBinding.rvLikedUsers.setVisibility(View.GONE);
                                            homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                                        }
                                    }
                                },bundle1);
                                homeBinding.rvLikedUsers.setAdapter(userLikedListAdapter);
                                userLikedListAdapter.notifyDataSetChanged();

                            } else {
                                String msgError = obj.getString("message");
                                AppConstants.setToastStr(nActivity, "" + msgError);
                                homeBinding.rvLikedUsers.setVisibility(View.GONE);
                                homeBinding.tvEmptyMsg.setText(getString(R.string.recent_profile_not_found));
                                homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                            }
                        }


                    } else {
                        String msgError = obj.getString("message");
                        AppConstants.setToastStr(nActivity, "" + msgError);
                        homeBinding.rvLikedUsers.setVisibility(View.GONE);
                        homeBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                    }


                    homeBinding.refresh.setRefreshing(false);
                } catch (Throwable t) {
                    homeBinding.refresh.setRefreshing(false);
                }
                homeBinding.refresh.setRefreshing(false);

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(Gender, AgeM, AgeF, HeightM, HeightF, ReligionId,
                CasteId, CountryId, StateId, CityId, HighestEducationId, AnnualIncome,
                login_matriId, MotherToungueID, Diet,Manglik);

    }

}
