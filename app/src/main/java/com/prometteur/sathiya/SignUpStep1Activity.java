package com.prometteur.sathiya;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v7.app.BaseActivity;
//import androidx.appcompat.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.prometteur.sathiya.BaseActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prometteur.sathiya.databinding.ActivitySignupStep2Binding;
import com.prometteur.sathiya.fragments.FragmentOtpVerificationBottomSheetDialog;
import com.prometteur.sathiya.fragments.FragmentSelectlanguageBottomSheetDialog;
import com.prometteur.sathiya.model.chatmodel.User;
import com.prometteur.sathiya.utills.AppConstants;
import com.prometteur.sathiya.utills.NetworkConnection;
import com.prometteur.sathiya.utills.SharedPreferenceHelper;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
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

import static com.prometteur.sathiya.SplashActivity.strLang;
import static com.prometteur.sathiya.fragments.FragmentOtpVerificationBottomSheetDialog.fromPage;
import static com.prometteur.sathiya.utills.AppConstants.isValidPassword;
import static com.prometteur.sathiya.utills.AppConstants.setToastStr;
import static com.prometteur.sathiya.utills.AppConstants.setToastStrPinkBg;
import static com.prometteur.sathiya.utills.AppMethods.showProgress;

//import Adepters.CasteAdapter;
//import Adepters.CountryAdapter;
//import Adepters.CountryCodeAdapter;
//import Adepters.MotherTongueAdapter;
//import Adepters.ProfileCreatedAdapter;
//import Adepters.ReligionAdapter;
//import Models.beanCaste;
//import Models.beanCountries;
//import Models.beanCountryCode;
//import Models.beanMotherTongue;
//import Models.beanProfileCreated;
//import Models.beanReligion;


public class SignUpStep1Activity extends BaseActivity {
    ActivitySignupStep2Binding signupStep;
    SharedPreferences prefUpdate;

    // ProgressBar progressBarSlider;
    private AuthUtils authUtils;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    String strDummyEmail="0";

public static String strPassword=null,strUsername=null;
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signupStep=ActivitySignupStep2Binding.inflate(getLayoutInflater());
        setContentView(signupStep.getRoot());
        signupStep.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    /*if (signupStep.edtFirstName.getText().toString().equalsIgnoreCase("")) {
                        signupStep.edtFirstName.requestFocus();
//                            edtEmailId.setError("Please enter email id.");
//                            Toast.makeText(SignUpStep1Activity.this, "Please enter email id", Toast.LENGTH_SHORT).show();
                        AppConstants.setToastStr(SignUpStep1Activity.this, getString(R.string.please_enter_firstname));
                        return;
                    }if (signupStep.edtLastName.getText().toString().equalsIgnoreCase("")) {
                        signupStep.edtLastName.requestFocus();
//                            edtEmailId.setError("Please enter email id.");
//                            Toast.makeText(SignUpStep1Activity.this, "Please enter email id", Toast.LENGTH_SHORT).show();
                        AppConstants.setToastStr(SignUpStep1Activity.this, getString(R.string.please_enter_lastname));
                        return;
                    }
                    if (signupStep.tvGender.getText().toString().equalsIgnoreCase("") || signupStep.tvGender.getText().toString().equalsIgnoreCase("Select Gender")) {
                        signupStep.tvGender.requestFocus();
//                            edtEmailId.setError("Please enter email id.");
//                            Toast.makeText(SignUpStep1Activity.this, "Please enter email id", Toast.LENGTH_SHORT).show();
                        AppConstants.setToastStr(SignUpStep1Activity.this, getString(R.string.please_select_gender));
                        return;
                    }
                    if (signupStep.edtEmailId.getText().toString().equalsIgnoreCase("")) {
                        signupStep.edtEmailId.requestFocus();
//                            edtEmailId.setError("Please enter email id.");
//                            Toast.makeText(SignUpStep1Activity.this, "Please enter email id", Toast.LENGTH_SHORT).show();
                        AppConstants.setToastStr(SignUpStep1Activity.this, getString(R.string.please_enter_valid_email_address));
                        return;
                    }
                    if (!checkEmail(signupStep.edtEmailId.getText().toString())) {
                        signupStep.edtEmailId.requestFocus();
//                            edtEmailId.setError("Please enter valid email address.");
//                            Toast.makeText(SignUpStep1Activity.this, "Please enter valid email address", Toast.LENGTH_SHORT).show();
                        AppConstants.setToastStr(SignUpStep1Activity.this, getResources().getString(R.string.please_enter_valid_email_address));
                        return;
                    }*/
                    if (signupStep.edtMobileNo.getText().toString().equalsIgnoreCase("")) {
                        signupStep.edtMobileNo.requestFocus();
//                            edtMobileNo.setError("Please enter your mobile no.");
//                            Toast.makeText(SignUpStep1Activity.this, "Please enter your mobile no", Toast.LENGTH_SHORT).show();
                        AppConstants.setToastStr(SignUpStep1Activity.this, getResources().getString(R.string.please_enter_mobile_no));
                        return;
                    }
                    if (signupStep.edtMobileNo.getText().toString().length()!=10) {
                        signupStep.edtMobileNo.requestFocus();
//                            edtMobileNo.setError("Please enter your mobile no.");
//                            Toast.makeText(SignUpStep1Activity.this, "Please enter your mobile no", Toast.LENGTH_SHORT).show();
                        AppConstants.setToastStr(SignUpStep1Activity.this, getResources().getString(R.string.please_enter_valid_mobile_no));
                        return;
                    }


