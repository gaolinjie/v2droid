
package com.v2ex.v2droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ProgressBar;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class FavoriteFragment extends Fragment {
	
	private final static String TAG = "FavoriteFragment";
	
    static final String KEY_ID = "id";
	static final String KEY_TITLE = "title";
	static final String KEY_REPLIES = "replies";
	static final String KEY_USERNAME = "username";
	static final String KEY_AVATAR = "avatar";
	static final String KEY_NODE = "node";
	static final String KEY_TIME = "time";
	LazyAdapter mAdapter = null;
	ArrayList<HashMap<String, String>> topicList = null;
	private ListView listView;
	private ProgressBar progressBar;
	private ProgressBar progressBar2;
	
	int recentPageNum = 1;

	private static FavoriteFragment instance;
	Document doc;

    public static FavoriteFragment getInstance() {
        if (FavoriteFragment.instance == null) {
            return new FavoriteFragment();
        }
        return FavoriteFragment.instance;
    }

    public FavoriteFragment() {
    	FavoriteFragment.instance = this;
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

    	View view = null;
    	view = inflater.inflate(R.layout.fragment_favorite, null);

		return view;
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        
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
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	((MainActivity) getSupportActivity()).getSupportActionBar().setTitle(R.string.favorite);	
    }
    
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			String url = "http://v2ex.com/my/topics?p="+recentPageNum;	

			AppContext ac = (AppContext) getActivity().getApplication();
			
			//Document doc;
			try {
				doc = ApiClient.get(ac, url, URLs.HOST);
				ApiClient.getFavorites(ac, doc, topicList);
				
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
			
			AppContext ac = (AppContext) getActivity().getApplication();
			String num = ApiClient.getMessageNum(ac, doc);
			System.out.println("num=====>" + num);
			((MainActivity)getActivity()).setMessageNum(num);

			super.onPostExecute(result);
		}
	}
    
    public void onRefresh() {
    	topicList.clear();
    	mAdapter.notifyDataSetChanged();
    	progressBar.setVisibility(View.VISIBLE);
    	recentPageNum = 0;
    	new GetDataTask().execute();
    }
    
    @Override
    public void onCreateOptionsMenu(
          Menu menu, MenuInflater inflater) {
       inflater.inflate(R.menu.fragment_favorite, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		((MainActivity)getActivity()).toggle();
        		break;
                
            case R.id.refresh:
            	onRefresh();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
