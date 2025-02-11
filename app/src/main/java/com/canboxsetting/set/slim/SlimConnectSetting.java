package com.canboxsetting.set.slim;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.canboxsetting.adapter.SlimVehicleSettingAdapter;
import com.canboxsetting.adapter.bean.SlimVehicleSettingItemBean;
import com.common.utils.UtilSystem;

import java.util.HashMap;
import java.util.Map;

public class SlimConnectSetting extends MyFragment {
    private static final String TAG = "SlimConnectSetting";
    private static final int[] iconIds = new int[]{R.mipmap.slim_setting_wifi_icon, R.mipmap.slim_setting_bt_icon};
    private static final int[] settingNames = new int[]{R.string.wifi, R.string.bluetooth};
    private static final SlimVehicleSettingAdapter.SlimVehicleSettingType[] types = new SlimVehicleSettingAdapter.SlimVehicleSettingType[]{SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE1, SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE1};

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slim_vehicle_setting_layout, container, false);
        init(view);
        return view;
    }

    private void init(View v) {
        Map<String, SlimVehicleSettingItemBean> settingItemBeanMap = new HashMap<>();
        RecyclerView showSettingRv = v.findViewById(R.id.show_vehicle_setting);
        showSettingRv.setLayoutManager(new LinearLayoutManager(getContext()));
        showSettingRv.setItemAnimator(new DefaultItemAnimator());
        for (int i = 0; i < settingNames.length; i++) {
            SlimVehicleSettingItemBean bean = new SlimVehicleSettingItemBean();
            bean.setIcon(iconIds[i]);
            bean.setType(types[i]);
            bean.setTitleName(getString(settingNames[i]));
            settingItemBeanMap.put(getString(settingNames[i]), bean);
        }
        SlimVehicleSettingAdapter settingAdapter = new SlimVehicleSettingAdapter(getContext(), settingNames, settingItemBeanMap, clickListener);
        showSettingRv.setAdapter(settingAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private final SlimVehicleSettingAdapter.OnSlimVehicleClickListener clickListener = (position, bean) -> {
        Log.d(TAG, "clickListener: titleName = " + bean.getTitleName());
        if (bean.getTitleName().equals(getString(R.string.wifi))) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        } else if (bean.getTitleName().equals(getString(R.string.bluetooth))) {
            UtilSystem.doRunActivity(getContext(), "com.my.bt", "com.my.bt.ATBluetoothActivity");
        }
    };

}
