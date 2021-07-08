package com.prometteur.sathiya.dialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.prometteur.sathiya.BaseActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.RestAPI.ApiClient;
import com.prometteur.sathiya.RestAPI.ApiInterface;
import com.prometteur.sathiya.RoboPOJO.ProfileManagePhotoResponse;
import com.prometteur.sathiya.adapters.CityAdapter;
import com.prometteur.sathiya.beans.beanCity;
import com.prometteur.sathiya.beans.beanHobby;
import com.prometteur.sathiya.databinding.DialogAddPhotoBinding;
import com.prometteur.sathiya.databinding.DialogUpdateFieldBinding;
import com.prometteur.sathiya.fragments.EditPhotosFragment;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import static com.prometteur.sathiya.SplashActivity.strLang;

public class DialogAddPhotoActivity extends BaseActivity implements LocationListener {
   DialogAddPhotoBinding fieldBinding;
    List<beanHobby> arrHobby = new ArrayList<beanHobby>();
    List<String> strArrHobby = new ArrayList<String>();
    String hobbyId="";
    SharedPreferences prefUpdate;
    protected LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fieldBinding=DialogAddPhotoBinding.inflate(getLayoutInflater());
        setContentView(fieldBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(DialogAddPhotoActivity.this);
        matri_id = prefUpdate.getString("matri_id", "");
        strLang = prefUpdate.getString("selLang", "");
        if (NetworkConnection.hasConnection(this)) {
            getHobbyList();
        } else {
            AppConstants.CheckConnection(this);
        }

        fieldBinding.tvHobbies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldBinding.spHobbies.performClick();
            }
        });
        fieldBinding.spHobbies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fieldBinding.tvHobbies.setText(""+fieldBinding.spHobbies.getSelectedItem().toString());
                hobbyId=arrHobby.get(i).getHobby_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fieldBinding.linAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAllowRotation(false)
                        .setAllowFlipping(false)
                        .setAspectRatio(700, 500)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(DialogAddPhotoActivity.this);
            }
        });

        fieldBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (strFilePath.equalsIgnoreCase("")) {


                    Toast.makeText(DialogAddPhotoActivity.this, "Please select hobby picture.", Toast.LENGTH_LONG).show();


                } else if(hobbyId.equalsIgnoreCase("0") || hobbyId.isEmpty()) {
                    Toast.makeText(DialogAddPhotoActivity.this, "Please select hobby.", Toast.LENGTH_LONG).show();
                }else
                {
                    if (NetworkConnection.hasConnection(DialogAddPhotoActivity.this)){
                        uploadImage(matri_id, strFilePath);

                    }else
                    {
                        AppConstants.CheckConnection(DialogAddPhotoActivity.this);
                    }
                }

            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("call home api","resume");
            getLocationPermission();
            return;
        }else
        {
            //getLocationPermission();
            Loc_permissiongranted=true;
            getDeviceLocation();
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,DialogAddPhotoActivity.this);
    }



    private void getHobbyList() {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "hobbies_title.php";
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

                Log.e("--City --", "==" + Ressponce);

                try {
                    arrHobby = new ArrayList<beanHobby>();
                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");


                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String hobyId = resItem.getString("hoby_id");
                            String hobyName = resItem.getString("title"); //hobby_name

                            arrHobby.add(new beanHobby(hobyId, hobyName));
                            strArrHobby.add(hobyName);
                        }

                        if (arrHobby.size() > 0) {
                            /*Collections.sort(arrHobby, new Comparator<beanHobby>() {
                                @Override
                                public int compare(beanHobby lhs, beanHobby rhs) {
                                    return lhs.getHobby_name().compareTo(rhs.getHobby_name());
                                }
                            });*/
                            ArrayAdapter adapter =new ArrayAdapter(DialogAddPhotoActivity.this, android.R.layout.simple_spinner_item,strArrHobby);
                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item);
                            fieldBinding.spHobbies.setAdapter(adapter);
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

String strFilePath="",matri_id="";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Glide.with(DialogAddPhotoActivity.this)
                        .load(result.getUri())
                        .placeholder(R.drawable.loadimage)
                        .error(R.drawable.ic_profile)
                        .into(fieldBinding.tvAddPhoto);


                strFilePath = result.getUri().toString().replace("file://", "");



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(DialogAddPhotoActivity.this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }


    private void uploadImage(String strUserId, String strfilePath) {


        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        final ProgressDialog progressDialogSendReq = new ProgressDialog(DialogAddPhotoActivity.this);
        progressDialogSendReq.setCancelable(false);
        progressDialogSendReq.setMessage(getResources().getString(R.string.Please_Wait));
        progressDialogSendReq.setIndeterminate(true);
        progressDialogSendReq.show();

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), strUserId);
        RequestBody hobby_id = RequestBody.create(MediaType.parse("text/plain"), hobbyId);
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), strLat);
        RequestBody longi = RequestBody.create(MediaType.parse("text/plain"), strLong);

        File file = new File(strfilePath);
        Log.d("ravi", "file is = " + file.toString());

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image_path", file.getName(), requestFile);


        Call<ProfileManagePhotoResponse> call = apiService.postManagePhotoHobby(user_id, hobby_id,image, lat,longi);// uplaod_photo.php


        call.enqueue(new retrofit2.Callback<ProfileManagePhotoResponse>() {
            @Override
            public void onResponse(Call<ProfileManagePhotoResponse> call, retrofit2.Response<ProfileManagePhotoResponse> response) {
                ProfileManagePhotoResponse profileManagePhotoResponse = response.body();
                Log.e("Responce =", "" + profileManagePhotoResponse);

                if (profileManagePhotoResponse.getStatus().equalsIgnoreCase("1")) {
                    String message = profileManagePhotoResponse.getMessage();
                    Toast.makeText(DialogAddPhotoActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                    String responseImage = profileManagePhotoResponse.getImage();

                    /*if (PhotoId.equalsIgnoreCase("1")) {
                        Glide.with(DialogAddPhotoActivity.this)
                                .load(responseImage)
                                .placeholder(R.drawable.loadimage)
                                .into(imgUserPhotos);
                    }*/
                } else {
                    String msgError = profileManagePhotoResponse.getMessage();
                    AlertDialog.Builder builder = new AlertDialog.Builder(DialogAddPhotoActivity.this);
                    builder.setMessage("" + msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                progressDialogSendReq.dismiss();
            }

            @Override
            public void onFailure(Call<ProfileManagePhotoResponse> call, Throwable t) {

            }
        });

    }


    private static final int loc_request_code = 1234;
    private static final String Fine_Location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Coarse_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    public void getLocationPermission() {
        Log.d("Location", "getLocationPermission: getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            if (ContextCompat.checkSelfPermission(DialogAddPhotoActivity.this.getApplicationContext(), Fine_Location) == PackageManager.PERMISSION_GRANTED) {
                Log.d("Location", "getLocationPermission:  fine permissions already granted");
                if (ContextCompat.checkSelfPermission(DialogAddPhotoActivity.this.getApplicationContext(), Coarse_Location) == PackageManager.PERMISSION_GRANTED) {
                    Loc_permissiongranted = true;
                    //initalizeMap();
                    getDeviceLocation();
                    Log.d("Location", "getLocationPermission:  coarse permissions already granted");

                } else {
                    ActivityCompat.requestPermissions(DialogAddPhotoActivity.this,
                            permissions, loc_request_code);
                }
            } else {
                ActivityCompat.requestPermissions(DialogAddPhotoActivity.this,
                        permissions, loc_request_code);
            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(loc_request_code==requestCode)
        {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                Loc_permissiongranted = true;
            }else{
                //show some warning
            }

            // enableLocation(DialogAddPhotoActivity.this);
            //getDeviceLocation();
        }
    }

    private static boolean Loc_permissiongranted = false;
    private static FusedLocationProviderClient mFusedLocationProviderClient;
    public static String strLat="0";
    public static String strLong="0";
    private void getDeviceLocation() {
        Log.d("Location", "getDeviceLocation: Getting current Location of device");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DialogAddPhotoActivity.this);
        try {
            if (Loc_permissiongranted) {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(DialogAddPhotoActivity.this,
                        new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Log.d("Location", "onSuccess: location data");
                                    Log.d("Location", "onSuccess: " + location.getLatitude() + "    " + location.getLongitude());

                                            strLat = "" + location.getLatitude();
                                            strLong = "" + location.getLongitude();

                                } else {
                                    Log.d("Location", "onfailure: unable to find current device location");
                                    /*strLat=Preferences.getPreferenceValue(DialogAddPhotoActivity.this,"lat");
                                    strLong=Preferences.getPreferenceValue(DialogAddPhotoActivity.this,"lon");*/
                                    if(strLat.equalsIgnoreCase("NA"))
                                    {
                                        strLat="0";
                                        strLong="0";
                                       
                                    }
                                    
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Location", "getDeviceLocation: " + e.getMessage());
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Location", "getDeviceLocation: " + e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
