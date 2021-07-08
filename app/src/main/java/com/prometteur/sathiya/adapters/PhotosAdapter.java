package com.prometteur.sathiya.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanPhotos;
import com.prometteur.sathiya.home.FullScreenImageActivity;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

import static com.prometteur.sathiya.profile.EditProfileActivity.rivProfileImage;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.MyViewHolder> {

    public Activity activity;
    ArrayList<beanPhotos> arrPopularJobList;

    ProgressDialog progresDialog;
    SharedPreferences prefUpdate;
    String matri_id="";
    String strFilePath="";
    String GenderType ="";

    private AlertDialog alertDialog;




    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photos_list, parent, false);
        return new MyViewHolder(itemView);
    }

    public PhotosAdapter(Activity context, ArrayList<beanPhotos> fields_list, String Gender) {

        this.activity = context;
        this.arrPopularJobList = fields_list;
        this.GenderType = Gender;
        Log.e("gender",GenderType);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        final beanPhotos objJobs= arrPopularJobList.get(position);
        holder.lltext.setVisibility(View.VISIBLE);
            if (position ==0)
            {
                if(objJobs.getApproveStatus().equalsIgnoreCase("APPROVED")){
                    holder.tvProfilePhoto.setText(R.string.approved);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorGreen));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("UNAPPROVED")){
                    holder.tvProfilePhoto.setText(R.string.pending);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorOrange1));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("REJECTED")){
                    holder.tvProfilePhoto.setText(R.string.rejected);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorRed));
                }else {
                    holder.tvProfilePhoto.setText(R.string.profile_photo);
                }
                Glide.with(activity).load(objJobs.getImageURL()).error(R.drawable.img_item).into(rivProfileImage);
            }
            else if (position ==1) {

                if(objJobs.getApproveStatus().equalsIgnoreCase("APPROVED")){
                    holder.tvProfilePhoto.setText(R.string.approved);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorGreen));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("UNAPPROVED")){
                    holder.tvProfilePhoto.setText(R.string.pending);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorOrange1));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("REJECTED")){
                    holder.tvProfilePhoto.setText(R.string.rejected);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorRed));
                }else {
                    holder.tvProfilePhoto.setText(R.string.upload_photo);
                }
            } else if (position ==2) {

                if(objJobs.getApproveStatus().equalsIgnoreCase("APPROVED")){
                    holder.tvProfilePhoto.setText(R.string.approved);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorGreen));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("UNAPPROVED")){
                    holder.tvProfilePhoto.setText(R.string.pending);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorOrange1));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("REJECTED")){
                    holder.tvProfilePhoto.setText(R.string.rejected);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorRed));
                }else {
                    holder.tvProfilePhoto.setText(R.string.upload_photo);
                }
            } else if (position ==3) {

                if(objJobs.getApproveStatus().equalsIgnoreCase("APPROVED")){
                    holder.tvProfilePhoto.setText(R.string.approved);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorGreen));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("UNAPPROVED")){
                    holder.tvProfilePhoto.setText(R.string.pending);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorOrange1));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("REJECTED")){
                    holder.tvProfilePhoto.setText(R.string.rejected);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorRed));
                }else {
                    holder.tvProfilePhoto.setText(R.string.upload_photo);
                }
            } else if (position ==4) {

                if(objJobs.getApproveStatus().equalsIgnoreCase("APPROVED")){
                    holder.tvProfilePhoto.setText(R.string.approved);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorGreen));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("UNAPPROVED")){
                    holder.tvProfilePhoto.setText(R.string.pending);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorOrange1));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("REJECTED")){
                    holder.tvProfilePhoto.setText(R.string.rejected);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorRed));
                }else {
                    holder.tvProfilePhoto.setText(R.string.upload_photo);
                }
            } else if (position ==5) {

                if(objJobs.getApproveStatus().equalsIgnoreCase("APPROVED")){
                    holder.tvProfilePhoto.setText(R.string.approved);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorGreen));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("UNAPPROVED")){
                    holder.tvProfilePhoto.setText(R.string.pending);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorOrange1));
                }else if(objJobs.getApproveStatus().equalsIgnoreCase("REJECTED")){
                    holder.tvProfilePhoto.setText(R.string.rejected);
                    holder.lltext.setBackgroundColor(activity.getResources().getColor(R.color.transcolorRed));
                }else {
                    holder.tvProfilePhoto.setText(R.string.upload_photo);
                }
            }

        if(! objJobs.getImageURL().trim().equalsIgnoreCase(""))
        {

            if (GenderType.equalsIgnoreCase("Female")) {
                Glide.with(activity)
                        .load(objJobs.getImageURL())
                        .placeholder(R.drawable.female_placeholder)
                        .into(((MyViewHolder) holder).imgBusinessImage);
            }
            else{
                Glide.with(activity)
                        .load(objJobs.getImageURL())
                        .placeholder(R.drawable.male_placeholder)
                        .into(((MyViewHolder) holder).imgBusinessImage);
            }

            holder.imgBusinessImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(objJobs.getApproveStatus().equalsIgnoreCase("APPROVED")){
                        showFullScreen(objJobs.getImageURL());
                    }else if(objJobs.getApproveStatus().equalsIgnoreCase("UNAPPROVED")){
                        showFullScreen(objJobs.getImageURL());
                    }else if(objJobs.getApproveStatus().equalsIgnoreCase("REJECTED")){
                       // holder.tvProfilePhoto.setText(R.string.rejected);
                    }
                }
            });

        }

        ((MyViewHolder) holder).imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((MyViewHolder) holder).imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String imagePath=objJobs.getImageURL().toString().trim();
                if(!imagePath.equalsIgnoreCase("")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    alertDialogBuilder.setTitle(activity.getResources().getString(R.string.app_name))
                            .setMessage(activity.getString(R.string.are_you_sure_you_want_to_remove))
                            .setCancelable(false)
                            .setPositiveButton(activity.getString(R.string.remove), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String index = objJobs.getId().toString().trim();

                                    if (NetworkConnection.hasConnection(activity)){
                                        if(imagePath.contains("male-upload-photo.jpg") || imagePath.contains("female-upload-photo.jpg")|| imagePath.contains("male-photo-pending-approval.jpg")|| imagePath.contains("female-photo-pending-approval.jpg")){
                                            AppConstants.setToastStr(activity,activity.getString(R.string.please_upload_photo));
                                        }else {
                                            RemovePhoto(matri_id, index, String.valueOf(position));
                                        }
                                    }else
                                    {
                                        AppConstants.CheckConnection((Activity) activity);
                                    }

                                }
                            })
                            .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                else
                {
                    AppConstants.setToastStr(activity,"Image not available.");
                }
            }
        });

      /*  ((MyViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CropImage.activity(null)
                        .setAllowRotation(false)
                        .setAllowFlipping(false)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity);

                Log.d("test", "pos is = "+String.valueOf(position)+1);

            }
        });*/

        //uploadImage(matri_id,strFilePath, String.valueOf(position));

    }

    public void showFullScreen(String imagePath)
    {
        activity.startActivity(new Intent(activity, FullScreenImageActivity.class).putExtra("fullImage",imagePath));
    }

    @Override
    public int getItemCount()
    {
        return arrPopularJobList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView imgBusinessImage,imgEdit,imgDelete;
        public CardView cardView;
        public ProgressBar progressBar1;
        public TextView tvProfilePhoto;
        public LinearLayout lltext;

        public MyViewHolder(View view)
        {
            super(view);


            imgBusinessImage = (ImageView) view.findViewById(R.id.imgBusinessImage);
            imgEdit = (ImageView) view.findViewById(R.id.imgEdit);
            imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
            progressBar1= (ProgressBar) view.findViewById(R.id.progressBar1);
            lltext = view.findViewById(R.id.lltext);
            tvProfilePhoto = view.findViewById(R.id.tvProfilePhoto);
            cardView =  view.findViewById(R.id.cardView);

            prefUpdate= PreferenceManager.getDefaultSharedPreferences(activity);
            matri_id= prefUpdate.getString("matri_id","");

        }
    }


    private void RemovePhoto(String MatriId, String strIndex, final String position)
    {
        progresDialog =new ProgressDialog(activity);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(activity.getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
        progresDialog.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramMatriId = params[0];
                String paramIndex = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"remove_photo.php";
                Log.e("URL", "== "+URL);
                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPAir = new BasicNameValuePair("matri_id", paramMatriId);
                BasicNameValuePair IndexPAir = new BasicNameValuePair("index", paramIndex);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPAir);
                nameValuePairList.add(IndexPAir);


                try
                {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    Log.e("Parametters Array=", "== "+URL+(nameValuePairList.toString().trim().replaceAll(",","&")));
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
                progresDialog.dismiss();
                Log.e("--Remove Photo --", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);
                    String status=obj.getString("status");

                    if(status.equalsIgnoreCase("1"))
                    {
                        String message=obj.getString("message");
                        AppConstants.setToastStr(activity, message);
                        int pos= Integer.valueOf(position);
                        arrPopularJobList.get(Integer.valueOf(position)).setApproveStatus("no_image");
                        arrPopularJobList.get(Integer.valueOf(position)).setImageURL("http://saathiya.sairoses.com/img/app_img/male-photo-protected.jpg");
                        //refreshAt(pos);
                        notifyDataSetChanged();

                    }else
                    {
                        String msgError=obj.getString("message");

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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

                } catch (Throwable t)
                {

                }


            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(MatriId,strIndex,position);
    }


    public void removeAt(int position)
    {
        arrPopularJobList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrPopularJobList.size());
    }

   /* public void refreshAt(int position)
    {
        notifyItemChanged(position);
        notifyItemRangeChanged(position, arrPopularJobList.size());
    }*/

    /*public interface OnClickImageListener{
        void onClick(View view, String path);
    }*/


    private void Load_image(String description, ImageView imageView/*, final ProgressBar progressBar*/) {
//        progressBar.setVisibility(View.GONE);
//        Picasso.with(activity).load(description).into(imageView);

        if (description.equals("")) {
            //progressBar.setVisibility(View.GONE);
            Log.d("TAG","IF");
            Glide.with(activity)
                    .load(R.mipmap.ic_launcher)
                    .into(imageView);
        } else {
            Log.d("TAG","ELSE");
           /* Glide.with(activity)
                    .load(description)
                    //.placeholder(R.drawable.cities_icon)
                    //.error(R.mipmap.ic_launcher_round)
                    //.fit()
                    //.centerCrop()
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            //progressBar.setVisibility(View.GONE);
                        }
                    });*/
        }
    }

}
