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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.canboxsetting.R;
import com.common.util.AppConfig;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Util;
import com.my.factory.JsonParser.CanBaud;
import com.my.factory.JsonParser.CanSetting;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This activity plays a video from a specified URI.
 */
public class CanboxSettings2 extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener, OnPreferenceClickListener,
        OnClickListener {

    private static final String TAG = "CanboxSettings2";

    private static final String KEY_MANUFACTURER = "manufacturer";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_MODEL = "model";
    private static final String KEY_CONFIGURATION = "configuration";

    private ListPreference mManufacturerPreference;
    private ListPreference mCategoryPreference;
    private ListPreference mModelPreference;
    private ListPreference mConfigurationPreference;

    private String[] mManufacturer;
    private String[] mManufacturerValue;

    private String[] mCategory;
    private String[] mCategoryValue;

    private String[] mModel;
    private String[] mModelValue;

    private String[] mConfiguration;
    private String[] mConfigurationValue;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.canbox_setting2);
        addPreferencesFromResource(R.xml.canbox_settings2);

    }

    protected void onResume() {
        super.onResume();
        initData();
        initCanMachineConfig();
        updateAdvanced();
        initView();
        updateView();
    }

    ;

    XlmParser mXlmParser = new XlmParser();
    JsonParser mJsonParser;

    private void initData() {
        mXlmParser.parser(this);
        int c;
        c = mXlmParser.getManufacturerNum();
        mManufacturer = new String[c];
        mManufacturerValue = new String[c];
        mXlmParser.getManufacturer(mManufacturer, mManufacturerValue);

        c = mXlmParser.getCarConfigNum();
        mConfiguration = new String[c];
        mConfigurationValue = new String[c];
        mXlmParser.getCarConfig(mConfiguration, mConfigurationValue);

        mJsonParser = new JsonParser();
        mJsonParser.parser(this);
    }

    private void initView() {
        mManufacturerPreference = (ListPreference) findPreference(KEY_MANUFACTURER);
        mManufacturerPreference.setOnPreferenceChangeListener(this);
        mCategoryPreference = (ListPreference) findPreference(KEY_CATEGORY);
        mCategoryPreference.setOnPreferenceChangeListener(this);
        mModelPreference = (ListPreference) findPreference(KEY_MODEL);
        mModelPreference.setOnPreferenceChangeListener(this);
        mConfigurationPreference = (ListPreference) findPreference(KEY_CONFIGURATION);
        mConfigurationPreference.setOnPreferenceChangeListener(this);
        // mConfigurationPreference.setOnPreferenceClickListener(this);

        mCategoryPreference.setSummary("%s");
        mModelPreference.setSummary("%s");
        // mConfigurationPreference.setSummary("%s");

        mManufacturerPreference.setEntries(mManufacturer);
        mManufacturerPreference.setEntryValues(mManufacturerValue);

        mConfigurationPreference.setEntries(mConfiguration);
        mConfigurationPreference.setEntryValues(mConfigurationValue);

        //findViewById(R.id.external_boxes).setOnClickListener(this);
        initExternalBoxes();
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);

        if (mManufacturerName == null || mCategoryName == null || mModelName == null) {
            mManufacturerName = mXlmParser.getTranslation("通用机");
            mCategoryName = mManufacturerName;
            mModelName = mManufacturerName;
        }

        updateCarSettings();
        updateCarType2();
        if (mManufacturerName != null && mCategoryName != null && mModelName != null) {
            int c;
            updateAdvancedVisible(mManufacturerName);
            mManufacturerPreference.setValue(mManaId);
            mManufacturerPreference.setSummary(mManufacturerName);

            c = mXlmParser.getCategoryNum(mManufacturerName);
            mCategory = new String[c];
            mXlmParser.getCategory(mManufacturerName, mCategory);

            c = mXlmParser.getModelNum(mManufacturerName, mCategoryName);

            mModel = new String[c];
            mXlmParser.getModel(mManufacturerName, mCategoryName, mModel);

            if (c == 1) {
                if (mModel[0].equals("all")) {
                    c = mXlmParser.getAllCategoryModelNum(mCategoryName);
                    mModel = new String[c];
                    mXlmParser.getAllCategoryModel(mCategoryName, mModel);
                }
            }

            mCategoryPreference.setEntries(mCategory);
            mCategoryPreference.setEntryValues(mCategory);
            mCategoryPreference.setValue(mCategoryName);

            mModelPreference.setEntries(mModel);
            mModelPreference.setEntryValues(mModel);
            mModelPreference.setValue(mModelName);

            if (mCarConfig != null) {
                updateConfig(mCarConfig);
            } else {
                updateConfig(mConfigurationValue[mConfigurationValue.length - 1]);
            }

        }
    }

    private String mManufacturerName;
    private String mCategoryName;
    private String mModelName;

    // private String mManufacturerName;

    private void updateView() {

    }

    private void updateManufacturer(String name) {
        int c;
        mManufacturerPreference.setValue(name);
        name = mManufacturerPreference.getEntry().toString();
        c = mXlmParser.getCategoryNum(name);
        mCategory = new String[c];
        mXlmParser.getCategory(name, mCategory);

        mManufacturerPreference.setSummary(name);
        mManufacturerName = name;

        updateAdvancedVisible(name);
        updateCategory(mCategory[0]);
    }

    private void updateCategory(String name) {
        int c;
        c = mXlmParser.getModelNum(mManufacturerName, name);
        mModel = new String[c];
        mXlmParser.getModel(mManufacturerName, name, mModel);

        if (c == 1) {
            if (mModel[0].equals("all")) {
                c = mXlmParser.getAllCategoryModelNum(name);
                mModel = new String[c];
                mXlmParser.getAllCategoryModel(name, mModel);
            }
        }
        mCategoryPreference.setEntries(mCategory);
        mCategoryPreference.setEntryValues(mCategory);
        mCategoryPreference.setValue(name);

        mCategoryName = name;
        updateModel(mModel[0]);

        updateCarType2();
    }

    private void updateModel(String name) {

        mModelPreference.setEntries(mModel);
        mModelPreference.setEntryValues(mModel);
        mModelPreference.setValue(name);

        mModelName = name;
        updateConfig(mConfigurationValue[mConfigurationValue.length - 1]);
        clearAdvanced();
    }

    private void updateConfig(String name) {
        mConfigurationPreference.setValue(name);
        mConfigurationPreference
                .setSummary(mConfigurationPreference.getEntry());
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();

        if (KEY_MANUFACTURER.equals(key)) {
            updateManufacturer(((String) newValue));
        } else if (KEY_CATEGORY.equals(key)) {
            updateCategory(((String) newValue));
        } else if (KEY_MODEL.equals(key)) {
            updateModel(((String) newValue));
        } else if (KEY_CONFIGURATION.equals(key)) {
            updateConfig(((String) newValue));
        } else if ("canbox_front_door".equals(key)) {
            ((ListPreference) preference).setValue((String) newValue);
            ((ListPreference) preference)
                    .setSummary(((ListPreference) preference).getEntry());
            mFrontDoor = (String) newValue;
        } else if ("canbox_rear_door".equals(key)) {
            ((ListPreference) preference).setValue((String) newValue);
            ((ListPreference) preference)
                    .setSummary(((ListPreference) preference).getEntry());
            mBackDoor = (String) newValue;
        } else if ("canbox_air_ex".equals(key)) {
//			((ListPreference) preference).setValue((String) newValue);
//			((ListPreference) preference)
//					.setSummary(((ListPreference) preference).getEntry());
//			mAirCondition = (String) newValue;

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

            mAirCondition = otherSettings + "";

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

        } else if ("pg_switch_settings".equals(key)) {
            Set<String> s = (Set<String>) newValue;
            ((MultiSelectListPreference) preference).setValues(s);

            Iterator<String> it = s.iterator();
            int otherSettings = 0;
            CharSequence[] entry2 = ((MultiSelectListPreference) preference).getEntries();
            String summary = "";

            while (it.hasNext()) {
                String str = it.next();
                try {
                    int i = Integer.valueOf(str);
                    if (i > 0 && i < 32) {
                        otherSettings |= (0x1 << (i - 1));
                        summary += entry2[i - 1] + " ";
                    }
                } catch (Exception e) {

                }
            }

            preference.setSummary(summary);
            mCarSettingsValue = otherSettings + "";
        } else if ("canbox_key_change_ex".equals(key)) {
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

            mChangeKey = otherSettings + "";
        } else if ("canbox_car_type2".equals(key)) {
            ((ListPreference) preference).setValue((String) newValue);
            ((ListPreference) preference)
                    .setSummary(((ListPreference) preference).getEntry());
            mCarType2 = (String) newValue;

        } else if ("canbox_car_type2".equals(key)) {
            ((ListPreference) preference).setValue((String) newValue);
            ((ListPreference) preference)
                    .setSummary(((ListPreference) preference).getEntry());
            mCarType2 = (String) newValue;

        } else if ("external_radar".equals(key)) {
            ((ListPreference) preference).setValue((String) newValue);
            ((ListPreference) preference)
                    .setSummary(((ListPreference) preference).getEntry());
            mExternalRadar = (String) newValue;

        }
        return false;
    }

    public boolean onPreferenceClick(Preference arg0) {
        return true;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            setCanSettngs();

        }
    };

    private String mExternalRadar = null;
    private String mExternalRadarOrg = null;

    private void initExternalBoxes() {

        mExternalRadarOrg = MachineConfig.getProperty(MachineConfig.KEY_EXTERNAL_BOX);

        ListPreference lp = (ListPreference) findPreference("external_radar");
        lp.setOnPreferenceChangeListener(this);

        if (mExternalRadarOrg == null) {
            mExternalRadarOrg = "0";
        }
        lp.setValue(mExternalRadarOrg);
        lp.setSummary(lp.getEntry());

    }


