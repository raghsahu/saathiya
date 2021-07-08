package com.prometteur.sathiya;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.multidex.MultiDex;

import com.prometteur.sathiya.utills.LocaleUtils;

import java.util.Locale;


public class MyApp extends Application {
    private static MyApp instance;
    SharedPreferences prefUpdate;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this); // add this
    }
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            instance=this;
            prefUpdate = PreferenceManager.getDefaultSharedPreferences(instance);
            SplashActivity.strLang=prefUpdate.getString("selLang","English");
            LocaleUtils.setLocale(new Locale(prefUpdate.getString("selLangCode","en")));
            LocaleUtils.updateConfig(MyApp.getInstance(), getResources().getConfiguration());
            // mandatory
            // it creates the chat configurations
          /*  ChatManager.Configuration mChatConfiguration =
                    new ChatManager.Configuration.Builder("994937662376")
                    .firebaseUrl("https://sathiya-cec80-default-rtdb.firebaseio.com")
                    .storageBucket("sathiya-cec80.appspot.com")
                    .build();*/

            //initChatSDK();
            //firebase();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static MyApp getInstance() {
        return instance;
    }

    public static void setInstance(MyApp instance) {
        MyApp.instance = instance;
    }

/*    public void initChatSDK() {

        //enable persistence must be made before any other usage of FirebaseDatabase instance.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // it creates the chat configurations
        ChatManager.Configuration mChatConfiguration =
                new ChatManager.Configuration.Builder("994937662376")
                        .firebaseUrl("https://sathiya-cec80-default-rtdb.firebaseio.com")
                        .storageBucket("sathiya-cec80.appspot.com")
                        .build();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // assuming you have a login, check if the logged user (converted to IChatUser) is valid
//        if (currentUser != null) {
        if (currentUser != null) {
           *//* IChatUser iChatUser = (IChatUser) IOUtils.getObjectFromFile(instance,
                    _SERIALIZED_CHAT_CONFIGURATION_LOGGED_USER);*//*

            IChatUser iChatUser = new ChatUser();
            iChatUser.setId(currentUser.getUid());
            iChatUser.setEmail(currentUser.getEmail());
            iChatUser.setFullName(currentUser.getDisplayName());
           // iChatUser.setProfilePictureUrl(""+currentUser.getPhotoUrl().getPath());

            ChatManager.start(this, mChatConfiguration, iChatUser);
            Log.i("TAG", "chat has been initialized with success");

//            ChatManager.getInstance().initContactsSyncronizer();

            ChatUI.getInstance().setContext(instance);
            ChatUI.getInstance().enableGroups(false);

            ChatUI.getInstance().setOnMessageClickListener(new OnMessageClickListener() {
                @Override
                public void onMessageLinkClick(TextView message, ClickableSpan clickableSpan) {
                    String text = ((URLSpan) clickableSpan).getURL();

                    Uri uri = Uri.parse(text);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browserIntent);
                }
            });

            // set on new conversation click listener
//            final IChatUser support = new ChatUser("support", "Chat21 Support");
            final IChatUser support = null;
            ChatUI.getInstance().setOnNewConversationClickListener(new OnNewConversationClickListener() {
                @Override
                public void onNewConversationClicked() {
                    if (support != null) {
                        ChatUI.getInstance().openConversationMessagesActivity(support);
                    } else {
                        Intent intent = new Intent(instance, ContactListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // start activity from context

                        startActivity(intent);
                    }
                }
            });

//            // on attach button click listener
//            ChatUI.getInstance().setOnAttachClickListener(new OnAttachClickListener() {
//                @Override
//                public void onAttachClicked(Object object) {
//                    Toast.makeText(instance, "onAttachClickListener", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            // on create group button click listener
//            ChatUI.getInstance().setOnCreateGroupClickListener(new OnCreateGroupClickListener() {
//                @Override
//                public void onCreateGroupClicked() {
//                    Toast.makeText(instance, "setOnCreateGroupClickListener", Toast.LENGTH_SHORT).show();
//                }
//            });
            Log.i("TAG", "ChatUI has been initialized with success");

        } else {
            Log.w("TAG", "chat can't be initialized because chatUser is null");
        }
    }*/

    /*public void firebase() throws Exception {
        String rootPath = "pre_1";

        ChatSDK.builder()
                .setGoogleMaps("AIzaSyBKZroWouYGwdv8rF7eRhOAUDyG2Ov1Jk4")
                .setAnonymousLoginEnabled(false)
//                .setDebugModeEnabled(true)
                .setRemoteConfigEnabled(false)
                .setPublicChatRoomLifetimeMinutes(TimeUnit.HOURS.toMinutes(24))
                .setSendSystemMessageWhenRoleChanges(true)
                .setRemoteConfigEnabled(true)
                .build()

                // Add the network adapter module
                .addModule(
                        FirebaseModule.builder()
                                .setFirebaseRootPath(rootPath)
                                .setFirebaseDatabaseURL("https://sathiya-cec80-default-rtdb.firebaseio.com/")
                                .setDisableClientProfileUpdate(false)
                                .setDevelopmentModeEnabled(true)
                                .build()
                )

                // Add the UI module
                .addModule(UIModule.builder()
                        .setPublicRoomCreationEnabled(false)
                        .setPublicRoomsEnabled(false)
                        .setTheme(R.style.GGTheme)
                        //.setLocationMessagesEnabled(true)
                       // .setAllowBackPressFromMainActivity(true)
                        .build()
                )

                // Add modules to handle file uploads, push notifications
                .addModule(FirebaseUploadModule.shared())
                .addModule(FirebasePushModule.shared())
                .addModule(ProfilePicturesModule.shared())

                .addModule(ExtrasModule.builder(config -> {
                    if (Device.honor(this)) {
                        config.setDrawerEnabled(false);
                    }
                }))

                .addModule(FirebaseUIModule.builder()
                        .setProviders(EmailAuthProvider.PROVIDER_ID, PhoneAuthProvider.PROVIDER_ID)
                        .build()
                )

                // Activate
                .build()
                .activateWithEmail(this,"trimbake10@gmail.com");


        Disposable d = ChatSDK.events().sourceOnMain().subscribe(networkEvent -> {
            networkEvent.debug();
        });

        d = ChatSDK.events().errorSourceOnMain().subscribe(t -> {
            t.printStackTrace();
        });


    }*/

}
