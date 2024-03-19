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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
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
import com.common.util.Node;
import com.common.util.SystemConfig;
import com.common.util.Util;
import com.common.util.shell.ShellUtils;
import com.common.view.MyPreference2;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class Nission2013InfoSimpleFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
	private static final String TAG = "Nission2013InfoSimpleFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.nissian2013_settings);

		Preference p;
		p = findPreference("lang");
		p.setOnPreferenceClickListener(this);
		mLPCarType = (ListPreference) findPreference("car_type_cmd");
		mLPCarType.setOnPreferenceChangeListener(this);

		getCanboxSetting();
		updateCarType(mCanboxType);
	}

	ListPreference mLPCarType;


	

	private void updateCarType(String can) {
	

		String[] entry2 = { "0:AVM Middle", "1:AVM High" };
		String[] value2 = { "0", "1" };

		mLPCarType.setEntries(entry2);
		mLPCarType.setEntryValues(value2);

		if (mCarType != null) {
			mLPCarType.setValue(mCarType);
		} else {
			mLPCarType.setValue("0");
		}

		mLPCarType.setSummary(mLPCarType.getEntry());

	}


	
	private String mCanboxType;


	
	private void updateMachineConfig() {
		 SystemConfig.setProperty(getActivity(), MachineConfig.VALUE_CANBOX_NISSAN2013, mCarType);
		
			Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
			it.putExtra(MyCmd.EXTRA_COMMON_CMD,
					MachineConfig.KEY_CAN_BOX);
			getActivity().sendBroadcast(it);
	}
	

	private void getCanboxSetting() {
		mCarType = SystemConfig.getProperty(getActivity(), MachineConfig.VALUE_CANBOX_NISSAN2013);
		
	}
	String mCarType;
	private void udpatePreferenceValue(Preference preference, Object newValue) {
		String key = preference.getKey();
		if ("lang".equals(key)) {
			byte[] buf = new byte[] { (byte) 0xc6, 0x2, (byte) 2, (byte) 2 };
			BroadcastUtil.sendCanboxInfo(getActivity(), buf);
		} else if ("car_type_cmd".equals(key)) {

			mCarType = (String)newValue;
			
			updateMachineConfig();

			
			((ListPreference) preference).setValue(String
					.valueOf((String) newValue));
			preference.setSummary("%s");
		}
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		try {
			udpatePreferenceValue(preference, newValue);
		} catch (Exception e) {

		}
		return false;
	}

	public boolean onPreferenceClick(Preference arg0) {

		try {
			udpatePreferenceValue(arg0, null);
		} catch (Exception e) {

		}

		return false;
	}
	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// final Context context = inflater.getContext();
	// View mMainView = inflater.inflate(R.layout.nissian2013, container,
	// false);
	// mMainView.findViewById(R.id.lang).setOnClickListener(
	// new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// byte[] buf = new byte[] { (byte) 0xc6, 0x2, (byte) 2,
	// (byte) 2 };
	// BroadcastUtil.sendCanboxInfo(getActivity(), buf);
	// }
	// });
	//
	// return mMainView;
	// }

}
