package com.prometteur.sathiya.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appyvet.materialrangebar.RangeBar;
import com.prometteur.sathiya.BaseActivity;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.SignUpStep1Activity;
import com.prometteur.sathiya.adapters.CasteAdapter;
import com.prometteur.sathiya.adapters.EducationsAdapter;
import com.prometteur.sathiya.adapters.GeneralAdapter;
import com.prometteur.sathiya.adapters.GeneralAdapter1;
import com.prometteur.sathiya.adapters.MotherTongueAdapter;
import com.prometteur.sathiya.adapters.OccupationAdapter;
import com.prometteur.sathiya.adapters.ReligionAdapter;
import com.prometteur.sathiya.adapters.SubCastAdapter;
import com.prometteur.sathiya.adapters.beanMotherTongue;
import com.prometteur.sathiya.beans.SubCast;
import com.prometteur.sathiya.beans.beanCaste;
import com.prometteur.sathiya.beans.beanEducation;
import com.prometteur.sathiya.beans.beanOccupation;
import com.prometteur.sathiya.beans.beanReligion;
import com.prometteur.sathiya.databinding.DialogUpdateFieldBinding;
import com.prometteur.sathiya.databinding.DialogWarningBinding;
import com.prometteur.sathiya.home.HomeActivity;
import com.prometteur.sathiya.utills.AnimUtils;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.prometteur.sathiya.utills.RecyclerTouchListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class DialogUdateFieldsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{
   DialogUpdateFieldBinding educationDetailBinding,otherDetailsBinding;
   AppCompatActivity nActivity;
    ArrayList<beanReligion> arrReligion = new ArrayList<beanReligion>();
    ReligionAdapter religionAdapter = null;

    ArrayList<beanCaste> arrCaste = new ArrayList<beanCaste>();
    CasteAdapter casteAdapter = null;

        ArrayList<SubCast> arrSubCast = new ArrayList<SubCast>();
    SubCastAdapter subCastAdapter = null;


    ArrayList<beanEducation> arrEducation = new ArrayList<beanEducation>();
    EducationsAdapter educationAdapter = null;

    ArrayList<beanOccupation> arrOccupation = new ArrayList<beanOccupation>();
    OccupationAdapter occupationAdapter = null;
    String matri_id = "";
    SharedPreferences prefUpdate;

    String Income="0",EmployedIn="";

    //other
    ArrayList<beanMotherTongue> arrMotherTongue = new ArrayList<beanMotherTongue>();
    MotherTongueAdapter motherTongueAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
       // getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        educationDetailBinding=DialogUpdateFieldBinding.inflate(getLayoutInflater());
        setContentView(educationDetailBinding.getRoot());
        nActivity=this;
        hideShowFields(getIntent().getStringExtra("fieldToUpdate"));
        String fieldToUpdate=getIntent().getStringExtra("fieldToUpdate");


        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", "");
        strLang = prefUpdate.getString("selLang", "");
        SlidingMenu();
        educationDetailBinding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSkipUpdate(matri_id);

            }
        });
        educationDetailBinding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkConnection.hasConnection(nActivity)) {
                    EmployedIn=educationDetailBinding.edtEmployedIn.getText().toString();
                    Income=educationDetailBinding.edtMonthlyIncome.getText().toString();
                    //other details
                    String maritalStatusName=otherDetailsBinding.spinMaritalStatus.getText().toString();
                    String manglikStatus=otherDetailsBinding.spinManglikStat.getText().toString();
                    String specialyAbled=otherDetailsBinding.spinSpecialyAbled.getText().toString();
                    String parentMobile=otherDetailsBinding.edtMobileParent.getText().toString();

                    if (fieldToUpdate.equalsIgnoreCase("edu_detail")){
                        if (AppConstants.EducationId.isEmpty()) {
                            educationDetailBinding.edtEducation.requestFocus();
                            AppConstants.setToastStr(nActivity, getResources().getString(R.string.please_select_education));
                        }else{
                            getUpdateSteps(fieldToUpdate,matri_id,AppConstants.EducationId);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("occupation"))
                    {
                        if (AppConstants.OccupationID.isEmpty()) {
                            educationDetailBinding.edtOccupation.requestFocus();
                            AppConstants.setToastStr(nActivity, getResources().getString(R.string.please_select_occupation));
                        }else{
                            getUpdateSteps(fieldToUpdate,matri_id,AppConstants.OccupationID);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("emp_in"))
                    {
                        if (educationDetailBinding.edtEmployedIn.getText().toString().isEmpty() ||EmployedIn.equalsIgnoreCase("Not Available")) {
                            educationDetailBinding.edtEmployedIn.requestFocus();
                            AppConstants.setToastStr(nActivity, getResources().getString(R.string.please_select_employed_in));
                        }else{
                            getUpdateSteps(fieldToUpdate,matri_id,EmployedIn);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("income"))
                    {
                        if (educationDetailBinding.edtMonthlyIncome.getText().toString().isEmpty()) {
                            educationDetailBinding.edtMonthlyIncome.requestFocus();
                            AppConstants.setToastStr(nActivity, getResources().getString(R.string.please_select_monthly_income));
                        }else{
                            getUpdateSteps(fieldToUpdate,matri_id,Income);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("religion"))
                    {
                        if (AppConstants.ReligionId.isEmpty() ||AppConstants.ReligionId.equalsIgnoreCase("0") ||AppConstants.ReligionId.equalsIgnoreCase("Not Available")) {
                            educationDetailBinding.edtReligion.requestFocus();
                            AppConstants.setToastStr(nActivity, getResources().getString(R.string.please_select_religion));
                        }else if (AppConstants.CasteId.isEmpty() ||AppConstants.CasteId.equalsIgnoreCase("0") ||AppConstants.CasteId.equalsIgnoreCase("Not Available")) {
                            educationDetailBinding.edtCaste.requestFocus();
                            AppConstants.setToastStr(nActivity, getResources().getString(R.string.please_select_caste));
                        }else if (AppConstants.SubCasteID.isEmpty() ||AppConstants.SubCasteID.equalsIgnoreCase("0") ||AppConstants.SubCasteID.equalsIgnoreCase("Not Available")) {
                            educationDetailBinding.edtSubCaste.requestFocus();
                            AppConstants.setToastStr(nActivity, getResources().getString(R.string.please_select_sub_caste));
                        }else{
                            getUpdateReligionDet(fieldToUpdate,matri_id);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("manglik"))
                    {
                        if (manglikStatus.isEmpty()) {
                            otherDetailsBinding.spinManglikStat.requestFocus();
                            AppConstants.setToastStr(nActivity, "Please select manglik status");
                        }else{
                            getUpdateSteps(fieldToUpdate,matri_id,manglikStatus);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("m_status"))
                    {
                        if (maritalStatusName.isEmpty()) {
                            otherDetailsBinding.spinMaritalStatus.requestFocus();
                            AppConstants.setToastStr(nActivity, "Please select marital status");
                        }else{
                            getUpdateSteps(fieldToUpdate,matri_id,maritalStatusName);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("m_tongue"))
                    {
                        if (AppConstants.MotherTongueId.isEmpty()) {
                            otherDetailsBinding.spinMotherTongue.requestFocus();
                            AppConstants.setToastStr(nActivity, "Please select mothertongue");
                        }else{
                            getUpdateSteps(fieldToUpdate,matri_id,AppConstants.MotherTongueId);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("mobile_parent"))
                    {
                        if (parentMobile.length()!=10) {
                            otherDetailsBinding.edtMobileParent.requestFocus();
                            AppConstants.setToastStr(nActivity, getResources().getString(R.string.please_enter_valid_mobile_number));
                        }else{
                            getUpdateSteps(fieldToUpdate,matri_id,parentMobile);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("shadibudget"))
                    {
                        if (shadiMinBgt.isEmpty()) {
                            AppConstants.setToastStr(nActivity, "Please select shadi budget");
                        }else{
                            getUpdateSteps(fieldToUpdate,matri_id,shadiMinBgt);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("physicalStatus"))
                    {
                        if (specialyAbled.isEmpty()) {
                            AppConstants.setToastStr(nActivity, "Please select specially abled");
                        }else{
                            getUpdateSteps(fieldToUpdate,matri_id,specialyAbled);
                        }
                    }else if(fieldToUpdate.equalsIgnoreCase("time_to_call"))
                    {
                        if(!callTime.contains("Any Time"))
                        {
                            callTime=callStartTime+"-"+callEndTime;
                        }
                        getUpdateSteps(fieldToUpdate,matri_id,callTime);
                    }
                }
            }
        });
        strLang = prefUpdate.getString("selLang", "");
       /* otherDetailsBinding.tvNextClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(!parentMobile.isEmpty())
                {
                    if(parentMobile.length()!=10){
                        otherDetailsBinding.edtMobileParent.setError(getResources().getString(R.string.please_enter_valid_mobile_number));
                        otherDetailsBinding.edtMobileParent.requestFocus();
                        return;
                    }
                }
                getUpdateSteps(maritalStatusName, AppConstants.MotherTongueId,manglikStatus , specialyAbled,
                        parentMobile,shadiMinBgt, callTime,matri_id);

            }
        });
     */
        educationDetailBinding.edtReligion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                educationDetailBinding.edtReligion.setError(null);
                educationDetailBinding.includeSlide.edtSearchReligion.setText("");
                educationDetailBinding.includeSlide.linReligion.setVisibility(View.VISIBLE);
                educationDetailBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                if (NetworkConnection.hasConnection(nActivity)) {
                    getReligionRequest();
                    getReligions();
                } else {
                    AppConstants.CheckConnection(nActivity);
                }
            }
        });

        educationDetailBinding.edtCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!educationDetailBinding.edtReligion.getText().toString().isEmpty()) {
                    VisibleSlidingDrower();
                    Log.e("religionid", AppConstants.ReligionId);
                    getCastRequest(AppConstants.ReligionId);
                    getCaste();
                } else {
                    Toast.makeText(nActivity, "Please select religion First", Toast.LENGTH_SHORT).show();
                }
                educationDetailBinding.edtCaste.setError(null);
                educationDetailBinding.includeSlide.edtSearchCaste.setText("");
                if (arrCaste.size() > 0) {

                } else {
                    educationDetailBinding.includeSlide.rvCaste.setAdapter(null);
                }

                educationDetailBinding.includeSlide.linReligion.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linCaste.setVisibility(View.VISIBLE);
                educationDetailBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
            }
        });


        educationDetailBinding.edtSubCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisibleSlidingDrower();
                educationDetailBinding.includeSlide.linSBCaste.setVisibility(View.VISIBLE);
                educationDetailBinding.includeSlide.linCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linReligion.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                getSubCasteRequest();
                getSubCaste();
            }
        });

        educationDetailBinding.includeSlide.rvReligion.addOnItemTouchListener(new RecyclerTouchListener(nActivity, educationDetailBinding.includeSlide.rvReligion, new SignUpStep1Activity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                InputMethodManager imm = (InputMethodManager) nActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(educationDetailBinding.includeSlide.rvReligion.getWindowToken(), 0);

                beanReligion arrCo = arrReligion.get(position);
                AppConstants.ReligionId = arrCo.getReligion_id();
                AppConstants.ReligionName = arrCo.getName();

                educationDetailBinding.edtReligion.setText(AppConstants.ReligionName);

                AppConstants.CasteId = "";
                AppConstants.StateName = "";

                educationDetailBinding.edtCaste.setText("");

                GonelidingDrower();

                if (NetworkConnection.hasConnection(nActivity)) {
                    getCastRequest(AppConstants.ReligionId);
                    getCaste();
                } else {
                    AppConstants.CheckConnection(nActivity);
                }


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        educationDetailBinding.edtEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                educationDetailBinding.edtEducation.setError(null);
                educationDetailBinding.includeSlide.edtSearchHighestEducation.setText("");
                educationDetailBinding.includeSlide.linHighestEducation.setVisibility(View.VISIBLE);
                educationDetailBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linReligion.setVisibility(View.GONE);
                //educationDetailBinding.btnConfirm.setVisibility(View.GONE);
            }
        });





        educationDetailBinding.edtOccupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                educationDetailBinding.edtOccupation.setError(null);
                educationDetailBinding.includeSlide.edtSearchOccupation.setText("");
                educationDetailBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linOccupation.setVisibility(View.VISIBLE);
                educationDetailBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linReligion.setVisibility(View.GONE);
                // educationDetailBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
            }
        });


        educationDetailBinding.edtEmployedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                educationDetailBinding.edtEmployedIn.setError(null);
                educationDetailBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linGeneralView.setVisibility(View.VISIBLE);
                educationDetailBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linReligion.setVisibility(View.GONE);
                //educationDetailBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.rvGeneralView.setAdapter(null);
                educationDetailBinding.includeSlide.tvLabelFor.setHint(R.string.employed_in);
                GeneralAdapter generalAdapter = new GeneralAdapter(nActivity, getResources().getStringArray(R.array.arr_Employed_in), educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.btnMenuClose, educationDetailBinding.edtEmployedIn);
                educationDetailBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);

            }
        });
        educationDetailBinding.edtMonthlyIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                educationDetailBinding.edtMonthlyIncome.setError(null);
                educationDetailBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linGeneralView.setVisibility(View.VISIBLE);
                educationDetailBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linCaste.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.linReligion.setVisibility(View.GONE);
                //educationDetailBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                educationDetailBinding.includeSlide.rvGeneralView.setAdapter(null);
                educationDetailBinding.includeSlide.tvLabelFor.setHint(R.string.income_in_lacs);
                GeneralAdapter generalAdapter = new GeneralAdapter(nActivity, getResources().getStringArray(R.array.arr_monthly_income), educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.btnMenuClose, educationDetailBinding.edtMonthlyIncome);
                educationDetailBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);

            }
        });

        if (NetworkConnection.hasConnection(nActivity)) {
            getOccupationsRequest();
            getOccupations();
            getHighestEducationRequest();
            getHighestEducation();
        } else {
            AppConstants.CheckConnection(nActivity);
        }

