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
import android.os.Bundle;
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
import com.prometteur.sathiya.databinding.ItemProfileBlockedUserBinding;
import com.prometteur.sathiya.databinding.ItemProfileHomeUserBinding;
import com.prometteur.sathiya.home.HomeActivity;
import com.prometteur.sathiya.home.SecondHomeActivity;
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

import static com.prometteur.sathiya.utills.AppConstants.setToastStrPinkBg;
import static com.prometteur.sathiya.utills.AppConstants.vibe;
import static com.prometteur.sathiya.utills.AppConstants.vibrateBig;
import static com.prometteur.sathiya.utills.AppConstants.vibrateSmall;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;
import static com.prometteur.sathiya.utills.AppMethods.shrinkAnim;

public class UserHomeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "Predicted Address";
    public interface OnNotifyDataSetChanged {
        void OnNotifyDataSetChangedFired(int dataSize);
    }

    private OnNotifyDataSetChanged onNotifyDataSetChanged;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    Context nContext;
    List<beanUserData> homeResultList;
    Activity nActivity;
    boolean isMapFragment;
    ArrayList<String> tokans;

    SharedPreferences prefUpdate;
    String matri_id="";
    private OnLoadMoreListener mOnLoadMoreListener;
Bundle bundle;
    public UserHomeListAdapter(Activity nActivity, boolean isMapfargment, List<beanUserData> homeResultList,ArrayList<String> tokans,OnNotifyDataSetChanged onNotifyDataSetChanged,Bundle bundle) {/*,OnItemClickListener listener) {*/
        this.nContext = nActivity;
        this.nActivity = nActivity;
        this.tokans = tokans;
        this.isMapFragment = isMapfargment;
        this.homeResultList = homeResultList;
        prefUpdate= PreferenceManager.getDefaultSharedPreferences(nActivity);
        matri_id=prefUpdate.getString("matri_id","");
        this.onNotifyDataSetChanged = onNotifyDataSetChanged;
this.bundle=bundle;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

           /* View view = LayoutInflater.from(nActivity).inflate(R.layout.recycle_listsaloon_view,
                    parent, false);
            return new SalonListAdapter.ViewHolder(view);*/

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(nActivity).inflate(R.layout.item_profile_home_user, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(nActivity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
        /*if(homeResultList.size()==0)
        {
            nActivity.startActivity(new Intent(nActivity, HomeActivity.class));
            nActivity.finishAffinity();
        }*/
        if (holder1 instanceof ViewHolder) {
            final beanUserData singleUser= (beanUserData) homeResultList.get(position);
            ViewHolder holder = (ViewHolder) holder1;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newIntent=new Intent(nActivity, SecondHomeActivity.class);
                    newIntent.putExtra("pos",position);
                    newIntent.putExtra("bundle", bundle);
                    /*//Log.e("genderrsaf", Genderr);
                    //newIntent.putExtra("Gender", Genderr);
                    newIntent.putExtra("AgeM", "" + AgeM);
                    newIntent.putExtra("AgeF", "" + AgeF);
                    newIntent.putExtra("HeightM", "" + HeightM);
                    newIntent.putExtra("HeightF", "" + HeightF);
                    *//*newIntent.putExtra("MaritalStatus", "" + MaritalStatus);
                    newIntent.putExtra("PhysicalStatus", "" + PhysicalStatus);*//*
                    newIntent.putExtra("ReligionId", "" + ReligionId);
                    newIntent.putExtra("CasteId", "" + CasteId);
                    newIntent.putExtra("CountryId", "" + CountryId);
                    newIntent.putExtra("StateId", "" + StateId);
                    newIntent.putExtra("CityId", "" + CityId);
                    newIntent.putExtra("HighestEducationId", "" + HighestEducationId);
                    //newIntent.putExtra("OccupationId", "" + OccupationId);
                    newIntent.putExtra("AnnualIncome", "" + AnnualIncome);
                    newIntent.putExtra("MotherToungueID", "" + AppConstants.MotherTongueId);
                    newIntent.putExtra("Diet", "" + Diet);*/
                    nActivity.startActivity(newIntent);
                }
            });
if(singleUser.getUsername()!=null && !singleUser.getUsername().equalsIgnoreCase("null")) {
    holder.likedUserBinding.tvProfileName.setText("" + singleUser.getUsername());
}
            holder.likedUserBinding.tvAgeHeight.setText(""+singleUser.getHeight());
            holder.likedUserBinding.tvOccupationSal.setText("" + singleUser.getOcp_name()+"("+singleUser.getIncome()+")");
            holder.likedUserBinding.tvCity.setText(""+singleUser.getCity_name());
            holder.likedUserBinding.tvRefNo.setText("#"+singleUser.getMatri_id());
                Glide.with(nActivity)
                        .asBitmap()
                        .override(100, 120) // made the difference
                        .thumbnail(0.5f)
                        .load(singleUser.getUser_profile_picture())
                        .placeholder(R.drawable.placeholder_gray_corner)
                        .error(R.drawable.placeholder_gray_corner)
                        .into(holder.likedUserBinding.rivProfileImage);


            String is_interest=singleUser.getIs_favourite().toString().trim();

            if(is_interest.equalsIgnoreCase("1"))
            {
                holder.RequestType="Unlike";
                holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart);
                holder.likedUserBinding.tvLike.setText(nContext.getString(R.string.unlike));

                //holder.btnSendInterest.setVisibility(View.GONE);
                //holder.btnRemaind.setVisibility(View.VISIBLE);
            }else
            {
                holder.RequestType="Like";
                holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart_greybg);
                holder.likedUserBinding.tvLike.setText(nContext.getString(R.string.like));
            }


            holder.likedUserBinding.linInterest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.RequestType.equalsIgnoreCase("Like"))
                    {
                        String test = singleUser.getIs_blocked().toString();
                        Log.d("TAG","CHECK ="+test);

                        if (singleUser.getIs_blocked().equalsIgnoreCase("1"))
                        {
                            String msgBlock = nContext.getString(R.string.this_member_has_blocked_you);
                            String msgNotPaid = nContext.getString(R.string.you_are_not_paid_memeber);

                            AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                            builder.setMessage(msgBlock).setCancelable(false).setPositiveButton(nContext.getString(R.string.ok), new DialogInterface.OnClickListener()
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
                            AppConstants.sendPushNotification(tokans.get(position),
                                    AppConstants.msg_express_intress+" "+singleUser.getMatri_id(),
                                    AppConstants.msg_express_intress_title,AppConstants.express_intress_id);
                            sendInterestRequest(matri_id, singleUser.getMatri_id(), singleUser.getIs_favourite(), position,holder);
                        }
                    }
                    else if (holder.RequestType.equalsIgnoreCase("Unlike"))
                    {
                        if (singleUser.getIs_blocked().equalsIgnoreCase("1"))
                        {
                            String msgBlock = nContext.getString(R.string.this_member_has_blocked_you);
                            String msgNotPaid = nContext.getString(R.string.you_are_not_paid_memeber);

                            AlertDialog.Builder builder = new AlertDialog.Builder(nActivity);
                            builder.setMessage(msgBlock).setCancelable(false).setPositiveButton(nActivity.getString(R.string.ok), new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            AppConstants.sendPushNotification(tokans.get(position),
                                    AppConstants.msg_express_intress+" "+singleUser.getMatri_id(),
                                    AppConstants.msg_send_reminder_title,AppConstants.express_intress_id);
                            vibe.vibrate(vibrateSmall);
                            sendInterestRequestRemind(matri_id, singleUser.getei_reqid(), singleUser.getIs_favourite(), position,holder);
                        }
                    }
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

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemProfileHomeUserBinding likedUserBinding;
        String RequestType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            likedUserBinding= ItemProfileHomeUserBinding.bind(itemView);

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

    private void sendInterestRequest(String login_matri_id, String strMatriId, final String isFavorite, final int pos, final ViewHolder holder)
    {
        Dialog progresDialog = showProgress(nActivity);
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

                        holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart);
                        holder.likedUserBinding.tvLike.setText(nContext.getString(R.string.unlike));
                        String message=obj.getString("message").toString().trim();
                        setToastStrPinkBg(nActivity, ""+message);
                        homeResultList.remove(pos);
                        notifyDataSetChanged();
                        if(onNotifyDataSetChanged!=null) {
                            onNotifyDataSetChanged.OnNotifyDataSetChangedFired(homeResultList.size());
                        }
                       /* if(isFavorite.equalsIgnoreCase("1")) {
                            homeResultList.get(pos).setIs_favourite("0");
                        }else
                        {
                            homeResultList.get(pos).setIs_favourite("1");
                            homeResultList.get(pos).setei_reqid(""+obj.getString("ei_id"));
                        }*/
                        //refreshAt(pos);

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


    private void sendInterestRequestRemind(String login_matri_id, String strMatriId, final String isFavorite, final int pos,ViewHolder holder)
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
                        holder.likedUserBinding.ivLike.setImageResource(R.drawable.ic_heart_greybg);
                        holder.likedUserBinding.tvLike.setText(nContext.getString(R.string.like));
                        String message=obj.getString("message").toString().trim();
                        setToastStrPinkBg(nActivity, ""+message);

                        if(isFavorite.equalsIgnoreCase("1")) {
                            homeResultList.get(pos).setIs_favourite("0");
                        }else
                        {
                            homeResultList.get(pos).setIs_favourite("1");
                        }

                        refreshAt(pos);

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




    public void refreshAt(int position)
    {
        notifyItemChanged(position);
        notifyItemRangeChanged(position, homeResultList.size());
    }
}
