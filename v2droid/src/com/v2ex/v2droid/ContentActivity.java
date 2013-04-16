
package com.v2ex.v2droid;

import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;

import de.neofonie.mobile.app.android.widget.crouton.Crouton;
import de.neofonie.mobile.app.android.widget.crouton.Style;


public class ContentActivity extends Activity {
	
	private final static String TAG = "ContentFragment";

	Context mContext;
	private WebView mRepliesListView = null;
	private RepliesAdapter mRepliesListViewAdapter = null;
	private ImageView mFavImageView = null;
	private ArrayList<HashMap<String, String>> mRepliesList = null;
	public ImageLoader mImageLoader = null;
	private static Boolean mFlagContentDataDone = false;
	private static Boolean mFlagRepliesDataDone = false;
	private static Integer mRepliesCount = 0;

	// Url Suffix
	private static String URL_SUFFIX_TOPIC = "/api/topics/show.json?id=";
	private static String URL_SUFFIX_REPLIES = "/api/replies/show.json?topic_id=";

	// JSON Node names
	private static final String TAG_ID = "id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_CONTENT = "content_rendered";
	private static final String TAG_MEMBER = "member";
	private static final String TAG_MEMBER_USERNAME = "username";
	private static final String TAG_MEMBER_AVATAR_NORMAL = "avatar_normal";
	private static final String TAG_NODE = "node";
	private static final String TAG_NODE_TITLE = "title";
	private static final String TAG_THANKS = "thanks";
	private static final String TAG_MEMBER_AVATAR_MINI = "avatar_mini";

