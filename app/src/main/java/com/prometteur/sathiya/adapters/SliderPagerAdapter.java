package com.prometteur.sathiya.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanHobbyImage;
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

public class SliderPagerAdapter extends PagerAdapter {
private LayoutInflater layoutInflater;
    Activity activity;
    List<beanHobbyImage> image_arraylist;
    SharedPreferences prefUpdate;
    String matri_id="";
    public SliderPagerAdapter(Activity activity, List<beanHobbyImage> image_arraylist) {
        this.activity = activity;
        this.image_arraylist = image_arraylist;

        prefUpdate= PreferenceManager.getDefaultSharedPreferences(activity);
        matri_id=prefUpdate.getString("matri_id","");
    }
 
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        View view = layoutInflater.inflate(R.layout.item_photos_slider, container, false);
        ImageView im_slider = (ImageView) view.findViewById(R.id.imgSlide);
        ImageView imgHeart = (ImageView) view.findViewById(R.id.imgHeart);
        TextView tvHobbieName =  view.findViewById(R.id.tvHobbieName);
        Glide.with(activity.getApplicationContext())
                .load(image_arraylist.get(position).getPhoto())
                .placeholder(R.drawable.shape_corner_round_square) // optional
                .error(R.drawable.shape_corner_round_square)         // optional
                .into(im_slider);

        tvHobbieName.setText(""+image_arraylist.get(position).getHobby_name());

        if(image_arraylist.get(position).getIsLiked().equalsIgnoreCase("1"))
        {
            imgHeart.setImageResource(R.drawable.ic_heart);
        }else
        {
            imgHeart.setImageResource(R.drawable.ic_heart_greybg);
        }

        imgHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLike(matri_id,image_arraylist.get(position),imgHeart);
            }
        });
        container.addView(view);
 
        return view;
    }
 
    @Override
    public int getCount() {
        return image_arraylist.size();
    }
 

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }
 
 
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }


    ProgressDialog progresDialog;
    private void setLike(String login_matri_id, beanHobbyImage beanHobby, ImageView imgHeart)
    {
        progresDialog= new ProgressDialog(activity);
        progresDialog.setCancelable(false);
        progresDialog.setMessage(activity.getResources().getString(R.string.Please_Wait));
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
                        setToastStrPinkBg(activity, ""+message);

                        if(beanHobby.getIsLiked().equalsIgnoreCase("1")) {
                            beanHobby.setIsLiked("0");
                            imgHeart.setImageResource(R.drawable.ic_heart_greybg);
                        }else
                        {
                            beanHobby.setIsLiked("1");
                            imgHeart.setImageResource(R.drawable.ic_heart);
//                            homeResultList.get(pos).setei_reqid(""+obj.getString("ei_id"));
                        }
                        notifyDataSetChanged();
                        //refreshAt(pos);

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