package com.my.factory;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * This activity plays a video from a specified URI.
 */
public class JsonParser {

    private ArrayList<CanSetting> mCanSettings = new ArrayList<CanSetting>();

    ;
    private ArrayList<CanAppShow> mCanAppShows = new ArrayList<CanAppShow>();
    private ArrayList<CanBaud> mCanBaudConfig = new ArrayList<CanBaud>();

    ;

    public CanBaud getCanBaudConfig(String id) {
        for (int i = 0; i < mCanBaudConfig.size(); ++i) {
            CanBaud cs = mCanBaudConfig.get(i);
            for (String s : cs.mPros) {
                if (s.equals(id)) {
                    return cs;
                }
            }
        }
        return mCanBaudConfig.get(0);
    }

    ;

    public CanSetting getCanSetting(String id) {
        for (int i = 0; i < mCanSettings.size(); ++i) {
            CanSetting cs = mCanSettings.get(i);
            if (id.equals(cs.mId)) {
                return cs;
            }
        }
        return null;
    }

    public boolean isAppShow(String app, String id, String pro) {
        for (int i = 0; i < mCanAppShows.size(); ++i) {
            CanAppShow cs = mCanAppShows.get(i);
            if (cs.mApp.equals(app)) {
                if (cs.mPros != null && pro != null) {
                    for (String s : cs.mPros) {
                        if (s.equals(pro)) {
                            return true;
                        }
                    }
                } else if (cs.mIds != null && id != null) {
                    // for (String s : cs.mIds) {
                    // if (s.equals(id)) {
                    // return true;
                    // }
                    // }
                }
            }
        }

        return false;
    }

    public String buildAppShow(String manaId, String cateId, String modelId, String pro, String config) {
        String show = "";
        for (int i = 0; i < mCanAppShows.size(); ++i) {
            CanAppShow cs = mCanAppShows.get(i);
            boolean set = false;
            if (cs.mPros != null && pro != null) {
                for (String s : cs.mPros) {
                    String[] ss = s.split(":");
                    if (ss[0].equals(pro)) {
                        if (ss.length == 1 || (ss.length > 1 && ss[1].equals(config))) {
                            show += cs.mApp + ",";
                            set = true;
                            break;
                        }
                    }

                }
            }

            if (!set) {
                if (cs.mIds != null) {
                    for (CanAppShowIDS ids : cs.mIds) {
                        if (ids.mMenaID.equals(manaId) && ids.mCategoryID.equals(cateId)) {
                            for (String s : ids.mModelID) {
                                String[] ss = s.split(":");
                                if (ss[0].equals(modelId)) {
                                    if (ss.length == 1 || (ss.length > 1 && ss[1].equals(config))) {
                                        show += cs.mApp + ",";
                                        set = true;
                                        break;
                                    }
                                }

                            }
                        }
                        if (set) {
                            break;
                        }
                    }
                }
            }

        }

        return show;
    }

    public void parser(Context c) {
        parserCanSetting(c);
        parserAppShow(c);
        parserCanBaud(c);
    }

    private void parserAppShow(Context c) {

        try {
            AssetManager assetManager = c.getAssets();
            InputStreamReader inputStreamReader = new InputStreamReader(assetManager.open("can_apps_show.json"), "UTF-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            inputStreamReader.close();

            JSONArray array = new JSONArray(builder.toString());
            mCanAppShows.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);

                String app = jsonObject.getString("app");
                String pro = null;
                try {
                    pro = jsonObject.getString("pro");
                } catch (Exception e) {

                }

                JSONArray ids = jsonObject.getJSONArray("ids");
                CanAppShowIDS[] appShows = new CanAppShowIDS[ids.length()];
                for (int j = 0; j < ids.length(); j++) {
                    jsonObject = ids.getJSONObject(j);
                    String id = jsonObject.getString("id");
                    String mid = jsonObject.getString("mid");
                    appShows[j] = new CanAppShowIDS(id, mid);
                }

                CanAppShow cs = new CanAppShow(appShows, app, pro);
                mCanAppShows.add(cs);
            }

        } catch (Exception e) {

        }
    }

    private void parserCanSetting(Context c) {
        try {
            AssetManager assetManager = c.getAssets();
            InputStreamReader inputStreamReader = new InputStreamReader(assetManager.open("can_settings.json"), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }

            br.close();
            inputStreamReader.close();

            JSONArray array = new JSONArray(builder.toString());
            mCanSettings.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);

                String text = jsonObject.getString("id");
                String value = jsonObject.getString("pro");
                String ext = null;
                try {
                    ext = jsonObject.getString("ext");
                } catch (Exception ignored) {
                }
                String app = null;
                try {
                    app = jsonObject.getString("app");
                } catch (Exception ignored) {
                }

                CanSetting cs = new CanSetting(text, value, ext, app);
                mCanSettings.add(cs);
            }

        } catch (Exception e) {
            Log.d("json", "" + e);
        }
    }

    private void parserCanBaud(Context c) {

        try {
            AssetManager assetManager = c.getAssets();
            InputStreamReader inputStreamReader = new InputStreamReader(assetManager.open("can_baud_config.json"), "UTF-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            inputStreamReader.close();

            JSONArray array = new JSONArray(builder.toString());
            mCanBaudConfig.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);

                String baud = jsonObject.getString("baud");
                String config = jsonObject.getString("config");
                String pro = jsonObject.getString("pro");

                CanBaud cb = new CanBaud(baud, config, pro);

                mCanBaudConfig.add(cb);
            }

        } catch (Exception e) {

        }
    }

    class CanSetting {
        public String mId;
        public String mPro;
        public String mExt;
        public String mAppShow;

        public CanSetting(String id, String pro, String ext, String appShow) {
            mId = id;
            mPro = pro;
            mExt = ext;
            mAppShow = appShow;
        }
    }

    class CanAppShowIDS {
        public String mMenaID;
        public String mCategoryID;
        public String[] mModelID;

        public CanAppShowIDS(String id, String modeId) {
            String[] ss = id.split(",");
            mMenaID = ss[0];
            if (ss.length > 1) {
                mCategoryID = ss[1];
            }
            mModelID = modeId.split(",");
        }
    }

    class CanAppShow {
        public CanAppShowIDS[] mIds;
        public String[] mPros;
        public String mApp;

        public CanAppShow(CanAppShowIDS[] ids, String app, String pro) {
            mIds = ids;
            if (pro != null) {
                mPros = pro.split(",");
            }
            mApp = app;
        }
    }

    class CanBaud {
        public String[] mPros;
        public String mBaud;
        public String mConfig;

        public CanBaud(String baud, String config, String pro) {
            mBaud = baud;
            if (pro != null) {
                mPros = pro.split(",");
            }
            mConfig = config;
        }
    }

}
