
package com.v2ex.v2droid;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SettingFragmen extends Fragment {
    private final class UrlListener implements OnClickListener {
        private final Uri uri;

        public UrlListener(String url) {
            uri = Uri.parse(url);
        }

        @Override
        public void onClick(View v) {
        	AlertDialog.Builder builder = new Builder(
					getActivity());
			builder.setMessage("确认登出吗？");
			builder.setTitle("登出");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					logout();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.create().show();
        }

    }

    private static SettingFragmen instance;

    public static SettingFragmen getInstance() {
        if (SettingFragmen.instance == null) {
            return new SettingFragmen();
        }
        return SettingFragmen.instance;
    }

    private final OnClickListener developersListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            //((MainActivity) getSupportActivity()).replaceFragment(DevelopersFragment.getInstance(),
                    //"developers");
        }
    };

    private final OnClickListener logoutListener = new UrlListener(
            "https://v2ex.com/logout");

    public SettingFragmen() {
    	SettingFragmen.instance = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        view.findViewById(R.id.logout).setOnClickListener(logoutListener);
        //view.findViewById(R.id.developers).setOnClickListener(developersListener);
    }
    
    @Override
    public void onCreateOptionsMenu(
          Menu menu, MenuInflater inflater) {
       //inflater.inflate(R.menu.fragment_content, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		((MainActivity)getActivity()).toggle();
        		break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    public void logout() {
    	AppConfig.setLogin(getActivity(), false);
    	AppConfig.setUsername(getActivity(), "");
    	AppConfig.setPassword(getActivity(), "");
    	AppConfig.setMessageNum(getActivity(), "");
    	
    	Intent intent = new Intent(getActivity(), LoginActivity.class);
    	getActivity().startActivity(intent);
    	getActivity().finish();
    }
}
