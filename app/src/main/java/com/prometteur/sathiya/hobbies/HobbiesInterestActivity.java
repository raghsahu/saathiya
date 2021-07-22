package com.prometteur.sathiya.hobbies;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.prometteur.sathiya.BaseActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.HobbiesListAdapter;
import com.prometteur.sathiya.adapters.HobbiesOtherListAdapter;
import com.prometteur.sathiya.adapters.LikeReceivedListAdapter;
import com.prometteur.sathiya.beans.beanHobby;
import com.prometteur.sathiya.beans.beanHobbyImage;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.databinding.ActivityHobbiesInterestBinding;
import com.prometteur.sathiya.dialog.DialogAddPhotoActivity;
import com.prometteur.sathiya.dialog.DialogHobbiesSearchActivity;
import com.prometteur.sathiya.model.DateObject;
import com.prometteur.sathiya.model.HistoryDataModelObject;
import com.prometteur.sathiya.model.ListObject;
import com.prometteur.sathiya.model.UserDataObject;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.DateParser;
import com.prometteur.sathiya.utills.NetworkConnection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

public class HobbiesInterestActivity extends BaseActivity {

     RecyclerView.LayoutManager linearLayoutManager;
    HobbiesListAdapter userLikedListAdapter;
    HobbiesOtherListAdapter hobbiesOtherListAdapter;

    BaseActivity nActivity= HobbiesInterestActivity.this;
ActivityHobbiesInterestBinding hobbiesInterestBinding;
String matri_id="",strUserImage,username,cityName,otherMatriId="";
    SharedPreferences prefUpdate;
    public static String intType="myHobby";
    public static String km="";
    public static String hobbyId="";
    String firstTime="0";
    boolean isLoading = false;
    Dialog progressDialogSendReq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hobbiesInterestBinding=ActivityHobbiesInterestBinding.inflate(getLayoutInflater());

