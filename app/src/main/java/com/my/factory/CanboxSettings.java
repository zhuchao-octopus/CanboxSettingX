/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.my.factory;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.canboxsetting.R;
import com.common.util.AppConfig;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.SystemConfig;
import com.common.util.UtilSystem;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This activity plays a video from a specified URI.
 */
public class CanboxSettings extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener, OnPreferenceClickListener,
		OnClickListener {

	private static final String TAG = "CanboxSettings";

	private static final String KEY_MANUFACTURER = "canbox_manufacturer";
	private static final String KEY_CANBOX = "canbox_setting";
	private static final String KEY_CANBOX_KEY = "canbox_key_mode";
	private static final String KEY_CANBOX_EQ = "canbox_eq_mode";

	private ListPreference mCanboxManufacturerPreference;
	private ListPreference mCanboxSettingPreference;
	private ListPreference mCanboxKeyPreference;
	private ListPreference mCanboxEQPreference;

	private final int M_VALUE_ARRARY_INDX_ALL = 0;
	private final int M_VALUE_ARRARY_INDX_SIMPLE = 1;
	private final int M_VALUE_ARRARY_INDX_RAISE = 2;
	private final int M_VALUE_ARRARY_INDX_BAGOO = 3;
	private final int M_VALUE_ARRARY_INDX_UNION = 4;
	private final int M_VALUE_ARRARY_INDX_CYT = 5;
	private final int M_VALUE_ARRARY_INDX_BINARYTEK = 6;
	private final int M_VALUE_ARRARY_INDX_XINBAS = 7;
	private final int M_VALUE_ARRARY_INDX_HIWORLD = 8;
	private final int M_VALUE_ARRARY_INDX_OD = 9;
	private final int M_VALUE_ARRARY_INDX_COUNT = M_VALUE_ARRARY_INDX_OD + 1;

	private String[] mManufacturerPreferenceValue;

	/**
	 * canbox_all & mCanboxValueXXX must correspond to the array in arrarys.xml
	 * canbox_select_xxx
	 */
	private final String[] canbox_all = {
			MachineConfig.VALUE_CANBOX_NONE,
			MachineConfig.VALUE_CANBOX_FORD_SIMPLE, // 1
			MachineConfig.VALUE_CANBOX_TOYOTA, // 2
			MachineConfig.VALUE_CANBOX_MAZDA, // 3
			MachineConfig.VALUE_CANBOX_BESTURN_X80, // 4
			MachineConfig.VALUE_CANBOX_TEANA_2013, // 5
			MachineConfig.VALUE_CANBOX_OPEL, // 6
			MachineConfig.VALUE_CANBOX_VW, // 7
			MachineConfig.VALUE_CANBOX_MITSUBISHI_OUTLANDER_SIMPLE, // 8
			MachineConfig.VALUE_CANBOX_HY, // 9
			MachineConfig.VALUE_CANBOX_PSA_BAGOO, // 10
			MachineConfig.VALUE_CANBOX_GM_SIMPLE, // 11
			MachineConfig.VALUE_CANBOX_HONDA_DA_SIMPLE, // 12
			MachineConfig.VALUE_CANBOX_VW_GOLF_SIMPLE, // 13
			MachineConfig.VALUE_CANBOX_RAM_FIAT, // 14
			MachineConfig.VALUE_CANBOX_RENAULT_MEGANE_FLUENCE_SMPLE, // 15
			MachineConfig.VALUE_CANBOX_BMW_E90X1_UNION, // 16
			MachineConfig.VALUE_CANBOX_FIAT, // 17
			MachineConfig.VALUE_CANBOX_FORD_MONDEO, // 18
			MachineConfig.VALUE_CANBOX_PSA, // 19
			MachineConfig.VALUE_CANBOX_BENZ_BAGOO, // 20
			MachineConfig.VALUE_CANBOX_KADJAR_RAISE, // 21
			MachineConfig.VALUE_CANBOX_GMC_SIMPLE, // 22
			MachineConfig.VALUE_CANBOX_BENZ_B200_UNION, // 23
			MachineConfig.VALUE_CANBOX_MAZDA_BT50_SIMPLE, // 24
			MachineConfig.VALUE_CANBOX_JEEP_SIMPLE, // 25
			MachineConfig.VALUE_CANBOX_ACCORD7_CHANGYUANTONG, // 26
			MachineConfig.VALUE_CANBOX_TOYOTA_BINARYTEK, // 27
			MachineConfig.VALUE_CANBOX_MAZDA_XINBAS, // 28
			MachineConfig.VALUE_CANBOX_PEUGEOT206, // 29
			MachineConfig.VALUE_CANBOX_ACCORD2013, // 30
			MachineConfig.VALUE_CANBOX_NISSAN2013, // 31
			MachineConfig.VALUE_CANBOX_PORSCHE_UNION, // 32
			MachineConfig.VALUE_CANBOX_MAZDA3_BINARYTEK, // 33
			MachineConfig.VALUE_CANBOX_BRAVO_UNION, // 34
			MachineConfig.VALUE_CANBOX_TOUAREG_HIWORLD, // 35
			MachineConfig.VALUE_CANBOX_DACIA_SIMPLE, // 36
			MachineConfig.VALUE_CANBOX_NISSAN_RAISE, // 37
			MachineConfig.VALUE_CANBOX_PETGEO_RAISE, // 38
			MachineConfig.VALUE_CANBOX_FORD_EXPLORER_SIMPLE, // 39
			MachineConfig.VALUE_CANBOX_ACCORD_BINARYTEK, // 40
			MachineConfig.VALUE_CANBOX_AUDI_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_SUBARU_ODS, // 42
			MachineConfig.VALUE_CANBOX_MINI_HIWORD, // 42
			MachineConfig.VALUE_CANBOX_NISSAN_BINARYTEK, // 42
			MachineConfig.VALUE_CANBOX_BENZ_VITO_SIMPLE, // 42
			MachineConfig.VALUE_CANBOX_VW_MQB_RAISE, // 41
			MachineConfig.VALUE_CANBOX_CHERY_OD, // 41
			MachineConfig.VALUE_CANBOX_CHRYSLER_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_MAZDA3_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_OBD_BINARUI, // 50
			MachineConfig.VALUE_CANBOX_HAFER_H2, // 51
			MachineConfig.VALUE_CANBOX_HONDA_RAISE, // 51
			MachineConfig.VALUE_CANBOX_PETGEO_SCREEN_RAISE, // 51
			MachineConfig.VALUE_CANBOX_FORD_RAISE, // 51
			MachineConfig.VALUE_CANBOX_SMART_HAOZHENG, // 51
			MachineConfig.VALUE_CANBOX_LANDROVER_HAOZHENG, // 51
			MachineConfig.VALUE_CANBOX_PEUGEOT307_UNION, // 41
			MachineConfig.VALUE_CANBOX_MAZDA_CX5_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_RX330_HAOZHENG, // 41
			MachineConfig.VALUE_CANBOX_PSA206_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_X30_RAISE, // 41
			MachineConfig.VALUE_CANBOX_MONDEO_DAOJUN,
			MachineConfig.VALUE_CANBOX_JEEP_XINBAS,
			MachineConfig.VALUE_CANBOX_OUSHANG_RAISE,
			MachineConfig.VALUE_CANBOX_FIAT_EGEA_RAISE,
			MachineConfig.VALUE_CANBOX_HY_RAISE,
			MachineConfig.VALUE_CANBOX_ALPHA_BAGOO,
			MachineConfig.VALUE_CANBOX_TOYOTA_RAISE,
			MachineConfig.VALUE_CANBOX_MINI_HAOZHENG,
			MachineConfig.VALUE_CANBOX_SUBARU_SIMPLE,
			MachineConfig.VALUE_CANBOX_GM_OD,
			MachineConfig.VALUE_CANBOX_MAZDA_RAISE,
			MachineConfig.VALUE_CANBOX_GM_RAISE, };

	private final String[] mCanboxValueSimple = {
			MachineConfig.VALUE_CANBOX_NONE,
			MachineConfig.VALUE_CANBOX_FORD_SIMPLE, // 1
			MachineConfig.VALUE_CANBOX_TOYOTA, // 2
			MachineConfig.VALUE_CANBOX_TEANA_2013, // 5
			MachineConfig.VALUE_CANBOX_OPEL, // 6
			MachineConfig.VALUE_CANBOX_VW, // 7
			MachineConfig.VALUE_CANBOX_MITSUBISHI_OUTLANDER_SIMPLE, // 8
			MachineConfig.VALUE_CANBOX_HY, // 9
			MachineConfig.VALUE_CANBOX_GM_SIMPLE, // 11
			MachineConfig.VALUE_CANBOX_HONDA_DA_SIMPLE, // 12
			MachineConfig.VALUE_CANBOX_VW_GOLF_SIMPLE, // 13
			MachineConfig.VALUE_CANBOX_RAM_FIAT, // 14
			MachineConfig.VALUE_CANBOX_RENAULT_MEGANE_FLUENCE_SMPLE, // 15
			MachineConfig.VALUE_CANBOX_FIAT, // 17
			MachineConfig.VALUE_CANBOX_FORD_MONDEO, // 18
			MachineConfig.VALUE_CANBOX_PSA, // 19
			MachineConfig.VALUE_CANBOX_GMC_SIMPLE, // 22
			MachineConfig.VALUE_CANBOX_MAZDA_BT50_SIMPLE, // 24
			MachineConfig.VALUE_CANBOX_JEEP_SIMPLE, // 25
			MachineConfig.VALUE_CANBOX_PEUGEOT206, // 29
			MachineConfig.VALUE_CANBOX_ACCORD2013, // 30
			MachineConfig.VALUE_CANBOX_NISSAN2013, // 31
			MachineConfig.VALUE_CANBOX_DACIA_SIMPLE, // 36
			MachineConfig.VALUE_CANBOX_FORD_EXPLORER_SIMPLE, // 39
			MachineConfig.VALUE_CANBOX_AUDI_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_BENZ_VITO_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_CHRYSLER_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_MAZDA3_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_MAZDA_CX5_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_PSA206_SIMPLE, // 41
			MachineConfig.VALUE_CANBOX_SUBARU_SIMPLE, };
	private final String[] mCanboxValueRaise = {
			MachineConfig.VALUE_CANBOX_NONE,
			MachineConfig.VALUE_CANBOX_MAZDA, // 3
			MachineConfig.VALUE_CANBOX_BESTURN_X80, // 4
			MachineConfig.VALUE_CANBOX_VW, // 7
			MachineConfig.VALUE_CANBOX_VW_GOLF_SIMPLE, // 13
			MachineConfig.VALUE_CANBOX_KADJAR_RAISE, // 21
			MachineConfig.VALUE_CANBOX_NISSAN_RAISE, // 37
			MachineConfig.VALUE_CANBOX_PETGEO_RAISE, // 38
			MachineConfig.VALUE_CANBOX_VW_MQB_RAISE, // 38
			MachineConfig.VALUE_CANBOX_HONDA_RAISE, // 51
			MachineConfig.VALUE_CANBOX_PETGEO_SCREEN_RAISE, // 51
			MachineConfig.VALUE_CANBOX_FORD_RAISE, // 51
			MachineConfig.VALUE_CANBOX_X30_RAISE, // 41
			MachineConfig.VALUE_CANBOX_OUSHANG_RAISE, // 41
			MachineConfig.VALUE_CANBOX_FIAT_EGEA_RAISE,
			MachineConfig.VALUE_CANBOX_HY_RAISE,
			MachineConfig.VALUE_CANBOX_TOYOTA_RAISE,
			MachineConfig.VALUE_CANBOX_MAZDA_RAISE,
			MachineConfig.VALUE_CANBOX_GM_RAISE, };
	private final String[] mCanboxValuebagoo = {
			MachineConfig.VALUE_CANBOX_NONE,
			MachineConfig.VALUE_CANBOX_PSA_BAGOO, // 10
			MachineConfig.VALUE_CANBOX_BENZ_BAGOO, // 20
			MachineConfig.VALUE_CANBOX_SUBARU_ODS, // 42
			MachineConfig.VALUE_CANBOX_ALPHA_BAGOO, };
	private final String[] mCanboxValueUnion = {
			MachineConfig.VALUE_CANBOX_NONE,
			MachineConfig.VALUE_CANBOX_BMW_E90X1_UNION, // 16
			MachineConfig.VALUE_CANBOX_FIAT, // 17
			MachineConfig.VALUE_CANBOX_BENZ_B200_UNION, // 23
			MachineConfig.VALUE_CANBOX_PORSCHE_UNION, // 32
			MachineConfig.VALUE_CANBOX_BRAVO_UNION, // 34
			MachineConfig.VALUE_CANBOX_PEUGEOT307_UNION, // 41
	};
	private final String[] mCanboxValueCYT = { MachineConfig.VALUE_CANBOX_NONE,
			MachineConfig.VALUE_CANBOX_ACCORD7_CHANGYUANTONG, // 26
	};
	private final String[] mCanboxValueOD = {
			MachineConfig.VALUE_CANBOX_NONE,
			MachineConfig.VALUE_CANBOX_CHERY_OD, // 41
			MachineConfig.VALUE_CANBOX_RX330_HAOZHENG, // 41
			MachineConfig.VALUE_CANBOX_MONDEO_DAOJUN, // 41
			MachineConfig.VALUE_CANBOX_MINI_HAOZHENG,
			MachineConfig.VALUE_CANBOX_GM_OD, };
	private final String[] mCanboxValueBinarytek = {
			MachineConfig.VALUE_CANBOX_NONE,
			MachineConfig.VALUE_CANBOX_TOYOTA_BINARYTEK, // 27
			MachineConfig.VALUE_CANBOX_MAZDA3_BINARYTEK, // 33
			MachineConfig.VALUE_CANBOX_ACCORD_BINARYTEK, // 40
			MachineConfig.VALUE_CANBOX_NISSAN_BINARYTEK, // 42
			MachineConfig.VALUE_CANBOX_OBD_BINARUI, // 50
	};
	private final String[] mCanboxValueXinbas = {
			MachineConfig.VALUE_CANBOX_NONE,
			MachineConfig.VALUE_CANBOX_MAZDA_XINBAS, // 28
			MachineConfig.VALUE_CANBOX_HAFER_H2, // 51
			MachineConfig.VALUE_CANBOX_JEEP_XINBAS, };
	private final String[] mCanboxValueHiworld = {
			MachineConfig.VALUE_CANBOX_NONE,
			MachineConfig.VALUE_CANBOX_TOUAREG_HIWORLD, // 35
			MachineConfig.VALUE_CANBOX_MINI_HIWORD // 43
	};

	private String getManufacturer(String canboxType) {
		int i;
		if (canboxType == null || canboxType.isEmpty())
			return mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_ALL];

		for (i = 0; i < mCanboxValueSimple.length; i++)
			if (mCanboxValueSimple[i].equals(canboxType))
				return mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_SIMPLE];
		for (i = 0; i < mCanboxValueRaise.length; i++)
			if (mCanboxValueRaise[i].equals(canboxType))
				return mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_RAISE];
		for (i = 0; i < mCanboxValuebagoo.length; i++)
			if (mCanboxValuebagoo[i].equals(canboxType))
				return mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_BAGOO];
		for (i = 0; i < mCanboxValueUnion.length; i++)
			if (mCanboxValueUnion[i].equals(canboxType))
				return mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_UNION];
		for (i = 0; i < mCanboxValueCYT.length; i++)
			if (mCanboxValueCYT[i].equals(canboxType))
				return mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_CYT];
		for (i = 0; i < mCanboxValueBinarytek.length; i++)
			if (mCanboxValueBinarytek[i].equals(canboxType))
				return mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_BINARYTEK];
		for (i = 0; i < mCanboxValueXinbas.length; i++)
			if (mCanboxValueXinbas[i].equals(canboxType))
				return mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_XINBAS];
		for (i = 0; i < mCanboxValueHiworld.length; i++)
			if (mCanboxValueHiworld[i].equals(canboxType))
				return mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_HIWORLD];
		return null;
	}

	private void updateManufacturerPreference(String value) {
		mCanboxManufacturerPreference.setValue(value);
		mCanboxManufacturerPreference.setSummary(mCanboxManufacturerPreference.getEntry());
	}

	private void setCanboxPreferenceEntry(String manufactuer) {
		if (manufactuer == null)
			manufactuer = "";
		if (manufactuer.equals(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_SIMPLE])) {
			mCanboxSettingPreference.setEntries(getResources().getStringArray(R.array.canbox_select_simple));
			mCanboxSettingPreference.setEntryValues(mCanboxValueSimple);
		} else if (manufactuer.equals(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_RAISE])) {
			mCanboxSettingPreference.setEntries(getResources().getStringArray(R.array.canbox_select_raise));
			mCanboxSettingPreference.setEntryValues(mCanboxValueRaise);
		} else if (manufactuer.equals(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_BAGOO])) {
			mCanboxSettingPreference.setEntries(getResources().getStringArray(R.array.canbox_select_bagoo));
			mCanboxSettingPreference.setEntryValues(mCanboxValuebagoo);
		} else if (manufactuer.equals(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_UNION])) {
			mCanboxSettingPreference.setEntries(getResources().getStringArray(R.array.canbox_select_union));
			mCanboxSettingPreference.setEntryValues(mCanboxValueUnion);
		} else if (manufactuer.equals(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_CYT])) {
			mCanboxSettingPreference.setEntries(getResources().getStringArray(R.array.canbox_select_cyt));
			mCanboxSettingPreference.setEntryValues(mCanboxValueCYT);
		} else if (manufactuer.equals(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_OD])) {
			mCanboxSettingPreference.setEntries(getResources().getStringArray(R.array.canbox_select_od));
			mCanboxSettingPreference.setEntryValues(mCanboxValueOD);
		} else if (manufactuer.equals(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_BINARYTEK])) {
			mCanboxSettingPreference.setEntries(getResources().getStringArray(R.array.canbox_select_binarytek));
			mCanboxSettingPreference.setEntryValues(mCanboxValueBinarytek);
		} else if (manufactuer.equals(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_XINBAS])) {
			mCanboxSettingPreference.setEntries(getResources().getStringArray(R.array.canbox_select_xinbas));
			mCanboxSettingPreference.setEntryValues(mCanboxValueXinbas);
		} else if (manufactuer.equals(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_HIWORLD])) {
			mCanboxSettingPreference.setEntries(getResources().getStringArray(R.array.canbox_select_hiworld));
			mCanboxSettingPreference.setEntryValues(mCanboxValueHiworld);
		} else {
			mCanboxSettingPreference.setEntries(getResources().getStringArray(R.array.canbox_select));
			mCanboxSettingPreference.setEntryValues(canbox_all);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mThis = this;
		setContentView(R.layout.canbox_setting);
		addPreferencesFromResource(R.xml.canbox_settings);

		mManufacturerPreferenceValue = getResources().getStringArray(R.array.canbox_manufacturer_values);
		if (mManufacturerPreferenceValue != null && mManufacturerPreferenceValue.length != M_VALUE_ARRARY_INDX_COUNT) {
			Log.e(TAG, "load manufacturer value failed");
			return;
		}
		mCanboxManufacturerPreference = (ListPreference) findPreference(KEY_MANUFACTURER);
		mCanboxManufacturerPreference.setValue(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_ALL]);
		mCanboxManufacturerPreference.setOnPreferenceChangeListener(this);

		mCanboxSettingPreference = (ListPreference) findPreference(KEY_CANBOX);
		mCanboxSettingPreference.setEntryValues(canbox_all);
		mCanboxSettingPreference.setValue(MachineConfig.VALUE_CANBOX_NONE);
		mCanboxSettingPreference.setSummary(mCanboxSettingPreference.getEntry());
		mCanboxSettingPreference.setOnPreferenceChangeListener(this);

		mCanboxKeyPreference = (ListPreference) findPreference(KEY_CANBOX_KEY);
		mCanboxKeyPreference.setOnPreferenceChangeListener(this);

		mCanboxEQPreference = (ListPreference) findPreference(KEY_CANBOX_EQ);
		mCanboxEQPreference.setOnPreferenceChangeListener(this);

		String[] entry = { getString(R.string.normal),getString(R.string.canbox_pre_next),	getString(R.string.canbox_volume_increase_decrease) };
		String[] value = { "0", "1", "2" };

		ListPreference lp;

