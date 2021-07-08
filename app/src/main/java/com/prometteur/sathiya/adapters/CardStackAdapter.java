package com.prometteur.sathiya.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.chat.ChatActivity;
import com.prometteur.sathiya.chat.FriendsFragment;
import com.prometteur.sathiya.databinding.ItemHomeProfileBinding;
import com.prometteur.sathiya.home.MusicService;
import com.prometteur.sathiya.home.ThirdHomeActivity;
import com.prometteur.sathiya.translateapi.Http;
import com.prometteur.sathiya.translateapi.MainViewModel;
import com.prometteur.sathiya.utills.AppConstants;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.SplashActivity.strLangCode;
import static com.prometteur.sathiya.chat.FriendsFragment.openChatOnce;
import static com.prometteur.sathiya.home.SecondHomeActivity.activityRunning;
import static com.prometteur.sathiya.home.SecondHomeActivity.countDownTimer;
import static com.prometteur.sathiya.home.SecondHomeActivity.isSwiped;
import static com.prometteur.sathiya.home.SecondHomeActivity.mMainViewModel;
import static com.prometteur.sathiya.profile.ProfileActivity.resItem;
import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppConstants.vibe;
import static com.prometteur.sathiya.utills.AppConstants.vibrateBig;
import static com.prometteur.sathiya.utills.AppConstants.vibrateSmall;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;
import static com.prometteur.sathiya.utills.AppMethods.shrinkAnim;

