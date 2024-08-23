package com.canboxsetting.info;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MachineConfig;
import com.common.utils.MyCmd;
import com.common.utils.SettingProperties;


public class Nission2013InfoSimpleFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "Nission2013InfoSimpleFragment";
    ListPreference mLPCarType;
    String mCarType;
    private String mCanboxType;

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

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void updateCarType(String can) {


        String[] entry2 = {"0:AVM Middle", "1:AVM High"};
        String[] value2 = {"0", "1"};

        mLPCarType.setEntries(entry2);
        mLPCarType.setEntryValues(value2);

        if (mCarType != null) {
            mLPCarType.setValue(mCarType);
        } else {
            mLPCarType.setValue("0");
        }

        mLPCarType.setSummary(mLPCarType.getEntry());

    }

    private void updateMachineConfig() {
        SettingProperties.setProperty(getActivity(), MachineConfig.VALUE_CANBOX_NISSAN2013, mCarType);

        Intent it = new Intent(MyCmd.BROADCAST_MACHINECONFIG_UPDATE);
        it.putExtra(MyCmd.EXTRA_COMMON_CMD, MachineConfig.KEY_CAN_BOX);
        getActivity().sendBroadcast(it);
    }

    private void getCanboxSetting() {
        mCarType = SettingProperties.getProperty(getActivity(), MachineConfig.VALUE_CANBOX_NISSAN2013);

    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        if ("lang".equals(key)) {
            byte[] buf = new byte[]{(byte) 0xc6, 0x2, (byte) 2, (byte) 2};
            BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        } else if ("car_type_cmd".equals(key)) {

            mCarType = (String) newValue;

            updateMachineConfig();


            ((ListPreference) preference).setValue(String.valueOf((String) newValue));
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
