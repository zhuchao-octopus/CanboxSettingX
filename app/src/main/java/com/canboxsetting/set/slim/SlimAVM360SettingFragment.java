package com.canboxsetting.set.slim;

import android.graphics.Color;
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
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SlimAVM360SettingFragment extends MyFragment implements View.OnClickListener {
    private RecyclerView showData;
    private SlimVehicleSettingAdapter settingAdapter;
    private ImageView backPage;
    private Map<String, SlimVehicleSettingItemBean> settingItemBeanMap;
    private int[] avmSettingNames = new int[] {R.string.lane_departure_warning, R.string.speed_for_quitting_avm};
    private SlimAVMQuittingSpeedDialog dialog;
    private byte[] currAllData;
    private SlimVehicleSettingAdapter.SlimVehicleSettingType[] slimVehicleSettingTypes = new SlimVehicleSettingAdapter.SlimVehicleSettingType[]{SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE2, SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE1};
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
        View view = inflater.inflate(R.layout.slim_avm360_setting_layout,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        settingItemBeanMap = new HashMap<>();
        backPage = view.findViewById(R.id.show_setting_icon);
        backPage.setOnClickListener(this);
        showData = view.findViewById(R.id.show_avm360_setting);
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
                if (bean.getTitleName().equals(getString(R.string.lane_departure_warning))) {//车道偏离警告
                    SlimCanUtils.getInstance().sendCanboxInfo(getContext(), (byte) 0x90, (byte) (bean.isSelect()?0x00:0x01));
                } else if (bean.getTitleName().equals(getString(R.string.speed_for_quitting_avm))) {//退出AVM速度
                    if (dialog != null) {
                        Window window = dialog.getWindow();
                        if (window != null) {
                            WindowManager.LayoutParams layoutParams = window.getAttributes();
                            // 设置宽度和高度
                            layoutParams.width = 1000; // 或者指定宽度值，例如 300
                            layoutParams.height = 525; // 或者指定高度值
                            window.setAttributes(layoutParams);
                        }
//                        dialog.setSelectPosition(currAllData[3] - 1);
                        dialog.show();
                    }
                }
            }
        });
        showData.setAdapter(settingAdapter);
        dialog = new SlimAVMQuittingSpeedDialog(getActivity(), new SlimAVMQuittingSpeedDialog.OnAVMDialogStateListener() {
            @Override
            public void onAVMDialogStateListener(boolean isOpen, int position, String value) {
                SlimCanUtils.getInstance().sendCanboxInfo(getContext(), (byte) 0x91, (byte) (position +1));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        currAllData = SlimCanUtils.getInstance().getSettingData();
//        if (currAllData != null) updateLaneDepartureWarning(currAllData[2] == 0x01);
        Message laneDepartureWarningMessage = mHandler.obtainMessage();
        laneDepartureWarningMessage.what = SEND_CAN;
        laneDepartureWarningMessage.arg1 = 0x00;
        laneDepartureWarningMessage.arg2 = 0x10;
        mHandler.sendMessageDelayed(laneDepartureWarningMessage,200);
        Message exitAvmSpeed = mHandler.obtainMessage();
        exitAvmSpeed.what = SEND_CAN;
        exitAvmSpeed.arg1 = 0x00;
        exitAvmSpeed.arg2 = 0x11;
        mHandler.sendMessageDelayed(exitAvmSpeed,400);
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

    public void updateLaneDepartureWarning(boolean isOpen) {
        SlimVehicleSettingItemBean securityTips = settingItemBeanMap.get(getString(R.string.lane_departure_warning));
        securityTips.setSelect(isOpen);
        settingItemBeanMap.put(getString(R.string.lane_departure_warning),securityTips);
        MMLog.d("TAG", "updateView: size = " + settingItemBeanMap.keySet().size());
        settingAdapter.setSettingItemBeanList(settingItemBeanMap);
    }

    public void updateView(byte[] buf) {
        MMLog.d("TAG", "AVM updateView: buf = " + ByteUtils.BuffToHexStr(buf));
        if (buf != null && buf.length == 6 && buf[0] == 0x00 && buf[1] == 0x00 && buf[2] == 0x02) {
            switch (buf[3]) {
                case 0x10://车道偏离警告
//                    SlimVehicleSettingItemBean securityTips = settingItemBeanMap.get(getString(R.string.lane_departure_warning));
//                    securityTips.setSelect(buf[4] == 0x01);
//                    settingItemBeanMap.put(getString(R.string.lane_departure_warning),securityTips);
//                    MMLog.d("TAG", "updateView: size = " + settingItemBeanMap.keySet().size());
//                    settingAdapter.setSettingItemBeanList(settingItemBeanMap);
                    updateLaneDepartureWarning(buf[4] == 0x01);
                    break;
                case 0x11://AVM退出速度
                    dialog.setSelectPosition(buf[4] - 1);
                    break;
            }
        }
    }
}
