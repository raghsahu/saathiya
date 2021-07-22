package com.prometteur.sathiya.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.prometteur.sathiya.BaseActivity;
import com.prometteur.sathiya.LoginActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.CardStackAdapter;
import com.prometteur.sathiya.adapters.RecycleImagesAdapter;
import com.prometteur.sathiya.databinding.DialogPlayVideoBinding;
import com.prometteur.sathiya.databinding.DialogReasonBinding;
import com.prometteur.sathiya.home.MusicService;
import com.prometteur.sathiya.translateapi.Http;
import com.prometteur.sathiya.translateapi.MainViewModel;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import darren.googlecloudtts.GoogleCloudTTS;
import darren.googlecloudtts.GoogleCloudTTSFactory;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.prometteur.sathiya.SplashActivity.strLangCode;
import static com.prometteur.sathiya.home.MusicService.mPlayer;
import static com.prometteur.sathiya.home.SecondHomeActivity.activityRunning;
import static com.prometteur.sathiya.home.SecondHomeActivity.mMainViewModel;
import static com.prometteur.sathiya.profile.ProfileActivity.resItem;
import static com.prometteur.sathiya.utills.AppConstants.isNotification;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class DialogPlayVideoActivity extends BaseActivity {
   DialogPlayVideoBinding profileBinding;
BaseActivity nActivity= DialogPlayVideoActivity.this;
    SharedPreferences prefUpdate;
    String userId="",gender;
    CountDownTimer countDownTimer;
    int current=0;
    long milliLeft=0;
    List<String> stringData;
    boolean paused = false;
    boolean repeat = false;
    public boolean activityRunning = false;
    public boolean voiceCall = true;
    public MainViewModel mMainViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileBinding=DialogPlayVideoBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        userId = prefUpdate.getString("user_id", "");
        gender = prefUpdate.getString("gender", "");
        strLangCode = prefUpdate.getString("selLangCode", "en");
        activityRunning = true;
        GoogleCloudTTS googleCloudTTS = GoogleCloudTTSFactory.create("AIzaSyCacicvhIfLWPSlmZxL-dG0nwbx5yo8lCI");
        mMainViewModel = new MainViewModel(nActivity.getApplication(), googleCloudTTS);

        try {
            if(resItem.getString("gender")!=null){
                gender=resItem.getString("gender");
            }
            if(gender.equalsIgnoreCase("Male")) {
                profileBinding.videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_male));
            }else{
                profileBinding.videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_female));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        profileBinding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setScreenOnWhilePlaying(true);
            }
        });

        stringData = new ArrayList<>();

        //ArrayList<String> fillerTextList=new ArrayList<>();
        JSONArray fillerTextArr= null;
        try {
            fillerTextArr = new JSONArray(resItem.getString("filler"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //stringData.add("");// for initial filler
        for(int i1=0;i1<fillerTextArr.length();i1++) {
            JSONObject hobbiesPhotoObj= null;
            try {
                hobbiesPhotoObj = fillerTextArr.getJSONObject(i1);
                String textFiller = hobbiesPhotoObj.getString("text");
                stringData.add(textFiller);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        /*stringData.add("Blank");
        stringData.add("Mera naam Sumit Dhara hai");
        stringData.add("Mein iss app pe apna Saathiya dhoondne aaya hoon");
        stringData.add("Mien orisa ka rahne wala hoon");
        stringData.add("Blank");
        stringData.add("Meri umar 38 saal hai");
        stringData.add("Blank");
        stringData.add("Mera kad 5 ft 10 inch hai");
        stringData.add("Par koshish karunga tumhare liye tare tod laaun");
        stringData.add("Mein shakahari hoon");
        stringData.add("Mein ghar pe bana kuch bhi khaa leta hoon");
        stringData.add("Mere ghar pe Hindi boli jaati h");
        stringData.add("Lekin hum pyaar ki bhasha jaldi samajh jaate h ya fir papa ki pitayi");
        stringData.add("Mien 12th tak pada hoon");
        stringData.add("Blank");
        stringData.add("Mein plumber hoon");
        stringData.add("Or zindagi mein m kuch bada karna chahta hoon");
        stringData.add("Meri maasik aay 10 haazaar hai");
        stringData.add("Lekin khushiya bonus mein");
        stringData.add("What I am looking for?");
        stringData.add("Agar apko mera profile aacha laga ho to mujhe jarror sampark kijiye.");*/
        milliLeft=0;
        profileBinding.ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //getViewVideo(singleUser.getMatri_id());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (paused) {
                    if (mPlayer != null) {
                        mPlayer.pause();
                    }
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    paused = false;
                    profileBinding.ivPlayPause.setImageResource(R.drawable.ic_play_circle_white);
                    profileBinding.ivPlayPause.setVisibility(View.VISIBLE);
                    /*drawable = profileBinding.imageview.getDrawable();
                    if (drawable instanceof Animatable) {
                        ((Animatable) drawable).stop();
                    }*/


                    if(countDownTimer!=null) {
                        countDownTimer.cancel();
                    }


                    if (profileBinding.videoView.isPlaying()) {
                        profileBinding.videoView.pause();
                    }
                   // showHideView(holder, paused);
                } else {
                    paused = true;
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    if (current == 0) {
                        // loadjpgs();
                        if (!repeat) {
                            repeat = true;
                            startService(new Intent(DialogPlayVideoActivity.this, MusicService.class));
                        }
                    }
                    if (mPlayer == null) {
                        mPlayer = new MediaPlayer();
                    }
                    try {
                        mPlayer.start();
                    }catch (Exception e){e.printStackTrace();}
                    profileBinding.ivPlayPause.setImageResource(R.drawable.ic_pause_circle_white);
                    profileBinding.ivPlayPause.setVisibility(View.GONE);

                   /* if (drawable instanceof Animatable) {
                        ((Animatable) drawable).start();
                    }*/


                    if (!profileBinding.videoView.isPlaying()) {
                        profileBinding.videoView.start();
                    }


                   // showHideView(holder, paused);
                    if(current==21){
                        current=0;
                    }
                    setViews(stringData, (int) current);
                }



            }
        });

        profileBinding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countDownTimer!=null) {
                    countDownTimer.cancel();
                }
                if (MusicService.mPlayer == null) {
                    MusicService.mPlayer = new MediaPlayer();
                }
                MusicService.mPlayer.stop();
                stopService(new Intent(nActivity, MusicService.class));
                finish();
            }
        });
    }


    private void setViews(final List<String> arraylist, int pos) {
        profileBinding.tvDetail.setVisibility(View.VISIBLE);
        profileBinding.videoView.setVisibility(View.VISIBLE);
        ArrayList<Long> longArrayList=new ArrayList<>();
        longArrayList.add(3000L);//holder.stringData.add("Blank");
        longArrayList.add(4000L);//holder.stringData.add("Mera naam Sumit Dhara hai");
        longArrayList.add(4000L);//holder.stringData.add("Mein iss app pe apna Saathiya dhoondne aaya hoon");
        longArrayList.add(4000L);//holder.stringData.add("Mien orisa ka rahne wala hoon");
        longArrayList.add(5000L);//holder.stringData.add("Blank");
        longArrayList.add(7000L);//holder.stringData.add("Meri umar 38 saal hai");
        longArrayList.add(5000L);//holder.stringData.add("Blank");
        longArrayList.add(4000L);//holder.stringData.add("Mera kad 5 ft 10 inch hai");
        longArrayList.add(3000L);//holder.stringData.add("Par koshish karunga tumhare liye tare tod laaun");
        longArrayList.add(4000L);//holder.stringData.add("Mein shakahari hoon");
        longArrayList.add(4000L);//holder.stringData.add("Mein ghar pe bana kuch bhi khaa leta hoon");
        longArrayList.add(4000L);//holder.stringData.add("Mere ghar pe Hindi boli jaati h");
        longArrayList.add(5000L);//holder.stringData.add("Lekin hum pyaar ki bhasha jaldi samajh jaate h ya fir papa ki pitayi");
        longArrayList.add(4000L);//holder.stringData.add("Mien 12th tak pada hoon");
        longArrayList.add(5000L);//holder.stringData.add("Blank");
        longArrayList.add(6000L);//holder.stringData.add("Mein plumber hoon");
        longArrayList.add(6000L);//holder.stringData.add("Or zindagi mein m kuch bada karna chahta hoon");
        longArrayList.add(5000L);//holder.stringData.add("Meri maasik aay 10 haazaar hai");
        longArrayList.add(3000L);//holder.stringData.add("Lekin khushiya bonus mein");
        longArrayList.add(3000L);//holder.stringData.add("What I am looking for?");
        longArrayList.add(4000L);//holder.stringData.add("Agar apko mera profile aacha laga ho to mujhe jarror sampark kijiye.");
        final int[] poss = {pos};
        /*if(milliLeft>0)
        {
           longArrayList.set(poss[0],milliLeft);
            milliLeft=0;
        }*/
        countDownTimer = new CountDownTimer(longArrayList.get(poss[0]), 1000) {
            @Override
            public void onTick(long l) {
                //milliLeft=l;
                if(!arraylist.get(poss[0]).isEmpty() && !arraylist.get(poss[0]).equalsIgnoreCase("blank")) {
                    translateText(arraylist.get(poss[0]),gender);
                }else {
                    profileBinding.tvDetail.setText(arraylist.get(poss[0]));
                }
            }

            @Override
            public void onFinish() {
                poss[0]++;
                current=poss[0];
                if(arraylist.size()==poss[0]){

                    /*if(imguris.size()>0) {
                        profileBinding.recycler.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,profileBinding.videoView.getHeight());
                        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        profileBinding.recycler.setLayoutParams(params);
                        adapter = new RecycleImagesAdapter(nContext, imguris, new RecycleImagesAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                profileBinding.ivPlayPause.setVisibility(View.VISIBLE);
                                playPauseHandler.postDelayed(playPauserunnable, 2000);
                            }
                        });
                        profileBinding.recycler.setLayoutManager(new LinearLayoutManager(nContext, LinearLayoutManager.HORIZONTAL, false));
                        profileBinding.recycler.setAdapter(adapter);
                        loadjpgs();
                    }else{
                        profileBinding.recycler.setVisibility(View.GONE);
                    }*/
                    profileBinding.tvDetail.setVisibility(View.GONE);
                    //profileBinding.videoView.setVisibility(View.GONE);

                    profileBinding.ivPlayPause.setVisibility(View.VISIBLE);
                    paused = false;
                    profileBinding.ivPlayPause.setImageResource(R.drawable.ic_play_circle_white);
                    if (MusicService.mPlayer == null) {
                        MusicService.mPlayer = new MediaPlayer();
                    }
                    MusicService.mPlayer.stop();
                    stopService(new Intent(nActivity, MusicService.class));
                    repeat=false;
                    /*if(imguris.size()==0) {


                        showHideView(holder, paused);
                    }*/
                }else {
                    voiceCall=true;
                    setViews(arraylist, poss[0]);
                }
            }
        }.start();

    }

    private void translateText(String input,String gender) {
        if(strLangCode.equalsIgnoreCase("en")){
            profileBinding.tvDetail.setText(input);
            // txt_hindi.animateText(transObject2.getString("translatedText"));
            if (voiceCall) {
                voiceCall = false;
                if (NetworkConnection.hasConnection(nActivity)) {
                    speakOut(input, gender);
                } else {
                   // AppConstants.CheckConnection(nActivity);
                }

            }
        }else {
            Http.post(input, "en", strLangCode, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {


                    try {
                        JSONObject serverResp = new JSONObject(response.toString());
                        JSONObject jsonObject = serverResp.getJSONObject("data");
                        JSONArray transObject = jsonObject.getJSONArray("translations");
                        JSONObject transObject2 = transObject.getJSONObject(0);

                        if (activityRunning) {
                            profileBinding.tvDetail.setText(transObject2.getString("translatedText"));
                            // txt_hindi.animateText(transObject2.getString("translatedText"));
                            if (voiceCall) {
                                voiceCall = false;
                                if (NetworkConnection.hasConnection(nActivity)) {
                                    speakOut(transObject2.getString("translatedText"), gender);
                                } else {
                                    //AppConstants.CheckConnection(nActivity);
                                }

                            }
                        }


                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            });
        }
    }

    private void speakOut(String text,String gender) {
        mMainViewModel.speak(text)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(t -> initTTSVoice(gender))
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        // makeToast("Speak success", false);
                        // mHandler.removeCallbacks(characterAdder);
                        // mHandler.postDelayed(characterAdder, text.length() * 500);

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Toast.makeText(nActivity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        //makeToast("Speak failed " + e.getMessage(), true);
                        Log.e("speak_failed ", "Speak failed", e);
                    }
                });
    }

    private void initTTSVoice(String gender) {
        //https://cloud.google.com/text-to-speech/docs/voices

        //India Hindi voice
        String languageCode = "hi-IN";
        String voiceName;
        if (gender.equalsIgnoreCase("male")) {
            voiceName = "hi-IN-Standard-B";
        } else {
            voiceName = "hi-IN-Standard-A";
        }

        float pitch = ((float) (0)); //between -20.0 to 20.0
        float speakRate = ((float) (75 + 25) / 100);//75


        mMainViewModel.initTTSVoice(languageCode, voiceName, pitch, speakRate);
    }


    @Override
    public void onResume() {
        try {
            activityRunning = true;
            mMainViewModel.resume();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onResume();

    }

    @Override
    protected void onPause() {
        try {
            activityRunning = false;
            mMainViewModel.pause();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        if (MusicService.mPlayer == null) {
            MusicService.mPlayer = new MediaPlayer();
        }
        try {
            MusicService.mPlayer.stop();
        }catch (Exception e){e.printStackTrace();}
        stopService(new Intent(nActivity, MusicService.class));
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        activityRunning = false;
        mMainViewModel.dispose();
        if(countDownTimer!=null) {
            countDownTimer.cancel();
        }
        if (MusicService.mPlayer == null) {
            MusicService.mPlayer = new MediaPlayer();
        }
        try {
            MusicService.mPlayer.stop();
        }catch (Exception e){e.printStackTrace();}
        stopService(new Intent(nActivity, MusicService.class));
        super.onDestroy();
    }


    @Override
    protected void onStop() {
        activityRunning = false;
        mMainViewModel.dispose();
        super.onStop();
        if (MusicService.mPlayer == null) {
            MusicService.mPlayer = new MediaPlayer();
        }
        try {
            MusicService.mPlayer.stop();
        }catch (Exception e){e.printStackTrace();}
        stopService(new Intent(nActivity, MusicService.class));
    }


}
