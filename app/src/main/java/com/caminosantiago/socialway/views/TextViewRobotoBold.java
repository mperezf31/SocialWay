package com.caminosantiago.socialway.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by root on 17/10/2015.
 */
public class TextViewRobotoBold extends TextView {

    public TextViewRobotoBold(Context context) {
        super(context);
        createFont();
    }

    public TextViewRobotoBold(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        createFont();
    }

    public TextViewRobotoBold(Context context, AttributeSet attr) {
        super(context, attr);
        createFont();
    }

    private void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "RobotoSlab-Bold.ttf");
        setTypeface(font);
    }
}