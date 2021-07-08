package com.prometteur.sathiya.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.RestAPI.ApiClient;
import com.prometteur.sathiya.RestAPI.ApiInterface;
import com.prometteur.sathiya.RoboPOJO.ProfileManagePhotoResponse;
import com.prometteur.sathiya.adapters.PhotosAdapter;
import com.prometteur.sathiya.beans.beanPhotos;
import com.prometteur.sathiya.databinding.FragmentEditDocumentBinding;
import com.prometteur.sathiya.databinding.FragmentEditPhotosBinding;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import static android.app.Activity.RESULT_OK;
import static com.prometteur.sathiya.profile.EditProfileActivity.tvPhoto;
import static com.prometteur.sathiya.profile.EditProfileActivity.viewPager;


public class EditPhotosFragment extends Fragment {



    public EditPhotosFragment() {
        // Required empty public constructor
    }



    private PhotosAdapter adapterPhotos;
    private ArrayList<beanPhotos> arrPhotosList;
    SharedPreferences prefUpdate;
    ProgressDialog progresDialog;
    String matri_id = "",gender="";
    ImageView imgBussiness;
    String PhotoId = "";
    int position;
    String strFilePath = "";
    public static String TAG="EditPhotosFragment";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    FragmentEditPhotosBinding documentBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        documentBinding=FragmentEditPhotosBinding.inflate(inflater, container, false);
        View view =documentBinding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(getActivity());
        matri_id = prefUpdate.getString("matri_id", "");
        gender = prefUpdate.getString("gender", "");
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        // documentBinding.recyclerPhoto.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        documentBinding.recyclerPhoto.setLayoutManager(mLayoutManager);
        if (NetworkConnection.hasConnection(getActivity())){
            ViewPhoto(matri_id);
        }else
        {
            AppConstants.CheckConnection(getActivity());
        }


