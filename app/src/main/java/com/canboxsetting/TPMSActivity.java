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

package com.canboxsetting;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import com.canboxsetting.tpms.MazdTpmsInfoaRaiseFragment;
import com.canboxsetting.tpms.VWMQBTpmsInfoRaiseFragment;
import com.canboxsetting.tpms.VWMQBTpmsInfoSimpleFragment;
import com.canboxsetting.tpms.ZhongXingFragment;
import com.car.ui.GlobalDef;
import com.common.util.AppConfig;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * This activity plays a video from a specified URI.
 */
public class TPMSActivity extends Activity {
	private static final String TAG = "CanboxSetting";

	private FragmentManager mFragmentManager;

	private Fragment mSetting;

//	public static String mCanboxType = null;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.main);
		mFragmentManager = getFragmentManager();

		String mCanboxType = MachineConfig.getPropertyOnce(MachineConfig.KEY_CAN_BOX);
		int mProVersion = 0;
		String mProIndex = null;
		if (mCanboxType != null) {
			String[] ss = mCanboxType.split(",");
			mCanboxType = ss[0];
			try {
				for (int i = 1; i < ss.length; ++i) {
					if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_VERSION)) {
						mProVersion = Integer.valueOf(ss[i].substring(1));
					} else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX)) {
						mProIndex = ss[i].substring(1);
						GlobalDef.setProId(Integer.valueOf(mProIndex));
					}
				}
			} catch (Exception e) {

			}
		}

		if (mProVersion >= 3 && mProIndex != null) {
			Class<?> c = FragmentPro.getFragmentTpmsByID(mProIndex);
			if (c != null) {
				try {
					mSetting = (Fragment) c.newInstance();
					Bundle b = new Bundle();
					b.putString(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX, mProIndex);
					mSetting.setArguments(b);
				} catch (Exception e) {

				}

			}
			if (mSetting == null) {
				finish();
				return;
			}
		} else {
			// mCanboxType =
			// AppConfig.getCanboxSetting();//MachineConfig.getPropertyOnce(MachineConfig.KEY_CAN_BOX);
			if (mCanboxType != null) {
				if (mCanboxType.equals(MachineConfig.VALUE_CANBOX_VW_MQB_RAISE)) {
					mSetting = new VWMQBTpmsInfoRaiseFragment();
				}
				else if (mCanboxType.equals(MachineConfig.VALUE_CANBOX_VW_GOLF_SIMPLE)) {
					mSetting = new VWMQBTpmsInfoSimpleFragment();
				}
				
				else if (mCanboxType.equals(MachineConfig.VALUE_CANBOX_MAZDA_RAISE)) {
					mSetting = new MazdTpmsInfoaRaiseFragment();
				}
				
				else if (mCanboxType.equals(MachineConfig.VALUE_CANBOX_ZHONGXING_OD)) {
					mSetting = new ZhongXingFragment();
				}
			}

			if (mSetting == null) {
				mSetting = new VWMQBTpmsInfoRaiseFragment();
			}
		}

		replaceFragment(R.id.main, mSetting, false);

		mCmd = 0;
		upateIntent(getIntent());
	}

	private void replaceFragment(int layoutId, Fragment fragment,
			boolean isAddStack) {
		if (fragment != null) {
			FragmentTransaction transation = mFragmentManager
					.beginTransaction();
			transation.replace(layoutId, fragment);
			if (isAddStack) {
				transation.addToBackStack(null);
			}
			transation.commit();
		}
	}

	public static int mCmd = 0;
	public static byte[] mBuf;

	private void upateIntent(Intent it) {
		if (it != null) {
			mBuf = it.getByteArrayExtra(MyCmd.EXTRA_COMMON_CMD);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		upateIntent(intent);
	}

	@Override
	protected void onPause() {

		super.onPause();
		// finish();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		// System.exit(0);
	}
}
