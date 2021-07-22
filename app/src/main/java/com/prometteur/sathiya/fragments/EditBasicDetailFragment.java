package com.prometteur.sathiya.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.SignUpStep1Activity;
import com.prometteur.sathiya.SplashActivity;
import com.prometteur.sathiya.adapters.CityAdapter;
import com.prometteur.sathiya.adapters.StateAdapter;
import com.prometteur.sathiya.beans.beanCity;
import com.prometteur.sathiya.beans.beanState;
import com.prometteur.sathiya.databinding.FragmentEditBasicDetailBinding;
import com.prometteur.sathiya.home.HomeActivity;
import com.prometteur.sathiya.utills.AnimUtils;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.AppMethods;
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
import static com.prometteur.sathiya.profile.EditProfileActivity.pageType;
import static com.prometteur.sathiya.profile.EditProfileActivity.rivProfileImage;
import static com.prometteur.sathiya.profile.EditProfileActivity.viewPager;
import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppConstants.setToastStrPinkBg;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;


public class EditBasicDetailFragment extends Fragment {


    //voice to text
    public static final Integer RecordAudioRequestCode = 1;
    static FragmentEditBasicDetailBinding detailBinding;
    String matri_id = "";
    SharedPreferences prefUpdate;
    ArrayList<beanState> arrState = new ArrayList<beanState>();
    StateAdapter stateAdapter = null;
    ArrayList<beanCity> arrCity = new ArrayList<beanCity>();
    CityAdapter cityAdapter = null;
    String Height = "0", HeightInch = "0";
    String description = "";
    DatePickerDialog.OnDateSetListener pickerListener;
    private SpeechRecognizer speechRecognizer;

    public EditBasicDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailBinding = FragmentEditBasicDetailBinding.inflate(inflater, container, false);
        View view = detailBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(getActivity());
        matri_id = prefUpdate.getString("matri_id", "");
        strLang = prefUpdate.getString("selLang", "");
        if(pageType!=null && pageType.equalsIgnoreCase("basicDetails"))
        {
            detailBinding.tvNextClick.setText(getString(R.string.update));
        }else
        {
            detailBinding.tvNextClick.setText(getString(R.string.next));
        }
        detailBinding.tvNextClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email = detailBinding.edtEmail.getText().toString();

                String Birthdate = detailBinding.edtDOB.getText().toString();
                String Gender = detailBinding.edtGender.getText().toString();
                String MobileNo = detailBinding.edtMobileNum.getText().toString();
                String ProfileText = detailBinding.edtDescription.getText().toString().trim();

