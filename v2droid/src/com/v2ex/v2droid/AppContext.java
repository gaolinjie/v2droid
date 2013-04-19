
package com.v2ex.v2droid;

import java.util.UUID;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.app.Application.Config.PreferenceImpl;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppContext extends Application {
    static {
        config().setDebugMode(true).setPreferenceImpl(PreferenceImpl.JSON);
        LayoutInflater.remap(WidgetContainer.class);
        LayoutInflater.remap(NavigationItem.class);
        ThemeManager.setDefaultTheme(ThemeManager.DARK);
        // Android 2.1 incorrect process FULLSCREEN flag
        // ThemeManager.modify(ThemeManager.FULLSCREEN);
        ThemeManager.map(ThemeManager.LIGHT,
                R.style.Holo_v2droid_Theme_Light);
        ThemeManager.map(ThemeManager.MIXED,
                R.style.Holo_v2droid_Theme_Light_DarkActionBar);
        ThemeManager.map(ThemeManager.DARK,
                R.style.Holo_v2droid_Theme);
    }
    
    private boolean login = false;	//登录状态
    
    /**
	 * 用户是否登录
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}
    
    /**
	 * 用户登录验证
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public String loginVerify(String account, String pwd) throws AppException {
		return ApiClient.login(this, account, pwd);
	}
	
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	
	/**
	 * 获取App安装包信息
	 * @return
	 */
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
	
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

}
