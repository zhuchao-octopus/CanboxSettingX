package com.canboxsetting;

public class FragmentProData {

    public String mId;
    public Class<?> mFragmentAC;
    public Class<?> mFragmentInfo;
    public Class<?> mFragmentTPMS;
    public Class<?> mFragmentSet;
    public Class<?> mFragmentCD;

    public FragmentProData(String id, Class<?> info, Class<?> set, Class<?> tpms, Class<?> ac, Class<?> cd) {
        mId = id;
        mFragmentInfo = info;
        mFragmentSet = set;
        mFragmentTPMS = tpms;
        mFragmentAC = ac;
        mFragmentCD = cd;
    }

}
