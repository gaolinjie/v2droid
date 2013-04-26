
package com.v2ex.v2droid;

import java.util.List;

import org.apache.http.cookie.Cookie;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.PersistentCookieStore;

public class MessageFragment extends Fragment {

    private static MessageFragment instance;

    public static MessageFragment getInstance() {
        if (MessageFragment.instance == null) {
            return new MessageFragment();
        }
        return MessageFragment.instance;
    }

    public MessageFragment() {
    	MessageFragment.instance = this;
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
        return inflater.inflate(R.layout.about);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        //view.findViewById(R.id.github).setOnClickListener(githubListener);
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
}
