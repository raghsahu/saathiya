package com.prometteur.sathiya;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prometteur.sathiya.databinding.FragmentOtpVerificationBottomSheetDialogBinding;
import com.prometteur.sathiya.dialog.DialogMessgeActivity;
import com.prometteur.sathiya.fragments.FragmentOtpVerificationBottomSheetDialog;
import com.prometteur.sathiya.utills.AppConstants;

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
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class OtpVerificationActivity extends AppCompatActivity {
    SharedPreferences prefVerifyOtp;

    String enteredOTP="",matri_id="",mobileNo="";
    boolean isFinish = false;
    public static String fromPage="";
FragmentOtpVerificationBottomSheetDialogBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=FragmentOtpVerificationBottomSheetDialogBinding.inflate(getLayoutInflater());
setContentView(binding.getRoot());
        prefVerifyOtp = PreferenceManager.getDefaultSharedPreferences(OtpVerificationActivity.this);
        final String[] otp = {prefVerifyOtp.getString("otp", "")};
        matri_id = prefVerifyOtp.getString("matri_id", "");
        mobileNo = prefVerifyOtp.getString("mobileNo", "");
        strLang = prefVerifyOtp.getString("selLang", "");
       // binding.tvOtp.setText(""+otp[0]);
        binding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        startCounter();
        binding.otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                enteredOTP=otp;
            }
        });
        binding.btnSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)OtpVerificationActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.btnSelected.getWindowToken(), 0);

                otp[0] =prefVerifyOtp.getString("otp","");
                String UserId=prefVerifyOtp.getString("user_id_r","");
                if(!enteredOTP.isEmpty()) {
                    if (otp[0].equalsIgnoreCase(enteredOTP)) {

                            VeryfyOTPRequest(UserId, enteredOTP);

                    }else
                    {
                        AppConstants.setToastStr(OtpVerificationActivity.this,"OTP not matched");
                    }
                }else
                {
                    AppConstants.setToastStr(OtpVerificationActivity.this,"Please enter valid OTP");
                }
            }
        });

        binding.tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String UserId=prefVerifyOtp.getString("user_id_r","");
                if(isFinish)
                {
                    reSendOTPRequest(UserId);
                }
            }
        });


    }


    Dialog progresDialog;
    private void VeryfyOTPRequest(String user_id,final String strOTP)
    {
        progresDialog= showProgress(OtpVerificationActivity.this);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramUserId= params[0];
                String paramOtp= params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"verify_otp.php";
                Log.e("URL", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair UserIdPAir = new BasicNameValuePair("user_id", paramUserId);
                BasicNameValuePair OTPPAir = new BasicNameValuePair("otp", paramOtp);

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(UserIdPAir);
                nameValuePairList.add(OTPPAir);

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
                Log.e("--verify_otp --", "=="+Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);

                    String status=responseObj.getString("status");

                    if (status.equalsIgnoreCase("1"))
                    {
                        String message=responseObj.getString("message");

                        Intent intent=new Intent();
                        intent.putExtra("isVerify",true);
                        setResult(1010,intent);
                        finish();

                    }else
                    {
                        String msgError=responseObj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(OtpVerificationActivity.this);
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
        sendPostReqAsyncTask.execute(user_id,strOTP);
    }



    private void reSendOTPRequest(String user_id)
    {
        progresDialog= showProgress(OtpVerificationActivity.this);
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
                BasicNameValuePair UserIdPAir = new BasicNameValuePair("user_id", paramUserId);
                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", matri_id);
                BasicNameValuePair MobNoPair = new BasicNameValuePair("mobile", mobileNo);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(UserIdPAir);
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(MobNoPair);
                nameValuePairList.add(languagePAir);

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
                        SharedPreferences.Editor editor=prefVerifyOtp.edit();
                        editor.putString("otp",""+otp);
                        editor.commit();

                        String message=responseObj.getString("message");
                        //  Toast.makeText(OtpVerificationActivity.this,""+message,Toast.LENGTH_LONG).show();
                        isFinish = false;
                        startCounter();

                    }else
                    {
                        String msgError=responseObj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(OtpVerificationActivity.this);
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
        sendPostReqAsyncTask.execute(user_id);
    }
    void startCounter()
    {

        new CountDownTimer(30000, 1) {
            public void onTick(long millisUntilFinished) {

                binding.tvResend.setText("Resent in "+millisUntilFinished/1000 +" sec");
            }
            public void onFinish() {

                binding.tvResend.setText("Resend");
                isFinish = true;
                cancel();
            }
        }.start();
    }

}