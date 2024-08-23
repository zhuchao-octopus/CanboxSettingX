package com.canboxsetting.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MachineConfig;
import com.common.utils.MyCmd;

public class VWInfoSimpleFragment extends PreferenceFragmentCompat {
    private static final String TAG = "GMInfoSimpleFragment";
    private View mMainView;
    private boolean mRudder = false;
    private int mFlashLight = 0;
    private int mFrontDoor = 0;
    private int mBackDoor = 0;
    private int mLightStringId = 0;
    private boolean mFlashing = true;
    private BroadcastReceiver mReceiver;    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mHandler.removeMessages(msg.what);
            // mHandler.sendEmptyMessageDelayed(msg.what, 700);

            sendCanboxInfo(msg.what);
            if (msg.what == 2) {
                mHandler.sendEmptyMessageDelayed(2, 400);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // addPreferencesFromResource(R.xml.gm_simple_info);

    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = inflater.getContext();
        mMainView = inflater.inflate(R.layout.vw_simple_info_new, container, false);

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
        return mMainView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        mHandler.removeMessages(2);
        mHandler.removeMessages(3);
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        mHandler.sendEmptyMessageDelayed(1, 1);
        mHandler.sendEmptyMessageDelayed(2, 200);
        mHandler.sendEmptyMessageDelayed(3, 400);
    }