                if (detailBinding.edtFullName.getText().toString().trim().equalsIgnoreCase("")) {
                    detailBinding.edtFullName.requestFocus();
                    AppConstants.setToastStr(getActivity(), getString(R.string.please_enter_firstname_lastname));
                } else if (!Email.isEmpty() && !checkEmail(Email)) { //on client demand
                    detailBinding.edtEmail.requestFocus();
                    detailBinding.edtEmail.setError(getResources().getString(R.string.Please_enter_valid_email_address));
                } else if (detailBinding.edtMobileNum.getText().toString().equalsIgnoreCase("")) {
                    detailBinding.edtMobileNum.requestFocus();
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_enter_mobile_no));
                } else if (detailBinding.edtMobileNum.getText().toString().length() != 10) {
                    detailBinding.edtMobileNum.requestFocus();
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_enter_valid_mobile_number));
                } /*else if (detailBinding.edtGender.getText().toString().equalsIgnoreCase("") || detailBinding.edtGender.getText().toString().equalsIgnoreCase("Select Gender")) {
                    detailBinding.edtGender.requestFocus();
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_gender));
                } */else if (Birthdate.isEmpty()) {
                    detailBinding.edtDOB.requestFocus();
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_date_of_birth));
                } else if (AppConstants.StateId.isEmpty() || AppConstants.StateId.equalsIgnoreCase("null")) {
                    detailBinding.edtState.requestFocus();
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_state));
                } else if (AppConstants.CityId.isEmpty()  || AppConstants.CityId.equalsIgnoreCase("null")) {
                    detailBinding.edtCity.requestFocus();
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_city));
                } else if (Height.equalsIgnoreCase("0") || Height.isEmpty()) {
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_select_height));
                } /*else if (ProfileText.isEmpty()) {    //on client demand
                    detailBinding.edtDescription.requestFocus();
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_enter_brief_description));
                } *//*else if (DealMaker.isEmpty()) {
                    detailBinding.edtDealMaker.requestFocus();
                    AppConstants.setToastStr(getActivity(), getResources().getString(R.string.please_enter_prime_quality));
                }*/ else {
                    String FirstName = detailBinding.edtFullName.getText().toString().split(" ")[0];
                    String LastName = "";
                    try {
                        LastName = detailBinding.edtFullName.getText().toString().split(" ")[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    getUpdateSteps(Email, FirstName, LastName, Birthdate,
                            Gender, AppConstants.CityId, AppConstants.StateId, Height,
                            MobileNo, matri_id, HeightInch, ProfileText);
                }
            }
        });
        if (NetworkConnection.hasConnection(getActivity())) {
            getViewProfileRequest(matri_id);
        } else {
            AppConstants.CheckConnection(getActivity());
        }

        if (NetworkConnection.hasConnection(getActivity())) {
            getStateRequest("");
            getStates();


            getCityRequest(AppConstants.CountryId, AppConstants.StateId);
            getCity();


        } else {
            AppConstants.CheckConnection(getActivity());
        }
        SlidingMenu();

        detailBinding.edtState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                detailBinding.edtState.setError(null);
                detailBinding.includeSlide.edtSearchState.setText("");
                detailBinding.edtState.setFocusable(true);
                detailBinding.includeSlide.linState.setVisibility(View.VISIBLE);
                detailBinding.includeSlide.linCity.setVisibility(View.GONE);
                detailBinding.includeSlide.linCountry.setVisibility(View.GONE);

            }
        });

        detailBinding.edtCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleSlidingDrower();
                detailBinding.edtCity.setError(null);
                detailBinding.includeSlide.edtSearchCity.setText("");
                detailBinding.includeSlide.linState.setVisibility(View.GONE);
                detailBinding.includeSlide.linCity.setVisibility(View.VISIBLE);
                detailBinding.includeSlide.linCountry.setVisibility(View.GONE);

            }
        });


        detailBinding.includeSlide.rvState.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), detailBinding.includeSlide.rvState, new SignUpStep1Activity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(detailBinding.includeSlide.rvState.getWindowToken(), 0);

                beanState arrCo = arrState.get(position);
                AppConstants.StateName = arrCo.getState_name();
                AppConstants.StateId = arrCo.getState_id();

                detailBinding.edtState.setText(AppConstants.StateName);

                AppConstants.CityName = "";
                AppConstants.CityId = "";
                detailBinding.edtCity.setText("");

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

        detailBinding.includeSlide.rvCity.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), detailBinding.includeSlide.rvCity, new SignUpStep1Activity.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(detailBinding.includeSlide.rvCity.getWindowToken(), 0);

                if (arrCity != null && arrCity.size() != 0) {
                    beanCity arrCo = arrCity.get(position);
                    AppConstants.CityName = arrCo.getCity_name();
                    AppConstants.CityId = arrCo.getCity_id();
                } else {
                    setToastStr(getActivity(), "Select State ");
                }
                detailBinding.edtCity.setText(AppConstants.CityName);

                GonelidingDrower();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.arr_gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        detailBinding.spGender.setVisibility(View.GONE);
        detailBinding.spGender.setAdapter(adapter);
       /* detailBinding.edtGender.setOnClickListener(new View.OnClickListener() {  // on client demand
            @Override
            public void onClick(View view) {
                detailBinding.spGender.performClick();
            }
        });*/
        /*detailBinding.spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                if(i==0){

                }else {
                    detailBinding.edtGender.setText("" + detailBinding.spGender.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        detailBinding.dynamicSeekbar.setSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    Height = "" + i;
                    detailBinding.dynamicSeekbar.setInfoText("" + i + "ft", i);
                }else{
                    detailBinding.dynamicSeekbar.setInfoText("" + heightFt + "ft",heightFt);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        detailBinding.dynamicSeekbarInch.setSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    HeightInch = "" + i;
                    detailBinding.dynamicSeekbarInch.setInfoText("" + i + "in", i);
                }else{
                    detailBinding.dynamicSeekbarInch.setInfoText("" + heightInch + "in", heightInch);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        initSpeechToText();
    }

    private void initSpeechToText() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                description = detailBinding.edtDescription.getText().toString();
                detailBinding.edtDescription.setText(description);
                detailBinding.edtDescription.setHint(getString(R.string.listening));
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                detailBinding.cirMicrophone.setImageResource(R.drawable.ic_microphone_logo);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                detailBinding.edtDescription.setText(description + " " + data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        detailBinding.cirMicrophone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    detailBinding.cirMicrophone.setImageResource(R.drawable.ic_mic_black_24dp);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                setToastStrPinkBg(getActivity(), getString(R.string.permission_granted));
        }
    }

    private boolean checkEmail(String email) {
        return AppConstants.email_pattern.matcher(email).matches();
    }
    int heightFt=0,heightInch=0;
    private void getViewProfileRequest(String strMatriId) {
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
                                JSONObject resItem = responseData.getJSONObject(key);


                                //  edtProfileCreatedBy.setText(resItem.getString("profileby"));

                                String FName = resItem.getString("firstname");
                                String LName = resItem.getString("lastname");
                                //String[] name_array = LFName.split(" ");
                                if (!FName.isEmpty()) {
                                    detailBinding.edtFullName.setText((FName + " " + LName).trim());
                                }
                                //edtLastName.setText(name_array[1]);


                                detailBinding.edtEmail.setText(resItem.getString("email"));
                                detailBinding.edtDOB.setText(resItem.getString("birthdate"));
                                String strGender = resItem.getString("gender");
                                detailBinding.edtGender.setText("" + resItem.getString("gender"));
                                AppConstants.MotherTongueId = resItem.getString("m_tongue_id");
                                detailBinding.edtMobileNum.setText(resItem.getString("mobile"));

                                detailBinding.edtState.setText(resItem.getString("state_name"));
                                AppConstants.StateId = resItem.getString("state_id");

                                detailBinding.edtCity.setText(resItem.getString("city_name"));
                                AppConstants.CityId = resItem.getString("city");

                                if (resItem.getString("profile_text") != null && !resItem.getString("profile_text").equalsIgnoreCase("null")) {
                                    detailBinding.edtDescription.setText(resItem.getString("profile_text"));
                                }

                                if (resItem.getString("user_height_in_ft") != null && !resItem.getString("user_height_in_ft").isEmpty() && !resItem.getString("user_height_in_ft").equalsIgnoreCase("null")) {
                                    Height = resItem.getString("user_height_in_ft");
                                    heightFt=Integer.parseInt(Height);
                                    detailBinding.dynamicSeekbar.setInfoText("" + heightFt + "ft",heightFt);
                                }

                                if (resItem.getString("user_height_in_inch") != null && !resItem.getString("user_height_in_inch").isEmpty() && !resItem.getString("user_height_in_inch").equalsIgnoreCase("null")) {
                                    HeightInch = resItem.getString("user_height_in_inch");
                                    heightInch=Integer.parseInt(HeightInch);
                                    detailBinding.dynamicSeekbarInch.setInfoText("" + heightInch + "in", heightInch);
                                }

                                Glide.with(getActivity()).load(resItem.getString("photo1")).error(R.drawable.img_item).into(rivProfileImage);
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

    public void VisibleSlidingDrower() {
        detailBinding.slidingDrawer.setVisibility(View.VISIBLE);
        AnimUtils.SlideAnimation(getActivity(), detailBinding.slidingDrawer, R.anim.slide_right);
        detailBinding.slidingPage.setVisibility(View.VISIBLE);
    }

    public void GonelidingDrower() {
        detailBinding.slidingDrawer.setVisibility(View.GONE);
        AnimUtils.SlideAnimation(getActivity(), detailBinding.slidingDrawer, R.anim.slide_left);
        detailBinding.slidingPage.setVisibility(View.GONE);
    }

    public void SlidingMenu() {

        detailBinding.includeSlide.rvState.setLayoutManager(new LinearLayoutManager(getActivity()));
        detailBinding.includeSlide.rvState.setHasFixedSize(true);

        detailBinding.includeSlide.rvCity.setLayoutManager(new LinearLayoutManager(getActivity()));
        detailBinding.includeSlide.rvCity.setHasFixedSize(true);


//        rvGeneralView.setLayoutManager(new LinearLayoutManager(this));
//        rvGeneralView.setHasFixedSize(true);
//


        detailBinding.btnMenuClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GonelidingDrower();
            }
        });

        detailBinding.slidingPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GonelidingDrower();

            }
        });

        detailBinding.edtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppMethods.hideKeyboard(view);
                detailBinding.edtDOB.setError(null);

                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
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
                            stateAdapter = new StateAdapter(getActivity(), arrState, detailBinding.slidingDrawer, detailBinding.slidingPage, detailBinding.btnMenuClose, detailBinding.edtState);
                            detailBinding.includeSlide.rvState.setAdapter(stateAdapter);
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
        detailBinding.includeSlide.edtSearchState.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (arrState.size() > 0) {
                    String charcter = cs.toString();
                    String text = detailBinding.includeSlide.edtSearchState.getText().toString().toLowerCase(Locale.getDefault());
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
                            cityAdapter = new CityAdapter(getActivity(), arrCity, detailBinding.slidingDrawer, detailBinding.slidingPage, detailBinding.btnMenuClose, detailBinding.edtCity);
                            detailBinding.includeSlide.rvCity.setAdapter(cityAdapter);
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
        detailBinding.includeSlide.edtSearchCity.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (arrCity.size() > 0) {
                    String charcter = cs.toString();
                    String text = detailBinding.includeSlide.edtSearchCity.getText().toString().toLowerCase(Locale.getDefault());
                    cityAdapter.filter(text);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });

    }

    private void getUpdateSteps(String Email, String Firstname, String LastName, String Birthdate
            , String Gender, String City, String State,
                                String Height, String MobileNo, String matri_id, String HeightInch, String ProfileText) {
      Dialog  progresDialog = showProgress(getActivity());
        /*progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);*/
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String paramEmail = params[0];
                String paramFirstname = params[1];
                String paramLastname = params[2];
                String paramBirthdate = params[3];
                String paramGender = params[4];
                String paramCity = params[5];
                String paramState = params[6];
                String paramHeight = params[7];
                String paramMobileNo = params[8];
                String paramMatriid = params[9];
                String paramHeightInch = params[10];
                String paramProfileText = params[11];


                HttpClient httpClient = new DefaultHttpClient();

                // String URL= AppConstants.MAIN_URL +"edit_step1.php";
                String URL = AppConstants.MAIN_URL + "edit_basic_details.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);


                BasicNameValuePair FirstnamePAir = new BasicNameValuePair("firstname", paramFirstname);
                BasicNameValuePair LastnamePAir = new BasicNameValuePair("lastname", paramLastname);
                BasicNameValuePair Email = new BasicNameValuePair("email", paramEmail);
                BasicNameValuePair BirthdatePAir = new BasicNameValuePair("birthdate", paramBirthdate);
                BasicNameValuePair MobileNoPAir = new BasicNameValuePair("mobile_no", paramMobileNo);
                BasicNameValuePair MatriIdPAir = new BasicNameValuePair("matri_id", paramMatriid);
                BasicNameValuePair Gender = new BasicNameValuePair("gender", paramGender);
                BasicNameValuePair City = new BasicNameValuePair("city", paramCity);
                BasicNameValuePair State = new BasicNameValuePair("state", paramState);
                BasicNameValuePair Height = new BasicNameValuePair("height", paramHeight);
                BasicNameValuePair HeightInch = new BasicNameValuePair("height_inch", paramHeightInch);
                BasicNameValuePair ProfileText = new BasicNameValuePair("profile_text", paramProfileText);


                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(Email);
                nameValuePairList.add(FirstnamePAir);
                nameValuePairList.add(LastnamePAir);
                nameValuePairList.add(BirthdatePAir);
                nameValuePairList.add(Gender);
                nameValuePairList.add(City);
                nameValuePairList.add(State);
                nameValuePairList.add(Height);
                nameValuePairList.add(MobileNoPAir);
                nameValuePairList.add(MatriIdPAir);
                nameValuePairList.add(HeightInch);
                nameValuePairList.add(ProfileText);


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
                        setToastStrPinkBg(getActivity(), "" + message);
                        if(pageType!=null && pageType.equalsIgnoreCase("basicDetails"))
                        {
                            SharedPreferences.Editor editor=prefUpdate.edit();
                            editor.putString("username",Firstname+" "+LastName);
                            editor.putString("gender",Gender);
                            editor.apply();
                            Intent intLogin = new Intent(getActivity(), HomeActivity.class);
                            startActivity(intLogin);
                            getActivity().finishAffinity();
                        }else {
                            viewPager.setCurrentItem(1);
                        }
                        /*Intent intLogin = new Intent(getActivity(), MenuProfileEdit.class);
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
                    Log.e("exception0", e.getMessage());

                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(Email, Firstname, LastName, Birthdate,
                Gender, City, State, Height, MobileNo, matri_id, HeightInch, ProfileText);
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = (calendar.get(Calendar.YEAR) - 18);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, yy, mm, dd);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(yy, mm, dd);
            datePickerDialog.getDatePicker().setMaxDate(calendar1.getTimeInMillis());
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }

        public void populateSetDate(int year, int month, int day) {

            SimpleDateFormat sDateFormat = new SimpleDateFormat("MM-dd-yyyy");
            SimpleDateFormat tDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                detailBinding.edtDOB.setText(tDateFormat.format(sDateFormat.parse(month + "-" + day + "-" + year)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}