package com.prometteur.sathiya.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.appyvet.materialrangebar.RangeBar;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.GeneralAdapter;
import com.prometteur.sathiya.adapters.GeneralAdapter1;
import com.prometteur.sathiya.adapters.GeneralAdapter2;
import com.prometteur.sathiya.adapters.MotherTongueAdapter;
import com.prometteur.sathiya.adapters.beanMotherTongue;
import com.prometteur.sathiya.beans.beanGeneralData;
import com.prometteur.sathiya.databinding.FragmentEditOtherDetailsBinding;
import com.prometteur.sathiya.utills.AnimUtils;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.AppMethods;
import com.prometteur.sathiya.utills.NetworkConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.profile.EditProfileActivity.viewPager;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;


public class EditOtherDetailsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {


    ArrayList<beanMotherTongue> arrMotherTongue = new ArrayList<beanMotherTongue>();
    MotherTongueAdapter motherTongueAdapter = null;
    String matri_id = "";
    SharedPreferences prefUpdate;
    public EditOtherDetailsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentEditOtherDetailsBinding otherDetailsBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        otherDetailsBinding=FragmentEditOtherDetailsBinding.inflate(inflater,container, false);
        View view=otherDetailsBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(getActivity());
        matri_id = prefUpdate.getString("matri_id", "");
        strLang = prefUpdate.getString("selLang", "");
        otherDetailsBinding.tvNextClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String maritalStatusName=otherDetailsBinding.spinMaritalStatus.getText().toString();
                String manglikStatus=otherDetailsBinding.spinManglikStat.getText().toString();
                String specialyAbled=otherDetailsBinding.spinSpecialyAbled.getText().toString();
                String parentMobile=otherDetailsBinding.edtMobileParent.getText().toString();
                String DealMaker = otherDetailsBinding.edtDealMaker.getText().toString().trim();
                String Diet = otherDetailsBinding.tvDiet.getText().toString().trim();
                if(!callTime.contains("Any Time")) {
                    callTime=callStartTime+"-"+callEndTime;
                }
                if(!parentMobile.isEmpty())
                {
                    if(parentMobile.length()!=10){
                        otherDetailsBinding.edtMobileParent.setError(getResources().getString(R.string.please_enter_valid_mobile_number));
                        otherDetailsBinding.edtMobileParent.requestFocus();
                        return;
                    }
                }

