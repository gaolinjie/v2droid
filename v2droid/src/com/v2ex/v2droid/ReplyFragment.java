
package com.v2ex.v2droid;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class ReplyFragment extends Fragment {

    private static ReplyFragment instance;

    public static ReplyFragment getInstance() {
        if (ReplyFragment.instance == null) {
            return new ReplyFragment();
        }
        return ReplyFragment.instance;
    }


    public ReplyFragment() {
    	ReplyFragment.instance = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        ((MainActivity) getSupportActivity()).getSupportActionBar().setTitle(R.string.reply);
		setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reply);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        //view.findViewById(R.id.github).setOnClickListener(githubListener);
        //view.findViewById(R.id.developers).setOnClickListener(developersListener);
    }
}
