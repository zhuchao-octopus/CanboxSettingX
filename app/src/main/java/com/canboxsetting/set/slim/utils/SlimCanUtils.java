package com.canboxsetting.set.slim.utils;

import android.content.Context;
import android.text.TextUtils;

import com.common.utils.BroadcastUtil;
import com.common.utils.MachineConfig;
import com.zhuchao.android.fbase.ByteUtils;
import com.zhuchao.android.fbase.MMLog;

public class SlimCanUtils {
    private static final String TAG = "SlimCanUtils";
    private static SlimCanUtils instance;

    private SlimCanUtils() {

    }

    public static SlimCanUtils getInstance() {
        if (instance == null) {
            instance = new SlimCanUtils();
        }
        return instance;
    }

    public void sendCanboxInfo(Context context, byte d0, byte d1) {
        byte[] buf = new byte[]{
                0x00, (byte) 0xA4, 0x01, 0x00, 0x02, d0, d1, (byte) (0xA7 + d0 + d1)
        };
        MMLog.d(TAG, "sendCanboxInfo: buf = " + ByteUtils.BuffToHexStr(buf));
        BroadcastUtil.sendCanboxInfo(context, buf);
    }

    public byte[] getSettingData() {
        String settingData = MachineConfig.getProperty("SETTING_UPDATE_DATA");
        if (!TextUtils.isEmpty(settingData)){
            MMLog.d(TAG, "getSettingData: settingData = " + settingData);
            byte[] settings = ByteUtils.HexStrToByteArray(settingData.replaceAll(" ",""));
            MMLog.d(TAG, "getSettingData: setting size = " + settings.length + "   setting = " + ByteUtils.BuffToHexStr(settings));
            return settings;
        }
        return null;
    }
}