                if(AppConstants.MotherTongueId.equalsIgnoreCase("null") || AppConstants.MotherTongueId.isEmpty()){
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_mother_tongue));
                }else if(specialyAbled.equalsIgnoreCase("null") || specialyAbled.isEmpty()){
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_specially_abled));
                }else if(Diet.isEmpty() || Diet.equalsIgnoreCase("Not Available")){
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_diet));
                }else {
                    getUpdateSteps(maritalStatusName, AppConstants.MotherTongueId, manglikStatus, specialyAbled,
                            parentMobile, shadiMinBgt, callTime, matri_id, DealMaker, Diet);
                }

            }
        });
        otherDetailsBinding.tvBackClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });


        otherDetailsBinding.spinMaritalStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisibleSlidingDrower();
                otherDetailsBinding.spinMaritalStatus.setError(null);

                otherDetailsBinding.includeSlide.edtdrawerMaritalStatus.setText("");
                otherDetailsBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linProfileCreatedBy.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linReligion.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linCaste.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linMotherTongue.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linCountry.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linCountryCode.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linGeneralView.setVisibility(View.VISIBLE);
                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(null);
                otherDetailsBinding.includeSlide.tvLabelFor.setHint(R.string.marital_status);
                otherDetailsBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                GeneralAdapter1 generalAdapter= new GeneralAdapter1(getActivity(), getResources().getStringArray(R.array.arr_marital_status),otherDetailsBinding.slidingDrawer,
                        otherDetailsBinding.slidingPage,otherDetailsBinding.btnMenuClose,otherDetailsBinding.spinMaritalStatus/*,textInputNoOfChiled,textInputChiledLivingStatus*/);
                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);
            }
        });


        otherDetailsBinding.spinMotherTongue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();

                getMotherTongue();
                getMotherToungeRequest();

                otherDetailsBinding.spinMotherTongue.setError(null);
                otherDetailsBinding.includeSlide.edtSearchMotherTongue.setText("");
                otherDetailsBinding.includeSlide.linProfileCreatedBy.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linReligion.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linCaste.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linMotherTongue.setVisibility(View.VISIBLE);
                otherDetailsBinding.includeSlide.linCountry.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linCountryCode.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.btnConfirm.setVisibility(View.GONE);

            }
        });

        otherDetailsBinding.spinManglikStat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisibleSlidingDrower();
                otherDetailsBinding.spinManglikStat.setError(null);

                otherDetailsBinding.includeSlide.linGeneralView.setVisibility(View.VISIBLE);
                otherDetailsBinding.includeSlide.btnConfirm.setVisibility(View.GONE);

                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(null);
                otherDetailsBinding.includeSlide.tvLabelFor.setHint(R.string.manglik_status);
                GeneralAdapter generalAdapter= new GeneralAdapter(getActivity(), getResources().getStringArray(R.array.arr_manglik),otherDetailsBinding.slidingDrawer,otherDetailsBinding.slidingPage,otherDetailsBinding.btnMenuClose,otherDetailsBinding.spinManglikStat);
                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);


            }
        });

        otherDetailsBinding.spinSpecialyAbled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                otherDetailsBinding.spinSpecialyAbled.setError(null);
                otherDetailsBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linGeneralView.setVisibility(View.VISIBLE);
                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(null);
                otherDetailsBinding.includeSlide.tvLabelFor.setHint(R.string.specially_abled);
                GeneralAdapter generalAdapter = new GeneralAdapter(getActivity(), getResources().getStringArray(R.array.arr_physical_status), otherDetailsBinding.slidingDrawer, otherDetailsBinding.slidingPage, otherDetailsBinding.btnMenuClose, otherDetailsBinding.spinSpecialyAbled);
                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);

            }
        });


        otherDetailsBinding.tvDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                otherDetailsBinding.tvDiet.setError(null);
                otherDetailsBinding.includeSlide.tvLabelFor.setHint(R.string.diet);
                otherDetailsBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linGeneralView.setVisibility(View.VISIBLE);
                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(null);
                otherDetailsBinding.includeSlide.linReligion.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linCaste.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linMotherTongue.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linCountry.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linState.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linCity.setVisibility(View.GONE);

                otherDetailsBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);

                otherDetailsBinding.includeSlide.btnConfirm.setVisibility(View.VISIBLE);
                ArrayList<beanGeneralData> arrGeneralData = new ArrayList<beanGeneralData>();
                Resources res = getResources();
                String[] arr_diet = res.getStringArray(R.array.arr_diet);
                selectedDiet=otherDetailsBinding.tvDiet.getText().toString();
                boolean isSelected=false;
                for (int i = 0; i < arr_diet.length; i++) {
                    if(!selectedDiet.isEmpty()){
                        String[] selDietArr=selectedDiet.split(",");
                        for(int j=0;j<selDietArr.length;j++){
                            if(arr_diet[i].equalsIgnoreCase(selDietArr[j])){
                                isSelected=true;
                                break;
                            }else{
                                isSelected=false;
                            }
                        }
                        arrGeneralData.add(new beanGeneralData("" + i, arr_diet[i], isSelected));
                    }else {
                        arrGeneralData.add(new beanGeneralData("" + i, arr_diet[i], false));
                    }
                }

                if (arrGeneralData.size() > 0) {
                    otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(null);
                    GeneralAdapter2 generalAdapter = new GeneralAdapter2(getActivity(), "eating_Habits", arrGeneralData, otherDetailsBinding.slidingDrawer, otherDetailsBinding.slidingPage, otherDetailsBinding.btnMenuClose, otherDetailsBinding.tvDiet, otherDetailsBinding.includeSlide.btnConfirm);
                    otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);

                } else {
                    otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(null);
                    GeneralAdapter generalAdapter = new GeneralAdapter(getActivity(), getResources().getStringArray(R.array.arr_diet), otherDetailsBinding.slidingDrawer,otherDetailsBinding.slidingPage, otherDetailsBinding.btnMenuClose, otherDetailsBinding.tvDiet);
                    otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);
                }
               /* GeneralAdapter generalAdapter= new GeneralAdapter(nActivity, getResources().getStringArray(R.array.arr_diet_2),SlidingDrawer,Slidingpage,btnMenuClose,edtDiet);
                rvGeneralView.setAdapter(generalAdapter);*/

            }
        });




        otherDetailsBinding.cbAnyTime.setOnCheckedChangeListener(this);
        if (NetworkConnection.hasConnection(getActivity())) {
            getViewProfileRequest(matri_id);

        } else {
            AppConstants.CheckConnection(getActivity());
        }

        otherDetailsBinding.rangebarCallTiming.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                callStartTime=""+leftPinValue;
                callEndTime=""+rightPinValue;
                SimpleDateFormat sdfSource=new SimpleDateFormat("HH:mm");
                SimpleDateFormat sdf=new SimpleDateFormat("hh:mm aa");
                try {
                    otherDetailsBinding.tvShowTiming.setText("("+sdf.format(sdfSource.parse(callStartTime+":00").getTime())+" - "+sdf.format(sdfSource.parse(callEndTime+":00").getTime())+")");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(!otherDetailsBinding.cbAnyTime.isChecked()){
                callTime=callStartTime+"-"+callEndTime;
                }

            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {

            }
        });

        SlidingMenu();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b) {
            callTime = "Any Time";
            otherDetailsBinding.rangebarCallTiming.setRangePinsByValue(1,24);
            otherDetailsBinding.tvShowTiming.setText("");
            otherDetailsBinding.rangebarCallTiming.setEnabled(false);
        }else
        {
            otherDetailsBinding.rangebarCallTiming.setEnabled(true);
        }
    }
