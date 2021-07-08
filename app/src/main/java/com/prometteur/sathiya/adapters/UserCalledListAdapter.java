package com.prometteur.sathiya.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.prometteur.sathiya.databinding.ItemProfileCalledUserBinding;
import com.prometteur.sathiya.home.ThirdHomeActivity;
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

import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppConstants.setToastStrPinkBg;
import static com.prometteur.sathiya.utills.AppConstants.vibe;
import static com.prometteur.sathiya.utills.AppConstants.vibrateBig;
import static com.prometteur.sathiya.utills.AppConstants.vibrateSmall;
import static com.prometteur.sathiya.utills.AppMethods.shrinkAnim;

public class UserCalledListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "Predicted Address";
    /*private final SalonListAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }*/
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    Context nContext;
    List<beanUserData> homeResultList;
    Activity nActivity;
    boolean isMapFragment;
    SharedPreferences prefUpdate;
    String matri_id="";

    public UserCalledListAdapter(Activity nActivity, boolean isMapfargment, List<beanUserData> homeResultList) {/*,OnItemClickListener listener) {*/
        this.nContext = nActivity;
        this.nActivity = nActivity;
        this.isMapFragment = isMapfargment;
        this.homeResultList = homeResultList;
        //this.listener = listener;
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
            View view = LayoutInflater.from(nActivity).inflate(R.layout.item_profile_called_user, parent, false);
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
holder.likedUserBinding.tvProfileName.setText(""+homeResultList.get(position).getUsername());
holder.likedUserBinding.tvCity.setText(""+homeResultList.get(position).getCity_name());
holder.likedUserBinding.tvRefNo.setText("#"+homeResultList.get(position).getMatri_id());
            if(homeResultList.get(position).getIs_favourite().equalsIgnoreCase("1"))
            {
                holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart);
            }else
            {
                holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart_greybg);
            }
                Glide.with(nActivity)
                        .asBitmap()
                        .override(130, 100) // made the difference
                        .thumbnail(0.5f)
                        .load(homeResultList.get(position).getUser_profile_picture())
                        .placeholder(R.drawable.placeholder_gray_corner)
                        .error(R.drawable.placeholder_gray_corner)
                        .into(new Target<Bitmap>() {
                            @Override
                            public void onLoadStarted(@Nullable Drawable placeholder) {

                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                holder.likedUserBinding.rivProfileImage.setImageResource(R.drawable.placeholder_gray_corner);

                            }

                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.likedUserBinding.rivProfileImage.setImageBitmap(resource);


                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                holder.likedUserBinding.rivProfileImage.setImageResource(R.drawable.placeholder_gray_corner);

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
                    if(homeResultList.get(position).getIs_favourite().equalsIgnoreCase("0"))
                    {
                        if (homeResultList.get(position).getIs_blocked().equalsIgnoreCase("1"))
                        {
                            String msgBlock = "This member has blocked you. You can't express your interest.";
                            String msgNotPaid = "You are not paid member. Please update your membership to express your interest.";

                            AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                            builder.setMessage(msgBlock).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            vibe.vibrate(vibrateBig);
                            shrinkAnim(holder.likedUserBinding.ivLike,nContext);
                            sendInterestRequest(matri_id,homeResultList.get(position).getMatri_id(), homeResultList.get(position).getIs_favourite(),holder,position);
                        }
                    }else
                    {
                        if (homeResultList.get(position).getIs_blocked().equalsIgnoreCase("1"))
                        {
                            String msgBlock = "This member has blocked you. You can't express your interest.";
                            String msgNotPaid = "You are not paid member. Please update your membership to express your interest.";

                            AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                            builder.setMessage(msgBlock).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            vibe.vibrate(vibrateSmall);
                            sendInterestRequestRemind(matri_id,homeResultList.get(position).getei_reqid(), homeResultList.get(position).getIs_favourite(),holder,position);
                        }
                    }
                }
            });

                String is_blocked = homeResultList.get(position).getIs_blocked();

                if (is_blocked.equalsIgnoreCase("1")) {
                    holder.likedUserBinding.tvBlock.setText(nContext.getString(R.string.unblock));
                } else {
                    holder.likedUserBinding.tvBlock.setText(nContext.getString(R.string.block));
                }

            String finalIs_blocked = is_blocked;
            holder.likedUserBinding.linBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToBlockRequest(matri_id, homeResultList.get(position).getMatri_id(), finalIs_blocked,position);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ThirdHomeActivity.matri_id=homeResultList.get(position).getMatri_id();
                    nActivity.startActivity(new Intent(nActivity, ThirdHomeActivity.class).putExtra("getListType","call"));
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

        ItemProfileCalledUserBinding likedUserBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            likedUserBinding=ItemProfileCalledUserBinding.bind(itemView);

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



    private void addToBlockRequest(String login_matri_id, String strMatriId, final String isBlocked,int position) {
        ProgressDialog progresDialog = new ProgressDialog(nActivity);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(nActivity.getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
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
                            /*homeResultList.remove(position);*/

                        }
                        notifyDataSetChanged();
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
                    Log.e("fjkhgjkfa",t.getMessage());
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id, strMatriId, isBlocked);
    }



    String eiId="";
    private void sendInterestRequest(String login_matri_id, String strMatriId, String isFavorite,ViewHolder holder,int pos)
    {
        ProgressDialog progresDialog= new ProgressDialog(nActivity);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(nActivity.getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramsLoginMatriId = params[0];
                String paramsUserMatriId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"send_intrest.php";
                Log.e("send_intrest", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("sender_id", paramsLoginMatriId);
                BasicNameValuePair UserMatriIdPair  = new BasicNameValuePair("receiver_id", paramsUserMatriId);


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
                        //   ivInterest.setImageResource(R.drawable.ic_reminder);
                        holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart);
                        String message=obj.getString("message").toString().trim();
                        setToastStrPinkBg(nActivity, ""+message);

                        homeResultList.get(pos).setIs_favourite("1");
                        eiId=""+obj.getString("ei_id");
                        homeResultList.get(pos).setei_reqid(eiId);

                        notifyDataSetChanged();

                    }else
                    {
                        String msgError=obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage(""+msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
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
                } catch (Exception t)
                {
                    Log.e("fjglfjl",t.getMessage());
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id,strMatriId);
    }


    private void sendInterestRequestRemind(String login_matri_id, String strMatriId, final String isFavorite,ViewHolder holder,int pos)
    {
        ProgressDialog progresDialog= new ProgressDialog(nActivity);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(nActivity.getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
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
                Log.e("send_intrest", "== "+URL);

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

                        String message=obj.getString("message").toString().trim();
                        setToastStrPinkBg(nActivity, ""+message);
                        holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart_greybg);

                        homeResultList.get(pos).setIs_favourite("0");
                        notifyDataSetChanged();
                    }else
                    {
                        String msgError=obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                        builder.setMessage(""+msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
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

}
