package com.common.view;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MyPreference2 extends Preference {

	public MyPreference2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public MyPreference2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyPreference2(Context context) {
		super(context);
	}

	private View mMainView;

	protected View onCreateView(ViewGroup parent) {
		mMainView = super.onCreateView(parent);
		return mMainView;
	}
	
	public View getMainView(){
		return mMainView;
	}
}
