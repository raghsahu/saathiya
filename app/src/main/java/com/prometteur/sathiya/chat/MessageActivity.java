package com.prometteur.sathiya.chat;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prometteur.sathiya.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.MessageAdapter;
import com.prometteur.sathiya.chat.helperClass.ChatInterface;
import com.prometteur.sathiya.chat.helperClass.Chathelper;
import com.prometteur.sathiya.databinding.ActivityChatsBinding;
import com.prometteur.sathiya.databinding.ActivityMessagesBinding;
import com.prometteur.sathiya.model.Chat_Model;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.prometteur.sathiya.utills.AppConstants.sendPushNotification;

public class MessageActivity extends BaseActivity {


    BaseActivity nActivity= MessageActivity.this;
ActivityMessagesBinding messagesBinding;
    public static boolean isOpen = false;
    String toUserName, ToUserId, ToUSerProfile, UserProfile, tokan;

    //  Adapter_OnlineChating adapterOnlineChating;
    SharedPreferences prefUpdate;
    ArrayList<Chat_Model> mesgList;
    MessageAdapter adapterOnlineChating;
    String FromUserId = "";
    ChatInterface chatInterface = new ChatInterface() {
        @Override
        public void NotifyAdapterItem(int i) {

        }

        @Override
        public void displayUsersAndImage() {

        }

        @Override
        public void setMemberNames(String names) {

        }

        @Override
        public void notifyDataSetChanged() {

        }

        @Override
        public void scrollToLast() {

        }

        @Override
        public void showProgressBar() {

        }

        @Override
        public void hideProgressBar() {

        }

        @Override
        public void notifyDataSetChanged(int i) {

        }

        @Override
        public void updateVideoProgress(int i, String id) {

        }

        @Override
        public void DialogModified() {

        }
    };
    Chathelper chathelper;
    boolean isFirst = true;
    Handler handler;
    Runnable runs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messagesBinding=ActivityMessagesBinding.inflate(getLayoutInflater());

        setContentView(messagesBinding.getRoot());

        prefUpdate = PreferenceManager.getDefaultSharedPreferences(nActivity);
        UserProfile = prefUpdate.getString("profile_image", "");
        FromUserId = prefUpdate.getString("matri_id", "");

        ToUserId = getIntent().getStringExtra("mid");
        ToUSerProfile = getIntent().getStringExtra("profile");
        toUserName = getIntent().getStringExtra("uname");
        tokan = getIntent().getStringExtra("tokan");

        messagesBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        initilize();

