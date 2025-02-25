package com.canboxsetting.view;

import android.app.Instrumentation;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.canboxsetting.R;
import com.common.utils.BroadcastUtil;
import com.common.utils.MyCmd;
import com.common.utils.UtilSystem;

public class SlimBottomShortcut extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "SlimBottomShortcut";
    private ImageView home,music,avm360,ac, wind,btPhone,setting;
    public SlimBottomShortcut(Context context) {
        super(context);
        init();
    }

    public SlimBottomShortcut(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlimBottomShortcut(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = View.inflate(getContext(), R.layout.slim_bottom_shortcut_layout, this);
        home = findViewById(R.id.slim_bottom_home);
        home.setOnClickListener(this);
        music = findViewById(R.id.slim_bottom_music);
        music.setOnClickListener(this);
        avm360 = findViewById(R.id.slim_bottom_360);
        avm360.setOnClickListener(this);
        ac = findViewById(R.id.slim_bottom_ac);
        ac.setOnClickListener(this);
        wind = findViewById(R.id.slim_bottom_wind);
        wind.setOnClickListener(this);
        btPhone = findViewById(R.id.slim_bottom_bt_phone);
        btPhone.setOnClickListener(this);
        setting = findViewById(R.id.slim_bottom_setting);
        setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.slim_bottom_home:
                simulateHomeKey();
                break;
            case R.id.slim_bottom_music:
                UtilSystem.doRunActivity(getContext(), "com.octopus.android.carapps", "com.octopus.android.carapps.audio.MusicActivity");
                break;
            case R.id.slim_bottom_360:
                BroadcastUtil.sendToCarServiceCmd(getContext(), MyCmd.Cmd.KEY_ENTER_REVERSE);
                break;
            case R.id.slim_bottom_ac:
            case R.id.slim_bottom_wind:
                UtilSystem.doRunActivity(getContext(), "com.canboxsetting", "com.canboxsetting.CanAirControlActivity");
                break;
            case R.id.slim_bottom_bt_phone:
                UtilSystem.doRunActivity(getContext(), "com.my.bt", "com.my.bt.ATBluetoothActivity");
                break;
            case R.id.slim_bottom_setting:
                UtilSystem.doRunActivity(getContext(), "com.canboxsetting", "com.canboxsetting.MainActivity");
                break;
        }
    }

    private void simulateHomeKey() {
        // 创建 Instrumentation 实例
        Instrumentation inst = new Instrumentation();

        // 模拟按下 Home 键
        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
    }
}
