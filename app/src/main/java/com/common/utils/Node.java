package com.common.utils;

public class Node {

    public final static int TYPE_CUSTOM = -1;
    public final static int TYPE_BUFF1 = 1;
    public final static int TYPE_BUFF2 = 2;
    public final static int TYPE_BUFF3 = 3;
    public final static int TYPE_BUFF4 = 4;
    public final static int TYPE_BUFF8 = 8;
    public final static int TYPE_BUFF9 = 9;
    public final static int TYPE_BUFF14 = 14;
    public final static int TYPE_DEFINE1 = 0x10;

    // the mMask 0xff00 is buf index. mMask 0xff is mask.
    // it only need 1 byte mMask
    public final static int TYPE_BUFF1_INDEX = 0x100;

    public final static int TYPE_DEF_BUFF = TYPE_BUFF4;

    public int mShow; // canbox return if to show this Preference
    public int mStatus; // canbox return status
    public int mMask;// canbox return mask
    public int mCmd; // cmd set to canbox
    public String mKey; // Preference id
    public int mType;

    /*
     * default type 0: canbox return status 4 byte 1: canbox return status 1
     * byte
     */

    public Node(String key, int cmd, int status, int mask, int show, int type) {
        mKey = key;
        mCmd = cmd;
        mStatus = status;
        mShow = show;
        mMask = mask;
        mType = type;
    }

    public Node(String key, int cmd, int status, int mask, int show) {
        this(key, cmd, status, mask, show, TYPE_DEF_BUFF);
    }

    public Node(String key, int cmd, int status, int mask) {
        this(key, cmd, status, mask, 0);
    }

    public Node(String key, int cmd, int status) {
        this(key, cmd, status, 0, 0);
    }

    public Node(String key, int cmd) {
        this(key, cmd, 0, 0, 0);
    }
}
