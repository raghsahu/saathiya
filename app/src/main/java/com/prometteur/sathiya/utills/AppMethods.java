package com.prometteur.sathiya.utills;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cunoraz.gifview.library.GifView;
import com.prometteur.sathiya.R;

public class AppMethods
{

    //TODO: Show soft keyboard
    public static void showKeyboard(View view) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && view.hasFocus()) {
            view.clearFocus();
        }
     /*   view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);*/
        InputMethodManager inputMethodManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    //TODO: Hide soft keyboard
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void getResizedDrawable(Context context, int imgId, TextView textView, Button button, EditText editText, int size)
    {
        Drawable img = context.getResources().getDrawable(imgId);
        if(button!=null) {
            img.setBounds(0, 0, (int) button.getContext().getResources().getDimension(size),(int) button.getContext().getResources().getDimension(size));
            button.setCompoundDrawables(img, null, null, null);
        }
        if(textView!=null) {
            img.setBounds(0, 0, (int) textView.getContext().getResources().getDimension(size),(int) textView.getContext().getResources().getDimension(size));
            textView.setCompoundDrawables(img, null, null, null);
        }
        if(editText!=null) {
            img.setBounds(0, 0, (int) editText.getContext().getResources().getDimension(size), (int) editText.getContext().getResources().getDimension(size));
            editText.setCompoundDrawables(img, null, null, null);
        }

    }

    public static void shrinkAnim(ImageView view,Context context)
    {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.ani_zooming);
        view.startAnimation(animFadeIn);
    }

    public static Dialog dialog1=null;

    public static Dialog showProgress(Context context) {
        // custom dialog
        dialog1 = new Dialog(context);
        dialog1.setCancelable(false);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog1.setContentView(R.layout.dialog_progress);
        GifView gifView1 = (GifView) dialog1.findViewById(R.id.gif1);
        gifView1.setVisibility(View.VISIBLE);
        gifView1.play();
        // gifView1.pause();
        gifView1.setGifResource(R.drawable.ring_gif);
        //gifView1.setMovieTime(time);
        //gifView1.getMovie();

        return dialog1;

    }
}
