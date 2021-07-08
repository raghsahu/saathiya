package com.prometteur.sathiya.profilestatus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.prometteur.sathiya.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.UserCalledListAdapter;
import com.prometteur.sathiya.adapters.UserVisitedListAdapter;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.databinding.ActivityProfileCalledBinding;
import com.prometteur.sathiya.databinding.ActivityProfileVisitedBinding;
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


public class ProfileVisitedActivity extends BaseActivity {

     LinearLayoutManager linearLayoutManager;
    UserVisitedListAdapter userLikedListAdapter;
    ArrayList<String> tokans;
    ArrayList<beanUserData> arrShortListedUser=new ArrayList<>();
    String HeaderTitle = "", PageType = "";
    SharedPreferences prefUpdate;
    String matri_id = "",gender="";

    BaseActivity nActivity= ProfileVisitedActivity.this;
ActivityProfileVisitedBinding profileLikedBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileLikedBinding=ActivityProfileVisitedBinding.inflate(getLayoutInflater());

        setContentView(profileLikedBinding.getRoot());
        tokans = new ArrayList<>();
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", "");
        gender = prefUpdate.getString("gender", "");
        strLang = prefUpdate.getString("selLang", "");
        PageType="4";
        profileLikedBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        linearLayoutManager=new LinearLayoutManager(nActivity);

        // listSalonBinding.recycleListsaloonView.setNestedScrollingEnabled(false);



    }


    private void getShortlistedProfileRequest(String strMatriId) {
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
                if(PageType.equalsIgnoreCase("1"))
                {
                    MatriIdPair = new BasicNameValuePair("matri_id", paramsMatriId);
                }else if(PageType.equalsIgnoreCase("2"))
                {
                    MatriIdPair = new BasicNameValuePair("receiver_id", paramsMatriId);
                }else
                {
                    MatriIdPair = new BasicNameValuePair("matri_id", paramsMatriId);
                }
                BasicNameValuePair GenderIdPair = new BasicNameValuePair("gender", gender);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(GenderIdPair);
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

                Log.e("visited", "==" + result);
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
                                try {
                                    while (resIter.hasNext()) {
                                        keyList.add(resIter.next());
                                    }
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                try {
                                    for (int i = 0; i < keyList.size(); i++) {
                                        JSONObject resItem = responseData.getJSONObject(keyList.get(i));

                                        //String user_id = resItem.getString("user_id");
                                        String matri_id1 = resItem.getString("matri_id");
                                        //String eiId = resItem.getString("ei_id");
                                        String gender1 = resItem.getString("gender");
                                        String username = resItem.getString("username");
                                        String city_name = resItem.getString("city_name");
                                        String user_profile_picture = resItem.getString("user_profile_picture");
                                        String is_blocked = resItem.getString("is_blocked");
                                        //String eiSentDate = resItem.getString("ei_sent_date");
                                        String height = resItem.getString("height");
                                        String is_favourite = resItem.getString("is_favourite");
                                        String eiId = resItem.getString("ei_id");


                                        tokans.add(resItem.getString("tokan"));
                                        arrShortListedUser.add(new beanUserData("", matri_id1, username, "", "", height, "", city_name, "", "", "", "",
                                                "", gender1, "", is_blocked, is_favourite, user_profile_picture, eiId,""));
                                    }
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                if (arrShortListedUser.size() > 0) {
                                    profileLikedBinding.rvLikedUsers.setVisibility(View.VISIBLE);
                                    profileLikedBinding.tvEmptyMsg.setVisibility(View.GONE);

                                    profileLikedBinding.rvLikedUsers.setLayoutManager(linearLayoutManager);
                                    userLikedListAdapter=new UserVisitedListAdapter(nActivity, false, arrShortListedUser);
                                    profileLikedBinding.rvLikedUsers.setAdapter(userLikedListAdapter);

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
                        getShortlistedProfileRequest(matri_id);
                    }
                } catch (Exception t) {
                    Log.d("ERRRR", t.toString());
                    progresDialog11.dismiss();
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
            getShortlistedProfileRequest(matri_id);
        } else {
            AppConstants.CheckConnection(nActivity);
        }
    }

}
