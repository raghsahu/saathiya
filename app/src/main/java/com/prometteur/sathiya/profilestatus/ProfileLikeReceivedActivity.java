package com.prometteur.sathiya.profilestatus;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.prometteur.sathiya.BaseActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.LikeReceivedListAdapter;
import com.prometteur.sathiya.adapters.UserBlockedListAdapter;
import com.prometteur.sathiya.adapters.UserLikedListAdapter;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.databinding.ActivityProfileLikeReceivedBinding;
import com.prometteur.sathiya.listeners.OnClickOnItemListener;
import com.prometteur.sathiya.model.DateObject;
import com.prometteur.sathiya.model.HistoryDataModelObject;
import com.prometteur.sathiya.model.ListObject;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.DateParser;
import com.prometteur.sathiya.utills.NetworkConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

//import android.support.v7.app.BaseActivity;

public class ProfileLikeReceivedActivity extends BaseActivity implements OnClickOnItemListener {

    LinearLayoutManager linearLayoutManager;
    LikeReceivedListAdapter userLikedRecListAdapter;
    UserLikedListAdapter userLikedListAdapter;
    ArrayList<String> tokans;
    String PageType = "";
    SharedPreferences prefUpdate;
    String matri_id = "", gender = "";
    BaseActivity nActivity = ProfileLikeReceivedActivity.this;
    ActivityProfileLikeReceivedBinding profileLikedBinding;
    private ArrayList<beanUserData> arrShortListedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileLikedBinding = ActivityProfileLikeReceivedBinding.inflate(getLayoutInflater());

        setContentView(profileLikedBinding.getRoot());

