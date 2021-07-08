package com.prometteur.sathiya.language;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prometteur.sathiya.BaseActivity;

import com.prometteur.sathiya.LoginActivity;
import com.prometteur.sathiya.MyApp;
import com.prometteur.sathiya.R;

import com.prometteur.sathiya.SplashActivity;
import com.prometteur.sathiya.adapters.AdapterLanguages;
import com.prometteur.sathiya.beans.beanLanguage;
import com.prometteur.sathiya.beans.beanSaveSearch;
import com.prometteur.sathiya.databinding.ActivityLanguageBinding;
import com.prometteur.sathiya.home.HomeActivity;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.LocaleUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import static com.prometteur.sathiya.SplashActivity.strLangCode;
import static com.prometteur.sathiya.SplashActivity.txt_language;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;


public class LanguateActivity extends BaseActivity {


    //ProgressDialog progresDialog;
ActivityLanguageBinding languageBinding;
BaseActivity nActivity=this;
    ArrayList<beanLanguage> langList;
    //ArrayList<String> langListCode;
    SharedPreferences prefUpdate;
    String matri_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageBinding=ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(languageBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id=prefUpdate.getString("matri_id","");
        languageBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        langList=new ArrayList();
        getLanguageList();
        /*
        langList.add("English");
        langList.add("Marathi");
        langList.add("Hindi");
        langList.add("Bengali");
        langList.add("Kannada");
        langList.add("Malayalam");
        langList.add("Tamil");
        langList.add("Telugu");
        langList.add("Gujarati");
        langList.add("Assamese");
        langList.add("Manipuri");
        langList.add("Oriya");
        langList.add("Nepali");*/

      /*  langListCode=new ArrayList();
        langListCode.add("en"); //English
        langListCode.add("mr"); //Marathi
        langListCode.add("hi"); //Hindi
        langListCode.add("bn"); //Bengali
        langListCode.add("kn");  //Kannada
        langListCode.add("ml");  //Malayalam
        langListCode.add("ta"); //Tamil
        langListCode.add("te"); //Telugu
        langListCode.add("gu");  //Gujarati
        langListCode.add("as");  //Assamese
        langListCode.add("ma");    //Manipuri
        langListCode.add("or");  //Oriya
        langListCode.add("ne");  //Nepali*/

       /* as-IN
        bn-IN
        en-IN
        gu-IN
        hi-IN
        kn-IN
        kok-IN
        ml-IN
        mr-IN
        ne-NP
        or-IN
        pa-IN
        sa-IN
        ta-IN
        te-IN*/
/*

        for (int i = 0; i <langList.size() ; i++) {
            RadioButton rbn = new RadioButton(this);
            rbn.setId(View.generateViewId());
            rbn.setText(""+langList.get(i));
            rbn.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            rbn.setPadding(10,10,10,10);
            RadioGroup.LayoutParams rprms= new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            rprms.setMargins(10,0,10,10);
            languageBinding.rgLanguage.addView(rbn,rprms);
        }


        languageBinding.rgLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                strLangCode=langListCode.get(i-1);
               LocaleUtils.setLocale(new Locale( strLangCode));
                LocaleUtils.updateConfig(MyApp.getInstance(), getResources().getConfiguration());

//
//        Resources res = mContext.getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        res.updateConfiguration(conf, dm);

            }
        });
*/



    }
    AdapterLanguages adapterLanguages;

    private AdapterLanguages getAdapter() {
        adapterLanguages = new AdapterLanguages(nActivity, langList) {
            @Override
            public void ItemClick(int pos, String language) {
                strLang = langList.get(pos).getLanguage();
                strLangCode=langList.get(pos).getLang_code();
                LocaleUtils.setLocale(new Locale( strLangCode));
                LocaleUtils.updateConfig(MyApp.getInstance(), getResources().getConfiguration());
                SharedPreferences.Editor editor=prefUpdate.edit();
                editor.putString("selLang",strLang);
                editor.putString("selLangCode",strLangCode);
                editor.apply();
                languageBinding.recyclerViewLanguages.setAdapter(getAdapter());
                getUpdateLanguage();
            }
        };
        return adapterLanguages;
    }

    private void getLanguageList()
    {
        /*progresDialog= new ProgressDialog(nActivity);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(nActivity.getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);*/
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"languages.php";
                Log.e("languages", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);



                try
                {
                   /* UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);*/
                  //  Log.e("Parametters Array=", "== "+(nameValuePairList.toString().trim().replaceAll(",","&")));
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

                } catch (Exception uee) //UnsupportedEncodingException
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

                Log.e("seved_search", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);

                   // String status=obj.getString("status");


                      //  arrSaveSearchResultList= new ArrayList<beanSaveSearch>();
                        JSONObject responseData = obj.getJSONObject("responseData");

                        if (responseData.has("1"))
                        {
                            Iterator<String> resIter = responseData.keys();

                            while (resIter.hasNext())
                            {
                                String key = resIter.next();
                                JSONObject resItem = responseData.getJSONObject(key);

                                String lang_id=resItem.getString("lang_id");
                                String language=resItem.getString("language");
                                String code=resItem.getString("code");
                                langList.add(new beanLanguage(lang_id,language,code));
                            }
                            LinearLayoutManager linLayoutManager = new LinearLayoutManager(nActivity, RecyclerView.VERTICAL,false);

                            languageBinding.recyclerViewLanguages.setLayoutManager(linLayoutManager);
                            languageBinding.recyclerViewLanguages.setItemAnimator(new DefaultItemAnimator());
                            languageBinding.recyclerViewLanguages.setAdapter(getAdapter());
                            languageBinding.recyclerViewLanguages.setHasFixedSize(true);

                        }
                        progresDialog.dismiss();



                    progresDialog.dismiss();
                } catch (Exception t)
                {
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
private void getUpdateLanguage()
    {
        /*progresDialog= new ProgressDialog(nActivity);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(nActivity.getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);*/
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"update_user_language.php";
                Log.e("languages", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);
                 BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("matri_id", matri_id);
                BasicNameValuePair UserLanguagePair  = new BasicNameValuePair("language", strLang);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                  nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(UserLanguagePair);

                try
                {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                  //  Log.e("Parametters Array=", "== "+(nameValuePairList.toString().trim().replaceAll(",","&")));
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

                } catch (Exception uee) //UnsupportedEncodingException
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

                Log.e("update_lang", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);

                    String status=obj.getString("status");
if(status.equalsIgnoreCase("1")) {
    finishAffinity();
    Intent refresh = new Intent(nActivity, HomeActivity.class);
    refresh.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    startActivity(refresh);
}

                        progresDialog.dismiss();



                    progresDialog.dismiss();
                } catch (Exception t)
                {
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }


}
