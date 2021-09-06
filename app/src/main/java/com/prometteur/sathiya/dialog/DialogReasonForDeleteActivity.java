package com.prometteur.sathiya.dialog;

import static com.prometteur.sathiya.utills.AppMethods.showProgress;

import android.app.Dialog;
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
import android.widget.RadioButton;

import com.prometteur.sathiya.BaseActivity;
import com.prometteur.sathiya.LoginActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.databinding.DialogReasonBinding;
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

public class DialogReasonForDeleteActivity extends BaseActivity {
    DialogReasonBinding reasonBinding;
    BaseActivity nActivity = DialogReasonForDeleteActivity.this;
    SharedPreferences prefUpdate;
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reasonBinding = DialogReasonBinding.inflate(getLayoutInflater());
        setContentView(reasonBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        userId = prefUpdate.getString("user_id", "");
        reasonBinding.btnYes.setText(getString(R.string.delete));
        reasonBinding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton radioButton = findViewById(reasonBinding.rgReason.getCheckedRadioButtonId());
                if (radioButton != null) {

                    setDeleteAccount(userId, radioButton.getText().toString());
                } else {
                    AppConstants.setToastStr(nActivity, getString(R.string.please_select_reason));
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


    private void setDeleteAccount(String userId, String reason) {
        final Dialog progresDialog11 = showProgress(nActivity);
        if (progresDialog11 != null && !progresDialog11.isShowing()) {
            progresDialog11.show();
        }
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";

                URL = AppConstants.MAIN_URL + "delete_account.php";
                Log.e("URL get price", "== " + URL);


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = null, ReasonPair = null;

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
                    if (result != null) {
                        JSONObject obj = new JSONObject(result);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            AppConstants.setToastStr(nActivity, "" + obj.getString("message"));
                            SharedPreferences.Editor editor = prefUpdate.edit();
                            editor.clear().apply();
                            editor.commit();

                            Intent intLogin = new Intent(nActivity, LoginActivity.class);
                            startActivity(intLogin);
                            finishAffinity();
                        } else {
                            AppConstants.setToastStr(nActivity, "" + obj.getString("message"));
                        }
                        progresDialog11.dismiss();
                    }
                } catch (Exception t) {
                    Log.d("ERRRR", t.toString());
                    progresDialog11.dismiss();
                }
                progresDialog11.dismiss();
                finish();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
