
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
    
    private final class EmailListener implements OnClickListener {
        private final String subject, to;

        public EmailListener(String to, String subject) {
            this.to = to;
            this.subject = subject;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                    to
            });
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent = Intent.createChooser(intent, getText(R.string.select_email_programm));
            if (intent != null) {
                getActivity().startActivity(intent);
            }
        }
    }

    private final OnClickListener v2exListener = new UrlListener(
            "https://v2ex.com/about");

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
        view.findViewById(R.id.v2ex).setOnClickListener(v2exListener);
        view.findViewById(R.id.developers).setOnClickListener(new EmailListener(
                "gaolinjie@gmail.com", "v2droid"));
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
