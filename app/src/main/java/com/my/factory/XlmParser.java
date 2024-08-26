package com.my.factory;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.canboxsetting.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This activity plays a video from a specified URI.
 */
public class XlmParser {

    private ArrayList<Translation> mTranslations;
    private ArrayList<Category> mCategorys;
    private ArrayList<Node> mCarConfig;
    private ArrayList<Menu> mMenus;

    public void parserCarMenuXml(XmlResourceParser xml) {

        try {
            Sub sub = null;
            Menu menu = null;
            String cname;
            int cvalue;

            while (xml.getEventType() != XmlResourceParser.END_DOCUMENT) { // XML开始解析
                String name = null;
                if (xml.getEventType() == XmlResourceParser.START_TAG) { // 标签开头

                    name = xml.getName();// 标签的名字

                    if (name.trim().toLowerCase().equals("menu")) {
                        if (xml.getAttributeCount() > 1) {
                            if (xml.getAttributeName(0).trim().toLowerCase().equals("name") && xml.getAttributeName(1).trim().toLowerCase().equals("value")) {

                                cname = xml.getAttributeValue(0);
                                cname = getTranslation(cname);
                                cvalue = xml.getAttributeIntValue(1, 0);
                                menu = new Menu(cname, cvalue);
                                if (mMenus != null) {
                                    mMenus.add(menu);
                                }
                            }
                        }
                    } else if (name.trim().toLowerCase().equals("sub")) {
                        if (xml.getAttributeCount() > 0) {
                            if (xml.getAttributeName(0).trim().toLowerCase().equals("name")) {

                                cname = xml.getAttributeValue(0);
                                cname = getTranslation(cname);
                                sub = new Sub(cname);
                                if (menu != null) {
                                    menu.add(sub);
                                }
                            }
                        }
                    } else if (name.trim().toLowerCase().equals("mods")) {
                        cname = xml.nextText();

                        if (sub != null) {
                            if (sub.getCount() == 0) {
                                String[] ss = cname.split(",");
                                for (int i = 0; i < ss.length; ++i) {
                                    cname = ss[i];
                                    cname = getTranslation(cname);
                                    sub.add(cname);
                                }
                            } else {
                                cname = getTranslation(cname);
                                sub.add(cname);
                            }

                        }
                    }
                } else if (xml.getEventType() == XmlResourceParser.END_TAG) {
                } else if (xml.getEventType() == XmlResourceParser.TEXT) {
                }

                xml.next();// 读取下一个标签
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parserCarXml(XmlResourceParser xml) {

        try {
            Category categorys = null;
            Node node;
            String cname;
            int cvalue;

            while (xml.getEventType() != XmlResourceParser.END_DOCUMENT) {
                String name = null;
                if (xml.getEventType() == XmlResourceParser.START_TAG) {

                    name = xml.getName();

                    if (name.trim().toLowerCase().equals("category")) {
                        if (xml.getAttributeCount() > 1) {
                            if (xml.getAttributeName(0).trim().toLowerCase().equals("name") && xml.getAttributeName(1).trim().toLowerCase().equals("value")) {
                                cname = xml.getAttributeValue(0);
                                cname = getTranslation(cname);
                                cvalue = xml.getAttributeIntValue(1, 0);
                                categorys = new Category(cname, cvalue);
                                mCategorys.add(categorys);
                            }
                        }
                    } else if (name.trim().toLowerCase().equals("model")) {
                        if (xml.getAttributeCount() > 1) {
                            if (xml.getAttributeName(0).trim().toLowerCase().equals("name") && xml.getAttributeName(1).trim().toLowerCase().equals("value")) {
                                cname = xml.getAttributeValue(0);
                                cname = getTranslation(cname);
                                cvalue = xml.getAttributeIntValue(1, 0);
                                node = new Node(cname, cvalue);
                                if (categorys != null) {
                                    categorys.add(node);
                                }
                            }
                        }
                    } else if (name.trim().toLowerCase().equals("config")) {
                        if (xml.getAttributeCount() > 1) {
                            if (xml.getAttributeName(0).trim().toLowerCase().equals("name") && xml.getAttributeName(1).trim().toLowerCase().equals("value")) {
                                cname = xml.getAttributeValue(0);
                                cname = getTranslation(cname);
                                cvalue = xml.getAttributeIntValue(1, 0);
                                node = new Node(cname, cvalue);
                                if (mCarConfig != null) {
                                    mCarConfig.add(node);
                                }
                            }
                        }
                    }
                }

                xml.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parserTranslationXml(XmlResourceParser xml) {

        try {
            Translation t;

            while (xml.getEventType() != XmlResourceParser.END_DOCUMENT) { // XML开始解析
                String name = null;
                if (xml.getEventType() == XmlResourceParser.START_TAG) { // 标签开头

                    name = xml.getName();// 标签的名字
                    if (name.trim().toLowerCase().equals("string")) { // 省份查询
                        if (xml.getAttributeCount() > 1) {
                            t = new Translation(xml.getAttributeValue(0), xml.getAttributeValue(1));
                            mTranslations.add(t);
                        }

                    }
                }

                xml.next();// 读取下一个标签
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parser(Context c) {
        init();

        XmlResourceParser x = null;
        Locale lc = Locale.getDefault();
        if (lc.getLanguage().equals("zh")) {
            if (lc.getCountry().equals("TW") || lc.getCountry().equals("HK")) {
                x = c.getResources().getXml(R.xml.car_translation_hk);
            }
        } else {

        }
        if (x == null) {
            x = c.getResources().getXml(R.xml.car_translation);
        }
        Log.d("ddcd", "mTranslations:" + mTranslations.size());
        parserTranslationXml(x);
        x = c.getResources().getXml(R.xml.car);
        parserCarXml(x);
        x = c.getResources().getXml(R.xml.car_menu);
        parserCarMenuXml(x);

    }

    private void init() {
        mTranslations = new ArrayList<Translation>();
        mCategorys = new ArrayList<Category>();
        mMenus = new ArrayList<Menu>();
        mCarConfig = new ArrayList<Node>();
    }

    public String getTranslation(String key) {

        Locale lc = Locale.getDefault();
        if (lc.getLanguage().equals("zh")) {
            if (lc.getCountry().equals("TW") || lc.getCountry().equals("HK")) {
                for (int i = 0; i < mTranslations.size(); ++i) {
                    Translation cs = mTranslations.get(i);
                    if (key.equals(cs.mZH)) {
                        return cs.mEN;
                    }
                }
            }
        } else {
            for (int i = 0; i < mTranslations.size(); ++i) {
                Translation cs = mTranslations.get(i);
                if (key.equals(cs.mZH)) {
                    return cs.mEN;
                }
            }
        }
        return key;
    }

    public int getAllCategoryNum() {
        return mCategorys.size();
    }

    public void getAllCategory(String[] k, String[] v) {
        for (int i = 0; i < mCategorys.size(); ++i) {
            Category cs = mCategorys.get(i);
            k[i] = cs.mName;
            v[i] = cs.mValue + "";
        }
    }

    public int getAllCategoryModelNum(String categoryName) {
        for (int i = 0; i < mCategorys.size(); ++i) {
            Category cs = mCategorys.get(i);
            if (categoryName.equals(cs.mName)) {
                return cs.mNode.size();
            }
        }
        return 0;
    }

    public void getAllCategoryModel(String categoryName, String[] k) {
        for (int i = 0; i < mCategorys.size(); ++i) {
            Category cs = mCategorys.get(i);
            if (categoryName.equals(cs.mName)) {
                for (int j = 0; j < cs.mNode.size(); ++j) {
                    k[j] = cs.mNode.get(j).mName;
                }
            }
        }
    }

    public int getCarConfigNum() {
        return mCarConfig.size();
    }

    public void getCarConfig(String[] k, String[] v) {
        for (int i = 0; i < mCarConfig.size(); ++i) {
            Node cs = mCarConfig.get(i);
            k[i] = getTranslation(cs.mName);
            v[i] = cs.mValue + "";
        }
    }

    public int getManufacturerNum() {
        return mMenus.size();
    }

    public void getManufacturer(String[] k, String[] v) {
        for (int i = 0; i < mMenus.size(); ++i) {
            Menu cs = mMenus.get(i);
            k[i] = getTranslation(cs.mName);
            v[i] = cs.mValue + "";
        }
    }

    public String getManufacturerByValue(int v) {
        for (int i = 0; i < mMenus.size(); ++i) {
            Menu cs = mMenus.get(i);
            if (v == cs.mValue) {
                return cs.mName;
            }
        }
        return null;
    }

    public int getCategoryNum(int value) {
        for (int i = 0; i < mMenus.size(); ++i) {
            Menu cs = mMenus.get(i);
            if (value == cs.mValue) {
                return cs.mNode.size();
            }
        }
        return 0;
    }

    public int getCategoryNum(String name) {
        for (int i = 0; i < mMenus.size(); ++i) {
            Menu cs = mMenus.get(i);
            if (name.equals(cs.mName)) {
                return cs.mNode.size();
            }
        }
        return 0;
    }

    public void getCategory(String name, String[] k) {
        for (int i = 0; i < mMenus.size(); ++i) {
            Menu cs = mMenus.get(i);
            if (name.equals(cs.mName)) {
                for (int j = 0; j < cs.mNode.size(); ++j) {
                    k[j] = cs.mNode.get(j).mName;
                }
                return;
            }
        }
    }

    public String getMenaByValue(String v) {
        for (int i = 0; i < mMenus.size(); ++i) {
            Menu cs = mMenus.get(i);
            if (v.equals(cs.mValue + "")) {
                return cs.mName;
            }
        }
        return null;
    }

    public int getModelNum(String manuName, String categoryName) {
        for (int i = 0; i < mMenus.size(); ++i) {
            Menu cs = mMenus.get(i);
            if (manuName.equals(cs.mName)) {
                for (int j = 0; j < cs.mNode.size(); ++j) {
                    if (categoryName.equals(cs.mNode.get(j).mName)) {
                        return cs.mNode.get(j).mNode.size();
                    }
                }
            }
        }
        return 0;
    }

    public void getModel(String manuName, String categoryName, String[] key) {
        for (int i = 0; i < mMenus.size(); ++i) {
            Menu cs = mMenus.get(i);
            if (manuName.equals(cs.mName)) {
                for (int j = 0; j < cs.mNode.size(); ++j) {
                    if (categoryName.equals(cs.mNode.get(j).mName)) {
                        for (int k = 0; k < cs.mNode.get(j).mNode.size(); ++k) {
                            key[k] = cs.mNode.get(j).mNode.get(k);
                        }
                        return;
                    }
                }
            }
        }
    }

    public String getCategorysValue(String name) {
        for (int i = 0; i < mCategorys.size(); ++i) {
            Category cs = mCategorys.get(i);
            if (name.equals(cs.mName)) {
                return cs.mValue + "";
            }
        }
        return "";
    }

    public String getCategorysByValue(String v) {
        for (int i = 0; i < mCategorys.size(); ++i) {
            Category cs = mCategorys.get(i);
            if (v.equals(cs.mValue + "")) {
                return cs.mName;
            }
        }
        return null;
    }

    public String getModelValue(String categoryName, String modelName) {
        for (int i = 0; i < mCategorys.size(); ++i) {
            Category cs = mCategorys.get(i);
            if (categoryName.equals(cs.mName)) {
                for (int j = 0; j < cs.mNode.size(); ++j) {
                    Node node = cs.mNode.get(j);
                    if (modelName.equals(node.mName)) {
                        return node.mValue + "";
                    }
                }
            }
        }
        return null;
    }

    public String getModelByValue(String categoryName, String v) {
        for (int i = 0; i < mCategorys.size(); ++i) {
            Category cs = mCategorys.get(i);
            if (categoryName.equals(cs.mName)) {
                for (int j = 0; j < cs.mNode.size(); ++j) {
                    Node node = cs.mNode.get(j);
                    if (v.equals(node.mValue + "")) {
                        return node.mName;
                    }
                }
            }
        }
        return null;
    }

    class Translation {
        public String mZH;
        public String mEN;

        public Translation(String z, String e) {
            mZH = z;
            mEN = e;
        }
    }

    class Node {
        int mValue;
        String mName;

        public Node(String n, int v) {
            mValue = v;
            mName = n;
        }

    }

    class Category {
        ArrayList<Node> mNode;
        String mName;
        int mValue;

        public Category(String n, int v) {
            mNode = new ArrayList<Node>();
            mName = n;
            mValue = v;
        }

        public void add(Node n) {
            mNode.add(n);
        }

        public int getCount() {
            return mNode.size();
        }

        public Node get(int index) {
            return mNode.get(index);
        }
    }

    class Menu {
        ArrayList<Sub> mNode;
        String mName;
        int mValue;

        public Menu(String n, int v) {
            mNode = new ArrayList<Sub>();
            mName = n;
            mValue = v;
        }

        public void add(Sub n) {
            mNode.add(n);
        }

        public int getCount() {
            return mNode.size();
        }

        public Sub get(int index) {
            return mNode.get(index);
        }
    }

    class Sub {
        ArrayList<String> mNode;
        String mName;

        public Sub(String n) {
            mNode = new ArrayList<String>();
            mName = n;
        }

        public void add(String n) {
            mNode.add(n);
        }

        public int getCount() {
            return mNode.size();
        }

        public String get(int index) {
            return mNode.get(index);
        }
    }

}
