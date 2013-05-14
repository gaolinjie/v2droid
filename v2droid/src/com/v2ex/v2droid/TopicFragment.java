package com.v2ex.v2droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Toast;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TopicFragment extends Fragment {

	private final static String TAG = "TopicFragment";

	static final String KEY_ID = "id";
	static final String KEY_TITLE = "title";
	static final String KEY_REPLIES = "replies";
	static final String KEY_USERNAME = "username";
	static final String KEY_AVATAR = "avatar";
	static final String KEY_NODE = "node";
	static final String KEY_TIME = "time";
	LazyAdapter mAdapter = null;
	ArrayList<HashMap<String, String>> topicList = null;
	ArrayList<HashMap<String, String>> tempList = null;
	private ListView listView;
	private ProgressBar progressBar;
	private MenuItem refresh;

	int recentPageNum = 0;
	boolean bRefresh = false;

	private static TopicFragment instance;
	Document doc;

	public static TopicFragment getInstance() {
		if (TopicFragment.instance == null) {
			return new TopicFragment();
		}
		return TopicFragment.instance;
	}

	public TopicFragment() {
		TopicFragment.instance = this;
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
		view = inflater.inflate(R.layout.fragment_topic, null);

		return view;
	}

	@Override
	public void onViewCreated(View view) {
		super.onViewCreated(view);

		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		progressBar.setVisibility(View.GONE);

		if (topicList == null) {
			topicList = new ArrayList<HashMap<String, String>>();
			tempList = new ArrayList<HashMap<String, String>>();
			new GetDataTask().execute();
			mAdapter = new LazyAdapter((Activity) getActivity(), topicList);
		}

		listView = (ListView) view.findViewById(R.id.pull_refresh_list);
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (topicList.get(position).get(TopicFragment.KEY_ID) == MainActivity.MORE_TAG) {
					progressBar.setVisibility(View.VISIBLE);
					new GetDataTask().execute();
				} else {
					String tid = topicList.get(position).get(
							TopicFragment.KEY_ID);
					String node = topicList.get(position).get(
							TopicFragment.KEY_NODE);
					Intent contentIntent = new Intent(Intents.SHOW_CONTENT);
					contentIntent.putExtra("EXTRA_TOPIC_ID", tid);
					contentIntent.putExtra("EXTRA_NODE_NAME", node);
					getActivity().startActivity(contentIntent);
				}
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity) getSupportActivity()).getSupportActionBar().setTitle(
				R.string.topic);
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			String url;
			if (recentPageNum == 0) {
				url = "http://www.v2ex.com/?tab=all";
			} else {
				url = "http://v2ex.com/recent?p=" + recentPageNum;
			}
			AppContext ac = (AppContext) getActivity().getApplication();
			tempList.clear();
			try {
				doc = ApiClient.get(ac, url, URLs.HOST);
				ApiClient.getTopics(ac, doc, tempList);
			} catch (IOException e) {
			}

			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (tempList.size() > 1) {

				if (bRefresh) {
					topicList.clear();
					bRefresh = false;
					if (!listView.isStackFromBottom()) {
						listView.setStackFromBottom(true);
					}
					listView.setStackFromBottom(false);
				}

				if (!topicList.isEmpty()) {
					topicList.remove(topicList.size() - 1);
				}

				for (int i = 0; i < tempList.size(); i++) {
					topicList.add(tempList.get(i));
				}
				recentPageNum++;
				mAdapter.notifyDataSetChanged();
			} else {
				if (getActivity()!=null) {
					Toast.makeText(getActivity().getApplicationContext(),
							"貌似网络不给力啊...", Toast.LENGTH_SHORT).show();
				}
			}

			progressBar.setVisibility(View.GONE);

			AppContext ac = (AppContext) getActivity().getApplication();
			String num = ApiClient.getMessageNum(ac, doc);
			((MainActivity) getActivity()).setMessageNum(num);

			refresh.setActionView(null);

			super.onPostExecute(result);
		}
	}

	public void onRefresh() {
		// topicList.clear();
		refresh.setActionView(R.layout.refresh);
		bRefresh = true;
		recentPageNum = 0;
		new GetDataTask().execute();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_topic, menu);
		refresh = menu.findItem(R.id.refresh);
		if (topicList == null || topicList.isEmpty()) {
			refresh.setActionView(R.layout.refresh);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			((MainActivity) getActivity()).toggle();
			break;

		case R.id.newtopic:
			Intent intent = new Intent(Intents.SHOW_NEW);
			getActivity().startActivity(intent);
			break;

		case R.id.refresh:
			refresh.setActionView(R.layout.refresh);
			onRefresh();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
