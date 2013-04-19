
package com.v2ex.v2droid;

import org.holoeverywhere.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ReplyActivity extends Activity {
	
	private EditText replyEdit;  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply);
		
		final ActionBar ab = getSupportActionBar();
        //ab.setTitle(R.string.app_name);
        ab.setDisplayHomeAsUpEnabled(true);
		
		replyEdit = (EditText) findViewById(R.id.reply_edit);
		replyEdit.requestFocus();
		
	
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getSupportMenuInflater().inflate(R.menu.activity_reply, menu);
            return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		finish();
        		break;
                
            case R.id.send:
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
