
package org.holoeverywhere;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.preference.SharedPreferences;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Watson.OnCreateOptionsMenuListener;
import android.support.v4.app.Watson.OnOptionsItemSelectedListener;
import android.support.v4.app.Watson.OnPrepareOptionsMenuListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.internal.view.menu.ContextMenuListener;

public interface IHoloFragment extends IHolo, OnPrepareOptionsMenuListener,
        OnCreateOptionsMenuListener, OnOptionsItemSelectedListener, ContextMenuListener {
    @Override
    public SharedPreferences getDefaultSharedPreferences();

    public LayoutInflater getLayoutInflater(Bundle savedInstanceState);

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode);

    public Activity getSupportActivity();

    public FragmentManager getSupportFragmentManager();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState);

    public void onInflate(Activity activity, AttributeSet attrs,
            Bundle savedInstanceState);

    public View prepareDecorView(View view);
}
