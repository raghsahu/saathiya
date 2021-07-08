package com.prometteur.sathiya.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prometteur.sathiya.MyApp;
import com.prometteur.sathiya.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import com.prometteur.sathiya.SplashActivity;
import com.prometteur.sathiya.adapters.AdapterLanguages;
import com.prometteur.sathiya.beans.beanLanguage;
import com.prometteur.sathiya.home.HomeActivity;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.LocaleUtils;

import org.json.JSONObject;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.SplashActivity.strLangCode;
import static com.prometteur.sathiya.SplashActivity.txt_language;

public class FragmentSelectlanguageBottomSheetDialog extends BottomSheetDialogFragment {
    AdapterLanguages adapterLanguages;
    String selectedLanguage = "";
    ArrayList<beanLanguage> languages;
    RecyclerView recyclerView_languages;
    SharedPreferences prefUpdate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_language_bottom_sheet_dialog, container, false);
        ImageView img_close = (ImageView) v.findViewById(R.id.img_close);
        Button btnSelected = (Button) v.findViewById(R.id.btnSelected);
        recyclerView_languages = (RecyclerView) v.findViewById(R.id.recyclerView_languages);
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(getActivity());

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocaleUtils.setLocale(new Locale( strLangCode));
                LocaleUtils.updateConfig(MyApp.getInstance(), getResources().getConfiguration());

                getActivity().finishAffinity();
                Intent refresh = new Intent(getActivity(), SplashActivity.class);
                refresh.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(refresh);
                dismiss();
            }
        });

        languages = new ArrayList<>();
getLanguageList();
     /*   languages.add("English");
        languages.add("Marathi");
        languages.add("Hindi");
        languages.add("Bengali");
        languages.add("Kannada");
        languages.add("Malayalam");
        languages.add("Tamil");
        languages.add("Telugu");
        languages.add("Gujarati");
        languages.add("Assamese");
        languages.add("Manipuri");
        languages.add("Oriya");
        languages.add("Nepali");*/

     /* langListCode=new ArrayList();
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


        return v;
    }

    private AdapterLanguages getAdapter() {
        adapterLanguages = new AdapterLanguages(getActivity(), languages) {
            @Override
            public void ItemClick(int pos, String language) {
                selectedLanguage = languages.get(pos).getLanguage();
                SplashActivity.strLang=selectedLanguage;
                strLangCode=languages.get(pos).getLang_code();
                SharedPreferences.Editor editor=prefUpdate.edit();
                editor.putString("selLang",strLang);
                editor.putString("selLangCode",strLangCode);
                editor.apply();
                txt_language.setText(""+strLang);
                recyclerView_languages.setAdapter(getAdapter());



            }
        };
        return adapterLanguages;
    }

    ProgressDialog progresDialog;

    private void getLanguageList()
    {
        progresDialog= new ProgressDialog(getActivity());
        progresDialog.setCancelable(false);
        progresDialog.setMessage(getActivity().getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
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

                    //String status=obj.getString("status");

                    /*if (status.equalsIgnoreCase("1"))
                    {*/
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
                                languages.add(new beanLanguage(lang_id,language,code));
                            }
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    return adapterLanguages.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
                                }
                            });
                            recyclerView_languages.setLayoutManager(gridLayoutManager);
                            recyclerView_languages.setItemAnimator(new DefaultItemAnimator());
                            recyclerView_languages.setAdapter(getAdapter());
                            recyclerView_languages.setHasFixedSize(true);
                        }
                        progresDialog.dismiss();


                   /* }else
                    {

                        progresDialog.dismiss();
                    }*/
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