package com.prometteur.sathiya.home;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.chat.FriendsFragment.openChatOnce;
import static com.prometteur.sathiya.utills.AppConstants.sendPushNotification;
import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppConstants.setToastStrPinkBg;
import static com.prometteur.sathiya.utills.AppConstants.vibe;
import static com.prometteur.sathiya.utills.AppConstants.vibrateBig;
import static com.prometteur.sathiya.utills.AppConstants.vibrateSmall;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;
import static com.prometteur.sathiya.utills.AppMethods.shrinkAnim;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.prometteur.sathiya.BaseActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.SliderPhotoPagerAdapter;
import com.prometteur.sathiya.beans.beanHobbyImage;
import com.prometteur.sathiya.chat.ChatActivity;
import com.prometteur.sathiya.chat.FriendsFragment;
import com.prometteur.sathiya.databinding.ActivityThirdHomeBinding;
import com.prometteur.sathiya.hobbies.HobbiesInterestActivity;
import com.prometteur.sathiya.profile.ProfileActivity;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class ThirdHomeActivity extends BaseActivity {
    public static String matri_id, login_matri_id, gender, is_shortlist, strUserImage, username;
    ActivityThirdHomeBinding thirdHomeBinding;
    BaseActivity nActivity = ThirdHomeActivity.this;
    SharedPreferences prefUpdate;

    String mobileNo = "", userMobileNo = "", country_code = "", firebase_email = "";
    String Maritalstatus = "";
    String is_interest = "", RequestType = "";
    String tokans = "";
    String Photo_Pass = "";
    String is_blocked, genderOther = "";
    FriendsFragment.FragFriendClickFloatButton onClickFloatButton;
    String isFavorite = "", eiId = "";
    SliderPhotoPagerAdapter sliderPagerAdapter;
    List<beanHobbyImage> arrPhotos = new ArrayList<>();
    TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thirdHomeBinding = ActivityThirdHomeBinding.inflate(getLayoutInflater());
        setContentView(thirdHomeBinding.getRoot());
        thirdHomeBinding.ivBackClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        onClickFloatButton = new FriendsFragment.FragFriendClickFloatButton();
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        login_matri_id = prefUpdate.getString("matri_id", "");
        strUserImage = prefUpdate.getString("profile_image", "");
        gender = prefUpdate.getString("gender", "");
        username = prefUpdate.getString("username", "");
        strLang = prefUpdate.getString("selLang", "");
        userMobileNo = prefUpdate.getString("mobile", "");
        if (getIntent().getStringExtra("getListType") != null) {
            if (getIntent().getStringExtra("getListType").equalsIgnoreCase("call") || getIntent().getStringExtra("getListType").equalsIgnoreCase("msg")) {
                thirdHomeBinding.tvBlock.setVisibility(View.VISIBLE);
            } else {
                thirdHomeBinding.tvBlock.setVisibility(View.GONE);
            }
        } else {
            thirdHomeBinding.tvBlock.setVisibility(View.GONE);
        }
        Onclick();
       /* refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMemberProfile(login_matri_id, matri_id);
            }
        });*/
       /* if (!strUserImage.equalsIgnoreCase("")) {

            Glide.with(nActivity)
                    .load(strUserImage)
                    .error(R.drawable.ic_profile)
                    .into(thirdHomeBinding.civUserPicture);
        }*/
        //thirdHomeBinding.textUsername.setText("" + username);
        // thirdHomeBinding.tvRefNo.setText("#"+login_matri_id);
        if (NetworkConnection.hasConnection(nActivity)) {
            Log.e("getMemberData", "" + login_matri_id + " " + matri_id);
            getMemberProfile(login_matri_id, matri_id);

        } else {
            AppConstants.CheckConnection(nActivity);
        }
    }

    private void Onclick() {

        if (getIntent().getStringExtra("pageType") == null) {
            thirdHomeBinding.ivLike.setEnabled(true);
            thirdHomeBinding.tvLike.setEnabled(true);
            thirdHomeBinding.linlayCall.setEnabled(true);
            thirdHomeBinding.linlayChat.setEnabled(true);
            thirdHomeBinding.linlayLike.setEnabled(true);
        } else {
            if (getIntent().getStringExtra("pageType").equalsIgnoreCase("rejected")) {
                if(getIntent().getStringExtra("rejectStatus").equalsIgnoreCase("rejected_by")){
                    thirdHomeBinding.ivLike.setEnabled(false);
                    thirdHomeBinding.tvLike.setEnabled(false);
                    thirdHomeBinding.linlayLike.setEnabled(false);
                }else {
                    thirdHomeBinding.ivLike.setEnabled(true);
                    thirdHomeBinding.tvLike.setEnabled(true);
                    thirdHomeBinding.linlayLike.setEnabled(true);
                }
                thirdHomeBinding.linlayCall.setEnabled(false);
                thirdHomeBinding.linlayChat.setEnabled(false);

            } else {
                thirdHomeBinding.ivLike.setEnabled(true);
                thirdHomeBinding.tvLike.setEnabled(true);
                thirdHomeBinding.linlayCall.setEnabled(true);
                thirdHomeBinding.linlayChat.setEnabled(true);
                thirdHomeBinding.linlayLike.setEnabled(true);
            }
        }

        thirdHomeBinding.linlayLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RequestType.equalsIgnoreCase("Like")) {
                    String test = is_blocked;
                    Log.d("TAG", "CHECK =" + test);

                    if (!is_blocked.equalsIgnoreCase("1")) {
                        String msgBlock = nActivity.getString(R.string.you_can_not_express_you_interest_you_can_not_express_your_interest);

                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage(msgBlock).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        if(vibe!=null) {
                            vibe.vibrate(vibrateBig);
                        }
                        shrinkAnim(thirdHomeBinding.ivLike, nActivity);
                        sendPushNotification(tokans,
                                AppConstants.msg_express_intress + " " + login_matri_id,
                                AppConstants.msg_express_intress_title, AppConstants.express_intress_id);
                        sendInterestRequest(login_matri_id, matri_id, is_interest);
                    }
                } else if (RequestType.equalsIgnoreCase("Unlike")) {
                    if (!is_blocked.equalsIgnoreCase("1")) {
                        String msgBlock = nActivity.getString(R.string.you_can_not_express_you_interest_you_can_not_express_your_interest);

                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage(msgBlock).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        if(vibe!=null) {
                            vibe.vibrate(vibrateSmall);
                        }
                        shrinkAnim(thirdHomeBinding.ivLike, nActivity);
                        sendInterestRequestRemind(login_matri_id, eiId, is_interest);
                    }
                }
            }
        });


