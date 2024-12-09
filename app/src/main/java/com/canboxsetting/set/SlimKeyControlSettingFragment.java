package com.canboxsetting.set;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.canboxsetting.MyFragment;
import com.canboxsetting.R;
import com.common.utils.GlobalDef;
import com.zhuchao.android.fbase.MMLog;

public class SlimKeyControlSettingFragment extends MyFragment {
    private View mMainView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.ac_peugeot_slim, container, false);
        if ((GlobalDef.getProId() == 186)) {
            mMainView.findViewById(R.id.wheel).setVisibility(View.GONE);
        }

        return mMainView;
    }
}
