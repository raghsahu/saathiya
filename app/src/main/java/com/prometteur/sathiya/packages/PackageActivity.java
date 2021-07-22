package com.prometteur.sathiya.packages;

import com.prometteur.sathiya.BaseActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.SignUpStep1Activity;
import com.prometteur.sathiya.adapters.PlansAdapter;
import com.prometteur.sathiya.adapters.UserVisitedListAdapter;
import com.prometteur.sathiya.beans.beanPackages;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.databinding.ActivityPackageBinding;
import com.prometteur.sathiya.utills.AppConstants;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class PackageActivity extends BaseActivity implements PaymentResultWithDataListener {

    ActivityPackageBinding packageBinding;
    BaseActivity nActivity=PackageActivity.this;
    String calls="";
    float price=0.0f;
    SharedPreferences prefUpdate;
    String matri_id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageBinding=ActivityPackageBinding.inflate(getLayoutInflater());
        setContentView(packageBinding.getRoot());
        packageBinding.toolBar.ivSearchIcon.setVisibility(View.GONE);
        packageBinding.toolBar.toolBarTitle.setText(getString(R.string.package1));
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", "");
        strLang=prefUpdate.getString("selLang","English");
packageBinding.dynamicSeekbar.setSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        packageBinding.dynamicSeekbar.setInfoText(""+i+" number of calls",i);
        calls=""+i;

        for (beanPackages package1:packages) {
            float startVal=Float.parseFloat(package1.getPlanCallLimit().split("-")[0]);
            float endVal=Float.parseFloat(package1.getPlanCallLimit().split("-")[1]);
            if(startVal<=i && endVal>=i)
            {
                float pricePerCall=Float.parseFloat(package1.getPlanCallRate());
                packageBinding.tvPrice.setText(i+" calls *"+" ₹"+pricePerCall+" per call = ₹"+i*pricePerCall);
                price=(i*pricePerCall);
                Log.e("callprice",""+pricePerCall);

            }
        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
});
packageBinding.toolBar.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        finish();
    }
});
        packageBinding.tvCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("code", ""+packageBinding.tvCode.getText().toString());
                clipboard.setPrimaryClip(clip);
                AppConstants.setToastStr(PackageActivity.this,getString(R.string.copied));
            }
        });
        packageBinding.imgCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("code", ""+packageBinding.tvCode.getText().toString());
                clipboard.setPrimaryClip(clip);
                AppConstants.setToastStr(PackageActivity.this,getString(R.string.copied));
            }
        });
        getAllPackages();
        packageBinding.buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!calls.isEmpty() && !calls.equalsIgnoreCase("0")) {
                    startPayment();
                }else
                {
                    AppConstants.setToastStr(nActivity,getString(R.string.please_select_calls));
                }
               /* if(packageBinding.buyNow.getText().toString().equalsIgnoreCase("Get Price")){*/

               /* }else
                {
                    getSetPriceNCalls(matri_id,calls,price,"","");
                }*/
            }
        });
        packageBinding.edtNoOfCalls.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty()) {
                    int totCalls = 0;
                    try {
                        totCalls = currentCalls;
                    } catch (Exception e) {
                        totCalls = 0;
                        e.printStackTrace();
                    }
                    int enteredCalls = Integer.parseInt(charSequence.toString());
                    if(totCalls>=enteredCalls) {
                        packageBinding.tvBalanceCalls.setText(getString(R.string.balance) + " - " + (totCalls - enteredCalls) + " " + getString(R.string.calls));
                    }else{
                        AppConstants.setToastStr(nActivity,getString(R.string.calls_not_available)+" "+currentCalls+" "+getString(R.string.so_enter_within_that));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        packageBinding.sendCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(packageBinding.edtMobileNo.getText().toString().length()!=10){
                    packageBinding.edtMobileNo.requestFocus();
                    AppConstants.setToastStr(nActivity,getString(R.string.please_enter_valid_mobile_number));
                }else if(packageBinding.edtNoOfCalls.getText().toString().isEmpty() ){
                    packageBinding.edtNoOfCalls.requestFocus();
                    AppConstants.setToastStr(nActivity,getString(R.string.please_enter_calls));
                }else
                {
                    if(currentCalls>=Integer.parseInt(packageBinding.edtNoOfCalls.getText().toString())) {
                        sendCallToOther(matri_id, packageBinding.edtNoOfCalls.getText().toString());
                    }else
                    {
                        packageBinding.edtNoOfCalls.requestFocus();
                        AppConstants.setToastStr(nActivity,getString(R.string.calls_not_available)+" "+currentCalls+" "+getString(R.string.so_enter_within_that));
                    }
                }
            }
        });
    }
