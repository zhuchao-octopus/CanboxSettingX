package com.canboxsetting.info;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.android.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;

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
