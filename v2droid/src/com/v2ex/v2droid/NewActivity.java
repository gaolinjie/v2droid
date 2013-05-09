
package com.v2ex.v2droid;

import java.io.IOException;

import org.holoeverywhere.app.Activity;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class NewActivity extends Activity {
	
	private EditText nodeEdit;
	private EditText titleEdit; 
	private EditText contentEdit; 
	
	String nodeID;
	String title;
	String content;
	Response response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new);
		
		final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
		
        nodeEdit = (EditText) findViewById(R.id.node_edit);
        ///
        nodeEdit.setText("问与答"); // this line for temp test
        nodeID = "qna";
        ///
        titleEdit = (EditText) findViewById(R.id.title_edit);
        contentEdit = (EditText) findViewById(R.id.content_edit);
        nodeEdit.requestFocus();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getSupportMenuInflater().inflate(R.menu.activity_new, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		finish();
        		break;
                
            case R.id.send:
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
			String url = "http://v2ex.com/new/" + nodeID;
		
			title = titleEdit.getText().toString();
			content = contentEdit.getText().toString();
			
			AppContext ac = (AppContext) getApplication();
			
			Document doc;
			
			try {
				doc = ApiClient.get(ac, url, URLs.HOST);
				response = ApiClient.newTopic(ac, url, title, content);
				
			} catch (IOException e) {
				
			}
			
			return s;
		}

		@Override
		protected void onPostExecute(String[] result) {
			if (response.statusCode() == 200) {
				/*
				try {
				Intent intent = new Intent();
                intent.putExtra("html", response.parse().toString());

                setResult(RESULT_OK, intent);
				finish();
				} catch (IOException e) {
				
				}*/
				finish();
			}

			super.onPostExecute(result);
		}
	}
}
