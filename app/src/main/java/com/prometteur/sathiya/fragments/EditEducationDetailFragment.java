package com.prometteur.sathiya.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.SignUpStep1Activity;
import com.prometteur.sathiya.adapters.CasteAdapter;
import com.prometteur.sathiya.adapters.EducationsAdapter;
import com.prometteur.sathiya.adapters.GeneralAdapter;
import com.prometteur.sathiya.adapters.OccupationAdapter;
import com.prometteur.sathiya.adapters.ReligionAdapter;
import com.prometteur.sathiya.adapters.SubCastAdapter;
import com.prometteur.sathiya.beans.SubCast;
import com.prometteur.sathiya.beans.beanCaste;
import com.prometteur.sathiya.beans.beanEducation;
import com.prometteur.sathiya.beans.beanOccupation;
import com.prometteur.sathiya.beans.beanReligion;
import com.prometteur.sathiya.databinding.FragmentEditEducationDetailBinding;
import com.prometteur.sathiya.utills.AnimUtils;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.prometteur.sathiya.utills.RecyclerTouchListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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


public class EditEducationDetailFragment extends Fragment {

    ArrayList<beanReligion> arrReligion = new ArrayList<beanReligion>();
    ReligionAdapter religionAdapter = null;

    ArrayList<beanCaste> arrCaste = new ArrayList<beanCaste>();
    CasteAdapter casteAdapter = null;

    ArrayList<SubCast> arrMotherTongue = new ArrayList<SubCast>();
    SubCastAdapter motherTongueAdapter = null;


    ArrayList<beanEducation> arrEducation = new ArrayList<beanEducation>();
    EducationsAdapter educationAdapter = null;

    ArrayList<beanOccupation> arrOccupation = new ArrayList<beanOccupation>();
    OccupationAdapter occupationAdapter = null;
    String matri_id = "";
    SharedPreferences prefUpdate;