//	private void initExternalBoxes() {
//
//		int i = MachineConfig.getIntProperty2(MachineConfig.KEY_EXTERNAL_BOX);
//		String[] ss = getResources().getStringArray(
//				R.array.obd_view_spinner_values);
//		String s;
//		if (i < 0 || i >= ss.length) {
//			i = 0;
//		}
//		s = ss[i];
//
//		((TextView) findViewById(R.id.external_boxes)).setText(s);
//	}
//	
//	private void setExternalBoxes(int index) {
//		MachineConfig.setProperty(MachineConfig.KEY_EXTERNAL_BOX, "" + index);
//		
//		Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
//		it.putExtra(MyCmd.EXTRA_COMMON_CMD,
//				MachineConfig.KEY_CAN_BOX);
//		sendBroadcast(it);
//		
//		initExternalBoxes();
//	}
//	
//	private void showExternalBoxes(){
//		AlertDialog ad = new AlertDialog.Builder(this).
//				setItems(getResources().getStringArray(R.array.obd_view_spinner_values), 
//						new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog,
//							int whichButton) {
//						setExternalBoxes(whichButton);					
//					}
//					
//				}).setNegativeButton(R.string.cancel,null).create();
//		
//		ad.show();
//	}

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        if (id == R.id.btn_ok) {
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessageDelayed(0, 500);
            //	setCanSettngs();
        } else if (id == R.id.btn_cancel) {
            finish();
            //		case R.id.external_boxes:
//			showExternalBoxes();
//			break;
        }
    }

    private String mProId = null;
    private String mManaId = null;
    private String mCateId = null;
    private String mModelId = null;

    private String mKeyType = null;
    private String mChangeKey = null;
    private String mFrontDoor = null;
    private String mBackDoor = null;
    private String mAirCondition = null;
    private String mCarType = null;
    private String mCarType2 = null;
    private String mCarEQ = null;
    private String mCarOtherSettings = null;
    private String mCarConfig = null;

    private String mAppShow = null;

    private String mCarSettingsValue = null;

    private void initCanMachineConfig() {
        String mCanboxValue;
        // mCanboxType = null;
        mKeyType = null;
        mChangeKey = null;
        mFrontDoor = null;
        mBackDoor = null;
        mAirCondition = null;
        mProId = null;

        mCanboxValue = MachineConfig.getProperty(MachineConfig.KEY_CAN_BOX);
        if (mCanboxValue != null) {
            String[] ss = mCanboxValue.split(",");
            //String mCanboxType = ss[0];
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
                } else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_ID)) {
                    mProId = ss[i].substring(1);
                } else if (ss[i]
                        .startsWith(MachineConfig.KEY_SUB_CANBOX_CAR_CONFIG)) {
                    mCarConfig = ss[i].substring(1);
                }
            }
        } else {
        }

        // mProId = "130141";
        mManufacturerName = null;
        mCategoryName = null;
        mModelName = null;
        if (mProId != null && mProId.length() >= 4) {
            int start = 0;
            int end = 0;
            if (mProId.charAt(1) == '0' && mProId.charAt(2) == '0') {
                end = 2;
            } else if (mProId.charAt(1) == '0' && mProId.charAt(2) != 0) {
                end = 1;
            } else if (mProId.charAt(2) == '0') {
                end = 2;
            }
            mManaId = mProId.substring(start, end);

            start = end + 1;

            if (mProId.contains("-")) {
                String[] ss = mProId.substring(start).split("-");
                mCateId = ss[0];
                mModelId = ss[1];
            } else {
                if ((mProId.length() - start) == 2) {
                    mCateId = mProId.substring(start, start + 1);
                    mModelId = mProId.substring(start + 1, start + 2);
                } else if ((mProId.length() - start) == 4) {
                    mCateId = mProId.substring(start, start + 2);
                    mModelId = mProId.substring(start + 2, start + 4);
                } else if ((mProId.length() - start) == 3) {
                    mCateId = mProId.substring(start, start + 2);
                    mModelId = mProId.substring(start + 2, start + 3);
                }
            }

            Log.d("abcd", mManaId + ":" + mCateId + ":" + mModelId);

            mManufacturerName = mXlmParser.getMenaByValue(mManaId);
            if (mCategoryName == null) {
                mCategoryName = mXlmParser.getCategorysByValue(mCateId);
                if (mCategoryName != null) {
                    mModelName = mXlmParser.getModelByValue(mCategoryName,
                            mModelId);
                }
            }
            Log.d("abcd", mManufacturerName + ":" + mCategoryName + ":" + mModelName);
        }


        mCarSettingsValue = MachineConfig.getProperty(MachineConfig.KEY_CAN_BOX_PG_SWITCH);
        //if (mCarSettingsValue == null){
        //	mCarSettingsValue = "3";
        //}

    }

    //	AlertDialog.Builder mDialogResult = new AlertDialog.Builder(