/////////////////////////////////////////////Other Details page content//////////////////////////////////////////
        otherDetailsBinding=educationDetailBinding;



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
                GeneralAdapter1 generalAdapter= new GeneralAdapter1(nActivity, getResources().getStringArray(R.array.arr_marital_status),otherDetailsBinding.slidingDrawer,
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
//                btnConfirm.setVisibility(View.GONE);

                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(null);
                otherDetailsBinding.includeSlide.tvLabelFor.setHint(R.string.manglik_status);
                GeneralAdapter generalAdapter= new GeneralAdapter(nActivity, getResources().getStringArray(R.array.arr_manglik),otherDetailsBinding.slidingDrawer,otherDetailsBinding.slidingPage,otherDetailsBinding.btnMenuClose,otherDetailsBinding.spinManglikStat);
                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);


            }
        });

        otherDetailsBinding.spinSpecialyAbled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                otherDetailsBinding.spinSpecialyAbled.setError(null);
                //btnConfirm.setVisibility(View.GONE);
                otherDetailsBinding.includeSlide.linGeneralView.setVisibility(View.VISIBLE);

                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(null);
                otherDetailsBinding.includeSlide.tvLabelFor.setHint(R.string.specially_abled);
                GeneralAdapter generalAdapter = new GeneralAdapter(nActivity, getResources().getStringArray(R.array.arr_physical_status), otherDetailsBinding.slidingDrawer, otherDetailsBinding.slidingPage, otherDetailsBinding.btnMenuClose, otherDetailsBinding.spinSpecialyAbled);
                otherDetailsBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);

            }
        });

        otherDetailsBinding.cbAnyTime.setOnCheckedChangeListener(this);

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
                callTime=callStartTime+"-"+callEndTime;

            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {

            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {

            }
        });

        SlidingMenuOther();
    }



    public void hideShowFields(String fieldToUpdate) {

        educationDetailBinding.tvLblEducation.setVisibility(View.GONE);
        educationDetailBinding.tvLblEmployedIn.setVisibility(View.GONE);
        educationDetailBinding.tvLblIncome.setVisibility(View.GONE);
        educationDetailBinding.tvLblManglik.setVisibility(View.GONE);
        educationDetailBinding.tvLblMaritalStatus.setVisibility(View.GONE);
        educationDetailBinding.tvLblMotherTongue.setVisibility(View.GONE);
        educationDetailBinding.tvLblOccupation.setVisibility(View.GONE);
        educationDetailBinding.tvLblSpeciallyAbled.setVisibility(View.GONE);
        educationDetailBinding.tvMobileNoParent.setVisibility(View.GONE);

        educationDetailBinding.edtEducation.setVisibility(View.GONE);
        educationDetailBinding.edtEmployedIn.setVisibility(View.GONE);
        educationDetailBinding.edtMonthlyIncome.setVisibility(View.GONE);
        educationDetailBinding.spinManglikStat.setVisibility(View.GONE);
        educationDetailBinding.spinMaritalStatus.setVisibility(View.GONE);
        educationDetailBinding.spinMotherTongue.setVisibility(View.GONE);
        educationDetailBinding.edtOccupation.setVisibility(View.GONE);
        educationDetailBinding.spinSpecialyAbled.setVisibility(View.GONE);
        educationDetailBinding.edtMobileParent.setVisibility(View.GONE);

        educationDetailBinding.linRelDetails.setVisibility(View.GONE);
        educationDetailBinding.linShadiBudget.setVisibility(View.GONE);
        educationDetailBinding.dynamicSeekbarShadiBudget.setVisibility(View.GONE);
        educationDetailBinding.linShowTiming.setVisibility(View.GONE);
        educationDetailBinding.rangebarCallTiming.setVisibility(View.GONE);
        educationDetailBinding.cbAnyTime.setVisibility(View.GONE);

        if (fieldToUpdate.equalsIgnoreCase("edu_detail")){
            educationDetailBinding.tvLblEducation.setVisibility(View.VISIBLE);
            educationDetailBinding.edtEducation.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("occupation"))
        {
            educationDetailBinding.tvLblOccupation.setVisibility(View.VISIBLE);
            educationDetailBinding.edtOccupation.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("emp_in"))
        {
            educationDetailBinding.tvLblEmployedIn.setVisibility(View.VISIBLE);
            educationDetailBinding.edtEmployedIn.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("income"))
        {
            educationDetailBinding.tvLblIncome.setVisibility(View.VISIBLE);
            educationDetailBinding.edtMonthlyIncome.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("religion"))
        {
            educationDetailBinding.linRelDetails.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("manglik"))
        {
            educationDetailBinding.tvLblManglik.setVisibility(View.VISIBLE);
            educationDetailBinding.spinManglikStat.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("m_status"))
        {
            educationDetailBinding.tvLblMaritalStatus.setVisibility(View.VISIBLE);
            educationDetailBinding.spinMaritalStatus.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("m_tongue"))
        {
            educationDetailBinding.tvLblMotherTongue.setVisibility(View.VISIBLE);
            educationDetailBinding.spinMotherTongue.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("physicalStatus"))
        {
            educationDetailBinding.tvLblSpeciallyAbled.setVisibility(View.VISIBLE);
            educationDetailBinding.spinSpecialyAbled.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("mobile_parent"))
        {
            educationDetailBinding.tvMobileNoParent.setVisibility(View.VISIBLE);
            educationDetailBinding.edtMobileParent.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("shadibudget"))
        {
            educationDetailBinding.linShadiBudget.setVisibility(View.VISIBLE);
            educationDetailBinding.dynamicSeekbarShadiBudget.setVisibility(View.VISIBLE);
        }else if(fieldToUpdate.equalsIgnoreCase("time_to_call"))
        {
            educationDetailBinding.linShowTiming.setVisibility(View.VISIBLE);
            educationDetailBinding.rangebarCallTiming.setVisibility(View.VISIBLE);
            educationDetailBinding.cbAnyTime.setVisibility(View.VISIBLE);
        }
    }



    private void getHighestEducationRequest() {


        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                // String paramUsername = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "education.php";
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
                Log.e("--religion --", "==" + Ressponce);

                try {
                    arrEducation = new ArrayList<beanEducation>();
                    // arrAdditionalDgree = new ArrayList<beanEducation>();

                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");


                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String edu_id = resItem.getString("edu_id");
                            String edu_name = resItem.getString("edu_name");

                            arrEducation.add(new beanEducation(edu_id, edu_name));
                            //arrAdditionalDgree.add(new beanEducation(edu_id, edu_name));

                        }

                        if (arrEducation.size() > 0) {
                            Collections.sort(arrEducation, new Comparator<beanEducation>() {
                                @Override
                                public int compare(beanEducation lhs, beanEducation rhs) {
                                    return lhs.getEducation_name().compareTo(rhs.getEducation_name());
                                }
                            });
                            educationAdapter = new EducationsAdapter(nActivity, arrEducation, educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.btnMenuClose, educationDetailBinding.edtEducation);
                            educationDetailBinding.includeSlide.rvHighestEducation.setAdapter(educationAdapter);

                        }

                        /*if (arrEducation.size() > 0) {
                            Collections.sort(arrAdditionalDgree, new Comparator<beanEducation>() {
                                @Override
                                public int compare(beanEducation lhs, beanEducation rhs) {
                                    return lhs.getEducation_name().compareTo(rhs.getEducation_name());
                                }
                            });

                            additionalDgreeAdapter = new AdditionalDgreeAdapter(nActivity, arrAdditionalDgree, SlidingDrawer, Slidingpage, btnMenuClose, edtDegree);
                            rvAdditionalDegree.setAdapter(additionalDgreeAdapter);

                        }*/


                        resIter = null;

                    }

                    responseData = null;
                    responseObj = null;

                } catch (Exception e) {

                } finally {
                    //getAdditionalDgree();
                    getOccupationsRequest();
                    getOccupations();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    public void getHighestEducation() {
        educationDetailBinding.includeSlide.edtSearchHighestEducation.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (arrEducation.size() > 0) {
                    String charcter = cs.toString();
                    String text = educationDetailBinding.includeSlide.edtSearchHighestEducation.getText().toString().toLowerCase(Locale.getDefault());
                    educationAdapter.filter(text);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });

    }



    private void getOccupationsRequest() {


        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                // String paramUsername = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "occupation.php";
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
                Log.e("--religion --", "==" + Ressponce);

                try {
                    arrOccupation = new ArrayList<beanOccupation>();

                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");


                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String edu_id = resItem.getString("ocp_id");
                            String edu_name = resItem.getString("ocp_name");

                            arrOccupation.add(new beanOccupation(edu_id, edu_name));

                        }

                        if (arrOccupation.size() > 0) {
                            Collections.sort(arrOccupation, new Comparator<beanOccupation>() {
                                @Override
                                public int compare(beanOccupation lhs, beanOccupation rhs) {
                                    return lhs.getOccupation_name().compareTo(rhs.getOccupation_name());
                                }
                            });
                            occupationAdapter = new OccupationAdapter(nActivity, arrOccupation, educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.btnMenuClose, educationDetailBinding.edtOccupation);
                            educationDetailBinding.includeSlide.rvOccupation.setAdapter(occupationAdapter);

                        }


                        resIter = null;

                    }

                    responseData = null;
                    responseObj = null;

                } catch (Exception e) {


                } finally {

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    public void getOccupations() {
        educationDetailBinding.includeSlide.edtSearchOccupation.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (arrOccupation.size() > 0) {
                    String charcter = cs.toString();
                    String text = educationDetailBinding.includeSlide.edtSearchOccupation.getText().toString().toLowerCase(Locale.getDefault());
                    occupationAdapter.filter(text);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });

    }



    public void SlidingMenu() {

        educationDetailBinding.includeSlide.rvReligion.setLayoutManager(new LinearLayoutManager(nActivity));
        educationDetailBinding.includeSlide.rvReligion.setHasFixedSize(true);
        educationDetailBinding.includeSlide.rvCaste.setLayoutManager(new LinearLayoutManager(nActivity));
        educationDetailBinding.includeSlide.rvCaste.setHasFixedSize(true);
        educationDetailBinding.includeSlide.rvSBCaste.setLayoutManager(new LinearLayoutManager(nActivity));
        educationDetailBinding.includeSlide.rvSBCaste.setHasFixedSize(true);

        educationDetailBinding.includeSlide.rvHighestEducation.setLayoutManager(new LinearLayoutManager(nActivity));
        educationDetailBinding.includeSlide.rvHighestEducation.setHasFixedSize(true);

        educationDetailBinding.includeSlide.rvOccupation.setLayoutManager(new LinearLayoutManager(nActivity));
        educationDetailBinding.includeSlide.rvOccupation.setHasFixedSize(true);

        educationDetailBinding.includeSlide.rvGeneralView.setLayoutManager(new LinearLayoutManager(nActivity));
        educationDetailBinding.includeSlide.rvGeneralView.setHasFixedSize(true);

        educationDetailBinding.slidingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GonelidingDrower();

            }
        });


    }

    public void VisibleSlidingDrower() {
        educationDetailBinding.slidingDrawer.setVisibility(View.VISIBLE);
        AnimUtils.SlideAnimation(nActivity, educationDetailBinding.slidingDrawer, R.anim.slide_right);
        //SlidingDrawer.startAnimation(AppConstants.inFromRightAnimation()) ;
        educationDetailBinding.slidingPage.setVisibility(View.VISIBLE);


    }

    public void GonelidingDrower() {
        educationDetailBinding.slidingDrawer.setVisibility(View.GONE);
        AnimUtils.SlideAnimation(nActivity, educationDetailBinding.slidingDrawer, R.anim.slide_left);
        educationDetailBinding.slidingPage.setVisibility(View.GONE);
    }




    public void getReligions() {

        try {
            educationDetailBinding.includeSlide.edtSearchReligion.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (arrReligion.size() > 0) {
                        String charcter = cs.toString();
                        String text = educationDetailBinding.includeSlide.edtSearchReligion.getText().toString().toLowerCase(Locale.getDefault());
                        religionAdapter.filter(text);
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


    public void getCaste() {

        try {
            educationDetailBinding.includeSlide.edtSearchCaste.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (arrCaste.size() > 0) {
                        String charcter = cs.toString();
                        String text = educationDetailBinding.includeSlide.edtSearchCaste.getText().toString().toLowerCase(Locale.getDefault());
                        casteAdapter.filter(text);
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
    public void getSubCaste() {
        try {
            educationDetailBinding.edtSubCaste.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (arrMotherTongue.size() > 0) {
                        String charcter = cs.toString();
                        String text = educationDetailBinding.edtSubCaste.getText().toString().toLowerCase(Locale.getDefault());
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


    private void getReligionRequest(){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                // String paramUsername = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "religion.php";
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
                //   progresDialog.dismiss();
                Log.e("--religion --", "==" + Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");

                    arrReligion = new ArrayList<beanReligion>();

                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String religionId = resItem.getString("religion_id");
                            AppConstants.ReligionId = religionId;
                            String religionName = resItem.getString("religion_name");

                            arrReligion.add(new beanReligion(religionId, religionName));

                        }

                        if (arrReligion.size() > 0) {
                            Collections.sort(arrReligion, new Comparator<beanReligion>() {
                                @Override
                                public int compare(beanReligion lhs, beanReligion rhs) {
                                    return lhs.getName().compareTo(rhs.getName());
                                }
                            });
                            religionAdapter = new ReligionAdapter(nActivity, arrReligion, educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.edtReligion);
                            educationDetailBinding.includeSlide.rvReligion.setAdapter(religionAdapter);

                        }

                        resIter = null;

                    }

                    responseData = null;
                    responseObj = null;

                } catch (Exception e) {
                    Log.e("relegion", e.getMessage());

                } finally {
                    if (NetworkConnection.hasConnection(nActivity)) {
                        getCastRequest(AppConstants.ReligionId);
                        getCaste();
                    } else {
                        AppConstants.CheckConnection(nActivity);
                    }
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }


    private void getCastRequest(String strReligion) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramUsername = params[0];


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "cast.php";//?religion_id="+paramUsername;
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair UsernamePAir = new BasicNameValuePair("religion_id", paramUsername);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(UsernamePAir);
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
                //progresDialog.dismiss();
                Log.e("--cast --", "==" + Ressponce);

                try {
                    arrCaste = new ArrayList<beanCaste>();

                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");


                    // {"status":"0","message":"No Cast Data Found"}


                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String casteId = resItem.getString("caste_id");
                            String casteName = resItem.getString("caste_name");

                            arrCaste.add(new beanCaste(casteId, casteName));

                        }

                        if (arrCaste.size() > 0) {
                            Collections.sort(arrCaste, new Comparator<beanCaste>() {
                                @Override
                                public int compare(beanCaste lhs, beanCaste rhs) {
                                    return lhs.getName().compareTo(rhs.getName());
                                }
                            });
                            casteAdapter = new CasteAdapter(nActivity, arrCaste, educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.edtCaste);
                            educationDetailBinding.includeSlide.rvCaste.setAdapter(casteAdapter);

                        }

                        resIter = null;

                    }

                    responseData = null;
                    responseObj = null;

                } catch (Exception e) {
                } finally {

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strReligion);
    }

    private void getSubCasteRequest() {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                // String paramUsername = params[0];


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "sub-caste.php";
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
                Log.e("--subcaste --", "==" + Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");

                    arrSubCast = new ArrayList<SubCast>();
                    Log.e("subcastarrayup", arrSubCast.size() + "");
                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String mtongue_id = resItem.getString("sub_caste_id");
                            String mtongue_name = resItem.getString("sub_caste_name");

                            arrSubCast.add(new SubCast(mtongue_id, mtongue_name));
                            Log.e("subcastarraystore", arrSubCast.size() + "");
                        }

                        if (arrSubCast.size() > 0) {
                            Collections.sort(arrSubCast, new Comparator<SubCast>() {
                                @Override
                                public int compare(SubCast lhs, SubCast rhs) {
                                    return lhs.getSB_name().compareTo(rhs.getSB_name());
                                }
                            });
                            Log.e("subcastarraydown", arrMotherTongue.size() + "");
                            subCastAdapter = new SubCastAdapter(nActivity, arrSubCast, educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.edtSubCaste);
                            educationDetailBinding.includeSlide.rvSBCaste.setAdapter(subCastAdapter);

                        }

                        resIter = null;

                    }

                    responseData = null;
                    responseObj = null;

                } catch (Exception e) {

                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b) {
            callTime = "Any Time";
            otherDetailsBinding.rangebarCallTiming.setEnabled(false);
        }else
        {
            otherDetailsBinding.rangebarCallTiming.setEnabled(true);
        }
    }
    String callTime="0",callStartTime="0",callEndTime="0";
    String shadiMinBgt="0";
    public void VisibleSlidingDrowerOther() {
        otherDetailsBinding.slidingDrawer.setVisibility(View.VISIBLE);
        AnimUtils.SlideAnimation(nActivity, otherDetailsBinding.slidingDrawer, R.anim.slide_right);
        //SlidingDrawer.startAnimation(AppConstants.inFromRightAnimation()) ;
        otherDetailsBinding.slidingPage.setVisibility(View.VISIBLE);


    }
    public void SlidingMenuOther() {






        otherDetailsBinding.includeSlide.rvMaritalStatus.setLayoutManager(new LinearLayoutManager(nActivity));
        otherDetailsBinding.includeSlide.rvMaritalStatus.setHasFixedSize(true);

        otherDetailsBinding.includeSlide.rvMotherTongue.setLayoutManager(new LinearLayoutManager(nActivity));
        otherDetailsBinding.includeSlide.rvMotherTongue.setHasFixedSize(true);

        otherDetailsBinding.includeSlide.rvGeneralView.setLayoutManager(new LinearLayoutManager(nActivity));
        otherDetailsBinding.includeSlide.rvGeneralView.setHasFixedSize(true);

        otherDetailsBinding.dynamicSeekbarShadiBudget.setSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                shadiMinBgt = "" + i;
                if(i==16){
                    otherDetailsBinding.dynamicSeekbarShadiBudget.setInfoText((i-1) + " lacs above approx", i);
                    otherDetailsBinding.tvShowBudget.setText("(" + (i-1) + " lacs above approx)");
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
                GonelidingDrowerOther();
            }
        });

        otherDetailsBinding.slidingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GonelidingDrowerOther();

            }
        });


    }
    public void GonelidingDrowerOther() {
        otherDetailsBinding.slidingDrawer.setVisibility(View.GONE);
        AnimUtils.SlideAnimation(nActivity, otherDetailsBinding.slidingDrawer, R.anim.slide_left);
        otherDetailsBinding.slidingPage.setVisibility(View.GONE);
    }

    private void getMotherToungeRequest() {
        progresDialog = showProgress(nActivity);
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
                            motherTongueAdapter = new MotherTongueAdapter(nActivity, arrMotherTongue, otherDetailsBinding.slidingDrawer, otherDetailsBinding.slidingPage, otherDetailsBinding.spinMotherTongue);
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

    Dialog progresDialog;
    private void getUpdateSteps(String fieldToUpdate,String matri_id,String value) {
        progresDialog = showProgress(DialogUdateFieldsActivity.this);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                HttpClient httpClient = new DefaultHttpClient();

                // String URL= AppConstants.MAIN_URL +"edit_step1.php";
                String URL = AppConstants.MAIN_URL + "update_single_field.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);


                BasicNameValuePair FieldToUpdatePAir = new BasicNameValuePair("field", fieldToUpdate);
                BasicNameValuePair MatriIdPAir = new BasicNameValuePair("matri_id", matri_id);
                BasicNameValuePair ValuePAir = new BasicNameValuePair("value", value);
                //for religion details
                BasicNameValuePair CasteIdPAir = new BasicNameValuePair("caste", "");
                BasicNameValuePair SubcasteIdPAir = new BasicNameValuePair("subcaste", "");

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(FieldToUpdatePAir);
                nameValuePairList.add(MatriIdPAir);
                nameValuePairList.add(ValuePAir);

                nameValuePairList.add(CasteIdPAir);
                nameValuePairList.add(SubcasteIdPAir);


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
                Log.e("--Update Field --", "==" + Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);
//                    JSONObject responseData = responseObj.getJSONObject("responseData");

                    String status = responseObj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        String message = responseObj.getString("message");
                        Toast.makeText(DialogUdateFieldsActivity.this, "" + message, Toast.LENGTH_LONG).show();
                       finish();
                    } else {
                        String msgError = responseObj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(DialogUdateFieldsActivity.this);
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
        sendPostReqAsyncTask.execute();
    }

    private void getUpdateReligionDet(String fieldToUpdate,String matri_id) {
        progresDialog = showProgress(DialogUdateFieldsActivity.this);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                HttpClient httpClient = new DefaultHttpClient();

                // String URL= AppConstants.MAIN_URL +"edit_step1.php";
                String URL = AppConstants.MAIN_URL + "update_single_field.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);


                BasicNameValuePair FieldToUpdatePAir = new BasicNameValuePair("field", fieldToUpdate);
                BasicNameValuePair MatriIdPAir = new BasicNameValuePair("matri_id", matri_id);
                BasicNameValuePair ValuePAir = new BasicNameValuePair("value", AppConstants.ReligionId);
                BasicNameValuePair CasteIdPAir = new BasicNameValuePair("caste", AppConstants.CasteId);
                BasicNameValuePair SubcasteIdPAir = new BasicNameValuePair("subcaste", AppConstants.SubCasteID);

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(FieldToUpdatePAir);
                nameValuePairList.add(MatriIdPAir);
                nameValuePairList.add(ValuePAir); //for religion
                nameValuePairList.add(CasteIdPAir);
                nameValuePairList.add(SubcasteIdPAir);


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
                Log.e("--Update Field --", "==" + Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);
//                    JSONObject responseData = responseObj.getJSONObject("responseData");

                    String status = responseObj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        String message = responseObj.getString("message");
                        Toast.makeText(DialogUdateFieldsActivity.this, "" + message, Toast.LENGTH_LONG).show();
                       finish();
                    } else {
                        String msgError = responseObj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(DialogUdateFieldsActivity.this);
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
        sendPostReqAsyncTask.execute();
    }


    private void getSkipUpdate(String matri_id) {
        progresDialog = showProgress(DialogUdateFieldsActivity.this);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                HttpClient httpClient = new DefaultHttpClient();

                // String URL= AppConstants.MAIN_URL +"edit_step1.php";
                String URL = AppConstants.MAIN_URL + "skip_update.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);



                BasicNameValuePair MatriIdPAir = new BasicNameValuePair("matri_id", matri_id);


                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(MatriIdPAir);


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
                Log.e("--Update Field --", "==" + Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);
//                    JSONObject responseData = responseObj.getJSONObject("responseData");

                    String status = responseObj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        String message = responseObj.getString("message");
                        //Toast.makeText(DialogUdateFieldsActivity.this, "" + message, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String msgError = responseObj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(DialogUdateFieldsActivity.this);
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
        sendPostReqAsyncTask.execute();
    }
}
