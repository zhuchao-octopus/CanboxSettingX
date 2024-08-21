package com.common.utils;

import android.content.Context;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;

import com.common.view.MyPreference;
import com.common.view.MyPreferenceEdit;
import com.common.view.MyPreferenceSeekBar;

public class NodePreference {

    public String mKey;
    public int mStatus; // canbox return status
    public int mMask;// canbox return mask
    public int mCmd; // cmd set to canbox
    public int mType;
    public int mEntry;
    public int mEntryValue;

    public final static int PREFERENCE_MASK = 0xff000000;
    public final static int SCREENPREFERENCE = 0x1000000;
    public final static int SWITCHPREFERENCE = 0x2000000;
    public final static int LISTPREFERNCE = 0x3000000;
    public final static int SEEKBARPREFERNCE = 0x4000000;
    public final static int EDITPREFERNCE = 0x5000000;
    public final static int PREFERENCECATEGORY = 0x6000000;
    public final static int MYPREFERENCE = 0x7000000;

    public NodePreference(String id) {
        mKey = id;
        mType = PREFERENCECATEGORY;
    }

    public NodePreference(String id, int cmd) {
        mKey = id;
        mCmd = cmd;
        mType = MYPREFERENCE;
    }

    public NodePreference(String id, int cmd, int type) {
        mKey = id;
        mCmd = cmd;
        mStatus = 0;
        mMask = 0;
        mType = type | SCREENPREFERENCE;
        mEntry = 0;
        mEntryValue = 0;
    }

    public NodePreference(String id, int cmd, int status, int type) {
        mKey = id;
        mCmd = cmd;
        mStatus = status;
        mMask = 0;
        mType = type | SCREENPREFERENCE;
        mEntry = 0;
        mEntryValue = 0;
    }

    public NodePreference(String id, int cmd, int status, int mask, int type) {
        mKey = id;
        mCmd = cmd;
        mStatus = status;
        mMask = mask;
        mType = type | SWITCHPREFERENCE;
        mEntry = 0;
        mEntryValue = 0;
    }


    public NodePreference(String id, int cmd, int status, int mask, int type, int seekMax) {
        mKey = id;
        mCmd = cmd;
        mStatus = status;
        mMask = mask;
        mType = type | EDITPREFERNCE;

        mEntry = 0;
        mEntryValue = seekMax;
    }

    public NodePreference(String id, int cmd, int status, int mask, int type, int seekMin, int seekMax, int step) {
        mKey = id;
        mCmd = cmd;
        mStatus = status;
        mMask = mask;
        mType = type | SEEKBARPREFERNCE;
        mEntry = seekMin;
        mEntryValue = seekMax | (step << 16);
    }

    //	public NodePreference(String id, int cmd, int status, int mask, int type,
    //			int entry) {
    //		this(id, cmd, status, mask, type | LISTPREFERNCE, entry, 0);
    //	}

    public NodePreference(String id, int cmd, int status, int mask, int type, int entry, int entryValue) {
        mKey = id;
        mCmd = cmd;
        mStatus = status;
        mMask = mask;
        mType = type | LISTPREFERNCE;
        mEntry = entry;
        mEntryValue = entryValue;
    }

    public Preference createPreference(Context c) {

        try {
            if ((mType & PREFERENCE_MASK) == SWITCHPREFERENCE) {
                SwitchPreference sp = new SwitchPreference(c);
                sp.setTitle(ResourceUtil.getStringId(c, mKey));
                sp.setKey(mKey);
                return sp;
            } else if ((mType & PREFERENCE_MASK) == LISTPREFERNCE) {
                ListPreference sp = new ListPreference(c);
                sp.setTitle(ResourceUtil.getStringId(c, mKey));
                sp.setKey(mKey);
                sp.setDialogTitle(ResourceUtil.getStringId(c, mKey));

                if (mEntry > 0) {
                    sp.setEntries(mEntry);
                } else {
                    sp.setEntries(ResourceUtil.getArrayId(c, mKey));
                }
                if (mEntryValue > 0) {
                    sp.setEntryValues(mEntryValue);
                } else {
                    sp.setEntryValues(ResourceUtil.getArrayId(c, mKey + "_value"));
                }

                return sp;
            } else if ((mType & PREFERENCE_MASK) == SCREENPREFERENCE) {
                Preference sp = new Preference(c, null);
                sp.setTitle(ResourceUtil.getStringId(c, mKey));
                sp.setKey(mKey);
                return sp;
            } else if ((mType & PREFERENCE_MASK) == EDITPREFERNCE) {
                MyPreferenceEdit sp = new MyPreferenceEdit(c, null);
                sp.setTitle(ResourceUtil.getStringId(c, mKey));
                sp.setKey(mKey);
                return sp;
            } else if ((mType & PREFERENCE_MASK) == SEEKBARPREFERNCE) {
                MyPreferenceSeekBar sp = new MyPreferenceSeekBar(c, null);
                sp.updateSeekBar(mEntry, mEntryValue & 0xffff, (mEntryValue & 0xff0000) >> 16);
                sp.setTitle(ResourceUtil.getStringId(c, mKey));
                sp.setKey(mKey);
                return sp;
            } else if ((mType & PREFERENCE_MASK) == PREFERENCECATEGORY) {
                PreferenceCategory sp = new PreferenceCategory(c, null);
                sp.setTitle(ResourceUtil.getStringId(c, mKey));
                return sp;
            } else if ((mType & PREFERENCE_MASK) == MYPREFERENCE) {
                MyPreference sp = new MyPreference(c, null);
                sp.setTitle(ResourceUtil.getStringId(c, mKey));
                sp.setKey(mKey);
                return sp;
            }
        } catch (Exception e) {
            Log.d("fcck", mKey + "::createPreference: fail!!!");
        }
        return null;
    }
}