String callTime="0",callStartTime="0",callEndTime="0";
    String shadiMinBgt="0";
    public void VisibleSlidingDrower() {
        otherDetailsBinding.slidingDrawer.setVisibility(View.VISIBLE);
        AnimUtils.SlideAnimation(getActivity(), otherDetailsBinding.slidingDrawer, R.anim.slide_right);
        //SlidingDrawer.startAnimation(AppConstants.inFromRightAnimation()) ;
        otherDetailsBinding.slidingPage.setVisibility(View.VISIBLE);


    }
    public void SlidingMenu() {






        otherDetailsBinding.includeSlide.rvMaritalStatus.setLayoutManager(new LinearLayoutManager(getActivity()));
        otherDetailsBinding.includeSlide.rvMaritalStatus.setHasFixedSize(true);

        otherDetailsBinding.includeSlide.rvMotherTongue.setLayoutManager(new LinearLayoutManager(getActivity()));
        otherDetailsBinding.includeSlide.rvMotherTongue.setHasFixedSize(true);

otherDetailsBinding.includeSlide.rvGeneralView.setLayoutManager(new LinearLayoutManager(getActivity()));
        otherDetailsBinding.includeSlide.rvGeneralView.setHasFixedSize(true);

        otherDetailsBinding.dynamicSeekbarShadiBudget.setSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                shadiMinBgt = "" + i;
                if(i==16){
                    otherDetailsBinding.dynamicSeekbarShadiBudget.setInfoText("above "+(i-1) + " lacs approx          ", i); //space given for visibility of ox in approx
                    otherDetailsBinding.tvShowBudget.setText("(above " + (i-1) + " lacs approx)");
                }else {
                    otherDetailsBinding.dynamicSeekbarShadiBudget.setInfoText(i + " lacs approx", i);
                    otherDetailsBinding.tvShowBudget.setText("(" + shadiMinBgt + " lacs approx)");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        otherDetailsBinding.btnMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GonelidingDrower();
            }
        });

        otherDetailsBinding.slidingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GonelidingDrower();

            }
        });


    }
    public void GonelidingDrower() {
        otherDetailsBinding.slidingDrawer.setVisibility(View.GONE);
        AnimUtils.SlideAnimation(getActivity(), otherDetailsBinding.slidingDrawer, R.anim.slide_left);
        otherDetailsBinding.slidingPage.setVisibility(View.GONE);
    }

    private void getMotherToungeRequest() {
        Dialog  progresDialog = showProgress(getActivity());
        /*progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);*/
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                // String paramUsername = params[0];


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "mother_tounge.php";
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                List<NameValuePair> nameValuePairList = new ArrayList<>();
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
                progresDialog.dismiss();
                Log.e("--mother_tounge --", "==" + Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");

                    arrMotherTongue = new ArrayList<beanMotherTongue>();

                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String mtongue_id = resItem.getString("mtongue_id");
                            String mtongue_name = resItem.getString("mtongue_name");

                            arrMotherTongue.add(new beanMotherTongue(mtongue_id, mtongue_name));

                        }

                        if (arrMotherTongue.size() > 0) {
                            Collections.sort(arrMotherTongue, new Comparator<beanMotherTongue>() {
                                @Override
                                public int compare(beanMotherTongue lhs, beanMotherTongue rhs) {
                                    return lhs.getName().compareTo(rhs.getName());
                                }
                            });
                            motherTongueAdapter = new MotherTongueAdapter(getActivity(), arrMotherTongue, otherDetailsBinding.slidingDrawer, otherDetailsBinding.slidingPage, otherDetailsBinding.spinMotherTongue);
                            otherDetailsBinding.includeSlide.rvMotherTongue.setAdapter(motherTongueAdapter);

                        }

                        resIter = null;

                    }

                    responseData = null;
                    responseObj = null;

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    public void getMotherTongue() {
        try {
            otherDetailsBinding.includeSlide.edtSearchMotherTongue.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (arrMotherTongue.size() > 0) {
                        String charcter = cs.toString();
                        String text = otherDetailsBinding.includeSlide.edtSearchMotherTongue.getText().toString().toLowerCase(Locale.getDefault());
                        motherTongueAdapter.filter(text);
                    }
                }

                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                public void afterTextChanged(Editable arg0) {
                }
            });
        } catch (Exception e) {

        }


    }

    JSONObject resItem;
    String selectedDiet="";
    private void getViewProfileRequest(String strMatriId) {
        /*final ProgressDialog progresDialog11 = new ProgressDialog(getActivity());
        progresDialog11.setCancelable(false);
        progresDialog11.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog11.setIndeterminate(true);*/
        final Dialog progresDialog11 = showProgress(getActivity());
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


                                //  edtProfileCreatedBy.setText(resItem.getString("profileby"));

                                String LFName = resItem.getString("username");
                                //String[] name_array = LFName.split(" ");


                                AppConstants.MotherTongueId = resItem.getString("m_tongue_id");

                                if(!resItem.getString("m_status").equalsIgnoreCase("null") && !resItem.getString("m_status").isEmpty() && !resItem.getString("m_status").equalsIgnoreCase("Not Available"))
                                {
                                    otherDetailsBinding.spinMaritalStatus.setText(resItem.getString("m_status"));
                                }
                                if(!resItem.getString("m_tongue").equalsIgnoreCase("null") && !resItem.getString("m_tongue").isEmpty() && !resItem.getString("m_tongue").equalsIgnoreCase("Not Available")) {
                                    otherDetailsBinding.spinMotherTongue.setText(resItem.getString("m_tongue"));
                                }
                                if(!resItem.getString("manglik").equalsIgnoreCase("null") && !resItem.getString("manglik").isEmpty() && !resItem.getString("manglik").equalsIgnoreCase("Not Available"))
                                {
                                    otherDetailsBinding.spinManglikStat.setText(resItem.getString("manglik"));
                                }
                                if(!resItem.getString("physicalStatus").equalsIgnoreCase("null") && !resItem.getString("physicalStatus").isEmpty() && !resItem.getString("physicalStatus").equalsIgnoreCase("Not Available"))
                                {
                                    otherDetailsBinding.spinSpecialyAbled.setText(resItem.getString("physicalStatus"));
                                }
                                otherDetailsBinding.edtMobileParent.setText(resItem.getString("mobile_parent"));
                                if(resItem.getString("shadibudget")!=null && !resItem.getString("shadibudget").isEmpty()) {
                                    shadiMinBgt=resItem.getString("shadibudget");
                                    if(Integer.parseInt(shadiMinBgt)==16){
                                        otherDetailsBinding.tvShowBudget.setText("(above "+(Integer.parseInt(shadiMinBgt)-1)+" lacs approx)");
                                        otherDetailsBinding.dynamicSeekbarShadiBudget.setInfoText("above "+(Integer.parseInt(resItem.getString("shadibudget"))-1) + " lacs approx",Integer.parseInt(resItem.getString("shadibudget")));
                                    }else {
                                        otherDetailsBinding.tvShowBudget.setText("(" + Integer.parseInt(shadiMinBgt) + " lacs approx)");
                                        otherDetailsBinding.dynamicSeekbarShadiBudget.setInfoText("" + Integer.parseInt(resItem.getString("shadibudget")) + " lacs approx", Integer.parseInt(resItem.getString("shadibudget")));
                                    }
                                    // otherDetailsBinding.rangebarShadiBgt.setTickEnd(Float.parseFloat(resItem.getString("shadibudget").split("-")[1]));
                                }
                                if (resItem.getString("deal_maker") != null && !resItem.getString("deal_maker").equalsIgnoreCase("null")) {
                                    otherDetailsBinding.edtDealMaker.setText(resItem.getString("deal_maker"));
                                }
                                if (resItem.getString("diet") != null && !resItem.getString("diet").equalsIgnoreCase("null")) {
                                    selectedDiet=resItem.getString("diet");
                                    otherDetailsBinding.tvDiet.setText(resItem.getString("diet"));
                                }
                                if(resItem.getString("time_to_call")!=null && !resItem.getString("time_to_call").isEmpty()) {
                                    if (resItem.getString("time_to_call").equalsIgnoreCase("Any Time")) {
                                        /*otherDetailsBinding.cbAnyTime.setOnCheckedChangeListener(null);
                                        otherDetailsBinding.cbAnyTime.setChecked(true);*/
                                        otherDetailsBinding.cbAnyTime.performClick();
                                       // otherDetailsBinding.cbAnyTime.setOnCheckedChangeListener(EditOtherDetailsFragment.this);
                                    } else {
                                        /*otherDetailsBinding.cbAnyTime.setOnCheckedChangeListener(null);
                                        otherDetailsBinding.cbAnyTime.setChecked(false);*/
                                      //  otherDetailsBinding.cbAnyTime.setOnCheckedChangeListener(EditOtherDetailsFragment.this);
                                        otherDetailsBinding.rangebarCallTiming.setRangePinsByValue(Float.parseFloat(resItem.getString("time_to_call").split("-")[0]),Float.parseFloat(resItem.getString("time_to_call").split("-")[1]));
//                                        otherDetailsBinding.rangebarCallTiming.setTickEnd(Float.parseFloat(resItem.getString("time_to_call").split("-")[1]));
                                    }
                                }

                            }

                        }


                    } else {
                        String msgError = obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("" + msgError).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                    progresDialog11.dismiss();
                } catch (Exception t) {
                    Log.e("eeeeeeee", t.getMessage());
                }
                progresDialog11.dismiss();


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strMatriId);
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            try {
                otherDetailsBinding.cbAnyTime.performClick();
                if(resItem.getString("time_to_call")!=null && !resItem.getString("time_to_call").isEmpty() && !resItem.getString("time_to_call").equalsIgnoreCase("Not Available")) {
                    if (resItem.getString("time_to_call").equalsIgnoreCase("Any Time")) {
                                            /*otherDetailsBinding.cbAnyTime.setOnCheckedChangeListener(null);*/
                        otherDetailsBinding.rangebarCallTiming.setTickStart(1);
                        otherDetailsBinding.rangebarCallTiming.setTickEnd(24);
                        otherDetailsBinding.tvShowTiming.setText("");
                                            otherDetailsBinding.cbAnyTime.setChecked(true);
//                        otherDetailsBinding.cbAnyTime.performClick();
                        // otherDetailsBinding.cbAnyTime.setOnCheckedChangeListener(EditOtherDetailsFragment.this);
                    } else {
                        otherDetailsBinding.cbAnyTime.setChecked(false);
                                            /*otherDetailsBinding.cbAnyTime.setOnCheckedChangeListener(null);
                                           */
                        //  otherDetailsBinding.cbAnyTime.setOnCheckedChangeListener(EditOtherDetailsFragment.this);
                        if(resItem.getString("time_to_call").split("-").length>0) {
                            SimpleDateFormat sdfSource=new SimpleDateFormat("HH:mm");
                            SimpleDateFormat sdf=new SimpleDateFormat("hh:mm aa");

                            otherDetailsBinding.tvShowTiming.setText("(" + sdf.format(sdfSource.parse(resItem.getString("time_to_call").split("-")[0] + ":00").getTime()) + " - " + sdf.format(sdfSource.parse(resItem.getString("time_to_call").split("-")[1] + ":00").getTime()) + ")");
                            callStartTime = resItem.getString("time_to_call").split("-")[0];
                            callEndTime = resItem.getString("time_to_call").split("-")[1];
                            otherDetailsBinding.rangebarCallTiming.setRangePinsByValue(Float.parseFloat(resItem.getString("time_to_call").split("-")[0]), Float.parseFloat(resItem.getString("time_to_call").split("-")[1]));
                            //                                        otherDetailsBinding.rangebarCallTiming.setTickEnd(Float.parseFloat(resItem.getString("time_to_call").split("-")[1]));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getUpdateSteps(String MaritalStatus, String MotherTongueId, String ManglikStatus, String SpeciallyAbled
            , String ParentMobile, String ShadiBget, String CallTiming, String matri_id,String DealMaker,String Diet) {
        Dialog  progresDialog = showProgress(getActivity());
        /*progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);*/
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String paramMaritalStatus = params[0];
                String paramMotherTongueId = params[1];
                String paramManglikStatus = params[2];
                String paramSpeciallyAbled = params[3];
                String paramParentMobile = params[4];
                String paramShadiBget = params[5];
                String paramCallTiming = params[6];
                String paramMatriid = params[7];
                String paramDealMaker = params[8];
                String paramDiet = params[9];


                HttpClient httpClient = new DefaultHttpClient();

                // String URL= AppConstants.MAIN_URL +"edit_step1.php";
                String URL = AppConstants.MAIN_URL + "edit_physical_details.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);


                BasicNameValuePair MaritalStatusPAir = new BasicNameValuePair("marital_status", paramMaritalStatus);
                BasicNameValuePair MotherTongueIdPAir = new BasicNameValuePair("mother_tongue", paramMotherTongueId);
                BasicNameValuePair ManglikStatusPair = new BasicNameValuePair("manglik", paramManglikStatus);
                BasicNameValuePair SpeciallyAbledPAir = new BasicNameValuePair("physical_status", paramSpeciallyAbled);
                BasicNameValuePair ParentMobilePAir = new BasicNameValuePair("mobile_parent", paramParentMobile);
                BasicNameValuePair MatriIdPAir = new BasicNameValuePair("matri_id", paramMatriid);
                BasicNameValuePair ShadiBgetPair = new BasicNameValuePair("shadibudget", paramShadiBget);
                BasicNameValuePair CallTimingPair = new BasicNameValuePair("time_to_call", paramCallTiming);
                BasicNameValuePair DealMakerPair = new BasicNameValuePair("deal_text", paramDealMaker);
                BasicNameValuePair DietPair = new BasicNameValuePair("diet", paramDiet);



                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(MaritalStatusPAir);
                nameValuePairList.add(MotherTongueIdPAir);
                nameValuePairList.add(ManglikStatusPair);
                nameValuePairList.add(SpeciallyAbledPAir);
                nameValuePairList.add(ParentMobilePAir);
                nameValuePairList.add(ShadiBgetPair);
                nameValuePairList.add(CallTimingPair);
                nameValuePairList.add(MatriIdPAir);
                nameValuePairList.add(DealMakerPair);
                nameValuePairList.add(DietPair);


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
                progresDialog.dismiss();
                Log.e("--cast --", "==" + Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);
//                    JSONObject responseData = responseObj.getJSONObject("responseData");

                    String status = responseObj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        String message = responseObj.getString("message");
                        AppConstants.setToastStrPinkBg(getActivity(), "" + message);
                        //viewPager.setCurrentItem(2);
                        //getActivity().finish();
                        viewPager.setCurrentItem(3);


                    } else {
                        String msgError = responseObj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("" + msgError).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                    responseObj = null;

                } catch (Exception e) {
                    Log.e("exception0", e.getMessage());

                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(MaritalStatus, MotherTongueId, ManglikStatus, SpeciallyAbled,
                ParentMobile, ShadiBget, CallTiming, matri_id,DealMaker,Diet);
    }
}
