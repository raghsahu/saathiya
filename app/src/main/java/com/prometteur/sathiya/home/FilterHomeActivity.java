package com.prometteur.sathiya.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.prometteur.sathiya.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.appyvet.materialrangebar.RangeBar;
import com.prometteur.sathiya.ChangePasswordActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.SignUpStep1Activity;
import com.prometteur.sathiya.adapters.CasteAdapter;
import com.prometteur.sathiya.adapters.CityAdapter;
import com.prometteur.sathiya.adapters.EducationsAdapter;
import com.prometteur.sathiya.adapters.GeneralAdapter;
import com.prometteur.sathiya.adapters.GeneralAdapter2;
import com.prometteur.sathiya.adapters.MotherTongueAdapter;
import com.prometteur.sathiya.adapters.ReligionAdapter;
import com.prometteur.sathiya.adapters.StateAdapter;
import com.prometteur.sathiya.adapters.SubCastAdapter;
import com.prometteur.sathiya.adapters.beanMotherTongue;
import com.prometteur.sathiya.beans.SubCast;
import com.prometteur.sathiya.beans.beanCaste;
import com.prometteur.sathiya.beans.beanCity;
import com.prometteur.sathiya.beans.beanEducation;
import com.prometteur.sathiya.beans.beanGeneralData;
import com.prometteur.sathiya.beans.beanOccupation;
import com.prometteur.sathiya.beans.beanReligion;
import com.prometteur.sathiya.beans.beanSaveSearch;
import com.prometteur.sathiya.beans.beanState;
import com.prometteur.sathiya.databinding.ActivityFilterBinding;
import com.prometteur.sathiya.databinding.ActivitySettingBinding;
import com.prometteur.sathiya.dialog.DialogReasonActivity;
import com.prometteur.sathiya.language.LanguateActivity;
import com.prometteur.sathiya.utills.AnimUtils;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.AppMethods;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.prometteur.sathiya.utills.RecyclerTouchListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppConstants.setToastStrPinkBg;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

//import android.support.v7.app.BaseActivity;

public class FilterHomeActivity extends BaseActivity {

    BaseActivity nActivity= FilterHomeActivity.this;
ActivityFilterBinding filterBinding;

    private ArrayList<beanSaveSearch> arrSaveSearchResultList;


    SharedPreferences prefUpdate;
    String matri_id="", AgeM = "", AgeF = "",HeightM="",HeightF="",IncomeF="",IncomeT="";

    ArrayList<beanReligion> arrReligion = new ArrayList<beanReligion>();
    ReligionAdapter religionAdapter = null;

    ArrayList<beanCaste> arrCaste = new ArrayList<beanCaste>();
    CasteAdapter casteAdapter = null;

    ArrayList<SubCast> arrSubCaste = new ArrayList<SubCast>();
    ArrayList<beanMotherTongue> arrMotherTongue = new ArrayList<beanMotherTongue>();

    SubCastAdapter subCastAdapter = null;
    MotherTongueAdapter motherTongueAdapter = null;


    ArrayList<beanEducation> arrEducation = new ArrayList<beanEducation>();
    EducationsAdapter educationAdapter = null;

    ArrayList<beanState> arrState = new ArrayList<beanState>();
    StateAdapter stateAdapter = null;

    ArrayList<beanCity> arrCity = new ArrayList<beanCity>();
    CityAdapter cityAdapter = null;
String searchId="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterBinding=ActivityFilterBinding.inflate(getLayoutInflater());

        setContentView(filterBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", "");
        strLang = prefUpdate.getString("selLang", "");
        SlidingMenu();
        filterBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        filterBinding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkConnection.hasConnection(nActivity)){
                    getSavedSearchResultList(matri_id);
                }else {
                    AppConstants.CheckConnection(nActivity);
                }
            }
        });

        if (NetworkConnection.hasConnection(nActivity)){
            getSavedSearchResultList(matri_id);

        }else
        {
            AppConstants.CheckConnection(nActivity);
        }

        filterBinding.cbAbove50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    filterBinding.rangebarIncome.setEnabled(false);
                    IncomeF="51000";
                    IncomeT="51000";
                }else
                {
                    filterBinding.rangebarIncome.setEnabled(true);
                    IncomeF="";
                    IncomeT="";
                }
            }
        });

        //clear search
        filterBinding.btnClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!searchId.equalsIgnoreCase("0") && !searchId.isEmpty()) {
                    getDeleteRequest(searchId);
                }
                filterBinding.rangebarAge.setRangePinsByValue(18,60);
                filterBinding.rangebarHeight.setRangePinsByValue(1,48);
                filterBinding.rangebarIncome.setRangePinsByValue(0,50);
                AgeM = ""; AgeF = "";HeightM="";HeightF="";IncomeF="";IncomeT="";
                filterBinding.tvCaste.setText("");
                filterBinding.tvSubCaste.setText("");
                filterBinding.tvMotherTongue.setText("");
                filterBinding.tvCity.setText("");
                filterBinding.tvState.setText("");
                filterBinding.tvManglik.setText("");
                filterBinding.tvDiet.setText("");
                filterBinding.edtEducation.setText("");

                filterBinding.tvReligion.setText("");

            }
        });

        // Sets the display values of the indices
        filterBinding.tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                filterBinding.tvState.setError(null);
                filterBinding.includeSlide.edtSearchState.setText("");
                filterBinding.tvState.setFocusable(true);
                filterBinding.includeSlide.linState.setVisibility(View.VISIBLE);
                filterBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
