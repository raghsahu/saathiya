package com.prometteur.sathiya;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v7.app.BaseActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prometteur.sathiya.BaseActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import com.prometteur.sathiya.fragments.FragmentOtpVerificationBottomSheetDialog;
import com.prometteur.sathiya.utills.AppConstants;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.fragments.FragmentOtpVerificationBottomSheetDialog.fromPage;
import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class ForgotPasswordActivity extends BaseActivity {
    LinearLayout llBack;
    TextView txt_login/*,textviewSignUp*/;

    AppCompatButton btnSubmit;
    EditText edtEmailId;


    Dialog progresDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        llBack = findViewById(R.id.ll_back);
        txt_login = findViewById(R.id.txt_login);
//        textviewSignUp=(TextView)findViewById(R.id.textviewSignUp);
//        textviewSignUp.setText(getResources().getString(R.string.Login));
//        btnBack.setVisibility(View.GONE);
//        textviewHeaderText.setVisibility(View.GONE);

        edtEmailId = findViewById(R.id.edtEmailId);
        btnSubmit = findViewById(R.id.btnSubmit);
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(ForgotPasswordActivity.this);
        strLang=prefUpdate.getString("selLang","English");
       // llBack.setVisibility(View.GONE);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        if(getIntent().getStringExtra("mobileNo")!=null){
            sendOTPRequest(getIntent().getStringExtra("mobileNo"));
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnSubmit.getWindowToken(), 0);

                String strUsername = edtEmailId.getText().toString().trim();

                if (strUsername.length()!=10) {
                    //edtEmailId.setError(getResources().getString(R.string.Please_enter_username));
                    setToastStr(ForgotPasswordActivity.this, getResources().getString(R.string.Please_enter_your_register_your_mobile_no));
                } else {
                    sendOTPRequest(strUsername);
                   //getForgotPasswordRequest(strUsername);

                }
            }
        });


    }

    private boolean checkEmail(String email) {
        return AppConstants.email_pattern.matcher(email).matches();
    }


  /*  private void getForgotPasswordRequest(String EmailId) {
        progresDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String paramEmailId = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "forgot_pass.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair EmailIdPAir = new BasicNameValuePair("email_id", paramEmailId);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(EmailIdPAir);
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

                        final AlertDialog.Builder alert = new AlertDialog.Builder(
                                ForgotPasswordActivity.this);
                        alert.setMessage(getString(R.string.your_password_link_sent));
                        alert.setCancelable(false)
                                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        SignUpStep1Activity.strPassword=null;SignUpStep1Activity.strUsername=null;
                                        Intent intLogin = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                        startActivity(intLogin);
                                        finish();
                                    }
                                });
                        final AlertDialog dialog = alert.create();
                        dialog.show();

                    } else {
                        String msgError = responseObj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        builder.setMessage("" + msgError).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                } catch (Exception e) {

                } finally {

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(EmailId);
    }*/


    private void sendOTPRequest(String mobNo)
    {
        progresDialog= showProgress(ForgotPasswordActivity.this);
       /* progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);*/
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramUserId= params[0];
                //  String paramOtp= params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"get_otp.php";
                Log.e("URL", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair UserIdPAir = new BasicNameValuePair("mobile", paramUserId);

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(UserIdPAir);

                try
                {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
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

                } catch (Exception uee)
                {
                    System.out.println("Anption given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String Ressponce)
            {
                super.onPostExecute(Ressponce);
                progresDialog.dismiss();
                Log.e("--resend_otp --", "=="+Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);
                    // JSONObject responseData = responseObj.getJSONObject("responseData");

                    String status=responseObj.getString("status");

                    if (status.equalsIgnoreCase("1"))
                    {
                        String otp=responseObj.getString("otp");
                        //edtOTP.setText(""+otp);
                        SharedPreferences.Editor editor=prefUpdate.edit();
                        editor.putString("otp",""+otp);
                        editor.putString("mobileNo",""+mobNo);
                        editor.apply();
                        fromPage="forgotPass";
                        new FragmentOtpVerificationBottomSheetDialog().show(getSupportFragmentManager(), "Dialog");
                        String message=responseObj.getString("message");
                        AppConstants.setToastStrPinkBg(ForgotPasswordActivity.this,message);
                        //  Toast.makeText(ForgotPasswordActivity.this,""+message,Toast.LENGTH_LONG).show();
                       /* isFinish = false;
                        startCounter();*/

                    }else
                    {
                        String msgError=responseObj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                        builder.setMessage(""+msgError).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                    responseObj = null;

                } catch (Exception e)
                {

                } finally
                {

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(mobNo);
    }

}
