package com.canboxsetting.set.slim.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.canboxsetting.R;

import java.util.ArrayList;
import java.util.List;

public class SlimAVMQuittingSpeedDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "SlimAVMQuittingSpeedDia";
    private ImageButton ok,cancel;
    private Switch isOpenSpeed;
    private WheelView selectSpeed;
    private List<String> selectSpeedData;
    private OnAVMDialogStateListener listener;
    private int currSelect = 0;
    public SlimAVMQuittingSpeedDialog(@NonNull Context context,OnAVMDialogStateListener listener) {
        this(context,R.style.dialog,listener);
        this.listener = listener;
    }

    public SlimAVMQuittingSpeedDialog(@NonNull Context context, int themeResId,OnAVMDialogStateListener listener) {
        super(context, themeResId);
        this.listener = listener;
        init();
    }
    private void init() {
        setContentView(R.layout.slim_avm_dialog_layout);
        ok = findViewById(R.id.dialog_ok);
        ok.setOnClickListener(this);
        cancel = findViewById(R.id.dialog_cancel);
        cancel.setOnClickListener(this);
        isOpenSpeed = findViewById(R.id.slim_select_speed);
        isOpenSpeed.setVisibility(View.GONE);
        isOpenSpeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: isChecked = " + isChecked);
            }
        });
        selectSpeed = findViewById(R.id.speed_select_view);
        selectSpeedData = new ArrayList<>();
        for (int i = 30;i<= 60; ) {
            selectSpeedData.add(i + "Km/h");
            i +=10;
        }
        selectSpeed.setData(selectSpeedData);
        selectSpeed.setDefault(currSelect);
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

    public void setSelectPosition(int position) {
        currSelect = position;
        if (selectSpeed != null) {
            selectSpeed.setDefault(currSelect);
        }
    }

    public interface OnAVMDialogStateListener{
        void onAVMDialogStateListener(boolean isOpen, int position, String value);
    }
}
