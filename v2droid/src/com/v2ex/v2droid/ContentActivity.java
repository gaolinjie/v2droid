package com.v2ex.v2droid;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpStatus;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.neofonie.mobile.app.android.widget.crouton.Crouton;
import de.neofonie.mobile.app.android.widget.crouton.Style;

public class ContentActivity extends Activity {

	private final static String TAG = "ContentFragment";

	Context mContext;
	private WebView mRepliesListView = null;
	private RepliesAdapter mRepliesListViewAdapter = null;
	private ImageView mFavImageView = null;
	private String mReplies = null;
	public ImageLoader mImageLoader = null;
	private static Integer mRepliesCount = 0;
	View contentView;
	ScrollView scrollView;  

	// Replies HashMap Key
	static final String KEY_ID = "id";
	static final String KEY_THANKS = "thanks";
	static final String KEY_CONTENT = "content_rendered";
	static final String KEY_USERNAME = "username";
	static final String KEY_AVATAR = "avatar";

	String topicID;
	String nodeName;
	String fav="";
	boolean isFav = false;
	boolean favSuccess = false;

	TextView title_text;
	WebView content_text;
	ImageView thumb_image;
	TextView info_text;
	TextView bottom_text;
	TextView noreplyView;

	HashMap<String, String> mContent = null;
	int replyNum;
	String once;
	Document docReply;

