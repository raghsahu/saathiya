package com.prometteur.sathiya.utills;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;


import com.prometteur.sathiya.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomeEditText extends androidx.appcompat.widget.AppCompatEditText {
    public CustomeEditText(Context context) {
        super(context);
        initRegix();
    }

    public CustomeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCustomTextView(context,attrs);
        initRegix();
    }

    public CustomeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomTextView(context,attrs);
        initRegix();
    }

   /* public CustomeEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCustomTextView(context,attrs);
        initRegix();
    }*/

    private void initRegix() {

        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try {
                    String value = editable.toString();

                    Pattern letter = Pattern.compile("[a-zA-z0-9]");
                    Matcher hasLetter = letter.matcher(value);

                    if (!hasLetter.find()) {
                        if(!value.isEmpty()) {
                           // setError(getContext().getResources().getString(R.string.should_not_spec_char));
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }


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
        }

    }

}