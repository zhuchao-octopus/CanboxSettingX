package com.canboxsetting.set.slim;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.set.slim.utils.SlimCanUtils;
import com.canboxsetting.set.slim.view.SlimStringSelectDialog;
import com.common.utils.BroadcastUtil;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

import java.util.ArrayList;
import java.util.List;

public class SlimMeterSettingFragment extends MyFragment implements View.OnClickListener {
    private static final String TAG = "SlimMeterSettingFragmen";
    private TextView currLightView;
    private SeekBar lightSeekBar;
    private RelativeLayout speedOver, fatigueDriving;
    private List<String> speedList, timeList;
    private SlimStringSelectDialog speedDialog, timeDialog;
    private byte[] settingAllData;
    private ImageView backPage;
    private static final int SEND_CAN = 0x01;
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_CAN:
                    byte a1 = (byte) msg.arg1;
                    byte a2 = (byte) msg.arg2;
                    SlimCanUtils.getInstance().sendCanboxInfo(getContext(), a1, a2);
                    break;
            }
        }
    };

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slim_meter_setting_layout, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        speedList = new ArrayList<>();
        for (int i = 0; i <= 21; i++) {
            if (i == 0) {
                speedList.add(getString(R.string.turned_off));
            } else {
                speedList.add((25 + i * 5) + "km/h");
            }
        }
        timeList = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            if (i == 0) {
                timeList.add(getString(R.string.turned_off));
            } else {
                timeList.add((0.5 + i * 0.5) + "km/h");
            }
        }
        speedDialog = new SlimStringSelectDialog(getActivity(), speedList, 0, new SlimStringSelectDialog.OnAVMDialogStateListener() {
            @Override
            public void onAVMDialogStateListener(boolean isOpen, int position, String value) {
                MMLog.d(TAG, "onAVMDialogStateListener   speedDialog: isOpen = " + isOpen + "   position = " + position + "   value = " + value);
                sendCanboxInfo((byte) 0x97, (byte) position);
            }
        });
        speedDialog.setDialogTitle(getString(R.string.over_speed_alarm));
        timeDialog = new SlimStringSelectDialog(getActivity(), timeList, 0, new SlimStringSelectDialog.OnAVMDialogStateListener() {
            @Override
            public void onAVMDialogStateListener(boolean isOpen, int position, String value) {
                MMLog.d(TAG, "onAVMDialogStateListener  timeDialog: isOpen = " + isOpen + "   position = " + position + "   value = " + value);
                sendCanboxInfo((byte) 0x98, (byte) position);
            }
        });
        timeDialog.setDialogTitle(getString(R.string.fatigue_driving));
        currLightView = view.findViewById(R.id.curr_value);
        lightSeekBar = view.findViewById(R.id.back_light_bar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lightSeekBar.setMin(1);
        }
        lightSeekBar.setMax(10);

        lightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    sendCanboxInfo((byte) 0x96, (byte) (progress + 1));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        speedOver = view.findViewById(R.id.over_speed);
        speedOver.setOnClickListener(this);
        fatigueDriving = view.findViewById(R.id.fatigue_driving);
        fatigueDriving.setOnClickListener(this);
        backPage = view.findViewById(R.id.back_page);
        backPage.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        settingAllData = SlimCanUtils.getInstance().getSettingData();
        //        if (settingAllData != null) {
        //            updateLightSeek(settingAllData[6]);
        //            updateSpeed(settingAllData[7]);
        //            updateTime(settingAllData[8]);
        //        }
        for (int i = 0x16; i <= 0x18; i++) {
            Message message = mHandler.obtainMessage();
            message.what = SEND_CAN;
            message.arg1 = 0x00;
            message.arg2 = i;
            mHandler.sendMessageDelayed(message, (i - 0x16) * 200);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.over_speed:
                speedDialog.show();
                break;
            case R.id.fatigue_driving:
                timeDialog.show();
                break;
            case R.id.back_page:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    private void sendCanboxInfo(byte d0, byte d1) {
        byte[] buf = new byte[]{0x00, (byte) 0xA4, 0x01, 0x00, 0x02, d0, d1, (byte) (0xA7 + d0 + d1)};
        MMLog.d(TAG, "sendCanboxInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }

    private void updateLightSeek(byte value) {
        backLight = value;
        if (lightSeekBar != null && currLightView != null) {
            lightSeekBar.setProgress(backLight);
            currLightView.setText(backLight + "");
        }
    }

    private void updateSpeed(byte value) {
        speedValue = value;
        speedDialog.setDefPosition(speedValue);
    }

    private void updateTime(byte time) {
        timeValue = time;
        timeDialog.setDefPosition(timeValue);
    }

    private int speedValue, timeValue, backLight;

    public void updateView(byte[] buf) {
        if (buf != null && buf.length == 6 && buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x02) {
            switch (buf[3]) {
                case 0x16:
                    //                    backLight = buf[4];
                    //                    if (lightSeekBar != null && currLightView != null) {
                    //                        lightSeekBar.setProgress(buf[4]);
                    //                        currLightView.setText(buf[4] + "");
                    //                    }
                    updateLightSeek(buf[4]);
                    break;
                case 0x17:
                    //                    speedValue = buf[4];
                    //                    speedDialog.setDefPosition(speedValue);
                    updateSpeed(buf[4]);
                    break;
                case 0x18:
                    //                    timeValue = buf[4];
                    //                    timeDialog.setDefPosition(timeValue);
                    updateTime(buf[4]);
                    break;
            }
        }
    }
}
