package com.prometteur.sathiya.chat;

import static com.prometteur.sathiya.chat.ChatActivity.edtSearch;
import static com.prometteur.sathiya.chat.ChatActivity.imgClose;
import static com.prometteur.sathiya.chat.ChatActivity.imgSearchBack;
import static com.prometteur.sathiya.chat.ChatActivity.ivBackArrowimg;
import static com.prometteur.sathiya.chat.ChatActivity.ivSearch;
import static com.prometteur.sathiya.chat.ChatActivity.linSearch;
import static com.prometteur.sathiya.chat.ChatActivity.tvTitle;
import static com.prometteur.sathiya.chat.ChatDetailsActivity.strEmailOthers;
import static com.prometteur.sathiya.utills.AppConstants.setToastStrPinkBg;
import static com.prometteur.sathiya.utills.AppConstants.vibe;
import static com.prometteur.sathiya.utills.AppConstants.vibrateBig;
import static com.prometteur.sathiya.utills.AppConstants.vibrateSmall;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;
import static com.prometteur.sathiya.utills.AppMethods.shrinkAnim;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.beans.beanUserData;
import com.prometteur.sathiya.fcm.ServiceUtils;
import com.prometteur.sathiya.home.ThirdHomeActivity;
import com.prometteur.sathiya.model.chatmodel.Friend;
import com.prometteur.sathiya.model.chatmodel.ListFriend;
import com.prometteur.sathiya.model.chatmodel.Message;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.FriendDB;
import com.prometteur.sathiya.utills.ImageScaleView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