	// Replies HashMap Key
	static final String KEY_ID = "id";
	static final String KEY_THANKS = "thanks";
	static final String KEY_CONTENT = "content_rendered";
	static final String KEY_USERNAME = "username";
	static final String KEY_AVATAR = "avatar";
	
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);
		mContext = this;
		
		final ActionBar ab = getSupportActionBar();
        //ab.setTitle(R.string.app_name);
        ab.setDisplayHomeAsUpEnabled(true);
        
        mImageLoader = new ImageLoader(getApplicationContext());
        
		Intent intent = getIntent();
		String topicID = intent.getStringExtra("EXTRA_TOPIC_ID");
		System.out.println("访问[@@@@@" + topicID);

		mRepliesListView = (WebView) findViewById(R.id.replies_list);
		mRepliesListView.setVisibility(View.GONE);

		// At first, disappear the content and replies view
		View contentView = findViewById(R.id.content_wraper);
		contentView.setVisibility(View.GONE);
		TextView noreplyView = (TextView) findViewById(R.id.noreply_text);
		noreplyView.setVisibility(View.GONE);
		
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		progressBar.setVisibility(View.VISIBLE);

		// Setup the favorite icon
		mFavImageView = (ImageView) findViewById(R.id.fav_icon);
		mFavImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if ((String) mFavImageView.getTag() == mContext
						.getString(R.string.favourite_off)) {
					mFavImageView.setImageResource(R.drawable.favourite_red);
					mFavImageView.setTag(mContext
							.getString(R.string.favourite_on));
					Crouton.makeText((ContentActivity)mContext, R.string.favourite_on,
							Style.ALERT).show();
				} else {
					mFavImageView.setImageResource(R.drawable.favourite);
					mFavImageView.setTag(mContext
							.getString(R.string.favourite_off));
					Crouton.makeText((ContentActivity)mContext, R.string.favourite_off,
							Style.CONFIRM).show();
				}
			}
		});

		// Setup the content and replies view
		setupTopicContentUI(topicID);
		setupTopicRepliesUI(topicID);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getSupportMenuInflater().inflate(R.menu.fragment_content, menu);
            return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		finish();
        		break;
        		
        	case R.id.reply:
                break;
                
            case R.id.refresh:
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    public void setupTopicContentUI(String topicID) {
		V2HttpClient.get(URL_SUFFIX_TOPIC + topicID, null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray topics) {
						progressBar.setVisibility(View.GONE);
						try {
							JSONObject topic = topics.getJSONObject(0);

							// Storing each json item in variable
							String id = topic.getString(TAG_ID);
							String title = topic.getString(TAG_TITLE);
							String content = topic.getString(TAG_CONTENT);

							JSONObject member = topic.getJSONObject(TAG_MEMBER);
							String member_username = member
									.getString(TAG_MEMBER_USERNAME);
							String member_avatar_normal = member
									.getString(TAG_MEMBER_AVATAR_NORMAL);

							JSONObject node = topic.getJSONObject(TAG_NODE);
							String node_title = node.getString(TAG_NODE_TITLE);

							TextView title_text = (TextView) findViewById(R.id.title);
							WebView content_text = (WebView) findViewById(R.id.content);
							ImageView thumb_image = (ImageView) findViewById(R.id.user_image);
							title_text.setText(title);

							String text = "<html><head>"
									+ "<style type=\"text/css\">body{color: #63656a }"
									+ "</style></head>"
									+ "<body link=\"#C0C0C0\" vlink=\"#808080\" alink=\"#FF0000\">"
									+ content + "</body></html>";
							content_text.loadDataWithBaseURL(null, text,
									"text/html", "UTF-8", null);
							content_text.getSettings().setLayoutAlgorithm(
									LayoutAlgorithm.SINGLE_COLUMN);

							mImageLoader.DisplayImage(member_avatar_normal,
									thumb_image);

							if (id != null) {
								View contentView = findViewById(R.id.content_wraper);
								contentView.setVisibility(View.VISIBLE);
							}

							mFlagContentDataDone = true;

							if (mFlagRepliesDataDone) {
								TextView noreplyView = (TextView) findViewById(R.id.noreply_text);
								if (mRepliesCount == 0) {
									mRepliesListView.setVisibility(View.GONE);
									noreplyView.setVisibility(View.VISIBLE);
								} else {
									mRepliesListView
											.setVisibility(View.VISIBLE);
									noreplyView.setVisibility(View.GONE);
									mRepliesListViewAdapter
											.notifyDataSetChanged();
								}
							}
							
							

						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	public void setupTopicRepliesUI(String topicID) {
		V2HttpClient.get(URL_SUFFIX_REPLIES + topicID, null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray replies) {
						try {
							mRepliesCount = replies.length();

							StringBuilder sb = new StringBuilder();
							sb.append("<html><head>");
							sb.append("<style type=\"text/css\">body{color: #63656a }");
							sb.append("</style></head>");
							sb.append("<body link=\"#C0C0C0\" vlink=\"#808080\" alink=\"#FF0000\">");
							sb.append("<table cellpadding=\"5\" cellspacing=\"0\"  width=\"100%\" border=\"0\">");

							// looping through All Contacts
							for (int i = 0; i < mRepliesCount; i++) {
								JSONObject c = replies.getJSONObject(i);

								// Storing each json item in variable
								String id = c.getString(TAG_ID);
								String thanks = c.getString(TAG_THANKS);
								String content = c.getString(TAG_CONTENT);

								// Member is agin JSON Object
								JSONObject member = c.getJSONObject(TAG_MEMBER);

								String member_username = member
										.getString(TAG_MEMBER_USERNAME);
								String member_avatar_mini = member
										.getString(TAG_MEMBER_AVATAR_MINI);

								sb.append("<tr>");
								sb.append("<td width=\"32\" border=\"1\" valign=\"top\" align=\"left\"><img src=");
								sb.append(member_avatar_mini);
								sb.append(" Height=32  Width=32 class=\"avatar\" border=\"0\" align=\"center\"auto /></td>");
								// sb.append("<td width=\"10\" valign=\"top\"></td>");
								sb.append("<td width=\"auto\" valign=\"top\" align=\"left\">");
								sb.append("<strong>");
								sb.append(member_username);
								sb.append("</strong>");
								sb.append("<br/>");
								sb.append("<div class=\"reply_content\">");
								sb.append(content);
								sb.append("</div>");
								sb.append("</td>");
								sb.append("</tr>");

							}
							sb.append("</table>");
							sb.append("</body></html>");

							System.out.println(sb.toString());

							mRepliesListView.loadDataWithBaseURL(null,
									sb.toString(), "text/html", "UTF-8", null);
							mRepliesListView.getSettings().setLayoutAlgorithm(
									LayoutAlgorithm.SINGLE_COLUMN);

							if (mFlagContentDataDone) {
								TextView noreplyView = (TextView) findViewById(R.id.noreply_text);
								if (mRepliesCount == 0) {
									mRepliesListView.setVisibility(View.GONE);
									noreplyView.setVisibility(View.VISIBLE);
								} else {
									mRepliesListView
											.setVisibility(View.VISIBLE);
									noreplyView.setVisibility(View.GONE);
									mRepliesListViewAdapter
											.notifyDataSetChanged();
								}
							} else {
								mFlagRepliesDataDone = true;
							}
							

						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
}
