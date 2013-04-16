package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TopicFragment extends SherlockFragment {

	private final static String TAG = "TopicFragment";
	private static final String KEY_CONTENT = "TopicFragment:Content";
	public static final String SHOW_CONTENT = "com.v2ex.v2droid.action.SHOW_CONTENT";

	Context mContext;

	static final int MENU_MANUAL_REFRESH = 0;
	static final int MENU_DISABLE_SCROLL = 1;
	static final int MENU_SET_MODE = 2;
	static final int MENU_DEMO = 3;

	// private LinkedList<String> mListItems;
	private PullToRefreshListView mPullRefreshListView;

	// ListView list;
	LazyAdapter mAdapter = null;

	// url to make request
	private static String URL_SUFFIX_TOPICS = "/api/topics/latest.json";

	static final String KEY_ID = "id";
	static final String KEY_TITLE = "title";
	static final String KEY_REPLIES = "replies";
	static final String KEY_USERNAME = "username";
	static final String KEY_AVATAR = "avatar";
	static final String KEY_NODE = "node";

	// JSON Node names
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_REPLIES = "replies";
	private static final String TAG_MEMBER = "member";
	// private static final String TAG_MEMBER_ID = "id";
	private static final String TAG_MEMBER_USERNAME = "username";
	private static final String TAG_MEMBER_AVATAR_NORMAL = "avatar_normal";
	private static final String TAG_NODE = "node";
	// private static final String TAG_NODE_ID = "id";
	private static final String TAG_NODE_TITLE = "title";

	// contacts JSONArray
	JSONArray topics = null;
	ArrayList<HashMap<String, String>> topicList = null;

	private String[] TOPIC_TYPE_TITLE = new String[] { "全部", "技术", "创意", "好玩",
			"Apple", "酷工作", "交易", "城市", "问与答", "最热", "节点收藏", "关注" };

	public static TopicFragment newInstance(String text) {
		TopicFragment fragment = new TopicFragment();

		fragment.mContent = text;
		Log.i(TAG, fragment.mContent);

		return fragment;
	}

	private String mContent = "???";
	int recentPageNum = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null;

		if (mContent != "类别") {
			view = inflater.inflate(R.layout.fragment_topic, null);

			mPullRefreshListView = (PullToRefreshListView) view
					.findViewById(R.id.pull_refresh_list);
			mPullRefreshListView.setMode(Mode.BOTH);

			topicList = new ArrayList<HashMap<String, String>>();
			ListView actualListView = mPullRefreshListView.getRefreshableView();
			mAdapter = new LazyAdapter(getActivity(), topicList);
			actualListView.setAdapter(mAdapter);

			// Set a listener to be invoked when the list should be refreshed.
			mPullRefreshListView
					.setOnRefreshListener(new OnRefreshListener<ListView>() {
						@Override
						public void onRefresh(
								PullToRefreshBase<ListView> refreshView) {
							mPullRefreshListView.setLastUpdatedLabel(DateUtils.formatDateTime(
									mContext.getApplicationContext(),
									System.currentTimeMillis(),
									DateUtils.FORMAT_SHOW_TIME
											| DateUtils.FORMAT_SHOW_DATE
											| DateUtils.FORMAT_ABBREV_ALL));

							// Do work to refresh the list here.
							new GetDataTask().execute();
						}
					});

			// Click event for single list row
			mPullRefreshListView
					.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							String tid = topicList.get(position - 1).get(
									TopicFragment.KEY_ID);
							Log.e(TAG, tid);
							Intent contentIntent = new Intent(SHOW_CONTENT);
							contentIntent.putExtra("EXTRA_TOPIC_ID", tid);
							mContext.startActivity(contentIntent);
						}
					});

			/**
			 * Add Sound Event Listener
			 */
			SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(
					mContext);
			soundListener.addSoundEvent(State.RESET, R.raw.refresh);
			mPullRefreshListView.setOnPullEventListener(soundListener);

			//setupTopicsUI(view);
			HtmlParser.getTopics("http://v2ex.com/?tab=all", topicList);
			if (!topicList.isEmpty()) {
				mAdapter.notifyDataSetChanged();
			}		
		} else {
			view = inflater.inflate(R.layout.fragment_topic_type, null);
			ListView listView = (ListView) view
					.findViewById(R.id.topic_type_list);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
					R.layout.list_row_topic_type, R.id.topic_type_title,
					TOPIC_TYPE_TITLE);

			// Assign adapter to ListView
			listView.setAdapter(adapter);
		}

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}

		mContext = getActivity();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}

	public void setupTopicsUI(final View view) {
		V2HttpClient.get(URL_SUFFIX_TOPICS, null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray topics) {
						try {
							int n = topics.length();

							// looping through All Contacts
							for (int i = 0; i < n; i++) {
								JSONObject c = topics.getJSONObject(i);

								// Storing each json item in variable
								String id = c.getString(TAG_ID);
								String title = c.getString(TAG_TITLE);
								String replies = c.getString(TAG_REPLIES);

								// Member is agin JSON Object
								JSONObject member = c.getJSONObject(TAG_MEMBER);
								// String member_id =
								// member.getString(TAG_MEMBER_ID);
								String member_username = member
										.getString(TAG_MEMBER_USERNAME);
								String member_avatar_normal = member
										.getString(TAG_MEMBER_AVATAR_NORMAL);

								// Node is agin JSON Object
								JSONObject node = c.getJSONObject(TAG_NODE);
								// String node_id = node.getString(TAG_NODE_ID);
								String node_title = node
										.getString(TAG_NODE_TITLE);

								// creating new HashMap
								HashMap<String, String> map = new HashMap<String, String>();

								// adding each child node to HashMap key =>
								// value
								map.put(KEY_ID, id);
								map.put(KEY_TITLE, title);
								map.put(KEY_USERNAME, member_username);
								map.put(KEY_REPLIES, replies);
								map.put(KEY_AVATAR, member_avatar_normal);
								map.put(KEY_NODE, node_title);

								// adding HashList to ArrayList
								topicList.add(map);
							}

							if (n > 0) {
								mAdapter.notifyDataSetChanged();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			String[] s = { "A", "A" };
			/*try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}*/
			//topicList.clear();
			HtmlParser.getTopics("http://v2ex.com/recent?p="+recentPageNum, topicList);
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// mListItems.addFirst("Added after refresh...");
			if (!topicList.isEmpty()) {
				mAdapter.notifyDataSetChanged();
				recentPageNum++;
			}

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

}
