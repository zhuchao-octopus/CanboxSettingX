package com.canboxsetting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.canboxsetting.R;
import com.canboxsetting.adapter.bean.SlimVehicleSettingItemBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SlimVehicleSettingAdapter extends RecyclerView.Adapter<SlimVehicleSettingAdapter.MyViewHolder> {
    private Context mContext;
    private Map<String, SlimVehicleSettingItemBean> settingItemBeanMap;
    private int[] titleNames;
    private OnSlimVehicleClickListener listener;
    public SlimVehicleSettingAdapter(Context context,int[] titleNames, Map<String, SlimVehicleSettingItemBean> beanMap, OnSlimVehicleClickListener onSlimVehicleClickListener) {
        this.mContext = context;
        this.settingItemBeanMap = beanMap;
        this.listener = onSlimVehicleClickListener;
        this.titleNames = titleNames;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.slim_vehicle_setting_item1,parent,false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SlimVehicleSettingItemBean bean = settingItemBeanMap.get(mContext.getString(titleNames[position]));
        if (bean.getIcon() == -1) {
            holder.icon.setVisibility(View.INVISIBLE);
        } else {
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageResource(bean.getIcon());
        }
        holder.title.setText(bean.getTitleName());
        switch (bean.getType()) {
            case TYPE1:
                holder.select.setVisibility(View.GONE);
                holder.selectIv.setVisibility(View.VISIBLE);
                holder.showTv2.setVisibility(View.GONE);
                holder.showTv.setVisibility(View.VISIBLE);
                holder.showTv.setText(bean.getSettingValue());
                break;
            case TYPE2:
                holder.select.setVisibility(View.VISIBLE);
                holder.select.setChecked(bean.isSelect());
                holder.showTv2.setVisibility(View.GONE);
                holder.selectIv.setVisibility(View.GONE);
                holder.showTv.setVisibility(View.GONE);

                break;
            case TYPE3:
                holder.select.setVisibility(View.GONE);
                holder.selectIv.setVisibility(View.GONE);
                holder.showTv.setVisibility(View.GONE);
                holder.showTv2.setVisibility(View.VISIBLE);
                holder.showTv2.setText(bean.getSettingValue());
                break;
            case TYPE4:
                break;
            default:
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSlimVehicleClickListener(position, bean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (settingItemBeanMap != null && settingItemBeanMap.keySet().size() > 0) {
            return settingItemBeanMap.keySet().size();
        }
        return 0;
    }

    public void setSettingItemBeanList(Map<String,SlimVehicleSettingItemBean> settingItemBeanMap) {
        this.settingItemBeanMap = settingItemBeanMap;
        notifyDataSetChanged();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView icon;
        public TextView title;
        public Switch select;
        public TextView showTv;
        public TextView showTv2;
        private ImageView selectIv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.show_setting_icon);
            title = itemView.findViewById(R.id.show_setting_title);
            select = itemView.findViewById(R.id.slim_select_switch);
            select.setEnabled(false);
            select.setFocusable(false);
            select.setClickable(false);
            showTv = itemView.findViewById(R.id.show_vehicle_tv);
            selectIv = itemView.findViewById(R.id.slim_select_bt);
            showTv2 = itemView.findViewById(R.id.show_vehicle_tv2);        }
    }

    public interface OnSlimVehicleClickListener{
        void onSlimVehicleClickListener(int position,SlimVehicleSettingItemBean bean);
    }

    public enum SlimVehicleSettingType{
        TYPE1,TYPE2,TYPE3,TYPE4
    }
}
