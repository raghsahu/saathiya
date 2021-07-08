package com.prometteur.sathiya.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.chat.ChatActivity;
import com.prometteur.sathiya.chat.MessageActivity;
import com.prometteur.sathiya.home.HomeActivity;
import com.prometteur.sathiya.home.SecondHomeActivity;
import com.prometteur.sathiya.utills.AppConstants;

import org.json.JSONObject;

import static com.prometteur.sathiya.utills.AppConstants.isNotification;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    SharedPreferences prefUpdate;


    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("PRINT", "HELLO");
        mContext = getApplicationContext();
        Log.e("getpushdata",""+remoteMessage.getData());
        Log.e("getpushdata",""+remoteMessage.getData());

        prefUpdate = PreferenceManager.getDefaultSharedPreferences(mContext);
        String user_id = prefUpdate.getString("user_id", "");

        if(!user_id.equalsIgnoreCase(""))
        {
            Log.e("getpushdata","user_id "+user_id);
            try {
                JSONObject obj = new JSONObject(remoteMessage.getData());
              /*  String id = obj.getString("id");
                switch (id) {
                    case "201"://message id*/
                        Intent send = new Intent();
                        send.setAction(AppConstants.Action_MessageRecived);
                        sendBroadcast(send);
                       /* if (!MessageActivity.isOpen) {*/
                           /* String title = obj.getString("title");
                            String msg = obj.getString("msg");*/
                           if(obj.getString("name").contains("New Message")) {
                               String userId = prefUpdate.getString("userId", "");
                               AppConstants.UID = userId;
                               Log.e("getpushdata", "userid " + AppConstants.UID);
                               if (AppConstants.UID.equalsIgnoreCase(obj.getString("sender_id"))) {
                                   String title = getString(R.string.new_message);
                                   String msg = obj.getString("chat_msg");
                                   createNotification(title, msg, obj.getString("sender_id"));
                               }
                           }else
                           {
                               String title = obj.getString("name");
                               String msg = obj.getString("chat_msg");
                               createNotification(title, msg, obj.getString("sender_id"));
                               try {
                                   isNotification = true;


                               }catch (Exception e)
                               {
                                   e.printStackTrace();
                               }try {
                                   isNotification = true;

                               }catch (Exception e)
                               {
                                   e.printStackTrace();
                               }
                           }


                       // }
                       /* break;
                    case "202":
                        String title = obj.getString("title");
                        String msg = obj.getString("msg");
                        createNotification(title, msg, "202");
                        break;
                }*/

            } catch (Exception e) {

                Log.d("PRINT", e.toString());
            }
        }


    }

    public void createNotification(String title, String message, String senderId) {
        Intent resultIntent = new Intent(mContext, HomeActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("name",title);
        bundle.putString("chat_msg",message);
        bundle.putString("senderId",senderId);
        resultIntent.putExtra("notificat", bundle);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.drawable.ic_wedding_rings);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }


}

