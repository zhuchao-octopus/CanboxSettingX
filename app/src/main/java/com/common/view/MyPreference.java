package com.common.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.Preference;

import com.canboxsetting.R;

public class MyPreference extends Preference {

    public MyPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference);
    }

    public MyPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference);
    }

    public MyPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.preference);
    }


}
