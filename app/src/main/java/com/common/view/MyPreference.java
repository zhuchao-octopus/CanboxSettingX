package com.common.view;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import com.android.canboxsetting.R;


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