        documentBinding.acceptRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (NetworkConnection.hasConnection(getActivity())){
                    ViewPhoto(matri_id);

                }else
                {
                    AppConstants.CheckConnection(getActivity());
                }

            }
        });


        // Upload Photo
        documentBinding.recyclerPhoto.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                try {
                    CardView cardView = child.findViewById(R.id.cardView);
                    ImageView imgEdit = child.findViewById(R.id.imgEdit);

                    imgBussiness = child.findViewById(R.id.imgBusinessImage);
                    position = recyclerView.getChildPosition(cardView);

                    //position = recyclerView.getChildPosition(imgEdit);

                    imgEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "position = " + String.valueOf(position));

                            PhotoId = arrPhotosList.get(position).getId().toString().trim();
                            Log.d(TAG, "photo id = " + PhotoId);

                            CropImage.activity()
                                    .setAllowRotation(false)
                                    .setAllowFlipping(false)
                                    .setAspectRatio(500, 600)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(getActivity(),EditPhotosFragment.this);
                        }
                    });

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final ProgressBar progressBar;
                            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);


                            if (!arrPhotosList.get(position).getImageURL().toString().trim().equalsIgnoreCase("")) {
                               /* Glide.with(getActivity())
                                        .load(arrPhotosList.get(position).getImageURL().toString().trim())
                                        //.transform(new CircleTransform())
                                        .placeholder(R.drawable.loading1)
                                        .error(R.drawable.male)
                                        .into(imgZoomProfilePicture, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                progressBar.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                            }

                                        });*/

                                //relZoomImageView.setVisibility(View.VISIBLE);
                            } else {
                                AppConstants.setToastStr(getActivity(), "Image not available.");
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }


    private void ViewPhoto(String strMatriId) {
      /*  progresDialog = new ProgressDialog(getActivity());
        progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);
        progresDialog.show();*/
        documentBinding.acceptRefresh.setRefreshing(true);

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsMatriId = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "view_photo.php";
                Log.e("matri_search", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair MatriIdPair = new BasicNameValuePair("matri_id", paramsMatriId);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(MatriIdPair);

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

                Log.e("view_photo", "==" + result);

                try {
                    JSONObject obj = new JSONObject(result);

                    arrPhotosList = new ArrayList<beanPhotos>();

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {

                        JSONObject responseData = obj.getJSONObject("responseData");
                        JSONObject responseKey = responseData.getJSONObject("1");

                        String photo1 = responseKey.getString("photo1").toString().trim();
                        String photo2 = responseKey.getString("photo2").toString().trim();
                        String photo3 = responseKey.getString("photo3").toString().trim();
                        String photo4 = responseKey.getString("photo4").toString().trim();
                        String photo5 = responseKey.getString("photo5").toString().trim();
                        String photo6 = responseKey.getString("photo6").toString().trim();

                        String photo1Status = responseKey.getString("photo1_status").toString().trim();
                        String photo2Status = responseKey.getString("photo2_status").toString().trim();
                        String photo3Status = responseKey.getString("photo3_status").toString().trim();
                        String photo4Status = responseKey.getString("photo4_status").toString().trim();
                        String photo5Status = responseKey.getString("photo5_status").toString().trim();
                        String photo6Status = responseKey.getString("photo6_status").toString().trim();

                        arrPhotosList.add(new beanPhotos("1", photo1, photo1Status));
                        arrPhotosList.add(new beanPhotos("2", photo2, photo2Status));
                        arrPhotosList.add(new beanPhotos("3", photo3, photo3Status));
                        arrPhotosList.add(new beanPhotos("4", photo4, photo4Status));
                        arrPhotosList.add(new beanPhotos("5", photo5, photo5Status));
                        arrPhotosList.add(new beanPhotos("6", photo6, photo6Status));

                        if (arrPhotosList.size() > 0) {
                            documentBinding.recyclerPhoto.setVisibility(View.VISIBLE);
                            //textEmptyView.setVisibility(View.GONE);
                            adapterPhotos = new PhotosAdapter(getActivity(), arrPhotosList,gender);
                            documentBinding.recyclerPhoto.setAdapter(adapterPhotos);

                            String imgProfile = arrPhotosList.get(0).getImageURL().toString().trim();

                            SharedPreferences.Editor editor=prefUpdate.edit();
                            editor.putString("profile_image",responseKey.getString("user_profile_picture").toString().trim());
                            editor.apply();
                           /* Glide.with(getActivity())
                                    .load(imgProfile)
                                    .placeholder(R.drawable.loadimage)
                                    .error(R.drawable.ic_profile1)
                                    .into(documentBinding.imgUserPhotos);*/
                        } else {
                            documentBinding.recyclerPhoto.setVisibility(View.GONE);
                        }

                    } else {
                        String msgError = obj.getString("message");
                        AppConstants.setToastStr(getActivity(), "" + msgError);
                    }
                    documentBinding.acceptRefresh.setRefreshing(false);
                } catch (Exception t) {
                    Log.e("ndfjdshbjg",t.getMessage());
                    documentBinding.acceptRefresh.setRefreshing(false);
                }
                documentBinding.acceptRefresh.setRefreshing(false);

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strMatriId);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Glide.with(getActivity())
                        .load(result.getUri())
                        .placeholder(R.drawable.loadimage)
                        .error(R.drawable.ic_profile)
                        .into(imgBussiness);


                strFilePath = result.getUri().toString().replace("file://", "");

                if (!strFilePath.equalsIgnoreCase("")) {

                    if (NetworkConnection.hasConnection(getActivity())){
                        uploadImage(matri_id, strFilePath, PhotoId);

                    }else
                    {
                        AppConstants.CheckConnection(getActivity());
                    }



                } else {
                    AppConstants.setToastStr(getActivity(), "Please select profile picture.");
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                AppConstants.setToastStr(getActivity(), "Cropping failed: " + result.getError());
            }
        }
    }



    private void uploadImage(String strUserId, String strfilePath, final String PhotoId) {


        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        final ProgressDialog progressDialogSendReq = new ProgressDialog(getActivity());
        progressDialogSendReq.setCancelable(false);
        progressDialogSendReq.setMessage(getResources().getString(R.string.Please_Wait));
        progressDialogSendReq.setIndeterminate(true);
        progressDialogSendReq.show();

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), strUserId);
        RequestBody index = RequestBody.create(MediaType.parse("text/plain"), PhotoId);

        File file = new File(strfilePath);
        Log.d("ravi", "file is = " + file.toString());

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image_path", file.getName(), requestFile);


        Call<ProfileManagePhotoResponse> call = apiService.postManagePhotoProfile(user_id, image, index);// uplaod_photo.php


        call.enqueue(new retrofit2.Callback<ProfileManagePhotoResponse>() {
            @Override
            public void onResponse(Call<ProfileManagePhotoResponse> call, retrofit2.Response<ProfileManagePhotoResponse> response) {
                ProfileManagePhotoResponse profileManagePhotoResponse = response.body();
                Log.e("Responce =", "" + profileManagePhotoResponse);

                if (profileManagePhotoResponse.getStatus().equalsIgnoreCase("1")) {
                    String message = profileManagePhotoResponse.getMessage();
                    AppConstants.setToastStrPinkBg(getActivity(), message);

                    String responseImage = profileManagePhotoResponse.getImage();

                    /*if (PhotoId.equalsIgnoreCase("1")) {
                        Glide.with(getActivity())
                                .load(responseImage)
                                .placeholder(R.drawable.loadimage)
                                .into(imgUserPhotos);
                    }*/
                } else {
                    String msgError = profileManagePhotoResponse.getMessage();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("" + msgError).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                progressDialogSendReq.dismiss();
            }

            @Override
            public void onFailure(Call<ProfileManagePhotoResponse> call, Throwable t) {

            }
        });

    }

}