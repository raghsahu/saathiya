package com.prometteur.sathiya.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.prometteur.sathiya.BaseActivity;

import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.prometteur.sathiya.LoginActivity;
import com.prometteur.sathiya.R;
import com.prometteur.sathiya.SignUpStep1Activity;
import com.prometteur.sathiya.databinding.ActivityChatsBinding;
import com.prometteur.sathiya.fcm.ServiceUtils;
import com.prometteur.sathiya.model.OnlineUsersModel;
import com.prometteur.sathiya.utills.AppConstants;

import java.util.ArrayList;

import static com.prometteur.sathiya.SplashActivity.strLang;

public class ChatActivity extends BaseActivity {

    ArrayList<OnlineUsersModel> list;

    BaseActivity nActivity= ChatActivity.this;
    ActivityChatsBinding chatsBinding;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    String friendEmail;
    static ImageView ivSearch,imgSearchBack,imgClose,ivBackArrowimg;
    static LinearLayout linSearch;
    static TextView tvTitle;
    static EditText edtSearch;
    SharedPreferences prefUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatsBinding=ActivityChatsBinding.inflate(getLayoutInflater());

        setContentView(chatsBinding.getRoot());
        list = new ArrayList<OnlineUsersModel>();
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);

AppConstants.UID=prefUpdate.getString("userId","");
        ivSearch=chatsBinding.ivSearch;
        linSearch=chatsBinding.linSearch;
        imgSearchBack=chatsBinding.imgSearchBack;
        imgClose=chatsBinding.imgClose;
       ivBackArrowimg=chatsBinding.ivBackArrowimg;
       tvTitle=chatsBinding.tvTitle;
        edtSearch=chatsBinding.edtSearch;
      //  ChatSDK.ui().startSplashScreenActivity(this);
      //  ChatSDK.ui().startEditProfileActivity(this,"Saathiya");
        chatsBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });


        friendEmail=getIntent().getStringExtra("friendEmail");
        FriendsFragment friendsFragment = new FriendsFragment();
        Bundle bundle = new Bundle();
        if(friendEmail!=null) {

            bundle.putString("friendEmail", friendEmail);
            friendsFragment.setArguments(bundle);
        }
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameContainer, friendsFragment, "NewFragmentTag");
        ft.commit();
        //floatButton.setOnClickListener(((FriendsFragment) adapter.getItem(0)).onClickFloatButton.getInstance(this));
        initFirebase();

    }
    private void initFirebase() {
        //Khoi tao thanh phan de dang nhap, dang ky
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    AppConstants.UID = user.getUid();
                    prefUpdate = PreferenceManager.getDefaultSharedPreferences(ChatActivity.this);
                    SharedPreferences.Editor editor=prefUpdate.edit();
                    editor.putString("userId",AppConstants.UID);
                    editor.apply();
                } else {
                    ChatActivity.this.finish();
                    // User is signed in
                    SignUpStep1Activity.strPassword=null;SignUpStep1Activity.strUsername=null;
                    startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        ServiceUtils.stopServiceFriendChat(getApplicationContext(), false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy() {
        ServiceUtils.startServiceFriendChat(getApplicationContext());
        super.onDestroy();
    }


}
