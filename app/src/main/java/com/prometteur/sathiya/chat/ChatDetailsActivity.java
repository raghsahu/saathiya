package com.prometteur.sathiya.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.prometteur.sathiya.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.adapters.PhotosAdapter;
import com.prometteur.sathiya.beans.beanPhotos;
import com.prometteur.sathiya.model.chatmodel.Consersation;
import com.prometteur.sathiya.model.chatmodel.Message;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.MySingleton;
import com.prometteur.sathiya.utills.SharedPreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.prometteur.sathiya.chat.FriendsFragment.openChatOnce;


public class ChatDetailsActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView recyclerChat;
    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    private ListMessageAdapter adapter;
    private String roomId;
    private ArrayList<CharSequence> idFriend;
    private Consersation consersation;
    private ImageView btnSend, ivSmile, ivBackArrowimg;
    private EditText editWriteMessage;
    TextView tvProfileName;
    private LinearLayoutManager linearLayoutManager;
    public static HashMap<String, Bitmap> bitmapAvataFriend;
    public String bitmapAvataUser;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA56bn66g:APA91bH9DqGdbu41_5X1bsqgbA8nqZV8V-j2awIb2uoDOePubU6pSIBJGIskCti9jm3I7HEIbbsNHald0ggf9cFI-qLfv7Z0xZQEjSrpnrSctoSzGtuRB7gq8SePho7XOuXCiugaLOXi";
    final private String contentType = "application/json";
    SharedPreferences prefUpdate;
    beanPhotos profileOthers;
    public static String strEmailOthers;
