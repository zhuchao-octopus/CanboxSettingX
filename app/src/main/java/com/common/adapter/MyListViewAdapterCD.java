package com.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.canboxsetting.R;

import java.util.ArrayList;

public class MyListViewAdapterCD extends ArrayAdapter<String> {

    LayoutInflater mInflater;
    private int mLayout;
    private Context mActivity;

    // private String mTrack;
    private int mTextId;
    private int mCustomListFocusColor;
    private ArrayList<TextShow> mItem = new ArrayList<TextShow>();
    private int mPos = -1;

    public MyListViewAdapterCD(Context context, int layout) {
        super(context, layout);
        mLayout = layout;
        mActivity = context;
        mTextId = R.id.list_text;
        mInflater = LayoutInflater.from(mActivity);

        mCustomListFocusColor = context.getResources().getColor(R.color.list_hilight_colre);

    }

    public int getCount() {
        return mItem.size();
    }

    public int getSelectIndex(int pos) {
        int ret = -1;
        if (pos < mItem.size()) {
            ret = mItem.get(pos).index;
        }
        return ret;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (mItem.size() == 0 || position > mItem.size()) return null;

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayout, null, false);
            viewHolder = new ViewHolder();
            viewHolder.text = (TextView) convertView.findViewById(mTextId);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text.setText(mItem.get(position).index + "  " + mItem.get(position).text);
        viewHolder.index = position;
        if (mPos == position) {
            viewHolder.text.setTextColor(mCustomListFocusColor);
        } else {

            viewHolder.text.setTextColor(mActivity.getResources().getColor(R.color.list_normal_colre));
        }

        return convertView;

    }

    public void setSelectItem(int pos) {

        if (mPos != pos) {
            mPos = pos;
            notifyDataSetChanged();
        }
    }

    public void addList(int index, String name) {
        boolean exist = false;
        for (int i = 0; i < mItem.size(); ++i) {
            if (index == mItem.get(i).index) {
                exist = true;
                mItem.get(i).text = name;
                notifyDataSetChanged();
                break;
            }
        }

        if (!exist) {
            TextShow ts = new TextShow();
            ts.index = index;
            ts.text = name;

            // sort
            if (mItem.size() > 0) {
                int i = 0;
                for (i = 0; i < mItem.size(); ++i) {
                    if (mItem.get(i).index > index) {
                        // if (i > 0) {
                        // i--;
                        // }
                        break;
                    }
                }
                mItem.add(i, ts);
            } else {
                mItem.add(ts);
            }

            notifyDataSetChanged();
        }

    }

    public static class TextShow {
        public String text;
        public int index;

    }

    public static class ViewHolder {
        public TextView text;
        // public ImageView playing;
        public int index;

    }

}
