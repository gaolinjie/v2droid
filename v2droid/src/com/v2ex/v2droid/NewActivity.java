package com.v2ex.v2droid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.AutoCompleteTextView;
import org.holoeverywhere.widget.Toast;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class NewActivity extends Activity {

	private AutoCompleteTextView nodeEdit;
	private EditText titleEdit;
	private EditText contentEdit;

	String link;
	String title;
	String content;
	Response response;

	ArrayAdapter<String> adapter;

	private static String[] NODES = new String[] {};
	ArrayList<HashMap<String, String>> nodeList = null;

	private MenuItem refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);

		nodeEdit = (AutoCompleteTextView) findViewById(R.id.node_edit);
		titleEdit = (EditText) findViewById(R.id.title_edit);
		contentEdit = (EditText) findViewById(R.id.content_edit);
		nodeEdit.requestFocus();
		nodeEdit.setThreshold(1);

		nodeList = new ArrayList<HashMap<String, String>>();

		new GetNodeTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.activity_new, menu);
		refresh = menu.findItem(R.id.send);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(NewActivity.this.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			finish();
			break;

		case R.id.send:
			refresh.setActionView(R.layout.refresh);
			new GetDataTask().execute();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };

			System.out.println("nodeEdit.getText().toString()=====>"
					+ nodeEdit.getText().toString());
			link = getNodeLinkByName(nodeEdit.getText().toString());
			link = link.replaceAll("/go/", "/");
			String url = "http://v2ex.com/new" + link;

			System.out.println("url=====>" + url);

			title = titleEdit.getText().toString();
			content = contentEdit.getText().toString();

			AppContext ac = (AppContext) getApplication();

			Document doc;

			try {
				doc = ApiClient.get(ac, url, URLs.HOST).parse();
				response = ApiClient.newTopic(ac, url, title, content);

			} catch (IOException e) {

			}

			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (response != null && response.statusCode() == 200) {
				NewActivity.this.finish();
			} else {
				Toast.makeText(getApplicationContext(), "对不住啊，好像没发成功...",
						Toast.LENGTH_SHORT).show();
			}
			refresh.setActionView(null);

			super.onPostExecute(result);
		}
	}

	private class GetNodeTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };

			if (NODES.length == 0) {
				NODES = getAllNodeNames();
				if (NODES.length == 0) {
					String url = "http://v2ex.com/planes";
					AppContext ac = (AppContext) getApplication();
					Document doc;
					try {
						doc = ApiClient.get(ac, url, URLs.HOST).parse();
						NODES = ApiClient.getAllNodes(ac, doc, nodeList);

						if (!nodeList.isEmpty()) {
							setAllNodes(nodeList);
						}
					} catch (IOException e) {
					}
				}
			}

			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (NODES.length > 0) {
				adapter = new ArrayAdapter<String>(NewActivity.this,
						android.R.layout.simple_dropdown_item_1line, NODES);
				nodeEdit.setAdapter(adapter);
			}

			super.onPostExecute(result);
		}
	}

	public String[] getAllNodeNames() {
		String[] s = new String[] {};
		DatabaseHelper dbhelper = new DatabaseHelper(this, AppConfig.DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		if (db != null) {
			db.execSQL("CREATE TABLE IF NOT EXISTS all_nodes  ( id TEXT, name TEXT, link TEXT );");
			Cursor result = db.rawQuery("SELECT name FROM all_nodes", null);
			if (result.getCount() > 0) {
				int i = 0;
				s = new String[result.getCount()];
				result.moveToFirst();
				while (!result.isAfterLast()) {
					String name = result.getString(0);
					s[i] = name;
					i++;
					result.moveToNext();
				}
			}
			result.close();
			db.close();
		}
		return s;
	}

	public String getNodeLinkByName(String name) {
		String link = "";
		DatabaseHelper dbhelper = new DatabaseHelper(this, AppConfig.DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		if (db != null) {
			db.execSQL("CREATE TABLE IF NOT EXISTS all_nodes  ( id TEXT, name TEXT, link TEXT );");
			Cursor result = db.rawQuery(
					"SELECT link FROM all_nodes where name = ?",
					new String[] { name });
			if (result.getCount() > 0) {
				result.moveToFirst();
				if (!result.isAfterLast()) {
					link = result.getString(0);
				}
			}
			result.close();
			db.close();
		}
		return link;
	}

	public void setAllNodes(ArrayList<HashMap<String, String>> nodes) {
		String[] s = new String[] {};
		DatabaseHelper dbhelper = new DatabaseHelper(this, AppConfig.DB_NAME,
				null, 1);
		SQLiteDatabase db = dbhelper.getWritableDatabase();

		if (db != null) {
			db.execSQL("DROP TABLE IF EXISTS all_nodes");
			db.execSQL("CREATE TABLE IF NOT EXISTS all_nodes  ( id TEXT, name TEXT, link TEXT );");

			for (int i = 0; i < nodes.size(); i++) {
				ContentValues values = new ContentValues();
				values.put("id", nodes.get(i).get(ApiClient.KEY_ID));
				values.put("name", nodes.get(i).get(ApiClient.KEY_NAME));
				values.put("link", nodes.get(i).get(ApiClient.KEY_LINK));

				db.insert("all_nodes", null, values);
			}

			db.close();
		}
	}
}
