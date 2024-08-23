package com.canboxsetting.set;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.NodePreference;
import com.common.view.MyPreferenceSeekBar;

public class Set9 extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "Set275";

    private static final NodePreference[] NODES = {

            new NodePreference("car_type_cmd", 0x7501, 0x2700, 0xff, 0, R.array.parking_sound_type_entries, R.array.three_values),

    };

    private final static int[] INIT_CMDS = {0x27};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.empty_setting);

        init();

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private void init() {

        for (int i = 0; i < NODES.length; ++i) {
            Preference p = NODES[i].createPreference(getActivity());
            if (p != null) {

                Preference ps = getPreferenceScreen();
                if (ps instanceof PreferenceScreen) {
                    boolean add = true;

                    if (add) {
                        ((PreferenceScreen) ps).addPreference(p);
                    }

                    if ((p instanceof ListPreference) || (p instanceof SwitchPreference)) {
                        p.setOnPreferenceChangeListener(this);
                    } else if ((p instanceof MyPreferenceSeekBar)) {
                        p.setOnPreferenceChangeListener(this);
                        if (NODES[i].mKey.equals("energy_recovery")) {
                            // ((MyPreferenceSeekBar)p).setUnit(" % ");
                        }
                    }
                }

            }
        }
    }

    private void udpatePreferenceValue(Preference preference, Object newValue) {
        String key = preference.getKey();
        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(key)) {
                if (preference instanceof ListPreference) {

                    sendCanboxInfo(0xca, Integer.parseInt((String) newValue));

                    ((ListPreference) preference).setValue(String.valueOf((String) newValue));
                    preference.setSummary("%s");
                }
                break;
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        try {
            udpatePreferenceValue(preference, newValue);
        } catch (Exception e) {

        }
        return false;
    }

    private void sendCanboxInfo(int d0, int d1) {
        byte[] buf = new byte[]{(byte) d0, 0x1, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, int index) {
        Preference p = findPreference(key);
        if (p != null) {
            if (p instanceof ListPreference) {
                ListPreference lp = (ListPreference) p;
                CharSequence[] ss = lp.getEntries();
                if (ss != null && (ss.length > index)) {
                    lp.setValue(String.valueOf(index));
                }
                lp.setSummary("%s");
            } else if (p instanceof SwitchPreference) {
                SwitchPreference sp = (SwitchPreference) p;
                sp.setChecked(index == 1 ? true : false);
            } else if (p instanceof MyPreferenceSeekBar) {
                p.setSummary(index + "");
            }
        }
    }

}
