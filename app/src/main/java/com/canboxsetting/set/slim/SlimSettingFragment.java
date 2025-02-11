package com.canboxsetting.set.slim;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.adapter.bean.SlimVehicleSettingItemBean;
import com.common.utils.MyCmd;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

public class SlimSettingFragment extends MyFragment {
    private BroadcastReceiver mReceiver;
    private SlimAVM360SettingFragment avm360SettingFragment;
    private SlimDoorSettingFragment slimDoorSettingFragment;
    private SlimMeterSettingFragment slimMeterSettingFragment;
    private SlimClockSettingFragment slimClockSettingFragment;
    public static final String ACTION_VEHICLE_OPEN_PAGE = "ACTION_VEHICLE_OPEN_PAGE";
    public static final String DATA_OPEN_PAGE = "ACTION_PAGE_DATA";
    public static final String PAGE_AVM360 = "PAGE_AVM360";
    public static final String PAGE_DOOR = "PAGE_DOOR";
    public static final String PAGE_METER = "PAGE_METER";
    public static final String PAGE_CLOCK = "PAGE_CLOCK";
    private View mMainView;
    private FragmentManager mFragmentManager;
    private SlimVehicleSetting slimVehicleSetting;
    private SlimConnectSetting slimConnectSetting;
    private Fragment currFragment;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        mFragmentManager = getActivity().getSupportFragmentManager();
        slimVehicleSetting = new SlimVehicleSetting();
        slimConnectSetting = new SlimConnectSetting();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.slim_contral_setting_layout, container, false);
        replaceFragment(R.id.show_slim_setting,slimVehicleSetting,false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_VEHICLE_OPEN_PAGE);
        getActivity().registerReceiver(receiver,filter);
        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//        );
//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);

        registerListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListener();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.slim_setting_vehicle:
                replaceFragment(R.id.show_slim_setting,slimVehicleSetting,false);
                break;
            case R.id.slim_setting_connection:
                replaceFragment(R.id.show_slim_setting,slimConnectSetting,false);
                break;
            case R.id.slim_setting_display:
                break;
            case R.id.slim_setting_sound:
                break;
            case R.id.slim_setting_system:
                break;
            case R.id.close_ac:
                getActivity().finish();
                break;
        }
    }

    public void popBackStack() {
        mFragmentManager.popBackStack();
    }

    public void replaceFragment(int layoutId, Fragment fragment, boolean isAddStack) {
        FragmentActivity fragmentActivity = getActivity();
        if (fragment != null && fragmentActivity != null) {
            mFragmentManager = fragmentActivity.getSupportFragmentManager();
            currFragment = fragment;
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(layoutId, fragment);
            if (isAddStack) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_VEHICLE_OPEN_PAGE:
                    String page = intent.getStringExtra(DATA_OPEN_PAGE);
                    if (page.equals(PAGE_AVM360)) {
                        if (avm360SettingFragment == null) avm360SettingFragment = new SlimAVM360SettingFragment();
                        replaceFragment(R.id.show_slim_setting,avm360SettingFragment,true);
                    } else if (page.equals(PAGE_DOOR)) {
                        if (slimDoorSettingFragment == null) slimDoorSettingFragment = new SlimDoorSettingFragment();
                        replaceFragment(R.id.show_slim_setting,slimDoorSettingFragment,true);
                    } else if (page.equals(PAGE_METER)) {
                        if (slimMeterSettingFragment == null) slimMeterSettingFragment = new SlimMeterSettingFragment();
                        replaceFragment(R.id.show_slim_setting,slimMeterSettingFragment,true);
                    } else if (page.equals(PAGE_CLOCK)) {
                        if (slimClockSettingFragment == null) slimClockSettingFragment = new SlimClockSettingFragment();
                        replaceFragment(R.id.show_slim_setting,slimClockSettingFragment,true);
                    }
                    break;
            }
        }
    };

    private void unregisterListener() {
        if (mReceiver != null) {
            this.getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerListener() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (MyCmd.BROADCAST_SEND_FROM_CAN.equals(action)) {
                        byte[] buf = intent.getByteArrayExtra("buf");
                        if (buf != null) {
                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateView(buf);
                                    }
                                });
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

    public void updateView(byte[] buf) {
        Log.d("TAG", "updateView: mian buf = " + ByteUtils.BuffToHexStr(buf));
        if (buf != null && buf.length >= 6 && buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x02) {
            switch (buf[3]) {
                case 0x10:
                case 0x11:
//                    lane_departure_warning.setChecked(buf[4] == 0x01 ? true : false);
                    if (currFragment != null && currFragment instanceof SlimAVM360SettingFragment) {
                        avm360SettingFragment.updateView(buf);
                    }
                    break;
                case 0x12://关机自动解锁
                case 0x13://自动驾驶锁
                case 0x14://接近解锁/离开锁定
                case 0x15://迎宾灯
                    if (currFragment != null && currFragment instanceof SlimDoorSettingFragment) {
                        slimDoorSettingFragment.updateView(buf);
                    }
                    break;
                case 0x16:
                case 0x17:
                case 0x18:
                    if (currFragment != null && currFragment instanceof SlimMeterSettingFragment) {
                        SlimMeterSettingFragment meterSettingFragment = (SlimMeterSettingFragment) currFragment;
                        meterSettingFragment.updateView(buf);
                    }
                    break;
                case 0x1C:
                case 0x1D:
                    if (currFragment != null && currFragment instanceof SlimClockSettingFragment) {
                        slimClockSettingFragment.updateView(buf);
                    }
                    break;
                case 0x1E:
                    break;
                case 0x19://安全提示
                case 0x1A://无线充电
                case 0x1B://手机遗忘提示
                case 0x1F://自动折叠后视镜
                case 0x20://紧急制动警告灯
//                    if (currFragment != null && currFragment instanceof SlimVehicleSetting) {
//                        SlimVehicleSetting slimVehicleSetting1 = (SlimVehicleSetting) currFragment;
//                        slimVehicleSetting1.updateView(buf);
//                    }
                    if (slimVehicleSetting != null) {
                        slimVehicleSetting.updateView(buf);
                    }
                    break;
            }
        }
    }
}
