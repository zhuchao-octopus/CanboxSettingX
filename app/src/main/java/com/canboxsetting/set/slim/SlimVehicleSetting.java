package com.canboxsetting.set.slim;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.canboxsetting.set.slim.view.SlimLightSelectDialog;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlimVehicleSetting extends MyFragment {
    private static final String TAG = "SlimVehicleSetting";
    private SlimVehicleSettingAdapter settingAdapter;
    private RecyclerView showSettingRv;
    private Context mContext = null;
    private SlimAVM360SettingFragment avm360SettingFragment;
    private byte securityState = 0;
    private byte[]settingAllData;
    private static final int[] iconIds = new int[]{R.mipmap.slim_vehicle_360_icon, R.mipmap.slim_vehicle_lock_icon, R.mipmap.slim_vehicle_back_vision_icon,
            R.mipmap.slim_vehicle_warning_light_icon, R.mipmap.slim_vehicle_sound_lock_icon, R.mipmap.slim_vehicle_battery_icon, R.mipmap.slim_vehicle_phone_icon,
            R.mipmap.slim_vehicle_instrument_icon, R.mipmap.slim_vehicle_time_icon, R.mipmap.slim_vehicle_language_icon};
    private static final int[] settingNames = new int[]{R.string.around_view_monitor, R.string.smart_car_door_lock, R.string.auto_folding_of_outer_reaview_mirror, R.string.aemergency_brake_warning_light,
            R.string.security_tips, R.string.wireless_charging, R.string.mobile_forgotten_tips, R.string.meter_setting, R.string.clock_setting, R.string.car_language};
    private static final SlimVehicleSettingAdapter.SlimVehicleSettingType[] types = new SlimVehicleSettingAdapter.SlimVehicleSettingType[]{SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE1,
            SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE1, SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE2, SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE2,
            SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE1, SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE2, SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE2,
            SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE1, SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE1, SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE3};
    private Map<String,SlimVehicleSettingItemBean> settingItemBeanMap;
    private SlimLightSelectDialog lightSelectDialog;
    private ImageView backPage;private static final int SEND_CAN = 0x01;
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
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slim_vehicle_setting_layout, container, false);
        init(view);
        return view;
    }

    private void init(View v) {
        mContext = getContext();
        settingItemBeanMap = new HashMap<>();
        showSettingRv = v.findViewById(R.id.show_vehicle_setting);
        showSettingRv.setLayoutManager(new LinearLayoutManager(getContext()));
        showSettingRv.setItemAnimator(new DefaultItemAnimator());
        for (int i = 0; i < settingNames.length; i++) {
            SlimVehicleSettingItemBean bean = new SlimVehicleSettingItemBean();
            bean.setIcon(iconIds[i]);
            bean.setType(types[i]);
            bean.setTitleName(getString(settingNames[i]));
            settingItemBeanMap.put(getString(settingNames[i]),bean);
            if (getString(settingNames[i]).equals(getString(R.string.car_language))) {
                bean.setSettingValue("English");
            }
        }
        settingAdapter = new SlimVehicleSettingAdapter(getContext(),settingNames, settingItemBeanMap, clickListener);
        showSettingRv.setAdapter(settingAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
//        settingAllData = SlimCanUtils.getInstance().getSettingData();
//        if (settingAllData != null) {
//            updateSecurityTips(settingAllData[11]);
//            updateWirelessCharging(settingAllData[12] == 0x01);
//            updateMobileForgottenTips(settingAllData[13] == 0x01);
//            updateAutoMirror(settingAllData[17] == 0x01);
//            updateWarningLight(settingAllData[18] == 0x01);
//        }
        sendCanMessage((byte) 0x19,0);
        sendCanMessage((byte) 0x1A,200);
        sendCanMessage((byte) 0x1B,400);
        sendCanMessage((byte) 0x1F,600);
        sendCanMessage((byte) 0x20,800);
    }

    private void sendCanMessage(byte a,int time) {
        Message message = mHandler.obtainMessage();
        message.what = SEND_CAN;
        message.arg1 = 0x00;
        message.arg2 = a;
        mHandler.sendMessageDelayed(message,time);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateSecurityTips(byte state) {
        SlimVehicleSettingItemBean securityTips = settingItemBeanMap.get(getString(R.string.security_tips));
        securityTips.setSettingValue(state == 0x01?getString(R.string.light_horn):getString(R.string.str_light));
        securityState = state;
        settingItemBeanMap.put(getString(R.string.security_tips),securityTips);
        settingAdapter.setSettingItemBeanList(settingItemBeanMap);
        if (lightSelectDialog != null) lightSelectDialog.update(securityState);
    }
    private void updateWirelessCharging(boolean isOpen) {
        SlimVehicleSettingItemBean wirelessCharging = settingItemBeanMap.get(getString(R.string.wireless_charging));
        wirelessCharging.setSelect(isOpen);
        settingItemBeanMap.put(getString(R.string.wireless_charging),wirelessCharging);
        settingAdapter.setSettingItemBeanList(settingItemBeanMap);
    }
    private void updateMobileForgottenTips(boolean isOpen) {
        SlimVehicleSettingItemBean mobileForgottenTips = settingItemBeanMap.get(getString(R.string.mobile_forgotten_tips));
        mobileForgottenTips.setSelect(isOpen);
        settingItemBeanMap.put(getString(R.string.mobile_forgotten_tips),mobileForgottenTips);
        settingAdapter.setSettingItemBeanList(settingItemBeanMap);
    }

    private void updateAutoMirror(boolean isOpen) {
        SlimVehicleSettingItemBean doorBean = settingItemBeanMap.get(getString(R.string.auto_folding_of_outer_reaview_mirror));
        doorBean.setSelect(isOpen);
        settingItemBeanMap.put(getString(R.string.auto_folding_of_outer_reaview_mirror),doorBean);
        settingAdapter.setSettingItemBeanList(settingItemBeanMap);
    }

    private void updateWarningLight(boolean isOpen) {
        SlimVehicleSettingItemBean lightBean = settingItemBeanMap.get(getString(R.string.aemergency_brake_warning_light));
        lightBean.setSelect(isOpen);
        settingItemBeanMap.put(getString(R.string.aemergency_brake_warning_light),lightBean);
        settingAdapter.setSettingItemBeanList(settingItemBeanMap);
    }

    public void updateView(byte[] buf) {
        MMLog.d(TAG, "updateView: buf = " + ByteUtils.BuffToHexStr(buf));
        if (buf != null && buf.length == 6 && buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x02) {
            switch (buf[3]) {
                case 0x19://安全提示
//                    SlimVehicleSettingItemBean securityTips = settingItemBeanMap.get(getString(R.string.security_tips));
//                    securityTips.setSettingValue(buf[4] == 0x01?getString(R.string.light_horn):getString(R.string.str_light));
//                    securityState = buf[4];
//                    settingItemBeanMap.put(getString(R.string.security_tips),securityTips);
//                    settingAdapter.setSettingItemBeanList(settingItemBeanMap);
//                    lightSelectDialog.update(securityState);
                    updateSecurityTips(buf[4]);
                    break;
                case 0x1A://无线充电
//                    SlimVehicleSettingItemBean wirelessCharging = settingItemBeanMap.get(getString(R.string.wireless_charging));
//                    wirelessCharging.setSelect(buf[4] == 0x01);
//                    settingItemBeanMap.put(getString(R.string.wireless_charging),wirelessCharging);
//                    settingAdapter.setSettingItemBeanList(settingItemBeanMap);
                    updateWirelessCharging(buf[4] == 0x01);
                    break;
                case 0x1B://手机遗忘提示
//                    SlimVehicleSettingItemBean mobileForgottenTips = settingItemBeanMap.get(getString(R.string.mobile_forgotten_tips));
//                    mobileForgottenTips.setSelect(buf[4] == 0x01);
//                    settingItemBeanMap.put(getString(R.string.mobile_forgotten_tips),mobileForgottenTips);
//                    settingAdapter.setSettingItemBeanList(settingItemBeanMap);
                    updateMobileForgottenTips(buf[5] == 0x01);
                    break;
                case 0x1F://自动折叠后视镜
//                    SlimVehicleSettingItemBean doorBean = settingItemBeanMap.get(getString(R.string.auto_folding_of_outer_reaview_mirror));
//                    doorBean.setSelect(buf[4] == 0x01);
//                    settingItemBeanMap.put(getString(R.string.auto_folding_of_outer_reaview_mirror),doorBean);
//                    settingAdapter.setSettingItemBeanList(settingItemBeanMap);
                    updateAutoMirror(buf[4] == 0x01);
                    break;
                case 0x20://紧急制动警告灯
//                    SlimVehicleSettingItemBean lightBean = settingItemBeanMap.get(getString(R.string.aemergency_brake_warning_light));
//                    lightBean.setSelect(buf[4] == 0x01);
//                    settingItemBeanMap.put(getString(R.string.aemergency_brake_warning_light),lightBean);
//                    settingAdapter.setSettingItemBeanList(settingItemBeanMap);
                    updateWarningLight(buf[4] == 0x01);
                    break;
            }
        }
    }


    private SlimVehicleSettingAdapter.OnSlimVehicleClickListener clickListener = (position, bean) -> {
        Log.d(TAG, "clickListener: titleName = " + bean.getTitleName());
        if (getString(R.string.around_view_monitor).equals(bean.getTitleName())) {
            Intent startAvm360 = new Intent();
            startAvm360.setAction(SlimSettingFragment.ACTION_VEHICLE_OPEN_PAGE);
            startAvm360.putExtra(SlimSettingFragment.DATA_OPEN_PAGE,SlimSettingFragment.PAGE_AVM360);
            mContext.sendBroadcast(startAvm360);
        } else if (getString(R.string.smart_car_door_lock).equals(bean.getTitleName())) {
            Intent startDoor = new Intent();
            startDoor.setAction(SlimSettingFragment.ACTION_VEHICLE_OPEN_PAGE);
            startDoor.putExtra(SlimSettingFragment.DATA_OPEN_PAGE,SlimSettingFragment.PAGE_DOOR);
            mContext.sendBroadcast(startDoor);
        } else if (getString(R.string.auto_folding_of_outer_reaview_mirror).equals(bean.getTitleName())) {
            if (settingItemBeanMap.get(getString(R.string.auto_folding_of_outer_reaview_mirror)).isSelect()) sendCanboxInfo((byte) 0x9F, (byte) 0);
            else sendCanboxInfo((byte) 0x9F, (byte) 1);
        } else if (getString(R.string.aemergency_brake_warning_light).equals(bean.getTitleName())) {
            if (settingItemBeanMap.get(getString(R.string.aemergency_brake_warning_light)).isSelect()) sendCanboxInfo((byte) 0xA0, (byte) 0);
            else sendCanboxInfo((byte) 0xA0, (byte) 1);
        } else if (getString(R.string.security_tips).equals(bean.getTitleName())) {
            if (lightSelectDialog == null) {
                lightSelectDialog = new SlimLightSelectDialog(getActivity(), 0, new SlimLightSelectDialog.OnLightSelectListener() {
                    @Override
                    public void onLightSelectListener(int state) {
                        sendCanboxInfo((byte) 0x99, (byte) state);
                    }
                });
            }
            updateSecurityTips(settingAllData[11]);
            lightSelectDialog.show();
        } else if (getString(R.string.wireless_charging).equals(bean.getTitleName())) {
            if (settingItemBeanMap.get(getString(R.string.wireless_charging)).isSelect()) sendCanboxInfo((byte) 0x9A, (byte) 0);
            else sendCanboxInfo((byte) 0x9A, (byte) 1);
        } else if (getString(R.string.mobile_forgotten_tips).equals(bean.getTitleName())) {
            if (settingItemBeanMap.get(getString(R.string.mobile_forgotten_tips)).isSelect()) sendCanboxInfo((byte) 0x9B, (byte) 0);
            else sendCanboxInfo((byte) 0x9B, (byte) 1);
        } else if (getString(R.string.meter_setting).equals(bean.getTitleName())) {
            Intent startDoor = new Intent();
            startDoor.setAction(SlimSettingFragment.ACTION_VEHICLE_OPEN_PAGE);
            startDoor.putExtra(SlimSettingFragment.DATA_OPEN_PAGE,SlimSettingFragment.PAGE_METER);
            mContext.sendBroadcast(startDoor);
        } else if (getString(R.string.clock_setting).equals(bean.getTitleName())) {
            Intent startClock = new Intent();
            startClock.setAction(SlimSettingFragment.ACTION_VEHICLE_OPEN_PAGE);
            startClock.putExtra(SlimSettingFragment.DATA_OPEN_PAGE,SlimSettingFragment.PAGE_CLOCK);
            mContext.sendBroadcast(startClock);
        } else if (getString(R.string.car_language).equals(bean.getTitleName())) {

        }
    };

    private void sendCanboxInfo(byte d0, byte d1) {
        byte[] buf = new byte[]{
                0x00, (byte) 0xA4, 0x01, 0x00, 0x02, d0, d1, (byte) (0xA7 + d0 + d1)
        };
        MMLog.d(TAG, "sendCanboxInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(getActivity(), buf);
    }
}
