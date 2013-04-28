package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.ProgressBar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class NodeActivity extends Activity {

	private final static String TAG = "ContentFragment";

	

	static final String KEY_ID = "id";
	static final String KEY_TITLE = "title";
	static final String KEY_REPLIES = "replies";
	static final String KEY_USERNAME = "username";
	static final String KEY_AVATAR = "avatar";
	static final String KEY_NODE = "node";
	LazyAdapter mAdapter = null;
	ArrayList<HashMap<String, String>> topicList = null;
	private ListView listView;
	private ProgressBar progressBar;
	private ProgressBar progressBar2;

	int recentPageNum = 1;
	String nodeLink = null;
	String nodeName = null;

	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_topic);
		mContext = this;

		final ActionBar ab = getSupportActionBar();
		// ab.setTitle(R.string.app_name);
		ab.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		nodeName = intent.getStringExtra("EXTRA_NODE_NAME");
		nodeLink = intent.getStringExtra("EXTRA_NODE_LINK");
		
		getSupportActionBar().setTitle(nodeName);

		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		progressBar2 = (ProgressBar) findViewById(R.id.progress_bar2);

		if (topicList == null) {
			topicList = new ArrayList<HashMap<String, String>>();
			new GetDataTask().execute();
			mAdapter = new LazyAdapter(this, topicList);
			progressBar.setVisibility(View.VISIBLE);
			progressBar2.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.GONE);
			progressBar2.setVisibility(View.GONE);
		}

		listView = (ListView) findViewById(R.id.pull_refresh_list);
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (topicList.get(position).get(TopicFragment.KEY_ID) == MainActivity.MORE_TAG) {

					progressBar2.setVisibility(View.VISIBLE);
					new GetDataTask().execute();
				} else {
					String tid = topicList.get(position).get(
							TopicFragment.KEY_ID);
					Intent contentIntent = new Intent(Intents.SHOW_CONTENT);
					contentIntent.putExtra("EXTRA_TOPIC_ID", tid);
					startActivity(contentIntent);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.fragment_topic, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case R.id.newtopic:
			Intent intent = new Intent(Intents.SHOW_NEW);
			startActivity(intent);
			break;

		case R.id.refresh:
			onRefresh();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public void onRefresh() {
		topicList.clear();
		mAdapter.notifyDataSetChanged();
		progressBar.setVisibility(View.VISIBLE);
		recentPageNum = 0;
		new GetDataTask().execute();
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			String url = "http://v2ex.com" + nodeLink + "?p=" + recentPageNum;
			System.out.println("url===> " + url);

			HtmlParser.getNodeTopics(url, nodeName, topicList);
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