                    if(signupStep.edtPassword.getText().toString().length()<6)
                    {
                        signupStep.edtPassword.requestFocus();
                        signupStep.edtPassword.setError(getString(R.string.please_enter_your_password));
                        return;
                    }
                    /*if(!isValidPassword(signupStep.edtPassword.getText().toString()))
                    {
                        signupStep.edtPassword.setError(getResources().getString(R.string.enter_password_with_special_characters));
                        signupStep.edtPassword.requestFocus();
                        return;
                    }*/
                    if(! signupStep.tvTermAndCondi.isChecked())
                    {
                        setToastStr(SignUpStep1Activity.this, getString(R.string.please_accept_terms_and_conditions));
                        return;
                    }


                    /*} else {*/

                    String FirstName = signupStep.edtFirstName.getText().toString();
                    String LastName = signupStep.edtLastName.getText().toString();
                    String Gender = signupStep.tvGender.getText().toString();
                    String MobileNo = signupStep.edtMobileNo.getText().toString();
                    String EmailId = signupStep.edtEmailId.getText().toString();
                    strDummyEmail="0";
                    if (EmailId.equalsIgnoreCase("")) { //for creation of email id
                        EmailId=MobileNo+"@gmail.com";
                        strDummyEmail="1";
                    }



                    // AppConstants.CountryCodeName = edtCountryCode.getText().toString();
                    Log.e("c_code", AppConstants.CountryCodeName);
                    String Password=signupStep.edtPassword.getText().toString();
                    String ConPassword=signupStep.edtConPassword.getText().toString();

                    if(Password.equalsIgnoreCase(ConPassword)) {
                        if (NetworkConnection.hasConnection(SignUpStep1Activity.this)){
                            getRegistationSteps(AppConstants.CountryCodeName,
                                    MobileNo, EmailId, Password,FirstName,LastName,Gender);

                        }else
                        {
                            AppConstants.CheckConnection(SignUpStep1Activity.this);
                        }

                    }else
                    {
                        AppConstants.setToastStr(SignUpStep1Activity.this,getString(R.string.password_not_matched));
                    }
                    //}
                } catch (Exception e) {
                    Log.e("Error=", "" + e);
                }

            }
        });
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.arr_gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        signupStep.spGender.setAdapter(adapter);
        signupStep.tvGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupStep.spGender.performClick();
            }
        });
        signupStep.spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                signupStep.tvGender.setText(""+signupStep.spGender.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        signupStep.linBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        signupStep.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpStep1Activity.strPassword=null;SignUpStep1Activity.strUsername=null;
                startActivity(new Intent(SignUpStep1Activity.this, LoginActivity.class));
                finish();
            }
        });

        signupStep.tvTermAndCondiText.setMovementMethod(LinkMovementMethod.getInstance());
        signupStep.tvTermAndCondiText.setText(setSpanText(getString(R.string.terms_conditions)), TextView.BufferType.SPANNABLE);
       // signupStep.tvTermAndCondiText.setText(getString(R.string.terms_conditions));
        prefUpdate = PreferenceManager.getDefaultSharedPreferences(SignUpStep1Activity.this);
        strLang=prefUpdate.getString("selLang","English");
        signupStep.tvTermAndCondiText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage("http://saathiya.sairoses.com/terms_and_condition.php");
            }
        });
