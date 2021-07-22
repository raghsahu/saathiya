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
import android.widget.TextView;

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
import com.prometteur.sathiya.databinding.ItemProfileLikereceivedUserBinding;
import com.prometteur.sathiya.home.ThirdHomeActivity;
import com.prometteur.sathiya.listeners.OnClickOnItemListener;
import com.prometteur.sathiya.model.DateObject;
import com.prometteur.sathiya.model.HistoryDataModelObject;
import com.prometteur.sathiya.model.ListObject;
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

import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppConstants.setToastStrPinkBg;
import static com.prometteur.sathiya.utills.AppConstants.vibe;
import static com.prometteur.sathiya.utills.AppConstants.vibrateBig;
import static com.prometteur.sathiya.utills.AppConstants.vibrateSmall;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;
import static com.prometteur.sathiya.utills.AppMethods.shrinkAnim;

public class LikeReceivedListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "LikeReceivedListAdapter";
    /*private final SalonListAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }*/
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    Context nContext;
    List<ListObject> homeResultList;
    Activity nActivity;
    boolean isMapFragment;
    String pageType;
    String matri_id = "";
    SharedPreferences prefUpdate;
    String eiId = "";
    private OnClickOnItemListener itemListener;


    public LikeReceivedListAdapter(Activity nActivity, boolean isMapfargment, List<ListObject> homeResultList, String pageType, OnClickOnItemListener itemListener) {
        this.nContext = nActivity;
        this.nActivity = nActivity;
        this.isMapFragment = isMapfargment;
        this.homeResultList = homeResultList;
        this.pageType = pageType;
        this.itemListener = itemListener;
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id = prefUpdate.getString("matri_id", "");

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ListObject.TYPE_GENERAL:
                View currentUserView = inflater.inflate(R.layout.item_profile_likereceived_user, parent, false);
                viewHolder = new ViewHolderData(currentUserView); // view holder for normal items
                break;

            case ListObject.TYPE_DATE:
                View v2 = inflater.inflate(R.layout.date_row, parent, false);
                viewHolder = new DateViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    public void setDataChange(List<ListObject> asList) {
        this.homeResultList = asList;
        //now, tell the adapter about the update
        notifyDataSetChanged();
    }
int isReject=0;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
        switch (holder1.getItemViewType()) {
            case ListObject.TYPE_GENERAL:
                ViewHolderData holder = (ViewHolderData) holder1;
                final HistoryDataModelObject singleUser = (HistoryDataModelObject) homeResultList.get(position);
                if (singleUser.getChatModel().getUsername() != null && !singleUser.getChatModel().getUsername().equalsIgnoreCase("null")) {
                    holder.likedUserBinding.tvProfileName.setText("" + singleUser.getChatModel().getUsername());
                }
                if (singleUser.getChatModel().getHeight() != null && !singleUser.getChatModel().getHeight().equalsIgnoreCase("null")) {
                    holder.likedUserBinding.tvAgeHeight.setText("" + singleUser.getChatModel().getHeight());
                }
                holder.likedUserBinding.tvCity.setText("" + singleUser.getChatModel().getCity_name());
                holder.likedUserBinding.tvRefNo.setText("#" + singleUser.getChatModel().getMatri_id());
                if (pageType.equalsIgnoreCase("5")) {
                    holder.likedUserBinding.tvRejectedStatus.setVisibility(View.VISIBLE);
                    holder.likedUserBinding.tvRejectedDate.setVisibility(View.VISIBLE);
                    holder.likedUserBinding.tvRejectedDate.setText(singleUser.getChatModel().getBirthdate()); //this is not birth date temp used this key to store
                    if (singleUser.getChatModel().getRejectedStatus().equalsIgnoreCase("rejected_by")) {
                        holder.likedUserBinding.tvRejectedStatus.setText(nContext.getString(R.string.you_rejected_by) + " " + singleUser.getChatModel().getUsername());
                    } else {
                        holder.likedUserBinding.tvRejectedStatus.setText(nContext.getString(R.string.you_rejected) + " " + singleUser.getChatModel().getUsername());
                    }
                } else if (pageType.equalsIgnoreCase("1")) {
                    holder.likedUserBinding.linBook.setVisibility(View.VISIBLE);
                    holder.likedUserBinding.ivLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            shrinkAnim(holder.likedUserBinding.ivLike, nContext);
                            vibe.vibrate(vibrateBig);
                            sendInterestRequest(matri_id, singleUser.getChatModel().getMatri_id(), singleUser.getChatModel().getIs_favourite(), holder, position);
                        }
                    });
                } else {
                    holder.likedUserBinding.tvRejectedDate.setVisibility(View.GONE);
                    holder.likedUserBinding.tvRejectedStatus.setVisibility(View.GONE);

                    holder.likedUserBinding.linBook.setVisibility(View.VISIBLE);
                    holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart);
                    holder.likedUserBinding.ivLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            shrinkAnim(holder.likedUserBinding.ivLike, nContext);
                            vibe.vibrate(vibrateBig);
                            isReject=1; //unlike
                            sendInterestRequestRemind(singleUser.getChatModel(), position);
                        }
                    });
                }
                Glide.with(nActivity)
                        .asBitmap()
                        .override(130, 100) // made the difference
                        .thumbnail(0.5f)
                        .load(singleUser.getChatModel().getUser_profile_picture())
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

                            @Nullable
                            @Override
                            public Request getRequest() {
                                return null;
                            }

                            @Override
                            public void setRequest(@Nullable Request request) {

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
                if (/*pageType.equalsIgnoreCase("2") ||*/ pageType.equalsIgnoreCase("5")) {
                    holder.likedUserBinding.linRemove.setVisibility(View.GONE);
                } else {
                    holder.likedUserBinding.linRemove.setVisibility(View.VISIBLE);
                }
                holder.likedUserBinding.tvUnblock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vibe.vibrate(vibrateSmall);
                        isReject=0;//for reject
                        sendInterestRequestRemind(singleUser.getChatModel(), position);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ThirdHomeActivity.matri_id = singleUser.getChatModel().getMatri_id();
                        if (pageType.equalsIgnoreCase("2")) {
                            nActivity.startActivity(new Intent(nActivity, ThirdHomeActivity.class).putExtra("pageType", "accepted"));
                        } else if (pageType.equalsIgnoreCase("5")) {
                            nActivity.startActivity(new Intent(nActivity, ThirdHomeActivity.class).putExtra("pageType", "rejected"));
                        } else {

                            nActivity.startActivity(new Intent(nActivity, ThirdHomeActivity.class));
                        }
                    }
                });

                break;
            case ListObject.TYPE_DATE:

                DateObject dateItem = (DateObject) homeResultList.get(position);
                DateViewHolder viewHolder1 = (DateViewHolder) holder1;
                viewHolder1.bind(dateItem.getDate());
                if (pageType.equalsIgnoreCase("5")) {
                    viewHolder1.tvDate.setVisibility(View.GONE);
                } else {
                    viewHolder1.tvDate.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return homeResultList == null ? 0 : homeResultList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return homeResultList.get(position).getType();
    }

    public void sendInterestRequestRemind(beanUserData singleUser, final int pos) {
        Dialog progresDialog = showProgress(nActivity);
        progresDialog.show();

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsLoginMatriId = params[0];
                String paramsEiId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";
                if (pageType.equalsIgnoreCase("2")) {
                    if(isReject==1) {
                        URL = AppConstants.MAIN_URL + "remove_intrest.php";
                    }else{
                        URL = AppConstants.MAIN_URL + "intrest_reject.php";
                    }
                } else {
                    URL = AppConstants.MAIN_URL + "intrest_reject.php";
                }
                Log.e("reject_intrest", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", paramsLoginMatriId);
                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("login_matri_id", paramsLoginMatriId);
                BasicNameValuePair UserEiIdPair = new BasicNameValuePair("ei_id", paramsEiId);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(UserEiIdPair);

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
                      /*  holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heartt_greybg);
                        holder.likedUserBinding.tvLike.setText("Like");*/
                        String message = obj.getString("message").toString().trim();
                        setToastStr(nActivity, "" + message);

                        if (singleUser.getIs_favourite().equalsIgnoreCase("1")) {
                            singleUser.setIs_favourite("0");
                        } else {
                            singleUser.setIs_favourite("1");
                        }
                        if (pageType.equalsIgnoreCase("2")) {
                            itemListener.onItemClick();
                        }
                        homeResultList.remove(pos);
                        refreshAt(pos);

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
                } catch (Throwable t) {
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(matri_id, singleUser.getei_reqid());
    }

    public void refreshAt(int position) {
        //  notifyItemChanged(position);
        //notifyItemRangeChanged(position, homeResultList.size());
        notifyDataSetChanged();
    }

    private void sendInterestRequest(String login_matri_id, String strMatriId, String isFavorite, ViewHolderData holder, int pos) {
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
                        //   ivInterest.setImageResource(R.drawable.ic_reminder);
                        holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart);
                        String message = obj.getString("message").toString().trim();
                        setToastStrPinkBg(nActivity, "" + message);

                        // homeResultList.get(pos).setIs_favourite("1");
                        eiId = "" + obj.getString("ei_id");
                        // homeResultList.get(pos).setei_reqid(eiId);
                        itemListener.onItemClick();
                        homeResultList.remove(pos);
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
                    Log.e("fjglfjl", t.getMessage());
                    progresDialog.dismiss();
                }
                progresDialog.dismiss();
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id, strMatriId);
    }

    public class ViewHolderData extends RecyclerView.ViewHolder {

        ItemProfileLikereceivedUserBinding likedUserBinding;

        public ViewHolderData(@NonNull View itemView) {
            super(itemView);
            likedUserBinding = ItemProfileLikereceivedUserBinding.bind(itemView);

        }

    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;

        public DateViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            //TODO initialize your xml views
        }

        public void bind(final String date) {
            //TODO set data to xml view via textivew.setText();
            tvDate.setText("" + date);
        }
    }

}
