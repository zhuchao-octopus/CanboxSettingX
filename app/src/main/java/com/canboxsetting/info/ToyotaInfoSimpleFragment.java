package com.canboxsetting.info;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceScreen;

import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.util.BroadcastUtil;
import com.common.util.MachineConfig;
import com.common.util.MyCmd;
import com.common.util.Node;
import com.common.util.SystemConfig;
import com.common.view.MyPreference2;

public class ToyotaInfoSimpleFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "ToyotaInfoSimpleFragment";

    private boolean mRudder = false;
    private int mFlashLight = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.toyota_info);

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

        Log.d(TAG, "onCreate");
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

    private final static int[] INIT_CMDS = {0x4101, 0x4102, 0x4103, 0x2400, 0x1f00, 0x2300,
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
                mTpmsView = null;
                sendCanboxInfo0xff(0x25);
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
        mHandlerHybird.removeMessages(0);
        mHandlerHybird.removeMessages(1);
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

    private byte mHybrid;
    private View mTpmsView;
    private View mBatteryView;
    private View mCar;

    boolean mShowHybird = false;

    private Handler mHandlerHybird = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mHandlerHybird.removeMessages(0);
                if (mShowHybird) {
                    clearHybird();
                    mShowHybird = false;
                    mHandlerHybird.sendEmptyMessageDelayed(0, 1000);
                } else {
                    showHyBird(mHybrid);
                }
            } else if (msg.what == 1) {
                mHandlerHybird.removeMessages(1);
                sendCanboxInfo0x90(0x1f, msg.what & 0xff);
                mHandlerHybird.sendEmptyMessageDelayed(1, 1000);
            }

        }
    };

    private void clearHybird() {
        mBatteryView.findViewById(R.id.canbus8_wheel_motor).setVisibility(View.GONE);

        mBatteryView.findViewById(R.id.canbus8_motor_wheel).setVisibility(View.GONE);
        mBatteryView.findViewById(R.id.canbus8_battery_motor).setVisibility(View.GONE);
        mBatteryView.findViewById(R.id.canbus8_motor_battery).setVisibility(View.GONE);
        mBatteryView.findViewById(R.id.canbus8_arrow_set).setVisibility(View.GONE);
        mBatteryView.findViewById(R.id.canbus8_img_lf2_1).setVisibility(View.GONE);
    }

    private void showHyBird(byte b) {
        mHybrid = b;

        mHandlerHybird.removeMessages(0);
        if (mHybrid != 0) {
            mShowHybird = true;
            mHandlerHybird.sendEmptyMessageDelayed(0, 1000);
        }
        if ((mHybrid & 0x20) == 0) {
            mBatteryView.findViewById(R.id.canbus8_wheel_motor).setVisibility(View.GONE);

            if ((mHybrid & 0x2) == 0) {
                mBatteryView.findViewById(R.id.canbus8_motor_wheel).setVisibility(View.GONE);
            } else {

                mBatteryView.findViewById(R.id.canbus8_motor_wheel).setVisibility(View.VISIBLE);
            }

        } else {

            mBatteryView.findViewById(R.id.canbus8_wheel_motor).setVisibility(View.VISIBLE);

            mBatteryView.findViewById(R.id.canbus8_motor_wheel).setVisibility(View.GONE);
        }

        if ((mHybrid & 0x10) == 0) {
            mBatteryView.findViewById(R.id.canbus8_battery_motor).setVisibility(View.GONE);

            if ((mHybrid & 0x1) == 0) {
                mBatteryView.findViewById(R.id.canbus8_motor_battery).setVisibility(View.GONE);
            } else {

                mBatteryView.findViewById(R.id.canbus8_motor_battery).setVisibility(View.VISIBLE);
            }

        } else {

            mBatteryView.findViewById(R.id.canbus8_battery_motor).setVisibility(View.VISIBLE);

            mBatteryView.findViewById(R.id.canbus8_motor_battery).setVisibility(View.GONE);
        }

        if ((mHybrid & 0x4) == 0) {
            mBatteryView.findViewById(R.id.canbus8_arrow_set).setVisibility(View.GONE);
        } else {

            mBatteryView.findViewById(R.id.canbus8_arrow_set).setVisibility(View.VISIBLE);
        }

        if ((mHybrid & 0x8) == 0) {
            mBatteryView.findViewById(R.id.canbus8_img_lf2_1).setVisibility(View.GONE);
        } else {

            mBatteryView.findViewById(R.id.canbus8_img_lf2_1).setVisibility(View.VISIBLE);
        }
    }

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
        //if (mCar == null) {
        MyPreference2 p = (MyPreference2) findPreference("car_content");
        if (p != null) {
            mCar = p.getMainView();
        }
        //}
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
            case 0x24:
                checkCarView();
                if (mCar == null) {
                    return;
                }
                int door = (buf[2]);
                door = (((door & 0x40) >> 6) | ((door & 0x80) >> 6) | ((door & 0x10) >> 2) | ((door & 0x20) >> 2) | ((door & 0x08) << 1) | ((buf[3] & 0x80) >> 2));

                if ((buf[3] & 0x60) == 0x20) {
                    door |= 0x80;
                } else if ((buf[3] & 0x60) == 0x60) {
                    door |= 0x40;
                }

                // if (mRudder) {// change temp
                if (mFrontDoor == 1) {
                    int temp1 = door;
                    door &= ~0x3;
                    door |= (((temp1 & 0x1) << 1) | ((temp1 & 0x2) >> 1));
                }
                if (mBackDoor == 1) {
                    int temp1 = door;
                    door &= ~0xC;
                    door |= (((temp1 & 0x4) << 1) | ((temp1 & 0x8) >> 1));
                }
                // }

                View v;
                v = mCar.findViewById(R.id.door_status_1);
                if ((door & 0x1) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

                v = mCar.findViewById(R.id.door_status_2);
                if ((door & 0x2) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

                v = mCar.findViewById(R.id.door_status_3);
                if ((door & 0x4) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

                v = mCar.findViewById(R.id.door_status_4);
                if ((door & 0x8) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

                v = mCar.findViewById(R.id.door_status_5);
                if ((door & 0x10) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

                v = mCar.findViewById(R.id.door_status_6);
                if ((door & 0x20) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

                v = mCar.findViewById(R.id.door_status_7);
                if ((door & 0x40) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

                v = mCar.findViewById(R.id.door_status_8);
                if ((door & 0x80) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }
                break;
            case 0x41: {
                switch (buf[2]) {
                    case 0x1:
                        checkCarView();
                        if (mCar == null) {
                            return;
                        }

                        boolean showback = false;
                        int string_id = 0;
                        boolean all_visible = false;
                        v = mCar.findViewById(R.id.width_lamp);
                        if ((buf[6] & 0x20) == 0) {
                            v.setVisibility(View.GONE);
                        } else {
                            if (string_id == 0 && v.getVisibility() == View.GONE) {
                                string_id = R.string.width_lamp;
                                if (mLightStringId == 0) {
                                    mLightStringId = string_id;
                                }
                            }

                            v.setVisibility(View.VISIBLE);
                            all_visible = true;
                            showback = true;
                        }

                        v = mCar.findViewById(R.id.the_low_beam_light);
                        if ((buf[6] & 0x80) == 0) {
                            v.setVisibility(View.GONE);
                        } else {
                            if (string_id == 0 && v.getVisibility() == View.GONE) {
                                string_id = R.string.the_low_beam_light;
                                if (mLightStringId == 0) {
                                    mLightStringId = string_id;
                                }
                            }
                            all_visible = true;
                            v.setVisibility(View.VISIBLE);
                            showback = true;
                        }

                        v = mCar.findViewById(R.id.toyaota_car_light_fog_rear);
                        if ((buf[7] & 0x4) == 0) {
                            v.setVisibility(View.GONE);
                        } else {
                            if (string_id == 0 && v.getVisibility() == View.GONE) {
                                string_id = R.string.rear_fog_lamp;
                                if (mLightStringId == 0) {
                                    mLightStringId = string_id;
                                }
                            }
                            all_visible = true;
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mCar.findViewById(R.id.toyaota_car_light_fog_front);
                        if ((buf[7] & 0x2) == 0) {
                            v.setVisibility(View.GONE);
                        } else {
                            if (string_id == 0 && v.getVisibility() == View.GONE) {
                                string_id = R.string.the_front_fog_lamp;
                                if (mLightStringId == 0) {
                                    mLightStringId = string_id;
                                }
                            }
                            all_visible = true;
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mCar.findViewById(R.id.toyaota_car_light_reverse);
                        if ((buf[7] & 0x80) == 0) {
                            v.setVisibility(View.GONE);
                        } else {
                            if (string_id == 0 && v.getVisibility() == View.GONE) {
                                string_id = R.string.reversing_lamp;
                            }
                            all_visible = true;
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mCar.findViewById(R.id.toyaota_car_light_brake);
                        if ((buf[7] & 0x40) == 0) {
                            v.setVisibility(View.GONE);
                        } else {
                            if (string_id == 0 && v.getVisibility() == View.GONE) {
                                string_id = R.string.brake_lamp;

                            }

                            all_visible = true;
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mCar.findViewById(R.id.high_beam);
                        if ((buf[6] & 0x40) == 0) {
                            v.setVisibility(View.GONE);
                        } else {
                            if (string_id == 0 && v.getVisibility() == View.GONE) {
                                string_id = R.string.high_beam;
                                mLightStringId = string_id;
                            }

                            all_visible = true;
                            v.setVisibility(View.VISIBLE);
                            showback = true;
                        }

                        if (string_id != 0) {
                            setText(R.id.toyota_car_light, getActivity().getString(string_id));
                        } else {
                            if (!all_visible) {
                                mLightStringId = 0;
                            }
                            if (mLightStringId == 0) {
                                setText(R.id.toyota_car_light, "");
                            } else {
                                setText(R.id.toyota_car_light, getActivity().getString(mLightStringId));
                            }
                        }

                        v = mCar.findViewById(R.id.toyaota_car_light_back_normal);
                        if (!showback) {
                            v.setVisibility(View.GONE);
                        } else {
                            v.setVisibility(View.VISIBLE);
                        }

                        flashLight(buf[7] & 0x38, false);
                        break;
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

                        index = ((buf[15] & 0xff) | ((buf[14] & 0xff) << 8));
                        s = String.format("%d.%02d km/h", index / 100, index % 100);
                        setPreference("am_runningspeed", s);

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
            case 0x22: {
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
                setPreference("instant", s);
            }
            break;
            case 0x21:
                if (buf[8] == 0x0) {
                    s = "--";

                    setPreference("averagespeed", s);
                    setPreference("traveltime", s);
                    setPreference("mileage", s);

                    setTextEx(R.id.toyota_car_info3, s, R.string.traveltime);
                } else {
                    String unit = "";
                    if (buf[8] == 0x1) {
                        unit = "MILE";
                    } else if (buf[8] == 0x2) {
                        unit = "KM";
                    }

                    index = ((buf[3] & 0xff) | ((buf[2] & 0xff) << 8));
                    s = String.format("%d.%d %s/H", index / 10, index % 10, unit);
                    setPreference("averagespeed", s);

                    index = ((buf[5] & 0xff) | ((buf[4] & 0xff) << 8));
                    s = String.format("%02d:%02d", index / 60, index % 60);
                    setPreference("traveltime", s);

                    setTextEx(R.id.toyota_car_info3, s, R.string.traveltime);

                    index = ((buf[7] & 0xff) | ((buf[6] & 0xff) << 8));
                    s = String.format("%d %s", index, unit);
                    setPreference("mileage", s);
                }

                break;
            case 0x1f:
                if ((buf[2] & 0x80) == 0) {
                    showPreference("hybrid", 0, null);
                    break;
                } else {
                    mHandlerHybird.removeMessages(1);
                    mHandlerHybird.sendEmptyMessageDelayed(1, 1000);
                    showPreference("hybrid", 1, null);
                }

                //			if (mBatteryView == null) {
                MyPreference2 p = (MyPreference2) findPreference("hybrid_content");
                if (p != null) {
                    mBatteryView = p.getMainView();
                }
                //			}

                if (mBatteryView == null) {
                    break;
                }

                showHyBird(buf[3]);

                int level = (buf[2] & 0xf);
                Drawable d;

                d = mBatteryView.findViewById(R.id.battery).getBackground();
                d.setLevel(level);

                break;
            case 0x25:

                if ((buf[2] & 0x80) == 0) {
                    showPreference("tpms", 0, null);
                    break;
                } else {
                    showPreference("tpms", 1, null);
                }

                // if (mTpmsView == null) {
                p = (MyPreference2) findPreference("tpms_content");
                if (p != null) {
                    mTpmsView = p.getMainView();
                    if (mTpmsView != null) {
                        View vv = mTpmsView.findViewById(R.id.tpms);
                        if (vv != null) {
                            vv.setVisibility(View.GONE);
                        }
                    }
                }
                // }

                if (mTpmsView == null) {
                    break;
                }

                if ((buf[2] & 0x4) == 0) {
                    mTpmsView.findViewById(R.id.type1).setVisibility(View.GONE);
                    mTpmsView.findViewById(R.id.type2).setVisibility(View.VISIBLE);
                } else {

                    mTpmsView.findViewById(R.id.type1).setVisibility(View.VISIBLE);
                    mTpmsView.findViewById(R.id.type2).setVisibility(View.GONE);
                }

                String unit = "";
                String t1 = "";
                String t2 = "";
                String t3 = "";
                String t4 = "";
                String t5 = "";
                int tpms;
                if ((buf[2] & 0x3) == 1) {

                    t1 = (buf[3] & 0xff) + unit;
                    t2 = (buf[4] & 0xff) + unit;
                    t3 = (buf[5] & 0xff) + unit;
                    t4 = (buf[6] & 0xff) + unit;
                    t5 = (buf[7] & 0xff) + unit;

                    unit = " PSI";

                } else if ((buf[2] & 0x3) == 2) {
                    unit = " KPA";

                    tpms = 25 * (buf[3] & 0xff);
                    t1 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));

                    tpms = 25 * (buf[4] & 0xff);
                    t2 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = 25 * (buf[5] & 0xff);
                    t3 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = 25 * (buf[6] & 0xff);
                    t4 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = 25 * (buf[7] & 0xff);
                    t5 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));

                } else {
                    unit = " BAR";

                    tpms = (buf[3] & 0xff);
                    t1 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = (buf[4] & 0xff);
                    t2 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = (buf[5] & 0xff);
                    t3 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = (buf[6] & 0xff);
                    t4 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));
                    tpms = (buf[7] & 0xff);
                    t5 = String.format("%d.%d", tpms / 10, (tpms >= 0) ? (tpms % 10) : (-tpms % 10));

                }

                if (buf[3] == 0xff) {
                    t1 = "--";
                }
                if (buf[4] == 0xff) {
                    t2 = "--";
                }
                if (buf[5] == 0xff) {
                    t3 = "--";
                }
                if (buf[6] == 0xff) {
                    t4 = "--";
                }

                if (buf[7] == 0xff || ((buf[2] & 0x20) == 0)) {
                    t5 = "--";
                }


                t1 += " " + unit;
                t2 += " " + unit;
                t3 += " " + unit;
                t4 += " " + unit;
                t5 += " " + unit;

                ((TextView) mTpmsView.findViewById(R.id.type11_info)).setText(t1);
                ((TextView) mTpmsView.findViewById(R.id.type12_info)).setText(t2);
                ((TextView) mTpmsView.findViewById(R.id.type21_info)).setText(t3);
                ((TextView) mTpmsView.findViewById(R.id.type22_info)).setText(t4);

                ((TextView) mTpmsView.findViewById(R.id.type30_info)).setText(t5);

                ((TextView) mTpmsView.findViewById(R.id.type91_info)).setText(t1);
                ((TextView) mTpmsView.findViewById(R.id.type92_info)).setText(t2);
                ((TextView) mTpmsView.findViewById(R.id.type93_info)).setText(t3);
                ((TextView) mTpmsView.findViewById(R.id.type94_info)).setText(t4);

                ((TextView) mTpmsView.findViewById(R.id.type95_info)).setText(t5);

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
