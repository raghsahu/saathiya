package com.prometteur.sathiya.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.chat.ChatActivity;
import com.prometteur.sathiya.model.chatmodel.Friend;
import com.prometteur.sathiya.model.chatmodel.ListFriend;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.FriendDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class FriendChatService extends Service {
    private static String TAG = "FriendChatService";
    // Binder given to clients
    public final IBinder mBinder = new LocalBinder();
    public Map<String, Boolean> mapMark;
    public Map<String, Query> mapQuery;
    public Map<String, ChildEventListener> mapChildEventListenerMap;
    public Map<String, Bitmap> mapBitmap;
    public ArrayList<String> listKey;
    public ListFriend listFriend;
    public CountDownTimer updateOnline;

    public FriendChatService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mapMark = new HashMap<>();
        mapQuery = new HashMap<>();
        mapChildEventListenerMap = new HashMap<>();
        listFriend = FriendDB.getInstance(this).getListFriend();
        Collections.sort(listFriend.getListFriend(), new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Friend p1 = (Friend) o1;
                Friend p2 = (Friend) o2;
                return p1.timestamp<(p2.timestamp)?0:1;
            }
        });
        listKey = new ArrayList<>();//9850654516
        mapBitmap = new HashMap<>();
        updateOnline = new CountDownTimer(System.currentTimeMillis(), AppConstants.TIME_TO_REFRESH) {
            @Override
            public void onTick(long l) {
                ServiceUtils.updateUserStatus(getApplicationContext());
            }

            @Override
            public void onFinish() {

            }
        };
        updateOnline.start();

        if (listFriend.getListFriend().size() > 0 /*|| listGroup.size() > 0*/) {
            //Dang ky lang nghe cac room tai day
            for (final Friend friend : listFriend.getListFriend()) {
                if (!listKey.contains(friend.idRoom)) {
                    mapQuery.put(friend.idRoom, FirebaseDatabase.getInstance().getReference().child("message/" + friend.idRoom).limitToLast(1));
                    mapChildEventListenerMap.put(friend.idRoom, new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (mapMark.get(friend.idRoom) != null && mapMark.get(friend.idRoom)) {
//                                Toast.makeText(FriendChatService.this, friend.name + ": " + ((HashMap)dataSnapshot.getValue()).get("text"), Toast.LENGTH_SHORT).show();
                                if (mapBitmap.get(friend.idRoom) == null) {
                                    if (!friend.avata.equals(AppConstants.STR_DEFAULT_BASE64)) {
                                        byte[] decodedString = Base64.decode(friend.avata, Base64.DEFAULT);
                                        mapBitmap.put(friend.idRoom, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                    } else {
                                        mapBitmap.put(friend.idRoom, BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_avatar));
                                    }
                                }
                                createNotify(friend.name, (String) ((HashMap) dataSnapshot.getValue()).get("text"), friend.idRoom.hashCode(), mapBitmap.get(friend.idRoom), false);

                            } else {
                                mapMark.put(friend.idRoom, true);
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
                    listKey.add(friend.idRoom);
                }
                mapQuery.get(friend.idRoom).addChildEventListener(mapChildEventListenerMap.get(friend.idRoom));
            }

            /*for (final Group group : listGroup) {
                if (!listKey.contains(group.id)) {
                    mapQuery.put(group.id, FirebaseDatabase.getInstance().getReference().child("message/" + group.id).limitToLast(1));
                    mapChildEventListenerMap.put(group.id, new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (mapMark.get(group.id) != null && mapMark.get(group.id)) {
                                if (mapBitmap.get(group.id) == null) {
                                    mapBitmap.put(group.id, BitmapFactory.decodeResource(getResources(), R.drawable.ic_notify_group));
                                }
                                createNotify(group.groupInfo.get("name"), (String) ((HashMap) dataSnapshot.getValue()).get("text"), group.id.hashCode(), mapBitmap.get(group.id) , true);
                            } else {
                                mapMark.put(group.id, true);
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
                    listKey.add(group.id);
                }
                mapQuery.get(group.id).addChildEventListener(mapChildEventListenerMap.get(group.id));
            }*/

        } else {
            stopSelf();
        }
    }

    public void stopNotify(String id) {
        mapMark.put(id, false);
    }

    public void createNotify(String name, String content, int id, Bitmap icon, boolean isGroup) {
        Intent activityIntent = new Intent(this, ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setLargeIcon(icon)
                .setContentTitle(name)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] { 1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);
       /* if (isGroup) {
            notificationBuilder.setSmallIcon(R.drawable.ic_tab_group);
        } else {*/
            notificationBuilder.setSmallIcon(R.drawable.ic_person_avatar);
//        }
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        notificationManager.notify(id,
                notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "OnStartService");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "OnBindService");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (String id : listKey) {
            mapQuery.get(id).removeEventListener(mapChildEventListenerMap.get(id));
        }
        mapQuery.clear();
        mapChildEventListenerMap.clear();
        mapBitmap.clear();
        updateOnline.cancel();
        Log.d(TAG, "OnDestroyService");
    }

    public class LocalBinder extends Binder {
        public FriendChatService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FriendChatService.this;
        }
    }
}
