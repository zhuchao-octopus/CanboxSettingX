package com.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.canboxsetting.R;


public class MyPreferenceProgress extends Preference {

	public int title;

	public MyPreferenceProgress(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init(context, attrs);
		initAttrs(attrs, defStyleAttr);
	}

	public MyPreferenceProgress(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);
		initAttrs(attrs, 0);

	}

	public MyPreferenceProgress(Context context) {
		super(context);
		init(context, null);
	}

	int mMax;

	private void initAttrs(AttributeSet attrs, int defStyle) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.MyPreferenceSeekBar, defStyle, 0);

		mMax = a.getInteger(R.styleable.MyPreferenceSeekBar_max, 0);
	}

	private ProgressBar mProgressBar;
	private TextView tv;
	private int mProgress = 0;

	private void init(Context context, AttributeSet attrs) {
		setLayoutResource(R.layout.preference_progressbar);
	}

	protected View onCreateView(ViewGroup parent) {
		View v = super.onCreateView(parent);

		mProgressBar = (ProgressBar) v.findViewById(R.id.progressbar_value);
		if (mMax != 0) {
			mProgressBar.setMax(mMax);
		}
		mProgressBar.setProgress(mProgress);
		tv = (TextView) v.findViewById(R.id.summary);
		tv.setText(getSummary());
		
		return v;
	}

	@Override
	public void setSummary(CharSequence summary) {
		super.setSummary(summary);
		// TODO Auto-generated method stub
		tv.setText(summary);

		if (mProgressBar != null) {
			try {
				mProgress = Integer.valueOf(summary.toString());
				mProgressBar.setProgress(mProgress);
			} catch (Exception e) {

			}
		}
	}

}
