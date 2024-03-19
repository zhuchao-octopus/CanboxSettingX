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
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.canboxsetting.R;
import com.canboxsetting.R.xml;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class KadjarRaiseFragment extends PreferenceFragment implements
 OnPreferenceClickListener{
	private static final String TAG = "KadjarRaiseFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.kadjar_raise_info);

		findPreference("vehicle_reset").setOnPreferenceClickListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		registerListener();

		

	}
	public boolean onPreferenceClick(Preference arg0) {
		String key = arg0.getKey();
		if ("vehicle_reset".equals(key)) {
			byte[] buf = new byte[] { (byte) 0x83, 0x02, (byte) 0x80, (byte) 0x01};
			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		}
		return false;
	}


	private void setPreference(String key, String s) {
		Preference p = findPreference(key);
		if (p != null) {
			p.setSummary(s);
		}
	}

	private void updateView(byte[] buf) {
		int index;
		String s = "";
		switch (buf[0]) {
		case (byte)0x81:
			index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
			s = index/10 + "." + index%10 + " L/100KM";		
			setPreference("averagefuel", s);
			
			
			index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));

			s = index/10 + "." + index%10 + " KM/H";
			setPreference("averageapeed", s);	

			index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8)
					| ((buf[8] & 0x01) << 16));

			s = index/10 + "." + index%10 + " KM";
			setPreference("running_distance", s);
			
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
