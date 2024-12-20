package com.canboxsetting.set.slim;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.adapter.SlimVehicleSettingAdapter;
import com.canboxsetting.adapter.bean.SlimVehicleSettingItemBean;
import com.canboxsetting.set.slim.utils.SlimCanUtils;
import com.canboxsetting.set.slim.view.SlimAVMQuittingSpeedDialog;
import com.common.utils.BroadcastUtil;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

import java.util.HashMap;
import java.util.Map;

public class SlimDoorSettingFragment extends MyFragment implements View.OnClickListener {
    private static final String TAG = "SlimDoorSettingFragment";
    private RecyclerView showData;
    private SlimVehicleSettingAdapter settingAdapter;
    private ImageView backPage;
    private Map<String, SlimVehicleSettingItemBean> settingItemBeanMap;
    private int[] avmSettingNames = new int[] {R.string.power_down_auto_unlock, R.string.driving_auto_lock, R.string.approaching_unlock,R.string.welcome_light};
    private SlimVehicleSettingAdapter.SlimVehicleSettingType[] slimVehicleSettingTypes = new SlimVehicleSettingAdapter.SlimVehicleSettingType[]{SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE2, SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE2,
            SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE2, SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE2};
    private byte[] settingAllData;

    private static final int SEND_CAN = 0x01;
    private Handler mHandler = new Handler(Looper.myLooper()){
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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slim_door_setting_layout,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        settingItemBeanMap = new HashMap<>();
        backPage = view.findViewById(R.id.show_setting_icon);
        backPage.setOnClickListener(this);
        showData = view.findViewById(R.id.show_door_setting);
        showData.setLayoutManager(new LinearLayoutManager(getContext()));
        showData.setItemAnimator(new DefaultItemAnimator());
        for (int i = 0; i < avmSettingNames.length; i++) {
            SlimVehicleSettingItemBean bean = new SlimVehicleSettingItemBean();
            bean.setIcon(-1);
            bean.setType(slimVehicleSettingTypes[i]);
            bean.setTitleName(getString(avmSettingNames[i]));
            settingItemBeanMap.put(getString(avmSettingNames[i]),bean);
        }
        settingAdapter = new SlimVehicleSettingAdapter(getContext(), avmSettingNames, settingItemBeanMap, new SlimVehicleSettingAdapter.OnSlimVehicleClickListener() {
            @Override
            public void onSlimVehicleClickListener(int position, SlimVehicleSettingItemBean bean) {
                if (bean.getTitleName().equals(getString(R.string.power_down_auto_unlock))) {//关机自动解锁
                    sendCanboxInfo((byte) 0x92, (byte) (bean.isSelect() == true?0x00:0x01));
                } else if (bean.getTitleName().equals(getString(R.string.driving_auto_lock))) {//自动驾驶锁
                    sendCanboxInfo((byte) 0x93, (byte) (bean.isSelect() == true?0x00:0x01));
                } else if (bean.getTitleName().equals(getString(R.string.approaching_unlock))) {//接近解锁/离开锁定
                    sendCanboxInfo((byte) 0x94, (byte) (bean.isSelect() == true?0x00:0x01));
                } else if (bean.getTitleName().equals(getString(R.string.welcome_light))) {//迎宾灯
                    sendCanboxInfo((byte) 0x95, (byte) (bean.isSelect() == true?0x00:0x01));
                }
            }
        });
        showData.setAdapter(settingAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
//        settingAllData = SlimCanUtils.getInstance().getSettingData();
//        if (settingAllData != null) {
//            updatePowerUnlock(settingAllData[2] == 0x01);
//            updateDrivingLock(settingAllData[3] == 0x01);
//            updateApproachingUnlock(settingAllData[4] == 0x01);
//            updateWelcomeLight(settingAllData[5] == 0x01);
//        }
        for (int i = 0x12;i<=0x15;i++) {
            Message message = mHandler.obtainMessage();
            message.what = SEND_CAN;
            message.arg1 = 0x00;
            message.arg2 = i;
            mHandler.sendMessageDelayed(message,(i - 0x12) * 200);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.show_setting_icon:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }
    private void updatePowerUnlock(boolean isOpen) {
        SlimVehicleSettingItemBean powerLock = settingItemBeanMap.get(getString(R.string.power_down_auto_unlock));
        powerLock.setSelect(isOpen);
        settingItemBeanMap.put(getString(R.string.power_down_auto_unlock),powerLock);
        settingAdapter.setSettingItemBeanList(settingItemBeanMap);
    }
    private void updateDrivingLock(boolean isOpen) {
        SlimVehicleSettingItemBean autoLock = settingItemBeanMap.get(getString(R.string.driving_auto_lock));
        autoLock.setSelect(isOpen);
        settingItemBeanMap.put(getString(R.string.driving_auto_lock),autoLock);
        settingAdapter.setSettingItemBeanList(settingItemBeanMap);
    }
    private void updateApproachingUnlock(boolean isOpen) {
        SlimVehicleSettingItemBean approachingUnlock = settingItemBeanMap.get(getString(R.string.approaching_unlock));
        approachingUnlock.setSelect(isOpen);
        settingItemBeanMap.put(getString(R.string.approaching_unlock),approachingUnlock);
        settingAdapter.setSettingItemBeanList(settingItemBeanMap);
    }
    private void updateWelcomeLight(boolean isOpen) {
        SlimVehicleSettingItemBean welcomeLight = settingItemBeanMap.get(getString(R.string.welcome_light));
        welcomeLight.setSelect(isOpen);
        settingItemBeanMap.put(getString(R.string.welcome_light),welcomeLight);
        settingAdapter.setSettingItemBeanList(settingItemBeanMap);
    }
    public void updateView(byte[] buf) {
        if (buf != null && buf.length == 6 && buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x02) {
            switch (buf[3]) {
                case 0x12://关机自动解锁
//                    SlimVehicleSettingItemBean powerLock = settingItemBeanMap.get(getString(R.string.power_down_auto_unlock));
//                    powerLock.setSelect(buf[4] == 0x01?true:false);
//                    settingItemBeanMap.put(getString(R.string.power_down_auto_unlock),powerLock);
//                    settingAdapter.setSettingItemBeanList(settingItemBeanMap);
                    updatePowerUnlock(buf[4] == 0x01);
                    break;
                case 0x13://自动驾驶锁
//                    SlimVehicleSettingItemBean autoLock = settingItemBeanMap.get(getString(R.string.driving_auto_lock));
//                    autoLock.setSelect(buf[4] == 0x01?true:false);
//                    settingItemBeanMap.put(getString(R.string.driving_auto_lock),autoLock);
//                    settingAdapter.setSettingItemBeanList(settingItemBeanMap);
                    updateDrivingLock(buf[4] == 0x01);
                    break;
                case 0x14://接近解锁/离开锁定
//                    SlimVehicleSettingItemBean approachingUnlock = settingItemBeanMap.get(getString(R.string.approaching_unlock));
//                    approachingUnlock.setSelect(buf[4] == 0x01?true:false);
//                    settingItemBeanMap.put(getString(R.string.approaching_unlock),approachingUnlock);
//                    settingAdapter.setSettingItemBeanList(settingItemBeanMap);
                    updateApproachingUnlock(buf[4] == 0x01);
                    break;
                case 0x15://迎宾灯
//                    SlimVehicleSettingItemBean welcomeLight = settingItemBeanMap.get(getString(R.string.welcome_light));
//                    welcomeLight.setSelect(buf[4] == 0x01?true:false);
//                    settingItemBeanMap.put(getString(R.string.welcome_light),welcomeLight);
//                    settingAdapter.setSettingItemBeanList(settingItemBeanMap);
                    updateWelcomeLight(buf[4] == 0x01);
                    break;
            }
        }
    }
    private void sendCanboxInfo(byte d0, byte d1) {
        byte[] buf = new byte[]{
                0x00, (byte) 0xA4, 0x01, 0x00, 0x02, d0, d1, (byte) (0xA7 + d0 + d1)
        };
        MMLog.d(TAG, "sendCanboxInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }
}