//____NOTE:____________0 = BLOCK_________1=UNBLOCK____________


        thirdHomeBinding.tvBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (thirdHomeBinding.tvBlock.getText().toString().equalsIgnoreCase("Block")) {

                addToBlockRequest(login_matri_id, matri_id, is_blocked);

                //}

               /* } else if (thirdHomeBinding.tvBlock.getText().toString().equalsIgnoreCase("Unblock")) {
                    addToBlockRequest(login_matri_id, matri_id, "1");
                }*/
            }
        });

       /* llShort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToShortListRequest(login_matri_id, matri_id,*//*singleUser.getMatri_id()*//*is_shortlist);
            }
        });*/
        thirdHomeBinding.linlayChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_blocked.equalsIgnoreCase("1")) {
                    String msgBlock = nActivity.getString(R.string.this_member_has_blocked_you_for_message);

                    AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                    builder.setMessage(msgBlock).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    openChatOnce = false;
                    Intent activityIntent = new Intent(ThirdHomeActivity.this, ChatActivity.class);
                    activityIntent.putExtra("friendEmail", firebase_email);
                    startActivity(activityIntent);
                    finish();
                }
                // onClickFloatButton.findIDEmail(,nActivity);
            }
        });


        thirdHomeBinding.linlayCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!is_blocked.equalsIgnoreCase("1")) {
                    String msgBlock = nActivity.getString(R.string.this_member_has_blocked_you_for_message);
                    
                    AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                    builder.setMessage(msgBlock).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    setContact(login_matri_id, matri_id, mobileNo);
                }
            }
        });

        thirdHomeBinding.tvHobbiesInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ThirdHomeActivity.this, HobbiesInterestActivity.class).putExtra("fromOtherProfile", "fromOtherProfile").putExtra("matriId", matri_id));
            }
        });
    }

    public void getMemberProfile(String strLoginMatriId, String strMatriId) {
        thirdHomeBinding.progressBar1.setVisibility(View.VISIBLE);
        final Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramLoginMatriId = params[0];
                String paramsMatriId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "profile_view.php";
                Log.e("View Profile", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                Log.e("getMemberData11", "" + paramLoginMatriId + " " + paramsMatriId);

                BasicNameValuePair LOginMatriIdPair = new BasicNameValuePair("login_matri_id", paramLoginMatriId);
                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", paramsMatriId);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(LOginMatriIdPair);
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

                Log.e("Search by maitri Id", "==" + result);

                try {
                    JSONObject obj = new JSONObject(result);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        JSONObject responseData = obj.getJSONObject("responseData");

                        if (responseData.has("1")) {
                            Iterator<String> resIter = responseData.keys();

                            while (resIter.hasNext()) {

                                String key = resIter.next();
                                JSONObject resItem = responseData.getJSONObject(key);
                                ProfileActivity.resItem = responseData.getJSONObject(key);
                                String matri_id = resItem.getString("matri_id");
                                String email = resItem.getString("email");
                                firebase_email = resItem.getString("firebase_email");
                                mobileNo = resItem.getString("mobile");
                                String firstname = resItem.getString("firstname");
                                String lastname = resItem.getString("lastname");
                                String userNname = resItem.getString("username");
                                country_code = resItem.getString("country_code");
                                String subcaste = resItem.getString("subcaste");
                                String gender = resItem.getString("gender");
                                genderOther = gender;
                                String birthdate = resItem.getString("birthdate");

                                tokans = resItem.getString("tokan");
                                Photo_Pass = resItem.getString("photo_pass");
                                String profile_text = resItem.getString("profile_text").trim();

                                thirdHomeBinding.tvUserDescription.setText(getNullAvoid(profile_text));

                                String diet = resItem.getString("diet");

                                is_shortlist = resItem.getString("is_shortlisted");
                                is_interest = resItem.getString("is_favourite");


                                if (is_interest.equalsIgnoreCase("1")) {
                                    RequestType = "Unlike";
                                    // thirdHomeBinding.ivInterest.setImageResource(R.drawable.ic_reminder);
                                    thirdHomeBinding.ivLike.setImageResource(R.drawable.ic_heart);
                                    thirdHomeBinding.tvLike.setText(getString(R.string.unlike));

                                } else {
                                    RequestType = "Like";
                                    thirdHomeBinding.ivLike.setImageResource(R.drawable.ic_heart_greybg);
                                    //thirdHomeBinding.ivInterest.setImageResource(R.drawable.ic_heart);
                                    thirdHomeBinding.tvLike.setText(getString(R.string.like));
                                }


                                thirdHomeBinding.textMaritalSts.setText("" + getNullAvoid(resItem.getString("m_status")));
                                thirdHomeBinding.tvUserMangedBy.setText("" + getNullAvoid(resItem.getString("profileby")));


                                thirdHomeBinding.tvUserName.setText(getNullAvoid(firstname) + " " + getNullAvoid(lastname));
                                // thirdHomeBinding.tvAge.setText(resItem.getString("age") + ", ");
                                thirdHomeBinding.tvUserEmail.setText(email);
                                thirdHomeBinding.tvUserHeight.setText(getNullAvoid(resItem.getString("height")));
                                thirdHomeBinding.tvUserDOB.setText("" + getNullAvoid(birthdate));
                                thirdHomeBinding.tvUserCaste.setText(getNullAvoid(resItem.getString("caste_name")));
                                thirdHomeBinding.tvUserReligion.setText(getNullAvoid(resItem.getString("religion")));
                                thirdHomeBinding.tvUserCity.setText(getNullAvoid(resItem.getString("city_name")));

                                thirdHomeBinding.tvUserProfession.setText("" + getNullAvoid(resItem.getString("occupation")));
                                thirdHomeBinding.textOccupation.setText("" + getNullAvoid(resItem.getString("occupation")));
                                thirdHomeBinding.tvUserSubCaste.setText("" + getNullAvoid(subcaste));
                                thirdHomeBinding.tvUserEducation.setText("" + getNullAvoid(resItem.getString("edu_detail")));
                                thirdHomeBinding.tvUserIncome.setText("" + getNullAvoid(resItem.getString("income")));
                                thirdHomeBinding.textEmployedIn.setText("" + getNullAvoid(resItem.getString("emp_in")));
                                thirdHomeBinding.tvUserMotherTongue.setText("" + getNullAvoid(resItem.getString("m_tongue")));

                                eiId = resItem.getString("ei_id");

                                thirdHomeBinding.tvUserDealMaker.setText("" + getNullAvoid(resItem.getString("deal_maker")));
                                thirdHomeBinding.tvUserState.setText("" + getNullAvoid(resItem.getString("state_name")));
                                thirdHomeBinding.tvUserReligion.setText("" + getNullAvoid(resItem.getString("religion_name")));
                                thirdHomeBinding.tvUserCaste.setText("" + getNullAvoid(resItem.getString("caste_name")));
                                //not existing
                                thirdHomeBinding.tvUserParntMob.setText("" + getNullAvoid(resItem.getString("profileby")));
                                if (getNullAvoid(resItem.getString("shadibudget")).isEmpty() || getNullAvoid(resItem.getString("shadibudget")).equalsIgnoreCase("Not Available") || getNullAvoid(resItem.getString("shadibudget")).equalsIgnoreCase("0")) {
                                    thirdHomeBinding.tvUserShadiBudget.setText(getNullAvoid(resItem.getString("shadibudget")));
                                } else {
                                    thirdHomeBinding.tvUserShadiBudget.setText("₹" + getNullAvoid(resItem.getString("shadibudget")) + " lacs approx");
                                }
                                thirdHomeBinding.tvUserAbled.setText("" + getNullAvoid(resItem.getString("physicalStatus")));
                                if (resItem.getString("time_to_call").equalsIgnoreCase("Any Time")) {
                                    thirdHomeBinding.tvUserCallTime.setText(getString(R.string.all_time));
                                } else {
                                    if (getNullAvoid(resItem.getString("time_to_call")).contains("Not Available")) {
                                        thirdHomeBinding.tvUserCallTime.setText("" + getString(R.string.all_time));
                                    } else {
                                        String[] timeArr = resItem.getString("time_to_call").split("-");
                                        if (timeArr.length > 1) {
                                            SimpleDateFormat sdfSource = new SimpleDateFormat("HH:mm");
                                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                                            try {
                                                thirdHomeBinding.tvUserCallTime.setText("" + sdf.format(sdfSource.parse(timeArr[0] + ":00").getTime()) + " - " + sdf.format(sdfSource.parse(timeArr[1] + ":00").getTime()));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            thirdHomeBinding.tvUserCallTime.setText("" + getNullAvoid(resItem.getString("time_to_call")));
                                        }
//                                    thirdHomeBinding.tvUserCallTime.setText("" +getNullAvoid(resItem.getString("time_to_call")));
                                    }
                                }

                                thirdHomeBinding.tvUserManglik.setText("" + getNullAvoid(resItem.getString("manglik")));


                                if (resItem.has("is_blocked")) {
                                    is_blocked = resItem.getString("is_blocked");

                                    if (is_blocked.equalsIgnoreCase("1")) {
                                        is_blocked = "0";
                                        thirdHomeBinding.tvBlock.setText(getString(R.string.block_cap));
                                    } else {
                                        is_blocked = "1";
                                        thirdHomeBinding.tvBlock.setText(getString(R.string.unblock_cap));
                                    }
                                } else {
                                    is_blocked = "0";
                                }
                                Log.e("bfgh", resItem.getString("is_blocked"));


                                String part_income = resItem.getString("part_income");


                                arrPhotos = new ArrayList<>();
                                String photo1 = resItem.getString("photo1");
                                String photo2 = resItem.getString("photo2");
                                String photo3 = resItem.getString("photo3");
                                String photo4 = resItem.getString("photo4");
                                String photo5 = resItem.getString("photo5");
                                String photo6 = resItem.getString("photo6");
                                String TotalMs = resItem.getString("total_cnt");
                                String useMs = resItem.getString("used_cnt");
                                Log.e("cnnttt", TotalMs + "  " + useMs);

                                String strUsername = firstname + " " + lastname;


                                thirdHomeBinding.tvUserMobile.setText("" + mobileNo);
                                thirdHomeBinding.tvUserGender.setText("" + gender);
                                thirdHomeBinding.tvUserFamIncome.setText("" + getNullAvoid(part_income));
                                thirdHomeBinding.tvUserDiet.setText("" + getNullAvoid(diet));

                                if (!photo1.equalsIgnoreCase("")) {
                                    Log.d("PROFILE_____", photo1);
                                    progresDialog.dismiss();
                                    /*thirdHomeBinding.progressBar1.setVisibility(View.VISIBLE);
                                    Glide.with(nActivity)
                                            .load(photo1)
                                            //.fit()
                                            .error(R.drawable.ic_profile)
                                            .into(thirdHomeBinding.ivClientThumbnail);*/
                                    beanHobbyImage hobbyImage = new beanHobbyImage("1", photo1);
                                    arrPhotos.add(hobbyImage);
                                }
                                if (!photo2.equalsIgnoreCase("")) {
                                    Log.d("PROFILE_____", photo2);
                                    progresDialog.dismiss();
                                    beanHobbyImage hobbyImage = new beanHobbyImage("2", photo2);
                                    arrPhotos.add(hobbyImage);
                                }
                                if (!photo3.equalsIgnoreCase("")) {
                                    Log.d("PROFILE_____", photo3);
                                    progresDialog.dismiss();
                                    beanHobbyImage hobbyImage = new beanHobbyImage("3", photo3);
                                    arrPhotos.add(hobbyImage);
                                }
                                if (!photo4.equalsIgnoreCase("")) {
                                    Log.d("PROFILE_____", photo4);
                                    progresDialog.dismiss();
                                    beanHobbyImage hobbyImage = new beanHobbyImage("4", photo4);
                                    arrPhotos.add(hobbyImage);
                                }
                                if (!photo5.equalsIgnoreCase("")) {
                                    Log.d("PROFILE_____", photo5);
                                    progresDialog.dismiss();
                                    beanHobbyImage hobbyImage = new beanHobbyImage("5", photo5);
                                    arrPhotos.add(hobbyImage);
                                }
                                if (!photo6.equalsIgnoreCase("")) {
                                    Log.d("PROFILE_____", photo6);
                                    progresDialog.dismiss();
                                    beanHobbyImage hobbyImage = new beanHobbyImage("6", photo6);
                                    arrPhotos.add(hobbyImage);
                                }


                            }
                            init();
                        }

                    }else if (status.equalsIgnoreCase("2")) {
                        progresDialog.dismiss();
                        thirdHomeBinding.progressBar1.setVisibility(View.GONE);
                        String msgError = obj.getString("message");
                        setToastStr(nActivity, "" + msgError);
                            finish();
                    } else {
                        progresDialog.dismiss();
                        thirdHomeBinding.progressBar1.setVisibility(View.GONE);
                        String msgError = obj.getString("message");
                        setToastStr(nActivity, "" + msgError);

                    }

                    progresDialog.dismiss();
                    thirdHomeBinding.progressBar1.setVisibility(View.GONE);
                } catch (Exception t) {
                    Log.e("edrj", t.getMessage());
                    setToastStr(nActivity, "" + t.getMessage());
                    progresDialog.dismiss();
                    thirdHomeBinding.progressBar1.setVisibility(View.GONE);
                }
                progresDialog.dismiss();
                thirdHomeBinding.progressBar1.setVisibility(View.GONE);
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strLoginMatriId, strMatriId);
    }

    private void addToBlockRequest(String login_matri_id, String strMatriId, final String isBlocked) {
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsLoginMatriId = params[0];
                String paramsUserMatriId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";
                if (isBlocked.equalsIgnoreCase("1")) {
                    URL = AppConstants.MAIN_URL + "remove_blocklist.php";
                    Log.e("remove_blocklist", "== " + URL);
                } else {
                    URL = AppConstants.MAIN_URL + "block_user.php";
                    Log.e("block_user", "== " + URL);
                }


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("matri_id", paramsLoginMatriId);
                BasicNameValuePair UserMatriIdPair = new BasicNameValuePair("block_matri_id", paramsUserMatriId);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(UserMatriIdPair);

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

                Log.e("block_user", "==" + result);

                try {
                    JSONObject obj = new JSONObject(result);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        //String message=obj.getString("message").toString().trim();

                        if (isBlocked.equalsIgnoreCase("1")) {
                            is_blocked = "0";
                            thirdHomeBinding.tvBlock.setText(getString(R.string.block_cap));
                            setToastStr(nActivity, getString(R.string.sucessfully_unblock));
                        } else {
                            is_blocked = "1";
                            thirdHomeBinding.tvBlock.setText(getString(R.string.unblock_cap));
                            setToastStr(nActivity, getString(R.string.successfully_blocked));
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

                    progresDialog.dismiss();
                } catch (Exception t) {
                    Log.e("fjkhgjkfa", t.getMessage());
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id, strMatriId, isBlocked);
    }

    private void sendInterestRequest(String login_matri_id, String strMatriId, String isFavorite) {
        this.isFavorite = isFavorite;
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsLoginMatriId = params[0];
                String paramsUserMatriId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "send_intrest.php";
                Log.e("send_intrest", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("sender_id", paramsLoginMatriId);
                BasicNameValuePair UserMatriIdPair = new BasicNameValuePair("receiver_id", paramsUserMatriId);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(UserMatriIdPair);

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

                Log.e("send_intrest", "==" + result);

                try {
                    JSONObject obj = new JSONObject(result);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        //   ivInterest.setImageResource(R.drawable.ic_reminder);

                        RequestType = "Unlike";
                        thirdHomeBinding.tvLike.setText(getString(R.string.unlike));
                        thirdHomeBinding.ivLike.setImageResource(R.drawable.ic_heart);
                        String message = obj.getString("message").toString().trim();
                        setToastStrPinkBg(nActivity, "" + message);

                        if (ThirdHomeActivity.this.isFavorite.equalsIgnoreCase("1")) {
                            //arrUserList.get(pos).setIs_favourite("0");
                            is_interest = "0";
                            ThirdHomeActivity.this.isFavorite = "0";
                        } else {
                            // arrUserList.get(pos).setIs_favourite("1");
                            is_interest = "1";
                            ThirdHomeActivity.this.isFavorite = "1";
                            eiId = "" + obj.getString("ei_id");
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
                    progresDialog.dismiss();
                } catch (Exception t) {
                    Log.e("fjglfjl", t.getMessage());
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id, strMatriId);
    }

    private void sendInterestRequestRemind(String login_matri_id, String strMatriId, final String isFavorite) {
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsLoginMatriId = params[0];
                String paramsEiId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "remove_intrest.php";
                Log.e("send_intrest", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("matri_id", paramsLoginMatriId);
                BasicNameValuePair UserMatriIdPair = new BasicNameValuePair("ei_id", paramsEiId);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(UserMatriIdPair);

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

                Log.e("send_intrest", "==" + result);

                try {
                    JSONObject obj = new JSONObject(result);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {

                        String message = obj.getString("message").toString().trim();
                        setToastStrPinkBg(nActivity, "" + message);
                        RequestType = "Like";
                        thirdHomeBinding.tvLike.setText(getString(R.string.like));
                        thirdHomeBinding.ivLike.setImageResource(R.drawable.ic_heart_greybg);
                        if (ThirdHomeActivity.this.isFavorite.equalsIgnoreCase("1")) {
                            //arrUserList.get(pos).setIs_favourite("0");
                            ThirdHomeActivity.this.isFavorite = "0";
                        } else {
                            // arrUserList.get(pos).setIs_favourite("1");
                            ThirdHomeActivity.this.isFavorite = "1";
                            //4  eiId=""+obj.getString("ei_id");
                        }

                        // }


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


                    progresDialog.dismiss();
                } catch (Throwable t) {
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id, strMatriId);
    }

    private void setContact(String strLoginMatriId, String matriId, String userMobile) {

        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";

                URL = AppConstants.MAIN_URL + "contact.php";
                Log.e("URL get price", "== " + URL);


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = null;

                MatriIdPair = new BasicNameValuePair("matri_id", matriId);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("login_matri_id", strLoginMatriId);
                BasicNameValuePair LoginSelectedUserPair = new BasicNameValuePair("other_user_mb", userMobile); //selected user
                BasicNameValuePair LoginUserPair = new BasicNameValuePair("user_mob", userMobileNo);//loginUser

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(LoginSelectedUserPair);
                nameValuePairList.add(LoginUserPair);

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

                Log.e("get price", "==" + result);

                try {
                    if (result != null) {
                        JSONObject obj = new JSONObject(result);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:+911140844881,"+obj.getString("call_id")));//08046805845 //local number
                            nActivity.startActivity(callIntent);
                            AppConstants.setToastStrPinkBg(nActivity, "" + obj.getString("message"));
                        } else {
                            AppConstants.setToastStr(nActivity, "" + obj.getString("message"));
                        }
                        progresDialog11.dismiss();
                    }
                } catch (Exception t) {
                    Log.d("ERRRR", t.toString());
                    progresDialog11.dismiss();
                }
                progresDialog11.dismiss();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    public String getNullAvoid(String text) {
        if (text != null && !text.isEmpty() && !text.equalsIgnoreCase("null") && !text.equalsIgnoreCase("Not Available")) {
            return text;
        } else {
            return getString(R.string.not_available);
        }
    }

    private void init() {

        sliderPagerAdapter = new SliderPhotoPagerAdapter(nActivity, arrPhotos, genderOther);
        thirdHomeBinding.vpSlider.setAdapter(sliderPagerAdapter);

        thirdHomeBinding.vpSlider.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    private void addBottomDots(int currentPage) {
        dots = new TextView[arrPhotos.size()];

        thirdHomeBinding.llDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#000000"));
            thirdHomeBinding.llDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
    }

}