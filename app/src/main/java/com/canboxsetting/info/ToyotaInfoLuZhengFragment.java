package com.canboxsetting.info;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.utils.Node;
import com.common.util.SystemConfig;
import com.common.view.MyPreference2;

public class ToyotaInfoLuZhengFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "ToyotaInfoSimpleFragment";

    private boolean mRudder = false;
    private int mFlashLight = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.toyota_info_luzheng);

        for (int i = 0; i < NODES.length; ++i) {
            mPreferences[i] = findPreference(NODES[i].mKey);
            if (mPreferences[i] != null) {
                mPreferences[i].setOnPreferenceClickListener(this);
            }
        }

        getCanboxSetting();
        String value = MachineConfig.getProperty(MachineConfig.KEY_RUDDER);

        if (MachineConfig.VALUE_ON.equals(value)) {
            mRudder = true;
        } else {
            mRudder = false;
        }

        if (mRudder) {
            if (mFrontDoor == 1) {
                mFrontDoor = 0;
            } else {
                mFrontDoor = 1;
            }

            if (mBackDoor == 1) {
                mBackDoor = 0;
            } else {
                mBackDoor = 1;
            }
        }

        // findPreference("tpms").setOnPreferenceClickListener(this);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    private int mFrontDoor = 0;
    private int mBackDoor = 0;

    private void getCanboxSetting() {
        mFrontDoor = 0;
        mBackDoor = 0;

        String mCanboxType = MachineConfig.getPropertyForce(MachineConfig.KEY_CAN_BOX);
        if (mCanboxType != null) {
            String[] ss = mCanboxType.split(",");
            mCanboxType = ss[0];
            try {
                for (int i = 1; i < ss.length; ++i) {
                    if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_FRONT_DOOR)) {
                        mFrontDoor = Integer.valueOf(ss[i].substring(1));
                    } else if (ss[i].startsWith(MachineConfig.KEY_SUB_CANBOX_REAR_DOOR)) {
                        mBackDoor = Integer.valueOf(ss[i].substring(1));
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    private final static int[] INIT_CMDS = {0x2300, 0x2a00, 0x2b00, 0x1f00,
            /*
             * 0x4010, 0x4020, 0x4030, 0x4031, 0x4040, 0x4050, 0x4051, 0x4060, 0x4070,
             * 0x4080, 0x4090,
             */};

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
                if ((msg.what & 0xff00) == 0xff00) {
                    sendCanboxInfo0xff(msg.what & 0xff);
                } else {

                    sendCanboxInfo0x90((msg.what & 0xff00) >> 8, msg.what & 0xff);
                }
            }
        }
    };

    private void sendCanboxInfo0xff(int d1) {// no canbox cmd.
        byte[] buf = new byte[]{(byte) 0xff, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{(byte) 0x90, 0x2, (byte) d0, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    public boolean onPreferenceClick(Preference arg0) {

        try {
            String key = arg0.getKey();
            if ("tpms".equals(key)) {
            } else if ("hybrid".equals(key)) {
                mBatteryView = null;
                sendCanboxInfo0x90(0x1f, 0);
                // sendCanboxInfo(0xff,0x1f);
            } else if ("carbodyinfo".equals(key)) {
                requestInitData();
                // sendCanboxInfo(0xff,0x1f);
            } else if ("car_amplifier_volume".equals(key)) {
                showVolumeDialog();
            }
        } catch (Exception e) {

        }

        return false;
    }

    TextView mTextVolume;
    SeekBar mLevel;

    private void showVolumeDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setTitle(R.string.car_amplifier_volume);
        View view = getActivity().getLayoutInflater().inflate(R.layout.volume_dialog, null);
        alertDialog.setView(view);
        alertDialog.show();

        // alertDialog.setContentView(R.layout.volume_dialog);
        mLevel = (SeekBar) alertDialog.findViewById(R.id.level);

        mTextVolume = (TextView) alertDialog.findViewById(R.id.volume);
        if (mLevel != null) {
            mLevel.setMax(63);

            int volume = MachineConfig.getIntProperty2(SystemConfig.CANBOX_EQ_VOLUME);
            if (volume == -1) {
                volume = 45;
            }
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
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        if (mTextVolume != null) {
                            mTextVolume.setText("" + progress);

                            byte[] buf = new byte[]{(byte) 0x84, 0x2, 0x07, (byte) progress};
                            BroadcastUtil.sendCanboxInfo(getActivity(), buf);

                            MachineConfig.setIntProperty(SystemConfig.CANBOX_EQ_VOLUME, progress);
                        }
                    }
                }
            });
        }
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
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestInitData();
            }
        }, 100);
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private void updateVisible(byte[] buf) {

        showPreference("cardoorspeed", buf[2] & 0x1, null);
        showPreference("cardoorautomatic", buf[2] & 0x2, null);
        showPreference("plinkage", buf[2] & 0x4, null);
        showPreference("linkagedoorlock", buf[2] & 0x8, null);
        showPreference("twokeyunlock", buf[2] & 0x10, null);
        showPreference("remoteunlock", buf[2] & 0x20, null);
        showPreference("opendoorflash", buf[2] & 0x40, null);
        showPreference("timeswitchlight", buf[2] & 0x80, null);

        // showPreference("cardoorspeed", buf[3] & 0x1, null);
        showPreference("autolight", buf[3] & 0x2, null);
        showPreference("smartdoorlock", buf[3] & 0x4, null);
        showPreference("lockakey", buf[3] & 0x8, null);

        showPreference("airautokey", buf[3] & 0x10, null);
        showPreference("airswitchautokey", buf[3] & 0x20, null);
        showPreference("back_camera_path", buf[3] & 0x40, null);
        showPreference("cell_back_door", buf[3] & 0x80, null);

    }

    private static final Node[] NODES = {

            new Node("tpms", 0x0), new Node("hybrid", 0x0), new Node("carbodyinfo", 0x0), new Node("car_amplifier_volume", 0x0)

    };
    private Preference[] mPreferences = new Preference[NODES.length];

    private void showPreference(String id, int show, String parant) {
        Preference preference = null;

        for (int i = 0; i < NODES.length; ++i) {
            if (NODES[i].mKey.equals(id)) {
                preference = mPreferences[i];
                break;
            }
        }

        if (preference != null) {
            PreferenceScreen ps;
            if (parant != null) {
                ps = (PreferenceScreen) findPreference(parant);
            } else {
                ps = getPreferenceScreen();
            }
            if (ps != null) {
                if (show != 0) {
                    if (ps.findPreference(id) == null) {
                        ps.addPreference(preference);
                    }
                } else {
                    if (findPreference(id) != null) {
                        boolean b = ps.removePreference(preference);
                        // Log.d("dd", "" + b);
                    }
                }
            }
        }

    }

    private View mBatteryView;
    private View mCar;

    private void setText(int id, String s) {

        if (mCar != null) {
            ((TextView) mCar.findViewById(id)).setText(s);
        }
    }

    private void setTextEx(int id, String s, int string_id) {
        if (string_id != 0) {
            s = getActivity().getString(string_id) + "\n" + s;
        }
        setText(id, s);
    }

    private void checkCarView() {
        if (mCar == null) {
            MyPreference2 p = (MyPreference2) findPreference("car_content");
            if (p != null) {
                mCar = p.getMainView();
            }
        }
    }

    private int mLightStringId = 0;
    private Handler mHandlerFlash = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            flashLight(mFlashLight, true);
        }
    };

    private boolean mFlashing = true;

    private void flashLight(int b, boolean auto) {
        View v;

        if (b != 0) {
            if (mFlashLight != b) {
                mFlashing = true;
            } else {
                if (!auto) {
                    mFlashLight = b;
                    return;
                }
                mFlashing = !mFlashing;
            }
        } else {
            mFlashing = true;
        }

        mFlashLight = b;

        int flashLight = 0;
        v = mCar.findViewById(R.id.toyaota_car_light_right);
        if ((b & 0x20) == 0 && (b & 0x8) == 0) {
            v.setVisibility(View.GONE);
        } else {
            // if (string_id == 0 && v.getVisibility() == View.GONE) {
            // string_id = R.string.lights_and_turn_right;
            // }
            if (mFlashing) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }
        }

        v = mCar.findViewById(R.id.toyaota_car_light_left);
        if ((b & 0x10) == 0 && (b & 0x8) == 0) {
            v.setVisibility(View.GONE);
        } else {
            if (mFlashing) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }
        }

        mHandlerFlash.removeMessages(0);
        mHandlerFlash.sendEmptyMessageDelayed(0, 500);
    }

    private void updateView(byte[] buf) {

        int index = 0;
        String s = "";
        String temp;

        switch (buf[0]) {
            case 0x41: {
                switch (buf[2]) {
                    case 0x2:
                        index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[3] & 0xff) << 16));
                        s = String.format("%d km", index);
                        setPreference("mileage_sum", s);

                        setTextEx(R.id.toyota_car_info1, s, R.string.mileage_sum);

                        index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
                        s = String.format("%d km", index);
                        setPreference("mileage17", s);

                        setTextEx(R.id.toyota_car_info2, s, R.string.mileage17);

                        index = ((buf[10] & 0xff) | ((buf[9] & 0xff) << 8) | ((buf[8] & 0xff) << 16));

                        s = String.format("%d.%d km/h", index / 10, index % 10);
                        setPreference("trip_a", s);

                        setTextEx(R.id.toyota_car_info4, s, R.string.trip_a1);

                        index = ((buf[13] & 0xff) | ((buf[12] & 0xff) << 8) | ((buf[11] & 0xff) << 16));

                        s = String.format("%d.%d km/h", index / 10, index % 10);
                        setPreference("trip_b", s);
                        setTextEx(R.id.toyota_car_info5, s, R.string.trip_b1);


                        index = ((buf[17] & 0xff) | ((buf[16] & 0xff) << 8));
                        s = String.format("%d.%d km/h", index / 10, index % 10);
                        setPreference("avg_speed", s);

                        break;
                    case 0x3:
                        index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                        s = String.format("%d RPM", index);
                        setPreference("engine_speed55", s);
                        break;
                }

            }
            break;

            case 0x23: {
                String unit = "";
                if (buf[2] == 0x1) {
                    unit = "KM/L";
                } else if (buf[2] == 0x2) {
                    unit = "L/100KM";
                } else {
                    unit = "MPG";
                }

                index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                s = String.format("%d.%d %s", index / 10, index % 10, unit);
                setPreference("historyfuel", s);

                setTextEx(R.id.toyota_car_info6, s, R.string.historyfuel);
            }
            break;


            case 0x2b: {
                String unit = "";
                if (buf[2] == 0x1) {
                    unit = "KM/L";
                } else if (buf[2] == 0x2) {
                    unit = "L/100KM";
                } else {
                    unit = "MPG";
                }

                index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                s = String.format("%d.%d %s", index / 10, index % 10, unit);
                setPreference("averagefuel", s);

                s = String.format("%02d:%02d", (buf[7] & 0xff), (buf[8] & 0xff));
                setPreference("traveltime", s);

                index = ((buf[6] & 0xff) | ((buf[5] & 0xff) << 8));
                s = String.format("%d km/h", index);
                setPreference("averagespeed", s);
            }
            break;
            case 0x2a:

                index = ((buf[4] & 0xff) | ((buf[3] & 0xff) << 8));
                s = String.format("%d RPM", index);
                setPreference("engine_speed55", s);

                index = ((buf[6] & 0xff) | ((buf[5] & 0xff) << 8));
                s = String.format("%d km/h", index);
                setPreference("am_runningspeed", s);
                index = ((buf[11] & 0xff) | ((buf[10] & 0xff) << 8));
                s = String.format("%d km", index);
                setPreference("mileage", s);
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
