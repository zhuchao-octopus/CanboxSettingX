package com.canboxsetting.adapter.bean;

import com.canboxsetting.adapter.SlimVehicleSettingAdapter;

public class SlimVehicleSettingItemBean {
    private int icon;
    private String titleName;
    private SlimVehicleSettingAdapter.SlimVehicleSettingType type = SlimVehicleSettingAdapter.SlimVehicleSettingType.TYPE1;
    private String settingValue = "";
    private boolean isSelect = false;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public SlimVehicleSettingAdapter.SlimVehicleSettingType getType() {
        return type;
    }

    public void setType(SlimVehicleSettingAdapter.SlimVehicleSettingType type) {
        this.type = type;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}