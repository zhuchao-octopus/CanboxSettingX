package com.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class GlobalDef {

    public static final String TAG = "GlobalDef";
    public final static String CAMERA_SIGNAL_701 = "/sys/class/misc/mst701/device/lock";
    public final static String CAMERA_SIGNAL_PX5 = "/sys/class/ak/source/cvbs_status";
    private final static String CAMERA_INDEX_701 = "/sys/class/misc/mst701/device/source";
    private final static String CAMERA_INDEX_PX5 = "/sys/class/ak/source/cam_ch";
    public static int mSource = MyCmd.SOURCE_NONE;
    public static int mSourceWillUpdate = MyCmd.SOURCE_NONE;
    public static int mReverseStatus = 0;
    public static Context mContext;
    public static int mTempUnit = 0;
    public static boolean USE_OLD_CAMER_IF_NO_DVR = false;
    public static int mCameraTryNum = 0;
    public static String CAMERA_SIGNAL = CAMERA_SIGNAL_701;
    private static int mModelId;
    private static int mProId;
    private static int mCarConfig;
    private static int mCameraPreview = 0;
    private static boolean mOpenCameraPreview = false;
    private static String mSystemUI;
    private static String CAMERA_INDEX = CAMERA_INDEX_701;

    public static int getModelId() {
        return mModelId;
    }

    public static void setModelId(int m) {
        mModelId = m;
    }

    public static int getProId() {
        return mProId;
    }

    public static void setProId(int m) {
        mProId = m;
    }

    public static int getCarConfig() {
        return mCarConfig;
    }

    public static void setCarConfig(int m) {
        mCarConfig = m;
    }

    public static void init(Context c) {
        mContext = c;
        if (Util.isPX5()) {
            CAMERA_INDEX = CAMERA_INDEX_PX5;
            CAMERA_SIGNAL = CAMERA_SIGNAL_PX5;
        }
        //		mTempUnit = SettingProperties.getIntProperty(c, SettingProperties.CANBOX_TEMP_UNIT);
        updateTempUnit(c);
    }

    public static void updateTempUnit(Context c) {
        mTempUnit = SettingProperties.getIntProperty(c, SettingProperties.CANBOX_TEMP_UNIT);
    }

    public static boolean isCameraTryNumMax() {
        mCameraTryNum++;
        return (mCameraTryNum > 4);
    }

    public static int getCameraPreview() {
        return mCameraPreview;
    }

    public static void setCameraPreview(int preview) {
        mCameraPreview = preview;
        if (preview > 0) {
            mCameraTryNum = 0;
        }
    }

    public static boolean getCameramOpenCameraPreview() {
        return mOpenCameraPreview;
    }

    public static void setCameramOpenCameraPreview(boolean preview) {
        mOpenCameraPreview = preview;
        if (preview) {
            mCameraTryNum = 0;
        }
    }

    public static boolean isGLCamera() {
        boolean ret = false;
        if (Util.isGLCamera()) {
            if (USE_OLD_CAMER_IF_NO_DVR) {
                if ("1".equals(SettingProperties.getProperty(mContext, SettingProperties.KEY_DVR_RECORDING)) && "1".equals(SettingProperties.getProperty(mContext, SettingProperties.KEY_DVR_ACTITUL_RECORDING))) {
                    ret = true;
                }
            } else {
                ret = true;
            }
        }
        Log.d(TAG, "isGLCamera" + ret);
        return ret;
    }

    public static String getSystemUI() {
        return mSystemUI;
    }

    public static int getCameraSource() {
        return Util.getFileValue(CAMERA_INDEX);
    }

    public static void setCameraSource(int source) {
        Util.setFileValue(CAMERA_INDEX, source);

    }

    @SuppressLint("InvalidWakeLockTag")
    public static void wakeLockOnce() {
        if (mContext != null) {
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            WakeLock mWakeLockOne = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
            if (null != mWakeLockOne) {
                mWakeLockOne.acquire();
                mWakeLockOne.release();
            }
        }
    }

}
