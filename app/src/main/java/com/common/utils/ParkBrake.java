package com.common.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ParkBrake {

    public static boolean checkCamera0IfFacing = false;
    public static int dvrExist = 0;

    public static String readLine(String path) {

        File file = new File(path);

        String source = null;
        if (file.exists()) {
            BufferedReader buf;

            try {
                FileReader fr = new FileReader(file);
                buf = new BufferedReader(fr);
                source = buf.readLine();
                buf.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return source;
    }

    public static boolean isBrake() {

        String s = readLine("/sys/class/ak/source/reaksw");
        if ("0".equals(s)) {
            return true;
        } else {
            String source = readLine("/sys/class/ak/source/brake_status");

            if (source != null && source.equals("1")) {
                return true;
            }

            return false;
        }

    }

    public static void saveCameraStatasIfSleep() {
        Log.d("ParkBrake", "saveCameraStatas");
        checkCamera0IfFacing = false;
        if (android.hardware.Camera.getNumberOfCameras() >= 2) {
            dvrExist = 2;
        }
    }

    public static int isSignal() {
        String source;

        // if(Util.isPX5()){
        source = readLine(GlobalDef.CAMERA_SIGNAL);
        // } else {
        // source = readLine("/sys/class/misc/mst701/device/lock");
        // }
        Log.w("ParkBrake", "isSignal" + ":" + source);
        if (source != null && source.equals("1")) {
            //			if (!checkCamera0IfFacing && Util.isRKSystem()) {
            //				try {
            //					android.hardware.Camera.CameraInfo mCameraInfo = new android.hardware.Camera.CameraInfo();
            //					android.hardware.Camera.getCameraInfo(0, mCameraInfo);
            //					Log.w("ParkBrake", "camera_surfaceview "
            //							+ mCameraInfo.facing +":"+ dvrExist);
            //					if (mCameraInfo.facing == 1) {
            //						return 0;
            //					}
            //					if (dvrExist > 0) {
            //						--dvrExist;
            //						return 0;
            //					}
            //					checkCamera0IfFacing = true;
            //				} catch (RuntimeException e) {
            //					Log.w("ParkBrake", "camera_surfaceview 0"
            //							+ " maybe doesn't exist");
            //					return 0;
            //				}
            //			}
            return 1;
        }

        return 0;

    }
}
