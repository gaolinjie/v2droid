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

public class UserFragment extends Fragment {

	private static UserFragment instance;
	private String mContent = "???";

	LazyAdapter mAdapter = null;
	ArrayList<HashMap<String, String>> topicList = null;
	ArrayList<HashMap<String, String>> tempList = null;
	private ListView listView;
	private ProgressBar progressBar;
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

	private MenuItem refresh;
	boolean bRefresh = false;
	boolean bIsLastPageTopic = false;
   	boolean bNotLoagTopic = false;
   	boolean bIsLastPageReply = false;
   	boolean bNotLoagReply= false;

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
		setHasOptionsMenu(true);

		Intent intent = getActivity().getIntent();
		userID = intent.getStringExtra("EXTRA_USER_ID");
		getSupportActivity().getSupportActionBar().setTitle("@" + userID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null;

		if (mContent != "最近的回复") {
			view = inflater.inflate(R.layout.fragment_user, null);
		} else {
			view = inflater.inflate(R.layout.fragment_user_replies, null);
		}

		return view;
	}

	@Override
	public void onViewCreated(View view) {
		super.onViewCreated(view);

		if (tempList == null) {
			tempList = new ArrayList<HashMap<String, String>>();
		}

		if (mContent != "最近的回复") {
			progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
			progressBar.setVisibility(View.GONE);

			if (topicList == null) {
				topicList = new ArrayList<HashMap<String, String>>();
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
						Intent contentIntent = new Intent(Intents.SHOW_CONTENT);
						contentIntent.putExtra("EXTRA_TOPIC_ID", tid);
						String node = topicList.get(position).get(
								TopicFragment.KEY_NODE);
						contentIntent.putExtra("EXTRA_NODE_NAME", node);
						getActivity().startActivity(contentIntent);
					}
				}
			});
		} else {
			progressBarReplies = (ProgressBar) view
					.findViewById(R.id.progress_bar2);
			progressBarReplies.setVisibility(View.GONE);
			if (repliesList == null) {
				repliesList = new ArrayList<HashMap<String, String>>();
				new GetDataTask2().execute();
				mRepliesAdapter = new UserRepliesAdapter(
						(Activity) getActivity(), repliesList);
			} else {
			}

			repliesListView = (ListView) view.findViewById(R.id.replies_list);
			repliesListView.setAdapter(mRepliesAdapter);

			repliesListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					if (repliesList.get(position).get(ApiClient.KEY_GRAY) == MainActivity.MORE_TAG) {
						progressBarReplies.setVisibility(View.VISIBLE);
						new GetDataTask2().execute();
					} else {
						String tid = repliesList.get(position).get(
								TopicFragment.KEY_ID);
						Intent contentIntent = new Intent(Intents.SHOW_CONTENT);
						contentIntent.putExtra("EXTRA_TOPIC_ID", tid);
						String node = "";
						contentIntent.putExtra("EXTRA_NODE_NAME", node);
						getActivity().startActivity(contentIntent);
						
					}
				}
			});
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
			if (!bIsLastPageTopic) {
				String url = "http://v2ex.com/member/" + userID + "/topics?p="
						+ recentPageNum;
				AppContext ac;
				if (getActivity()!=null) {
					ac = (AppContext) getActivity().getApplication();
				tempList.clear();
				try {
					if (userAvatar == null) {
						String urlAvatar = "http://v2ex.com/member/" + userID;
						Document docAvatar = ApiClient
								.get(ac, urlAvatar, URLs.HOST).parse();
						userAvatar = ApiClient.getUserAvatar(ac, docAvatar);
						if (userAvatar != null) {
							userAvatar = userAvatar.replace("large", "normal");
						} else {
							userAvatar = "";
						}
					}
					doc = ApiClient.get(ac, url, URLs.HOST).parse();
					bIsLastPageTopic = ApiClient.getUserTopics(ac, doc, tempList, userAvatar);
				} catch (IOException e) {
				}
				}
			} else {
				bNotLoagTopic = true;
			}
			
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			progressBar.setVisibility(View.GONE);
			if (bNotLoagTopic) {				
				Toast.makeText(getActivity().getApplicationContext(), R.string.no_more,
						Toast.LENGTH_LONG).show();
				return;
			}
			
			if (tempList != null && tempList.size() > 1) {
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
					Toast.makeText(getActivity().getApplicationContext(), R.string.load_failed,
					Toast.LENGTH_LONG).show();
				}
			}

			if (refresh.getActionView() != null) {
				refresh.setActionView(null);
			}

			super.onPostExecute(result);
		}
	}

	private class GetDataTask2 extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			if (!bIsLastPageReply) {
				String url = "http://v2ex.com/member/" + userID + "/replies?p="
						+ recentRepliesPageNum;
				AppContext ac;
				if (getActivity()!=null) {
					ac = (AppContext) getActivity().getApplication();
					tempList.clear();
					try {
						docReplies = ApiClient.get(ac, url, URLs.HOST).parse();
						bIsLastPageReply = ApiClient.getUserReplies(ac, docReplies, tempList);
					} catch (IOException e) {
					}
				}	
				
			} else {
				bNotLoagTopic = true;
			}
			

			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			progressBarReplies.setVisibility(View.GONE);
			if (bNotLoagTopic) {				
				Toast.makeText(getActivity().getApplicationContext(), R.string.no_more,
						Toast.LENGTH_LONG).show();
				return;
			}
			
			if (tempList != null && tempList.size() > 1) {
				if (bRefresh) {
					repliesList.clear();
					bRefresh = false;
					if (!repliesListView.isStackFromBottom()) {
						repliesListView.setStackFromBottom(true);
					}
					repliesListView.setStackFromBottom(false);
				}

				if (!repliesList.isEmpty()) {
					repliesList.remove(repliesList.size() - 1);
				}

				for (int i = 0; i < tempList.size(); i++) {
					repliesList.add(tempList.get(i));
				}
				recentRepliesPageNum++;
				mRepliesAdapter.notifyDataSetChanged();
			} else {
				if (getActivity()!=null) {
					Toast.makeText(getActivity().getApplicationContext(), R.string.load_failed,
					Toast.LENGTH_LONG).show();
				}
			}

			if (refresh!= null && refresh.getActionView() != null) {
				refresh.setActionView(null);
			}

			super.onPostExecute(result);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_user, menu);
		System.out
				.println("inflater.inflate(R.menu.activity_user, menu);======>");
		refresh = menu.findItem(R.id.refresh);

		if (mContent != "最近的回复") {
			if (topicList == null || topicList.isEmpty()) {
				refresh.setActionView(R.layout.refresh);
			}
		} else {
			if (repliesList == null || repliesList.isEmpty()) {
				refresh.setActionView(R.layout.refresh);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getActivity().finish();
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

		if (mContent != "最近的回复") {
			bNotLoagTopic = false;
			bIsLastPageTopic = false;
			recentPageNum = 1;
			new GetDataTask().execute();
		} else {
			bNotLoagReply = false;
			bIsLastPageReply = false;
			recentRepliesPageNum = 1;
			new GetDataTask2().execute();
		}

	}
}
