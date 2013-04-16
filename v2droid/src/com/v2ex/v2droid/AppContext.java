
package com.v2ex.v2droid;

import java.io.InputStream;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.ThemeManager;
import org.holoeverywhere.app.Application;
import org.holoeverywhere.app.Application.Config.PreferenceImpl;

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
	public InputStream loginVerify(String account, String pwd) throws AppException {
		return ApiClient.login(this, account, pwd);
	}
	
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}

}