        setContentView(hobbiesInterestBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", "");
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        strUserImage = prefUpdate.getString("profile_image", "");
        username = prefUpdate.getString("username", "");
        cityName = prefUpdate.getString("city_name", "");
        strLang = prefUpdate.getString("selLang", "");
        hobbiesInterestBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        hobbiesInterestBinding.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(nActivity, DialogHobbiesSearchActivity.class));
            }
        });
        hobbiesInterestBinding.rvOthersHobbies.setVisibility(View.GONE);
        hobbiesInterestBinding.rvLikedUsers.setVisibility(View.VISIBLE);
        hobbiesInterestBinding.tvSearchResults.setVisibility(View.VISIBLE);
        linearLayoutManager=new GridLayoutManager(nActivity,2);


        if(getIntent().getStringExtra("fromOtherProfile")!=null){
            otherMatriId=getIntent().getStringExtra("matriId");

            hobbiesInterestBinding.radioGroup1.setVisibility(View.GONE);
            linearLayoutManager=new LinearLayoutManager(nActivity);
            firstTime="0";
            intType="Others";
           /* if (NetworkConnection.hasConnection(nActivity)) {
                progressDialogSendReq = showProgress(nActivity);
                progressDialogSendReq.show();
                consolidatedList1=new ArrayList<>();
                getOtherHobbyImageList(matri_id,hobbyId,km,0);
            } else {
                AppConstants.CheckConnection(nActivity);
            }*/
        }


        hobbiesInterestBinding.tvMyInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hobbiesInterestBinding.rvOthersHobbies.setVisibility(View.GONE);
                hobbiesInterestBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                hobbiesInterestBinding.tvSearchResults.setVisibility(View.VISIBLE);
                linearLayoutManager=new GridLayoutManager(nActivity,2);

                intType="myHobby";
                // listSalonBinding.recycleListsaloonView.setNestedScrollingEnabled(false);
                //  hobbiesInterestBinding.rvLikedUsers.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(getResources().getDimensionPixelOffset(R.dimen._5sdp)), true));
                if (NetworkConnection.hasConnection(nActivity)) {
                    getHobbyImageList(matri_id,hobbyId,km);
                } else {
                    AppConstants.CheckConnection(nActivity);
                }

            }
        });
        hobbiesInterestBinding.tvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hobbiesInterestBinding.rvOthersHobbies.setVisibility(View.VISIBLE);
                hobbiesInterestBinding.rvLikedUsers.setVisibility(View.GONE);
                hobbiesInterestBinding.tvSearchResults.setVisibility(View.GONE);
                linearLayoutManager=new LinearLayoutManager(nActivity);
                firstTime="0";
                intType="Others";
                if (NetworkConnection.hasConnection(nActivity)) {
                    progressDialogSendReq = showProgress(nActivity);
                    progressDialogSendReq.show();
                    consolidatedList1=new ArrayList<>();
                    getOtherHobbyImageList(matri_id,hobbyId,km,0);
                } else {
                    AppConstants.CheckConnection(nActivity);
                }



                // listSalonBinding.recycleListsaloonView.setNestedScrollingEnabled(false);
                //  hobbiesInterestBinding.rvLikedUsers.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(getResources().getDimensionPixelOffset(R.dimen._5sdp)), true));


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(intType.equalsIgnoreCase("myHobby")) {
            if (NetworkConnection.hasConnection(nActivity)) {
                getHobbyImageList(matri_id,hobbyId,km);
            } else {
                AppConstants.CheckConnection(nActivity);
            }
        }else
        {
            consolidatedList1=new ArrayList<>();
            if (NetworkConnection.hasConnection(nActivity)) {

                 progressDialogSendReq = showProgress(nActivity);
                progressDialogSendReq.show();
                firstTime="0";
                getOtherHobbyImageList(matri_id,hobbyId,km,0);
            } else {
                AppConstants.CheckConnection(nActivity);
            }
        }
    }

    List<beanHobbyImage> arrHobby =new ArrayList<>();
    private void getHobbyImageList(String matri_id,String hoby_id,String km) {

        progressDialogSendReq = showProgress(nActivity);
        progressDialogSendReq.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "get_my_interest.php";
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("login_matri_id", ""); //for used slider
                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", matri_id);
                BasicNameValuePair HobbyIdPair = new BasicNameValuePair("hoby_id", hoby_id);
                BasicNameValuePair KmPair = new BasicNameValuePair("km", km);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
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
                            String approvalStatus = resItem.getString("approval_status");

                            arrHobby.add(new beanHobbyImage(hobyId, hobyName,matriId,intrestId,photo,approvalStatus));

                        }
                        arrHobby.add(new beanHobbyImage("","","","","",""));
                        if (arrHobby.size() > 0) {
                            hobbiesInterestBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                            hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.GONE);
                            hobbiesInterestBinding.rvLikedUsers.setLayoutManager(linearLayoutManager);
                            userLikedListAdapter=new HobbiesListAdapter(nActivity, false, arrHobby);
                            hobbiesInterestBinding.rvLikedUsers.setAdapter(userLikedListAdapter);
                        }else
                        {
                            hobbiesInterestBinding.rvLikedUsers.setVisibility(View.GONE);
                            hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                        }

                        resIter = null;

                    }else{
                        arrHobby.add(new beanHobbyImage("","","","","",""));
                        if (arrHobby.size() > 0) {
                            hobbiesInterestBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                            hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.GONE);
                            hobbiesInterestBinding.rvLikedUsers.setLayoutManager(linearLayoutManager);
                            userLikedListAdapter=new HobbiesListAdapter(nActivity, false, arrHobby);
                            hobbiesInterestBinding.rvLikedUsers.setAdapter(userLikedListAdapter);
                        }else
                        {
                            hobbiesInterestBinding.rvLikedUsers.setVisibility(View.GONE);
                            hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                        }
                    }

                    responseData = null;
                    responseObj = null;

                    progressDialogSendReq.dismiss();
                } catch (Exception e) {
                    progressDialogSendReq.dismiss();
                    arrHobby.add(new beanHobbyImage("","","","","",""));
                    if (arrHobby.size() > 0) {
                        hobbiesInterestBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                        hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.GONE);
                        hobbiesInterestBinding.rvLikedUsers.setLayoutManager(linearLayoutManager);
                        userLikedListAdapter=new HobbiesListAdapter(nActivity, false, arrHobby);
                        hobbiesInterestBinding.rvLikedUsers.setAdapter(userLikedListAdapter);
                    }
                    progressDialogSendReq.dismiss();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    private void getOtherHobbyImageList(String matri_id,String hoby_id,String km,int nextLimit) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "get_other_photo_details.php";
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", matri_id);
                BasicNameValuePair HobbyIdPair = new BasicNameValuePair("hoby_id", hoby_id);
                BasicNameValuePair KmPair = new BasicNameValuePair("km", km);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                BasicNameValuePair nextLimitPAir = new BasicNameValuePair("last_id", ""+nextLimit);
                BasicNameValuePair otherMatriIdPAir = new BasicNameValuePair("other_matri_id", ""+otherMatriId);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(HobbyIdPair);
                nameValuePairList.add(KmPair);
                nameValuePairList.add(languagePAir);
                nameValuePairList.add(nextLimitPAir);
                nameValuePairList.add(otherMatriIdPAir);
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
if(progressDialogSendReq!=null){
    progressDialogSendReq.dismiss();
}
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
                            hobbiesInterestBinding.rvOthersHobbies.setVisibility(View.VISIBLE);
                            hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.GONE);
                            groupDataIntoHashMap(arrHobby);


                        }else
                        {
                            hobbiesInterestBinding.rvOthersHobbies.setVisibility(View.GONE);
                            hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                        }

                        resIter = null;

                    }else{

                            hobbiesInterestBinding.rvOthersHobbies.setVisibility(View.GONE);
                            hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.VISIBLE);

                    }

                    responseData = null;
                    responseObj = null;
                    if(progressDialogSendReq!=null && progressDialogSendReq.isShowing()) {
                        progressDialogSendReq.dismiss();
                    }

                } catch (Exception e) {
                   /* hobbiesInterestBinding.rvOthersHobbies.setVisibility(View.GONE);
                    hobbiesInterestBinding.tvEmptyMsg.setVisibility(View.VISIBLE);*/
                    if(progressDialogSendReq!=null){
                        progressDialogSendReq.dismiss();
                    }
                    if(consolidatedList1.size()!=0) {
                        consolidatedList1.remove(consolidatedList1.size() - 1);
                    }
                    int scrollPosition = consolidatedList1.size();
                    if(hobbiesOtherListAdapter!=null) {
                        hobbiesOtherListAdapter.notifyItemRemoved(scrollPosition);
                    }
                    if(progressDialogSendReq!=null && progressDialogSendReq.isShowing()) {
                        progressDialogSendReq.dismiss();
                    }
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
    private void groupDataIntoHashMap(List<beanHobbyImage> chatModelList) {
        LinkedHashMap<String, Set<beanHobbyImage>> groupedHashMap = new LinkedHashMap<>();
        Set<beanHobbyImage> list = null;
        for (beanHobbyImage chatModel : chatModelList) {
            //Log.d(TAG, travelActivityDTO.toString());
            String hashMapKey = chatModel.getMatri_id().trim(); //eidate set to birthdate
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
        if(consolidatedList1.size()!=0) {
            consolidatedList1.remove(consolidatedList1.size() - 1);
        }
        int scrollPosition = consolidatedList1.size();
        if(hobbiesOtherListAdapter!=null) {
            hobbiesOtherListAdapter.notifyItemRemoved(scrollPosition);
        }
        List<ListObject> objectList=generateListFromMap(groupedHashMap);
        if(objectList.size()!=0) {
            consolidatedList1.addAll(objectList);
        }
        if(firstTime.equalsIgnoreCase("0")) {
            hobbiesInterestBinding.rvOthersHobbies.setLayoutManager(linearLayoutManager);
            hobbiesOtherListAdapter = new HobbiesOtherListAdapter(nActivity, false, null);
            hobbiesInterestBinding.rvOthersHobbies.setAdapter(hobbiesOtherListAdapter);
            hobbiesOtherListAdapter.setDataChange(consolidatedList1);
        }else {
            hobbiesOtherListAdapter.setDataChange(consolidatedList1);
            hobbiesOtherListAdapter.notifyDataSetChanged();
        }
        isLoading = false;
        int currentSize = scrollPosition;
        int nextLimit = currentSize + 10;
        initScrollListener(Integer.parseInt(arrHobby.get(arrHobby.size()-1).getIntrest_id()));
    }
    List<ListObject> consolidatedList1=new ArrayList<>();
    private List<ListObject> generateListFromMap(LinkedHashMap<String, Set<beanHobbyImage>> groupedHashMap) {
        // We linearly add every item into the consolidatedList.
        List<ListObject> consolidatedList = new ArrayList<>();
        for (String date : groupedHashMap.keySet()) {
            UserDataObject dateItem = new UserDataObject();
            Set<beanHobbyImage> chatModelSet = groupedHashMap.get(date);
            List<beanHobbyImage> chatModelList=new ArrayList<>(chatModelSet);
            dateItem.setMatriId(date);
            dateItem.setUserName(chatModelList.get(0).getName());
            if(chatModelList.get(0).getLocation()!=null && !chatModelList.get(0).getLocation().equalsIgnoreCase("null")) {
                dateItem.setLocation(chatModelList.get(0).getLocation());
            }
            dateItem.setUserPhoto(chatModelList.get(0).getUserPhoto());
            consolidatedList.add(dateItem);
            for (beanHobbyImage chatModel : groupedHashMap.get(date)) {
                HistoryDataModelObject generalItem = new HistoryDataModelObject();
                generalItem.setHobbyImageModel(chatModel);
                consolidatedList.add(generalItem);
            }
        }

        return consolidatedList;
    }

    private void initScrollListener(int nextLimit) {
        hobbiesInterestBinding.rvOthersHobbies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == consolidatedList1.size() - 1) {
                        //bottom of list!
                        loadMore(nextLimit);
                        isLoading = true;
                    }
                }
            }
        });


    }

    private void loadMore(int nextLimit) {
        consolidatedList1.add(null);
        hobbiesOtherListAdapter.notifyItemInserted(consolidatedList1.size() - 1);
        firstTime="1";
        getOtherHobbyImageList(matri_id,hobbyId,km,nextLimit);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getIntent().getStringExtra("fromOtherProfile")!=null) {
            intType = "Others";
        }else
        {
            intType = "myHobby";
        }
        km="";
        hobbyId="";
    }
}