//                filterBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                filterBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                filterBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                filterBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                filterBinding.includeSlide.linReligion.setVisibility(View.GONE);
                filterBinding.includeSlide.linMotherTongue.setVisibility(View.GONE);
                filterBinding.includeSlide.linCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.linCountry.setVisibility(View.GONE);
                filterBinding.includeSlide.linCity.setVisibility(View.GONE);
                filterBinding.includeSlide.linAnnualIncome.setVisibility(View.GONE);
                filterBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);
                getStateRequest("");
                getStates();
            }
        });

        filterBinding.tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                filterBinding.tvCity.setError(null);
                filterBinding.includeSlide.edtSearchCity.setText("");
                filterBinding.includeSlide.linCity.setVisibility(View.VISIBLE);
                filterBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
//                filterBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                filterBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                filterBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                filterBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                filterBinding.includeSlide.linReligion.setVisibility(View.GONE);
                filterBinding.includeSlide.linMotherTongue.setVisibility(View.GONE);
                filterBinding.includeSlide.linCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.linCountry.setVisibility(View.GONE);
                filterBinding.includeSlide.linState.setVisibility(View.GONE);
                filterBinding.includeSlide.linAnnualIncome.setVisibility(View.GONE);
                filterBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);

            }
        });


        filterBinding.includeSlide.rvState.addOnItemTouchListener(new RecyclerTouchListener(nActivity, filterBinding.includeSlide.rvState, new SignUpStep1Activity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                InputMethodManager imm = (InputMethodManager) nActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(filterBinding.includeSlide.rvState.getWindowToken(), 0);

                beanState arrCo = arrState.get(position);
                AppConstants.StateName = arrCo.getState_name();
                AppConstants.StateId = arrCo.getState_id();

                filterBinding.tvState.setText(AppConstants.StateName);

                AppConstants.CityName = "";
                AppConstants.CityId = "";
                filterBinding.tvCity.setText("");

                ArrayList<beanCity> arrCity = null;
                CityAdapter cityAdapter = null;
                GonelidingDrower();
                getCityRequest(AppConstants.CountryId, AppConstants.StateId);
                getCity();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        filterBinding.includeSlide.rvCity.addOnItemTouchListener(new RecyclerTouchListener(nActivity, filterBinding.includeSlide.rvCity, new SignUpStep1Activity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                InputMethodManager imm = (InputMethodManager) nActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(filterBinding.includeSlide.rvCity.getWindowToken(), 0);

                if (arrCity != null && arrCity.size() != 0) {
                    beanCity arrCo = arrCity.get(position);
                    AppConstants.CityName = arrCo.getCity_name();
                    AppConstants.CityId = arrCo.getCity_id();
                } else {
                    setToastStr(nActivity, getString(R.string.select_state));
                }
                filterBinding.tvCity.setText(AppConstants.CityName);

                GonelidingDrower();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        filterBinding.tvMotherTongue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();

                getMotherTongue();
                getMotherToungeRequest();

                filterBinding.tvMotherTongue.setError(null);
                filterBinding.includeSlide.edtSearchMotherTongue.setText("");

                filterBinding.includeSlide.linMotherTongue.setVisibility(View.VISIBLE);
                filterBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
//                filterBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                filterBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                filterBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                filterBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                filterBinding.includeSlide.linReligion.setVisibility(View.GONE);
                filterBinding.includeSlide.linCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.linCountry.setVisibility(View.GONE);
                filterBinding.includeSlide.linState.setVisibility(View.GONE);
                filterBinding.includeSlide.linCity.setVisibility(View.GONE);
                filterBinding.includeSlide.linAnnualIncome.setVisibility(View.GONE);
                filterBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);

            }
        });

        filterBinding.tvManglik.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                VisibleSlidingDrower();
                filterBinding.tvManglik.setError(null);

                filterBinding.includeSlide.linGeneralView.setVisibility(View.VISIBLE);
                filterBinding.includeSlide.rvGeneralView.setAdapter(null);
                filterBinding.includeSlide.tvLabelFor.setHint(R.string.manglik_status);
                filterBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
//                filterBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                filterBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                filterBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                filterBinding.includeSlide.linReligion.setVisibility(View.GONE);
                filterBinding.includeSlide.linMotherTongue.setVisibility(View.GONE);
                filterBinding.includeSlide.linCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.linCountry.setVisibility(View.GONE);
                filterBinding.includeSlide.linState.setVisibility(View.GONE);
                filterBinding.includeSlide.linCity.setVisibility(View.GONE);
                filterBinding.includeSlide.linAnnualIncome.setVisibility(View.GONE);
                filterBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);
                GeneralAdapter generalAdapter= new GeneralAdapter(nActivity, getResources().getStringArray(R.array.arr_manglik),filterBinding.slidingDrawer,filterBinding.slidingPage,filterBinding.btnMenuClose,filterBinding.tvManglik);
                filterBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);


            }
        });

        filterBinding.tvReligion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                filterBinding.tvReligion.setError(null);
                filterBinding.includeSlide.edtSearchReligion.setText("");
                filterBinding.includeSlide.linReligion.setVisibility(View.VISIBLE);
                filterBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
