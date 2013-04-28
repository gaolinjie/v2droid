package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;

public class NodeFragment extends Fragment implements OnItemClickListener {

	private static NodeFragment instance;
	private static final String KEY_LIST_POSITION = "key_list_position";
	private static final String NODE_PREFS = "NodePrefsFile";
	private static final String HOT_NODES = "hot_nodes";
	
	public static final String SHOW_NODE = "com.v2ex.v2droid.action.SHOW_NODE";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	private int mFirstVisible;

	private GridView mGridView;

	private Intent intent;

	ArrayList<HashMap<String, String>> nodeList = null;
	NodeStickyAdapter<HashMap<String, String>> nodeAdapter = null;

	static final String KEY_ID = "id";
	static final String KEY_HEADER_ID = "header_id";
	static final String KEY_HEADER = "header";
	static final String KEY_NAME = "name";
	static final String KEY_LINK = "link";

	private SharedPreferences nodePrefs;
	String storedCollection = null;

	public static NodeFragment getInstance() {
		if (NodeFragment.instance == null) {
			return new NodeFragment();
		}
		return NodeFragment.instance;
	}

	public NodeFragment() {
		NodeFragment.instance = this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		System.out.println("onCreate===> ");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("onCreateView===> ");
		View view = null;
		view = inflater.inflate(R.layout.fragment_node, container, false);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> gridView, View view, int position,
			long id) {
		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		// mCallbacks.onItemSelected(position);
		System.out.println("onCreateView===> " + nodeList.get(position).get(KEY_LINK));
		Intent intent = new Intent(SHOW_NODE);
		intent.putExtra("EXTRA_NODE_NAME", nodeList.get(position).get(KEY_NAME));
		intent.putExtra("EXTRA_NODE_LINK", nodeList.get(position).get(KEY_LINK));
		getActivity().startActivity(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
		/*
		if (storedCollection == null && !nodeList.isEmpty()) {
			System.out.println("onSaveInstanceState===> ");
			JSONArray result = new JSONArray(nodeList);
			SharedPreferences.Editor prefEditor = nodePrefs.edit();
			prefEditor.putString(HOT_NODES, result.toString());
			prefEditor.commit();
		}*/

	}

	@Override
	public void onViewCreated(View view) {
		super.onViewCreated(view);
		System.out.println("onViewCreated===> ");

		if (nodeList == null) {
			nodeList = new ArrayList<HashMap<String, String>>();
			
/*
			nodePrefs = getActivity().getApplicationContext()
					.getSharedPreferences(NODE_PREFS, 0);
			storedCollection = nodePrefs.getString(HOT_NODES, null);

			if (storedCollection != null) {
				System.out.println("storedCollection != null===> ");
				System.out.println("storedCollectionl===> " + storedCollection);
				try {
					JSONArray array = new JSONArray(storedCollection);
					HashMap<String, String> item = null;
					for (int i = 0; i < array.length(); i++) {
						String obj = (String) array.get(i);
						JSONObject ary = new JSONObject(obj);
						Iterator<String> it = ary.keys();
						item = new HashMap<String, String>();
						while (it.hasNext()) {
							String key = it.next();
							item.put(key, (String) ary.get(key));
						}
						nodeList.add(item);
					}
					
				} catch (JSONException e) {
					// Log.e(TAG, "while parsing", e);
				}
			} else {
				System.out.println("storedCollection == null===> ");
				new GetDataTask().execute();
			}*/
			new GetDataTask().execute();
		}
		nodeAdapter = new NodeStickyAdapter<HashMap<String, String>>(
				getActivity().getApplicationContext(), nodeList,
				R.layout.header, R.layout.item);
		

		mGridView = (GridView) view.findViewById(R.id.asset_grid);
		mGridView.setOnItemClickListener(this);
		
		mGridView.setAdapter(nodeAdapter);
		nodeAdapter.notifyDataSetChanged();

		/*
		 * Currently set in the XML layout, but this is how you would do it in
		 * your code.
		 */
		// mGridView.setColumnWidth((int) calculatePixelsFromDips(100));
		// mGridView.setNumColumns(StickyGridHeadersGridView.AUTO_FIT);
		/*
		 * mGridView.setAdapter(new
		 * StickyGridHeadersSimpleArrayAdapter<String>(getActivity()
		 * .getApplicationContext(),
		 * getResources().getStringArray(R.array.countries), R.layout.header,
		 * R.layout.item));
		 */

		/*
		 * if (savedInstanceState != null) { mFirstVisible =
		 * savedInstanceState.getInt(KEY_LIST_POSITION); }
		 */
		mGridView.setSelection(mFirstVisible);
		/*
		 * // Restore the previously serialized activated item position. if
		 * (savedInstanceState != null &&
		 * savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
		 * setActivatedPosition
		 * (savedInstanceState.getInt(STATE_ACTIVATED_POSITION)); }
		 */

		// ((StickyGridHeadersGridView)mGridView).setOnHeaderClickListener(this);
		// ((StickyGridHeadersGridView)mGridView).setOnHeaderLongClickListener(this);

		setHasOptionsMenu(true);
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mGridView
					.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
							: ListView.CHOICE_MODE_NONE);
		}
	}

	private float calculatePixelsFromDips(float dips) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips,
				getResources().getDisplayMetrics());
	}

	@SuppressLint("NewApi")
	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			mGridView.setItemChecked(mActivatedPosition, false);
		} else {
			mGridView.setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			String url = "http://www.v2ex.com";

			HtmlParser.getNodes(url, nodeList);
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (!nodeList.isEmpty()) {
				nodeAdapter.notifyDataSetChanged();
			}
			super.onPostExecute(result);
		}
	}
}
