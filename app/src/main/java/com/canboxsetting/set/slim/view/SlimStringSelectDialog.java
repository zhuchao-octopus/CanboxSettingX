package com.canboxsetting.set.slim.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.canboxsetting.R;

import java.util.ArrayList;
import java.util.List;

public class SlimStringSelectDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "SlimAVMQuittingSpeedDia";
    private ImageButton ok,cancel;
    private Switch isOpenSpeed;
    private WheelView selectSpeed;
    private List<String> selectSpeedData;
    private OnAVMDialogStateListener listener;
    private int defPosition;
    public SlimStringSelectDialog(@NonNull Context context, List<String> showData, int selectPosition, OnAVMDialogStateListener listener) {
        this(context,R.style.dialog,showData,selectPosition,listener);
        this.listener = listener;
    }

    public SlimStringSelectDialog(@NonNull Context context, int themeResId, List<String> showData, int selectPosition, OnAVMDialogStateListener listener) {
        super(context, themeResId);
        this.listener = listener;
        this.selectSpeedData = showData;
        this.defPosition = selectPosition;
        init();
    }
    private void init() {
        setContentView(R.layout.slim_avm_dialog_layout);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            // 设置宽度和高度
            layoutParams.width = 1000; // 或者指定宽度值，例如 300
            layoutParams.height = 525; // 或者指定高度值
            window.setAttributes(layoutParams);
        }
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
        selectSpeed = findViewById(R.id.speed_select_view);
        selectSpeed.setData(selectSpeedData);
        selectSpeed.setDefault(defPosition);
        selectSpeed.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                Log.d(TAG, "endSelect: id = " + id + "   text = " + text);
            }

            @Override
            public void selecting(int id, String text) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_ok:
                if (listener != null) listener.onAVMDialogStateListener(isOpenSpeed.isChecked(),selectSpeed.getSelected(),selectSpeedData.get(selectSpeed.getSelected()));
                dismiss();
                break;
            case R.id.dialog_cancel:
                dismiss();
                break;
        }
    }

    public void setDefPosition(int position) {
        if (selectSpeed != null) selectSpeed.setDefault(position);
    }

    public interface OnAVMDialogStateListener{
        void onAVMDialogStateListener(boolean isOpen, int position, String value);
    }
}
