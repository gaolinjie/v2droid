package com.v2ex.v2droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

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
	ArrayList<HashMap<String, String>> tempList = null;
	NodeStickyAdapter<HashMap<String, String>> nodeAdapter = null;

	static final String KEY_ID = "id";
	static final String KEY_HEADER_ID = "header_id";
	static final String KEY_HEADER = "header";
	static final String KEY_NAME = "name";
	static final String KEY_LINK = "link";

	private SharedPreferences nodePrefs;
	String storedCollection = null;
	
	Document doc;
	private MenuItem refresh;
    boolean bRefresh = false;

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
	}

	@Override
	public void onViewCreated(View view) {
		super.onViewCreated(view);
		System.out.println("onViewCreated===> ");

		if (nodeList == null) {
			nodeList = new ArrayList<HashMap<String, String>>();
			tempList = new ArrayList<HashMap<String, String>>();
			
			new GetDataTask().execute();
		}
		nodeAdapter = new NodeStickyAdapter<HashMap<String, String>>(
				getActivity().getApplicationContext(), nodeList,
				R.layout.header, R.layout.item);
		

		mGridView = (GridView) view.findViewById(R.id.asset_grid);
		mGridView.setOnItemClickListener(this);
		
		mGridView.setAdapter(nodeAdapter);
		nodeAdapter.notifyDataSetChanged();

		mGridView.setSelection(mFirstVisible);

		setHasOptionsMenu(true);
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setActivateOnItemClick(boolean activateOnItemClick) {

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
			
			AppContext ac = (AppContext) getActivity().getApplication();
			
			//Document doc;
			tempList.clear();
			try {
				doc = ApiClient.get(ac, url, URLs.HOST);
				ApiClient.getNodes(ac, doc, tempList);
				
			} catch (IOException e) {	
			}
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (!tempList.isEmpty()) {
				if (bRefresh) {
					nodeList.clear();
					bRefresh = false;
					if (!mGridView.isStackFromBottom()) {
						mGridView.setStackFromBottom(true);
					}
					mGridView.setStackFromBottom(false);
				}
				
				for (int i=0; i < tempList.size(); i++) {
					nodeList.add(tempList.get(i));
				}

				nodeAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "貌似网络不给力啊...",
						Toast.LENGTH_SHORT).show();
			}

			refresh.setActionView(null);
			super.onPostExecute(result);
		}
	}
	
	@Override
    public void onCreateOptionsMenu(
          Menu menu, MenuInflater inflater) {
       inflater.inflate(R.menu.fragment_node, menu);
       refresh = menu.findItem(R.id.refresh);
       if (nodeList==null || nodeList.isEmpty()) {
    	   refresh.setActionView(R.layout.refresh);
       }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		((MainActivity)getActivity()).toggle();
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
    	new GetDataTask().execute();
    }
}
