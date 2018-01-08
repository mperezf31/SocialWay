package com.caminosantiago.socialway.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by root on 17/10/2015.
 */
public class TextViewRoboto extends TextView {

    public TextViewRoboto(Context context) {
        super(context);
        createFont();
    }

    public TextViewRoboto(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        createFont();
    }

    public TextViewRoboto(Context context, AttributeSet attr) {
        super(context, attr);
        createFont();
    }

    private void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "RobotoSlab-Regular.ttf");
        setTypeface(font);
    }
}