package com.v2ex.v2droid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppConfig{
	
	private final static String APP_CONFIG = "config";
	
	public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";
	public final static String CONF_COOKIE = "cookie";
	public final static String CONF_LOGIN = "login";
	public final static String CONF_USERNAME = "username";
	public final static String CONF_PASSWORD = "password";
	public final static String CONF_MSGNUM = "msgnum";
	
	public final static String STRING_TRUE = "true";
	public final static String STRING_FALSE = "false";
	
	private Context mContext;
	private static AppConfig appConfig;
	
	public static AppConfig getAppConfig(Context context)
	{
		if(appConfig == null) {
			appConfig = new AppConfig();
			appConfig.mContext = context;
		}
		return appConfig;
	}
	
	/**
	 * 获取Preference设置
	 */
	public static SharedPreferences getSharedPreferences(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	
	public String getCookie(){
		return get(CONF_COOKIE);
	}
	
	public boolean getLogin(){
		return (get(CONF_LOGIN)==STRING_TRUE) ? true : false;
	}
	
	public String getUsername(){
		return get(CONF_USERNAME);
	}
	
	public String getPassword(){
		return get(CONF_PASSWORD);
	}
	
	public String getMessageNum(){
		return get(CONF_MSGNUM);
	}
	
	public void setLogin(boolean l){
		set(CONF_LOGIN, l ? STRING_TRUE : STRING_FALSE);
	}
	
	public void setUsername(String u){
		set(CONF_USERNAME, u);
	}
	
	public void setPassword(String p){
		set(CONF_PASSWORD, p);
	}

	public void setMessageNum(String n){
		set(CONF_MSGNUM, n);
	}
	
	public String get(String key)
	{
		SharedPreferences pref = getSharedPreferences(mContext); 	  
		return pref.getString(key, "");
	}
	
	
	public void set(String key,String value)
	{
		SharedPreferences pref = getSharedPreferences(mContext); 
		SharedPreferences.Editor editor = pref.edit();     
		editor.putString(key, value);   
		editor.commit();  
	}
	
	public void remove(String...key)
	{
		SharedPreferences pref = getSharedPreferences(mContext); 
		SharedPreferences.Editor editor = pref.edit();      
		  
		for(String k : key)
			editor.remove(k);
		editor.commit();
	}
}
