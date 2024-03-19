package com.common.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.canboxsetting.R;
import com.common.util.Kernel;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.ResourceUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.preference.Preference;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