CircleImageView civProfileImg;
String isBlocked="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intentData = getIntent();
        idFriend = intentData.getCharSequenceArrayListExtra(AppConstants.INTENT_KEY_CHAT_ID);
        roomId = intentData.getStringExtra(AppConstants.INTENT_KEY_CHAT_ROOM_ID);
        String nameFriend = intentData.getStringExtra(AppConstants.INTENT_KEY_CHAT_FRIEND);
        if(intentData.getStringExtra("isBlock")!=null) {
            isBlocked = intentData.getStringExtra("isBlock");
        }
        civProfileImg=findViewById(R.id.civProfileImg);
        viewProfileOthers(strEmailOthers);
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(ChatDetailsActivity.this);


        consersation = new Consersation();
        ivBackArrowimg = findViewById(R.id.ivBackArrowimg);
        btnSend = findViewById(R.id.btnSend);
        ivSmile = findViewById(R.id.ivSmile);
        btnSend.setOnClickListener(this);
        ivSmile.setOnClickListener(this);

         bitmapAvataUser = prefUpdate.getString("profile_image", ""); //SharedPreferenceHelper.getInstance(ChatDetailsActivity.this).getUserInfo().avata;
        /*if (!base64AvataUser.equals(AppConstants.STR_DEFAULT_BASE64)) {
            byte[] decodedString = Base64.decode(base64AvataUser, Base64.DEFAULT);
            bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } else {
            bitmapAvataUser = null;
        }*/

        editWriteMessage = (EditText) findViewById(R.id.editWriteMessage);
        tvProfileName = findViewById(R.id.tvProfileName);
        if (idFriend != null && nameFriend != null) {
            tvProfileName.setText(nameFriend);
            //   getSupportActionBar().setTitle(nameFriend);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
            recyclerChat.setLayoutManager(linearLayoutManager);
            adapter = new ListMessageAdapter(this, consersation, bitmapAvataFriend, bitmapAvataUser);
            FirebaseDatabase.getInstance().getReference().child("message/" + roomId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        Message newMessage = new Message();
                        newMessage.idSender = (String) mapMessage.get("idSender");
                        newMessage.idReceiver = (String) mapMessage.get("idReceiver");
                        newMessage.text = (String) mapMessage.get("text");
                        newMessage.timestamp = (long) mapMessage.get("timestamp");
                        consersation.getListMessageData().add(newMessage);
                        adapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            recyclerChat.setAdapter(adapter);
        }

        ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent result = new Intent();
            result.putExtra("idFriend", idFriend.get(0));
            setResult(RESULT_OK, result);
            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("idFriend", idFriend.get(0));
        setResult(RESULT_OK, result);
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        openChatOnce=true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSend) {
            if(!isBlocked.equalsIgnoreCase("1")) {
                String content = editWriteMessage.getText().toString().trim();
                if (content.length() > 0) {
                    editWriteMessage.setText("");
                    Message newMessage = new Message();
                    newMessage.text = content;
                    newMessage.idSender = AppConstants.UID;
                    newMessage.idReceiver = roomId;
                    newMessage.type = "text";
                    newMessage.timestamp = System.currentTimeMillis();
                    FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                    sendNotification(newMessage.text, idFriend.get(0).toString());
                }
            }else
            {
                AppConstants.setToastStr(ChatDetailsActivity.this,getString(R.string.this_member_has_blocked_you_for_message));
            }
        } else if (view.getId() == R.id.ivSmile) {

            editWriteMessage.setText("");
            Message newMessage = new Message();
            newMessage.text = "http://fdsfdsfdsf.png";
            newMessage.type = "image";
            newMessage.idSender = AppConstants.UID;
            newMessage.idReceiver = roomId;
            newMessage.timestamp = System.currentTimeMillis();
            FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
            sendNotification(newMessage.text, idFriend.get(0).toString());

        }
    }

    private void sendNotification(String message, String userId) {
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("name", "New Message");
            notifcationBody.put("chat_msg", message);
            notifcationBody.put("sender_id", userId);  //reciever of messgae

            notification.put("to", "/topics/chats");
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e("Chat Notification error", "onCreate: " + e.getMessage());
        }
        sendNotification(notification);

    }


    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private Consersation consersation;
        private HashMap<String, Bitmap> bitmapAvata;
        private HashMap<String, DatabaseReference> bitmapAvataDB;
        private String bitmapAvataUser;

        public ListMessageAdapter(Context context, Consersation consersation, HashMap<String, Bitmap> bitmapAvata, String bitmapAvataUser) {
            this.context = context;
            this.consersation = consersation;
            this.bitmapAvata = bitmapAvata;
            this.bitmapAvataUser = bitmapAvataUser;
            bitmapAvataDB = new HashMap<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ChatDetailsActivity.VIEW_TYPE_FRIEND_MESSAGE) {
                View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_friend, parent, false);
                return new ItemMessageFriendHolder(view);
            } else if (viewType == ChatDetailsActivity.VIEW_TYPE_USER_MESSAGE) {
                View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_user, parent, false);
                return new ItemMessageUserHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemMessageFriendHolder) {
                ((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
                ((ItemMessageFriendHolder) holder).textTimeStamp.setText(getTimestamp(consersation.getListMessageData().get(position).timestamp));
                Bitmap currentAvata = bitmapAvata.get(consersation.getListMessageData().get(position).idSender);
                /*if (currentAvata != null) {
                    ((ItemMessageFriendHolder) holder).avata.setImageBitmap(currentAvata);
                } else {
                    final String id = consersation.getListMessageData().get(position).idSender;
                    if (bitmapAvataDB.get(id) == null) {
                        bitmapAvataDB.put(id, FirebaseDatabase.getInstance().getReference().child("user/" + id + "/avata"));
                        bitmapAvataDB.get(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    String avataStr = (String) dataSnapshot.getValue();
                                    if (!avataStr.equals(AppConstants.STR_DEFAULT_BASE64)) {
                                        byte[] decodedString = Base64.decode(avataStr, Base64.DEFAULT);
                                        ChatDetailsActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                    } else {
                                        ChatDetailsActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_avatar));
                                    }
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }*/
if(profileOthers!=null) {
    Glide.with(context).load(profileOthers.getImageURL()).into(((ItemMessageFriendHolder) holder).avata);
}
            } else if (holder instanceof ItemMessageUserHolder) {
                ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
                ((ItemMessageUserHolder) holder).textTimeStampUser.setText(getTimestamp(consersation.getListMessageData().get(position).timestamp));
                if (bitmapAvataUser != null) {
                    Glide.with(context).load(bitmapAvataUser).into(((ItemMessageUserHolder) holder).avata);
                    //  ((ItemMessageUserHolder) holder).avata.setImageBitmap(bitmapAvataUser);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            return consersation.getListMessageData().get(position).idSender.equals(AppConstants.UID) ? ChatDetailsActivity.VIEW_TYPE_USER_MESSAGE : ChatDetailsActivity.VIEW_TYPE_FRIEND_MESSAGE;
        }

        @Override
        public int getItemCount() {
            return consersation.getListMessageData().size();
        }

        public String getTimestamp(long timestamp) {
        /*SimpleDateFormat dateFormatdef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date=null;
        try {
            date.setTime(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            return dateFormat.format(timestamp);
        }
    }

    class ItemMessageUserHolder extends RecyclerView.ViewHolder {
        public TextView txtContent;
        public TextView textTimeStampUser;
        public CircleImageView avata;

        public ItemMessageUserHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.textContentUser);
            textTimeStampUser = (TextView) itemView.findViewById(R.id.textTimeStampUser);
            avata = (CircleImageView) itemView.findViewById(R.id.imageView2);
        }
    }

    class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
        public TextView txtContent;
        public TextView textTimeStamp;
        public CircleImageView avata;

        public ItemMessageFriendHolder(View itemView) {
            super(itemView);
            txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);
            textTimeStamp = (TextView) itemView.findViewById(R.id.textTimeStamp);
            avata = (CircleImageView) itemView.findViewById(R.id.imageView3);
        }
    }

        private void viewProfileOthers(String strEmail) {

            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    String paramsMatriId = params[0];

                    HttpClient httpClient = new DefaultHttpClient();

                    String URL = AppConstants.MAIN_URL + "get_profile_photo.php";
                    Log.e("matri_search", "== " + URL);

                    HttpPost httpPost = new HttpPost(URL);

                    BasicNameValuePair MatriIdPair = new BasicNameValuePair("email", paramsMatriId);


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

                    Log.e("view_other_profile", "==" + result);

                    try {
                        JSONObject obj = new JSONObject(result);


                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {

                           /* JSONObject responseData = obj.getJSONObject("responseData");
                            JSONObject responseKey = responseData.getJSONObject("1");*/

                            String photo1 = obj.getString("photo").toString().trim();

                            profileOthers = new beanPhotos("1", photo1,"");
                            Glide.with(ChatDetailsActivity.this).load(photo1).into(civProfileImg);
                            adapter.notifyDataSetChanged();
                        } else {
                            String msgError = obj.getString("message");
                            //Toast.makeText((), "" + msgError, Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception t) {
                        Log.e("ndfjdshbjg", t.getMessage());
                    }

                }
            }

            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute(strEmail);
        }


}
