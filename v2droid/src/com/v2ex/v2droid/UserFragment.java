
package com.v2ex.v2droid;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class UserFragment extends Fragment {

    private static UserFragment instance;
    
    
    
    private Intent intent; 

    public static UserFragment getInstance() {
        if (UserFragment.instance == null) {
            return new UserFragment();
        }
        return UserFragment.instance;
    }

    public UserFragment() {
    	UserFragment.instance = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view = null;
    	view = inflater.inflate(R.layout.fragment_user, null);
    	return view;
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
    }
}
