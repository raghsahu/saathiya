package com.prometteur.sathiya.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.SplashActivity;
import com.prometteur.sathiya.beans.beanHobbyImage;
import com.prometteur.sathiya.databinding.ItemPhotosBinding;
import com.prometteur.sathiya.databinding.ItemPhotosOtherBinding;
import com.prometteur.sathiya.databinding.ItemUserHeaderBinding;
import com.prometteur.sathiya.dialog.DialogAddPhotoActivity;
import com.prometteur.sathiya.hobbies.PhotoSliderActivity;
import com.prometteur.sathiya.home.ThirdHomeActivity;
import com.prometteur.sathiya.model.DateObject;
import com.prometteur.sathiya.model.HistoryDataModelObject;
import com.prometteur.sathiya.model.ListObject;
import com.prometteur.sathiya.model.UserDataObject;
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

import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class HobbiesOtherListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "Predicted Address";
    Context nContext;
    List<ListObject> homeResultList;
    Activity nActivity;
    boolean isMapFragment;
    SharedPreferences prefUpdate;
    String matri_id="";
    public HobbiesOtherListAdapter(Activity nActivity, boolean isMapfargment, List<ListObject> homeResultList) {/*,OnItemClickListener listener) {*/
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

        if (viewType == ListObject.TYPE_GENERAL) {
            View view = LayoutInflater.from(nActivity).inflate(R.layout.item_photos_other, parent, false);
            return new ViewHolder(view);
        } else if (viewType == ListObject.TYPE_DATE) {
            View view = LayoutInflater.from(nActivity).inflate(R.layout.item_user_header, parent, false);
            return new DateViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, final int position) {
        switch (holder1.getItemViewType()) {
            case ListObject.TYPE_GENERAL:
            ViewHolder holder = (ViewHolder) holder1;
            final HistoryDataModelObject singleUser= (HistoryDataModelObject) homeResultList.get(position);
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        nActivity.startActivity(new Intent(nActivity, PhotoSliderActivity.class).putExtra("matriId",singleUser.getHobbyImageModel().getMatri_id()));
    }
});
holder.photosBinding.imgHeart.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        setLike(matri_id,singleUser.getHobbyImageModel(),holder);
    }
});

                Glide.with(nActivity)
                        .asBitmap()
                        .override(130, 100)
                        .apply(new RequestOptions()
                                .fitCenter()
                                .format(DecodeFormat.PREFER_ARGB_8888)
                                .override(Target.SIZE_ORIGINAL))
                        .thumbnail(0.5f)
                        .load(singleUser.getHobbyImageModel().getPhoto())
                        .placeholder(R.drawable.shape_corner_round_square)
                        .error(R.drawable.shape_corner_round_square)
                        .into(holder.photosBinding.rivProfileImage);


            if(singleUser.getHobbyImageModel().getIsLiked().equalsIgnoreCase("1"))
            {
                holder.photosBinding.imgHeart.setImageResource(R.drawable.ic_heart);
            }else
            {
                holder.photosBinding.imgHeart.setImageResource(R.drawable.ic_heart_greybg);
            }
break;
            case ListObject.TYPE_DATE:
            DateViewHolder loadingViewHolder = (DateViewHolder) holder1;
                UserDataObject dateItem = (UserDataObject) homeResultList.get(position);
                Glide.with(nActivity)
                        .asBitmap()
                        .override(130, 100)
                        .apply(new RequestOptions()
                                .fitCenter()
                                .format(DecodeFormat.PREFER_ARGB_8888)
                                .override(Target.SIZE_ORIGINAL))
                        .thumbnail(0.5f)
                        .load(dateItem.getUserPhoto())
                        .placeholder(R.drawable.bg_circle_placeholder_blackfill)
                        .error(R.drawable.bg_circle_placeholder_blackfill)
                        .into(loadingViewHolder.headerBinding.civProfileImg);

            loadingViewHolder.headerBinding.tvProfileName.setText(""+dateItem.getUserName());
            if(dateItem.getLocation()!=null && !dateItem.getLocation().equalsIgnoreCase("null") && !dateItem.getLocation().isEmpty()){
                loadingViewHolder.headerBinding.tvLocation.setText("" + dateItem.getLocation());
            }else {
                loadingViewHolder.headerBinding.tvLocation.setVisibility(View.INVISIBLE);
            }

            loadingViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ThirdHomeActivity.matri_id=dateItem.getMatriId();
                    nActivity.startActivity(new Intent(nActivity, ThirdHomeActivity.class));
                }
            });
            break;
            case ListObject.VIEW_TYPE_LOADING:
                LoadingViewHolder viewHolder =(LoadingViewHolder) holder1;
                showLoadingView((LoadingViewHolder) viewHolder, position);
            break;
        }

    }
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
        viewHolder.progressBar.setVisibility(View.VISIBLE);
    }
    public void setDataChange(List<ListObject> asList) {
        this.homeResultList = asList;
        //now, tell the adapter about the update
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return homeResultList == null ? 0 : homeResultList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(homeResultList.get(position)==null)
        {
            return ListObject.VIEW_TYPE_LOADING;
        }else {
            return homeResultList.get(position).getType();
        }
    }

    public class DateViewHolder extends RecyclerView.ViewHolder {
        ItemUserHeaderBinding headerBinding;
        public DateViewHolder(View itemView) {
            super(itemView);
            headerBinding=ItemUserHeaderBinding.bind(itemView);
            //TODO initialize your xml views
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPhotosOtherBinding photosBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photosBinding = ItemPhotosOtherBinding.bind(itemView);

        }


    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }


    //ProgressDialog progresDialog;
    private void setLike(String login_matri_id, beanHobbyImage beanHobby, final ViewHolder holder)
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
                String paramsLoginMatriId = params[0];
                String paramsUserMatriId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"like_dislike_hobby_photo.php";
                Log.e("send_intrest", "== "+URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("login_matri_id", paramsLoginMatriId);
                BasicNameValuePair UserMatriIdPair  = new BasicNameValuePair("matri_id", paramsUserMatriId);
                BasicNameValuePair IntrestId  = new BasicNameValuePair("intrest_id", beanHobby.getIntrest_id());


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(LoginMatriIdPair);
                nameValuePairList.add(UserMatriIdPair);
                nameValuePairList.add(IntrestId);

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

                        String message=obj.getString("message").toString().trim();
                        AppConstants.setToastStrPinkBg(nActivity, ""+message);

                        if(beanHobby.getIsLiked().equalsIgnoreCase("1")) {
                            beanHobby.setIsLiked("0");
                            holder.photosBinding.imgHeart.setImageResource(R.drawable.ic_heart_greybg);
                        }else
                        {
                            beanHobby.setIsLiked("1");
                            holder.photosBinding.imgHeart.setImageResource(R.drawable.ic_heart);
//                            homeResultList.get(pos).setei_reqid(""+obj.getString("ei_id"));
                        }
                        notifyDataSetChanged();
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
        sendPostReqAsyncTask.execute(login_matri_id,beanHobby.getMatri_id());
    }
}