public class CardStackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CardStackAdapter";
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    Context nContext;
    List<beanUserData> mDataList;
    Activity nActivity;
    ArrayList<String> tokans;
    CardStackView cardStackView;
    String RequestType;
    String matri_id = "";
    SharedPreferences prefUpdate;
    CardStackLayoutManager manager;
    FriendsFragment.FragFriendClickFloatButton onClickFloatButton;
    //ProgressDialog progresDialog;
    private OnNotifyDataSetChanged onNotifyDataSetChanged;
    long milliLeft = 0;

    public CardStackAdapter(Activity nActivity, List<beanUserData> mDataList, CardStackView cardStackView, ArrayList<String> tokans, CardStackLayoutManager manager, OnNotifyDataSetChanged onNotifyDataSetChanged) {
        this.nContext = nActivity;
        this.nActivity = nActivity;
        this.mDataList = mDataList;
        this.tokans = tokans;
        this.cardStackView = cardStackView;
        this.manager = manager;
        this.onNotifyDataSetChanged = onNotifyDataSetChanged;
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", ""); //loginMatriId
        onClickFloatButton = new FriendsFragment.FragFriendClickFloatButton();
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        strLang = prefUpdate.getString("selLang", "English");
        strLangCode = prefUpdate.getString("selLangCode", "en");
        activityRunning = true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

           /* View view = LayoutInflater.from(nActivity).inflate(R.layout.recycle_listsaloon_view,
                    parent, false);
            return new SalonListAdapter.ViewHolder(view);*/

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(nActivity).inflate(R.layout.item_home_profile, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(nActivity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
        if (holder1 instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) holder1;
            beanUserData singleUser = mDataList.get(position);
            holder.profileBinding.civRewind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                            .setDirection(Direction.Bottom)
                            .setDuration(Duration.Normal.duration)
                            .build();
                    manager.setRewindAnimationSetting(setting);
                    cardStackView.rewind();
                }
            });
            holder.profileBinding.civInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ThirdHomeActivity.matri_id = singleUser.getMatri_id();
                    nActivity.startActivity(new Intent(nActivity, ThirdHomeActivity.class));
                }
            });
            holder.profileBinding.civCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setContact(matri_id, singleUser.getMatri_id(), singleUser.getUserMobile());
                }
            });
            if (singleUser.getUsername() != null && !singleUser.getUsername().equalsIgnoreCase("null")) {
                holder.profileBinding.tvProfileName.setText("" + singleUser.getUsername());
            }
            holder.profileBinding.tvUserAge.setText("" + singleUser.getHeight());
            holder.profileBinding.tvRefNo.setText("#" + singleUser.getMatri_id());
            holder.profileBinding.tvOccupationSal.setText("" + singleUser.getOcp_name() + "(" + singleUser.getIncome() + ")");
            holder.profileBinding.tvUserAddress.setText("" + singleUser.getCity_name());
            Glide.with(nActivity)
                    .asBitmap()
                    .apply(new RequestOptions()
//                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .thumbnail(0.5f)
//                    .load(R.drawable.bg_pink_card)
                    .load(singleUser.getUser_profile_picture())
                    .placeholder(R.drawable.bg_pink_card)
                    .error(R.drawable.bg_pink_card)
                    .into(holder.profileBinding.rivProfileImage);


            String is_interest = singleUser.getIs_favourite().toString().trim();

            if (is_interest.equalsIgnoreCase("1")) {
                RequestType = "Unlike";
                /*holder.profileBinding.ivLike.setImageResource(R.drawable.ic_heart);
                holder.profileBinding.tvLike.setText("Unlike");*/

                //holder.btnSendInterest.setVisibility(View.GONE);
                //holder.btnRemaind.setVisibility(View.VISIBLE);
            } else {
                RequestType = "Like";
                /*holder.profileBinding.ivLike.setImageResource(R.drawable.ic_heart_greybg);
                holder.profileBinding.tvLike.setText("Like");*/
            }


            holder.profileBinding.civLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RequestType.equalsIgnoreCase("Like")) {
                        String test = singleUser.getIs_blocked().toString();
                        Log.d("TAG", "CHECK =" + test);

                        if (singleUser.getIs_blocked().equalsIgnoreCase("1")) {
                            String msgBlock = "This member has blocked you. You can't express your interest.";
                            String msgNotPaid = "You are not paid member. Please update your membership to express your interest.";

                            AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                            builder.setMessage(msgBlock).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            vibe.vibrate(vibrateBig);
                            shrinkAnim(holder.profileBinding.civLike, nContext);
                            AppConstants.sendPushNotification(tokans.get(position),
                                    AppConstants.msg_express_intress + " " + singleUser.getMatri_id(),
                                    AppConstants.msg_express_intress_title, AppConstants.express_intress_id);
                            sendInterestRequest(matri_id, singleUser.getMatri_id(), singleUser.getIs_favourite(), position, "adapter");
                        }
                    } else if (RequestType.equalsIgnoreCase("Unlike")) {
                        if (singleUser.getIs_blocked().equalsIgnoreCase("1")) {
                            String msgBlock = "This member has blocked you. You can't express your interest.";
                            String msgNotPaid = "You are not paid member. Please update your membership to express your interest.";

                            AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                            builder.setMessage(msgBlock).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            AppConstants.sendPushNotification(tokans.get(position),
                                    AppConstants.msg_express_intress + " " + singleUser.getMatri_id(),
                                    AppConstants.msg_send_reminder_title, AppConstants.express_intress_id);
                            vibe.vibrate(vibrateSmall);
                            sendInterestRequestRemind(matri_id, singleUser.getMatri_id(), singleUser.getIs_favourite(), position, "adapter");
                        }
                    }
                }
            });

            holder.profileBinding.civMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openChatOnce = false;
                    Intent activityIntent = new Intent(nActivity, ChatActivity.class);
                    activityIntent.putExtra("friendEmail", singleUser.getEmail());
                    nActivity.startActivity(activityIntent);
                    nActivity.finish();
//                   onClickFloatButton.findIDEmail(singleUser.getEmail(),nActivity);
                }
            });