        tokans = new ArrayList<>();
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", "");
        gender = prefUpdate.getString("gender", "");
        strLang = prefUpdate.getString("selLang", "");
        profileLikedBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        linearLayoutManager = new LinearLayoutManager(nActivity);
        PageType = "1";
        profileLikedBinding.tvWaitingForResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PageType = "1";
                arrShortListedUser = new ArrayList<>();
                if (NetworkConnection.hasConnection(nActivity)) {
                    getShortlistedProfileRequest(matri_id);

                } else {
                    AppConstants.CheckConnection(nActivity);
                }
            }
        });
        profileLikedBinding.tvLikedUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrShortListedUser = new ArrayList<>();
                PageType = "0";
                linearLayoutManager = new LinearLayoutManager(nActivity);
                // listSalonBinding.recycleListsaloonView.setNestedScrollingEnabled(false);
                profileLikedBinding.rvLikedUsers.setLayoutManager(linearLayoutManager);
                if (NetworkConnection.hasConnection(nActivity)) {
                    getShortlistedProfileRequest(matri_id, gender);
                } else {
                    AppConstants.CheckConnection(nActivity);
                }

            }
        });
        profileLikedBinding.tvAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PageType = "2";
                arrShortListedUser = new ArrayList<>();

                if (NetworkConnection.hasConnection(nActivity)) {
                    getShortlistedProfileRequest(matri_id);
                } else {
                    AppConstants.CheckConnection(nActivity);
                }
            }
        });
        /*profileLikedBinding.tvRejected.setOnClickListener(new View.OnClickListener() {  //commented due to separate rejected screen
            @Override
            public void onClick(View view) {
                PageType="5";
                arrShortListedUser=new ArrayList<>();
                getShortlistedProfileRequest(matri_id);
            }
        });*/


        // listSalonBinding.recycleListsaloonView.setNestedScrollingEnabled(false);


    }


    private void groupDataIntoHashMap(List<beanUserData> chatModelList) {
        LinkedHashMap<String, Set<beanUserData>> groupedHashMap = new LinkedHashMap<>();
        Set<beanUserData> list = null;
        for (beanUserData chatModel : chatModelList) {
            //Log.d(TAG, travelActivityDTO.toString());
            String hashMapKey = DateParser.convertDateToString(chatModel.getBirthdate().trim()); //eidate set to birthdate
            //Log.d(TAG, "start date: " + DateParser.convertDateToString(travelActivityDTO.getStartDate()));
            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashMap.get(hashMapKey).add(chatModel);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                list = new LinkedHashSet<>();
                list.add(chatModel);
                groupedHashMap.put(hashMapKey, list);
            }
        }
        //Generate list from map
        generateListFromMap(groupedHashMap);

    }

    private List<ListObject> generateListFromMap(LinkedHashMap<String, Set<beanUserData>> groupedHashMap) {
        // We linearly add every item into the consolidatedList.
        List<ListObject> consolidatedList = new ArrayList<>();
        for (String date : groupedHashMap.keySet()) {
            DateObject dateItem = new DateObject();
            dateItem.setDate(date);
            consolidatedList.add(dateItem);
            for (beanUserData chatModel : groupedHashMap.get(date)) {
                HistoryDataModelObject generalItem = new HistoryDataModelObject();
                generalItem.setChatModel(chatModel);
                consolidatedList.add(generalItem);
            }
        }
        profileLikedBinding.rvLikedUsers.setLayoutManager(linearLayoutManager);
        userLikedRecListAdapter = new LikeReceivedListAdapter(nActivity, false, null, PageType,ProfileLikeReceivedActivity.this);
        profileLikedBinding.rvLikedUsers.setAdapter(userLikedRecListAdapter);
        userLikedRecListAdapter.setDataChange(consolidatedList);

        return consolidatedList;
    }


    private void getShortlistedProfileRequest(String strMatriId) {

        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsMatriId = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";
                if (PageType.equalsIgnoreCase("1")) {
                    URL = AppConstants.MAIN_URL + "intrest_receive_all.php";
                    Log.e("URL shortlisted", "== " + URL);
                } else if (PageType.equalsIgnoreCase("2")) {
                    URL = AppConstants.MAIN_URL + "intrest_accept_list.php";
                    Log.e("blocklist", "== " + URL);
                } else if (PageType.equalsIgnoreCase("3")) {
                    URL = AppConstants.MAIN_URL + "profile_viewd_by_me.php";
                    Log.e("profile_viewd_by_me", "== " + URL);
                } else if (PageType.equalsIgnoreCase("4")) {
                    URL = AppConstants.MAIN_URL + "profile_visited_by_i.php";
                    Log.e("profile_visited_by_i", "== " + URL);
                } else if (PageType.equalsIgnoreCase("5")) {
                    URL = AppConstants.MAIN_URL + "reject_list.php";
                    Log.e("reject_list", "== " + URL);
                }


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = null;
                BasicNameValuePair GenderPair = null;

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                if (PageType.equalsIgnoreCase("1")) {
                    MatriIdPair = new BasicNameValuePair("matri_id", paramsMatriId);
                    GenderPair = new BasicNameValuePair("gender", gender);
                    nameValuePairList.add(GenderPair);
                } else if (PageType.equalsIgnoreCase("2")) {
                    MatriIdPair = new BasicNameValuePair("receiver_id", paramsMatriId);
                } else {
                    MatriIdPair = new BasicNameValuePair("matri_id", paramsMatriId);
                }

                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                nameValuePairList.add(MatriIdPair);
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
                    if (result != null) {
                        JSONObject obj = new JSONObject(result);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            arrShortListedUser = new ArrayList<beanUserData>();
                            JSONObject responseData = obj.getJSONObject("responseData");

                            if (responseData.has("1")) {
                                Iterator<String> resIter = responseData.keys();
                                List<String> keyList = new ArrayList<>();
                                try {
                                    while (resIter.hasNext()) {
                                        keyList.add(resIter.next());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    for (int i = 0; i < keyList.size(); i++) {
                                        JSONObject resItem = responseData.getJSONObject(keyList.get(i));

                                        String user_id = resItem.getString("user_id");
                                        String matri_id1 = resItem.getString("matri_id");
                                        String eiId = resItem.getString("ei_id");
                                        String gender1 = resItem.getString("gender");
                                        String username = resItem.getString("username");
                                        String city_name = "";
                                        if (PageType.equalsIgnoreCase("2")) {
                                            if (resItem.getString("city_name") != null && !resItem.getString("city_name").equalsIgnoreCase("null")) {
                                                city_name = resItem.getString("city_name");
                                            }
                                        } else {
                                            if (resItem.getString("city") != null && !resItem.getString("city").equalsIgnoreCase("null")) {
                                                city_name = resItem.getString("city");
                                            }
                                        }
                                        String user_profile_picture = resItem.getString("user_profile_picture");
                                        String is_blocked = resItem.getString("is_blocked");
                                        String eiSentDate = resItem.getString("ei_sent_date");
                                        String height = resItem.getString("height");

                                        String rejectedStatus = "";
                                        if (PageType.equalsIgnoreCase("5")) {
                                            rejectedStatus = resItem.getString("rejected_status");  //for rejected section only
                                        }

                                        tokans.add(resItem.getString("tokan"));
                                        beanUserData beanUserData = new beanUserData(user_id, matri_id1, username, eiSentDate, "", height, "", city_name, "", "", "", "",
                                                "", gender1, "", is_blocked, "", user_profile_picture, eiId, "");
                                        beanUserData.setRejectedStatus(rejectedStatus);
                                        arrShortListedUser.add(beanUserData);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (arrShortListedUser.size() > 0) {
                                    profileLikedBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                                    profileLikedBinding.tvEmptyMsg.setVisibility(View.GONE);
                                    groupDataIntoHashMap(arrShortListedUser);
                                    /*if (PageType.equalsIgnoreCase("2")) {
                                        UserBlockedListAdapter adapterBlockedUser = new UserBlockedListAdapter(nActivity, arrShortListedUser, profileLikedBinding.rvLikedUsers);
                                        profileLikedBinding.rvLikedUsers.setAdapter(adapterBlockedUser);
                                    } else {

                                        userLikedListAdapter=new UserLikedListAdapter(nActivity, arrShortListedUser,tokans);
                                        profileLikedBinding.rvLikedUsers.setAdapter(userLikedListAdapter);
                                    }*/

                                } else {
                                    profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                                    profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                            profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                        }
                        progresDialog11.dismiss();
                    } else {
                        if (NetworkConnection.hasConnection(nActivity)) {
                            getShortlistedProfileRequest(matri_id);

                        } else {
                            AppConstants.CheckConnection(nActivity);
                        }

                    }
                } catch (Exception t) {
                    profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                    profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                    Log.d("ERRRR", t.toString());
                    progresDialog11.dismiss();
                }
                progresDialog11.dismiss();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strMatriId);
    }

    private void getShortlistedProfileRequest(String strMatriId, String strGender) {
        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsMatriId = params[0];
                String paramsGender = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";
                if (PageType.equalsIgnoreCase("0")) {
                    URL = AppConstants.MAIN_URL + "intrest_sent_all.php";
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
                    if (result != null) {
                        JSONObject obj = new JSONObject(result);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            arrShortListedUser = new ArrayList<beanUserData>();
                            JSONObject responseData = obj.getJSONObject("responseData");

                            //if (responseData.has("1")) {
                            Iterator<String> resIter = responseData.keys();
                            List<String> keyList = new ArrayList<>();

                            while (resIter.hasNext()) {
                                // keyList.add(resIter.next());


                                JSONObject resItem = responseData.getJSONObject(resIter.next());

                                String user_id = resItem.getString("user_id");
                                String matri_id1 = resItem.getString("matri_id");
                                String eiId = resItem.getString("ei_id");
                                String gender1 = resItem.getString("gender");
                                String username = resItem.getString("username");
                                String city_name = resItem.getString("city");
                                String user_profile_picture = resItem.getString("user_profile_picture");
                                String is_blocked = resItem.getString("is_blocked");


                                tokans.add(resItem.getString("tokan"));
                                arrShortListedUser.add(new beanUserData(user_id, matri_id1, username, "", "", "", "", city_name, "", "", "", "",
                                        "", gender1, "", is_blocked, "", user_profile_picture, eiId, ""));
                            }

                            if (arrShortListedUser.size() > 0) {
                                profileLikedBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                                profileLikedBinding.tvEmptyMsg.setVisibility(View.GONE);

                                if (PageType.equalsIgnoreCase("2")) {
                                    UserBlockedListAdapter adapterBlockedUser = new UserBlockedListAdapter(nActivity, arrShortListedUser, profileLikedBinding.rvLikedUsers);
                                    profileLikedBinding.rvLikedUsers.setAdapter(adapterBlockedUser);
                                } else {

                                    userLikedListAdapter = new UserLikedListAdapter(nActivity, arrShortListedUser, tokans,ProfileLikeReceivedActivity.this);
                                    profileLikedBinding.rvLikedUsers.setAdapter(userLikedListAdapter);
                                }

                            } else {
                                profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                                profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                            }
                            // }
                        } else {
                            profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                            profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                        }
                        progresDialog11.dismiss();
                    } else {

                        getShortlistedProfileRequest(matri_id, gender);
                    }
                } catch (Exception t) {
                    profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                    profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                    Log.d("ERRRR", t.toString());
                    progresDialog11.dismiss();
                }
                progresDialog11.dismiss();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strMatriId, strGender);
    }

    private void getLikedCounts(String strMatriId) {
        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsMatriId = params[0];
                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "likes_counter.php";


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = new BasicNameValuePair("login_matri_id", paramsMatriId);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);

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

                Log.e("counts", "==" + result);
                tokans = new ArrayList<>();
                tokans.clear();
                try {
                    if (result != null) {
                        JSONObject obj = new JSONObject(result);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {

                            int mutually_liked = obj.getInt("mutually_liked");
                            int liked_profiles = obj.getInt("liked_profiles");
                            int likes_received = obj.getInt("likes_received");

                            if (mutually_liked != 0) {
                                profileLikedBinding.tvAccepted.setText(getString(R.string.mutually_liked) + "(" + mutually_liked + ")");
                            }else{
                                profileLikedBinding.tvAccepted.setText(getString(R.string.mutually_liked));
                            }
                            if (liked_profiles != 0) {
                                profileLikedBinding.tvLikedUsers.setText(getString(R.string.liked_profiles) + "(" + liked_profiles + ")");
                            }else{
                                profileLikedBinding.tvLikedUsers.setText(getString(R.string.liked_profiles));
                            }
                            if (likes_received != 0) {
                                profileLikedBinding.tvWaitingForResponse.setText(getString(R.string.likes_received) + "(" + likes_received + ")");
                            }else{
                                profileLikedBinding.tvWaitingForResponse.setText(getString(R.string.likes_received));
                            }


                        }

                    }
                    progresDialog11.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progresDialog11.dismiss();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strMatriId);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkConnection.hasConnection(nActivity)) {
            try {
                getLikedCounts(matri_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (PageType.equalsIgnoreCase("0")) {

                getShortlistedProfileRequest(matri_id, gender);
            } else {
                getShortlistedProfileRequest(matri_id);
            }
        } else {
            AppConstants.CheckConnection(nActivity);
        }
    }

    @Override
    public void onItemClick() {
        if (NetworkConnection.hasConnection(nActivity)) {
            try {
                getLikedCounts(matri_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AppConstants.CheckConnection(nActivity);
        }
    }


}