	private MenuItem refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);
		mContext = this;

		final ActionBar ab = getSupportActionBar();
		// ab.setTitle(R.string.app_name);
		ab.setDisplayHomeAsUpEnabled(true);

		scrollView = (ScrollView) findViewById(R.id.ScrollView);  
		
		mImageLoader = new ImageLoader(getApplicationContext());

		Intent intent = getIntent();
		topicID = intent.getStringExtra("EXTRA_TOPIC_ID");
		nodeName = intent.getStringExtra("EXTRA_NODE_NAME");
		getSupportActionBar().setTitle(nodeName);
		System.out.println("访问[@@@@@" + topicID);

		if (mReplies == null) {
			new GetDataTask().execute();
		}

		mRepliesListView = (WebView) findViewById(R.id.replies_list);
		mRepliesListView.getSettings().setJavaScriptEnabled(true);
		mRepliesListView.getSettings().setPluginState(PluginState.ON);
		mRepliesListView.getSettings().setPluginsEnabled(true);
		mRepliesListView.setVisibility(View.VISIBLE);
		mRepliesListView.setVisibility(View.GONE);

		noreplyView = (TextView) findViewById(R.id.noreply_text);
		noreplyView.setVisibility(View.GONE);

		// At first, disappear the content and replies view
		contentView = findViewById(R.id.content_wraper);
		contentView.setVisibility(View.GONE);

		// Setup the favorite icon
		mFavImageView = (ImageView) findViewById(R.id.fav_icon);
		mFavImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if ((String) mFavImageView.getTag() == mContext
						.getString(R.string.favourite_off)) {
					mFavImageView.setImageResource(R.drawable.favourite_red);
					mFavImageView.setTag(mContext
							.getString(R.string.favourite_on));
					//Crouton.makeText((ContentActivity) mContext,
							//R.string.favourite_on, Style.ALERT).show();
				} else {
					mFavImageView.setImageResource(R.drawable.favourite);
					mFavImageView.setTag(mContext
							.getString(R.string.favourite_off));
					//Crouton.makeText((ContentActivity) mContext,
							//R.string.favourite_off, Style.CONFIRM).show();
				}
				
				new GetDataTask3().execute();
			}
		});

		// Setup the content and replies view
		title_text = (TextView) findViewById(R.id.title);
		content_text = (WebView) findViewById(R.id.content);
		content_text.getSettings().setJavaScriptEnabled(true);
		content_text.getSettings().setPluginState(PluginState.ON);
		content_text.getSettings().setPluginsEnabled(true);
		thumb_image = (ImageView) findViewById(R.id.user_image);
		info_text = (TextView) findViewById(R.id.info_text);
		bottom_text = (TextView) findViewById(R.id.bottom_text);
		
		LinearLayout avatar_layout = (LinearLayout) findViewById(R.id.avatar);
		avatar_layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(ContentActivity.this, UserActivity.class);
				intent.putExtra("EXTRA_USER_ID", mContent.get(ApiClient.KEY_USERNAME));
				startActivity(intent);  	
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.fragment_content, menu);
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

		case R.id.reply:
			Intent intent = new Intent(Intents.SHOW_REPLY);
			intent.putExtra("EXTRA_TOPIC_ID", topicID);
			intent.putExtra("EXTRA_TOPIC_ONCE", once);
			startActivityForResult(intent, 1);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			String html = data.getExtras().getString("html");
			docReply = Jsoup.parse(html);
			new GetDataTask2().execute();

			break;
		}
	}

	public void onRefresh() {
		new GetDataTask().execute();
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };
			String url = "http://v2ex.com/t/" + topicID;

			AppContext ac = (AppContext) getApplication();

			Document doc;
			mContent = new HashMap<String, String>();

			try {
				doc = ApiClient.get(ac, url, URLs.HOST).parse();
				replyNum = ApiClient.getTopic(ac, doc, mContent);

			} catch (IOException e) {

			}

			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			updateUI();
			refresh.setActionView(null);

			super.onPostExecute(result);
		}
	}

	private class GetDataTask2 extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };

			AppContext ac = (AppContext) getApplication();

			replyNum = ApiClient.getTopic(ac, docReply, mContent);

			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			updateUI();

			super.onPostExecute(result);
		}
	}
	
	private class GetDataTask3 extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			String[] s = { "", "" };

			String favUrl = "http://v2ex.com" + fav;
			String refUrl = "http://v2ex.com/t/" + topicID;
			AppContext ac = (AppContext) getApplication();
			System.out.println("favUrl=====>" + favUrl);
			System.out.println("refUrl=====>" + refUrl);
			try {
				Response response = ApiClient.get(ac, favUrl, refUrl);
				System.out.println("reply response=====>" + response.statusCode());
				if (response.statusCode() == 200) {
					isFav = !isFav;
					favSuccess = true;
				} else {
					favSuccess = false;
				}
			} catch (IOException e) {
				System.out.println("IOException=====>");

			}
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (favSuccess) {
				if (isFav) {
					mFavImageView.setImageResource(R.drawable.favourite_red);
					mFavImageView.setTag(mContext
							.getString(R.string.favourite_on));
					Crouton.makeText((ContentActivity) mContext,
							R.string.favourite_on, Style.ALERT).show();
				} else {
					mFavImageView.setImageResource(R.drawable.favourite);
					mFavImageView.setTag(mContext
							.getString(R.string.favourite_off));
					Crouton.makeText((ContentActivity) mContext,
							R.string.favourite_off, Style.CONFIRM).show();
				}
			} else {
				if (isFav) {
					mFavImageView.setImageResource(R.drawable.favourite_red);
					mFavImageView.setTag(mContext
							.getString(R.string.favourite_on));
				} else {
					mFavImageView.setImageResource(R.drawable.favourite);
					mFavImageView.setTag(mContext
							.getString(R.string.favourite_off));
				}
				Crouton.makeText((ContentActivity) mContext,
						R.string.favourite_failed, Style.INFO).show();
			}
			

			super.onPostExecute(result);
		}
	}

	private void updateUI() {
		if (nodeName.isEmpty()) {
			nodeName = mContent.get(ApiClient.KEY_NODE);
			getSupportActionBar().setTitle(nodeName);
		}
		
		scrollView.scrollTo(10, 10);	
		
		fav = mContent.get(ApiClient.KEY_FAV);
		if (fav!=null) {
			isFav = fav.contains("unfavorite");
			if (isFav) {
				mFavImageView.setImageResource(R.drawable.favourite_red);
				mFavImageView.setTag(mContext
						.getString(R.string.favourite_on));
			} else {
				mFavImageView.setImageResource(R.drawable.favourite);
				mFavImageView.setTag(mContext
						.getString(R.string.favourite_off));
			}
		}

		once = mContent.get(ApiClient.KEY_ONCE);

		title_text.setText(mContent.get(ApiClient.KEY_TITLE));
		info_text.setText(mContent.get(ApiClient.KEY_INFO));
		bottom_text.setText(mContent.get(ApiClient.KEY_FAVORITE));

		String text = "<html><head>"
				+ "<style type=\"text/css\">body{color: #63656a }"
				+ "</style></head>"
				+ "<body link=\"#C0C0C0\" vlink=\"#808080\" alink=\"#FF0000\">"
				+ mContent.get(ApiClient.KEY_CONTENT) + "</body></html>";
		content_text
				.loadDataWithBaseURL(null, text, "text/html", "UTF-8", null);
		content_text.getSettings().setLayoutAlgorithm(
				LayoutAlgorithm.SINGLE_COLUMN);

		mImageLoader.DisplayImage(mContent.get(ApiClient.KEY_AVATAR),
				thumb_image);
		contentView.setVisibility(View.VISIBLE);

		if (replyNum == 0) {
			mRepliesListView.setVisibility(View.GONE);
			noreplyView.setVisibility(View.VISIBLE);
		} else {
			mReplies = mContent.get(ApiClient.KEY_REPLIES);
			mRepliesListView.setVisibility(View.VISIBLE);
			mRepliesListView.loadDataWithBaseURL(null, mReplies, "text/html",
					"UTF-8", null);
			mRepliesListView.getSettings().setLayoutAlgorithm(
					LayoutAlgorithm.SINGLE_COLUMN);

			noreplyView.setVisibility(View.GONE);

		}
	}
}
