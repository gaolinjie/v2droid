package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Toast;
import org.jsoup.nodes.Document;

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
	ArrayList<HashMap<String, String>> tempList = null;
	private ListView listView;
	private ProgressBar progressBar;

	int recentPageNum = 1;
	String nodeLink = null;
	String nodeName = null;

	Context mContext;

	private MenuItem refresh;
	boolean bRefresh = false;
	boolean bIsLastPage = false;
	boolean bNotLoag = false;
	Document doc;

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
		progressBar.setVisibility(View.GONE);

		if (topicList == null) {
			topicList = new ArrayList<HashMap<String, String>>();
			tempList = new ArrayList<HashMap<String, String>>();
			new GetDataTask().execute();
			mAdapter = new LazyAdapter(this, topicList);
		}

		listView = (ListView) findViewById(R.id.pull_refresh_list);
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
					Intent contentIntent = new Intent(Intents.SHOW_CONTENT);
					contentIntent.putExtra("EXTRA_TOPIC_ID", tid);
					String node = topicList.get(position).get(
							TopicFragment.KEY_NODE);
					contentIntent.putExtra("EXTRA_NODE_NAME", node);
					startActivity(contentIntent);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.fragment_topic, menu);
		refresh = menu.findItem(R.id.refresh);
		refresh.setActionView(R.layout.refresh);

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
			refresh.setActionView(R.layout.refresh);
			onRefresh();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	public void onRefresh() {
		refresh.setActionView(R.layout.refresh);
		bRefresh = true;
		bNotLoag = false;
		bIsLastPage = false;
		recentPageNum = 1;

		new GetDataTask().execute();
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			if (!bIsLastPage) {
				String url = "http://v2ex.com" + nodeLink + "?p="
						+ recentPageNum;
				AppContext ac = (AppContext) getApplication();
				tempList.clear();
				bIsLastPage = ApiClient.getNodeTopics(ac, url, nodeName,
						tempList);
			} else {
				bNotLoag = true;
			}

			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			progressBar.setVisibility(View.GONE);
			if (bNotLoag) {
				Toast.makeText(getApplicationContext(), "没有更多啦...",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (tempList.size() > 1) {
				System.out.println("!tempList.isEmpty()======>");
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
				Toast.makeText(getApplicationContext(), "貌似网络不给力啊...",
						Toast.LENGTH_SHORT).show();
			}

			refresh.setActionView(null);

			super.onPostExecute(result);
		}
	}
}