//
//        if (NetworkConnection.hasConnection(getApplicationContext())) {
//            getCountrysRequest();
//            getCountries();
//        } else {
//            AppConstants.CheckConnection(SignUpStep1Activity.this);
//        }
        initFirebase();
    }


    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpStep1Activity.this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage(getResources().getString(R.string.go_back));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor=prefUpdate.edit();
                editor.putString("signup_step","0");
                editor.commit();

                Intent intLogin = new Intent(SignUpStep1Activity.this, LoginActivity.class);
                startActivity(intLogin);
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    /**
     * Khởi tạo các thành phần cần thiết cho việc quản lý đăng nhập
     */
    private void initFirebase() {
        //Khoi tao thanh phan de dang nhap, dang ky
        FirebaseMessaging.getInstance().subscribeToTopic("chats");  //

        mAuth = FirebaseAuth.getInstance();
        authUtils = new AuthUtils();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    AppConstants.UID = user.getUid();
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                    SharedPreferences.Editor editor=prefUpdate.edit();
                    editor.putString("userId",AppConstants.UID);
                    editor.commit();
                    // Preferences.setPreferenceValue(LoginActivity.this,"userId",StaticConfig.UID);
                   /* if (firstTimeAccess) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finish();
                    }*/
                } else {
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
                // firstTimeAccess = false;
            }
        };

        //Khoi tao dialog waiting khi dang nhap
        // waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
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
        super.onDestroy();

        AppConstants.CountryId = "";
        AppConstants.CountryName = "";
        AppConstants.ReligionId = "";
        AppConstants.CasteId = "";
        AppConstants.MotherTongueId = "";
        AppConstants.CountryCodeId = "";
        AppConstants.ReligionName = "";
        AppConstants.CasteName = "";
        AppConstants.MotherTongueName = "";
        AppConstants.CountryCodeName = "";


    }



    private boolean checkEmail(String email) {
        return AppConstants.email_pattern.matcher(email).matches();
    }





    Dialog progresDialog;
    private void getRegistationSteps(String CountryCode, String MobileNo, String EmailId
            ,String Password,String FirstName, String LastName,String Gender
    ) {
        progresDialog= showProgress(SignUpStep1Activity.this);
        progresDialog.show();


        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // progressBarSlider.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(String... params) {

                //String paramCountryId = params[0];
                String paramCountryCode = params[0];
                String paramMobileNo = params[1];
                String paramEmailId = params[2];
                String paramPassword = params[3];
                String paramFirstName = params[4];
                String paramLastName = params[5];
                String paramGender = params[6];

                HttpClient httpClient = new DefaultHttpClient();

                String URL = AppConstants.MAIN_URL + "signup_step1.php";
                Log.e("URL", "== " + URL);

                HttpPost httpPost = new HttpPost(URL);
                // BasicNameValuePair CountryIdPAir = new BasicNameValuePair("country_id", paramCountryId);
                BasicNameValuePair CountryCodePAir = new BasicNameValuePair("country_code", paramCountryCode);
                BasicNameValuePair MobileNoPAir = new BasicNameValuePair("mobile_no", paramMobileNo);
                BasicNameValuePair EmailIdPAir = new BasicNameValuePair("email_id", paramEmailId);
                BasicNameValuePair PasswordPAir = new BasicNameValuePair("password", paramPassword);
                BasicNameValuePair FirstNamePAir = new BasicNameValuePair("firstname", paramFirstName);
                BasicNameValuePair LastNamePAir = new BasicNameValuePair("lastname", paramLastName );
                BasicNameValuePair GenderPAir = new BasicNameValuePair("gender", paramGender );
                BasicNameValuePair languagePAir = new BasicNameValuePair("language", strLang );
                BasicNameValuePair DummyEmailPAir = new BasicNameValuePair("dummy_email", strDummyEmail);





                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(CountryCodePAir);
                nameValuePairList.add(MobileNoPAir);
                nameValuePairList.add(EmailIdPAir);
                nameValuePairList.add(PasswordPAir);
                nameValuePairList.add(FirstNamePAir);
                nameValuePairList.add(LastNamePAir);
                nameValuePairList.add(GenderPAir);
                nameValuePairList.add(languagePAir);
                nameValuePairList.add(DummyEmailPAir);


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

                } catch (Exception uee) {
                    System.out.println("Anption given because of UrlEncodedFormEntity argument :" + uee);
                    uee.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String Ressponce) {
                super.onPostExecute(Ressponce);
                progresDialog.dismiss();
                Log.e("--cast --", "==" + Ressponce);
                //   progressBarSlider.setVisibility(View.GONE);
                try {
                    JSONObject responseObj = new JSONObject(Ressponce);
//                    JSONObject responseData = responseObj.getJSONObject("responseData");


                    String status = responseObj.getString("status");

                    if (status.equalsIgnoreCase("1")) {

                        strUsername=EmailId;
                        strPassword=Password;

                        SharedPreferences.Editor editor = prefUpdate.edit();

                        editor.putString("otp", responseObj.getString("otp"));
                        editor.putString("CountryId", AppConstants.CountryId);
                        editor.putString("CountryName", AppConstants.CountryName);
                        editor.putString("CountryCode", AppConstants.CountryCodeName);
                        editor.putString("mobile", MobileNo);
                        editor.putString("signup_step", "1");
                        editor.putString("user_id_r", responseObj.getString("user_id"));
                        editor.putString(AppConstants.EMAIL_ID, signupStep.edtEmailId.getText().toString());
                        editor.putString(AppConstants.MOBILE_NO, signupStep.edtMobileNo.getText().toString());
                        editor.commit();



                        authUtils.createUser(EmailId, Password,FirstName+" "+LastName);

//                        Log.e("signup1",message);
                        //Toast.makeText(SignUpStep1Activity.this,""+message,Toast.LENGTH_LONG).show();

                        //finish();

                    } else {
                        String msgError = responseObj.getString("message");
                        if(msgError.contains("Mobile Number Already Exist")){
                            authUtils.createUser(EmailId, Password,FirstName+" "+LastName);
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpStep1Activity.this);
                        builder.setMessage("" + msgError).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                    responseObj = null;

                } catch (Exception e) {
                    Log.e("exaception", "" + e);
                } finally {

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute( CountryCode, MobileNo, EmailId,Password,FirstName,LastName,Gender);
    }


    public SpannableString setSpanText(String text)
    {
        SpannableString spannableString = new SpannableString(text);

        // It is used to set foreground color.
        ForegroundColorSpan blue = new ForegroundColorSpan(getResources().getColor(R.color.colorBlue));

        ClickableSpan teremsAndCondition = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                openWebPage("http://saathiya.prometteur.in/terms_and_condition.php");

            }
        };

        /*spannableString.setSpan(teremsAndCondition, 12, 31, 0);
        spannableString.setSpan(blue, 12, 31, 0);*/

        spannableString.setSpan(teremsAndCondition, 0, text.length(), 0);
        spannableString.setSpan(blue, 0, text.length(), 0);
        return spannableString;
    }
    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            //showFailToast(this, "No application can handle this request. Please install a web browser or check your URL.");
            e.printStackTrace();
        }
    }

    /**
     * Dinh nghia cac ham tien ich cho quas trinhf dang nhap, dang ky,...
     */
    class AuthUtils {
        /**
         * Action register
         *
         * @param email
         * @param password
         */
        void createUser(String email, String password,String username) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpStep1Activity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            // waitingDialog.dismiss();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                /*new LovelyInfoDialog(LoginActivity.this) {
                                    @Override
                                    public LovelyInfoDialog setConfirmButtonText(String text) {
                                        findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dismiss();
                                            }
                                        });
                                        return super.setConfirmButtonText(text);
                                    }
                                }
                                        .setTopColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_add_friend)
                                        .setTitle("Register false")
                                        .setMessage("Email exist or weak password!")
                                        .setConfirmButtonText("ok")
                                        .setCancelable(false)
                                        .show();*/
                                AppConstants.setToastStr(SignUpStep1Activity.this,getString(R.string.email_exist_or_weak_password));
                            } else {
                                initNewUserInfo(username);
                                Log.e("firebase login","Register and Login success");
                                setToastStrPinkBg(SignUpStep1Activity.this, getString(R.string.registration_successful));
                                fromPage="register";
                                new FragmentOtpVerificationBottomSheetDialog().show(getSupportFragmentManager(), "Dialog");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //waitingDialog.dismiss();
                        }
                    })
            ;
        }



        /**
         * Action reset password
         *
         * @param email
         */
        void resetPassword(final String email) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            /*new LovelyInfoDialog(LoginActivity.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setIcon(R.drawable.ic_pass_reset)
                                    .setTitle("Password Recovery")
                                    .setMessage("Sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();*/
                            AppConstants.setToastStrPinkBg(SignUpStep1Activity.this,getString(R.string.sent_mail_to)+" " + email);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            /*new LovelyInfoDialog(LoginActivity.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_pass_reset)
                                    .setTitle("False")
                                    .setMessage("False to sent email to " + email)
                                    .setConfirmButtonText("Ok")
                                    .show();*/
                            AppConstants.setToastStr(SignUpStep1Activity.this,getString(R.string.failed_to_sent_email_to)+" " + email);
                        }
                    });
        }



        /**
         * Khoi tao thong tin mac dinh cho tai khoan moi
         */
        void initNewUserInfo(String username) {
            User newUser = new User();
            newUser.email = user.getEmail();
            newUser.name = username;//user.getEmail().substring(0, user.getEmail().indexOf("@"));
            newUser.avata = AppConstants.STR_DEFAULT_BASE64;
            FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser);
        }
    }

}