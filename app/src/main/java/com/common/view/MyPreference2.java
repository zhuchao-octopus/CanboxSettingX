package com.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class MyPreference2 extends Preference {

    private View mMainView;

    public MyPreference2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyPreference2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPreference2(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        mMainView = holder.itemView;
    }

    ///protected View onCreateView(ViewGroup parent) {
    ///	mMainView = super.onCreateView(parent);
    ///	return mMainView;
    ///}

    public View getMainView() {
        return mMainView;
    }
}
