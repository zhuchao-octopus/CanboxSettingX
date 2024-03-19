package com.common.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.AttributeSet;
import android.util.Log;

import com.canboxsetting.R;
import com.common.util.MachineConfig;

public class MyListViewAdapterRadio extends ArrayAdapter<String> {

	private int mLayout;
	private Context mActivity;
	private int mTextId;

	// private String mTrack;

	LayoutInflater mInflater;
	private int mCustomListFocusColor;

	public MyListViewAdapterRadio(Context context, int layout) {
		super(context, layout);
		mLayout = layout;
		mActivity = context;
		mTextId = R.id.list_text;
		mInflater = LayoutInflater.from(mActivity);

		mCustomListFocusColor = context.getResources().getColor(
				R.color.list_hilight_colre);

	}

	public int getCount() {
		return mItem.size();
	}

	public int getSelectFreq(int pos) {
		int ret = -1;
		if (pos < mItem.size()) {
			ret = mItem.get(pos).freq;
		}
		return ret;
	}
	
	public int getSelectIndex(int pos) {
		int ret = -1;
		if (pos < mItem.size()) {
			ret = mItem.get(pos).index;
		}
		return ret;
	}

	public static class TextShow {
		public String text;
		public int index;
		public int freq;

	}

	public static class ViewHolder {
		public TextView text;
		// public ImageView playing;
		public int index;

	}

	private ArrayList<TextShow> mItem = new ArrayList<TextShow>();

	public View getView(int position, View convertView, ViewGroup parent) {
		if (mItem.size() == 0 || position > mItem.size())
			return null;

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(mLayout, null, false);
			viewHolder = new ViewHolder();
			viewHolder.text = (TextView) convertView.findViewById(mTextId);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.text.setText((mItem.get(position).index + 1) + "  "
				+ mItem.get(position).text);
		viewHolder.index = position;
		if (mPos == position) {
			viewHolder.text.setTextColor(mCustomListFocusColor);
		} else {

			viewHolder.text.setTextColor(mActivity.getResources().getColor(
					R.color.list_normal_colre));
		}

		return convertView;

	}

	private int mPos = -1;

	public void setSelectItem(int pos) {

		if (mPos != pos) {
			mPos = pos;
			notifyDataSetChanged();
		}
	}

	public void addList(int index, String name, int freq) {
		boolean exist = false;
		for (int i = 0; i < mItem.size(); ++i) {
			if (index == mItem.get(i).index) {
				exist = true;
				mItem.get(i).text = name;
				mItem.get(i).freq = freq;
				notifyDataSetChanged();
				break;
			}
		}

		if (!exist) {
			TextShow ts = new TextShow();
			ts.index = index;
			ts.text = name;
			ts.freq = freq;

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

}
