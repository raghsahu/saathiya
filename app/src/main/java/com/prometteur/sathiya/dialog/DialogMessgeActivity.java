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
import com.prometteur.sathiya.databinding.DialogMessageBinding;
import com.prometteur.sathiya.databinding.DialogWarningBinding;

import static com.prometteur.sathiya.SignUpStep1Activity.strPassword;
import static com.prometteur.sathiya.SignUpStep1Activity.strUsername;

public class DialogMessgeActivity extends BaseActivity {
   DialogMessageBinding warningBinding;
    SharedPreferences prefUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        warningBinding=DialogMessageBinding.inflate(getLayoutInflater());
        setContentView(warningBinding.getRoot());
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(DialogMessgeActivity.this);
        warningBinding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=prefUpdate.edit();
                editor.clear().apply();
                editor.commit();

                Intent intLogin = new Intent(DialogMessgeActivity.this, LoginActivity.class);
                if(strUsername!=null) {
                    intLogin.putExtra("strUsername", strUsername);
                    intLogin.putExtra("strPassword", strPassword);
                }
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