//new added integration of video


            holder.animationtypes.add(AnimationUtils.loadAnimation(nActivity, R.anim.bounce_anim));
            holder.animationtypes.add(AnimationUtils.loadAnimation(nActivity, R.anim.bounce_anim));
            holder.animationtypes.add(AnimationUtils.loadAnimation(nActivity, R.anim.bounce_anim));
            holder.animationtypes.add(AnimationUtils.loadAnimation(nActivity, R.anim.bounce_anim));
            holder.animationtypes.add(AnimationUtils.loadAnimation(nActivity, R.anim.bounce_anim));
            holder.animationtypes.add(AnimationUtils.loadAnimation(nActivity, R.anim.bounce_anim));
            holder.animationtypes.add(AnimationUtils.loadAnimation(nActivity, R.anim.bounce_anim));
            holder.animationtypes.add(AnimationUtils.loadAnimation(nActivity, R.anim.bounce_anim));

            holder.typefaces = Typeface.createFromAsset(nActivity.getAssets(), "fonts/Poppins-SemiBold.otf");

            if (mDataList.get(position).getImageUri() != null) {
                holder.imguris.addAll(mDataList.get(position).getImageUri());
            }


            holder.profileBinding.RelRay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.profileBinding.ivPlayPause.setVisibility(View.VISIBLE);
                    holder.playPauseHandler.postDelayed(holder.playPauserunnable, 1000);
                }
            });


                if(singleUser.getGender()!=null && singleUser.getGender().equalsIgnoreCase("Male")) {
                    holder.profileBinding.videoView.setVideoURI(Uri.parse("android.resource://" + nContext.getPackageName() + "/" + R.raw.video));
                }else{
                    holder.profileBinding.videoView.setVideoURI(Uri.parse("android.resource://" + nContext.getPackageName() + "/" + R.raw.video_female));
                }

            holder.profileBinding.videoView.setVisibility(View.GONE);
            holder.profileBinding.tvDetail.setVisibility(View.GONE);
            holder.stringData = new ArrayList<>();
            if (mDataList.get(position).getPhotoPath() != null) {
                //holder.stringData.add("");// for initial filler
                holder.stringData.addAll(mDataList.get(position).getPhotoPath());
            }
            /*holder.stringData.add("Blank");
            holder.stringData.add("Mera naam Sumit Dhara hai");
            holder.stringData.add("Mein iss app pe apna Saathiya dhoondne aaya hoon");
            holder.stringData.add("Mien orisa ka rahne wala hoon");
            holder.stringData.add("Blank");
            holder.stringData.add("Meri umar 38 saal hai");
            holder.stringData.add("Blank");
            holder.stringData.add("Mera kad 5 ft 10 inch hai");
            holder.stringData.add("Par koshish karunga tumhare liye tare tod laaun");
            holder.stringData.add("Mein shakahari hoon");
            holder.stringData.add("Mein ghar pe bana kuch bhi khaa leta hoon");
            holder.stringData.add("Mere ghar pe Hindi boli jaati h");
            holder.stringData.add("Lekin hum pyaar ki bhasha jaldi samajh jaate h ya fir papa ki pitayi");
            holder.stringData.add("Mien 12th tak pada hoon");
            holder.stringData.add("Blank");
            holder.stringData.add("Mein plumber hoon");
            holder.stringData.add("Or zindagi mein m kuch bada karna chahta hoon");
            holder.stringData.add("Meri maasik aay 10 haazaar hai");
            holder.stringData.add("Lekin khushiya bonus mein");
            holder.stringData.add("Agar apko mera profile aacha laga ho to mujhe jarror sampark kijiye.");*/
            milliLeft = 0;
            holder.profileBinding.ivPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        getViewVideo(singleUser.getMatri_id());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    holder.profileBinding.videoView.setVisibility(View.VISIBLE);
                    holder.profileBinding.tvDetail.setVisibility(View.VISIBLE);
                    Glide.with(nActivity)
                            .asBitmap()
                            .override(130, 100)
                            .apply(new RequestOptions()
                                    .fitCenter()
                                    .format(DecodeFormat.PREFER_ARGB_8888)
                                    .override(Target.SIZE_ORIGINAL))
                            .thumbnail(0.5f)
                            .load(R.drawable.bg_pink_card)
