package com.prometteur.sathiya;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
//import com.google.android.material.textfield.TextInputLayout;
//import android.support.v7.app.BaseActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.prometteur.sathiya.BaseActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prometteur.sathiya.fragments.FragmentOtpVerificationBottomSheetDialog;
import com.prometteur.sathiya.home.HomeActivity;
import com.prometteur.sathiya.model.chatmodel.User;
import com.prometteur.sathiya.profile.EditProfileActivity;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.prometteur.sathiya.utills.RequestPermissionHandler;
import com.prometteur.sathiya.utills.SharedPreferenceHelper;

import static com.prometteur.sathiya.fragments.FragmentOtpVerificationBottomSheetDialog.fromPage;
import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppConstants.setToastStrPinkBg;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;


public class LoginActivity extends BaseActivity
{

    TextInputLayout inputPassword;

    Button btnLogin;
    EditText edtEmailId,edtPassword;
    TextView txtForgotPass,tvRegister;
LinearLayout linBack;
    SharedPreferences prefUpdate;
    Dialog progresDialog;
    private FirebaseAuth mAuth;
    RequestPermissionHandler requestPermissionHandler;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
// get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        prefUpdate= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SplashActivity.strLang=prefUpdate.getString("selLang","English");
        init();
        onClick();

        requestPermissionHandler = new RequestPermissionHandler();
        requestPermissionHandler.requestPermission(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE
        }, 123, new RequestPermissionHandler.RequestPermissionListener() {
            @Override
            public void onSuccess() {


            }

            @Override
            public void onFailed()
            {
                setToastStr(LoginActivity.this, "request permission failed");
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    //redirect
                   // updateUI(user);

                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                   // updateUI(null);
                }

            }
        };

        if(getIntent().getStringExtra("strUsername")!=null) {
            if (NetworkConnection.hasConnection(LoginActivity.this)) {
                sendLoginRequest(getIntent().getStringExtra("strUsername"), getIntent().getStringExtra("strPassword"));

            } else {
                AppConstants.CheckConnection(LoginActivity.this);
            }
        }
    }


    public void init()
    {

        btnLogin=(Button)findViewById(R.id.btnLogin);
        edtEmailId=findViewById(R.id.edtEmailId);
        edtPassword=findViewById(R.id.edtPassword);
        txtForgotPass=(TextView)findViewById(R.id.txtForgotPass);
        tvRegister=(TextView)findViewById(R.id.tvRegister);
        linBack=findViewById(R.id.linBack);
        tvRegister.setText(" "+getString(R.string.register_now));

        inputPassword = findViewById(R.id.inputPassword);

    }

    public void onClick(){

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpStep1Activity.class));
            }
        });
        txtForgotPass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtForgotPass.getWindowToken(), 0);
                String strUsername= edtEmailId.getText().toString().trim();
                if(!strUsername.isEmpty() && !strUsername.contains("@")){
                    Intent intLogin = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    intLogin.putExtra("mobileNo",strUsername);
                    LoginActivity.this.startActivity(intLogin);
                }else {
                    Intent intLogin = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    LoginActivity.this.startActivity(intLogin);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnLogin.getWindowToken(), 0);

//startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                String strUsername= edtEmailId.getText().toString().trim();
                String strPassword= edtPassword.getText().toString().trim();

                /*if(strUsername.equalsIgnoreCase("") && strPassword.equalsIgnoreCase(""))
                {
                    //edtEmailId.setError(getResources().getString(R.string.Please_enter_username_and_password));
                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.Please_enter_username_and_password), Toast.LENGTH_LONG).show();
                }else if(strUsername.equalsIgnoreCase(""))
                {
                    //edtEmailId.setError(getResources().getString(R.string.Please_enter_username));
                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.Please_enter_username), Toast.LENGTH_LONG).show();
                }*//*else if(! checkEmail(strUsername))
                {
                    //edtEmailId.setError(getResources().getString(R.string.Please_enter_valid_email_address));
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.Please_enter_valid_email_address), Toast.LENGTH_LONG).show();
                }*//*else if(strPassword.equalsIgnoreCase(""))
                {
                    //edtPassword.setError(getResources().getString(R.string.Please_enter_password));
                    Toast.makeText(LoginActivity.this,getResources().getString(R.string.Please_enter_password), Toast.LENGTH_LONG).show();
                }else
                {*/
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {

                            Log.d("TOKEN__",task.getResult().getToken());
                            AppConstants.tokan = task.getResult().getToken();
                        }
                    });
                    //Toast.makeText(LoginActivity.this,"Cooming Soon",Toast.LENGTH_LONG).show();

                    if (NetworkConnection.hasConnection(LoginActivity.this)){
                        sendLoginRequest(strUsername,strPassword);

                    }else
                    {
                        AppConstants.CheckConnection(LoginActivity.this);
                    }

                //}
            }
        });
        linBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private boolean checkEmail (String email)
    {
        return AppConstants.email_pattern.matcher(email).matches();
    }


    public void SignUpMethod()
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(textviewSignUp.getWindowToken(), 0);

        String signup_step= prefUpdate.getString("signup_step","");

