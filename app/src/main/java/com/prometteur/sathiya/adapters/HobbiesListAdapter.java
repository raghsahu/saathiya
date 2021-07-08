package com.prometteur.sathiya.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.prometteur.sathiya.beans.beanHobby;
import com.prometteur.sathiya.beans.beanHobbyImage;
import com.prometteur.sathiya.databinding.ItemPhotosBinding;
import com.prometteur.sathiya.dialog.DialogAddPhotoActivity;
import com.prometteur.sathiya.home.FullScreenImageActivity;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.prometteur.sathiya.utills.OnLoadMoreListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import static com.prometteur.sathiya.utills.AppMethods.showProgress;

public class HobbiesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "Predicted Address";
    /*private final SalonListAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }*/
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    Context nContext;
    List<beanHobbyImage> homeResultList;
    Activity nActivity;
    boolean isMapFragment;
    private OnLoadMoreListener mOnLoadMoreListener;

    public HobbiesListAdapter(Activity nActivity, boolean isMapfargment, List<beanHobbyImage> homeResultList) {/*,OnItemClickListener listener) {*/
        this.nContext = nActivity;
        this.nActivity = nActivity;
        this.isMapFragment = isMapfargment;
        this.homeResultList = homeResultList;
        //this.listener = listener;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

           /* View view = LayoutInflater.from(nActivity).inflate(R.layout.recycle_listsaloon_view,
                    parent, false);
            return new SalonListAdapter.ViewHolder(view);*/

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(nActivity).inflate(R.layout.item_photos, parent, false);
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

            if (homeResultList.size() - 1 == position) {
                holder.photosBinding.linAddPhoto.setVisibility(View.VISIBLE);
                holder.photosBinding.linShowPhoto.setVisibility(View.GONE);
                holder.photosBinding.linAddPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nActivity.startActivity(new Intent(nActivity, DialogAddPhotoActivity.class));
                    }
                });
            } else {
                holder.photosBinding.linAddPhoto.setVisibility(View.GONE);
                holder.photosBinding.linShowPhoto.setVisibility(View.VISIBLE);
            }
holder.photosBinding.tvHobbieName.setText(homeResultList.get(position).getHobby_name());
            Glide.with(nActivity)
                    .asBitmap()
                    .override(150, 180) // made the difference
                    .thumbnail(0.5f)
                    .load(homeResultList.get(position).getPhoto())
                    .placeholder(R.drawable.shape_corner_round_square)
                    .error(R.drawable.shape_corner_round_square)
                    .into(new Target<Bitmap>() {
                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {

                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            holder.photosBinding.rivProfileImage.setImageResource(R.drawable.shape_corner_round_square);

                        }

                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.photosBinding.rivProfileImage.setImageBitmap(resource);


                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            holder.photosBinding.rivProfileImage.setImageResource(R.drawable.shape_corner_round_square);

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

            holder.photosBinding.rivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showFullScreen(homeResultList.get(position).getPhoto());
                }
            });

            if(homeResultList.get(position).getApproveStatus().equalsIgnoreCase("APPROVED")){
                holder.photosBinding.tvProfilePhoto.setText(R.string.approved);
                holder.photosBinding.lltext.setBackgroundColor(nActivity.getResources().getColor(R.color.transcolorGreen));
            }else if(homeResultList.get(position).getApproveStatus().equalsIgnoreCase("UNAPPROVED")){
                holder.photosBinding.tvProfilePhoto.setText(R.string.pending);
                holder.photosBinding.lltext.setBackgroundColor(nActivity.getResources().getColor(R.color.transcolorOrange1));
            }else if(homeResultList.get(position).getApproveStatus().equalsIgnoreCase("REJECTED")){
                holder.photosBinding.tvProfilePhoto.setText(R.string.rejected);
                holder.photosBinding.lltext.setBackgroundColor(nActivity.getResources().getColor(R.color.transcolorRed));
            }else {
                holder.photosBinding.tvProfilePhoto.setText(R.string.upload_photo);
            }

            holder.photosBinding.imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NetworkConnection.hasConnection(nActivity)) {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(nActivity);
                            alertDialogBuilder.setTitle(nActivity.getResources().getString(R.string.app_name))
                                    .setMessage(nActivity.getString(R.string.are_you_sure_you_want_to_remove))
                                    .setCancelable(false)
                                    .setPositiveButton(nActivity.getString(R.string.remove), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            getDeletePhoto(homeResultList.get(position).getIntrest_id(),position);

                                        }
                                    })
                                    .setNegativeButton(nActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();


                    } else {
                        AppConstants.CheckConnection(nActivity);
                    }
                }
            });
        } else if (holder1 instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder1;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }
    public void showFullScreen(String imagePath)
    {
        nActivity.startActivity(new Intent(nActivity, FullScreenImageActivity.class).putExtra("fullImage",imagePath));
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

        ItemPhotosBinding photosBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photosBinding = ItemPhotosBinding.bind(itemView);

        }


    }


    private void getDeletePhoto(String interestId, int position) {
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

                String URL = AppConstants.MAIN_URL + "delete_interest_photo.php";
                Log.e("URL", "== " + URL);
                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair InrestIdPair = new BasicNameValuePair("intrest_id", interestId);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(InrestIdPair);
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

                Log.e("--Deleted --", "==" + Ressponce);

                try {
                    JSONObject responseObj = new JSONObject(Ressponce);


                    if (responseObj.getString("status").equalsIgnoreCase("1")) {

                        AppConstants.setToastStrPinkBg(nActivity,responseObj.getString("message"));
                        homeResultList.remove(position);
                        notifyDataSetChanged();
                    }


                    progresDialog11.dismiss();
                } catch (Exception e) {
                    progresDialog11.dismiss();
e.printStackTrace();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

}
