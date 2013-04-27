
package com.v2ex.v2droid;

import java.util.UUID;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.app.Application.Config.PreferenceImpl;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppContext extends Application {
    static {
        config().setDebugMode(true).setPreferenceImpl(PreferenceImpl.JSON);
        LayoutInflater.remap(WidgetContainer.class);
        LayoutInflater.remap(NavigationItem.class);
        ThemeManager.setDefaultTheme(ThemeManager.DARK);
        
        ThemeManager.map(ThemeManager.LIGHT,
                R.style.Holo_v2droid_Theme_Light);
        ThemeManager.map(ThemeManager.MIXED,
                R.style.Holo_v2droid_Theme_Light_DarkActionBar);
        ThemeManager.map(ThemeManager.DARK,
                R.style.Holo_v2droid_Theme);
    }
    private static final String APP_PREFS = "AppPrefsFile";
	private static final String LOGIN_STATE = "login_state";
	private static final String USER_NAME = "user_name";
	private static final String PASS_WORD = "pass_word";
    
    private boolean login = false;	//登录状态
    private SharedPreferences prefs= null;
    private String username;
    private String password;
    
	public boolean isLogin() {
		return login;
	}
	
	public String getUserName() {
		return username;
	}
	
	public String getPassWord() {
		return password;
	}
	
	public boolean getLogin() {
		prefs = getSharedPreferences(APP_PREFS, 0);
		String strLogin = prefs.getString(LOGIN_STATE, null);
		
		if (strLogin != null) {
			if (strLogin == "TRUE") {
				login = true;
				username = prefs.getString(USER_NAME, null);
				password = prefs.getString(PASS_WORD, null);
			} else {
				login = false;
			}
		} else {
			login = false;
		}
		return login;
	}
	
	public void setLogin(boolean b, String u, String p) {
		login = b;
		
		if (prefs == null) {
			prefs = getSharedPreferences(APP_PREFS, 0);
		}
		
		SharedPreferences.Editor prefEditor = prefs.edit();
		prefEditor.putString(LOGIN_STATE, b?"TRUE":"FALSE");
		prefEditor.putString(USER_NAME, u);
		prefEditor.putString(PASS_WORD, p);
		prefEditor.commit();
	}
    
	public String loginVerify(String account, String pwd) throws AppException, Exception{
		return ApiClient.login(this, account, pwd);
	}
	
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}
	
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

}
