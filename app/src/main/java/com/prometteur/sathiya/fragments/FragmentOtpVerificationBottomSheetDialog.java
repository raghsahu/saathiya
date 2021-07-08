package com.prometteur.sathiya.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.prometteur.sathiya.BaseActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prometteur.sathiya.ChangeForgotPasswordActivity;
import com.prometteur.sathiya.LoginActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.SignUpStep1Activity;
import com.prometteur.sathiya.adapters.AdapterLanguages;
import com.prometteur.sathiya.databinding.DialogUpdateFieldBinding;
import com.prometteur.sathiya.dialog.DialogMessgeActivity;
import com.prometteur.sathiya.dialog.DialogUdateFieldsActivity;
import com.prometteur.sathiya.home.HomeActivity;
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

public class FragmentOtpVerificationBottomSheetDialog extends BottomSheetDialogFragment {
    TextView tvResend,tvOtp;
    SharedPreferences prefVerifyOtp;
    OtpTextView otpTextView;
    String enteredOTP="";
    boolean isFinish = false;
    public static String fromPage="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_otp_verification_bottom_sheet_dialog, container, false);
        ImageView img_close = (ImageView) v.findViewById(R.id.img_close);
        Button btnSelected = (Button) v.findViewById(R.id.btnSelected);
         otpTextView =  v.findViewById(R.id.otpTextView);
        tvResend =  v.findViewById(R.id.tvResend);
        tvOtp =  v.findViewById(R.id.tvOtp);

        prefVerifyOtp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String[] otp = {prefVerifyOtp.getString("otp", "")};
        tvOtp.setText(""+otp[0]);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        startCounter();
        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                enteredOTP=otp;
            }
        });
        btnSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnSelected.getWindowToken(), 0);

                otp[0] =prefVerifyOtp.getString("otp","");
                String UserId=prefVerifyOtp.getString("user_id_r","");
                if(!enteredOTP.isEmpty()) {
                    if (otp[0].equalsIgnoreCase(enteredOTP)) {
                        if(fromPage.equalsIgnoreCase("register")) {
                            VeryfyOTPRequest(UserId, enteredOTP);
                        }else {

                            VeryfyOTPRequest(prefVerifyOtp.getString("mobileNo",""), enteredOTP);
                        }
                    }else
                    {
                        AppConstants.setToastStr(getActivity(),"OTP not matched");
                    }
                }else
                {
                    AppConstants.setToastStr(getActivity(),"Please enter valid OTP");
                }
            }
        });

        tvResend.setOnClickListener(new View.OnClickListener() {
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


        return v;
    }

    ProgressDialog progresDialog;
    private void VeryfyOTPRequest(String user_id,final String strOTP)
    {
        progresDialog= new ProgressDialog(getActivity());
        progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
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
                        //Toast.makeText(getActivity(),""+message,Toast.LENGTH_LONG).show();
                        if(fromPage.equalsIgnoreCase("register")) {
                            startActivity(new Intent(getActivity(), DialogMessgeActivity.class));
                        }else
                        {
                            startActivity(new Intent(getActivity(), ChangeForgotPasswordActivity.class));

                        }
                        dismiss();

                    }else
                    {
                        String msgError=responseObj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        progresDialog= new ProgressDialog(getActivity());
        progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramUserId= params[0];
                //  String paramOtp= params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"resend_otp.php";
                Log.e("URL", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair UserIdPAir = new BasicNameValuePair("user_id", paramUserId);

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
                        SharedPreferences.Editor editor=prefVerifyOtp.edit();
                        editor.putString("otp",""+otp);
                        editor.commit();

                        String message=responseObj.getString("message");
                        //  Toast.makeText(getActivity(),""+message,Toast.LENGTH_LONG).show();
                        isFinish = false;
                        startCounter();

                    }else
                    {
                        String msgError=responseObj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                tvResend.setText("Resent in "+millisUntilFinished/1000 +" sec");
            }
            public void onFinish() {

                tvResend.setText("Resend");
                isFinish = true;
                cancel();
            }
        }.start();
    }

}