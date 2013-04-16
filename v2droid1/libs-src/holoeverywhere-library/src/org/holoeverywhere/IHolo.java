
package org.holoeverywhere;

import org.holoeverywhere.app.Application;
import org.holoeverywhere.app.Application.Config;
import org.holoeverywhere.app.Application.Config.PreferenceImpl;
import org.holoeverywhere.preference.SharedPreferences;

public interface IHolo {
    public Config getConfig();

    public SharedPreferences getDefaultSharedPreferences();

    public SharedPreferences getDefaultSharedPreferences(PreferenceImpl impl);

    public LayoutInflater getLayoutInflater();

    public SharedPreferences getSharedPreferences(PreferenceImpl impl,
            String name, int mode);

    public SharedPreferences getSharedPreferences(String name, int mode);

    public Application getSupportApplication();

    public boolean isABSSupport();
}
