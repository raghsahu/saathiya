package com.prometteur.sathiya.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

import com.prometteur.sathiya.BaseActivity;

import com.prometteur.sathiya.LoginActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.databinding.DialogReasonBinding;
import com.prometteur.sathiya.databinding.DialogWarningBinding;
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

import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class DialogReasonActivity extends BaseActivity {
   DialogReasonBinding reasonBinding;
BaseActivity nActivity=DialogReasonActivity.this;
    SharedPreferences prefUpdate;
    String userId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reasonBinding=DialogReasonBinding.inflate(getLayoutInflater());
        setContentView(reasonBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        userId = prefUpdate.getString("user_id", "");
        reasonBinding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton radioButton=findViewById(reasonBinding.rgReason.getCheckedRadioButtonId());
                if(radioButton!=null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                    builder.setMessage(getString(R.string.do_you_want_to_deactivate_account)).setCancelable(false).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            setDeactivateAccount(userId,radioButton.getText().toString());
                            dialog.dismiss();
                            finish();
                        }
                    }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }else
                {
                    AppConstants.setToastStr(nActivity,getString(R.string.please_select_reason));
                }

            }
        });

        reasonBinding.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    private void setDeactivateAccount(String userId,String reason) {
        /*final ProgressDialog progresDialog11 = new ProgressDialog(nActivity);
        progresDialog11.setCancelable(false);
        progresDialog11.setMessage(nActivity.getResources().getString(R.string.Please_Wait));
        progresDialog11.setIndeterminate(true);*/
        final Dialog progresDialog11 = showProgress(nActivity);
        if(progresDialog11!=null && !progresDialog11.isShowing()) {
            progresDialog11.show();
        }
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";

                URL = AppConstants.MAIN_URL + "deactivate_account.php";
                Log.e("URL get price", "== " + URL);


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = null,ReasonPair=null;

                MatriIdPair = new BasicNameValuePair("user_id", userId);
                ReasonPair = new BasicNameValuePair("reason", reason);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(ReasonPair);

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

                Log.e("get price", "==" + result);

                try {
                    if (result!=null) {
                        JSONObject obj = new JSONObject(result);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            AppConstants.setToastStr(nActivity,""+obj.getString("message"));
                            SharedPreferences.Editor editor=prefUpdate.edit();
                            editor.clear().apply();
                            editor.commit();

                            Intent intLogin = new Intent(nActivity, LoginActivity.class);
                            startActivity(intLogin);
                            finishAffinity();
                        }else
                        {
                            AppConstants.setToastStr(nActivity,""+obj.getString("message"));
                        }
                        progresDialog11.dismiss();
                    }
                } catch (Exception t) {
                    Log.d("ERRRR", t.toString());
                    progresDialog11.dismiss();
                }
                progresDialog11.dismiss();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
