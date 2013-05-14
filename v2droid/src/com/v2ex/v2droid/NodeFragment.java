package com.v2ex.v2droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Toast;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

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
			
			tempList.clear();
			getHotNodes(tempList);
			
			if (bRefresh || tempList.isEmpty()) {
				String url = "http://www.v2ex.com";
				AppContext ac = (AppContext) getActivity().getApplication();
				tempList.clear();
				try {
					doc = ApiClient.get(ac, url, URLs.HOST);
					ApiClient.getHotNodes(ac, doc, tempList);
					
					if (!tempList.isEmpty()) {
						setHotNodes(tempList);
					}
					
				} catch (IOException e) {	
				}
			}
			
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (!tempList.isEmpty()) {
				nodeList.clear();
				if (bRefresh) {
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
				if (getActivity()!=null) {
					Toast.makeText(getActivity().getApplicationContext(),
							"貌似网络不给力啊...", Toast.LENGTH_SHORT).show();
				}
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
    
    public void getHotNodes(ArrayList<HashMap<String, String>> nodes) {
		DatabaseHelper dbhelper = new DatabaseHelper(getActivity(), AppConfig.DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		if (db != null) {
			db.execSQL(
					  "CREATE TABLE IF NOT EXISTS hot_nodes  ( id TEXT, header_id TEXT, header TEXT, name TEXT, link TEXT );"
					  );
			Cursor result = db.rawQuery("SELECT * FROM hot_nodes", null);
			if (result.getCount() > 0) {
				result.moveToFirst();
				while (!result.isAfterLast()) {
					String id = result.getString(0);
					String header_id = result.getString(1);
					String header = result.getString(2);
					String name = result.getString(3);
					String link = result.getString(4);
					
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(KEY_ID, id);
					map.put(KEY_HEADER_ID, header_id);
					map.put(KEY_HEADER, header);
					map.put(KEY_NAME, name);
					map.put(KEY_LINK, link);
					nodes.add(map);
					
					result.moveToNext();
				}
			}
			result.close();
			db.close();
		}
	}
    
    public void setHotNodes(ArrayList<HashMap<String, String>> nodes) {
		String[] s = new String[] {};
		DatabaseHelper dbhelper = new DatabaseHelper(getActivity(), AppConfig.DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getWritableDatabase();

		if (db != null) {
			  db.execSQL("DROP TABLE IF EXISTS hot_nodes"); 
			  db.execSQL(
			  "CREATE TABLE IF NOT EXISTS hot_nodes  ( id TEXT, header_id TEXT, header TEXT, name TEXT, link TEXT );"
			  );
			  
			  for (int i=0; i<nodes.size(); i++) {
				  ContentValues values = new ContentValues();   
		          values.put("id", nodes.get(i).get(ApiClient.KEY_ID));
		          values.put("header_id", nodes.get(i).get(ApiClient.KEY_HEADER_ID));
		          values.put("header", nodes.get(i).get(ApiClient.KEY_HEADER));
		          values.put("name", nodes.get(i).get(ApiClient.KEY_NAME));
		          values.put("link", nodes.get(i).get(ApiClient.KEY_LINK));

		          db.insert("hot_nodes", null, values);
			  }
			  	 
			db.close();
		}
	}
}
