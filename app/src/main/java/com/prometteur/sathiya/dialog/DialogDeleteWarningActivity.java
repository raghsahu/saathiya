package com.prometteur.sathiya.dialog;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.SplashActivity.strLangCode;
import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.prometteur.sathiya.BaseActivity;
import com.prometteur.sathiya.LoginActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.SplashActivity;
import com.prometteur.sathiya.databinding.DialogWarningBinding;
import com.prometteur.sathiya.utills.AppConstants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

public class DialogDeleteWarningActivity extends BaseActivity {
    DialogWarningBinding warningBinding;
    SharedPreferences prefUpdate;
    String matriId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        warningBinding=DialogWarningBinding.inflate(getLayoutInflater());
        setContentView(warningBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(DialogDeleteWarningActivity.this);
        matriId = prefUpdate.getString("matri_id", "");
        SplashActivity.strLang = prefUpdate.getString("selLang", "");

        warningBinding.tvMessage.setText(R.string.are_you_sure_you_want_to_delete);
        warningBinding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DialogDeleteWarningActivity.this, DialogReasonForDeleteActivity.class));
                finish();
            }
        });
        warningBinding.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DialogDeleteWarningActivity.this, DialogReasonActivity.class));
                finish();
            }
        });
    }

    public void closeDialog(View view)
    {
        finish();
    }
    Dialog progresDialog;



    private void sendLogoutRequest(String matriId)
    {
        progresDialog=showProgress(DialogDeleteWarningActivity.this);
  /*  progresDialog.setCancelable(false);
    progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
    progresDialog.setIndeterminate(true);*/
        progresDialog.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramUsername = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"logout.php";
                Log.e("URL", "== "+URL);
                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPAir = new BasicNameValuePair("matri_id", paramUsername);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", SplashActivity.strLang);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPAir);
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

                } catch (UnsupportedEncodingException uee)
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
                progresDialog.dismiss();
                Log.e("--Login --", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);

                    String status=obj.getString("status");

                    if(status.equalsIgnoreCase("1"))
                    {
                        String message=obj.getString("message");
                        setToastStr(DialogDeleteWarningActivity.this,message);
                        SharedPreferences.Editor editor=prefUpdate.edit();
                        editor.clear();
                        editor.putString("selLang",strLang);
                        editor.putString("selLangCode",strLangCode);
                        editor.apply();
                        editor.commit();

                        Intent intLogin = new Intent(DialogDeleteWarningActivity.this, LoginActivity.class);
                        startActivity(intLogin);
                        finishAffinity();
                    }else
                    {

                        String message=obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(DialogDeleteWarningActivity.this);
                        builder.setMessage(""+message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                } catch (Throwable t)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(DialogDeleteWarningActivity.this);

                    builder.setMessage(getString(R.string.please_enter_valid_username_or_password)).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(matriId);
    }

}
