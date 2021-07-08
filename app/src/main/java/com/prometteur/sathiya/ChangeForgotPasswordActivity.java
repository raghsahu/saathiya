package com.prometteur.sathiya;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.prometteur.sathiya.databinding.ActivityChangeForgotPasswordBinding;
import com.prometteur.sathiya.databinding.ActivityChangePasswordBinding;
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

import static com.prometteur.sathiya.SignUpStep1Activity.strPassword;
import static com.prometteur.sathiya.SignUpStep1Activity.strUsername;
import static com.prometteur.sathiya.fragments.FragmentOtpVerificationBottomSheetDialog.fromPage;
import static com.prometteur.sathiya.utills.AppConstants.isValidPassword;
import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppMethods.getResizedDrawable;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

//import android.support.v7.app.BaseActivity;

public class ChangeForgotPasswordActivity extends BaseActivity {


    //ProgressDialog progresDialog;
    ActivityChangeForgotPasswordBinding passwordBinding;
BaseActivity nActivity=this;
    SharedPreferences prefUpdate;
    String mobileNo = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passwordBinding= ActivityChangeForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(passwordBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        mobileNo = prefUpdate.getString("mobileNo", "");
        getResizedDrawable(nActivity,R.drawable.ic_new_pass,null,null,passwordBinding.edtNewPassword,R.dimen._11sdp);
        getResizedDrawable(nActivity,R.drawable.ic_conf_new_pass,null,null,passwordBinding.edtConfPassword,R.dimen._11sdp);
        passwordBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });


        passwordBinding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(passwordBinding.btnDone.getWindowToken(), 0);
                String newPassword = passwordBinding.edtNewPassword.getText().toString().trim();
                String conPassword = passwordBinding.edtConfPassword.getText().toString().trim();

               if(newPassword.equalsIgnoreCase("")) {
                    //edtEmailId.setError(getResources().getString(R.string.Please_enter_username));
                   setToastStr(ChangeForgotPasswordActivity.this,getResources().getString(R.string.please_enter_new_password));
                }else if(conPassword.equalsIgnoreCase("")) {
                    //edtEmailId.setError(getResources().getString(R.string.Please_enter_username));
                   setToastStr(ChangeForgotPasswordActivity.this, getResources().getString(R.string.please_enter_confirm_password));
                } else if (!newPassword.equalsIgnoreCase(conPassword)) {
                   setToastStr(ChangeForgotPasswordActivity.this, getResources().getString(R.string.password_does_not_match));
                } else if(!isValidPassword(newPassword)) {
                   setToastStr(ChangeForgotPasswordActivity.this, getResources().getString(R.string.enter_password_with_special_characters));
                    passwordBinding.edtNewPassword.requestFocus();
                }else if(!isValidPassword(conPassword)) {
                   setToastStr(ChangeForgotPasswordActivity.this, getResources().getString(R.string.enter_password_with_special_characters));
                    passwordBinding.edtConfPassword.requestFocus();
                } else {
                    getChangePasswordRequest(newPassword);
                }
            }
        });


    }




    private void getChangePasswordRequest(String newPass) {
        /*progresDialog = new ProgressDialog(ChangeForgotPasswordActivity.this);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);*/
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "change_forgot_password.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPAir = new BasicNameValuePair("mob_no", mobileNo);
                BasicNameValuePair NewPassPAir = new BasicNameValuePair("new_password", newPass);

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(MatriIdPAir);
                nameValuePairList.add(NewPassPAir);

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
                    String msgError = responseObj.getString("message");
                    if (status.equalsIgnoreCase("1")) {


                        setToastStr(nActivity, ""+msgError);
                        passwordBinding.edtNewPassword.clearFocus();
                        passwordBinding.edtConfPassword.clearFocus();

                        passwordBinding.edtNewPassword.setText("");
                        passwordBinding.edtConfPassword.setText("");
                        SignUpStep1Activity.strUsername =mobileNo;
                        SignUpStep1Activity.strPassword=newPass;
                        Intent intLogin = new Intent(ChangeForgotPasswordActivity.this, LoginActivity.class);
                        if(strUsername!=null) {
                            intLogin.putExtra("strUsername", strUsername);
                            intLogin.putExtra("strPassword", strPassword);
                        }
                        startActivity(intLogin);
                        finishAffinity();
                    } else {

                        progresDialog.dismiss();
                        setToastStr(nActivity, ""+msgError);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    progresDialog.dismiss();
                } finally {

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }






}
