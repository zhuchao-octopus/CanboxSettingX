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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.android.canboxsetting.R;
import com.canboxsetting.info.Nission2013InfoSimpleFragment;
import com.canboxsetting.keyboard.PSABagooKeyboardFragment;
import com.canboxsetting.set.Accord2013SettingsSimpleFragment;
import com.canboxsetting.set.AccordSettingsBinaryTekFragment;
import com.canboxsetting.set.BMWE90X1UnionSettingsFragment;
import com.canboxsetting.set.CheryODSettingFragment;
import com.canboxsetting.set.FiatEGEARaiseSettingFragment;
import com.canboxsetting.set.FocusSettingsFragment;
import com.canboxsetting.set.FordExplorerSimpleSettingsFragment;
import com.canboxsetting.set.GMSettingsSimpleFragment;
import com.canboxsetting.set.Golf7SettingsSimpleFragment;
import com.canboxsetting.set.HaferH2Setting;
import com.canboxsetting.set.HondaSettingsSimpleFragment;
import com.canboxsetting.set.JeepSettingsSimpleFragment;
import com.canboxsetting.set.JeepSettingsXinbasFragment;
import com.canboxsetting.set.KadjarRaiseSettingFragment;
import com.canboxsetting.set.LandRoverHaoZhengSettingsFragment;
import com.canboxsetting.set.Mazda3BinarytekSettingsFragment;
import com.canboxsetting.set.Mazda3SettingsSimpleFragment;
import com.canboxsetting.set.Mazda3XinbasiSettingsFragment;
import com.canboxsetting.set.MazdaCX5SettingsSimpleFragment;
import com.canboxsetting.set.MazdaRaiseSettingsFragment;
import com.canboxsetting.set.MitsubishiSettingsSimpleFragment;
import com.canboxsetting.set.OpelSettingsSimpleFragment;
import com.canboxsetting.set.OuShangSettingsRaiseFragment;
import com.canboxsetting.set.PSASettingsBagooFragment;
import com.canboxsetting.set.PSASettingsRaiseFragment;
import com.canboxsetting.set.PSASettingsSimpleFragment;
import com.canboxsetting.set.RAMFiatSettingSimpleFragment;
import com.canboxsetting.set.SmartHaoZhengSettingsFragment;
import com.canboxsetting.set.SubaruSimpleSettingFragment;
import com.canboxsetting.set.TouaregHiworldSettingFragment;
import com.canboxsetting.set.ToyotaSettingsSimpleFragment;
import com.canboxsetting.set.VWMQBSettingsRaiseFragment;
import com.car.ui.GlobalDef;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;

/**
 *type1: VWMQBSettingsRaiseFragment
 *DataType 0x40 数据类型
Length 0x05 数据长度
Data0 指令
参照下表
Data 1 参数1
Data 2 参数2
Data3 参数3
Data4 参数4
 */
