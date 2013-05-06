
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

public class UserFragment2 extends Fragment {

    private static UserFragment2 instance;
    private String mContent = "???";
    
    LazyAdapter mAdapter = null;
	ArrayList<HashMap<String, String>> topicList = null;
	private ListView listView;
	private ProgressBar progressBar;
	private ProgressBar progressBar2;
	Document doc;
	int recentPageNum = 1;
	String userID;
	String userAvatar = null;
    
    private Intent intent; 
    
    ArrayList<HashMap<String, String>> repliesList = null;
    private ListView repliesListView;
    UserRepliesAdapter mRepliesAdapter = null;
    int recentRepliesPageNum = 1;
    Document docReplies;
    private ProgressBar progressBarReplies;

    public static UserFragment2 getInstance() {
        if (UserFragment2.instance == null) {
            return new UserFragment2();
        }
        return UserFragment2.instance;
    }

    public UserFragment2() {
    	UserFragment2.instance = this;
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
    	
    	
    	if (mContent != "最近的回复") {
    		view = inflater.inflate(R.layout.fragment_user, null);
		} else {
			view = inflater.inflate(R.layout.fragment_user_replies2, null);
		}
    	
    	return view;
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        
        if (mContent != "最近的回复") {
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
			progressBarReplies = (ProgressBar) view
    				.findViewById(R.id.progress_bar2);
			progressBarReplies.setVisibility(View.GONE);
            if (repliesList==null) {
            	repliesList = new ArrayList<HashMap<String, String>>();
        		new GetDataTask2().execute();
        		mRepliesAdapter = new UserRepliesAdapter((Activity)getActivity(), repliesList);
            } else {
            }
            
            repliesListView = (ListView) view
    				.findViewById(R.id.replies_list);
            repliesListView.setAdapter(mRepliesAdapter);
            
            repliesListView.setOnItemClickListener(new OnItemClickListener() {

    			@Override
    			public void onItemClick(AdapterView<?> parent,
    					View view, int position, long id) {

    				if (repliesList.get(position).get(
    						ApiClient.KEY_GRAY) == MainActivity.MORE_TAG) {
    					progressBarReplies.setVisibility(View.VISIBLE);
    					new GetDataTask2().execute();
    				}
    			}
    		});	
		}
    }
    
    public static UserFragment2 newInstance(String text) {
    	UserFragment2 fragment = new UserFragment2();
    	
    	fragment.mContent = text;

		return fragment;
	}
    
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			String url = "http://v2ex.com/member/" + userID + "/topics?p=" + recentPageNum;

			AppContext ac = (AppContext) getActivity().getApplication();

			try {
				if (userAvatar==null) {
					String urlAvatar = "http://v2ex.com/member/" + userID;
					Document docAvatar = ApiClient.get(ac, urlAvatar, URLs.HOST);
					userAvatar = ApiClient.getUserAvatar(ac, docAvatar);
					if (userAvatar != null) {
						userAvatar = userAvatar.replace("large", "normal");
					} else {
						userAvatar = "";
					}					
				}
				doc = ApiClient.get(ac, url, URLs.HOST);
				ApiClient.getUserTopics(ac, doc, topicList, userAvatar);
				
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
    
    private class GetDataTask2 extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			String url = "http://v2ex.com/member/" + userID + "/replies?p=" + recentRepliesPageNum;

			AppContext ac = (AppContext) getActivity().getApplication();

			try {
				docReplies = ApiClient.get(ac, url, URLs.HOST);
				System.out.println("getUserReplies doInBackground======>");
				System.out.println("docReplies======>"+docReplies.toString());
				ApiClient.getUserReplies(ac, docReplies, repliesList);
				
			} catch (IOException e) {				
			}
	
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (!repliesList.isEmpty()) {
				mRepliesAdapter.notifyDataSetChanged();
				recentRepliesPageNum++;
			}
			
			progressBarReplies.setVisibility(View.GONE);

			super.onPostExecute(result);
		}
	}
}
