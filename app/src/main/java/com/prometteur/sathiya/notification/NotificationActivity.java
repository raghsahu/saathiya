package com.prometteur.sathiya.notification;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prometteur.sathiya.BaseActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.prometteur.sathiya.BaseActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.NotificationAdapter;
import com.prometteur.sathiya.beans.beanNotification;
import com.prometteur.sathiya.home.HomeActivity;
import com.prometteur.sathiya.home.SecondHomeActivity;
import com.prometteur.sathiya.utills.AppConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


import cz.msebera.android.httpclient.Header;

import static com.prometteur.sathiya.utills.AppConstants.isNotification;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationActivity extends BaseActivity {

    ImageView btnBack;
    TextView textviewHeaderText, textviewSignUp;
    SwipeRefreshLayout refresh;
    SharedPreferences prefUpdate;
    private static final String ARG_SECTION_NUMBER = "section_number";
    ArrayList<beanNotification> notification_models = new ArrayList<>();
    NotificationAdapter adapter_notification;
    TextView textEmptyView;
    RecyclerView rv_notification;
    BaseActivity nActivity=NotificationActivity.this;

    public NotificationActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        refresh = findViewById(R.id.refresh);
        textEmptyView = findViewById(R.id.textEmptyView);
        rv_notification = findViewById(R.id.rv_notification);

        btnBack = (ImageView) findViewById(R.id.ivBackArrowimg);
        textviewHeaderText = (TextView) findViewById(R.id.textviewHeaderText);
        textviewSignUp = (TextView) findViewById(R.id.textviewSignUp);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {

                CallAllnotification();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        }


        @Override
        protected void onResume () {
            super.onResume();
            CallAllnotification();
        }

        @Override
        public void onBackPressed () {
            super.onBackPressed();
            Intent intent = new Intent(nActivity, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        }

        public void CallAllnotification () {
            refresh.setRefreshing(true);
            notification_models = new ArrayList<>();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("matri_id", prefUpdate.getString("matri_id", ""));
            params.put("gender", prefUpdate.getString("gender", ""));

            Log.e("",""+prefUpdate.getString("matri_id", ""));
            Log.e("",""+prefUpdate.getString("gender", ""));
            client.post(AppConstants.MAIN_URL + "app_notification.php", params, new TextHttpResponseHandler() {
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    refresh.setRefreshing(false);

                    try {
                        JSONObject obj = new JSONObject(responseString);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            JSONObject responseData = obj.getJSONObject("responseData");
                            Log.e("respone", responseData + "");
                            if (responseData.has("1")) {
                                Iterator<String> resIter = responseData.keys();

                                while (resIter.hasNext()) {

                                    String key = resIter.next();
                                    JSONObject resItem = responseData.getJSONObject(key);

                                    beanNotification model = new beanNotification();
//                                model.setReceiver_id(resItem.getString("receiver_id"));
//                                model.setSender_id(resItem.getString("sender_id"));
                                    // model.setReminder_view_status(resItem.getString("reminder_view_status"));
                                    model.setReminder_mes_type(resItem.getString("notification_type"));
                                    model.setImgProfileUrl(resItem.getString("user_profile_picture"));
                                    model.setMatriId(resItem.getString("matri_id"));
                                    model.setNotification(resItem.getString("notification"));
                                    model.setDate(resItem.getString("send_date"));
                                    notification_models.add(model);
                                }

                                if (notification_models.size() > 0) {
                                    rv_notification.setVisibility(View.VISIBLE);
                                    textEmptyView.setVisibility(View.GONE);

                                    LinearLayoutManager manager = new LinearLayoutManager(nActivity, LinearLayoutManager.VERTICAL, false);
                                    rv_notification.setLayoutManager(manager);
                                    adapter_notification = new NotificationAdapter(notification_models, nActivity);
                                    rv_notification.setAdapter(adapter_notification);

                                } else {
                                    rv_notification.setVisibility(View.GONE);
                                    textEmptyView.setVisibility(View.VISIBLE);
                                }
                            }
                            refresh.setRefreshing(false);
                            try {
                                isNotification = false;
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        } else {
                            refresh.setRefreshing(false);
                            Log.e("respone", status + "");
                            textEmptyView.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception t) {
                        refresh.setRefreshing(false);

                        Log.d("ERRRR", t.toString());
                    }
                }
            });
        }


        public void CallApIClearNotiFications () {
            refresh.setRefreshing(true);
            notification_models = new ArrayList<>();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("matri_id", prefUpdate.getString("matri_id", ""));
            params.put("hide_notification", "No");

            client.post(AppConstants.MAIN_URL + "remove_notification.php", params, new TextHttpResponseHandler() {
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                }

                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    refresh.setRefreshing(false);

                    try {
                        JSONObject obj = new JSONObject(responseString);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            JSONObject responseData = obj.getJSONObject("responseData");
                            Log.e("responess", responseData + "");
                            if (responseData.has("1")) {
                                Iterator<String> resIter = responseData.keys();


                                notification_models.removeAll(notification_models);
                                rv_notification.setVisibility(View.GONE);
                                rv_notification.setAdapter(null);
                                textEmptyView.setVisibility(View.VISIBLE);
                                CallAllnotification();
                            }
                            refresh.setRefreshing(false);
                        } else {
                            refresh.setRefreshing(false);
                            Log.e("respone", status + "");

                            textEmptyView.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception t) {
                        refresh.setRefreshing(false);

                        Log.d("ERRRR", t.toString());
                    }
                }
            });
        }
    }
