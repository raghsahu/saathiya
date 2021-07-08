package com.prometteur.sathiya.utills;

import android.app.Activity;
//import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.prometteur.sathiya.R;
//import com.thegreentech.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class AppConstants {


    public static String BASE_URL = "http://saathiya.prometteur.in/";
//    public static String BASE_URL = "http://saathiya.sairoses.com/";
    public static String MAIN_URL = BASE_URL + "api/";
    public static String m_id = "";
    public static String tokan = "";
    public static String fromNotification = "";

    public static String msg_express_intress = "New Express interest Recived from";
    public static String msg_express_intress_title = "Express Interest";
    public static String msg_send_reminder_title = "Send Reminder";

    public static String msg_massage_sent = "New Message Received From";
    public static String msg_sent_title = "New Message Received From";


    public static String express_intress_id = "202";
    public static String Photo_Password_id = "203";
    public static String Photo_Request_id = "204";
    public static String Check_Contact = "205";
    public static String msg_id = "206";


    //-------Push Notifications-------//
    public static String Action_MessageRecived = "CHAT_RECIVED";
    //-------------------------------//
    public static String SELECTED_TAB_MAIN = "MainTab";
    public static String SELECTED_TAB_HOME = "HomeTab";
    public static String SELECTED_TAB_MESSAGE = "MessageTab";
    public static String SELECTED_TAB_MATCHES = "MatchesTab";
    public static String SELECTED_TAB_SEARCH = "Search";
    public static String SELECTED_TAB_Success_Story = "SuccessStoryTab";

    public static String MaritalStatusName = "", EatingHabitNAme = "", SmokingHabitsNAME = "", DrinkingNAME = "", StarNAME = "", DosTypeNAME = "", Raasi = "";

    public static String BrideID = "", BrideName = "";
    public static String CountryId = "", StateId = "", CityId = "";
    public static String CountryName = "", StateName = "", CityName = "";
    public static String ReligionId = "", CasteId = "", MotherTongueId = "", CountryCodeId = "", EducationId = "", AditionalEducationId = "", OccupationID = "", HeightID = "", HeightFromID = "", HeightToID = "";
    public static String ReligionName = "", CasteName = "", MotherTongueName = "", CountryCodeName = "+91", EducationName = "", AditionalEducationName = "", OccupationName = "";
    public static String GeneralName = "", SubCasteID = "", SubCasteName = "", UserID = "", UserName = "";


    public static String FIRST_NAME = "firstName";
    public static String LAST_NAME = "lastName";
    public static String MOBILE_NO = "mobileno.";
    public static String EMAIL_ID = "emailid";
    public static String PASSWORD = "password";
    public static String DOSHTYPE = "";


    public static String is_shortlisted;
    public static String is_blocked;
    public static String is_interest;


    public static boolean isNotification=false;
    public static int vibrateSmall=80;
    public static int vibrateBig=500;
    public static Vibrator vibe;


    public static void sendPushNotification(String tokan, String msg, String title, final String id) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("tokan", tokan);
        params.put("msg", msg);
        params.put("title", title);
        params.put("id", id);
        client.post(AppConstants.MAIN_URL + "user_chat_notification.php", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("NOTIFICATION__fail" + id, responseString);
            }

            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Log.d("NOTIFICATION__" + id, responseString);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });

    }

    public static Pattern email_pattern = Pattern.compile
            ("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            );

    public static Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(700);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public static Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(100);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }


    public static Animation FromTopToButtomAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    public static Animation FromButtomToTopAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }


    public static void CheckConnection(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle);
        builder.setTitle(activity.getResources().getString(R.string.app_name));
        builder.setMessage(activity.getString(R.string.please_check_your_internet));
        builder.setPositiveButton(activity.getString(R.string.ok), null);
        builder.setNegativeButton(activity.getString(R.string.cancel), null);
        builder.show();
    }


    public static String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd MMM yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String mDateFormateDDMMM(String strDate) {
        String finalDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("hh:mm dd MMM yyyy"); //dd MMM yyyy
            finalDate = spf.format(newDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalDate;
    }

    public static class chatDb {
        public static final String COLUMN_NAME_DATE = "update_time";
        public static final String COLUMN_NAME_RECORD = "record";
        public static final String COLUMN_MSG = "message";
    }

    public static void setToastStr(Activity context, String message) {
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_design, (ViewGroup) context.findViewById(R.id.custom_toast_layout));

        TextView tv = (TextView) layout.findViewById(R.id.txtToast);
        tv.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void setToastStrPinkBg(Activity context, String message) {
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_design_pink_bg, (ViewGroup) context.findViewById(R.id.custom_toast_layout));

        TextView tv = (TextView) layout.findViewById(R.id.txtToast);
        tv.setText(message);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }




    public static boolean isValidPassword(final String password) {


        if (password.length() >= 8) {
            Pattern letter = Pattern.compile("[a-zA-z]");
            Pattern digit = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
            //Pattern eight = Pattern.compile (".{8}");


            Matcher hasLetter = letter.matcher(password);
            Matcher hasDigit = digit.matcher(password);
            Matcher hasSpecial = special.matcher(password);

            return hasLetter.find() && hasDigit.find() && hasSpecial.find();

        } else
            return false;


    }

    public static String getReviewDate(Context context, String date) {
        SimpleDateFormat sourceDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = "";
        try {
            Date date1 = sourceDate.parse(date);
            formattedDate = (String) DateUtils.getRelativeTimeSpanString(date1.getTime(), new Date().getTime(), DateUtils.MINUTE_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }


    public static int REQUEST_CODE_REGISTER = 2000;
    public static String STR_EXTRA_ACTION_LOGIN = "login";
    public static String STR_EXTRA_ACTION_RESET = "resetpass";
    public static String STR_EXTRA_ACTION = "action";
    public static String STR_EXTRA_USERNAME = "username";
    public static String STR_EXTRA_PASSWORD = "password";
    public static String STR_DEFAULT_BASE64 = "default";
    public static String UID = "";
    //TODO only use this UID for debug mode
//    public static String UID = "6kU0SbJPF5QJKZTfvW1BqKolrx22";
    public static String INTENT_KEY_CHAT_FRIEND = "friendname";
    public static String INTENT_KEY_CHAT_AVATA = "friendavata";
    public static String INTENT_KEY_CHAT_ID = "friendid";
    public static String INTENT_KEY_CHAT_ROOM_ID = "roomid";
    public static String INTENT_KEY_CHAT_REC_ID = "recid";
    public static long TIME_TO_REFRESH = 10 * 1000;
    public static long TIME_TO_OFFLINE = 2 * 60 * 1000;
}

