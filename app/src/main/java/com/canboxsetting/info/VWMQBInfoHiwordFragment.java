package com.canboxsetting.info;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;

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

import com.canboxsetting.R;
import com.canboxsetting.R.string;
import com.common.util.BroadcastUtil;
import com.common.util.MyCmd;
import com.common.util.NodeDrivingHiworldData;
import com.common.util.UtilSystem;
import com.common.view.MyPopDialog;

import java.util.Locale;

public class VWMQBInfoHiwordFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "VWMQBInfoHiwordFragment";

    private View mMainView;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.vw_hiworld_info, container, false);

        initPresentationUI();

        showUI(R.string.driving_data);

        return mMainView;
    }

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

    private static final NodeDrivingHiworldData[] NODES_DRIVINGDATA = {new NodeDrivingHiworldData(R.string.since_start), new NodeDrivingHiworldData(R.string.long_term), new NodeDrivingHiworldData(R.string.since_refuelling)};

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
    ArrayAdapter<String> mAdapter;
    ListView mLv;

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

    private int mVehiclePage = 0;

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

    private int mDrivingDataPage = 0;

    private void showDrivingData() {

        NodeDrivingHiworldData n = NODES_DRIVINGDATA[mDrivingDataPage];

        setTextViewStringEx(mMainView.findViewById(R.id.driving_data), R.id.title, n.mId);

        setTextViewStringEx(mMainView.findViewById(R.id.driving_data), R.id.avg_speed, n.mAverageSpeed);
        setTextViewStringEx(mMainView.findViewById(R.id.driving_data), R.id.distance, n.mDrivenDistance);
        setTextViewStringEx(mMainView.findViewById(R.id.driving_data), R.id.travelling_time, n.mDrivenTime);
        setTextViewStringEx(mMainView.findViewById(R.id.driving_data), R.id.avg_consumption, n.mOilConsumption);


        //续航里程

    }

    private void setVisible(int id, int value) {
        View v = mMainView.findViewById(id);
        if (v != null) {
            v.setVisibility(value != 0 ? View.VISIBLE : View.GONE);
        }
    }

    private static final int[] BUTTON_ON_CLICK = new int[]{R.id.setting, R.id.views, R.id.set,};

    private static final int[] POP_LIST = new int[]{R.string.driving_data, R.string.conv_consumers, R.string.vehicle_status, R.string.energy_flow_view};

    private void initPresentationUI() {
        //注册按钮回调函数
        for (int i : BUTTON_ON_CLICK) {
            View v = mMainView.findViewById(i);
            if (v != null) {
                v.setOnClickListener(mOnClick);
            }
        }
        //注册相关的回调函数
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

    private Handler mHandlerDialog = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showUI(POP_LIST[msg.what]);
        }
    };

    private int mCurPage = -1;

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
        requestInitData();
    }

    private final static int[] INIT_CMDS = {0x13, 0x14, 0x15, 0x16, 0x17, 0x18};


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
                sendCanboxInfo0x90((msg.what & 0xff00) >> 8, msg.what & 0xff);

            }
        }
    };

    private void sendCanboxInfo0x90(int d0, int d1) {
        byte[] buf = new byte[]{0x2, (byte) 0xa, 0x1, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{0x02, (byte) d0, (byte) d1, (byte) d2};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void setPreference(String key, String s) {
        Preference p = findPreference(key);
        if (p != null) {
            p.setSummary(s);
        }
    }

    private View mTpmsView;

    public boolean onPreferenceClick(Preference arg0) {

        try {
            String key = arg0.getKey();
            if ("tpms".equals(key)) {
                mTpmsView = null;
                sendCanboxInfo(0x4b, 0x1, 0x1);
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

    private void updateView(byte[] buf) {
        int index = 0;
        int mEnduranceMileage = 0;
        String s = "";
        switch (buf[0]) {
            case 0x13: {
                //获取油耗的值
                index = ((buf[2] & 0xff) << 8 | (buf[3] & 0xff));
                s = index / 10 + " L/100km";
                NODES_DRIVINGDATA[0].mOilConsumption = s;

                //获取续航里程
                index = ((buf[4] & 0xff) << 8 | (buf[5] & 0xff));
                s = index + " km";
                mEnduranceMileage = index;
                NODES_DRIVINGDATA[0].mEnduranceMileage = s;

                //行驶里程
                index = ((buf[6] & 0xff) << 8 | (buf[7] & 0xff));
                index = index / 10;
                s = index + " km";
                NODES_DRIVINGDATA[0].mDrivenDistance = s;

                //行驶时间
                index = ((buf[8] & 0xff) << 8 | (buf[9] & 0xff));
                s = String.format(Locale.ENGLISH, "%02d:%02d h", index / 60, index % 60);
                NODES_DRIVINGDATA[0].mDrivenTime = s;

                //平均速度
                index = buf[10] & 0xff;
                s = index + " km/h";
                NODES_DRIVINGDATA[0].mAverageSpeed = s;

                //更新页面
                setMileage(mEnduranceMileage, NODES_DRIVINGDATA[0].mEnduranceMileage);

                showDrivingData();
                break;
            }
            case 0x14: {
                //获取油耗的值
                index = ((buf[2] & 0xff) << 8 | (buf[3] & 0xff));
                s = index / 10 + " L/100km";
                NODES_DRIVINGDATA[1].mOilConsumption = s;

                //获取续航里程
                index = ((buf[4] & 0xff) << 8 | (buf[5] & 0xff));
                s = index + " km";
                mEnduranceMileage = index;
                NODES_DRIVINGDATA[1].mEnduranceMileage = s;

                //行驶里程
                index = ((buf[6] & 0xff) << 8 | (buf[7] & 0xff));
                index = index / 10;
                s = index + " km";
                NODES_DRIVINGDATA[1].mDrivenDistance = s;

                //行驶时间
                index = ((buf[8] & 0xff) << 8 | (buf[9] & 0xff));
                s = String.format(Locale.ENGLISH, "%02d:%02d h", index / 60, index % 60);
                NODES_DRIVINGDATA[1].mDrivenTime = s;

                //平均速度
                index = buf[10] & 0xff;
                s = index + " km/h";
                NODES_DRIVINGDATA[1].mAverageSpeed = s;

                //更新页面
                setMileage(mEnduranceMileage, NODES_DRIVINGDATA[1].mEnduranceMileage);

                showDrivingData();
                break;
            }
            case 0x15: {
                //获取油耗的值
                index = ((buf[2] & 0xff) << 8 | (buf[3] & 0xff));
                s = index / 10 + " L/100km";
                NODES_DRIVINGDATA[2].mOilConsumption = s;

                //获取续航里程
                index = ((buf[4] & 0xff) << 8 | (buf[5] & 0xff));
                s = index + " km";
                mEnduranceMileage = index;
                NODES_DRIVINGDATA[2].mEnduranceMileage = s;

                //行驶里程
                index = ((buf[6] & 0xff) << 8 | (buf[7] & 0xff));
                index = index / 10;
                s = index + " km";
                NODES_DRIVINGDATA[2].mDrivenDistance = s;

                //行驶时间
                index = ((buf[8] & 0xff) << 8 | (buf[9] & 0xff));
                s = String.format(Locale.ENGLISH, "%02d:%02d h", index / 60, index % 60);
                NODES_DRIVINGDATA[2].mDrivenTime = s;

                //平均速度
                index = buf[10] & 0xff;
                s = index + " km/h";
                NODES_DRIVINGDATA[2].mAverageSpeed = s;

                //更新页面
                setMileage(mEnduranceMileage, NODES_DRIVINGDATA[2].mEnduranceMileage);

                showDrivingData();
                break;
            }
            case 0x16: {
                switch (buf[2] & 0xff) {
                    case 0x00: {
                        setTextViewString(R.id.progress_center, "1/8");
                        setTextViewString(R.id.progress_max, "1/4");
                        break;
                    }
                    case 0x01: {
                        setTextViewString(R.id.progress_center, "3/16");
                        setTextViewString(R.id.progress_max, "3/8");
                        break;
                    }
                    case 0x02: {
                        setTextViewString(R.id.progress_center, "1/4");
                        setTextViewString(R.id.progress_max, "1/2");
                        break;
                    }
                    case 0x03: {
                        setTextViewString(R.id.progress_center, "1/2");
                        setTextViewString(R.id.progress_max, "1");
                        break;
                    }
                    case 0x04: {
                        setTextViewString(R.id.progress_center, "3/4");
                        setTextViewString(R.id.progress_max, "3/2");
                        break;
                    }
                    case 0x05: {
                        setTextViewString(R.id.progress_center, "1");
                        setTextViewString(R.id.progress_max, "2");
                        break;
                    }
                }

                //单位
                if ((buf[11] & 0x1) == 0x00) {
                    s = " l/h";
                } else {
                    s = " gal/h";
                }
                setTextViewString(R.id.unit, s);

                //百分比
                index = buf[3] & 0xff;
                ProgressBar sb = (ProgressBar) mMainView.findViewById(R.id.progressbar);
                sb.setMax(100);
                sb.setProgress(index);
                break;
            }
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