        chathelper = new Chathelper(nActivity, chatInterface, ToUserId);
        List<Chat_Model> chatList = chathelper.getChatData();
        setAdapterList(chatList);


    }
    public void initilize() {
        // tvEmpty = (TextView) findViewById(R.id.textEmptyView);


        Log.e("cht_token", tokan + " " + toUserName);

        Glide.with(getApplicationContext()).load(ToUSerProfile).into(messagesBinding.civProfileImg);
        messagesBinding.tvProfileName.setText(toUserName + " (" + ToUserId + ")");


        ProgressDialog dialog = new ProgressDialog(nActivity);
        dialog.setCancelable(false);
        dialog.setMessage("Loading..");

        mesgList = new ArrayList<>();
        handler = new Handler();
        runs = new Runnable() {
            public void run() {
                getAllmessages();
            }
        };

        messagesBinding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllmessagesrefresh();
            }
        });

        messagesBinding.ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!messagesBinding.edtMessage.getText().toString().trim().isEmpty() && messagesBinding.edtMessage.getText().toString().trim().length() != 0) {
                    if (mesgList.size() >0)
                        messagesBinding.rvChatUsers.smoothScrollToPosition(mesgList.size() - 1);
                    if (NetworkConnection.hasConnection(nActivity)){
                        setndMessage();


                    }else
                    {
                        AppConstants.CheckConnection(nActivity);
                    }

                } else {
                    AppConstants.setToastStr(nActivity, "Please Enter Message");
                }
            }
        });
    }



    public void getAllmessages() {
        final ArrayList<Chat_Model> tempList = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("to_id", ToUserId);
        params.put("from_id", prefUpdate.getString("matri_id", ""));
        client.post(AppConstants.MAIN_URL + "chat_history.php", params, new TextHttpResponseHandler() {
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("CHAT-HISTORY", responseString);
                try {
                    JSONObject obj = new JSONObject(responseString);
                    String status = obj.getString("status");
                    tempList.clear();
                    mesgList.clear();
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject responseData = obj.getJSONObject("responseData");
                        if (responseData.has("1")) {
                            Iterator<String> resIter = responseData.keys();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            while (resIter.hasNext()) {
                                String key = resIter.next();
                                JSONObject resItem = responseData.getJSONObject(key);
                                Chat_Model model = new Chat_Model();
                                model.setFrom_id(resItem.getString("from_id"));
                                model.setTo_id(resItem.getString("to_id"));
                                Date dd = format.parse(resItem.getString("sent"));
                                SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");
                                model.setDate(format1.format(dd));
                                model.setMsg(resItem.getString("message"));
                                model.setImgReciverProfileurl(resItem.getString("user_profile_picture"));
                                mesgList.add(model);
                            }

                            tempList.addAll(mesgList);
                            setAdapterList(tempList);
                            chathelper.saveChatData(tempList);
                            //handler.postDelayed(runs, 10000);

                        }
                    }
                } catch (Exception e) {
                    Log.e("mmmmmm", e.getMessage());
                }
            }
        });
    }

    //___________when user refresh________________

    public void getAllmessagesrefresh() {
        messagesBinding.refresh.setRefreshing(true);
        final ArrayList<Chat_Model> tempList = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("to_id", ToUserId);
        params.put("from_id", prefUpdate.getString("matri_id", ""));
        client.post(AppConstants.MAIN_URL + "chat_history.php", params, new TextHttpResponseHandler() {
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("CHAT-HISTORY", responseString);
                try {
                    JSONObject obj = new JSONObject(responseString);
                    String status = obj.getString("status");
                    tempList.clear();
                    mesgList.clear();
                    if (status.equalsIgnoreCase("1")) {
                        JSONObject responseData = obj.getJSONObject("responseData");
                        if (responseData.has("1")) {
                            Iterator<String> resIter = responseData.keys();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            while (resIter.hasNext()) {
                                String key = resIter.next();
                                JSONObject resItem = responseData.getJSONObject(key);
                                Chat_Model model = new Chat_Model();
                                model.setFrom_id(resItem.getString("from_id"));
                                model.setTo_id(resItem.getString("to_id"));
                                Date dd = format.parse(resItem.getString("sent"));
                                SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");
                                model.setDate(format1.format(dd));
                                model.setMsg(resItem.getString("message"));
                                model.setImgReciverProfileurl(resItem.getString("user_profile_picture"));
                                mesgList.add(model);
                            }

                            tempList.addAll(mesgList);
                            setAdapterList(tempList);
                            chathelper.saveChatData(tempList);


                        }
                        messagesBinding.refresh.setRefreshing(false);
                    }
                    messagesBinding.refresh.setRefreshing(false);
                } catch (Exception e) {
                    messagesBinding.refresh.setRefreshing(false);
                    Log.e("mmmmmm", e.getMessage());
                }
                messagesBinding.refresh.setRefreshing(false);
            }
        });
    }


    void setndMessage() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("from_id", FromUserId);
        params.put("to_id", ToUserId);
        params.put("message", messagesBinding.edtMessage.getText().toString().trim());
        client.post(AppConstants.MAIN_URL + "chat_insert.php", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("insertchat", responseString);
            }

            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Log.e("insertchat", responseString);
                sendPushNotification(tokan, "Message recived from " + prefUpdate.getString("matri_id", "")
                        , "New Message Received", "201");
                getAllmessages();
                messagesBinding.edtMessage.setText("");
            }
        });
    }

    public void setAdapterList(List<Chat_Model> list){
        LinearLayoutManager manager = new LinearLayoutManager(nActivity);
        manager.setStackFromEnd(true);
        messagesBinding.rvChatUsers.setHasFixedSize(true);
        messagesBinding.rvChatUsers.setLayoutManager(manager);
        adapterOnlineChating = new MessageAdapter(nActivity, list, FromUserId, ToUserId, ToUSerProfile, UserProfile);
        messagesBinding.rvChatUsers.setAdapter(adapterOnlineChating);
        adapterOnlineChating.notifyDataSetChanged();
        messagesBinding.rvChatUsers.smoothScrollToPosition(0);
        isFirst = false;
    }
    @Override
    protected void onStart () {
        super.onStart();
        isOpen = true;

    }

    @Override
    protected void onStop () {
        super.onStop();
        isOpen = false;
        Log.e("Stop", "Stop");
        /*if (runs != null) {
            handler.removeCallbacks(runs);
        }*/
    }

    @Override
    protected void onPause () {
        super.onPause();
        isOpen = false;

    }

    @Override
    protected void onResume () {
        super.onResume();
        isOpen = true;
        // getAllmessages();
    }
}