public class MainActivity extends Activity {
	private static final String TAG = "CanboxSetting";
	private FragmentManager mFragmentManager;
	private Fragment mSetting;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.main);
		mFragmentManager = getFragmentManager();

		int mCarType = 0;

		String value = null;
		String mCanboxType = MachineConfig.getPropertyOnce(MachineConfig.KEY_CAN_BOX);

		int mProVersion = 0;
		String mProIndex = null;
		if (mCanboxType != null) {
			String[] ss = mCanboxType.split(",");
			value = ss[0];
			try {
				for (int i = 1; i < ss.length; ++i) {
					if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_VERSION)) {
						mProVersion = Integer.parseInt(ss[i].substring(1));
					} else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX)) {
						mProIndex = ss[i].substring(1);
						try {
							GlobalDef.setProId(Integer.parseInt(mProIndex));
						} catch (Exception ignored) {
						}

					} else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_CAR_TYPE)) {
						try {
							mCarType = Integer.parseInt(ss[i].substring(1));
						} catch (Exception ignored) {

						}
					} else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_ID)) {
						String mProId = ss[i].substring(1);
						if (mProId != null && mProId.length() >= 4) {
							int start = 0;
							int end = 0;
							if (mProId.charAt(1) == '0'
									&& mProId.charAt(2) != 0) {
								end = 1;
							} else if (mProId.charAt(2) == '0') {
								end = 2;
							}

							start = end + 1;
							int mModelId = -1;
							if (mProId.contains("-")) {
								String[] sss = mProId.substring(start).split("-");
								mModelId = Integer.valueOf(sss[1]);
							} else {
								if ((mProId.length() - start) == 2) {
									mModelId = Integer.valueOf(mProId.substring(start + 1, start + 2));
								} else if ((mProId.length() - start) == 4) {
									mModelId = Integer.valueOf(mProId.substring(start + 2, start + 4));
								} else if ((mProId.length() - start) == 3) {
									mModelId = Integer.valueOf(mProId.substring(start + 2, start + 3));
								}
							}

							GlobalDef.setModelId(mModelId);
							
						}
					}

				}
			} catch (Exception ignored) {
			}
		}

		if (mProVersion >= 3 && mProIndex != null) {
			Class<?> c = FragmentPro.getFragmentSetByID(mProIndex);
			if (c != null) {
				try {
					if (mProIndex.equals("10")){ //to do better future
						if (mCarType == 2) {
							mSetting = new PSABagooKeyboardFragment();
						} else {
							mSetting = (Fragment) c.newInstance();
						}
					} else {
						mSetting = (Fragment) c.newInstance();
					}
				} catch (Exception ignored) {
				}

			}
			if (mSetting == null) {
				finish();
				return;
			}
		} else {

			if (value != null) {
				if (value.equals(MachineConfig.VALUE_CANBOX_GM_SIMPLE)) {
					mSetting = new GMSettingsSimpleFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_GM_RAISE)) {
					mSetting = new GMSettingsSimpleFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_FORD_SIMPLE)
						|| value.equals(MachineConfig.VALUE_CANBOX_FORD_RAISE)) {
					mSetting = new FocusSettingsFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_PSA_BAGOO)) {
					if (mCarType == 2) {
						mSetting = new PSABagooKeyboardFragment();
					} else {
						mSetting = new PSASettingsBagooFragment();
					}
				} else if (value.equals(MachineConfig.VALUE_CANBOX_OPEL)) {
					mSetting = new OpelSettingsSimpleFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_VW_GOLF_SIMPLE)) {
					mSetting = new Golf7SettingsSimpleFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_VW_MQB_RAISE)) {
					mSetting = new VWMQBSettingsRaiseFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_BMW_E90X1_UNION)) {
					mSetting = new BMWE90X1UnionSettingsFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_RAM_FIAT)) {
					mSetting = new RAMFiatSettingSimpleFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_KADJAR_RAISE)) {
					mSetting = new KadjarRaiseSettingFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_PSA)) {
					mSetting = new PSASettingsSimpleFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_TOYOTA)
						|| value.equals(MachineConfig.VALUE_CANBOX_TOYOTA_BINARYTEK)
						|| value.equals(MachineConfig.VALUE_CANBOX_TOYOTA_RAISE)) {
					mSetting = new ToyotaSettingsSimpleFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_HONDA_DA_SIMPLE)
						|| value.equals(MachineConfig.VALUE_CANBOX_HONDA_RAISE)) {
					mSetting = new HondaSettingsSimpleFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_JEEP_SIMPLE)) {
					mSetting = new JeepSettingsSimpleFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_ACCORD2013)) {
					mSetting = new Accord2013SettingsSimpleFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_MAZDA3_BINARYTEK)) {
					mSetting = new Mazda3BinarytekSettingsFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_MAZDA_XINBAS)) {
					mSetting = new Mazda3XinbasiSettingsFragment();
//					((Mazda3XinbasiSettingsFragment) mSetting)
//							.setType2(mCarType2);
				} else if (value.equals(MachineConfig.VALUE_CANBOX_MAZDA_RAISE)) {
					mSetting = new MazdaRaiseSettingsFragment();
//					((MazdaRaiseSettingsFragment) mSetting).setType2(mCarType2);
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_TOUAREG_HIWORLD)) {
					mSetting = new TouaregHiworldSettingFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_PETGEO_RAISE)
						|| value.equals(MachineConfig.VALUE_CANBOX_PETGEO_SCREEN_RAISE)) {
					mSetting = new PSASettingsRaiseFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_FORD_EXPLORER_SIMPLE)) {
					mSetting = new FordExplorerSimpleSettingsFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_MITSUBISHI_OUTLANDER_SIMPLE)) {
					mSetting = new MitsubishiSettingsSimpleFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_ACCORD_BINARYTEK)) {
					mSetting = new AccordSettingsBinaryTekFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_NISSAN2013)) {
					mSetting = new Nission2013InfoSimpleFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_CHERY_OD)) {
					mSetting = new CheryODSettingFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_HAFER_H2)) {
					mSetting = new HaferH2Setting();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_MAZDA3_SIMPLE)) {
					mSetting = new Mazda3SettingsSimpleFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_SMART_HAOZHENG)) {
					mSetting = new SmartHaoZhengSettingsFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_LANDROVER_HAOZHENG)) {
					mSetting = new LandRoverHaoZhengSettingsFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_MAZDA_CX5_SIMPLE)) {
					mSetting = new MazdaCX5SettingsSimpleFragment();
				} else if (value.equals(MachineConfig.VALUE_CANBOX_JEEP_XINBAS)) {
					mSetting = new JeepSettingsXinbasFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_OUSHANG_RAISE)) {
					mSetting = new OuShangSettingsRaiseFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_FIAT_EGEA_RAISE)) {
					mSetting = new FiatEGEARaiseSettingFragment();
				} else if (value
						.equals(MachineConfig.VALUE_CANBOX_SUBARU_SIMPLE)) {
					mSetting = new SubaruSimpleSettingFragment();
				}
			}

		}
		// if(){
		//
		// } else {
		//
		// }
		if (mSetting == null) {
			mSetting = new FocusSettingsFragment();
		}
		updateIntent(getIntent());
		replaceFragment(R.id.main, mSetting, false);
	}

	private void updateIntent(Intent intent) {
		int i = intent.getIntExtra(MyCmd.EXTRA_COMMON_CMD, 0);
		if (mSetting instanceof Golf7SettingsSimpleFragment) {
			Golf7SettingsSimpleFragment new_name = (Golf7SettingsSimpleFragment) mSetting;
			new_name.setType(i);

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		updateIntent(intent);
	}

	private void replaceFragment(int layoutId, Fragment fragment,boolean isAddStack) {
		if (fragment != null) {
			FragmentTransaction transation = mFragmentManager.beginTransaction();
			transation.replace(layoutId, fragment);
			if (isAddStack) {
				transation.addToBackStack(null);
			}
			transation.commit();
		}
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
