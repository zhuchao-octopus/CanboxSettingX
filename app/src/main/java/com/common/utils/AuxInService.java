package com.common.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.common.service.ServiceBase;

public class AuxInService extends ServiceBase {
    public static final String TAG = "RadioService";

    private static AuxInService mThis;
    private static Handler[] mHandlerUICallBack = new Handler[2];
    private int mReoverSource = -1;

    public AuxInService(Context context) {
        super(context);
    }

    public static AuxInService getInstanse(Context context) {
        if (mThis == null) {
            mThis = new AuxInService(context);

            mThis.onCreate();
        }
        return mThis;
    }

    public static void setUICallBack(Handler cb, int index) {
        mHandlerUICallBack[index] = cb;
    }

    private static void callBackToUI(int what, int status, Object obj) {
        for (int i = 0; i < mHandlerUICallBack.length; ++i) {
            if (mHandlerUICallBack[i] != null) {
                mHandlerUICallBack[i].sendMessageDelayed(mHandlerUICallBack[i].obtainMessage(what, status, 0, obj), 20);
            }
        }
    }

    public void onDestroy() {
    }

    public void onCreate() {

    }

    public void doKeyControl(int code) {

    }

    public void doCmd(int cmd, Intent intent) {
        switch (cmd) {
            case MyCmd.Cmd.REVERSE_STATUS:
                int status = intent.getIntExtra(MyCmd.EXTRA_COMMON_DATA, 0);

                if (status == 1) {
                    AuxInUI aui;
                    for (int i = 0; i < 2; i++) {
                        aui = AuxInUI.mUI[i];

                        // Log.d("tt", "aux:"+aui);

                        if (aui != null && !aui.mPause) {

                            // Log.d("tt", "aux:"+aui.mPause);

                            mReoverSource = i;
                            aui.reverseStart();

                            break;
                        }
                    }
                } else {
                    if (mReoverSource >= 0 && mReoverSource < 2) {
                        AuxInUI.mUI[mReoverSource].reverseStop();
                        mReoverSource = -1;
                    }
                }
                break;
        }

    }

    public int getSource() {
        return AuxInUI.SOURCE;
    }
}
