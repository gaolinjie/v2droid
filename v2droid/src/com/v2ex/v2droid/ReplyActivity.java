
package com.v2ex.v2droid;

import org.holoeverywhere.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ReplyActivity extends Activity {
	
	private EditText replyEdit;
	String topicID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply);
		
		Intent intent = getIntent();
		topicID = intent.getStringExtra("EXTRA_TOPIC_ID");
		
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
            	replyTopic();
            	//finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    
    public void replyTopic() {
    	
    	final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				System.out.println("reply handleMessage=====>");
				if(msg.what == 1){
					
						finish();
					
				}else if(msg.what == 0){
					
					UIHelper.ToastMessage(ReplyActivity.this, getString(R.string.msg_login_fail)+msg.obj);
				}else if(msg.what == -1){
				}
			}
		};
		new Thread(){
			public void run() {
				System.out.println("reply run=====>");
				Message msg =new Message();
				try {
					String result;
					String strTopic = "http://www.v2ex.com/t/" + topicID;
					String content = replyEdit.getText().toString();
			    	//String once = HtmlParser.getTopicOnce(strTopic);
					AppContext ac = (AppContext)getApplication(); 

					String once = ac.getTopicOnce(strTopic);
						result = ac.replyTopic(topicID, content, once);
			   
	                
	                if(!StringUtils.isEmpty(result)){
	                	msg.what = 1;//成功
	                }else{
	                	msg.what = 0;//失败
	                }
	            } catch (AppException e) {
	            	e.printStackTrace();
			    	msg.what = -1;
	            }
				handler.sendMessage(msg);
			}
		}.start();

    }
}