//                            .load(singleUser.getUser_profile_picture())
                            .placeholder(R.drawable.bg_pink_card)
                            .error(R.drawable.bg_pink_card)
                            .into(holder.profileBinding.rivProfileImage);

                    if (holder.paused) {
                        holder.paused = false;
                        holder.profileBinding.ivPlayPause.setImageResource(R.drawable.ic_play_circle_white);
                        holder.profileBinding.ivPlayPause.setVisibility(View.VISIBLE);
                        holder.drawable = holder.profileBinding.imageview.getDrawable();
                        if (holder.drawable instanceof Animatable) {
                            ((Animatable) holder.drawable).stop();
                        }


                        holder.countDownTimer.cancel();

                        if (holder.imageIndex < holder.imguris.size()) {
                            holder.handler2.removeCallbacks(holder.runnable2);
                        }

                        if (holder.profileBinding.videoView.isPlaying()) {
                            holder.profileBinding.videoView.pause();
                        }
                        showHideView(holder, holder.paused);
                    } else {
                        holder.paused = true;

                        if (holder.current == 0) {
                            // holder.loadjpgs();
                            if (!holder.repeat) {
                                holder.repeat = true;
                              //  nContext.startService(new Intent(nContext, MusicService.class));
                            }
                        }

                        holder.profileBinding.ivPlayPause.setImageResource(R.drawable.ic_pause_circle_white);
                        holder.profileBinding.ivPlayPause.setVisibility(View.GONE);

                        if (holder.drawable instanceof Animatable) {
                            ((Animatable) holder.drawable).start();
                        }


                        if (!holder.profileBinding.videoView.isPlaying()) {
                            holder.profileBinding.videoView.start();
                        }


                        if (holder.current != 0 && holder.imageIndex != holder.imguris.size()) {
                            //holder.loadjpgs();

                        }


                        showHideView(holder, holder.paused);
                        if (holder.current == 21) {
                            holder.current = 0;
                        }
                        setViews(holder.stringData, holder, holder.current,singleUser);
                    }


                }
            });
            holder.profileBinding.ivVolume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.mute) {
                        holder.mute = false;
                        holder.profileBinding.ivVolume.setImageResource(R.drawable.ic_nosound_icon);
                        AudioManager mAlramMAnager = (AudioManager)nActivity.getSystemService(Context.AUDIO_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                           // mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
                            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
                            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
                        } else {
                           // mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, true);
                            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, true);
                            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                        }
                    } else {
                        holder.profileBinding.ivVolume.setImageResource(R.drawable.ic_volume_up);
                        holder.mute = true;
                        AudioManager mAlramMAnager = (AudioManager)nActivity.getSystemService(Context.AUDIO_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                           // mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
                            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
                            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0);
                            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
                            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
                        } else {
                           // mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, false);
                            mAlramMAnager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, false);
                            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                        }
                    }
                }
            });

        } else if (holder1 instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder1;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    private void setViews(final ArrayList<String> arraylist, ViewHolder holder, int pos,beanUserData userData) {
        holder.profileBinding.tvDetail.setVisibility(View.VISIBLE);
        holder.profileBinding.videoView.setVisibility(View.VISIBLE);
        ArrayList<Long> longArrayList = new ArrayList<>();
        longArrayList.add(3000L);//holder.stringData.add("Blank");
        longArrayList.add(3000L);//holder.stringData.add("Mera naam Sumit Dhara hai");
        longArrayList.add(3000L);//holder.stringData.add("Mein iss app pe apna Saathiya dhoondne aaya hoon");
        longArrayList.add(4000L);//holder.stringData.add("Mien orisa ka rahne wala hoon");
        longArrayList.add(3000L);//holder.stringData.add("Blank");
        longArrayList.add(10000L);//holder.stringData.add("Meri umar 38 saal hai");
        longArrayList.add(2000L);//holder.stringData.add("Blank");
        longArrayList.add(4000L);//holder.stringData.add("Mera kad 5 ft 10 inch hai");
        longArrayList.add(3000L);//holder.stringData.add("Par koshish karunga tumhare liye tare tod laaun");
        longArrayList.add(2000L);//holder.stringData.add("Mein shakahari hoon");
        longArrayList.add(4000L);//holder.stringData.add("Mein ghar pe bana kuch bhi khaa leta hoon");
        longArrayList.add(4000L);//holder.stringData.add("Mere ghar pe Hindi boli jaati h");
        longArrayList.add(2000L);//holder.stringData.add("Lekin hum pyaar ki bhasha jaldi samajh jaate h ya fir papa ki pitayi");
        longArrayList.add(4000L);//holder.stringData.add("Mien 12th tak pada hoon");
        longArrayList.add(2000L);//holder.stringData.add("Blank");
        longArrayList.add(6000L);//holder.stringData.add("Mein plumber hoon");
        longArrayList.add(4000L);//holder.stringData.add("Or zindagi mein m kuch bada karna chahta hoon");
        longArrayList.add(5000L);//holder.stringData.add("Meri maasik aay 10 haazaar hai");
        longArrayList.add(2000L);//holder.stringData.add("Lekin khushiya bonus mein");
        longArrayList.add(2000L);//holder.stringData.add("What I am looking for?");
        longArrayList.add(4000L);//holder.stringData.add("Agar apko mera profile aacha laga ho to mujhe jarror sampark kijiye.");
        final int[] poss = {pos};
        /*if(milliLeft>0)
        {
           longArrayList.set(poss[0],milliLeft);
            milliLeft=0;
        }*/
        if(activityRunning) {
            countDownTimer = holder.countDownTimer;
            if(longArrayList.size()>0) {
                holder.countDownTimer = new CountDownTimer(longArrayList.get(poss[0]), 1000) {
                    @Override
                    public void onTick(long l) {
                        milliLeft = l;
                        if (!arraylist.get(poss[0]).isEmpty() && !arraylist.get(poss[0]).equalsIgnoreCase("blank")) {
                            holder.translateText(arraylist.get(poss[0]), userData.getGender());
                        } else {
                            holder.profileBinding.tvDetail.setText(arraylist.get(poss[0]));
                        }
                    }

                    @Override
                    public void onFinish() {
                        poss[0]++;
                        holder.current = poss[0];
                        if (arraylist.size() == poss[0]) {

                            if (holder.imguris.size() > 0) {
                                holder.profileBinding.recycler.setVisibility(View.VISIBLE);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, holder.profileBinding.videoView.getHeight());
                                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                                holder.profileBinding.recycler.setLayoutParams(params);
                                holder.adapter = new RecycleImagesAdapter(nContext, holder.imguris, new RecycleImagesAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        holder.profileBinding.ivPlayPause.setVisibility(View.VISIBLE);
                                        holder.playPauseHandler.postDelayed(holder.playPauserunnable, 2000);
                                    }
                                });
                                holder.profileBinding.recycler.setLayoutManager(new LinearLayoutManager(nContext, LinearLayoutManager.HORIZONTAL, false));
                                holder.profileBinding.recycler.setAdapter(holder.adapter);
                                holder.loadjpgs();
                            } else {
                                holder.profileBinding.recycler.setVisibility(View.GONE);
                            }
                            Glide.with(nActivity)
                                    .asBitmap()
                                    .apply(new RequestOptions()
//                            .fitCenter()
                                            .format(DecodeFormat.PREFER_ARGB_8888)
                                            .override(Target.SIZE_ORIGINAL))
                                    .thumbnail(0.5f)
//                    .load(R.drawable.bg_pink_card)
                                    .load(userData.getUser_profile_picture())
                                    .placeholder(R.drawable.bg_pink_card)
                                    .error(R.drawable.bg_pink_card)
                                    .into(holder.profileBinding.rivProfileImage);
                            holder.profileBinding.tvDetail.setVisibility(View.GONE);
                            holder.profileBinding.videoView.setVisibility(View.GONE);
                            if (holder.imguris.size() == 0) {
                                holder.profileBinding.ivPlayPause.setVisibility(View.VISIBLE);
                                holder.paused = false;
                                holder.profileBinding.ivPlayPause.setImageResource(R.drawable.ic_play_circle_white);

                                showHideView(holder, holder.paused);
                            }
                        } else {
                            holder.voiceCall = true;
                            setViews(arraylist, holder, poss[0], userData);
                        }
                    }
                }.start();
            }
        }else{
            holder.countDownTimer.cancel();
        }
    }

    private void showHideView(ViewHolder holder, boolean showVal) {
        if (showVal) {
            holder.profileBinding.rivBgImage.setVisibility(View.GONE);
            holder.profileBinding.linuserdata.setVisibility(View.GONE);
            holder.profileBinding.linBottomMenu.setVisibility(View.GONE);
        } else {
            holder.profileBinding.rivBgImage.setVisibility(View.VISIBLE);
            holder.profileBinding.linuserdata.setVisibility(View.VISIBLE);
            holder.profileBinding.linBottomMenu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void sendInterestRequest(String login_matri_id, String strMatriId, final String isFavorite, final int pos, String from) {
        /*progresDialog = new ProgressDialog(nActivity);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(nActivity.getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);*/
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsLoginMatriId = params[0];
                String paramsUserMatriId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "send_intrest.php";
                Log.e("send_intrest", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("sender_id", paramsLoginMatriId);
                BasicNameValuePair UserMatriIdPair = new BasicNameValuePair("receiver_id", paramsUserMatriId);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(UserMatriIdPair);

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

                Log.e("send_intrest", "==" + result);

                try {
                    JSONObject obj = new JSONObject(result);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        /*holder.profileBinding.ivLike.setImageResource(R.drawable.ic_heart);
                        holder.profileBinding.tvLike.setText("Unlike");*/
                        String message = obj.getString("message").toString().trim();
                        setToastStr(nActivity, "" + message);

                        if (isFavorite.equalsIgnoreCase("1")) {
                            mDataList.get(pos).setIs_favourite("0");
                        } else {
                            mDataList.get(pos).setIs_favourite("1");
                        }
                        mDataList.remove(pos);
                        if (onNotifyDataSetChanged != null) {
                            onNotifyDataSetChanged.OnNotifyDataSetChangedFired(mDataList.size());
                            notifyDataSetChanged();
                        }

                        if (!from.equalsIgnoreCase("activity")) {
                            //notifyDataSetChanged();
                            isSwiped = true;
                            cardStackView.swipe();
                        } else {
                            notifyDataSetChanged();
                        }
                        //  refreshAt(pos);

                    } else {
                        String msgError = obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage("" + msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    progresDialog.dismiss();
                } catch (Exception t) {
                    Log.e("fjglfjl", t.getMessage());
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id, strMatriId);
    }

    public void sendInterestRequestRemind(String login_matri_id, String strMatriId, final String isFavorite, final int pos, String from) {
        Dialog progresDialog = showProgress(nActivity);
       /* progresDialog = new ProgressDialog(nActivity);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(nActivity.getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
       */
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsLoginMatriId = params[0];
                String paramsEiId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "intrest_reject.php";
                Log.e("reject_intrest", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("login_matri_id", paramsLoginMatriId);
                BasicNameValuePair UserMatriIdPair = new BasicNameValuePair("matri_id", paramsEiId);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(UserMatriIdPair);

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

                Log.e("send_intrest", "==" + result);

                try {
                    JSONObject obj = new JSONObject(result);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                      /*  holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart_greybg);
                        holder.likedUserBinding.tvLike.setText("Like");*/
                        String message = obj.getString("message").toString().trim();
                        setToastStr(nActivity, "" + message);

                        if (isFavorite.equalsIgnoreCase("1")) {
                            mDataList.get(pos).setIs_favourite("0");
                        } else {
                            mDataList.get(pos).setIs_favourite("1");
                        }
                        mDataList.remove(pos);
                        if (onNotifyDataSetChanged != null) {
                            onNotifyDataSetChanged.OnNotifyDataSetChangedFired(mDataList.size());
                            notifyDataSetChanged();
                        }
                        if (!from.equalsIgnoreCase("activity")) {
                            isSwiped = true;
                            cardStackView.swipe();
                        } else {
                            notifyDataSetChanged();
                        }

                    } else {
                        String msgError = obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage("" + msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                    progresDialog.dismiss();
                } catch (Throwable t) {
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id, strMatriId);
    }

    public void refreshAt(int position) {
        notifyItemChanged(position);
        notifyItemRangeChanged(position, mDataList.size());
    }

    private void setContact(String strLoginMatriId, String matriId, String userMobile) {
        /*final ProgressDialog progresDialog11 = new ProgressDialog(nActivity);
        progresDialog11.setCancelable(false);
        progresDialog11.setMessage(nActivity.getResources().getString(R.string.Please_Wait));
        progresDialog11.setIndeterminate(true);*/
        final Dialog progresDialog11 = showProgress(nActivity);
        progresDialog11.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";

                URL = AppConstants.MAIN_URL + "contact.php";
                Log.e("URL get price", "== " + URL);


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = null;

                MatriIdPair = new BasicNameValuePair("matri_id", matriId);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("login_matri_id", strLoginMatriId);

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(LoginMatriIdPair);

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

                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + userMobile));//change the number
                            nActivity.startActivity(callIntent);
                            AppConstants.setToastStr(nActivity, "" + obj.getString("message"));
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
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    private void getViewVideo(String loginMatriId) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {


                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "view_video.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);
                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("login_matri_id", matri_id);
                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", loginMatriId);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(languagePAir);


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

                Log.e("--Search by Result --", "==" + Ressponce);
                ArrayList<String> tokans = new ArrayList<>();
                tokans.clear();
                try {
                    JSONObject obj = new JSONObject(Ressponce);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {


                    } else {
                        String msgError = obj.getString("message");
                        // AppConstants.setToastStr(nActivity, "" + msgError);
                    }


                    // homeBinding.refresh.setRefreshing(false);
                } catch (Throwable t) {
                    // homeBinding.refresh.setRefreshing(false);
                    t.printStackTrace();
                }
                //  homeBinding.refresh.setRefreshing(false);

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute();

    }

    public interface OnNotifyDataSetChanged {
        void OnNotifyDataSetChangedFired(int dataSize);
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemHomeProfileBinding profileBinding;
        int imageIndex = 0;
        int current = 0;
        ArrayList<Uri> uris = new ArrayList<>();
        ArrayList<Uri> imguris = new ArrayList<>();
        ArrayList<Animation> animationtypes = new ArrayList<>();
        ArrayList<String> stringData = new ArrayList<>();
        Typeface typefaces;
        RecycleImagesAdapter adapter;
        boolean paused = false;
        boolean mute = true;
        Drawable drawable;
        boolean repeat = false;
        Handler handler2 = new Handler();
        CountDownTimer countDownTimer;
        boolean voiceCall=true;
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                imageIndex = 1 + imageIndex;
                if (imageIndex < imguris.size()) {
                    //loadVideo();
                    profileBinding.tvDetail.setVisibility(View.GONE);
                    loadjpgs();
                } else {
                    profileBinding.ivPlayPause.setVisibility(View.VISIBLE);
                    paused = false;
                    profileBinding.ivPlayPause.setImageResource(R.drawable.ic_play_circle_white);
                    showHideView(ViewHolder.this, false);
                    repeat = false;
                    imageIndex = 0;
                    current = 0;
                    profileBinding.tvDetail.setVisibility(View.VISIBLE);
                    profileBinding.recycler.setVisibility(View.GONE);
                    profileBinding.videoView.setVisibility(View.VISIBLE);

                }
            }
        };

        Handler playPauseHandler = new Handler();
        Runnable playPauserunnable = new Runnable() {
            @Override
            public void run() {
                if (paused) {
                    profileBinding.ivPlayPause.setVisibility(View.GONE);
                } else {
                    profileBinding.ivPlayPause.setVisibility(View.VISIBLE);
                    //playPauseHandler.removeCallbacks(playPauserunnable);
                }
                playPauseHandler.removeCallbacks(playPauserunnable);
            }
        };


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileBinding = ItemHomeProfileBinding.bind(itemView);
        }


        private void loadjpgs() {
            profileBinding.tvDetail.setText("");
            profileBinding.tvDetail.setVisibility(View.GONE);
            profileBinding.imageview.setVisibility(View.GONE);
            profileBinding.recycler.setVisibility(View.VISIBLE);

            profileBinding.recycler.smoothScrollToPosition(imageIndex);
            handler2.postDelayed(runnable2, 3000);
            //videoView.setVisibility(View.GONE);

        }

        private void translateText(String input,String gender) {
            if(strLangCode.equalsIgnoreCase("en")){
                profileBinding.tvDetail.setText(input);
                // txt_hindi.animateText(transObject2.getString("translatedText"));
                if (voiceCall) {
                    voiceCall = false;
                    speakOut(input, gender);
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
                                    speakOut(transObject2.getString("translatedText"), gender);
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
                        setToastStr(nActivity, "" + e.getMessage());
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
}
}
