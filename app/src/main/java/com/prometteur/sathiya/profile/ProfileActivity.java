package com.prometteur.sathiya.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.prometteur.sathiya.BaseActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.EditProfileTabAdapter;
import com.prometteur.sathiya.databinding.ActivityProfileBinding;
import com.prometteur.sathiya.dialog.DialogPlayVideoActivity;
import com.prometteur.sathiya.dialog.DialogWarningActivity;
import com.prometteur.sathiya.fragments.UserBasicInfoFragment;
import com.prometteur.sathiya.packages.PackageActivity;
import com.prometteur.sathiya.translateapi.MainViewModel;
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
import darren.googlecloudtts.GoogleCloudTTS;
import darren.googlecloudtts.GoogleCloudTTSFactory;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class ProfileActivity extends BaseActivity {

    public static JSONObject resItem;
    ActivityProfileBinding profileBinding;
    BaseActivity nActivity = ProfileActivity.this;
    String matri_id = "", call_package_status = "";
    SharedPreferences prefUpdate;
    EditProfileTabAdapter hotelDetailsTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", "");
        call_package_status = prefUpdate.getString("call_package_status", "");
        strLang = prefUpdate.getString("selLang", "");
        hotelDetailsTabAdapter = new EditProfileTabAdapter(getSupportFragmentManager());


        profileBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        profileBinding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, DialogWarningActivity.class));
            }
        });

        profileBinding.tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(nActivity, EditProfileActivity.class));
            }
        });
        profileBinding.ciProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(nActivity, EditProfileActivity.class).putExtra("page", "photo"));
            }
        });

        profileBinding.linPlayVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(nActivity, DialogPlayVideoActivity.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkConnection.hasConnection(nActivity)) {
            getViewProfileRequest(matri_id);

        } else {
            AppConstants.CheckConnection(nActivity);
        }
    }

    private void getViewProfileRequest(String strMatriId) {
      /*  final ProgressDialog progresDialog11 = new ProgressDialog(nActivity);
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

                String URL = AppConstants.MAIN_URL + "profile.php";
                Log.e("View Profile", "== " + URL);

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

                Log.e("updatebasic", "==" + result);

                String finalresult = "";
                try {
                    finalresult = result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1);

                    JSONObject obj = new JSONObject(finalresult);


                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        JSONObject responseData = obj.getJSONObject("responseData");

                        if (responseData.has("1")) {
                            Iterator<String> resIter = responseData.keys();

                            while (resIter.hasNext()) {

                                String key = resIter.next();
                                resItem = responseData.getJSONObject(key);
                                String FName = null;
                                String LName = null;
                                try {
                                    FName = resItem.getString("firstname");
                                    LName = resItem.getString("lastname");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                profileBinding.tvUserName.setText(FName + " " + LName);
                                profileBinding.tvUserId.setText("#" + resItem.getString("matri_id"));
                                if (call_package_status.equalsIgnoreCase("active")) {
                                    profileBinding.tvMembership.setText(getString(R.string.paid));
                                    profileBinding.linMembership.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(ProfileActivity.this, PackageActivity.class));
                                        }
                                    });
                                } else {
                                    profileBinding.tvMembership.setText(getString(R.string.unpaid));
                                }
                                Glide.with(nActivity).load(resItem.getString("photo1")).placeholder(R.drawable.bg_circle_placeholder_blackfill).error(R.drawable.bg_circle_placeholder_blackfill).into(profileBinding.ciProfilePic);
                                /*setUpHotelDetailsFragment*/
                                hotelDetailsTabAdapter.addFragment(new UserBasicInfoFragment(), "");
                                profileBinding.vpProfilePages.setAdapter(hotelDetailsTabAdapter);
                                profileBinding.courseViewPagerTabLayout.setupWithViewPager(profileBinding.vpProfilePages);
                                for (int i = 0; i < profileBinding.courseViewPagerTabLayout.getTabCount(); i++) {
                                    if (i == 0) {
                                        profileBinding.courseViewPagerTabLayout.getTabAt(i).setIcon(R.drawable.ic_menu_edt_prof);
                                    }
                                    if (i == 1) {
                                        profileBinding.courseViewPagerTabLayout.getTabAt(i).setIcon(R.drawable.ic_businessman_logo);
                                    }
                                    if (i == 2) {
                                        profileBinding.courseViewPagerTabLayout.getTabAt(i).setIcon(R.drawable.ic_bsuitcase_logo);
                                    }
                                }
                                profileBinding.vpProfilePages.setOffscreenPageLimit(1);


                            }

                        }


                    } else {
                        String msgError = obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage("" + msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                    if (progresDialog11.isShowing()) {
                        progresDialog11.dismiss();
                    }
                } catch (Exception t) {
                    Log.e("eeeeeeee", t.getMessage());
                }
                if (progresDialog11.isShowing()) {
                    progresDialog11.dismiss();
                }


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strMatriId);
    }
}