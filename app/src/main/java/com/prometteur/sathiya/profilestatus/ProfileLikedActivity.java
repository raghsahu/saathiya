package com.prometteur.sathiya.profilestatus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.prometteur.sathiya.BaseActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.prometteur.sathiya.LoginActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.UserBlockedListAdapter;
import com.prometteur.sathiya.adapters.UserLikedListAdapter;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.databinding.ActivityProfileLikedBinding;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;

import org.json.JSONArray;
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
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

//import android.support.v7.app.BaseActivity;

public class ProfileLikedActivity extends BaseActivity {

     LinearLayoutManager linearLayoutManager;
     UserLikedListAdapter userLikedListAdapter;
    ArrayList<String> tokans;
    String HeaderTitle = "", PageType = "";
    BaseActivity nActivity=ProfileLikedActivity.this;
ActivityProfileLikedBinding profileLikedBinding;
    private ArrayList<beanUserData> arrShortListedUser;
    SharedPreferences prefUpdate;
    String matri_id = "",gender="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileLikedBinding=ActivityProfileLikedBinding.inflate(getLayoutInflater());

        setContentView(profileLikedBinding.getRoot());
        tokans = new ArrayList<>();
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", "");
        gender = prefUpdate.getString("gender", "");
        strLang = prefUpdate.getString("selLang", "");
        Log.e("Save Data= ", " MatriId=> " + matri_id);

        PageType = "1";

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
        /*final ProgressDialog progresDialog11 = new ProgressDialog(nActivity);
        progresDialog11.setCancelable(false);
        progresDialog11.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog11.setIndeterminate(true);*/
        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsMatriId = params[0];
                String paramsGender = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";
                if (PageType.equalsIgnoreCase("1")) {
                    URL = AppConstants.MAIN_URL + "intrest_sent_all.php";
                    Log.e("URL shortlisted", "== " + URL);
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
                                List<String> keyList=new ArrayList<>();

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
                                            "", gender1, "", is_blocked, "", user_profile_picture,eiId,""));
                                }

                                if (arrShortListedUser.size() > 0) {
                                    profileLikedBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                                    profileLikedBinding.tvEmptyMsg.setVisibility(View.GONE);

                                    if (PageType.equalsIgnoreCase("2")) {
                                        UserBlockedListAdapter adapterBlockedUser = new UserBlockedListAdapter(nActivity, arrShortListedUser, profileLikedBinding.rvLikedUsers);
                                        profileLikedBinding.rvLikedUsers.setAdapter(adapterBlockedUser);
                                    } else {

                                        userLikedListAdapter=new UserLikedListAdapter(nActivity, arrShortListedUser,tokans);
                                        profileLikedBinding.rvLikedUsers.setAdapter(userLikedListAdapter);
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
                        progresDialog11.dismiss();
                    }else {
                        getShortlistedProfileRequest(matri_id,gender);
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