//                filterBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                filterBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                filterBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                filterBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                filterBinding.includeSlide.linMotherTongue.setVisibility(View.GONE);
                filterBinding.includeSlide.linCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.linCountry.setVisibility(View.GONE);
                filterBinding.includeSlide.linState.setVisibility(View.GONE);
                filterBinding.includeSlide.linCity.setVisibility(View.GONE);
                filterBinding.includeSlide.linAnnualIncome.setVisibility(View.GONE);
                filterBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);
                if (NetworkConnection.hasConnection(nActivity)) {
                    getReligionRequest();
                    getReligions();
                } else {
                    AppConstants.CheckConnection(nActivity);
                }
            }
        });

        filterBinding.tvCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (!filterBinding.tvReligion.getText().toString().isEmpty()) {
                    VisibleSlidingDrower();
                    Log.e("religionid", AppConstants.ReligionId);
                    getCastRequest(AppConstants.ReligionId);
                    getCaste();
                /*} else {
                    Toast.makeText(nActivity, "Please select religion First", Toast.LENGTH_SHORT).show();
                }*/
                filterBinding.tvCaste.setError(null);
                filterBinding.includeSlide.edtSearchCaste.setText("");
                if (arrCaste.size() > 0) {

                } else {
                    filterBinding.includeSlide.rvCaste.setAdapter(null);
                }

                filterBinding.includeSlide.linCaste.setVisibility(View.VISIBLE);
                filterBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
//                filterBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                filterBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                filterBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                filterBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                filterBinding.includeSlide.linReligion.setVisibility(View.GONE);
                filterBinding.includeSlide.linMotherTongue.setVisibility(View.GONE);
                filterBinding.includeSlide.linCountry.setVisibility(View.GONE);
                filterBinding.includeSlide.linState.setVisibility(View.GONE);
                filterBinding.includeSlide.linCity.setVisibility(View.GONE);
                filterBinding.includeSlide.linAnnualIncome.setVisibility(View.GONE);
                filterBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);
            }
        });


        filterBinding.tvSubCaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisibleSlidingDrower();
                filterBinding.includeSlide.linSBCaste.setVisibility(View.VISIBLE);
                filterBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
