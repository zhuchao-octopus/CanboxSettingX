package com.canboxsetting.info;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.R;
import com.canboxsetting.R.string;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;
import com.common.view.MyPreference2;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class TouaregHiworldFragment extends PreferenceFragment implements
		OnPreferenceClickListener {
	private static final String TAG = "KadjarRaiseFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.touareghiworld_info);
	}

	private final static int[] INIT_CMDS = { 0xc3 };

	private void requestInitData() {
		// mHandler.sendEmptyMessageDelayed(INIT_CMDS[0], 0);
		for (int i = 0; i < INIT_CMDS.length; ++i) {
			mHandler.sendEmptyMessageDelayed(INIT_CMDS[i], (i * 200));
		}

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (!mPaused) {
				sendCanboxInfo0x90((msg.what & 0xff00) >> 8, msg.what & 0xff);

			}
		}
	};

	private void sendCanboxInfo0x90(int d0, int d1) {
		byte[] buf = new byte[] { 0x3, (byte) 0x6a, 0x5, 0x1, (byte) d1 };
		BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	}

	public boolean onPreferenceClick(Preference arg0) {

		return false;
	}

	private boolean mPaused = true;

	@Override
	public void onPause() {
		super.onPause();
		unregisterListener();
		mPaused = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		registerListener();
		mPaused = false;
		requestInitData();
	}

	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}

	private int getFuelrange(byte b) {
		int ret = 0;
		switch (b) {
		case 0:
			ret = 60;
			break;
		case 1:
			ret = 10;
			break;
		case 2:
			ret = 12;
			break;
		case 3:
			ret = 20;
			break;
		case 4:
			ret = 30;
			break;
		case 5:
			ret = 40;
			break;
		case 6:
			ret = 50;
			break;
		case 7:
			ret = 60;
			break;
		case 8:
			ret = 70;
			break;
		case 9:
			ret = 80;
			break;
		case 10:
			ret = 90;
			break;
		case 11:
			ret = 100;
			break;
		}
		return ret;
	}

	private void updateView(byte[] buf) {

		int index = 0;
		String s = "";

		switch (buf[2]) {
		case (byte) 0xc3: {
			
			index = (buf[3] & 0xff);
			s = "" + index;
			setPreference("oil_data", s);

			int unit = (buf[5] & 0xff);

			index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));

			if (unit == 0) {
				s = index + "KM";
			} else {
				s = index + "Mile";
			}
			setPreference("mkm", s);

			index = ((buf[9] & 0xff) | ((buf[8] & 0xff) << 8));

			s = "" + index + " " + getString(R.string.days);
			setPreference("mday", s);

			index = (buf[10] & 0xff) * 100;
			if (unit == 0) {
				s = index + "KM";
			} else {
				s = index + "Mile";
			}
			setPreference("cycle_km", s);

			index = (buf[11] & 0xff);
			s = "" + index + " " + getString(R.string.days);
			setPreference("cycle_day", s);

		}

			break;

		}
	}

	private BroadcastReceiver mReceiver;

	private void unregisterListener() {
		if (mReceiver != null) {
			this.getActivity().unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	private void registerListener() {
		if (mReceiver == null) {
			mReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();
					if (action.equals(MyCmd.BROADCAST_SEND_FROM_CAN)) {

						byte[] buf = intent.getByteArrayExtra("buf");
						if (buf != null) {

							try {
								updateView(buf);
							} catch (Exception e) {
								Log.d("aa", "!!!!!!!!" + buf);
							}
						}
					}
				}
			};
			IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(MyCmd.BROADCAST_SEND_FROM_CAN);

			this.getActivity().registerReceiver(mReceiver, iFilter);
		}
	}

}
