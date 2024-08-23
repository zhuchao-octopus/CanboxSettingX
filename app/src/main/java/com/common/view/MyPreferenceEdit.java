package com.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.canboxsetting.R;

public class MyPreferenceEdit extends Preference {

    public int title;

    public MyPreferenceEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public MyPreferenceEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyPreferenceEdit(Context context) {
        super(context);
        init(context, null);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        //ViewGroup viewGroup = (ViewGroup) holder.itemView;
        initView(holder.itemView);
    }

    private void init(Context context, AttributeSet attrs) {
        setLayoutResource(R.layout.preference_edit);
    }

    private void initView(View view) {
        ((Button) view.findViewById(R.id.prefrence_a)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mButtonCallBack != null) {
                    mButtonCallBack.callback(getKey(), true);
                }
            }
        });
        ((Button) view.findViewById(R.id.prefrence_m)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mButtonCallBack != null) {
                    mButtonCallBack.callback(getKey(), false);
                }
            }
        });

    }

    public void setCallback(IButtonCallBack cb) {
        mButtonCallBack = cb;
    }

    private IButtonCallBack mButtonCallBack;

    public static interface IButtonCallBack {
        public void callback(String key, boolean add);
    }

    ;
}
