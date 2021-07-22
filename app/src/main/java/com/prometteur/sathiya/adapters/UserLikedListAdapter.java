package com.prometteur.sathiya.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.databinding.ItemProfileLikedUserBinding;
import com.prometteur.sathiya.home.SecondHomeActivity;
import com.prometteur.sathiya.home.ThirdHomeActivity;
import com.prometteur.sathiya.listeners.OnClickOnItemListener;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.OnLoadMoreListener;

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

import static com.prometteur.sathiya.home.ThirdHomeActivity.login_matri_id;
import static com.prometteur.sathiya.utills.AppConstants.setToastStrPinkBg;
import static com.prometteur.sathiya.utills.AppConstants.vibe;
import static com.prometteur.sathiya.utills.AppConstants.vibrateSmall;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class UserLikedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "Predicted Address";

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    Context nContext;
    List<beanUserData> homeResultList;
    Activity nActivity;
    List<String> tokans;
    SharedPreferences prefUpdate;
    String matri_id="";
    OnClickOnItemListener itemListener;
    public UserLikedListAdapter(Activity nActivity, List<beanUserData> homeResultList, List<String> tokans,OnClickOnItemListener itemListener) {
        this.nContext = nActivity;
        this.nActivity = nActivity;
        this.homeResultList = homeResultList;
        this.tokans = tokans;
        this.itemListener = itemListener;
        prefUpdate= PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id=prefUpdate.getString("matri_id","");
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

           /* View view = LayoutInflater.from(nActivity).inflate(R.layout.recycle_listsaloon_view,
                    parent, false);
            return new SalonListAdapter.ViewHolder(view);*/

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(nActivity).inflate(R.layout.item_profile_liked_user, parent, false);
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
            final beanUserData singleUser= (beanUserData) homeResultList.get(position);
            ViewHolder holder = (ViewHolder) holder1;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ThirdHomeActivity.matri_id=singleUser.getMatri_id();
                    nActivity.startActivity(new Intent(nActivity, ThirdHomeActivity.class));
                }
            });
                if(singleUser.getUsername()!=null && !singleUser.getUsername().equalsIgnoreCase("null")) {
                    holder.likedUserBinding.tvProfileName.setText("" + singleUser.getUsername());
                }
            holder.likedUserBinding.tvAgeHeight.setText(""+singleUser.getHeight());
            holder.likedUserBinding.tvCity.setText(""+singleUser.getCity_name());
            holder.likedUserBinding.tvRefNo.setText("#"+singleUser.getMatri_id());
                Glide.with(nActivity)
                        .asBitmap()
                        .override(130, 100) // made the difference
                        .thumbnail(0.5f)
                        .load(singleUser.getUser_profile_picture())
                        .placeholder(R.drawable.shape_corner_round_square)
                        .error(R.drawable.shape_corner_round_square)
                        .into(new Target<Bitmap>() {
                            @Override
                            public void onLoadStarted(@Nullable Drawable placeholder) {

                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                holder.likedUserBinding.rivProfileImage.setImageResource(R.drawable.shape_corner_round_square);

                            }

                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.likedUserBinding.rivProfileImage.setImageBitmap(resource);


                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                holder.likedUserBinding.rivProfileImage.setImageResource(R.drawable.shape_corner_round_square);

                            }

                            @Override
                            public void getSize(@NonNull SizeReadyCallback cb) {

                            }

                            @Override
                            public void removeCallback(@NonNull SizeReadyCallback cb) {

                            }

                            @Override
                            public void setRequest(@Nullable Request request) {

                            }

                            @Nullable
                            @Override
                            public Request getRequest() {
                                return null;
                            }

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onStop() {

                            }

                            @Override
                            public void onDestroy() {

                            }
                        });

                holder.likedUserBinding.ivLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vibe.vibrate(vibrateSmall);
                        sendInterestRequestRemind(matri_id, singleUser.getei_reqid(), singleUser.getIs_favourite(), position,holder);
                    }
                });

                /*holder.likedUserBinding.linBlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addToBlockRequest(matri_id, singleUser.getMatri_id(), "0",position);
                    }
                });*/
                holder.likedUserBinding.linReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendInterestReject(singleUser,position);
                    }
                });
        } else if (holder1 instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder1;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return homeResultList == null ? 0 : homeResultList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return homeResultList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemProfileLikedUserBinding likedUserBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            likedUserBinding=ItemProfileLikedUserBinding.bind(itemView);

        }

        /*public void bind(final int position, final SalonListAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }*/

    }


    private void sendInterestRequestRemind(String login_matri_id, String strMatriId, final String isFavorite, final int pos, ViewHolder holder)
    {
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramsLoginMatriId = params[0];
                String paramsEiId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"remove_intrest.php";
                Log.e("remove_intrest", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("matri_id", paramsLoginMatriId);
                BasicNameValuePair UserMatriIdPair  = new BasicNameValuePair("ei_id", paramsEiId);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(UserMatriIdPair);

                try
                {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    Log.e("Parametters Array=", "== "+(nameValuePairList.toString().trim().replaceAll(",","&")));
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

                Log.e("send_intrest", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);

                    String status=obj.getString("status");

                    if (status.equalsIgnoreCase("1"))
                    {

//                        holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart_greybg);
                        String message=obj.getString("message").toString().trim();
                        setToastStrPinkBg(nActivity, ""+message);
                        itemListener.onItemClick();
                        homeResultList.remove(pos);
                        notifyDataSetChanged();
                        /*if(isFavorite.equalsIgnoreCase("1")) {
                            homeResultList.get(pos).setIs_favourite("0");
                        }else
                        {
                            homeResultList.get(pos).setIs_favourite("1");
                        }
*/
                        //refreshAt(pos);

                    }else
                    {
                        String msgError=obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage(""+msgError).setCancelable(false).setPositiveButton(nActivity.getString(R.string.ok), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                    progresDialog.dismiss();
                } catch (Throwable t)
                {
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id,strMatriId);
    }

    public void refreshAt(int position)
    {
        notifyItemChanged(position);
        notifyItemRangeChanged(position, homeResultList.size());
    }

    private void addToBlockRequest(String login_matri_id, String strMatriId, final String isBlocked,int position) {

        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsLoginMatriId = params[0];
                String paramsUserMatriId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";
                if (isBlocked.equalsIgnoreCase("1")) {
                    URL = AppConstants.MAIN_URL + "remove_blocklist.php";
                    Log.e("remove_blocklist", "== " + URL);
                } else {
                    URL = AppConstants.MAIN_URL + "block_user.php";
                    Log.e("block_user", "== " + URL);
                }


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("matri_id", paramsLoginMatriId);
                BasicNameValuePair UserMatriIdPair = new BasicNameValuePair("block_matri_id", paramsUserMatriId);

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

                Log.e("block_user", "==" + result);

                try {
                    JSONObject obj = new JSONObject(result);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        //String message=obj.getString("message").toString().trim();

                        if (isBlocked.equalsIgnoreCase("1")) {
                            homeResultList.get(position).setIs_blocked("0");
                            setToastStrPinkBg(nActivity, nActivity.getString(R.string.sucessfully_unblock));
                        } else {
                            homeResultList.get(position).setIs_blocked("1");
                            setToastStrPinkBg(nActivity, nActivity.getString(R.string.successfully_blocked));
                            homeResultList.remove(position);
                            notifyDataSetChanged();
                        }
                    } else {
                        String msgError = obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage("" + msgError).setCancelable(false).setPositiveButton(nActivity.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                    progresDialog.dismiss();
                } catch (Exception t) {
                    Log.e("fjkhgjkfa",t.getMessage());
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id, strMatriId, isBlocked);
    }

    public void sendInterestReject(beanUserData singleUser,final int pos)
    {
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramsLoginMatriId = params[0];
                String paramsEiId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"intrest_reject.php";
                Log.e("reject_intrest", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);

                 BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("login_matri_id", paramsLoginMatriId);
                BasicNameValuePair UserEiIdPair  = new BasicNameValuePair("ei_id", paramsEiId);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                  nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(UserEiIdPair);

                try
                {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    Log.e("Parametters Array=", "== "+(nameValuePairList.toString().trim().replaceAll(",","&")));
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

                Log.e("send_intrest", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);

                    String status=obj.getString("status");

                    if (status.equalsIgnoreCase("1"))
                    {
                      /*  holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart_greybg);
                        holder.likedUserBinding.tvLike.setText("Like");*/
                        String message=obj.getString("message").toString().trim();
                        setToastStrPinkBg(nActivity, ""+message);

                        if(singleUser.getIs_favourite().equalsIgnoreCase("1")) {
                            singleUser.setIs_favourite("0");
                        }else
                        {
                            singleUser.setIs_favourite("1");
                        }
                        homeResultList.remove(pos);
                        notifyDataSetChanged();

                    }else
                    {
                        String msgError=obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage(""+msgError).setCancelable(false).setPositiveButton(nActivity.getString(R.string.ok), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                    progresDialog.dismiss();
                } catch (Throwable t)
                {
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(matri_id,singleUser.getei_reqid());
    }
}