    private void sendCanboxInfo(int d0) {
        byte[] buf = new byte[]{(byte) 0x90, 0x02, 0x41, (byte) d0};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

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

    private void updateView(byte[] buf) {

        TextView tv;
        View v;
        String temp = "";
        int i;
        int door;
        switch (buf[0]) {

            case 0x12:

                //			i = ((int) buf[10] & 0xff) * 256 + ((int) buf[11] & 0xff);
                temp = String.format("%d.%02d V", buf[10] & 0xff, buf[11] & 0xff);

                ((TextView) mMainView.findViewById(R.id.am_cellvoltage)).setText(temp);

                if ((buf[8] & 0x40) == 0) {
                    ((TextView) mMainView.findViewById(R.id.am_cellvoltage)).setTextColor(Color.WHITE);
                } else {
                    ((TextView) mMainView.findViewById(R.id.am_cellvoltage)).setTextColor(Color.RED);
                }

                ((TextView) mMainView.findViewById(R.id.beoilmass)).setText(((int) buf[9] & 0xff) + "L");

                if ((buf[8] & 0x80) == 0) {
                    ((TextView) mMainView.findViewById(R.id.beoilmass)).setTextColor(Color.WHITE);
                } else {
                    ((TextView) mMainView.findViewById(R.id.beoilmass)).setTextColor(Color.RED);
                }

                if ((buf[8] & 0x20) == 0) {
                    ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setText(R.string.am_buttoned);
                    ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setTextColor(Color.WHITE);
                } else {
                    ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setText(R.string.am_nobuttoned);
                    ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setTextColor(Color.RED);
                }
                if ((buf[8] & 0x10) == 0) {
                    ((TextView) mMainView.findViewById(R.id.cleaning_liquid_alarm)).setTextColor(Color.WHITE);
                    ((TextView) mMainView.findViewById(R.id.cleaning_liquid_alarm)).setText(R.string.am_normal);
                } else {
                    ((TextView) mMainView.findViewById(R.id.cleaning_liquid_alarm)).setTextColor(Color.RED);
                    ((TextView) mMainView.findViewById(R.id.cleaning_liquid_alarm)).setText(R.string.am_low);
                }
                break;
            case 0x72:
                if ((buf[2] & 0x08) != 0) {
                    ((TextView) mMainView.findViewById(R.id.am_handbrake)).setText(R.string.am_stop);
                    ((TextView) mMainView.findViewById(R.id.am_handbrake)).setTextColor(Color.RED);
                } else {
                    ((TextView) mMainView.findViewById(R.id.am_handbrake)).setText(R.string.am_nostop);
                    ((TextView) mMainView.findViewById(R.id.am_handbrake)).setTextColor(Color.WHITE);
                }
                break;
            case 0x13:
                i = (buf[4] & 0xff) | ((buf[3] & 0xff) << 8) | ((buf[2] & 0xff) << 16);
                temp = i + " KM";

                ((TextView) mMainView.findViewById(R.id.am_mileage)).setText(temp);


                temp = (((int) buf[10] & 0xff) * 256 + ((int) buf[11] & 0xff)) + "RPM";
                ((TextView) mMainView.findViewById(R.id.am_enginespeed)).setText(temp);

                break;
            case 0x73:
                i = (((buf[8] & 0xff)) / 2) - 40;
                temp = String.format("%d °C", i);

                ((TextView) mMainView.findViewById(R.id.outt)).setText(temp);

                door = (byte) (buf[9] & 0xfc);
                v = mMainView.findViewById(R.id.door_status_1);
                if ((door & 0x40) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

                v = mMainView.findViewById(R.id.door_status_2);
                if ((door & 0x80) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

                v = mMainView.findViewById(R.id.door_status_3);
                if ((door & 0x20) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

                v = mMainView.findViewById(R.id.door_status_4);
                if ((door & 0x10) == 0) {
                    v.setVisibility(View.INVISIBLE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }


                tv = (TextView) mMainView.findViewById(R.id.trunkdoor);

                v = mMainView.findViewById(R.id.door_status_5);
                if ((door & 0x08) == 0) {
                    v.setVisibility(View.INVISIBLE);
                    if (tv != null) {
                        tv.setText(R.string.close);
                    }
                } else {
                    v.setVisibility(View.VISIBLE);
                    if (tv != null) {
                        tv.setText(R.string.open);
                    }
                }
                break;

            //simple
            case 0x41:
                switch (buf[2]) {
                    case 0x2:
                        temp = (((int) buf[3] & 0xff) * 256 + ((int) buf[4] & 0xff)) + "RPM";
                        ((TextView) mMainView.findViewById(R.id.am_enginespeed)).setText(temp);

                        i = ((int) buf[5] & 0xff) * 256 + ((int) buf[6] & 0xff);
                        temp = String.format("%d.%02d km/h", i / 100, i % 100);

                        if (((TextView) mMainView.findViewById(R.id.am_runningspeed)) != null) {
                            ((TextView) mMainView.findViewById(R.id.am_runningspeed)).setText(temp);
                        }

                        i = ((int) buf[7] & 0xff) * 256 + ((int) buf[8] & 0xff);
                        temp = String.format("%d.%02d V", i / 100, i % 100);

                        ((TextView) mMainView.findViewById(R.id.am_cellvoltage)).setText(temp);

                        // i = ((int) buf[9] & 0xff) * 256 + ((int) buf[10] & 0xff);
                        i = (short) (((buf[9] & 0xff) << 8) | (buf[10] & 0xff));
                        temp = String.format("%d.%01d °C", i / 10, i % 10);

                        ((TextView) mMainView.findViewById(R.id.outt)).setText(temp);

                        i = ((int) buf[11] & 0xff) * 0x10000 + ((int) buf[12] & 0xff) * 256 + ((int) buf[13] & 0xff);
                        temp = i + " KM";

                        ((TextView) mMainView.findViewById(R.id.am_mileage)).setText(temp);

                        ((TextView) mMainView.findViewById(R.id.beoilmass)).setText(((int) buf[14] & 0xff) + "L");
                        break;

                    case 0x1:
                        if ((buf[3] & 0x80) == 0) {
                            ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setText(R.string.am_buttoned);
                            ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setTextColor(Color.WHITE);
                        } else {
                            ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setText(R.string.am_nobuttoned);
                            ((TextView) mMainView.findViewById(R.id.am_safetybelt)).setTextColor(Color.RED);
                        }

                        if ((buf[3] & 0x40) == 0) {
                            ((TextView) mMainView.findViewById(R.id.cleaning_liquid_alarm)).setTextColor(Color.WHITE);
                            ((TextView) mMainView.findViewById(R.id.cleaning_liquid_alarm)).setText(R.string.am_normal);
                        } else {
                            ((TextView) mMainView.findViewById(R.id.cleaning_liquid_alarm)).setTextColor(Color.RED);
                            ((TextView) mMainView.findViewById(R.id.cleaning_liquid_alarm)).setText(R.string.am_low);
                        }

                        if ((buf[3] & 0x20) != 0) {
                            ((TextView) mMainView.findViewById(R.id.am_handbrake)).setText(R.string.am_stop);
                            ((TextView) mMainView.findViewById(R.id.am_handbrake)).setTextColor(Color.RED);
                        } else {
                            ((TextView) mMainView.findViewById(R.id.am_handbrake)).setText(R.string.am_nostop);
                            ((TextView) mMainView.findViewById(R.id.am_handbrake)).setTextColor(Color.WHITE);
                        }

                        // door
                        door = (byte) (buf[3] & 0x1F);

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
                        v = mMainView.findViewById(R.id.door_status_1);
                        if ((door & 0x1) == 0) {
                            v.setVisibility(View.INVISIBLE);
                        } else {
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mMainView.findViewById(R.id.door_status_2);
                        if ((door & 0x2) == 0) {
                            v.setVisibility(View.INVISIBLE);
                        } else {
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mMainView.findViewById(R.id.door_status_3);
                        if ((door & 0x4) == 0) {
                            v.setVisibility(View.INVISIBLE);
                        } else {
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mMainView.findViewById(R.id.door_status_4);
                        if ((door & 0x8) == 0) {
                            v.setVisibility(View.INVISIBLE);
                        } else {
                            v.setVisibility(View.VISIBLE);
                        }

                        tv = (TextView) mMainView.findViewById(R.id.trunkdoor);

                        v = mMainView.findViewById(R.id.door_status_5);
                        if ((door & 0x10) == 0) {
                            v.setVisibility(View.INVISIBLE);
                            if (tv != null) {
                                tv.setText(R.string.close);
                            }
                        } else {
                            v.setVisibility(View.VISIBLE);
                            if (tv != null) {
                                tv.setText(R.string.open);
                            }
                        }

                        v = mMainView.findViewById(R.id.door_status_6);
                        if ((door & 0x20) == 0) {
                            v.setVisibility(View.INVISIBLE);
                        } else {
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mMainView.findViewById(R.id.door_status_7);
                        if ((door & 0x40) == 0) {
                            v.setVisibility(View.INVISIBLE);
                        } else {
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mMainView.findViewById(R.id.door_status_8);
                        if ((door & 0x80) == 0) {
                            v.setVisibility(View.INVISIBLE);
                        } else {
                            v.setVisibility(View.VISIBLE);
                        }

                        // light

                        boolean showback = false;
                        int string_id = 0;
                        boolean all_visible = false;
                        v = mMainView.findViewById(R.id.width_lamp);
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

                        v = mMainView.findViewById(R.id.the_low_beam_light);
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

                        v = mMainView.findViewById(R.id.toyaota_car_light_fog_rear);
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

                        v = mMainView.findViewById(R.id.toyaota_car_light_fog_front);
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

                        v = mMainView.findViewById(R.id.toyaota_car_light_reverse);
                        if ((buf[7] & 0x80) == 0) {
                            v.setVisibility(View.GONE);
                        } else {
                            if (string_id == 0 && v.getVisibility() == View.GONE) {
                                string_id = R.string.reversing_lamp;
                            }
                            all_visible = true;
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mMainView.findViewById(R.id.toyaota_car_light_brake);
                        if ((buf[7] & 0x40) == 0) {
                            v.setVisibility(View.GONE);
                        } else {
                            if (string_id == 0 && v.getVisibility() == View.GONE) {
                                string_id = R.string.brake_lamp;

                            }

                            all_visible = true;
                            v.setVisibility(View.VISIBLE);
                        }

                        v = mMainView.findViewById(R.id.high_beam);
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

                        v = mMainView.findViewById(R.id.toyaota_car_light_back_normal);
                        if (!showback) {
                            v.setVisibility(View.GONE);
                        } else {
                            v.setVisibility(View.VISIBLE);
                        }

                        flashLight(buf[7] & 0x38, false);

                        break;
                    case 0x3:
                        if ((buf[3] & 0x80) == 0) {
                            ((TextView) mMainView.findViewById(R.id.beoilmass)).setTextColor(Color.WHITE);
                        } else {
                            ((TextView) mMainView.findViewById(R.id.beoilmass)).setTextColor(Color.RED);
                        }
                        if ((buf[3] & 0x40) == 0) {
                            ((TextView) mMainView.findViewById(R.id.am_cellvoltage)).setTextColor(Color.WHITE);
                        } else {
                            ((TextView) mMainView.findViewById(R.id.am_cellvoltage)).setTextColor(Color.RED);
                        }
                        break;
                }
        }

    }

    private void setText(int id, String s) {

        if (mMainView != null) {
            ((TextView) mMainView.findViewById(id)).setText(s);
        }
    }

    private void setTextEx(int id, String s, int string_id) {
        if (string_id != 0) {
            s = getActivity().getString(string_id) + "\n" + s;
        }
        setText(id, s);
    }    private Handler mHandlerFlash = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            flashLight(mFlashLight, true);
        }
    };

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
        v = mMainView.findViewById(R.id.toyaota_car_light_right);
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

        v = mMainView.findViewById(R.id.toyaota_car_light_left);
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