public class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ACTION_DELETE_FRIEND = "com.android.rivchat.DELETE_FRIEND";
    public static int ACTION_START_CHAT = 1;
    public static boolean openChatOnce = false;
    private static ListFriendsAdapter adapter;
    private static ListFriend dataListFriend = null;
    private static ArrayList<String> listFriendID = null;
    private static ArrayList<String> listFriendEmails = null;
    private static ArrayList<beanUserData> arrShortListedUser;
    public FragFriendClickFloatButton onClickFloatButton;
    String matriId = "";
    SharedPreferences prefUpdate;
    Dialog dialogFindAllFriend;
    String friendEmail;
    Context context;
    private RecyclerView recyclerListFrends;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CountDownTimer detectFriendOnline;
    private BroadcastReceiver deleteFriendReceiver;

    public FriendsFragment() {
        onClickFloatButton = new FragFriendClickFloatButton();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(context);
        matriId = prefUpdate.getString("matri_id", "");
        detectFriendOnline = new CountDownTimer(System.currentTimeMillis(), AppConstants.TIME_TO_REFRESH) {
            @Override
            public void onTick(long l) {
                try {
                    ServiceUtils.updateFriendStatus(context, dataListFriend);
                    ServiceUtils.updateUserStatus(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {

            }
        };
        if (dataListFriend == null) {
            dataListFriend = FriendDB.getInstance(getContext()).getListFriend();
            if (dataListFriend.getListFriend().size() > 0) {
                Collections.sort(dataListFriend.getListFriend(), new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Friend p1 = (Friend) o1;
                        Friend p2 = (Friend) o2;
                        return p1.timestamp < (p2.timestamp) ? 0 : 1;
                    }
                });
                listFriendID = new ArrayList<>();
                listFriendEmails = new ArrayList<>();
                arrShortListedUser = new ArrayList<>();
                for (Friend friend : dataListFriend.getListFriend()) {
                    listFriendID.add(friend.id);
                    listFriendEmails.add(friend.email);
                    // arrShortListedUser.add(new beanUserData());
                }
                detectFriendOnline.start();

            }
        }
        View layout = inflater.inflate(R.layout.fragment_people, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerListFrends = (RecyclerView) layout.findViewById(R.id.recycleListFriend);
        recyclerListFrends.setLayoutManager(linearLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        try {
            Field f = mSwipeRefreshLayout.getClass().getDeclaredField("mCircleView");
            f.setAccessible(true);
            ImageView img = (ImageView) f.get(mSwipeRefreshLayout);
            img.setAlpha(0.0f);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        adapter = new ListFriendsAdapter(getContext(), dataListFriend, this, arrShortListedUser);
        recyclerListFrends.setAdapter(adapter);
        dialogFindAllFriend = showProgress(context);
     /*   if (listFriendID == null) {
            listFriendID = new ArrayList<>();
            dialogFindAllFriend.setMessage(getString(R.string.getting_all_friends));
            dialogFindAllFriend.show();
            getListFriendUId();
        }*/


        deleteFriendReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String idDeleted = intent.getExtras().getString("idFriend");
                for (Friend friend : dataListFriend.getListFriend()) {
                    if (idDeleted.equals(friend.id)) {
                        ArrayList<Friend> friends = dataListFriend.getListFriend();
                        friends.remove(friend);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACTION_DELETE_FRIEND);
        getContext().registerReceiver(deleteFriendReceiver, intentFilter);


        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivBackArrowimg.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
                ivSearch.setVisibility(View.GONE);
                linSearch.setVisibility(View.VISIBLE);
            }
        });

        imgSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivBackArrowimg.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.VISIBLE);
                ivSearch.setVisibility(View.VISIBLE);
                linSearch.setVisibility(View.GONE);
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivBackArrowimg.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.VISIBLE);
                ivSearch.setVisibility(View.VISIBLE);
                linSearch.setVisibility(View.GONE);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(edtSearch.getText().toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!openChatOnce) {
            if (getArguments() != null) {
                if (getArguments().getString("friendEmail") != null) {
                    friendEmail = getArguments().getString("friendEmail");
                    onClickFloatButton.findIDEmail(friendEmail, getContext(), this);
                }
            }
        } else {
            if (listFriendID == null) {
                listFriendID = new ArrayList<>();
                // adapter.notifyDataSetChanged();
                FriendDB.getInstance(getContext()).dropDB();
//                dialogFindAllFriend.setMessage(getString(R.string.getting_all_friends));
                //.show();
                getListFriendUId();
            } else {
                //mSwipeRefreshLayout.setRefreshing(true);
                dialogFindAllFriend.show();
                listFriendID.clear();
                dataListFriend.getListFriend().clear();
                // adapter.notifyDataSetChanged();
                FriendDB.getInstance(getContext()).dropDB();
                detectFriendOnline.cancel();
                getListFriendUId();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getContext().unregisterReceiver(deleteFriendReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ACTION_START_CHAT == requestCode && data != null && ListFriendsAdapter.mapMark != null) {
            ListFriendsAdapter.mapMark.put(data.getStringExtra("idFriend"), false);
        }
    }

    @Override
    public void onRefresh() {
        if (listFriendID != null) {
            listFriendID.clear();
        }
        if (dataListFriend.getListFriend() != null) {
            dataListFriend.getListFriend().clear();
        }
        //  adapter.notifyDataSetChanged();
        FriendDB.getInstance(getContext()).dropDB();
        detectFriendOnline.cancel();
        getListFriendUId();

    }

    /**
     * Lay danh sach ban be tren server
     */
    private void getListFriendUId() {
        FirebaseDatabase.getInstance().getReference().child("friend/" + AppConstants.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                    Iterator listKey = mapRecord.keySet().iterator();
                    if (listFriendEmails != null) {
                        if (listFriendEmails.size() < mapRecord.size()) {
                            while (listKey.hasNext()) {
                                String key = listKey.next().toString();
                                if (!listFriendID.contains(mapRecord.get(key).toString())) {
                                    listFriendID.add(mapRecord.get(key).toString());
                                }
                            }
                        } else {
                            getListFriendUId();
                        }
                    } else {
                        getListFriendUId();
                    }
                    listFriendEmails = new ArrayList<>();
                    getAllFriendInfo(0, FriendsFragment.this);
                } else {
                    if (dialogFindAllFriend != null) {
                        dialogFindAllFriend.dismiss();
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            AppConstants.setToastStr((Activity) context, getString(R.string.messaged_users_not_found));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Dberror", databaseError.getDetails());
                if (dialogFindAllFriend != null) {
                    dialogFindAllFriend.dismiss();
                    mSwipeRefreshLayout.setRefreshing(false);
                    AppConstants.setToastStr((Activity) context, getString(R.string.messaged_users_not_found));
                }
            }
        });
    }

    /**
     * Truy cap bang user lay thong tin id nguoi dung
     */
    private void getAllFriendInfo(final int index, FriendsFragment fragment) {
        try {
            if (index == listFriendID.size() && listFriendID.size() != 0) {

                Collections.sort(dataListFriend.getListFriend(), new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        Friend p1 = (Friend) o1;
                        Friend p2 = (Friend) o2;
                        return p1.timestamp < (p2.timestamp) ? 1 : -1;
                    }
                });
                listFriendEmails = new ArrayList<>();
                for (Friend friend : dataListFriend.getListFriend()) {
                    listFriendEmails.add(friend.email);
                }

                //save list friend
                getUserDetailsByEmails(matriId, TextUtils.join(",", listFriendEmails), fragment);
//            adapter.notifyDataSetChanged();
                if (dialogFindAllFriend != null && dialogFindAllFriend.isShowing()) {
                    dialogFindAllFriend.dismiss();
                }
                mSwipeRefreshLayout.setRefreshing(false);
                detectFriendOnline.start();
            } else {
                if (listFriendID.size() > 0) {
                    final String id = listFriendID.get(index);
                    FirebaseDatabase.getInstance().getReference().child("user/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Friend user = new Friend();
                                HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
                                user.name = (String) mapUserInfo.get("name");
                                user.email = (String) mapUserInfo.get("email");
                                user.avata = (String) mapUserInfo.get("avata");
                                user.id = id;
                                try {
                                    HashMap messageMap = (HashMap) mapUserInfo.get("message");
                                    user.message.timestamp = (long) messageMap.get("timestamp");
                                    user.message.text = (String) messageMap.get("text");
                                    user.timestamp = user.message.timestamp;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                user.idRoom = id.compareTo(AppConstants.UID) > 0 ? (AppConstants.UID + id).hashCode() + "" : "" + (id + AppConstants.UID).hashCode();
                                dataListFriend.getListFriend().add(user);
                                FriendDB.getInstance(getContext()).addFriend(user);
                                listFriendEmails.add(user.email);
                            }
                            getAllFriendInfo(index + 1, fragment);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void getUserDetailsByEmails(String login_matri_id, String strEmails, FriendsFragment fragment) {

        Dialog progresDialog = showProgress(context);
        try {
            if (!progresDialog.isShowing()) {
                progresDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String paramsLoginMatriId = params[0];
                String paramsUserMatriId = params[1];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = "";

                URL = AppConstants.MAIN_URL + "get_data_by_email.php";
                Log.e("get_data_by_email", "== " + URL);


                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("login_matri_id", paramsLoginMatriId);
                BasicNameValuePair UserMatriIdPair = new BasicNameValuePair("emails", paramsUserMatriId);

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

                Log.e("user_by_email", "==" + result);

                try {
                    JSONObject obj = new JSONObject(result);

                    String status = obj.getString("status");

                    if (status.equalsIgnoreCase("1")) {
                        arrShortListedUser = new ArrayList<beanUserData>();
                        JSONArray responseData = obj.getJSONArray("responseData");

                       /* if (responseData.has("1")) {
                            Iterator<String> resIter = responseData.keys();
                            List<String> keyList = new ArrayList<>();*/

                        for (int i = 0; i < responseData.length(); i++) {
                            // keyList.add(resIter.next());


                            JSONObject resItem = responseData.getJSONObject(i);

                            String matri_id1 = resItem.getString("matri_id");
                            String eiId = resItem.getString("ei_id");
                            String user_profile_picture = resItem.getString("profile_picture");
                            String is_blocked = resItem.getString("is_blocked");
                            String is_favourite = resItem.getString("is_favourite");
                            String is_rejected = resItem.getString("rejected_status");
                            String username = resItem.getString("username");

                            beanUserData beanUserData = new beanUserData("", matri_id1, username, "", "", "", "", "", "", "", "", "",
                                    "", "", "", is_blocked, is_favourite, user_profile_picture, eiId, "");
                            beanUserData.setRejectedStatus(is_rejected);
                            arrShortListedUser.add(beanUserData);
                        }
                        adapter = new ListFriendsAdapter(context, dataListFriend, fragment, arrShortListedUser);
                        recyclerListFrends.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        // }
                    } else {
                        listFriendID.clear();
                        dataListFriend.getListFriend().clear();
                        //  adapter.notifyDataSetChanged();
                        FriendDB.getInstance(getContext()).dropDB();
                        detectFriendOnline.cancel();
                        getListFriendUId();
                       /* String msgError = obj.getString("message");
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                        builder.setMessage("" + msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        android.app.AlertDialog alert = builder.create();
                        alert.show();*/
                    }

                    if (progresDialog != null && progresDialog.isShowing()) {
                        progresDialog.dismiss();
                    }
                } catch (Exception t) {
                    Log.e("fjkhgjkfa", t.getMessage());
                    if (progresDialog != null && progresDialog.isShowing()) {
                        progresDialog.dismiss();
                    }
                }
                if (progresDialog != null && progresDialog.isShowing()) {
                    progresDialog.dismiss();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login_matri_id, strEmails);
    }

    public static class FragFriendClickFloatButton implements View.OnClickListener {
        Context context;
        Dialog dialogWait;
        private FriendsFragment fragment;

        public FragFriendClickFloatButton() {
        }

        public FragFriendClickFloatButton getInstance(Context context) {
            this.context = context;
            dialogWait = showProgress(context);
            fragment = new FriendsFragment();
            return this;
        }

        @Override
        public void onClick(final View view) {
           /* new LovelyTextInputDialog(view.getContext(), R.style.EditTextTintTheme)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle("Add friend")
                    .setMessage("Enter friend email")
                    .setIcon(R.drawable.ic_add_friend)
                    .setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    .setInputFilter("Email not found", new LovelyTextInputDialog.TextFilter() {
                        @Override
                        public boolean check(String text) {
                            Pattern VALID_EMAIL_ADDRESS_REGEX =
                                    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(text);
                            return matcher.find();
                        }
                    })
                    .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                        @Override
                        public void onTextInputConfirmed(String text) {
                            //Tim id user id
                            findIDEmail(text);
                            //Check xem da ton tai ban ghi friend chua
                            //Ghi them 1 ban ghi
                        }
                    })
                    .show();*/
        }

        /**
         * TIm id cua email tren server
         *
         * @param email
         */
        public void findIDEmail(String email, Context context, FriendsFragment fragment) {

            FirebaseDatabase.getInstance().getReference().child("user").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // dialogWait.dismiss();
                    if (dataSnapshot.getValue() == null) {
                        //email not found
                        AppConstants.setToastStr((Activity) context, context.getString(R.string.email_not_found));
                    } else {
                        String id = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                        if (id.equals(AppConstants.UID)) {
                            AppConstants.setToastStr((Activity) context, context.getString(R.string.email_not_found));
                        } else {
                            HashMap userMap = (HashMap) ((HashMap) dataSnapshot.getValue()).get(id);
                            Friend user = new Friend();
                            user.name = (String) userMap.get("name");
                            user.email = (String) userMap.get("email");
                            user.avata = (String) userMap.get("avata");
                            user.id = id;
                            try {
                                HashMap messageMap = (HashMap) userMap.get("message");
                                user.message.timestamp = (long) messageMap.get("timestamp");
                                user.message.text = (String) messageMap.get("text");
                                user.timestamp = user.message.timestamp;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            user.idRoom = id.compareTo(AppConstants.UID) > 0 ? (AppConstants.UID + id).hashCode() + "" : "" + (id + AppConstants.UID).hashCode();
                            checkBeforAddFriend(id, user, context, fragment);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("chat", databaseError.getMessage());
                }
            });
        }

        /**
         * Lay danh sach friend cua một UID
         */
        private void checkBeforAddFriend(final String idFriend, Friend userInfo, Context context, FriendsFragment fragment) {
           /* dialogWait.setCancelable(false);
            dialogWait.setTitle("Add friend....");
            dialogWait.show();*/

            //Check xem da ton tai id trong danh sach id chua
            if (listFriendID != null && listFriendID.contains(idFriend)) {  //51an8ts59UOckgYMoxOxMRdOJzn1
                // dialogWait.dismiss();
                Log.e("Exist", "User " + userInfo.email + " has been friend");
            } else {
                addFriend(idFriend, true);
                // listFriendID.add(idFriend);
                //dataListFriend.getListFriend().add(userInfo);
                FriendDB.getInstance(context).addFriend(userInfo);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

            }

            //new added by dhananjay
            strEmailOthers = userInfo.email;
            Intent intent = new Intent(context, ChatDetailsActivity.class);
            intent.putExtra(AppConstants.INTENT_KEY_CHAT_FRIEND, userInfo.name);
            ArrayList<CharSequence> idFriend1 = new ArrayList<CharSequence>();
            idFriend1.add(idFriend);
            intent.putCharSequenceArrayListExtra(AppConstants.INTENT_KEY_CHAT_ID, idFriend1);
            intent.putExtra(AppConstants.INTENT_KEY_CHAT_ROOM_ID, userInfo.idRoom);
            intent.putExtra("isBlock", "0");
            ChatDetailsActivity.bitmapAvataFriend = new HashMap<>();
            if (!userInfo.avata.equals(AppConstants.STR_DEFAULT_BASE64)) {
                byte[] decodedString = Base64.decode(userInfo.avata, Base64.DEFAULT);
                ChatDetailsActivity.bitmapAvataFriend.put(idFriend, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
            } else {
                ChatDetailsActivity.bitmapAvataFriend.put(idFriend, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_avatar));
            }

            ListFriendsAdapter.mapMark.put(idFriend, null);
            try {
                if (fragment != null) {
                    fragment.startActivityForResult(intent, FriendsFragment.ACTION_START_CHAT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Add friend
         *
         * @param idFriend
         */
        private void addFriend(final String idFriend, boolean isIdFriend) {
            if (idFriend != null) {
                if (isIdFriend) {
                    FirebaseDatabase.getInstance().getReference().child("friend/" + AppConstants.UID).push().setValue(idFriend)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        addFriend(idFriend, false);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // dialogWait.dismiss();
                                    AppConstants.setToastStr((Activity) context, context.getString(R.string.failed_to_add_friend_success));
                                }
                            });
                } else {
                    FirebaseDatabase.getInstance().getReference().child("friend/" + idFriend).push().setValue(AppConstants.UID).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                addFriend(null, false);
                                Message newMessage = new Message();
                                newMessage.text = "";
                                newMessage.idSender = "0";
                                newMessage.idReceiver = "0";
                                newMessage.type = "text";
                                newMessage.timestamp = 0;
                                FirebaseDatabase.getInstance().getReference().child("user/" + idFriend + "/message").setValue(newMessage);

                            }
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // dialogWait.dismiss();
                                    AppConstants.setToastStr((Activity) context, context.getString(R.string.failed_to_add_friend_success));
                                }
                            });
                }
            } else {
                // dialogWait.dismiss();
                if (context != null) {
                    AppConstants.setToastStrPinkBg((Activity) context, context.getString(R.string.add_friend_success));
                }
            }
        }


    }

    static class ListFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static Map<String, Boolean> mapMark;
        public Map<String, Query> mapQuery;
        public Map<String, DatabaseReference> mapQueryOnline;
        public Map<String, ChildEventListener> mapChildListener;
        public Map<String, ChildEventListener> mapChildListenerOnline;
        Dialog dialogWaitDeleting;
        ArrayList<beanUserData> arrShortListedUser;
        ArrayList<beanUserData> arrShortListedfilter;
        SharedPreferences prefUpdate;
        String matriId = "";
        Dialog progresDialog;
        private ListFriend listFriend;
        private Context context;
        private FriendsFragment fragment;
        private ArrayList<Friend> arrFilter;

        public ListFriendsAdapter(Context context, ListFriend listFriend, FriendsFragment fragment, ArrayList<beanUserData> arrShortListedUser) {
            this.listFriend = listFriend;
            this.arrShortListedUser = arrShortListedUser;
            this.arrShortListedfilter = new ArrayList<>();
            if (arrShortListedUser != null && arrShortListedUser.size() > 0) {
                this.arrShortListedfilter.addAll(arrShortListedUser);
            }
            this.arrFilter = new ArrayList<Friend>();
            this.arrFilter.addAll(listFriend.getListFriend());
            this.context = context;
            mapQuery = new HashMap<>();
            mapChildListener = new HashMap<>();
            mapMark = new HashMap<>();
            mapChildListenerOnline = new HashMap<>();
            mapQueryOnline = new HashMap<>();
            this.fragment = fragment;
            dialogWaitDeleting = showProgress(context);
            prefUpdate = PreferenceManager.getDefaultSharedPreferences(context);
            matriId = prefUpdate.getString("matri_id", "");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_friend, parent, false);
            return new ItemFriendViewHolder(context, view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            String name = "";
           /* String name = listFriend.getListFriend().get(position).name;
            if(name==null || name.trim().isEmpty()){*/
            try {
                if (arrShortListedUser.size() > 0) {
                    name = arrShortListedUser.get(position).getUsername();
                    listFriend.getListFriend().get(position).name = name;
                    arrFilter.get(position).name = name;
                } else {
                    name = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                name = "";
            }
            // }
            final String id = listFriend.getListFriend().get(position).id;
            final String idRoom = listFriend.getListFriend().get(position).idRoom;
            final String avata = listFriend.getListFriend().get(position).avata;
            final String email = listFriend.getListFriend().get(position).email;
            ((ItemFriendViewHolder) holder).txtName.setText(name);

            String finalName = name;

            ((View) ((ItemFriendViewHolder) holder).txtName.getParent().getParent().getParent())
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (arrShortListedUser.get(position).getRejectedStatus().equalsIgnoreCase("not_rejected")) {
                                ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT);
                                ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT);
                                strEmailOthers = email;
                                Intent intent = new Intent(context, ChatDetailsActivity.class);
                                intent.putExtra(AppConstants.INTENT_KEY_CHAT_FRIEND, finalName);
                                ArrayList<CharSequence> idFriend = new ArrayList<CharSequence>();
                                idFriend.add(id);
                                intent.putCharSequenceArrayListExtra(AppConstants.INTENT_KEY_CHAT_ID, idFriend);
                                intent.putExtra(AppConstants.INTENT_KEY_CHAT_ROOM_ID, idRoom);
                                try {
                                    if (arrShortListedUser.size() > 0) {
                                        intent.putExtra("isBlock", arrShortListedUser.get(position).getIs_blocked());
                                    } else {
                                        intent.putExtra("isBlock", arrShortListedUser.get(position).getIs_blocked());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ChatDetailsActivity.bitmapAvataFriend = new HashMap<>();
                                if (!avata.equals(AppConstants.STR_DEFAULT_BASE64)) {
                                    byte[] decodedString = Base64.decode(avata, Base64.DEFAULT);
                                    ChatDetailsActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                } else {
                                    ChatDetailsActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_avatar));
                                }

                                mapMark.put(id, null);
                                fragment.startActivityForResult(intent, FriendsFragment.ACTION_START_CHAT);
                            }
                        }
                    });

            //nhấn giữ để xóa bạn
            ((View) ((ItemFriendViewHolder) holder).txtName.getParent().getParent().getParent())
                    .setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            String friendName = (String) ((ItemFriendViewHolder) holder).txtName.getText();

                            new AlertDialog.Builder(context)
                                    .setTitle(R.string.delete_friend)
                                    .setMessage(context.getString(R.string.are_you_sure_want_to_delete) + " " + friendName + "?")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            final String idFriendRemoval = listFriend.getListFriend().get(position).id;
                                            dialogWaitDeleting.setTitle(context.getString(R.string.deleting));
                                            dialogWaitDeleting.show();
                                            deleteFriend(idFriendRemoval);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();

                            return true;
                        }
                    });
            //for view profile
            ((View) ((ItemFriendViewHolder) holder).avata)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (arrShortListedUser.get(position).getRejectedStatus().equalsIgnoreCase("not_rejected")) {
                                if (!arrShortListedUser.get(position).getIs_blocked().equalsIgnoreCase("1")) {
                                    if (arrShortListedUser.size() > 0) {
                                        ThirdHomeActivity.matri_id = arrShortListedUser.get(position).getMatri_id();
                                        context.startActivity(new Intent(context, ThirdHomeActivity.class));
                                    }

                                } else {
                                    AppConstants.setToastStr(((Activity) context), context.getString(R.string.this_member_has_blocked_you_for_message));
                                }
                            }
                        }
                    });


            if (listFriend.getListFriend().get(position).message.text.length() > 0) {
                ((ItemFriendViewHolder) holder).txtMessage.setVisibility(View.VISIBLE);
                ((ItemFriendViewHolder) holder).txtTime.setVisibility(View.VISIBLE);
                if (!listFriend.getListFriend().get(position).message.text.startsWith(id)) {
                    ((ItemFriendViewHolder) holder).txtMessage.setText(listFriend.getListFriend().get(position).message.text);
                    ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT);
                    ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT);
                } else {
                    ((ItemFriendViewHolder) holder).txtMessage.setText(listFriend.getListFriend().get(position).message.text.substring((id + "").length()));
                    ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT_BOLD);
                    ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT_BOLD);
                }
                String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(listFriend.getListFriend().get(position).message.timestamp));
                String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));
                if (today.equals(time)) {
                    ((ItemFriendViewHolder) holder).txtTime.setText(new SimpleDateFormat("HH:mm").format(new Date(listFriend.getListFriend().get(position).message.timestamp)));
                } else {
                    ((ItemFriendViewHolder) holder).txtTime.setText(new SimpleDateFormat("MMM d").format(new Date(listFriend.getListFriend().get(position).message.timestamp)));
                }
            } else {
                ((ItemFriendViewHolder) holder).txtMessage.setVisibility(View.GONE);
                ((ItemFriendViewHolder) holder).txtTime.setVisibility(View.GONE);
                if (mapQuery.get(id) == null && mapChildListener.get(id) == null) {
                    mapQuery.put(id, FirebaseDatabase.getInstance().getReference().child("message/" + idRoom).limitToLast(1));
                    mapChildListener.put(id, new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                            if (mapMark.get(id) != null) {
                                if (!mapMark.get(id)) {
                                    listFriend.getListFriend().get(position).message.text = id + mapMessage.get("text");
                                } else {
                                    try {
                                        listFriend.getListFriend().get(position).message.text = (String) mapMessage.get("text");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                notifyDataSetChanged();
                                mapMark.put(id, false);
                            } else {
                                try {
                                    listFriend.getListFriend().get(position).message.text = (String) mapMessage.get("text");
                                    notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                listFriend.getListFriend().get(position).message.timestamp = (long) mapMessage.get("timestamp");
                            } catch (Exception e) {
                                e.printStackTrace();
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
                    mapQuery.get(id).addChildEventListener(mapChildListener.get(id));
                    mapMark.put(id, true);
                } else {
                    mapQuery.get(id).removeEventListener(mapChildListener.get(id));
                    mapQuery.get(id).addChildEventListener(mapChildListener.get(id));
                    mapMark.put(id, true);
                }
            }
            try {
                if (listFriend.getListFriend().get(position).avata.equals(AppConstants.STR_DEFAULT_BASE64)) {
                    if (arrShortListedUser.size() == 0) {
                        ((ItemFriendViewHolder) holder).avata.setImageResource(R.drawable.ic_person_avatar);
                    } else {
                        try {
                            Glide.with(context).load(arrShortListedUser.get(position).getUser_profile_picture()).placeholder(R.drawable.ic_person_avatar).error(R.drawable.ic_person_avatar).into(((ItemFriendViewHolder) holder).avata);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (arrShortListedUser.size() == 0) {
                        byte[] decodedString = Base64.decode(listFriend.getListFriend().get(position).avata, Base64.DEFAULT);
                        Bitmap src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ((ItemFriendViewHolder) holder).avata.setImageBitmap(src);
                    } else {
                        try {
                            Glide.with(context).load(arrShortListedUser.get(position).getUser_profile_picture()).placeholder(R.drawable.ic_person_avatar).error(R.drawable.ic_person_avatar).into(((ItemFriendViewHolder) holder).avata);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ((ItemFriendViewHolder) holder).avata.setImageResource(R.drawable.ic_person_avatar);
            }
            try {
                if (arrShortListedUser.size() != 0) {
                    ((ItemFriendViewHolder) holder).linBlock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String idFriendRemoval = listFriend.getListFriend().get(position).id;
                       /* dialogWaitDeleting.setTitle(context.getString(R.string.deleting));
                        dialogWaitDeleting.show();*/

                            addToBlockRequest(matriId, arrShortListedUser.get(position).getMatri_id(), "0", position, idFriendRemoval);
                        }
                    });

                    String is_interest = arrShortListedUser.get(position).getIs_favourite().toString().trim();

                    if (is_interest.equalsIgnoreCase("1")) {
                        ((ItemFriendViewHolder) holder).RequestType = "Unlike";
                        ((ItemFriendViewHolder) holder).ivLike.setImageResource(R.drawable.ic_heart);
                        ((ItemFriendViewHolder) holder).tvLike.setText(context.getString(R.string.unlike));

                        //holder.btnSendInterest.setVisibility(View.GONE);
                        //holder.btnRemaind.setVisibility(View.VISIBLE);
                    } else {
                        ((ItemFriendViewHolder) holder).RequestType = "Like";
                        ((ItemFriendViewHolder) holder).ivLike.setImageResource(R.drawable.ic_heart_greybg);
                        ((ItemFriendViewHolder) holder).tvLike.setText(context.getString(R.string.like));
                    }


                    ((ItemFriendViewHolder) holder).linInterest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (((ItemFriendViewHolder) holder).RequestType.equalsIgnoreCase("Like")) {
                                String test = "";
                                if (arrShortListedUser.size() > 0) {
                                    test = arrShortListedUser.get(position).getIs_blocked().toString();
                                    if (arrShortListedUser.get(position).getRejectedStatus().equalsIgnoreCase("not_rejected")) {
                                        if (arrShortListedUser.get(position).getIs_blocked().equalsIgnoreCase("1")) {
                                            String msgBlock = context.getString(R.string.this_member_has_blocked_you);
                                            String msgNotPaid = context.getString(R.string.you_are_not_paid_memeber);

                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                                            builder.setMessage(msgBlock).setCancelable(false).setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            android.app.AlertDialog alert = builder.create();
                                            alert.show();
                                        } else {
                                            vibe.vibrate(vibrateBig);
                                            shrinkAnim(((ItemFriendViewHolder) holder).ivLike, context);
                                            sendInterestRequest(matriId, arrShortListedUser.get(position).getMatri_id(), arrShortListedUser.get(position).getIs_favourite(), position, ((ItemFriendViewHolder) holder));
                                        }
                                    }
                                }
                                Log.d("TAG", "CHECK =" + test);


                            } else if (((ItemFriendViewHolder) holder).RequestType.equalsIgnoreCase("Unlike")) {
                                if (arrShortListedUser.size() > 0) {
                                    if (arrShortListedUser.get(position).getRejectedStatus().equalsIgnoreCase("not_rejected")) {
                                        if (arrShortListedUser.get(position).getIs_blocked().equalsIgnoreCase("1")) {
                                            String msgBlock = context.getString(R.string.this_member_has_blocked_you);
                                            String msgNotPaid = context.getString(R.string.you_are_not_paid_memeber);

                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                                            builder.setMessage(msgBlock).setCancelable(false).setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            android.app.AlertDialog alert = builder.create();
                                            alert.show();
                                        } else {
                                            vibe.vibrate(vibrateSmall);
                                            sendInterestRequestRemind(matriId, arrShortListedUser.get(position).getei_reqid(), arrShortListedUser.get(position).getIs_favourite(), position, ((ItemFriendViewHolder) holder));
                                        }
                                    }
                                }
                            }
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mapQueryOnline.get(id) == null && mapChildListenerOnline.get(id) == null) {
                mapQueryOnline.put(id, FirebaseDatabase.getInstance().getReference().child("user/" + id + "/status"));
                mapChildListenerOnline.put(id, new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("isOnline")) {
                            Log.d("FriendsFragment add " + id, (boolean) dataSnapshot.getValue() + "");
                            listFriend.getListFriend().get(position).status.isOnline = (boolean) dataSnapshot.getValue();
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("isOnline")) {
                            try {
                                Log.d("FriendsFragment change " + id, (boolean) dataSnapshot.getValue() + "");
                                listFriend.getListFriend().get(position).status.isOnline = (boolean) dataSnapshot.getValue();
                                notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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
                mapQueryOnline.get(id).addChildEventListener(mapChildListenerOnline.get(id));
            }

          /*  if (listFriend.getListFriend().get(position).status.isOnline) {
                ((ItemFriendViewHolder) holder).avata.setbo(10);
            } else {
                ((ItemFriendViewHolder) holder).avata.setBorderWidth(0);
            }*/
        }

        @Override
        public int getItemCount() {
            return listFriend.getListFriend() != null ? listFriend.getListFriend().size() : 0;
        }

        /**
         * Delete friend
         *
         * @param idFriend
         */
        private void deleteFriend(final String idFriend) {
            if (idFriend != null) {
                FirebaseDatabase.getInstance().getReference().child("friend").child(AppConstants.UID)
                        .orderByValue().equalTo(idFriend).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {
                            //email not found
                            dialogWaitDeleting.dismiss();
                            AppConstants.setToastStr((Activity) context, context.getString(R.string.error_occured_during_deleting_friend));
                        } else {
                            String idRemoval = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                            FirebaseDatabase.getInstance().getReference().child("friend")
                                    .child(AppConstants.UID).child(idRemoval).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            dialogWaitDeleting.dismiss();

                                            AppConstants.setToastStrPinkBg((Activity) context, context.getString(R.string.friend_deleting_successfully));
                                            Intent intentDeleted = new Intent(FriendsFragment.ACTION_DELETE_FRIEND);
                                            intentDeleted.putExtra("idFriend", idFriend);
                                            context.sendBroadcast(intentDeleted);

                                            /*if(listFriendID!=null) {
                                                listFriendID.clear();
                                            }
                                            if(dataListFriend.getListFriend()!=null) {
                                                dataListFriend.getListFriend().clear();
                                            }
                                            //  adapter.notifyDataSetChanged();
                                            FriendDB.getInstance(context).dropDB();
                                            //detectFriendOnline.cancel();
                                            getListFriendUId();*/
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialogWaitDeleting.dismiss();
                                            AppConstants.setToastStr((Activity) context, context.getString(R.string.error_occured_during_deleting_friend));
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                dialogWaitDeleting.dismiss();
                AppConstants.setToastStr((Activity) context, context.getString(R.string.error_occured_during_deleting_friend));
            }
        }

        private void deleteFriendWhileBlock(final String idFriend) {
            if (idFriend != null) {
                FirebaseDatabase.getInstance().getReference().child("friend").child(AppConstants.UID)
                        .orderByValue().equalTo(idFriend).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {
                            //email not found
                            //dialogWaitDeleting.dismiss();
                            AppConstants.setToastStr((Activity) context, context.getString(R.string.error_occured_during_deleting_friend));
                        } else {
                            String idRemoval = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                            FirebaseDatabase.getInstance().getReference().child("friend")
                                    .child(AppConstants.UID).child(idRemoval).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            dialogWaitDeleting.dismiss();

                                            // AppConstants.setToastStr((Activity) context, context.getString(R.string.friend_deleting_successfully));
                                            Intent intentDeleted = new Intent(FriendsFragment.ACTION_DELETE_FRIEND);
                                            intentDeleted.putExtra("idFriend", idFriend);
                                            context.sendBroadcast(intentDeleted);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialogWaitDeleting.dismiss();
                                            // AppConstants.setToastStr((Activity) context, context.getString(R.string.error_occured_during_deleting_friend));
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                dialogWaitDeleting.dismiss();
                AppConstants.setToastStr((Activity) context, context.getString(R.string.error_occured_during_deleting_friend));
            }
        }

        public void filter(String charText) {
            try {
                charText = charText.toLowerCase(Locale.getDefault());
                listFriend.getListFriend().clear();
                arrShortListedUser.clear();
                if (charText.length() == 0) {
                    listFriend.getListFriend().addAll(arrFilter);
                    arrShortListedUser.addAll(arrShortListedfilter);
                } else {
//                for (Friend wp : arrFilter) {
                    for (int i = 0; i < arrFilter.size(); i++) {
                        Friend wp = arrFilter.get(i);
                        beanUserData userData = arrShortListedfilter.get(i);
                        String ProductName = userData.getUsername();

                        if (ProductName.toLowerCase(Locale.getDefault()).contains(charText)) {
                            listFriend.getListFriend().add(wp);
                            arrShortListedUser.add(userData);
                        }
                    }
                }

                if (listFriend.getListFriend().size() == 0) {
                    /*listFriend.getListFriend().addAll(arrFilter);
                    arrShortListedUser.addAll(arrShortListedfilter);*/
                    arrShortListedUser = new ArrayList<>();
                }
                notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private void addToBlockRequest(String login_matri_id, String strMatriId, final String isBlocked, int position, String idFriendRemoval) {
            Dialog progresDialog = showProgress(context);
           /* progresDialog.setCancelable(false);
            progresDialog.setMessage(context.getResources().getString(R.string.Please_Wait));
            progresDialog.setIndeterminate(true);*/
            progresDialog.show();

            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    String paramsLoginMatriId = params[0];
                    String paramsUserMatriId = params[1];

                    HttpClient httpClient = new DefaultHttpClient();

                    String URL = "";
                    if (isBlocked.equalsIgnoreCase("1")) {
                        URL = AppConstants.MAIN_URL + "remove_blocklist.php";
                        Log.e("remove_blocklist", "== " + URL);
                    } else {
                        URL = AppConstants.MAIN_URL + "block_user.php";
                        Log.e("block_user", "== " + URL);
                    }


                    HttpPost httpPost = new HttpPost(URL);

                    BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("matri_id", paramsLoginMatriId);
                    BasicNameValuePair UserMatriIdPair = new BasicNameValuePair("block_matri_id", paramsUserMatriId);

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

                    Log.e("block_user", "==" + result);

                    try {
                        JSONObject obj = new JSONObject(result);

                        String status = obj.getString("status");

                        if (status.equalsIgnoreCase("1")) {
                            //String message=obj.getString("message").toString().trim();

                            if (isBlocked.equalsIgnoreCase("1")) {
                                if (arrShortListedUser.size() > 0) {
                                    arrShortListedUser.get(position).setIs_blocked("0");
                                    setToastStrPinkBg((Activity) context, context.getString(R.string.sucessfully_unblock));
                                }
                            } else {
                                if (arrShortListedUser.size() > 0) {
                                    arrShortListedUser.get(position).setIs_blocked("1");
                                    setToastStrPinkBg((Activity) context, context.getString(R.string.successfully_blocked));
                                    listFriend.getListFriend().remove(position);
                                    arrShortListedUser.remove(position);
                                    deleteFriendWhileBlock(idFriendRemoval);
                                    notifyDataSetChanged();
                                }
                            }
                        } else {
                            String msgError = obj.getString("message");
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                            builder.setMessage("" + msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            android.app.AlertDialog alert = builder.create();
                            alert.show();
                        }

                        progresDialog.dismiss();
                    } catch (Exception t) {
                        Log.e("fjkhgjkfa", t.getMessage());
                        progresDialog.dismiss();
                    }
                    progresDialog.dismiss();

                }
            }

            SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
            sendPostReqAsyncTask.execute(login_matri_id, strMatriId, isBlocked);
        }

        private void sendInterestRequest(String login_matri_id, String strMatriId, final String isFavorite, final int pos, final ItemFriendViewHolder holder) {
            progresDialog = showProgress(context);
            /*progresDialog.setCancelable(false);
            progresDialog.setMessage(context.getResources().getString(R.string.Please_Wait));
            progresDialog.setIndeterminate(true);*/
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

                            holder.ivLike.setImageResource(R.drawable.ic_heart);
                            holder.tvLike.setText(context.getString(R.string.unlike));
                            String message = obj.getString("message").toString().trim();
                            setToastStrPinkBg((Activity) context, "" + message);
                            //arrShortListedUser.remove(pos);

                            if (isFavorite.equalsIgnoreCase("1")) {
                                if (arrShortListedUser.size() > 0) {
                                    arrShortListedUser.get(pos).setIs_favourite("0");
                                }
                            } else {
                                if (arrShortListedUser.size() > 0) {
                                    arrShortListedUser.get(pos).setIs_favourite("1");
                                    arrShortListedUser.get(pos).setei_reqid("" + obj.getString("ei_id"));
                                }
                            }
                            notifyDataSetChanged();

                        } else {
                            String msgError = obj.getString("message");
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                            builder.setMessage("" + msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            android.app.AlertDialog alert = builder.create();
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

        private void sendInterestRequestRemind(String login_matri_id, String strMatriId, final String isFavorite, final int pos, ItemFriendViewHolder holder) {
            progresDialog = showProgress(context);
            /*progresDialog.setCancelable(false);
            progresDialog.setMessage(context.getResources().getString(R.string.Please_Wait));
            progresDialog.setIndeterminate(true);*/
            progresDialog.show();

            class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    String paramsLoginMatriId = params[0];
                    String paramsEiId = params[1];

                    HttpClient httpClient = new DefaultHttpClient();

                    String URL = AppConstants.MAIN_URL + "remove_intrest.php";
                    Log.e("remove_intrest", "== " + URL);

                    HttpPost httpPost = new HttpPost(URL);

                    BasicNameValuePair LoginMatriIdPair = new BasicNameValuePair("matri_id", paramsLoginMatriId);
                    BasicNameValuePair UserMatriIdPair = new BasicNameValuePair("ei_id", paramsEiId);


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
                            holder.ivLike.setImageResource(R.drawable.ic_heart_greybg);
                            holder.tvLike.setText(context.getString(R.string.like));
                            String message = obj.getString("message").toString().trim();
                            setToastStrPinkBg((Activity) context, "" + message);

                            if (isFavorite.equalsIgnoreCase("1")) {
                                if (arrShortListedUser.size() > 0) {
                                    arrShortListedUser.get(pos).setIs_favourite("0");
                                }
                            } else {
                                if (arrShortListedUser.size() > 0) {
                                    arrShortListedUser.get(pos).setIs_favourite("1");
                                }
                            }

                            notifyDataSetChanged();

                        } else {
                            String msgError = obj.getString("message");
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                            builder.setMessage("" + msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            android.app.AlertDialog alert = builder.create();
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
            sendPostReqAsyncTask.execute(login_matri_id, strMatriId);
        }

        class ItemFriendViewHolder extends RecyclerView.ViewHolder {
            public ImageScaleView avata;
            public TextView txtName, txtTime, txtMessage;
            LinearLayout linBlock, linInterest;
            TextView tvLike;
            ImageView ivLike;
            String RequestType;
            private Context context;

            ItemFriendViewHolder(Context context, View itemView) {
                super(itemView);
                avata = itemView.findViewById(R.id.icon_avata);
                txtName = (TextView) itemView.findViewById(R.id.txtName);
                txtTime = (TextView) itemView.findViewById(R.id.txtTime);
                txtMessage = (TextView) itemView.findViewById(R.id.txtMessage);
                tvLike = itemView.findViewById(R.id.tvLike);
                ivLike = itemView.findViewById(R.id.ivLike);
                linBlock = itemView.findViewById(R.id.linBlock);
                linInterest = itemView.findViewById(R.id.linInterest);
                this.context = context;
            }
        }

    }
}