    String Income="0",EmployedIn="";
    public EditEducationDetailFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
FragmentEditEducationDetailBinding educationDetailBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        educationDetailBinding=FragmentEditEducationDetailBinding.inflate(inflater,container, false);
        View view=educationDetailBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(getActivity());
        matri_id = prefUpdate.getString("matri_id", "");
        strLang = prefUpdate.getString("selLang", "");
        SlidingMenu();
        educationDetailBinding.tvNextClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkConnection.hasConnection(getActivity())) {
                    EmployedIn=educationDetailBinding.edtEmployedIn.getText().toString();
                    Income=educationDetailBinding.edtMonthlyIncome.getText().toString();
                    Income=Income.replace("₹ ","").replace("₹","");
                    if (AppConstants.EducationId.isEmpty() || AppConstants.EducationId.equalsIgnoreCase("0")) {
                        educationDetailBinding.edtEducation.requestFocus();
                        AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_education));
                    }else if (AppConstants.OccupationID.isEmpty() || AppConstants.OccupationID.equalsIgnoreCase("0")) {
                        educationDetailBinding.edtOccupation.requestFocus();
                        AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_occupation));
                    }/*else if (educationDetailBinding.edtEmployedIn.getText().toString().isEmpty() ||EmployedIn.equalsIgnoreCase("Not Available")) {  //on client demand
                        educationDetailBinding.edtEmployedIn.requestFocus();
                        AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_employed_in));
                    }*/else if (educationDetailBinding.edtMonthlyIncome.getText().toString().isEmpty()) {
                        educationDetailBinding.edtMonthlyIncome.requestFocus();
                        AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_monthly_income));
                    }else if (AppConstants.ReligionId.isEmpty() ||AppConstants.ReligionId.equalsIgnoreCase("0") ||AppConstants.ReligionId.equalsIgnoreCase("Not Available")) {
                        educationDetailBinding.edtReligion.requestFocus();
                        AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_religion));
                    }/*else if (AppConstants.CasteId.isEmpty() ||AppConstants.CasteId.equalsIgnoreCase("0") ||AppConstants.CasteId.equalsIgnoreCase("Not Available")) {  //on client demand
                        educationDetailBinding.edtCaste.requestFocus();
                        AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_caste));
                    }else if (AppConstants.SubCasteID.isEmpty() ||AppConstants.SubCasteID.equalsIgnoreCase("0") ||AppConstants.SubCasteID.equalsIgnoreCase("Not Available")) {
                        educationDetailBinding.edtSubCaste.requestFocus();
                        AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_sub_caste));
                    }*/else {
                        updateEducation(matri_id, AppConstants.EducationId, AppConstants.AditionalEducationId, AppConstants.OccupationID,
                                EmployedIn, Income, AppConstants.ReligionId, AppConstants.CasteId, AppConstants.SubCasteID);
                    }
                }
            }
        });
        educationDetailBinding.tvBackClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
            }
        });

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
                if (NetworkConnection.hasConnection(getActivity())) {
                    getReligionRequest();
                    getReligions();
                } else {
                    AppConstants.CheckConnection(getActivity());
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
                    Toast.makeText(getActivity(), "Please select religion First", Toast.LENGTH_SHORT).show();
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

        educationDetailBinding.includeSlide.rvReligion.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), educationDetailBinding.includeSlide.rvReligion, new SignUpStep1Activity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(educationDetailBinding.includeSlide.rvReligion.getWindowToken(), 0);

                beanReligion arrCo = arrReligion.get(position);
                AppConstants.ReligionId = arrCo.getReligion_id();
                AppConstants.ReligionName = arrCo.getName();

                educationDetailBinding.edtReligion.setText(AppConstants.ReligionName);

                AppConstants.CasteId = "";
                AppConstants.StateName = "";

                educationDetailBinding.edtCaste.setText("");

                GonelidingDrower();

                if (NetworkConnection.hasConnection(getActivity())) {
                    getCastRequest(AppConstants.ReligionId);
                    getCaste();
                } else {
                    AppConstants.CheckConnection(getActivity());
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
                GeneralAdapter generalAdapter = new GeneralAdapter(getActivity(), getResources().getStringArray(R.array.arr_Employed_in), educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.btnMenuClose, educationDetailBinding.edtEmployedIn);
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
                GeneralAdapter generalAdapter = new GeneralAdapter(getActivity(), getResources().getStringArray(R.array.arr_monthly_income), educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.btnMenuClose, educationDetailBinding.edtMonthlyIncome);
                educationDetailBinding.includeSlide.rvGeneralView.setAdapter(generalAdapter);

            }
        });

        if (NetworkConnection.hasConnection(getActivity())) {
            getProfileDetail(matri_id);
            // getAdditionalDgree();
            getOccupationsRequest();
            getOccupations();
        } else {
            AppConstants.CheckConnection(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getProfileDetail(matri_id);
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
                            educationAdapter = new EducationsAdapter(getActivity(), arrEducation, educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.btnMenuClose, educationDetailBinding.edtEducation);
                            educationDetailBinding.includeSlide.rvHighestEducation.setAdapter(educationAdapter);

                        }

                        /*if (arrEducation.size() > 0) {
                            Collections.sort(arrAdditionalDgree, new Comparator<beanEducation>() {
                                @Override
                                public int compare(beanEducation lhs, beanEducation rhs) {
                                    return lhs.getEducation_name().compareTo(rhs.getEducation_name());
                                }
                            });

                            additionalDgreeAdapter = new AdditionalDgreeAdapter(getActivity(), arrAdditionalDgree, SlidingDrawer, Slidingpage, btnMenuClose, edtDegree);
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
                            occupationAdapter = new OccupationAdapter(getActivity(), arrOccupation, educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.btnMenuClose, educationDetailBinding.edtOccupation);
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

    public void getProfileDetail(String MatriID) {

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
                Log.e("matri_id", paramsMatriId);

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
                Log.e("View Profile ", "==" + result);

                String finalresult = "";
                try {
                    finalresult = result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1);

                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject responseData = obj.getJSONObject("responseData");
                        if (responseData.has("1")) {
                            Iterator<String> resIter = responseData.keys();
                            while (resIter.hasNext()) {
                                String key = resIter.next();
                                JSONObject resItem = responseData.getJSONObject(key);

                                AppConstants.AditionalEducationId = resItem.getString("addition_dgree_id");
                                AppConstants.EducationId = resItem.getString("edu_detail_id");
                                AppConstants.OccupationID = resItem.getString("occupation_id");

                                AppConstants.ReligionId = resItem.getString("religion_id");
                                AppConstants.CasteId = resItem.getString("caste_id");
                                AppConstants.SubCasteID = resItem.getString("subcaste_id");

                                if(resItem.getString("caste_name")!=null && !resItem.getString("caste_name").equalsIgnoreCase("null") && !resItem.getString("caste_name").isEmpty()) {
                                    educationDetailBinding.edtCaste.setText("" + resItem.getString("caste_name").trim());
                                }
                                if(resItem.getString("religion")!=null && !resItem.getString("religion").equalsIgnoreCase("null") && !resItem.getString("religion").isEmpty()) {
                                    educationDetailBinding.edtReligion.setText("" + resItem.getString("religion").trim());
                                }
                                if(resItem.getString("subcaste")!=null && !resItem.getString("subcaste").equalsIgnoreCase("null") && !resItem.getString("subcaste").isEmpty()) {
                                    educationDetailBinding.edtSubCaste.setText("" + resItem.getString("subcaste").trim());
                                }

                                educationDetailBinding.edtEducation.setText(resItem.getString("edu_detail"));
                               // educationDetailBinding.includeSlide.edtDegree.setText(resItem.getString("addition_detail"));
                                educationDetailBinding.edtOccupation.setText(resItem.getString("occupation"));
                                if(resItem.getString("emp_in")!=null && !resItem.getString("emp_in").equalsIgnoreCase("Not Available")&& !resItem.getString("emp_in").isEmpty()) {
                                    educationDetailBinding.edtEmployedIn.setText(resItem.getString("emp_in"));
                                }
                                if(resItem.getString("income")!=null && !resItem.getString("income").equalsIgnoreCase("Not Available")&& !resItem.getString("income").isEmpty()) {
                                    Income=resItem.getString("income");
                                    educationDetailBinding.edtMonthlyIncome.setText(resItem.getString("income"));
                                }

                                /*educationDetailBinding.dynamicSeekbarIncome.setSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                        educationDetailBinding.dynamicSeekbarIncome.setInfoText(i+"lacs",i);
                                        Income=""+i;
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                    }
                                });*/
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
                        Toast.makeText(getActivity(), "msgError" + msgError, Toast.LENGTH_SHORT).show();

                    }


                } catch (Exception e) {
                    Toast.makeText(getActivity(), "exception" + e.
                            getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("educationeeeeee", e.getMessage());
                }

                if (NetworkConnection.hasConnection(context)) {
                    getHighestEducationRequest();
                    getHighestEducation();
                } else {
                    AppConstants.CheckConnection(getActivity());
                }


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(MatriID);
    }
