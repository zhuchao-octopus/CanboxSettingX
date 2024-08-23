package com.canboxsetting.info;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.canboxsetting.R.string;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.NodeDrivingData;
import com.common.utils.Util;
import com.common.utils.UtilSystem;
import com.common.view.MyPopDialog;

import java.util.Locale;

public class VWMQBInfoRaiseFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "VWMQBInfoRaiseFragment";
    private static final NodeDrivingData[] NODES_DRIVINGDATA = {new NodeDrivingData(R.string.since_start), new NodeDrivingData(R.string.long_term), new NodeDrivingData(R.string.since_refuelling)};
    private static final int[] BUTTON_ON_CLICK = new int[]{R.id.setting, R.id.views, R.id.set,};
    private static final int[] POP_LIST = new int[]{R.string.driving_data, R.string.conv_consumers, R.string.vehicle_status, R.string.energy_flow_view};
    ArrayAdapter<String> mAdapter;
    ListView mLv;
    private View mMainView;
    private MyPopDialog mDialog;
    private View.OnClickListener mOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("ccfk", "" + v.getId());
            int id = v.getId();
            if (id == R.id.setting) {
                UtilSystem.doRunActivity(getActivity(), "com.canboxsetting", "com.canboxsetting.MainActivity");
            } else if (id == R.id.views) {
                mDialog.show();
            }
        }

        ;
    };
    private int mVehiclePage = 0;
    private View.OnClickListener mOnClickVehicleStatus = new View.OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.bt_left) {
                mVehiclePage = (mVehiclePage + 1) % 2;
                showVehicletatus();
            } else if (id == R.id.bt_right) {
                mVehiclePage = (mVehiclePage + 1) % 2;
                showVehicletatus();
            } else if (id == R.id.set) {
                showTPMSSetDialog();
            } else if (id == R.id.start_stop) {
                showStopStartStatusDialog();
            }
        }

        ;
    };
    private int mDrivingDataPage = 0;
    private View.OnClickListener mOnClickDrivingData = new View.OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.bt_left) {
                mDrivingDataPage = (mDrivingDataPage + 1) % NODES_DRIVINGDATA.length;
                showDrivingData();
            } else if (id == R.id.bt_right) {
                mDrivingDataPage = (mDrivingDataPage + NODES_DRIVINGDATA.length - 1) % NODES_DRIVINGDATA.length;
                showDrivingData();
            }
        }

        ;
    };
    private int mCurPage = -1;
    private Handler mHandlerDialog = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showUI(POP_LIST[msg.what]);
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // mHandler.removeMessages(msg.what);
            // mHandler.sendEmptyMessageDelayed(msg.what, 700);
            sendCanboxInfo(0x90, (msg.what & 0xff00) >> 8, msg.what & 0xff);
        }
    };
    private View mTpmsView;
    private byte mHybrid;
    private View mBatteryView;
    private BroadcastReceiver mReceiver;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.vw_raise_info, container, false);

        initPresentationUI();

        showUI(R.string.driving_data);

        mBatteryView = mMainView.findViewById(R.id.vehicle_hybrid_power);
        return mMainView;
    }

    private void showStopStartStatusDialog() {

        Dialog d = new Dialog(getActivity());
        d.setContentView(R.layout.pop_view2);

        ((TextView) d.findViewById(R.id.pop_text)).setText(R.string.start_stop_status);
        mLv = (ListView) d.findViewById(R.id.pop_views);

        mLv.setAdapter(mAdapter);
        Window dialogWindow = d.getWindow();
        dialogWindow.setBackgroundDrawable(null);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        d.show();
    }

    private void showTPMSSetDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                sendCanboxInfo(0xc6, 0x22, 1);
            }
        });

        alertDialogBuilder.setNegativeButton(android.R.string.cancel, null);
        alertDialogBuilder.setTitle(R.string.tire_pressure_monitoring_display);
        alertDialogBuilder.setMessage(R.string.tpms_confirm);
        alertDialogBuilder.create().show();

    }

    private void showVehicletatus() {
        int id = R.string.vehicle_status;
        switch (mVehiclePage) {
            case 1:
                id = R.string.tire_pressure_monitoring_display;
                setVisible(R.id.vehicle_tmps, 1);
                setVisible(R.id.vehicle_reports, 0);
                break;
            case 0:
                id = R.string.vehicle_status;
                setVisible(R.id.vehicle_reports, 1);
                setVisible(R.id.vehicle_tmps, 0);
                break;
        }
        setTextViewStringEx(mMainView.findViewById(R.id.vehicle_status), R.id.title, id);

    }

    private void showDrivingData() {

        NodeDrivingData n = NODES_DRIVINGDATA[mDrivingDataPage];

        setTextViewStringEx(mMainView.findViewById(R.id.driving_data), R.id.title, n.mId);

        setTextViewStringEx(mMainView.findViewById(R.id.driving_data), R.id.avg_speed, n.mAVSpeed);
        setTextViewStringEx(mMainView.findViewById(R.id.driving_data), R.id.distance, n.mDistance);
        setTextViewStringEx(mMainView.findViewById(R.id.driving_data), R.id.travelling_time, n.mTime);
        setTextViewStringEx(mMainView.findViewById(R.id.driving_data), R.id.avg_consumption, n.mOilDistance);
    }

    private void setVisible(int id, int value) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setVisibility(value != 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void initPresentationUI() {
        for (int i : BUTTON_ON_CLICK) {
            View v = mMainView.findViewById(i);
            if (v != null) {
                v.setOnClickListener(mOnClick);
            }
        }

        mMainView.findViewById(R.id.driving_data).findViewById(R.id.bt_left).setOnClickListener(mOnClickDrivingData);
        mMainView.findViewById(R.id.driving_data).findViewById(R.id.bt_right).setOnClickListener(mOnClickDrivingData);
        mMainView.findViewById(R.id.vehicle_status).findViewById(R.id.bt_left).setOnClickListener(mOnClickVehicleStatus);
        mMainView.findViewById(R.id.vehicle_status).findViewById(R.id.bt_right).setOnClickListener(mOnClickVehicleStatus);
        mMainView.findViewById(R.id.vehicle_status).findViewById(R.id.start_stop).setOnClickListener(mOnClickVehicleStatus);
        mMainView.findViewById(R.id.vehicle_status).findViewById(R.id.set).setOnClickListener(mOnClickVehicleStatus);
        mDialog = new MyPopDialog(getActivity());

        mDialog.updateList(POP_LIST);
        mDialog.setCallbackHandler(mHandlerDialog);

        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

        // setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void showUI(int id) {
        if (mCurPage != id) {
            mCurPage = id;

            setVisible(R.id.driving_data, 0);
            setVisible(R.id.conv_consumers, 0);
            setVisible(R.id.vehicle_status, 0);
            setVisible(R.id.vehicle_hybrid_power, 0);
            if (id == string.driving_data) {
                setVisible(R.id.driving_data, 1);
            } else if (id == string.conv_consumers) {
                setVisible(R.id.conv_consumers, 1);
            } else if (id == string.vehicle_status) {
                setVisible(R.id.vehicle_status, 1);
            } else if (id == string.energy_flow_view) {
                setVisible(R.id.vehicle_hybrid_power, 1);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();

        // sendCanboxInfo(0x50, 0x1A);

        // mHandler.sendEmptyMessageDelayed(0x50ff, 0);
        mHandler.sendEmptyMessageDelayed(0x5010, 100 * 1);
        mHandler.sendEmptyMessageDelayed(0x5020, 100 * 2);
        mHandler.sendEmptyMessageDelayed(0x5021, 100 * 3);
        mHandler.sendEmptyMessageDelayed(0x5022, 100 * 4);

        mHandler.sendEmptyMessageDelayed(0x5030, 100 * 5);
        mHandler.sendEmptyMessageDelayed(0x5031, 100 * 6);
        mHandler.sendEmptyMessageDelayed(0x5032, 100 * 7);

        mHandler.sendEmptyMessageDelayed(0x5040, 100 * 8);
        mHandler.sendEmptyMessageDelayed(0x5041, 100 * 9);
        mHandler.sendEmptyMessageDelayed(0x5042, 100 * 10);

        mHandler.sendEmptyMessageDelayed(0x5050, 100 * 11);
        mHandler.sendEmptyMessageDelayed(0x5051, 100 * 12);
        mHandler.sendEmptyMessageDelayed(0x5052, 100 * 13);

        // mHandler.sendEmptyMessageDelayed(0x5060, 100 * 14);
        // mHandler.sendEmptyMessageDelayed(0x5061, 100 * 15);

        // mHandler.sendEmptyMessageDelayed(0x6310, 100 * 16);
        // mHandler.sendEmptyMessageDelayed(0x6311, 100 * 17);
        //
        // mHandler.sendEmptyMessageDelayed(0x6320, 100 * 18);
        // mHandler.sendEmptyMessageDelayed(0x6321, 100 * 19);
        //
        // mHandler.sendEmptyMessageDelayed(0x6300, 100 * 20);

    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x02, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    public boolean onPreferenceClick(Preference arg0) {

        try {
            String key = arg0.getKey();
            if ("tpms".equals(key)) {
                mTpmsView = null;
                sendCanboxInfo(0x90, 0x65, 0);
            }
        } catch (Exception e) {

        }

        return false;
    }

    private void setTpmsText(int id, int text) {
        switch (text) {
            case 1:
                text = R.string.tpms_low;
                break;
            case 2:
                text = R.string.tpms_hi;
                break;
            default:
                text = R.string.am_normal;
                break;
        }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setText(text);
    }

    private void setTextViewString(int id, String s) {
        setTextViewStringEx(mMainView, id, s);
    }

    private void setTextViewString(int id, int s) {
        setTextViewStringEx(mMainView, id, s);
    }

    private void setTextViewStringEx(View m, int id, String s) {
        View v = m.findViewById(id);
        if (v instanceof TextView) {
            ((TextView) v).setText(s);
        }
    }

    private void setTextViewStringEx(View m, int id, int s) {
        View v = m.findViewById(id);
        if (v instanceof TextView) {
            ((TextView) v).setText(s);
        }
    }

    private void setMileage(int value, String s) {
        setTextViewString(R.id.range, s);

        View v = mMainView.findViewById(R.id.vehicle);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        View v2 = mMainView.findViewById(R.id.vehicle_parent);
        int rang = v2.getWidth() - v.getWidth();

        int pos = 0;
        if (value <= 500) {
            pos = (((500 - value) * rang / 500));
        }
        // Log.d("ffck", rang + ":" + pos + ":" + value);
        lp.setMarginStart(pos);

        v.setLayoutParams(lp);
        v.invalidate();
    }

    private int getConvMsgID(int index) {
        switch (index) {
            case 0:
                index = R.id.conv_consumers_msg1;
                break;
            case 1:
                index = R.id.conv_consumers_msg2;
                break;
            case 2:
                index = R.id.conv_consumers_msg3;
                break;
        }
        return index;
    }

    private int getConvStringID(int index) {
        switch (index) {
            case 1:
                index = R.string.conv_tips_0;
                break;
            case 2:
                index = R.string.conv_tips_1;
                break;
            case 3:
                index = R.string.conv_tips_2;
                break;
            case 4:
                index = R.string.conv_tips_3;
                break;
            case 5:
                index = R.string.conv_tips_4;
                break;
            case 6:
                index = R.string.conv_tips_5;
                break;
            case 7:
                index = R.string.conv_tips_6;
                break;
            case 8:
                index = R.string.conv_tips_7;
                break;
            case 9:
                index = R.string.conv_tips_8;
                break;
            case 10:
                index = R.string.conv_tips_9;
                break;
            case 11:
                index = R.string.conv_tips_10;
                break;
            case 12:
                index = R.string.conv_tips_11;
                break;
            case 13:
                index = R.string.conv_tips_12;
                break;
            case 14:
                index = R.string.conv_tips_13;
                break;
            case 15:
                index = R.string.conv_tips_14;
                break;
            case 16:
                index = R.string.conv_tips_15;
                break;
            case 17:
                index = R.string.conv_tips_16;
                break;
            case 18:
                index = R.string.conv_tips_17;
                break;
            default:
                index = 0;
                break;
        }
        return index;
    }

    private int getStartStopStringID(int index) {
        switch (index) {
            case 0:
                index = R.string.tips_0;
                break;
            case 1:
                index = R.string.tips_1;
                break;
            case 2:
                index = R.string.tips_2;
                break;
            case 3:
                index = R.string.tips_3;
                break;
            case 4:
                index = R.string.tips_4;
                break;
            case 5:
                index = R.string.tips_5;
                break;
            case 6:
                index = R.string.tips_6;
                break;
            case 7:
                index = R.string.tips_7;
                break;
            case 8:
                index = R.string.tips_8;
                break;
            case 9:
                index = R.string.tips_9;
                break;
            case 10:
                index = R.string.tips_10;
                break;
            case 11:
                index = R.string.tips_11;
                break;
            case 12:
                index = R.string.tips_12;
                break;
            case 13:
                index = R.string.tips_13;
                break;
            case 14:
                index = R.string.tips_14;
                break;
            case 15:
                index = R.string.tips_15;
                break;
            case 16:
                index = R.string.tips_16;
                break;
            case 17:
                index = R.string.tips_17;
                break;
            case 18:
                index = R.string.tips_18;
                break;
            case 19:
                index = R.string.tips_19;
                break;
            case 20:
                index = R.string.tips_20;
                break;
            case 21:
                index = R.string.tips_21;
                break;
            case 22:
                index = R.string.tips_22;
                break;
            case 23:
                index = R.string.tips_23;
                break;
            case 24:
                index = R.string.tips_24;
                break;
            case 25:
                index = R.string.tips_25;
                break;
            case 26:
                index = R.string.tips_26;
                break;
            case 27:
                index = R.string.tips_27;
                break;
            case 28:
                index = R.string.tips_28;
                break;
            case 29:
                index = R.string.tips_29;
                break;
            case 30:
                index = R.string.tips_30;
                break;
            case 31:
                index = R.string.tips_31;
                break;
            case 32:
                index = R.string.tips_32;
                break;
            case 33:
                index = R.string.tips_33;
                break;
            case 34:
                index = R.string.tips_34;
                break;
            case 35:
                index = R.string.tips_35;
                break;
            case 36:
                index = R.string.tips_36;
                break;
            case 37:
                index = R.string.tips_37;
                break;
            default:
                index = 0;
                break;
        }
        return index;
    }

    private void updateStartStopMsg(byte[] buf) {
        int id = 0;

        mAdapter.clear();
        if (buf[2] == 0) {
            if (buf[9] != 0) {
                id = R.string.tips_34;
            } else {
                id = R.string.tips_35;
            }
            mAdapter.add(getString(id));
        } else {
            for (int i = 0; i < buf[2] && i < 6; ++i) {
                mAdapter.add(getString(getStartStopStringID(buf[3 + i])));
            }
        }
        mAdapter.notifyDataSetChanged();

        mLv.setAdapter(mAdapter);
    }

    private void updateConvComsumers(byte[] buf) {
        int i = 0;

        for (; i < 3; ++i) {
            int id = getConvStringID(buf[3 + i]);
            if (i < buf[2] && id != 0) {

                setTextViewString(getConvMsgID(i), id);
            } else {

                setTextViewString(getConvMsgID(i), "");
            }
        }
    }

    private void updateView(byte[] buf) {
        int index;
        String s = "";
        switch (buf[0]) {
            case 0x52:
                byte b = 0;
                switch ((buf[6] & 0x1c) >> 2) {
                    case 1:
                        b = 0x12;
                        break;
                    case 2:
                        b = 0x08;
                        break;
                    case 3:
                        b = 0x21;
                        break;
                    case 4:
                        b = 0x1A;
                        break;
                }

                switch ((buf[6] & 0xc0) >> 6) {
                    case 1:
                        b |= 0x01;
                        break;
                    case 2:
                        b |= 0x10;
                        break;
                }
                showHyBird(b);

                int level = (buf[7] & 0xff);

                level = ((level * 100) / 11) / 100;
                Drawable d;

                d = mBatteryView.findViewById(R.id.battery).getBackground();
                d.setLevel(level);

                break;
            case 0x62:
                updateConvComsumers(buf);
                break;
            case 0x60:
                updateStartStopMsg(buf);
                break;
            case 0x16:
                index = ((buf[2] & 0xff) | ((buf[3] & 0xff) << 8));
                index /= 16;
                if ((buf[4] & 0x1) == 0) {
                    s = index + " km/h";
                } else {
                    s = index + " mph";
                }
                setPreference("speed", s);
                break;
            case 0x50:
                switch (buf[2]) {
                    case 0x10:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " km";
                        } else {
                            s = index + " MI";
                        }
                        setMileage(index, s);
                        // setPreference("mileage", s);
                        break;
                    case 0x20:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8) | ((buf[6] & 0xff) << 16) | ((buf[7] & 0xff) << 24)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " km";
                        } else {
                            s = index + " MI";
                        }
                        NODES_DRIVINGDATA[0].mDistance = s;
                        // setPreference("since_start", s);
                        break;
                    case 0x21:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8) | ((buf[6] & 0xff) << 16) | ((buf[7] & 0xff) << 24)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " km";
                        } else {
                            s = index + " MI";
                        }
                        NODES_DRIVINGDATA[2].mDistance = s;
                        // setPreference("since_refueling", s);
                        break;
                    case 0x22:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8) | ((buf[6] & 0xff) << 16) | ((buf[7] & 0xff) << 24)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " km";
                        } else {
                            s = index + " MI";
                        }
                        NODES_DRIVINGDATA[1].mDistance = s;
                        // setPreference("long_term", s);
                        break;

                    case 0x30:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x3) == 0) {
                            s = index + " L/100KM";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " km/l";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " MPG(UK)";
                        } else {
                            s = index + " MPG(US)";
                        }
                        NODES_DRIVINGDATA[0].mOilDistance = s;
                        // setPreference("avg_start", s);
                        break;
                    case 0x31:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x3) == 0) {
                            s = index + " L/100KM";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " km/l";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " MPG(UK)";
                        } else {
                            s = index + " MPG(US)";
                        }
                        NODES_DRIVINGDATA[2].mOilDistance = s;
                        // setPreference("avrefueling", s);
                        break;
                    case 0x32:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x3) == 0) {
                            s = index + " L/100KM";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " km/l";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " MPG(UK)";
                        } else {
                            s = index + " MPG(US)";
                        }
                        NODES_DRIVINGDATA[1].mOilDistance = s;
                        // setPreference("avlong_term", s);
                        break;
                    case 0x40:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " km/h";
                        } else {
                            s = index + " MPH";
                        }
                        NODES_DRIVINGDATA[0].mAVSpeed = s;
                        // setPreference("avspeed_start", s);
                        break;
                    case 0x41:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " km/h";
                        } else {
                            s = index + " MPH";
                        }
                        NODES_DRIVINGDATA[2].mAVSpeed = s;
                        // setPreference("speeds_refueling", s);
                        break;
                    case 0x42:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x1) == 0) {
                            s = index + " km/h";
                        } else {
                            s = index + " MPH";
                        }
                        NODES_DRIVINGDATA[1].mAVSpeed = s;
                        // setPreference("speed_long", s);
                        break;
                    case 0x50:
                        index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16));
                        s = String.format(Locale.ENGLISH, "%02d:%02d h", index / 60, index % 60);
                        // s = index + " MIN";
                        NODES_DRIVINGDATA[0].mTime = s;
                        // setPreference("travelling_time", s);
                        break;
                    case 0x51:
                        index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16));
                        s = String.format(Locale.ENGLISH, "%02d:%02d h", index / 60, index % 60);
                        NODES_DRIVINGDATA[2].mTime = s;
                        // setPreference("ttsr", s);
                        break;
                    case 0x52:
                        index = ((buf[3] & 0xff) | ((buf[4] & 0xff) << 8) | ((buf[5] & 0xff) << 16));
                        s = String.format(Locale.ENGLISH, "%02d:%02d h", index / 60, index % 60);
                        NODES_DRIVINGDATA[1].mTime = s;
                        // setPreference("tt_long_term", s);
                        break;

                    case 0x60:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
                        ProgressBar sb = (ProgressBar) mMainView.findViewById(R.id.progressbar);
                        if ((buf[3] & 0x1) == 0) {
                            s = " gal/h";
                            setTextViewString(R.id.progress_center, "1/8");
                            setTextViewString(R.id.progress_max, "1/4");
                            sb.setMax(250);

                        } else {
                            s = " l/h";
                            setTextViewString(R.id.progress_center, "1/2");
                            setTextViewString(R.id.progress_max, "1");
                            sb.setMax(1000);
                        }
                        // setPreference("conv_consumers", s);
                        sb.setProgress(index);
                        setTextViewString(R.id.unit, s);
                        break;

                    case 0x61:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) / 10;
                        if ((buf[3] & 0x3) == 0) {
                            s = index + " L/100KM";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " km/l";
                        } else if ((buf[3] & 0x3) == 1) {
                            s = index + " MPG(UK)";
                        } else {
                            s = index + " MPG(US)";
                        }
                        setPreference("instant", s);
                        break;
                }
                showDrivingData();
                break;

            case 0x63:

                switch (buf[2]) {
                    case 0x0:
                        byte[] name = new byte[buf.length - 4];
                        Util.byteArrayCopy(name, buf, 0, 3, name.length);
                        try {
                            s = new String(name, "GBK");
                        } catch (Exception e) {

                        }
                        setPreference("vehicle", s);
                        break;
                    case 0x10:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
                        if ((buf[3] & 0xf) == 0) {
                            s = "------";
                        } else if ((buf[3] & 0xf) == 1) {
                            s = index + getString(R.string.days);
                        } else {
                            s = getString(R.string.be_overdue) + index + getString(R.string.days);
                        }
                        setPreference("vi_days", s);
                        break;

                    case 0x11:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) * 100;

                        if ((buf[3] & 0xf0) == 0) {
                            s = " km";
                        } else {
                            s = " MI";
                        }

                        if ((buf[3] & 0xf) == 0) {
                            s = "------";
                        } else if ((buf[3] & 0xf) == 1) {
                            s = index + s;
                        } else {
                            s = getString(R.string.be_overdue) + index + s;
                        }
                        setPreference("vi_distance", s);
                        break;

                    case 0x20:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8));
                        if ((buf[3] & 0xf) == 0) {
                            s = "------";
                        } else if ((buf[3] & 0xf) == 1) {
                            s = index + getString(R.string.days);
                        } else {
                            s = getString(R.string.be_overdue) + index + getString(R.string.days);
                        }
                        setPreference("oil_days", s);
                        break;

                    case 0x21:
                        index = ((buf[4] & 0xff) | ((buf[5] & 0xff) << 8)) * 100;

                        if ((buf[3] & 0xf0) == 0) {
                            s = " km";
                        } else {
                            s = " MI";
                        }

                        if ((buf[3] & 0xf) == 0) {
                            s = "------";
                        } else if ((buf[3] & 0xf) == 1) {
                            s = index + s;
                        } else {
                            s = getString(R.string.be_overdue) + index + s;
                        }
                        setPreference("oil_change", s);
                        break;
                }
                break;
        }
    }

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