//                filterBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                filterBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                filterBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                filterBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                filterBinding.includeSlide.linReligion.setVisibility(View.GONE);
                filterBinding.includeSlide.linMotherTongue.setVisibility(View.GONE);
                filterBinding.includeSlide.linCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.linCountry.setVisibility(View.GONE);
                filterBinding.includeSlide.linState.setVisibility(View.GONE);
                filterBinding.includeSlide.linCity.setVisibility(View.GONE);
                filterBinding.includeSlide.linAnnualIncome.setVisibility(View.GONE);
                filterBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);
                getSubCasteRequest();
                getSubCaste();
            }
        });

        filterBinding.includeSlide.rvReligion.addOnItemTouchListener(new RecyclerTouchListener(nActivity, filterBinding.includeSlide.rvReligion, new SignUpStep1Activity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                InputMethodManager imm = (InputMethodManager) nActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(filterBinding.includeSlide.rvReligion.getWindowToken(), 0);

                beanReligion arrCo = arrReligion.get(position);
                AppConstants.ReligionId = arrCo.getReligion_id();
                AppConstants.ReligionName = arrCo.getName();

                filterBinding.tvReligion.setText(AppConstants.ReligionName);

                AppConstants.CasteId = "";
                AppConstants.StateName = "";

                filterBinding.tvCaste.setText("");

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

        filterBinding.edtEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                filterBinding.edtEducation.setError(null);
                filterBinding.includeSlide.edtSearchHighestEducation.setText("");
                filterBinding.includeSlide.linHighestEducation.setVisibility(View.VISIBLE);
//                filterBinding.includeSlide.linAdditionalDegree.setVisibility(View.GONE);
                filterBinding.includeSlide.linSBCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.btnConfirm.setVisibility(View.GONE);
                filterBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                filterBinding.includeSlide.linGeneralView.setVisibility(View.GONE);
                filterBinding.includeSlide.linReligion.setVisibility(View.GONE);
                filterBinding.includeSlide.linMotherTongue.setVisibility(View.GONE);
                filterBinding.includeSlide.linCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.linCountry.setVisibility(View.GONE);
                filterBinding.includeSlide.linState.setVisibility(View.GONE);
                filterBinding.includeSlide.linCity.setVisibility(View.GONE);
                filterBinding.includeSlide.linAnnualIncome.setVisibility(View.GONE);
                filterBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);
                if (NetworkConnection.hasConnection(nActivity)) {
                    getHighestEducationRequest();
                    getHighestEducation();
                } else {
                    AppConstants.CheckConnection(nActivity);
                }

            }
        });


        filterBinding.tvDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                filterBinding.tvDiet.setError(null);
                filterBinding.includeSlide.tvLabelFor.setHint(R.string.diet);
                filterBinding.includeSlide.linHighestEducation.setVisibility(View.GONE);
                filterBinding.includeSlide.linOccupation.setVisibility(View.GONE);
                filterBinding.includeSlide.linGeneralView.setVisibility(View.VISIBLE);
                filterBinding.includeSlide.rvGeneralView.setAdapter(null);
                filterBinding.includeSlide.linReligion.setVisibility(View.GONE);
                filterBinding.includeSlide.linCaste.setVisibility(View.GONE);
                filterBinding.includeSlide.linMotherTongue.setVisibility(View.GONE);
                filterBinding.includeSlide.linCountry.setVisibility(View.GONE);
                filterBinding.includeSlide.linState.setVisibility(View.GONE);
                filterBinding.includeSlide.linCity.setVisibility(View.GONE);
                //filterBinding.includeSlide.linAnnualIncome.setVisibility(View.GONE);
                filterBinding.includeSlide.linMaritalStatus.setVisibility(View.GONE);
                //filterBinding.includeSlide.linPhysicalStatus.setVisibility(View.GONE);
               // filterBinding.includeSlide.linStar.setVisibility(View.GONE);
                //filterBinding.includeSlide.linManglik.setVisibility(View.GONE);
                filterBinding.includeSlide.btnConfirm.setVisibility(View.VISIBLE);
                ArrayList<beanGeneralData> arrGeneralData = new ArrayList<beanGeneralData>();
                Resources res = getResources();
                String[] arr_diet = res.getStringArray(R.array.arr_diet);

                for (int i = 0; i < arr_diet.length; i++) {
                    arrGeneralData.add(new beanGeneralData("" + i, arr_diet[i], false));
                }

                if (arrGeneralData.size() > 0) {
                    filterBinding.includeSlide.rvGeneralView.setAdapter(null);
                    GeneralAdapter2 generalAdapter = new GeneralAdapter2(nActivity, "eating_Habits", arrGeneralData, filterBinding.slidingDrawer, filterBinding.slidingPage, filterBinding.btnMenuClose, filterBinding.tvDiet, filterBinding.includeSlide.btnConfirm);
                    filterBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);

                } else {
                    filterBinding.includeSlide.rvGeneralView.setAdapter(null);
                    GeneralAdapter generalAdapter = new GeneralAdapter(nActivity, getResources().getStringArray(R.array.arr_diet), filterBinding.slidingDrawer,filterBinding.slidingPage, filterBinding.btnMenuClose, filterBinding.tvDiet);
                    filterBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);
                }
               /* GeneralAdapter generalAdapter= new GeneralAdapter(nActivity, getResources().getStringArray(R.array.arr_diet_2),SlidingDrawer,Slidingpage,btnMenuClose,edtDiet);
                rvGeneralView.setAdapter(generalAdapter);*/

            }
        });


        filterBinding.btnSearch.setOnClickListener(new View.OnClickListener() { //save and search
            @Override
            public void onClick(View v) {

                //AgeM = edtAgeM.getText().toString().trim();
               // AgeF = edtAgeF.getText().toString().trim();
//                final String HeightM = edtHeightM.getText().toString().trim();
//                final String HeightF = edtHeightF.getText().toString().trim();
//                final String MaritalStatus = edtMaritalStatus.getText().toString().trim();
//                final String PhysicalStatus = edtPhysicalStatus.getText().toString().trim();
                final String ReligionId = AppConstants.ReligionId;
                final String CasteId = AppConstants.CasteId;
                final String CountryId = AppConstants.CountryId;
                final String StateId = AppConstants.StateId;
                final String CityId = AppConstants.CityId;
                final String HighestEducationId = AppConstants.EducationId;
                final String MothertoungueID = AppConstants.MotherTongueId;

                final String Diet = filterBinding.tvDiet.getText().toString().trim();

                final String Manglik = filterBinding.tvManglik.getText().toString().trim();

String fianlIncome="";
if(!IncomeF.isEmpty() && !IncomeT.isEmpty())
{
    fianlIncome=IncomeF+"-"+IncomeT;
}
                                saveSearchResult(AgeM, AgeF, HeightM, HeightF, ReligionId,
                                        CasteId, CountryId, StateId, CityId, HighestEducationId, fianlIncome,/*,*//*Star,*/
                                        matri_id, MothertoungueID, Diet,Manglik);

            }
        });

        filterBinding.rangebarAge.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                filterBinding.tvAge.setText(getString(R.string.age)+" ("+leftPinValue+" - "+rightPinValue+""+" yr)");
                AgeM=leftPinValue;
                AgeF=rightPinValue;
            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {
                Log.d("RangeBar", "Touch ended");
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {
                Log.d("RangeBar", "Touch started");
            }
        });
        filterBinding.rangebarHeight.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {

                int inches = (int) (36+Float.parseFloat(leftPinValue)); //24 is added since we start from 2ft(12")
                int inchesRight = (int) (36+Float.parseFloat(rightPinValue)); //24 is added since we start from 2ft(12")
                HeightM=String.valueOf(inches/12) +"."+String.valueOf(inches%12);
                HeightF=String.valueOf(inchesRight/12) +"."+String.valueOf(inchesRight%12);
                filterBinding.tvFeetInches.setText(" ("+inches/12+"ft "+inches%12+"in - "+inchesRight/12+"ft "+inchesRight%12+"in)");
                Log.e("Height inches",HeightM+" - "+HeightF);
            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {
                Log.d("RangeBar", "Touch ended");
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {
                Log.d("RangeBar", "Touch started");
            }
        });
        filterBinding.rangebarIncome.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                IncomeF=""+(Integer.parseInt(leftPinValue)*1000);
                IncomeT=""+(Integer.parseInt(rightPinValue)*1000);
                filterBinding.tvPerMonth.setText(" (₹"+IncomeF+"-₹"+IncomeT+" Per Month)");
            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {
                Log.d("RangeBar", "Touch ended");
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {
                Log.d("RangeBar", "Touch started");
            }
        });

    }

    public void VisibleSlidingDrower() {
        filterBinding.slidingDrawer.setVisibility(View.VISIBLE);
        AnimUtils.SlideAnimation(nActivity, filterBinding.slidingDrawer, R.anim.slide_right);
        filterBinding.slidingPage.setVisibility(View.VISIBLE);
    }

    public void GonelidingDrower() {
        filterBinding.slidingDrawer.setVisibility(View.GONE);
        AnimUtils.SlideAnimation(nActivity, filterBinding.slidingDrawer, R.anim.slide_left);
        filterBinding.slidingPage.setVisibility(View.GONE);
    }

    public void SlidingMenu() {

        filterBinding.includeSlide.rvReligion.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvReligion.setHasFixedSize(true);
        filterBinding.includeSlide.rvCaste.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvCaste.setHasFixedSize(true);
        filterBinding.includeSlide.rvSBCaste.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvSBCaste.setHasFixedSize(true);
        filterBinding.includeSlide.rvCountry.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvCountry.setHasFixedSize(true);
        filterBinding.includeSlide.rvState.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvState.setHasFixedSize(true);
        filterBinding.includeSlide.rvCity.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvCity.setHasFixedSize(true);
        filterBinding.includeSlide.rvHighestEducation.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvHighestEducation.setHasFixedSize(true);
        filterBinding.includeSlide.rvOccupation.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvOccupation.setHasFixedSize(true);
        filterBinding.includeSlide.rvGeneralView.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvGeneralView.setHasFixedSize(true);

        filterBinding.includeSlide.rvAnnualIncome.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvAnnualIncome.setHasFixedSize(true);
        filterBinding.includeSlide.rvMaritalStatus.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvMaritalStatus.setHasFixedSize(true);
        filterBinding.includeSlide.rvPhysicalStatus.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvPhysicalStatus.setHasFixedSize(true);
   /*     rvStar.setLayoutManager(new LinearLayoutManager(nActivity));
        rvStar.setHasFixedSize(true);*/
        filterBinding.includeSlide.rvManglik.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvManglik.setHasFixedSize(true);
        filterBinding.includeSlide.rvMotherTongue.setLayoutManager(new LinearLayoutManager(nActivity));
        filterBinding.includeSlide.rvMotherTongue.setHasFixedSize(true);

        filterBinding.btnMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GonelidingDrower();
            }
        });

        filterBinding.slidingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GonelidingDrower();

            }
        });
    }
  

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppConstants.CountryId = "";
        AppConstants.StateId = "";
        AppConstants.CityId = "";
        AppConstants.CountryName = "";
        AppConstants.StateName = "";
        AppConstants.CityName = "";
        AppConstants.ReligionId = "";
        AppConstants.CasteId = "";
        AppConstants.EducationId = "";
        AppConstants.OccupationID = "";
        AppConstants.ReligionName = "";
        AppConstants.CasteName = "";
        AppConstants.EducationName = "";
        AppConstants.OccupationName = "";
        AppConstants.MotherTongueId = "";

        ArrayList<beanReligion> arrReligion = null;
        //ReligionAdapter religionAdapter=null;


        ArrayList<beanCaste> arrCaste = null;
        //CasteAdapter casteAdapter=null;


        ArrayList<beanState> arrState = null;
        //StateAdapter stateAdapter=null;

        ArrayList<beanCity> arrCity = null;
        //CityAdapter cityAdapter=null;

        ArrayList<beanEducation> arrEducation = null;
        //EducationsAdapter educationAdapter=null;

        ArrayList<beanOccupation> arrOccupation = null;
        //OccupationAdapter occupationAdapter=null;
    }

    private void getSavedSearchResultList(String strMatriId)
    {
        Dialog progressBar1=showProgress(FilterHomeActivity.this);
        progressBar1.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramsMatriId = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"saved_search.php";
                Log.e("seved_search", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", paramsMatriId);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(languagePAir);

                try
                {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    Log.e("Parametters Array=", "== "+(nameValuePairList.toString().trim().replaceAll(",","&")));
                    try
                    {
                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        InputStream inputStream = httpResponse.getEntity().getContent();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String bufferedStrChunk = null;
                        while((bufferedStrChunk = bufferedReader.readLine()) != null)
                        {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("Firstption caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe)
                    {
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
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);

                Log.e("seved_search", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);

                    String status=obj.getString("status");

                    if (status.equalsIgnoreCase("1"))
                    {
                        arrSaveSearchResultList= new ArrayList<beanSaveSearch>();
                        JSONObject responseData = obj.getJSONObject("responseData");

                        if (responseData.has("1"))
                        {
                            Iterator<String> resIter = responseData.keys();

                           /* while (resIter.hasNext())
                            {*/
                               // String key = resIter.next();
                                JSONObject resItem = responseData.getJSONObject("1");

                                String ss_id=resItem.getString("ss_id");
                                searchId=ss_id;
                                String ss_name=resItem.getString("ss_name");
                                String matri_id=resItem.getString("matri_id");
                                String fromage=resItem.getString("fromage");
                                String toage=resItem.getString("toage");
                                String from_height=resItem.getString("from_height");
                                String to_height=resItem.getString("to_height");
                                String marital_status=resItem.getString("marital_status");
                                String religion=resItem.getString("religion");
                                String caste=resItem.getString("caste");
                                String country=resItem.getString("country");
                                String state=resItem.getString("state");
                                String city=resItem.getString("city");
                                String education=resItem.getString("education");
                                String occupation=resItem.getString("occupation");
                                String annual_income=resItem.getString("annual_income");
                                String star=resItem.getString("star");
                                String manglik=resItem.getString("manglik");
                                String saveDate = resItem.getString("save_date");


                                arrSaveSearchResultList.add(new beanSaveSearch(ss_id,fromage,toage,from_height,to_height,marital_status,"",religion,
                                        caste,country,state,city,education,occupation,
                                        annual_income,star,manglik,ss_name,saveDate));

                               /* filterBinding.tvCaste.setText(caste);
                                filterBinding.tvSubCaste.setText(caste);
                                filterBinding.tvMotherTongue.setText(caste);
                                filterBinding.tvCity.setText(city);
                                filterBinding.tvState.setText(state);
                                filterBinding.tvManglik.setText(manglik);
                                filterBinding.tvDiet.setText(manglik);
                                filterBinding.edtEducation.setText(education);
                                filterBinding.tvReligion.setText(religion);
*/
                                if(fromage!=null && !fromage.isEmpty()) {
                                    filterBinding.rangebarAge.setRangePinsByValue(Float.parseFloat(fromage), Float.parseFloat(toage));
                                }
                                if(from_height!=null && !from_height.isEmpty()) {
                                    String fHeight="",tHeight="";
                                    if(from_height.contains("."))
                                    {
                                        fHeight=""+((Integer.parseInt(from_height.split("\\.")[0])*12)+Integer.parseInt(from_height.split("\\.")[1])-36);
                                    }else
                                    {
                                        fHeight=""+((Integer.parseInt(from_height)*12)-36);
                                    }
                                    if(from_height.contains("."))
                                    {
                                        tHeight=""+((Integer.parseInt(to_height.split("\\.")[0])*12)+Integer.parseInt(to_height.split("\\.")[1])-36);
                                    }else{
                                        tHeight=""+((Integer.parseInt(to_height)*12)-36);
                                    }

                                    filterBinding.rangebarHeight.setRangePinsByValue(Float.parseFloat(fHeight), Float.parseFloat(tHeight));
                                }
                                if(annual_income!=null && !annual_income.isEmpty()) {
                                    if(annual_income.split("-")[0].replace("000","").equalsIgnoreCase("51")){
                                        filterBinding.rangebarIncome.setEnabled(false);
                                        filterBinding.cbAbove50.setChecked(true);
                                    }else {
                                        filterBinding.rangebarIncome.setEnabled(true);
                                        filterBinding.cbAbove50.setChecked(false);
                                        filterBinding.rangebarIncome.setRangePinsByValue(Float.parseFloat(annual_income.split("-")[0].replace("000", "")), Float.parseFloat(annual_income.split("-")[1].replace("000", "")));
                                    }
                                }


                          //  }


                        }



                    }else {

                      /*  recyclerUser.setVisibility(View.GONE);
                        textEmptyView.setVisibility(View.VISIBLE);*/
                      /*  String msgError=obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage(""+msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();*/
                    }
                    progressBar1.dismiss();
                    filterBinding.refresh.setRefreshing(false);
                } catch (Exception t)
                {
                    progressBar1.dismiss();
                    filterBinding.refresh.setRefreshing(false);
                }
                progressBar1.dismiss();
                filterBinding.refresh.setRefreshing(false);

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strMatriId);
    }


    public void getReligions() {

        try {
            filterBinding.includeSlide.edtSearchReligion.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (arrReligion.size() > 0) {
                        String charcter = cs.toString();
                        String text = filterBinding.includeSlide.edtSearchReligion.getText().toString().toLowerCase(Locale.getDefault());
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
            filterBinding.includeSlide.edtSearchCaste.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (arrCaste.size() > 0) {
                        String charcter = cs.toString();
                        String text = filterBinding.includeSlide.edtSearchCaste.getText().toString().toLowerCase(Locale.getDefault());
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


    private void getReligionRequest() {
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
                            religionAdapter = new ReligionAdapter(nActivity, arrReligion, filterBinding.slidingDrawer, filterBinding.slidingPage, filterBinding.tvReligion);
                            filterBinding.includeSlide.rvReligion.setAdapter(religionAdapter);

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
                            casteAdapter = new CasteAdapter(nActivity, arrCaste, filterBinding.slidingDrawer, filterBinding.slidingPage, filterBinding.tvCaste);
                            filterBinding.includeSlide.rvCaste.setAdapter(casteAdapter);

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

                    arrSubCaste = new ArrayList<SubCast>();
                    Log.e("subcastarrayup", arrSubCaste.size() + "");
                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String mtongue_id = resItem.getString("sub_caste_id");
                            String mtongue_name = resItem.getString("sub_caste_name");

                            arrSubCaste.add(new SubCast(mtongue_id, mtongue_name));
                            Log.e("subcastarraystore", arrSubCaste.size() + "");
                        }

                        if (arrSubCaste.size() > 0) {
                            Collections.sort(arrSubCaste, new Comparator<SubCast>() {
                                @Override
                                public int compare(SubCast lhs, SubCast rhs) {
                                    return lhs.getSB_name().compareTo(rhs.getSB_name());
                                }
                            });
                            Log.e("subcastarraydown", arrMotherTongue.size() + "");
                            subCastAdapter = new SubCastAdapter(nActivity, arrSubCaste, filterBinding.slidingDrawer, filterBinding.slidingPage, filterBinding.tvSubCaste);
                            filterBinding.includeSlide.rvSBCaste.setAdapter(subCastAdapter);

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

    public void getSubCaste() {
        try {
            filterBinding.tvSubCaste.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (arrMotherTongue.size() > 0) {
                        String charcter = cs.toString();
                        String text = filterBinding.tvSubCaste.getText().toString().toLowerCase(Locale.getDefault());
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
    private void getHighestEducationRequest() {


        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                // String paramUsername = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "education.php";
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);
                //BasicNameValuePair UsernamePAir = new BasicNameValuePair("username", paramUsername);

                //List<NameValuePair> nameValuePairList = new ArrayList<>();
                /*   nameValuePairList.add(UsernamePAir);*/

                try {
                    // UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    // httpPost.setEntity(urlEncodedFormEntity);
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
                            educationAdapter = new EducationsAdapter(nActivity, arrEducation, filterBinding.slidingDrawer, filterBinding.slidingPage, filterBinding.btnMenuClose, filterBinding.edtEducation);
                            filterBinding.includeSlide.rvHighestEducation.setAdapter(educationAdapter);

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
                    /*getOccupationsRequest();
                    getOccupations();*/
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    public void getHighestEducation() {
        filterBinding.includeSlide.edtSearchHighestEducation.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (arrEducation.size() > 0) {
                    String charcter = cs.toString();
                    String text = filterBinding.includeSlide.edtSearchHighestEducation.getText().toString().toLowerCase(Locale.getDefault());
                    educationAdapter.filter(text);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });

    }

    private void getStateRequest(final String CoId) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramcountryID = params[0];


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "state.php";//?country_id="+paramUsername;
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);
                Log.e("countryID", paramcountryID);
                BasicNameValuePair UsernamePAir = new BasicNameValuePair("country_id", paramcountryID);
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
                Log.e("--State --", "==" + Ressponce);

                try {
                    arrState = new ArrayList<beanState>();
                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");


                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String SID = resItem.getString("state_id");
                            String SName = resItem.getString("state_name");

                            arrState.add(new beanState(SID, SName));

                        }

                        if (arrState.size() > 0) {
                            Collections.sort(arrState, new Comparator<beanState>() {
                                @Override
                                public int compare(beanState lhs, beanState rhs) {
                                    return lhs.getState_name().compareTo(rhs.getState_name());
                                }
                            });
                            stateAdapter = new StateAdapter(nActivity, arrState, filterBinding.slidingDrawer, filterBinding.slidingPage, filterBinding.btnMenuClose, filterBinding.tvState);
                            filterBinding.includeSlide.rvState.setAdapter(stateAdapter);
                        }

                        resIter = null;

                    }

                    responseData = null;
                    responseObj = null;

                } catch (Exception e) {
                    Log.e("ex", e.getMessage());

                } finally {

                    getCityRequest(AppConstants.CountryId, AppConstants.StateId);
                    getCity();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(CoId);
    }

    public void getStates() {
        filterBinding.includeSlide.edtSearchState.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (arrState.size() > 0) {
                    String charcter = cs.toString();
                    String text = filterBinding.includeSlide.edtSearchState.getText().toString().toLowerCase(Locale.getDefault());
                    stateAdapter.filter(text);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }


    private void getCityRequest(String CoId, String SaId) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramCountry = params[0];
                String paramState = params[1];


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "city.php";//?country_id="+paramCountry+"&state_id="+paramState;
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair CountryPAir = new BasicNameValuePair("country_id", paramCountry);
                BasicNameValuePair CityPAir = new BasicNameValuePair("state_id", paramState);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(CountryPAir);
                nameValuePairList.add(CityPAir);
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
                    arrCity = new ArrayList<beanCity>();
                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");


                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String CID = resItem.getString("city_id");
                            String CName = resItem.getString("city_name");

                            arrCity.add(new beanCity(CID, CName));

                        }

                        if (arrCity.size() > 0) {
                            Collections.sort(arrCity, new Comparator<beanCity>() {
                                @Override
                                public int compare(beanCity lhs, beanCity rhs) {
                                    return lhs.getCity_name().compareTo(rhs.getCity_name());
                                }
                            });
                            cityAdapter = new CityAdapter(nActivity, arrCity, filterBinding.slidingDrawer, filterBinding.slidingPage, filterBinding.btnMenuClose, filterBinding.tvCity);
                            filterBinding.includeSlide.rvCity.setAdapter(cityAdapter);
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
        sendPostReqAsyncTask.execute(CoId, SaId);
    }

    public void getCity() {
        filterBinding.includeSlide.edtSearchCity.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (arrCity.size() > 0) {
                    String charcter = cs.toString();
                    String text = filterBinding.includeSlide.edtSearchCity.getText().toString().toLowerCase(Locale.getDefault());
                    cityAdapter.filter(text);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });

    }

    private void getMotherToungeRequest() {
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                // String paramUsername = params[0];


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "mother_tounge.php";
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);


                try {
                    // UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    // httpPost.setEntity(urlEncodedFormEntity);
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
                            motherTongueAdapter = new MotherTongueAdapter(nActivity, arrMotherTongue, filterBinding.slidingDrawer, filterBinding.slidingPage, filterBinding.tvMotherTongue);
                            filterBinding.includeSlide.rvMotherTongue.setAdapter(motherTongueAdapter);

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
            filterBinding.includeSlide.edtSearchMotherTongue.addTextChangedListener(new TextWatcher() {
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (arrMotherTongue.size() > 0) {
                        String charcter = cs.toString();
                        String text = filterBinding.includeSlide.edtSearchMotherTongue.getText().toString().toLowerCase(Locale.getDefault());
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


    private void saveSearchResult(String AgeM, String AgeF, String HeightM, String HeightF,
                                   String ReligionId, String CasteId, String CountryId, String StateId,
                                  String CityId, String HighestEducationId, String AnnualIncome,
                                  String login_matriId, String MotherToungID, String Diet,String Manglik) {
        Dialog progressBar1=showProgress(FilterHomeActivity.this);
        progressBar1.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String paramAgeM = params[0];
                String paramAgeF = params[1];
                String paramHeightM = params[2];
                String paramHeightF = params[3];
                String paramReligionId = params[4];
                String paramCasteId = params[5];
                String paramCountryId = params[6];
                String paramStateId = params[7];
                String paramCityId = params[8];
                String paramEducationId = params[9];
                String paramAnnualIncome = params[10];
                String paramsLoginMatriId = params[11];
                String paramMothertoungue = params[12];
                String paramsDiet = params[13];
                String paramsManglik = params[14];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "save_search.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair MatriSeachIdPair = new BasicNameValuePair("matri_id", paramsLoginMatriId);
                BasicNameValuePair PairAgeM = new BasicNameValuePair("fromage", paramAgeM);
                BasicNameValuePair PairAgeF = new BasicNameValuePair("toage", paramAgeF);
                BasicNameValuePair PairHeightM = new BasicNameValuePair("from_height", paramHeightM);
                BasicNameValuePair PairHeightF = new BasicNameValuePair("to_height", paramHeightF);
                BasicNameValuePair PairReligionId = new BasicNameValuePair("religion", paramReligionId);
                BasicNameValuePair PairCasteId = new BasicNameValuePair("caste", paramCasteId);
                BasicNameValuePair PairCountryId = new BasicNameValuePair("country", paramCountryId);
                BasicNameValuePair PairStateId = new BasicNameValuePair("state", paramStateId);
                BasicNameValuePair PairCityId = new BasicNameValuePair("city", paramCityId);
                BasicNameValuePair PairEducationId = new BasicNameValuePair("education", paramEducationId);
                BasicNameValuePair PairAnnualIncome = new BasicNameValuePair("annual_income", paramAnnualIncome);
                BasicNameValuePair PairMotherToungue = new BasicNameValuePair("mother_tongue", paramMothertoungue);
                BasicNameValuePair DietPair = new BasicNameValuePair("diet", paramsDiet);
                BasicNameValuePair ManglikPair = new BasicNameValuePair("manglik", paramsManglik);



                List<NameValuePair> nameValuePairList = new ArrayList<>();
//                nameValuePairList.add(PairGender);
                nameValuePairList.add(PairAgeM);
                nameValuePairList.add(PairAgeF);
                nameValuePairList.add(PairHeightM);
                nameValuePairList.add(PairHeightF);
                nameValuePairList.add(PairReligionId);
                nameValuePairList.add(PairCasteId);
                nameValuePairList.add(PairCountryId);
                nameValuePairList.add(PairStateId);
                nameValuePairList.add(PairCityId);
                nameValuePairList.add(PairEducationId);
                nameValuePairList.add(PairAnnualIncome);
                nameValuePairList.add(MatriSeachIdPair);
                nameValuePairList.add(PairMotherToungue);
                nameValuePairList.add(DietPair);
                nameValuePairList.add(ManglikPair);



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

                Log.e("--Search by Result --", "==" + Ressponce);

                try {
                    JSONObject obj = new JSONObject(Ressponce);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        String msgError = obj.getString("message");
                        //Toast.makeText(nActivity, "" + msgError, Toast.LENGTH_LONG).show();

						

                    } else {
                        String msgError = obj.getString("message");
                        setToastStr(nActivity, "" + msgError);
                    }
                    Intent newIntent = new Intent(nActivity, HomeActivity.class);
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    newIntent.putExtra("SearchType", "bydata");
                    //Log.e("genderrsaf", Genderr);
                    //newIntent.putExtra("Gender", Genderr);
                    newIntent.putExtra("AgeM", "" + AgeM);
                    newIntent.putExtra("AgeF", "" + AgeF);
                    newIntent.putExtra("HeightM", "" + HeightM);
                    newIntent.putExtra("HeightF", "" + HeightF);
                    newIntent.putExtra("ReligionId", "" + ReligionId);
                    newIntent.putExtra("CasteId", "" + CasteId);
                    newIntent.putExtra("CountryId", "" + CountryId);
                    newIntent.putExtra("StateId", "" + StateId);
                    newIntent.putExtra("CityId", "" + CityId);
                    newIntent.putExtra("HighestEducationId", "" + HighestEducationId);
                    newIntent.putExtra("AnnualIncome", "" + AnnualIncome);
                    newIntent.putExtra("MotherToungueID", "" + AppConstants.MotherTongueId);
                    newIntent.putExtra("Diet", "" + Diet);
                    newIntent.putExtra("Manglik", "" + Manglik);

                    startActivity(newIntent);
                    finish();

                    progressBar1.dismiss();
                } catch (Exception t) {
                    Log.e("extfngjn", t.getMessage());
                    progressBar1.dismiss();
                }
                progressBar1.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute( AgeM, AgeF, HeightM, HeightF, ReligionId,
                CasteId, CountryId, StateId, CityId, HighestEducationId, AnnualIncome,
                login_matriId, MotherToungID, Diet,Manglik);

    }



    private void getDeleteRequest(String strSSId)
    {
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramsSSID = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"clear_filter.php";
                Log.e("delete_inbox", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair SSIdPair = new BasicNameValuePair("ss_id", paramsSSID);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(SSIdPair);

                try
                {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    Log.e("Parametters Array=", "== "+(nameValuePairList.toString().trim().replaceAll(",","&")));
                    try
                    {
                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        InputStream inputStream = httpResponse.getEntity().getContent();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String bufferedStrChunk = null;
                        while((bufferedStrChunk = bufferedReader.readLine()) != null)
                        {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("Firstption caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe)
                    {
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
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);

                Log.e("delete Saved search", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);

                    String status=obj.getString("status");

                    if (status.equalsIgnoreCase("1"))
                    {
                        String message=obj.getString("message").toString().trim();
                        setToastStrPinkBg(FilterHomeActivity.this, ""+message);
                    }else
                    {
                        String msgError=obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(FilterHomeActivity.this);
                        builder.setMessage(""+msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                    progresDialog.dismiss();
                } catch (Throwable t)
                {
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strSSId);
    }



}