//			getContext());
    private String getTotalName() {
        return mManufacturerName + mCategoryName + mModelName;
    }

    private String getExt2Config() {
        String s = "";
        if (mAirCondition != null) {
            s += "," + MachineConfig.KEY_SUB_CANBOX_AIR_CONDITION
                    + mAirCondition;
        }
        if (mChangeKey != null) {
            s += "," + MachineConfig.KEY_SUB_CANBOX_CHANGE_KEY + mChangeKey;
        }
        if (mFrontDoor != null) {
            s += "," + MachineConfig.KEY_SUB_CANBOX_FRONT_DOOR + mFrontDoor;
        }
        if (mBackDoor != null) {
            s += "," + MachineConfig.KEY_SUB_CANBOX_REAR_DOOR + mBackDoor;
        }
        if (mCarType != null) {
            s += "," + MachineConfig.KEY_SUB_CANBOX_CAR_TYPE + mCarType;
        }
        if (s.length() <= 1) {
            s = null;
        }
        return s;
    }

    private void setCanSettngs() {

        String manuId = mManufacturerPreference.getValue();

        String mCategory = mXlmParser.getCategorysValue(mCategoryName);
        String mModel = mXlmParser.getModelValue(mCategoryName, mModelName);

        if ("45".equals(mCategory)) { //update
            canboxUpdate(manuId);
            return;
        }

        if ((mExternalRadar != null) && (!mExternalRadar.equals(mExternalRadarOrg))) {
            mExternalRadarOrg = mExternalRadar;
            MachineConfig.setProperty(MachineConfig.KEY_EXTERNAL_BOX,
                    mExternalRadar);
        }

        if (mModel == null) {
            if (mXlmParser.getTranslation("其他兼容车型").equals(mModelName)) {
                mModel = "99";
            } else if (mXlmParser.getTranslation("其他带空调控制兼容车型").equals(mModelName)) {
                mModel = "98";
            }
        }

        if (mModel != null) {
            String config = mConfigurationPreference.getValue();

            String id = manuId + "0" + mCategory + mModel;
            if (mCategory.length() == 1 && 2 == mModel.length()) {
                if (mCategory.length() == 1) {
                    id = manuId + "0" + mCategory + "-" + mModel;
                }
            }

            String idEx = id + ":" + config;

            CanSetting cs = mJsonParser.getCanSetting(idEx);

            if (cs == null) {
                cs = mJsonParser.getCanSetting(id);
                Log.d("abcd", "id:" + id + ":" + cs);
            } else {
                Log.d("abcd", "idEx:" + idEx + ":" + cs);
            }

//		if (cs == null && mModel.length() == 1 && mCategory.length() == 2) {
//			id = manuId + "0" + mCategory + "," + mModel;
//			cs = mJsonParser.getCanSetting(id);
//			Log.d("abcd", "22:" + id);
//		}
            if (cs != null) {
                if (cs.mPro != null) {
                    try {
                        Integer.parseInt(cs.mPro);

                        String s = getTotalName() + ","
                                + MachineConfig.KEY_SUB_CANBOX_PROTOCAL_VERSION
                                + "3" + ","
                                + MachineConfig.KEY_SUB_CANBOX_PROTOCAL_INDEX
                                + cs.mPro + "," + MachineConfig.KEY_SUB_CANBOX_ID
                                + id + "," + MachineConfig.KEY_SUB_CANBOX_CAR_CONFIG
                                + config;
                        if (cs.mExt != null) {
                            s += "," + cs.mExt;
                        }

                        String ext2 = getExt2Config();
                        if (ext2 != null) {
                            s += ext2;
                        }

                        if (mCarOtherSettings != null) {
                            s += "," + MachineConfig.KEY_SUB_CANBOX_OTHER
                                    + mCarOtherSettings;
                        }

                        if (mCarType2 != null) {
                            s += "," + MachineConfig.KEY_SUB_CANBOX_CAR_TYPE2
                                    + mCarType2;
                        }

                        CanBaud cb = mJsonParser.getCanBaudConfig(cs.mPro);
                        if (cb != null) {
                            s += "," + MachineConfig.KEY_SUB_CANBOX_MCU_BAUD + cb.mBaud
                                    + "," + MachineConfig.KEY_SUB_CANBOX_MCU_CONFIG + cb.mConfig;
                        }


                        MachineConfig.setProperty(MachineConfig.KEY_CAN_BOX, s);

                        mAppShow = mJsonParser.buildAppShow(manuId, mCategory,
                                mModel, cs.mPro, config);
                        /*check ac force set*/
                        if (mAppShow != null) {
                            if (mAirCondition != null) {
                                int acFlag = 0;
                                try {
                                    acFlag = Integer.valueOf(mAirCondition);
                                } catch (Exception e) {

                                }

                                String[] ss;
                                if ((acFlag & 0x20) != 0) {

                                    if (mAppShow.contains(AppConfig.HIDE_CANBOX_AC)) {
                                        ss = mAppShow.split(",");
                                        mAppShow = null;
                                        if (ss != null && ss.length > 1) {
                                            mAppShow = "";
                                            for (int i = 0; i < ss.length; ++i) {
                                                if (!AppConfig.HIDE_CANBOX_AC
                                                        .equals(ss[i])) {
                                                    mAppShow += ss[i] + ",";
                                                }
                                            }
                                        }
                                    } else {

                                        mAppShow += AppConfig.HIDE_CANBOX_AC
                                                + ",";
                                    }
                                }

                            }
                        }
                        MachineConfig.setProperty(
                                MachineConfig.KEY_CAN_BOX_SHOW_APP, mAppShow);


                        Log.d(TAG, manuId + ":" + mCategory + ":" + mModel + ":"
                                + config + ":" + cs.mPro + ":" + mAppShow);


                        MachineConfig.setProperty(MachineConfig.KEY_CAN_BOX_PG_SWITCH, mCarSettingsValue);
                        Util.sudoExec("sync");
                        Toast.makeText(this, R.string.success, Toast.LENGTH_LONG)
                                .show();

                        Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
                        it.putExtra(MyCmd.EXTRA_COMMON_CMD,
                                MachineConfig.KEY_CAN_BOX);
                        sendBroadcast(it);
                        // finish();
                        return;
                    } catch (Exception e) {

                    }
                }

            }
        }

        if (mManufacturerName != null
                && mManufacturerName.equals(mXlmParser.getTranslation("通用机"))) {

            MachineConfig.setProperty(MachineConfig.KEY_CAN_BOX_PG_SWITCH, mCarSettingsValue);
            Util.sudoExec("sync");
            Toast.makeText(this, R.string.success, Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(this, R.string.not_support, Toast.LENGTH_LONG).show();
        }
        // Toast.makeText(this, "no found", Toast.LENGTH_LONG).show();
        // finish();

        MachineConfig.setProperty(MachineConfig.KEY_CAN_BOX, null);
        MachineConfig.setProperty(
                MachineConfig.KEY_CAN_BOX_SHOW_APP, null);


        Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
        it.putExtra(MyCmd.EXTRA_COMMON_CMD,
                MachineConfig.KEY_CAN_BOX);
        sendBroadcast(it);
    }

    private void canboxUpdate(String i) {
        try {
            String s = null;
            int id = Integer.valueOf(i);
            switch (id) {
                case 2:
                    String config = mConfigurationPreference.getValue();
                    s = "hiworld：" + config;
                    break;
                case 3:
                case 13:
                    s = "raise";
                    break;
                case 15:
                    s = "haozheng";
                    break;
                default:
                    s = "simple";
                    break;
            }
            Intent it;
//			MachineConfig.setProperty(MachineConfig.KEY_CAN_BOX, s);
//			MachineConfig.setProperty(
//					MachineConfig.KEY_CAN_BOX_SHOW_APP, null);
//			
//			it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
//			it.putExtra(MyCmd.EXTRA_COMMON_CMD,
//					MachineConfig.KEY_CAN_BOX);
//
//			sendBroadcast(it);

            it = new Intent(
                    MyCmd.BROADCAST_CMD_TO_CAR_SERVICE);
            it.putExtra(
                    MyCmd.EXTRA_COMMON_CMD,
                    MyCmd.Cmd.UPDATE_CANBOX);
            it.putExtra(
                    MyCmd.EXTRA_COMMON_DATA,
                    s);
            it.setPackage(AppConfig.PACKAGE_CAR_SERVICE);
            sendBroadcast(it);
        } catch (Exception e) {

        }
    }


    //advanced settings
    ListPreference mLPFrontDoor;
    ListPreference mLPBackDoor;
    MultiSelectListPreference mLPAC;

    MultiSelectListPreference mOtherSettings;
    MultiSelectListPreference mCarSettings;
    MultiSelectListPreference mKeyChangeSettings;

    private void updateAdvancedVisible(String name) {
        if (name != null && name.equals(mXlmParser.getTranslation("通用机"))) {
            updateAdvancedVisible(false);
        } else {
            updateAdvancedVisible(true);
        }

    }

    private boolean mAdvanceVisible = true;

    private void updateAdvancedVisible(boolean show) {
        if (mAdvanceVisible == show) {
            return;
        }
        mAdvanceVisible = show;
        if (show) {
            if (findPreference("canbox_front_door") == null
                    && mLPFrontDoor != null) {
                getPreferenceScreen().addPreference(mLPFrontDoor);
            }
            if (findPreference("canbox_rear_door") == null
                    && mLPBackDoor != null) {
                getPreferenceScreen().addPreference(mLPBackDoor);
            }
            if (findPreference("canbox_air_ex") == null && mLPAC != null) {
                getPreferenceScreen().addPreference(mLPAC);
            }
            if (findPreference("canbox_key_change_ex") == null
                    && mKeyChangeSettings != null) {
                getPreferenceScreen().addPreference(mKeyChangeSettings);
            }
//			if (findPreference("pg_switch_settings") == null
//					&& mCarSettings != null) {
//				getPreferenceScreen().addPreference(mCarSettings);
//			}
            if (findPreference("canbox_other_settings") == null
                    && mCarSettings != null) {
                getPreferenceScreen().addPreference(mOtherSettings);
            }
        } else {

            mLPFrontDoor = (ListPreference) findPreference("canbox_front_door");
            mLPBackDoor = (ListPreference) findPreference("canbox_rear_door");
            mLPAC = (MultiSelectListPreference) findPreference("canbox_air_ex");
            mKeyChangeSettings = (MultiSelectListPreference) findPreference("canbox_key_change_ex");

            if (mLPFrontDoor != null) {
                getPreferenceScreen().removePreference(mLPFrontDoor);
            }
            if (mLPBackDoor != null) {
                getPreferenceScreen().removePreference(mLPBackDoor);
            }
            if (mLPAC != null) {
                getPreferenceScreen().removePreference(mLPAC);
            }
            if (mKeyChangeSettings != null) {
                getPreferenceScreen().removePreference(mKeyChangeSettings);
            }
//			if (mCarSettings != null) {
//				getPreferenceScreen().removePreference(mCarSettings);
//			}
            if (mOtherSettings != null) {
                getPreferenceScreen().removePreference(mOtherSettings);
            }
        }
    }

    private void clearAdvanced() {
        mBackDoor = null;
        mChangeKey = null;
        mAirCondition = null;
        mBackDoor = null;
        updateAdvanced();
    }

    private void updateAdvanced() {

        ListPreference lp;

        lp = (ListPreference) findPreference("canbox_front_door");
        if (lp == null) {
            return;
        }
        String[] entry1 = {getString(R.string.normal),
                getString(R.string.change), getString(R.string.hide)};
        String[] value1 = {"0", "1", "2"};
        mLPFrontDoor = lp;
        lp.setEntries(entry1);
        lp.setEntryValues(value1);
        lp.setOnPreferenceChangeListener(this);
        if (mFrontDoor != null) {
            lp.setValue(mFrontDoor);
        } else {
            lp.setValue("0");
        }
        lp.setSummary(lp.getEntry());

        lp = (ListPreference) findPreference("canbox_rear_door");
        mLPBackDoor = lp;
        lp.setEntries(entry1);
        lp.setEntryValues(value1);
        lp.setOnPreferenceChangeListener(this);


        if (mBackDoor != null) {
            lp.setValue(mBackDoor);
        } else {
            lp.setValue("0");
        }
        lp.setSummary(lp.getEntry());

//		lp = (ListPreference) findPreference("canbox_air_ex");
//		mLPAC = lp;
//
//		String[] entry2 = { getString(R.string.normal),
//				getString(R.string.temp_change), getString(R.string.hide), };
//
//		try {
//			int a = Integer.valueOf(mAirCondition);
//			if (a > 2) {
//				mAirCondition = "0";
//			}
//		} catch (Exception e) {
//
//		}		
//
//		lp.setEntries(entry2);
//		lp.setEntryValues(value1);
//		if (mAirCondition != null) {
//			lp.setValue(mAirCondition);
//		} else {
//			lp.setValue("0");
//		}
//		lp.setSummary(lp.getEntry());
//		lp.setOnPreferenceChangeListener(this);

        updateOtherSettings();
        updateKeysChageSettings();
        updateACSettings();
    }


    private void updateCarSettings() {
        if (mCarSettings == null) {

            mCarSettings = (MultiSelectListPreference) findPreference("pg_switch_settings");
            if (mCarSettings == null) {
                return;
            }
            mCarSettings.setOnPreferenceChangeListener(this);
            mCarSettings.setOnPreferenceClickListener(this);
        }

//		getPreferenceScreen().addPreference(mOtherSettings);
        String[] entry2 = { //getResources().getString(R.string.reverse_by_canbox),
                getResources().getString(R.string.lr_turner_by_canbox),
                getResources().getString(R.string.brake_by_canbox)};
        String[] value2 = {"2", "3"};

        mCarSettings.setEntries(entry2);
        mCarSettings.setEntryValues(value2);

        String summary = "";
//		for (int i = 0; i < entry2.length; ++i) {
//			summary += entry2[i] + " ";
//		}

//		mCarSettings.setSummary(summary);

        HashSet<String> ss = new HashSet<String>();

        if (mCarSettingsValue != null) {
            try {
                int v = Integer.valueOf(mCarSettingsValue);
                for (int i = 0; i < 32; i++) {
                    if ((v & (0x1 << i)) != 0) {
                        ss.add("" + (i + 1));
                        summary += entry2[i] + " ";
                    }
                }
            } catch (Exception e) {

            }
        }
        mCarSettings.setValues(ss);
        mCarSettings.setSummary(summary);
    }

    private void updateOtherSettings() {
        if (mOtherSettings == null) {

            mOtherSettings = (MultiSelectListPreference) findPreference("canbox_other_settings");
            if (mOtherSettings == null) {
                return;
            }
            mOtherSettings.setOnPreferenceChangeListener(this);
            mOtherSettings.setOnPreferenceClickListener(this);
        }

//		getPreferenceScreen().addPreference(mOtherSettings);
        String[] entry2 = {
                //getResources().getString(R.string.radar_volume),
                //getResources().getString(R.string.radar_ui) ,
                //getResources().getString(R.string.radar_only_in_rverse),
                getResources().getString(R.string.time_hour_add_1),
                getResources().getString(R.string.time_hour_minus_1),
                getResources().getString(R.string.data_distribution)};
        String[] value2 = { /*"1", "2", "3",*/ "4", "5", "7"};


        if (mCategoryName != null) {
            String mCategory = mXlmParser.getCategorysValue(mCategoryName);
            if ("6".equals(mCategory)) {
                String[] entry3 = {
                        getResources().getString(R.string.time_hour_add_1),
                        getResources().getString(R.string.time_hour_minus_1),
                        getResources().getString(R.string.right_camera),
                        getResources().getString(R.string.data_distribution)};
                String[] value3 = { /*"1", "2", "3",*/ "4", "5", "6", "7"};
                entry2 = entry3;
                value2 = value3;
            }
        }

        mOtherSettings.setEntries(entry2);
        mOtherSettings.setEntryValues(value2);

        String summary = "";
//		for (int i = 0; i < entry2.length; ++i) {
//			summary += entry2[i] + " ";
//		}

//		mOtherSettings.setSummary(summary);

        HashSet<String> ss = new HashSet<String>();

        if (mCarOtherSettings != null) {
            try {
                int v = Integer.valueOf(mCarOtherSettings);
                for (int i = 0; i < 32; i++) {
                    if ((v & (0x1 << i)) != 0) {
                        ss.add("" + i);
                        summary += entry2[i] + " ";
                    }
                }
            } catch (Exception e) {

            }
        }
        mOtherSettings.setValues(ss);
        mOtherSettings.setSummary(summary);
    }


    private void updateKeysChageSettings() {
        if (mKeyChangeSettings == null) {

            mKeyChangeSettings = (MultiSelectListPreference) findPreference("canbox_key_change_ex");
            if (mKeyChangeSettings == null) {
                return;
            }
        }

        mKeyChangeSettings.setOnPreferenceChangeListener(this);
        mKeyChangeSettings.setOnPreferenceClickListener(this);
//		getPreferenceScreen().addPreference(mKeyChangeSettings);
        String[] entry2 = {
                getString(R.string.canbox_pre_next),
                getString(R.string.canbox_volume_increase_decrease)};
        String[] value2 = {"0", "1"};

        mKeyChangeSettings.setEntries(entry2);
        mKeyChangeSettings.setEntryValues(value2);

        String summary = "";
//		for (int i = 0; i < entry2.length; ++i) {
//			summary += entry2[i] + " ";
//		}


        HashSet<String> ss = new HashSet<String>();

        if (mChangeKey != null) {
            try {
                int v = Integer.valueOf(mChangeKey);
                for (int i = 0; i < 32; i++) {
                    if ((v & (0x1 << i)) != 0) {
                        ss.add("" + i);
//						summary += " " + entry2[i];
                    }
                }
            } catch (Exception e) {

            }
        }
        mKeyChangeSettings.setValues(ss);
//		mKeyChangeSettings.setSummary(summary);
    }

    private void updateACSettings() {
        if (mLPAC == null) {

            mLPAC = (MultiSelectListPreference) findPreference("canbox_air_ex");
            if (mLPAC == null) {
                return;
            }
        }

        mLPAC.setOnPreferenceChangeListener(this);
        mLPAC.setOnPreferenceClickListener(this);

        //ac control
        boolean forceShowAC = true;
        try {
            String manuId = mManufacturerPreference.getValue();

            String mCategory = mXlmParser.getCategorysValue(mCategoryName);
            String mModel = mXlmParser.getModelValue(mCategoryName, mModelName);

            if (mModel != null) {
                String config = mConfigurationPreference.getValue();

                String id = manuId + "0" + mCategory + mModel;
                if (mCategory.length() == 1 && 2 == mModel.length()) {
                    if (mCategory.length() == 1) {
                        id = manuId + "0" + mCategory + "-" + mModel;
                    }
                }

                String idEx = id + ":" + config;

                CanSetting cs = mJsonParser.getCanSetting(idEx);

                if (cs == null) {
                    cs = mJsonParser.getCanSetting(id);
                }

                mAppShow = mJsonParser.buildAppShow(manuId, mCategory, mModel,
                        cs.mPro, config);
                if (mAppShow != null
                        && mAppShow.contains(AppConfig.HIDE_CANBOX_AC)) {
                    forceShowAC = false;
                }
            }
        } catch (Exception e) {

        }

        String[] entry2;// = { getString(R.string.temp_change),	getString(R.string.hide),	getString(R.string.hide_ourdoor_temp) };
        String[] value2 = {"2", "3", "4", "5"};

        if (forceShowAC) {

            entry2 = new String[]{
                    getString(R.string.temp_change),
                    getString(R.string.hide),
                    getString(R.string.hide_ourdoor_temp),
                    getString(R.string.display) + getString(R.string.air_control)};
        } else {

            entry2 = new String[]{
                    getString(R.string.temp_change),
                    getString(R.string.hide),
                    getString(R.string.hide_ourdoor_temp),
                    getString(R.string.hide) + getString(R.string.air_control)};
        }

        //
        mLPAC.setEntries(entry2);
        mLPAC.setEntryValues(value2);

        HashSet<String> ss = new HashSet<String>();

        if (mAirCondition != null) {
            try {
                int v = Integer.valueOf(mAirCondition);
                for (int i = 0; i < 32; i++) {
                    if ((v & (0x1 << i)) != 0) {
                        ss.add("" + i);
                    }
                }
            } catch (Exception e) {

            }
        }
        mLPAC.setValues(ss);
    }


    ListPreference mLPCarType2;

    private void updateCarType2() {

        if (mLPCarType2 == null) {

            mLPCarType2 = (ListPreference) findPreference("canbox_car_type2");
            mLPCarType2.setOnPreferenceChangeListener(this);
        }

        boolean add = true;
        if ("通用".equals(mCategoryName) && "睿志诚".equals(mManufacturerName)) {
            String[] entry2 = {getString(R.string.str_auto),
                    getString(R.string.canbox_key_mode) + "1",
                    getString(R.string.canbox_key_mode) + "2",
                    getString(R.string.canbox_key_mode) + "3"};
            String[] value2 = {"0", "1", "2", "3"};

            mLPCarType2.setEntries(entry2);
            mLPCarType2.setEntryValues(value2);

            if (mCarType2 != null) {
                mLPCarType2.setValue(mCarType2);
            } else {
                mLPCarType2.setValue("0");
            }
            mLPCarType2.setTitle(getString(R.string.canbox_key_mode));
            mLPCarType2.setSummary(mLPCarType2.getEntry());
        } else {
            mCarType2 = null;
            add = false;
        }

        if (add) {
            getPreferenceScreen().addPreference(mLPCarType2);
        } else {
            getPreferenceScreen().removePreference(mLPCarType2);
        }
    }
}