//		lp = (ListPreference) findPreference("canbox_key_change");
//		lp.setEntries(entry);
//		lp.setEntryValues(value);
//		lp.setOnPreferenceChangeListener(this);
		// lp = (ListPreference) findPreference("canbox_front_door");
		// lp.setEntries(entry);
		// lp.setEntryValues(value);
		// lp.setOnPreferenceChangeListener(this);
		// lp = (ListPreference) findPreference("canbox_rear_door");
		// lp.setEntries(entry);
		// lp.setEntryValues(value);
		// lp.setOnPreferenceChangeListener(this);

		String[] entry1 = { getString(R.string.normal),getString(R.string.change), getString(R.string.hide) };
		String[] value1 = { "0", "1", "2" };

		lp = (ListPreference) findPreference("canbox_front_door");
		lp.setEntries(entry1);
		lp.setEntryValues(value1);
		lp.setOnPreferenceChangeListener(this);

		lp = (ListPreference) findPreference("canbox_rear_door");
		lp.setEntries(entry1);
		lp.setEntryValues(value1);
		lp.setOnPreferenceChangeListener(this);

		findViewById(R.id.btn_cancel).setOnClickListener(this);
		findViewById(R.id.btn_ok).setOnClickListener(this);
		findViewById(R.id.btn_advanced).setOnClickListener(this);

		mEQVolume = (PreferenceScreen) findPreference("canbox_eq_volume");

	}

	private PreferenceScreen mEQVolume;
	private String mWillSetCan = null;

	private void initEQVolume(String can) {

		mWillSetCan = can;
		if (MachineConfig.VALUE_CANBOX_TOYOTA.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_TOYOTA_RAISE.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_HY.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_CHRYSLER_SIMPLE.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_MITSUBISHI_OUTLANDER_SIMPLE.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_RX330_HAOZHENG.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_HY_RAISE.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_SUBARU_SIMPLE.equalsIgnoreCase(can))
		{
			getPreferenceScreen().addPreference(mEQVolume);
			mEQVolume.setOnPreferenceClickListener(this);

			volume = MachineConfig.getIntProperty2(SystemConfig.CANBOX_EQ_VOLUME);

			if (MachineConfig.VALUE_CANBOX_TOYOTA.equalsIgnoreCase(can)	|| MachineConfig.VALUE_CANBOX_TOYOTA_RAISE.equalsIgnoreCase(can))
			{
				max = 63;
				if (volume == -1 || volume > max) {
					volume = 45;
				}
			} else if (MachineConfig.VALUE_CANBOX_HY.equalsIgnoreCase(can)) {
				max = 35;
				if (volume == -1 || volume > max) {
					volume = 30;
				}
			} else if (MachineConfig.VALUE_CANBOX_CHRYSLER_SIMPLE.equalsIgnoreCase(can)) {
				max = 38;
				if (volume == -1 || volume > max) {
					volume = 28;
				}
			} else if (MachineConfig.VALUE_CANBOX_MITSUBISHI_OUTLANDER_SIMPLE.equalsIgnoreCase(can)) {
				max = 45;
				if (volume == -1 || volume > max) {
					volume = 38;
				}
			} else if (MachineConfig.VALUE_CANBOX_RX330_HAOZHENG
					.equalsIgnoreCase(can)) {
				max = 63;
				if (volume == -1 || volume > max) {
					volume = 40;
				}
			} else if (MachineConfig.VALUE_CANBOX_HY_RAISE
					.equalsIgnoreCase(can)) {
				max = 35;
				if (volume == -1 || volume > max) {
					volume = 28;
				}
			} else if (MachineConfig.VALUE_CANBOX_SUBARU_SIMPLE
					.equalsIgnoreCase(can)) {
				if ("1".equals(mCarType2)) {
					max = 0x26;
					if (volume == -1 || volume > max) {
						volume = 30;
					}
				} else {
					max = 0x3f;
					if (volume == -1 || volume > max) {
						volume = 45;
					}
				}

			}

		} else {
			getPreferenceScreen().removePreference(mEQVolume);
		}
	}

	MultiSelectListPreference mOtherSettings;

	private void updateOtherSettings(String can) {
		if (mOtherSettings == null) {

			mOtherSettings = (MultiSelectListPreference) findPreference("canbox_other_settings");
			mOtherSettings.setOnPreferenceChangeListener(this);
			mOtherSettings.setOnPreferenceClickListener(this);
		}

		getPreferenceScreen().addPreference(mOtherSettings);
		String[] entry2 = { getResources().getString(R.string.radar_volume),
				getResources().getString(R.string.radar_ui) ,
				getResources().getString(R.string.radar_only_in_rverse),
				getResources().getString(R.string.time_hour_add_1) ,
				getResources().getString(R.string.time_hour_minus_1)};
		
		if (MachineConfig.VALUE_CANBOX_HONDA_RAISE.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_HONDA_DA_SIMPLE
						.equalsIgnoreCase(can)) {
			String[] entry3 = {
					getResources().getString(R.string.radar_volume),
					getResources().getString(R.string.radar_ui),
					getResources().getString(R.string.radar_only_in_rverse),
					getResources().getString(R.string.time_hour_add_1),
					getResources().getString(R.string.time_hour_minus_1),
					getResources().getString(R.string.right_camera) };
			entry2 = entry3;
		}
		
		String[] value2 = { "1", "2", "3", "4", "5", "6" };

		
		mOtherSettings.setEntries(entry2);
		mOtherSettings.setEntryValues(value2);

		String summary = "";
		for (int i = 0; i < entry2.length; ++i) {
			summary += entry2[i] + " ";
		}

		mOtherSettings.setSummary(summary);

		HashSet<String> ss = new HashSet<String>();

		if (mCarOtherSettings != null) {
			try {
				int v = Integer.parseInt(mCarOtherSettings);
				for (int i = 0; i < 32; i++) {
					if ((v & (0x1 << i)) != 0) {
						ss.add("" + i);
					}
				}
			} catch (Exception ignored) {
			}
		}
		mOtherSettings.setValues(ss);
		mOtherSettings.setSummary(summary);
	}
	

	MultiSelectListPreference mKeyChangeSettings;

	private void updateKeysChageSettings(String can) {
		if (mKeyChangeSettings == null) {

			mKeyChangeSettings = (MultiSelectListPreference) findPreference("canbox_key_change_ex");
			mKeyChangeSettings.setOnPreferenceChangeListener(this);
			mKeyChangeSettings.setOnPreferenceClickListener(this);
		}

		getPreferenceScreen().addPreference(mKeyChangeSettings);
		String[] entry2 = {getString(R.string.canbox_pre_next),getString(R.string.canbox_volume_increase_decrease) };
		String[] value2 = { "0", "1" };

		mKeyChangeSettings.setEntries(entry2);
		mKeyChangeSettings.setEntryValues(value2);

		String summary = "";
		for (int i = 0; i < entry2.length; ++i) {
			summary += entry2[i] + " ";
		}

		mKeyChangeSettings.setSummary(summary);

		HashSet<String> ss = new HashSet<String>();

		if (mChangeKey != null) {
			try {
				int v = Integer.parseInt(mChangeKey);
				for (int i = 0; i < 32; i++) {
					if ((v & (0x1 << i)) != 0) {
						ss.add("" + i);
					}
				}
			} catch (Exception ignored) {
			}
		}
		mKeyChangeSettings.setValues(ss);
		mKeyChangeSettings.setSummary(summary);
	}

	ListPreference mLPCarType2;

	private void updateCarType2(String can) {

		if (mLPCarType2 == null) {

			mLPCarType2 = (ListPreference) findPreference("canbox_car_type2");
			mLPCarType2.setOnPreferenceChangeListener(this);
		}

		if (MachineConfig.VALUE_CANBOX_FORD_RAISE.equalsIgnoreCase(can)
				&& !"1".equals(mCarType)) {
			getPreferenceScreen().addPreference(mLPCarType2);
			String[] entry2 = { "0: 12 focus/eco sport/kuga & 13 fiesta (Low)",
					"1: 12 focus/eco sport/kuga & 13 fiesta (Middle)",
					"2: 12 focus/eco sport/kuga & 13 fiesta (High)",
					"3: 17 kuga & 18 kuga (Low)",
					"4: 17 kuga & 18 kuga (Middle)",
					"5: 17 kuga & 18 kuga (High)", "6: 19 focus  (Low)",
					"7: 19 focus  (Middle)", "8: 19 focus  (High)",
					"9: 19 Taurus  (Low)", "10: 19 Taurus  (Middle)",
					"11: 19 Taurus  (High)", "12: 19 Territory  (Low)",
					"13: 19 Territory  (Middle)", "14: 19 Territory  (High)",
					"15: No Sync  (Low)", "16: No Sync  (Middle)",
					"17: No Sync  (High)" };
			String[] value2 = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
					"9", "10", "11", "12", "13", "14", "15", "16", "17" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_RX330_HAOZHENG
				.equalsIgnoreCase(can) && !"1".equals(mCarType)) {
			getPreferenceScreen().addPreference(mLPCarType2);
			String[] entry2 = { "0: None", "1: After installation host",
					"2: Front-mounted host" };
			String[] value2 = { "0", "1", "2" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		} else if ((MachineConfig.VALUE_CANBOX_NISSAN2013.equalsIgnoreCase(can) || MachineConfig.VALUE_CANBOX_NISSAN_RAISE
				.equalsIgnoreCase(can))
				&& ("1".equals(mKeyType) || "3".equals(mKeyType)
						|| "4".equals(mKeyType) || "6".equals(mKeyType) || "5"
							.equals(mKeyType))) {
			getPreferenceScreen().addPreference(mLPCarType2);
			String[] entry2 = { "0: Show 360 Button", "1: Hide 360 Button" };
			String[] value2 = { "0", "1" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_OUSHANG_RAISE
				.equalsIgnoreCase(can) && ("3".equals(mCarType))) {
			getPreferenceScreen().addPreference(mLPCarType2);

			String[] entry2 = { "1: 18 款长安欧尚X70A", "2: 18 款长安凌轩",
					"3: 18 款长安逸动DT", "4: 18 款长安欧尚A600", "5: 17 款长安CX70",
					"6: 18 款长安欧尚科赛", "7: 18 款长安CS75/⑤ 20 款长安CS75",
					"8: 19 款长安悦翔", "9: 19 款长安CS15", "10: 20 款长安欧尚X7",
					"11: 19 款长安逸动", "12: 19 款长安CS35", "13: 19 款长安 欧尚科尚",
					"14: 19 款长安悦翔 低配" };
			String[] value2 = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
					"10", "11", "12", "13", "14" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_HY_RAISE.equalsIgnoreCase(can)
				&& ("32".equals(mCarType))) {
			getPreferenceScreen().addPreference(mLPCarType2);

			String[] entry2 = { "1: 18款起亚智跑",
					"2: 18款现代索纳塔 9 混动 低配 (欧迪协议 19 款现代途胜)",
					"3: 18款现代索纳塔 9 混动 中配", "4: 18款现代索纳塔 9 混动 高配",
					"5: 10~15款现代 IX35 低配", "6: 19款现代菲斯塔", "7: 19款现代胜达 IX45",
					"8: 19款现代途胜", "9: 19款起亚 KX5", "10: 19 款起亚 K3 （新能源）",
					"11: 20 款起亚 KX3 傲跑", "12: 19 款起亚 K5 （新能源）" };
			String[] value2 = { "1", "2", "3", "4", "129", "5", "6", "7", "8",
					"9", "10", "11", "12" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_TOYOTA.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_TOYOTA_RAISE
						.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType2);

			String[] entry2 = { "1: None",
					"2: Open 360 if back radar come (Hide Radar UI)",
					"3: Open 360 if back radar come" };
			String[] value2 = { "0", "1", "2" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_SUBARU_SIMPLE
				.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType2);

			String[] entry2 = { "1: SU-SS-01", "2:SU-SS-02" };
			String[] value2 = { "0", "1" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_MAZDA_XINBAS
				.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType2);

			String[] entry2 = { "1: Normal", "2:2020 Mazda3" };
			String[] value2 = { "0", "1" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_FORD_EXPLORER_SIMPLE
				.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType2);

			String[] entry2 = { "1: Normal", "2:F150 no front camera",
					"3:F150 with front camera" };
			String[] value2 = { "0", "1", "2" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_MAZDA_RAISE.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType2);

			String[] entry2 = { "0: Normal", "1:2020 Atenza(high)" };
			String[] value2 = { "0", "1" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_HY.equalsIgnoreCase(can)||
				MachineConfig.VALUE_CANBOX_TOUAREG_HIWORLD.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType2);

			String[] entry2 = { "0: Normal", "1:Time hour +1", "2:Time hour -1" };
			String[] value2 = { "0", "1", "2" };

			mLPCarType2.setEntries(entry2);
			mLPCarType2.setEntryValues(value2);

			if (mCarType2 != null) {
				mLPCarType2.setValue(mCarType2);
			} else {
				mLPCarType2.setValue("0");
			}

			mLPCarType2.setSummary(mLPCarType2.getEntry());
		}
		// else if
		// (MachineConfig.VALUE_CANBOX_MAZDA_RAISE.equalsIgnoreCase(can)) {
		// getPreferenceScreen().addPreference(mLPCarType2);
		//
		//
		// String[] entry2 = { "1: Normal",
		// "1:2020 mazda3 TPMS" ,};
		// String[] value2 = { "0", "1"};
		//
		// mLPCarType2.setEntries(entry2);
		// mLPCarType2.setEntryValues(value2);
		//
		// if (mCarType2 != null) {
		// mLPCarType2.setValue(mCarType2);
		// } else {
		// mLPCarType2.setValue("0");
		// }
		//
		// mLPCarType2.setSummary(mLPCarType2.getEntry());
		// }
		// else if
		// (MachineConfig.VALUE_CANBOX_JEEP_SIMPLE.equalsIgnoreCase(can)) {
		// getPreferenceScreen().addPreference(mLPCarType2);
		//
		//
		// String[] entry2 = { "1: None", "2: Type 1 High", "3: Type 2 Low", };
		//
		// String[] value2 = { "0", "1", "2" };
		//
		// mLPCarType2.setEntries(entry2);
		// mLPCarType2.setEntryValues(value2);
		//
		// if (mCarType2 != null) {
		// mLPCarType2.setValue(mCarType2);
		// } else {
		// mLPCarType2.setValue("0");
		// }
		//
		// mLPCarType2.setSummary(mLPCarType2.getEntry());
		// }
		else {
			getPreferenceScreen().removePreference(mLPCarType2);
		}
	}

	ListPreference mLPCarType;

	private void updateCarType(String can) {
		updateOtherSettings(can);
		if (mLPCarType == null) {
			mLPCarType = (ListPreference) findPreference("canbox_car_type");
			mLPCarType.setOnPreferenceChangeListener(this);
		}
		if (MachineConfig.VALUE_CANBOX_JEEP_SIMPLE.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);
			String[] entry2 = { "1:2016 Cherokee", "2:2016 Renegade",
					"3:2016 FiatAegea", "4:2017 JeepCompass",
					"5:2015 FiatDoblo", "6:2014 Grand Cherokee",
					"7:2018 Renegade M" };

			String[] value2 = { "1", "2", "3", "4", "5", "6", "7" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_NISSAN2013.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);
			String[] entry2 = { "1:NO", "2:Type Hi", "3:Type KLD360",
					"4:Type IXB", "5:1050" };
			String[] value2 = { "1", "2", "3", "4", "5" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_PSA_BAGOO.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);
			String[] entry2 = { "1:Normal", "2:Type 2" };
			String[] value2 = { "1", "2", };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_FORD_SIMPLE.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_FORD_EXPLORER_SIMPLE
						.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_FORD_RAISE.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);
			String[] entry2 = { "0:Sync", "1:No SYNC", "2:SYNC3",
					"3:SYNC3 Reverse" };
			String[] value2 = { "0", "1", "2", "3" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_ACCORD_BINARYTEK
				.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);
			String[] entry2 = { "0: Normal", "1:No Right Camera" };
			String[] value2 = { "0", "1" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_NISSAN_BINARYTEK
				.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);
			String[] entry2 = { "0: Normal", "1:Simaa", "2:Simaa High" };
			String[] value2 = { "0", "1", "2" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_HY.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);
			String[] entry2 = { "1: type 1", "2: type 2", "3: type 3",
					"4: 1050" };
			String[] value2 = { "1", "2", "3", "4" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_MAZDA_XINBAS
				.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_MAZDA3_SIMPLE
						.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);
			String[] entry2 = { "1: Car CD", "2:No Car CD" };
			String[] value2 = { "0", "1" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_TOYOTA.equalsIgnoreCase(can)
				|| MachineConfig.VALUE_CANBOX_TOYOTA_RAISE
						.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);
			String[] entry2 = { "0: Auto", "1:12 Camry & 16 Cruiser",
					"2:15 Camry ", "3:18 Camry ", "4:15 Highlander Manual AC",
					"5:15 Highlander Auto AC", "6:14 Tundra Manual AC",
					"7:14 Tundra Auto AC", "8:10 Prado Auto AC", };
			String[] value2 = { "0", "1", "2", "3", "4", "5", "6", "7", "8" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		}
//		else if (MachineConfig.VALUE_CANBOX_HONDA_RAISE.equalsIgnoreCase(can)) {
//			getPreferenceScreen().addPreference(mLPCarType);
//			String[] entry2 = { "0: Normal", "1: No Right View",
//					"2: No Backview Button" };
//			String[] value2 = { "0", "1", "2" };
//
//			mLPCarType.setEntries(entry2);
//			mLPCarType.setEntryValues(value2);
//
//			if (mCarType != null) {
//				mLPCarType.setValue(mCarType);
//			} else {
//				mLPCarType.setValue("0");
//			}
//
//			mLPCarType.setSummary(mLPCarType.getEntry());
//		} 
		else if (MachineConfig.VALUE_CANBOX_PETGEO_SCREEN_RAISE.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);
			String[] entry2 = { "0: 508", "1: RZC" };
			String[] value2 = { "0", "1" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}
			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_RX330_HAOZHENG
				.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);

			String[] entry2 = { "1: Lexus IS low", "2: Lexus IS high",
					"3: 06-12 ES low", "4: 06-12 ES high",
					"5: 02-09 PRADO low", "6: 02-09 PRADO high",
					"7: 09 Lexus IS low", "8: 09 Lexus IS high",
					"9: 05-09 Reiz low", "10:  05-09 Reiz high",
					"11: LC100 low", "12: LC100 high" };
			String[] value2 = { "32", "33", "48", "49", "64", "65", "80", "81",
					"96", "97", "98", "99" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_X30_RAISE.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);

			String[] entry2 = { "1: None", "2: 19 B50 Manual AC",
					"3: 19 B50 Auto AC", };
			String[] value2 = { "0", "1", "2" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_OUSHANG_RAISE
				.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);

			String[] entry2 = { "1: 启辰", "2: 传祺", "3: 长安", "4: (Renault)雷诺" };
			String[] value2 = { "1", "2", "3", "16" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_HY_RAISE.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);

			String[] entry2 = { "1: 启辰", "2: 传祺", "3: (Renault)雷诺",
					"4: (HY KIA)现代起亚" };
			String[] value2 = { "1", "2", "16", "32" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_GM_OD.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);

			String[] entry2 = { "1: Low", "2: High" };
			String[] value2 = { "0", "1" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else if (MachineConfig.VALUE_CANBOX_FIAT.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);

			String[] entry2 = { "1: type 1", "2: type 2", "2: type 3" };
			String[] value2 = { "0", "1", "2" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		}  else if (MachineConfig.VALUE_CANBOX_VW_GOLF_SIMPLE.equalsIgnoreCase(can)) {
			getPreferenceScreen().addPreference(mLPCarType);

			String[] entry2 = { "1: Normal", "2: Romania 2016 Octavia " };
			String[] value2 = { "0", "1" };

			mLPCarType.setEntries(entry2);
			mLPCarType.setEntryValues(value2);

			if (mCarType != null) {
				mLPCarType.setValue(mCarType);
			} else {
				mLPCarType.setValue("0");
			}

			mLPCarType.setSummary(mLPCarType.getEntry());
		} else {
			getPreferenceScreen().removePreference(mLPCarType);
			// String[] entry2 = { "none",
			// getString(R.string.change), getString(R.string.hide) };
			// String[] value2 = { "0" };
			//
			//
			// lp.setEntries(entry2);
			// lp.setEntryValues(value2);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		getCanboxSetting();
		updateCarType(getCanboxType());
		updateCarType2(getCanboxType());
		initEQVolume(getCanboxType());
		updateOtherSettings(getCanboxType());
		updateKeysChageSettings(getCanboxType());
		mPreCanboxType = mCanboxType;
		// if (onManufacturerChanged(getCanboxType(), null)) {
		updateCanboxSetting(getCanboxType());
		updateCanboxKey(null);
		// }
	}

	@Override
	public void onPause() {
		finish();
		super.onPause();
	}

	private void updateCanboxKeyValue(String which) {

		mCanboxKeyPreference.setValue(which);

		mCanboxKeyPreference.setSummary(mCanboxKeyPreference.getEntry());
	}

	private void updateCanboxEQValue(String which) {

		mCanboxEQPreference.setValue(which);

		mCanboxEQPreference.setSummary(mCanboxEQPreference.getEntry());
	}

	private String getCanboxType() {
		if (mCanboxValue != null) {
			String ss[] = mCanboxValue.split(",");
			return ss[0];
		}
		return null;
	}

	// private String getKeyType(String s) {
	// String ss[] = s.split(",");
	// if (ss.length > 1) {
	// return ss[1];
	// }
	// return null;
	// }

	private void updateCanboxKey(String s) {
		// String s = mCanboxValue;

		if (s == null) {
			s = mCanboxValue;
		}
		boolean show = false;
		if (s != null) {

			if (s.startsWith(MachineConfig.VALUE_CANBOX_GM_SIMPLE)) {
				show = true;

				String[] entry = { "normal", "envision_low", "GL8",	"ASTRA J CD600" };
				String[] value = { "0", "1", "2", "3" };
				mCanboxKeyPreference.setEntries(entry);
				mCanboxKeyPreference.setEntryValues(value);

				if (mKeyType == null) {
					updateCanboxKeyValue(value[0]);
				} else {
					updateCanboxKeyValue(mKeyType);
				}
			}
			else if (s.startsWith(MachineConfig.VALUE_CANBOX_NISSAN2013)
					|| s.startsWith(MachineConfig.VALUE_CANBOX_NISSAN_RAISE)) {
				show = true;

				String[] entry = { "mode1", "mode2 AVM", "mode3", "mode4 AVM",
						"mode5 AVM", "mode6 AVM", "mode7 AVM" };
				String[] value = { "0", "1", "2", "3", "4", "5", "6" };
				mCanboxKeyPreference.setEntries(entry);
				mCanboxKeyPreference.setEntryValues(value);

				if (mKeyType == null) {

					updateCanboxKeyValue(value[0]);
				} else {

					updateCanboxKeyValue(mKeyType);
				}
			}
			// else if (s.startsWith(MachineConfig.VALUE_CANBOX_NISSAN2013)) {
			// show = true;
			//
			// String[] entry = { "mode1", "mode2 AVM", "mode3", "mode4 AVM" };
			// String[] value = { "0", "1", "2", "3" };
			// mCanboxKeyPreference.setEntries(entry);
			// mCanboxKeyPreference.setEntryValues(value);
			//
			// if (mKeyType == null) {
			//
			// updateCanboxKeyValue(value[0]);
			// } else {
			//
			// updateCanboxKeyValue(mKeyType);
			// }
			// }
			else if (s.startsWith(MachineConfig.VALUE_CANBOX_HY)) {
				show = true;

				String[] entry = { "Normal", "KX5 H", "KX5 M", "SONATA 9 H","SONATA 9 M", "Sportage" };
				String[] value = { "0", "1", "2", "3", "4", "5" };

				mCanboxKeyPreference.setEntries(entry);
				mCanboxKeyPreference.setEntryValues(value);

				if (mKeyType == null) {
					updateCanboxKeyValue(value[0]);
				} else {
					updateCanboxKeyValue(mKeyType);
				}
			} else if (s.startsWith(MachineConfig.VALUE_CANBOX_FORD_SIMPLE)) {
				show = true;

				String[] entry = { "Normal", "Kuga" };
				String[] value = { "0", "1" };

				mCanboxKeyPreference.setEntries(entry);
				mCanboxKeyPreference.setEntryValues(value);

				if (mKeyType == null) {

					updateCanboxKeyValue(value[0]);
				} else {

					updateCanboxKeyValue(mKeyType);
				}
			} else if (s.startsWith(MachineConfig.VALUE_CANBOX_JEEP_SIMPLE)) {
				show = true;

				String[] entry = { "Phone char format: ASCII",
						"Phone char format: UNICODE" };
				String[] value = { "0", "1" };

				mCanboxKeyPreference.setEntries(entry);
				mCanboxKeyPreference.setEntryValues(value);

				if (mKeyType == null) {

					updateCanboxKeyValue(value[0]);
				} else {

					updateCanboxKeyValue(mKeyType);
				}
			} else if (s.startsWith(MachineConfig.VALUE_CANBOX_TOYOTA)
					|| s.startsWith(MachineConfig.VALUE_CANBOX_TOYOTA_RAISE)) {
				show = true;

				String[] entry = { "AVM Rav4", "AVM PRADO", "AVM 18Camry" };
				String[] value = { "0", "1", "2", };

				mCanboxKeyPreference.setEntries(entry);
				mCanboxKeyPreference.setEntryValues(value);

				if (mKeyType == null) {

					updateCanboxKeyValue(value[0]);
				} else {

					updateCanboxKeyValue(mKeyType);
				}
			} else if (s.startsWith(MachineConfig.VALUE_CANBOX_HAFER_H2)) {
				show = true;

				String[] entry = { "Normal", "17_H2" };
				String[] value = { "0", "1" };

				mCanboxKeyPreference.setEntries(entry);
				mCanboxKeyPreference.setEntryValues(value);

				if (mKeyType == null) {

					updateCanboxKeyValue(value[0]);
				} else {

					updateCanboxKeyValue(mKeyType);
				}
			} else if (s.startsWith(MachineConfig.VALUE_CANBOX_HONDA_RAISE)) {
				show = true;
				String[] entry = { "0: Normal", "1: 15 city & 16 crider" };
				String[] value = { "0", "1" };

				mCanboxKeyPreference.setEntries(entry);
				mCanboxKeyPreference.setEntryValues(value);

				if (mKeyType == null) {

					updateCanboxKeyValue(value[0]);
				} else {

					updateCanboxKeyValue(mKeyType);
				}
			} else if (s.startsWith(MachineConfig.VALUE_CANBOX_X30_RAISE)) {
				show = true;
				String[] entry = { "0: Normal", "1: B50 High" };
				String[] value = { "0", "1" };

				mCanboxKeyPreference.setEntries(entry);
				mCanboxKeyPreference.setEntryValues(value);

				if (mKeyType == null) {

					updateCanboxKeyValue(value[0]);
				} else {

					updateCanboxKeyValue(mKeyType);
				}
			} else if (s.startsWith(MachineConfig.VALUE_CANBOX_OUSHANG_RAISE)) {

				show = true;
				String[] entry = { "1: None", "2: 360", "2: Right View", "3: 360 & Right View" };
				String[] value = { "0", "1", "2", "3" };

				mCanboxKeyPreference.setEntries(entry);
				mCanboxKeyPreference.setEntryValues(value);

				if (mKeyType == null) {
					updateCanboxKeyValue(value[0]);
				} else {
					updateCanboxKeyValue(mKeyType);
				}
			}

		}

		if (show) {
			if (findPreference(KEY_CANBOX_KEY) == null) {
				getPreferenceScreen().addPreference(mCanboxKeyPreference);
				// updateCanboxKeyValue(null);
			}
		} else {
			if (findPreference(KEY_CANBOX_KEY) != null) {
				getPreferenceScreen().removePreference(mCanboxKeyPreference);
			}
		}
	}

	private void updateCanboxEQ(String s) {
		// String s = mCanboxValue;

		if (s == null) {
			s = mCanboxValue;
		}
		boolean show = false;
		if (s != null) {

			// if (s.startsWith(MachineConfig.VALUE_CANBOX_HY)
			// || s.startsWith(MachineConfig.VALUE_CANBOX_JEEP_SIMPLE)) {
			show = true;

			String[] entry = { "Auto Amplifier", "Amplifier", "No Amplifier" };
			String[] value = { "0", "1", "2" };
			mCanboxEQPreference.setEntries(entry);
			mCanboxEQPreference.setEntryValues(value);

			if (mCarEQ == null) {

				updateCanboxEQValue(value[0]);
			} else {

				updateCanboxEQValue(mCarEQ);
			}
			// }
		}

		if (show) {
			if (findPreference(KEY_CANBOX_EQ) == null) {
				getPreferenceScreen().addPreference(mCanboxEQPreference);
				// updateCanboxKeyValue(null);
			}
		} else {
			if (findPreference(KEY_CANBOX_EQ) != null) {
				getPreferenceScreen().removePreference(mCanboxEQPreference);
			}
		}
	}

	private String mCanboxValue;

	private String mCanboxType;

	private String mPreCanboxType;

	private String mKeyType = null;
	private String mChangeKey = null;
	private String mFrontDoor = null;
	private String mBackDoor = null;
	private String mAirCondition = null;
	private String mCarType = null;
	private String mCarType2 = null;
	private String mCarEQ = null;
	private String mCarOtherSettings = null;

	private void setCanboxKeySetting(String which) {
		mKeyType = which;
		// if (mCanboxValue != null) {
		// String ss[] = mCanboxValue.split(",");
		// if (ss.length > 1) {
		// ss[1] = which;
		// mCanboxValue = ss[0];
		// for (int i = 1; i < ss.length; ++i) {
		// mCanboxValue += "," + ss[i];
		// }
		// } else {
		// mCanboxValue += "," + which;
		// }
		// }
	}

	private void setCanboxEQSetting(String which) {
		mCarEQ = which;
		// if (mCanboxValue != null) {
		// String ss[] = mCanboxValue.split(",");
		// if (ss.length > 1) {
		// ss[1] = which;
		// mCanboxValue = ss[0];
		// for (int i = 1; i < ss.length; ++i) {
		// mCanboxValue += "," + ss[i];
		// }
		// } else {
		// mCanboxValue += "," + which;
		// }
		// }
	}

	private String getCanboxSetting() {
		mCanboxType = null;
		mKeyType = null;
		mChangeKey = null;
		mFrontDoor = null;
		mBackDoor = null;
		mAirCondition = null;

		mCanboxValue = MachineConfig.getProperty(MachineConfig.KEY_CAN_BOX);
		if (mCanboxValue != null) {
			String[] ss = mCanboxValue.split(",");
			mCanboxType = ss[0];
			for (int i = 1; i < ss.length; ++i) {
				if (ss[i]
						.startsWith(MachineConfig.KEY_SUB_CANBOX_AIR_CONDITION)) {
					mAirCondition = ss[i].substring(1);
				} else if (ss[i]
						.startsWith(MachineConfig.KEY_SUB_CANBOX_KEY_TYPE)) {
					mKeyType = ss[i].substring(1);
				} else if (ss[i]
						.startsWith(MachineConfig.KEY_SUB_CANBOX_CHANGE_KEY)) {
					mChangeKey = ss[i].substring(1);
				} else if (ss[i]
						.startsWith(MachineConfig.KEY_SUB_CANBOX_FRONT_DOOR)) {
					mFrontDoor = ss[i].substring(1);
				} else if (ss[i]
						.startsWith(MachineConfig.KEY_SUB_CANBOX_REAR_DOOR)) {
					mBackDoor = ss[i].substring(1);
				} else if (ss[i]
						.startsWith(MachineConfig.KEY_SUB_CANBOX_CAR_TYPE)) {
					mCarType = ss[i].substring(1);
				} else if (ss[i]
						.startsWith(MachineConfig.KEY_SUB_CANBOX_CAR_TYPE2)) {
					mCarType2 = ss[i].substring(1);
				} else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_EQ)) {
					mCarEQ = ss[i].substring(1);
				} else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_OTHER)) {
					mCarOtherSettings = ss[i].substring(1);
				}
			}
		} else {
		}
		return mCanboxValue;
	}

	private void setCanboxSetting(String which) {
		mPreCanboxType = mCanboxType;

		mCanboxType = which;

		if (mCanboxValue != null && mCanboxValue.startsWith(mCanboxType)) {
			getCanboxSetting();
		} else {
			mKeyType = null;
			mChangeKey = null;
			mFrontDoor = null;
			mBackDoor = null;
			mAirCondition = null;

			mCarType = null;
			mCarType2 = null;
			mCarEQ = null;
			mCarOtherSettings = null;
		}

		if (MachineConfig.VALUE_CANBOX_NISSAN2013.equals(mPreCanboxType) || MachineConfig.VALUE_CANBOX_NISSAN_RAISE.equals(mPreCanboxType))
		{
			hideNissian360Button();
		}
		// updateCanboxKey();
	}

	private boolean onManufacturerChanged(String canboxType, String manufacturer) {
		boolean result = false;
		String vender = null;

		if (manufacturer == null) {
			vender = getManufacturer(canboxType);
			// Log.d(TAG, "find canbox manufacturer=" + vender + " by " +
			// canboxTypte);
		} else {
			vender = manufacturer;
		}

		if (vender == null
				|| vender.isEmpty()
				|| vender.equals(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_ALL])) {
			updateManufacturerPreference(mManufacturerPreferenceValue[M_VALUE_ARRARY_INDX_ALL]);
		} else {
			updateManufacturerPreference(vender);
			result = true;
		}
		setCanboxPreferenceEntry(vender);

		return result;
	}

	private void updateCanboxSetting(String which) {
		mCanboxType = which;

		if ((null == which) || (which.isEmpty())) {
			mCanboxSettingPreference.setValue(MachineConfig.VALUE_CANBOX_NONE);
		} else {
			mCanboxSettingPreference.setValue(which);
		}

		Log.d("ddd", "" + mCanboxSettingPreference.getEntry());
		mCanboxSettingPreference.setSummary(mCanboxSettingPreference.getEntry());
		updateCanboxKey(mCanboxSettingPreference.getValue());
		updateCanboxEQ(mCanboxSettingPreference.getValue());
		ListPreference lp;

//		lp = (ListPreference) findPreference("canbox_key_change_ex");
//		if (mChangeKey != null) {
//			lp.setValue(mChangeKey);
//		} else {
//			lp.setValue("0");
//		}
//		lp.setSummary(lp.getEntry());
//
//		// lp = (ListPreference) findPreference("canbox_key_change");
//		// if (mChangeKey != null) {
//		// lp.setValue(mChangeKey);
//		// } else {
//		// lp.setValue("0");
//		// }
//
//		lp.setSummary(lp.getEntry());
		lp = (ListPreference) findPreference("canbox_front_door");
		if (mFrontDoor != null) {
			lp.setValue(mFrontDoor);
		} else {
			lp.setValue("0");
		}
		lp.setSummary(lp.getEntry());
		lp = (ListPreference) findPreference("canbox_rear_door");
		if (mBackDoor != null) {
			lp.setValue(mBackDoor);
		} else {
			lp.setValue("0");
		}
		lp.setSummary(lp.getEntry());

		lp = (ListPreference) findPreference("canbox_air");

		String[] entry1 = { getString(R.string.normal),getString(R.string.temp_change), getString(R.string.hide),getString(R.string.air_single) };
		String[] value1 = { "0", "1", "2", "3" };
		lp = (ListPreference) findPreference("canbox_air");

		if (MachineConfig.VALUE_CANBOX_HY.equalsIgnoreCase(mCanboxType))
		{
			entry1 = new String[] { getString(R.string.normal),getString(R.string.change), getString(R.string.hide),getString(R.string.air_single), "17°C ~ 32°C","15°C ~ 32°C", "15°C ~ 30°C" };
			value1 = new String[] { "0", "1", "2", "3", "4", "5", "6" };
		}
		else
		{
			try {
				int a = Integer.parseInt(mAirCondition);
				if (a > 2) {
					mAirCondition = "0";
				}
			} catch (Exception ignored) {
			}
		}

		lp.setEntries(entry1);
		lp.setEntryValues(value1);
		if (mAirCondition != null) {
			lp.setValue(mAirCondition);
		} else {
			lp.setValue("0");
		}
		lp.setSummary(lp.getEntry());
		lp.setOnPreferenceChangeListener(this);
		// lp = (ListPreference) findPreference("canbox_car_type");
		// if (mCarType != null) {
		// lp.setValue(mCarType);
		// } else {
		// lp.setValue("0");
		// }
		// lp.setSummary(lp.getEntry());

		updateCarType(which);
		updateCarType2(which);
		initEQVolume(which);
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = preference.getKey();
        Log.d(TAG,"onPreferenceChange key="+key);

		if (KEY_MANUFACTURER.equals(key)) {
			onManufacturerChanged(null, (String) newValue);
			updateCanboxSetting(null);
		} else if (KEY_CANBOX.equals(key)) {

			setCanboxSetting((String) newValue);
			updateCanboxSetting((String) newValue);
		} else if (KEY_CANBOX_KEY.equals(key)) {
			updateCanboxKeyValue((String) newValue);
			setCanboxKeySetting((String) newValue);

			updateCarType2(mCanboxType);
		} else if (KEY_CANBOX_EQ.equals(key)) {
			updateCanboxEQValue((String) newValue);
			setCanboxEQSetting((String) newValue);
		} 
//		else if ("canbox_key_change".equals(key)) {
//			((ListPreference) preference).setValue((String) newValue);
//			((ListPreference) preference)
//					.setSummary(((ListPreference) preference).getEntry());
//			mChangeKey = (String) newValue;
//		} 
		else if ("canbox_front_door".equals(key)) {
			((ListPreference) preference).setValue((String) newValue);
			((ListPreference) preference)
					.setSummary(((ListPreference) preference).getEntry());
			mFrontDoor = (String) newValue;
		} else if ("canbox_rear_door".equals(key)) {
			((ListPreference) preference).setValue((String) newValue);
			((ListPreference) preference)
					.setSummary(((ListPreference) preference).getEntry());
			mBackDoor = (String) newValue;
		} else if ("canbox_air".equals(key)) {
			((ListPreference) preference).setValue((String) newValue);
			((ListPreference) preference)
					.setSummary(((ListPreference) preference).getEntry());
			mAirCondition = (String) newValue;
		} else if ("canbox_air".equals(key)) {
			((ListPreference) preference).setValue((String) newValue);
			((ListPreference) preference)
					.setSummary(((ListPreference) preference).getEntry());
			mAirCondition = (String) newValue;
		} else if ("canbox_car_type".equals(key)) {
			((ListPreference) preference).setValue((String) newValue);
			((ListPreference) preference)
					.setSummary(((ListPreference) preference).getEntry());
			mCarType = (String) newValue;
			updateCarType2(mCanboxType);
		} else if ("canbox_car_type2".equals(key)) {
			((ListPreference) preference).setValue((String) newValue);
			((ListPreference) preference)
					.setSummary(((ListPreference) preference).getEntry());
			mCarType2 = (String) newValue;
			initEQVolume(getCanboxType());

		} else if ("canbox_other_settings".equals(key)) {
			Set<String> s = (Set<String>) newValue;
			((MultiSelectListPreference) preference).setValues(s);

			Iterator<String> it = s.iterator();
			int otherSettings = 0;
			while (it.hasNext()) {
				String str = it.next();
				try {
					int i = Integer.valueOf(str);
					if (i < 32) {
						otherSettings |= (0x1 << i);
					}
				} catch (Exception e) {

				}
			}

			mCarOtherSettings = otherSettings + "";
		}  else if ("canbox_key_change_ex".equals(key)) {
			Set<String> s = (Set<String>) newValue;
			((MultiSelectListPreference) preference).setValues(s);

			Iterator<String> it = s.iterator();
			int otherSettings = 0;
			while (it.hasNext()) {
				String str = it.next();
				try {
					int i = Integer.parseInt(str);
					if (i < 32) {
						otherSettings |= (0x1 << i);
					}
				} catch (Exception ignored) {
				}
			}
			mChangeKey = otherSettings + "";
		}
		return false;
	}

	public boolean onPreferenceClick(Preference arg0) {
		/// if (arg0.getKey().equals(KEY_RESET_SYSTEM)) {
		///
		/// }
		String key = arg0.getKey();
		if ("canbox_eq_volume".equals(key)) {
			showVolumeDialog();
		}
		return false;
	}

	private static void updateHideAppConboxVersion1(String value,
			String msCarType) {

		boolean hideSync = true;
		boolean hideConboxSetting = true;
		boolean hideCanboxCarInfo = true;
		boolean hideCanboxAirControl = true;
		boolean hideTpms = true;
		boolean hideCarCD = true;
		boolean hideAuxin = false;
		boolean hideFrontCamera = false;

		int mCarType = 0;
		try {
			mCarType = Integer.parseInt(msCarType);
		} catch (Exception e) {

		}
		if (null != value) {
			if (value.equals(MachineConfig.VALUE_CANBOX_FORD_SIMPLE)
					|| value.equals(MachineConfig.VALUE_CANBOX_FORD_RAISE)
					|| value.equals(MachineConfig.VALUE_CANBOX_FORD_EXPLORER_SIMPLE))
			{
				if (value.equals(MachineConfig.VALUE_CANBOX_FORD_SIMPLE) || value.equals(MachineConfig.VALUE_CANBOX_FORD_RAISE))
				{
					hideCanboxCarInfo = false;
				}
				hideConboxSetting = false;
				if (1 != mCarType) {
					hideSync = false;
					if (mCarType == 2 || mCarType == 3) {
						hideAuxin = true;
					}
				}
			} else if (value.equals(MachineConfig.VALUE_CANBOX_GM_SIMPLE)
					|| value.equals(MachineConfig.VALUE_CANBOX_HONDA_DA_SIMPLE)
					|| value.equals(MachineConfig.VALUE_CANBOX_HONDA_RAISE)
					|| value.equals(MachineConfig.VALUE_CANBOX_PSA_BAGOO)
					|| value.equals(MachineConfig.VALUE_CANBOX_BMW_E90X1_UNION)
					|| value.equals(MachineConfig.VALUE_CANBOX_PSA)
					|| value.equals(MachineConfig.VALUE_CANBOX_KADJAR_RAISE)
					|| value.equals(MachineConfig.VALUE_CANBOX_TOYOTA)
					|| value.equals(MachineConfig.VALUE_CANBOX_TOYOTA_RAISE)
					|| value.equals(MachineConfig.VALUE_CANBOX_TOYOTA_BINARYTEK)
					|| value.equals(MachineConfig.VALUE_CANBOX_ACCORD_BINARYTEK)
					|| value.equals(MachineConfig.VALUE_CANBOX_ACCORD2013)
					|| value.equals(MachineConfig.VALUE_CANBOX_PETGEO_RAISE)
					|| value.equals(MachineConfig.VALUE_CANBOX_PETGEO_SCREEN_RAISE)
					|| value.equals(MachineConfig.VALUE_CANBOX_LANDROVER_HAOZHENG)
					|| value.equals(MachineConfig.VALUE_CANBOX_OUSHANG_RAISE)
					|| value.equals(MachineConfig.VALUE_CANBOX_FIAT_EGEA_RAISE)
			        || value.equals(MachineConfig.VALUE_CANBOX_MAZDA_RAISE))
			{
				hideConboxSetting = false;
				hideCanboxCarInfo = false;
			} else if (value.equals(MachineConfig.VALUE_CANBOX_VW_GOLF_SIMPLE)
					|| value.equals(MachineConfig.VALUE_CANBOX_TOUAREG_HIWORLD)) {
				hideConboxSetting = false;
				hideCanboxCarInfo = false;
				hideCanboxAirControl = false;
			} else if (value.equals(MachineConfig.VALUE_CANBOX_VW)
					|| value.equals(MachineConfig.VALUE_CANBOX_PEUGEOT206)
					|| value.equals(MachineConfig.VALUE_CANBOX_PORSCHE_UNION)
					|| value.equals(MachineConfig.VALUE_CANBOX_OBD_BINARUI)
					|| value.equals(MachineConfig.VALUE_CANBOX_PSA206_SIMPLE)
					|| value.equals(MachineConfig.VALUE_CANBOX_MONDEO_DAOJUN)
					|| value.equals(MachineConfig.VALUE_CANBOX_HY_RAISE)
					|| value.equals(MachineConfig.VALUE_CANBOX_NISSAN_RAISE)) {
				hideCanboxCarInfo = false;
			} else if (value.equals(MachineConfig.VALUE_CANBOX_OPEL)
					|| value.equals(MachineConfig.VALUE_CANBOX_RAM_FIAT)
					|| value.equals(MachineConfig.VALUE_CANBOX_MITSUBISHI_OUTLANDER_SIMPLE)
					|| value.equals(MachineConfig.VALUE_CANBOX_CHERY_OD)
					|| value.equals(MachineConfig.VALUE_CANBOX_NISSAN2013)
					|| value.equals(MachineConfig.VALUE_CANBOX_HAFER_H2)
					|| value.equals(MachineConfig.VALUE_CANBOX_SMART_HAOZHENG)
					|| value.equals(MachineConfig.VALUE_CANBOX_MAZDA_CX5_SIMPLE)
					|| value.equals(MachineConfig.VALUE_CANBOX_SUBARU_SIMPLE)) {
				hideConboxSetting = false;
			} else if (value.equals(MachineConfig.VALUE_CANBOX_JEEP_SIMPLE)) {

				if (mCarType == 0 || mCarType == 1 || mCarType == 4
						|| mCarType == 6 || mCarType == 7) {
					hideCanboxAirControl = false;
				}
				hideCanboxCarInfo = false;
				hideConboxSetting = false;
				hideCarCD = false;
			} else if (value
					.equals(MachineConfig.VALUE_CANBOX_MAZDA3_BINARYTEK)) {
				hideCanboxCarInfo = false;
				hideConboxSetting = false;
				hideCarCD = false;
			} else if (value.equals(MachineConfig.VALUE_CANBOX_MAZDA_XINBAS)) {
				hideCanboxCarInfo = false;
				hideConboxSetting = false;
				if (mCarType != 1) {
					hideAuxin = true;
					hideCarCD = false;
				}
			} else if (value.equals(MachineConfig.VALUE_CANBOX_MAZDA3_SIMPLE)) {
				if (mCarType != 1) {
					hideAuxin = true;
					hideCarCD = false;
				}
				hideConboxSetting = false;
				hideCanboxCarInfo = false;
			} else if (value.equals(MachineConfig.VALUE_CANBOX_HAFER_H2)) {
				hideFrontCamera = true;
			} else if (value.equals(MachineConfig.VALUE_CANBOX_RX330_HAOZHENG)) {
				hideCanboxCarInfo = false;
				hideCarCD = false;
				hideAuxin = true;
				hideCanboxAirControl = false;
			} else if (value.equals(MachineConfig.VALUE_CANBOX_X30_RAISE)) {
				if (mCarType == 1 || mCarType == 2) {
					hideCanboxAirControl = false;
				}
			} else if (value.equals(MachineConfig.VALUE_CANBOX_JEEP_XINBAS)) {
				hideConboxSetting = false;
				hideCarCD = false;
				hideCanboxAirControl = false;
			} else if (value.equals(MachineConfig.VALUE_CANBOX_GM_OD)) {
				hideAuxin = true;
				hideCarCD = false;
				hideCanboxAirControl = false;
			} else if (value.equals(MachineConfig.VALUE_CANBOX_VW_MQB_RAISE)) {
				hideConboxSetting = false;
				hideCanboxCarInfo = false;
				hideCanboxAirControl = false;
				hideTpms = false;
			}
		}

		String appShow = "";
		if (!hideSync) {
			// mSetHideApp.add("com.focussync.MainActivity");
			appShow += AppConfig.HIDE_CANBOX_SYNC + ",";
		}
		if (!hideConboxSetting) {
			// mSetHideApp.add("com.canboxsetting.MainActivity");
			appShow += AppConfig.HIDE_CANBOX_SET + ",";
		}
		if (!hideCanboxCarInfo) {
			// mSetHideApp.add("com.canboxsetting.CarInfoActivity");
			appShow += AppConfig.HIDE_CANBOX_INFO + ",";
		}
		if (!hideCanboxAirControl) {
			// mSetHideApp.add("com.canboxsetting.CanAirControlActivity");
			appShow += AppConfig.HIDE_CANBOX_AC + ",";
		}
		if (!hideTpms) {
			// mSetHideApp.add("com.canboxsetting.CanAirControlActivity");
			appShow += AppConfig.HIDE_CANBOX_TPMS + ",";
		}

		// if (!(MachineConfig.VALUE_CANBOX_JEEP_SIMPLE.equals(value)
		// || MachineConfig.VALUE_CANBOX_MAZDA3_BINARYTEK.equals(value)
		// || MachineConfig.VALUE_CANBOX_MAZDA_XINBAS.equals(value))) {
		if (!hideCarCD) {
			// mSetHideApp.add("com.canboxsetting.JeepCarCDPlayerActivity");
			appShow += AppConfig.HIDE_CANBOX_CD + ",";
		}

		if (hideAuxin) {
			appShow += AppConfig.HIDE_APP_AUX + ",";
		}
		if (hideFrontCamera) {
			appShow += AppConfig.HIDE_APP_FRONT_CMAERA + ",";
		}

		MachineConfig.setProperty(MachineConfig.KEY_CAN_BOX_SHOW_APP, appShow);
	}

	private void updateMachineConfig() {

		updateHideAppConboxVersion1(mCanboxType, mCarType);

		if (mCanboxType == null	|| MachineConfig.VALUE_CANBOX_NONE.equals(mCanboxType)) {
			MachineConfig.setProperty(MachineConfig.KEY_CAN_BOX, null);

			Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
			it.putExtra(MyCmd.EXTRA_COMMON_CMD, MachineConfig.KEY_CAN_BOX);
			sendBroadcast(it);
		}
		else
		{
			if (mCanboxType != null) {
				String newCanboxValue = mCanboxType;

				if (mKeyType != null) {
					newCanboxValue += "," + MachineConfig.KEY_SUB_CANBOX_KEY_TYPE + mKeyType;
				}
				if (mAirCondition != null) {
					newCanboxValue += "," + MachineConfig.KEY_SUB_CANBOX_AIR_CONDITION + mAirCondition;
				}
				if (mChangeKey != null) {
					newCanboxValue += ","
							+ MachineConfig.KEY_SUB_CANBOX_CHANGE_KEY
							+ mChangeKey;
				}
				if (mFrontDoor != null) {
					newCanboxValue += ","
							+ MachineConfig.KEY_SUB_CANBOX_FRONT_DOOR
							+ mFrontDoor;
				}
				if (mBackDoor != null) {
					newCanboxValue += ","
							+ MachineConfig.KEY_SUB_CANBOX_REAR_DOOR
							+ mBackDoor;
				}
				if (mCarType != null) {
					newCanboxValue += ","
							+ MachineConfig.KEY_SUB_CANBOX_CAR_TYPE + mCarType;
				}

				if (mCarType2 != null) {
					newCanboxValue += ","
							+ MachineConfig.KEY_SUB_CANBOX_CAR_TYPE2
							+ mCarType2;
				}

				if (mCarEQ != null) {
					newCanboxValue += "," + MachineConfig.KEY_SUB_CANBOX_EQ
							+ mCarEQ;
				}
				if (mCarOtherSettings != null) {
					newCanboxValue += "," + MachineConfig.KEY_SUB_CANBOX_OTHER + mCarOtherSettings;
				}
				newCanboxValue += ",v2";
				if (!newCanboxValue.equals(mCanboxValue))
				{
					MachineConfig.setProperty(MachineConfig.KEY_CAN_BOX,newCanboxValue);
					Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
					it.putExtra(MyCmd.EXTRA_COMMON_CMD,	MachineConfig.KEY_CAN_BOX);
					sendBroadcast(it);
				}
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		if (id == R.id.btn_ok) {
			updateMachineConfig();
			finish();
		} else if (id == R.id.btn_cancel) {
			finish();
		} else if (id == R.id.btn_advanced) {
			UtilSystem.doRunActivity(this, "com.my.factory.intent.action.CanboxSettings2");
		}
	}

	private void hideNissian360Button() {
		SystemConfig.setIntProperty(this,
				SystemConfig.KEY_NISSIAN_360_SYSTEM_BUTTON, 0);

		Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
		it.putExtra(MyCmd.EXTRA_COMMON_CMD,
				SystemConfig.KEY_NISSIAN_360_SYSTEM_BUTTON);
		it.putExtra(MyCmd.EXTRA_COMMON_DATA, false);
		sendBroadcast(it);
	}

	TextView mTextVolume;
	SeekBar mLevel;
	CanboxSettings mThis;
	int max = 0;
	int volume = -1;

	private void showVolumeDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setTitle(R.string.car_amplifier_volume);
		View view = getLayoutInflater().inflate(R.layout.volume_dialog, null);
		alertDialog.setView(view);
		alertDialog.show();

		// alertDialog.setContentView(R.layout.volume_dialog);
		mLevel = (SeekBar) alertDialog.findViewById(R.id.level);

		mTextVolume = (TextView) alertDialog.findViewById(R.id.volume);

		if (mLevel != null) {

			mLevel.setMax(max);

			mTextVolume.setText("" + volume);
			mLevel.setProgress(volume);
			mLevel.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if (fromUser) {
						if (mTextVolume != null) {
							mTextVolume.setText("" + progress);
							sendCanboxVolume(mWillSetCan, progress);
							volume = progress;
							MachineConfig.setIntProperty(SystemConfig.CANBOX_EQ_VOLUME, progress);

						}
					}
				}
			});
		}
	}

	private void sendCanboxVolume(String can, int progress) {

		byte[] buf = null;// new byte[] { (byte) 0x84, 0x2, 0x07, (byte)progress
							// };

		if (MachineConfig.VALUE_CANBOX_TOYOTA.equalsIgnoreCase(can)	|| MachineConfig.VALUE_CANBOX_TOYOTA_RAISE.equalsIgnoreCase(can))
		{
			buf = new byte[] { (byte) 0x84, 0x2, 0x07, (byte) progress };
		} else if (MachineConfig.VALUE_CANBOX_HY.equalsIgnoreCase(can)) {
			buf = new byte[] { (byte) 0xc4, 0x1, (byte) (0x00 | progress) };
		} else if (MachineConfig.VALUE_CANBOX_HY_RAISE.equalsIgnoreCase(can)) {
			buf = new byte[] { (byte) 0x05, 0x5, (byte) (0x00 | progress) };
		}

		BroadcastUtil.sendCanboxInfo(mThis, buf);
	}
}
