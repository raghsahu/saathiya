package com.prometteur.sathiya.utills;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.prometteur.sathiya.R;


public class TextViewCustomFont extends androidx.appcompat.widget.AppCompatTextView {
    public TextViewCustomFont(Context context) {
        super(context);
       // this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
    }

    public TextViewCustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCustomTextView(context,attrs);
      //  this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
    }

    public TextViewCustomFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomTextView(context,attrs);
       // this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
    }

   /* public TextViewCustomFont(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCustomTextView(context,attrs);
       // this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
    }*/

   /* private void initCustomTextView(Context context) {

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Light.otf");
//            Typeface typeface = Typeface.createFromFile("font/poppins_light.ttf");//.createFromAsset(context.getAssets(), "poppins_light.ttf");
        setTypeface(typeface);
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/poppins_light.ttf");
//        Typeface typeface = FontCache.getTypeface("Poppins-Light.otf", context);

    }*/


    private void initCustomTextView(Context context, AttributeSet attrs) {


        try {


            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CustomeFont,
                    0, 0);

            try {

                int value = a.getInt(R.styleable.CustomeFont_textFont, 0);

                switch (value) {
                    case 0:
                        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Medium.otf");
                        setTypeface(typeface);
                        break;
                    case 1:
                        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Light.otf");
                        setTypeface(typeface1);
                        break;
                    case 2:
                        Typeface typeface2 = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-SemiBold.otf");
                        setTypeface(typeface2);
                        break;
                }

            } finally {
                a.recycle();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}