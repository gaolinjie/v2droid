
package com.v2ex.v2droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ProgressBar;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class UserFragment extends Fragment {

    private static UserFragment instance;
    private String mContent = "???";
    
    LazyAdapter mAdapter = null;
	ArrayList<HashMap<String, String>> topicList = null;
	private ListView listView;
	private ProgressBar progressBar;
	private ProgressBar progressBar2;
	Document doc;
	int recentPageNum = 1;
	String userID;
    
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
        
        Intent intent = getActivity().getIntent();
        userID = intent.getStringExtra("EXTRA_USER_ID");
        getSupportActivity().getSupportActionBar().setTitle("@"+userID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view = null;
    	
    	
    	if (mContent != "类别") {
    		view = inflater.inflate(R.layout.fragment_user, null);

			
		} else {
		}
    	
    	return view;
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        
        if (mContent != "类别") {
        	progressBar = (ProgressBar) view
    				.findViewById(R.id.progress_bar);
            progressBar2 = (ProgressBar) view
    				.findViewById(R.id.progress_bar2);
            
            if (topicList==null) {
            	topicList = new ArrayList<HashMap<String, String>>();
        		new GetDataTask().execute();
        		mAdapter = new LazyAdapter((Activity)getActivity(), topicList);
        		progressBar.setVisibility(View.VISIBLE);
        		progressBar2.setVisibility(View.GONE);
            } else {
            	progressBar.setVisibility(View.GONE);
            	progressBar2.setVisibility(View.GONE);
            }
            
            listView = (ListView) view
    				.findViewById(R.id.pull_refresh_list);
    		listView.setAdapter(mAdapter);
    		
    		listView.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> parent,
    					View view, int position, long id) {

    				if (topicList.get(position).get(
    						TopicFragment.KEY_ID) == MainActivity.MORE_TAG) {
    					
    					progressBar2.setVisibility(View.VISIBLE);
    					new GetDataTask().execute();
    				} else {
    					String tid = topicList.get(position).get(
    							TopicFragment.KEY_ID);
    					Intent contentIntent = new Intent(Intents.SHOW_CONTENT);
    					contentIntent.putExtra("EXTRA_TOPIC_ID", tid);
    					getActivity().startActivity(contentIntent);
    				}
    			}
    		});

			
		} else {

		}
    }
    
    public static UserFragment newInstance(String text) {
    	UserFragment fragment = new UserFragment();
    	
    	fragment.mContent = text;

		return fragment;
	}
    
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			String url = "http://v2ex.com/member/" + userID + "/topics?p=" + recentPageNum;

			AppContext ac = (AppContext) getActivity().getApplication();
			
			//Document doc;
			try {
				doc = ApiClient.get(ac, url, URLs.HOST);
				ApiClient.getUserTopics(ac, doc, topicList);
				
			} catch (IOException e) {				
			}
	
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (!topicList.isEmpty()) {
				mAdapter.notifyDataSetChanged();
				recentPageNum++;
			}

			progressBar.setVisibility(View.GONE);
			progressBar2.setVisibility(View.GONE);

			super.onPostExecute(result);
		}
	}
}
