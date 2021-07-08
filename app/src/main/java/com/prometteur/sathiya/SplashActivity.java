package com.prometteur.sathiya;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
//import android.support.annotation.NonNull;
//import android.support.v7.app.BaseActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cunoraz.gifview.library.GifView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.prometteur.sathiya.BaseActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
/*import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;*/

import com.prometteur.sathiya.chat.ChatActivity;
import com.prometteur.sathiya.fragments.FragmentSelectlanguageBottomSheetDialog;
import com.prometteur.sathiya.home.HomeActivity;
import com.prometteur.sathiya.home.ThirdHomeActivity;
import com.prometteur.sathiya.profile.EditProfileActivity;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.LocaleUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

public class SplashActivity extends BaseActivity {
    Button btnLogin, btnSignUp;
    SharedPreferences prefUpdate;
    String user_id = "",matriId="";
public static TextView txt_language;
    public static String strLang="English";
    public static String strLangCode="en";
    LinearLayout linLanguage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        user_id = prefUpdate.getString("user_id", "");
        strLang=prefUpdate.getString("selLang","English");
        matriId=prefUpdate.getString("matri_id","");
        LocaleUtils.setLocale(new Locale(prefUpdate.getString("selLangCode","en")));
        LocaleUtils.updateConfig(MyApp.getInstance(), getResources().getConfiguration());
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        txt_language = (TextView) findViewById(R.id.txt_language);
        linLanguage =  findViewById(R.id.linLanguage);
        FirebaseApp.initializeApp(this);
        getToken();

        if (!user_id.equalsIgnoreCase(""))
        {
            AppConstants.m_id = user_id;
            getSharedPreferences("data", MODE_PRIVATE).edit().putString("isdisplay", "false").apply();
            getSharedPreferences("data", MODE_PRIVATE).edit().putString("ad_display", "false").apply();
            new CountDownTimer(5000,1000){
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    getCheckBasicDetails();
                }
            }.start();


            btnSignUp.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            linLanguage.setVisibility(View.GONE);

        } else {
            btnSignUp.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            linLanguage.setVisibility(View.VISIBLE);
            btnLogin.setText(getString(R.string.Login));
        }

        txt_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentSelectlanguageBottomSheetDialog().show(getSupportFragmentManager(), "Dialog");
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!user_id.equalsIgnoreCase("")) {
//                    Intent intLogin = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intLogin);
//                    finish();
                } else {
                    SignUpStep1Activity.strPassword=null;SignUpStep1Activity.strUsername=null;
                    Intent intLogin = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intLogin);
                }

            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String signup_step = prefUpdate.getString("signup_step", "");
               /* if (signup_step.equalsIgnoreCase("1")) {
//                    Intent intLogin = new Intent(SplashActivity.this, VerifyMobileNumberActivity.class);
//                    startActivity(intLogin);
                } else if (signup_step.equalsIgnoreCase("2")) {
//                    Intent intLogin = new Intent(SplashActivity.this, SignUpStep2Activity.class);
//                    startActivity(intLogin);
//                } else if (signup_step.equalsIgnoreCase("3")) {
//                    Intent intLogin = new Intent(SplashActivity.this, SignUpStep3Activity.class);
//                    startActivity(intLogin);
//                } else if (signup_step.equalsIgnoreCase("4")) {
//                    Intent intLogin = new Intent(SplashActivity.this, SignUpStep4Activity.class);
//                    startActivity(intLogin);
//                } else {
//                    Intent intLogin = new Intent(SplashActivity.this, SignUpStep1Activity.class);
//                    startActivity(intLogin);
                }*/

                        startActivity(new Intent(SplashActivity.this,SignUpStep1Activity.class));

            }
        });

        try {
            Log.e("notifi message", "In SplashActivity");
            Bundle bundle = new Bundle();
            bundle = getIntent().getExtras();

            if (bundle != null) {
                Log.e("notifi message", "" + bundle.getString("name"));
                Log.e("notifi message", "" + bundle.getString("chat_msg"));
                Log.e("notifi message", "" + bundle.getString("sender_id"));
                openActivityOnNotification(bundle);
            }
        }catch (Exception e){
            Log.e("notifi message", "In SplashActivity e");
            e.printStackTrace();
        }

        ImageView gifView1 =  findViewById(R.id.gif1);
       /* gifView1.setVisibility(View.VISIBLE);
        gifView1.play();*/
        // gifView1.pause();
        //gifView1.setGifResource(R.drawable.bg_splash);
        Glide.with(SplashActivity.this)
                .asGif()
                .load(R.drawable.bg_splash)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        resource.setLoopCount(1);
                        resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                //do whatever after specified number of loops complete
                            }
                        });
                        return false;
                    }})
                .into(gifView1);
    }

    private void openActivityOnNotification(Bundle bundle) {
        if(bundle.getString("name").contains("New Message"))
        {
            startActivity(new Intent(SplashActivity.this, ChatActivity.class));
        }else //if(bundle.getString("name").contains("Interest Rejected") ||bundle.getString("name").contains("Interest Received")||bundle.getString("name").contains("Interest accepted") )
        {
            ThirdHomeActivity.matri_id=bundle.getString("sender_id");
            startActivity(new Intent(SplashActivity.this, ThirdHomeActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        txt_language.setText(""+strLang);
    }

    void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                Log.d("TOKEN__", task.getResult().getToken());
                AppConstants.tokan = task.getResult().getToken();
            }
        });
    }


    private void getCheckBasicDetails()
    {
        ProgressDialog progresDialog=new ProgressDialog(SplashActivity.this);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
       // progresDialog.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"check_basic_details.php";
                Log.e("URL", "== "+URL);
                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair UsernamePAir = new BasicNameValuePair("matri_id", matriId);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(UsernamePAir);

                try
                {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    try {
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
                try {
                    if(progresDialog!=null && progresDialog.isShowing()) {
                        progresDialog.dismiss();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                Log.e("--Login --", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);

                    String status=obj.getString("status");

                    if(status.equalsIgnoreCase("1")) {
                        Intent intLogin = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intLogin);
                        finish();
                    }else {
                        startActivity(new Intent(SplashActivity.this, EditProfileActivity.class).putExtra("page","basicDetails"));
                        finish();
                    }

                } catch (Throwable t)
                {
                    t.printStackTrace();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

}
