package com.common.view;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
public class MyPreferenceDialog extends DialogPreference {

	public MyPreferenceDialog(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public MyPreferenceDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyPreferenceDialog(Context context) {
		super(context);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// TODO Auto-generated method stub
		super.onDialogClosed(positiveResult);
		if (positiveResult && getOnPreferenceChangeListener() != null) {
			getOnPreferenceChangeListener().onPreferenceChange(this,
					positiveResult);
		}
	}

}