Context context=null;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public void SlidingMenu() {

        educationDetailBinding.includeSlide.rvReligion.setLayoutManager(new LinearLayoutManager(getActivity()));
        educationDetailBinding.includeSlide.rvReligion.setHasFixedSize(true);
        educationDetailBinding.includeSlide.rvCaste.setLayoutManager(new LinearLayoutManager(getActivity()));
        educationDetailBinding.includeSlide.rvCaste.setHasFixedSize(true);
        educationDetailBinding.includeSlide.rvSBCaste.setLayoutManager(new LinearLayoutManager(getActivity()));
        educationDetailBinding.includeSlide.rvSBCaste.setHasFixedSize(true);

       educationDetailBinding.includeSlide.rvHighestEducation.setLayoutManager(new LinearLayoutManager(getActivity()));
       educationDetailBinding.includeSlide.rvHighestEducation.setHasFixedSize(true);

        educationDetailBinding.includeSlide.rvOccupation.setLayoutManager(new LinearLayoutManager(getActivity()));
        educationDetailBinding.includeSlide.rvOccupation.setHasFixedSize(true);

        educationDetailBinding.includeSlide.rvGeneralView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        AnimUtils.SlideAnimation(getActivity(), educationDetailBinding.slidingDrawer, R.anim.slide_right);
        //SlidingDrawer.startAnimation(AppConstants.inFromRightAnimation()) ;
        educationDetailBinding.slidingPage.setVisibility(View.VISIBLE);


    }

    public void GonelidingDrower() {
        educationDetailBinding.slidingDrawer.setVisibility(View.GONE);
        AnimUtils.SlideAnimation(getActivity(), educationDetailBinding.slidingDrawer, R.anim.slide_left);
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


    private void getReligionRequest() {
       /* progresDialog= new ProgressDialog(SignUpStep1Activity.this);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
        progresDialog.show();
*/
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
                            religionAdapter = new ReligionAdapter(getActivity(), arrReligion, educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.edtReligion);
                            educationDetailBinding.includeSlide.rvReligion.setAdapter(religionAdapter);

                        }

                        resIter = null;

                    }

                    responseData = null;
                    responseObj = null;

                } catch (Exception e) {
                    Log.e("relegion", e.getMessage());

                } finally {
                    if (NetworkConnection.hasConnection(getActivity())) {
                        getCastRequest(AppConstants.ReligionId);
                        getCaste();
                    } else {
                        AppConstants.CheckConnection(getActivity());
                    }
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }


    private void getCastRequest(String strReligion) {
       /* progresDialog= new ProgressDialog(SignUpStep1Activity.this);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
        progresDialog.show();
*/
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
                            casteAdapter = new CasteAdapter(getActivity(), arrCaste, educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.edtCaste);
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

                    arrMotherTongue = new ArrayList<SubCast>();
                    Log.e("subcastarrayup", arrMotherTongue.size() + "");
                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String mtongue_id = resItem.getString("sub_caste_id");
                            String mtongue_name = resItem.getString("sub_caste_name");

                            arrMotherTongue.add(new SubCast(mtongue_id, mtongue_name));
                            Log.e("subcastarraystore", arrMotherTongue.size() + "");
                        }

                        if (arrMotherTongue.size() > 0) {
                            Collections.sort(arrMotherTongue, new Comparator<SubCast>() {
                                @Override
                                public int compare(SubCast lhs, SubCast rhs) {
                                    return lhs.getSB_name().compareTo(rhs.getSB_name());
                                }
                            });
                            Log.e("subcastarraydown", arrMotherTongue.size() + "");
                            motherTongueAdapter = new SubCastAdapter(getActivity(), arrMotherTongue, educationDetailBinding.slidingDrawer, educationDetailBinding.slidingPage, educationDetailBinding.edtSubCaste);
                            educationDetailBinding.includeSlide.rvSBCaste.setAdapter(motherTongueAdapter);

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



    ProgressDialog progresDialog;
    public void updateEducation(String MatriId, String educationID, String AditionalDegreeID, String OccupationID, String EmployeedIn
            , String AnnualIncome,String ReligionId,String CasteId,String SubCasteId) {
        progresDialog = new ProgressDialog(getActivity());
        progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String Pmatri_id = params[0];
                String PEducationId = params[1];
                String PAditionalEducationId = params[2];
                String POccupationID = params[3];
                String PEmployedIn = params[4];
                String PAnnualIncome = params[5];
                String PReligionId = params[6];
                String PCasteId = params[7];
                String PSubCasteId = params[8];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "edit_edu_occ_details.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", Pmatri_id);
                BasicNameValuePair EducationIdPair = new BasicNameValuePair("education_id", PEducationId);
                BasicNameValuePair AditionalEducationIdPair = new BasicNameValuePair("additional_degree_id", PAditionalEducationId);
                BasicNameValuePair OccupationIDPair = new BasicNameValuePair("occupation_id", POccupationID);
                BasicNameValuePair EmployedInPair = new BasicNameValuePair("employed_in", PEmployedIn);
                BasicNameValuePair AnnualIncomePair = new BasicNameValuePair("annual_income", PAnnualIncome);
                BasicNameValuePair ReligionIdPair = new BasicNameValuePair("religion_id", PReligionId);
                BasicNameValuePair CasteIdPair = new BasicNameValuePair("caste_id", PCasteId);
                BasicNameValuePair SubCastIdPair = new BasicNameValuePair("sub_caste_id", PSubCasteId);

                List<NameValuePair> nameValuePairList = new ArrayList<>();

                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(EducationIdPair);
                nameValuePairList.add(AditionalEducationIdPair);
                nameValuePairList.add(OccupationIDPair);
                nameValuePairList.add(EmployedInPair);
                nameValuePairList.add(AnnualIncomePair);
                nameValuePairList.add(ReligionIdPair);
                nameValuePairList.add(CasteIdPair);
                nameValuePairList.add(SubCastIdPair);


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
                Log.e("--Update Education--", "==" + Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);
                    // JSONObject responseData = responseObj.getJSONObject("responseData");

                    String status = responseObj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        getProfileDetail(matri_id);
                        viewPager.setCurrentItem(2);
                       /* Intent intLogin = new Intent(getActivity(), MenuProfileEdit.class);
                        startActivity(intLogin);
                        finish();*/
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
                    Log.e("exceptionEdu", e.getMessage());
                }


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(MatriId, educationID, AditionalDegreeID, OccupationID, EmployeedIn, AnnualIncome,ReligionId,CasteId,SubCasteId);

    }

}