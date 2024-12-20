package com.canboxsetting.set.slim.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.canboxsetting.R;

public class SlimLightSelectDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "SlimAVMQuittingSpeedDia";
    private ImageButton ok,cancel;
    private Switch isOpenSpeed;
    private RelativeLayout light, lightHorn;
    private OnLightSelectListener listener;
    private int currState;
    public SlimLightSelectDialog(@NonNull Context context, int state, OnLightSelectListener listener) {
        this(context,R.style.dialog,listener,state);
        this.listener = listener;
    }

    public SlimLightSelectDialog(@NonNull Context context, int themeResId, OnLightSelectListener listener, int state) {
        super(context, themeResId);
        this.listener = listener;
        this.currState = state;
        init();
    }
    private void init() {
        setContentView(R.layout.slim_security_dialog_layout);
        ok = findViewById(R.id.dialog_ok);
        ok.setOnClickListener(this);
        cancel = findViewById(R.id.dialog_cancel);
        cancel.setOnClickListener(this);
        isOpenSpeed = findViewById(R.id.slim_select_speed);
        isOpenSpeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: isChecked = " + isChecked);
            }
        });
        isOpenSpeed.setVisibility(View.GONE);
        light = findViewById(R.id.light_model);
        lightHorn = findViewById(R.id.light_horn_model);
        light.setOnClickListener(this);
        lightHorn.setOnClickListener(this);
        if (currState == 0) {
            light.setSelected(true);
            lightHorn.setSelected(false);
        } else {
            light.setSelected(false);
            lightHorn.setSelected(true);
        }
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            // 设置宽度和高度
            layoutParams.width = 1000; // 或者指定宽度值，例如 300
            layoutParams.height = 525; // 或者指定高度值
            window.setAttributes(layoutParams);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_ok:
                if (listener != null) listener.onLightSelectListener(currState);
                dismiss();
                break;
            case R.id.dialog_cancel:
                dismiss();
                break;
            case R.id.light_horn_model:
                currState = 1;
                lightHorn.setSelected(true);
                light.setSelected(false);
                break;
            case R.id.light_model:
                currState = 0;
                light.setSelected(true);
                lightHorn.setSelected(false);
                break;
        }
    }

    public void update(byte securityState) {
        light.setSelected(securityState == 0);
        light.setSelected(securityState == 1);
    }

    public interface OnLightSelectListener{
        void onLightSelectListener(int state);
    }
}
