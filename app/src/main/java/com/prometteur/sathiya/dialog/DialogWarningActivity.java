package com.prometteur.sathiya.dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;

import com.prometteur.sathiya.BaseActivity;

import com.prometteur.sathiya.LoginActivity;
import com.prometteur.sathiya.SignUpStep1Activity;
import com.prometteur.sathiya.databinding.ActivitySignupStep2Binding;
import com.prometteur.sathiya.databinding.DialogWarningBinding;

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.SplashActivity.strLangCode;

public class DialogWarningActivity extends BaseActivity {
   DialogWarningBinding warningBinding;
    SharedPreferences prefUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        warningBinding=DialogWarningBinding.inflate(getLayoutInflater());
        setContentView(warningBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(DialogWarningActivity.this);
        warningBinding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=prefUpdate.edit();
                editor.clear();
                editor.putString("selLang",strLang);
                editor.putString("selLangCode",strLangCode);
                editor.apply();
                editor.commit();

                Intent intLogin = new Intent(DialogWarningActivity.this, LoginActivity.class);
                startActivity(intLogin);
                finishAffinity();
            }
        });
    }

    public void closeDialog(View view)
    {
        finish();
    }
}
