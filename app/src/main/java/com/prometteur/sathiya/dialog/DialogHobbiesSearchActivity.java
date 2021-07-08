package com.prometteur.sathiya.dialog;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import com.prometteur.sathiya.BaseActivity;

import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanHobby;
import com.prometteur.sathiya.databinding.DialogAddPhotoBinding;
import com.prometteur.sathiya.databinding.DialogSearchHobbyBinding;
import com.prometteur.sathiya.hobbies.HobbiesInterestActivity;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
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

public class DialogHobbiesSearchActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener{
   DialogSearchHobbyBinding fieldBinding;
    SharedPreferences prefUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fieldBinding=DialogSearchHobbyBinding.inflate(getLayoutInflater());
        setContentView(fieldBinding.getRoot());
        fieldBinding.dynamicSeekbar.setSeekBarChangeListener(this);
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(DialogHobbiesSearchActivity.this);
        strLang = prefUpdate.getString("selLang", "");
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.arr_hobbies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldBinding.spHobbies.setAdapter(adapter);
        fieldBinding.tvHobbies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fieldBinding.spHobbies.performClick();
            }
        });
        fieldBinding.spHobbies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fieldBinding.tvHobbies.setText(""+fieldBinding.spHobbies.getSelectedItem().toString());
                if(arrHobby.size()>0) {
                    HobbiesInterestActivity.hobbyId = arrHobby.get(i).getHobby_id();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (NetworkConnection.hasConnection(this)) {
            getHobbyList();
        } else {
            AppConstants.CheckConnection(this);
        }
    }

    public void closeDialog(View view)
    {
        finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        fieldBinding.dynamicSeekbar.setInfoText(""+i + " KM",i);
        HobbiesInterestActivity.km=""+i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
List<beanHobby> arrHobby=new ArrayList<>();
List<String> strArrHobby=new ArrayList<>();
    private void getHobbyList() {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "hobbies_title.php";
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(languagePAir);

                try {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
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

                Log.e("--City --", "==" + Ressponce);

                try {
                    arrHobby = new ArrayList<beanHobby>();
                    JSONObject responseObj = new JSONObject(Ressponce);
                    JSONObject responseData = responseObj.getJSONObject("responseData");


                    if (responseData.has("1")) {
                        Iterator<String> resIter = responseData.keys();

                        while (resIter.hasNext()) {

                            String key = resIter.next();
                            JSONObject resItem = responseData.getJSONObject(key);

                            String hobyId = resItem.getString("hoby_id");
                            String hobyName = resItem.getString("title"); //hobby_name

                            arrHobby.add(new beanHobby(hobyId, hobyName));
                            strArrHobby.add(hobyName);
                        }

                        if (arrHobby.size() > 0) {
                            /*Collections.sort(arrHobby, new Comparator<beanHobby>() {
                                @Override
                                public int compare(beanHobby lhs, beanHobby rhs) {
                                    return lhs.getHobby_name().compareTo(rhs.getHobby_name());
                                }
                            });*/
                            ArrayAdapter adapter =new ArrayAdapter(DialogHobbiesSearchActivity.this, android.R.layout.simple_spinner_item,strArrHobby);
                            adapter.setDropDownViewResource(
                                    android.R.layout.simple_spinner_dropdown_item);
                            fieldBinding.spHobbies.setAdapter(adapter);
                        }

                        resIter = null;

                    }

                    responseData = null;
                    responseObj = null;


                } catch (Exception e) {

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }
}
