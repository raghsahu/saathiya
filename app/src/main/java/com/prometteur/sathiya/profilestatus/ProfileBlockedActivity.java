package com.prometteur.sathiya.profilestatus;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.prometteur.sathiya.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prometteur.sathiya.adapters.UserBlockedListAdapter;
import com.prometteur.sathiya.adapters.UserCalledListAdapter;
import com.prometteur.sathiya.adapters.UserLikedListAdapter;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.databinding.ActivityProfileBlockedBinding;
import com.prometteur.sathiya.databinding.ActivityProfileCalledBinding;
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

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import static com.prometteur.sathiya.SplashActivity.strLang;

//import android.support.v7.app.BaseActivity;

public class ProfileBlockedActivity extends BaseActivity {

     LinearLayoutManager linearLayoutManager;
    UserBlockedListAdapter userLikedListAdapter;
    BaseActivity nActivity= ProfileBlockedActivity.this;
ActivityProfileBlockedBinding profileLikedBinding;

    ArrayList<String> tokans;
    String HeaderTitle = "", PageType = "";
    private ArrayList<beanUserData> arrShortListedUser;
    SharedPreferences prefUpdate;
    String matri_id = "",gender="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileLikedBinding=ActivityProfileBlockedBinding.inflate(getLayoutInflater());

        setContentView(profileLikedBinding.getRoot());

        tokans = new ArrayList<>();
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", "");
        ThirdHomeActivity.login_matri_id=matri_id;
        gender = prefUpdate.getString("gender", "");
        strLang = prefUpdate.getString("selLang", "");
        Log.e("Save Data= ", " MatriId=> " + matri_id);

        PageType = "2";

        profileLikedBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        linearLayoutManager=new LinearLayoutManager(nActivity);
        // listSalonBinding.recycleListsaloonView.setNestedScrollingEnabled(false);
        profileLikedBinding.rvLikedUsers.setLayoutManager(linearLayoutManager);



    }


    private void getShortlistedProfileRequest(String strMatriId,String strGender) {
        profileLikedBinding.progressBar1.setVisibility(View.VISIBLE);
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsMatriId = params[0];
                String paramsGender = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";
                if (PageType.equalsIgnoreCase("1")) {
                    URL = AppConstants.MAIN_URL + "shortlisted.php";
                    Log.e("URL shortlisted", "== " + URL);
                } else if (PageType.equalsIgnoreCase("2")) {
                    URL = AppConstants.MAIN_URL + "blocklist.php";
                    Log.e("blocklist", "== " + URL);
                } else if (PageType.equalsIgnoreCase("3")) {
                    URL = AppConstants.MAIN_URL + "profile_viewd_by_me.php";
                    Log.e("profile_viewd_by_me", "== " + URL);
                } else if (PageType.equalsIgnoreCase("4")) {
                    URL = AppConstants.MAIN_URL + "profile_visited_by_i.php";
                    Log.e("profile_visited_by_i", "== " + URL);
                } else if (PageType.equalsIgnoreCase("5")) {
                    URL = AppConstants.MAIN_URL + "wath_mobileno.php";
                    Log.e("wath_mobileno", "== " + URL);
                }


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", paramsMatriId);
                BasicNameValuePair GenderPair = new BasicNameValuePair("gender", paramsGender);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(GenderPair);
                nameValuePairList.add(languagePAir);


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

                Log.e("shortlisted", "==" + result);
                tokans = new ArrayList<>();
                tokans.clear();
                try {
                    if (result!=null) {
                        JSONObject obj = new JSONObject(result);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            arrShortListedUser = new ArrayList<beanUserData>();
                            JSONObject responseData = obj.getJSONObject("responseData");

                            if (responseData.has("1")) {
                                Iterator<String> resIter = responseData.keys();

                                while (resIter.hasNext()) {

                                    String key = resIter.next();
                                    JSONObject resItem = responseData.getJSONObject(key);

                                   // String user_id = resItem.getString("user_id");
                                    String matri_id1 = resItem.getString("matri_id");
                                    String username = resItem.getString("username");
                                    String birthdate = resItem.getString("birthdate");
                                    String ocp_name = resItem.getString("ocp_name");
                                    String height = resItem.getString("height");
                                    //String Address=ocp_name;
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
                                    tokans.add(resItem.getString("tokan"));
                                    arrShortListedUser.add(new beanUserData(/*user_id,*/ matri_id1, username, birthdate, ocp_name, height, Address, city_name, country_name, photo1_approve, photo_view_status, photo_protect,
                                            photo_pswd, gender1, is_shortlisted, is_blocked, is_favourite, user_profile_picture));

                                }

                                if (arrShortListedUser.size() > 0) {
                                    profileLikedBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                                    profileLikedBinding.tvEmptyMsg.setVisibility(View.GONE);

                                    if (PageType.equalsIgnoreCase("2")) {
                                        UserBlockedListAdapter adapterBlockedUser = new UserBlockedListAdapter(nActivity, arrShortListedUser, profileLikedBinding.rvLikedUsers);
                                        profileLikedBinding.rvLikedUsers.setAdapter(adapterBlockedUser);
                                    } else {

                                      /*  userLikedListAdapter=new UserLikedListAdapter(nActivity, arrShortListedUser,tokans);
                                        profileLikedBinding.rvLikedUsers.setAdapter(userLikedListAdapter);*/
                                    }

                                } else {
                                    profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                                    profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                            profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                        }
                        profileLikedBinding.progressBar1.setVisibility(View.GONE);
                    }else {
                        getShortlistedProfileRequest(matri_id,gender);
                    }
                } catch (Exception t) {
                    Log.d("ERRRR", t.toString());
                    profileLikedBinding.progressBar1.setVisibility(View.GONE);
                }
                profileLikedBinding.progressBar1.setVisibility(View.GONE);
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strMatriId,strGender);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkConnection.hasConnection(nActivity)) {
            getShortlistedProfileRequest(matri_id,gender);
        } else {
            AppConstants.CheckConnection(nActivity);
        }
    }


}