//        if(signup_step.equalsIgnoreCase("1"))
//        {
//            Intent intLogin= new Intent(LoginActivity.this,VerifyMobileNumberActivity.class);
//            startActivity(intLogin);
//        }else if(signup_step.equalsIgnoreCase("2"))
//        {
//            Intent intLogin= new Intent(LoginActivity.this,SignUpStep2Activity.class);
//            startActivity(intLogin);
//        }else if(signup_step.equalsIgnoreCase("3"))
//        {
//            Intent intLogin= new Intent(LoginActivity.this,SignUpStep3Activity.class);
//            startActivity(intLogin);
//        }else if(signup_step.equalsIgnoreCase("4"))
//        {
//            Intent intLogin= new Intent(LoginActivity.this,SignUpStep4Activity.class);
//            startActivity(intLogin);
//        }else
        {
            Intent intLogin= new Intent(LoginActivity.this,SignUpStep1Activity.class);
            startActivity(intLogin);
            finish();
        }
    }

    private void sendLoginRequest(String strUsername, String strPassword)
    {
        progresDialog=showProgress(LoginActivity.this);
      /*  progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);*/
        progresDialog.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {
                String paramUsername = params[0];
                String paramPasswod = params[1];
               // String paramGcmId = params[2];
              //  String paramLanguage = params[3];

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"login.php";
                Log.e("URL", "== "+URL);
                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair UsernamePAir = new BasicNameValuePair("email_id", paramUsername);
                BasicNameValuePair PasswodfiPAir = new BasicNameValuePair("password", paramPasswod);
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", SplashActivity.strLang);
                BasicNameValuePair tokan = new BasicNameValuePair("tokan", AppConstants.tokan);
                /*BasicNameValuePair GCmPAir = new BasicNameValuePair("gcm_id", paramGcmId);
                BasicNameValuePair deviceTypePAir = new BasicNameValuePair("device_type", "android");
                BasicNameValuePair LanguagePAir = new BasicNameValuePair("language", paramLanguage);*/

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(UsernamePAir);
                nameValuePairList.add(PasswodfiPAir);
                nameValuePairList.add(languagePAir);
                nameValuePairList.add(tokan);
                /*nameValuePairList.add(GCmPAir);
                nameValuePairList.add(deviceTypePAir);
                nameValuePairList.add(LanguagePAir);*/

                try
                {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    try
                    {
                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        InputStream inputStream = httpResponse.getEntity().getContent();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String bufferedStrChunk = null;
                        while((bufferedStrChunk = bufferedReader.readLine()) != null)
                        {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("Firstption caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe)
                    {
                        System.out.println("Secondption caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }

                } catch (UnsupportedEncodingException uee)
                {
                    System.out.println("Anption given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                progresDialog.dismiss();
                Log.e("--Login --", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);

                    String status=obj.getString("status");

                    if(status.equalsIgnoreCase("1"))
                    {
                        JSONObject responseData = obj.getJSONObject("responseData");
                        SharedPreferences.Editor editor=prefUpdate.edit();
                        editor.putString("user_id",responseData.getString("user_id"));
                        editor.putString("user_id_r", responseData.getString("user_id"));//for otp verify used
                        editor.putString("email",responseData.getString("email"));
                        editor.putString("firbase_pass",responseData.getString("firebase_password"));
                        editor.putString("firbase_email",responseData.getString("firebase_email"));
                        editor.putString("profile_image",responseData.getString("profile_path"));
                        editor.putString("matri_id",responseData.getString("matri_id"));
                        editor.putString("username",responseData.getString("username"));
                        editor.putString("gender",responseData.getString("gender"));
                        editor.putString("mobile", responseData.getString("phone"));
                        editor.putString("paid_status",responseData.getString("membership_status"));
                        editor.putString("call_package_status",responseData.getString("call_package_status"));
                        editor.putString("city_name","");//responseData.getString("city_name"));

                        editor.apply();

                        String dt = responseData.getString("reg_date");
                        getSharedPreferences("data",MODE_PRIVATE).edit().putString("join_date",dt).apply();
                        getSharedPreferences("data",MODE_PRIVATE).edit().putString("isdisplay","false").apply();
                        Log.d("DEBUG_LOGIN", "ChatLoginActivity.initPasswordIMEAction");

//                    performLogin();
                       /* if(FragmentOtpVerificationBottomSheetDialog.fromPage!=null && FragmentOtpVerificationBottomSheetDialog.fromPage.equalsIgnoreCase("forgotPass")) {
                            getChangeFirebasePassword(responseData.getString("email"),strPassword ,dt, responseData.getString("username"), responseData.getString("matri_id"));
                        }else {*/
                            signIn(responseData.getString("firebase_email"), responseData.getString("firebase_password"), dt, responseData.getString("username"), responseData.getString("matri_id"));
//                        }

                    }else
                    {
                        edtEmailId.setText("");
                        edtPassword.setText("");
                        String message=obj.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(""+message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                } catch (Throwable t)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                    builder.setMessage(getString(R.string.please_enter_valid_username_or_password)).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(strUsername,strPassword/*,GcmId,language1*/);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    /**
     * Action Login
     *
     * @param email
     * @param password
     */
    void signIn(String email, String password,String dt,String name,String matriId) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("TAG", "signInWithEmail:failed", task.getException());
                            createUser( email, password, name,dt,matriId);
                            //AppConstants.setToastStr(LoginActivity.this,getString(R.string.email_not_exist_or_wrong_password));
                        } else {
                            saveUserInfo(name);
                            getCheckBasicDetails(matriId);
                           /* if(getIntent().getStringExtra("strUsername")!=null) {
                                startActivity(new Intent(LoginActivity.this, EditProfileActivity.class).putExtra("page","basicDetails"));
                            }else {
                                Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                myIntent.putExtra("classname", "LoginActivity");
                                myIntent.putExtra("newuser_time", dt);
                                startActivity(myIntent);
                                finish();
                            }*/
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //waitingDialog.dismiss();
                        e.printStackTrace();
                        Log.e("LoginActivity",e.getMessage());
                    }
                });
    }



    void createUser(String email, String password,String username,String dt,String matriId) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // waitingDialog.dismiss();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            AppConstants.setToastStr(LoginActivity.this,getString(R.string.email_exist_or_weak_password));
                        } else {
                            initNewUserInfo(username);
                            Log.e("firebase login","Register and Login success");
                            setToastStrPinkBg(LoginActivity.this, getString(R.string.registration_successful));
                            signIn(email, password, dt, username, matriId);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //waitingDialog.dismiss();
                        if(!e.getMessage().contains("FirebaseTooManyRequestsException")) {
                            if(callReg>2) {
                                callReg++;
                                createUser(email, password, username, dt, matriId);
                            }
                        }
                    }
                })
        ;
    }
    public static int callReg=0;
    private FirebaseUser user;
    void initNewUserInfo(String username) {
        User newUser = new User();
        newUser.email = user.getEmail();
        newUser.name = username;//user.getEmail().substring(0, user.getEmail().indexOf("@"));
        newUser.avata = AppConstants.STR_DEFAULT_BASE64;
        FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser);
    }

    /**
     * Luu thong tin user info cho nguoi dung dang nhap
     */
    void saveUserInfo(String name) {
        FirebaseDatabase.getInstance().getReference().child("user/" + AppConstants.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //  waitingDialog.dismiss();
                HashMap hashUser = (HashMap) dataSnapshot.getValue();
                User userInfo = new User();
                userInfo.name =name; //(String) hashUser.get("name");
                if(hashUser!=null) {
                    userInfo.email = (String) hashUser.get("email");
                    userInfo.avata = (String) hashUser.get("avata");
                }
                SharedPreferenceHelper.getInstance(LoginActivity.this).saveUserInfo(userInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getCheckBasicDetails(String matriId)
    {
        Dialog progresDialog=showProgress(LoginActivity.this);
        /*progresDialog.setCancelable(false);
        progresDialog.setMessage(getResources().getString(R.string.Please_Wait));
        progresDialog.setIndeterminate(true);*/
        progresDialog.show();
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params)
            {

                HttpClient httpClient = new DefaultHttpClient();

                String URL= AppConstants.MAIN_URL +"check_basic_details.php";
                Log.e("URL", "== "+URL);
                HttpPost httpPost = new HttpPost(URL);

                BasicNameValuePair UsernamePAir = new BasicNameValuePair("matri_id", matriId);


                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(UsernamePAir);

                try
                {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                    try
                    {
                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        InputStream inputStream = httpResponse.getEntity().getContent();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String bufferedStrChunk = null;
                        while((bufferedStrChunk = bufferedReader.readLine()) != null)
                        {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out.println("Firstption caz of HttpResponese :" + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe)
                    {
                        System.out.println("Secondption caz of HttpResponse :" + ioe);
                        ioe.printStackTrace();
                    }

                } catch (UnsupportedEncodingException uee)
                {
                    System.out.println("Anption given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                try {
                    if(progresDialog!=null && progresDialog.isShowing()) {
                        progresDialog.dismiss();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                Log.e("--Login --", "=="+result);

                try
                {
                    JSONObject obj = new JSONObject(result);

                    String status=obj.getString("status");

                    if(status.equalsIgnoreCase("1")) {
                        Intent intLogin = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intLogin);
                        finishAffinity();
                    }else {
                        startActivity(new Intent(LoginActivity.this, EditProfileActivity.class).putExtra("page","basicDetails"));
                        finishAffinity();
                    }

                } catch (Throwable t)
                {
                    t.printStackTrace();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

/*public void getChangeFirebasePassword(String email,String password,String dt,String username,String matriId) {
   *//* mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                String UserId = user.getUid();
                // Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.

            }
            else {
                Toast.makeText(LoginActivity.this, "no id got", Toast.LENGTH_SHORT).show();
            }

        }
    };*//*

    AuthCredential credential = EmailAuthProvider
            .getCredential(email, password);

// Prompt the user to re-provide their sign-in credentials
    mAuth.getCurrentUser().reauthenticate(credential)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mAuth.getCurrentUser().updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("ForgotPass", "Password updated");
                                    signIn(email,password,dt,username,matriId);
                                } else {
                                    Log.d("ForgotPass", "Error password not updated");
                                }
                            }
                        });
                    } else {
                        Log.d("ForgotPass", "Error auth failed");
                    }
                }
            });

}*/
}
