
package com.v2ex.v2droid;

import org.holoeverywhere.app.Activity;

import android.os.Bundle;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class NewActivity extends Activity {
	
	private EditText nodeEdit;
	private EditText titleEdit; 
	private EditText contentEdit; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new);
		
		final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
		
        nodeEdit = (EditText) findViewById(R.id.node_edit);
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
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
