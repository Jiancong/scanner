package com.fzcyjh.invoicescanner.manager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by zhangyb on 2018/6/5.
 */

public class ConfigManager {
    private static final String TAG = "ConfigManager";
    private static ConfigManager sThis;
    private  Properties mProperties = new Properties();
    private String  mPathConfig;
    public static String CONF_URL_UPLOAD = "url.upload";
    public static String CONF_GUIDE_NEVER_TIP = "guide.nevertip";
    public static String CONF_SETTING_QUICKUPLOAD = "setting.quickupload";

    public    static  ConfigManager getInstance(){
        if(sThis == null){
            sThis = new ConfigManager();
        }
        return  sThis;
    }

    public void init(Context context){
        mPathConfig = context.getApplicationContext().getFilesDir().toString() + "/config.properties";
        loadConfig();
        initDefaultConfig();
    }

    private void initDefaultConfig(){
        if(!mProperties.containsKey(CONF_URL_UPLOAD)){
            String strUrl ="http://121.42.164.2/uploader"; //"http://10.26.7.144:5000/api/upload"; //
            setString(CONF_URL_UPLOAD, strUrl);
        }
    }

    private void loadConfig(){
        try {
            mProperties.load( new FileInputStream(mPathConfig) );
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "loadConfig");
        }
    }

    private void saveConfig(){
        OutputStream fos;
        try {
            fos = new FileOutputStream(mPathConfig);
            mProperties.store(fos, null);
        } catch ( IOException e ) {
            e.printStackTrace ( );
        }
    }

    static public String getString(String strKey, String strDefault){
        return  getInstance().mProperties.getProperty(strKey, strDefault);
    }

    static public String getString(String strKey){
        return  getInstance().mProperties.getProperty(strKey, "");
    }

    static public void setString(String strKey, String strValue){
        getInstance().mProperties.setProperty(strKey, strValue);
        getInstance().saveConfig();
    }

    static public boolean getBoolean(String strKey, boolean bDefault){
        String strDefault = Boolean.toString(bDefault);
        String strRet = getInstance().mProperties.getProperty(strKey, strDefault);
        return  Boolean.parseBoolean(strRet);
    }

    static public void  setBoolean(String strKey, boolean bValue){
        getInstance().setString(strKey, Boolean.toString(bValue));
    }
}
