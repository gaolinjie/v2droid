
package com.v2ex.v2droid;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {
    private final class UrlListener implements OnClickListener {
        private final Uri uri;

        public UrlListener(String url) {
            uri = Uri.parse(url);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent = Intent.createChooser(intent, getText(R.string.select_browser));
            if (intent != null) {
                getActivity().startActivity(intent);
            }
        }

    }

    private static AboutFragment instance;

    public static AboutFragment getInstance() {
        if (AboutFragment.instance == null) {
            return new AboutFragment();
        }
        return AboutFragment.instance;
    }

    private final OnClickListener developersListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            //((MainActivity) getSupportActivity()).replaceFragment(DevelopersFragment.getInstance(),
                    //"developers");
        }
    };

    private final OnClickListener githubListener = new UrlListener(
            "https://github.com/ChristopheVersieux/HoloEverywhere");

    public AboutFragment() {
        AboutFragment.instance = this;
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
