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
import android.widget.TextView;

public class MyPreferenceEdit extends Preference {

	public int title;

	public MyPreferenceEdit(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init(context, attrs);
	}

	public MyPreferenceEdit(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);

	}

	public MyPreferenceEdit(Context context) {
		super(context);
		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		setLayoutResource(R.layout.preference_edit);
	}

	protected View onCreateView(ViewGroup parent) {
		View v = super.onCreateView(parent);
		// Log.d("bb", ""+v.findViewById(R.id.prefrence_button1));
		// getKey()
		((Button) v.findViewById(R.id.prefrence_a))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mButtonCallBack!=null){
							mButtonCallBack.callback(getKey(), true);
						}
					}
				});
		((Button) v.findViewById(R.id.prefrence_m))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mButtonCallBack!=null){
							mButtonCallBack.callback(getKey(), false);
						}
					}
				});
		
		return v;
	}

	public void setCallback(IButtonCallBack cb){
		mButtonCallBack = cb;
	}
	private IButtonCallBack mButtonCallBack;
	public static interface IButtonCallBack {
		public void callback(String key, boolean add);
	};
}
