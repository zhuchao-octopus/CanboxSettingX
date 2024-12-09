package com.canboxsetting.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.canboxsetting.R;

import java.util.ArrayList;
import java.util.List;

public class ACSpeedLevelSelect extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "ACLevelSelect";
    private static int maxLevel = 7;
    private static int currLevel = 7;
    private List<ImageView> viewList;
    private OnACLevelClickListener listener;
    public ACSpeedLevelSelect(Context context) {
        super(context);
        init();
    }

    public ACSpeedLevelSelect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ACSpeedLevelSelect(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        viewList = new ArrayList<>();
        for (int i = 0;i < maxLevel;i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setTag(maxLevel - 1 -i);
            imageView.setImageResource(R.mipmap.slim_ac_temperature_strip_off);
            imageView.setPadding(0,12,0,12);
            imageView.setOnClickListener(this);
            viewList.add(imageView);
            addView(imageView);
        }
    }

    @Override
    public void onClick(View v) {
        int currLevel = (int) v.getTag();
        if (listener != null) {
            listener.onACLevelClickListener(currLevel);
        }
        Log.d(TAG, "onClick: currLevel = " + currLevel);
//        if (viewList != null && viewList.size() > 0) {
//            updateLevel(currLevel);
//        }
    }

    private void updateLevel(int level) {
        currLevel = level;
        for (ImageView view:viewList) {
            if ((int) view.getTag() < level) {
                view.setImageResource(R.mipmap.slim_ac_speed_strip_on);
            } else {
                view.setImageResource(R.mipmap.slim_ac_speed_strip_off);
            }
        }
    }

    public void setLevel(int level) {
        updateLevel(level);
    }

    public void setListener(OnACLevelClickListener listener) {
        this.listener = listener;
    }
    public void removeListener() {
        listener = null;
    }

    public interface OnACLevelClickListener {
        void onACLevelClickListener(int level);
    }
}
