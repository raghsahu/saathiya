package com.prometteur.sathiya.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.prometteur.sathiya.BaseActivity;

import com.prometteur.sathiya.ChangePasswordActivity;
import com.prometteur.sathiya.databinding.ActivitySettingBinding;
import com.prometteur.sathiya.dialog.DialogDeleteWarningActivity;
import com.prometteur.sathiya.dialog.DialogReasonActivity;
import com.prometteur.sathiya.dialog.DialogWarningActivity;
import com.prometteur.sathiya.language.LanguateActivity;

//import android.support.v7.app.BaseActivity;

public class SettingActivity extends BaseActivity {

    BaseActivity nActivity= SettingActivity.this;
ActivitySettingBinding settingBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingBinding=ActivitySettingBinding.inflate(getLayoutInflater());

        setContentView(settingBinding.getRoot());


        settingBinding.ivBackArrowimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        settingBinding.linChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(nActivity, ChangePasswordActivity.class));
            }
        });
        settingBinding.linLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(nActivity, LanguateActivity.class));
            }
        });

        settingBinding.btnDeactAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(nActivity, DialogReasonActivity.class));
            }
        });

        settingBinding.btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(nActivity, DialogDeleteWarningActivity.class));
            }
        });
    }

}