List<beanPackages> packages=new ArrayList<>();
    int currentCalls=0;
    int endVal=300;
    private void getAllPackages() {
        /*final ProgressDialog progresDialog11 = new ProgressDialog(nActivity);
        progresDialog11.setCancelable(false);
        progresDialog11.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog11.setIndeterminate(true);*/
        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {


                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";

                    URL = AppConstants.MAIN_URL + "call_packages.php";
                    Log.e("URL Call packages", "== " + URL);


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = null;

                    MatriIdPair = new BasicNameValuePair("matri_id", matri_id);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);

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

                Log.e("get all calls", "==" + result);

                try {
                    if (result!=null) {
                        JSONObject obj = new JSONObject(result);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            packages = new ArrayList<beanPackages>();
                            JSONObject responseData = obj.getJSONObject("responseData");


                            if (responseData.has("1")) {


                                Iterator<String> resIter = responseData.keys();
                                while (resIter.hasNext()) {

                                    String key = resIter.next();
                                    JSONObject resItem = responseData.getJSONObject(key);
                                    packages.add(new beanPackages(resItem.getString("plan_id"),
                                            resItem.getString("plan_call_limit"),
                                            resItem.getString("plan_call_rate")));
                                }
                                packageBinding.cvPlans.setVisibility(View.GONE);
                                if(packages.size()>0){
                                    packageBinding.cvPlans.setVisibility(View.VISIBLE);
                                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(PackageActivity.this, RecyclerView.VERTICAL,false);
                                    packageBinding.rvPlans.setLayoutManager(linearLayoutManager);
                                    packageBinding.rvPlans.setAdapter(new PlansAdapter(PackageActivity.this, (ArrayList<beanPackages>) packages));
                                }

                            }

                            if(obj.getString("end_val")!=null && !obj.getString("end_val").equalsIgnoreCase("null"))
                            {
                                endVal=Integer.parseInt(obj.getString("end_val"));
                            }
                            packageBinding.dynamicSeekbar.setMax(endVal);
                            JSONObject currentPackage = obj.getJSONObject("current_package");
                            if(currentPackage!=null)
                            {
                                currentCalls=Integer.parseInt(currentPackage.getString("calls"));
                                packageBinding.tvPrice.setText(currentPackage.getString("calls")+" "+getString(R.string.calls));
                                packageBinding.tvBalanceCalls.setText(getString(R.string.balance)+" - "+currentPackage.getString("calls")+" "+getString(R.string.calls));
                                if(currentCalls>0){
                                    packageBinding.cvBorrowCalls.setVisibility(View.VISIBLE);
                                }else
                                {
                                    packageBinding.cvBorrowCalls.setVisibility(View.GONE);
                                }
                            }
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

    public void startPayment() {
        final Activity activity = this;
        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", calls+" Calls "+matri_id);
            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "https://thegreentech.in/premium-matri-demo/img/thegreentech-upgraded.png");
            options.put("image", getResources().getDrawable(R.drawable.ic_wedding_rings));
            options.put("currency", "INR");
            options.put("amount", String.valueOf(Math.round(price) * 100));

            /*JSONObject preFill = new JSONObject();
            preFill.put("email", "test@razorpay.com");
            preFill.put("contact", "9876543210");

            options.put("prefill", preFill);*/

            co.open(activity, options);
        } catch (Exception e) {
            //Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void getSetPriceNCalls(String strMatriId,String calls,String price,String orderid,String transactionid) {
       /* final ProgressDialog progresDialog11 = new ProgressDialog(nActivity);
        progresDialog11.setCancelable(false);
        progresDialog11.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog11.setIndeterminate(true);*/
        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsMatriId = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";

                    URL = AppConstants.MAIN_URL + "mark_payment_success.php";
                    Log.e("URL get price", "== " + URL);


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = null;

                    MatriIdPair = new BasicNameValuePair("matri_id", paramsMatriId);

                BasicNameValuePair CallsPair = new BasicNameValuePair("calls", calls);
                BasicNameValuePair PricePair = new BasicNameValuePair("price", price);
                BasicNameValuePair OrderIdPair = new BasicNameValuePair("orderid", orderid);
                BasicNameValuePair TransactionIdPair = new BasicNameValuePair("transactionid", transactionid);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(CallsPair);
                nameValuePairList.add(PricePair);
                nameValuePairList.add(OrderIdPair);
                nameValuePairList.add(TransactionIdPair);

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
                            editor.putString("call_package_status",obj.getString("call_package_status"));
                            editor.apply();
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
        sendPostReqAsyncTask.execute(strMatriId);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
Log.e("Payment",""+paymentData+" S "+s);
        if(!calls.isEmpty()){
            getSetPriceNCalls(matri_id,calls,""+Math.round(price),"",s);
        }else {
            AppConstants.setToastStr(nActivity,getString(R.string.please_select_calls));
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.e("Payment",""+paymentData+" S "+s);
        try {
//            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
            builder.setCancelable(false);
            builder.setMessage(getString(R.string.your_payment_failed));
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError", e);
        }
    }



    private void sendCallToOther(String strMatriId,String calls) {
        /*final ProgressDialog progresDialog11 = new ProgressDialog(nActivity);
        progresDialog11.setCancelable(false);
        progresDialog11.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog11.setIndeterminate(true);*/
        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsMatriId = params[0];
                String paramsCalls = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";

                URL = AppConstants.MAIN_URL + "gift_calls.php";
                Log.e("URL get price", "== " + URL);


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = null;

                MatriIdPair = new BasicNameValuePair("login_matri_id", paramsMatriId);
              BasicNameValuePair CallsPair = new BasicNameValuePair("calls", paramsCalls);
              BasicNameValuePair MobilePair = new BasicNameValuePair("mobile", packageBinding.edtMobileNo.getText().toString());
              BasicNameValuePair LangPair = new BasicNameValuePair("lang", strLang);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(CallsPair);
                nameValuePairList.add(MobilePair);
                nameValuePairList.add(LangPair);

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

                            packageBinding.edtMobileNo.setText("");
                            packageBinding.edtNoOfCalls.setText("");
                            AppConstants.setToastStr(nActivity,""+obj.getString("message"));
                            packageBinding.tvPrice.setText((currentCalls-Integer.parseInt(packageBinding.edtNoOfCalls.getText().toString()))+" calls");
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
        sendPostReqAsyncTask.execute(strMatriId,calls);
    }
}