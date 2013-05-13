package com.v2ex.v2droid;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
	
	final public static String DB_NAME = "v2droid.db";
	
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
	
	public static boolean getLogin(Context context) {
		DatabaseHelper dbhelper = new DatabaseHelper(context, DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		int n = 0;
		if (db != null) {
			db.execSQL(
					  "CREATE TABLE IF NOT EXISTS login_table  ( login INTEGER );"
					  );
			Cursor result = db.rawQuery("SELECT * FROM login_table", null);
			if (result.getCount() > 0) {
				result.moveToFirst();
				if (!result.isAfterLast()) {
					n = result.getInt(0);
					System.out.println("s = result.getString(0);=====>" + n);
				}
			}
			
			result.close();
			db.close();
		}
		return (n==1) ? true : false;
	}
	
	public static String getUsername(Context context){
		DatabaseHelper dbhelper = new DatabaseHelper(context, DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		String s = "";
		if (db != null) {
			db.execSQL(
					  "CREATE TABLE IF NOT EXISTS username_table  ( username TEXT );"
					  );
			Cursor result = db.rawQuery("SELECT * FROM username_table", null);
			if (result.getCount() > 0) {
				result.moveToFirst();
				if (!result.isAfterLast()) {
					s = result.getString(0);
				}
			}
			
			result.close();
			db.close();
		}
		return s;
	}
	
	public static String getPassword(Context context){
		DatabaseHelper dbhelper = new DatabaseHelper(context, DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		String s = "";
		if (db != null) {
			db.execSQL(
					  "CREATE TABLE IF NOT EXISTS password_table  ( password TEXT );"
					  );
			Cursor result = db.rawQuery("SELECT * FROM password_table", null);
			if (result.getCount() > 0) {
				result.moveToFirst();
				if (!result.isAfterLast()) {
					s = result.getString(0);
				}
			}
			
			result.close();
			db.close();
		}
		return s;
	}
	
	public static String getMessageNum(Context context){
		DatabaseHelper dbhelper = new DatabaseHelper(context, DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		String s = "";
		if (db != null) {
			db.execSQL(
					  "CREATE TABLE IF NOT EXISTS num_table  ( num TEXT );"
					  ); 
			Cursor result = db.rawQuery("SELECT * FROM num_table", null);
			if (result.getCount() > 0) {
				result.moveToFirst();
				if (!result.isAfterLast()) {
					s = result.getString(0);
				}
			}
			
			result.close();
			db.close();
		}
		return s;
	}
	
	public static void setLogin(Context comtext, boolean l){
		int n = l ? 1 : 0;
		DatabaseHelper dbhelper = new DatabaseHelper(comtext, DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getWritableDatabase();

		if (db != null) {

			  db.execSQL("DROP TABLE IF EXISTS login_table"); 
			  db.execSQL(
			  "CREATE TABLE IF NOT EXISTS login_table  ( login INTEGER );"
			  ); 
			  
			  ContentValues values = new ContentValues();   
	            values.put("login", n);  
  
	            db.insert("login_table", null, values);
			 
			db.close();
		}
	}
	
	public static void setUsername(Context comtext, String u){
		DatabaseHelper dbhelper = new DatabaseHelper(comtext, DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getWritableDatabase();

		if (db != null) {

			  db.execSQL("DROP TABLE IF EXISTS username_table"); 
			  db.execSQL(
			  "CREATE TABLE IF NOT EXISTS username_table  ( username TEXT );"
			  ); 
			  
			  ContentValues values = new ContentValues();   
	            values.put("username", u);  
  
	            db.insert("username_table", null, values);
			 
			db.close();
		}
	}
	
	public static void setPassword(Context comtext, String p){
		DatabaseHelper dbhelper = new DatabaseHelper(comtext, DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getWritableDatabase();

		if (db != null) {

			  db.execSQL("DROP TABLE IF EXISTS password_table"); 
			  db.execSQL(
			  "CREATE TABLE IF NOT EXISTS password_table  ( password TEXT );"
			  ); 
			  
			  ContentValues values = new ContentValues();   
	            values.put("password", p);  
  
	            db.insert("password_table", null, values);
			 
			db.close();
		}
	}

	public static void setMessageNum(Context comtext, String n){
		DatabaseHelper dbhelper = new DatabaseHelper(comtext, DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getWritableDatabase();

		if (db != null) {

			  db.execSQL("DROP TABLE IF EXISTS num_table"); 
			  db.execSQL(
			  "CREATE TABLE IF NOT EXISTS num_table  ( num TEXT );"
			  ); 
			  
			  ContentValues values = new ContentValues();   
	            values.put("num", n);  
  
	            db.insert("num_table", null, values);
			 
			db.close();
		}
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
