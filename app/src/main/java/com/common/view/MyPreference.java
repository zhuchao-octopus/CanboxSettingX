package com.common.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.canboxsetting.R;
import com.common.util.Kernel;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
