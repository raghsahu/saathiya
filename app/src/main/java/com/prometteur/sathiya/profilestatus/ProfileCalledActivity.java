package com.prometteur.sathiya.profilestatus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.prometteur.sathiya.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.UserCalledListAdapter;
import com.prometteur.sathiya.adapters.UserLikedListAdapter;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.databinding.ActivityProfileCalledBinding;
import com.prometteur.sathiya.databinding.ActivityProfileLikedBinding;
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
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

//import android.support.v7.app.BaseActivity;

public class ProfileCalledActivity extends BaseActivity {

     LinearLayoutManager linearLayoutManager;
     UserCalledListAdapter userLikedListAdapter;
List<beanUserData> mDataList=new ArrayList<>();

    BaseActivity nActivity= ProfileCalledActivity.this;
ActivityProfileCalledBinding profileLikedBinding;

    SharedPreferences prefUpdate;
    String matri_id = "",gender="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileLikedBinding=ActivityProfileCalledBinding.inflate(getLayoutInflater());

        setContentView(profileLikedBinding.getRoot());
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



    }
    @Override
    public void onResume() {
        super.onResume();
        if (NetworkConnection.hasConnection(nActivity)) {
            setContactPrivacyRequest(matri_id);
        } else {
            AppConstants.CheckConnection(nActivity);
        }
    }
    private void setContactPrivacyRequest(String strMatriId) {
        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsMatriId = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "contact_details.php";
                Log.e("contact_details", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", paramsMatriId);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
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

                Log.e("contact_details", "==" + result);

                try {
                    JSONObject obj = new JSONObject(result);

                    String status = obj.getString("status");
                    String msg = obj.getString("message");

                    if (status.equalsIgnoreCase("1")) {
                        mDataList = new ArrayList<beanUserData>();
                        JSONObject responseData = obj.getJSONObject("responseData");

                        if (responseData.has("1")) {

                            Iterator<String> resIter = responseData.keys();
                            while (resIter.hasNext()) {

                                String key = resIter.next();
                                JSONObject resItem = responseData.getJSONObject(key);

                               // String user_id = resItem.getString("user_id");
                                String matri_id1 = resItem.getString("matri_id");
                                String eiId = resItem.getString("ei_id");
//                                String gender1 = resItem.getString("gender");
                                String username = resItem.getString("name");
                                String city_name = resItem.getString("city");
                                String user_profile_picture = resItem.getString("photo");
                                String height = resItem.getString("height");
                                String is_blocked = resItem.getString("is_blocked");
                                String is_favourite = resItem.getString("is_favourite");

                                mDataList.add(new beanUserData("", matri_id1, username, "", "", height, "", city_name, "", "", "", "",
                                        "", "", "", is_blocked, is_favourite, user_profile_picture,eiId,""));
                                /*String contact_view_security = resItem.getString("contact_view_security");
                                String memberType = "";
                                if (contact_view_security.equalsIgnoreCase("0")) {
                                    memberType = "0";
                                    textVisibleMembers.setText("Show To Express Interest Accepted Paid Members");
                                    radioAcceptedPaidMembers.setChecked(true);
                                    radioPaidMembers.setChecked(false);
                                } else if (contact_view_security.equalsIgnoreCase("1")) {
                                    memberType = "1";
                                    textVisibleMembers.setText("Show To Paid Members");
                                    radioAcceptedPaidMembers.setChecked(false);
                                    radioPaidMembers.setChecked(true);
                                }*/


                            }
                            if(mDataList.size()>0) {
                                profileLikedBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                                profileLikedBinding.tvEmptyMsg.setVisibility(View.GONE);
                                linearLayoutManager = new LinearLayoutManager(nActivity);
                                // listSalonBinding.recycleListsaloonView.setNestedScrollingEnabled(false);
                                profileLikedBinding.rvLikedUsers.setLayoutManager(linearLayoutManager);
                                userLikedListAdapter = new UserCalledListAdapter(nActivity, false, mDataList);
                                profileLikedBinding.rvLikedUsers.setAdapter(userLikedListAdapter);
                            }else
                            {
                                profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                                profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                            }

                        }
                        else {
                            profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                            profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                            progresDialog11.dismiss();
                           // Toast.makeText(nActivity, "" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                        profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                        progresDialog11.dismiss();
                       // Toast.makeText(nActivity, "" + msg, Toast.LENGTH_SHORT).show();
                    }


                    progresDialog11.dismiss();
                } catch (Exception t) {
                    profileLikedBinding.rvLikedUsers.setVisibility(View.GONE);
                    profileLikedBinding.tvEmptyMsg.setVisibility(View.VISIBLE);
                    progresDialog11.dismiss();
                    Log.e("kflj", t.getMessage());
                   // Toast.makeText(nActivity, "ex..." + t.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strMatriId);
    }


}
