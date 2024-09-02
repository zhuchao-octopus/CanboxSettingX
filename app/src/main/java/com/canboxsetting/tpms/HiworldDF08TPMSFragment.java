package com.canboxsetting.tpms;

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
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.view.MarqueeTextView;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

public class HiworldDF08TPMSFragment extends PreferenceFragmentCompat implements OnPreferenceClickListener {
    private static final String TAG = "HiworldDF08Fragment";

    private boolean mPause = true;
    private View mTpmsView;
    private View mTpmsReset;
    private int mColor = 0;
    private int mUnit = 0;
    private BroadcastReceiver mReceiver;

    private TextView frontLiftTemp, frontRightTemp, behindLiftTemp, behindRightTemp, frontLiftPressure, frontRightPressure, behindLiftPressure, behindRightPressure;
    private MarqueeTextView frontLiftState, frontRightState, behindLiftState, behindRightState;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        mTpmsView = inflater.inflate(R.layout.type_info_od, container, false);
        initView(mTpmsView);
        return mTpmsView;
    }

    private void initView(View mTpmsView) {
        frontLiftTemp = mTpmsView.findViewById(R.id.type11_info);
        frontRightTemp = mTpmsView.findViewById(R.id.type12_info);
        behindLiftTemp = mTpmsView.findViewById(R.id.type21_info);
        behindRightTemp = mTpmsView.findViewById(R.id.type22_info);
        frontLiftPressure = mTpmsView.findViewById(R.id.type11_num);
        frontRightPressure = mTpmsView.findViewById(R.id.type12_num);
        behindLiftPressure = mTpmsView.findViewById(R.id.type21_num);
        behindRightPressure = mTpmsView.findViewById(R.id.type22_num);
        frontLiftState = mTpmsView.findViewById(R.id.type11_info2);
        frontRightState = mTpmsView.findViewById(R.id.type12_info2);
        behindLiftState = mTpmsView.findViewById(R.id.type21_info2);
        behindRightState = mTpmsView.findViewById(R.id.type22_info2);
    }

    @Override
    public void onPause() {
        mPause = true;
        stopRequestInitData();
        super.onPause();
        unregisterListener();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    requestInitData();
                    break;
                case 1:
                    updateInfo();
                    break;
            }
        }
    };

    private void initTpmsView() {

        // MyPreference2 p = (MyPreference2) findPreference("tpms_content");
        // if (p != null) {
        // mTpmsView = p.getMainView();
        if (mTpmsView != null) {
            mTpmsReset = mTpmsView.findViewById(R.id.tpms);
            if (mTpmsReset != null) {
                mTpmsReset.setVisibility(View.GONE);
            }
        }

    }

    private void requestInitData() {
        if (mPause) {
            return;
        }
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(0, 1500);
        mHandler.sendEmptyMessageDelayed(1, 1500);
    }

    private void stopRequestInitData() {
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        mHandler.removeMessages(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        initTpmsView();
        updateInfo();

    }

    private void sendCanboxInfo(int d0, int d1, int d2) {
        byte[] buf = new byte[]{(byte) d0, 0x01, (byte) d1};
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateInfo() {
        byte[] buf = new byte[]{
                0x00, (byte) 0x5A, (byte) 0xA5, 0x03, 0x6A, 0x05, 0x01, (byte) 0xED, (byte) 0x5F
        };
        MMLog.d(TAG, "updateInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        Log.d(TAG, "updateInfo: ",new Exception());
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessageDelayed(1, 1500);
    }

    public boolean onPreferenceClick(Preference arg0) {

        try {
            String key = arg0.getKey();
            if ("tpms".equals(key)) {
                mTpmsView = null;
//                updateInfo();
            }
        } catch (Exception e) {

        }

        return false;
    }

    private void setTpmsText(int id, int text, int color) {
        switch (text) {
            case 0:
                text = R.string.am_normal;
                break;
            case 1:
                text = R.string.tpms_low;
                break;
            case 2:
                text = R.string.tpms_hi;
                break;
            default:
                text = 0;
                break;
        }

        if (color != 0) {
            color = Color.RED;
        } else {
            color = Color.WHITE;
        }
        TextView tv = mTpmsView.findViewById(id);

        tv.setTextColor(color);
        if (text != 0) {
            tv.setText(text);
        } else {
            if (tv.getText().length() == 0) {
                tv.setText(R.string.am_normal);
            }
        }
    }

    public void setTpmsTempText(int frontLiftTempValue, int frontRightTempValue, int behindLiftTempValue, int behindRightTempValue) {
        MMLog.d(TAG, "setTpmsTempText: frontLiftTempValue = " + frontLiftTempValue +"  frontRightTempValue = " + frontRightTempValue + "  behindLiftTempValue = " +behindLiftTempValue + "  behindRightTempValue = " + behindRightTempValue);
        if (frontLiftTempValue == 0xff) frontLiftTemp.setText("-");
        else frontLiftTemp.setText((frontLiftTempValue) + " °C");
        if (frontRightTempValue == 0xff) frontRightTemp.setText("-");
        else frontRightTemp.setText((frontRightTempValue) + " °C");
        if (behindLiftTempValue == 0xff) behindLiftTemp.setText("-");
        else behindLiftTemp.setText((behindLiftTempValue) + " °C");
        if (behindRightTempValue == 0xff) behindRightTemp.setText("-");
        else behindRightTemp.setText((behindRightTempValue) + " °C");
    }

    public void setTpmsPressureText(int frontLiftPressureValue, int frontRightPressureValue, int behindLiftPressureValue, int behindRightPressureValue) {
        MMLog.d(TAG, "setTpmsTempText: frontLiftPressureValue = " + frontLiftPressureValue +"  frontRightPressureValue = " + frontRightPressureValue + "  behindLiftPressureValue = " +behindLiftPressureValue + "  behindRightPressureValue = " + behindRightPressureValue);
        if (frontLiftPressureValue == 0xff) frontLiftPressure.setText("-.-");
        else frontLiftPressure.setText((frontLiftPressureValue ) + " kPa");
        if (frontRightPressureValue == 0xff) frontRightPressure.setText("-.-");
        else frontRightPressure.setText((frontRightPressureValue) + " kPa");
        if (behindLiftPressureValue == 0xff) behindLiftPressure.setText("-.-");
        else behindLiftPressure.setText((behindLiftPressureValue) + " kPa");
        if (behindRightPressureValue == 0xff) behindRightPressure.setText("-.-");
        else behindRightPressure.setText((behindRightPressureValue) + " kPa");
    }

    private void setTpmsTextValue(int id, int value1, int value2, int color) {
        String text = "";

        if (value1 == 0xff) {
            text = (value2 - 40) + "°C";
        } else {
            double v = (value1 & 0xFF) * 1.373;

            text = v + "KPa " + (value2 - 40) + "°C";
        }

        if (color != 0) {
            color = Color.RED;
        } else {
            color = Color.WHITE;
        }

        TextView tv = ((TextView) mTpmsView.findViewById(id));

        tv.setTextColor(color);
        tv.setText(text);
    }

    private void updateView(byte[] buf) {
        if (mTpmsView == null) {
            initTpmsView();
        }
        MMLog.d(TAG, "updateView: buf = " + ByteUtils.BuffToHexStr(buf));
        if (buf != null && buf[2] == (byte) 0xED && buf[1] + 4 == buf.length) {
            MMLog.d(TAG, "updateView 数据验证通过: buf = " + ByteUtils.BuffToHexStr(buf));
            setTpmsPressureText((buf[5] & 0xFF) + (buf[10] & 0xFF),(buf[6] & 0xFF) + (buf[11] & 0xFF),(buf[7] & 0xFF) + (buf[12] & 0xFF),(buf[8] & 0xFF) + (buf[13] & 0xFF));
            setTpmsTempText((buf[15] & 0xFF) - 40,(buf[16] & 0xFF) - 40,(buf[17] & 0xFF) - 40,(buf[18] & 0xFF) - 40);
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
