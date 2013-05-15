
package com.v2ex.v2droid;

import java.io.IOException;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Toast;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ReplyActivity extends Activity {
	
	private EditText replyEdit;
	String topicID;
	String once;
	String content;
	boolean success = false;
	Response response;
	
	private MenuItem refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply);
		
		Intent intent = getIntent();
		topicID = intent.getStringExtra("EXTRA_TOPIC_ID");
		once = intent.getStringExtra("EXTRA_TOPIC_ONCE");
		
		final ActionBar ab = getSupportActionBar();
        //ab.setTitle(R.string.app_name);
        ab.setDisplayHomeAsUpEnabled(true);
		
		replyEdit = (EditText) findViewById(R.id.reply_edit);
		replyEdit.requestFocus();
	
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getSupportMenuInflater().inflate(R.menu.activity_reply, menu);
            refresh = menu.findItem(R.id.send);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		finish();
        		break;
                
            case R.id.send:
            	refresh.setActionView(R.layout.refresh);
            	new GetDataTask().execute();
            	//finish();
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
			String url = "http://v2ex.com/t/" + topicID;
		
			content = replyEdit.getText().toString();
			
			AppContext ac = (AppContext) getApplication();
			
			Document doc;
			
			try {
				doc = ApiClient.get(ac, url, URLs.HOST).parse();
				response = ApiClient.reply(ac, url, content, once);
				
			} catch (IOException e) {
				
			}
			
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (response!= null && response.statusCode() == 200) {
				try {
				Intent intent = new Intent();
                intent.putExtra("html", response.parse().toString());

                setResult(RESULT_OK, intent);
				finish();
				} catch (IOException e) {
				
				}
			} else {
				Toast.makeText(getApplicationContext(), R.string.send_failed,
						Toast.LENGTH_LONG).show();
			}
			
			refresh.setActionView(null);

			super.onPostExecute(result);
		}
	}
}
